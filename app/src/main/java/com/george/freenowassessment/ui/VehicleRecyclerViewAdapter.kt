package com.george.freenowassessment.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.george.freenowassessment.databinding.VehicleListItemBinding
import com.george.freenowassessment.ui.vo.SingleVehicle

/**
 * [RecyclerView.Adapter] that can display a [SingleVehicle].
 */
class VehicleRecyclerViewAdapter :
    PagingDataAdapter<SingleVehicle,
            VehicleRecyclerViewAdapter.ViewHolder>(VehicleDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            VehicleListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.vehicleType.text = item?.type
        holder.address.text = item?.address
        holder.state.text = item?.state
        holder.itemView.setOnClickListener{
            onItemClickListener?.let { click ->
                click(item)
            }
        }
    }

    /** item click listener with [SingleVehicle] as param */
    private var onItemClickListener: ((SingleVehicle?) -> Unit)? = null

    fun setOnItemClickListener(listener: (SingleVehicle?) -> Unit) {
        onItemClickListener = listener
    }

    inner class ViewHolder(binding: VehicleListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val vehicleType: TextView = binding.vehicleType
        val address: TextView = binding.address
        val state: TextView = binding.state

        override fun toString(): String {
            return super.toString() + " '" + vehicleType.text + "'"
        }
    }
}

class VehicleDiff : DiffUtil.ItemCallback<SingleVehicle>() {
    override fun areItemsTheSame(
        oldItem: SingleVehicle,
        newItem: SingleVehicle
    ): Boolean {
        return oldItem.vehicle.id == newItem.vehicle.id
    }

    override fun areContentsTheSame(
        oldItem: SingleVehicle,
        newItem: SingleVehicle
    ): Boolean {
        return oldItem == newItem
    }
}