package io.stoh.transmit;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import java.io.IOException;
import org.quietmodem.Quiet.*;

/**
 * Created by csinko on 11/12/2016.
 */
public class ReceiverService extends IntentService {
    FrameReceiverConfig config = null;
    FrameReceiver receiver = null;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ReceiverService(String name) {
        super(name);
    }
    public ReceiverService() {
        super("receiver");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("RECEIVER", "Receiver Service Started");
        //create config
        try {
            config = new FrameReceiverConfig(
                    this,
                    "ultrasonic");

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Create transmitter object
        try {
            receiver = new FrameReceiver(config);

        } catch (ModemException e) {
            e.printStackTrace();
        }

        //set up buffer
        receiver.setBlocking(30, 0);
        byte[] buf = new byte[1024];
        long recvLen = 0;

        //listen for message
        try {
            recvLen = receiver.receive(buf);
            Log.d("RECEIVER", "Possible Message Received");

            //Make Notification if a real message was received
            if(recvLen > 0) {
                String message = new String(buf, "UTF-8");
                Log.d("RECEIVER", message);
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setContentTitle("Message Received!")
                                .setContentText(message);

                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                mNotifyMgr.notify(1, mBuilder.build());


            }
        } catch (IOException e) {
            // read timed out
            Log.e("RECEIVER", "Receiver Timed Out");
        }
    }
}
