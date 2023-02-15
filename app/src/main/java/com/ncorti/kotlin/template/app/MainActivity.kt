package com.ncorti.kotlin.template.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ncorti.kotlin.template.app.databinding.ActivityMainBinding
import com.walletconnect.android.Core
import com.walletconnect.android.CoreClient
import com.walletconnect.android.relay.ConnectionType
import com.walletconnect.web3.wallet.client.Wallet
import com.walletconnect.web3.wallet.client.Web3Wallet
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode
import timber.log.Timber

class MainActivity : AppCompatActivity(), CoreClient.CoreDelegate, Web3Wallet.WalletDelegate {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonCompute.setOnClickListener {
            scanQrCodeLauncher.launch(null)
        }

        val projectId = ""
        val relayUrl = "relay.walletconnect.com"
        val serverUrl = "wss://$relayUrl?projectId=$projectId"
        val appMetaData = Core.Model.AppMetaData(
            name = "Mixin Wallet",
            url = "https://mixin.one",
            description = "Mixin Wallet",
            icons = emptyList(),
            redirect = null,
        )
        CoreClient.initialize(
            relayServerUrl = serverUrl,
            connectionType = ConnectionType.AUTOMATIC,
            application = this.application,
            metaData = appMetaData,
            onError = { error ->
                Timber.d("$TAG CoreClient init error: $error")
            },
        )
        val initParams = Wallet.Params.Init(core = CoreClient)
        Web3Wallet.initialize(initParams) { error ->
            Timber.d("$TAG Web3Wallet init error: $error")
        }
        CoreClient.setDelegate(this)
        Web3Wallet.setWalletDelegate(this)
    }

    private val scanQrCodeLauncher = registerForActivityResult(ScanQRCode()) { result: QRResult ->
        if (result is QRResult.QRSuccess) {
            val uri = result.content.rawValue
            Timber.d("$TAG uri: $uri")
            val pairParams = Wallet.Params.Pair(uri)
            Web3Wallet.pair(pairParams, { pair ->
                Timber.d("$TAG pair success $pair")
            }) { error ->
                Timber.d("$TAG pair $uri, error: $error")
            }
        }
    }

    override fun onPairingDelete(deletedPairing: Core.Model.DeletedPairing) {
        Timber.d("$TAG onPairingDelete $deletedPairing")
    }

    override fun onAuthRequest(authRequest: Wallet.Model.AuthRequest) {
        Timber.d("$TAG onAuthRequest $authRequest")
    }

    override fun onConnectionStateChange(state: Wallet.Model.ConnectionState) {
        Timber.d("$TAG onConnectionStateChange $state")
    }

    override fun onError(error: Wallet.Model.Error) {
        Timber.d("$TAG onError $error")
    }

    override fun onSessionDelete(sessionDelete: Wallet.Model.SessionDelete) {
        Timber.d("$TAG onSessionDelete $sessionDelete")
    }

    override fun onSessionProposal(sessionProposal: Wallet.Model.SessionProposal) {
        Timber.d("$TAG onSessionProposal $sessionProposal")
    }

    override fun onSessionRequest(sessionRequest: Wallet.Model.SessionRequest) {
        Timber.d("$TAG onSessionRequest $sessionRequest")
    }

    override fun onSessionSettleResponse(settleSessionResponse: Wallet.Model.SettledSessionResponse) {
        Timber.d("$TAG onSettleSessionResponse $settleSessionResponse")
    }

    override fun onSessionUpdateResponse(sessionUpdateResponse: Wallet.Model.SessionUpdateResponse) {
        Timber.d("$TAG onSessionUpdateResponse $sessionUpdateResponse")
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
