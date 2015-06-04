package com.mycompany.p2pwifi;

/**
 * Created by robert on 5/19/15.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;



public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = "herro";

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private P2pMain mActivity;

    private List peers = new ArrayList();
    private List peerNames = new ArrayList();

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            peers.clear();
            peers.addAll(peerList.getDeviceList());
            peerNames.clear();
            for(int i = 0; i < peerList.getDeviceList().size(); i++) {
                WifiP2pDevice device = (WifiP2pDevice) peers.get(i);
                peerNames.add(device.deviceName);
                Log.d(TAG, String.format("Added device: %s", device.deviceName));
            }
            mActivity.update_list();
            Log.d(TAG, String.format("PeerListListener: %d peers available, updating device list", peerList.getDeviceList().size()));
        }
    };

    private WifiP2pManager.ConnectionInfoListener connectionListener = new WifiP2pManager.ConnectionInfoListener() {

        @Override
        public void onConnectionInfoAvailable(final WifiP2pInfo info) {
            String hostAddress = "";
            if(info.groupOwnerAddress != null) {
                 hostAddress = info.groupOwnerAddress.getHostAddress();
            }
            if(info.isGroupOwner) {
                Log.d("connected", "I am group owner.");
            }
            //Log.d("connected", String.format("connected to device with host: %s", hostAddress));

            // After the group negotiation, we can determine the group owner.
            if (info.groupFormed && info.isGroupOwner) {
                // Do whatever tasks are specific to the group owner.
                // One common case is creating a server thread and accepting
                // incoming connections.
                PersistantServer server = new PersistantServer(mActivity.getApplicationContext(), mActivity);
                server.execute();

            } else if (info.groupFormed ) {
                // The other device acts as the client. In this case,
                // you'll want to create a client thread that connects to the group
                // owner.

                mActivity.start_file_chooser(hostAddress);

            }
        }
    };

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, P2pMain activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Log.d("wifi", "receive: " + intent.getAction());

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                mActivity.setIsWifiP2pEnabled(true);
            }
            else {
                mActivity.setIsWifiP2pEnabled(false);
            }
        }
        else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            Log.d("wifi", "peers changed");
            if(mManager != null){
                mManager.requestPeers(mChannel, peerListListener);
            }
        }
        else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // We are connected with the other device, request connection
                // info to find group owner IP

                mManager.requestConnectionInfo(mChannel, connectionListener);
            }
        }
        else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            /*DeviceListFragment fragment = (DeviceListFragment) mActivity.getFragmentManager().findFragmentById(R.id.frag_list);
            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));*/
        }
    }

    public List get_peers(){
        return peers;
    }

    public List get_peerNames() {
        return peerNames;
    }
}
