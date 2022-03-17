package dev.haenara.test.remotemonster

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.remotemonster.sdk.RemonCall
import com.remotemonster.sdk.data.AudioType
import org.webrtc.SurfaceViewRenderer


class MainActivity : AppCompatActivity() {
    private lateinit var remonCall: RemonCall

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val localRender: SurfaceViewRenderer =
            findViewById<View>(R.id.local_video_view) as SurfaceViewRenderer
        val remoteRender: SurfaceViewRenderer =
            findViewById<View>(R.id.remote_video_view) as SurfaceViewRenderer

//        val config = com.remotemonster.sdk.Config().apply {
//            videoCall = true
//            meta = hashMapOf()
//            audioType = AudioType.VOICE
//            logLevel = Log.DEBUG
//            localView = localRender
//            remoteView = remoteRender
//            findViewById<EditText>(R.id.tv).text.toString()
//                .takeIf { it.isNotBlank() }
//                ?.let {
//                    channelId = it
//                }
//        }

        findViewById<Button>(R.id.btn_connect).setOnClickListener {
            remonCall.connect(
                findViewById<EditText>(R.id.tv).text.toString()
                    .takeIf { it.isNotBlank() }
                    ?: "TEST1"
            )
        }
        findViewById<Button>(R.id.btn_disconnect).setOnClickListener {
            remonCall.close()
        }

        initRemonCall(localRender, remoteRender)

    }

    private fun initRemonCall(
        localRender: SurfaceViewRenderer,
        remoteRender: SurfaceViewRenderer
    ) {
        try {
            remonCall = RemonCall.builder()
                .context(this)
                .serviceId(BuildConfig.SERVICE_ID)
                .key(BuildConfig.API_KEY)
                .audioType(AudioType.VOICE)
                .logLevel(Log.DEBUG)
                .localView(localRender)
                .remoteView(remoteRender)
                .videoCodec("VP8")
                //                .videoWidth(640)
                //                .videoHeight(480)
                .onConnect { channelId ->
                    runOnUiThread {
                        setText(channelId)
                    }
                }
                .onError { it ->
                    it.printStackTrace()
                    runOnUiThread {
                        setText(it.message ?: it::javaClass.name)
                        Toast.makeText(this, it.localizedMessage ?: "", Toast.LENGTH_LONG).show()
                    }
                }
                .build()


        } catch (e: Exception) {
            e.printStackTrace()
            setText(e.message ?: e::javaClass.name)

            findViewById<Button>(R.id.btn_connect).isEnabled = false
            findViewById<Button>(R.id.btn_disconnect).isEnabled = false

            localRender.isVisible = false
            remoteRender.isVisible = false
        }
    }

    private fun setText(message: String) {
        runOnUiThread {
            findViewById<EditText>(R.id.tv).setText(message)
        }
    }
}
