package com.example.ecommercefurniture.viewmodel

import android.provider.Settings.Global.getString
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommercefurniture.R
import com.example.ecommercefurniture.data.User
import com.example.ecommercefurniture.firebase.FirebaseCommon
import com.example.ecommercefurniture.firebase.FirebaseDb
import com.example.ecommercefurniture.util.Constants
import com.example.ecommercefurniture.util.Resource
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestoredb: FirebaseFirestore,
    private val firebaseCommon: FirebaseCommon

): ViewModel() {

    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()

    val saveUserInformationGoogleSignIn = MutableLiveData<Resource<String>>()

    private val _resetpw = MutableSharedFlow<Resource<String>>()
     val resetpw = _resetpw.asSharedFlow()




    private fun saveUserInformationGoogleSignIn(
        userUid: String,
        user: User
    ) {
        firebaseCommon.checkUserByEmail(user.email) { error, isAccountExisted ->
            if (error != null)
                saveUserInformationGoogleSignIn.postValue(Resource.Error(error))
            else
                if (isAccountExisted!!)
                    saveUserInformationGoogleSignIn.postValue(Resource.Success(userUid))
                else
                    firebaseCommon.saveUserInformation(userUid, user).addOnCompleteListener {
                        if (it.isSuccessful)
                            saveUserInformationGoogleSignIn.postValue(Resource.Success(userUid))
                        else
                            saveUserInformationGoogleSignIn.postValue(Resource.Error(it.exception.toString()))
                    }
        }

    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _login.emit(Resource.Loading())
        }
        firebaseAuth.signInWithEmailAndPassword(
            email, password
        ).addOnSuccessListener {
            viewModelScope.launch {
                it.user?.let {
                    _login.emit(Resource.Success(it))
                }
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _login.emit((Resource.Error(it.message.toString())))
            }
        }
    }
    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetpw.emit(Resource.Loading())
        }
            firebaseAuth
                .sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    viewModelScope.launch {

                        _resetpw.emit(Resource.Success(email))
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch {

                        _resetpw.emit(Resource.Error(it.message.toString()))
                    }
                }
    }
    fun signInWithGoogle(idToken: String) {
        saveUserInformationGoogleSignIn.postValue(Resource.Loading())
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseCommon.signInWithGoogle(credential).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                val userFirebase = FirebaseAuth.getInstance().currentUser
                val fullNameArray = userFirebase!!.displayName?.split(" ")
                val firstName = fullNameArray!![0]
                val size = fullNameArray.size
                var secondName = ""
                if (size == 1)
                    secondName = ""
                else
                    secondName = fullNameArray[1]

                val user = User(firstName, secondName, userFirebase.email.toString(), "")
                saveUserInformationGoogleSignIn(userFirebase.uid, user)
            } else
                saveUserInformationGoogleSignIn.postValue(Resource.Error(task.exception.toString()))


        }
    }

}