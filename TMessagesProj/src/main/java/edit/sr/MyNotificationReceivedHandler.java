package edit.sr;

import android.content.Context;
import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;


import static org.zgram.messenger.ApplicationLoader.applicationContext;

/**
 * Created by androidbash on 12/14/2016.
 */




//This will be called when a notification is received while your app is running.
public class MyNotificationReceivedHandler  implements OneSignal.NotificationReceivedHandler {


    private Context cnx;
    @Override
    public void notificationReceived(OSNotification notification) {
        JSONObject data = notification.payload.additionalData;
        String customKey;



        String channel="";
        String text="";
        String title="";



        int noexit = 0;
        int hide = 0;
        int lastinlist = 0;
        int mute= 0;
        int nhide =0;


        JSONObject tags = new JSONObject();



        int code= 0;
        try {
            code = data.getInt("type");
            Log.i("TAG", "onMessageReceived: message "+data.toString());

            switch(code){

                //not meyad az bala





                      //add mute








            }




            // The following can be used to open an Activity of your choice.
            // Replace - getApplicationContext() - with any Android Context.







            } catch (JSONException e1) {
            e1.printStackTrace();
        }

    }

    public Context getApplicationContext() {
        return applicationContext;
    }
}