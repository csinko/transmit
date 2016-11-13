package io.stoh.transmit;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.quietmodem.Quiet.*;

/**
 * Created by csinko on 11/12/2016.
 */
public class SenderService extends IntentService {
    FrameTransmitterConfig config = null;
    FrameTransmitter transmitter = null;
    boolean running = true;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SenderService(String name) {
        super(name);
    }
    public SenderService() {
        super("receiver");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("SENDER", "Sender Service Started");
        //get message from intent
        byte[] message = intent.getExtras().getByteArray("message");
        try {
            Log.d("SENDER", new String(message, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //create config
        try {
            config = new FrameTransmitterConfig(
                    this,
                    "audible");

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Create transmitter object
        try {
            transmitter = new FrameTransmitter(config);

        } catch (ModemException e) {
            e.printStackTrace();
        }
        while(running) {
            try {
                transmitter.send(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void onDestroy() {
        super.onDestroy();
        running = false;

    }


}
