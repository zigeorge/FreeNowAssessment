package com.george.freenowassessment.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.george.freenowassessment.databinding.FragmentVehicleBinding
import com.george.freenowassessment.ui.presenter.VehicleListPresenter

/**
 * [RecyclerView.Adapter] that can display a [VehicleListPresenter.SingleVehicle].
 */
class VehicleRecyclerViewAdapter :
    ListAdapter<VehicleListPresenter.SingleVehicle,
            VehicleRecyclerViewAdapter.ViewHolder>(VehicleDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentVehicleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.vehicleType.text = item.type
        holder.location.text = item.location
    }

    /** item click listener with [VehicleListPresenter.SingleVehicle] as param */
    private var onItemClickListener: ((VehicleListPresenter.SingleVehicle) -> Unit)? = null

    fun setOnItemClickListener(listener: (VehicleListPresenter.SingleVehicle) -> Unit) {
        onItemClickListener = listener
    }

    inner class ViewHolder(binding: FragmentVehicleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val vehicleType: TextView = binding.vehicleType
        val location: TextView = binding.location

        override fun toString(): String {
            return super.toString() + " '" + vehicleType.text + "'"
        }
    }
}

class VehicleDiff : DiffUtil.ItemCallback<VehicleListPresenter.SingleVehicle>() {
    override fun areItemsTheSame(
        oldItem: VehicleListPresenter.SingleVehicle,
        newItem: VehicleListPresenter.SingleVehicle
    ): Boolean {
        return oldItem.vehicle.id == newItem.vehicle.id
    }

    override fun areContentsTheSame(
        oldItem: VehicleListPresenter.SingleVehicle,
        newItem: VehicleListPresenter.SingleVehicle
    ): Boolean {
        return oldItem == newItem
    }
}