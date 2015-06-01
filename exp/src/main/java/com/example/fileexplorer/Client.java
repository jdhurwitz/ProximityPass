package com.example.fileexplorer;

/**
 * Created by Jonny on 5/30/15.
 */

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;
import android.util.Log;
import android.telephony.TelephonyManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

public class Client extends Activity {

    private static final int PORT = 8888;
    private static final int TIMEOUT = 500;
    private Context context;

    private class Sender extends AsyncTask<Void,Void,String> {
        // On construction, start sending
        private String host;
        private String file_name;
        public Sender(String host, String file_name)
        {
            this.host = host;
            this.file_name = file_name;
        }

        @Override
        protected String doInBackground(Void... params)
        {
            // Toast.makeText(context, "Sending your file", Toast.LENGTH_SHORT).show();
            Log.d("Client", "Starting fft: " + this.host + " : " + this.file_name);
            String ret = send_fft(this.host, this.file_name);
            Log.d("Client", "Finished fft: " + ret);
            return ret;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                //statusText.setText("File copied - " + result);
                Toast.makeText(context, "File sent", Toast.LENGTH_SHORT).show();
                // Intent intent = new Intent();
                // intent.setAction(android.content.Intent.ACTION_VIEW);
                // intent.setDataAndType(Uri.parse("file://" + result), "image/*");
                // context.startActivity(intent);
            }
        }
    }

    public Client(Context context) {
        this.context = context;
    }

    public void send(String host, String file_name)
    {
        Sender s = new Sender(host, file_name);
        s.execute();
    }

    private String getMyPhoneNumber()
    {
        TelephonyManager tMgr = (TelephonyManager)this.context.getSystemService(Context.TELEPHONY_SERVICE);
        String ret = tMgr.getLine1Number();
        return ret;
    }

    private static long getFileSize(String fname)
    {
        File f = new File(fname);
        return f.length();
    }

    private void send_rts(String host, String file_name)
    {
        Context context = this.getApplicationContext();
        int len;
        Socket socket = new Socket();
        byte buf[]  = new byte[1024];
        // ...
        // Set host
        // Use defined port no.
        // Set len
        len = 1000;
        try {
            /**
             * Create a client socket with the host,
             * port, and timeout information.
             */
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, PORT)), TIMEOUT);

            OutputStream outputstream = socket.getOutputStream();
            InputStream  server_resp  = socket.getInputStream();
            String phone_id = getMyPhoneNumber();
            int file_size = 2048; // hard coded for now
            String file_size_str = Integer.toString(file_size);
            String dataString = "RTS\n"+phone_id+"\n"+file_name+"\n"+file_size_str+"\n";
            buf = dataString.getBytes();
            outputstream.write(buf, 0, len);
            outputstream.close();
            // Parse response
            InputStream respStream = socket.getInputStream();

            String resp_fname = "";
            int ch;
            String resp_type = "";
            while ( (ch = server_resp.read()) != '\n')
                resp_type += ch;
            String phone_no = "";
            while ( (ch = server_resp.read()) != '\n')
                phone_no += ch;
            while ( (ch = server_resp.read()) != '\n')
                resp_fname += ch;
            String resp_fsize_str = "";
            while ( (ch = server_resp.read()) != '\n')
                resp_fsize_str += ch;
            int resp_fsize = Integer.parseInt(resp_fsize_str);

            // Test for valid response
            if (resp_fname == file_name
                    && resp_fsize == file_size)
            {
                // This is so far OK
                if (resp_type == "CTS")
                    send_fft(host, file_name);
                else if (resp_type == "RFP")
                    send_rtp(host, file_name);
                else
                {
                    // This is an error case
                    // TODO: handle this
                }
            }
            else
            {
                // This is an error case
                // TODO: handle this
            }
        } catch (FileNotFoundException e) {
            //catch logic
        } catch (IOException e) {
            //catch logic
        }

        /**
         * Clean up any open sockets when done
         * transferring or if an exception occurred.
         */
        finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        //catch logic
                    }
                }
            }
        }
    }

    public String send_fft(String host, String file_name)
    {
        Log.d("Client", "File is: " + file_name);
        int len;
        Socket socket = new Socket();
        byte buf[]  = new byte[1024];
        // ...
        // Set host
        // Use defined port no.
        try {
            /**
             * Create a client socket with the host,
             * port, and timeout information.
             */
            Log.d("Test", "Before binding");
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, PORT)), 500);
            Log.d("Test", "After connecting");

            // /**
            //  * Create a byte stream from a JPEG file and pipe it to the output stream
            //  * of the socket. This data will be retrieved by the server device.
            //  */
            OutputStream network_output = socket.getOutputStream();
            ContentResolver cr = context.getContentResolver();
            InputStream file_in_stream;
            Log.d("Test", "Before parsing");
            Uri tmp = Uri.parse("file://"+file_name);
            file_in_stream = cr.openInputStream(tmp);
            Log.d("Test", "After input stream");
            String phone_id = "";
            try
            {
                phone_id = getMyPhoneNumber();
            }
            catch (Exception e)
            {
                Log.d("Error", e.toString() );
            }
            Log.d("Test", "After phone number");
            Log.d("Phone id", phone_id);
            Log.d("Client", "Before getFileSize");
            long file_size = this.getFileSize(file_name);
            String file_size_str = Long.toString(file_size);
            File f = new File(file_name);
            String short_name = f.getName();
            String headerString = "FFT\n"+phone_id+"\n"+short_name+"\n"+file_size_str+"\n";

            // We've now completed the header
            Log.d("Client", "headerString: " + headerString);
            String dataString = ""; // initialize empty
            Log.d("Client", "Before while loop");
            try
            {
                while ((len = file_in_stream.read(buf)) != -1) {
                    // network_output.write(buf, 0, 1024);

                    // buf stores the next several bytes from the file

                    // Put a zero byte in there
                    // buf[len] = 0;
                    String BufString = new String(buf);
                    Log.d("Buffer:", BufString);
                    dataString += BufString;
                }
            }
            catch (Exception e)
            {
                Log.d("Error", e.toString() );
            }
            Log.d("Client", "After while loop");
            // Toast.makeText(this.context, "Finished reading file", Toast.LENGTH_SHORT).show();
            // Now dataString is complete
            String outputString = headerString + dataString;
            byte outputBuf[] = outputString.getBytes();
            // network_output.write(outputBuf, 0, 1024);
            network_output.write(outputBuf); // Write entire buffer to network

            network_output.close();
            file_in_stream.close();

            // FFT requests receive no response, so don't listen for one

        } catch (FileNotFoundException e) {
            //catch logic
            Log.d("Client", "FileNotFound: " + e.getMessage());
        } catch (IOException e) {
            Log.d("Client", "IO exception: " + e.getMessage());
            //catch logic
        }

        /**
         * Clean up any open sockets when done
         * transferring or if an exception occurred.
         */
        finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                        return "Success";
                    } catch (IOException e) {
                        //catch logic
                    }
                }
            }
        }
        return null;
    }

    private void send_rtp(String host, String file_name)
    {
        Context context = this.getApplicationContext();
        int len;
        Socket socket = new Socket();
        byte buf[]  = new byte[1024];
        // ...
        // Set host
        // Use defined port no.
        // Set len
        len = 1000;
        try {
            /**
             * Create a client socket with the host,
             * port, and timeout information.
             */
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, PORT)), 500);

            // /**
            //  * Create a byte stream from a JPEG file and pipe it to the output stream
            //  * of the socket. This data will be retrieved by the server device.
            //  */
            OutputStream outputstream = socket.getOutputStream();
            // ContentResolver cr = context.getContentResolver();
            // InputStream inputstream = null;
            // inputstream = cr.openInputStream(Uri.parse("path/to/picture.jpg"));
            // while ((len = inputstream.read(buf)) != -1) {
            //     outputstream.write(buf, 0, len);
            // }
            String phone_id = getMyPhoneNumber();
            int file_size = 2048; // hard coded for now
            String file_size_str = Integer.toString(file_size);
            String dataString = "RTP\n"+phone_id+"\n"+file_name+"\n"+file_size_str+"\n";
            buf = dataString.getBytes();
            outputstream.write(buf, 0, len);
            outputstream.close();
            // Parse response
            InputStream respStream = socket.getInputStream();

            int ch;
            String resp_type = "";
            while ( (ch = respStream.read()) != '\n')
                resp_type += ch;
            String phone_no = "";
            while ( (ch = respStream.read()) != '\n')
                phone_no += ch;
            String resp_fname = "";
            while ( (ch = respStream.read()) != '\n')
                resp_fname += ch;
            String resp_fsize_str = "";
            while ( (ch = respStream.read()) != '\n')
                resp_fsize_str += ch;
            int resp_fsize = Integer.parseInt(resp_fsize_str);

            // Test for valid response
            if (resp_fname == file_name
                    && resp_fsize == file_size)
            {
                // This is so far OK
                if (resp_type == "CTS")
                    send_fft(host, file_name);
                else
                {
                    // This is an error case
                    // TODO: handle this
                }
            }
            else
            {
                // This is an error case
                // TODO: handle this
            }
        } catch (FileNotFoundException e) {
            //catch logic
        } catch (IOException e) {
            //catch logic
        }

        /**
         * Clean up any open sockets when done
         * transferring or if an exception occurred.
         */
        finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        //catch logic
                    }
                }
            }
        }
    }
}
