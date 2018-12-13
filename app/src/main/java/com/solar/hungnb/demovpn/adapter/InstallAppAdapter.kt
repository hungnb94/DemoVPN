package com.solar.hungnb.demovpn.adapter

import android.content.pm.ApplicationInfo
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.solar.hungnb.demovpn.R

class InstallAppAdapter(private val listApp: ArrayList<ApplicationInfo>) : RecyclerView.Adapter<InstallAppAdapter.AppHolder>() {

    override fun onBindViewHolder(holder: AppHolder, position: Int) {
        val packageInfo = listApp[position]
        holder.tvName.text = packageInfo.loadLabel(holder.itemView.context.packageManager).toString()
        Log.d("Package name", packageInfo.packageName)
    }

    override fun getItemCount(): Int {
        return listApp.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppHolder {
        val inflater = LayoutInflater.from(parent.context)
        return AppHolder(inflater.inflate(R.layout.item_app, parent, false))
    }

    class AppHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.tvAppName)
    }

}