package edu.itvo.trackergym.domain.repository

import edu.itvo.trackergym.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<UserProfile?>
    val isUserLoggedIn: Boolean

    suspend fun loginWithEmail(email: String, pass: String): Result<Unit>
    suspend fun registerWithEmail(email: String, pass: String): Result<Unit>
    suspend fun loginWithGoogleCredential(idToken: String): Result<Unit>
    suspend fun logout()

    suspend fun saveUserProfile(profile: UserProfile): Result<Unit>
}