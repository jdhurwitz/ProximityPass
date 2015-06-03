package com.mycompany.p2pwifi;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.os.SystemClock; // DEBUG
import java.net.InetSocketAddress;
import java.lang.Runnable;
import android.app.Activity;
// import android.util.Base64.Decoder;
// import android.util.Base64;
import java.util.*;
import android.content.DialogInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Created by Jonny on 5/30/15.
 */
public class PersistantServer extends AsyncTask<Void,Void,String> {

    private int lock;
    private boolean ret;
    private int counter;
    private Context context;
    private Activity activity;
    private static final int PORT = 8888;
    private Set<Transaction> all_transactions;
    public PersistantServer(Context ctx, Activity act)
    {
        this.counter = 0;
        this.context = ctx;
        this.activity = act;
        this.all_transactions = new HashSet();
    }

    @Override
    protected String doInBackground(Void... params) {
        // Keep listening for connections
        try {
            for (;;) // infinite loop
            {
                Log.d("Server", "Starting server " + Integer.toString(this.counter));
                listen_for_connection();
                // SystemClock.sleep(7000);
                Log.d("Server", "Closing server " + Integer.toString(this.counter));
                this.counter = this.counter + 1;
            }
        }
        catch (Exception e) {
            Log.d("Exception", e.toString());
        }
        return null; // never reached
    }


    private String getMyPhoneNumber()
    {
        TelephonyManager tMgr = (TelephonyManager)this.context.getSystemService(Context.TELEPHONY_SERVICE);
        String ret = tMgr.getLine1Number();
        return ret;
    }

    private static boolean copyStream(InputStream in, OutputStream out, int maxSize) {
        // Log.d("Copy Stream", Integer.toString(maxSize) );
        int bufSize = 1024;
        byte buf[] = new byte[bufSize];
        int len;
        int amt_read = 0;
        try {
            // Loop, but not indefinitely (see maxSize)
            // while ((len = in.read(buf)) != -1 && amt_read < maxSize)
            // {
            //     out.write(buf, 0, len);
            //     amt_read += len;
            // }
            while ((len = in.read(buf)) != -1 && amt_read < maxSize)
            {
                out.write(buf, 0, len);
                // Log.d("Buffer:", new String(buf) );
                amt_read += len;
            }
            // Log.d("CS", "Should leave "+Integer.toString(amt_read));
            out.close();
            in.close();
            // Log.d("Copy Stream", Integer.toString(amt_read) );
        } catch (IOException e) {
            // Log.d(WiFiDirectActivity.TAG, e.toString());
            Log.d("Copy Stream Error", e.toString() );
            return false;
        }
        return true;
    }

