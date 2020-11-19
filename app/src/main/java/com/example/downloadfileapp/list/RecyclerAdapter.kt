package com.example.downloadfileapp.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.downloadfileapp.R
import com.example.downloadfileapp.database.DownloadedFileEntity
import java.util.*

class RecyclerAdapter(var ctx: Context, private val actionListener: ListItemAction) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var downloadedFileList: List<DownloadedFileEntity> = ArrayList()
    fun setData(fileList: List<DownloadedFileEntity>) {
        downloadedFileList = fileList
        notifyDataSetChanged()
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txt_fileName: TextView
        var btn_play: ImageView
        var btn_delete: ImageView

        init {
            txt_fileName = itemView.findViewById(R.id.fileName)
            btn_play = itemView.findViewById(R.id.btn_play)
            btn_delete = itemView.findViewById(R.id.btn_delete)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val Vw: RecyclerView.ViewHolder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        Vw = RecyclerViewHolder(view)
        return Vw
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val fileData = downloadedFileList[position]
        val viewHolder = holder as RecyclerViewHolder
        viewHolder.txt_fileName.text = fileData.title
        viewHolder.btn_delete.setOnClickListener { actionListener.deleteFileListener(fileData) }
        viewHolder.btn_play.setOnClickListener { actionListener.playFileListener(fileData) }
        // viewHolder.fileProgress.setProgress(Integer.parseInt(fileData.getProgress()));
        /*   if (fileData.isIs_Paused()) viewHolder.btn_pause_resume.setText("Resume");
        else viewHolder.btn_pause_resume.setText("Pause");
        viewHolder.btn_pause_resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileData.isIs_Paused()){
                    fileData.setIs_Paused(false);
                    viewHolder.btn_pause_resume.setText("Resume");
                }else{
                    fileData.setIs_Paused(true);
                    viewHolder.btn_pause_resume.setText("Pause");
                }
            }
        });*/
    }

    override fun getItemCount(): Int {
        return downloadedFileList.size
    }
}