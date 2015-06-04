package com.mycompany.p2pwifi;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.fileexplorer.FileChooser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class P2pMain extends Activity implements Serializable{

    ListView listView;

    private final IntentFilter intentFilter = new IntentFilter();
    WifiP2pManager.Channel mChannel;
    WifiP2pManager mManager;
    WiFiDirectBroadcastReceiver mReceiver;
    boolean wifiP2pEnabled;
    private List peers = new ArrayList();
    private List peerNames = new ArrayList();
    private ArrayAdapter<String> adapter;

    private WifiP2pConfig config = new WifiP2pConfig();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        config.groupOwnerIntent = 15;


        peers = mReceiver.get_peers();
        peerNames = mReceiver.get_peerNames();

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, peerNames);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                connect(position);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, intentFilter);

        cancel_connection();
        //startActivity(i);
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("wifi", "onsuccess");
            }

            @Override
            public void onFailure(int reason) {
                Log.d("wifi", "onfailure");
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //Open dialog box
            new android.app.AlertDialog.Builder(P2pMain.this)
                    .setTitle("Help Menu")
                    .setMessage(Html.fromHtml(getString(R.string.send_file)) + "\n"
                            +Html.fromHtml(getString(R.string.receive_file)) + "\n"
                            +Html.fromHtml(getString(R.string.note)))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                            //Negative button not necessary, help menu is only for help
                   /* .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })*/
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setIsWifiP2pEnabled(boolean s){
        wifiP2pEnabled = s;
    }

    public boolean isWifiP2pEnabled(){
        return wifiP2pEnabled;
    }

    /*public void discover_peers(View view) {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("wifi", "onsuccess");
            }
            @Override
            public void onFailure(int reason) {
                Log.d("wifi", "onfailure");
            }
        });
    }*/

    public void update_list() {
        this.adapter.notifyDataSetChanged();
    }

    public void connect(int position) {

        WifiP2pDevice device = (WifiP2pDevice) peers.get(position);

        //WifiP2pConfig config = new WifiP2pConfig();
        config.groupOwnerIntent = 0;

        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {

                /*Intent intent = new Intent(this.getActivity(), FileChooser.class);
                String
                startActivity(intent);*/

            }

            @Override
            public void onFailure(int reason) {
                Log.d("connect", "Failed to connect to device.");
            }
        });
    }

    public void start_file_chooser(String hostAddress) {
        Intent intent = new Intent(this, FileChooser.class);
        intent.putExtra("serverIP", hostAddress); //pass the host address to the file chooser

        //intent.putExtra("manager", mManager);
        //intent.putExtra("channel", mChannel);
        //intent.putExtra("P2pMain", this);
        startActivity(intent);
    }

    public void cancel_connection(){
        this.mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("Remove", "removeGroup onSuccess");
                Intent i= new Intent(P2pMain.this,P2pMain.class);
                startActivity(i);
            }
                @Override
                public void onFailure ( int reason){
                    Log.d("Remove", "Failure, reason: " + reason);

                    mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            Log.d("wifi", "onsuccess");
                        }

                        @Override
                        public void onFailure(int reason) {
                            Log.d("wifi", "onfailure");
                        }
                    });
                }
            }

            );
        }

    }