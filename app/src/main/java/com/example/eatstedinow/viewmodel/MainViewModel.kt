package com.example.eatstedinow.viewmodel

import androidx.lifecycle.ViewModel
import com.example.eatstedinow.model.FoodItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HomeUiState(
    val menuList: List<FoodItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

data class ProfileUiState(
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val redeemedVouchers: List<String> = emptyList(),
    val pendingRatingCount: Int = 0
)

class MainViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _homeState = MutableStateFlow(HomeUiState())
    val homeState: StateFlow<HomeUiState> = _homeState.asStateFlow()

    private val _profileState = MutableStateFlow(ProfileUiState())
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    private var menuListener: ListenerRegistration? = null
    private var userListener: ListenerRegistration? = null
    private var orderListener: ListenerRegistration? = null

    init {
        fetchMenus()
        observeCurrentUser()
    }

    private fun fetchMenus() {
        _homeState.update { it.copy(isLoading = true) }
        menuListener = db.collection("menus").addSnapshotListener { snapshot, e ->
            if (e != null) {
                _homeState.update { it.copy(isLoading = false, errorMessage = e.message) }
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val items = snapshot.documents.mapNotNull { doc ->
                    try {
                        FoodItem(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            description = doc.getString("description") ?: "",
                            price = doc.getLong("price")?.toInt() ?: 0,
                            originalPrice = doc.getLong("originalPrice")?.toInt(),
                            imageUrl = doc.getString("imageUrl") ?: "",
                            rating = doc.getDouble("rating") ?: 0.0,
                            category = doc.getString("category") ?: "Makanan",
                            stock = doc.getLong("stock")?.toInt() ?: 0
                        )
                    } catch (e: Exception) { null }
                }
                _homeState.update { it.copy(menuList = items, isLoading = false) }
            }
        }
    }

    private fun observeCurrentUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            updateLocalProfileState(currentUser)

            userListener = db.collection("users").document(currentUser.uid)
                .addSnapshotListener { doc, _ ->
                    val vouchers = doc?.get("redeemedVouchers") as? List<String> ?: emptyList()
                    _profileState.update { it.copy(redeemedVouchers = vouchers) }
                }

            orderListener = db.collection("orders")
                .whereEqualTo("userId", currentUser.uid)
                .whereEqualTo("isRated", false)
                .addSnapshotListener { s, _ ->
                    _profileState.update { it.copy(pendingRatingCount = s?.size() ?: 0) }
                }
        }
    }

    private fun updateLocalProfileState(user: com.google.firebase.auth.FirebaseUser) {
        val name = user.displayName ?: "User EatsTedi"
        val photo = user.photoUrl?.toString() ?: "https://ui-avatars.com/api/?name=$name&background=FF8C00&color=fff"
        _profileState.update {
            it.copy(displayName = name, email = user.email ?: "", photoUrl = photo)
        }
    }

    fun updateProfile(newName: String, newEmail: String, onResult: (Boolean, String) -> Unit) {
        val user = auth.currentUser ?: return

        if (newName != user.displayName) {
            val updates = UserProfileChangeRequest.Builder().setDisplayName(newName).build()
            user.updateProfile(updates).addOnCompleteListener { task ->
                if (task.isSuccessful) updateLocalProfileState(user)
            }
        }

        if (newEmail != user.email && newEmail.isNotEmpty()) {
            user.verifyBeforeUpdateEmail(newEmail).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, "Link verifikasi dikirim ke $newEmail")
                } else {
                    onResult(false, task.exception?.message ?: "Gagal update email")
                }
            }
        } else {
            onResult(true, "Profil berhasil diperbarui")
        }
    }

    override fun onCleared() {
        super.onCleared()
        menuListener?.remove()
        userListener?.remove()
        orderListener?.remove()
    }
}