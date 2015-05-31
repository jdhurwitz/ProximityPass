package com.example.fileexplorer;

/**
 * Created by Jonny on 5/30/15.
 */

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

public class Client extends Activity {

    // private Set<Transaction> all_transactions;
    private static final int PORT = 8888;
    private static final int TIMEOUT = 500;
    private Context context;

     public Client(Context context) {
         //this.all_transactions = new HashSet();
         this.context = context;
     }

    private static String getRecipientPhone()
    {
        return "19499222058"; // TODO: fix this
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
            String phone_id = "19499222058"; // hard coded for now
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

    public void send_fft(String host, String file_name)
    {
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
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, PORT)), 500);

            // /**
            //  * Create a byte stream from a JPEG file and pipe it to the output stream
            //  * of the socket. This data will be retrieved by the server device.
            //  */
            OutputStream outputstream = socket.getOutputStream();
            ContentResolver cr = context.getContentResolver();
            InputStream inputstream = null;
            inputstream = cr.openInputStream(Uri.parse(file_name));
            while ((len = inputstream.read(buf)) != -1) {
                outputstream.write(buf, 0, 1024);
            }
            String phone_id = "19499222058"; // hard coded for now
            long file_size = this.getFileSize(file_name);
            String file_size_str = Long.toString(file_size);
            String dataString = "FFT\n"+phone_id+"\n"+file_name+"\n"+file_size_str;
            String BufString = Arrays.toString(buf);
            dataString += BufString + "\n";
            byte outputBuf[] = dataString.getBytes();
            outputstream.write(outputBuf, 0, len);
            outputstream.close();
            inputstream.close();

            // No response to FFT

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
            String phone_id = "19499222058"; // hard coded for now
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