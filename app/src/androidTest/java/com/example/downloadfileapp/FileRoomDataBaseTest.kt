package com.example.downloadfileapp

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.downloadfileapp.database.FileRoomDataBase.Companion.TEST_MODE
import com.example.downloadfileapp.database.FileRoomDataBase.Companion.invoke
import com.example.downloadfileapp.database.DownloadedFileEntity
import com.example.downloadfileapp.database.FilesDAO
import com.example.downloadfileapp.util.MainActivityUtility
import org.junit.*
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import androidx.test.platform.app.InstrumentationRegistry as InstrumentationRegistry1
import com.google.common.truth.Truth.assertThat


@RunWith(AndroidJUnit4::class)


class FileRoomDataBaseTest: MainActivityUtility() {

    private var fileDAO: FilesDAO? = null
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    val fileEntity = DownloadedFileEntity(
        "123123",
        "345435".toLong(),
        "fileName",
        "dirPath",
        "20",
        "completed",
        "40mb",
        false,
        "http://androhub.com/demo/demo.doc",
        ""
    )
    @Before
    fun setup() {
        TEST_MODE = true
        val context: Context = InstrumentationRegistry1.getInstrumentation().getTargetContext().getApplicationContext()
        var db = invoke(context);
        fileDAO = db.filesDAO()
    }

    @After
    fun tearDown() {

    }
    //Test for insert operation of Room DB
    @Test
    fun should_Insert_File_Item() {

        fileDAO?.insert(fileEntity);
        val fileTest = fileDAO?.getFile(fileEntity.id)!!.getOrAwaitValue()
        Assert.assertEquals(fileEntity.title, fileTest?.title)
    }

    //Test for delete operation of Room DB
    @Test
    fun should_delete_file(){
        fileDAO?.delete(fileEntity);
        Assert.assertEquals(fileDAO?.getFilesCount(), 0)
    }

    //function to get file enitity from liveData
    fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        afterObserve: () -> Unit = {}
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(o: T?) {
                data = o
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }
        this.observeForever(observer)

        afterObserve.invoke()

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            this.removeObserver(observer)
            throw TimeoutException("LiveData value was never set.")
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }

//Test for checkFileAlreadyDownloaded function
    @Test
    fun checkFileAlreadyDownloadedTest(){
        val url : String = "http://androhub.com/demo/demo.mp3"
        var fileList = ArrayList<DownloadedFileEntity>();
        fileList.add(fileEntity);
        var result = checkFileAlreadyDownloaded(url, fileList);
        assertThat(result).isTrue();
    }
}

