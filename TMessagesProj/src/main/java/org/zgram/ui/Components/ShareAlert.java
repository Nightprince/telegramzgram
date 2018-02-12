package org.zgram.ui.Components;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.zgram.messenger.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import org.zgram.SQLite.SQLiteCursor;
import org.zgram.messenger.AndroidUtilities;
import org.zgram.messenger.ApplicationLoader;
import org.zgram.messenger.ChatObject;
import org.zgram.messenger.ContactsController;
import org.zgram.messenger.FileLog;
import org.zgram.messenger.LocaleController;
import org.zgram.messenger.MessageObject;
import org.zgram.messenger.MessagesController;
import org.zgram.messenger.MessagesStorage;
import org.zgram.messenger.NotificationCenter;
import org.zgram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.zgram.messenger.SendMessagesHelper;
import org.zgram.messenger.support.widget.GridLayoutManager;
import org.zgram.messenger.support.widget.RecyclerView;
import org.zgram.messenger.support.widget.RecyclerView.Adapter;
import org.zgram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.zgram.messenger.support.widget.RecyclerView.LayoutManager;
import org.zgram.messenger.support.widget.RecyclerView.LayoutParams;
import org.zgram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.zgram.messenger.support.widget.RecyclerView.State;
import org.zgram.messenger.support.widget.RecyclerView.ViewHolder;
import org.zgram.messenger.volley.DefaultRetryPolicy;
import org.zgram.tgnet.ConnectionsManager;
import org.zgram.tgnet.NativeByteBuffer;
import org.zgram.tgnet.RequestDelegate;
import org.zgram.tgnet.TLObject;
import org.zgram.tgnet.TLRPC;
import org.zgram.tgnet.TLRPC.Chat;
import org.zgram.tgnet.TLRPC.TL_channels_exportMessageLink;
import org.zgram.tgnet.TLRPC.TL_contact;
import org.zgram.tgnet.TLRPC.TL_dialog;
import org.zgram.tgnet.TLRPC.TL_error;
import org.zgram.tgnet.TLRPC.TL_exportedMessageLink;
import org.zgram.tgnet.TLRPC.User;
import org.zgram.ui.ActionBar.BottomSheet;
import org.zgram.ui.ActionBar.Theme;
import org.zgram.ui.Cells.ShareDialogCell;
import org.zgram.ui.Components.RecyclerListView.Holder;
import org.zgram.ui.Components.RecyclerListView.OnItemClickListener;
import org.zgram.ui.Components.RecyclerListView.SelectionAdapter;
import org.zgram.ui.DialogsActivity;

import edit.Favourite;
import edit.markers.ZoomTouchView;
import edit.settt.LastInListController;
import edit.timerSendMessage;

public class ShareAlert extends BottomSheet implements NotificationCenterDelegate {
  private ImageView allTab;
  private ImageView botsTab;
  private ImageView channelsTab;
  private CheckBoxSquare checkBox;
  private boolean checked = false;
  private ImageView contactsTab;
  private boolean copyLinkOnEnd;
  private ArrayList<TL_dialog> dialogs = new ArrayList();
  private int dialogsType = 0;
  private LinearLayout doneButton;
  private TextView doneButtonBadgeTextView;
  private TextView doneButtonTextView;
  private TL_exportedMessageLink exportedMessageLink;
  private boolean favsFirst;
  private ImageView favsTab;
  private FrameLayout frameLayout;
  private RecyclerListView gridView;
  private ImageView groupsTab;
  private boolean isPublicChannel;
  private GridLayoutManager layoutManager;
  private String linkToCopy;
  private ShareDialogsAdapter listAdapter;
  private boolean loadingLink;
  private EditText nameTextView;
  private Switch quoteSwitch;
  private int scrollOffsetY;
  private ShareSearchAdapter searchAdapter;
  private EmptyTextProgressView searchEmptyView;
  private HashMap<Long, TL_dialog> selectedDialogs = new HashMap();
  private MessageObject sendingMessageObject;
  private String sendingText;
  private View shadow;
  private Drawable shadowDrawable;
  private ImageView superGroupsTab;
  private int tabsHeight = 40;
  private LinearLayout tabsLayout;
  private FrameLayout tabsView;
  private int topBeforeSwitch;
  private ImageView usersTab;

  class C22753 implements OnTouchListener {
    C22753() {
    }

    public boolean onTouch(View v, MotionEvent event) {
      return true;
    }
  }

  class C22764 implements OnClickListener {
    C22764() {
    }

    public void onClick(View v) {

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
  }

  class C22775 implements OnClickListener {
    C22775() {
    }

    public void onClick(View v) {

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
  }

  class C22786 implements OnCheckedChangeListener {
    C22786() {
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
      ApplicationLoader.applicationContext.getSharedPreferences("IrangramConfig", 0).edit().putBoolean("directShareQuote", isChecked).apply();
      ShareAlert.this.setCheckColor();
    }
  }

  class C22797 implements TextWatcher {
    C22797() {
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    public void afterTextChanged(Editable s) {
      String text = ShareAlert.this.nameTextView.getText().toString();
      if (text.length() != 0) {
        if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.searchAdapter) {
          ShareAlert.this.topBeforeSwitch = ShareAlert.this.getCurrentTop();
          ShareAlert.this.gridView.setAdapter(ShareAlert.this.searchAdapter);
          ShareAlert.this.searchAdapter.notifyDataSetChanged();
        }
        if (ShareAlert.this.searchEmptyView != null) {
          ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        }
      } else if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.listAdapter) {
        int top = ShareAlert.this.getCurrentTop();
        ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoChats", R.string.NoChats));
        ShareAlert.this.gridView.setAdapter(ShareAlert.this.listAdapter);
        ShareAlert.this.listAdapter.notifyDataSetChanged();
        if (top > 0) {
          ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -top);
        }
      }
      if (ShareAlert.this.searchAdapter != null) {
        ShareAlert.this.searchAdapter.searchDialogs(text);
      }
    }
  }

  class C22808 extends ItemDecoration {
    C22808() {
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
      int i = 0;
      Holder holder = (Holder) parent.getChildViewHolder(view);
      if (holder != null) {
        int pos = holder.getAdapterPosition();
        outRect.left = pos % 4 == 0 ? 0 : AndroidUtilities.dp(ZoomTouchView.DOUBLE_TAP_ZOOM_LEVEL);
        if (pos % 4 != 3) {
          i = AndroidUtilities.dp(ZoomTouchView.DOUBLE_TAP_ZOOM_LEVEL);
        }
        outRect.right = i;
        return;
      }
      outRect.left = AndroidUtilities.dp(ZoomTouchView.DOUBLE_TAP_ZOOM_LEVEL);
      outRect.right = AndroidUtilities.dp(ZoomTouchView.DOUBLE_TAP_ZOOM_LEVEL);
    }
  }

  class C22819 implements OnItemClickListener {
    C22819() {
    }

    public void onItemClick(View view, int position) {
      if (position >= 0) {
        TL_dialog dialog;
        if (ShareAlert.this.gridView.getAdapter() == ShareAlert.this.listAdapter) {
          dialog = ShareAlert.this.listAdapter.getItem(position);
        } else {
          dialog = ShareAlert.this.searchAdapter.getItem(position);
        }
        if (dialog != null) {
          ShareDialogCell cell = (ShareDialogCell) view;
          if (ShareAlert.this.selectedDialogs.containsKey(Long.valueOf(dialog.id))) {
            ShareAlert.this.selectedDialogs.remove(Long.valueOf(dialog.id));
            cell.setChecked(false, true);
          } else {
            ShareAlert.this.selectedDialogs.put(Long.valueOf(dialog.id), dialog);
            cell.setChecked(true, true);
          }
          ShareAlert.this.updateSelectedCount();
        }
      }
    }
  }

  private class ShareDialogsAdapter extends SelectionAdapter {
    private Context context;
    private int currentCount;

    public ShareDialogsAdapter(Context context) {
      this.context = context;
      fetchDialogs();
    }

