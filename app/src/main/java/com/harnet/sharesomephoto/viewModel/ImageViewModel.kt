package com.harnet.sharesomephoto.viewModel

import android.app.Application
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageViewModel(application: Application) : BaseViewModel(application) {
    private var imagesLiveData: MutableLiveData<List<String>> = MutableLiveData()

    fun getImageList(): MutableLiveData<List<String>> {
        return imagesLiveData
    }

    /**
     * Getting All Images Path.
     *
     * Required Storage Permission
     *
     * @return ArrayList with images Path
     */

    private fun loadImagesfromSDCard(): ArrayList<String> {
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor?
        val column_index_data: Int
        val column_index_folder_name: Int
        val listOfAllImages = ArrayList<String>()
        var absolutePathOfImage: String? = null

        val projection =
            arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        cursor = getApplication<Application>().contentResolver.query(uri, projection, null, null, null)

        column_index_data = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        column_index_folder_name = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data)
            listOfAllImages.add(absolutePathOfImage)
        }
        return listOfAllImages
    }

    fun getAllImages() {
        launch(Dispatchers.Main) {
            imagesLiveData.setValue(withContext(Dispatchers.IO) {
                loadImagesfromSDCard()
            })
        }
    }

}