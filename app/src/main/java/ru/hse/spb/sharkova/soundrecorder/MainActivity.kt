package ru.hse.spb.sharkova.soundrecorder

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.TextView
import java.io.File
import java.io.IOException
import android.support.v7.widget.DividerItemDecoration


class MainActivity : AppCompatActivity() {
    private lateinit var statusTextView: TextView
    private lateinit var startRecordingButton: Button
    private lateinit var stopRecordingButton: Button
    private lateinit var recordingRecyclerView: RecyclerView
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var player: MediaPlayer
    private lateinit var file: File
    private val recordings = mutableListOf<File>()
    private var cnt = 1

    private fun initializeLayout() {
        statusTextView = findViewById(R.id.statusTextView)
        startRecordingButton = findViewById(R.id.startRecordingButton)
        stopRecordingButton = findViewById(R.id.stopRecordingButton)
        recordingRecyclerView = findViewById(R.id.recordingsRecyclerView)

        player = MediaPlayer()

        stopRecordingButton.isEnabled = false
        statusTextView.text = getString(R.string.readyToRecord)

        startRecordingButton.setOnClickListener {
            mediaRecorder = MediaRecorder()
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            val path = File (Environment.getExternalStorageDirectory().path)
            try {
                file = File.createTempFile ((cnt++).toString() + "  ", ".3gpp", path)
            } catch (e: IOException) { }

            mediaRecorder.setOutputFile(file.absolutePath)
            try {
                mediaRecorder.prepare()
            } catch (e: IOException) { }

            mediaRecorder.start()
            statusTextView.text = getString(R.string.currentlyRecording)
            startRecordingButton.isEnabled = false
            stopRecordingButton.isEnabled = true
            updateRecordings(true)
        }

        stopRecordingButton.setOnClickListener {
            mediaRecorder.stop()
            mediaRecorder.release()

            recordings.add(file)
            updateRecordings(false)

            startRecordingButton.isEnabled = true
            stopRecordingButton.isEnabled = false
            recordingRecyclerView.isEnabled = true
            recordingRecyclerView.isClickable = true
            statusTextView.text = getString(R.string.recordingFinished)
        }

        recordingRecyclerView.layoutManager = LinearLayoutManager(this)
        updateRecordings(false)

        recordingRecyclerView.addItemDecoration(DividerItemDecoration(recordingRecyclerView.context,
                DividerItemDecoration.VERTICAL))

    }

    private fun updateRecordings(isRecording: Boolean) {
        recordingRecyclerView.adapter = RecordingsAdapter(recordings, this, isRecording, player)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeLayout()
    }
}
