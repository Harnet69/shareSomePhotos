package com.harnet.sharesomephoto.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.harnet.sharesomephoto.model.Image
import com.harnet.sharesomephoto.model.User
import com.parse.FindCallback
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import java.lang.Error

class UserDetailsViewModel(application: Application) : BaseViewModel(application) {
    // user block
    val mUser = MutableLiveData<User>()
    val mIsUserLoadError = MutableLiveData<Boolean>()
    val mIsLoading = MutableLiveData<Boolean>()

    // user images block
    val mUserImages = MutableLiveData<List<Image>>()
    val mIsImagesLoadError = MutableLiveData<Boolean>()
    val mIsImagesLoading = MutableLiveData<Boolean>()

    //appbar
    val mIsUserFollowing = MutableLiveData<Boolean>()

    fun refresh(userId: String) {
        getUserById(userId)
        getUserImages(userId)
        isUserFollowing(userId)
    }

    // retrieve a user
    private fun retrieveUser(userFromParse: User) {
        // set received list to observable mutable list
        mUser.postValue(userFromParse)
        // switch off error message
        mIsUserLoadError.postValue(false)
        // switch off waiting spinner
        mIsLoading.postValue(false)
    }

    // retrieve user images
    private fun retrieveImages(userImagesFromParse: ArrayList<Image>) {
        // set received list to observable mutable list
        mUserImages.postValue(userImagesFromParse)
        // switch off error message
        mIsImagesLoadError.postValue(false)
        // switch off waiting spinner
        mIsImagesLoading.postValue(false)
    }

    private fun getUserById(userId: String) {
        val query: ParseQuery<ParseUser> = ParseUser.getQuery()
        query.whereEqualTo("objectId", userId)
        query.findInBackground(FindCallback { objects, e ->
            if (e == null) {
                if (objects.isNotEmpty()) {
                    val retrievedUser = objects[0]
                    val userForBind = User(retrievedUser.username, "", "")
                    userForBind.profileImgUrl = retrievedUser["profileImg"].toString()
                    retrieveUser(userForBind)
                } else {
                    mIsLoading.postValue(false)
                    mIsUserLoadError.postValue(false)
                }
            } else {
                // switch off waiting spinner and inform user is smth wrong
                mIsLoading.postValue(false)
                // switch off error message
                mIsUserLoadError.postValue(true)
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    // get user images from server
    private fun getUserImages(authorId: String) {
        // clean previous version of feeds
        mUserImages.postValue(arrayListOf())

        val userImages = ArrayList<Image>()

        val query = ParseQuery<ParseObject>("Image")
        query.whereEqualTo("authorId", authorId)
        query.orderByDescending("createdAt")

        query.findInBackground(FindCallback { objects, parseObjectError ->
            if (parseObjectError == null) {
                if (objects.isNotEmpty()) {
                    for (image in objects) {
                        val parseFile = image.getParseFile("image")
                        val imageForBind = Image(image.get("authorId").toString())
                        imageForBind.imageURL = parseFile.url
                        userImages.add(imageForBind)
                    }
                    retrieveImages(userImages)
                } else {
                    mIsImagesLoadError.postValue(false)
                    mIsImagesLoading.postValue(false)
                }
            } else {
                // switch off waiting spinner and inform user is smth wrong
                mIsLoading.postValue(false)
                // switch off error message
                mIsImagesLoading.postValue(true)
                Toast.makeText(getApplication(), parseObjectError.message, Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    // follow user
    fun follow(userId: String){
        if(mIsUserFollowing.value == true){
            removeFromFollowing(userId)
            mIsUserFollowing.value = false
        }else{
            addToFollowing(userId)
            mIsUserFollowing.value = true
        }
    }

    // add to current user a user which is following
    private fun addToFollowing(userId: String) {
        val query: ParseQuery<ParseUser> = ParseUser.getQuery()

        query.whereEqualTo("objectId", ParseUser.getCurrentUser().objectId)
        query.findInBackground(FindCallback { objects, e ->
            if (e == null && objects.isNotEmpty()) {
                if (objects.isNotEmpty()) {
                    objects[0].addUnique("following", userId)
                    objects[0].saveEventually()
                }
            } else {
                e.printStackTrace()
            }
        })
    }

    private fun isUserFollowing(userId: String){
        val query: ParseQuery<ParseUser> = ParseUser.getQuery()

        query.whereContains("following", userId)
        query.findInBackground(FindCallback { objects, e ->
            if (e == null) {
                mIsUserFollowing.value = objects.isNotEmpty()
            } else {
                e.printStackTrace()
            }
        })
    }

    // remove from following
    private fun removeFromFollowing(userId: String){
        val query: ParseQuery<ParseUser> = ParseUser.getQuery()
        val currentUser = ParseUser.getCurrentUser()
        query.whereEqualTo("objectId", currentUser.objectId)
        query.findInBackground(FindCallback { objects, e ->
            if (e == null && objects.isNotEmpty()) {
                if (objects.isNotEmpty()) {
                    val followingUsers = objects[0].getJSONArray("following")
                    for (i in 0 until followingUsers.length()-1) {
                        Log.i("followingUser", "removeFromFollowing: " + followingUsers[i])
                        //TODO doesn't remove the last user
                        if (followingUsers[i].toString() == userId) {
                            followingUsers.remove(i)
                        }
                    }
                    currentUser.put("following", followingUsers)
                    currentUser.saveInBackground()
                }
            } else {
                e.printStackTrace()
            }
        })
    }
}