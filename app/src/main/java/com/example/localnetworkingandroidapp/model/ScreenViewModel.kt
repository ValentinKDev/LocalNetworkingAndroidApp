package com.example.localnetworkingandroidapp.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localnetworkingandroidapp.ui.screen.ConnectionButtonState
import com.example.localnetworkingandroidapp.ui.screen.getScreenLayoutConstraints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class ScreenViewModel() : ViewModel() {
    private val TAG = "ScreenVM"
    val constraints = getScreenLayoutConstraints()
    val info = ConnectionInfo()
    val listeners = Listeners()
    val connectionVM = ConnectionViewModel(listeners)

    private val _textInput = MutableStateFlow("Say Something...")
    val textInput: StateFlow<String> = _textInput.asStateFlow()
    fun handleKeyboardInput(str: String) {
        _textInput.value += str
    }

    private val _connectionButton = MutableStateFlow(ConnectionButtonState.Join.string)
    val connectionButton: StateFlow<String> = _connectionButton.asStateFlow()
    fun connectionButtonSwitch() {
        when (_connectionButton.value) {
            ConnectionButtonState.Leave.string -> _connectionButton.value =
                ConnectionButtonState.Join.string
            ConnectionButtonState.Join.string -> _connectionButton.value =
                ConnectionButtonState.Leave.string
        }
    }
    fun connectionButtonClick() {
        connectionButtonSwitch()
        Log.e(TAG, "click on connection button")
//        if ( WifiConnectionState.connected ) {
        if (WifiConnectionState.isHosting() || WifiConnectionState.connected) {
            Log.i(
                TAG,
                "is Hosting ${WifiConnectionState.isHosting()} connected ${WifiConnectionState.connected}"
            )
            Log.i(TAG, "stop discovery")
            try {
//                WifiConnectionState.nsdManager.stopServiceDiscovery(listeners.discoveryListener)
                WifiConnectionState.nsdManager.stopServiceDiscovery(listeners.getDiscoveryListener())
            } catch (e: IllegalArgumentException) {
                Log.i("nsdManager", "discoveryListener not registered")
            }
            WifiConnectionState.cleanServerSocket()
            WifiConnectionState.cleanSocket()
        } else
            connectionVM.start()
    }


    fun handleSendButtonClick() {
        Log.e(TAG, "click on send button")
    }
}