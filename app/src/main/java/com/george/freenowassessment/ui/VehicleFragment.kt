package com.george.freenowassessment.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.george.freenowassessment.R
import javax.inject.Inject

/**
 * A fragment representing a list of Items.
 */
class VehicleFragment : Fragment() {

    @Inject lateinit var vehicleRecyclerViewAdapter: VehicleRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_vehicle_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = vehicleRecyclerViewAdapter
            }
        }
        vehicleRecyclerViewAdapter.setOnItemClickListener {
            findNavController().navigate(
                VehicleFragmentDirections
                    .actionVehicleFragmentToMapsFragment()
            )
        }
        return view
    }

}