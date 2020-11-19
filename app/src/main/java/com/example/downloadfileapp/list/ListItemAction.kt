package com.example.downloadfileapp.list

import com.example.downloadfileapp.database.DownloadedFileEntity

interface ListItemAction {
    fun deleteFileListener(downloadedFileEntity: DownloadedFileEntity?)
    fun playFileListener(downloadedFileEntity: DownloadedFileEntity?)
}