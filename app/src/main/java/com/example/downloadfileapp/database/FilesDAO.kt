package com.example.downloadfileapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FilesDAO {
    @Insert
    fun insert(file: DownloadedFileEntity?)

    @Update
    fun update(file: DownloadedFileEntity?)

    @get:Query("SELECT * FROM Files")
    val allFiles: LiveData<List<DownloadedFileEntity?>?>?

    @Query("SELECT * FROM Files WHERE shortenedURL LIKE :shortURL")
    fun searchFile(shortURL: String?): DownloadedFileEntity?

    @Query("SELECT * FROM Files WHERE id=:fileid")
    fun getFile(fileid: String): LiveData<DownloadedFileEntity>
    @Delete
    fun delete(downloadedFileEntity: DownloadedFileEntity?): Int

    @Query("SELECT count(*) FROM Files")
    fun getFilesCount(): Int
}