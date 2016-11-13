package io.stoh.transmit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            boolean permission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
            Log.d("PERMISSIONS", String.valueOf(permission));
            if (!permission) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
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

                } else {
                    Log.e("PERMISSIONS", "No permission for audio");
                    finish();
                }
            }
        }
    }

    public void startSendService(View view) {
        TextView status = (TextView) findViewById(R.id.tv_send_status);
        status.setText("Starting");
        Intent sendServiceIntent = new Intent(this, SenderService.class);
        sendServiceIntent.putExtra("message", "It works!".getBytes());
        startService(sendServiceIntent);
        status.setText("Running");
    }

    public void startReceiveService(View view) {
        Intent receiveServiceIntent = new Intent(this, ReceiverService.class);
        startService(receiveServiceIntent);
    }

    public void startSocketServerService(View view) {
        Intent socketServerServiceIntent = new Intent(this, SocketService.class);
        Log.d("SOCKET", "About to start socket");
        startService(socketServerServiceIntent);
    }
}
