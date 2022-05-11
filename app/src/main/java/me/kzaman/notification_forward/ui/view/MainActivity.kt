package me.kzaman.notification_forward.ui.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import me.kzaman.notification_forward.R
import me.kzaman.notification_forward.database.SharedPreferenceManager
import me.kzaman.notification_forward.ui.view.activities.AuthActivity
import me.kzaman.notification_forward.ui.view.activities.DashboardActivity
import me.kzaman.notification_forward.utils.startNewActivityAnimation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var prefManager: SharedPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val progressBar = findViewById<ProgressBar>(R.id.m_progress_bar)
        val motionLayout = findViewById<MotionLayout>(R.id.motion_layout)

        motionLayout.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
            ) {
                // TODO Started transition
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float,
            ) {
                // TODO Change transition
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                Handler(Looper.getMainLooper()).postDelayed(
                    { //This method will be executed once the timer is over
                        progressBar.visibility = View.VISIBLE
                    },
                    100,
                )
                Handler(Looper.getMainLooper()).postDelayed(
                    { //This method will be executed once the timer is over
                        if (prefManager.getIsUserLoggedIn()) {
                            startNewActivityAnimation(DashboardActivity::class.java)
                        } else {
                            startNewActivityAnimation(AuthActivity::class.java)
                        }
                    },
                    800,
                )
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float,
            ) {
                // TODO onTransitionTrigger
            }

        })
    }
}