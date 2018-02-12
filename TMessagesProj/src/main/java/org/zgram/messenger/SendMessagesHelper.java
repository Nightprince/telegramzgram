package org.zgram.messenger;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v13.view.inputmethod.InputContentInfoCompat;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import com.google.firebase.analytics.FirebaseAnalytics.Param;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.apache.commons.io.IOUtils;
import org.zgram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.zgram.messenger.audioinfo.AudioInfo;
import org.zgram.messenger.exoplayer2.DefaultRenderersFactory;
import org.zgram.messenger.exoplayer2.util.MimeTypes;
import org.zgram.messenger.query.DraftQuery;
import org.zgram.messenger.query.SearchQuery;
import org.zgram.messenger.query.StickersQuery;
import org.zgram.tgnet.AbstractSerializedData;
import org.zgram.tgnet.ConnectionsManager;
import org.zgram.tgnet.NativeByteBuffer;
import org.zgram.tgnet.QuickAckDelegate;
import org.zgram.tgnet.RequestDelegate;
import org.zgram.tgnet.SerializedData;
import org.zgram.tgnet.TLObject;
import org.zgram.tgnet.TLRPC;
import org.zgram.tgnet.TLRPC.Chat;
import org.zgram.tgnet.TLRPC.ChatFull;
import org.zgram.tgnet.TLRPC.EncryptedChat;
import org.zgram.tgnet.TLRPC.FileLocation;
import org.zgram.tgnet.TLRPC.InputDocument;
import org.zgram.tgnet.TLRPC.InputEncryptedFile;
import org.zgram.tgnet.TLRPC.InputFile;
import org.zgram.tgnet.TLRPC.InputMedia;
import org.zgram.tgnet.TLRPC.InputPeer;
import org.zgram.tgnet.TLRPC.KeyboardButton;
import org.zgram.tgnet.TLRPC.Message;
import org.zgram.tgnet.TLRPC.MessageEntity;
import org.zgram.tgnet.TLRPC.MessageFwdHeader;
import org.zgram.tgnet.TLRPC.MessageMedia;
import org.zgram.tgnet.TLRPC.Peer;
import org.zgram.tgnet.TLRPC.PhotoSize;
import org.zgram.tgnet.TLRPC.ReplyMarkup;
import org.zgram.tgnet.TLRPC.TL_decryptedMessage;
import org.zgram.tgnet.TLRPC.TL_decryptedMessageActionAbortKey;
import org.zgram.tgnet.TLRPC.TL_decryptedMessageActionAcceptKey;
import org.zgram.tgnet.TLRPC.TL_decryptedMessageActionCommitKey;
import org.zgram.tgnet.TLRPC.TL_decryptedMessageActionDeleteMessages;
import org.zgram.tgnet.TLRPC.TL_decryptedMessageActionFlushHistory;
import org.zgram.tgnet.TLRPC.TL_decryptedMessageActionNoop;
import org.zgram.tgnet.TLRPC.TL_decryptedMessageActionNotifyLayer;
import org.zgram.tgnet.TLRPC.TL_decryptedMessageActionReadMessages;
import org.zgram.tgnet.TLRPC.TL_decryptedMessageActionRequestKey;
import org.zgram.tgnet.TLRPC.TL_decryptedMessageActionResend;
import org.zgram.tgnet.TLRPC.TL_decryptedMessageActionScreenshotMessages;
import org.zgram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.zgram.tgnet.TLRPC.TL_decryptedMessageActionTyping;
import org.zgram.tgnet.TLRPC.TL_decryptedMessageMediaDocument;
import org.zgram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto;
import org.zgram.tgnet.TLRPC.TL_decryptedMessageMediaVideo;
import org.zgram.tgnet.TLRPC.TL_document;
import org.zgram.tgnet.TLRPC.TL_documentAttributeAnimated;
import org.zgram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.zgram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.zgram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.zgram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.zgram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.zgram.tgnet.TLRPC.TL_documentAttributeVideo_layer65;
import org.zgram.tgnet.TLRPC.TL_error;
import org.zgram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.zgram.tgnet.TLRPC.TL_game;
import org.zgram.tgnet.TLRPC.TL_geoPoint;
import org.zgram.tgnet.TLRPC.TL_inputPeerChannel;
import org.zgram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.zgram.tgnet.TLRPC.TL_inputPeerUser;
import org.zgram.tgnet.TLRPC.TL_inputStickerSetEmpty;
import org.zgram.tgnet.TLRPC.TL_keyboardButtonBuy;
import org.zgram.tgnet.TLRPC.TL_keyboardButtonGame;
import org.zgram.tgnet.TLRPC.TL_message;
import org.zgram.tgnet.TLRPC.TL_messageActionScreenshotTaken;
import org.zgram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.zgram.tgnet.TLRPC.TL_messageEntityBold;
import org.zgram.tgnet.TLRPC.TL_messageEntityCode;
import org.zgram.tgnet.TLRPC.TL_messageEntityItalic;
import org.zgram.tgnet.TLRPC.TL_messageEntityPre;
import org.zgram.tgnet.TLRPC.TL_messageEntityTextUrl;
import org.zgram.tgnet.TLRPC.TL_messageFwdHeader;
import org.zgram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.zgram.tgnet.TLRPC.TL_messageMediaGame;
import org.zgram.tgnet.TLRPC.TL_messageMediaGeo;
import org.zgram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.zgram.tgnet.TLRPC.TL_messageMediaVenue;
import org.zgram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.zgram.tgnet.TLRPC.TL_messageService;
import org.zgram.tgnet.TLRPC.TL_messages_botCallbackAnswer;
import org.zgram.tgnet.TLRPC.TL_messages_editMessage;
import org.zgram.tgnet.TLRPC.TL_messages_forwardMessages;
import org.zgram.tgnet.TLRPC.TL_messages_getBotCallbackAnswer;
import org.zgram.tgnet.TLRPC.TL_messages_sendBroadcast;
import org.zgram.tgnet.TLRPC.TL_messages_sendMedia;
import org.zgram.tgnet.TLRPC.TL_messages_sendMessage;
import org.zgram.tgnet.TLRPC.TL_messages_sendScreenshotNotification;
import org.zgram.tgnet.TLRPC.TL_payments_getPaymentForm;
import org.zgram.tgnet.TLRPC.TL_payments_getPaymentReceipt;
import org.zgram.tgnet.TLRPC.TL_payments_paymentForm;
import org.zgram.tgnet.TLRPC.TL_payments_paymentReceipt;
import org.zgram.tgnet.TLRPC.TL_peerChannel;
import org.zgram.tgnet.TLRPC.TL_peerUser;
import org.zgram.tgnet.TLRPC.TL_photo;
import org.zgram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.zgram.tgnet.TLRPC.TL_updateMessageID;
import org.zgram.tgnet.TLRPC.TL_updateNewChannelMessage;
import org.zgram.tgnet.TLRPC.TL_updateNewMessage;
import org.zgram.tgnet.TLRPC.TL_updateShortSentMessage;
import org.zgram.tgnet.TLRPC.TL_userContact_old2;
import org.zgram.tgnet.TLRPC.Update;
import org.zgram.tgnet.TLRPC.Updates;
import org.zgram.tgnet.TLRPC.User;
import org.zgram.tgnet.TLRPC.WebPage;
import org.zgram.ui.ActionBar.AlertDialog.Builder;
import org.zgram.ui.ActionBar.BaseFragment;
import org.zgram.ui.ChatActivity;
import org.zgram.ui.Components.AlertsCreator;
import org.zgram.ui.PaymentFormActivity;

public class SendMessagesHelper implements NotificationCenterDelegate {
    private static volatile SendMessagesHelper Instance = null;
    private ChatFull currentChatInfo = null;
    private HashMap<String, ArrayList<DelayedMessage>> delayedMessages = new HashMap();
    private LocationProvider locationProvider = new LocationProvider(new C11961());
    private HashMap<Integer, Message> sendingMessages = new HashMap();
    private HashMap<Integer, MessageObject> unsentMessages = new HashMap();
    private HashMap<String, MessageObject> waitingForCallback = new HashMap();
    private HashMap<String, MessageObject> waitingForLocation = new HashMap();

    class C11961 implements LocationProvider.LocationProviderDelegate {
        C11961() {
        }

        public void onLocationAcquired(Location location) {
            SendMessagesHelper.this.sendLocation(location);
            SendMessagesHelper.this.waitingForLocation.clear();
        }