    public void fetchDialogs() {
      int a;
      TL_dialog dialog;
      int lower_id;
      Chat chat;
      ShareAlert.this.dialogs.clear();
      if (ShareAlert.this.favsFirst && ShareAlert.this.dialogsType == 0) {
        for (a = 0; a < MessagesController.getInstance().dialogsFavs.size(); a++) {
          dialog = (TL_dialog) MessagesController.getInstance().dialogsFavs.get(a);
          lower_id = (int) dialog.id;
          int high_id = (int) (dialog.id >> 32);
          if (!(lower_id == 0 || high_id == 1)) {
            if (lower_id > 0) {
              ShareAlert.this.dialogs.add(dialog);
            } else {
              chat = MessagesController.getInstance().getChat(Integer.valueOf(-lower_id));
              if (!(chat == null || ChatObject.isNotInChat(chat) || (ChatObject.isChannel(chat) && !chat.creator && ((chat.admin_rights == null || !chat.admin_rights.post_messages) && !chat.megagroup)))) {
                ShareAlert.this.dialogs.add(dialog);
              }
            }
          }
        }
      }
      ArrayList<TL_dialog> mdialogs = ShareAlert.this.getDialogsArray();
      for (a = 0; a < mdialogs.size(); a++) {
        dialog = (TL_dialog) mdialogs.get(a);
        if (!ShareAlert.this.favsFirst || ShareAlert.this.dialogsType != 0 || !Favourite.isFavourite(Long.valueOf(dialog.id))) {
          lower_id = (int) dialog.id;
          int high_id = (int) (dialog.id >> 32);
          if (!(lower_id == 0 || high_id == 1)) {
            if (lower_id > 0) {
              ShareAlert.this.dialogs.add(dialog);
            } else {
              chat = MessagesController.getInstance().getChat(Integer.valueOf(-lower_id));
              if (!(chat == null || ChatObject.isNotInChat(chat) || (ChatObject.isChannel(chat) && !chat.creator && ((chat.admin_rights == null || !chat.admin_rights.post_messages) && !chat.megagroup)))) {
                ShareAlert.this.dialogs.add(dialog);
              }
            }
          }
        }
      }
      notifyDataSetChanged();
    }

    public int getItemCount() {
      return ShareAlert.this.dialogs.size();
    }

    public TL_dialog getItem(int i) {
      if (i < 0 || i >= ShareAlert.this.dialogs.size()) {
        return null;
      }
      return (TL_dialog) ShareAlert.this.dialogs.get(i);
    }

