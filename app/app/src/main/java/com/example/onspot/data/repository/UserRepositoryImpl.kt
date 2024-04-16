package com.example.onspot.data.repository

import android.net.Uri
import com.example.onspot.data.model.User
import com.example.onspot.utils.Resource
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl : UserRepository {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val usersCollection: CollectionReference =
        FirebaseFirestore.getInstance().collection("users")
    private val storageReference = FirebaseStorage.getInstance().reference

    override fun loginWithEmailAndPassword(email: String, password: String): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    override fun connectWithGoogle(account: GoogleSignInAccount): Flow<Resource<FirebaseUser>> = flow{
        try {
            emit(Resource.Loading())
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: throw Exception("Authentication failed. Please check your credentials and try again.")

            val creationTimestamp = System.currentTimeMillis()

            if (authResult.additionalUserInfo?.isNewUser == true) {
                val user = User(
                    uuid = firebaseUser.uid,
                    firstName = account.givenName ?: "",
                    lastName = account.familyName ?: "",
                    email = firebaseUser.email!!,
                    isAdmin = false,
                    creationTimestamp = creationTimestamp
                )
                usersCollection
                    .document(firebaseUser.uid)
                    .set(user)
                    .await()
            }
            emit(Resource.Success(firebaseUser))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to authenticate with Google"))
        }
    }

    override suspend fun logoutUser(googleSignInClient: GoogleSignInClient?): Flow<Resource<Void?>> = flow {
        try {
            emit(Resource.Loading())
            firebaseAuth.signOut()
            googleSignInClient?.signOut()?.await()
            emit(Resource.Success(null))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to log out"))
        }
    }

    override fun registerUser(email: String, password: String, user: User): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user!!.uid
            val creationTimestamp = System.currentTimeMillis()
            usersCollection
                .document(userId)
                .set(user.copy(uuid = userId, creationTimestamp = creationTimestamp))
                .await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    override fun deleteUserAccount(googleSignInClient: GoogleSignInClient?): Flow<Resource<Void?>> = flow {
        try {
            emit(Resource.Loading())
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                usersCollection
                    .document(currentUser.uid)
                    .delete()
                    .await()
                currentUser.delete().await()
                googleSignInClient?.signOut()?.await()
                emit(Resource.Success(null))
            } else {
                emit(Resource.Error("User not logged in"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override suspend fun verifyPassword(password: String): Boolean {
        val currentUser = firebaseAuth.currentUser
        val email = currentUser?.email ?: return false
        val credential = EmailAuthProvider.getCredential(email, password)

        return try {
            currentUser
                .reauthenticate(credential)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun sendPasswordResetEmail(email: String): Flow<Resource<Void?>> = flow {
        try {
            emit(Resource.Loading())
            firebaseAuth.sendPasswordResetEmail(email).await()
            emit(Resource.Success(null))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to send password reset email"))
        }
    }

    override fun changePassword(currentPassword: String, newPassword: String): Flow<Resource<Void?>> = flow {
        try {
            emit(Resource.Loading())
            val currentUser = firebaseAuth.currentUser ?: throw FirebaseAuthException("No user logged in", "User must be logged in to change password.")

            val email = currentUser.email ?: throw FirebaseAuthException("No email found", "User email is required for reauthentication.")
            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            currentUser.reauthenticate(credential).await()

            currentUser.updatePassword(newPassword).await()
            emit(Resource.Success(null))
        } catch (e: FirebaseAuthException) {
            emit(Resource.Error(e.message ?: "Authentication failed"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to change password: ${e.localizedMessage}"))
        }
    }

    override suspend fun getCurrentUserAuthProvider(): String? {
        return firebaseAuth.currentUser?.providerData
            ?.firstOrNull { it.providerId != "firebase" }?.providerId
    }

    override fun getCurrentUserDetails(): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userDocument = usersCollection
                .document(currentUser.uid)
                .get()
                .await()
            val user = userDocument.toObject(User::class.java)
            if (user != null) {
                emit(Resource.Success(user))
            } else {
                emit(Resource.Error("User not found"))
            }
        } else {
            emit(Resource.Error("No user logged in"))
        }
    }.catch { e ->
        emit(Resource.Error(e.message ?: "An error occurred"))
    }

    override fun updateUserProfilePicture(imageUri: Uri): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        val user = firebaseAuth.currentUser ?: throw Exception("User not logged in")
        val imageRef = storageReference.child("profilePictures/${user.uid}")

        val uploadTaskSnapshot = imageRef.putFile(imageUri).await()
        val downloadUri = uploadTaskSnapshot
            .storage
            .downloadUrl
            .await()

        usersCollection
            .document(user.uid)
            .update("profilePictureUrl", downloadUri.toString())
            .await()

        emit(Resource.Success(downloadUri.toString()))
    }.catch { e ->
        emit(Resource.Error(e.message ?: "Failed to update profile picture"))
    }

    override fun deleteUserProfilePicture(): Flow<Resource<Void?>> = flow {
        try {
            emit(Resource.Loading())
            val user = firebaseAuth.currentUser ?: throw Exception("User not logged in")
            val imageRef = storageReference.child("profilePictures/${user.uid}")

            imageRef.delete().await()
            usersCollection.document(user.uid).update("profilePictureUrl", "").await()

            emit(Resource.Success(null))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to delete profile picture"))
        }
    }

    override fun updateUserDetails(userId: String, firstName: String, lastName: String): Flow<Resource<Void?>> = flow {
        try {
            emit(Resource.Loading())
            val updates = mapOf(
                "firstName" to firstName,
                "lastName" to lastName
            )
            usersCollection.document(userId).update(updates).await()
            emit(Resource.Success(null))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to update user details"))
        }
    }
}