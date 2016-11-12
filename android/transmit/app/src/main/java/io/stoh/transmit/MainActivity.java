package io.stoh.transmit;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import org.quietmodem.Quiet.*;


public class MainActivity extends AppCompatActivity {

    FrameReceiverConfig receiverConfig = null;
    FrameTransmitterConfig transmitterConfig = null;
    FrameReceiver receiver = null;
    FrameTransmitter transmitter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            transmitterConfig = new FrameTransmitterConfig(
                    this,
                    "audible");
            receiverConfig = new FrameReceiverConfig(
                    this,
                    "audible");
        } catch (IOException e) {
            // could not build configs
        }

        try {
            boolean permission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
            Log.d("PERMISSIONS", String.valueOf(permission));
            if (!permission) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            }
            else {
                setupReceiver();
            }
            transmitter = new FrameTransmitter(transmitterConfig);



        } catch (ModemException e) {
            e.printStackTrace();
            // could not set up receiver/transmitter
        }



    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSIONS", "Permission granted for audio");
                    setupReceiver();

                } else {
                    Log.e("PERMISSIONS", "No permission for audio");
                    finish();
                }
            }
        }
    }

    public void setupReceiver() {
        try {
            receiverConfig = new FrameReceiverConfig(
                    this,
                    "audible-7k-channel-0");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            receiver = new FrameReceiver(receiverConfig);
            Log.d("RECEIVER", "Receiver Created!");
        }
        catch(ModemException e) {
            e.printStackTrace();
        }
    }

    public void send_sound_message(View view) {
        TextView tv_status = (TextView) findViewById(R.id.tv_status);
        String payload = "Hello, World!";
        if (transmitter != null) {
            try {
                tv_status.setText("Status: Sending Message");
                transmitter.send(payload.getBytes());
            } catch (IOException e) {
                // our message might be too long or the transmit queue full
            }
        }
    }

    public void receive_sound_message(View view) {
        TextView tv_status = (TextView) findViewById(R.id.tv_status);
        TextView tv_receive = (TextView) findViewById(R.id.tv_receive);
        if (receiver != null) {
            receiver.setBlocking(10, 0);
            byte[] buf = new byte[1024];
            long recvLen = 0;
            try {
                tv_status.setText("Status: Receiving Message (Timeout 10 seconds)");
                recvLen = receiver.receive(buf);
            } catch (IOException e) {
                tv_status.setText("Status: Receiver Timed Out");
                // read timed out
            }

            if (recvLen > 0) {
                tv_status.setText("Status: Message Received");
                tv_receive.setText(buf.toString());
            }
        }
    }
}
