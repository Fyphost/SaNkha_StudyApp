package com.fyphost.sankhadip

import android.app.Application
import com.fyphost.sankhadip.data.AppDatabase
import com.fyphost.sankhadip.data.NodeRepository

class SanKhadipApp : Application() {
    val repository: NodeRepository by lazy {
        NodeRepository(this, AppDatabase.get(this).nodeDao())
    }
}
