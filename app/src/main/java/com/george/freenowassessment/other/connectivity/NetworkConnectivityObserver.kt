package com.george.freenowassessment.other.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class NetworkConnectivityObserver(
    private val context: Context
): ConnectivityObserver {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * this function observe for connectivity changes and notify using a callback flow
     * only two status is maintained here (Available, Unavailable)
     * support added for minimum API Level 21
     * */
    override fun observe(): Flow<ConnectivityObserver.Status> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            callbackFlow {
                val callback = object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        super.onAvailable(network)
                        launch { send(ConnectivityObserver.Status.Available) }
                    }

                    override fun onLosing(network: Network, maxMsToLive: Int) {
                        super.onLosing(network, maxMsToLive)
                        launch { send(ConnectivityObserver.Status.Unavailable) }
                    }

                    override fun onLost(network: Network) {
                        super.onLost(network)
                        launch { send(ConnectivityObserver.Status.Unavailable) }
                    }

                    override fun onUnavailable() {
                        super.onUnavailable()
                        launch { send(ConnectivityObserver.Status.Unavailable) }
                    }
                }
                connectivityManager.registerDefaultNetworkCallback(callback)
                awaitClose {
                    connectivityManager.unregisterNetworkCallback(callback)
                }
            }.distinctUntilChanged()
        } else {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val nInfo = cm.activeNetworkInfo
            val status = if((nInfo != null) && nInfo.isAvailable && nInfo.isConnected) {
                ConnectivityObserver.Status.Available
            } else ConnectivityObserver.Status.Unavailable
            return flow {
                emit(status)
            }
        }
    }

}