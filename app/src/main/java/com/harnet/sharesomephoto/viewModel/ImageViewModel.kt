package com.harnet.sharesomephoto.viewModel

import android.app.Activity
import android.app.Application
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import com.harnet.sharesomephoto.model.User
import com.harnet.sharesomephoto.service.Imageable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URI

class ImageViewModel(application: Application) : BaseViewModel(application), Imageable {
    var mImagesLiveData = MutableLiveData<List<Uri>>()

    fun getAllImages(activity: Activity) {
        launch(Dispatchers.Main) {
            mImagesLiveData.setValue(getAllShownImagesPath(activity))
        }
    }
}