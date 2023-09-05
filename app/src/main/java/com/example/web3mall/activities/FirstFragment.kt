package com.example.web3mall.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.web3mall.R
import com.example.web3mall.databinding.ActivityFirstFragmentBinding
import com.walletconnect.android.CoreClient
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import java.util.concurrent.TimeUnit

class FirstFragment : Fragment() {
    private lateinit var binding: ActivityFirstFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.activity_first_fragment,
            container,
            false
        )
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var apprSession: Sign.Model.ApprovedSession? = null
        var deeplinkPairingUri: String? = null
        binding.buttonPair.setOnClickListener {
            // Logic for buttonPair goes here
            // for proposer to create inactive pairing
            val pairing = CoreClient.Pairing.create()
            deeplinkPairingUri = pairing!!.uri
            val dappDelegate = object : SignClient.DappDelegate {
                override fun onSessionApproved(approvedSession: Sign.Model.ApprovedSession) {
                    apprSession = approvedSession
                    // Triggered when dapp receives the session approval from wallet
                    Log.d("Session_Approved", "Session was approved by the wallet")
                    Log.d("Session_Topic", "Approved session's topic is: "+ approvedSession.topic)
                }
                override fun onSessionRejected(rejectedSession: Sign.Model.RejectedSession) {
                    // Triggered when Dapp receives the session rejection from wallet
                    Log.d("Session_Rejected", "The wallet rejected the session")
                }
                override fun onSessionUpdate(updatedSession: Sign.Model.UpdatedSession) {
                    // Triggered when Dapp receives the session update from wallet
                    Log.d("Session_Updated", updatedSession.toString())
                }
                override fun onSessionExtend(session: Sign.Model.Session) {
                    // Triggered when Dapp receives the session extend from wallet
                    Log.d("Session_Extended", session.toString())
                }
                override fun onSessionEvent(sessionEvent: Sign.Model.SessionEvent) {
                    // Triggered when the peer emits events that match the list of events agreed upon session settlement
                    Log.d("Received_Session_Event", sessionEvent.toString())
                }
                override fun onSessionDelete(deletedSession: Sign.Model.DeletedSession) {
                    // Triggered when Dapp receives the session delete from wallet
                    Log.d("Session_Deleted", deletedSession.toString())
                }
                override fun onSessionRequestResponse(response: Sign.Model.SessionRequestResponse) {
                    // Triggered when Dapp receives the session request response from wallet
                    Log.d("Received_Session_Request_Response", response.toString())
                }
                override fun onConnectionStateChange(state: Sign.Model.ConnectionState) {
                    //Triggered whenever the connection state is changed
                    Log.d("Connection_State_Changed", state.toString())
                }
                override fun onError(error: Sign.Model.Error) {
                    // Triggered whenever there is an issue inside the SDK
                    Log.e("Error_In_SignClient_SDK", error.toString())
                }
            }
            SignClient.setDappDelegate(dappDelegate)
            val chains = listOf("eip155:1", "eip155:137")
            val methods = listOf("eth_sendTransaction", "eth_signTransaction", "eth_sign", "personal_sign", "eth_signTypedData","get_balance")
            val events = listOf("accountsChanged", "chainChanged", "connect", "disconnect")
            val namespaces = mapOf("eip155" to Sign.Model.Namespace.Proposal(chains, methods, events))
            // for proposer to create a session
            val connectParams = Sign.Params.Connect(namespaces,null,null, pairing!!)
            SignClient.connect(connectParams,
                onSuccess = { -> /*Session proposal was being sent successfully over pairing topic*/
                    Log.d("SignClient_Connect_Proposal_Send_Success", "The sign client connection proposal was successfully sent to the wallet")},
                onError = { error -> /*callback for error while sending session proposal*/
                    Log.e("CONNECTION_ERROR", error.throwable.stackTraceToString()) })
            startActivity(Intent(Intent.ACTION_VIEW, deeplinkPairingUri!!.toUri()))
        }
        binding.buttonSign.setOnClickListener {
            // Logic for buttonSign goes here
            val sessionTopic = apprSession!!.topic
            val method = "personal_sign"
            val account = apprSession!!.accounts[0]
            val params = SignUtils.getPersonalSignBody(account)
            val chainId = "eip155:1"
            val expiry = (System.currentTimeMillis() / 1000) + TimeUnit.SECONDS.convert(7, TimeUnit.DAYS)
            val requestParams = Sign.Params.Request(sessionTopic,method,params,chainId, expiry);
            val activeConnection = checkNotNull(SignClient.getActiveSessionByTopic(sessionTopic))
            Log.d("Connection is active", activeConnection.toString())
            SignClient.request(requestParams,
                onSuccess = { req: Sign.Model.SentRequest ->
                    /* callback for success while sending request over session */
                    Log.d("Sign_Client_Request_Send_Success", req.toString())
                },
                onError = { error: Sign.Model.Error ->
                    /* callback for error while sending request over session */
                    Log.e("Sign_Client_Request_Send_Error", error.throwable.stackTraceToString()) })
            startActivity(Intent(Intent.ACTION_VIEW, deeplinkPairingUri!!.toUri()))
        }
    }
}
