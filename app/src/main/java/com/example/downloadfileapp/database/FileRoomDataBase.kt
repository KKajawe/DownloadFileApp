package com.example.downloadfileapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DownloadedFileEntity::class], version = 1, exportSchema = false)
abstract class FileRoomDataBase : RoomDatabase() {

    abstract fun filesDAO(): FilesDAO

    companion object {
        var TEST_MODE = false
        @Volatile private var instance: FileRoomDataBase? = null
        private val LOCK = Any()


        fun invoke(context: Context): FileRoomDataBase {
            if (instance == null) {
                if(TEST_MODE){
                    instance =   Room.inMemoryDatabaseBuilder(context, FileRoomDataBase::class.java).allowMainThreadQueries().build()
                }else{
                    instance =  Room.databaseBuilder(context,
                        FileRoomDataBase::class.java, "file")
                        .build()
                }
            }
            return instance!!;
        }

    }
}