    private boolean ask_for_confirmation(String phone, String fname)
    {
        this.lock = 1;
        this.ret = true;
        final String f_fname = fname;
        final String f_phone = phone;
        this.activity.runOnUiThread(new Runnable() {
            public void run()
            {
                new android.app.AlertDialog.Builder(activity)
                .setTitle("Incoming file")
                .setMessage("Would you like to receive "+f_fname+" from "+f_phone+"?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        ret = true;
                        lock = 0;
                    }
                })
                // Negative button not necessary, help menu is only for help
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        ret = false;
                        lock = 0;
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
            }
        });
        while (this.lock == 1);
        return this.ret; // TODO
    }

    protected String listen_for_connection() {
        try {
            // Log.d("Simple server", "listen_for_connection");

            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */
            // ServerSocket serverSocket = new ServerSocket(PORT);
            // Log.d("Simple server", "line 2");
            // Socket client = serverSocket.accept();
            // Log.d("Simple server", "line 3");
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(PORT));
            // Log.d("Simple server", "Accepting connection");
            Socket client = serverSocket.accept();
            Log.d("Simple server", "Connection established");

            /**
             * If this code is reached, a client has connected and
             * transferred data
             * Save the input stream from the client as a JPEG file
             */
            InputStream clientInput = client.getInputStream();
            OutputStream outputstream = client.getOutputStream();

            // InputStreamReader isr = new  InputStreamReader(clientInput);
            // BufferedReader ir = new BufferedReader(isr);

            // String request_type = ir.readLine();
            // String phone_no = ir.readLine();
            // String file_name = ir.readLine();
            // String file_size_str = ir.readLine();
            // Log.d("Server", "request_type" + request_type);
            // Log.d("Server", "phone_no" + phone_no);
            // Log.d("Server", "file_name" + file_name);
            // Log.d("Server", "file_size_str" + file_size_str);
            int ch;
            int newlineByte = '\n';
            String request_type = "";
            while ( (ch = clientInput.read()) != newlineByte)
                request_type += (char)ch;
            Log.d("Server", "Request: <" + request_type + ">");
            String phone_no = "";
            while ( (ch = clientInput.read()) != newlineByte)
                phone_no += (char)ch;
            String file_name = "";
            while ( (ch = clientInput.read()) != newlineByte)
                file_name += (char)ch;
            // Log.d("Server", "File name: " + file_name);
            String file_size_str = "";
            while ( (ch = clientInput.read()) != newlineByte)
                file_size_str += (char)ch;
            // Log.d("Server", file_size_str);
            int file_size = Integer.parseInt(file_size_str);

            // Build a Transaction
            Transaction cur = new Transaction(phone_no, file_name, file_size);

            if (all_transactions.contains(cur))
            {
                Log.d("Trans", "Duplicate transaction!");
                // remove it from set
                all_transactions.remove(cur);
                // add updated Transaction
                all_transactions.add(cur);
            }

            // Handle different requests differently
            if (request_type.equals("RTS") )
            {
                // Log.d("FFT", "You reached rts");
                // create transaction
                cur.updateStage(0);
                all_transactions.add(cur);

                // get user approval or automatically request preview
                // Assume we must get user approval
                boolean approval = ask_for_confirmation(phone_no, file_name);

                String my_phone_no = getMyPhoneNumber();
                if (approval)
                {
                    // Send CTS
                    String dataString = "CTS\n"+my_phone_no+"\n"+file_name+"\n"+file_size_str+"\n";
                    byte buf[] = dataString.getBytes();
                    outputstream.write(buf);
                    Log.d("Approval", "APPROVED!!!");
                }
                else
                {
                    String dataString = "DTS\n"+my_phone_no+"\n"+file_name+"\n"+file_size_str+"\n";
                    byte buf[] = dataString.getBytes();
                    outputstream.write(buf);
                    Log.d("Approval", "DENIED");
                }
                outputstream.close();
                serverSocket.close();

                // update transaction state
                boolean isRemoved = all_transactions.remove(cur);
                if (!isRemoved)
                    Log.d("Set", "Wasn't removed (bad)");
                else
                    Log.d("Set", "Successfull removed!");
                cur.updateStage(1);
                boolean isAdded = all_transactions.add(cur);
                if (!isAdded)
                    Log.d("Set", "Wasn't added (bad)");
                else
                    Log.d("Set", "Successfull added!");
            }
            else if (request_type.equals("RTP") )
            {
                // Log.d("FFT", "You reached rtp");
                // // update transaction
                // if (!all_transactions.contains(cur))
                // {
                //     // Throw an exception
                // }

                // display preview on screen

                // get user approval
                boolean approval = true; // hard coded for now

                String my_phone_no = getMyPhoneNumber();
                if (approval)
                {
                    // Send CTS
                    String dataString = "CTS\n"+my_phone_no+"\n"+file_name+"\n"+file_size_str+"\n";
                    byte buf[] = dataString.getBytes();
                    outputstream.write(buf);
                }
                outputstream.close();
                serverSocket.close();

                // update transaction
            }
            else if (request_type.equals("FFT") )
            {
                // Log.d("FFT", "You reached FFT");
                // update transaction
                if (!all_transactions.contains(cur))
                {
                    // Throw an exception
                    Log.d("Transaction", "It isn't there");
                    Log.d("Set", "Size: "+Integer.toString(all_transactions.size()));
                    // Weird, it has 1 element, but it isn't the transaction we're looking for
                }
                Iterator<Transaction> it = all_transactions.iterator();
                boolean valid = false;
                while (it.hasNext())
                {
                    Transaction tmp = it.next();
                    if (!tmp.equals(cur))
                        continue;
                    else
                    {
                        if (tmp.stage() != 1)
                        {
                            // Throw an exception, this was never confirmed
                            Log.d("Transaction", "This transaction wasn't confirmed!!!!!!");
                        }
                        else
                        {
                            valid = true;
                            break;
                        }
                    }
                }

                // if (!valid) // should be redundant
                // {
                //     // Throw an exception, we didn't have a hit
                // }

                // save file
                final File f = new File(Environment.getExternalStorageDirectory()
                        + "/ProximityPass/" + file_name);

                File dirs = new File(f.getParent());
                if (!dirs.exists())
                {
                    Log.d("File saving", "Creating your directories right now");
                    dirs.mkdirs();
                }
                f.createNewFile();
                copyStream(clientInput, new FileOutputStream(f), file_size); // TODO: Make this copy only file_size bytes
                serverSocket.close();

                // remove transaction

                // Indicate success with the filepath
                after_file_transfer(f.getAbsolutePath() );
                return f.getAbsolutePath();
            }
            else
            {
                // Log.d("FFT", "You reached      ELSE CASE");
                serverSocket.close();
            }


            return null;
        } catch (IOException e) {
            Log.d("Server", e.getMessage());
            return null;
        } catch (NumberFormatException e) {
            Log.d("Server", e.getMessage());
            return null;
        } catch (Exception e) {
            Log.d("Server", e.getMessage());
            return null;
        }
    }

    /**
     * Start activity that can handle the JPEG image
     */
    protected void after_file_transfer(String result) {
        if (result != null) {
            // Toast.makeText(context, "File copied - " + result, Toast.LENGTH_SHORT).show();
            final String fname = result; // leaves at full path
            // Activity act = (Activity) this.context;
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(context, "File copied - " + fname, Toast.LENGTH_LONG).show();
                }
            });

            // Intent intent = new Intent();
            // intent.setAction(android.content.Intent.ACTION_VIEW);
            // intent.setDataAndType(Uri.parse("file://" + result), "image/*");
            // context.startActivity(intent);
        }
    }
}
