package com.harnet.sharesomephoto.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.harnet.sharesomephoto.model.User

class UsersViewModel : ViewModel() {
    val mUsers = MutableLiveData<List<User>>()
    val mIsArticleLoadError = MutableLiveData<Boolean>()
    val mIsLoading = MutableLiveData<Boolean>()

    // refresh mArticles with a new data TWO WAYS TO DO IT: PARSER & RETROFIT
    fun refresh() {
        // TODO add getting from Parse service method
        val user1 = User("Adam", "","")
        val user2 = User("Pawel", "","")
        val user3 = User("George", "","")
        val user4 = User("Mirek", "","")
        val users = ArrayList<User>()
        users.add(user1)
        users.add(user2)
        users.add(user3)
        users.add(user4)

        retrieveUsers(users)
    }

    // retrieve articles
    private fun retrieveUsers(usersFromParse: ArrayList<User>) {
        // set received list to observable mutable list
        mUsers.postValue(usersFromParse)
        // switch off error message
        mIsArticleLoadError.postValue(false)
        // switch off waiting spinner
        mIsLoading.postValue(false)
    }
}