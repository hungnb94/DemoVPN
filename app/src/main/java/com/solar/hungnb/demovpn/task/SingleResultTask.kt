package com.solar.hungnb.demovpn.task

import android.os.AsyncTask


class SingleResultTask<H> : AsyncTask<Void, Void, H>() {
    private var action: (() -> H)? = null
    private var onFinished: ((result: H?) -> Unit)? = null

    fun setAction(action: () -> H): SingleResultTask<H> {
        this.action = action
        return this
    }

    fun onFinished(onFinished: (result: H?) -> Unit): SingleResultTask<H> {
        this.onFinished = onFinished
        return this
    }

    override fun doInBackground(vararg params: Void?): H? {
        return action?.invoke()
    }

    override fun onPostExecute(result: H?) {
        this.onFinished?.invoke(result)
    }
}