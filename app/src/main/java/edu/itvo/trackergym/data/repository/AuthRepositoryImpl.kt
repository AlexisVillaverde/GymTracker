package edu.itvo.trackergym.data.repository

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import edu.itvo.trackergym.domain.model.UserProfile
import edu.itvo.trackergym.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val googleSignInClient: GoogleSignInClient
) : AuthRepository {

    override val isUserLoggedIn: Boolean
        get() = auth.currentUser != null

    override val currentUser: Flow<UserProfile?>
        get() = callbackFlow {
            var snapshotListener: ListenerRegistration? = null
            val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                val user = firebaseAuth.currentUser
                snapshotListener?.remove()

                if (user == null) {
                    trySend(null).isSuccess
                } else {
                    snapshotListener = db.collection("users").document(user.uid)
                        .addSnapshotListener { snapshot, error ->
                            if (snapshot != null && snapshot.exists()) {
                                trySend(snapshot.toObject(UserProfile::class.java)).isSuccess
                            } else if (error == null) {
                                // User logged in, but profile doesn't exist yet
                                trySend(UserProfile(uid = user.uid)).isSuccess
                            }
                        }
                }
            }
            auth.addAuthStateListener(authListener)
            awaitClose {
                snapshotListener?.remove()
                auth.removeAuthStateListener(authListener)
            }
        }

    override suspend fun loginWithEmail(email: String, pass: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerWithEmail(email: String, pass: String): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            result.user?.uid?.let { uid ->
                saveUserProfile(UserProfile(uid = uid))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginWithGoogleCredential(idToken: String): Result<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user
            if (user != null) {
                val userProfileUpdates = mutableMapOf<String, Any>()
                userProfileUpdates["uid"] = user.uid
                user.displayName?.let { userProfileUpdates["userName"] = it }
                user.photoUrl?.let { userProfileUpdates["photoUrl"] = it.toString() }

                db.collection("users").document(user.uid).set(userProfileUpdates, SetOptions.merge()).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        auth.signOut()
        googleSignInClient.signOut().await()
    }

    override suspend fun saveUserProfile(profile: UserProfile): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("No user")
            // Nos aseguramos que el UID del perfil coincida con el auth
            val secureProfile = profile.copy(uid = uid)
            db.collection("users").document(uid).set(secureProfile, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}