package com.solar.hungnb.demovpn.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.solar.hungnb.demovpn.R
import com.solar.hungnb.demovpn.model.AppInfoWrapper

class InstallAppAdapter(
        val listApp: List<AppInfoWrapper>,
        var listener: OnSelectItemAppListener? = null
) : RecyclerView.Adapter<InstallAppAdapter.AppHolder>() {
    private val TAG = "InstallAppAdapter"

    override fun onBindViewHolder(holder: AppHolder, position: Int) {
        val appInfo = listApp[position]
        holder.tvName.text = appInfo.applicationInfo.loadLabel(holder.itemView.context.packageManager).toString()

        //Keep this line, because checkbox keep listener before
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = appInfo.isFavorite


        //Set onClick
        holder.itemView.setOnClickListener {
            holder.checkBox.isChecked = !appInfo.isFavorite
        }

        //Handle selected change
        holder.checkBox.setOnCheckedChangeListener { checkBox, isChecked ->
                appInfo.isFavorite = isChecked
                listener?.onItemCheckChange(position, isChecked)
        }
    }

    override fun getItemCount(): Int {
        return listApp.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppHolder {
        val inflater = LayoutInflater.from(parent.context)
        return AppHolder(inflater.inflate(R.layout.item_app, parent, false))
    }

    fun setCheckAll() {
        listApp.filter { appInfoWrapper ->
            appInfoWrapper.isFavorite = true
            true
        }
        notifyDataSetChanged()
    }

    inner class AppHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var checkBox: CheckBox = itemView.findViewById(R.id.cbItemApp)
        var tvName: TextView = itemView.findViewById(R.id.tvItemAppName)
    }

    interface OnSelectItemAppListener {
        fun onItemCheckChange(position: Int, isCheck: Boolean)
    }

}