        public void onUnableLocationAcquire() {
            HashMap<String, MessageObject> waitingForLocationCopy = new HashMap(SendMessagesHelper.this.waitingForLocation);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.wasUnableToFindCurrentLocation, waitingForLocationCopy);
            SendMessagesHelper.this.waitingForLocation.clear();
        }
    }

    protected class DelayedMessage {
        public TL_document documentLocation;
        public EncryptedChat encryptedChat;
        public String httpLocation;
        public FileLocation location;
        public MessageObject obj;
        public String originalPath;
        public TL_decryptedMessage sendEncryptedRequest;
        public TLObject sendRequest;
        public int type;
        public VideoEditedInfo videoEditedInfo;

        protected DelayedMessage() {
        }
    }

    public static class LocationProvider {
        private LocationProviderDelegate delegate;
        private GpsLocationListener gpsLocationListener = new GpsLocationListener();
        private Location lastKnownLocation;
        private LocationManager locationManager;
        private Runnable locationQueryCancelRunnable;
        private GpsLocationListener networkLocationListener = new GpsLocationListener();

        public interface LocationProviderDelegate {
            void onLocationAcquired(Location location);

            void onUnableLocationAcquire();
        }

        class C12221 implements Runnable {
            C12221() {
            }

            public void run() {
                if (LocationProvider.this.locationQueryCancelRunnable == this) {
                    if (LocationProvider.this.delegate != null) {
                        if (LocationProvider.this.lastKnownLocation != null) {
                            LocationProvider.this.delegate.onLocationAcquired(LocationProvider.this.lastKnownLocation);
                        } else {
                            LocationProvider.this.delegate.onUnableLocationAcquire();
                        }
                    }
                    LocationProvider.this.cleanup();
                }
            }
        }

        private class GpsLocationListener implements LocationListener {
            private GpsLocationListener() {
            }

            public void onLocationChanged(Location location) {
                if (location != null && LocationProvider.this.locationQueryCancelRunnable != null) {
                    FileLog.e("found location " + location);
                    LocationProvider.this.lastKnownLocation = location;
                    if (location.getAccuracy() < 100.0f) {
                        if (LocationProvider.this.delegate != null) {
                            LocationProvider.this.delegate.onLocationAcquired(location);
                        }
                        if (LocationProvider.this.locationQueryCancelRunnable != null) {
                            AndroidUtilities.cancelRunOnUIThread(LocationProvider.this.locationQueryCancelRunnable);
                        }
                        LocationProvider.this.cleanup();
                    }
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        }

        public LocationProvider(LocationProviderDelegate locationProviderDelegate) {
            this.delegate = locationProviderDelegate;
        }

        public void setDelegate(LocationProviderDelegate locationProviderDelegate) {
            this.delegate = locationProviderDelegate;
        }

        private void cleanup() {
            this.locationManager.removeUpdates(this.gpsLocationListener);
            this.locationManager.removeUpdates(this.networkLocationListener);
            this.lastKnownLocation = null;
            this.locationQueryCancelRunnable = null;
        }

        public void start() {
            if (this.locationManager == null) {
                this.locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService(Param.LOCATION);
            }
            try {
                this.locationManager.requestLocationUpdates("gps", 1, 0.0f, this.gpsLocationListener);
            } catch (Throwable e) {
                FileLog.e(e);
            }
            try {
                this.locationManager.requestLocationUpdates("network", 1, 0.0f, this.networkLocationListener);
            } catch (Throwable e2) {
                FileLog.e(e2);
            }
            try {
                this.lastKnownLocation = this.locationManager.getLastKnownLocation("gps");
                if (this.lastKnownLocation == null) {
                    this.lastKnownLocation = this.locationManager.getLastKnownLocation("network");
                }
            } catch (Throwable e22) {
                FileLog.e(e22);
            }
            if (this.locationQueryCancelRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.locationQueryCancelRunnable);
            }
            this.locationQueryCancelRunnable = new C12221();
            AndroidUtilities.runOnUIThread(this.locationQueryCancelRunnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        }

        public void stop() {
            if (this.locationManager != null) {
                if (this.locationQueryCancelRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(this.locationQueryCancelRunnable);
                }
                cleanup();
            }
        }
    }

    //private static volatile SendMessagesHelper Instance = null;

    public static SendMessagesHelper getInstance() {
        SendMessagesHelper localInstance = Instance;
        if (localInstance == null) {
            synchronized (SendMessagesHelper.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new SendMessagesHelper();
                }
            }
        }
        return localInstance;
    }

    public SendMessagesHelper() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidUpload);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailUpload);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FilePreparingStarted);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileNewChunkAvailable);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FilePreparingFailed);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.httpFileDidFailedLoad);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.httpFileDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
    }

    public void cleanup() {
        this.delayedMessages.clear();
        this.unsentMessages.clear();
        this.sendingMessages.clear();
        this.waitingForLocation.clear();
        this.waitingForCallback.clear();
        this.currentChatInfo = null;
        this.locationProvider.stop();
    }

    public void setCurrentChatInfo(ChatFull info) {
        this.currentChatInfo = info;
    }

    public void didReceivedNotification(int id, Object... args) {
        String location;
        ArrayList<DelayedMessage> arr;
        int a;
        DelayedMessage message;
        if (id == NotificationCenter.FileDidUpload) {
            location = (String) args[0];
            InputFile file = (InputFile) args[1];
            InputEncryptedFile encryptedFile = (InputEncryptedFile) args[2];
            arr = (ArrayList) this.delayedMessages.get(location);
            if (arr != null) {
                a = 0;
                while (a < arr.size()) {
                    message = (DelayedMessage) arr.get(a);
                    InputMedia media = null;
                    if (message.sendRequest instanceof TL_messages_sendMedia) {
                        media = ((TL_messages_sendMedia) message.sendRequest).media;
                    } else if (message.sendRequest instanceof TL_messages_sendBroadcast) {
                        media = ((TL_messages_sendBroadcast) message.sendRequest).media;
                    }
                    if (file != null && media != null) {
                        if (message.type == 0) {
                            media.file = file;
                            performSendMessageRequest(message.sendRequest, message.obj, message.originalPath);
                        } else if (message.type == 1) {
                            if (media.file == null) {
                                media.file = file;
                                if (media.thumb != null || message.location == null) {
                                    performSendMessageRequest(message.sendRequest, message.obj, message.originalPath);
                                } else {
                                    performSendDelayedMessage(message);
                                }
                            } else {
                                media.thumb = file;
                                media.flags |= 4;
                                performSendMessageRequest(message.sendRequest, message.obj, message.originalPath);
                            }
                        } else if (message.type == 2) {
                            if (media.file == null) {
                                media.file = file;
                                if (media.thumb != null || message.location == null) {
                                    performSendMessageRequest(message.sendRequest, message.obj, message.originalPath);
                                } else {
                                    performSendDelayedMessage(message);
                                }
                            } else {
                                media.thumb = file;
                                media.flags |= 4;
                                performSendMessageRequest(message.sendRequest, message.obj, message.originalPath);
                            }
                        } else if (message.type == 3) {
                            media.file = file;
                            performSendMessageRequest(message.sendRequest, message.obj, message.originalPath);
                        }
                        arr.remove(a);
                        a--;
                    } else if (!(encryptedFile == null || message.sendEncryptedRequest == null)) {
                        if ((message.sendEncryptedRequest.media instanceof TL_decryptedMessageMediaVideo) || (message.sendEncryptedRequest.media instanceof TL_decryptedMessageMediaPhoto) || (message.sendEncryptedRequest.media instanceof TL_decryptedMessageMediaDocument)) {
                            message.sendEncryptedRequest.media.size = (int) ((Long) args[5]).longValue();
                        }
                        message.sendEncryptedRequest.media.key = (byte[]) args[3];
                        message.sendEncryptedRequest.media.iv = (byte[]) args[4];
                        SecretChatHelper.getInstance().performSendEncryptedRequest(message.sendEncryptedRequest, message.obj.messageOwner, message.encryptedChat, encryptedFile, message.originalPath, message.obj);
                        arr.remove(a);
                        a--;
                    }
                    a++;
                }
                if (arr.isEmpty()) {
                    this.delayedMessages.remove(location);
                }
            }
        } else if (id == NotificationCenter.FileDidFailUpload) {
            location = (String) args[0];
            boolean enc = ((Boolean) args[1]).booleanValue();
            arr = (ArrayList) this.delayedMessages.get(location);
            if (arr != null) {
                a = 0;
                while (a < arr.size()) {
                    DelayedMessage obj = (DelayedMessage) arr.get(a);
                    if ((enc && obj.sendEncryptedRequest != null) || !(enc || obj.sendRequest == null)) {
                        MessagesStorage.getInstance().markMessageAsSendError(obj.obj.messageOwner);
                        obj.obj.messageOwner.send_state = 2;
                        arr.remove(a);
                        a--;
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(obj.obj.getId()));
                        processSentMessage(obj.obj.getId());
                    }
                    a++;
                }
                if (arr.isEmpty()) {
                    this.delayedMessages.remove(location);
                }
            }
        } else if (id == NotificationCenter.FilePreparingStarted) {
            MessageObject messageObject = (MessageObject) args[0];
            //messageObject = args[0];
            if (messageObject.getId() != 0) {
                String finalPath = (String) args[1];
                //finalPath = args[1];
                arr = (ArrayList) this.delayedMessages.get(messageObject.messageOwner.attachPath);
                if (arr != null) {
                    for (a = 0; a < arr.size(); a++) {
                        message = (DelayedMessage) arr.get(a);
                        if (message.obj == messageObject) {
                            message.videoEditedInfo = null;
                            performSendDelayedMessage(message);
                            arr.remove(a);
                            break;
                        }
                    }
                    if (arr.isEmpty()) {
                        this.delayedMessages.remove(messageObject.messageOwner.attachPath);
                    }
                }
            }
        } else if (id == NotificationCenter.FileNewChunkAvailable) {
            MessageObject messageObject = (MessageObject) args[0];
            if (messageObject.getId() != 0) {
                String finalPath = (String) args[1];
                //finalPath = (String) args[1];
                long finalSize = ((Long) args[2]).longValue();
                FileLoader.getInstance().checkUploadNewDataAvailable(finalPath, ((int) messageObject.getDialogId()) == 0, finalSize);
                if (finalSize != 0) {
                    arr = (ArrayList) this.delayedMessages.get(messageObject.messageOwner.attachPath);
                    if (arr != null) {
                        for (a = 0; a < arr.size(); a++) {
                            message = (DelayedMessage) arr.get(a);
                            if (message.obj == messageObject) {
                                message.obj.videoEditedInfo = null;
                                message.obj.messageOwner.message = "-1";
                                message.obj.messageOwner.media.document.size = (int) finalSize;
                                ArrayList messages = new ArrayList();
                                messages.add(message.obj.messageOwner);
                                MessagesStorage.getInstance().putMessages(messages, false, true, false, 0);
                                break;
                            }
                        }
                        if (arr.isEmpty()) {
                            this.delayedMessages.remove(messageObject.messageOwner.attachPath);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.FilePreparingFailed) {
            MessageObject messageObject = (MessageObject) args[0];
            // messageObject = (MessageObject) args[0];
            if (messageObject.getId() != 0) {

                String finalPath = (String) args[1];
                stopVideoService(messageObject.messageOwner.attachPath);
                arr = (ArrayList) this.delayedMessages.get(finalPath);
                if (arr != null) {
                    a = 0;
                    while (a < arr.size()) {
                        message = (DelayedMessage) arr.get(a);
                        if (message.obj == messageObject) {
                            MessagesStorage.getInstance().markMessageAsSendError(message.obj.messageOwner);
                            message.obj.messageOwner.send_state = 2;
                            arr.remove(a);
                            a--;
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message.obj.getId()));
                            processSentMessage(message.obj.getId());
                        }
                        a++;
                    }
                    if (arr.isEmpty()) {
                        this.delayedMessages.remove(finalPath);
                    }
                }
            }
        } else if (id == NotificationCenter.httpFileDidLoaded) {
            String path = (String) args[0];
            arr = (ArrayList) this.delayedMessages.get(path);
            if (arr != null) {
                for (a = 0; a < arr.size(); a++) {
                    message = (DelayedMessage) arr.get(a);
                    if (message.type == 0) {
                        final File file2 = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(message.httpLocation) + "." + ImageLoader.getHttpUrlExtension(message.httpLocation, "file"));
                        final DelayedMessage delayedMessage = message;
                        Utilities.globalQueue.postRunnable(new Runnable() {
                            public void run() {
                                final TL_photo photo = SendMessagesHelper.getInstance().generatePhotoSizes(file2.toString(), null);
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        if (photo != null) {
                                            delayedMessage.httpLocation = null;
                                            delayedMessage.obj.messageOwner.media.photo = photo;
                                            delayedMessage.obj.messageOwner.attachPath = file2.toString();
                                            delayedMessage.location = ((PhotoSize) photo.sizes.get(photo.sizes.size() - 1)).location;
                                            ArrayList messages = new ArrayList();
                                            messages.add(delayedMessage.obj.messageOwner);
                                            MessagesStorage.getInstance().putMessages(messages, false, true, false, 0);
                                            SendMessagesHelper.this.performSendDelayedMessage(delayedMessage);
                                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateMessageMedia, delayedMessage.obj.messageOwner);
                                            return;
                                        }
                                        FileLog.e("can't load image " + delayedMessage.httpLocation + " to file " + file2.toString());
                                        MessagesStorage.getInstance().markMessageAsSendError(delayedMessage.obj.messageOwner);
                                        delayedMessage.obj.messageOwner.send_state = 2;
                                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(delayedMessage.obj.getId()));
                                        SendMessagesHelper.this.processSentMessage(delayedMessage.obj.getId());
                                    }
                                });
                            }
                        });
                    } else if (message.type == 2) {
                        final DelayedMessage delayedMessage2 = message;
                        final File file3 = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(message.httpLocation) + ".gif");
                        Utilities.globalQueue.postRunnable(new Runnable() {

                            class C11991 implements Runnable {
                                C11991() {
                                }

                                public void run() {
                                    delayedMessage2.httpLocation = null;
                                    delayedMessage2.obj.messageOwner.attachPath = file3.toString();
                                    delayedMessage2.location = delayedMessage2.documentLocation.thumb.location;
                                    ArrayList messages = new ArrayList();
                                    messages.add(delayedMessage2.obj.messageOwner);
                                    MessagesStorage.getInstance().putMessages(messages, false, true, false, 0);
                                    SendMessagesHelper.this.performSendDelayedMessage(delayedMessage2);
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateMessageMedia, delayedMessage2.obj.messageOwner);
                                }
                            }

                            public void run() {
                                boolean z = true;
                                if (delayedMessage2.documentLocation.thumb.location instanceof TL_fileLocationUnavailable) {
                                    try {
                                        Bitmap bitmap = ImageLoader.loadBitmap(file3.getAbsolutePath(), null, 90.0f, 90.0f, true);
                                        if (bitmap != null) {
                                            TL_document tL_document = delayedMessage2.documentLocation;
                                            if (delayedMessage2.sendEncryptedRequest == null) {
                                                z = false;
                                            }
                                            tL_document.thumb = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, z);
                                            bitmap.recycle();
                                        }
                                    } catch (Throwable e) {
                                        delayedMessage2.documentLocation.thumb = null;
                                        FileLog.e(e);
                                    }
                                    if (delayedMessage2.documentLocation.thumb == null) {
                                        delayedMessage2.documentLocation.thumb = new TL_photoSizeEmpty();
                                        delayedMessage2.documentLocation.thumb.type = "s";
                                    }
                                }
                                AndroidUtilities.runOnUIThread(new C11991());
                            }
                        });
                    }
                }
                this.delayedMessages.remove(path);
            }
        } else if (id == NotificationCenter.FileDidLoaded) {
            String path = (String) args[0];
            // path = (String) args[0];
            arr = (ArrayList) this.delayedMessages.get(path);
            if (arr != null) {
                for (a = 0; a < arr.size(); a++) {
                    performSendDelayedMessage((DelayedMessage) arr.get(a));
                }
                this.delayedMessages.remove(path);
            }
        } else if (id == NotificationCenter.httpFileDidFailedLoad || id == NotificationCenter.FileDidFailedLoad) {
            String path = (String) args[0];
            arr = (ArrayList) this.delayedMessages.get(path);
            if (arr != null) {
                Iterator it = arr.iterator();
                while (it.hasNext()) {
                    message = (DelayedMessage) it.next();
                    MessagesStorage.getInstance().markMessageAsSendError(message.obj.messageOwner);
                    message.obj.messageOwner.send_state = 2;
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message.obj.getId()));
                    processSentMessage(message.obj.getId());
                }
                this.delayedMessages.remove(path);
            }
        }
    }

    public void cancelSendingMessage(MessageObject object) {
        String keyToRemvoe = null;
        boolean enc = false;
        for (Entry<String, ArrayList<DelayedMessage>> entry : this.delayedMessages.entrySet()) {
            ArrayList<DelayedMessage> messages = (ArrayList) entry.getValue();
            int a = 0;
            while (a < messages.size()) {
                DelayedMessage message = (DelayedMessage) messages.get(a);
                if (message.obj.getId() == object.getId()) {
                    messages.remove(a);
                    MediaController.getInstance().cancelVideoConvert(message.obj);
                    if (messages.size() == 0) {
                        keyToRemvoe = (String) entry.getKey();
                        if (message.sendEncryptedRequest != null) {
                            enc = true;
                        }
                    }
                } else {
                    a++;
                }
            }
        }
        if (keyToRemvoe != null) {
            if (keyToRemvoe.startsWith("http")) {
                ImageLoader.getInstance().cancelLoadHttpFile(keyToRemvoe);
            } else {
                FileLoader.getInstance().cancelUploadFile(keyToRemvoe, enc);
            }
            stopVideoService(keyToRemvoe);
        }
        ArrayList<Integer> messages2 = new ArrayList();
        messages2.add(Integer.valueOf(object.getId()));
        MessagesController.getInstance().deleteMessages(messages2, null, null, object.messageOwner.to_id.channel_id, false);
    }

    public boolean retrySendMessage(MessageObject messageObject, boolean unsent) {
        if (messageObject.getId() >= 0) {
            return false;
        }
        if (messageObject.messageOwner.action instanceof TL_messageEncryptedAction) {
            EncryptedChat encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (messageObject.getDialogId() >> 32)));
            if (encryptedChat == null) {
                MessagesStorage.getInstance().markMessageAsSendError(messageObject.messageOwner);
                messageObject.messageOwner.send_state = 2;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(messageObject.getId()));
                processSentMessage(messageObject.getId());
                return false;
            }
            if (messageObject.messageOwner.random_id == 0) {
                messageObject.messageOwner.random_id = getNextRandomId();
            }
            if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL) {
                SecretChatHelper.getInstance().sendTTLMessage(encryptedChat, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionDeleteMessages) {
                SecretChatHelper.getInstance().sendMessagesDeleteMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionFlushHistory) {
                SecretChatHelper.getInstance().sendClearHistoryMessage(encryptedChat, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionNotifyLayer) {
                SecretChatHelper.getInstance().sendNotifyLayerMessage(encryptedChat, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionReadMessages) {
                SecretChatHelper.getInstance().sendMessagesReadMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionScreenshotMessages) {
                SecretChatHelper.getInstance().sendScreenshotMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (!((messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionTyping) || (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionResend))) {
                if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionCommitKey) {
                    SecretChatHelper.getInstance().sendCommitKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionAbortKey) {
                    SecretChatHelper.getInstance().sendAbortKeyMessage(encryptedChat, messageObject.messageOwner, 0);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionRequestKey) {
                    SecretChatHelper.getInstance().sendRequestKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionAcceptKey) {
                    SecretChatHelper.getInstance().sendAcceptKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionNoop) {
                    SecretChatHelper.getInstance().sendNoopMessage(encryptedChat, messageObject.messageOwner);
                }
            }
            return true;
        }
        if (messageObject.messageOwner.action instanceof TL_messageActionScreenshotTaken) {
            sendScreenshotMessage(MessagesController.getInstance().getUser(Integer.valueOf((int) messageObject.getDialogId())), messageObject.messageOwner.reply_to_msg_id, messageObject.messageOwner);
        }
        if (unsent) {
            this.unsentMessages.put(Integer.valueOf(messageObject.getId()), messageObject);
        }
        sendMessage(messageObject);
        return true;
    }

    protected void processSentMessage(int id) {
        int prevSize = this.unsentMessages.size();
        this.unsentMessages.remove(Integer.valueOf(id));
        if (prevSize != 0 && this.unsentMessages.size() == 0) {
            checkUnsentMessages();
        }
    }

    public void processForwardFromMyName(MessageObject messageObject, long did) {
        if (messageObject != null) {
            ArrayList<MessageObject> arrayList;
            if (messageObject.messageOwner.media == null || (messageObject.messageOwner.media instanceof TL_messageMediaEmpty) || (messageObject.messageOwner.media instanceof TL_messageMediaWebPage)) {
                if (messageObject.messageOwner.message != null) {
                    WebPage webPage = null;
                    if (messageObject.messageOwner.media instanceof TL_messageMediaWebPage) {
                        webPage = messageObject.messageOwner.media.webpage;
                    }
                    sendMessage(messageObject.messageOwner.message, did, messageObject.replyMessageObject, webPage, true, messageObject.messageOwner.entities, null, null);
                    return;
                }
                arrayList = new ArrayList();
                arrayList.add(messageObject);
                sendMessage(arrayList, did);
            } else if (messageObject.messageOwner.media.photo instanceof TL_photo) {
                TL_photo tl_photo = (TL_photo) messageObject.messageOwner.media.photo;
                if (messageObject.caption != null) {
                    tl_photo.caption = messageObject.messageOwner.media.caption;
                }
                sendMessage((TL_photo) messageObject.messageOwner.media.photo, null, did, messageObject.replyMessageObject, null, null, messageObject.messageOwner.media.ttl_seconds);
            } else if (messageObject.messageOwner.media.document instanceof TL_document) {
                TL_document tl_document = (TL_document) messageObject.messageOwner.media.document;
                if (messageObject.caption != null) {
                    tl_document.caption = messageObject.messageOwner.media.caption;
                }
                sendMessage((TL_document) messageObject.messageOwner.media.document, null, messageObject.messageOwner.attachPath, did, messageObject.replyMessageObject, null, null, messageObject.messageOwner.media.ttl_seconds);
            } else if ((messageObject.messageOwner.media instanceof TL_messageMediaVenue) || (messageObject.messageOwner.media instanceof TL_messageMediaGeo)) {
                sendMessage(messageObject.messageOwner.media, did, messageObject.replyMessageObject, null, null);
            } else if (messageObject.messageOwner.media.phone_number != null) {
                User user = new TL_userContact_old2();
                user.phone = messageObject.messageOwner.media.phone_number;
                user.first_name = messageObject.messageOwner.media.first_name;
                user.last_name = messageObject.messageOwner.media.last_name;
                user.id = messageObject.messageOwner.media.user_id;
                sendMessage(user, did, messageObject.replyMessageObject, null, null);
            } else {
                arrayList = new ArrayList();
                arrayList.add(messageObject);
                sendMessage(arrayList, did);
            }
        }
    }

    public void sendScreenshotMessage(User user, int messageId, Message resendMessage) {
        if (user != null && messageId != 0 && user.id != UserConfig.getClientUserId()) {
            Message message;
            TL_messages_sendScreenshotNotification req = new TL_messages_sendScreenshotNotification();
            req.peer = new TL_inputPeerUser();
            req.peer.access_hash = user.access_hash;
            req.peer.user_id = user.id;
            if (resendMessage != null) {
                message = resendMessage;
                req.reply_to_msg_id = messageId;
                req.random_id = resendMessage.random_id;
            } else {
                message = new TL_messageService();
                message.random_id = getNextRandomId();
                message.dialog_id = (long) user.id;
                message.unread = true;
                message.out = true;
                int newMessageId = UserConfig.getNewMessageId();
                message.id = newMessageId;
                message.local_id = newMessageId;
                message.from_id = UserConfig.getClientUserId();
                message.flags |= 256;
                message.flags |= 8;
                message.reply_to_msg_id = messageId;
                message.to_id = new TL_peerUser();
                message.to_id.user_id = user.id;
                message.date = ConnectionsManager.getInstance().getCurrentTime();
                message.action = new TL_messageActionScreenshotTaken();
                UserConfig.saveConfig(false);
            }
            req.random_id = message.random_id;
            MessageObject newMsgObj = new MessageObject(message, null, false);
            newMsgObj.messageOwner.send_state = 1;
            ArrayList<MessageObject> objArr = new ArrayList();
            objArr.add(newMsgObj);
            MessagesController.getInstance().updateInterfaceWithMessages(message.dialog_id, objArr);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            ArrayList arr = new ArrayList();
            arr.add(message);
            MessagesStorage.getInstance().putMessages(arr, false, true, false, 0);
            performSendMessageRequest(req, newMsgObj, null);
        }
    }

    public void sendSticker(TLRPC.Document document, long peer, MessageObject replyingMessageObject) {
        if (document == null) {
            return;
        }
        if ((int) peer == 0) {
            int high_id = (int) (peer >> 32);
            TLRPC.EncryptedChat encryptedChat = MessagesController.getInstance().getEncryptedChat(high_id);
            if (encryptedChat == null) {
                return;
            }
            TLRPC.TL_document newDocument = new TLRPC.TL_document();
            newDocument.id = document.id;
            newDocument.access_hash = document.access_hash;
            newDocument.date = document.date;
            newDocument.mime_type = document.mime_type;
            newDocument.size = document.size;
            newDocument.dc_id = document.dc_id;
            newDocument.attributes = new ArrayList<>(document.attributes);
            if (newDocument.mime_type == null) {
                newDocument.mime_type = "";
            }
            if (document.thumb instanceof TLRPC.TL_photoSize) {
                File file = FileLoader.getPathToAttach(document.thumb, true);
                if (file.exists()) {
                    try {
                        int len = (int) file.length();
                        byte[] arr = new byte[(int) file.length()];
                        RandomAccessFile reader = new RandomAccessFile(file, "r");
                        reader.readFully(arr);

                        newDocument.thumb = new TLRPC.TL_photoCachedSize();
                        newDocument.thumb.location = document.thumb.location;
                        newDocument.thumb.size = document.thumb.size;
                        newDocument.thumb.w = document.thumb.w;
                        newDocument.thumb.h = document.thumb.h;
                        newDocument.thumb.type = document.thumb.type;
                        newDocument.thumb.bytes = arr;
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            }
            if (newDocument.thumb == null) {
                newDocument.thumb = new TLRPC.TL_photoSizeEmpty();
                newDocument.thumb.type = "s";
            }
            document = newDocument;
        }
        SendMessagesHelper.getInstance().sendMessage((TLRPC.TL_document) document, null, null, peer, replyingMessageObject, null, null, 0);
    }

    public void sendMessage(ArrayList<MessageObject> messages, long peer) {
        if (messages != null && !messages.isEmpty()) {
            int lower_id = (int) peer;
            int a;
            if (lower_id != 0) {
                Chat chat;
                final Peer to_id = MessagesController.getPeer((int) peer);
                boolean isMegagroup = false;
                boolean isSignature = false;
                if (lower_id <= 0) {
                    chat = MessagesController.getInstance().getChat(Integer.valueOf(-lower_id));
                    if (ChatObject.isChannel(chat)) {
                        isMegagroup = chat.megagroup;
                        isSignature = chat.signatures;
                    }
                } else if (MessagesController.getInstance().getUser(Integer.valueOf(lower_id)) == null) {
                    return;
                }
                ArrayList<MessageObject> objArr = new ArrayList();
                ArrayList<Message> arr = new ArrayList();
                ArrayList<Long> randomIds = new ArrayList();
                ArrayList<Integer> ids = new ArrayList();
                HashMap<Long, Message> messagesByRandomIds = new HashMap();
                InputPeer inputPeer = MessagesController.getInputPeer(lower_id);
                boolean toMyself = peer == ((long) UserConfig.getClientUserId());
                a = 0;
                while (a < messages.size()) {
                    MessageObject msgObj = (MessageObject) messages.get(a);
                    if (msgObj.getId() > 0) {
                        Message newMsg = new TL_message();
                        if (msgObj.isForwarded()) {
                            newMsg.fwd_from = msgObj.messageOwner.fwd_from;
                        } else {
                            newMsg.fwd_from = new TL_messageFwdHeader();
                            MessageFwdHeader messageFwdHeader;
                            if (msgObj.isFromUser()) {
                                newMsg.fwd_from.from_id = msgObj.messageOwner.from_id;
                                messageFwdHeader = newMsg.fwd_from;
                                messageFwdHeader.flags |= 1;
                            } else {
                                newMsg.fwd_from.channel_id = msgObj.messageOwner.to_id.channel_id;
                                messageFwdHeader = newMsg.fwd_from;
                                messageFwdHeader.flags |= 2;
                                if (msgObj.messageOwner.post) {
                                    newMsg.fwd_from.channel_post = msgObj.getId();
                                    messageFwdHeader = newMsg.fwd_from;
                                    messageFwdHeader.flags |= 4;
                                    if (msgObj.messageOwner.from_id > 0) {
                                        newMsg.fwd_from.from_id = msgObj.messageOwner.from_id;
                                        messageFwdHeader = newMsg.fwd_from;
                                        messageFwdHeader.flags |= 1;
                                    }
                                }
                            }
                            newMsg.date = msgObj.messageOwner.date;
                        }
                        newMsg.media = msgObj.messageOwner.media;
                        newMsg.flags = 4;
                        if (newMsg.media != null) {
                            newMsg.flags |= 512;
                        }
                        if (isMegagroup) {
                            newMsg.flags |= Integer.MIN_VALUE;
                        }
                        if (msgObj.messageOwner.via_bot_id != 0) {
                            newMsg.via_bot_id = msgObj.messageOwner.via_bot_id;
                            newMsg.flags |= 2048;
                        }
                        newMsg.message = msgObj.messageOwner.message;
                        newMsg.fwd_msg_id = msgObj.getId();
                        newMsg.attachPath = msgObj.messageOwner.attachPath;
                        newMsg.entities = msgObj.messageOwner.entities;
                        if (!newMsg.entities.isEmpty()) {
                            newMsg.flags |= 128;
                        }
                        if (newMsg.attachPath == null) {
                            newMsg.attachPath = "";
                        }
                        int newMessageId = UserConfig.getNewMessageId();
                        newMsg.id = newMessageId;
                        newMsg.local_id = newMessageId;
                        newMsg.out = true;
                        if (to_id.channel_id == 0 || isMegagroup) {
                            newMsg.from_id = UserConfig.getClientUserId();
                            newMsg.flags |= 256;
                        } else {
                            newMsg.from_id = isSignature ? UserConfig.getClientUserId() : -to_id.channel_id;
                            newMsg.post = true;
                        }
                        if (newMsg.random_id == 0) {
                            newMsg.random_id = getNextRandomId();
                        }
                        randomIds.add(Long.valueOf(newMsg.random_id));
                        messagesByRandomIds.put(Long.valueOf(newMsg.random_id), newMsg);
                        ids.add(Integer.valueOf(newMsg.fwd_msg_id));
                        newMsg.date = ConnectionsManager.getInstance().getCurrentTime();
                        if (!(inputPeer instanceof TL_inputPeerChannel)) {
                            if ((msgObj.messageOwner.flags & 1024) != 0) {
                                newMsg.views = msgObj.messageOwner.views;
                                newMsg.flags |= 1024;
                            }
                            newMsg.unread = true;
                        } else if (isMegagroup) {
                            newMsg.unread = true;
                        } else {
                            newMsg.views = 1;
                            newMsg.flags |= 1024;
                        }
                        newMsg.dialog_id = peer;
                        newMsg.to_id = to_id;
                        if (MessageObject.isVoiceMessage(newMsg) && newMsg.to_id.channel_id == 0) {
                            newMsg.media_unread = true;
                        }
                        if (msgObj.messageOwner.to_id instanceof TL_peerChannel) {
                            newMsg.ttl = -msgObj.messageOwner.to_id.channel_id;
                        }
                        MessageObject messageObject = new MessageObject(newMsg, null, true);
                        messageObject.messageOwner.send_state = 1;
                        objArr.add(messageObject);
                        arr.add(newMsg);
                        putToSendingMessages(newMsg);
                        if (BuildVars.DEBUG_VERSION) {
                            FileLog.e("forward message user_id = " + inputPeer.user_id + " chat_id = " + inputPeer.chat_id + " channel_id = " + inputPeer.channel_id + " access_hash = " + inputPeer.access_hash);
                        }
                        if (!(arr.size() == 100 || a == messages.size() - 1)) {
                            if (a != messages.size() - 1) {
                                if (((MessageObject) messages.get(a + 1)).getDialogId() == msgObj.getDialogId()) {
                                }
                            }
                        }
                        MessagesStorage.getInstance().putMessages(new ArrayList(arr), false, true, false, 0);
                        MessagesController.getInstance().updateInterfaceWithMessages(peer, objArr);
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                        UserConfig.saveConfig(false);
                        final TL_messages_forwardMessages req = new TL_messages_forwardMessages();
                        req.to_peer = inputPeer;
                        if (req.to_peer instanceof TL_inputPeerChannel) {
                            req.silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("silent_" + peer, false);
                        }
                        if (msgObj.messageOwner.to_id instanceof TL_peerChannel) {
                            chat = MessagesController.getInstance().getChat(Integer.valueOf(msgObj.messageOwner.to_id.channel_id));
                            req.from_peer = new TL_inputPeerChannel();
                            req.from_peer.channel_id = msgObj.messageOwner.to_id.channel_id;
                            if (chat != null) {
                                req.from_peer.access_hash = chat.access_hash;
                            }
                        } else {
                            req.from_peer = new TL_inputPeerEmpty();
                        }
                        req.random_id = randomIds;
                        req.id = ids;
                        boolean z = messages.size() == 1 && ((MessageObject) messages.get(0)).messageOwner.with_my_score;
                        req.with_my_score = z;
                        final ArrayList<Message> newMsgObjArr = arr;
                        final ArrayList<MessageObject> newMsgArr = objArr;
                        final HashMap<Long, Message> messagesByRandomIdsFinal = messagesByRandomIds;
                        final boolean isMegagroupFinal = isMegagroup;
                        final long j = peer;
                        final boolean z2 = toMyself;
                        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                            public void run(TLObject response, TL_error error) {
                                int a;
                                Message newMsgObj;
                                if (error == null) {
                                    Update update;
                                    HashMap<Integer, Long> newMessagesByIds = new HashMap();
                                    Updates updates = (Updates) response;
                                    a = 0;
                                    while (a < updates.updates.size()) {
                                        update = (Update) updates.updates.get(a);
                                        if (update instanceof TL_updateMessageID) {
                                            TL_updateMessageID updateMessageID = (TL_updateMessageID) update;
                                            newMessagesByIds.put(Integer.valueOf(updateMessageID.id), Long.valueOf(updateMessageID.random_id));
                                            updates.updates.remove(a);
                                            a--;
                                        }
                                        a++;
                                    }
                                    Integer value = (Integer) MessagesController.getInstance().dialogs_read_outbox_max.get(Long.valueOf(j));
                                    if (value == null) {
                                        value = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, j));
                                        MessagesController.getInstance().dialogs_read_outbox_max.put(Long.valueOf(j), value);
                                    }
                                    int sentCount = 0;
                                    for (a = 0; a < updates.updates.size(); a++) {
                                        update = (Update) updates.updates.get(a);
                                        if ((update instanceof TL_updateNewMessage) || (update instanceof TL_updateNewChannelMessage)) {
                                            final Message message;
                                            if (update instanceof TL_updateNewMessage) {
                                                message = ((TL_updateNewMessage) update).message;
                                                MessagesController.getInstance().processNewDifferenceParams(-1, update.pts, -1, update.pts_count);
                                            } else {
                                                message = ((TL_updateNewChannelMessage) update).message;
                                                MessagesController.getInstance().processNewChannelDifferenceParams(update.pts, update.pts_count, message.to_id.channel_id);
                                                if (isMegagroupFinal) {
                                                    message.flags |= Integer.MIN_VALUE;
                                                }
                                            }
                                            message.unread = value.intValue() < message.id;
                                            if (z2) {
                                                message.out = true;
                                                message.unread = false;
                                            }
                                            Long random_id = (Long) newMessagesByIds.get(Integer.valueOf(message.id));
                                            if (random_id != null) {
                                                newMsgObj = (Message) messagesByRandomIdsFinal.get(random_id);
                                                if (newMsgObj != null) {
                                                    int index = newMsgObjArr.indexOf(newMsgObj);
                                                    if (index != -1) {
                                                        MessageObject msgObj = (MessageObject) newMsgArr.get(index);
                                                        newMsgObjArr.remove(index);
                                                        newMsgArr.remove(index);
                                                        final int oldId = newMsgObj.id;
                                                        final ArrayList<Message> sentMessages = new ArrayList();
                                                        sentMessages.add(message);
                                                        newMsgObj.id = message.id;
                                                        sentCount++;
                                                        SendMessagesHelper.this.updateMediaPaths(msgObj, message, null, true);
                                                        final Message finalNewMsgObj = newMsgObj;
                                                        final Message finalNewMsgObj1 = newMsgObj;
                                                        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {

                                                            class C12011 implements Runnable {
                                                                C12011() {
                                                                }

                                                                public void run() {
                                                                    finalNewMsgObj.send_state = 0;
                                                                    SearchQuery.increasePeerRaiting(j);
                                                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(oldId), Integer.valueOf(message.id), message, Long.valueOf(j));
                                                                    SendMessagesHelper.this.processSentMessage(oldId);
                                                                    SendMessagesHelper.this.removeFromSendingMessages(oldId);
                                                                }
                                                            }

                                                            public void run() {
                                                                MessagesStorage.getInstance().updateMessageStateAndId(finalNewMsgObj1.random_id, Integer.valueOf(oldId), finalNewMsgObj1.id, 0, false, to_id.channel_id);
                                                                MessagesStorage.getInstance().putMessages(sentMessages, true, false, false, 0);
                                                                AndroidUtilities.runOnUIThread(new C12011());
                                                                if (MessageObject.isVideoMessage(finalNewMsgObj1) || MessageObject.isNewGifMessage(finalNewMsgObj1)) {
                                                                    SendMessagesHelper.this.stopVideoService(finalNewMsgObj1.attachPath);
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    StatsController.getInstance().incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 1, sentCount);
                                } else {
                                    final TL_error tL_error = error;
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            AlertsCreator.processError(tL_error, null, req, new Object[0]);
                                        }
                                    });
                                }
                                for (a = 0; a < newMsgObjArr.size(); a++) {
                                    newMsgObj = (Message) newMsgObjArr.get(a);
                                    MessagesStorage.getInstance().markMessageAsSendError(newMsgObj);
                                    final Message finalNewMsgObj2 = newMsgObj;
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            finalNewMsgObj2.send_state = 2;
                                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(finalNewMsgObj2.id));
                                            SendMessagesHelper.this.processSentMessage(finalNewMsgObj2.id);
                                            if (MessageObject.isVideoMessage(finalNewMsgObj2) || MessageObject.isNewGifMessage(finalNewMsgObj2)) {
                                                SendMessagesHelper.this.stopVideoService(finalNewMsgObj2.attachPath);
                                            }
                                            SendMessagesHelper.this.removeFromSendingMessages(finalNewMsgObj2.id);
                                        }
                                    });
                                }
                            }
                        }, 68);
                        if (a != messages.size() - 1) {
                            objArr = new ArrayList();
                            arr = new ArrayList();
                            randomIds = new ArrayList();
                            ids = new ArrayList();
                            messagesByRandomIds = new HashMap();
                        }
                    }
                    a++;
                }
                return;
            }
            for (a = 0; a < messages.size(); a++) {
                processForwardFromMyName((MessageObject) messages.get(a), peer);
            }
        }
    }

    public void processForwardFromMyName2(MessageObject messageObject, long did) {
        if (messageObject != null) {
            ArrayList<MessageObject> arrayList;
            if (messageObject.messageOwner.media == null || (messageObject.messageOwner.media instanceof TL_messageMediaEmpty) || (messageObject.messageOwner.media instanceof TL_messageMediaWebPage) || (messageObject.messageOwner.media instanceof TL_messageMediaGame) || (messageObject.messageOwner.media instanceof TL_messageMediaInvoice)) {
                if (messageObject.messageOwner.message != null) {
                    ArrayList entities;
                    WebPage webPage = null;
                    if (messageObject.messageOwner.media instanceof TL_messageMediaWebPage) {
                        webPage = messageObject.messageOwner.media.webpage;
                    }
                    if (messageObject.messageOwner.entities == null || messageObject.messageOwner.entities.isEmpty()) {
                        entities = null;
                    } else {
                        entities = new ArrayList();
                        for (int a = 0; a < messageObject.messageOwner.entities.size(); a++) {
                            MessageEntity entity = (MessageEntity) messageObject.messageOwner.entities.get(a);
                            if ((entity instanceof TL_messageEntityBold) || (entity instanceof TL_messageEntityItalic) || (entity instanceof TL_messageEntityPre) || (entity instanceof TL_messageEntityCode) || (entity instanceof TL_messageEntityTextUrl)) {
                                entities.add(entity);
                            }
                        }
                    }
                    sendMessage(messageObject.messageOwner.message, did, messageObject.replyMessageObject, webPage, true, entities, null, null);
                } else if (((int) did) != 0) {
                    arrayList = new ArrayList();
                    arrayList.add(messageObject);
                    sendMessage(arrayList, did);
                }
            } else if (messageObject.messageOwner.media.photo instanceof TL_photo) {
                sendMessage((TL_photo) messageObject.messageOwner.media.photo, null, did, messageObject.replyMessageObject, null, null, messageObject.messageOwner.media.ttl_seconds);
            } else if (messageObject.messageOwner.media.document instanceof TL_document) {
                sendMessage((TL_document) messageObject.messageOwner.media.document, null, messageObject.messageOwner.attachPath, did, messageObject.replyMessageObject, null, null, messageObject.messageOwner.media.ttl_seconds);
            } else if ((messageObject.messageOwner.media instanceof TL_messageMediaVenue) || (messageObject.messageOwner.media instanceof TL_messageMediaGeo)) {
                sendMessage(messageObject.messageOwner.media, did, messageObject.replyMessageObject, null, null);
            } else if (messageObject.messageOwner.media.phone_number != null) {
                User user = new TL_userContact_old2();
                user.phone = messageObject.messageOwner.media.phone_number;
                user.first_name = messageObject.messageOwner.media.first_name;
                user.last_name = messageObject.messageOwner.media.last_name;
                user.id = messageObject.messageOwner.media.user_id;
                sendMessage(user, did, messageObject.replyMessageObject, null, null);
            } else if (((int) did) != 0) {
                arrayList = new ArrayList();
                arrayList.add(messageObject);
                sendMessage(arrayList, did);
            }
        }
    }

    public int editMessage(MessageObject messageObject, String message, boolean searchLinks, final BaseFragment fragment, ArrayList<MessageEntity> entities, final Runnable callback) {
        boolean z = false;
        if (fragment == null || fragment.getParentActivity() == null || callback == null) {
            return 0;
        }
        final TL_messages_editMessage req = new TL_messages_editMessage();
        req.peer = MessagesController.getInputPeer((int) messageObject.getDialogId());
        req.message = message;
        req.flags |= 2048;
        req.id = messageObject.getId();
        if (!searchLinks) {
            z = true;
        }
        req.no_webpage = z;
        if (entities != null) {
            req.entities = entities;
            req.flags |= 8;
        }
        return ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {

            class C12061 implements Runnable {
                C12061() {
                }

                public void run() {
                    callback.run();
                }
            }

            public void run(TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new C12061());
                if (error == null) {
                    MessagesController.getInstance().processUpdates((Updates) response, false);
                } else {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            AlertsCreator.processError(error, fragment, req, new Object[0]);
                        }
                    });
                }
            }
        });
    }

    private void sendLocation(Location location) {
        MessageMedia mediaGeo = new TL_messageMediaGeo();
        mediaGeo.geo = new TL_geoPoint();
        mediaGeo.geo.lat = location.getLatitude();
        mediaGeo.geo._long = location.getLongitude();
        for (Entry<String, MessageObject> entry : this.waitingForLocation.entrySet()) {
            MessageObject messageObject = (MessageObject) entry.getValue();
            getInstance().sendMessage(mediaGeo, messageObject.getDialogId(), messageObject, null, null);
        }
    }

    public void sendCurrentLocation(MessageObject messageObject, KeyboardButton button) {
        if (messageObject != null && button != null) {
            this.waitingForLocation.put(messageObject.getDialogId() + "_" + messageObject.getId() + "_" + Utilities.bytesToHex(button.data) + "_" + (button instanceof TL_keyboardButtonGame ? "1" : "0"), messageObject);
            this.locationProvider.start();
        }
    }

    public boolean isSendingCurrentLocation(MessageObject messageObject, KeyboardButton button) {
        if (messageObject == null || button == null) {
            return false;
        }
        return this.waitingForLocation.containsKey(messageObject.getDialogId() + "_" + messageObject.getId() + "_" + Utilities.bytesToHex(button.data) + "_" + (button instanceof TL_keyboardButtonGame ? "1" : "0"));
    }

    public void sendCallback(boolean cache, MessageObject messageObject, KeyboardButton button, ChatActivity parentFragment) {
        if (messageObject != null && button != null && parentFragment != null) {
            final boolean cacheFinal;
            int type;
            if (button instanceof TL_keyboardButtonGame) {
                cacheFinal = false;
                type = 1;
            } else {
                cacheFinal = cache;
                if (button instanceof TL_keyboardButtonBuy) {
                    type = 2;
                } else {
                    type = 0;
                }
            }
            final String key = messageObject.getDialogId() + "_" + messageObject.getId() + "_" + Utilities.bytesToHex(button.data) + "_" + type;
            this.waitingForCallback.put(key, messageObject);
            final MessageObject messageObject2 = messageObject;
            final KeyboardButton keyboardButton = button;
            final ChatActivity chatActivity = parentFragment;
            RequestDelegate requestDelegate = new RequestDelegate() {
                public void run(final TLObject response, TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            SendMessagesHelper.this.waitingForCallback.remove(key);
                            if (cacheFinal && response == null) {
                                SendMessagesHelper.this.sendCallback(false, messageObject2, keyboardButton, chatActivity);
                            } else if (response == null) {
                            } else {
                                if (!(keyboardButton instanceof TL_keyboardButtonBuy)) {
                                    TL_messages_botCallbackAnswer res = (TL_messages_botCallbackAnswer) response;
                                    if (!(cacheFinal || res.cache_time == 0)) {
                                        MessagesStorage.getInstance().saveBotCache(key, res);
                                    }
                                    int uid;
                                    User user;
                                    if (res.message != null) {
                                        if (!res.alert) {
                                            uid = messageObject2.messageOwner.from_id;
                                            if (messageObject2.messageOwner.via_bot_id != 0) {
                                                uid = messageObject2.messageOwner.via_bot_id;
                                            }
                                            String name = null;
                                            if (uid > 0) {
                                                user = MessagesController.getInstance().getUser(Integer.valueOf(uid));
                                                if (user != null) {
                                                    name = ContactsController.formatName(user.first_name, user.last_name);
                                                }
                                            } else {
                                                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-uid));
                                                if (chat != null) {
                                                    name = chat.title;
                                                }
                                            }
                                            if (name == null) {
                                                name = "bot";
                                            }
                                            chatActivity.showAlert(name, res.message);
                                        } else if (chatActivity.getParentActivity() != null) {
                                            Builder builder = new Builder(chatActivity.getParentActivity());
                                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                                            builder.setMessage(res.message);
                                            chatActivity.showDialog(builder.create());
                                        }
                                    } else if (res.url != null && chatActivity.getParentActivity() != null) {
                                        uid = messageObject2.messageOwner.from_id;
                                        if (messageObject2.messageOwner.via_bot_id != 0) {
                                            uid = messageObject2.messageOwner.via_bot_id;
                                        }
                                        user = MessagesController.getInstance().getUser(Integer.valueOf(uid));
                                        boolean verified = user != null && user.verified;
                                        if (keyboardButton instanceof TL_keyboardButtonGame) {
                                            TL_game game = messageObject2.messageOwner.media instanceof TL_messageMediaGame ? messageObject2.messageOwner.media.game : null;
                                            if (game != null) {
                                                boolean z;
                                                MessageObject messageObject = messageObject2;
                                                String str = res.url;
                                                if (verified || !ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("askgame_" + uid, true)) {
                                                    z = false;
                                                } else {
                                                    z = true;
                                                }
                                                chatActivity.showOpenGameAlert(game, messageObject, str, z, uid);
                                                return;
                                            }
                                            return;
                                        }
                                        chatActivity.showOpenUrlAlert(res.url, false);
                                    }
                                } else if (response instanceof TL_payments_paymentForm) {
                                    TL_payments_paymentForm form = (TL_payments_paymentForm) response;
                                    MessagesController.getInstance().putUsers(form.users, false);
                                    chatActivity.presentFragment(new PaymentFormActivity(form, messageObject2));
                                } else if (response instanceof TL_payments_paymentReceipt) {
                                    chatActivity.presentFragment(new PaymentFormActivity(messageObject2, (TL_payments_paymentReceipt) response));
                                }
                            }
                        }
                    });
                }
            };
            if (cacheFinal) {
                MessagesStorage.getInstance().getBotCache(key, requestDelegate);
            } else if (!(button instanceof TL_keyboardButtonBuy)) {
                TL_messages_getBotCallbackAnswer req = new TL_messages_getBotCallbackAnswer();
                req.peer = MessagesController.getInputPeer((int) messageObject.getDialogId());
                req.msg_id = messageObject.getId();
                req.game = button instanceof TL_keyboardButtonGame;
                if (button.data != null) {
                    req.flags |= 1;
                    req.data = button.data;
                }
                ConnectionsManager.getInstance().sendRequest(req, requestDelegate, 2);
            } else if ((messageObject.messageOwner.media.flags & 4) == 0) {
                TL_payments_getPaymentForm req2 = new TL_payments_getPaymentForm();
                req2.msg_id = messageObject.getId();
                ConnectionsManager.getInstance().sendRequest(req2, requestDelegate, 2);
            } else {
                TL_payments_getPaymentReceipt req3 = new TL_payments_getPaymentReceipt();
                req3.msg_id = messageObject.messageOwner.media.receipt_msg_id;
                ConnectionsManager.getInstance().sendRequest(req3, requestDelegate, 2);
            }
        }
    }

    public boolean isSendingCallback(MessageObject messageObject, KeyboardButton button) {
        if (messageObject == null || button == null) {
            return false;
        }
        int type;
        if (button instanceof TL_keyboardButtonGame) {
            type = 1;
        } else if (button instanceof TL_keyboardButtonBuy) {
            type = 2;
        } else {
            type = 0;
        }
        return this.waitingForCallback.containsKey(messageObject.getDialogId() + "_" + messageObject.getId() + "_" + Utilities.bytesToHex(button.data) + "_" + type);
    }

    public void sendGame(TLRPC.InputPeer peer, TLRPC.TL_inputMediaGame game, long random_id, final long taskId) {
        if (peer == null || game == null) {
            return;
        }
        TLRPC.TL_messages_sendMedia request = new TLRPC.TL_messages_sendMedia();
        request.peer = peer;
        if (request.peer instanceof TLRPC.TL_inputPeerChannel) {
            request.silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", Activity.MODE_PRIVATE).getBoolean("silent_" + peer.channel_id, false);
        }
        request.random_id = random_id != 0 ? random_id : getNextRandomId();
        request.media = game;
        final long newTaskId;
        if (taskId == 0) {
            NativeByteBuffer data = null;
            try {
                data = new NativeByteBuffer(peer.getObjectSize() + game.getObjectSize() + 4 + 8);
                data.writeInt32(3);
                data.writeInt64(random_id);
                peer.serializeToStream(data);
                game.serializeToStream(data);
            } catch (Exception e) {
                FileLog.e(e);
            }
            newTaskId = MessagesStorage.getInstance().createPendingTask(data);
        } else {
            newTaskId = taskId;
        }
        ConnectionsManager.getInstance().sendRequest(request, new RequestDelegate() {
            @Override
            public void run(final TLObject response, final TLRPC.TL_error error) {
                if (error == null) {
                    MessagesController.getInstance().processUpdates((TLRPC.Updates) response, false);
                }
                if (newTaskId != 0) {
                    MessagesStorage.getInstance().removePendingTask(newTaskId);
                }
            }
        });
    }

    public void sendMessage(MessageObject retryMessageObject) {
        sendMessage(null, null, null, null, null, null, null, retryMessageObject.getDialogId(), retryMessageObject.messageOwner.attachPath, null, null, true, retryMessageObject, null, retryMessageObject.messageOwner.reply_markup, retryMessageObject.messageOwner.params, 0);
    }

    public void sendMessage(User user, long peer, MessageObject reply_to_msg, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(null, null, null, null, user, null, null, peer, null, reply_to_msg, null, true, null, null, replyMarkup, params, 0);
    }

    public void sendMessage(TL_document document, VideoEditedInfo videoEditedInfo, String path, long peer, MessageObject reply_to_msg, ReplyMarkup replyMarkup, HashMap<String, String> params, int ttl) {
        sendMessage(null, null, null, videoEditedInfo, null, document, null, peer, path, reply_to_msg, null, true, null, null, replyMarkup, params, ttl);
    }

    public void sendMessage(String message, long peer, MessageObject reply_to_msg, WebPage webPage, boolean searchLinks, ArrayList<MessageEntity> entities, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(message, null, null, null, null, null, null, peer, null, reply_to_msg, webPage, searchLinks, null, entities, replyMarkup, params, 0);
    }

    public void sendMessage(MessageMedia location, long peer, MessageObject reply_to_msg, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(null, location, null, null, null, null, null, peer, null, reply_to_msg, null, true, null, null, replyMarkup, params, 0);
    }

    public void sendMessage(TL_game game, long peer, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(null, null, null, null, null, null, game, peer, null, null, null, true, null, null, replyMarkup, params, 0);
    }

    public void sendMessage(TL_photo photo, String path, long peer, MessageObject reply_to_msg, ReplyMarkup replyMarkup, HashMap<String, String> params, int ttl) {
        sendMessage(null, null, photo, null, null, null, null, peer, path, reply_to_msg, null, true, null, null, replyMarkup, params, ttl);
    }



    private void sendMessage(String message, TLRPC.MessageMedia location, TLRPC.TL_photo photo, VideoEditedInfo videoEditedInfo, TLRPC.User user, TLRPC.TL_document document, TLRPC.TL_game game, long peer, String path, MessageObject reply_to_msg, TLRPC.WebPage webPage, boolean searchLinks, MessageObject retryMessageObject, ArrayList<TLRPC.MessageEntity> entities, TLRPC.ReplyMarkup replyMarkup, HashMap<String, String> params, int ttl) {
        if (peer == 0) {
            return;
        }

        String originalPath = null;
        if (params != null && params.containsKey("originalPath")) {
            originalPath = params.get("originalPath");
        }

        TLRPC.Message newMsg = null;
        MessageObject newMsgObj = null;
        int type = -1;
        int lower_id = (int) peer;
        int high_id = (int) (peer >> 32);
        boolean isChannel = false;
        TLRPC.EncryptedChat encryptedChat = null;
        TLRPC.InputPeer sendToPeer = lower_id != 0 ? MessagesController.getInputPeer(lower_id) : null;
        ArrayList<TLRPC.InputUser> sendToPeers = null;
        if (lower_id == 0) {
            encryptedChat = MessagesController.getInstance().getEncryptedChat(high_id);
            if (encryptedChat == null) {
                if (retryMessageObject != null) {
                    MessagesStorage.getInstance().markMessageAsSendError(retryMessageObject.messageOwner);
                    retryMessageObject.messageOwner.send_state = MessageObject.MESSAGE_SEND_STATE_SEND_ERROR;
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, retryMessageObject.getId());
                    processSentMessage(retryMessageObject.getId());
                }
                return;
            }
        } else if (sendToPeer instanceof TLRPC.TL_inputPeerChannel) {
            TLRPC.Chat chat = MessagesController.getInstance().getChat(sendToPeer.channel_id);
            isChannel = chat != null && !chat.megagroup;
        }

        try {
            if (retryMessageObject != null) {
                newMsg = retryMessageObject.messageOwner;
                if (retryMessageObject.isForwarded()) {
                    type = 4;
                } else {
                    if (retryMessageObject.type == 0) {
                        if (retryMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame) {
                            //game = retryMessageObject.messageOwner.media.game;
                        } else {
                            message = newMsg.message;
                        }
                        type = 0;
                    } else if (retryMessageObject.type == 4) {
                        location = newMsg.media;
                        type = 1;
                    } else if (retryMessageObject.type == 1) {
                        photo = (TLRPC.TL_photo) newMsg.media.photo;
                        type = 2;
                    } else if (retryMessageObject.type == 3 || retryMessageObject.type == 5 || videoEditedInfo != null) {
                        type = 3;
                        document = (TLRPC.TL_document) newMsg.media.document;
                    } else if (retryMessageObject.type == 12) {
                        user = new TLRPC.TL_userRequest_old2();
                        user.phone = newMsg.media.phone_number;
                        user.first_name = newMsg.media.first_name;
                        user.last_name = newMsg.media.last_name;
                        user.id = newMsg.media.user_id;
                        type = 6;
                    } else if (retryMessageObject.type == 8 || retryMessageObject.type == 9 || retryMessageObject.type == 13 || retryMessageObject.type == 14) {
                        document = (TLRPC.TL_document) newMsg.media.document;
                        type = 7;
                    } else if (retryMessageObject.type == 2) {
                        document = (TLRPC.TL_document) newMsg.media.document;
                        type = 8;
                    }
                    if (params != null && params.containsKey("query_id")) {
                        type = 9;
                    }
                }
            } else {
                if (message != null) {
                    if (encryptedChat != null) {
                        newMsg = new TLRPC.TL_message_secret();
                    } else {
                        newMsg = new TLRPC.TL_message();
                    }
                    if (entities != null && !entities.isEmpty()) {
                        newMsg.entities = entities;
                    }
                    if (encryptedChat != null && webPage instanceof TLRPC.TL_webPagePending) {
                        if (webPage.url != null) {
                            TLRPC.WebPage newWebPage = new TLRPC.TL_webPageUrlPending();
                            newWebPage.url = webPage.url;
                            webPage = newWebPage;
                        } else {
                            webPage = null;
                        }
                    }
                    if (webPage == null) {
                        newMsg.media = new TLRPC.TL_messageMediaEmpty();
                    } else {
                        newMsg.media = new TLRPC.TL_messageMediaWebPage();
                        newMsg.media.webpage = webPage;
                    }
                    if (params != null && params.containsKey("query_id")) {
                        type = 9;
                    } else {
                        type = 0;
                    }
                    newMsg.message = message;
                } else if (location != null) {
                    if (encryptedChat != null) {
                        newMsg = new TLRPC.TL_message_secret();
                    } else {
                        newMsg = new TLRPC.TL_message();
                    }
                    newMsg.media = location;
                    newMsg.message = "";
                    if (params != null && params.containsKey("query_id")) {
                        type = 9;
                    } else {
                        type = 1;
                    }
                } else if (photo != null) {
                    if (encryptedChat != null) {
                        newMsg = new TLRPC.TL_message_secret();
                    } else {
                        newMsg = new TLRPC.TL_message();
                    }
                    newMsg.media = new TLRPC.TL_messageMediaPhoto();
                    newMsg.media.flags |= 3;
                    newMsg.media.caption = photo.caption != null ? photo.caption : "";
                    if (ttl != 0) {
                        newMsg.ttl = newMsg.media.ttl_seconds = ttl;
                        newMsg.media.flags |= 4;
                    }
                    newMsg.media.photo = photo;
                    if (params != null && params.containsKey("query_id")) {
                        type = 9;
                    } else {
                        type = 2;
                    }
                    newMsg.message = "-1";
                    if (path != null && path.length() > 0 && path.startsWith("http")) {
                        newMsg.attachPath = path;
                    } else {
                        TLRPC.FileLocation location1 = photo.sizes.get(photo.sizes.size() - 1).location;
                        newMsg.attachPath = FileLoader.getPathToAttach(location1, true).toString();
                    }
                } else if (game != null) {
                    newMsg = new TLRPC.TL_message();
                    newMsg.media = new TLRPC.TL_messageMediaGame();
                    newMsg.media.caption = "";
                    newMsg.media.game = game;
                    newMsg.message = "";
                    if (params != null && params.containsKey("query_id")) {
                        type = 9;
                    }
                } else if (user != null) {
                    if (encryptedChat != null) {
                        newMsg = new TLRPC.TL_message_secret();
                    } else {
                        newMsg = new TLRPC.TL_message();
                    }
                    newMsg.media = new TLRPC.TL_messageMediaContact();
                    newMsg.media.phone_number = user.phone;
                    newMsg.media.first_name = user.first_name;
                    newMsg.media.last_name = user.last_name;
                    newMsg.media.user_id = user.id;
                    if (newMsg.media.first_name == null) {
                        user.first_name = newMsg.media.first_name = "";
                    }
                    if (newMsg.media.last_name == null) {
                        user.last_name = newMsg.media.last_name = "";
                    }
                    newMsg.message = "";
                    if (params != null && params.containsKey("query_id")) {
                        type = 9;
                    } else {
                        type = 6;
                    }
                } else if (document != null) {
                    if (encryptedChat != null) {
                        newMsg = new TLRPC.TL_message_secret();
                    } else {
                        newMsg = new TLRPC.TL_message();
                    }
                    newMsg.media = new TLRPC.TL_messageMediaDocument();
                    newMsg.media.flags |= 3;
                    if (ttl != 0) {
                        newMsg.ttl = newMsg.media.ttl_seconds = ttl;
                        newMsg.media.flags |= 4;
                    }
                    newMsg.media.caption = document.caption != null ? document.caption : "";
                    newMsg.media.document = document;
                    if (params != null && params.containsKey("query_id")) {
                        type = 9;
                    } else if (MessageObject.isVideoDocument(document) || MessageObject.isRoundVideoDocument(document) || videoEditedInfo != null) {
                        type = 3;
                    } else if (MessageObject.isVoiceDocument(document)) {
                        type = 8;
                    } else {
                        type = 7;
                    }
                    if (videoEditedInfo == null) {
                        newMsg.message = "-1";
                    } else {
                        newMsg.message = videoEditedInfo.getString();
                    }
                    if (encryptedChat != null && document.dc_id > 0 && !MessageObject.isStickerDocument(document)) {
                        newMsg.attachPath = FileLoader.getPathToAttach(document).toString();
                    } else {
                        newMsg.attachPath = path;
                    }
                    if (encryptedChat != null && MessageObject.isStickerDocument(document)) {
                        for (int a = 0; a < document.attributes.size(); a++) {
                            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                            if (attribute instanceof TLRPC.TL_documentAttributeSticker) {
                                document.attributes.remove(a);
                                TLRPC.TL_documentAttributeSticker_layer55 attributeSticker = new TLRPC.TL_documentAttributeSticker_layer55();
                                document.attributes.add(attributeSticker);
                                attributeSticker.alt = attribute.alt;
                                if (attribute.stickerset != null) {
                                    String name;
                                    if (attribute.stickerset instanceof TLRPC.TL_inputStickerSetShortName) {
                                        name = attribute.stickerset.short_name;
                                    } else {
                                        name = StickersQuery.getStickerSetName(attribute.stickerset.id);
                                    }
                                    if (!TextUtils.isEmpty(name)) {
                                        attributeSticker.stickerset = new TLRPC.TL_inputStickerSetShortName();
                                        attributeSticker.stickerset.short_name = name;
                                    } else {
                                        attributeSticker.stickerset = new TLRPC.TL_inputStickerSetEmpty();
                                    }
                                } else {
                                    attributeSticker.stickerset = new TLRPC.TL_inputStickerSetEmpty();
                                }
                                break;
                            }
                        }
                    }
                }
                if (newMsg.attachPath == null) {
                    newMsg.attachPath = "";
                }
                newMsg.local_id = newMsg.id = UserConfig.getNewMessageId();
                newMsg.out = true;
                if (isChannel && sendToPeer != null) {
                    newMsg.from_id = -sendToPeer.channel_id;
                } else {
                    newMsg.from_id = UserConfig.getClientUserId();
                    newMsg.flags |= TLRPC.MESSAGE_FLAG_HAS_FROM_ID;
                }
                UserConfig.saveConfig(false);
            }
            if (newMsg.random_id == 0) {
                newMsg.random_id = getNextRandomId();
            }
            if (params != null && params.containsKey("bot")) {
                if (encryptedChat != null) {
                    newMsg.via_bot_name = params.get("bot_name");
                    if (newMsg.via_bot_name == null) {
                        newMsg.via_bot_name = "";
                    }
                } else {
                    newMsg.via_bot_id = Utilities.parseInt(params.get("bot"));
                }
                newMsg.flags |= TLRPC.MESSAGE_FLAG_HAS_BOT_ID;
            }
            newMsg.params = params;
            if (retryMessageObject == null || !retryMessageObject.resendAsIs) {
                newMsg.date = ConnectionsManager.getInstance().getCurrentTime();
                if (sendToPeer instanceof TLRPC.TL_inputPeerChannel) {
                    if (isChannel) {
                        newMsg.views = 1;
                        newMsg.flags |= TLRPC.MESSAGE_FLAG_HAS_VIEWS;
                    }
                    TLRPC.Chat chat = MessagesController.getInstance().getChat(sendToPeer.channel_id);
                    if (chat != null) {
                        if (chat.megagroup) {
                            newMsg.flags |= TLRPC.MESSAGE_FLAG_MEGAGROUP;
                            newMsg.unread = true;
                        } else {
                            newMsg.post = true;
                            if (chat.signatures) {
                                newMsg.from_id = UserConfig.getClientUserId();
                            }
                        }
                    }
                } else {
                    newMsg.unread = true;
                }
            }
            newMsg.flags |= TLRPC.MESSAGE_FLAG_HAS_MEDIA;
            newMsg.dialog_id = peer;
            if (reply_to_msg != null) {
                if (encryptedChat != null && reply_to_msg.messageOwner.random_id != 0) {
                    newMsg.reply_to_random_id = reply_to_msg.messageOwner.random_id;
                    newMsg.flags |= TLRPC.MESSAGE_FLAG_REPLY;
                } else {
                    newMsg.flags |= TLRPC.MESSAGE_FLAG_REPLY;
                }
                newMsg.reply_to_msg_id = reply_to_msg.getId();
            }
            if (replyMarkup != null && encryptedChat == null) {
                newMsg.flags |= TLRPC.MESSAGE_FLAG_HAS_MARKUP;
                newMsg.reply_markup = replyMarkup;
            }
            if (lower_id != 0) {
                if (high_id == 1) {
                    if (currentChatInfo == null) {
                        MessagesStorage.getInstance().markMessageAsSendError(newMsg);
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, newMsg.id);
                        processSentMessage(newMsg.id);
                        return;
                    }
                    sendToPeers = new ArrayList<>();
                    for (TLRPC.ChatParticipant participant : currentChatInfo.participants.participants) {
                        TLRPC.User sendToUser = MessagesController.getInstance().getUser(participant.user_id);
                        TLRPC.InputUser peerUser = MessagesController.getInputUser(sendToUser);
                        if (peerUser != null) {
                            sendToPeers.add(peerUser);
                        }
                    }
                    newMsg.to_id = new TLRPC.TL_peerChat();
                    newMsg.to_id.chat_id = lower_id;
                } else {
                    newMsg.to_id = MessagesController.getPeer(lower_id);
                    if (lower_id > 0) {
                        TLRPC.User sendToUser = MessagesController.getInstance().getUser(lower_id);
                        if (sendToUser == null) {
                            processSentMessage(newMsg.id);
                            return;
                        }
                        if (sendToUser.bot) {
                            newMsg.unread = false;
                        }
                    }
                }
            } else {
                newMsg.to_id = new TLRPC.TL_peerUser();
                if (encryptedChat.participant_id == UserConfig.getClientUserId()) {
                    newMsg.to_id.user_id = encryptedChat.admin_id;
                } else {
                    newMsg.to_id.user_id = encryptedChat.participant_id;
                }
                if (ttl != 0) {
                    newMsg.ttl = ttl;
                } else {
                    newMsg.ttl = encryptedChat.ttl;
                }
                if (newMsg.ttl != 0 && newMsg.media.document != null) {
                    if (MessageObject.isVoiceMessage(newMsg)) {
                        int duration = 0;
                        for (int a = 0; a < newMsg.media.document.attributes.size(); a++) {
                            TLRPC.DocumentAttribute attribute = newMsg.media.document.attributes.get(a);
                            if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                                duration = attribute.duration;
                                break;
                            }
                        }
                        newMsg.ttl = Math.max(newMsg.ttl, duration + 1);
                    } else if (MessageObject.isVideoMessage(newMsg) || MessageObject.isRoundVideoMessage(newMsg)) {
                        int duration = 0;
                        for (int a = 0; a < newMsg.media.document.attributes.size(); a++) {
                            TLRPC.DocumentAttribute attribute = newMsg.media.document.attributes.get(a);
                            if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                                duration = attribute.duration;
                                break;
                            }
                        }
                        newMsg.ttl = Math.max(newMsg.ttl, duration + 1);
                    }
                }
            }
            if (high_id != 1 && (MessageObject.isVoiceMessage(newMsg) || MessageObject.isRoundVideoMessage(newMsg)) && newMsg.to_id.channel_id == 0) {
                newMsg.media_unread = true;
            }

            newMsg.send_state = MessageObject.MESSAGE_SEND_STATE_SENDING;
            newMsgObj = new MessageObject(newMsg, null, true);
            newMsgObj.replyMessageObject = reply_to_msg;
            if (!newMsgObj.isForwarded() && (newMsgObj.type == 3 || videoEditedInfo != null || newMsgObj.type == 2) && !TextUtils.isEmpty(newMsg.attachPath)) {
                newMsgObj.attachPathExists = true;
            }

            ArrayList<MessageObject> objArr = new ArrayList<>();
            objArr.add(newMsgObj);
            ArrayList<TLRPC.Message> arr = new ArrayList<>();
            arr.add(newMsg);
            MessagesStorage.getInstance().putMessages(arr, false, true, false, 0);
            MessagesController.getInstance().updateInterfaceWithMessages(peer, objArr);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload);

            if (BuildVars.DEBUG_VERSION) {
                if (sendToPeer != null) {
                    FileLog.e("send message user_id = " + sendToPeer.user_id + " chat_id = " + sendToPeer.chat_id + " channel_id = " + sendToPeer.channel_id + " access_hash = " + sendToPeer.access_hash);
                }
            }

            if (type == 0 || type == 9 && message != null && encryptedChat != null) {
                if (encryptedChat == null) {
                    if (sendToPeers != null) {
                        TLRPC.TL_messages_sendBroadcast reqSend = new TLRPC.TL_messages_sendBroadcast();
                        ArrayList<Long> random_ids = new ArrayList<>();
                        for (int a = 0; a < sendToPeers.size(); a++) {
                            random_ids.add(Utilities.random.nextLong());
                        }
                        reqSend.message = message;
                        reqSend.contacts = sendToPeers;
                        reqSend.media = new TLRPC.TL_inputMediaEmpty();
                        reqSend.random_id = random_ids;
                        performSendMessageRequest(reqSend, newMsgObj, null);
                    } else {
                        TLRPC.TL_messages_sendMessage reqSend = new TLRPC.TL_messages_sendMessage();
                        reqSend.message = message;
                        reqSend.clear_draft = retryMessageObject == null;
                        if (newMsg.to_id instanceof TLRPC.TL_peerChannel) {
                            reqSend.silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", Activity.MODE_PRIVATE).getBoolean("silent_" + peer, false);
                        }
                        reqSend.peer = sendToPeer;
                        reqSend.random_id = newMsg.random_id;
                        if (reply_to_msg != null) {
                            reqSend.flags |= 1;
                            reqSend.reply_to_msg_id = reply_to_msg.getId();
                        }
                        if (!searchLinks) {
                            reqSend.no_webpage = true;
                        }
                        if (entities != null && !entities.isEmpty()) {
                            reqSend.entities = entities;
                            reqSend.flags |= 8;
                        }
                        performSendMessageRequest(reqSend, newMsgObj, null);
                        if (retryMessageObject == null) {
                            DraftQuery.cleanDraft(peer, false);
                        }
                    }
                } else {
                    TLRPC.TL_decryptedMessage reqSend;
                    reqSend = new TLRPC.TL_decryptedMessage();
                    reqSend.ttl = newMsg.ttl;
                    if (entities != null && !entities.isEmpty()) {
                        reqSend.entities = entities;
                        reqSend.flags |= TLRPC.MESSAGE_FLAG_HAS_ENTITIES;
                    }
                    if (reply_to_msg != null && reply_to_msg.messageOwner.random_id != 0) {
                        reqSend.reply_to_random_id = reply_to_msg.messageOwner.random_id;
                        reqSend.flags |= TLRPC.MESSAGE_FLAG_REPLY;
                    }
                    if (params != null && params.get("bot_name") != null) {
                        reqSend.via_bot_name = params.get("bot_name");
                        reqSend.flags |= TLRPC.MESSAGE_FLAG_HAS_BOT_ID;
                    }
                    reqSend.random_id = newMsg.random_id;
                    reqSend.message = message;
                    if (webPage != null && webPage.url != null) {
                        reqSend.media = new TLRPC.TL_decryptedMessageMediaWebPage();
                        reqSend.media.url = webPage.url;
                        reqSend.flags |= TLRPC.MESSAGE_FLAG_HAS_MEDIA;
                    } else {
                        reqSend.media = new TLRPC.TL_decryptedMessageMediaEmpty();
                    }
                    SecretChatHelper.getInstance().performSendEncryptedRequest(reqSend, newMsgObj.messageOwner, encryptedChat, null, null, newMsgObj);
                    if (retryMessageObject == null) {
                        DraftQuery.cleanDraft(peer, false);
                    }
                }
            } else if (type >= 1 && type <= 3 || type >= 5 && type <= 8 || type == 9 && encryptedChat != null) {
                if (encryptedChat == null) {
                    TLRPC.InputMedia inputMedia = null;
                    DelayedMessage delayedMessage = null;
                    if (type == 1) {
                        if (location instanceof TLRPC.TL_messageMediaVenue) {
                            inputMedia = new TLRPC.TL_inputMediaVenue();
                            inputMedia.address = location.address;
                            inputMedia.title = location.title;
                            inputMedia.provider = location.provider;
                            inputMedia.venue_id = location.venue_id;
                        } else {
                            inputMedia = new TLRPC.TL_inputMediaGeoPoint();
                        }
                        inputMedia.geo_point = new TLRPC.TL_inputGeoPoint();
                        inputMedia.geo_point.lat = location.geo.lat;
                        inputMedia.geo_point._long = location.geo._long;
                    } else if (type == 2 || type == 9 && photo != null) {
                        if (photo.access_hash == 0) {
                            inputMedia = new TLRPC.TL_inputMediaUploadedPhoto();
                            inputMedia.caption = photo.caption != null ? photo.caption : "";
                            if (ttl != 0) {
                                newMsg.ttl = inputMedia.ttl_seconds = ttl;
                                inputMedia.flags |= 2;
                            }
                            if (params != null) {
                                String masks = params.get("masks");
                                if (masks != null) {
                                    SerializedData serializedData = new SerializedData(Utilities.hexToBytes(masks));
                                    int count = serializedData.readInt32(false);
                                    for (int a = 0; a < count; a++) {
                                        inputMedia.stickers.add(TLRPC.InputDocument.TLdeserialize(serializedData, serializedData.readInt32(false), false));
                                    }
                                    inputMedia.flags |= 1;
                                }
                            }
                            delayedMessage = new DelayedMessage();
                            delayedMessage.originalPath = originalPath;
                            delayedMessage.type = 0;
                            delayedMessage.obj = newMsgObj;
                            if (path != null && path.length() > 0 && path.startsWith("http")) {
                                delayedMessage.httpLocation = path;
                            } else {
                                delayedMessage.location = photo.sizes.get(photo.sizes.size() - 1).location;
                            }
                        } else {
                            TLRPC.TL_inputMediaPhoto media = new TLRPC.TL_inputMediaPhoto();
                            media.id = new TLRPC.TL_inputPhoto();
                            media.caption = photo.caption != null ? photo.caption : "";
                            media.id.id = photo.id;
                            media.id.access_hash = photo.access_hash;
                            inputMedia = media;
                        }
                    } else if (type == 3) {
                        if (document.access_hash == 0) {
                            inputMedia = new TLRPC.TL_inputMediaUploadedDocument();
                            inputMedia.caption = document.caption != null ? document.caption : "";
                            inputMedia.mime_type = document.mime_type;
                            inputMedia.attributes = document.attributes;
                            if (ttl != 0) {
                                newMsg.ttl = inputMedia.ttl_seconds = ttl;
                                inputMedia.flags |= 2;
                            }
                            delayedMessage = new DelayedMessage();
                            delayedMessage.originalPath = originalPath;
                            delayedMessage.type = 1;
                            delayedMessage.obj = newMsgObj;
                            delayedMessage.location = document.thumb.location;
                            delayedMessage.documentLocation = document;
                            delayedMessage.videoEditedInfo = videoEditedInfo;
                        } else {
                            TLRPC.TL_inputMediaDocument media = new TLRPC.TL_inputMediaDocument();
                            media.id = new TLRPC.TL_inputDocument();
                            media.caption = document.caption != null ? document.caption : "";
                            media.id.id = document.id;
                            media.id.access_hash = document.access_hash;
                            inputMedia = media;
                        }
                    } else if (type == 6) {
                        inputMedia = new TLRPC.TL_inputMediaContact();
                        inputMedia.phone_number = user.phone;
                        inputMedia.first_name = user.first_name;
                        inputMedia.last_name = user.last_name;
                    } else if (type == 7 || type == 9) {
                        if (document.access_hash == 0) {
                            if (encryptedChat == null && originalPath != null && originalPath.length() > 0 && originalPath.startsWith("http") && params != null) {
                                inputMedia = new TLRPC.TL_inputMediaGifExternal();
                                String args[] = params.get("url").split("\\|");
                                if (args.length == 2) {
                                    ((TLRPC.TL_inputMediaGifExternal) inputMedia).url = args[0];
                                    inputMedia.q = args[1];
                                }
                            } else {
                                inputMedia = new TLRPC.TL_inputMediaUploadedDocument();
                                if (ttl != 0) {
                                    newMsg.ttl = inputMedia.ttl_seconds = ttl;
                                    inputMedia.flags |= 2;
                                }
                                delayedMessage = new DelayedMessage();
                                delayedMessage.originalPath = originalPath;
                                delayedMessage.type = 2;
                                delayedMessage.obj = newMsgObj;
                                delayedMessage.documentLocation = document;
                                delayedMessage.location = document.thumb.location;
                            }
                            inputMedia.mime_type = document.mime_type;
                            inputMedia.attributes = document.attributes;
                            inputMedia.caption = document.caption != null ? document.caption : "";
                        } else {
                            TLRPC.TL_inputMediaDocument media = new TLRPC.TL_inputMediaDocument();
                            media.id = new TLRPC.TL_inputDocument();
                            media.id.id = document.id;
                            media.id.access_hash = document.access_hash;
                            media.caption = document.caption != null ? document.caption : "";
                            inputMedia = media;
                        }
                    } else if (type == 8) {
                        if (document.access_hash == 0) {
                            inputMedia = new TLRPC.TL_inputMediaUploadedDocument();
                            inputMedia.mime_type = document.mime_type;
                            inputMedia.attributes = document.attributes;
                            inputMedia.caption = document.caption != null ? document.caption : "";
                            if (ttl != 0) {
                                newMsg.ttl = inputMedia.ttl_seconds = ttl;
                                inputMedia.flags |= 2;
                            }
                            delayedMessage = new DelayedMessage();
                            delayedMessage.type = 3;
                            delayedMessage.obj = newMsgObj;
                            delayedMessage.documentLocation = document;
                        } else {
                            TLRPC.TL_inputMediaDocument media = new TLRPC.TL_inputMediaDocument();
                            media.id = new TLRPC.TL_inputDocument();
                            media.caption = document.caption != null ? document.caption : "";
                            media.id.id = document.id;
                            media.id.access_hash = document.access_hash;
                            inputMedia = media;
                        }
                    }
                    // random_ids = new ArrayList();

                    TLObject reqSend;

                    if (sendToPeers != null) {
                        TLRPC.TL_messages_sendBroadcast request = new TLRPC.TL_messages_sendBroadcast();
                        ArrayList<Long> random_ids = new ArrayList<>();
                        for (int a = 0; a < sendToPeers.size(); a++) {
                            random_ids.add(Utilities.random.nextLong());
                        }
                        request.contacts = sendToPeers;
                        request.media = inputMedia;
                        request.random_id = random_ids;
                        request.message = "";
                        if (delayedMessage != null) {
                            delayedMessage.sendRequest = request;
                        }
                        reqSend = request;
                        if (retryMessageObject == null) {
                            DraftQuery.cleanDraft(peer, false);
                        }
                    } else {
                        TLRPC.TL_messages_sendMedia request = new TLRPC.TL_messages_sendMedia();
                        request.peer = sendToPeer;
                        if (newMsg.to_id instanceof TLRPC.TL_peerChannel) {
                            request.silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", Activity.MODE_PRIVATE).getBoolean("silent_" + peer, false);
                        }
                        request.random_id = newMsg.random_id;
                        request.media = inputMedia;
                        if (reply_to_msg != null) {
                            request.flags |= 1;
                            request.reply_to_msg_id = reply_to_msg.getId();
                        }
                        if (delayedMessage != null) {
                            delayedMessage.sendRequest = request;
                        }
                        reqSend = request;
                    }
                    if (type == 1) {
                        performSendMessageRequest(reqSend, newMsgObj, null);
                    } else if (type == 2) {
                        if (photo.access_hash == 0) {
                            performSendDelayedMessage(delayedMessage);
                        } else {
                            performSendMessageRequest(reqSend, newMsgObj, null);
                        }
                    } else if (type == 3) {
                        if (document.access_hash == 0) {
                            performSendDelayedMessage(delayedMessage);
                        } else {
                            performSendMessageRequest(reqSend, newMsgObj, null);
                        }
                    } else if (type == 6) {
                        performSendMessageRequest(reqSend, newMsgObj, null);
                    } else if (type == 7) {
                        if (document.access_hash == 0 && delayedMessage != null) {
                            performSendDelayedMessage(delayedMessage);
                        } else {
                            performSendMessageRequest(reqSend, newMsgObj, originalPath);
                        }
                    } else if (type == 8) {
                        if (document.access_hash == 0) {
                            performSendDelayedMessage(delayedMessage);
                        } else {
                            performSendMessageRequest(reqSend, newMsgObj, null);
                        }
                    }
                } else {
                    TLRPC.TL_decryptedMessage reqSend;
                    reqSend = new TLRPC.TL_decryptedMessage();
                    reqSend.ttl = newMsg.ttl;
                    if (entities != null && !entities.isEmpty()) {
                        reqSend.entities = entities;
                        reqSend.flags |= TLRPC.MESSAGE_FLAG_HAS_ENTITIES;
                    }
                    if (reply_to_msg != null && reply_to_msg.messageOwner.random_id != 0) {
                        reqSend.reply_to_random_id = reply_to_msg.messageOwner.random_id;
                        reqSend.flags |= TLRPC.MESSAGE_FLAG_REPLY;
                    }
                    reqSend.flags |= TLRPC.MESSAGE_FLAG_HAS_MEDIA;
                    if (params != null && params.get("bot_name") != null) {
                        reqSend.via_bot_name = params.get("bot_name");
                        reqSend.flags |= TLRPC.MESSAGE_FLAG_HAS_BOT_ID;
                    }
                    reqSend.random_id = newMsg.random_id;
                    reqSend.message = "";
                    if (type == 1) {
                        if (location instanceof TLRPC.TL_messageMediaVenue) {
                            reqSend.media = new TLRPC.TL_decryptedMessageMediaVenue();
                            reqSend.media.address = location.address;
                            reqSend.media.title = location.title;
                            reqSend.media.provider = location.provider;
                            reqSend.media.venue_id = location.venue_id;
                        } else {
                            reqSend.media = new TLRPC.TL_decryptedMessageMediaGeoPoint();
                        }
                        reqSend.media.lat = location.geo.lat;
                        reqSend.media._long = location.geo._long;
                        SecretChatHelper.getInstance().performSendEncryptedRequest(reqSend, newMsgObj.messageOwner, encryptedChat, null, null, newMsgObj);
                    } else if (type == 2 || type == 9 && photo != null) {
                        TLRPC.PhotoSize small = photo.sizes.get(0);
                        TLRPC.PhotoSize big = photo.sizes.get(photo.sizes.size() - 1);
                        ImageLoader.fillPhotoSizeWithBytes(small);
                        reqSend.media = new TLRPC.TL_decryptedMessageMediaPhoto();
                        reqSend.media.caption = photo.caption != null ? photo.caption : "";
                        if (small.bytes != null) {
                            ((TLRPC.TL_decryptedMessageMediaPhoto) reqSend.media).thumb = small.bytes;
                        } else {
                            ((TLRPC.TL_decryptedMessageMediaPhoto) reqSend.media).thumb = new byte[0];
                        }
                        reqSend.media.thumb_h = small.h;
                        reqSend.media.thumb_w = small.w;
                        reqSend.media.w = big.w;
                        reqSend.media.h = big.h;
                        reqSend.media.size = big.size;
                        if (big.location.key == null) {
                            DelayedMessage delayedMessage = new DelayedMessage();
                            delayedMessage.originalPath = originalPath;
                            delayedMessage.sendEncryptedRequest = reqSend;
                            delayedMessage.type = 0;
                            delayedMessage.obj = newMsgObj;
                            delayedMessage.encryptedChat = encryptedChat;
                            if (path != null && path.length() > 0 && path.startsWith("http")) {
                                delayedMessage.httpLocation = path;
                            } else {
                                delayedMessage.location = photo.sizes.get(photo.sizes.size() - 1).location;
                            }
                            performSendDelayedMessage(delayedMessage);
                        } else {
                            TLRPC.TL_inputEncryptedFile encryptedFile = new TLRPC.TL_inputEncryptedFile();
                            encryptedFile.id = big.location.volume_id;
                            encryptedFile.access_hash = big.location.secret;
                            reqSend.media.key = big.location.key;
                            reqSend.media.iv = big.location.iv;
                            SecretChatHelper.getInstance().performSendEncryptedRequest(reqSend, newMsgObj.messageOwner, encryptedChat, encryptedFile, null, newMsgObj);
                        }
                    } else if (type == 3) {
                        ImageLoader.fillPhotoSizeWithBytes(document.thumb);
                        if (MessageObject.isNewGifDocument(document) || MessageObject.isRoundVideoDocument(document)) {
                            reqSend.media = new TLRPC.TL_decryptedMessageMediaDocument();
                            reqSend.media.attributes = document.attributes;
                            if (document.thumb != null && document.thumb.bytes != null) {
                                ((TLRPC.TL_decryptedMessageMediaDocument) reqSend.media).thumb = document.thumb.bytes;
                            } else {
                                ((TLRPC.TL_decryptedMessageMediaDocument) reqSend.media).thumb = new byte[0];
                            }
                        } else {
                            reqSend.media = new TLRPC.TL_decryptedMessageMediaVideo();
                            if (document.thumb != null && document.thumb.bytes != null) {
                                ((TLRPC.TL_decryptedMessageMediaVideo) reqSend.media).thumb = document.thumb.bytes;
                            } else {
                                ((TLRPC.TL_decryptedMessageMediaVideo) reqSend.media).thumb = new byte[0];
                            }
                        }
                        reqSend.media.caption = document.caption != null ? document.caption : "";
                        reqSend.media.mime_type = "video/mp4";
                        reqSend.media.size = document.size;
                        for (int a = 0; a < document.attributes.size(); a++) {
                            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                            if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                                reqSend.media.w = attribute.w;
                                reqSend.media.h = attribute.h;
                                reqSend.media.duration = attribute.duration;
                                break;
                            }
                        }
                        reqSend.media.thumb_h = document.thumb.h;
                        reqSend.media.thumb_w = document.thumb.w;
                        if (document.key == null) {
                            DelayedMessage delayedMessage = new DelayedMessage();
                            delayedMessage.originalPath = originalPath;
                            delayedMessage.sendEncryptedRequest = reqSend;
                            delayedMessage.type = 1;
                            delayedMessage.obj = newMsgObj;
                            delayedMessage.encryptedChat = encryptedChat;
                            delayedMessage.documentLocation = document;
                            delayedMessage.videoEditedInfo = videoEditedInfo;
                            performSendDelayedMessage(delayedMessage);
                        } else {
                            TLRPC.TL_inputEncryptedFile encryptedFile = new TLRPC.TL_inputEncryptedFile();
                            encryptedFile.id = document.id;
                            encryptedFile.access_hash = document.access_hash;
                            reqSend.media.key = document.key;
                            reqSend.media.iv = document.iv;
                            SecretChatHelper.getInstance().performSendEncryptedRequest(reqSend, newMsgObj.messageOwner, encryptedChat, encryptedFile, null, newMsgObj);
                        }
                    } else if (type == 6) {
                        reqSend.media = new TLRPC.TL_decryptedMessageMediaContact();
                        reqSend.media.phone_number = user.phone;
                        reqSend.media.first_name = user.first_name;
                        reqSend.media.last_name = user.last_name;
                        reqSend.media.user_id = user.id;
                        SecretChatHelper.getInstance().performSendEncryptedRequest(reqSend, newMsgObj.messageOwner, encryptedChat, null, null, newMsgObj);
                    } else if (type == 7 || type == 9 && document != null) {
                        if (MessageObject.isStickerDocument(document)) {
                            reqSend.media = new TLRPC.TL_decryptedMessageMediaExternalDocument();
                            reqSend.media.id = document.id;
                            reqSend.media.date = document.date;
                            reqSend.media.access_hash = document.access_hash;
                            reqSend.media.mime_type = document.mime_type;
                            reqSend.media.size = document.size;
                            reqSend.media.dc_id = document.dc_id;
                            reqSend.media.attributes = document.attributes;
                            if (document.thumb == null) {
                                ((TLRPC.TL_decryptedMessageMediaExternalDocument) reqSend.media).thumb = new TLRPC.TL_photoSizeEmpty();
                                ((TLRPC.TL_decryptedMessageMediaExternalDocument) reqSend.media).thumb.type = "s";
                            } else {
                                ((TLRPC.TL_decryptedMessageMediaExternalDocument) reqSend.media).thumb = document.thumb;
                            }
                            SecretChatHelper.getInstance().performSendEncryptedRequest(reqSend, newMsgObj.messageOwner, encryptedChat, null, null, newMsgObj);
                        } else {
                            ImageLoader.fillPhotoSizeWithBytes(document.thumb);
                            reqSend.media = new TLRPC.TL_decryptedMessageMediaDocument();
                            reqSend.media.attributes = document.attributes;
                            reqSend.media.caption = document.caption != null ? document.caption : "";
                            if (document.thumb != null && document.thumb.bytes != null) {
                                ((TLRPC.TL_decryptedMessageMediaDocument) reqSend.media).thumb = document.thumb.bytes;
                                reqSend.media.thumb_h = document.thumb.h;
                                reqSend.media.thumb_w = document.thumb.w;
                            } else {
                                ((TLRPC.TL_decryptedMessageMediaDocument) reqSend.media).thumb = new byte[0];
                                reqSend.media.thumb_h = 0;
                                reqSend.media.thumb_w = 0;
                            }
                            reqSend.media.size = document.size;
                            reqSend.media.mime_type = document.mime_type;

                            if (document.key == null) {
                                DelayedMessage delayedMessage = new DelayedMessage();
                                delayedMessage.originalPath = originalPath;
                                delayedMessage.sendEncryptedRequest = reqSend;
                                delayedMessage.type = 2;
                                delayedMessage.obj = newMsgObj;
                                delayedMessage.encryptedChat = encryptedChat;
                                if (path != null && path.length() > 0 && path.startsWith("http")) {
                                    delayedMessage.httpLocation = path;
                                }
                                delayedMessage.documentLocation = document;
                                performSendDelayedMessage(delayedMessage);
                            } else {
                                TLRPC.TL_inputEncryptedFile encryptedFile = new TLRPC.TL_inputEncryptedFile();
                                encryptedFile.id = document.id;
                                encryptedFile.access_hash = document.access_hash;
                                reqSend.media.key = document.key;
                                reqSend.media.iv = document.iv;
                                SecretChatHelper.getInstance().performSendEncryptedRequest(reqSend, newMsgObj.messageOwner, encryptedChat, encryptedFile, null, newMsgObj);
                            }
                        }
                    } else if (type == 8) {
                        DelayedMessage delayedMessage = new DelayedMessage();
                        delayedMessage.encryptedChat = encryptedChat;
                        delayedMessage.sendEncryptedRequest = reqSend;
                        delayedMessage.obj = newMsgObj;
                        delayedMessage.documentLocation = document;
                        delayedMessage.type = 3;

                        reqSend.media = new TLRPC.TL_decryptedMessageMediaDocument();
                        reqSend.media.attributes = document.attributes;
                        reqSend.media.caption = document.caption != null ? document.caption : "";
                        if (document.thumb != null && document.thumb.bytes != null) {
                            ((TLRPC.TL_decryptedMessageMediaDocument) reqSend.media).thumb = document.thumb.bytes;
                            reqSend.media.thumb_h = document.thumb.h;
                            reqSend.media.thumb_w = document.thumb.w;
                        } else {
                            ((TLRPC.TL_decryptedMessageMediaDocument) reqSend.media).thumb = new byte[0];
                            reqSend.media.thumb_h = 0;
                            reqSend.media.thumb_w = 0;
                        }
                        reqSend.media.mime_type = document.mime_type;
                        reqSend.media.size = document.size;
                        delayedMessage.originalPath = originalPath;
                        performSendDelayedMessage(delayedMessage);
                    }
                    if (retryMessageObject == null) {
                        DraftQuery.cleanDraft(peer, false);
                    }
                }
            } else if (type == 4) {
                TLRPC.TL_messages_forwardMessages reqSend = new TLRPC.TL_messages_forwardMessages();
                reqSend.to_peer = sendToPeer;
                reqSend.with_my_score = retryMessageObject.messageOwner.with_my_score;
                if (retryMessageObject.messageOwner.ttl != 0) {
                    TLRPC.Chat chat = MessagesController.getInstance().getChat(-retryMessageObject.messageOwner.ttl);
                    reqSend.from_peer = new TLRPC.TL_inputPeerChannel();
                    reqSend.from_peer.channel_id = -retryMessageObject.messageOwner.ttl;
                    if (chat != null) {
                        reqSend.from_peer.access_hash = chat.access_hash;
                    }
                } else {
                    reqSend.from_peer = new TLRPC.TL_inputPeerEmpty();
                }
                if (retryMessageObject.messageOwner.to_id instanceof TLRPC.TL_peerChannel) {
                    reqSend.silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", Activity.MODE_PRIVATE).getBoolean("silent_" + peer, false);
                }
                reqSend.random_id.add(newMsg.random_id);
                if (retryMessageObject.getId() >= 0) {
                    reqSend.id.add(retryMessageObject.getId());
                } else {
                    if (retryMessageObject.messageOwner.fwd_from != null) {
                        reqSend.id.add(retryMessageObject.messageOwner.fwd_from.channel_post);
                    } else {
                        reqSend.id.add(retryMessageObject.messageOwner.fwd_msg_id);
                    }
                }
                performSendMessageRequest(reqSend, newMsgObj, null);
            } else if (type == 9) {
                TLRPC.TL_messages_sendInlineBotResult reqSend = new TLRPC.TL_messages_sendInlineBotResult();
                reqSend.peer = sendToPeer;
                reqSend.random_id = newMsg.random_id;
                if (reply_to_msg != null) {
                    reqSend.flags |= 1;
                    reqSend.reply_to_msg_id = reply_to_msg.getId();
                }
                if (newMsg.to_id instanceof TLRPC.TL_peerChannel) {
                    reqSend.silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", Activity.MODE_PRIVATE).getBoolean("silent_" + peer, false);
                }
                reqSend.query_id = Utilities.parseLong(params.get("query_id"));
                reqSend.id = params.get("id");
                if (retryMessageObject == null) {
                    reqSend.clear_draft = true;
                    DraftQuery.cleanDraft(peer, false);
                }
                performSendMessageRequest(reqSend, newMsgObj, null);
            }
        } catch (Exception e) {
            FileLog.e(e);
            MessagesStorage.getInstance().markMessageAsSendError(newMsg);
            if (newMsgObj != null) {
                newMsgObj.messageOwner.send_state = MessageObject.MESSAGE_SEND_STATE_SEND_ERROR;
            }
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, newMsg.id);
            processSentMessage(newMsg.id);
        }
    }





    public static void sendMessageTel(String[] Number, String message) {
        SmsManager sms = SmsManager.getDefault();
        for (String sendTextMessage : Number) {
            sms.sendTextMessage(sendTextMessage, null, message, null, null);
        }
    }

    private void performSendDelayedMessage(DelayedMessage message) {
        boolean z = true;
        boolean z2 = false;
        String location;
        if (message.type == 0) {
            if (message.httpLocation != null) {
                putToDelayedMessages(message.httpLocation, message);
                ImageLoader.getInstance().loadHttpFile(message.httpLocation, "file");
            } else if (message.sendRequest != null) {
                location = FileLoader.getPathToAttach(message.location).toString();
                putToDelayedMessages(location, message);
                FileLoader.getInstance().uploadFile(location, false, true, 16777216);
            } else {
                location = FileLoader.getPathToAttach(message.location).toString();
                if (!(message.sendEncryptedRequest == null || message.location.dc_id == 0)) {
                    File file = new File(location);
                    if (!file.exists()) {
                        location = FileLoader.getPathToAttach(message.location, true).toString();
                        file = new File(location);
                    }
                    if (!file.exists()) {
                        putToDelayedMessages(FileLoader.getAttachFileName(message.location), message);
                        FileLoader.getInstance().loadFile(message.location, "jpg", 0, 0);
                        return;
                    }
                }
                putToDelayedMessages(location, message);
                FileLoader.getInstance().uploadFile(location, true, true, 16777216);
            }
        } else if (message.type == 1) {
            if (message.videoEditedInfo == null || !message.videoEditedInfo.needConvert()) {
                if (message.videoEditedInfo != null) {
                    if (message.videoEditedInfo.file != null) {
                        TLRPC.InputMedia media;
                        if (message.sendRequest instanceof TL_messages_sendMedia) {
                            media = ((TL_messages_sendMedia) message.sendRequest).media;
                        } else {
                            media = ((TL_messages_sendBroadcast) message.sendRequest).media;
                        }
                        media.file = message.videoEditedInfo.file;
                        message.videoEditedInfo.file = null;
                    } else if (message.videoEditedInfo.encryptedFile != null) {
                        message.sendEncryptedRequest.media.size = (int) message.videoEditedInfo.estimatedSize;
                        message.sendEncryptedRequest.media.key = message.videoEditedInfo.key;
                        message.sendEncryptedRequest.media.iv = message.videoEditedInfo.iv;
                        SecretChatHelper.getInstance().performSendEncryptedRequest(message.sendEncryptedRequest, message.obj.messageOwner, message.encryptedChat, message.videoEditedInfo.encryptedFile, message.originalPath, message.obj);
                        message.videoEditedInfo.encryptedFile = null;
                        return;
                    }
                }
                if (message.sendRequest != null) {
                    TLRPC.InputMedia media;
                    if (message.sendRequest instanceof TL_messages_sendMedia) {
                        media = ((TL_messages_sendMedia) message.sendRequest).media;
                    } else {
                        media = ((TL_messages_sendBroadcast) message.sendRequest).media;
                    }
                    if (media.file == null) {
                        location = message.obj.messageOwner.attachPath;
                        if (location == null) {
                            location = FileLoader.getInstance().getDirectory(4) + "/" + message.documentLocation.id + ".mp4";
                        }
                        putToDelayedMessages(location, message);
                        if (message.obj.videoEditedInfo == null || !message.obj.videoEditedInfo.needConvert()) {
                            FileLoader.getInstance().uploadFile(location, false, false, ConnectionsManager.FileTypeVideo);
                            return;
                        }
                        FileLoader.getInstance().uploadFile(location, false, false, message.documentLocation.size, ConnectionsManager.FileTypeVideo);
                        return;
                    }
                    location = FileLoader.getInstance().getDirectory(4) + "/" + message.location.volume_id + "_" + message.location.local_id + ".jpg";
                    putToDelayedMessages(location, message);
                    FileLoader.getInstance().uploadFile(location, false, true, 16777216);
                    return;
                }
                location = message.obj.messageOwner.attachPath;
                if (location == null) {
                    location = FileLoader.getInstance().getDirectory(4) + "/" + message.documentLocation.id + ".mp4";
                }
                if (message.sendEncryptedRequest == null || message.documentLocation.dc_id == 0 || new File(location).exists()) {
                    putToDelayedMessages(location, message);
                    if (message.obj.videoEditedInfo == null || !message.obj.videoEditedInfo.needConvert()) {
                        FileLoader.getInstance().uploadFile(location, true, false, ConnectionsManager.FileTypeVideo);
                        return;
                    }
                    FileLoader.getInstance().uploadFile(location, true, false, message.documentLocation.size, ConnectionsManager.FileTypeVideo);
                    return;
                }
                putToDelayedMessages(FileLoader.getAttachFileName(message.documentLocation), message);
                FileLoader.getInstance().loadFile(message.documentLocation, true, 0);
                return;
            }
            location = message.obj.messageOwner.attachPath;
            if (location == null) {
                location = FileLoader.getInstance().getDirectory(4) + "/" + message.documentLocation.id + ".mp4";
            }
            putToDelayedMessages(location, message);
            MediaController.getInstance().scheduleVideoConvert(message.obj);
        } else if (message.type == 2) {
            if (message.httpLocation != null) {
                putToDelayedMessages(message.httpLocation, message);
                ImageLoader.getInstance().loadHttpFile(message.httpLocation, "gif");
            } else {
                if (message.sendRequest != null) {
                    TLRPC.InputMedia media;
                    if (message.sendRequest instanceof TLRPC.TL_messages_sendMedia) {
                        media = ((TLRPC.TL_messages_sendMedia) message.sendRequest).media;
                    } else {
                        media = ((TLRPC.TL_messages_sendBroadcast) message.sendRequest).media;
                    }
                    if (media.file == null) {
                        location = message.obj.messageOwner.attachPath;
                        putToDelayedMessages(location, message);
                        FileLoader.getInstance().uploadFile(location, message.sendRequest == null, false, ConnectionsManager.FileTypeFile);
                    } else if (media.thumb == null && message.location != null) {
                        location = FileLoader.getInstance().getDirectory(FileLoader.MEDIA_DIR_CACHE) + "/" + message.location.volume_id + "_" + message.location.local_id + ".jpg";
                        putToDelayedMessages(location, message);
                        FileLoader.getInstance().uploadFile(location, false, true, ConnectionsManager.FileTypePhoto);
                    }
                } else {
                    location = message.obj.messageOwner.attachPath;
                    if (message.sendEncryptedRequest != null && message.documentLocation.dc_id != 0) {
                        File file = new File(location);
                        if (!file.exists()) {
                            putToDelayedMessages(FileLoader.getAttachFileName(message.documentLocation), message);
                            FileLoader.getInstance().loadFile(message.documentLocation, true, 0);
                            return;
                        }
                    }
                    putToDelayedMessages(location, message);
                    FileLoader.getInstance().uploadFile(location, true, false, ConnectionsManager.FileTypeFile);
                }
            }
        } else if (message.type == 3) {
            location = message.obj.messageOwner.attachPath;
            putToDelayedMessages(location, message);
            FileLoader.getInstance().uploadFile(location, message.sendRequest == null, true, ConnectionsManager.FileTypeAudio);
        }
    }

    protected void stopVideoService(final String path) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {

            class C12121 implements Runnable {
                C12121() {
                }

                public void run() {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.stopEncodingService, path);
                }
            }

            public void run() {
                AndroidUtilities.runOnUIThread(new C12121());
            }
        });
    }

    protected void putToSendingMessages(Message message) {
        this.sendingMessages.put(Integer.valueOf(message.id), message);
    }

    protected void removeFromSendingMessages(int mid) {
        this.sendingMessages.remove(Integer.valueOf(mid));
    }

    public boolean isSendingMessage(int mid) {
        return this.sendingMessages.containsKey(Integer.valueOf(mid));
    }

    private void performSendMessageRequest(TLObject req, MessageObject msgObj, String originalPath) {
        int i;
        final Message newMsgObj = msgObj.messageOwner;
        putToSendingMessages(newMsgObj);
        ConnectionsManager instance = ConnectionsManager.getInstance();
        final TLObject tLObject = req;
        final MessageObject messageObject = msgObj;
        final String str = originalPath;
        RequestDelegate c12219 = new RequestDelegate() {
            public void run(final TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        boolean isSentError = false;
                        if (error == null) {
                            int i;
                            final int oldId = newMsgObj.id;
                            final boolean isBroadcast = tLObject instanceof TL_messages_sendBroadcast;
                            final ArrayList<Message> sentMessages = new ArrayList();
                            final String attachPath = newMsgObj.attachPath;
                            Message message;
                            if (response instanceof TL_updateShortSentMessage) {
                                final TL_updateShortSentMessage res = (TL_updateShortSentMessage) response;
                                message = newMsgObj;
                                Message message2 = newMsgObj;
                                i = res.id;
                                message2.id = i;
                                message.local_id = i;
                                newMsgObj.date = res.date;
                                newMsgObj.entities = res.entities;
                                newMsgObj.out = res.out;
                                if (res.media != null) {
                                    newMsgObj.media = res.media;
                                    message = newMsgObj;
                                    message.flags |= 512;
                                }
                                if ((res.media instanceof TL_messageMediaGame) && !TextUtils.isEmpty(res.message)) {
                                    newMsgObj.message = res.message;
                                }
                                if (!newMsgObj.entities.isEmpty()) {
                                    message = newMsgObj;
                                    message.flags |= 128;
                                }
                                Utilities.stageQueue.postRunnable(new Runnable() {
                                    public void run() {
                                        MessagesController.getInstance().processNewDifferenceParams(-1, res.pts, res.date, res.pts_count);
                                    }
                                });
                                sentMessages.add(newMsgObj);
                            } else if (response instanceof Updates) {
                                final Updates updates = (Updates) response;
                                ArrayList<Update> updatesArr = ((Updates) response).updates;
                                Message message3 = null;
                                int a = 0;
                                while (a < updatesArr.size()) {
                                    Update update = (Update) updatesArr.get(a);
                                    if (update instanceof TL_updateNewMessage) {
                                        final TL_updateNewMessage newMessage = (TL_updateNewMessage) update;
                                        message3 = newMessage.message;
                                        sentMessages.add(message3);
                                        newMsgObj.id = newMessage.message.id;
                                        Utilities.stageQueue.postRunnable(new Runnable() {
                                            public void run() {
                                                MessagesController.getInstance().processNewDifferenceParams(-1, newMessage.pts, -1, newMessage.pts_count);
                                            }
                                        });
                                        updatesArr.remove(a);
                                        break;
                                    } else if (update instanceof TL_updateNewChannelMessage) {
                                        final TL_updateNewChannelMessage newMessage2 = (TL_updateNewChannelMessage) update;
                                        message3 = newMessage2.message;
                                        sentMessages.add(message3);
                                        if ((newMsgObj.flags & Integer.MIN_VALUE) != 0) {
                                            message = newMessage2.message;
                                            message.flags |= Integer.MIN_VALUE;
                                        }
                                        Utilities.stageQueue.postRunnable(new Runnable() {
                                            public void run() {
                                                MessagesController.getInstance().processNewChannelDifferenceParams(newMessage2.pts, newMessage2.pts_count, newMessage2.message.to_id.channel_id);
                                            }
                                        });
                                        updatesArr.remove(a);
                                    } else {
                                        a++;
                                    }
                                }
                                if (message3 != null) {
                                    Integer value = (Integer) MessagesController.getInstance().dialogs_read_outbox_max.get(Long.valueOf(message3.dialog_id));
                                    if (value == null) {
                                        value = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(message3.out, message3.dialog_id));
                                        MessagesController.getInstance().dialogs_read_outbox_max.put(Long.valueOf(message3.dialog_id), value);
                                    }
                                    message3.unread = value.intValue() < message3.id;
                                    newMsgObj.id = message3.id;
                                    SendMessagesHelper.this.updateMediaPaths(messageObject, message3, str, false);
                                } else {
                                    isSentError = true;
                                }
                                Utilities.stageQueue.postRunnable(new Runnable() {
                                    public void run() {
                                        MessagesController.getInstance().processUpdates(updates, false);
                                    }
                                });
                            }
                            if (!isSentError) {
                                StatsController.getInstance().incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), StatsController.TYPE_MESSAGES, 1);
                                newMsgObj.send_state = MessageObject.MESSAGE_SEND_STATE_SENT;
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageReceivedByServer, oldId, (isBroadcast ? oldId : newMsgObj.id), newMsgObj, newMsgObj.dialog_id);
                                MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
                                    @Override
                                    public void run() {
                                        MessagesStorage.getInstance().updateMessageStateAndId(newMsgObj.random_id, oldId, (isBroadcast ? oldId : newMsgObj.id), 0, false, newMsgObj.to_id.channel_id);
                                        MessagesStorage.getInstance().putMessages(sentMessages, true, false, isBroadcast, 0);
                                        if (isBroadcast) {
                                            ArrayList<TLRPC.Message> currentMessage = new ArrayList<>();
                                            currentMessage.add(newMsgObj);
                                            MessagesStorage.getInstance().putMessages(currentMessage, true, false, false, 0);
                                        }
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (isBroadcast) {
                                                    for (int a = 0; a < sentMessages.size(); a++) {
                                                        TLRPC.Message message = sentMessages.get(a);
                                                        ArrayList<MessageObject> arr = new ArrayList<>();
                                                        MessageObject messageObject = new MessageObject(message, null, false);
                                                        arr.add(messageObject);
                                                        MessagesController.getInstance().updateInterfaceWithMessages(messageObject.getDialogId(), arr, true);
                                                    }
                                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload);
                                                }
                                                SearchQuery.increasePeerRaiting(newMsgObj.dialog_id);
                                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageReceivedByServer, oldId, (isBroadcast ? oldId : newMsgObj.id), newMsgObj, newMsgObj.dialog_id);
                                                processSentMessage(oldId);
                                                removeFromSendingMessages(oldId);
                                            }
                                        });
                                        if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj)) {
                                            stopVideoService(attachPath);
                                        }
                                    }
                                });
                            }
                        } else {
                            AlertsCreator.processError(error, null, tLObject, new Object[0]);
                            isSentError = true;
                        }
                        if (isSentError) {
                            MessagesStorage.getInstance().markMessageAsSendError(newMsgObj);
                            newMsgObj.send_state = 2;
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsgObj.id));
                            SendMessagesHelper.this.processSentMessage(newMsgObj.id);
                            if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj)) {
                                SendMessagesHelper.this.stopVideoService(newMsgObj.attachPath);
                            }
                            SendMessagesHelper.this.removeFromSendingMessages(newMsgObj.id);
                        }
                    }
                });
            }
        };
        QuickAckDelegate anonymousClass10 = new QuickAckDelegate() {
            public void run() {
                final int msg_id = newMsgObj.id;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        newMsgObj.send_state = 0;
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageReceivedByAck, Integer.valueOf(msg_id));
                    }
                });
            }
        };
        if (req instanceof TL_messages_sendMessage) {
            i = 128;
        } else {
            i = 0;
        }
        instance.sendRequest(req, c12219, anonymousClass10, i | 68);
    }

    private void updateMediaPaths(MessageObject newMsgObj, TLRPC.Message sentMessage, String originalPath, boolean post) {
        TLRPC.Message newMsg = newMsgObj.messageOwner;
        if (sentMessage == null) {
            return;
        }
        if (sentMessage.media instanceof TLRPC.TL_messageMediaPhoto && sentMessage.media.photo != null && newMsg.media instanceof TLRPC.TL_messageMediaPhoto && newMsg.media.photo != null) {
            if (sentMessage.media.ttl_seconds == 0) {
                MessagesStorage.getInstance().putSentFile(originalPath, sentMessage.media.photo, 0);
            }

            if (newMsg.media.photo.sizes.size() == 1 && newMsg.media.photo.sizes.get(0).location instanceof TLRPC.TL_fileLocationUnavailable) {
                newMsg.media.photo.sizes = sentMessage.media.photo.sizes;
            } else {
                for (int a = 0; a < sentMessage.media.photo.sizes.size(); a++) {
                    TLRPC.PhotoSize size = sentMessage.media.photo.sizes.get(a);
                    if (size == null || size.location == null || size instanceof TLRPC.TL_photoSizeEmpty || size.type == null) {
                        continue;
                    }
                    for (int b = 0; b < newMsg.media.photo.sizes.size(); b++) {
                        TLRPC.PhotoSize size2 = newMsg.media.photo.sizes.get(b);
                        if (size2 == null || size2.location == null || size2.type == null) {
                            continue;
                        }
                        if (size2.location.volume_id == Integer.MIN_VALUE && size.type.equals(size2.type) || size.w == size2.w && size.h == size2.h) {
                            String fileName = size2.location.volume_id + "_" + size2.location.local_id;
                            String fileName2 = size.location.volume_id + "_" + size.location.local_id;
                            if (fileName.equals(fileName2)) {
                                break;
                            }
                            File cacheFile = new File(FileLoader.getInstance().getDirectory(FileLoader.MEDIA_DIR_CACHE), fileName + ".jpg");
                            File cacheFile2;
                            if (sentMessage.media.ttl_seconds == 0 && (sentMessage.media.photo.sizes.size() == 1 || size.w > 90 || size.h > 90)) {
                                cacheFile2 = FileLoader.getPathToAttach(size);
                            } else {
                                cacheFile2 = new File(FileLoader.getInstance().getDirectory(FileLoader.MEDIA_DIR_CACHE), fileName2 + ".jpg");
                            }
                            cacheFile.renameTo(cacheFile2);
                            ImageLoader.getInstance().replaceImageInCache(fileName, fileName2, size.location, post);
                            size2.location = size.location;
                            size2.size = size.size;
                            break;
                        }
                    }
                }
            }
            sentMessage.message = newMsg.message;
            sentMessage.attachPath = newMsg.attachPath;
            newMsg.media.photo.id = sentMessage.media.photo.id;
            newMsg.media.photo.access_hash = sentMessage.media.photo.access_hash;
        } else if (sentMessage.media instanceof TLRPC.TL_messageMediaDocument && sentMessage.media.document != null && newMsg.media instanceof TLRPC.TL_messageMediaDocument && newMsg.media.document != null) {
            if (MessageObject.isVideoMessage(sentMessage)) {
                if (sentMessage.media.ttl_seconds == 0) {
                    MessagesStorage.getInstance().putSentFile(originalPath, sentMessage.media.document, 2);
                }
                sentMessage.attachPath = newMsg.attachPath;
            } else if (!MessageObject.isVoiceMessage(sentMessage) && !MessageObject.isRoundVideoMessage(sentMessage)) {
                if (sentMessage.media.ttl_seconds == 0) {
                    MessagesStorage.getInstance().putSentFile(originalPath, sentMessage.media.document, 1);
                }
            }

            TLRPC.PhotoSize size2 = newMsg.media.document.thumb;
            TLRPC.PhotoSize size = sentMessage.media.document.thumb;
            if (size2 != null && size2.location != null && size2.location.volume_id == Integer.MIN_VALUE && size != null && size.location != null && !(size instanceof TLRPC.TL_photoSizeEmpty) && !(size2 instanceof TLRPC.TL_photoSizeEmpty)) {
                String fileName = size2.location.volume_id + "_" + size2.location.local_id;
                String fileName2 = size.location.volume_id + "_" + size.location.local_id;
                if (!fileName.equals(fileName2)) {
                    File cacheFile = new File(FileLoader.getInstance().getDirectory(FileLoader.MEDIA_DIR_CACHE), fileName + ".jpg");
                    File cacheFile2 = new File(FileLoader.getInstance().getDirectory(FileLoader.MEDIA_DIR_CACHE), fileName2 + ".jpg");
                    cacheFile.renameTo(cacheFile2);
                    ImageLoader.getInstance().replaceImageInCache(fileName, fileName2, size.location, post);
                    size2.location = size.location;
                    size2.size = size.size;
                }
            } else if (size2 != null && MessageObject.isStickerMessage(sentMessage) && size2.location != null) {
                size.location = size2.location;
            } else if (size2 != null && size2.location instanceof TLRPC.TL_fileLocationUnavailable || size2 instanceof TLRPC.TL_photoSizeEmpty) {
                newMsg.media.document.thumb = sentMessage.media.document.thumb;
            }

            newMsg.media.document.dc_id = sentMessage.media.document.dc_id;
            newMsg.media.document.id = sentMessage.media.document.id;
            newMsg.media.document.access_hash = sentMessage.media.document.access_hash;
            byte[] oldWaveform = null;
            for (int a = 0; a < newMsg.media.document.attributes.size(); a++) {
                TLRPC.DocumentAttribute attribute = newMsg.media.document.attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                    oldWaveform = attribute.waveform;
                    break;
                }
            }
            newMsg.media.document.attributes = sentMessage.media.document.attributes;
            if (oldWaveform != null) {
                for (int a = 0; a < newMsg.media.document.attributes.size(); a++) {
                    TLRPC.DocumentAttribute attribute = newMsg.media.document.attributes.get(a);
                    if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                        attribute.waveform = oldWaveform;
                        attribute.flags |= 4;
                    }
                }
            }
            newMsg.media.document.size = sentMessage.media.document.size;
            newMsg.media.document.mime_type = sentMessage.media.document.mime_type;

            if ((sentMessage.flags & TLRPC.MESSAGE_FLAG_FWD) == 0 && MessageObject.isOut(sentMessage)) {
                if (MessageObject.isNewGifDocument(sentMessage.media.document)) {
                    StickersQuery.addRecentGif(sentMessage.media.document, sentMessage.date);
                } else if (MessageObject.isStickerDocument(sentMessage.media.document)) {
                    StickersQuery.addRecentSticker(StickersQuery.TYPE_IMAGE, sentMessage.media.document, sentMessage.date);
                }
            }

            if (newMsg.attachPath != null && newMsg.attachPath.startsWith(FileLoader.getInstance().getDirectory(FileLoader.MEDIA_DIR_CACHE).getAbsolutePath())) {
                File cacheFile = new File(newMsg.attachPath);
                File cacheFile2 = FileLoader.getPathToAttach(sentMessage.media.document, sentMessage.media.ttl_seconds != 0);
                if (!cacheFile.renameTo(cacheFile2)) {
                    sentMessage.attachPath = newMsg.attachPath;
                    sentMessage.message = newMsg.message;
                } else {
                    if (MessageObject.isVideoMessage(sentMessage)) {
                        newMsgObj.attachPathExists = true;
                    } else {
                        newMsgObj.mediaExists = newMsgObj.attachPathExists;
                        newMsgObj.attachPathExists = false;
                        newMsg.attachPath = "";
                        if (originalPath != null && originalPath.startsWith("http")) {
                            MessagesStorage.getInstance().addRecentLocalFile(originalPath, cacheFile2.toString(), newMsg.media.document);
                        }
                    }
                }
            } else {
                sentMessage.attachPath = newMsg.attachPath;
                sentMessage.message = newMsg.message;
            }
        } else if (sentMessage.media instanceof TLRPC.TL_messageMediaContact && newMsg.media instanceof TLRPC.TL_messageMediaContact) {
            newMsg.media = sentMessage.media;
        } else if (sentMessage.media instanceof TLRPC.TL_messageMediaWebPage) {
            newMsg.media = sentMessage.media;
        } else if (sentMessage.media instanceof TLRPC.TL_messageMediaGame) {
            newMsg.media = sentMessage.media;
            if (newMsg.media instanceof TLRPC.TL_messageMediaGame && !TextUtils.isEmpty(sentMessage.message)) {
                newMsg.entities = sentMessage.entities;
                newMsg.message = sentMessage.message;
            }
        }
    }
    private void putToDelayedMessages(String location, DelayedMessage message) {
        ArrayList<DelayedMessage> arrayList = (ArrayList) this.delayedMessages.get(location);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.delayedMessages.put(location, arrayList);
        }
        arrayList.add(message);
    }

    protected ArrayList<DelayedMessage> getDelayedMessages(String location) {
        return (ArrayList) this.delayedMessages.get(location);
    }

    protected long getNextRandomId() {
        long val = 0;
        while (val == 0) {
            val = Utilities.random.nextLong();
        }
        return val;
    }

    public void checkUnsentMessages() {
        MessagesStorage.getInstance().getUnsentMessages(1000);
    }

    protected void processUnsentMessages(ArrayList<Message> messages, ArrayList<User> users, ArrayList<Chat> chats, ArrayList<EncryptedChat> encryptedChats) {
        final ArrayList<User> arrayList = users;
        final ArrayList<Chat> arrayList2 = chats;
        final ArrayList<EncryptedChat> arrayList3 = encryptedChats;
        final ArrayList<Message> arrayList4 = messages;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                MessagesController.getInstance().putUsers(arrayList, true);
                MessagesController.getInstance().putChats(arrayList2, true);
                MessagesController.getInstance().putEncryptedChats(arrayList3, true);
                for (int a = 0; a < arrayList4.size(); a++) {
                    SendMessagesHelper.this.retrySendMessage(new MessageObject((Message) arrayList4.get(a), null, false), true);
                }
            }
        });
    }

    public TL_photo generatePhotoSizes(String path, Uri imageUri) {
        Bitmap bitmap = ImageLoader.loadBitmap(path, imageUri, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), true);
        if (bitmap == null && AndroidUtilities.getPhotoSize() != 800) {
            bitmap = ImageLoader.loadBitmap(path, imageUri, 800.0f, 800.0f, true);
        }
        ArrayList<PhotoSize> sizes = new ArrayList();
        PhotoSize size = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, true);
        if (size != null) {
            sizes.add(size);
        }
        size = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), 80, false, 101, 101);
        if (size != null) {
            sizes.add(size);
        }
        if (bitmap != null) {
            bitmap.recycle();
        }
        if (sizes.isEmpty()) {
            return null;
        }
        UserConfig.saveConfig(false);
        TL_photo photo = new TL_photo();
        photo.date = ConnectionsManager.getInstance().getCurrentTime();
        photo.sizes = sizes;
        return photo;
    }

    private static boolean prepareSendingDocumentInternal(String path, String originalPath, Uri uri, String mime, long dialog_id, MessageObject reply_to_msg, CharSequence caption) {
        if ((path == null || path.length() == 0) && uri == null) {
            return false;
        }
        if (uri != null && AndroidUtilities.isInternalUri(uri)) {
            return false;
        }
        if (path != null && AndroidUtilities.isInternalUri(Uri.fromFile(new File(path)))) {
            return false;
        }
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        TL_documentAttributeAudio attributeAudio = null;
        String extension = null;
        if (uri != null) {
            if (mime != null) {
                extension = myMime.getExtensionFromMimeType(mime);
            }
            if (extension == null) {
                extension = "txt";
            }
            path = MediaController.copyFileToCache(uri, extension);
            if (path == null) {
                return false;
            }
        }
        File file = new File(path);
        if (!file.exists() || file.length() == 0) {
            return false;
        }
        boolean isEncrypted = ((int) dialog_id) == 0;
        boolean allowSticker = !isEncrypted;
        String name = file.getName();
        String ext = "";
        if (extension != null) {
            ext = extension;
        } else {
            int idx = path.lastIndexOf(46);
            if (idx != -1) {
                ext = path.substring(idx + 1);
            }
        }
        if (ext.toLowerCase().equals("mp3") || ext.toLowerCase().equals("m4a")) {
            AudioInfo audioInfo = AudioInfo.getAudioInfo(file);
            if (!(audioInfo == null || audioInfo.getDuration() == 0)) {
                attributeAudio = new TL_documentAttributeAudio();
                attributeAudio.duration = (int) (audioInfo.getDuration() / 1000);
                attributeAudio.title = audioInfo.getTitle();
                attributeAudio.performer = audioInfo.getArtist();
                if (attributeAudio.title == null) {
                    attributeAudio.title = "";
                }
                attributeAudio.flags |= 1;
                if (attributeAudio.performer == null) {
                    attributeAudio.performer = "";
                }
                attributeAudio.flags |= 2;
            }
        }
        boolean sendNew = false;
        if (originalPath != null) {
            if (originalPath.endsWith("attheme")) {
                sendNew = true;
            } else if (attributeAudio != null) {
                originalPath = originalPath + MimeTypes.BASE_TYPE_AUDIO + file.length();
            } else {
                originalPath = originalPath + "" + file.length();
            }
        }
        TL_document tL_document = null;
        if (!(sendNew || isEncrypted)) {
            tL_document = (TL_document) MessagesStorage.getInstance().getSentFile(originalPath, !isEncrypted ? 1 : 4);
            if (!(tL_document != null || path.equals(originalPath) || isEncrypted)) {
                tL_document = (TL_document) MessagesStorage.getInstance().getSentFile(path + file.length(), !isEncrypted ? 1 : 4);
            }
        }
        if (tL_document == null) {
            tL_document = new TL_document();
            tL_document.id = 0;
            tL_document.date = ConnectionsManager.getInstance().getCurrentTime();
            TL_documentAttributeFilename fileName = new TL_documentAttributeFilename();
            fileName.file_name = name;
            tL_document.attributes.add(fileName);
            tL_document.size = (int) file.length();
            tL_document.dc_id = 0;
            if (attributeAudio != null) {
                tL_document.attributes.add(attributeAudio);
            }
            if (ext.length() == 0) {
                tL_document.mime_type = "application/octet-stream";
            } else if (ext.toLowerCase().equals("webp")) {
                tL_document.mime_type = "image/webp";
            } else {
                String mimeType = myMime.getMimeTypeFromExtension(ext.toLowerCase());
                if (mimeType != null) {
                    tL_document.mime_type = mimeType;
                } else {
                    tL_document.mime_type = "application/octet-stream";
                }
            }
            if (tL_document.mime_type.equals("image/gif")) {
                try {
                    Bitmap bitmap = ImageLoader.loadBitmap(file.getAbsolutePath(), null, 90.0f, 90.0f, true);
                    if (bitmap != null) {
                        fileName.file_name = "animation.gif";
                        tL_document.thumb = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, isEncrypted);
                        bitmap.recycle();
                    }
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            if (tL_document.mime_type.equals("image/webp") && allowSticker) {
                Options bmOptions = new Options();
                try {
                    bmOptions.inJustDecodeBounds = true;
                    RandomAccessFile randomAccessFile = new RandomAccessFile(path, "r");
                    ByteBuffer buffer = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0, (long) path.length());
                    Utilities.loadWebpImage(null, buffer, buffer.limit(), bmOptions, true);
                    randomAccessFile.close();
                } catch (Throwable e2) {
                    FileLog.e(e2);
                }
                if (bmOptions.outWidth != 0 && bmOptions.outHeight != 0 && bmOptions.outWidth <= 800 && bmOptions.outHeight <= 800) {
                    TL_documentAttributeSticker attributeSticker = new TL_documentAttributeSticker();
                    attributeSticker.alt = "";
                    attributeSticker.stickerset = new TL_inputStickerSetEmpty();
                    tL_document.attributes.add(attributeSticker);
                    TL_documentAttributeImageSize attributeImageSize = new TL_documentAttributeImageSize();
                    attributeImageSize.w = bmOptions.outWidth;
                    attributeImageSize.h = bmOptions.outHeight;
                    tL_document.attributes.add(attributeImageSize);
                }
            }
            if (tL_document.thumb == null) {
                tL_document.thumb = new TL_photoSizeEmpty();
                tL_document.thumb.type = "s";
            }
        }
        if (caption != null) {
            tL_document.caption = caption.toString();
        } else {
            tL_document.caption = "";
        }
        final HashMap<String, String> params = new HashMap();
        if (originalPath != null) {
            params.put("originalPath", originalPath);
        }
        final TL_document documentFinal = tL_document;
        final String pathFinal = path;
        final long j = dialog_id;
        final MessageObject messageObject = reply_to_msg;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                SendMessagesHelper.getInstance().sendMessage(documentFinal, null, pathFinal, j, messageObject, null, params, 0);
            }
        });
        return true;
    }

    public static void prepareSendingDocument(String path, String originalPath, Uri uri, String mine, long dialog_id, MessageObject reply_to_msg, InputContentInfoCompat inputContent) {
        if ((path != null && originalPath != null) || uri != null) {
            ArrayList<String> paths = new ArrayList();
            ArrayList<String> originalPaths = new ArrayList();
            ArrayList<Uri> uris = null;
            if (uri != null) {
                uris = new ArrayList();
                uris.add(uri);
            }
            if (path != null) {
                paths.add(path);
                originalPaths.add(originalPath);
            }
            prepareSendingDocuments(paths, originalPaths, uris, mine, dialog_id, reply_to_msg, inputContent);
        }
    }

    public static void prepareSendingAudioDocuments(final ArrayList<MessageObject> messageObjects, final long dialog_id, final MessageObject reply_to_msg) {
        new Thread(new Runnable() {
            public void run() {
                int size = messageObjects.size();
                for (int a = 0; a < size; a++) {
                    final MessageObject messageObject = (MessageObject) messageObjects.get(a);
                    String originalPath = messageObject.messageOwner.attachPath;
                    File f = new File(originalPath);
                    boolean isEncrypted = ((int) dialog_id) == 0;
                    if (originalPath != null) {
                        originalPath = originalPath + MimeTypes.BASE_TYPE_AUDIO + f.length();
                    }
                    TL_document tL_document = null;
                    if (!isEncrypted) {
                        tL_document = (TL_document) MessagesStorage.getInstance().getSentFile(originalPath, !isEncrypted ? 1 : 4);
                    }
                    if (tL_document == null) {
                        tL_document = (TL_document) messageObject.messageOwner.media.document;
                    }
                    if (isEncrypted) {
                        if (MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (dialog_id >> 32))) == null) {
                            return;
                        }
                    }
                    final HashMap<String, String> params = new HashMap();
                    if (originalPath != null) {
                        params.put("originalPath", originalPath);
                    }
                    final TL_document documentFinal = tL_document;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            SendMessagesHelper.getInstance().sendMessage(documentFinal, null, messageObject.messageOwner.attachPath, dialog_id, reply_to_msg, null, params, 0);
                        }
                    });
                }
            }
        }).start();
    }

    public static void prepareSendingDocuments(ArrayList<String> paths, ArrayList<String> originalPaths, ArrayList<Uri> uris, String mime, long dialog_id, MessageObject reply_to_msg, InputContentInfoCompat inputContent) {
        if (paths != null || originalPaths != null || uris != null) {
            if (paths == null || originalPaths == null || paths.size() == originalPaths.size()) {
                final ArrayList<String> arrayList = paths;
                final ArrayList<String> arrayList2 = originalPaths;
                final String str = mime;
                final long j = dialog_id;
                final MessageObject messageObject = reply_to_msg;
                final ArrayList<Uri> arrayList3 = uris;
                final InputContentInfoCompat inputContentInfoCompat = inputContent;
                new Thread(new Runnable() {

                    class C11881 implements Runnable {
                        C11881() {
                        }

                        public void run() {
                            try {
                                Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("UnsupportedAttachment", R.string.UnsupportedAttachment), 0).show();
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                        }
                    }

                    public void run() {
                        int a;
                        boolean error = false;
                        if (arrayList != null) {
                            for (a = 0; a < arrayList.size(); a++) {
                                if (!SendMessagesHelper.prepareSendingDocumentInternal((String) arrayList.get(a), (String) arrayList2.get(a), null, str, j, messageObject, null)) {
                                    error = true;
                                }
                            }
                        }
                        if (arrayList3 != null) {
                            for (a = 0; a < arrayList3.size(); a++) {
                                if (!SendMessagesHelper.prepareSendingDocumentInternal(null, null, (Uri) arrayList3.get(a), str, j, messageObject, null)) {
                                    error = true;
                                }
                            }
                        }
                        if (inputContentInfoCompat != null) {
                            inputContentInfoCompat.releasePermission();
                        }
                        if (error) {
                            AndroidUtilities.runOnUIThread(new C11881());
                        }
                    }
                }).start();
            }
        }
    }

    public static void prepareSendingPhoto(String imageFilePath, Uri imageUri, long dialog_id, MessageObject reply_to_msg, CharSequence caption, ArrayList<InputDocument> stickers, InputContentInfoCompat inputContent, int ttl) {
        ArrayList<String> paths = null;
        ArrayList<Uri> uris = null;
        ArrayList<String> captions = null;
        ArrayList<Integer> ttls = null;
        ArrayList<ArrayList<InputDocument>> masks = null;
        if (!(imageFilePath == null || imageFilePath.length() == 0)) {
            paths = new ArrayList();
            paths.add(imageFilePath);
        }
        if (imageUri != null) {
            uris = new ArrayList();
            uris.add(imageUri);
        }
        if (ttl != 0) {
            ttls = new ArrayList();
            ttls.add(Integer.valueOf(ttl));
        }
        if (caption != null) {
            captions = new ArrayList();
            captions.add(caption.toString());
        }
        if (!(stickers == null || stickers.isEmpty())) {
            masks = new ArrayList();
            masks.add(new ArrayList(stickers));
        }
        prepareSendingPhotos(paths, uris, dialog_id, reply_to_msg, captions, masks, inputContent, false, ttls);
    }

    public static void prepareSendingBotContextResult(final TLRPC.BotInlineResult result, final HashMap<String, String> params, final long dialog_id, final MessageObject reply_to_msg) {
        if (result == null) {
            return;
        }
        if (result.send_message instanceof TLRPC.TL_botInlineMessageMediaAuto) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String finalPath = null;
                    TLRPC.TL_document document = null;
                    TLRPC.TL_photo photo = null;
                    TLRPC.TL_game game = null;
                    if (result instanceof TLRPC.TL_botInlineMediaResult) {
                        if (result.type.equals("game")) {
                            if ((int) dialog_id == 0) {
                                return; //doesn't work in secret chats for now
                            }
                            game = new TLRPC.TL_game();
                            game.title = result.title;
                            game.description = result.description;
                            game.short_name = result.id;
                            game.photo = result.photo;
                            if (result.document instanceof TLRPC.TL_document) {
                                game.document = result.document;
                                game.flags |= 1;
                            }
                        } else if (result.document != null) {
                            if (result.document instanceof TLRPC.TL_document) {
                                document = (TLRPC.TL_document) result.document;
                            }
                        } else if (result.photo != null) {
                            if (result.photo instanceof TLRPC.TL_photo) {
                                photo = (TLRPC.TL_photo) result.photo;
                            }
                        }
                    } else {
                        if (result.content_url != null) {
                            File f = new File(FileLoader.getInstance().getDirectory(FileLoader.MEDIA_DIR_CACHE), Utilities.MD5(result.content_url) + "." + ImageLoader.getHttpUrlExtension(result.content_url, "file"));
                            if (f.exists()) {
                                finalPath = f.getAbsolutePath();
                            } else {
                                finalPath = result.content_url;
                            }
                            switch (result.type) {
                                case "audio":
                                case "voice":
                                case "file":
                                case "video":
                                case "sticker":
                                case "gif": {
                                    document = new TLRPC.TL_document();
                                    document.id = 0;
                                    document.size = 0;
                                    document.dc_id = 0;
                                    document.mime_type = result.content_type;
                                    document.date = ConnectionsManager.getInstance().getCurrentTime();
                                    TLRPC.TL_documentAttributeFilename fileName = new TLRPC.TL_documentAttributeFilename();
                                    document.attributes.add(fileName);

                                    switch (result.type) {
                                        case "gif": {
                                            fileName.file_name = "animation.gif";
                                            if (finalPath.endsWith("mp4")) {
                                                document.mime_type = "video/mp4";
                                                document.attributes.add(new TLRPC.TL_documentAttributeAnimated());
                                            } else {
                                                document.mime_type = "image/gif";
                                            }
                                            try {
                                                Bitmap bitmap;
                                                if (finalPath.endsWith("mp4")) {
                                                    bitmap = ThumbnailUtils.createVideoThumbnail(finalPath, MediaStore.Video.Thumbnails.MINI_KIND);
                                                } else {
                                                    bitmap = ImageLoader.loadBitmap(finalPath, null, 90, 90, true);
                                                }
                                                if (bitmap != null) {
                                                    document.thumb = ImageLoader.scaleAndSaveImage(bitmap, 90, 90, 55, false);
                                                    bitmap.recycle();
                                                }
                                            } catch (Throwable e) {
                                                FileLog.e(e);
                                            }
                                            break;
                                        }
                                        case "voice": {
                                            TLRPC.TL_documentAttributeAudio audio = new TLRPC.TL_documentAttributeAudio();
                                            audio.duration = result.duration;
                                            audio.voice = true;
                                            fileName.file_name = "audio.ogg";
                                            document.attributes.add(audio);

                                            document.thumb = new TLRPC.TL_photoSizeEmpty();
                                            document.thumb.type = "s";

                                            break;
                                        }
                                        case "audio": {
                                            TLRPC.TL_documentAttributeAudio audio = new TLRPC.TL_documentAttributeAudio();
                                            audio.duration = result.duration;
                                            audio.title = result.title;
                                            audio.flags |= 1;
                                            if (result.description != null) {
                                                audio.performer = result.description;
                                                audio.flags |= 2;
                                            }
                                            fileName.file_name = "audio.mp3";
                                            document.attributes.add(audio);

                                            document.thumb = new TLRPC.TL_photoSizeEmpty();
                                            document.thumb.type = "s";

                                            break;
                                        }
                                        case "file": {
                                            int idx = result.content_type.indexOf('/');
                                            if (idx != -1) {
                                                fileName.file_name = "file." + result.content_type.substring(idx + 1);
                                            } else {
                                                fileName.file_name = "file";
                                            }
                                            break;
                                        }
                                        case "video": {
                                            fileName.file_name = "video.mp4";
                                            TLRPC.TL_documentAttributeVideo attributeVideo = new TLRPC.TL_documentAttributeVideo();
                                            attributeVideo.w = result.w;
                                            attributeVideo.h = result.h;
                                            attributeVideo.duration = result.duration;
                                            document.attributes.add(attributeVideo);
                                            try {
                                                String thumbPath = new File(FileLoader.getInstance().getDirectory(FileLoader.MEDIA_DIR_CACHE), Utilities.MD5(result.thumb_url) + "." + ImageLoader.getHttpUrlExtension(result.thumb_url, "jpg")).getAbsolutePath();
                                                Bitmap bitmap = ImageLoader.loadBitmap(thumbPath, null, 90, 90, true);
                                                if (bitmap != null) {
                                                    document.thumb = ImageLoader.scaleAndSaveImage(bitmap, 90, 90, 55, false);
                                                    bitmap.recycle();
                                                }
                                            } catch (Throwable e) {
                                                FileLog.e(e);
                                            }
                                            break;
                                        }
                                        case "sticker": {
                                            TLRPC.TL_documentAttributeSticker attributeSticker = new TLRPC.TL_documentAttributeSticker();
                                            attributeSticker.alt = "";
                                            attributeSticker.stickerset = new TLRPC.TL_inputStickerSetEmpty();
                                            document.attributes.add(attributeSticker);
                                            TLRPC.TL_documentAttributeImageSize attributeImageSize = new TLRPC.TL_documentAttributeImageSize();
                                            attributeImageSize.w = result.w;
                                            attributeImageSize.h = result.h;
                                            document.attributes.add(attributeImageSize);
                                            fileName.file_name = "sticker.webp";
                                            try {
                                                String thumbPath = new File(FileLoader.getInstance().getDirectory(FileLoader.MEDIA_DIR_CACHE), Utilities.MD5(result.thumb_url) + "." + ImageLoader.getHttpUrlExtension(result.thumb_url, "webp")).getAbsolutePath();
                                                Bitmap bitmap = ImageLoader.loadBitmap(thumbPath, null, 90, 90, true);
                                                if (bitmap != null) {
                                                    document.thumb = ImageLoader.scaleAndSaveImage(bitmap, 90, 90, 55, false);
                                                    bitmap.recycle();
                                                }
                                            } catch (Throwable e) {
                                                FileLog.e(e);
                                            }
                                            break;
                                        }
                                    }
                                    if (fileName.file_name == null) {
                                        fileName.file_name = "file";
                                    }
                                    if (document.mime_type == null) {
                                        document.mime_type = "application/octet-stream";
                                    }
                                    if (document.thumb == null) {
                                        document.thumb = new TLRPC.TL_photoSize();
                                        document.thumb.w = result.w;
                                        document.thumb.h = result.h;
                                        document.thumb.size = 0;
                                        document.thumb.location = new TLRPC.TL_fileLocationUnavailable();
                                        document.thumb.type = "x";
                                    }
                                    break;
                                }
                                case "photo": {
                                    if (f.exists()) {
                                        photo = SendMessagesHelper.getInstance().generatePhotoSizes(finalPath, null);
                                    }
                                    if (photo == null) {
                                        photo = new TLRPC.TL_photo();
                                        photo.date = ConnectionsManager.getInstance().getCurrentTime();
                                        TLRPC.TL_photoSize photoSize = new TLRPC.TL_photoSize();
                                        photoSize.w = result.w;
                                        photoSize.h = result.h;
                                        photoSize.size = 1;
                                        photoSize.location = new TLRPC.TL_fileLocationUnavailable();
                                        photoSize.type = "x";
                                        photo.sizes.add(photoSize);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    final String finalPathFinal = finalPath;
                    final TLRPC.TL_document finalDocument = document;
                    final TLRPC.TL_photo finalPhoto = photo;
                    final TLRPC.TL_game finalGame = game;
                    if (params != null && result.content_url != null) {
                        params.put("originalPath", result.content_url);
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (finalDocument != null) {
                                finalDocument.caption = result.send_message.caption;
                                SendMessagesHelper.getInstance().sendMessage(finalDocument, null, finalPathFinal, dialog_id, reply_to_msg, result.send_message.reply_markup, params, 0);
                            } else if (finalPhoto != null) {
                                finalPhoto.caption = result.send_message.caption;
                                SendMessagesHelper.getInstance().sendMessage(finalPhoto, result.content_url, dialog_id, reply_to_msg, result.send_message.reply_markup, params, 0);
                            } else if (finalGame != null) {
                                SendMessagesHelper.getInstance().sendMessage(finalGame, dialog_id, result.send_message.reply_markup, params);
                            }
                        }
                    });
                }
            }).run();
        } else if (result.send_message instanceof TLRPC.TL_botInlineMessageText) {
            TLRPC.WebPage webPage = null;
            if ((int) dialog_id == 0) {
                for (int a = 0; a < result.send_message.entities.size(); a++) {
                    TLRPC.MessageEntity entity = result.send_message.entities.get(a);
                    if (entity instanceof TLRPC.TL_messageEntityUrl) {
                        webPage = new TLRPC.TL_webPagePending();
                        webPage.url = result.send_message.message.substring(entity.offset, entity.offset + entity.length);
                        break;
                    }
                }
            }
            SendMessagesHelper.getInstance().sendMessage(result.send_message.message, dialog_id, reply_to_msg, webPage, !result.send_message.no_webpage, result.send_message.entities, result.send_message.reply_markup, params);
        } else if (result.send_message instanceof TLRPC.TL_botInlineMessageMediaVenue) {
            TLRPC.TL_messageMediaVenue venue = new TLRPC.TL_messageMediaVenue();
            venue.geo = result.send_message.geo;
            venue.address = result.send_message.address;
            venue.title = result.send_message.title;
            venue.provider = result.send_message.provider;
            venue.venue_id = result.send_message.venue_id;
            SendMessagesHelper.getInstance().sendMessage(venue, dialog_id, reply_to_msg, result.send_message.reply_markup, params);
        } else if (result.send_message instanceof TLRPC.TL_botInlineMessageMediaGeo) {
            TLRPC.TL_messageMediaGeo location = new TLRPC.TL_messageMediaGeo();
            location.geo = result.send_message.geo;
            SendMessagesHelper.getInstance().sendMessage(location, dialog_id, reply_to_msg, result.send_message.reply_markup, params);
        } else if (result.send_message instanceof TLRPC.TL_botInlineMessageMediaContact) {
            TLRPC.User user = new TLRPC.TL_user();
            user.phone = result.send_message.phone_number;
            user.first_name = result.send_message.first_name;
            user.last_name = result.send_message.last_name;
            SendMessagesHelper.getInstance().sendMessage(user, dialog_id, reply_to_msg, result.send_message.reply_markup, params);
        }
    }
    public static void prepareSendingPhotosSearch(final ArrayList<MediaController.SearchImage> photos, final long dialog_id, final MessageObject reply_to_msg) {
        if (photos == null || photos.isEmpty()) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isEncrypted = (int) dialog_id == 0;
                for (int a = 0; a < photos.size(); a++) {
                    final MediaController.SearchImage searchImage = photos.get(a);
                    final int ttl = searchImage.ttl;
                    if (searchImage.type == 1) {
                        final HashMap<String, String> params = new HashMap<>();
                        TLRPC.TL_document document = null;
                        File cacheFile;
                        if (searchImage.document instanceof TLRPC.TL_document) {
                            document = (TLRPC.TL_document) searchImage.document;
                            cacheFile = FileLoader.getPathToAttach(document, true);
                        } else {
                            if (!isEncrypted) {
                                TLRPC.Document doc = (TLRPC.Document) MessagesStorage.getInstance().getSentFile(searchImage.imageUrl, !isEncrypted ? 1 : 4);
                                if (doc instanceof TLRPC.TL_document) {
                                    document = (TLRPC.TL_document) doc;
                                }
                            }
                            String md5 = Utilities.MD5(searchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(searchImage.imageUrl, "jpg");
                            cacheFile = new File(FileLoader.getInstance().getDirectory(FileLoader.MEDIA_DIR_CACHE), md5);
                        }
                        if (document == null) {
                            if (searchImage.localUrl != null) {
                                params.put("url", searchImage.localUrl);
                            }
                            File thumbFile = null;
                            document = new TLRPC.TL_document();
                            document.id = 0;
                            document.date = ConnectionsManager.getInstance().getCurrentTime();
                            TLRPC.TL_documentAttributeFilename fileName = new TLRPC.TL_documentAttributeFilename();
                            fileName.file_name = "animation.gif";
                            document.attributes.add(fileName);
                            document.size = searchImage.size;
                            document.dc_id = 0;
                            if (cacheFile.toString().endsWith("mp4")) {
                                document.mime_type = "video/mp4";
                                document.attributes.add(new TLRPC.TL_documentAttributeAnimated());
                            } else {
                                document.mime_type = "image/gif";
                            }
                            if (cacheFile.exists()) {
                                thumbFile = cacheFile;
                            } else {
                                cacheFile = null;
                            }
                            if (thumbFile == null) {
                                String thumb = Utilities.MD5(searchImage.thumbUrl) + "." + ImageLoader.getHttpUrlExtension(searchImage.thumbUrl, "jpg");
                                thumbFile = new File(FileLoader.getInstance().getDirectory(FileLoader.MEDIA_DIR_CACHE), thumb);
                                if (!thumbFile.exists()) {
                                    thumbFile = null;
                                }
                            }
                            if (thumbFile != null) {
                                try {
                                    Bitmap bitmap;
                                    if (thumbFile.getAbsolutePath().endsWith("mp4")) {
                                        bitmap = ThumbnailUtils.createVideoThumbnail(thumbFile.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                                    } else {
                                        bitmap = ImageLoader.loadBitmap(thumbFile.getAbsolutePath(), null, 90, 90, true);
                                    }
                                    if (bitmap != null) {
                                        document.thumb = ImageLoader.scaleAndSaveImage(bitmap, 90, 90, 55, isEncrypted);
                                        bitmap.recycle();
                                    }
                                } catch (Exception e) {
                                    FileLog.e(e);
                                }
                            }
                            if (document.thumb == null) {
                                document.thumb = new TLRPC.TL_photoSize();
                                document.thumb.w = searchImage.width;
                                document.thumb.h = searchImage.height;
                                document.thumb.size = 0;
                                document.thumb.location = new TLRPC.TL_fileLocationUnavailable();
                                document.thumb.type = "x";
                            }
                        }

                        if (searchImage.caption != null) {
                            document.caption = searchImage.caption.toString();
                        }
                        final TLRPC.TL_document documentFinal = document;
                        final String originalPathFinal = searchImage.imageUrl;
                        final String pathFinal = cacheFile == null ? searchImage.imageUrl : cacheFile.toString();
                        if (params != null && searchImage.imageUrl != null) {
                            params.put("originalPath", searchImage.imageUrl);
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                SendMessagesHelper.getInstance().sendMessage(documentFinal, null, pathFinal, dialog_id, reply_to_msg, null, params, 0);
                            }
                        });
                    } else {
                        boolean needDownloadHttp = true;
                        TLRPC.TL_photo photo = null;
                        if (!isEncrypted && ttl == 0) {
                            photo = (TLRPC.TL_photo) MessagesStorage.getInstance().getSentFile(searchImage.imageUrl, !isEncrypted ? 0 : 3);
                        }
                        if (photo == null) {
                            String md5 = Utilities.MD5(searchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(searchImage.imageUrl, "jpg");
                            File cacheFile = new File(FileLoader.getInstance().getDirectory(FileLoader.MEDIA_DIR_CACHE), md5);
                            if (cacheFile.exists() && cacheFile.length() != 0) {
                                photo = SendMessagesHelper.getInstance().generatePhotoSizes(cacheFile.toString(), null);
                                if (photo != null) {
                                    needDownloadHttp = false;
                                }
                            }
                            if (photo == null) {
                                md5 = Utilities.MD5(searchImage.thumbUrl) + "." + ImageLoader.getHttpUrlExtension(searchImage.thumbUrl, "jpg");
                                cacheFile = new File(FileLoader.getInstance().getDirectory(FileLoader.MEDIA_DIR_CACHE), md5);
                                if (cacheFile.exists()) {
                                    photo = SendMessagesHelper.getInstance().generatePhotoSizes(cacheFile.toString(), null);
                                }
                                if (photo == null) {
                                    photo = new TLRPC.TL_photo();
                                    photo.date = ConnectionsManager.getInstance().getCurrentTime();
                                    TLRPC.TL_photoSize photoSize = new TLRPC.TL_photoSize();
                                    photoSize.w = searchImage.width;
                                    photoSize.h = searchImage.height;
                                    photoSize.size = 0;
                                    photoSize.location = new TLRPC.TL_fileLocationUnavailable();
                                    photoSize.type = "x";
                                    photo.sizes.add(photoSize);
                                }
                            }
                        }
                        if (photo != null) {
                            if (searchImage.caption != null) {
                                photo.caption = searchImage.caption.toString();
                            }
                            final TLRPC.TL_photo photoFinal = photo;
                            final boolean needDownloadHttpFinal = needDownloadHttp;
                            final HashMap<String, String> params = new HashMap<>();
                            if (searchImage.imageUrl != null) {
                                params.put("originalPath", searchImage.imageUrl);
                            }
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    SendMessagesHelper.getInstance().sendMessage(photoFinal, needDownloadHttpFinal ? searchImage.imageUrl : null, dialog_id, reply_to_msg, null, params, ttl);
                                }
                            });
                        }
                    }
                }
            }
        }).start();
    }
    private static String getTrimmedString(String src) {
        String result = src.trim();
        if (result.length() == 0) {
            return result;
        }
        while (src.startsWith(IOUtils.LINE_SEPARATOR_UNIX)) {
            src = src.substring(1);
        }
        while (src.endsWith(IOUtils.LINE_SEPARATOR_UNIX)) {
            src = src.substring(0, src.length() - 1);
        }
        return src;
    }

    public static void prepareSendingText(final String text, final long dialog_id) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {

            class C11931 implements Runnable {

                class C11921 implements Runnable {
                    C11921() {
                    }

                    public void run() {
                        String textFinal = SendMessagesHelper.getTrimmedString(text);
                        if (textFinal.length() != 0) {
                            int count = (int) Math.ceil((double) (((float) textFinal.length()) / 4096.0f));
                            for (int a = 0; a < count; a++) {
                                SendMessagesHelper.getInstance().sendMessage(textFinal.substring(a * 4096, Math.min((a + 1) * 4096, textFinal.length())), dialog_id, null, null, true, null, null, null);
                            }
                        }
                    }
                }

                C11931() {
                }

                public void run() {
                    AndroidUtilities.runOnUIThread(new C11921());
                }
            }

            public void run() {
                Utilities.stageQueue.postRunnable(new C11931());
            }
        });
    }

    public static void prepareSendingPhotos(ArrayList<String> paths, ArrayList<Uri> uris, long dialog_id, MessageObject reply_to_msg, ArrayList<String> captions, ArrayList<ArrayList<InputDocument>> masks, InputContentInfoCompat inputContent, boolean forceDocument, ArrayList<Integer> ttls) {
        if (paths != null || uris != null) {
            if (paths != null && paths.isEmpty()) {
                return;
            }
            if (uris == null || !uris.isEmpty()) {
                final ArrayList<String> pathsCopy = new ArrayList();
                final ArrayList<Uri> urisCopy = new ArrayList();
                if (paths != null) {
                    pathsCopy.addAll(paths);
                }
                if (uris != null) {
                    urisCopy.addAll(uris);
                }
                final long j = dialog_id;
                final ArrayList<Integer> arrayList = ttls;
                final boolean z = forceDocument;
                final ArrayList<String> arrayList2 = captions;
                final ArrayList<ArrayList<InputDocument>> arrayList3 = masks;
                final MessageObject messageObject = reply_to_msg;
                final InputContentInfoCompat inputContentInfoCompat = inputContent;
                new Thread(new Runnable() {
                    /* JADX WARNING: inconsistent code. */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void run() {
                        int a;
                        boolean isEncrypted = ((int) j) == 0;
                        ArrayList<String> sendAsDocuments = null;
                        ArrayList<String> sendAsDocumentsOriginal = null;
                        ArrayList<String> sendAsDocumentsCaptions = null;
                        int count = !pathsCopy.isEmpty() ? pathsCopy.size() : urisCopy.size();
                        String path = null;
                        Uri uri = null;
                        String extension = null;
                        for (a = 0; a < count; a++) {
                            if (!pathsCopy.isEmpty()) {
                                path = (String) pathsCopy.get(a);
                            } else if (!urisCopy.isEmpty()) {
                                uri = (Uri) urisCopy.get(a);
                            }
                            String originalPath = path;
                            String tempPath = path;
                            if (tempPath == null && uri != null) {
                                tempPath = AndroidUtilities.getPath(uri);
                                originalPath = uri.toString();
                            }
                            int ttl = arrayList != null ? ((Integer) arrayList.get(a)).intValue() : 0;
                            boolean isDocument = false;
                            if (z) {
                                isDocument = true;
                                extension = FileLoader.getFileExtension(new File(tempPath));
                            } else {
                                if (tempPath != null) {
                                    if (!tempPath.endsWith(".gif")) {
                                    }
                                    if (tempPath.endsWith(".gif")) {
                                        extension = "gif";
                                    } else {
                                        extension = "webp";
                                    }
                                    isDocument = true;
                                }
                                if (tempPath == null && uri != null) {
                                    if (MediaController.isGif(uri)) {
                                        isDocument = true;
                                        originalPath = uri.toString();
                                        tempPath = MediaController.copyFileToCache(uri, "gif");
                                        extension = "gif";
                                    } else if (MediaController.isWebp(uri)) {
                                        isDocument = true;
                                        originalPath = uri.toString();
                                        tempPath = MediaController.copyFileToCache(uri, "webp");
                                        extension = "webp";
                                    }
                                }
                            }
                            if (isDocument) {
                                Object obj;
                                if (sendAsDocuments == null) {
                                    sendAsDocuments = new ArrayList();
                                    sendAsDocumentsOriginal = new ArrayList();
                                    sendAsDocumentsCaptions = new ArrayList();
                                }
                                sendAsDocuments.add(tempPath);
                                sendAsDocumentsOriginal.add(originalPath);
                                if (arrayList2 != null) {
                                    obj = (String) arrayList2.get(a);
                                } else {
                                    obj = null;
                                }
                                sendAsDocumentsCaptions.add((String) obj);
                            } else {
                                if (tempPath != null) {
                                    File temp = new File(tempPath);
                                    originalPath = originalPath + temp.length() + "_" + temp.lastModified();
                                } else {
                                    originalPath = null;
                                }
                                TL_photo photo = null;
                                if (!isEncrypted && ttl == 0) {
                                    photo = (TL_photo) MessagesStorage.getInstance().getSentFile(originalPath, !isEncrypted ? 0 : 3);
                                    if (photo == null && uri != null) {
                                        photo = (TL_photo) MessagesStorage.getInstance().getSentFile(AndroidUtilities.getPath(uri), !isEncrypted ? 0 : 3);
                                    }
                                }
                                if (photo == null) {
                                    photo = SendMessagesHelper.getInstance().generatePhotoSizes(path, uri);
                                }
                                if (photo != null) {
                                    TL_photo photoFinal = photo;
                                    HashMap<String, String> params = new HashMap();
                                    if (arrayList2 != null) {
                                        photo.caption = (String) arrayList2.get(a);
                                    }
                                    if (arrayList3 != null) {
                                        ArrayList<InputDocument> arrayList = (ArrayList) arrayList3.get(a);
                                        boolean z = (arrayList == null || arrayList.isEmpty()) ? false : true;
                                        photo.has_stickers = z;
                                        if (z) {
                                            AbstractSerializedData serializedData = new SerializedData((arrayList.size() * 20) + 4);
                                            serializedData.writeInt32(arrayList.size());
                                            for (int b = 0; b < arrayList.size(); b++) {
                                                ((InputDocument) arrayList.get(b)).serializeToStream(serializedData);
                                            }
                                            //params.put("masks", Utilities.bytesToHex(serializedData.toByteArray()));
                                        }
                                    }
                                    if (originalPath != null) {
                                        params.put("originalPath", originalPath);
                                    }
                                    final TL_photo tL_photo = photoFinal;
                                    final HashMap<String, String> hashMap = params;
                                    final int i = ttl;
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            SendMessagesHelper.getInstance().sendMessage(tL_photo, null, j, messageObject, null, hashMap, i);
                                        }
                                    });
                                } else {
                                    if (sendAsDocuments == null) {
                                        sendAsDocuments = new ArrayList();
                                        sendAsDocumentsOriginal = new ArrayList();
                                        sendAsDocumentsCaptions = new ArrayList();
                                    }
                                    sendAsDocuments.add(tempPath);
                                    sendAsDocumentsOriginal.add(originalPath);
                                    sendAsDocumentsCaptions.add(arrayList2 != null ? (String) arrayList2.get(a) : null);
                                }
                            }
                        }
                        if (inputContentInfoCompat != null) {
                            inputContentInfoCompat.releasePermission();
                        }
                        if (sendAsDocuments != null && !sendAsDocuments.isEmpty()) {
                            for (a = 0; a < sendAsDocuments.size(); a++) {
                                SendMessagesHelper.prepareSendingDocumentInternal((String) sendAsDocuments.get(a), (String) sendAsDocumentsOriginal.get(a), null, extension, j, messageObject, (CharSequence) sendAsDocumentsCaptions.get(a));
                            }
                        }
                    }
                }).start();
            }
        }
    }

    private static void fillVideoAttribute(String videoPath, TLRPC.TL_documentAttributeVideo attributeVideo, VideoEditedInfo videoEditedInfo) {
        boolean infoObtained = false;

        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(videoPath);
            String width = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            if (width != null) {
                attributeVideo.w = Integer.parseInt(width);
            }
            String height = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            if (height != null) {
                attributeVideo.h = Integer.parseInt(height);
            }
            String duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (duration != null) {
                attributeVideo.duration = (int) Math.ceil(Long.parseLong(duration) / 1000.0f);
            }
            if (Build.VERSION.SDK_INT >= 17) {
                String rotation = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
                if (rotation != null) {
                    int val = Utilities.parseInt(rotation);
                    if (videoEditedInfo != null) {
                        videoEditedInfo.rotationValue = val;
                    } else if (val == 90 || val == 270) {
                        int temp = attributeVideo.w;
                        attributeVideo.w = attributeVideo.h;
                        attributeVideo.h = temp;
                    }
                }
            }
            infoObtained = true;
        } catch (Exception e) {
            FileLog.e(e);
        } finally {
            try {
                if (mediaMetadataRetriever != null) {
                    mediaMetadataRetriever.release();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        if (!infoObtained) {
            try {
                MediaPlayer mp = MediaPlayer.create(ApplicationLoader.applicationContext, Uri.fromFile(new File(videoPath)));
                if (mp != null) {
                    attributeVideo.duration = (int) Math.ceil(mp.getDuration() / 1000.0f);
                    attributeVideo.w = mp.getVideoWidth();
                    attributeVideo.h = mp.getVideoHeight();
                    mp.release();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }
    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static Bitmap createVideoThumbnail(String filePath, long time) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(time, 1);
            try {
                retriever.release();
            } catch (RuntimeException e) {
            }
        } catch (Exception e2) {
        } catch (Throwable th) {
            try {
                retriever.release();
            } catch (RuntimeException e3) {
            }
        }
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int max = Math.max(width, height);
        if (max > 90) {
            float scale = 90.0f / ((float) max);
            bitmap = Bitmaps.createScaledBitmap(bitmap, Math.round(((float) width) * scale), Math.round(((float) height) * scale), true);
        }
        return bitmap;
    }

    public static void prepareSendingVideo(String videoPath, long estimatedSize, long duration, int width, int height, VideoEditedInfo videoEditedInfo, long dialog_id, MessageObject reply_to_msg, CharSequence caption, int ttl) {
        if (videoPath != null && videoPath.length() != 0) {
            final long j = dialog_id;
            final VideoEditedInfo videoEditedInfo2 = videoEditedInfo;
            final String str = videoPath;
            final long j2 = duration;
            final int i = ttl;
            final int i2 = height;
            final int i3 = width;
            final long j3 = estimatedSize;
            final CharSequence charSequence = caption;
            final MessageObject messageObject = reply_to_msg;
            new Thread(new Runnable() {
                public void run() {
                    boolean isEncrypted = ((int) j) == 0;
                    boolean isRound = videoEditedInfo2 != null && videoEditedInfo2.roundVideo;
                    Bitmap thumb = null;
                    String thumbKey = null;
                    if (videoEditedInfo2 != null || str.endsWith("mp4") || isRound) {
                        String path = str;
                        String originalPath = str;
                        File file = new File(originalPath);
                        long startTime = 0;
                        originalPath = originalPath + file.length() + "_" + file.lastModified();
                        if (videoEditedInfo2 != null) {
                            if (!isRound) {
                                originalPath = originalPath + j2 + "_" + videoEditedInfo2.startTime + "_" + videoEditedInfo2.endTime;
                                if (videoEditedInfo2.resultWidth == videoEditedInfo2.originalWidth) {
                                    originalPath = originalPath + "_" + videoEditedInfo2.resultWidth;
                                }
                            }
                            startTime = videoEditedInfo2.startTime >= 0 ? videoEditedInfo2.startTime : 0;
                        }
                        TL_document tL_document = null;
                        if (!isEncrypted && i == 0) {
                            tL_document = (TL_document) MessagesStorage.getInstance().getSentFile(originalPath, !isEncrypted ? 2 : 5);
                        }
                        if (tL_document == null) {
                            TL_documentAttributeVideo attributeVideo;
                            thumb = SendMessagesHelper.createVideoThumbnail(str, startTime);
                            if (thumb == null) {
                                thumb = ThumbnailUtils.createVideoThumbnail(str, 1);
                            }
                            PhotoSize size = ImageLoader.scaleAndSaveImage(thumb, 90.0f, 90.0f, 55, isEncrypted);
                            if (!(thumb == null || size == null)) {
                                if (!isRound) {
                                    thumb = null;
                                } else if (isEncrypted) {
                                    Utilities.blurBitmap(thumb, 7, VERSION.SDK_INT < 21 ? 0 : 1, thumb.getWidth(), thumb.getHeight(), thumb.getRowBytes());
                                    thumbKey = String.format(size.location.volume_id + "_" + size.location.local_id + "@%d_%d_b2", new Object[]{Integer.valueOf((int) (((float) AndroidUtilities.roundMessageSize) / AndroidUtilities.density)), Integer.valueOf((int) (((float) AndroidUtilities.roundMessageSize) / AndroidUtilities.density))});
                                } else {
                                    Utilities.blurBitmap(thumb, 3, VERSION.SDK_INT < 21 ? 0 : 1, thumb.getWidth(), thumb.getHeight(), thumb.getRowBytes());
                                    thumbKey = String.format(size.location.volume_id + "_" + size.location.local_id + "@%d_%d_b", new Object[]{Integer.valueOf((int) (((float) AndroidUtilities.roundMessageSize) / AndroidUtilities.density)), Integer.valueOf((int) (((float) AndroidUtilities.roundMessageSize) / AndroidUtilities.density))});
                                }
                            }
                            tL_document = new TL_document();
                            tL_document.thumb = size;
                            if (tL_document.thumb == null) {
                                tL_document.thumb = new TL_photoSizeEmpty();
                                tL_document.thumb.type = "s";
                            } else {
                                tL_document.thumb.type = "s";
                            }
                            tL_document.mime_type = MimeTypes.VIDEO_MP4;
                            UserConfig.saveConfig(false);
                            if (isEncrypted) {
                                EncryptedChat encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (j >> 32)));
                                if (encryptedChat != null) {
                                    if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 66) {
                                        attributeVideo = new TL_documentAttributeVideo();
                                    } else {
                                        attributeVideo = new TL_documentAttributeVideo_layer65();
                                    }
                                } else {
                                    return;
                                }
                            }
                            attributeVideo = new TL_documentAttributeVideo();
                            attributeVideo.round_message = isRound;
                            tL_document.attributes.add(attributeVideo);
                            if (videoEditedInfo2 == null || !videoEditedInfo2.needConvert()) {
                                if (file.exists()) {
                                    tL_document.size = (int) file.length();
                                }
                                SendMessagesHelper.fillVideoAttribute(str, attributeVideo, null);
                            } else {
                                if (videoEditedInfo2.muted) {
                                    tL_document.attributes.add(new TL_documentAttributeAnimated());
                                    SendMessagesHelper.fillVideoAttribute(str, attributeVideo, videoEditedInfo2);
                                    videoEditedInfo2.originalWidth = attributeVideo.w;
                                    videoEditedInfo2.originalHeight = attributeVideo.h;
                                    attributeVideo.w = videoEditedInfo2.resultWidth;
                                    attributeVideo.h = videoEditedInfo2.resultHeight;
                                } else {
                                    attributeVideo.duration = (int) (j2 / 1000);
                                    if (videoEditedInfo2.rotationValue == 90 || videoEditedInfo2.rotationValue == 270) {
                                        attributeVideo.w = i2;
                                        attributeVideo.h = i3;
                                    } else {
                                        attributeVideo.w = i3;
                                        attributeVideo.h = i2;
                                    }
                                }
                                tL_document.size = (int) j3;
                                String fileName = "-2147483648_" + UserConfig.lastLocalId + ".mp4";
                                UserConfig.lastLocalId--;
                                file = new File(FileLoader.getInstance().getDirectory(4), fileName);
                                UserConfig.saveConfig(false);
                                path = file.getAbsolutePath();
                            }
                        }
                        final TL_document videoFinal = tL_document;
                        String originalPathFinal = originalPath;
                        final String finalPath = path;
                        final HashMap<String, String> params = new HashMap();
                        final Bitmap thumbFinal = thumb;
                        final String thumbKeyFinal = thumbKey;
                        if (charSequence != null) {
                            videoFinal.caption = charSequence.toString();
                        } else {
                            videoFinal.caption = "";
                        }
                        if (originalPath != null) {
                            params.put("originalPath", originalPath);
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (!(thumbFinal == null || thumbKeyFinal == null)) {
                                    ImageLoader.getInstance().putImageToCache(new BitmapDrawable(thumbFinal), thumbKeyFinal);
                                }
                                SendMessagesHelper.getInstance().sendMessage(videoFinal, videoEditedInfo2, finalPath, j, messageObject, null, params, i);
                            }
                        });
                        return;
                    }
                    SendMessagesHelper.prepareSendingDocumentInternal(str, str, null, null, j, messageObject, charSequence);
                }
            }).start();
        }
    }
}
