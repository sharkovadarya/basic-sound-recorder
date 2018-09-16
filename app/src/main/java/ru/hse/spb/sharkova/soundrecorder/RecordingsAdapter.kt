package ru.hse.spb.sharkova.soundrecorder

import android.content.Context
import android.media.MediaPlayer
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.recordings_list_item.view.*
import java.io.File
import java.io.IOException

class RecordingsAdapter(private val items: MutableList<File>, private val context: Context,
                        private val isRecording: Boolean, private var player: MediaPlayer)
    : RecyclerView.Adapter<ViewHolder>() {

    private var currentPos = -1

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recordings_list_item, parent, false)
        return ViewHolder(view).listen { pos, _ ->

            if (isRecording) {
                return@listen
            }

            val item = items[pos]

            if (player.isPlaying) {
                player.reset()
                if (pos == currentPos) {
                    return@listen
                }
            }

            try {
                player = MediaPlayer()
                player.setDataSource(item.absolutePath)
            } catch (e: IOException) {}

            try {
                player.prepare()
            } catch (e: IOException) {}

            player.start()
            currentPos = pos
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.tvRecordingName?.text = String.format(context.resources.getString(R.string.recording),
                items[pos].name.split(" ")[0])
    }


}


fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
    itemView.setOnClickListener {
        event.invoke(adapterPosition, itemViewType)
    }
    return this
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val tvRecordingName: TextView? = view.text_view_recording_name
}

