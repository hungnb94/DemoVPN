package com.solar.hungnb.demovpn.task

import android.os.AsyncTask

class SimpleTask(private var action: () -> Unit) : AsyncTask<Void, Void, Void>() {

    override fun doInBackground(vararg params: Void?): Void? {
        action()
        return null
    }
}