package com.harnet.sharesomephoto.viewModel

import android.app.Application
import android.text.TextUtils
import android.util.ArraySet
import android.util.Log
import android.util.Patterns
import android.widget.ArrayAdapter
import androidx.collection.arraySetOf
import androidx.lifecycle.MutableLiveData
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.model.User
import com.harnet.sharesomephoto.util.jsonToArray
import com.parse.*
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : BaseViewModel(application) {
    val mIsUserLogged = MutableLiveData<Boolean>()

    val mErrUserName = MutableLiveData<String>()
    val mErrUserPassword = MutableLiveData<String>()
    val mErrIsUserEmailValid = MutableLiveData<String>()
    val mErrIsUserExists = MutableLiveData<String>()
    val mErrUserLoginOrPass = MutableLiveData<String>()
    var mErrUserNameLength = MutableLiveData<String>()

    var mUserFeedsCount = MutableLiveData<Int>()
    var mUserListFollowingUsers = MutableLiveData<List<String>>()
    val mFollowers = MutableLiveData<List<String>>()
    val mChats = MutableLiveData<ArraySet<String>>()

    // refresh user statistic
    fun refreshUserStats() {
        getQttUserFeeds()
        getFollowingUsers()
        getFollowers()
        getChatsQtt()
    }

    // sign Up a new user
    fun signUp(newUser: User) {
        clearErrors()

        // are fields valid
        if (checkUserInputForEmpty(newUser) && checkUserNameAndPassForWhiteSpaces(newUser)
            && isUsernameLength(newUser.name)
        ) {
            //create a new user
            val parseUser = ParseUser()
            parseUser.username = newUser.name.trim()
            parseUser.setPassword(newUser.password.trim())
            parseUser.email = newUser.email.trim()

            // sign in user
            parseUser.signUpInBackground { e ->
                if (e == null) {
                    getCurrentUserIfLogged()
                } else {
                    mErrIsUserExists.value = e.message
                }
            }
        }
    }

    //log in
    fun logIn(userName: String, userPassword: String) {
        clearErrors()

        if (userName != "") {
            if (userPassword != "") {
                val user = User(userName, userPassword, "")
                if (checkUserNameAndPassForWhiteSpaces(user)) {
                    ParseUser.logInInBackground(userName, userPassword, LogInCallback { user, e ->
                        if (e == null && user != null) {
                            mIsUserLogged.setValue(true)
                        } else {
                            mErrUserLoginOrPass.value = e.message
                        }
                    })
                }
            } else {
                mErrUserPassword.value =
                    getApplication<Application>().getString(R.string.err_msg_field_cant_be_empty)
            }
        } else {
            mErrUserName.value =
                getApplication<Application>().getString(R.string.err_msg_field_cant_be_empty)
        }
    }

    //log out
    fun logOut() {
        clearErrors()

        ParseUser.logOut()
        mIsUserLogged.setValue(false)
    }

    //check if the user logged in
    fun getCurrentUserIfLogged(): ParseUser? {
        if (ParseUser.getCurrentUser() != null) {
            mIsUserLogged.setValue(true)
        } else {
            mIsUserLogged.setValue(false)
        }
        return ParseUser.getCurrentUser()
    }

    // check if user fields empty
    private fun checkUserInputForEmpty(newUser: User): Boolean {
        // check if fields not empty
        if (newUser.name != "") {
            if (newUser.password != "") {
                if (newUser.email != "") {
                    if (isValidEmail(newUser.email)) {
                        return true
                    } else {
                        mErrIsUserEmailValid.value =
                            getApplication<Application>().getString(R.string.err_msg_wrong_email_format)
                    }
                } else {
                    mErrIsUserEmailValid.value =
                        getApplication<Application>().getString(R.string.err_msg_field_cant_be_empty)
                }
            } else {
                mErrUserPassword.value =
                    getApplication<Application>().getString(R.string.err_msg_field_cant_be_empty)
            }
        } else {
            mErrUserName.value =
                getApplication<Application>().getString(R.string.err_msg_field_cant_be_empty)
        }
        return false
    }

    //check if user fields has a whitespace
    private fun checkUserNameAndPassForWhiteSpaces(newUser: User): Boolean {
        if (!newUser.name.contains(" ")) {
            if (!newUser.password.contains(" ")) {
                return true
            } else {
                mErrUserPassword.value =
                    getApplication<Application>().getString(R.string.err_msg_whiteSpaces_not_allowed)
            }
        } else {
            mErrUserName.value =
                getApplication<Application>().getString(R.string.err_msg_whiteSpaces_not_allowed)
        }
        return false
    }

    private fun isUsernameLength(username: String): Boolean {
        val userNameLength = 10

        mErrUserNameLength.value =
            getApplication<Application>().getString(R.string.err_msg_username_too_long)
        return username.length <= userNameLength
    }

    // check if email valid
    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    // clear input fields errors
    fun clearErrors() {
        mErrUserName.value = null
        mErrUserPassword.value = null
        mErrIsUserEmailValid.value = null
        mErrUserNameLength.value = null
        mErrUserLoginOrPass.value = null
    }

    // get quantity of user feeds from server
    private fun getQttUserFeeds() {
        val query = ParseQuery<ParseObject>("Image")
        query.whereEqualTo("authorId", ParseUser.getCurrentUser().objectId)

        query.countInBackground(CountCallback { count, e ->
            if (e == null) {
                mUserFeedsCount.value = count
            }
        })
    }

    // get following users from server
    private fun getFollowingUsers() {

        val query: ParseQuery<ParseUser> = ParseUser.getQuery()
        query.whereEqualTo("objectId", ParseUser.getCurrentUser().objectId)
        query.findInBackground(FindCallback { objects, e ->
            if (e == null) {
                if (objects.isNotEmpty()) {
                    val followingUsers = objects[0].getJSONArray("following")
                    mUserListFollowingUsers.value = jsonToArray(followingUsers)
                }
            }
        })
    }

    // get followers from server
    private fun getFollowers() {
        val followersId = mutableListOf<String>()

        val query: ParseQuery<ParseUser> = ParseUser.getQuery()
        query.whereContains("following", ParseUser.getCurrentUser().objectId)
        query.findInBackground(FindCallback { objects, e ->
            if (e == null) {
                if (objects.isNotEmpty()) {
                    for (user in objects) {
//                        Log.i("getFollowers:", "getFollowers: ${user.objectId}")
                        followersId.add(user.objectId)
                    }

                    mFollowers.value = followersId
                }
            }
        })
    }

    // get quantity of chats
    private fun getChatsQtt() {
        launch {
            val query1 = ParseQuery<ParseObject>("Message")
            query1.whereEqualTo("sender", ParseUser.getCurrentUser().objectId)

            val query2 = ParseQuery<ParseObject>("Message")
            query2.whereEqualTo("recipient", ParseUser.getCurrentUser().objectId)

            val queries = arrayListOf<ParseQuery<ParseObject>>()
            queries.add(query1)
            queries.add(query2)

            val query = ParseQuery.or(queries)

            query.findInBackground { objects, e ->
                if (e == null) {
                    val chatUsersIds = ArraySet<String>()

                    for (i in objects.indices) {
                        if (objects[i].get("sender")
                                .toString() == ParseUser.getCurrentUser().objectId
                        ) {
                            chatUsersIds.add(objects[i].get("recipient").toString())
                        }
                        if (objects[i].get("recipient")
                                .toString() == (ParseUser.getCurrentUser().objectId)
                        ) {
                            chatUsersIds.add(objects[i].get("sender").toString())
                        }
                    }

                    mChats.value = chatUsersIds
                } else {
                    e.printStackTrace()
                }
            }
        }
    }
}