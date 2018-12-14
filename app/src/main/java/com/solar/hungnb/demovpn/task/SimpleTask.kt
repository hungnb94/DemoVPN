package com.solar.hungnb.demovpn.task

import android.os.AsyncTask

class SimpleTask(private var action: () -> Unit) : AsyncTask<Void, Void, Void>() {

    fun setAction(action: () -> Unit): SimpleTask {
        this.action = action
        return this
    }

    override fun doInBackground(vararg params: Void?): Void? {
        action()
        return null
    }
}