package io.stoh.transmit;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.annotation.BoolRes;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by csinko on 11/12/2016.
 */
public class SocketService extends IntentService {
    ServerSocket serverSocket;
    NotificationCompat.Builder mBuilder;
    Intent sendServiceIntent;

    public SocketService(String name) {
        super(name);
    }

    public SocketService() {
        super("socket");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("SOCKET", "Socket Service Started");
        sendServiceIntent = new Intent(getApplicationContext(), SenderService.class);
        String locAddr = getIpAddress() + ":8080";
        sendServiceIntent.putExtra("message", locAddr.getBytes());
        startService(sendServiceIntent);

        Thread socketServerThread = new Thread(new SocketServerThread());
        mBuilder = new NotificationCompat.Builder(this);
        socketServerThread.run();

    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip = inetAddress.getHostAddress();
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip = "0.0.0.0";
        }

        return ip;
    }

    private class SocketServerThread extends Thread {
        static final int SocketServerPORT = 8080;
        InetAddress address;

        public void run() {
            try {
                serverSocket = new ServerSocket(SocketServerPORT);
                String ip = "IP: " + getIpAddress();
                Log.d("SOCKET", ip);

                mBuilder.setContentTitle("Socket Server Running")
                        .setContentText(ip)
                        .setSmallIcon(R.drawable.icon);

                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                mNotifyMgr.notify(1, mBuilder.build());
                Boolean running = true;

                while (running) {
                    Socket socket = serverSocket.accept();
                    stopService(sendServiceIntent);
                    Log.d("SOCKET", "CLIENT Connected");
                    address = socket.getInetAddress();
                    Log.d("SOCKET", address.getHostAddress());
                    PrintWriter out = new PrintWriter(socket.getOutputStream());
                    out.write("received");
                    out.flush();
                    out.close();
                    socket.close();
                    sendServiceIntent.removeExtra("message");
                    sendServiceIntent.putExtra("message", address.getHostAddress().getBytes());
                    startService(sendServiceIntent);
                    running = false;



                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SocketServerReplyThread extends Thread {
        private Socket hostThreadSocket;

        SocketServerReplyThread(Socket socket) {
            hostThreadSocket = socket;
        }

        public void run() {
            OutputStream outputStream;
            String msgReply = "received";

            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply.getBytes());
                printStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

