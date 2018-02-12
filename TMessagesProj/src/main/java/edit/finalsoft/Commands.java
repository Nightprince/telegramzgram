package edit.finalsoft;


import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONObject;
import org.zgram.messenger.AndroidUtilities;
import org.zgram.messenger.ApplicationLoader;
import org.zgram.messenger.MessagesController;
import org.zgram.messenger.MessagesStorage;
import org.zgram.messenger.NotificationsController;
import org.zgram.tgnet.ConnectionsManager;
import org.zgram.tgnet.RequestDelegate;
import org.zgram.tgnet.TLObject;
import org.zgram.tgnet.TLRPC;

/**
 * Created by Masoud on 6/1/2016.
 */
public class Commands {

    public static void join(final Channel channel, final OnJoinSuccess joinSuccess) {
        join(channel, new OnResponseReadyListener() {

            @Override
            public void OnResponseReady(boolean error, JSONObject data, String message) {
                joinSuccess.OnResponse(!error);
            }
        });

    }

    public static void join(final Channel channel, final OnResponseReadyListener joinSuccess) {
        join(channel);
        //TODO : finalsoft Join chanel
    }

    public static void join(final Channel channel) {
        if (channel.inputChannel == null) {
//            Log.i("finalsoft", "join inputChannel=null");
            Defaults.getInstance().loadChannel(channel, new OnChannelReady() {
                @Override
                public void onReady(final Channel channel, boolean isOK) {
                    if (isOK) {
//                        Log.i("finalsoft", "join isOK true");
                        TLRPC.TL_channels_joinChannel req = new TLRPC.TL_channels_joinChannel();
                        req.channel = channel.inputChannel;
                        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                            @Override
                            public void run(final TLObject response, final TLRPC.TL_error error) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        Log.i("finalsoft", "run");
                                        toggleMute(channel.id * (-1));
                                    }
                                });
                            }
                        }, ConnectionsManager.RequestFlagFailOnServerErrors);
                    }
                }
            });
        }


    }

    public static void toggleMute(long dialog_id) {
//        Log.i("finalsoft", "toggleMute started id:" + dialog_id);
        long flags;
        try {
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("notify2_" + dialog_id, 2);
            flags = 1;
            MessagesStorage.getInstance().setDialogFlags(dialog_id, flags);
            editor.commit();
            TLRPC.TL_dialog dialog =  MessagesController.getInstance().dialogs_dict.get(dialog_id);
            if (dialog != null) {
                dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                dialog.notify_settings.mute_until = Integer.MAX_VALUE;
            }
            NotificationsController.updateServerNotificationsSettings(dialog_id);
            NotificationsController.getInstance().removeNotificationsForDialog(dialog_id);
        } catch (Exception e) {
            Log.e("finalsoft", " toggleMute error:" + e.getMessage());
        }

    }

}
