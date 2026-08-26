package com.example.basecamp.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basecamp.domain.model.User
import com.example.basecamp.domain.model.Ticket
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.io.File
import androidx.core.content.FileProvider
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.ByteArrayOutputStream

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val user: User) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

data class VolunteerBadge(
    val title: String,
    val requiredRsvps: Int,
    val color: Long
)

val GAMIFICATION_BADGES = listOf(
    VolunteerBadge("FIRST BLOOD", 1, 0xFFFAFF00), // Electric Yellow
    VolunteerBadge("COMMUNITY VETERAN", 5, 0xFFFF007F), // Hot Pink
    VolunteerBadge("LOCAL HERO", 10, 0xFF00E5FF)  // Cyan
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val supabaseClient: SupabaseClient
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()
    
    private val _updateState = MutableStateFlow<Boolean>(false)
    val updateState: StateFlow<Boolean> = _updateState.asStateFlow()

    private val _rsvpCount = MutableStateFlow<Int>(0)
    val rsvpCount: StateFlow<Int> = _rsvpCount.asStateFlow()

    private val _logoutState = MutableStateFlow<Boolean>(false)
    val logoutState: StateFlow<Boolean> = _logoutState.asStateFlow()

    private val _isUploadingPhoto = MutableStateFlow<Boolean>(false)
    val isUploadingPhoto: StateFlow<Boolean> = _isUploadingPhoto.asStateFlow()
    
    private val _uploadError = MutableStateFlow<String?>(null)
    val uploadError: StateFlow<String?> = _uploadError.asStateFlow()

    val currentUserId: String
        get() = supabaseClient.auth.currentUserOrNull()?.id ?: "unknown"

    init {
        fetchProfile()
    }

    fun fetchProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val userId = supabaseClient.auth.currentUserOrNull()?.id ?: throw Exception("Not logged in")
                val user = supabaseClient.postgrest["users"]
                    .select { filter { eq("id", userId) } }
                    .decodeSingle<User>()
                    
                val tickets = supabaseClient.postgrest["tickets"]
                    .select { filter { eq("volunteer_id", userId) } }
                    .decodeList<Ticket>()
                    
                val attendedTickets = tickets.filter { it.status == "Attended" }
                _rsvpCount.value = attendedTickets.size
                _profileState.value = ProfileState.Success(user)
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Failed to fetch profile")
            }
        }
    }

    fun updateProfile(name: String, phone: String, website: String) {
        viewModelScope.launch {
            try {
                val userId = supabaseClient.auth.currentUserOrNull()?.id ?: throw Exception("Not logged in")
                
                // Get current user to preserve role and email
                val currentUserState = _profileState.value
                if (currentUserState is ProfileState.Success) {
                    val updatedUser = currentUserState.user.copy(
                        name = name,
                        phone = phone,
                        website = website
                    )
                    
                    supabaseClient.postgrest["users"].update(
                        {
                            set("name", updatedUser.name)
                            set("phone", updatedUser.phone)
                            set("website", updatedUser.website)
                        }
                    ) {
                        filter { eq("id", userId) }
                    }
                    
                    _profileState.value = ProfileState.Success(updatedUser)
                    _updateState.value = true
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Failed to update profile")
            }
        }
    }
    
    fun resetUpdateState() {
        _updateState.value = false
    }
    
    fun logout() {
        viewModelScope.launch {
            try {
                supabaseClient.auth.signOut()
            } catch (e: Exception) {
                // Ignore error on logout
            } finally {
                _logoutState.value = true
            }
        }
    }

    fun clearUploadError() {
        _uploadError.value = null
    }

    fun uploadProfilePhoto(uri: android.net.Uri, context: android.content.Context) {
        viewModelScope.launch {
            try {
                _isUploadingPhoto.value = true
                val userId = supabaseClient.auth.currentSessionOrNull()?.user?.id ?: throw Exception("Not logged in")
                
                // Compress image
                val inputStream = context.contentResolver.openInputStream(uri) ?: throw Exception("Could not open image")
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                
                if (originalBitmap == null) throw Exception("Could not decode image")
                
                // Read EXIF to fix orientation
                var rotatedBitmap = originalBitmap
                val exifInputStream = context.contentResolver.openInputStream(uri)
                if (exifInputStream != null) {
                    val exif = ExifInterface(exifInputStream)
                    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                    
                    val matrix = Matrix()
                    when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                    }
                    
                    if (!matrix.isIdentity) {
                        rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
                        originalBitmap.recycle() // Free memory
                    }
                    exifInputStream.close()
                }

                val outputStream = ByteArrayOutputStream()
                // Compress to JPEG with 70% quality to save space
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                val compressedBytes = outputStream.toByteArray()
                
                // Upload to Supabase Storage
                val path = "${userId}/avatar_${System.currentTimeMillis()}.jpg"
                val bucket = supabaseClient.storage["profile_pics"]
                
                bucket.upload(path, compressedBytes, upsert = true)
                
                val publicUrl = bucket.publicUrl(path)
                
                // Update user profile in database
                val updateData = mapOf("profile_image_url" to publicUrl)
                supabaseClient.postgrest["users"]
                    .update(updateData) {
                        filter { eq("id", userId) }
                    }
                
                fetchProfile() // Refresh profile
            } catch (e: Exception) {
                _uploadError.value = e.localizedMessage ?: "Failed to upload photo"
            } finally {
                _isUploadingPhoto.value = false
            }
        }
    }

    fun createTempFileUri(context: android.content.Context): android.net.Uri {
        val imagePath = File(context.cacheDir, "images")
        imagePath.mkdirs()
        val newFile = File(imagePath, "camera_photo_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            context.packageName + ".fileprovider",
            newFile
        )
    }
}


