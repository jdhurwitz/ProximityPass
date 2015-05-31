package com.mycompany.p2pwifi;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Created by Jonny on 5/30/15.
 */
public class SimpleServer extends AsyncTask<Void,Void,String> {
    //Make sure to include a package including Transaction.java

    private static final int PORT = 8888;
    // private Set<Transaction> all_transactions;
    private Context context;
    private TextView statusText;

    public SimpleServer(Context context, View statusText) {

        this.context = context;
        this.statusText = (TextView) statusText;
        // this.all_transactions = new HashSet();
    }

    public static boolean copyStream(InputStream in, OutputStream out, int maxSize) {
        int bufSize = 1024;
        byte buf[] = new byte[bufSize];
        int len;
        int amt_read = 0;
        try {
            // Loop, but not indefinitely (see maxSize)
            while ((len = in.read(buf)) != -1 && amt_read < maxSize)
            {
                out.write(buf, 0, len);
                amt_read += len;
            }
            out.close();
            in.close();
        } catch (IOException e) {
            // Log.d(WiFiDirectActivity.TAG, e.toString());
            return false;
        }
        return true;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            Log.d("server", "Server has started.");


            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */
            ServerSocket serverSocket = new ServerSocket(PORT);
            Socket client = serverSocket.accept();

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
            Log.d("Server", "Request: " + request_type);
            String phone_no = "";
            while ( (ch = clientInput.read()) != newlineByte)
                phone_no += (char)ch;
            String file_name = "";
            while ( (ch = clientInput.read()) != newlineByte)
                file_name += (char)ch;
            Log.d("Server", "File name: " + file_name);
            String file_size_str = "";
            while ( (ch = clientInput.read()) != newlineByte)
                file_size_str += (char)ch;
            Log.d("Server", file_size_str);
            int file_size = Integer.parseInt(file_size_str);

            // // Build a Transaction
            // Transaction cur = new Transaction(phone_no, file_name, file_size);

            // if (all_transactions.contains(cur))
            // {
            //     // remove it from set
            //     all_transactions.remove(cur);
            //     // add updated Transaction
            //     all_transactions.add(cur);
            // }

            // Handle different requests differently
            if (request_type == "RTS")
            {
                // // create transaction
                // cur.updateStage(0);
                // all_transactions.add(cur);

                // get user approval or automatically request preview
                // Assume we must get user approval
                boolean approval = true; // hard coded for now

                String my_phone_no = "123456789"; // TODO
                if (approval)
                {
                    // Send CTS
                    String dataString = "CTS\n"+my_phone_no+"\n"+file_name+"\n"+file_size_str+"\n";
                    byte buf[] = new byte[1024];
                    int len = 1000;
                    buf = dataString.getBytes();
                    outputstream.write(buf, 0, len);
                    outputstream.close();
                }

                // update transaction state
                // all_transactions.remove(cur);
                // cur.updateStage(1);
                // all_transactions.add(cur);
            }
            else if (request_type == "RTP")
            {
                // // update transaction
                // if (!all_transactions.contains(cur))
                // {
                //     // Throw an exception
                // }

                // display preview on screen

                // get user approval
                boolean approval = true; // hard coded for now

                String my_phone_no = "123456789"; // TODO
                if (approval)
                {
                    // Send CTS
                    String dataString = "CTS\n"+my_phone_no+"\n"+file_name+"\n"+file_size_str+"\n";
                    byte buf[] = new byte[1024];
                    int len = 1000;
                    buf = dataString.getBytes();
                    outputstream.write(buf, 0, len);
                    outputstream.close();
                }

                // update transaction
            }
            else if (request_type == "FFT")
            {
                // // update transaction
                // if (!all_transactions.contains(cur))
                // {
                //     // Throw an exception
                // }
                // Iterator<Transaction> it = all_transactions.iterator();
                // boolean valid = false;
                // while (it.hasNext())
                // {
                //     Transaction tmp = it.next();
                //     if (!tmp.equals(cur))
                //         continue;
                //     else
                //     {
                //         if (tmp.stage() != 1)
                //         {
                //             // Throw an exception, this was never confirmed
                //         }
                //         else
                //         {
                //             valid = true;
                //             break;
                //         }
                //     }
                // }

                // if (!valid) // should be redundant
                // {
                //     // Throw an exception, we didn't have a hit
                // }

                // save file
                final File f = new File(Environment.getExternalStorageDirectory()
                        + "/ProximityPass/" + file_name);

                File dirs = new File(f.getParent());
                if (!dirs.exists())
                    dirs.mkdirs();
                f.createNewFile();
                copyStream(clientInput, new FileOutputStream(f), file_size); // TODO: Make this copy only file_size bytes
                serverSocket.close();

                // display message saying it was successful

                // remove transaction

                return f.getAbsolutePath();
            }

            return null;
        } catch (IOException e) {
            //Log.e(WiFiDirectActivity.TAG, e.getMessage());
            return null;
        } catch (NumberFormatException e) {
            Log.d("Server", e.getMessage());
            return null;
        }
    }

    /**
     * Start activity that can handle the JPEG image
     */
    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            //statusText.setText("File copied - " + result);
            Toast.makeText(context, "File copied - " + result, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://" + result), "image/*");
            context.startActivity(intent);
        }
    }
}

