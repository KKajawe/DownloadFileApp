package com.example.downloadfileapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.Status
import com.example.downloadfileapp.util.Utils.getProgressDisplayLine
import com.example.downloadfileapp.util.Utils.getRootDirPath
import com.example.downloadfileapp.database.DownloadedFileEntity
import com.example.downloadfileapp.list.ListItemAction
import com.example.downloadfileapp.list.RecyclerAdapter
import com.example.downloadfileapp.util.CustomDialogClass
import com.example.downloadfileapp.util.MainActivityUtility
import com.example.downloadfileapp.viewModel.FileViewModel
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity(), ListItemAction {
    private lateinit var mainUtility: MainActivityUtility
    private var list_RecyclerVw: RecyclerView? = null
    private var urlEdt: EditText? = null
    private var download: ImageButton? = null
    private var btn_cancel: ImageButton? = null
    private var rAdapter: RecyclerAdapter? = null
    private var viewModel: FileViewModel? = null
    private var downLoadId = 0
    private var progress_layout: RelativeLayout? = null
    private var progressBar: ProgressBar? = null
    private var txt_progress: TextView? = null
    private var fileSize: String? = null
    private var isPaused = false
    private var fileEntityList: List<DownloadedFileEntity?>? = ArrayList()
    private var filesList: LiveData<List<DownloadedFileEntity?>?>? = null
    private var dialog: CustomDialogClass? = null
    private var btn_pauseResume: Button? = null
    var expandedURL = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainActivity = this
        mainUtility = MainActivityUtility()
        init()
        download!!.setOnClickListener { v ->
            val str = urlEdt!!.text.toString()
            if (str.length != 0) {
                // hide softkeypad
                val inputMethodManager = mainActivity!!.getSystemService(
                    INPUT_METHOD_SERVICE
                ) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    mainActivity!!.currentFocus!!.windowToken, 0
                )
                if (mainUtility.checkFileAlreadyDownloaded(str, filesList?.value)) {
                    dialog!!.showDialog(getString(R.string.file_present))
                } else {
                    v.visibility = View.GONE
                    btn_pauseResume!!.visibility = View.VISIBLE
                    btn_pauseResume!!.text = "Pause"
                    isPaused = false
                    //expand shortner url then download file
                    ExpandUrl().execute(str)
                }
            } else dialog!!.showDialog(getString(R.string.validUrl))
        }

        btn_cancel!!.setOnClickListener(
            View.OnClickListener {
                if (downLoadId != null) {
                    PRDownloader.cancel(downLoadId);
                    btn_pauseResume!!.visibility = View.GONE
                    download!!.visibility = View.VISIBLE
                    download!!.isEnabled = true
                    progress_layout!!.visibility = View.GONE
                }
            }
        )
        btn_pauseResume!!.setOnClickListener {
            if (isPaused) {
                isPaused = false
                btn_pauseResume!!.text = "Pause"
                PRDownloader.resume(downLoadId)
            } else {
                isPaused = true
                btn_pauseResume!!.text = "Resume"
                PRDownloader.pause(downLoadId)
            }
        }
    }

//initialize UI
    private fun init() {
        progress_layout = findViewById(R.id.progress_layout)
        progressBar = findViewById(R.id.dwnld_progress)
        txt_progress = findViewById(R.id.status_txt)
        urlEdt = findViewById(R.id.edt_url)
        download = findViewById(R.id.btn_download)
        btn_pauseResume = findViewById(R.id.btn_pauseResume)
        btn_cancel = findViewById(R.id.btn_cancel);
        list_RecyclerVw = findViewById(R.id.lst_files)
        rAdapter = RecyclerAdapter(mainActivity!!, this)
        list_RecyclerVw?.setLayoutManager(LinearLayoutManager(mainActivity))
        list_RecyclerVw?.setAdapter(rAdapter)
        viewModel = FileViewModel(applicationContext as FileDownloaderApp)
        viewModel!!.allDownlodedFiles!!.observe(this, fileListUpdateObserver)
        filesList = viewModel!!.allDownlodedFiles
        dialog = CustomDialogClass(mainActivity!!)

        if (!mainUtility.checkPermission(applicationContext as FileDownloaderApp)) {
            mainUtility.requestPermission()
        } else {
            dialog!!.showDialog(getString(R.string.permission_granted))
        }
    }

