package com.example.web3mall.activities

import android.app.Application
import android.content.res.Configuration
import com.example.web3mall.R
import com.walletconnect.android.Core
import com.walletconnect.android.CoreClient
import com.walletconnect.android.relay.ConnectionType
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import timber.log.Timber

class MyCustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val projectId = "10fd3420ee9a1d352a6fefddebb18c0c" //Get Project ID at https://cloud.walletconnect.com/
        val relayUrl = "relay.walletconnect.com"
        val serverUrl = "wss://$relayUrl?projectId=$projectId"
        val connectionType = ConnectionType.AUTOMATIC // or ConnectionType.MANUAL
        val appMetaData = Core.Model.AppMetaData(
            name = "Test App",
            description = "Kotlin App",
            url = "Test App url",
            icons = listOf("https://gblobscdn.gitbook.com/spaces%2F-LJJeCjcLrr53DcT1Ml7%2Favatar.png?alt=media"),
            redirect = getString(R.string.deep_link_url)  // Custom Redirect URI
        )
        CoreClient.initialize(
            relayServerUrl = serverUrl,
            connectionType = connectionType,
            application = this,
            metaData = appMetaData
        ) { error ->
            Timber.tag("CoreClient_Initialization_Error").e(error.throwable.stackTraceToString())
        }
        val init = Sign.Params.Init(core = CoreClient)
        SignClient.initialize(init) { error ->
            Timber.tag("SignClient_Initialization_Error").e(error.throwable.stackTraceToString())
        }

        fun onConfigurationChanged(newConfig: Configuration) {
            super.onConfigurationChanged(newConfig)
        }

        fun onLowMemory() {
            super.onLowMemory()
        }
    }
}