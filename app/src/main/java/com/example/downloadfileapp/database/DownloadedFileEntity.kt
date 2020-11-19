package com.example.downloadfileapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Files")
data class DownloadedFileEntity(@field:PrimaryKey var id: String, var downloadId: Long, var title: String, var filePath: String,
                                var progress: String, var status: String, var fileSize: String, var isIs_Paused: Boolean,
                                var shortenedURL: String, var expandedURL: String)