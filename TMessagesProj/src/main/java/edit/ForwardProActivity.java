package edit;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils.TruncateAt;
import android.text.style.CharacterStyle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import org.zgram.messenger.R;
import java.util.Calendar;
import org.zgram.messenger.AndroidUtilities;
import org.zgram.messenger.ApplicationLoader;
import org.zgram.messenger.ChatObject;
import org.zgram.messenger.FileLog;
import org.zgram.messenger.LocaleController;
import org.zgram.messenger.MessageObject;
import org.zgram.tgnet.TLRPC.Chat;
import org.zgram.tgnet.TLRPC.KeyboardButton;
import org.zgram.tgnet.TLRPC.Message;
import org.zgram.tgnet.TLRPC.MessageMedia;
import org.zgram.tgnet.TLRPC.TL_message;
import org.zgram.tgnet.TLRPC.TL_messageMediaAudio_layer45;
import org.zgram.tgnet.TLRPC.TL_messageMediaContact;
import org.zgram.tgnet.TLRPC.TL_messageMediaDocument;
import org.zgram.tgnet.TLRPC.TL_messageMediaDocument_old;
import org.zgram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.zgram.tgnet.TLRPC.TL_messageMediaGeo;
import org.zgram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.zgram.tgnet.TLRPC.TL_messageMediaPhoto_old;
import org.zgram.tgnet.TLRPC.TL_messageMediaUnsupported;
import org.zgram.tgnet.TLRPC.TL_messageMediaUnsupported_old;
import org.zgram.tgnet.TLRPC.TL_messageMediaVenue;
import org.zgram.tgnet.TLRPC.TL_messageMediaVideo_layer45;
import org.zgram.tgnet.TLRPC.TL_messageMediaVideo_old;
import org.zgram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.zgram.tgnet.TLRPC.TL_message_secret;
import org.zgram.tgnet.TLRPC.User;
import org.zgram.ui.ActionBar.ActionBar;
import org.zgram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.zgram.ui.ActionBar.BaseFragment;
import org.zgram.ui.ActionBar.Theme;
import org.zgram.ui.Cells.ChatMessageCell;
import org.zgram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate;
import org.zgram.ui.Components.ChatActivityEnterView;
import org.zgram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate;
import org.zgram.ui.Components.LayoutHelper;
import org.zgram.ui.Components.ShareAlert;
import org.zgram.ui.Components.SizeNotifierFrameLayout;

public class ForwardProActivity extends BaseFragment {
    private static final int id_chat_compose_panel = 1000;
    private String caption = "";
    protected ChatActivityEnterView chatActivityEnterView;
    protected Chat currentChat;
    private FrameLayout emptyViewContainer;
    private Bitmap imaage = null;
    private TextView media;
    private TextView mediaCaption;
    int mode = 0;
    private MessageObject selectedObject;
    long timer = 0;