    public boolean isEnabled(ViewHolder holder) {
      return true;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = new ShareDialogCell(this.context);
      view.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(100.0f)));
      return new Holder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
      ShareDialogCell cell = (ShareDialogCell) holder.itemView;
      TL_dialog dialog = getItem(position);
      cell.setDialog((int) dialog.id, ShareAlert.this.selectedDialogs.containsKey(Long.valueOf(dialog.id)), null);
    }

    public int getItemViewType(int i) {
      return 0;
    }
  }

  public class ShareSearchAdapter extends SelectionAdapter {
    private Context context;
    private int lastReqId;
    private int lastSearchId = 0;
    private String lastSearchText;
    private int reqId = 0;
    private ArrayList<DialogSearchResult> searchResult = new ArrayList();
    private Timer searchTimer;

    private class DialogSearchResult {
      public int date;
      public TL_dialog dialog;
      public CharSequence name;
      public TLObject object;

      private DialogSearchResult() {
        this.dialog = new TL_dialog();
      }
    }

    public ShareSearchAdapter(Context context) {
      this.context = context;
    }

    private void searchDialogsInternal(final String query, final int searchId) {
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {

        class C22821 implements Comparator<DialogSearchResult> {
          C22821() {
          }

          public int compare(DialogSearchResult lhs, DialogSearchResult rhs) {
            if (lhs.date < rhs.date) {
              return 1;
            }
            if (lhs.date > rhs.date) {
              return -1;
            }
            return 0;
          }
        }

        public void run() {
          try {
            String search1 = query.trim().toLowerCase();
            if (search1.length() == 0) {
              ShareSearchAdapter.this.lastSearchId = -1;
              ShareSearchAdapter.this.updateSearchResults(new ArrayList(), ShareSearchAdapter.this.lastSearchId);
              return;
            }
            DialogSearchResult dialogSearchResult;
            String name;
            String tName;
            String username;
            int usernamePos;
            int found;
            int length;
            int i;
            String q;
            NativeByteBuffer data;
            TLRPC.TL_user user;
            String search2 = LocaleController.getInstance().getTranslitString(search1);
            if (search1.equals(search2) || search2.length() == 0) {
              search2 = null;
            }
            String[] search = new String[((search2 != null ? 1 : 0) + 1)];
            search[0] = search1;
            if (search2 != null) {
              search[1] = search2;
            }
            ArrayList<Integer> usersToLoad = new ArrayList();
            ArrayList<Integer> chatsToLoad = new ArrayList();
            int resultCount = 0;
            HashMap<Long, DialogSearchResult> dialogsResult = new HashMap();
            SQLiteCursor cursor = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 400", new Object[0]);
            while (cursor.next()) {
              long id = cursor.longValue(0);
              dialogSearchResult = new DialogSearchResult();
              dialogSearchResult.date = cursor.intValue(1);
              dialogsResult.put(Long.valueOf(id), dialogSearchResult);
              int lower_id = (int) id;
              int high_id = (int) (id >> 32);
              if (!(lower_id == 0 || high_id == 1)) {
                if (lower_id > 0) {
                  if (!usersToLoad.contains(Integer.valueOf(lower_id))) {
                    usersToLoad.add(Integer.valueOf(lower_id));
                  }
                } else if (!chatsToLoad.contains(Integer.valueOf(-lower_id))) {
                  chatsToLoad.add(Integer.valueOf(-lower_id));
                }
              }
            }
            cursor.dispose();
            if (!usersToLoad.isEmpty()) {
              cursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, status, name FROM users WHERE uid IN(%s)", new Object[]{TextUtils.join(",", usersToLoad)}), new Object[0]);
              while (cursor.next()) {
                name = cursor.stringValue(2);
                tName = LocaleController.getInstance().getTranslitString(name);
                if (name.equals(tName)) {
                  tName = null;
                }
                username = null;
                usernamePos = name.lastIndexOf(";;;");
                if (usernamePos != -1) {
                  username = name.substring(usernamePos + 3);
                }
                found = 0;
                length = search.length;
                i = 0;
                while (i < length) {
                  q = search[i];
                  if (name.startsWith(q) || name.contains(" " + q) || (tName != null && (tName.startsWith(q) || tName.contains(" " + q)))) {
                    found = 1;
                  } else if (username != null && username.startsWith(q)) {
                    found = 2;
                  }
                  if (found != 0) {
                    data = cursor.byteBufferValue(0);
                    if (data != null) {
                      user = (TLRPC.TL_user) User.TLdeserialize(data, data.readInt32(false), false);
                      data.reuse();
                      dialogSearchResult = (DialogSearchResult) dialogsResult.get(Long.valueOf((long) user.id));
                      if (user.status != null) {
                        user.status.expires = cursor.intValue(1);
                      }
                      if (found == 1) {
                        dialogSearchResult.name = AndroidUtilities.generateSearchName(user.first_name, user.last_name, q);
                      } else {
                        dialogSearchResult.name = AndroidUtilities.generateSearchName("@" + user.username, null, "@" + q);
                      }
                      dialogSearchResult.object = user;
                      dialogSearchResult.dialog.id = (long) user.id;
                      resultCount++;
                    }
                  } else {
                    i++;
                  }
                }
              }
              cursor.dispose();
            }
            if (!chatsToLoad.isEmpty()) {
              cursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, name FROM chats WHERE uid IN(%s)", new Object[]{TextUtils.join(",", chatsToLoad)}), new Object[0]);
              while (cursor.next()) {
                name = cursor.stringValue(1);
                tName = LocaleController.getInstance().getTranslitString(name);
                if (name.equals(tName)) {
                  tName = null;
                }
                int a = 0;
                while (a < search.length) {
                  q = search[a];
                  if (name.startsWith(q) || name.contains(" " + q) || (tName != null && (tName.startsWith(q) || tName.contains(" " + q)))) {
                    data = cursor.byteBufferValue(0);
                    if (data != null) {
                      Chat chat = Chat.TLdeserialize(data, data.readInt32(false), false);
                      data.reuse();
                      if (!(chat == null || ChatObject.isNotInChat(chat))) {
                        if (!ChatObject.isChannel(chat) || chat.creator || ((chat.admin_rights != null && chat.admin_rights.post_messages) || chat.megagroup)) {
                          dialogSearchResult = (DialogSearchResult) dialogsResult.get(Long.valueOf(-((long) chat.id)));
                          dialogSearchResult.name = AndroidUtilities.generateSearchName(chat.title, null, q);
                          dialogSearchResult.object = chat;
                          dialogSearchResult.dialog.id = (long) (-chat.id);
                          resultCount++;
                        }
                      }
                    }
                  } else {
                    a++;
                  }
                }
              }
              cursor.dispose();
            }
            ArrayList<DialogSearchResult> arrayList = new ArrayList(resultCount);
            for (DialogSearchResult dialogSearchResult2 : dialogsResult.values()) {
              if (!(dialogSearchResult2.object == null || dialogSearchResult2.name == null)) {
                arrayList.add(dialogSearchResult2);
              }
            }
            cursor = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid", new Object[0]);
            while (cursor.next()) {
              if (!dialogsResult.containsKey(Long.valueOf((long) cursor.intValue(3)))) {
                name = cursor.stringValue(2);
                tName = LocaleController.getInstance().getTranslitString(name);
                if (name.equals(tName)) {
                  tName = null;
                }
                username = null;
                usernamePos = name.lastIndexOf(";;;");
                if (usernamePos != -1) {
                  username = name.substring(usernamePos + 3);
                }
                found = 0;
                length = search.length;
                i = 0;
                while (i < length) {
                  q = search[i];
                  if (name.startsWith(q) || name.contains(" " + q) || (tName != null && (tName.startsWith(q) || tName.contains(" " + q)))) {
                    found = 1;
                  } else if (username != null && username.startsWith(q)) {
                    found = 2;
                  }
                  if (found != 0) {
                    data = cursor.byteBufferValue(0);
                    if (data != null) {
                      user = (TLRPC.TL_user) User.TLdeserialize(data, data.readInt32(false), false);
                      data.reuse();
                      DialogSearchResult  dialogSearchResult2 = new DialogSearchResult();
                      if (user.status != null) {
                        user.status.expires = cursor.intValue(1);
                      }
                      dialogSearchResult2.dialog.id = (long) user.id;
                      dialogSearchResult2.object = user;
                      if (found == 1) {
                        dialogSearchResult2.name = AndroidUtilities.generateSearchName(user.first_name, user.last_name, q);
                      } else {
                        dialogSearchResult2.name = AndroidUtilities.generateSearchName("@" + user.username, null, "@" + q);
                      }
                      arrayList.add(dialogSearchResult2);
                    }
                  } else {
                    i++;
                  }
                }
              }
            }
            cursor.dispose();
            Collections.sort(arrayList, new C22821());
            ShareSearchAdapter.this.updateSearchResults(arrayList, searchId);
          } catch (Exception e) {
            FileLog.e(e);
          }
        }
      });
    }

    private void updateSearchResults(final ArrayList<DialogSearchResult> result, final int searchId) {
      AndroidUtilities.runOnUIThread(new Runnable() {
        public void run() {
          if (searchId == ShareSearchAdapter.this.lastSearchId) {
            boolean becomeEmpty;
            boolean isEmpty;
            for (int a = 0; a < result.size(); a++) {
              DialogSearchResult obj = (DialogSearchResult) result.get(a);
              if (obj.object instanceof User) {
                MessagesController.getInstance().putUser((User) obj.object, true);
              } else if (obj.object instanceof Chat) {
                MessagesController.getInstance().putChat((Chat) obj.object, true);
              }
            }
            if (ShareSearchAdapter.this.searchResult.isEmpty() || !result.isEmpty()) {
              becomeEmpty = false;
            } else {
              becomeEmpty = true;
            }
            if (ShareSearchAdapter.this.searchResult.isEmpty() && result.isEmpty()) {
              isEmpty = true;
            } else {
              isEmpty = false;
            }
            if (becomeEmpty) {
              ShareAlert.this.topBeforeSwitch = ShareAlert.this.getCurrentTop();
            }
            ShareSearchAdapter.this.searchResult = result;
            ShareSearchAdapter.this.notifyDataSetChanged();
            if (!isEmpty && !becomeEmpty && ShareAlert.this.topBeforeSwitch > 0) {
              ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -ShareAlert.this.topBeforeSwitch);
              ShareAlert.this.topBeforeSwitch = -1000;
            }
          }
        }
      });
    }

    public void searchDialogs(final String query) {
      if (query == null || this.lastSearchText == null || !query.equals(this.lastSearchText)) {
        this.lastSearchText = query;
        try {
          if (this.searchTimer != null) {
            this.searchTimer.cancel();
            this.searchTimer = null;
          }
        } catch (Exception e) {
          FileLog.e(e);
        }
        if (query == null || query.length() == 0) {
          this.searchResult.clear();
          ShareAlert.this.topBeforeSwitch = ShareAlert.this.getCurrentTop();
          notifyDataSetChanged();
          return;
        }
        final int searchId = this.lastSearchId + 1;
        this.lastSearchId = searchId;
        this.searchTimer = new Timer();
        this.searchTimer.schedule(new TimerTask() {
          public void run() {
            try {
              cancel();
              ShareSearchAdapter.this.searchTimer.cancel();
              ShareSearchAdapter.this.searchTimer = null;
            } catch (Exception e) {
              FileLog.e(e);
            }
            ShareSearchAdapter.this.searchDialogsInternal(query, searchId);
          }
        }, 200, 300);
      }
    }

    public int getItemCount() {
      return this.searchResult.size();
    }

    public TL_dialog getItem(int i) {
      if (i < 0 || i >= this.searchResult.size()) {
        return null;
      }
      return ((DialogSearchResult) this.searchResult.get(i)).dialog;
    }

    public long getItemId(int i) {
      return (long) i;
    }

    public boolean isEnabled(ViewHolder holder) {
      return true;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = new ShareDialogCell(this.context);
      view.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(100.0f)));
      return new Holder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
      ShareDialogCell cell = (ShareDialogCell) holder.itemView;
      TLRPC.TL_dialog dialog = getItem(position);
      cell.setDialog((int) dialog.id, selectedDialogs.containsKey(dialog.id), null);


    }

    public int getItemViewType(int i) {
      return 0;
    }
  }

  public ShareAlert(final Context context, MessageObject messageObject, String text, boolean publicChannel, String copyLink, boolean fullScreen) {
    super(context, true);
    this.shadowDrawable = context.getResources().getDrawable(R.drawable.sheet_shadow).mutate();
    this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), Mode.MULTIPLY));
    this.linkToCopy = copyLink;
    this.sendingMessageObject = messageObject;
    this.searchAdapter = new ShareSearchAdapter(context);
    this.isPublicChannel = publicChannel;
    this.sendingText = text;
    if (publicChannel) {
      this.loadingLink = true;
      TL_channels_exportMessageLink req = new TL_channels_exportMessageLink();
      req.id = messageObject.getId();
      req.channel = MessagesController.getInputChannel(messageObject.messageOwner.to_id.channel_id);
      ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
        public void run(final TLObject response, TL_error error) {
          AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
              if (response != null) {
                ShareAlert.this.exportedMessageLink = (TL_exportedMessageLink) response;
                if (ShareAlert.this.copyLinkOnEnd) {
                  ShareAlert.this.copyLink(context);
                }
              }
              ShareAlert.this.loadingLink = false;
            }
          });
        }
      });
    }
    this.containerView = new FrameLayout(context) {
      private boolean ignoreLayout = false;

      public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() != 0 || ShareAlert.this.scrollOffsetY == 0 || ev.getY() >= ((float) ShareAlert.this.scrollOffsetY)) {
          return super.onInterceptTouchEvent(ev);
        }
        ShareAlert.this.dismiss();
        return true;
      }

      public boolean onTouchEvent(MotionEvent e) {
        return !ShareAlert.this.isDismissed() && super.onTouchEvent(e);
      }

      protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (VERSION.SDK_INT >= 21) {
          height -= AndroidUtilities.statusBarHeight;
        }
        int contentSize = (AndroidUtilities.dp(48.0f) + (Math.max(3, (int) Math.ceil((double) (((float) Math.max(ShareAlert.this.searchAdapter.getItemCount(), ShareAlert.this.listAdapter.getItemCount())) / ZoomTouchView.DOUBLE_TAP_ZOOM_LEVEL))) * AndroidUtilities.dp(100.0f))) + ShareAlert.backgroundPaddingTop;
        int padding = contentSize < height ? 0 : (height - ((height / 5) * 3)) + AndroidUtilities.dp(8.0f);
        if (ShareAlert.this.gridView.getPaddingTop() != padding) {
          this.ignoreLayout = true;
          ShareAlert.this.gridView.setPadding(0, padding, 0, AndroidUtilities.dp(8.0f));
          this.ignoreLayout = false;
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(Math.min(contentSize, height), 1073741824));
      }

      protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        ShareAlert.this.updateLayout();
      }

      public void requestLayout() {
        if (!this.ignoreLayout) {
          super.requestLayout();
        }
      }

      protected void onDraw(Canvas canvas) {
        ShareAlert.this.shadowDrawable.setBounds(0, ShareAlert.this.scrollOffsetY - ShareAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
        ShareAlert.this.shadowDrawable.draw(canvas);
      }
    };
    this.containerView.setWillNotDraw(false);
    this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
    this.frameLayout = new FrameLayout(context);
    this.frameLayout.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
    this.frameLayout.setOnTouchListener(new C22753());
    this.doneButton = new LinearLayout(context);
    this.doneButton.setOrientation(0);
    this.doneButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 0));
    this.doneButton.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
    this.frameLayout.addView(this.doneButton, LayoutHelper.createFrame(-2, -1, 53));
    this.doneButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        ShareAlert.this.DoneClicked();

      }

    });
    this.doneButtonBadgeTextView = new TextView(context);
    this.doneButtonBadgeTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.doneButtonBadgeTextView.setTextSize(1, 13.0f);
    this.doneButtonBadgeTextView.setTextColor(Theme.getColor(Theme.key_dialogBadgeText));
    this.doneButtonBadgeTextView.setGravity(17);
    this.doneButtonBadgeTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(12.5f), Theme.getColor(Theme.key_dialogBadgeBackground)));
    this.doneButtonBadgeTextView.setMinWidth(AndroidUtilities.dp(23.0f));
    this.doneButtonBadgeTextView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    this.doneButton.addView(this.doneButtonBadgeTextView, LayoutHelper.createLinear(-2, 23, 16, 0, 0, 10, 0));
    this.doneButtonTextView = new TextView(context);
    this.doneButtonTextView.setTextSize(1, 14.0f);
    this.doneButtonTextView.setGravity(17);
    this.doneButtonTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
    this.doneButtonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.doneButton.addView(this.doneButtonTextView, LayoutHelper.createLinear(-2, -2, 16));
    this.checkBox = new CheckBoxSquare(context, false);
    this.checkBox.setVisibility(0);
    //this.checkBox.setColor(-12664327);
    this.checkBox.setChecked(this.checked, false);
    this.frameLayout.addView(this.checkBox, LayoutHelper.createFrame(18, 18.0f, 19, 10.0f, 0.0f, 5.0f, 0.0f));
    this.checkBox.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
         ShareAlert.this.checked = !ShareAlert.this.checked;
         ShareAlert.this.checkBox.setChecked(ShareAlert.this.checked, true);
         ShareAlert.this.setCheckAll(ShareAlert.this.checked);
       }
     });
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("IrangramConfig", 0);
    this.quoteSwitch = new Switch(context);
    this.quoteSwitch.setTag("chat");
    this.quoteSwitch.setDuplicateParentStateEnabled(false);
    this.quoteSwitch.setFocusable(false);
    this.quoteSwitch.setFocusableInTouchMode(false);
    this.quoteSwitch.setClickable(true);
    setCheck(preferences.getBoolean("directShareQuote", true));
    setCheckColor();
    this.frameLayout.addView(this.quoteSwitch, LayoutHelper.createFrame(-2, -2.0f, 19, 32.0f, 2.0f, 0.0f, 0.0f));
    this.quoteSwitch.setOnCheckedChangeListener(new C22786());
    TextView quoteTextView = new TextView(context);
    quoteTextView.setTextSize(1, 9.0f);
    quoteTextView.setTextColor(-9079435);
    quoteTextView.setGravity(17);
    quoteTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
    quoteTextView.setText(LocaleController.getString("Quote", R.string.Quote).toUpperCase());
    quoteTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.frameLayout.addView(quoteTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 44.0f, 2.0f, 0.0f, 0.0f));
    ImageView imageView = new ImageView(context);
    imageView.setImageResource(R.drawable.ic_ab_search);
    imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogIcon), Mode.MULTIPLY));
    imageView.setScaleType(ScaleType.CENTER);
    imageView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
    this.frameLayout.addView(imageView, LayoutHelper.createFrame(48, 48.0f, 19, 80.0f, 0.0f, 0.0f, 0.0f));
    this.nameTextView = new EditText(context);
    this.nameTextView.setHint(LocaleController.getString("ShareSendTo", R.string.ShareSendTo));
    this.nameTextView.setMaxLines(1);
    this.nameTextView.setSingleLine(true);
    this.nameTextView.setGravity(19);
    this.nameTextView.setTextSize(1, 16.0f);
    this.nameTextView.setBackgroundDrawable(null);
    this.nameTextView.setHintTextColor(Theme.getColor(Theme.key_dialogTextHint));
    this.nameTextView.setImeOptions(268435456);
    this.nameTextView.setInputType(16385);
    AndroidUtilities.clearCursorDrawable(this.nameTextView);
    this.nameTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
    this.frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -1.0f, 51, 128.0f, 2.0f, 96.0f, 0.0f));
    this.nameTextView.addTextChangedListener(new C22797());
    this.gridView = new RecyclerListView(context);
    this.gridView.setTag(Integer.valueOf(13));
    this.gridView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
    this.gridView.setClipToPadding(false);
    RecyclerListView recyclerListView = this.gridView;
    LayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
    this.layoutManager = (GridLayoutManager) gridLayoutManager;
    recyclerListView.setLayoutManager(gridLayoutManager);
    this.gridView.setHorizontalScrollBarEnabled(false);
    this.gridView.setVerticalScrollBarEnabled(false);
    this.gridView.addItemDecoration(new C22808());
    this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 88.0f, 0.0f, 0.0f));
    recyclerListView = this.gridView;
    Adapter shareDialogsAdapter = new ShareDialogsAdapter(context);
    this.listAdapter = (ShareDialogsAdapter) shareDialogsAdapter;
    recyclerListView.setAdapter(shareDialogsAdapter);
    this.gridView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
    this.gridView.setOnItemClickListener(new C22819());
    this.gridView.setOnScrollListener(new OnScrollListener() {
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        ShareAlert.this.updateLayout();
      }
    });
    this.searchEmptyView = new EmptyTextProgressView(context);
    this.searchEmptyView.setShowAtCenter(true);
    this.searchEmptyView.showTextView();
    this.searchEmptyView.setText(LocaleController.getString("NoChats", R.string.NoChats));
    this.gridView.setEmptyView(this.searchEmptyView);
    this.containerView.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 88.0f, 0.0f, 0.0f));
    this.containerView.addView(this.frameLayout, LayoutHelper.createFrame(-1, 48, 51));
    this.shadow = new View(context);
    this.shadow.setBackgroundResource(R.drawable.header_shadow);
    this.containerView.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0f, 51, 0.0f, 88.0f, 0.0f, 0.0f));
    this.tabsView = new FrameLayout(context);
    this.tabsView.setBackgroundColor(AndroidUtilities.themeColor);
    createTabs(context);
    this.containerView.addView(this.tabsView, LayoutHelper.createFrame(-1, (float) this.tabsHeight, 51, 0.0f, 48.0f, 0.0f, 0.0f));
    updateSelectedCount();
    if (!DialogsActivity.dialogsLoaded) {
      MessagesController.getInstance().loadDialogs(0, 100, true);
      ContactsController.getInstance().checkInviteText();
      DialogsActivity.dialogsLoaded = true;
    }
    if (this.dialogs.isEmpty()) {
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogsNeedReload);
    }
  }

  public ShareAlert(final Context context, MessageObject messageObject, String text, boolean publicChannel, String copyLink, boolean fullScreen, Long time) {
    super(context, true);
    this.shadowDrawable = context.getResources().getDrawable(R.drawable.sheet_shadow).mutate();
    this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), Mode.MULTIPLY));
    this.linkToCopy = copyLink;
    this.sendingMessageObject = messageObject;
    this.searchAdapter = new ShareSearchAdapter(context);
    this.isPublicChannel = publicChannel;
    this.sendingText = text;
    if (publicChannel) {
      this.loadingLink = true;
      TL_channels_exportMessageLink req = new TL_channels_exportMessageLink();
      req.id = messageObject.getId();
      req.channel = MessagesController.getInputChannel(messageObject.messageOwner.to_id.channel_id);
      ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
        public void run(final TLObject response, TL_error error) {
          AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
              if (response != null) {
                ShareAlert.this.exportedMessageLink = (TL_exportedMessageLink) response;
                if (ShareAlert.this.copyLinkOnEnd) {
                  ShareAlert.this.copyLink(context);
                }
              }
              ShareAlert.this.loadingLink = false;
            }
          });
        }
      });
    }
    this.containerView = new FrameLayout(context) {
      private boolean ignoreLayout = false;

      public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() != 0 || ShareAlert.this.scrollOffsetY == 0 || ev.getY() >= ((float) ShareAlert.this.scrollOffsetY)) {
          return super.onInterceptTouchEvent(ev);
        }
        ShareAlert.this.dismiss();
        return true;
      }

      public boolean onTouchEvent(MotionEvent e) {
        return !ShareAlert.this.isDismissed() && super.onTouchEvent(e);
      }

      protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (VERSION.SDK_INT >= 21) {
          height -= AndroidUtilities.statusBarHeight;
        }
        int contentSize = (AndroidUtilities.dp(48.0f) + (Math.max(3, (int) Math.ceil((double) (((float) Math.max(ShareAlert.this.searchAdapter.getItemCount(), ShareAlert.this.listAdapter.getItemCount())) / ZoomTouchView.DOUBLE_TAP_ZOOM_LEVEL))) * AndroidUtilities.dp(100.0f))) + ShareAlert.backgroundPaddingTop;
        int padding = contentSize < height ? 0 : (height - ((height / 5) * 3)) + AndroidUtilities.dp(8.0f);
        if (ShareAlert.this.gridView.getPaddingTop() != padding) {
          this.ignoreLayout = true;
          ShareAlert.this.gridView.setPadding(0, padding, 0, AndroidUtilities.dp(8.0f));
          this.ignoreLayout = false;
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(Math.min(contentSize, height), 1073741824));
      }

      protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        ShareAlert.this.updateLayout();
      }

      public void requestLayout() {
        if (!this.ignoreLayout) {
          super.requestLayout();
        }
      }

      protected void onDraw(Canvas canvas) {
        ShareAlert.this.shadowDrawable.setBounds(0, ShareAlert.this.scrollOffsetY - ShareAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
        ShareAlert.this.shadowDrawable.draw(canvas);
      }
    };
    this.containerView.setWillNotDraw(false);
    this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
    this.frameLayout = new FrameLayout(context);
    this.frameLayout.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
    this.frameLayout.setOnTouchListener(new OnTouchListener() {
      public boolean onTouch(View v, MotionEvent event) {
        return true;
      }
    });
    this.doneButton = new LinearLayout(context);
    this.doneButton.setOrientation(0);
    this.doneButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 0));
    this.doneButton.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
    this.frameLayout.addView(this.doneButton, LayoutHelper.createFrame(-2, -1, 53));
    final Long l = time;
    this.doneButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        ShareAlert.this.TimedDoneClicked(context, l.longValue());

      }
    });
    this.doneButtonBadgeTextView = new TextView(context);
    this.doneButtonBadgeTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.doneButtonBadgeTextView.setTextSize(1, 13.0f);
    this.doneButtonBadgeTextView.setTextColor(Theme.getColor(Theme.key_dialogBadgeText));
    this.doneButtonBadgeTextView.setGravity(17);
    this.doneButtonBadgeTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(12.5f), Theme.getColor(Theme.key_dialogBadgeBackground)));
    this.doneButtonBadgeTextView.setMinWidth(AndroidUtilities.dp(23.0f));
    this.doneButtonBadgeTextView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    this.doneButton.addView(this.doneButtonBadgeTextView, LayoutHelper.createLinear(-2, 23, 16, 0, 0, 10, 0));
    this.doneButtonTextView = new TextView(context);
    this.doneButtonTextView.setTextSize(1, 14.0f);
    this.doneButtonTextView.setGravity(17);
    this.doneButtonTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
    this.doneButtonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.doneButton.addView(this.doneButtonTextView, LayoutHelper.createLinear(-2, -2, 16));
    this.checkBox = new CheckBoxSquare(context, false);
    this.checkBox.setVisibility(0);
    //this.checkBox.setColor(-12664327);
    this.checkBox.setChecked(this.checked, false);
    this.frameLayout.addView(this.checkBox, LayoutHelper.createFrame(18, 18.0f, 19, 10.0f, 0.0f, 5.0f, 0.0f));
    this.checkBox.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
         ShareAlert.this.checked = !ShareAlert.this.checked;
         ShareAlert.this.checkBox.setChecked(ShareAlert.this.checked, true);
         ShareAlert.this.setCheckAll(ShareAlert.this.checked);
       }
     });
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("IrangramConfig", 0);
    this.quoteSwitch = new Switch(context);
    this.quoteSwitch.setTag("chat");
    this.quoteSwitch.setDuplicateParentStateEnabled(false);
    this.quoteSwitch.setFocusable(false);
    this.quoteSwitch.setFocusableInTouchMode(false);
    this.quoteSwitch.setClickable(true);
    setCheck(preferences.getBoolean("directShareQuote", true));
    setCheckColor();
    this.frameLayout.addView(this.quoteSwitch, LayoutHelper.createFrame(-2, -2.0f, 19, 32.0f, 2.0f, 0.0f, 0.0f));
    this.quoteSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ApplicationLoader.applicationContext.getSharedPreferences("IrangramConfig", 0).edit().putBoolean("directShareQuote", isChecked).apply();
        ShareAlert.this.setCheckColor();
      }
    });
    TextView quoteTextView = new TextView(context);
    quoteTextView.setTextSize(1, 9.0f);
    quoteTextView.setTextColor(-9079435);
    quoteTextView.setGravity(17);
    quoteTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
    quoteTextView.setText(LocaleController.getString("Quote", R.string.Quote).toUpperCase());
    quoteTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.frameLayout.addView(quoteTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 44.0f, 2.0f, 0.0f, 0.0f));
    ImageView imageView = new ImageView(context);
    imageView.setImageResource(R.drawable.ic_ab_search);
    imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogIcon), Mode.MULTIPLY));
    imageView.setScaleType(ScaleType.CENTER);
    imageView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
    this.frameLayout.addView(imageView, LayoutHelper.createFrame(48, 48.0f, 19, 80.0f, 0.0f, 0.0f, 0.0f));
    this.nameTextView = new EditText(context);
    this.nameTextView.setHint(LocaleController.getString("ShareSendTo", R.string.ShareSendTo));
    this.nameTextView.setMaxLines(1);
    this.nameTextView.setSingleLine(true);
    this.nameTextView.setGravity(19);
    this.nameTextView.setTextSize(1, 16.0f);
    this.nameTextView.setBackgroundDrawable(null);
    this.nameTextView.setHintTextColor(Theme.getColor(Theme.key_dialogTextHint));
    this.nameTextView.setImeOptions(268435456);
    this.nameTextView.setInputType(16385);
    AndroidUtilities.clearCursorDrawable(this.nameTextView);
    this.nameTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
    this.frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -1.0f, 51, 128.0f, 2.0f, 96.0f, 0.0f));
    this.nameTextView.addTextChangedListener(new TextWatcher() {
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      public void afterTextChanged(Editable s) {
        String text = ShareAlert.this.nameTextView.getText().toString();
        if (text.length() != 0) {
          if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.searchAdapter) {
            ShareAlert.this.topBeforeSwitch = ShareAlert.this.getCurrentTop();
            ShareAlert.this.gridView.setAdapter(ShareAlert.this.searchAdapter);
            ShareAlert.this.searchAdapter.notifyDataSetChanged();
          }
          if (ShareAlert.this.searchEmptyView != null) {
            ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
          }
        } else if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.listAdapter) {
          int top = ShareAlert.this.getCurrentTop();
          ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoChats", R.string.NoChats));
          ShareAlert.this.gridView.setAdapter(ShareAlert.this.listAdapter);
          ShareAlert.this.listAdapter.notifyDataSetChanged();
          if (top > 0) {
            ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -top);
          }
        }
        if (ShareAlert.this.searchAdapter != null) {
          ShareAlert.this.searchAdapter.searchDialogs(text);
        }
      }
    });
    this.gridView = new RecyclerListView(context);
    this.gridView.setTag(Integer.valueOf(13));
    this.gridView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
    this.gridView.setClipToPadding(false);
    RecyclerListView recyclerListView = this.gridView;
    LayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
    this.layoutManager = (GridLayoutManager) gridLayoutManager;
    recyclerListView.setLayoutManager(gridLayoutManager);
    this.gridView.setHorizontalScrollBarEnabled(false);
    this.gridView.setVerticalScrollBarEnabled(false);
    this.gridView.addItemDecoration(new ItemDecoration() {
      public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        int i = 0;
        Holder holder = (Holder) parent.getChildViewHolder(view);
        if (holder != null) {
          int pos = holder.getAdapterPosition();
          outRect.left = pos % 4 == 0 ? 0 : AndroidUtilities.dp(ZoomTouchView.DOUBLE_TAP_ZOOM_LEVEL);
          if (pos % 4 != 3) {
            i = AndroidUtilities.dp(ZoomTouchView.DOUBLE_TAP_ZOOM_LEVEL);
          }
          outRect.right = i;
          return;
        }
        outRect.left = AndroidUtilities.dp(ZoomTouchView.DOUBLE_TAP_ZOOM_LEVEL);
        outRect.right = AndroidUtilities.dp(ZoomTouchView.DOUBLE_TAP_ZOOM_LEVEL);
      }
    });
    this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 88.0f, 0.0f, 0.0f));
    recyclerListView = this.gridView;
    Adapter shareDialogsAdapter = new ShareDialogsAdapter(context);
    this.listAdapter = (ShareDialogsAdapter) shareDialogsAdapter;
    recyclerListView.setAdapter(shareDialogsAdapter);
    this.gridView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
    this.gridView.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(View view, int position) {
        if (position >= 0) {
          TL_dialog dialog;
          if (ShareAlert.this.gridView.getAdapter() == ShareAlert.this.listAdapter) {
            dialog = ShareAlert.this.listAdapter.getItem(position);
          } else {
            dialog = ShareAlert.this.searchAdapter.getItem(position);
          }
          if (dialog != null) {
            ShareDialogCell cell = (ShareDialogCell) view;
            if (ShareAlert.this.selectedDialogs.containsKey(Long.valueOf(dialog.id))) {
              ShareAlert.this.selectedDialogs.remove(Long.valueOf(dialog.id));
              cell.setChecked(false, true);
            } else {
              ShareAlert.this.selectedDialogs.put(Long.valueOf(dialog.id), dialog);
              cell.setChecked(true, true);
            }
            ShareAlert.this.updateSelectedCount();
          }
        }
      }
    });
    this.gridView.setOnScrollListener(new OnScrollListener() {
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        ShareAlert.this.updateLayout();
      }
    });
    this.searchEmptyView = new EmptyTextProgressView(context);
    this.searchEmptyView.setShowAtCenter(true);
    this.searchEmptyView.showTextView();
    this.searchEmptyView.setText(LocaleController.getString("NoChats", R.string.NoChats));
    this.gridView.setEmptyView(this.searchEmptyView);
    this.containerView.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 88.0f, 0.0f, 0.0f));
    this.containerView.addView(this.frameLayout, LayoutHelper.createFrame(-1, 48, 51));
    this.shadow = new View(context);
    this.shadow.setBackgroundResource(R.drawable.header_shadow);
    this.containerView.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0f, 51, 0.0f, 88.0f, 0.0f, 0.0f));
    this.tabsView = new FrameLayout(context);
    this.tabsView.setBackgroundColor(AndroidUtilities.themeColor);
    createTabs(context);
    this.containerView.addView(this.tabsView, LayoutHelper.createFrame(-1, (float) this.tabsHeight, 51, 0.0f, 48.0f, 0.0f, 0.0f));
    updateSelectedCount();
    if (!DialogsActivity.dialogsLoaded) {
      MessagesController.getInstance().loadDialogs(0, 100, true);
      ContactsController.getInstance().checkInviteText();
      DialogsActivity.dialogsLoaded = true;
    }
    if (this.dialogs.isEmpty()) {
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogsNeedReload);
    }
  }

  private int getCurrentTop() {
    int i = 0;
    if (this.gridView.getChildCount() != 0) {
      View child = this.gridView.getChildAt(0);
      Holder holder = (Holder) this.gridView.findContainingViewHolder(child);
      if (holder != null) {
        int paddingTop = this.gridView.getPaddingTop();
        if (holder.getAdapterPosition() == 0 && child.getTop() >= 0) {
          i = child.getTop();
        }
        return paddingTop - i;
      }
    }
    return -1000;
  }

  public void didReceivedNotification(int id, Object... args) {
    if (id == NotificationCenter.dialogsNeedReload) {
      if (this.listAdapter != null) {
        this.listAdapter.fetchDialogs();
      }
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogsNeedReload);
    }
  }

  protected boolean canDismissWithSwipe() {
    return false;
  }

  @SuppressLint({"NewApi"})
  private void updateLayout() {
    int newOffset = 0;
    if (this.gridView.getChildCount() > 0) {
      View child = this.gridView.getChildAt(0);
      Holder holder = (Holder) this.gridView.findContainingViewHolder(child);
      int top = child.getTop() - AndroidUtilities.dp(8.0f);
      if (top > 0 && holder != null && holder.getAdapterPosition() == 0) {
        newOffset = top;
      }
      if (this.scrollOffsetY != newOffset) {
        RecyclerListView recyclerListView = this.gridView;
        this.scrollOffsetY = newOffset;
        recyclerListView.setTopGlowOffset(newOffset);
        this.frameLayout.setTranslationY((float) this.scrollOffsetY);
        this.shadow.setTranslationY((float) this.scrollOffsetY);
        this.tabsView.setTranslationY((float) this.scrollOffsetY);
        this.searchEmptyView.setTranslationY((float) this.scrollOffsetY);
        this.containerView.invalidate();
      }
    }
  }

  private void copyLink(Context context) {
    if (this.exportedMessageLink != null || this.linkToCopy != null) {
      try {
        ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.linkToCopy != null ? this.linkToCopy : this.exportedMessageLink.link));
        Toast.makeText(context, LocaleController.getString("LinkCopied", R.string.LinkCopied), 0).show();
      } catch (Exception e) {
        FileLog.e(e);
      }
    }
  }

  public void updateSelectedCount() {
    if (this.selectedDialogs.isEmpty()) {
      this.doneButtonBadgeTextView.setVisibility(8);
      if (this.isPublicChannel || this.linkToCopy != null) {
        this.doneButtonTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        this.doneButton.setEnabled(true);
        this.doneButtonTextView.setText(LocaleController.getString("CopyLink", R.string.CopyLink).toUpperCase());
        return;
      }
      this.doneButtonTextView.setTextColor(Theme.getColor(Theme.key_dialogTextGray4));
      this.doneButton.setEnabled(false);
      this.doneButtonTextView.setText(LocaleController.getString("Send", R.string.Send).toUpperCase());
      return;
    }
    this.doneButtonTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    this.doneButtonBadgeTextView.setVisibility(0);
    this.doneButtonBadgeTextView.setText(String.format("%d", new Object[]{Integer.valueOf(this.selectedDialogs.size())}));
    this.doneButtonTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue3));
    this.doneButton.setEnabled(true);
    this.doneButtonTextView.setText(LocaleController.getString("Send", R.string.Send).toUpperCase());
  }

  public void dismiss() {
    super.dismiss();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogsNeedReload);
  }

  public void setCheck(boolean checked) {
    if (VERSION.SDK_INT < 11) {
      this.quoteSwitch.resetLayout();
      this.quoteSwitch.requestLayout();
    }
    this.quoteSwitch.setChecked(checked);
    setCheckColor();
  }

  private void setCheckColor() {
    this.quoteSwitch.setColor(ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0).getInt("chatAttachTextColor", -2536390));
  }

  public TextView getDoneButtonTextView() {
    return this.doneButtonTextView;
  }

  public void DoneClicked() {
    if (this.selectedDialogs.isEmpty() && this.isPublicChannel) {
      if (this.loadingLink) {
        this.copyLinkOnEnd = true;
        Toast.makeText(getContext(), LocaleController.getString("Loading", R.string.Loading), 0).show();
      } else {
        copyLink(getContext());
      }
      dismiss();
      return;
    }
    ArrayList<MessageObject> arrayList = new ArrayList();
    arrayList.add(this.sendingMessageObject);
    for (Entry<Long, TL_dialog> entry : this.selectedDialogs.entrySet()) {
      int lower_id = (int) ((TL_dialog) entry.getValue()).id;
      if (lower_id < 0 && MessagesController.getInstance().getChat(Integer.valueOf(-lower_id)).megagroup) {
      }
      if (this.quoteSwitch.isChecked()) {
        SendMessagesHelper.getInstance().sendMessage(arrayList, ((Long) entry.getKey()).longValue());
      } else {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
          SendMessagesHelper.getInstance().processForwardFromMyName((MessageObject) it.next(), ((Long) entry.getKey()).longValue());
        }
      }
    }
    dismiss();
  }

  public void TimedDoneClicked(Context context, long time) {
    if (this.selectedDialogs.isEmpty() && this.isPublicChannel) {
      if (this.loadingLink) {
        this.copyLinkOnEnd = true;
        Toast.makeText(getContext(), LocaleController.getString("Loading", R.string.Loading), 0).show();
      } else {
        copyLink(getContext());
      }
      dismiss();
      return;
    }
    ArrayList<Integer> ints = new ArrayList();
    new ArrayList().add(this.sendingMessageObject);
    for (Entry<Long, TL_dialog> entry : this.selectedDialogs.entrySet()) {
      int id = (int) ((TL_dialog) entry.getValue()).id;
      if (id < 0) {
        id *= -1;
      }
      ints.add(Integer.valueOf(id));
    }
    if (ints.size() > 0) {
      Intent intent = new Intent(context, timerSendMessage.class);
      intent.putExtra("msg_id", this.sendingMessageObject.getId());
      intent.putExtra("dialogs_ids", ints);
      ((AlarmManager) context.getSystemService("alarm")).set(0, time, PendingIntent.getBroadcast(context, new Random().nextInt(6000) + 100, intent, 0));
      Toast.makeText(context, LocaleController.getString("Sent", R.string.Sent), 0).show();
    }
    dismiss();
  }

  private void createTabs(final Context context) {
    this.tabsLayout = new LinearLayout(context);
    this.tabsLayout.setOrientation(0);
    this.tabsLayout.setGravity(17);
    this.allTab = new ImageView(context);
    Drawable tab_all = context.getResources().getDrawable(R.drawable.tab_all);
    tab_all.setColorFilter(AndroidUtilities.getIntDef("chatHeaderIconsColor", -1), Mode.MULTIPLY);
    this.allTab.setImageDrawable(tab_all);
    addTabView(context, this.allTab);
    Drawable tab_user = context.getResources().getDrawable(R.drawable.tab_user);
    tab_user.setColorFilter(AndroidUtilities.getIntDef("chatHeaderIconsColor", -1), Mode.MULTIPLY);
    this.usersTab = new ImageView(context);
    this.usersTab.setImageDrawable(tab_user);
    addTabView(context, this.usersTab);
    Drawable tab_group = context.getResources().getDrawable(R.drawable.tab_group);
    tab_group.setColorFilter(AndroidUtilities.getIntDef("chatHeaderIconsColor", -1), Mode.MULTIPLY);
    this.groupsTab = new ImageView(context);
    this.groupsTab.setImageDrawable(tab_group);
    addTabView(context, this.groupsTab);
    Drawable tab_supergroup = context.getResources().getDrawable(R.drawable.tab_supergroup);
    tab_supergroup.setColorFilter(AndroidUtilities.getIntDef("chatHeaderIconsColor", -1), Mode.MULTIPLY);
    this.superGroupsTab = new ImageView(context);
    this.superGroupsTab.setImageDrawable(tab_supergroup);
    addTabView(context, this.superGroupsTab);
    Drawable tab_channel = context.getResources().getDrawable(R.drawable.tab_channel);
    tab_channel.setColorFilter(AndroidUtilities.getIntDef("chatHeaderIconsColor", -1), Mode.MULTIPLY);
    this.channelsTab = new ImageView(context);
    this.channelsTab.setImageDrawable(tab_channel);
    addTabView(context, this.channelsTab);
    Drawable tab_bot = context.getResources().getDrawable(R.drawable.tab_bot);
    tab_bot.setColorFilter(AndroidUtilities.getIntDef("chatHeaderIconsColor", -1), Mode.MULTIPLY);
    this.botsTab = new ImageView(context);
    this.botsTab.setImageDrawable(tab_bot);
    addTabView(context, this.botsTab);
    Drawable tab_favs = context.getResources().getDrawable(R.drawable.tab_favs);
    tab_favs.setColorFilter(AndroidUtilities.getIntDef("chatHeaderIconsColor", -1), Mode.MULTIPLY);
    this.favsTab = new ImageView(context);
    this.favsTab.setImageDrawable(tab_favs);
    addTabView(context, this.favsTab);
    Drawable tab_contacts = context.getResources().getDrawable(R.drawable.menu_contacts);
    tab_contacts.setColorFilter(AndroidUtilities.getIntDef("chatHeaderIconsColor", -1), Mode.MULTIPLY);
    this.contactsTab = new ImageView(context);
    this.contactsTab.setImageDrawable(tab_contacts);
    addTabView(context, this.contactsTab);
    this.tabsView.addView(this.tabsLayout, LayoutHelper.createFrame(-1, -1.0f));
    refreshTabs(context);
    this.allTab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (ShareAlert.this.dialogsType != 0) {
          ShareAlert.this.dialogsType = 0;
          ShareAlert.this.refreshAdapter(context);
        }
      }
    });
    this.usersTab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (ShareAlert.this.dialogsType != 3) {
          ShareAlert.this.dialogsType = 3;
          ShareAlert.this.refreshAdapter(context);
        }
      }
    });
    this.groupsTab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        int i = ApplicationLoader.applicationContext.getSharedPreferences("IrangramConfig", 0).getBoolean("hideSGroups", false) ? 9 : 4;
        if (ShareAlert.this.dialogsType != i) {
          ShareAlert.this.dialogsType = i;
          ShareAlert.this.refreshAdapter(context);
        }

      }
    });

    this.superGroupsTab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        if (ShareAlert.this.dialogsType != 7) {
          ShareAlert.this.dialogsType = 7;
          ShareAlert.this.refreshAdapter(context);
        }

      }
    });
    this.channelsTab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        if (ShareAlert.this.dialogsType != 5) {
          ShareAlert.this.dialogsType = 5;
          ShareAlert.this.refreshAdapter(context);
        }

      }
    });
    this.botsTab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        if (ShareAlert.this.dialogsType != 6) {
          ShareAlert.this.dialogsType = 6;
          ShareAlert.this.refreshAdapter(context);
        }

      }
    });
    this.favsTab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        if (ShareAlert.this.dialogsType != 8) {
          ShareAlert.this.dialogsType = 8;
          ShareAlert.this.refreshAdapter(context);
        }

      }
    });
    this.contactsTab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        if (ShareAlert.this.dialogsType != 15) {
          ShareAlert.this.dialogsType = 15;
          ShareAlert.this.refreshAdapter(context);
        }

      }
    });
  }

  private void addTabView(Context context, ImageView iv) {
    iv.setScaleType(ScaleType.CENTER);
    RelativeLayout layout = new RelativeLayout(context);
    layout.addView(iv, LayoutHelper.createRelative(-1, -1));
    this.tabsLayout.addView(layout, LayoutHelper.createLinear(0, -1, (float) DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
  }

  private void refreshAdapter(Context context) {
    if (this.checked) {
      this.checkBox.setChecked(false, false);
      this.checked = false;
    }
    refreshAdapterAndTabs(new ShareDialogsAdapter(context), context);
  }

  private void refreshAdapterAndTabs(ShareDialogsAdapter adapter, Context context) {
    this.listAdapter = adapter;
    this.gridView.setAdapter(this.listAdapter);
    this.listAdapter.notifyDataSetChanged();
    refreshTabs(context);
  }

  private void refreshTabs(Context context) {
    int i;
    SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0);
    int defColor = themePrefs.getInt("chatsHeaderIconsColor", -1);
    int iconColor = themePrefs.getInt("chatsHeaderTabIconColor", defColor);
    int iColor = themePrefs.getInt("chatsHeaderTabUnselectedIconColor", AndroidUtilities.getIntAlphaColor("chatsHeaderTabIconColor", defColor, 0.3f));
    this.allTab.setBackgroundResource(0);
    this.usersTab.setBackgroundResource(0);
    this.groupsTab.setBackgroundResource(0);
    this.superGroupsTab.setBackgroundResource(0);
    this.channelsTab.setBackgroundResource(0);
    this.botsTab.setBackgroundResource(0);
    this.favsTab.setBackgroundResource(0);
    this.contactsTab.setBackgroundResource(0);
    this.allTab.setColorFilter(iColor, Mode.SRC_IN);
    this.usersTab.setColorFilter(iColor, Mode.SRC_IN);
    this.groupsTab.setColorFilter(iColor, Mode.SRC_IN);
    this.superGroupsTab.setColorFilter(iColor, Mode.SRC_IN);
    this.channelsTab.setColorFilter(iColor, Mode.SRC_IN);
    this.botsTab.setColorFilter(iColor, Mode.SRC_IN);
    this.favsTab.setColorFilter(iColor, Mode.SRC_IN);
    this.contactsTab.setColorFilter(iColor, Mode.SRC_IN);
    Drawable selected = context.getResources().getDrawable(R.drawable.tab_selected);
    selected.setColorFilter(iconColor, Mode.SRC_IN);
    if (this.dialogsType == 9) {
      i = 4;
    } else {
      i = this.dialogsType;
    }
    switch (i) {
      case 3:
        this.usersTab.setColorFilter(iconColor, Mode.SRC_IN);
        this.usersTab.setBackgroundDrawable(selected);
        return;
      case 4:
        this.groupsTab.setColorFilter(iconColor, Mode.SRC_IN);
        this.groupsTab.setBackgroundDrawable(selected);
        return;
      case 5:
        this.channelsTab.setColorFilter(iconColor, Mode.SRC_IN);
        this.channelsTab.setBackgroundDrawable(selected);
        return;
      case 6:
        this.botsTab.setColorFilter(iconColor, Mode.SRC_IN);
        this.botsTab.setBackgroundDrawable(selected);
        return;
      case 7:
        this.superGroupsTab.setColorFilter(iconColor, Mode.SRC_IN);
        this.superGroupsTab.setBackgroundDrawable(selected);
        return;
      case 8:
        this.favsTab.setColorFilter(iconColor, Mode.SRC_IN);
        this.favsTab.setBackgroundDrawable(selected);
        return;
      case 15:
        this.contactsTab.setColorFilter(iconColor, Mode.SRC_IN);
        this.contactsTab.setBackgroundDrawable(selected);
        return;
      default:
        this.allTab.setColorFilter(iconColor, Mode.SRC_IN);
        this.allTab.setBackgroundDrawable(selected);
        return;
    }
  }

  private ArrayList<TL_dialog> getDialogsArray() {
    SharedPreferences plusPreferences = ApplicationLoader.applicationContext.getSharedPreferences("IrangramConfig", 0);
    if (this.dialogsType == 0) {
      sortDefault(MessagesController.getInstance().dialogs);
      return MessagesController.getInstance().dialogs;
    } else if (this.dialogsType == 1) {
      return MessagesController.getInstance().dialogsServerOnly;
    } else {
      if (this.dialogsType == 2) {
        return MessagesController.getInstance().dialogsGroupsOnly;
      }
      if (this.dialogsType == 3) {
        return MessagesController.getInstance().dialogsUsers;
      }
      if (this.dialogsType == 4) {
        sortDefault(MessagesController.getInstance().dialogsGroups);
        return MessagesController.getInstance().dialogsGroups;
      } else if (this.dialogsType == 5) {
        sortDefault(MessagesController.getInstance().dialogsChannels);
        return MessagesController.getInstance().dialogsChannels;
      } else if (this.dialogsType == 6) {
        sortDefault(MessagesController.getInstance().dialogsBots);
        return MessagesController.getInstance().dialogsBots;
      } else if (this.dialogsType == 7) {
        sortDefault(MessagesController.getInstance().dialogsMegaGroups);
        return MessagesController.getInstance().dialogsMegaGroups;
      } else if (this.dialogsType == 8) {
        sortDefault(MessagesController.getInstance().dialogsFavs);
        return MessagesController.getInstance().dialogsFavs;
      } else if (this.dialogsType == 9) {
        sortDefault(MessagesController.getInstance().dialogsGroupsAll);
        return MessagesController.getInstance().dialogsGroupsAll;
      } else if (this.dialogsType != 15) {
        return null;
      } else {
        ArrayList<TL_dialog> arrayList = new ArrayList();
        Iterator it = ContactsController.getInstance().contacts.iterator();
        while (it.hasNext()) {
          TL_contact tL_contact = (TL_contact) it.next();
          TL_dialog tL_dialog = new TL_dialog();
          tL_dialog.id = (long) tL_contact.user_id;
          arrayList.add(tL_dialog);
        }
        return arrayList;
      }
    }
  }

  private void sortDefault(ArrayList<TL_dialog> dialogs) {
    Collections.sort(dialogs, new Comparator<TL_dialog>() {
      public int compare(TL_dialog dialog, TL_dialog dialog2) {
        if (LastInListController.is(dialog).booleanValue()) {
          return 1;
        }
        if (LastInListController.is(dialog2).booleanValue()) {
          return -1;
        }
        if (dialog.last_message_date == dialog2.last_message_date) {
          return 0;
        }
        if (dialog.last_message_date >= dialog2.last_message_date) {
          return -1;
        }
        return 1;
      }
    });
  }

  public Switch getQuoteSwitch() {
    return this.quoteSwitch;
  }

  private void setCheckAll(boolean check) {
    for (int i = 0; i < this.dialogs.size(); i++) {
      TL_dialog dialog = (TL_dialog) this.dialogs.get(i);
      if (!check) {
        this.selectedDialogs.clear();
      } else if (check) {
        this.selectedDialogs.put(Long.valueOf(dialog.id), dialog);
      }
      updateSelectedCount();
    }
    this.listAdapter.notifyDataSetChanged();
  }
}
