package com.mycompany.p2pwifi;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

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
public class SimpleServer extends AsyncTask<Void,Void,String> {
    //Make sure to include a package including Transaction.java




    //public class SimpleServer extends AsyncTask {

        private static final int PORT = 8888;
        // private Set<Transaction> all_transactions;
        private Context context;
        private TextView statusText;

        public SimpleServer(Context context, View statusText) {
            this.context = context;
            this.statusText = (TextView) statusText;
            // this.all_transactions = new HashSet();
        }

    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);

            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            //Log.d(WiFiDirectActivity.TAG, e.toString());
            return false;
        }
        return true;
    }

        @Override
        protected String doInBackground(Void... params) {
            try {

                /**
                 * Create a server socket and wait for client connections. This
                 * call blocks until a connection is accepted from a client
                 */
                ServerSocket serverSocket = new ServerSocket(PORT);
                Socket client = serverSocket.accept();

                /**
                 * If this code is reached, a client has connected and transferred data
                 * Save the input stream from the client as a JPEG file
                 */
                InputStream inputstream = client.getInputStream();
                OutputStream outputstream = client.getOutputStream();

                int ch;
                String request_type = "";
                while ( (ch = inputstream.read()) != '\n')
                    request_type += ch;
                String phone_no = "";
                while ( (ch = inputstream.read()) != '\n')
                    phone_no += ch;
                String file_name = "";
                while ( (ch = inputstream.read()) != '\n')
                    file_name += ch;
                String file_size_str = "";
                while ( (ch = inputstream.read()) != '\n')
                    file_size_str += ch;
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
                    final File f = new File(Environment.getExternalStorageDirectory() + "/"
                            + context.getPackageName() + "/ProximityPass/" + file_name);

                    File dirs = new File(f.getParent());
                    if (!dirs.exists())
                        dirs.mkdirs();
                    f.createNewFile();
                    copyFile(inputstream, new FileOutputStream(f)); // TODO: Make this copy only file_size bytes
                    serverSocket.close();

                    // display message saying it was successful

                    // remove transaction

                    return f.getAbsolutePath();
                }

                return null;
            } catch (IOException e) {
                //Log.e(WiFiDirectActivity.TAG, e.getMessage());
                return null;
            }
        }

        /**
         * Start activity that can handle the JPEG image
         */
        //@Override
        protected void onPostExecute(String result) {
            if (result != null) {
                statusText.setText("File copied - " + result);
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + result), "image/*");
                context.startActivity(intent);
            }
        }
    }

