package io.stoh.transmit;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by csinko on 11/12/2016.
 */
public class ReceiverService extends IntentService {

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

    }
}
