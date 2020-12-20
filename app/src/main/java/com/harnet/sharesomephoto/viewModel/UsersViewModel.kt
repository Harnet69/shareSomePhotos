package com.harnet.sharesomephoto.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.harnet.sharesomephoto.model.User
import com.harnet.sharesomephoto.util.jsonToArray
import com.parse.FindCallback
import com.parse.ParseQuery
import com.parse.ParseUser

class UsersViewModel(application: Application) : BaseViewModel(application) {
    val mUsers = MutableLiveData<List<User>>()
    val mIsArticleLoadError = MutableLiveData<Boolean>()
    val mIsLoading = MutableLiveData<Boolean>()

    fun refresh(isFollowing: Boolean) {
        if (isFollowing) {
            getFollowingUsers()
//            getFollowers()
        } else {
            getUsersFromParseServer()
        }
    }

    // retrieve users
    private fun retrieveUsers(usersFromParse: List<User>) {
        // set received list to observable mutable list
        mUsers.postValue(usersFromParse)
        // switch off error message
        mIsArticleLoadError.postValue(false)
        // switch off waiting spinner
        mIsLoading.postValue(false)
    }

    //get users from server
    private fun getUsersFromParseServer(followingUsersList: List<String>? = null) {
        val query: ParseQuery<ParseUser> = ParseUser.getQuery()
        // exclude user of this device
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().username)

        // if followed users
        if (followingUsersList != null) {
            //TODO think about implementing a list of matched users
            query.whereContainedIn("objectId", followingUsersList)
        }

        // sort by name anciently
        query.addAscendingOrder("username")
        query.findInBackground(FindCallback { objects, e ->
            val usersFromParse = mutableListOf<User>()

            if (e == null) {
                if (objects.isNotEmpty()) {
                    for (i in objects.indices) {
                        val userToSend = User(objects[i].username, "", "")
                        userToSend.userId = objects[i].objectId
                        userToSend.userIdForList = i.toString() // for users list blinking reduction
                        userToSend.profileImgUrl = objects[i].get("profileImg").toString()

                        usersFromParse.add(userToSend)
                    }
//                    retrieveUsers(usersFromParse)

                } else {
                    mIsLoading.postValue(false)
                    mIsArticleLoadError.postValue(false)
                }

                retrieveUsers(usersFromParse)
            } else {
                // switch off waiting spinner and inform user is smth wrong
                mIsLoading.postValue(false)
                // switch off error message
                mIsArticleLoadError.postValue(true)
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_SHORT).show()
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
                    getUsersFromParseServer(jsonToArray(followingUsers))
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
                        followersId.add(user.objectId)
                    }

                    getUsersFromParseServer(followersId)
                }
            }
        })
    }
}