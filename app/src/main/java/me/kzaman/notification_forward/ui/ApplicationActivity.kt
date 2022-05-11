package me.kzaman.notification_forward.ui

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import me.kzaman.notification_forward.R
import me.kzaman.notification_forward.adapter.ApplicationAdapter
import me.kzaman.notification_forward.data.model.ApplicationModel

@AndroidEntryPoint
class ApplicationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var applicationAdapter: ApplicationAdapter

    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("Message", "Start")

        recyclerView = findViewById(R.id.appList)

        val pm = packageManager
        //get a list of installed apps.
        val packages = pm.getInstalledPackages(PackageManager.GET_META_DATA)
        val applicationModel = ArrayList<ApplicationModel>()
        packages.forEach { packageInfo ->
            val item = ApplicationModel(
                appName = packageInfo.applicationInfo.loadLabel(packageManager).toString(),
                packageName = packageInfo.packageName,
                appIcon = packageInfo.applicationInfo.loadIcon(packageManager)
            )
            applicationModel.add(item)
        }
        Log.d("Message", packages.toString())

        applicationAdapter = ApplicationAdapter(applicationModel, applicationContext)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = applicationAdapter
        }
        Log.d("Message", "Done")
    }
}