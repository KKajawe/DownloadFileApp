package com.example.downloadfileapp.viewModel

import android.os.AsyncTask
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.downloadfileapp.FileDownloaderApp
import com.example.downloadfileapp.database.DownloadedFileEntity
import com.example.downloadfileapp.database.FileRoomDataBase
import com.example.downloadfileapp.database.FilesDAO

class FileViewModel(private val application: FileDownloaderApp) : AndroidViewModel(application) {
    var allDownlodedFiles: LiveData<List<DownloadedFileEntity?>?>?

    // ArrayList<DownLoadedFileEntity> fileModelArrayList;
    private val fileDB: FileRoomDataBase?
    private val filesDAO: FilesDAO?
    private val flag = false
    fun insert(file: DownloadedFileEntity?) {
        InsertAsyncTask(filesDAO).execute(file)
    }

    fun update(file: DownloadedFileEntity?) {
        UpdateAsyncTask(filesDAO).execute(file)
    }

 //   val pendingFile: DownloadedFileEntity?
       // get() = filesDAO!!.getFile(true)!!.value

    fun delete(fileEntity: DownloadedFileEntity?) {
        DeleteAsyncTask(filesDAO).execute(fileEntity)
    }
    private inner class InsertAsyncTask(var filesDAO: FilesDAO?) : AsyncTask<DownloadedFileEntity?, Void?, Void?>() {
        override fun doInBackground(vararg params: DownloadedFileEntity?): Void? {
            filesDAO!!.insert(params[0])
            return null
        }
    }

    private inner class UpdateAsyncTask(var filesDAO: FilesDAO?) : AsyncTask<DownloadedFileEntity?, Void?, Void?>() {
         override fun doInBackground(vararg downloadedFileEntities: DownloadedFileEntity?): Void? {
            filesDAO!!.update(downloadedFileEntities[0])
            return null
        }
    }

    private inner class DeleteAsyncTask(var filesDAO: FilesDAO?) : AsyncTask<DownloadedFileEntity?, Void?, Void?>() {
         override fun doInBackground(vararg downloadedFileEntities: DownloadedFileEntity?): Void? {
            filesDAO!!.delete(downloadedFileEntities[0])
            return null
        }
    }

    init {
        fileDB = FileRoomDataBase.invoke(application)
        filesDAO = fileDB!!.filesDAO()
        allDownlodedFiles = filesDAO!!.allFiles
    }
}