    class C05241 extends ActionBarMenuOnItemClick {
        C05241() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ForwardProActivity.this.finishFragment();
            }
        }
    }

    class C05263 implements ChatMessageCellDelegate {
        C05263() {
        }

        public void didPressedUserAvatar(ChatMessageCell cell, User user) {
        }

        public void didPressedViaBot(ChatMessageCell cell, String username) {
        }

        public void didPressedChannelAvatar(ChatMessageCell cell, Chat chat, int postId) {
        }

        public void didPressedCancelSendButton(ChatMessageCell cell) {
        }

        public void didLongPressed(ChatMessageCell cell) {
        }

        public void didPressedReplyMessage(ChatMessageCell cell, int id) {
        }

        public void didPressedUrl(MessageObject messageObject, CharacterStyle url, boolean longPress) {
        }

        public void needOpenWebView(String url, String title, String description, String originalUrl, int w, int h) {
        }

        public void didPressedImage(ChatMessageCell cell) {
        }

        public void didPressedShare(ChatMessageCell cell) {
        }

        public void didPressedOther(ChatMessageCell cell) {
        }

        public void didPressedBotButton(ChatMessageCell cell, KeyboardButton button) {
        }

        public void didPressedInstantButton(ChatMessageCell cell, int type) {
        }

        public boolean needPlayMessage(MessageObject messageObject) {
            return false;
        }

        @Override
        public void didPressedInstantButton(ChatMessageCell cell) {
            
        }

        @Override
        public boolean needPlayAudio(MessageObject messageObject) {
            return false;
        }

        public boolean canPerformActions() {
            return false;
        }

        public void didPressedTimer(ChatMessageCell chatMessageCell) {
        }
    }

    class C05274 implements OnTouchListener {
        C05274() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    class C05285 implements ChatActivityEnterViewDelegate {
        C05285() {
        }

        public void onMessageSend(CharSequence message) {
        }

        public void needSendTyping() {
        }

        public void onTextChanged(CharSequence text, boolean bigChange) {
        }

        public void onAttachButtonHidden() {
        }

        public void onAttachButtonShow() {
        }

        public void onWindowSizeChanged(int size) {
        }

        public void onStickersTab(boolean opened) {
        }

        public void onMessageEditEnd(boolean loading) {
        }

        public void didPressedAttachButton() {
        }

        public void didPressedPaintButton() {
        }

        public void needStartRecordVideo(int state) {
        }

        public void needChangeVideoPreviewState(int state, float seekProgress) {
        }

        public void onSwitchRecordMode(boolean video) {
        }

        public void onPreAudioVideoRecord() {
        }

        public void needStartRecordAudio(int state) {
        }

        public void needShowMediaBanHint() {
        }
    }

    class C05307 implements OnTimeSetListener {
        C05307() {
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();
            calSet.set(11, hourOfDay);
            calSet.set(12, minute);
            calSet.set(13, 0);
            calSet.set(14, 0);
            if (calSet.compareTo(calNow) <= 0) {
                calSet.add(5, 1);
            }
            ForwardProActivity.this.timer = calSet.getTimeInMillis();
            ForwardProActivity.this.editDone();
        }
    }

    public ForwardProActivity(MessageObject selectedObject, Chat currentChat) {
        this.selectedObject = new MessageObject(newMessage(selectedObject.messageOwner), null, true);
        this.selectedObject.photoThumbs = selectedObject.photoThumbs;
        this.currentChat = currentChat;
    }

    public ForwardProActivity(MessageObject selectedObject, Chat currentChat, int i) {
        this.selectedObject = new MessageObject(newMessage(selectedObject.messageOwner), null, true);
        this.selectedObject.photoThumbs = selectedObject.photoThumbs;
        this.currentChat = currentChat;
        this.mode = i;
    }

    public View createView(Context context) {
        CharSequence string;
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
        this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR, false);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        ActionBar actionBar = this.actionBar;
        if (this.mode == 1) {
            string = LocaleController.getString("ProForward", R.string.ProForward);
        } else {
            string = LocaleController.getString("TimedForward", R.string.TimedForward);
        }
        actionBar.setTitle(string);
        this.actionBar.setActionBarMenuOnItemClick(new C05241());
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setLayoutParams(new LayoutParams(-1, -1));
        MessageObject myObject = this.selectedObject;
        if (myObject.caption != null) {
            this.caption = myObject.caption.toString();
        } else if (myObject.messageText != null) {
            this.caption = myObject.messageText.toString();
        }
        this.fragmentView = new SizeNotifierFrameLayout(context) {
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (child == ForwardProActivity.this.actionBar) {
                    ForwardProActivity.this.parentLayout.drawHeaderShadow(canvas, ForwardProActivity.this.actionBar.getMeasuredHeight());
                }
                return result;
            }

            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int emojiPadding;
                int size = MeasureSpec.getSize(widthMeasureSpec);
                int size2 = MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(size, size2);
                size2 -= getPaddingTop();
                if (getKeyboardHeight() <= AndroidUtilities.dp(20.0f)) {
                    emojiPadding = size2 - ForwardProActivity.this.chatActivityEnterView.getEmojiPadding();
                } else {
                    emojiPadding = size2;
                }
                int childCount = getChildCount();
                measureChildWithMargins(ForwardProActivity.this.chatActivityEnterView, widthMeasureSpec, 0, heightMeasureSpec, 0);
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt = getChildAt(i3);
                    if (!(childAt == null || childAt.getVisibility() == 8 || childAt == ForwardProActivity.this.chatActivityEnterView)) {
                        if (childAt == this) {
                            try {
                                childAt.measure(MeasureSpec.makeMeasureSpec(size, 1073741824), MeasureSpec.makeMeasureSpec(emojiPadding, 1073741824));
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                        } else if (ForwardProActivity.this.chatActivityEnterView.isPopupView(childAt)) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, 1073741824), MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, 1073741824));
                        } else {
                            measureChildWithMargins(childAt, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        }
                    }
                }
            }

            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                int childCount = getChildCount();
                int emojiPadding = getKeyboardHeight() <= AndroidUtilities.dp(20.0f) ? ForwardProActivity.this.chatActivityEnterView.getEmojiPadding() : 0;
                setBottomClip(emojiPadding);
                for (int i5 = 0; i5 < childCount; i5++) {
                    View childAt = getChildAt(i5);
                    if (childAt.getVisibility() != 8) {
                        int i6;
                        LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                        int measuredWidth = childAt.getMeasuredWidth();
                        int measuredHeight = childAt.getMeasuredHeight();
                        int i7 = layoutParams.gravity;
                        if (i7 == -1) {
                            i7 = 51;
                        }
                        int i8 = i7 & 112;
                        int i = (i7 & 7) & 7;
                        i7 = layoutParams.leftMargin;
                        switch (i8) {
                            case 16:
                                i6 = (((((b - emojiPadding) - t) - measuredHeight) / 2) + layoutParams.topMargin) - layoutParams.bottomMargin;
                                break;
                            case 48:
                                i6 = layoutParams.topMargin + getPaddingTop();
                                break;
                            case 80:
                                i6 = (((b - emojiPadding) - t) - measuredHeight) - layoutParams.bottomMargin;
                                break;
                            default:
                                i6 = layoutParams.topMargin;
                                break;
                        }
                        if (ForwardProActivity.this.chatActivityEnterView.isPopupView(childAt)) {
                            i6 = ForwardProActivity.this.fragmentView.getMeasuredHeight() - ForwardProActivity.this.chatActivityEnterView.getEmojiView().getMeasuredHeight();
                        }
                        childAt.layout(i7, i6, measuredWidth + i7, measuredHeight + i6);
                    }
                }
                notifyHeightChanged();
            }
        };
        SizeNotifierFrameLayout contentView = (SizeNotifierFrameLayout) this.fragmentView;
        contentView.setBackgroundImage(Theme.getCachedWallpaper());
        ScrollView scrollView = new ScrollView(context);
        LinearLayout l = new LinearLayout(context);
        l.setOrientation(1);
        l.setGravity(48);
        l.setPadding(0, 80, 0, 250);
        ChatMessageCell chatMessageCell = new ChatMessageCell(getParentActivity());
        MessageObject temp = this.selectedObject;
        if (temp.messageOwner != null) {
            temp.messageOwner.message = "";
            if (temp.messageOwner.media != null) {
                temp.messageOwner.media.caption = "";
            }
        }
        temp.caption = "";
        temp.messageText = "";
        chatMessageCell.setMessageObject(temp, false, false);
        chatMessageCell.setOnClickListener(null);
        chatMessageCell.setOnTouchListener(null);
        chatMessageCell.setOnLongClickListener(null);
        chatMessageCell.setDelegate(new C05263());
        l.addView(chatMessageCell, LayoutHelper.createFrame(-2, -2.0f, 48, 0.0f, 0.0f, 0.0f, 15.0f));
        this.emptyViewContainer = new FrameLayout(context);
        this.emptyViewContainer.setBackgroundColor(-1);
        this.mediaCaption = new TextView(context);
        this.mediaCaption.setTextSize(2, 15.0f);
        this.mediaCaption.setPadding(12, 0, 12, 0);
        this.mediaCaption.setSingleLine(true);
        this.mediaCaption.setEllipsize(TruncateAt.END);
        this.mediaCaption.setMaxLines(1);
        this.mediaCaption.setGravity(16);
        this.mediaCaption.setBackgroundColor(-328966);
        this.mediaCaption.setText(LocaleController.getString("MediaCaption", R.string.MediaCaption) + " : ");
        scrollView.addView(l);
        scrollView.setPadding(0, 80, 0, 250);
        contentView.addView(scrollView, contentView.getChildCount() - 1, LayoutHelper.createFrame(-1, -2, 83));
        this.emptyViewContainer = new FrameLayout(context);
        this.emptyViewContainer.setVisibility(4);
        contentView.addView(this.emptyViewContainer, LayoutHelper.createFrame(-1, -2, 17));
        this.emptyViewContainer.setOnTouchListener(new C05274());
        this.chatActivityEnterView = new ChatActivityEnterView(getParentActivity(), contentView, null, false);
        this.chatActivityEnterView.setDialogId(this.selectedObject.getDialogId());
        LinearLayout l2 = new LinearLayout(context);
        l2.setOrientation(1);
        l2.addView(this.mediaCaption, LayoutHelper.createFrame(-1, 34.0f, (LocaleController.isRTL ? 5 : 3) | 48, 0.0f, 0.0f, 0.0f, 0.0f));
        l2.addView(this.chatActivityEnterView, LayoutHelper.createFrame(-1, -2, 83));
        contentView.addView(l2, LayoutHelper.createFrame(-1, -2, 80));
        this.emptyViewContainer = new FrameLayout(context);
        this.emptyViewContainer.setBackgroundColor(-1);
        this.media = new TextView(context);
        this.media.setTextSize(2, 17.0f);
        this.media.setPadding(12, 0, 12, 0);
        this.media.setSingleLine(true);
        this.media.setEllipsize(TruncateAt.END);
        this.media.setMaxLines(1);
        this.media.setGravity(16);
        this.media.setBackgroundColor(-328966);
        this.media.setText(LocaleController.getString("Media", R.string.Media) + " : ");
        contentView.addView(this.media, LayoutHelper.createFrame(-1, 34.0f, (LocaleController.isRTL ? 5 : 3) | 48, 0.0f, 0.0f, 0.0f, 0.0f));
        this.chatActivityEnterView.setDelegate(new C05285());
        this.chatActivityEnterView.setAllowStickersAndGifs(false, false);
        this.chatActivityEnterView.getMessageEditText().setText(this.caption);
        if (!(myObject == null || myObject.messageOwner == null || myObject.messageOwner.media == null)) {
            this.chatActivityEnterView.getMessageEditText().setFilters(new InputFilter[]{new LengthFilter(200)});
        }
        final Context context2 = context;
        this.chatActivityEnterView.getSendButton().setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ForwardProActivity.this.mode == 1) {
                    ForwardProActivity.this.editDone();
                } else {
                    ForwardProActivity.this.setTime(context2);
                }
            }
        });
        return this.fragmentView;
    }

    public void setTime(Context context) {
        Calendar mcurrentTime = Calendar.getInstance();
        Calendar newTime = Calendar.getInstance();
        Context context2 = context;
        TimePickerDialog timePickerDialog = new TimePickerDialog(context2, new C05307(), mcurrentTime.get(11), mcurrentTime.get(12), false);
        timePickerDialog.setTitle(R.string.sendTime);
        timePickerDialog.show();
    }

    public void onResume() {
        super.onResume();
        updateTheme();
        if (urlco.AnalyticInitialized) {
            ApplicationLoader.getInstance().trackScreenView("ForwardProActivity");
        }
    }

    private void updateTheme() {
        SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0);
        this.actionBar.setBackgroundColor(themePrefs.getInt("prefHeaderColor", themePrefs.getInt("themeColor", AndroidUtilities.defColor)));
        this.actionBar.setTitleColor(themePrefs.getInt("prefHeaderTitleColor", -1));
        Drawable back = getParentActivity().getResources().getDrawable(R.drawable.ic_ab_back);
        back.setColorFilter(themePrefs.getInt("prefHeaderIconsColor", -1), Mode.MULTIPLY);
        this.actionBar.setBackButtonDrawable(back);
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.onDestroy();
        }
    }

    private void editDone() {
        boolean z;
        MessageObject m = this.selectedObject;
        if (!(m == null || this.chatActivityEnterView.getMessageEditText().getText() == null)) {
            m.messageOwner.message = this.chatActivityEnterView.getMessageEditText().getText().toString();
            if (m.messageOwner.media != null) {
                m.messageOwner.media.caption = this.chatActivityEnterView.getMessageEditText().getText().toString();
            }
            m.caption = this.chatActivityEnterView.getMessageEditText().getText().toString();
            m.messageText = this.chatActivityEnterView.getMessageEditText().getText().toString();
            m.messageOwner.from_id = -1;
            m.applyNewText();
        }
        Context parentActivity = getParentActivity();
        if (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup || this.currentChat.username == null || this.currentChat.username.length() <= 0) {
            z = false;
        } else {
            z = true;
        }
        final ShareAlert d = new ShareAlert(parentActivity, m, null, z, null, true);
        d.getQuoteSwitch().setChecked(false);
        d.getQuoteSwitch().setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (d.getQuoteSwitch().isChecked()) {
                    d.getQuoteSwitch().setChecked(false);
                }
                Toast.makeText(ForwardProActivity.this.getParentActivity(), ForwardProActivity.this.getParentActivity().getResources().getString(R.string.ProForwardError), 1).show();
            }
        });
        showDialog(d);
        this.chatActivityEnterView.openKeyboard();
        d.getDoneButtonTextView().setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ForwardProActivity.this.mode == 1) {
                    d.DoneClicked();
                } else {
                    d.TimedDoneClicked(ApplicationLoader.applicationContext, ForwardProActivity.this.timer);
                }
                Toast.makeText(ForwardProActivity.this.getParentActivity(), LocaleController.getString("Sent", R.string.Sent), 0).show();
                ForwardProActivity.this.finishFragment();
            }
        });
    }

    private Message newMessage(Message message) {
        if (message == null) {
            return null;
        }
        Message message2 = new Message();
        if (message instanceof TL_message) {
            message2 = new TL_message();
        } else if (message instanceof TL_message_secret) {
            message2 = new TL_message_secret();
        }
        message2.id = message.id;
        message2.from_id = message.from_id;
        message2.to_id = message.to_id;
        message2.date = message.date;
        message2.action = message.action;
        message2.reply_to_msg_id = message.reply_to_msg_id;
        message2.fwd_from = message.fwd_from;
        message2.reply_to_random_id = message.reply_to_random_id;
        message2.via_bot_name = message.via_bot_name;
        message2.edit_date = message.edit_date;
        message2.silent = message.silent;
        message2.message = message.message;
        if (message.media != null) {
            message2.media = newMessageMedia(message.media);
        }
        message2.flags = message.flags;
        message2.mentioned = message.mentioned;
        message2.media_unread = message.media_unread;
        message2.out = message.out;
        message2.unread = message.unread;
        message2.entities = message.entities;
        message2.reply_markup = message.reply_markup;
        message2.views = message.views;
        message2.via_bot_id = message.via_bot_id;
        message2.send_state = message.send_state;
        message2.fwd_msg_id = message.fwd_msg_id;
        message2.attachPath = message.attachPath;
        message2.params = message.params;
        message2.random_id = message.random_id;
        message2.local_id = message.local_id;
        message2.dialog_id = message.dialog_id;
        message2.ttl = message.ttl;
        message2.destroyTime = message.destroyTime;
        message2.layer = message.layer;
        message2.seq_in = message.seq_in;
        message2.seq_out = message.seq_out;
        message2.replyMessage = message.replyMessage;
        return message2;
    }

    private MessageMedia newMessageMedia(MessageMedia messageMedia) {
        MessageMedia tL_messageMediaUnsupported_old = messageMedia instanceof TL_messageMediaUnsupported_old ? new TL_messageMediaUnsupported_old() : messageMedia instanceof TL_messageMediaAudio_layer45 ? new TL_messageMediaAudio_layer45() : messageMedia instanceof TL_messageMediaPhoto_old ? new TL_messageMediaPhoto_old() : messageMedia instanceof TL_messageMediaUnsupported ? new TL_messageMediaUnsupported() : messageMedia instanceof TL_messageMediaEmpty ? new TL_messageMediaEmpty() : messageMedia instanceof TL_messageMediaVenue ? new TL_messageMediaVenue() : messageMedia instanceof TL_messageMediaVideo_old ? new TL_messageMediaVideo_old() : messageMedia instanceof TL_messageMediaDocument_old ? new TL_messageMediaDocument_old() : messageMedia instanceof TL_messageMediaDocument ? new TL_messageMediaDocument() : messageMedia instanceof TL_messageMediaContact ? new TL_messageMediaContact() : messageMedia instanceof TL_messageMediaPhoto ? new TL_messageMediaPhoto() : messageMedia instanceof TL_messageMediaVideo_layer45 ? new TL_messageMediaVideo_layer45() : messageMedia instanceof TL_messageMediaWebPage ? new TL_messageMediaWebPage() : messageMedia instanceof TL_messageMediaGeo ? new TL_messageMediaGeo() : new MessageMedia();
        tL_messageMediaUnsupported_old.bytes = messageMedia.bytes;
        tL_messageMediaUnsupported_old.caption = messageMedia.caption;
        tL_messageMediaUnsupported_old.photo = messageMedia.photo;
        tL_messageMediaUnsupported_old.audio_unused = messageMedia.audio_unused;
        tL_messageMediaUnsupported_old.geo = messageMedia.geo;
        tL_messageMediaUnsupported_old.title = messageMedia.title;
        tL_messageMediaUnsupported_old.address = messageMedia.address;
        tL_messageMediaUnsupported_old.provider = messageMedia.provider;
        tL_messageMediaUnsupported_old.venue_id = messageMedia.venue_id;
        tL_messageMediaUnsupported_old.document = messageMedia.document;
        tL_messageMediaUnsupported_old.video_unused = messageMedia.video_unused;
        tL_messageMediaUnsupported_old.phone_number = messageMedia.phone_number;
        tL_messageMediaUnsupported_old.first_name = messageMedia.first_name;
        tL_messageMediaUnsupported_old.last_name = messageMedia.last_name;
        tL_messageMediaUnsupported_old.user_id = messageMedia.user_id;
        tL_messageMediaUnsupported_old.webpage = messageMedia.webpage;
        return tL_messageMediaUnsupported_old;
    }


}
