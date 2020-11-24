package com.harnet.sharesomephoto.viewModel

import android.app.Application
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import com.harnet.sharesomephoto.service.Imageable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageViewModel(application: Application) : BaseViewModel(application), Imageable {
    var mImagesLiveData: MutableLiveData<List<String>> = MutableLiveData()

    fun getAllImages() {
        launch(Dispatchers.Main) {
            mImagesLiveData.setValue(withContext(Dispatchers.IO) {
                loadImagesfromPhone(getApplication())
            })
        }
    }

}