//download file request
    fun downLoadRequest(url: String?) {
        progress_layout!!.visibility = View.VISIBLE
        if (Status.RUNNING == PRDownloader.getStatus(downLoadId)) {
            PRDownloader.pause(downLoadId)
            return
        }
        download!!.isEnabled = false
        progressBar!!.isIndeterminate = true
        progressBar!!.indeterminateDrawable.setColorFilter(
            Color.BLUE, PorterDuff.Mode.SRC_IN
        )
        if (Status.PAUSED == PRDownloader.getStatus(downLoadId)) {
            PRDownloader.resume(downLoadId)
            return
        }
        val fileName = mainUtility.getFileNameFromURL(expandedURL)
        val dirPath = getRootDirPath(applicationContext)
        downLoadId =  /*download id*/PRDownloader.download(url, dirPath, fileName)
            .build()
            .setOnStartOrResumeListener {
                progressBar!!.isIndeterminate = false
                //download.setEnabled(true);
                //download.setText(R.string.pause);

                // buttonCancelTwo.setEnabled(true);
                //  buttonCancelTwo.setText(R.string.cancel);
            }
            .setOnPauseListener {
                //   isPaused = true;

                //download.setText(R.string.resume);
            }
            .setOnCancelListener { }
            .setOnProgressListener { progress ->
                fileSize = progress.totalBytes.toString()
                val progressPercent = progress.currentBytes * 100 / progress.totalBytes
                progressBar!!.progress = progressPercent.toInt()
                txt_progress!!.text =
                    getProgressDisplayLine(progress.currentBytes, progress.totalBytes)
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    val id = UUID.randomUUID().toString()
                    val file = DownloadedFileEntity(
                        id,
                        downLoadId.toLong(),
                        fileName,
                        dirPath,
                        progressBar!!.progress.toString(),
                        "completed",
                        fileSize!!,
                        false,
                        url!!,
                        expandedURL
                    )
                    /*        if(isPaused)
                    {

                    }else {

                        viewModel.update(file);
                    }*/viewModel!!.insert(file)
                    viewModel!!.allDownlodedFiles
                    urlEdt!!.text.clear()
                    dialog!!.dialogNegativeButton.visibility = View.GONE
                    dialog!!.showDialogWithSingleActionBtn(getString(R.string.download_complete)) {
                        dialog!!.cancelDialog()
                        progress_layout!!.visibility = View.GONE
                        download!!.visibility = View.VISIBLE
                        btn_pauseResume!!.visibility = View.GONE
                        download!!.isEnabled = true
                    }
                }

                override fun onError(error: Error) {
                    Log.d("error", error.toString())
                    dialog!!.showDialog(error.toString())
                    urlEdt!!.text.clear()
                    download!!.isEnabled = true
                    progress_layout!!.visibility = View.GONE
                    download!!.visibility = View.VISIBLE
                    btn_pauseResume!!.visibility = View.GONE
                    download!!.isEnabled = true
                }
            })
    }
//liveData observer
    var fileListUpdateObserver =
        Observer<List<DownloadedFileEntity?>?> { fileList -> rAdapter!!.setData(fileList as List<DownloadedFileEntity>) }

//delete list Item Listner
    override fun deleteFileListener(downloadedFileEntity: DownloadedFileEntity?) {
        dialog!!.showDialogWithAction(getString(R.string.delete_msg)) {
            dialog!!.cancelDialog()
            val dir = downloadedFileEntity!!.filePath
            val file = File(dir, downloadedFileEntity.title)
            val deleted = file.delete()
            if (deleted) viewModel!!.delete(downloadedFileEntity) else {
                if (viewModel!!.allDownlodedFiles!!.value!!.contains(downloadedFileEntity)) viewModel!!.delete(
                    downloadedFileEntity
                ) else dialog!!.showDialog(getString(R.string.delete_error))
            }
        }
    }

    //open list Item Listener
    override fun playFileListener(downloadedFileEntity: DownloadedFileEntity?) {
        val dir = downloadedFileEntity!!.filePath
        val file = File(dir, downloadedFileEntity.title)
        val intentUri = FileProvider.getUriForFile(
            this,
            this.applicationContext.packageName + ".provider",
            file
        )
        var type: String? = null
        type = if (downloadedFileEntity.title.lastIndexOf(".") != -1) {
            val ext =
                downloadedFileEntity.title.substring(downloadedFileEntity.title.lastIndexOf(".") + 1)
            val mime = MimeTypeMap.getSingleton()
            mime.getMimeTypeFromExtension(ext)
        } else {
            null
        }
        val myIntent = Intent(Intent.ACTION_VIEW)
        myIntent.setDataAndType(intentUri, type)
        myIntent.flags =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        if (myIntent.resolveActivity(packageManager) != null) {
            mainActivity!!.startActivity(myIntent)
        } else {
            dialog!!.showDialog(getString(R.string.file_open_error))
        }
    }

    override fun onBackPressed() {
        dialog!!.showDialogWithAction(getString(R.string.exit_msg)) {
            dialog!!.cancelDialog()
            finish()
        }
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 200
        var mainActivity: MainActivity? = null
    }

    //request READ WRITE permissions
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MainActivity.PERMISSION_REQUEST_CODE -> if (grantResults.size > 0) {
                val locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (locationAccepted && cameraAccepted) Toast.makeText(
                    MainActivity.mainActivity,
                    "Permission Granted, Now you can write and read data in storage.!",
                    Toast.LENGTH_SHORT
                ).show() else {
                    Toast.makeText(
                        this,
                        "Permission Denied, You cannot write or read data in storage",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            mainUtility.showMessageOKCancel(
                                "You need to allow access to both the permissions"
                            ) { dialog, which ->
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(
                                        arrayOf(
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE
                                        ),
                                        MainActivity.PERMISSION_REQUEST_CODE
                                    )
                                }
                            }
                            return
                        }
                    }
                }
            }
        }
    }
}