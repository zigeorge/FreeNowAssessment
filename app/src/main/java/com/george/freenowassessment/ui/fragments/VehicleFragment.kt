package com.george.freenowassessment.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.george.freenowassessment.R
import com.george.freenowassessment.databinding.FragmentVehicleBinding
import com.george.freenowassessment.ui.VehicleListViewModel
import com.george.freenowassessment.ui.adapters.VehicleRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of VehicleData.
 */
@AndroidEntryPoint
class VehicleFragment : Fragment(R.layout.fragment_vehicle) {

    private val vehicleRecyclerViewAdapter = VehicleRecyclerViewAdapter()
    private val viewModel by activityViewModels<VehicleListViewModel>()

    private var _binding: FragmentVehicleBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVehicleBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the adapter
        _binding?.list?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = vehicleRecyclerViewAdapter
        }
        lifecycleScope.launchWhenResumed {
            viewModel.vehicleList.collectLatest {
                vehicleRecyclerViewAdapter.submitData(it)
            }
        }
        /** to clear selection from [VehicleListViewModel] when returned from [MapsFragment]*/
        lifecycleScope.launchWhenResumed {
            viewModel.removeVehicleSelection()
        }
        vehicleRecyclerViewAdapter.setOnItemClickListener { singleVehicle ->
            singleVehicle?.let {
                viewModel.onVehicleSelected(it)
            }
        }
    }

}