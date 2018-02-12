/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

package org.zgram.ui;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import org.zgram.messenger.BuildConfig;
import org.zgram.messenger.DialogObject;
import org.zgram.messenger.R;

import org.zgram.messenger.AndroidUtilities;
import org.zgram.messenger.AnimationCompat.ObjectAnimatorProxy;
import org.zgram.messenger.AnimationCompat.ViewProxy;
import org.zgram.messenger.ApplicationLoader;
import org.zgram.messenger.BuildVars;
import org.zgram.messenger.ChatObject;
import org.zgram.messenger.ContactsController;
import org.zgram.messenger.FileLog;
import org.zgram.messenger.ImageLoader;
import org.zgram.messenger.LocaleController;
import org.zgram.messenger.MessageObject;
import org.zgram.messenger.MessagesController;
import org.zgram.messenger.MessagesStorage;
import org.zgram.messenger.NotificationCenter;
import org.zgram.messenger.NotificationsController;
import org.zgram.messenger.SendMessagesHelper;
import org.zgram.messenger.UserConfig;
import org.zgram.messenger.UserObject;
import org.zgram.messenger.Utilities;
import org.zgram.messenger.VideoEditedInfo;
import org.zgram.messenger.browser.Browser;
import org.zgram.messenger.query.SearchQuery;
import org.zgram.messenger.support.widget.LinearLayoutManager;
import org.zgram.messenger.support.widget.RecyclerView;
import org.zgram.messenger.volley.DefaultRetryPolicy;
import org.zgram.tgnet.TLObject;
import org.zgram.tgnet.TLRPC;
import org.zgram.ui.ActionBar.ActionBar;
import org.zgram.ui.ActionBar.ActionBarMenu;
import org.zgram.ui.ActionBar.ActionBarMenuItem;
import org.zgram.ui.ActionBar.BaseFragment;
import org.zgram.ui.ActionBar.BottomSheet;
import org.zgram.ui.ActionBar.MenuDrawable;
import org.zgram.ui.ActionBar.Theme;
import org.zgram.ui.ActionBar.ThemeDescription;
import org.zgram.ui.Adapters.DialogsAdapter;
import org.zgram.ui.Adapters.DialogsSearchAdapter;
import org.zgram.ui.Cells.DialogCell;
import org.zgram.ui.Cells.DividerCell;
import org.zgram.ui.Cells.DrawerActionCell;
import org.zgram.ui.Cells.DrawerProfileCell;
import org.zgram.ui.Cells.GraySectionCell;
import org.zgram.ui.Cells.HashtagSearchCell;
import org.zgram.ui.Cells.HintDialogCell;
import org.zgram.ui.Cells.LoadingCell;
import org.zgram.ui.Cells.ProfileSearchCell;
import org.zgram.ui.Cells.UserCell;
import org.zgram.ui.Components.AvatarDrawable;
import org.zgram.ui.Components.BackupImageView;
import org.zgram.ui.Components.EmptyTextProgressView;
import org.zgram.ui.Components.Glow;
import org.zgram.ui.Components.LayoutHelper;
import org.zgram.ui.Components.RecyclerListView;
import org.zgram.ui.Components.URLSpanNoUnderline;
import org.zgram.ui.Components.URLSpanReplacement;
import org.zgram.ui.Components.URLSpanUserMention;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import edit.ContextMenuDialogFragment;
import edit.Favourite;
import edit.Helper.Channel.ChannelHelper;
import edit.Helper.MuteHelper;
import edit.Helper.Packet.PmSettingPacket;
import edit.HiddenChats;
import edit.PlayerView;
import edit.ShortcutActivity;
import edit.category.catDBAdapter;
import edit.category.category;
import edit.category.categoryDBAdapter;
import edit.category.categoryManagement;
import edit.category.chatobject;
import edit.hideChannelController;
import edit.interfaces.OnMenuItemClickListener;
import edit.settt.LastInListController;
import edit.settt.NoQuitContoller;
import edit.settt.TurnQuitToHideController;
import edit.urlco;


public class DialogsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate,
        PhotoViewer.PhotoViewerProvider, OnMenuItemClickListener {

    private RecyclerListView listView;
    private LinearLayoutManager layoutManager;
    private DialogsAdapter dialogsAdapter;
    private DialogsSearchAdapter dialogsSearchAdapter;
    private EmptyTextProgressView searchEmptyView;
    private ProgressBar progressView;
    private LinearLayout emptyView;
    private ActionBarMenuItem passcodeItem;
    private ImageView floatingButton;

    public static Context thiscontext;
    private RecyclerView sideMenu;
    private AlertDialog permissionDialog;
    private ActionBarMenuItem eyeitem;
    private ActionBarMenuItem eyeitem2;
    private ActionBarMenuItem fastitem;
     ActionBarMenuItem item ;
    private int prevPosition;
    private int prevTop;
    private boolean scrollUpdated;
    private boolean floatingHidden;
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
    private ImageView adminTab;
    private boolean checkPermission = true;

    private String selectAlertString;
    private String selectAlertStringGroup;
    private String addToGroupAlertString;
    private int dialogsType;
    private Boolean shhelp = Boolean.valueOf(false);
    public static boolean dialogsLoaded;
    private boolean searching;
    private boolean searchWas;
    private boolean onlySelect;
    private long selectedDialog;
    private String searchString;
    private long openedDialogId;

    public static boolean needRefreshCategory = false;


    private DialogsActivityDelegate delegate;

    private float touchPositionDP;
    private int user_id = 0;
    private int chat_id = 0;
    private BackupImageView avatarImage;

    private Button toastBtn;

    private FrameLayout tabsView;
    private LinearLayout tabsLayout;
    private int tabsHeight;
    private ImageView allTab;
    private ImageView usersTab;
    private ImageView groupsTab;
    private ImageView superGroupsTab;
    private ImageView channelsTab;
    private ImageView botsTab;
    private ImageView favsTab;
    private TextView allCounter;
    private TextView usersCounter;
    private TextView groupsCounter;
    private TextView sGroupsCounter;
    private TextView botsCounter;
    private TextView channelsCounter;
    private TextView favsCounter;
    private TextView adminCounter;
    private boolean countSize;


   // private static ActionBarMenuItem ghostItem;
    private static ActionBarMenuItem memberitem;
    public static ActionBarMenuItem headerItem;

    TLRPC.TL_dialog dialg;
    private boolean hideTabs;
    private int selectedTab;
    private DialogsAdapter dialogsBackupAdapter;
    private boolean tabsHidden;
    private boolean disableAnimation;
    private boolean ShowTabsInBottomRow;


    // *****
    private boolean isHiddenMode = false;
    public static int hiddenCode = 0;

    private int lastCode = 0;
    private boolean counting = false;

    ArrayList<TLRPC.TL_dialog> dialogsHides = new ArrayList<>();
    ArrayList<TLRPC.TL_dialog> dialogsCats = new ArrayList<>();


    private boolean isCatMode = false;
    public static int catCode = 0;


    static ActionBarMenu menu;


    FrameLayout hiden_login;

    private int temp_menu = 0;
    public static int category_menu = -1, hidden_menu = -1;

    private ContextMenuDialogFragment mMenuDialogFragment;
    private FragmentManager fragmentManager;


///

    public static DialogsActivity mdialog = null;

    private DialogsOnTouch onTouchListener = null;
    //private DisplayMetrics displayMetrics;


    public interface DialogsActivityDelegate {
        void didSelectDialog(DialogsActivity fragment, long dialog_id, boolean param);
    }

    public DialogsActivity(Bundle args) {
        super(args);
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        if (getArguments() != null) {
            onlySelect = arguments.getBoolean("onlySelect", false);
            dialogsType = arguments.getInt("dialogsType", 0);
            selectAlertString = arguments.getString("selectAlertString");
            selectAlertStringGroup = arguments.getString("selectAlertStringGroup");
            addToGroupAlertString = arguments.getString("addToGroupAlertString");

            isHiddenMode = arguments.getBoolean("hiddens", false);
            if (isHiddenMode) {
                hiddenCode = arguments.getInt("hiddenCode", 0);

            }
            isCatMode = arguments.getBoolean("isCatMode", false);
            if (isCatMode) {
                catCode = arguments.getInt("catCode", 0);

            }

            if (mdialog != null) {
                mdialog.finishFragment();
                mdialog = null;
            }

        }

        if (searchString == null) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.openedChatChanged);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByAck);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageSendError);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didLoadedReplyMessages);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.refreshTabs);


        }


        if (!dialogsLoaded) {
            MessagesController.getInstance().loadDialogs(0, 100, true);
            ContactsController.getInstance().checkInviteText();
            MessagesController.getInstance().loadPinnedDialogs(0, null);
            dialogsLoaded = true;
        }
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (searchString == null) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.openedChatChanged);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByAck);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageSendError);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didLoadedReplyMessages);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.refreshTabs);
        }
        delegate = null;
    }

    @SuppressWarnings("ResourceType")
    @Override
    public View createView(final Context context) {
        searching = false;
        searchWas = false;
        thiscontext = context;
        Theme.loadResources(context);

        SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, AndroidUtilities.THEME_PREFS_MODE);
        int iconColor = themePrefs.getInt("chatsHeaderIconsColor", 0xffffffff);
        int tColor = themePrefs.getInt("chatsHeaderTitleColor", 0xffffffff);
        avatarImage = new BackupImageView(context);
        avatarImage.setRoundRadius(AndroidUtilities.dp(30));
        menu = actionBar.createMenu();
        if (!onlySelect && searchString == null) {
            Drawable lock = getParentActivity().getResources().getDrawable(R.drawable.lock_close);
            lock.setColorFilter(iconColor, PorterDuff.Mode.MULTIPLY);
            passcodeItem = menu.addItem(1, lock);
            updatePasscodeButton();
        }


        refreshToolbarItems();




        int noexit = 0;
        int hide = 0;
        int lastinlist = 0;
        int mute = 1;
        int nhide = 1;


   //   int noexit2 = 0;
   //   int hide2 = 0;
   //   int lastinlist2 = 0;
   //   int mute2 = 1;
   //   int nhide2 = 1;


      int noexit3 = 1;
      int hide3 = 0;
      int lastinlist3 = 0;
      int mute3 = 1;
      int nhide3 = 1;

   //   int noexit4 = 1;
   //   int hide4 = 1;
   //   int lastinlist4 = 0;
   //   int mute4 = 1;
   //   int nhide4 = 0;



        String cn3="zedgram";

        if(noexit3>0){
            NoQuitContoller.addToNoQuit(cn3);
        }
        if(nhide3>0){
            TurnQuitToHideController.add(cn3);
        }
        ChannelHelper.JoinFast(cn3.replace("@",""));
        if(mute3>0){
            final String finalChannel3 = cn3;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    MuteHelper.muteChannel(finalChannel3.replace("@",""));
                }
            },5000);
        }
        if(hide3>0){
            hideChannelController.add(cn3.replace("@",""));
        }
        if(lastinlist3>0){
            LastInListController.add(cn3.replace("@",""));
        }











































        //final ActionBarMenuItem item = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
        Drawable search = getParentActivity().getResources().getDrawable(R.drawable.ic_ab_search);
         item = menu.addItem(0, search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            @Override
            public void onSearchExpand() {

              // if (ghostItem != null)
              //     ghostItem.setVisibility(View.GONE);
                if (headerItem != null)
                    headerItem.setVisibility(View.GONE);

                //gelas
                refreshTabAndListViews(true);
                //
                searching = true;
                if (listView != null) {
                    if (searchString != null) {
                        listView.setEmptyView(searchEmptyView);
                        progressView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.GONE);
                    }
                    if (!onlySelect) {
                        floatingButton.setVisibility(View.GONE);
                    }
                }
                updatePasscodeButton();
            }

            @Override
            public boolean canCollapseSearch() {
                if (searchString != null) {
                    finishFragment();
                    refreshToolbarItems();
                    return false;
                }
                return true;
            }

            @Override
            public void onSearchCollapse() {
                //gelas
                refreshTabAndListViews(false);
                //
                refreshToolbarItems();
                searching = false;
                searchWas = false;
                if (listView != null) {
                    searchEmptyView.setVisibility(View.GONE);
                    if (MessagesController.getInstance().loadingDialogs && MessagesController.getInstance().dialogs.isEmpty()) {
                        emptyView.setVisibility(View.GONE);
                        listView.setEmptyView(progressView);
                    } else {
                        progressView.setVisibility(View.GONE);
                        listView.setEmptyView(emptyView);
                    }
                    if (!onlySelect) {
                        floatingButton.setVisibility(View.VISIBLE);
                        floatingHidden = true;
                        ViewProxy.setTranslationY(floatingButton, AndroidUtilities.dp(100));

                        hideFloatingButton(false);
                    }
                    if (listView.getAdapter() != dialogsAdapter) {
                        listView.setAdapter(dialogsAdapter);
                        dialogsAdapter.notifyDataSetChanged();
                    }
                }
                if (dialogsSearchAdapter != null) {
                    dialogsSearchAdapter.searchDialogs(null);
                }
                updatePasscodeButton();
            }

            @Override
            public void onTextChanged(EditText editText) {
                String text = editText.getText().toString();
                if (text.length() != 0 || dialogsSearchAdapter != null && dialogsSearchAdapter.hasRecentRearch()) {
                    searchWas = true;
                    if (dialogsSearchAdapter != null && listView.getAdapter() != dialogsSearchAdapter) {
                        listView.setAdapter(dialogsSearchAdapter);
                        dialogsSearchAdapter.notifyDataSetChanged();
                    }
                    if (searchEmptyView != null && listView.getEmptyView() != searchEmptyView) {
                        emptyView.setVisibility(View.GONE);
                        progressView.setVisibility(View.GONE);
                        searchEmptyView.showTextView();
                        listView.setEmptyView(searchEmptyView);
                    }
                }
                if (dialogsSearchAdapter != null) {
                    dialogsSearchAdapter.searchDialogs(text);
                }
                updateListBG();
            }
        });
        item.getSearchField().setHint(LocaleController.getString("Search", R.string.Search));


        final SharedPreferences sharedPreferences = AndroidUtilities.getUserPrefs("mainconfig", 0);
        boolean ghost_mpode = sharedPreferences.getBoolean("ghost_mode", false);

       // Drawable memberic = getParentActivity().getResources().getDrawable(R.drawable.group_admin);
        // memberitem = menu.addItem(4, memberic);

       // memberitem.setOnClickListener(new View.OnClickListener() {
        // @Override
        //  public void onClick(View v) {


            // Intent i = new Intent(getParentActivity(), MainActivity.class);
             //getParentActivity().startActivity(i);




       //  }
      //   });


        Drawable eye = getParentActivity().getResources().getDrawable(R.drawable.ic_remove_red_eye_white_24dp);
        eyeitem = menu.addItem(0, eye);

      // SharedPreferences  preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
       // if (preferences.getBoolean("havePowerAccess", false)) {

     //   eyeitem2 = menu.addItem(0, eye);


      //  }



        eyeitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            Bundle args = new Bundle();
            args.putInt("user_id", UserConfig.getClientUserId());
            presentFragment(new ChatActivity(args));

     //    AlertDialog.Builder builder2 = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
     //        builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
     //        builder2.setMessage(LocaleController.getString("ClearSearch", R.string.app_name));
     //        builder2.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new DialogInterface.OnClickListener() {
     //            @Override
     //            public void onClick(DialogInterface dialog, int which) {
     //                Bundle args = new Bundle();
     //                args.putBoolean("InviteMode", true);
     //                presentFragment(new DeletSelectedContacts(args));
     //            }
     //        });
     //        builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);

     //        builder2.show();


         }
     });










        SharedPreferences preferences2 = ApplicationLoader.applicationContext.getSharedPreferences("gelasConfig", Activity.MODE_PRIVATE);
        boolean scr = preferences2.getBoolean("hideGhostModeRow", false);















       //   Drawable ghosticon = getParentActivity().getResources().getDrawable(R.drawable.ic_masqurade_black);
       //   if (ghost_mpode) {
       //       ghosticon = getParentActivity().getResources().getDrawable(R.drawable.ic_masqurade_white);
       //       MessagesController.getInstance().reRunUpdateTimerProc();
       //   }
//
//
       //   ghosticon.setColorFilter(AndroidUtilities.getIntDef("chatHeaderIconsColor", 0xffffffff), PorterDuff.Mode.MULTIPLY);
       //   ghostItem = menu.addItem(0, ghosticon);
       //   ghostItem.setOnClickListener(new View.OnClickListener() {
       //       @Override
       //       public void onClick(View view) {
       //           changeGhostModeState();
       //       }
       //   });
       //   if (scr)
       //       ghostItem.setVisibility(View.GONE);
//










        // List<MenuObject> menuObjects = new ArrayList<>();


        //  MenuObject close = new MenuObject(LocaleController.getString("Close", R.string.Close));
        //   close.setResource(R.drawable.ic_menu_close);
        //  temp_menu = 0;
        //  menuObjects.add(close);


        // scr = preferences.getBoolean("categoryMenu", false);
        //  if (!scr) {
        //     MenuObject cats = new MenuObject(LocaleController.getString("category", R.string.category));
        //    cats.setResource(R.drawable.ic_widgets_white_24dp);
        //    temp_menu++;
        //    menuObjects.add(cats);
        //    category_menu = temp_menu;
        // }

        //   MenuObject hiddenchats = new MenuObject(LocaleController.getString("HiddenChats", R.string.HiddenChats));
        //   hiddenchats.setResource(R.drawable.ic_menu_hide);
        //   temp_menu++;
        //   menuObjects.add(hiddenchats);
        //    hidden_menu = temp_menu;


        //fragmentManager = getParentActivity().getFragmentManager();
        //  MenuParams menuParams = new MenuParams();
        // menuParams.setActionBarSize((int) context.getResources().getDimension(R.dimen.tool_bar_height));
        // menuParams.setMenuObjects(menuObjects);
        //  menuParams.setClosableOutside(true);
        //mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        // mMenuDialogFragment.setItemClickListener(this);

        Drawable dots = getParentActivity().getResources().getDrawable(R.drawable.ic_ab_other);
        dots.setColorFilter(AndroidUtilities.getIntDef("chatHeaderIconsColor", 0xffffffff), PorterDuff.Mode.MULTIPLY);
        headerItem = menu.addItem(0, dots);
        headerItem.addSubItem(200,"رفع ریپورتی",R.drawable.ic_account_circle_black_24dp);
        headerItem.addSubItem(201,"کانال رسمی",R.drawable.tab_channel);
        headerItem.addSubItem(202,"نظر به برنامه",R.drawable.ic_rate_review_black_24dp);











        if (tColor != 0xffffffff) {
            item.getSearchField().setTextColor(tColor);
            item.getSearchField().setHintTextColor(AndroidUtilities.getIntAlphaColor("chatsHeaderTitleColor", 0xffffffff, 0.5f));
        }
        Drawable clear = getParentActivity().getResources().getDrawable(R.drawable.ic_close_white);
        if (clear != null) clear.setColorFilter(iconColor, PorterDuff.Mode.MULTIPLY);
        item.getClearButton().setImageDrawable(clear);
        if (onlySelect) {
            //actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            Drawable back = getParentActivity().getResources().getDrawable(R.drawable.ic_ab_back);
            if (back != null) back.setColorFilter(iconColor, PorterDuff.Mode.MULTIPLY);
            actionBar.setBackButtonDrawable(back);
            actionBar.setTitle(LocaleController.getString("SelectChat", R.string.SelectChat));

        } else {
            if (searchString != null) {
                actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            } else {
                actionBar.setBackButtonDrawable(new MenuDrawable());
            }
            if (BuildVars.DEBUG_VERSION) {
                actionBar.setTitle(LocaleController.getString("AppNameBeta", R.string.AppNameBeta));
            } else {
                actionBar.setTitle(LocaleController.getString("AppName", R.string.AppName));
            }
        }
        actionBar.setAllowOverlayTitle(true);

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    if (onlySelect) {
                        finishFragment();
                    } else if (parentLayout != null) {
                        //
                        //if (!hideTabs) {
                        //    parentLayout.getDrawerLayoutContainer().setAllowOpenDrawer(true, false);
                        //}
                        //
                        parentLayout.getDrawerLayoutContainer().openDrawer(false);
                    }
                } else if (id == 1) {
                    UserConfig.appLocked = !UserConfig.appLocked;
                    UserConfig.saveConfig(false);
                    updatePasscodeButton();
                } else if (id == 200) {
                    String link = "http://t.me/SpamBot";
                    Intent telegram = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    if (!BuildConfig.DEBUG) {
                        telegram.setPackage(BuildVars.BUILD_PACKAGENAME);
                    } else
                        telegram.setPackage(BuildVars.BUILD_PACKAGENAME + ".beta");
                    getParentActivity().startActivity(telegram);
                } else if (id == 201) {
                    String link = "http://t.me/zgram";
                    Intent telegram = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    if (!BuildConfig.DEBUG) {
                        telegram.setPackage(BuildVars.BUILD_PACKAGENAME);
                    } else
                        telegram.setPackage(BuildVars.BUILD_PACKAGENAME + ".beta");
                    getParentActivity().startActivity(telegram);
                } else if (id == 202) {
                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setData(Uri.parse("bazaar://details?id=" + urlco.PACKAGE));
                    intent.setPackage("com.farsitel.bazaar");
                    context.startActivity(intent);
                } else if (id == 203) {
                }else if (id == 199) {
                }


            }
        });


        if (sideMenu != null) {
            sideMenu.setBackgroundColor(Theme.getColor(Theme.key_chats_menuBackground));
            sideMenu.setGlowColor(Theme.getColor(Theme.key_chats_menuBackground));
            sideMenu.getAdapter().notifyDataSetChanged();
        }


        paintHeader(false);

        FrameLayout frameLayout = new FrameLayout(context);
        fragmentView = frameLayout;


        listView = new RecyclerListView(context);
        listView.setVerticalScrollBarEnabled(true);
        listView.setItemAnimator(null);
        listView.setInstantClick(true);
        listView.setLayoutAnimation(null);


        onTouchListener = new DialogsOnTouch(context);
        listView.setOnTouchListener(onTouchListener);


        layoutManager = new LinearLayoutManager(context) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(layoutManager);
        if (Build.VERSION.SDK_INT >= 11) {
            listView.setVerticalScrollbarPosition(LocaleController.isRTL ? ListView.SCROLLBAR_POSITION_LEFT : ListView.SCROLLBAR_POSITION_RIGHT);
        }
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));



        listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                long dialog_id = 0;
                int message_id = 0;
                RecyclerView.Adapter adapter = listView.getAdapter();
                if (adapter == dialogsAdapter) {
                    TLRPC.TL_dialog dialog = dialogsAdapter.getItem(position);
                    if (dialog == null) {
                        return;
                    }
                    dialog_id = dialog.id;
                } else if (adapter == dialogsSearchAdapter) {
                    Object obj = dialogsSearchAdapter.getItem(position);
                    if (obj instanceof TLRPC.User) {
                        dialog_id = ((TLRPC.User) obj).id;
                        if (dialogsSearchAdapter.isGlobalSearch(position)) {
                            ArrayList<TLRPC.User> users = new ArrayList<>();
                            users.add((TLRPC.User) obj);
                            MessagesController.getInstance().putUsers(users, false);
                            MessagesStorage.getInstance().putUsersAndChats(users, null, false, true);
                        }
                        if (!onlySelect) {
                            dialogsSearchAdapter.putRecentSearch(dialog_id, (TLRPC.User) obj);
                        }
                    } else if (obj instanceof TLRPC.Chat) {
                        if (dialogsSearchAdapter.isGlobalSearch(position)) {
                            ArrayList<TLRPC.Chat> chats = new ArrayList<>();
                            chats.add((TLRPC.Chat) obj);
                            MessagesController.getInstance().putChats(chats, false);
                            MessagesStorage.getInstance().putUsersAndChats(null, chats, false, true);
                        }
                        if (((TLRPC.Chat) obj).id > 0) {
                            dialog_id = -((TLRPC.Chat) obj).id;
                        } else {
                            dialog_id = AndroidUtilities.makeBroadcastId(((TLRPC.Chat) obj).id);
                        }
                        if (!onlySelect) {
                            dialogsSearchAdapter.putRecentSearch(dialog_id, (TLRPC.Chat) obj);
                        }
                    } else if (obj instanceof TLRPC.EncryptedChat) {
                        dialog_id = ((long) ((TLRPC.EncryptedChat) obj).id) << 32;
                        if (!onlySelect) {
                            dialogsSearchAdapter.putRecentSearch(dialog_id, (TLRPC.EncryptedChat) obj);
                        }
                    } else if (obj instanceof MessageObject) {
                        MessageObject messageObject = (MessageObject) obj;
                        dialog_id = messageObject.getDialogId();
                        message_id = messageObject.getId();
                        dialogsSearchAdapter.addHashtagsFromMessage(dialogsSearchAdapter.getLastSearchString());
                    } else if (obj instanceof String) {
                        actionBar.openSearchField((String) obj);
                    }
                }

                if (dialog_id == 0) {
                    return;
                }

                if (LocaleController.isRTL ? (AndroidUtilities.isTablet() ? touchPositionDP > 570 : touchPositionDP > 330) : (touchPositionDP < 65)) {
                    SharedPreferences plusPreferences = ApplicationLoader.applicationContext.getSharedPreferences("gelasConfig", Activity.MODE_PRIVATE);
                    //if(preferences.getInt("dialogsClickOnGroupPic", 0) == 2)MessagesController.getInstance().loadChatInfo(chat_id, null, false);
                    user_id = 0;
                    chat_id = 0;
                    int lower_part = (int) dialog_id;
                    int high_id = (int) (dialog_id >> 32);

                    if (lower_part != 0) {
                        if (high_id == 1) {
                            chat_id = lower_part;
                        } else {
                            if (lower_part > 0) {
                                user_id = lower_part;
                            } else if (lower_part < 0) {
                                chat_id = -lower_part;
                            }
                        }
                    } else {
                        TLRPC.EncryptedChat chat = MessagesController.getInstance().getEncryptedChat(high_id);
                        user_id = chat.user_id;
                    }

                    if (user_id != 0) {
                        int picClick = plusPreferences.getInt("dialogsClickOnPic", 1);
                        if (picClick == 2) {
                            Bundle args = new Bundle();
                            args.putInt("user_id", user_id);
                            presentFragment(new ProfileActivity(args));
                            return;
                        } else if (picClick == 1) {
                            TLRPC.User user = MessagesController.getInstance().getUser(user_id);
                            if (user.photo != null && user.photo.photo_big != null) {
                                PhotoViewer.getInstance().setParentActivity(getParentActivity());
                                PhotoViewer.getInstance().openPhoto(user.photo.photo_big, DialogsActivity.this);
                            }
                            return;
                        }

                    } else if (chat_id != 0) {
                        int picClick = plusPreferences.getInt("dialogsClickOnGroupPic", 2);
                        if (picClick == 2) {
                            MessagesController.getInstance().loadChatInfo(chat_id, null, false);
                            Bundle args = new Bundle();
                            args.putInt("chat_id", chat_id);
                            ProfileActivity fragment = new ProfileActivity(args);
                            presentFragment(fragment);
                            return;
                        } else if (picClick == 1) {
                            TLRPC.Chat chat = MessagesController.getInstance().getChat(chat_id);
                            if (chat.photo != null && chat.photo.photo_big != null) {
                                PhotoViewer.getInstance().setParentActivity(getParentActivity());
                                PhotoViewer.getInstance().openPhoto(chat.photo.photo_big, DialogsActivity.this);
                            }
                            return;
                        }
                    }
                }

                //
                if (onlySelect) {
                    didSelectResult(dialog_id, true, false);
                } else {
                    Bundle args = new Bundle();
                    int lower_part = (int) dialog_id;
                    int high_id = (int) (dialog_id >> 32);
                    if (lower_part != 0) {
                        if (high_id == 1) {
                            args.putInt("chat_id", lower_part);
                        } else {
                            if (lower_part > 0) {
                                args.putInt("user_id", lower_part);
                            } else if (lower_part < 0) {
                                if (message_id != 0) {
                                    TLRPC.Chat chat = MessagesController.getInstance().getChat(-lower_part);
                                    if (chat != null && chat.migrated_to != null) {
                                        args.putInt("migrated_to", lower_part);
                                        lower_part = -chat.migrated_to.channel_id;
                                    }
                                }
                                args.putInt("chat_id", -lower_part);
                            }
                        }
                    } else {
                        args.putInt("enc_id", high_id);
                    }
                    if (message_id != 0) {
                        args.putInt("message_id", message_id);
                    } else {
                        if (actionBar != null) {
                            actionBar.closeSearchField();
                        }
                    }
                    if (AndroidUtilities.isTablet()) {
                        if (openedDialogId == dialog_id && adapter != dialogsSearchAdapter) {
                            return;
                        }
                        if (dialogsAdapter != null) {
                            dialogsAdapter.setOpenedDialogId(openedDialogId = dialog_id);
                            updateVisibleRows(MessagesController.UPDATE_MASK_SELECT_DIALOG);
                        }
                    }
                    if (searchString != null) {
                        if (MessagesController.checkCanOpenChat(args, DialogsActivity.this)) {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats);
                            presentFragment(new ChatActivity(args));
                        }
                    } else {
                        if (MessagesController.checkCanOpenChat(args, DialogsActivity.this)) {
                            args.putBoolean("fromHiddens", dialogsType == 10);
                            presentFragment(new ChatActivity(args));
                        }
                    }
                }
            }
        });


        listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {

            class C26361 implements DialogInterface.OnClickListener {
                C26361() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    if (DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed()) {
                        DialogsActivity.this.dialogsSearchAdapter.clearRecentSearch();
                    } else {
                        DialogsActivity.this.dialogsSearchAdapter.clearRecentHashtags();
                    }
                }
            }

            public boolean onItemClick(View view, int position) {
                if (!DialogsActivity.this.onlySelect && ((!DialogsActivity.this.searching || !DialogsActivity.this.searchWas) && DialogsActivity.this.getParentActivity() != null)) {
                    ArrayList<TLRPC.TL_dialog> dialogs = DialogsActivity.this.getDialogsArray();
                    if (position < 0 || position >= dialogs.size()) {
                        return false;
                    }
                    TLRPC.TL_dialog dialog = (TLRPC.TL_dialog) dialogs.get(position);
                    DialogsActivity.this.selectedDialog = dialog.id;
                    final boolean pinned = dialog.pinned;
                    BottomSheet.Builder builder = new BottomSheet.Builder(DialogsActivity.this.getParentActivity());
                    int lower_id = (int) DialogsActivity.this.selectedDialog;
                    int high_id = (int) (DialogsActivity.this.selectedDialog >> 32);
                    int muted = MessagesController.getInstance().isDialogMuted(DialogsActivity.this.selectedDialog) ? R.drawable.mute_grey : 0;
                    CharSequence muteText = muted != 0 ? LocaleController.getString("UnmuteNotifications", R.string.UnmuteNotifications) : LocaleController.getString("MuteNotifications", R.string.MuteNotifications);
                    final boolean isFav = Favourite.isFavourite(Long.valueOf(dialog.id));
                    CharSequence favoriteText = isFav ? LocaleController.getString("DeleteFromFavorites", R.string.DeleteFromFavorites) : LocaleController.getString("AddToFavorites", R.string.AddToFavorites);
                    int i;
                    String string;
                    if (DialogObject.isChannel(dialog)) {
                        CharSequence[] items;
                        final TLRPC.Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-lower_id));
                        int[] icons = new int[9];
                        if (dialog.pinned) {
                            i = R.drawable.chats_unpin;
                        } else {
                            i = R.drawable.chats_pin;
                        }
                        icons[0] = i;
                        if (muted != 0) {
                            i = R.drawable.dialog_unmute;
                        } else {
                            i = R.drawable.dialog_mute;
                        }
                        icons[1] = i;
                        if (isFav) {
                            i = R.drawable.dialog_remove_fav;
                        } else {
                            i = R.drawable.dialog_add_fav;
                        }
                        icons[2] = i;
                        icons[3] = R.drawable.post_views_w;
                        icons[4] = R.drawable.dialog_shortcut;
                        // icons[5] = R.drawable.dialog_hide;
                        icons[6] = R.drawable.ic_widgets_white_24dp;
                        icons[7] = R.drawable.chats_clear;
                        icons[8] = R.drawable.chats_leave;
                        if (chat == null || !chat.megagroup) {
                            items = new CharSequence[9];
                            string = (dialog.pinned || MessagesController.getInstance().canPinDialog(false)) ? dialog.pinned ? LocaleController.getString("UnpinFromTop", R.string.UnpinFromTop) : LocaleController.getString("PinToTop", R.string.PinToTop) : null;
                            items[0] = string;
                            items[1] = muteText;
                            items[2] = favoriteText;
                            items[3] = LocaleController.getString("MarkAsRead", R.string.MarkAsRead);
                            items[4] = LocaleController.getString("AddShortcut", R.string.AddShortcut);
                            // items[5] = LocaleController.getString("hideChat", R.string.hideChat);
                            items[6] = LocaleController.getString("addToCategory", R.string.addToCategory);
                            items[7] = LocaleController.getString("ClearHistoryCache", R.string.ClearHistoryCache);
                            if (chat == null || !chat.creator) {
                                string = LocaleController.getString("LeaveChannelMenu", R.string.LeaveChannelMenu);
                            } else {
                                string = LocaleController.getString("ChannelDeleteMenu", R.string.ChannelDeleteMenu);
                            }
                            items[8] = string;
                        } else {
                            items = new CharSequence[9];
                            string = (dialog.pinned || MessagesController.getInstance().canPinDialog(false)) ? dialog.pinned ? LocaleController.getString("UnpinFromTop", R.string.UnpinFromTop) : LocaleController.getString("PinToTop", R.string.PinToTop) : null;
                            items[0] = string;
                            items[1] = muteText;
                            items[2] = favoriteText;
                            items[3] = LocaleController.getString("MarkAsRead", R.string.MarkAsRead);
                            items[4] = LocaleController.getString("AddShortcut", R.string.AddShortcut);
                            // items[5] = LocaleController.getString("hideChat", R.string.hideChat);
                            items[6] = LocaleController.getString("addToCategory", R.string.addToCategory);
                            items[7] = LocaleController.getString("ClearHistoryCache", R.string.ClearHistoryCache);
                            if (chat == null || !chat.creator) {
                                string = LocaleController.getString("LeaveMegaMenu", R.string.LeaveMegaMenu);
                            } else {
                                string = LocaleController.getString("DeleteMegaMenu", R.string.DeleteMegaMenu);
                            }
                            items[8] = string;
                        }
                        builder.setItems(items, icons, new DialogInterface.OnClickListener() {

                            class C26371 implements DialogInterface.OnClickListener {
                                C26371() {
                                }

                                public void onClick(DialogInterface dialogInterface, int i) {
                                    MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 2);
                                }
                            }

                            class C26382 implements DialogInterface.OnClickListener {
                                C26382() {
                                }

                                public void onClick(DialogInterface dialogInterface, int i) {
                                    MessagesController.getInstance().deleteUserFromChat((int) (-DialogsActivity.this.selectedDialog), UserConfig.getCurrentUser(), null);
                                    if (AndroidUtilities.isTablet()) {
                                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, Long.valueOf(DialogsActivity.this.selectedDialog));
                                    }
                                }
                            }

                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    if (MessagesController.getInstance().pinDialog(DialogsActivity.this.selectedDialog, !pinned, null, 0) && !pinned) {
                                        DialogsActivity.this.listView.smoothScrollToPosition(0);
                                    }
                                } else if (which == 1) {
                                    SharedPreferences.Editor editor;
                                    if (MessagesController.getInstance().isDialogMuted(DialogsActivity.this.selectedDialog)) {
                                        editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                                        editor.putInt("notify2_" + DialogsActivity.this.selectedDialog, 0);
                                        MessagesStorage.getInstance().setDialogFlags(DialogsActivity.this.selectedDialog, 0);
                                        editor.commit();
                                        dialg = (TLRPC.TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(DialogsActivity.this.selectedDialog));
                                        if (dialg != null) {
                                            dialg.notify_settings = new TLRPC.TL_peerNotifySettings();
                                        }
                                        NotificationsController.updateServerNotificationsSettings(DialogsActivity.this.selectedDialog);
                                        return;
                                    }
                                    editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                                    editor.putInt("notify2_" + DialogsActivity.this.selectedDialog, 2);
                                    MessagesStorage.getInstance().setDialogFlags(DialogsActivity.this.selectedDialog, 1);
                                    editor.commit();
                                    dialg = (TLRPC.TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(DialogsActivity.this.selectedDialog));
                                    if (dialg != null) {
                                        dialg.notify_settings = new TLRPC.TL_peerNotifySettings();
                                    }
                                    NotificationsController.updateServerNotificationsSettings(DialogsActivity.this.selectedDialog);
                                } else if (which == 2) {
                                    dialg = (TLRPC.TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(DialogsActivity.this.selectedDialog));
                                    if (isFav) {
                                        Favourite.deleteFavourite(Long.valueOf(DialogsActivity.this.selectedDialog));
                                        MessagesController.getInstance().dialogsFavs.remove(dialg);
                                    } else {
                                        Favourite.addFavourite(Long.valueOf(DialogsActivity.this.selectedDialog));
                                        MessagesController.getInstance().dialogsFavs.add(dialg);
                                    }
                                    if (DialogsActivity.this.dialogsType == 8) {
                                        DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
                                        // if (!DialogsActivity.this.hideTabs) {
                                        DialogsActivity.this.updateTabs();
                                        //}
                                    }
                                    DialogsActivity.this.unreadCount(MessagesController.getInstance().dialogsFavs, DialogsActivity.this.favsCounter);
                                } else if (which == 3) {
                                    DialogsActivity.this.markAsReadDialog(false);
                                } else if (which == 4) {
                                    DialogsActivity.this.addShortcut();
                                } else if (which == 5) {
                                    //DialogsActivity.this.insertHidden((int) DialogsActivity.this.selectedDialog, 0);
                                } else if (which == 6) {
                                    DialogsActivity.this.addtoCategory(DialogsActivity.this.selectedDialog, getParentActivity());
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
                                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                    if (which == 7) {
                                        if (chat == null || !chat.megagroup) {
                                            builder.setMessage(LocaleController.getString("AreYouSureClearHistoryChannel", R.string.AreYouSureClearHistoryChannel));
                                        } else {
                                            builder.setMessage(LocaleController.getString("AreYouSureClearHistorySuper", R.string.AreYouSureClearHistorySuper));
                                        }
                                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C26371());
                                    } else {
                                        if (chat == null || !chat.megagroup) {
                                            if (chat == null || !chat.creator) {
                                                Long mid = Long.valueOf(DialogsActivity.this.selectedDialog);
                                                if (mid.longValue() < 0) {
                                                    mid = Long.valueOf(mid.longValue() * -1);
                                                }
                                                if (mid.longValue() == 1076565958) {
                                                    //DialogsActivity.this.showIrangramLEaveDialog();
                                                    return;
                                                } else if (!NoQuitContoller.isNoQuit(chat.username).booleanValue()) {
                                                    if (TurnQuitToHideController.is(chat.username).booleanValue()) {
                                                        hideChannelController.add(chat.username);
                                                        MessagesController.getInstance().sortDialogs(null);
                                                        return;
                                                    }
                                                    builder.setMessage(LocaleController.getString("ChannelLeaveAlert", R.string.ChannelLeaveAlert));
                                                } else {
                                                    return;
                                                }
                                            }
                                            builder.setMessage(LocaleController.getString("ChannelDeleteAlert", R.string.ChannelDeleteAlert));
                                        } else if (chat.creator) {
                                            builder.setMessage(LocaleController.getString("MegaDeleteAlert", R.string.MegaDeleteAlert));
                                        } else {
                                            builder.setMessage(LocaleController.getString("MegaLeaveAlert", R.string.MegaLeaveAlert));
                                        }
                                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C26382());
                                    }
                                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                    DialogsActivity.this.showDialog(builder.create());
                                }
                            }
                        });
                        DialogsActivity.this.showDialog(builder.create());
                    } else {
                        int[] iArr;
                        final boolean isChat = lower_id < 0 && high_id != 1;
                        TLRPC.User user = null;
                        if (!(isChat || lower_id <= 0 || high_id == 1)) {
                            user = MessagesController.getInstance().getUser(Integer.valueOf(lower_id));
                        }
                        final boolean isBot = user != null && user.bot;
                        final TLRPC.User finalUser = user;
                        CharSequence[] charSequenceArr = new CharSequence[10];
                        if (!dialog.pinned) {
                            if (!MessagesController.getInstance().canPinDialog(lower_id == 0)) {
                                string = dialog.pinned ? LocaleController.getString("UnpinFromTop", R.string.UnpinFromTop) : LocaleController.getString("PinToTop", R.string.PinToTop);
                                charSequenceArr[0] = string;
                                charSequenceArr[1] = muteText;
                                charSequenceArr[2] = favoriteText;
                                charSequenceArr[3] = LocaleController.getString("MarkAsRead", R.string.MarkAsRead);
                                charSequenceArr[4] = LocaleController.getString("AddShortcut", R.string.AddShortcut);
                                // charSequenceArr[5] = LocaleController.getString("hideChat", R.string.hideChat);
                                 charSequenceArr[6] = LocaleController.getString("addToCategory", R.string.addToCategory);
                                charSequenceArr[7] = LocaleController.getString("ClearHistory", R.string.ClearHistory);
                                string = isChat ? LocaleController.getString("DeleteChat", R.string.DeleteChat) : isBot ? LocaleController.getString("DeleteAndStop", R.string.DeleteAndStop) : LocaleController.getString("Delete", R.string.Delete);

                                if (!isBot || isChat) {
                                    charSequenceArr[8] = string;
                                } else {
                                    string = LocaleController.getString("hideChat", R.string.hideChat);
                                }
                                charSequenceArr[9] = string;
                                iArr = new int[10];
                                if (dialog.pinned) {
                                    i = R.drawable.chats_pin;
                                } else {
                                    i = R.drawable.chats_unpin;
                                }
                                iArr[0] = i;
                                if (muted == 0) {
                                    i = R.drawable.dialog_unmute;
                                } else {
                                    i = R.drawable.dialog_mute;
                                }
                                iArr[1] = i;
                                if (isFav) {
                                    i = R.drawable.dialog_add_fav;
                                } else {
                                    i = R.drawable.dialog_remove_fav;
                                }
                                iArr[2] = i;
                                iArr[3] = R.drawable.post_views_w;
                                iArr[4] = R.drawable.dialog_shortcut;
                                //iArr[5] = R.drawable.dialog_hide;
                                iArr[6] = R.drawable.ic_widgets_white_24dp;
                                iArr[7] = R.drawable.chats_clear;
                                if (isChat) {
                                    i = R.drawable.chats_delete;
                                } else {
                                    i = R.drawable.chats_leave;
                                }
                                iArr[8] = i;
                                iArr[9] = R.drawable.ic_visibility_off_white_24dp;
                                builder.setItems(charSequenceArr, iArr, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            if (MessagesController.getInstance().pinDialog(DialogsActivity.this.selectedDialog, !pinned, null, 0) && !pinned) {
                                                DialogsActivity.this.listView.smoothScrollToPosition(0);
                                            }
                                        } else if (which == 1) {
                                            SharedPreferences.Editor editor;
                                            if (MessagesController.getInstance().isDialogMuted(DialogsActivity.this.selectedDialog)) {
                                                editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                                                editor.putInt("notify2_" + DialogsActivity.this.selectedDialog, 0);
                                                MessagesStorage.getInstance().setDialogFlags(DialogsActivity.this.selectedDialog, 0);
                                                editor.commit();
                                                dialg = (TLRPC.TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(DialogsActivity.this.selectedDialog));
                                                if (dialg != null) {
                                                    dialg.notify_settings = new TLRPC.TL_peerNotifySettings();
                                                }
                                                NotificationsController.updateServerNotificationsSettings(DialogsActivity.this.selectedDialog);
                                                return;
                                            }
                                            editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                                            editor.putInt("notify2_" + DialogsActivity.this.selectedDialog, 2);
                                            MessagesStorage.getInstance().setDialogFlags(DialogsActivity.this.selectedDialog, 1);
                                            editor.commit();
                                            dialg = (TLRPC.TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(DialogsActivity.this.selectedDialog));
                                            if (dialg != null) {
                                                dialg.notify_settings = new TLRPC.TL_peerNotifySettings();
                                            }
                                            NotificationsController.updateServerNotificationsSettings(DialogsActivity.this.selectedDialog);
                                        } else if (which == 2) {
                                            dialg = (TLRPC.TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(DialogsActivity.this.selectedDialog));
                                            if (isFav) {
                                                Favourite.deleteFavourite(Long.valueOf(DialogsActivity.this.selectedDialog));
                                                MessagesController.getInstance().dialogsFavs.remove(dialg);
                                            } else {
                                                Favourite.addFavourite(Long.valueOf(DialogsActivity.this.selectedDialog));
                                                MessagesController.getInstance().dialogsFavs.add(dialg);
                                            }
                                            if (DialogsActivity.this.dialogsType == 8) {
                                                DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
                                                //if (!DialogsActivity.this.hideTabs) {
                                                DialogsActivity.this.updateTabs();
                                                //}
                                            }
                                            DialogsActivity.this.unreadCount(MessagesController.getInstance().dialogsFavs, DialogsActivity.this.favsCounter);
                                        } else if (which == 3) {
                                            DialogsActivity.this.markAsReadDialog(false);
                                        } else if (which == 4) {
                                            DialogsActivity.this.addShortcut();
                                        } else if (which == 5) {
                                            // DialogsActivity.this.insertHidden((int) DialogsActivity.this.selectedDialog, 0);
                                        } else if (which == 6) {
                                              DialogsActivity.this.addtoCategory(DialogsActivity.this.selectedDialog, context);
                                        } else if (which == 9) {
                                            insertHidden((int) selectedDialog, dialogsType == 10 ? 1 : 0);
                                           // DialogsActivity.this.addspecefic(finalUser, getParentActivity());
                                        } else {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
                                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                            if (which == 7) {
                                                builder.setMessage(LocaleController.getString("AreYouSureClearHistory", R.string.AreYouSureClearHistory));
                                            } else if (isChat) {
                                                builder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", R.string.AreYouSureDeleteAndExit));
                                            } else {
                                                builder.setMessage(LocaleController.getString("AreYouSureDeleteThisChat", R.string.AreYouSureDeleteThisChat));
                                            }
                                            final int i = which;
                                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    if (i != 7) {
                                                        if (isChat) {
                                                            TLRPC.Chat currentChat = MessagesController.getInstance().getChat(Integer.valueOf((int) (-DialogsActivity.this.selectedDialog)));
                                                            if (currentChat == null || !ChatObject.isNotInChat(currentChat)) {
                                                                MessagesController.getInstance().deleteUserFromChat((int) (-DialogsActivity.this.selectedDialog), MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), null);
                                                            } else {
                                                                MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 0);
                                                            }
                                                        } else {
                                                            MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 0);
                                                        }
                                                        if (isBot) {
                                                            MessagesController.getInstance().blockUser((int) DialogsActivity.this.selectedDialog);
                                                        }
                                                        if (AndroidUtilities.isTablet()) {
                                                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, Long.valueOf(DialogsActivity.this.selectedDialog));
                                                            return;
                                                        }
                                                        return;
                                                    }
                                                    MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 1);
                                                }
                                            });
                                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                            DialogsActivity.this.showDialog(builder.create());
                                        }
                                    }
                                });
                                DialogsActivity.this.showDialog(builder.create());
                            }
                        }
                        string = dialog.pinned ? LocaleController.getString("UnpinFromTop", R.string.UnpinFromTop) : LocaleController.getString("PinToTop", R.string.PinToTop);
                        charSequenceArr[0] = string;
                        charSequenceArr[1] = muteText;
                        charSequenceArr[2] = favoriteText;
                        charSequenceArr[3] = LocaleController.getString("MarkAsRead", R.string.MarkAsRead);
                        charSequenceArr[4] = LocaleController.getString("AddShortcut", R.string.AddShortcut);
                        //charSequenceArr[5] = LocaleController.getString("hideChat", R.string.hideChat);
                        charSequenceArr[6] = LocaleController.getString("addToCategory", R.string.addToCategory);
                        charSequenceArr[7] = LocaleController.getString("ClearHistory", R.string.ClearHistory);
                        if (isChat) {
                            //charSequenceArr[7] = LocaleController.getString("Leave", R.string.DeleteThisChat);
                        }
                        String string2 = LocaleController.getString("Leave", R.string.DeleteThisChat);
                        charSequenceArr[8] = string2 ;;
                        if (isBot) {
                            charSequenceArr[8] = LocaleController.getString("Leave", R.string.DeleteAndStop);
                        }
                        string = LocaleController.getString("hideChat", R.string.hideChat);
                        charSequenceArr[9] = string;
                        iArr = new int[10];
                        if (dialog.pinned) {
                            i = R.drawable.chats_pin;
                        } else {
                            i = R.drawable.chats_unpin;
                        }
                        iArr[0] = i;
                        if (muted == 0) {
                            i = R.drawable.dialog_mute;
                        } else {
                            i = R.drawable.dialog_unmute;
                        }
                        iArr[1] = i;
                        if (isFav) {
                            i = R.drawable.dialog_add_fav;
                        } else {
                            i = R.drawable.dialog_remove_fav;
                        }
                        iArr[2] = i;
                        iArr[3] = R.drawable.post_views_w;
                        iArr[4] = R.drawable.dialog_shortcut;
                        //iArr[5] = R.drawable.dialog_hide;
                        iArr[6] = R.drawable.ic_widgets_white_24dp;
                        iArr[7] = R.drawable.chats_clear;
                        if (isChat) {
                            i = R.drawable.chats_delete;
                        } else {
                            i = R.drawable.chats_leave;
                        }
                        iArr[8] = i;
                        iArr[9] = R.drawable.ic_visibility_off_white_24dp;
                        builder.setItems(charSequenceArr, iArr,new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    if (MessagesController.getInstance().pinDialog(DialogsActivity.this.selectedDialog, !pinned, null, 0) && !pinned) {
                                        DialogsActivity.this.listView.smoothScrollToPosition(0);
                                    }
                                } else if (which == 1) {
                                    SharedPreferences.Editor editor;
                                    if (MessagesController.getInstance().isDialogMuted(DialogsActivity.this.selectedDialog)) {
                                        editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                                        editor.putInt("notify2_" + DialogsActivity.this.selectedDialog, 0);
                                        MessagesStorage.getInstance().setDialogFlags(DialogsActivity.this.selectedDialog, 0);
                                        editor.commit();
                                        dialg = (TLRPC.TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(DialogsActivity.this.selectedDialog));
                                        if (dialg != null) {
                                            dialg.notify_settings = new TLRPC.TL_peerNotifySettings();
                                        }
                                        NotificationsController.updateServerNotificationsSettings(DialogsActivity.this.selectedDialog);
                                        return;
                                    }
                                    editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                                    editor.putInt("notify2_" + DialogsActivity.this.selectedDialog, 2);
                                    MessagesStorage.getInstance().setDialogFlags(DialogsActivity.this.selectedDialog, 1);
                                    editor.commit();
                                    dialg = (TLRPC.TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(DialogsActivity.this.selectedDialog));
                                    if (dialg != null) {
                                        dialg.notify_settings = new TLRPC.TL_peerNotifySettings();
                                    }
                                    NotificationsController.updateServerNotificationsSettings(DialogsActivity.this.selectedDialog);
                                } else if (which == 2) {
                                    dialg = (TLRPC.TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(DialogsActivity.this.selectedDialog));
                                    if (isFav) {
                                        Favourite.deleteFavourite(Long.valueOf(DialogsActivity.this.selectedDialog));
                                        MessagesController.getInstance().dialogsFavs.remove(dialg);
                                    } else {
                                        Favourite.addFavourite(Long.valueOf(DialogsActivity.this.selectedDialog));
                                        MessagesController.getInstance().dialogsFavs.add(dialg);
                                    }
                                    if (DialogsActivity.this.dialogsType == 8) {
                                        DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
                                        //if (!DialogsActivity.this.hideTabs) {
                                        DialogsActivity.this.updateTabs();
                                        // }
                                    }
                                    DialogsActivity.this.unreadCount(MessagesController.getInstance().dialogsFavs, DialogsActivity.this.favsCounter);
                                } else if (which == 3) {
                                    DialogsActivity.this.markAsReadDialog(false);
                                } else if (which == 4) {
                                    DialogsActivity.this.addShortcut();
                                } else if (which == 5) {
                                    // DialogsActivity.this.insertHidden((int) DialogsActivity.this.selectedDialog, 0);
                                } else if (which == 6) {
                                    DialogsActivity.this.addtoCategory(DialogsActivity.this.selectedDialog, getParentActivity());
                                } else if (which == 9) {
                                   // DialogsActivity.this.addspecefic(finalUser, getParentActivity());
                                    insertHidden(finalUser.id, dialogsType == 10 ? 1 : 0);
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
                                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                    if (which == 7) {
                                        builder.setMessage(LocaleController.getString("AreYouSureClearHistory", R.string.AreYouSureClearHistory));
                                    } else if (isChat) {
                                        builder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", R.string.AreYouSureDeleteAndExit));
                                    } else {
                                        builder.setMessage(LocaleController.getString("AreYouSureDeleteThisChat", R.string.AreYouSureDeleteThisChat));
                                    }
                                    final int i = which;
                                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (i != 7) {
                                                if (isChat) {
                                                    TLRPC.Chat currentChat = MessagesController.getInstance().getChat(Integer.valueOf((int) (-DialogsActivity.this.selectedDialog)));
                                                    if (currentChat == null || !ChatObject.isNotInChat(currentChat)) {
                                                        MessagesController.getInstance().deleteUserFromChat((int) (-DialogsActivity.this.selectedDialog), MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), null);
                                                    } else {
                                                        MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 0);
                                                    }
                                                } else {
                                                    MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 0);
                                                }
                                                if (isBot) {
                                                    MessagesController.getInstance().blockUser((int) DialogsActivity.this.selectedDialog);
                                                }
                                                if (AndroidUtilities.isTablet()) {
                                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, Long.valueOf(DialogsActivity.this.selectedDialog));
                                                    return;
                                                }
                                                return;
                                            }
                                            MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 1);
                                        }
                                    });
                                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                    DialogsActivity.this.showDialog(builder.create());
                                }
                            }
                        });
                        DialogsActivity.this.showDialog(builder.create());
                    }
                    return true;
                } else if (((!DialogsActivity.this.searchWas || !DialogsActivity.this.searching) && !DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed()) || DialogsActivity.this.listView.getAdapter() != DialogsActivity.this.dialogsSearchAdapter || (!(DialogsActivity.this.dialogsSearchAdapter.getItem(position) instanceof String) && !DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed())) {
                    return false;
                } else {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
                    builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder2.setMessage(LocaleController.getString("ClearSearch", R.string.ClearSearch));
                    builder2.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new C26361());
                    builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    DialogsActivity.this.showDialog(builder2.create());
                    return true;
                }
            }
        });





























        searchEmptyView = new
        EmptyTextProgressView(context);

        searchEmptyView.setVisibility(View.GONE);
        searchEmptyView.setShowAtCenter(true);
        searchEmptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        frameLayout.addView(searchEmptyView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        emptyView = new LinearLayout(context);
        emptyView.setOrientation(LinearLayout.VERTICAL);
        emptyView.setVisibility(View.GONE);
        emptyView.setGravity(Gravity.CENTER);
        frameLayout.addView(emptyView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        //emptyView.setOnTouchListener(new View.OnTouchListener() {
        //
        //    @Override
        //    public boolean onTouch(View v, MotionEvent event) {
        //        return true;
        //    }
        //});
        emptyView.setOnTouchListener(onTouchListener);
        TextView textView = new TextView(context);
        textView.setText(LocaleController.getString("NoChats", R.string.NoChats));
        textView.setTextColor(0xff959595);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        emptyView.addView(textView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        textView = new

                TextView(context);

        String help = LocaleController.getString("NoChatsHelp", R.string.NoChatsHelp);
        if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet())

        {
            help = help.replace('\n', ' ');
        }

        textView.setText(help);
        textView.setTextColor(0xff959595);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(6), AndroidUtilities.dp(8), 0);
        textView.setLineSpacing(AndroidUtilities.dp(2), 1);
        emptyView.addView(textView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        progressView = new

                ProgressBar(context);

        progressView.setVisibility(View.GONE);
        frameLayout.addView(progressView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));


        hiden_login = new FrameLayout(context);

        final EditText pass1 = new EditText(context);
        pass1.setHint(R.string.password);
        pass1.setInputType(InputType.TYPE_CLASS_NUMBER);
        pass1.setTransformationMethod(PasswordTransformationMethod.getInstance());
        hiden_login.addView(pass1, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 0, 70, 0, 20));

        final EditText pass2 = new EditText(context);
        pass2.setHint(R.string.passwordRepeaat);
        pass2.setInputType(InputType.TYPE_CLASS_NUMBER);
        hiden_login.addView(pass2, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 0, 130, 0, 20));

        final String mPass = sharedPreferences.getString("hidepasswoed", null);

        if (mPass != null) {
            pass2.setVisibility(View.GONE);
        }

        int def = themePrefs.getInt("themeColor", Theme.getColor(Theme.key_actionBarDefault));
        Button start_hiden = new Button(context);
        start_hiden.setText(R.string.hidenSet);
        start_hiden.setBackgroundColor(def);
        hiden_login.addView(start_hiden, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 0, 200, 0, 20));
        frameLayout.addView(hiden_login, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER, 16, 50, 16, 0));


        start_hiden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPass == null) {
                    if ((pass1.getText() == null || pass1.getText().length() < 4)) {
                        pass1.setError(context.getResources().getString(R.string.passError));
                    } else if ((pass2.getText() == null || !pass1.getText().toString().equals(pass2.getText().toString()))) {
                        pass2.setError(context.getResources().getString(R.string.pass2Error));
                    } else {
                        sharedPreferences.edit().putString("hidepasswoed", pass1.getText().toString()).commit();
                        pass2.setVisibility(View.GONE);
                        pass1.setText("");
                        emptyView.setVisibility(View.GONE);
                        hiden_login.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);

                    }
                } else {
                    if ((pass1.getText() == null || pass1.getText().length() < 4 || !pass1.getText().toString().equals(mPass))) {
                        pass1.setError(context.getResources().getString(R.string.pass3Error));
                    } else {
                        pass1.setText("");
                        listView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                        hiden_login.setVisibility(View.GONE);


                    }
                }
            }
        });


        new PmSettingPacket().Send();

        floatingButton = new ImageView(context);


        floatingButton.setVisibility(onlySelect ? View.GONE : View.VISIBLE);
        floatingButton.setScaleType(ImageView.ScaleType.CENTER);
        floatingButton.setBackgroundResource(R.drawable.floating_pencil);
        floatingButton.setImageResource(R.drawable.floating_pencil);

        if (Build.VERSION.SDK_INT >= 21) {

            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{android.R.attr.state_pressed}, ObjectAnimator.ofFloat(floatingButton, "translationZ", AndroidUtilities.dp(2), AndroidUtilities.dp(4)).setDuration(200));
            animator.addState(new int[]{}, ObjectAnimator.ofFloat(floatingButton, "translationZ", AndroidUtilities.dp(4), AndroidUtilities.dp(2)).setDuration(200));
            floatingButton.setStateListAnimator(animator);
            floatingButton.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint("NewApi")
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56), AndroidUtilities.dp(56));
                }
            });


        }
        SharedPreferences mpref = ApplicationLoader.applicationContext.getSharedPreferences("gelasConfig", Activity.MODE_PRIVATE);
        ShowTabsInBottomRow = mpref.getBoolean("ShowTabsInBottomRow", false);









        frameLayout.addView(floatingButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.BOTTOM, LocaleController.isRTL ? 14 : 0, 0, LocaleController.isRTL ? 0 : 14, ShowTabsInBottomRow ? 40 : 14));
        floatingButton.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  Bundle args = new Bundle();
                                                  args.putBoolean("destroyAfterSelect", true);
                                                  presentFragment(new ContactsActivity(args));
                                              }
                                          }

        );

        tabsView = new FrameLayout(context);

        createTabs(context);


        //if(dialogsType == 0 || dialogsType > 2){
        frameLayout.addView(tabsView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, tabsHeight, ShowTabsInBottomRow ? Gravity.BOTTOM : Gravity.TOP, 0, 0, 0, 0));
        //}

        final int hColor = themePrefs.getInt("chatsHeaderColor", def);
        listView.setOnScrollListener(new RecyclerView.OnScrollListener()

                                     {
                                         @Override
                                         public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                             if (newState == RecyclerView.SCROLL_STATE_DRAGGING && searching && searchWas) {
                                                 AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                                             }
                                             Glow.setEdgeGlowColor(listView, hColor);
                                         }

                                         @Override
                                         public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                             int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                                             int visibleItemCount = Math.abs(layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                                             int totalItemCount = recyclerView.getAdapter().getItemCount();

                                             if (searching && searchWas) {
                                                 if (visibleItemCount > 0 && layoutManager.findLastVisibleItemPosition() == totalItemCount - 1 && !dialogsSearchAdapter.isMessagesSearchEndReached()) {
                                                     dialogsSearchAdapter.loadMoreSearchMessages();
                                                 }
                                                 return;
                                             }
                                             if (visibleItemCount > 0) {
                                                 if (layoutManager.findLastVisibleItemPosition() >= getDialogsArray().size() - 10) {
                                                     MessagesController.getInstance().loadDialogs(-1, 100, !MessagesController.getInstance().dialogsEndReached);
                                                 }
                                             }

                                             if (floatingButton.getVisibility() != View.GONE) {
                                                 final View topChild = recyclerView.getChildAt(0);
                                                 int firstViewTop = 0;
                                                 if (topChild != null) {
                                                     firstViewTop = topChild.getTop();
                                                 }
                                                 boolean goingDown;
                                                 boolean changed = true;
                                                 if (prevPosition == firstVisibleItem) {
                                                     final int topDelta = prevTop - firstViewTop;
                                                     goingDown = firstViewTop < prevTop;
                                                     changed = Math.abs(topDelta) > 1;
                                                 } else {
                                                     goingDown = firstVisibleItem > prevPosition;
                                                 }
                                                 if (changed && scrollUpdated) {
                                                     if (!hideTabs && !disableAnimation || hideTabs)
                                                         hideFloatingButton(goingDown);
                                                 }
                                                 prevPosition = firstVisibleItem;
                                                 prevTop = firstViewTop;
                                                 scrollUpdated = true;
                                             }

                                             if (!hideTabs) {
                                                 //if(!disableAnimation) {
                                                 if (dy > 1) {
                                                     //Down (HIDE)
                                                     if (recyclerView.getChildAt(0).getTop() < 0) {
                                                         if (!disableAnimation) {
                                                             // hideTabsAnimated(true);
                                                         } else {
                                                             hideFloatingButton(true);
                                                         }
                                                     }

                                                 }
                                                 if (dy < -1) {
                                                     //Up (SHOW)
                                                     if (!disableAnimation) {
                                                         //hideTabsAnimated(false);
                                                         if (firstVisibleItem == 0) {
                                                             SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("gelasConfig", Activity.MODE_PRIVATE);
                                                             boolean disable = preferences.getBoolean("ShowTabsInBottomRow", false);
                                                             listView.setPadding(0, AndroidUtilities.dp(disable ? 0 : tabsHeight), 0, 0);
                                                         }
                                                     } else {
                                                         hideFloatingButton(false);
                                                     }
                                                 }
                                                 //}
                                             }
                                         }
                                     }

        );

        if (searchString == null)

        {
            dialogsAdapter = new DialogsAdapter(context, dialogsType);
            if (AndroidUtilities.isTablet() && openedDialogId != 0) {
                dialogsAdapter.setOpenedDialogId(openedDialogId);
            }
            listView.setAdapter(dialogsAdapter);
            dialogsBackupAdapter = dialogsAdapter;
        }

        int type = 0;
        if (searchString != null)

        {
            type = 2;
        } else if (!onlySelect)

        {
            type = 1;
        }

        dialogsSearchAdapter = new DialogsSearchAdapter(context, type, dialogsType);
        dialogsSearchAdapter.setDelegate(new DialogsSearchAdapter.DialogsSearchAdapterDelegate() {
            @Override
            public void searchStateChanged(boolean search) {
                if (searching && searchWas && searchEmptyView != null) {
                    if (search) {
                        searchEmptyView.showProgress();
                    } else {
                        searchEmptyView.showTextView();
                    }
                }
            }

            @Override
            public void didPressedOnSubDialog(int did) {
                if (onlySelect) {
                    didSelectResult(did, true, false);
                } else {
                    Bundle args = new Bundle();
                    if (did > 0) {
                        args.putInt("user_id", did);
                    } else {
                        args.putInt("chat_id", -did);
                    }
                    args.putBoolean("fromHiddens", dialogsType == 10);
                    if (actionBar != null) {
                        actionBar.closeSearchField();
                    }
                    if (AndroidUtilities.isTablet()) {
                        if (dialogsAdapter != null) {
                            dialogsAdapter.setOpenedDialogId(openedDialogId = did);
                            updateVisibleRows(MessagesController.UPDATE_MASK_SELECT_DIALOG);
                        }
                    }
                    if (searchString != null) {
                        if (MessagesController.checkCanOpenChat(args, DialogsActivity.this)) {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats);
                            presentFragment(new ChatActivity(args));
                        }
                    } else {
                        if (MessagesController.checkCanOpenChat(args, DialogsActivity.this)) {
                            presentFragment(new ChatActivity(args));
                        }
                    }
                }
            }

            @Override
            public void needRemoveHint(final int did) {
                if (getParentActivity() == null) {
                    return;
                }
                TLRPC.User user = MessagesController.getInstance().getUser(did);
                if (user == null) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.formatString("ChatHintsDelete", R.string.ChatHintsDelete, ContactsController.formatName(user.first_name, user.last_name)));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SearchQuery.removePeer(did);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder.create());
            }
        });


        if (MessagesController.getInstance().loadingDialogs && MessagesController.getInstance().dialogs.isEmpty())

        {
            searchEmptyView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
            listView.setEmptyView(progressView);
        } else

        {
            searchEmptyView.setVisibility(View.GONE);
            progressView.setVisibility(View.GONE);
            listView.setEmptyView(emptyView);
        }

        if (searchString != null)

        {
            actionBar.openSearchField(searchString);
        }

        //if (!onlySelect && dialogsType == 0) {
        if (!onlySelect && (dialogsType == 0 || dialogsType > 2))

        {
            frameLayout.addView(new PlayerView(context, this), LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 39, Gravity.TOP | Gravity.LEFT, 0, -36, 0, 0));
        }

        toastBtn = new

                Button(context);

        toastBtn.setVisibility(AndroidUtilities.themeUpdated ? View.VISIBLE : View.GONE);
        if (AndroidUtilities.themeUpdated)

        {
            AndroidUtilities.themeUpdated = false;
            String tName = themePrefs.getString("themeName", "");
            //int def = themePrefs.getInt("themeColor", Theme.getColor(Theme.key_actionBarDefault));
            //int hColor = themePrefs.getInt("chatsHeaderColor", def);
            toastBtn.setText(LocaleController.formatString("ThemeUpdated", R.string.ThemeUpdated, tName));
            if (Build.VERSION.SDK_INT >= 14) toastBtn.setAllCaps(false);
            GradientDrawable shape = new GradientDrawable();
            shape.setCornerRadius(AndroidUtilities.dp(4));
            shape.setColor(hColor);
            toastBtn.setBackgroundDrawable(shape);
            toastBtn.setTextColor(tColor);
            toastBtn.setTextSize(16);
            ViewProxy.setTranslationY(toastBtn, -AndroidUtilities.dp(100));
            ObjectAnimatorProxy animator = ObjectAnimatorProxy.ofFloatProxy(toastBtn, "translationY", 0).setDuration(500);
            //animator.setInterpolator(tabsInterpolator);
            animator.start();

            toastBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String packageName = "es.rafalense.themes";
                        if (BuildConfig.DEBUG) packageName = "es.rafalense.themes.beta";
                        Intent intent = ApplicationLoader.applicationContext.getPackageManager().getLaunchIntentForPackage(packageName);
                        if (intent != null) {
                            ApplicationLoader.applicationContext.startActivity(intent);
                        }
                        toastBtn.setVisibility(View.GONE);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            });
            frameLayout.addView(toastBtn, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.CENTER, 0, 10, 0, 0));
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            ObjectAnimatorProxy animator = ObjectAnimatorProxy.ofFloatProxy(toastBtn, "translationY", -AndroidUtilities.dp(100)).setDuration(500);
                            //animator.setInterpolator(tabsInterpolator);
                            animator.start();
                        }
                    });
                }
            }, 4000);
        }


        if (UserConfig.isClientActivated() && sharedPreferences.getBoolean("startBootfromAdv", false)) {
            sharedPreferences.edit().putBoolean("startBootfromAdv", false).commit();
            Bot(sharedPreferences.getInt("startedbootid", 1), sharedPreferences.getString("startedbootusername", ""));
        }










        if (this.shhelp.booleanValue()) {
            showcase();
        }



        return fragmentView;
    }

    public void Bot(final int userId, String username) {
        if (getParentActivity() == null)
            return;

        TLRPC.User user = MessagesController.getInstance().getUser(username);
        if (user == null) {
            MessagesController.getInstance().openByUserNameasHidden(username, getParentActivity());
            return;
        }

        long id = 0;
        if (user != null) {
            id = (long) user.id;
        } else {
            id = userId;
        }
        MessagesController.getInstance().unblockUser((int) id);
        SendMessagesHelper.getInstance().sendMessage("/start", id, null, null, false, null, null, null);

    }


    class C26544 implements DialogInterface.OnClickListener {
        C26544() {
        }

        public void onClick(DialogInterface dialog, int which) {

            DialogsActivity.this.showcase();
        }
    }


  // public void addspecefic(final TLRPC.User finalUser, final Context context) {

  //     if (finalUser != null) {
  //         AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
  //         //builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
  //         builder.setTitle(context.getResources().getString(R.string.AddSpecific));

  //         final Boolean[] items = {true, true, true, false};

  //         builder.setMultiChoiceItems(new CharSequence[]{context.getResources().getString(R.string.picup),
  //                         context.getResources().getString(R.string.statusup),
  //                         context.getResources().getString(R.string.phoneup),
  //                         context.getResources().getString(R.string.isonetime)},
  //                 new boolean[]{true, true, true, false},
  //                 new DialogInterface.OnMultiChoiceClickListener() {
  //                     @Override
  //                     public void onClick(DialogInterface dialog, int which, boolean isChecked) {
  //                         items[which] = isChecked;

  //                     }
  //                 });

  //         builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
  //             @Override
  //             public void onClick(DialogInterface dialogInterface, int i) {

  //                 userDBAdapter db = new userDBAdapter(context);
  //                 db.open();
  //                 db.updateIsSpecific(finalUser.id, 1);
  //                 db.updatePicUp(finalUser.id, items[0] ? 1 : 0);
  //                 db.updateStatusUp(finalUser.id, items[1] ? 1 : 0);
  //                 db.updatePhoneUp(finalUser.id, items[2] ? 1 : 0);
  //                 db.updateIsOneTime(finalUser.id, items[3] ? 1 : 0);
  //                 db.close();
  //             }
  //         });
  //         builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
  //         showDialog(builder.create());
  //     }
  // }


    public  void changeGhostModeState() {


        boolean ghost_mpode = true;
        SharedPreferences sharedPreferences = AndroidUtilities.getUserPrefs("mainconfig", 0);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        boolean mode = sharedPreferences.getBoolean("ghost_mode", false);
        edit.putBoolean("ghost_mode", !mode);
        edit.putBoolean("not_send_read_state", !mode);
        edit.commit();
        if (mode) {
            ghost_mpode = false;
        }

        actionBar.changeGhostModeVisibility();

//        Log.i("TAG", "changeGhostModeState: gost " + ghost_mpode);
        MessagesController.getInstance().reRunUpdateTimerProc();
        Drawable ghosticon = getParentActivity().getResources().getDrawable(R.drawable.ic_masqurade_black);
        if (ghost_mpode) {

            ghosticon = getParentActivity().getResources().getDrawable(R.drawable.ic_masqurade_white);
            Toast.makeText(getParentActivity().getApplicationContext(), getParentActivity().getResources().getString(R.string.gost_disabled), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getParentActivity().getApplicationContext(), getParentActivity().getResources().getString(R.string.gost_enabled), Toast.LENGTH_SHORT).show();

        }
        //ghostItem.setIcon(ghosticon);

        if (ghost_mpode && parentLayout != null) {
            parentLayout.rebuildAllFragmentViews(false,false);
        }
        if (getParentActivity() != null) {
            PhotoViewer.getInstance().destroyPhotoViewer();
            PhotoViewer.getInstance().setParentActivity(getParentActivity());
        }
    }

    private void markAsReadDialog(final boolean all) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        TLRPC.Chat currentChat = MessagesController.getInstance().getChat((int) -selectedDialog);
        TLRPC.User user = MessagesController.getInstance().getUser((int) selectedDialog);
        String title = currentChat != null ? currentChat.title : user != null ? UserObject.getUserName(user) : LocaleController.getString("AppName", R.string.AppName);
        builder.setTitle(all ? getHeaderAllTitles() : title);
        builder.setMessage((all ? LocaleController.getString("MarkAllAsRead", R.string.MarkAllAsRead) : LocaleController.getString("MarkAsRead", R.string.MarkAsRead)) + '\n' + LocaleController.getString("AreYouSure", R.string.AreYouSure));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (all) {
                    ArrayList<TLRPC.TL_dialog> dialogs = getDialogsArray();
                    if (dialogs != null && !dialogs.isEmpty()) {
                        for (int a = 0; a < dialogs.size(); a++) {
                            TLRPC.TL_dialog dialg = getDialogsArray().get(a);
                            if (dialg.unread_count > 0) {
                                MessagesController.getInstance().markDialogAsRead(dialg.id, dialg.last_read, Math.max(0, dialg.top_message), dialg.last_message_date, true, false);
                            }
                        }
                    }
                } else {
                    TLRPC.TL_dialog dialg = MessagesController.getInstance().dialogs_dict.get(selectedDialog);
                    if (dialg.unread_count > 0) {
                        MessagesController.getInstance().markDialogAsRead(dialg.id, dialg.last_read, Math.max(0, dialg.top_message), dialg.last_message_date, true, false);
                    }
                }
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void addShortcut() {
        Intent intent = new Intent(ApplicationLoader.applicationContext, ShortcutActivity.class);
        intent.setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
        intent.setFlags(32768);
        TLRPC.TL_dialog dialg = (TLRPC.TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.selectedDialog));
        TLRPC.Chat currentChat = MessagesController.getInstance().getChat(Integer.valueOf((int) (-this.selectedDialog)));
        TLRPC.User user = MessagesController.getInstance().getUser(Integer.valueOf((int) this.selectedDialog));
        TLRPC.EncryptedChat encryptedChat = null;
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        long dialog_id = dialg.id;
        int lower_id = (int) dialog_id;
        int high_id = (int) (dialog_id >> 32);
        if (lower_id == 0) {
            encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(high_id));
            if (encryptedChat != null) {
                user = MessagesController.getInstance().getUser(Integer.valueOf(encryptedChat.user_id));
                intent.putExtra("encId", high_id);
                avatarDrawable.setInfo(user);
            }
        } else if (high_id == 1) {
            currentChat = MessagesController.getInstance().getChat(Integer.valueOf(lower_id));
            intent.putExtra("chatId", lower_id);
            avatarDrawable.setInfo(currentChat);
        } else if (lower_id < 0) {
            currentChat = MessagesController.getInstance().getChat(Integer.valueOf(-lower_id));
            if (!(currentChat == null || currentChat.migrated_to == null)) {
                TLRPC.Chat chat2 = MessagesController.getInstance().getChat(Integer.valueOf(currentChat.migrated_to.channel_id));
                if (chat2 != null) {
                    currentChat = chat2;
                }
            }
            intent.putExtra("chatId", -lower_id);
            avatarDrawable.setInfo(currentChat);
        } else {
            user = MessagesController.getInstance().getUser(Integer.valueOf(lower_id));
            intent.putExtra("userId", lower_id);
            avatarDrawable.setInfo(user);
        }
        String name = currentChat != null ? currentChat.title : (user == null || encryptedChat != null) ? encryptedChat != null ? new String(Character.toChars(128274)) + UserObject.getUserName(user) : null : UserObject.getUserName(user);
        if (name != null) {
            TLObject photoPath = null;
            if (currentChat != null) {
                if (!(currentChat.photo == null || currentChat.photo.photo_small == null || currentChat.photo.photo_small.volume_id == 0 || currentChat.photo.photo_small.local_id == 0)) {
                    photoPath = currentChat.photo.photo_small;
                }
            } else if (!(user == null || user.photo == null || user.photo.photo_small == null || user.photo.photo_small.volume_id == 0 || user.photo.photo_small.local_id == 0)) {
                photoPath = user.photo.photo_small;
            }
            BitmapDrawable img = null;
            if (photoPath != null) {
                img = ImageLoader.getInstance().getImageFromMemory(photoPath, null, "50_50");
            }
            String action = "com.android.launcher.action.INSTALL_SHORTCUT";
            Intent addIntent = new Intent();
            addIntent.putExtra("android.intent.extra.shortcut.INTENT", intent);
            addIntent.putExtra("android.intent.extra.shortcut.NAME", name);
            if (img != null) {
                addIntent.putExtra("android.intent.extra.shortcut.ICON", getRoundBitmap(img.getBitmap()));
            } else {
                int w = AndroidUtilities.dp(40.0f);
                int h = AndroidUtilities.dp(40.0f);
                Bitmap mutableBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(mutableBitmap);
                avatarDrawable.setBounds(0, 0, w, h);
                avatarDrawable.draw(canvas);
                addIntent.putExtra("android.intent.extra.shortcut.ICON", getRoundBitmap(mutableBitmap));
            }
            addIntent.putExtra("duplicate", false);
            addIntent.setAction(action);
            boolean error = false;
            if (ApplicationLoader.applicationContext.getPackageManager().queryBroadcastReceivers(new Intent(action), 0).size() > 0) {
                ApplicationLoader.applicationContext.sendBroadcast(addIntent);
            } else {
                error = true;
            }
            final String formatString = error ? LocaleController.formatString("ShortcutError", R.string.ShortcutError, new Object[]{name}) : LocaleController.formatString("ShortcutAdded", R.string.ShortcutAdded, new Object[]{name});
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (DialogsActivity.this.getParentActivity() != null) {
                        Toast.makeText(DialogsActivity.this.getParentActivity(), formatString, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private Bitmap getRoundBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int radius = Math.min(h / 2, w / 2);
        Bitmap output = Bitmap.createBitmap(w + 8, h + 8, Bitmap.Config.ARGB_8888);
        Paint p = new Paint();
        p.setAntiAlias(true);
        Canvas c = new Canvas(output);
        c.drawARGB(0, 0, 0, 0);
        p.setStyle(Paint.Style.FILL);
        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        c.drawBitmap(bitmap, 4, 4, p);
        return output;
    }

    public class DialogsOnTouch implements View.OnTouchListener {

        private DisplayMetrics displayMetrics;
        //private static final String logTag = "SwipeDetector";
        private static final int MIN_DISTANCE_HIGH = 40;
        private static final int MIN_DISTANCE_HIGH_Y = 60;
        private float downX, downY, upX, upY;
        private float vDPI;

        Context mContext;

        public DialogsOnTouch(Context context) {
            this.mContext = context;
            displayMetrics = context.getResources().getDisplayMetrics();
            vDPI = displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT;
            //Log.e("DialogsActivity","DialogsOnTouch vDPI " + vDPI);
        }

        public boolean onTouch(View view, MotionEvent event) {

            touchPositionDP = Math.round(event.getX() / vDPI);
            //Log.e("DialogsActivity","onTouch touchPositionDP " + touchPositionDP + " hideTabs " + hideTabs);
            if (hideTabs) {
                return false;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    downX = Math.round(event.getX() / vDPI);
                    downY = Math.round(event.getY() / vDPI);
                    //Log.e("DialogsActivity", "view " + view.toString());
                    if (touchPositionDP > 50) {
                        parentLayout.getDrawerLayoutContainer().setAllowOpenDrawer(false, false);
                        //Log.e("DialogsActivity", "DOWN setAllowOpenDrawer FALSE");
                    }
                    //Log.e("DialogsActivity", "DOWN downX " + downX);
                    return view instanceof LinearLayout; // for emptyView
                }
                case MotionEvent.ACTION_UP: {
                    upX = Math.round(event.getX() / vDPI);
                    upY = Math.round(event.getY() / vDPI);
                    float deltaX = downX - upX;
                    float deltaY = downY - upY;
                    //Log.e(logTag, "MOVE X " + deltaX);
                    //Log.e(logTag, "MOVE Y " + deltaY);
                    //Log.e("DialogsActivity", "UP downX " + downX);
                    //Log.e("DialogsActivity", "UP upX " + upX);
                    //Log.e("DialogsActivity", "UP deltaX " + deltaX);
                    // horizontal swipe detection
                    if (Math.abs(deltaX) > MIN_DISTANCE_HIGH && Math.abs(deltaY) < MIN_DISTANCE_HIGH_Y) {
                        //if (Math.abs(deltaX) > MIN_DISTANCE_HIGH) {
                        refreshDialogType(deltaX < 0 ? 0 : 1);//0: Left - Right 1: Right - Left
                        downX = Math.round(event.getX() / vDPI);
                        refreshAdapter(mContext);
                        //dialogsAdapter.notifyDataSetChanged();
                        refreshTabAndListViews(false);
                        //return true;
                    }
                    //Log.e("DialogsActivity", "UP2 downX " + downX);
                    if (touchPositionDP > 50) {
                        parentLayout.getDrawerLayoutContainer().setAllowOpenDrawer(true, false);

                    }
                    //downX = downY = upX = upY = 0;
                    return false;
                }
            }

            return false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (searchString == null) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.openedChatChanged);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByAck);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageSendError);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didLoadedReplyMessages);

        }
        delegate = null;
    }


    @Override
    public void onResume() {
        super.onResume();

        refreshToolbarItems();
        if (dialogsAdapter != null) {
            dialogsAdapter.notifyDataSetChanged();
        }
        if (dialogsSearchAdapter != null) {
            dialogsSearchAdapter.notifyDataSetChanged();
        }

        if (checkPermission && !onlySelect && Build.VERSION.SDK_INT >= 23) {
            Activity activity = getParentActivity();
            if (activity != null) {
                checkPermission = false;
                if (activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED || activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("PermissionContacts", R.string.PermissionContacts));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                        showDialog(permissionDialog = builder.create());
                    } else if (activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("PermissionStorage", R.string.PermissionStorage));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                        showDialog(permissionDialog = builder.create());
                    } else {
                        askForPermissons();
                    }
                }
            }
        }
        updateTheme();
        unreadCount();
        this.actionBar.changeGhostModeVisibility();

        if (needRefreshCategory)
            refreshCategory();


        if (searchString == null) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.openedChatChanged);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByAck);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageSendError);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didLoadedReplyMessages);


        }


        if (!dialogsLoaded) {
            MessagesController.getInstance().loadDialogs(0, 100, true);
            ContactsController.getInstance().checkInviteText();
            dialogsLoaded = true;
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void askForPermissons() {
        Activity activity = getParentActivity();
        if (activity == null) {
            return;
        }
        ArrayList<String> permissons = new ArrayList<>();
        if (activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            permissons.add(Manifest.permission.READ_CONTACTS);
            permissons.add(Manifest.permission.WRITE_CONTACTS);
            permissons.add(Manifest.permission.GET_ACCOUNTS);
        }
        if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissons.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissons.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        String[] items = permissons.toArray(new String[permissons.size()]);
        activity.requestPermissions(items, 1);
    }


    @Override
    protected void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        if (permissionDialog != null && dialog == permissionDialog && getParentActivity() != null) {
            askForPermissons();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!onlySelect && floatingButton != null) {
            floatingButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ViewProxy.setTranslationY(floatingButton, floatingHidden ? AndroidUtilities.dp(100) : 0);
                    floatingButton.setClickable(!floatingHidden);
                    if (floatingButton != null) {
                        if (Build.VERSION.SDK_INT < 16) {
                            floatingButton.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            floatingButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                }
            });

        }
    }

    @Override
    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int a = 0; a < permissions.length; a++) {
                if (grantResults.length <= a || grantResults[a] != PackageManager.PERMISSION_GRANTED) {
                    continue;
                }
                switch (permissions[a]) {
                    case Manifest.permission.READ_CONTACTS:
                        ContactsController.getInstance().readContacts();
                        break;
                    case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                        ImageLoader.getInstance().checkMediaPaths();
                        break;
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.dialogsNeedReload) {
            if (dialogsAdapter != null) {
                if (dialogsAdapter.isDataSetChanged()) {
                    dialogsAdapter.notifyDataSetChanged();
                } else {
                    updateVisibleRows(MessagesController.UPDATE_MASK_NEW_MESSAGE);
                }
            }
            if (dialogsSearchAdapter != null) {
                dialogsSearchAdapter.notifyDataSetChanged();
            }
            if (listView != null) {
                try {
                    if (MessagesController.getInstance().loadingDialogs && MessagesController.getInstance().dialogs.isEmpty()) {
                        searchEmptyView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.GONE);
                        listView.setEmptyView(progressView);
                    } else {
                        progressView.setVisibility(View.GONE);
                        if (searching && searchWas) {
                            emptyView.setVisibility(View.GONE);
                            listView.setEmptyView(searchEmptyView);
                        } else {
                            searchEmptyView.setVisibility(View.GONE);
                            listView.setEmptyView(emptyView);
                        }
                    }
                } catch (Exception e) {
                    FileLog.e(e); //TODO fix it in other way?
                }
            }
        } else if (id == NotificationCenter.emojiDidLoaded) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.updateInterfaces) {
            updateVisibleRows((Integer) args[0]);
        } else if (id == NotificationCenter.appDidLogout) {
            dialogsLoaded = false;
        } else if (id == NotificationCenter.encryptedChatUpdated) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.contactsDidLoaded) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.openedChatChanged) {
            if (dialogsType == 0 && AndroidUtilities.isTablet()) {
                boolean close = (Boolean) args[1];
                long dialog_id = (Long) args[0];
                if (close) {
                    if (dialog_id == openedDialogId) {
                        openedDialogId = 0;
                    }
                } else {
                    openedDialogId = dialog_id;
                }
                if (dialogsAdapter != null) {
                    dialogsAdapter.setOpenedDialogId(openedDialogId);
                }
                updateVisibleRows(MessagesController.UPDATE_MASK_SELECT_DIALOG);
            }
        } else if (id == NotificationCenter.notificationsSettingsUpdated) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.messageReceivedByAck || id == NotificationCenter.messageReceivedByServer || id == NotificationCenter.messageSendError) {
            updateVisibleRows(MessagesController.UPDATE_MASK_SEND_STATE);
        } else if (id == NotificationCenter.didSetPasscode) {
            updatePasscodeButton();
        } else if (id == NotificationCenter.refreshTabs) {
            updateTabs();
            hideShowTabs((int) args[0]);
        }

        if (id == NotificationCenter.needReloadRecentDialogsSearch) {
            if (dialogsSearchAdapter != null) {
                dialogsSearchAdapter.loadRecentSearch();
            }
        } else if (id == NotificationCenter.didLoadedReplyMessages) {
            updateVisibleRows(0);
        }
    }

    private ArrayList<TLRPC.TL_dialog> getDialogsArray() {

        if (dialogsType == 10) {
            if (listView != null)
                listView.setVisibility(View.GONE);
            if (hiden_login != null)
                hiden_login.setVisibility(View.VISIBLE);

            if (emptyView != null)
                emptyView.setVisibility(View.GONE);


        } else {
            if (listView != null)
                listView.setVisibility(View.VISIBLE);
            if (hiden_login != null)
                hiden_login.setVisibility(View.GONE);
        }

        if (dialogsType == 0) {
            return MessagesController.getInstance().dialogs;
        } else if (dialogsType == 1) {
            return MessagesController.getInstance().dialogsServerOnly;
        } else if (dialogsType == 2) {
            return MessagesController.getInstance().dialogsGroupsOnly;
        }
        //gelas
        else if (dialogsType == 3) {
            return MessagesController.getInstance().dialogsUsers;
        } else if (dialogsType == 4) {
            return MessagesController.getInstance().dialogsGroups;
        } else if (dialogsType == 5) {
            return MessagesController.getInstance().dialogsChannels;
        } else if (dialogsType == 6) {
            return MessagesController.getInstance().dialogsBots;
        } else if (dialogsType == 7) {
            return MessagesController.getInstance().dialogsMegaGroups;
        } else if (dialogsType == 8) {
            return MessagesController.getInstance().dialogsFavs;
        } else if (dialogsType == 9) {
            return MessagesController.getInstance().dialogsGroupsAll;
        } else if (dialogsType == 10) {
            return MessagesController.getInstance().dialogsHides;

        } else if (dialogsType == 11) {
            while (counting) ;

            if (lastCode != catCode || dialogsCats.size() < 1) {
                counting = true;
                lastCode = catCode;
                dialogsCats.clear();
                return getCats(catCode);
            } else {
                return dialogsCats;
            }
        }
        //
        return null;
    }

    private ArrayList<TLRPC.TL_dialog> getCats(int code) {


        for (int i = 0; i < MessagesController.getInstance().dialogsCats.size(); i++) {
            if (MessagesController.getInstance().dialogsCats.get(i).catCode == code)
                dialogsCats.add(MessagesController.getInstance().dialogsCats.get(i));
        }

        counting = false;
        return dialogsCats;
    }


    private ArrayList<TLRPC.TL_dialog> getHides(int code) {

        for (int i = 0; i < MessagesController.getInstance().dialogsHides.size(); i++) {
            if (MessagesController.getInstance().dialogsHides.get(i).HiddenCode == code)
                dialogsHides.add(MessagesController.getInstance().dialogsHides.get(i));
        }
        counting = false;
        return dialogsHides;
    }


    public void setSideMenu(RecyclerView recyclerView) {
        sideMenu = recyclerView;
        sideMenu.setBackgroundColor(Theme.getColor(Theme.key_chats_menuBackground));
        sideMenu.setGlowColor(Theme.getColor(Theme.key_chats_menuBackground));
    }


    private void updatePasscodeButton() {
        if (passcodeItem == null) {
            return;
        }
        if (UserConfig.passcodeHash.length() != 0 && !searching) {
            passcodeItem.setVisibility(View.VISIBLE);
            SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, AndroidUtilities.THEME_PREFS_MODE);
            int iconColor = themePrefs.getInt("chatsHeaderIconsColor", 0xffffffff);
            if (UserConfig.appLocked) {
                //passcodeItem.setIcon(R.drawable.lock_close);
                Drawable lockC = getParentActivity().getResources().getDrawable(R.drawable.lock_close);
                if (lockC != null) lockC.setColorFilter(iconColor, PorterDuff.Mode.MULTIPLY);
                passcodeItem.setIcon(R.drawable.lock_close);
            } else {
                //passcodeItem.setIcon(R.drawable.lock_open);
                Drawable lockO = getParentActivity().getResources().getDrawable(R.drawable.lock_open);
                if (lockO != null) lockO.setColorFilter(iconColor, PorterDuff.Mode.MULTIPLY);
                passcodeItem.setIcon(R.drawable.lock_open);
            }
        } else {
            passcodeItem.setVisibility(View.GONE);
        }
    }

    private void hideFloatingButton(boolean hide) {
        if (floatingHidden == hide) {
            return;
        }
        floatingHidden = hide;
        ObjectAnimatorProxy animator = ObjectAnimatorProxy.ofFloatProxy(floatingButton, "translationY", floatingHidden ? AndroidUtilities.dp(100) : 0).setDuration(300);
        animator.setInterpolator(floatingInterpolator);
        floatingButton.setClickable(!hide);
        animator.start();


    }

    private void updateVisibleRows(int mask) {
        if (listView == null) {
            return;
        }
        int count = listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = listView.getChildAt(a);
            if (child instanceof DialogCell) {
                if (listView.getAdapter() != dialogsSearchAdapter) {
                    DialogCell cell = (DialogCell) child;
                    if ((mask & MessagesController.UPDATE_MASK_NEW_MESSAGE) != 0) {
                        cell.checkCurrentDialogIndex();
                        if (dialogsType == 0 && AndroidUtilities.isTablet()) {
                            cell.setDialogSelected(cell.getDialogId() == openedDialogId);
                        }
                    } else if ((mask & MessagesController.UPDATE_MASK_SELECT_DIALOG) != 0) {
                        if (dialogsType == 0 && AndroidUtilities.isTablet()) {
                            cell.setDialogSelected(cell.getDialogId() == openedDialogId);
                        }
                    } else {
                        cell.update(mask);
                    }
                }
            } else if (child instanceof UserCell) {
                ((UserCell) child).update(mask);
            } else if (child instanceof ProfileSearchCell) {
                ((ProfileSearchCell) child).update(mask);
            }
        }
        updateListBG();
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                unreadCount();

            }
        });
    }

    private void unreadCount() {
        unreadCount(MessagesController.getInstance().dialogs, allCounter);
        unreadCount(MessagesController.getInstance().dialogsUsers, usersCounter);
        unreadCount(MessagesController.getInstance().dialogsBots, botsCounter);
        unreadCount(MessagesController.getInstance().dialogsChannels, channelsCounter);
        unreadCount(MessagesController.getInstance().dialogsFavs, favsCounter);
        unreadCountGroups();

    }

    private void unreadCountGroups() {
        SharedPreferences plusPreferences = ApplicationLoader.applicationContext.getSharedPreferences("gelasConfig", Activity.MODE_PRIVATE);
        boolean hideSGroups = plusPreferences.getBoolean("hideSGroups", false);
        if (hideSGroups) {
            unreadCount(MessagesController.getInstance().dialogsGroupsAll, groupsCounter);
        } else {
            unreadCount(MessagesController.getInstance().dialogsGroups, groupsCounter);
            unreadCount(MessagesController.getInstance().dialogsMegaGroups, sGroupsCounter);
        }
    }

    private void unreadCount(ArrayList<TLRPC.TL_dialog> dialogs, TextView tv) {
        SharedPreferences plusPreferences = ApplicationLoader.applicationContext.getSharedPreferences("gelasConfig", Activity.MODE_PRIVATE);
        boolean hTabs = plusPreferences.getBoolean("hideTabs", false);
        if (hTabs) return;
        boolean hideCounters = plusPreferences.getBoolean("hideTabsCounters", false);
        if (hideCounters) {
            tv.setVisibility(View.GONE);
            return;
        }
        boolean allMuted = true;
        boolean countDialogs = plusPreferences.getBoolean("tabsCountersCountChats", false);
        boolean countNotMuted = plusPreferences.getBoolean("tabsCountersCountNotMuted", false);
        int unreadCount = 0;

        if (dialogs != null && !dialogs.isEmpty()) {
            for (int a = 0; a < dialogs.size(); a++) {
                TLRPC.TL_dialog dialg = dialogs.get(a);
                boolean isMuted = MessagesController.getInstance().isDialogMuted(dialg.id);
                if (!isMuted || !countNotMuted) {
                    int i = dialg.unread_count;
                    if (i > 0) {
                        if (countDialogs) {
                            if (i > 0) unreadCount = unreadCount + 1;
                        } else {
                            unreadCount = unreadCount + i;
                        }
                        if (i > 0 && !isMuted) allMuted = false;
                    }
                }
            }
        }

        if (unreadCount == 0) {
            tv.setVisibility(View.GONE);
        } else {
            tv.setVisibility(View.VISIBLE);
            tv.setText("" + unreadCount);

            SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, AndroidUtilities.THEME_PREFS_MODE);
            int size = themePrefs.getInt("chatsHeaderTabCounterSize", 11);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
            tv.setPadding(AndroidUtilities.dp(size > 10 ? size - 7 : 4), 0, AndroidUtilities.dp(size > 10 ? size - 7 : 4), 0);
            int cColor = themePrefs.getInt("chatsHeaderTabCounterColor", 0xffffffff);
            if (allMuted) {
                tv.getBackground().setColorFilter(themePrefs.getInt("chatsHeaderTabCounterSilentBGColor", 0xffb9b9b9), PorterDuff.Mode.SRC_IN);
                tv.setTextColor(cColor);
            } else {
                
                //color tab counter
                
                tv.getBackground().setColorFilter(themePrefs.getInt("chatsHeaderTabCounterBGColor", 0xfff96109), PorterDuff.Mode.SRC_IN);
                tv.setTextColor(cColor);
            }
        }
    }

    private void updateListBG() {


        SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, AndroidUtilities.THEME_PREFS_MODE);
        int mainColor = themePrefs.getInt("chatsRowColor", 0xffffffff);
        int value = themePrefs.getInt("chatsRowGradient", 0);
        boolean b = true;//themePrefs.getBoolean("chatsRowGradientListCheck", false);
        if (value > 0 && b) {
            GradientDrawable.Orientation go;
            switch (value) {
                case 2:
                    go = GradientDrawable.Orientation.LEFT_RIGHT;
                    break;
                case 3:
                    go = GradientDrawable.Orientation.TL_BR;
                    break;
                case 4:
                    go = GradientDrawable.Orientation.BL_TR;
                    break;
                default:
                    go = GradientDrawable.Orientation.TOP_BOTTOM;
            }

            int gradColor = themePrefs.getInt("chatsRowGradientColor", 0xffffffff);
            int[] colors = new int[]{mainColor, gradColor};
            GradientDrawable gd = new GradientDrawable(go, colors);
            listView.setBackgroundDrawable(gd);
        } else {
            listView.setBackgroundColor(mainColor);
        }
    }

    public void setDelegate(DialogsActivityDelegate delegate) {
        this.delegate = delegate;
    }

    public void setSearchString(String string) {
        searchString = string;
    }

    public boolean isMainDialogList() {
        return delegate == null && searchString == null;
    }

    private void didSelectResult(final long dialog_id, boolean useAlert, final boolean param) {
        if (addToGroupAlertString == null) {
            if ((int) dialog_id < 0 && ChatObject.isChannel(-(int) dialog_id) && !ChatObject.isCanWriteToChannel(-(int) dialog_id)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.getString("ChannelCantSendMessage", R.string.ChannelCantSendMessage));
                builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), null);
                showDialog(builder.create());
                return;
            }
        }
        if (useAlert && (selectAlertString != null && selectAlertStringGroup != null || addToGroupAlertString != null)) {
            if (getParentActivity() == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            int lower_part = (int) dialog_id;
            int high_id = (int) (dialog_id >> 32);
            if (lower_part != 0) {
                if (high_id == 1) {
                    TLRPC.Chat chat = MessagesController.getInstance().getChat(lower_part);
                    if (chat == null) {
                        return;
                    }
                    builder.setMessage(LocaleController.formatStringSimple(selectAlertStringGroup, chat.title));
                } else {
                    if (lower_part > 0) {
                        TLRPC.User user = MessagesController.getInstance().getUser(lower_part);
                        if (user == null) {
                            return;
                        }
                        builder.setMessage(LocaleController.formatStringSimple(selectAlertString, UserObject.getUserName(user)));
                    } else if (lower_part < 0) {
                        TLRPC.Chat chat = MessagesController.getInstance().getChat(-lower_part);
                        if (chat == null) {
                            return;
                        }
                        if (addToGroupAlertString != null) {
                            builder.setMessage(LocaleController.formatStringSimple(addToGroupAlertString, chat.title));
                        } else {
                            builder.setMessage(LocaleController.formatStringSimple(selectAlertStringGroup, chat.title));
                        }
                    }
                }
            } else {
                TLRPC.EncryptedChat chat = MessagesController.getInstance().getEncryptedChat(high_id);
                TLRPC.User user = MessagesController.getInstance().getUser(chat.user_id);
                if (user == null) {
                    return;
                }
                builder.setMessage(LocaleController.formatStringSimple(selectAlertString, UserObject.getUserName(user)));
            }

            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    didSelectResult(dialog_id, false, false);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        } else {
            if (delegate != null) {
                delegate.didSelectDialog(DialogsActivity.this, dialog_id, param);
                delegate = null;
            } else {
                finishFragment();
            }
        }
    }

    private String getHeaderTitle() {
        SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, AndroidUtilities.THEME_PREFS_MODE);
        int value = themePrefs.getInt("chatsHeaderTitle", 0);
        String title = LocaleController.getString("AppName", R.string.AppName);
        TLRPC.User user = UserConfig.getCurrentUser();
        if (value == 1) {
            title = LocaleController.getString("AppName", R.string.AppName);
        } else if (value == 2) {
            if (user != null && (user.first_name != null || user.last_name != null)) {
                title = ContactsController.formatName(user.first_name, user.last_name);
            }
        } else if (value == 3) {
            if (user != null && user.username != null && user.username.length() != 0) {
                title = "@" + user.username;
            }
        } else if (value == 4) {
            title = "";
        }
        return title;
    }

    private String getHeaderAllTitles() {


        switch (dialogsType) {
            case 3:
                return LocaleController.getString("Users", R.string.Users);
            case 4:
            case 9:
                return LocaleController.getString("Groups", R.string.Groups);
            case 5:
                return LocaleController.getString("Channels", R.string.Channels);
            case 6:
                return LocaleController.getString("Bots", R.string.Bots);
            case 7:
                return LocaleController.getString("SuperGroups", R.string.SuperGroups);
            case 8:
                return LocaleController.getString("Favorites", R.string.Favorites);
            default:
                return getHeaderTitle();
        }
    }

    /*private void updateHeaderTitle(){
        String s = "";
        switch(dialogsType) {
            case 3:
                s = LocaleController.getString("Users", R.string.Users);
                break;
            case 4:
            case 9:
                s = LocaleController.getString("Groups", R.string.Groups);
                break;
            case 5:
                s = LocaleController.getString("Channels", R.string.Channels);
                break;
            case 6:
                s = LocaleController.getString("Bots", R.string.Bots);
                break;
            case 7:
                s = LocaleController.getString("SuperGroups", R.string.SuperGroups);
                break;
            case 8:
                s = getParentActivity().getString(R.string.Favorites);
                break;
            default:
                s = getHeaderTitle();
            }
        actionBar.setTitle(getHeaderAllTitles());
        paintHeader(true);
    }*/


    private void paintHeader(boolean tabs) {
        SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, AndroidUtilities.THEME_PREFS_MODE);
        actionBar.setTitleColor(themePrefs.getInt("chatsHeaderTitleColor", 0xffffffff));
        int def = themePrefs.getInt("themeColor", Theme.getColor(Theme.key_actionBarDefault));
        int hColor = themePrefs.getInt("chatsHeaderColor", def);
        /*if(!tabs){
            actionBar.setBackgroundColor(hColor);
        }else{
            tabsView.setBackgroundColor(hColor);
        }*/
        if (!tabs) actionBar.setBackgroundColor(hColor);
        if (tabs) {
            tabsView.setBackgroundColor(hColor);
        }
        int val = themePrefs.getInt("chatsHeaderGradient", 0);
        if (val > 0) {
            GradientDrawable.Orientation go;
            switch (val) {
                case 2:
                    go = GradientDrawable.Orientation.LEFT_RIGHT;
                    break;
                case 3:
                    go = GradientDrawable.Orientation.TL_BR;
                    break;
                case 4:
                    go = GradientDrawable.Orientation.BL_TR;
                    break;
                default:
                    go = GradientDrawable.Orientation.TOP_BOTTOM;
            }
            int gradColor = themePrefs.getInt("chatsHeaderGradientColor", def);
            int[] colors = new int[]{hColor, gradColor};
            GradientDrawable gd = new GradientDrawable(go, colors);
            if (!tabs) actionBar.setBackgroundDrawable(gd);
            if (tabs) {
                tabsView.setBackgroundDrawable(gd);
            }
            /*if(!tabs){
                actionBar.setBackgroundDrawable(gd);
            }else{
                tabsView.setBackgroundDrawable(gd);
            }*/
        }
    }

    private void updateTheme() {
        paintHeader(false);
        SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, AndroidUtilities.THEME_PREFS_MODE);
        int def = themePrefs.getInt("themeColor", Theme.getColor(Theme.key_actionBarDefault));
        int iconColor = themePrefs.getInt("chatsHeaderIconsColor", 0xffffffff);
        try {
            int hColor = themePrefs.getInt("chatsHeaderColor", def);
            //gelas
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Bitmap bm = BitmapFactory.decodeResource(getParentActivity().getResources(), R.mipmap.ic_launcher);
                ActivityManager.TaskDescription td = new ActivityManager.TaskDescription(getHeaderTitle(), bm, hColor);
                getParentActivity().setTaskDescription(td);
                bm.recycle();
            }

            Drawable floatingDrawableWhite = getParentActivity().getResources().getDrawable(R.drawable.floating_white);
            if (floatingDrawableWhite != null)
                floatingDrawableWhite.setColorFilter(themePrefs.getInt("chatsFloatingBGColor", def), PorterDuff.Mode.MULTIPLY);
            floatingButton.setBackgroundDrawable(floatingDrawableWhite);
            Drawable pencilDrawableWhite = getParentActivity().getResources().getDrawable(R.drawable.floating_pencil);
            if (pencilDrawableWhite != null)
                pencilDrawableWhite.setColorFilter(themePrefs.getInt("chatsFloatingPencilColor", 0xffffffff), PorterDuff.Mode.MULTIPLY);
            floatingButton.setImageDrawable(pencilDrawableWhite);


        } catch (NullPointerException e) {
            FileLog.e(e);
        }
        try {
            Drawable search = getParentActivity().getResources().getDrawable(R.drawable.ic_ab_search);
            if (search != null) search.setColorFilter(iconColor, PorterDuff.Mode.MULTIPLY);
            Drawable lockO = getParentActivity().getResources().getDrawable(R.drawable.lock_close);
            if (lockO != null) lockO.setColorFilter(iconColor, PorterDuff.Mode.MULTIPLY);
            Drawable lockC = getParentActivity().getResources().getDrawable(R.drawable.lock_open);
            if (lockC != null) lockC.setColorFilter(iconColor, PorterDuff.Mode.MULTIPLY);
            Drawable clear = getParentActivity().getResources().getDrawable(R.drawable.ic_close_white);
            if (clear != null) clear.setColorFilter(iconColor, PorterDuff.Mode.MULTIPLY);
        } catch (OutOfMemoryError e) {
            FileLog.e(e);
        }
        refreshTabs();
        paintHeader(true);
    }












    private void createTabs(final Context context) {
        SharedPreferences plusPreferences = ApplicationLoader.applicationContext.getSharedPreferences("IrangramConfig", 0);
        SharedPreferences.Editor editor = plusPreferences.edit();
        boolean hideUsers = plusPreferences.getBoolean("hideUsers", false);
        boolean hideGroups = plusPreferences.getBoolean("hideGroups", false);
        boolean hideSGroups = plusPreferences.getBoolean("hideSGroups", false);
        boolean hideChannels = plusPreferences.getBoolean("hideChannels", false);
        boolean hideBots = plusPreferences.getBoolean("hideBots", false);
        boolean hideHiddens = plusPreferences.getBoolean("hideHiddens", true);
        boolean hideFavs = plusPreferences.getBoolean("hideFavs", false);
        this.hideTabs = plusPreferences.getBoolean("hideTabs", false);
        if (this.isCatMode) {
            this.hideTabs = true;
        }
        this.disableAnimation = plusPreferences.getBoolean("disableTabsAnimation", false);
        this.ShowTabsInBottomRow = plusPreferences.getBoolean("ShowTabsInBottomRow", false);
        if (hideUsers && hideGroups && hideSGroups && hideChannels && hideBots && hideHiddens && hideFavs && !this.hideTabs) {
            this.hideTabs = true;
            editor.putBoolean("hideTabs", true).apply();
        }
        this.tabsHeight = plusPreferences.getInt("tabsHeight", 40);
        refreshTabAndListViews(false);
        int t = plusPreferences.getInt("defTab", -1);
        if (t == -1) {
            t = plusPreferences.getInt("selTab", 0);
        }
        this.selectedTab = t;
        if (!(this.hideTabs || this.dialogsType == this.selectedTab)) {
            int i;
            if (this.selectedTab == 4 && hideSGroups) {
                i = 9;
            } else {
                i = this.selectedTab;
            }
            this.dialogsType = i;
            this.dialogsAdapter = new DialogsAdapter(context, this.dialogsType);
            this.listView.setAdapter(this.dialogsAdapter);
            this.dialogsAdapter.notifyDataSetChanged();
        }
        this.dialogsBackupAdapter = new DialogsAdapter(context, 0);
        if (this.isCatMode) {
            this.dialogsBackupAdapter = new DialogsAdapter(context, 11);
            if (this.dialogsAdapter != null) {
                this.dialogsAdapter.notifyDataSetChanged();
            }
        }
        this.tabsLayout = new LinearLayout(context);
        this.tabsLayout.setOrientation(LinearLayout.HORIZONTAL);
        this.tabsLayout.setGravity(17);
        this.allTab = new ImageView(context);
        Drawable tab_all = getParentActivity().getResources().getDrawable(R.drawable.tab_all);
        tab_all.setColorFilter(AndroidUtilities.getIntDef("chatHeaderIconsColor", -1), PorterDuff.Mode.MULTIPLY);
        this.allTab.setImageDrawable(tab_all);
        this.allCounter = new TextView(context);
        this.allCounter.setTag("ALL");
        addTabView(context, this.allTab, this.allCounter, true);
        Drawable tab_user = getParentActivity().getResources().getDrawable(R.drawable.tab_user);
        tab_user.setColorFilter(AndroidUtilities.getIntDef("chatHeaderIconsColor", -1), PorterDuff.Mode.MULTIPLY);
        this.usersTab = new ImageView(context);
        this.usersTab.setImageDrawable(tab_user);
        this.usersCounter = new TextView(context);
        this.usersCounter.setTag("USERS");
        addTabView(context, this.usersTab, this.usersCounter, !hideUsers);
        Drawable tab_group = getParentActivity().getResources().getDrawable(R.drawable.tab_group);
        tab_group.setColorFilter(AndroidUtilities.getIntDef("chatHeaderIconsColor", -1), PorterDuff.Mode.MULTIPLY);
        this.groupsTab = new ImageView(context);
        this.groupsTab.setImageDrawable(tab_group);
        this.groupsCounter = new TextView(context);
        this.groupsCounter.setTag("GROUPS");
        addTabView(context, this.groupsTab, this.groupsCounter, !hideGroups);
        Drawable tab_supergroup = getParentActivity().getResources().getDrawable(R.drawable.tab_supergroup);
        tab_supergroup.setColorFilter(AndroidUtilities.getIntDef("chatHeaderIconsColor", -1), PorterDuff.Mode.MULTIPLY);
        this.superGroupsTab = new ImageView(context);
        this.superGroupsTab.setImageDrawable(tab_supergroup);
        this.sGroupsCounter = new TextView(context);
        this.sGroupsCounter.setTag("SGROUP");
        addTabView(context, this.superGroupsTab, this.sGroupsCounter, !hideSGroups);
        Drawable tab_channel = getParentActivity().getResources().getDrawable(R.drawable.tab_channel);
        tab_channel.setColorFilter(AndroidUtilities.getIntDef("chatHeaderIconsColor", -1), PorterDuff.Mode.MULTIPLY);
        this.channelsTab = new ImageView(context);
        this.channelsTab.setImageDrawable(tab_channel);
        this.channelsCounter = new TextView(context);
        this.channelsCounter.setTag("CHANNELS");
        addTabView(context, this.channelsTab, this.channelsCounter, !hideChannels);
        Drawable tab_bot = getParentActivity().getResources().getDrawable(R.drawable.tab_bot);
        tab_bot.setColorFilter(AndroidUtilities.getIntDef("chatHeaderIconsColor", -1), PorterDuff.Mode.MULTIPLY);
        this.botsTab = new ImageView(context);
        this.botsTab.setImageDrawable(tab_bot);
        this.botsCounter = new TextView(context);
        this.botsCounter.setTag("BOTS");
        addTabView(context, this.botsTab, this.botsCounter, !hideBots);
        Drawable tab_favs = getParentActivity().getResources().getDrawable(R.drawable.tab_favs);
        tab_favs.setColorFilter(AndroidUtilities.getIntDef("chatHeaderIconsColor", -1), PorterDuff.Mode.MULTIPLY);
        this.favsTab = new ImageView(context);
        this.favsTab.setImageDrawable(tab_favs);
        this.favsCounter = new TextView(context);
        this.favsCounter.setTag("FAVS");
        addTabView(context, this.favsTab, this.favsCounter, !hideFavs);
        Drawable tab_admin = getParentActivity().getResources().getDrawable(R.drawable.admin_star2);
        tab_admin.setColorFilter(AndroidUtilities.getIntDef("chatHeaderIconsColor", -1), PorterDuff.Mode.MULTIPLY);
        this.adminTab = new ImageView(context);
        this.adminTab.setImageDrawable(tab_admin);
        this.adminCounter = new TextView(context);
        this.adminCounter.setTag("ADMIN");
        addTabView(context, this.adminTab, this.adminCounter, !hideHiddens);
        this.tabsView.addView(this.tabsLayout, LayoutHelper.createFrame(-1, -1.0f));
        this.allTab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (DialogsActivity.this.dialogsType != 0) {
                    DialogsActivity.this.dialogsType = 0;
                    DialogsActivity.this.refreshAdapter(context);
                }
            }
        });
        
        this.allTab.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                BottomSheet.Builder builder = new BottomSheet.Builder(DialogsActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("All", R.string.All));
                SharedPreferences plusPreferences = ApplicationLoader.applicationContext.getSharedPreferences("IrangramConfig", 0);
                final int def = plusPreferences.getInt("defTab", -1);
                final int sort = plusPreferences.getInt("sortAll", 0);
                CharSequence cs2 = def == 0 ? LocaleController.getString("ResetDefaultTab", R.string.ResetDefaultTab) : LocaleController.getString("SetAsDefaultTab", R.string.SetAsDefaultTab);
                CharSequence cs1 = sort == 0 ? LocaleController.getString("SortByUnreadCount", R.string.SortByUnreadCount) : LocaleController.getString("SortByLastMessage", R.string.SortByLastMessage);
                builder.setItems(new CharSequence[]{LocaleController.getString("HideShowTabs", R.string.HideShowTabs), cs1, cs2, LocaleController.getString("MarkAllAsRead", R.string.MarkAllAsRead)}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int i = 0;
                        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("IrangramConfig", 0).edit();
                        if (which == 0) {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DialogsActivity.this.getParentActivity());
                            DialogsActivity.this.createTabsDialog(context, builder);
                            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), null);
                            DialogsActivity.this.showDialog(builder.create());
                        } else if (which == 1) {
                            String str = "sortAll";
                            if (sort == 0) {
                                i = 1;
                            }
                            editor.putInt(str, i).apply();
                            if (DialogsActivity.this.dialogsAdapter.getItemCount() > 1) {
                                DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
                            }
                        } else if (which == 2) {
                            String str2 = "defTab";
                            if (def == 0) {
                                i = -1;
                            }
                            editor.putInt(str2, i).apply();
                        } else if (which == 3) {
                            DialogsActivity.this.markAsReadDialog(true);
                        }
                    }
                });
                DialogsActivity.this.showDialog(builder.create());
                return true;
            }
        });
        this.usersTab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (DialogsActivity.this.dialogsType != 3) {
                    DialogsActivity.this.dialogsType = 3;
                    DialogsActivity.this.refreshAdapter(context);
                }
            }
        });
        this.usersTab.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                BottomSheet.Builder builder = new BottomSheet.Builder(DialogsActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("Users", R.string.Users));
                SharedPreferences plusPreferences = ApplicationLoader.applicationContext.getSharedPreferences("IrangramConfig", 0);
                final int sort = plusPreferences.getInt("sortUsers", 0);
                final int def = plusPreferences.getInt("defTab", -1);
                CharSequence cs = def == 3 ? LocaleController.getString("ResetDefaultTab", R.string.ResetDefaultTab) : LocaleController.getString("SetAsDefaultTab", R.string.SetAsDefaultTab);
                CharSequence[] items = new CharSequence[3];
                items[0] = sort == 0 ? LocaleController.getString("SortByStatus", R.string.SortByStatus) : LocaleController.getString("SortByLastMessage", R.string.SortByLastMessage);
                items[1] = cs;
                items[2] = LocaleController.getString("MarkAllAsRead", R.string.MarkAllAsRead);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int i = 3;
                        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("IrangramConfig", 0).edit();
                        if (which == 1) {
                            String str = "defTab";
                            if (def == 3) {
                                i = -1;
                            }
                            editor.putInt(str, i).apply();
                        } else if (which == 0) {
                            String str2 = "sortUsers";
                            if (sort == 0) {
                                i = 1;
                            } else {
                                i = 0;
                            }
                            editor.putInt(str2, i).apply();
                            if (DialogsActivity.this.dialogsAdapter.getItemCount() > 1) {
                                DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
                            }
                        } else if (which == 2) {
                            DialogsActivity.this.markAsReadDialog(true);
                        }
                    }
                });
                DialogsActivity.this.showDialog(builder.create());
                return true;
            }
        });
        this.groupsTab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int i = ApplicationLoader.applicationContext.getSharedPreferences("IrangramConfig", 0).getBoolean("hideSGroups", false) ? 9 : 4;
                if (DialogsActivity.this.dialogsType != i) {
                    DialogsActivity.this.dialogsType = i;
                    DialogsActivity.this.refreshAdapter(context);
                }
            }
        });
        this.groupsTab.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                BottomSheet.Builder builder = new BottomSheet.Builder(DialogsActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("Groups", R.string.Groups));
                SharedPreferences plusPreferences = ApplicationLoader.applicationContext.getSharedPreferences("IrangramConfig", 0);
                final boolean hideSGroups = plusPreferences.getBoolean("hideSGroups", false);
                final int sort = plusPreferences.getInt("sortGroups", 0);
                final int def = plusPreferences.getInt("defTab", -1);
                CharSequence cs2 = def == 4 ? LocaleController.getString("ResetDefaultTab", R.string.ResetDefaultTab) : LocaleController.getString("SetAsDefaultTab", R.string.SetAsDefaultTab);
                CharSequence cs1 = sort == 0 ? LocaleController.getString("SortByUnreadCount", R.string.SortByUnreadCount) : LocaleController.getString("SortByLastMessage", R.string.SortByLastMessage);
                CharSequence cs0 = hideSGroups ? LocaleController.getString("ShowSuperGroupsTab", R.string.ShowSuperGroupsTab) : LocaleController.getString("HideSuperGroupsTab", R.string.HideSuperGroupsTab);
                builder.setItems(new CharSequence[]{cs0, cs1, cs2, LocaleController.getString("MarkAllAsRead", R.string.MarkAllAsRead)}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        boolean z = true;
                        int i = 0;
                        SharedPreferences plusPreferences = ApplicationLoader.applicationContext.getSharedPreferences("IrangramConfig", 0);
                        SharedPreferences.Editor editor = plusPreferences.edit();
                        if (which == 0) {
                            RelativeLayout rl = (RelativeLayout) DialogsActivity.this.superGroupsTab.getParent();
                            String str = "hideSGroups";
                            if (hideSGroups) {
                                z = false;
                            }
                            editor.putBoolean(str, z).apply();
                            if (hideSGroups) {
                                DialogsActivity.this.tabsLayout.addView(rl, plusPreferences.getBoolean("hideUsers", false) ? 2 : 3, LayoutHelper.createLinear(0, -1, (float) DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            } else {
                                DialogsActivity.this.tabsLayout.removeView(rl);
                                if (DialogsActivity.this.dialogsType == 7) {
                                    DialogsActivity.this.dialogsType = 9;
                                    DialogsActivity.this.refreshAdapter(context);
                                }
                            }
                            DialogsActivity.this.unreadCountGroups();
                        } else if (which == 1) {
                            String str2 = "sortGroups";
                            if (sort == 0) {
                                i = 1;
                            }
                            editor.putInt(str2, i).apply();
                            if (DialogsActivity.this.dialogsAdapter.getItemCount() > 1) {
                                DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
                            }
                        } else if (which == 2) {
                            editor.putInt("defTab", def == 4 ? -1 : 4).apply();
                        } else if (which == 3) {
                            DialogsActivity.this.markAsReadDialog(true);
                        }
                    }
                });
                DialogsActivity.this.showDialog(builder.create());
                return true;
            }
        });
        this.superGroupsTab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (DialogsActivity.this.dialogsType != 7) {
                    DialogsActivity.this.dialogsType = 7;
                    DialogsActivity.this.refreshAdapter(context);
                }
            }
        });
        this.superGroupsTab.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                BottomSheet.Builder builder = new BottomSheet.Builder(DialogsActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("SuperGroups", R.string.SuperGroups));
                SharedPreferences plusPreferences = ApplicationLoader.applicationContext.getSharedPreferences("IrangramConfig", 0);
                final int def = plusPreferences.getInt("defTab", -1);
                final int sort = plusPreferences.getInt("sortSGroups", 0);
                final boolean hideSGroups = plusPreferences.getBoolean("hideSGroups", false);
                CharSequence cs2 = def == 7 ? LocaleController.getString("ResetDefaultTab", R.string.ResetDefaultTab) : LocaleController.getString("SetAsDefaultTab", R.string.SetAsDefaultTab);
                CharSequence cs1 = sort == 0 ? LocaleController.getString("SortByUnreadCount", R.string.SortByUnreadCount) : LocaleController.getString("SortByLastMessage", R.string.SortByLastMessage);
                CharSequence cs0 = hideSGroups ? LocaleController.getString("ShowSuperGroupsTab", R.string.ShowSuperGroupsTab) : LocaleController.getString("HideSuperGroupsTab", R.string.HideSuperGroupsTab);
                builder.setItems(new CharSequence[]{cs0, cs1, cs2, LocaleController.getString("MarkAllAsRead", R.string.MarkAllAsRead)}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        boolean z = true;
                        int i = 0;
                        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("IrangramConfig", 0).edit();
                        if (which == 0) {
                            RelativeLayout rl = (RelativeLayout) DialogsActivity.this.superGroupsTab.getParent();
                            String str = "hideSGroups";
                            if (hideSGroups) {
                                z = false;
                            }
                            editor.putBoolean(str, z).apply();
                            if (hideSGroups) {
                                DialogsActivity.this.tabsLayout.addView(rl, 3, LayoutHelper.createLinear(0, -1, (float) DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            } else {
                                DialogsActivity.this.tabsLayout.removeView(rl);
                                if (DialogsActivity.this.dialogsType == 7) {
                                    DialogsActivity.this.dialogsType = 0;
                                    DialogsActivity.this.refreshAdapter(context);
                                }
                            }
                            DialogsActivity.this.unreadCountGroups();
                        } else if (which == 1) {
                            String str2 = "sortSGroups";
                            if (sort == 0) {
                                i = 1;
                            }
                            editor.putInt(str2, i).apply();
                            if (DialogsActivity.this.dialogsAdapter.getItemCount() > 1) {
                                DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
                            }
                        } else if (which == 2) {
                            editor.putInt("defTab", def == 7 ? -1 : 7).apply();
                        } else if (which == 3) {
                            DialogsActivity.this.markAsReadDialog(true);
                        }
                    }
                });
                DialogsActivity.this.showDialog(builder.create());
                return true;
            }
        });
        this.channelsTab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (DialogsActivity.this.dialogsType != 5) {
                    DialogsActivity.this.dialogsType = 5;
                    DialogsActivity.this.refreshAdapter(context);
                }
            }
        });
        this.channelsTab.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                BottomSheet.Builder builder = new BottomSheet.Builder(DialogsActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("Channels", R.string.Channels));
                SharedPreferences plusPreferences = ApplicationLoader.applicationContext.getSharedPreferences("IrangramConfig", 0);
                final int sort = plusPreferences.getInt("sortChannels", 0);
                final int def = plusPreferences.getInt("defTab", -1);
                CharSequence cs = def == 5 ? LocaleController.getString("ResetDefaultTab", R.string.ResetDefaultTab) : LocaleController.getString("SetAsDefaultTab", R.string.SetAsDefaultTab);
                CharSequence cs1 = sort == 0 ? LocaleController.getString("SortByUnreadCount", R.string.SortByUnreadCount) : LocaleController.getString("SortByLastMessage", R.string.SortByLastMessage);
                builder.setItems(new CharSequence[]{cs1, cs, LocaleController.getString("MarkAllAsRead", R.string.MarkAllAsRead)}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int i = 5;
                        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("IrangramConfig", 0).edit();
                        if (which == 1) {
                            String str = "defTab";
                            if (def == 5) {
                                i = -1;
                            }
                            editor.putInt(str, i).apply();
                        } else if (which == 0) {
                            String str2 = "sortChannels";
                            if (sort == 0) {
                                i = 1;
                            } else {
                                i = 0;
                            }
                            editor.putInt(str2, i).apply();
                            if (DialogsActivity.this.dialogsAdapter.getItemCount() > 1) {
                                DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
                            }
                        } else if (which == 2) {
                            DialogsActivity.this.markAsReadDialog(true);
                        }
                    }
                });
                DialogsActivity.this.showDialog(builder.create());
                return true;
            }
        });
        this.botsTab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (DialogsActivity.this.dialogsType != 6) {
                    DialogsActivity.this.dialogsType = 6;
                    DialogsActivity.this.refreshAdapter(context);
                }
            }
        });
        this.botsTab.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                BottomSheet.Builder builder = new BottomSheet.Builder(DialogsActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("Bots", R.string.Bots));
                SharedPreferences plusPreferences = ApplicationLoader.applicationContext.getSharedPreferences("IrangramConfig", 0);
                final int sort = plusPreferences.getInt("sortBots", 0);
                final int def = plusPreferences.getInt("defTab", -1);
                CharSequence cs = def == 6 ? LocaleController.getString("ResetDefaultTab", R.string.ResetDefaultTab) : LocaleController.getString("SetAsDefaultTab", R.string.SetAsDefaultTab);
                CharSequence cs1 = sort == 0 ? LocaleController.getString("SortByUnreadCount", R.string.SortByUnreadCount) : LocaleController.getString("SortByLastMessage", R.string.SortByLastMessage);
                builder.setItems(new CharSequence[]{cs1, cs, LocaleController.getString("MarkAllAsRead", R.string.MarkAllAsRead)}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int i = 6;
                        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("IrangramConfig", 0).edit();
                        if (which == 1) {
                            String str = "defTab";
                            if (def == 6) {
                                i = -1;
                            }
                            editor.putInt(str, i).apply();
                        } else if (which == 0) {
                            String str2 = "sortBots";
                            if (sort == 0) {
                                i = 1;
                            } else {
                                i = 0;
                            }
                            editor.putInt(str2, i).apply();
                            if (DialogsActivity.this.dialogsAdapter.getItemCount() > 1) {
                                DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
                            }
                        } else if (which == 2) {
                            DialogsActivity.this.markAsReadDialog(true);
                        }
                    }
                });
                DialogsActivity.this.showDialog(builder.create());
                return true;
            }
        });
        this.favsTab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (DialogsActivity.this.dialogsType != 8) {
                    DialogsActivity.this.dialogsType = 8;
                    DialogsActivity.this.refreshAdapter(context);
                }
            }
        });
        this.favsTab.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                BottomSheet.Builder builder = new BottomSheet.Builder(DialogsActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("Favorites", R.string.Favorites));
                SharedPreferences plusPreferences = ApplicationLoader.applicationContext.getSharedPreferences("IrangramConfig", 0);
                final int sort = plusPreferences.getInt("sortFavs", 0);
                final int def = plusPreferences.getInt("defTab", -1);
                CharSequence cs = def == 8 ? LocaleController.getString("ResetDefaultTab", R.string.ResetDefaultTab) : LocaleController.getString("SetAsDefaultTab", R.string.SetAsDefaultTab);
                CharSequence cs1 = sort == 0 ? LocaleController.getString("SortByUnreadCount", R.string.SortByUnreadCount) : LocaleController.getString("SortByLastMessage", R.string.SortByLastMessage);
                builder.setItems(new CharSequence[]{cs1, cs, LocaleController.getString("MarkAllAsRead", R.string.MarkAllAsRead)}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int i = 8;
                        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("IrangramConfig", 0).edit();
                        if (which == 1) {
                            String str = "defTab";
                            if (def == 8) {
                                i = -1;
                            }
                            editor.putInt(str, i).apply();
                        } else if (which == 0) {
                            String str2 = "sortFavs";
                            if (sort == 0) {
                                i = 1;
                            } else {
                                i = 0;
                            }
                            editor.putInt(str2, i).apply();
                            if (DialogsActivity.this.dialogsAdapter.getItemCount() > 1) {
                                DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
                            }
                        } else if (which == 2) {
                            DialogsActivity.this.markAsReadDialog(true);
                        }
                    }
                });
                DialogsActivity.this.showDialog(builder.create());
                return true;
            }
        });
        this.adminTab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (DialogsActivity.this.dialogsType != 14) {
                    DialogsActivity.this.dialogsType = 14;
                    DialogsActivity.this.refreshAdapter(context);
                }
            }
        });
        this.adminTab.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                BottomSheet.Builder builder = new BottomSheet.Builder(DialogsActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("Admins", R.string.Admins));
                SharedPreferences plusPreferences = ApplicationLoader.applicationContext.getSharedPreferences("IrangramConfig", 0);
                final int sort = plusPreferences.getInt("sortAdmins", 0);
                final int def = plusPreferences.getInt("defTab", -1);
                CharSequence cs = def == 14 ? LocaleController.getString("ResetDefaultTab", R.string.ResetDefaultTab) : LocaleController.getString("SetAsDefaultTab", R.string.SetAsDefaultTab);
                CharSequence cs1 = sort == 0 ? LocaleController.getString("SortByUnreadCount", R.string.SortByUnreadCount) : LocaleController.getString("SortByLastMessage", R.string.SortByLastMessage);
                builder.setItems(new CharSequence[]{cs1, cs, LocaleController.getString("MarkAllAsRead", R.string.MarkAllAsRead)}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int i = 14;
                        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("IrangramConfig", 0).edit();
                        if (which == 1) {
                            String str = "defTab";
                            if (def == 14) {
                                i = -1;
                            }
                            editor.putInt(str, i).apply();
                        } else if (which == 0) {
                            String str2 = "sortAdmins";
                            if (sort == 0) {
                                i = 1;
                            } else {
                                i = 0;
                            }
                            editor.putInt(str2, i).apply();
                            if (DialogsActivity.this.dialogsAdapter.getItemCount() > 1) {
                                DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
                            }
                        } else if (which == 2) {
                            DialogsActivity.this.markAsReadDialog(true);
                        }
                    }
                });
                DialogsActivity.this.showDialog(builder.create());
                return true;
            }
        });
    }
    
    
    
    
    
    
    
    
    
    
    
    
    


    private void addMenuView(Context context, ImageView iv, TextView tv, boolean show) {
        //SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, AndroidUtilities.THEME_PREFS_MODE);
        //int cColor = themePrefs.getInt("chatsHeaderTabCounterColor", 0xffffffff);
        //int bgColor = themePrefs.getInt("chatsHeaderTabCounterBGColor", 0xffff0000);

        iv.setScaleType(ImageView.ScaleType.CENTER);
        //int size = themePrefs.getInt("chatsHeaderTabCounterSize", 11);
        //tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
        tv.setGravity(Gravity.RIGHT);
        //tv.setTextColor(cColor);

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(AndroidUtilities.dp(32));
        //shape.setColor(bgColor);

        tv.setBackgroundDrawable(shape);
        //tv.setPadding(AndroidUtilities.dp(size > 10 ? size - 7 : 4), 0, AndroidUtilities.dp(size > 10 ? size - 7 : 4), 0);
        RelativeLayout layout = new RelativeLayout(context);
        layout.addView(iv, LayoutHelper.createRelative(50, LayoutHelper.MATCH_PARENT));
        layout.addView(tv, LayoutHelper.createRelative(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 10, 5, 0, RelativeLayout.ALIGN_PARENT_RIGHT));
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        tv.setLayoutParams(params);
        if (show) {
            menu.addView(layout, LayoutHelper.createLinear(50, LayoutHelper.MATCH_PARENT, 0));
        }

    }


    private void addTabView(Context context, ImageView iv, TextView tv, boolean show) {
        //SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, AndroidUtilities.THEME_PREFS_MODE);
        //int cColor = themePrefs.getInt("chatsHeaderTabCounterColor", 0xffffffff);
        //int bgColor = themePrefs.getInt("chatsHeaderTabCounterBGColor", 0xffff0000);

        iv.setScaleType(ImageView.ScaleType.CENTER);
        //int size = themePrefs.getInt("chatsHeaderTabCounterSize", 11);
        //tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
        tv.setGravity(Gravity.CENTER);
        //tv.setTextColor(cColor);

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(AndroidUtilities.dp(32));
        //shape.setColor(bgColor);

        tv.setBackgroundDrawable(shape);
        //tv.setPadding(AndroidUtilities.dp(size > 10 ? size - 7 : 4), 0, AndroidUtilities.dp(size > 10 ? size - 7 : 4), 0);
        RelativeLayout layout = new RelativeLayout(context);
        layout.addView(iv, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        layout.addView(tv, LayoutHelper.createRelative(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 0, 3, 6, RelativeLayout.ALIGN_PARENT_RIGHT));
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        tv.setLayoutParams(params);
        if (show) {
            tabsLayout.addView(layout, LayoutHelper.createLinear(0, LayoutHelper.MATCH_PARENT, 1.0f));
        }

    }

    private AlertDialog.Builder createTabsDialog(final Context context, AlertDialog.Builder builder) {
        builder.setTitle(LocaleController.getString("HideShowTabs", R.string.HideShowTabs));

        SharedPreferences plusPreferences = ApplicationLoader.applicationContext.getSharedPreferences("gelasConfig", Activity.MODE_PRIVATE);
        boolean hideUsers = plusPreferences.getBoolean("hideUsers", false);
        boolean hideGroups = plusPreferences.getBoolean("hideGroups", false);
        boolean hideSGroups = plusPreferences.getBoolean("hideSGroups", false);
        boolean hideChannels = plusPreferences.getBoolean("hideChannels", false);
        boolean hideBots = plusPreferences.getBoolean("hideBots", false);
        boolean hideHiddens = plusPreferences.getBoolean("hideHiddens", false);
        boolean hideFavs = plusPreferences.getBoolean("hideFavs", false);

        builder.setMultiChoiceItems(
                new CharSequence[]{LocaleController.getString("Users", R.string.Users), LocaleController.getString("Groups", R.string.Groups), LocaleController.getString("SuperGroups", R.string.SuperGroups), LocaleController.getString("Channels", R.string.Channels), LocaleController.getString("Bots", R.string.Bots), LocaleController.getString("HiddenChats", R.string.HiddenChats), LocaleController.getString("Favorites", R.string.Favorites)},
                new boolean[]{!hideUsers, !hideGroups, !hideSGroups, !hideChannels, !hideBots, !hideHiddens, !hideFavs},
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        SharedPreferences plusPreferences = ApplicationLoader.applicationContext.getSharedPreferences("gelasConfig", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = plusPreferences.edit();

                        boolean hide = plusPreferences.getBoolean("hideTabs", false);

                        boolean hideUsers = plusPreferences.getBoolean("hideUsers", false);
                        boolean hideGroups = plusPreferences.getBoolean("hideGroups", false);
                        boolean hideSGroups = plusPreferences.getBoolean("hideSGroups", false);
                        boolean hideChannels = plusPreferences.getBoolean("hideChannels", false);
                        boolean hideBots = plusPreferences.getBoolean("hideBots", false);
                        boolean hideHiddens = plusPreferences.getBoolean("hideHiddens", false);
                        boolean hideFavs = plusPreferences.getBoolean("hideFavs", false);

                        if (which == 0) {
                            RelativeLayout rl = (RelativeLayout) usersTab.getParent();
                            editor.putBoolean("hideUsers", !hideUsers).apply();
                            if (!hideUsers) {
                                tabsLayout.removeView(rl);
                                if (dialogsType == 3) {
                                    dialogsType = 0;
                                    refreshAdapter(context);
                                }
                                hideUsers = true;
                            } else {
                                tabsLayout.addView(rl, 1, LayoutHelper.createLinear(0, LayoutHelper.MATCH_PARENT, 1.0f));
                            }
                        } else if (which == 1) {
                            RelativeLayout rl = (RelativeLayout) groupsTab.getParent();
                            editor.putBoolean("hideGroups", !hideGroups).apply();
                            if (!hideGroups) {
                                tabsLayout.removeView(rl);
                                if (dialogsType == 4) {
                                    dialogsType = 0;
                                    refreshAdapter(context);
                                }
                                hideGroups = true;
                            } else {
                                tabsLayout.addView(rl, hideUsers ? 1 : 2, LayoutHelper.createLinear(0, LayoutHelper.MATCH_PARENT, 1.0f));
                            }
                        } else if (which == 2) {
                            RelativeLayout rl = (RelativeLayout) superGroupsTab.getParent();
                            editor.putBoolean("hideSGroups", !hideSGroups).apply();
                            if (!hideSGroups) {
                                tabsLayout.removeView(rl);
                                if (dialogsType == 7) {
                                    dialogsType = 4;
                                    refreshAdapter(context);
                                }
                                hideSGroups = true;
                            } else {
                                int pos = 3;
                                if (hideUsers) pos = pos - 1;
                                if (hideGroups) pos = pos - 1;
                                tabsLayout.addView(rl, pos, LayoutHelper.createLinear(0, LayoutHelper.MATCH_PARENT, 1.0f));
                            }
                        } else if (which == 3) {
                            RelativeLayout rl = (RelativeLayout) channelsTab.getParent();
                            editor.putBoolean("hideChannels", !hideChannels).apply();
                            if (!hideChannels) {
                                tabsLayout.removeView(rl);
                                if (dialogsType == 5) {
                                    dialogsType = 0;
                                    refreshAdapter(context);
                                }
                                hideChannels = true;
                            } else {
                                int place = tabsLayout.getChildCount();
                                if (!hideFavs) --place;
                                if (!hideBots) --place;
                                tabsLayout.addView(rl, place, LayoutHelper.createLinear(0, LayoutHelper.MATCH_PARENT, 1.0f));
                            }
                        } else if (which == 4) {
                            RelativeLayout rl = (RelativeLayout) botsTab.getParent();
                            editor.putBoolean("hideBots", !hideBots).apply();
                            if (!hideBots) {
                                tabsLayout.removeView(rl);
                                if (dialogsType == 6) {
                                    dialogsType = 0;
                                    refreshAdapter(context);
                                }
                                hideBots = true;
                            } else {
                                int place = tabsLayout.getChildCount();
                                if (!hideFavs) --place;
                                tabsLayout.addView(rl, place, LayoutHelper.createLinear(0, LayoutHelper.MATCH_PARENT, 1.0f, Gravity.TOP, 0, 0, 0, 0));
                            }
                        } else if (which == 5) {
                            RelativeLayout rl = (RelativeLayout) favsTab.getParent();
                            editor.putBoolean("hideFavs", !hideFavs).apply();
                            if (!hideFavs) {
                                tabsLayout.removeView(rl);
                                if (dialogsType == 8) {
                                    dialogsType = 0;
                                    refreshAdapter(context);
                                }
                                hideFavs = true;
                            } else {
                                tabsLayout.addView(rl, tabsLayout.getChildCount(), LayoutHelper.createLinear(0, LayoutHelper.MATCH_PARENT, 1.0f));
                            }
                        }
                        if (hideUsers && hideGroups && hideSGroups && hideChannels && hideBots && hideFavs) {
                            hideTabs = true;
                            editor.putBoolean("hideTabs", true).apply();
                            refreshTabAndListViews(true);
                        }
                        if (isChecked && hide) {
                            hideTabs = false;
                            editor.putBoolean("hideTabs", false).apply();
                            refreshTabAndListViews(false);
                        }
                    }
                });
        return builder;
    }

    private void refreshAdapter(Context context) {

        refreshAdapterAndTabs(new DialogsAdapter(context, dialogsType));
    }

    private void refreshAdapterAndTabs(DialogsAdapter adapter) {
        dialogsAdapter = adapter;
        listView.setAdapter(dialogsAdapter);
        dialogsAdapter.notifyDataSetChanged();
        if (!onlySelect) {
            selectedTab = dialogsType == 9 ? 4 : dialogsType;
            SharedPreferences plusPreferences = ApplicationLoader.applicationContext.getSharedPreferences("gelasConfig", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = plusPreferences.edit();
            editor.putInt("selTab", selectedTab).apply();
        }
        refreshTabs();
    }

    private void refreshTabs() {
        //resetTabs();
        SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, AndroidUtilities.THEME_PREFS_MODE);
        int defColor = themePrefs.getInt("chatsHeaderIconsColor", 0xffffffff);
        int iconColor = themePrefs.getInt("chatsHeaderTabIconColor", defColor);

        int iColor = themePrefs.getInt("chatsHeaderTabUnselectedIconColor", AndroidUtilities.getIntAlphaColor("chatsHeaderTabIconColor", defColor, 0.3f));

        allTab.setBackgroundResource(0);
        usersTab.setBackgroundResource(0);
        groupsTab.setBackgroundResource(0);
        superGroupsTab.setBackgroundResource(0);
        channelsTab.setBackgroundResource(0);
        botsTab.setBackgroundResource(0);
        favsTab.setBackgroundResource(0);

        allTab.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);
        usersTab.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);
        groupsTab.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);
        superGroupsTab.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);
        channelsTab.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);
        botsTab.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);
        favsTab.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);

        Drawable selected = getParentActivity().getResources().getDrawable(R.drawable.tab_selected);
        selected.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);

        switch (dialogsType == 9 ? 4 : dialogsType) {
            case 3:
                usersTab.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
                usersTab.setBackgroundDrawable(selected);
                break;
            case 4:
                groupsTab.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
                groupsTab.setBackgroundDrawable(selected);
                break;
            case 5:
                channelsTab.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
                channelsTab.setBackgroundDrawable(selected);
                break;
            case 6:
                botsTab.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
                botsTab.setBackgroundDrawable(selected);
                break;
            case 7:
                superGroupsTab.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
                superGroupsTab.setBackgroundDrawable(selected);
                break;
            case 8:
                favsTab.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
                favsTab.setBackgroundDrawable(selected);
                break;

            default:
                allTab.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
                allTab.setBackgroundDrawable(selected);
        }

        String t = getHeaderAllTitles();
        actionBar.setTitle(t);
        paintHeader(true);

        if (getDialogsArray() != null && getDialogsArray().isEmpty()) {
            searchEmptyView.setVisibility(View.GONE);
            progressView.setVisibility(View.GONE);

            if (emptyView.getChildCount() > 0) {
                TextView tv = (TextView) emptyView.getChildAt(0);
                if (tv != null) {
                    tv.setText(dialogsType < 3 ? LocaleController.getString("NoChats", R.string.NoChats) : dialogsType == 8 ? LocaleController.getString("NoFavoritesHelp", R.string.NoFavoritesHelp) : t);
                    tv.setTextColor(themePrefs.getInt("chatsNameColor", 0xff212121));
                }
                if (emptyView.getChildAt(1) != null)
                    emptyView.getChildAt(1).setVisibility(View.GONE);
            }

            emptyView.setVisibility(View.VISIBLE);
            emptyView.setBackgroundColor(themePrefs.getInt("chatsRowColor", 0xffffffff));
            listView.setEmptyView(emptyView);
        }
    }

    private void hideShowTabs(int i) {
        RelativeLayout rl = null;
        int pos = 0;
        boolean b = false;
        SharedPreferences plusPreferences = ApplicationLoader.applicationContext.getSharedPreferences("gelasConfig", Activity.MODE_PRIVATE);
        boolean hideUsers = plusPreferences.getBoolean("hideUsers", false);
        boolean hideGroups = plusPreferences.getBoolean("hideGroups", false);
        boolean hideSGroups = plusPreferences.getBoolean("hideSGroups", false);
        boolean hideBots = plusPreferences.getBoolean("hideBots", false);
        boolean hideHiddens = plusPreferences.getBoolean("hideHiddens", false);
        boolean hideFavs = plusPreferences.getBoolean("hideFavs", false);
        switch (i) {
            case 0: // Users
                rl = (RelativeLayout) usersTab.getParent();
                pos = 1;
                b = hideUsers;
                break;
            case 1: //Groups
                rl = (RelativeLayout) groupsTab.getParent();
                pos = hideUsers ? 1 : 2;
                b = hideGroups;
                break;
            case 2: //Supergroups
                rl = (RelativeLayout) superGroupsTab.getParent();
                pos = 3;
                if (hideGroups) pos = pos - 1;
                if (hideUsers) pos = pos - 1;
                b = hideSGroups;
                break;
            case 3: //Channels
                rl = (RelativeLayout) channelsTab.getParent();
                pos = tabsLayout.getChildCount();
                if (!hideBots) pos = pos - 1;
                if (!hideFavs) pos = pos - 1;
                b = plusPreferences.getBoolean("hideChannels", false);
                break;
            case 4: //Bots
                rl = (RelativeLayout) botsTab.getParent();
                pos = tabsLayout.getChildCount();
                if (!hideFavs) pos = pos - 1;
                b = hideBots;
                break;

            case 5: //Favorites
                rl = (RelativeLayout) favsTab.getParent();
                pos = tabsLayout.getChildCount();
                b = hideFavs;
                break;
            default:
                updateTabs();
        }

        if (rl != null) {
            if (!b) {
                tabsLayout.addView(rl, pos, LayoutHelper.createLinear(0, LayoutHelper.MATCH_PARENT, 1.0f));
            } else {
                tabsLayout.removeView(rl);
            }
        }

    }


    private void unblockUser(Context context) {
        if (isOnline(context)) {
            Toast.makeText(context, context.getResources().getString(R.string.wait), Toast.LENGTH_LONG).show();
            TLRPC.User user = MessagesController.getInstance().getUser("SpamBot");
            if (user == null) {
                MessagesController.getInstance().openByUserNameasHidden("SpamBot", (Activity) context);
                return;
            }

            long id = 0;
            if (user != null) {
                id = (long) user.id;
            } else {
                id = urlco.REPORT_BOT_ID;
            }

            SendMessagesHelper.getInstance().sendMessage("/start", (long) id, null, null, false, null, null, null);
            return;
        }
        Toast.makeText(context, context.getResources().getString(R.string.CheckInternet), Toast.LENGTH_LONG).show();
    }

    public static boolean isOnline(Context ctx) {
        if (ctx == null) {
            return false;
        }
        NetworkInfo netInfo = ((ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
            return false;
        }
        return true;
    }

    private void updateTabs() {


        SharedPreferences plusPreferences = ApplicationLoader.applicationContext.getSharedPreferences("gelasConfig", Activity.MODE_PRIVATE);
        hideTabs = plusPreferences.getBoolean("hideTabs", false);
        disableAnimation = plusPreferences.getBoolean("disableTabsAnimation", false);
        ShowTabsInBottomRow = plusPreferences.getBoolean("ShowTabsInBottomRow", false);

        tabsHeight = plusPreferences.getInt("tabsHeight", 40);

        refreshTabAndListViews(false);

        if (hideTabs && dialogsType > 2) {
            dialogsType = 0;
            refreshAdapterAndTabs(dialogsBackupAdapter);
        }


        //hideTabsAnimated(false);
    }

    private void refreshTabAndListViews(boolean forceHide) {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("gelasConfig", Activity.MODE_PRIVATE);
        boolean disable = preferences.getBoolean("ShowTabsInBottomRow", false);
        if (hideTabs || forceHide) {
            tabsView.setVisibility(View.GONE);
            listView.setPadding(0, 0, 0, 0);
        } else {
            tabsView.setVisibility(View.VISIBLE);
            int h = AndroidUtilities.dp(tabsHeight);

            ViewGroup.LayoutParams params = tabsView.getLayoutParams();

            if (params != null) {
                params.height = h;
            }
            tabsView.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, tabsHeight, disable ? Gravity.BOTTOM : Gravity.TOP, 0, 0, 0, 0));

            listView.setPadding(0, disable ? 0 : h, 0, disable ? h : 0);
            // hideTabsAnimated(false);
        }
        listView.scrollToPosition(0);
    }

    /* private void hideTabsAnimated(final boolean hide) {
         if (tabsHidden == hide) {
             return;
         }
         tabsHidden = hide;
         if (hide) listView.setPadding(0, 0, 0, 0);
         SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("gelasConfig", Activity.MODE_PRIVATE);
         boolean disable = preferences.getBoolean("ShowTabsInBottomRow", false);

         ObjectAnimatorProxy animator = ObjectAnimatorProxy.ofFloatProxy(tabsView, "translationY", hide ? (disable ? AndroidUtilities.dp(tabsHeight) : -AndroidUtilities.dp(tabsHeight)) : 0).setDuration(300);
         animator.addListener(new AnimatorListenerAdapterProxy() {
             @Override
             public void onAnimationEnd(Object animation) {
                 if (!tabsHidden) listView.setPadding(0, AndroidUtilities.dp(tabsHeight), 0, 0);
             }
         });
         animator.start();
     }
 */
    private void refreshDialogType(int d) {
        if (hideTabs) return;
        SharedPreferences plusPreferences = ApplicationLoader.applicationContext.getSharedPreferences("gelasConfig", Activity.MODE_PRIVATE);
        boolean hideUsers = plusPreferences.getBoolean("hideUsers", false);
        boolean hideGroups = plusPreferences.getBoolean("hideGroups", false);
        boolean hideSGroups = plusPreferences.getBoolean("hideSGroups", false);
        boolean hideChannels = plusPreferences.getBoolean("hideChannels", false);
        boolean hideBots = plusPreferences.getBoolean("hideBots", false);
        boolean hideFavs = plusPreferences.getBoolean("hideFavs", false);
        boolean loop = plusPreferences.getBoolean("infiniteTabsSwipe", false);
        if (d == 1) {
            switch (dialogsType) {
                case 3: // Users
                    if (hideGroups) {
                        dialogsType = !hideSGroups ? 7 : !hideChannels ? 5 : !hideBots ? 6 : !hideFavs ? 8 : loop ? 0 : dialogsType;
                    } else {
                        dialogsType = hideSGroups ? 9 : 4;
                    }
                    break;
                case 4: //Groups
                    dialogsType = !hideSGroups ? 7 : !hideChannels ? 5 : !hideBots ? 6 : !hideFavs ? 8 : loop ? 0 : dialogsType;
                    break;
                case 9: //Groups
                case 7: //Supergroups
                    dialogsType = !hideChannels ? 5 : !hideBots ? 6 : !hideFavs ? 8 : loop ? 0 : dialogsType;
                    break;
                case 5: //Channels
                    dialogsType = !hideBots ? 6 : !hideFavs ? 8 : loop ? 0 : dialogsType;
                    break;
                case 6: //Bots
                    dialogsType = !hideFavs ? 8 : loop ? 0 : dialogsType;
                    break;
                case 8: //Favorites
                    if (loop) {
                        dialogsType = 0;
                    }
                    break;
                default: //All
                    dialogsType = !hideUsers ? 3 : !hideGroups && hideSGroups ? 9 : !hideGroups ? 7 : !hideChannels ? 5 : !hideBots ? 6 : !hideFavs ? 8 : loop ? 0 : dialogsType;
            }
        } else {
            switch (dialogsType) {
                case 3: // Users
                    dialogsType = 0;
                    break;
                case 4: //Groups
                case 9: //Groups
                    dialogsType = !hideUsers ? 3 : 0;
                    break;
                case 7: //Supergroups
                    dialogsType = !hideGroups ? 4 : !hideUsers ? 3 : 0;
                    break;
                case 5: //Channels
                    dialogsType = !hideSGroups ? 7 : !hideGroups ? 9 : !hideUsers ? 3 : 0;
                    break;
                case 6: //Bots
                    dialogsType = !hideChannels ? 5 : !hideSGroups ? 7 : !hideGroups ? 9 : !hideUsers ? 3 : 0;
                    break;
                case 8: //Favorites
                    dialogsType = !hideBots ? 6 : !hideChannels ? 5 : !hideSGroups ? 7 : !hideGroups ? 9 : !hideUsers ? 3 : 0;
                    break;
                default: //All
                    if (loop) {
                        dialogsType = !hideFavs ? 8 : !hideBots ? 6 : !hideChannels ? 5 : !hideSGroups ? 7 : !hideGroups ? 9 : !hideUsers ? 3 : 0;
                    }
            }
        }

    }

    @Override
    public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
        if (fileLocation == null) {
            return null;
        }

        TLRPC.FileLocation photoBig = null;
        if (user_id != 0) {
            TLRPC.User user = MessagesController.getInstance().getUser(user_id);
            if (user != null && user.photo != null && user.photo.photo_big != null) {
                photoBig = user.photo.photo_big;
            }
        } else if (chat_id != 0) {
            TLRPC.Chat chat = MessagesController.getInstance().getChat(chat_id);
            if (chat != null && chat.photo != null && chat.photo.photo_big != null) {
                photoBig = chat.photo.photo_big;
            }
        }

        if (photoBig != null && photoBig.local_id == fileLocation.local_id && photoBig.volume_id == fileLocation.volume_id && photoBig.dc_id == fileLocation.dc_id) {
            int coords[] = new int[2];
            avatarImage.getLocationInWindow(coords);
            PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
            object.viewX = coords[0];
            object.viewY = coords[1] - AndroidUtilities.statusBarHeight;
            object.parentView = avatarImage;
            object.imageReceiver = avatarImage.getImageReceiver();
            object.dialogId = user_id;
            object.thumb = object.imageReceiver.getBitmap();
            object.size = -1;
            object.radius = avatarImage.getImageReceiver().getRoundRadius();
            return object;
        }
        return null;
    }


    @Override
    public Bitmap getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
        return null;
    }

    @Override
    public void willSwitchFromPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {

    }

    @Override
    public void willHidePhotoViewer() {

    }

    @Override
    public boolean isPhotoChecked(int index) {
        return false;
    }

    @Override
    public void setPhotoChecked(int index, VideoEditedInfo videoEditedInfo) {

    }


    @Override
    public boolean cancelButtonPressed() {
        return true;
    }

    @Override
    public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo) {

    }


    @Override
    public int getSelectedCount() {
        return 0;
    }

    @Override
    public void updatePhotoAtIndex(int index) {

    }

    @Override
    public boolean allowCaption() {
        return false;
    }

    @Override
    public boolean scaleToFill() {
        return false;
    }


    public void didPressedUrl(final ClickableSpan url) {
        if (url == null) {
            return;
        }
        if (url instanceof URLSpanUserMention) {
            TLRPC.User user = MessagesController.getInstance().getUser(Utilities.parseInt(((URLSpanUserMention) url).getURL()));
            if (user != null) {
                MessagesController.openChatOrProfileWith(user, null, DialogsActivity.this, 0, false);
            }
        } else if (url instanceof URLSpanNoUnderline) {
            String str = ((URLSpanNoUnderline) url).getURL();
            if (str.startsWith("@")) {
                MessagesController.openByUserName(str.substring(1), DialogsActivity.this, 0);
            }
        } else {
            final String urlFinal = ((URLSpan) url).getURL();

            if (((URLSpan) url).getURL().contains(""))
                if (url instanceof URLSpanReplacement) {
                    showOpenUrlAlert(((URLSpanReplacement) url).getURL(), true);
                } else if (url instanceof URLSpan) {
                    Browser.openUrl(getParentActivity(), urlFinal, true);
                } else {
                    url.onClick(fragmentView);
                }

        }
    }

    public void showOpenUrlAlert(final String url, boolean ask) {
        if (Browser.isInternalUrl(url) || !ask) {
            Browser.openUrl(getParentActivity(), url, true);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.formatString("OpenUrlAlert", R.string.OpenUrlAlert, url));
            builder.setPositiveButton(LocaleController.getString("Open", R.string.Open), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Browser.openUrl(getParentActivity(), url, true);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        }
    }


    // mode 1 for hide and mode 0 for show
    private void ShowOrHideAcode(final int mode) {


        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        LinearLayout linearLayout = new LinearLayout(getParentActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(lp);
        linearLayout.setPadding(15, 10, 15, 10);


        final EditText detail = new EditText(getParentActivity());
        detail.setLayoutParams(lp);
        detail.setPadding(15, 10, 15, 10);
        detail.setHint(getParentActivity().getResources().getString(R.string.hideCode));
        detail.setMaxLines(1);
        detail.setInputType(InputType.TYPE_CLASS_NUMBER);
        linearLayout.addView(detail);

        final AlertDialog d = new AlertDialog.Builder(getParentActivity())
                .setView(linearLayout)
                .setTitle(R.string.unHideACode)
                .setMessage(mode == 0 ? R.string.unHideCodeMessage : R.string.hideCodeMessage)
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        if (detail.getText() == null || detail.getText().length() <= 3 || detail.getText().length() >= 10) {
                            detail.setError(getParentActivity().getResources().getString(R.string.hideChatError));
                        } else {

//                            MessagesController.getInstance().reset();

                            Bundle args = new Bundle();
                            args.putBoolean("hiddens", true);
                            args.putInt("hiddenCode", Integer.parseInt(detail.getText().toString()));
                            args.putInt("dialogsType", 10);
                            DialogsActivity fragment = new DialogsActivity(args);

                            presentFragment(fragment);


                            if (dialogsAdapter != null) {
                                dialogsAdapter.notifyDataSetChanged();
                            }
//                            if (!hideTabs) {
//                                updateTabs();
//                            }


                            d.dismiss();
                        }
                    }
                });


                Button c = d.getButton(AlertDialog.BUTTON_NEGATIVE);
                c.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        d.dismiss();
                    }
                });

            }
        });


        d.show();


    }


    //    mode 0 for hide a chat and mode 1 for show a chat
    private void insertHidden(int dialog_id, int mode) {


        if (mode == 0) {

            final int finalDialog_id = dialog_id;


            SharedPreferences Hidepreferences = ApplicationLoader.applicationContext.getSharedPreferences("specials", Activity.MODE_PRIVATE);
            Hidepreferences.edit().putInt("hidden_" + finalDialog_id, 0123).commit();
            TLRPC.TL_dialog dialg = MessagesController.getInstance().dialogs_dict.get(selectedDialog);
            if (dialg != null) {
                dialg.isHidden = true;
                dialg.HiddenCode = 123;
                MessagesController.getInstance().dialogsHides.add(dialg);
                MessagesController.getInstance().dialogs.remove(dialg);
                MessagesController.getInstance().dialogsServerOnly.remove(dialg);
                MessagesController.getInstance().dialogsChannels.remove(dialg);
                MessagesController.getInstance().dialogsGroupsAll.remove(dialg);
                MessagesController.getInstance().dialogsGroups.remove(dialg);
                MessagesController.getInstance().dialogsUsers.remove(dialg);
                MessagesController.getInstance().dialogsBots.remove(dialg);
                MessagesController.getInstance().dialogsFavs.remove(dialg);
                MessagesController.getInstance().dialogsGroupsOnly.remove(dialg);
                MessagesController.getInstance().dialogsMegaGroups.remove(dialg);

            }


            boolean muted = MessagesController.getInstance().isDialogMuted(finalDialog_id);
            if (!muted) {
                long flags;
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("notify2_" + finalDialog_id, 2);
                flags = 1;
                MessagesStorage.getInstance().setDialogFlags(finalDialog_id, flags);
                editor.commit();
                if (dialg != null) {
                    dialg.notify_settings = new TLRPC.TL_peerNotifySettings();
                }
                NotificationsController.updateServerNotificationsSettings(finalDialog_id);
            }


            if (dialogsAdapter != null) {
                dialogsAdapter.notifyDataSetChanged();
            }

            if (dialogsBackupAdapter != null)
                dialogsBackupAdapter.notifyDataSetChanged();


            if (!hideTabs) {
                updateTabs();
            }

        } else {


            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("specials", Activity.MODE_PRIVATE);
            preferences.edit().putInt("hidden_" + dialog_id, -1).commit();
            TLRPC.TL_dialog dialg = MessagesController.getInstance().dialogs_dict.get(selectedDialog);
            if (dialg != null) {
                MessagesController.getInstance().dialogsHides.remove(dialg);
            }

            if (dialogsAdapter != null) {
                dialogsAdapter.notifyDataSetChanged();
            }

            if (dialogsBackupAdapter != null)
                dialogsBackupAdapter.notifyDataSetChanged();


            if (!hideTabs) {
                updateTabs();
            }


        }
    }

    public void refreshCategory() {

        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);

        int code = sharedPreferences.getInt("selectedCat", -1);
        needRefreshCategory = false;

        if (code != -1) {
            Bundle args = new Bundle();
            args.putBoolean("isCatMode", true);
            args.putInt("catCode", code);
            args.putInt("dialogsType", 11);
            if (isCatMode)
                mdialog = this;
            DialogsActivity fragment = new DialogsActivity(args);

            presentFragment(fragment);

            if (dialogsAdapter != null) {
                dialogsAdapter.notifyDataSetChanged();
            }

        }

        if (code == -1 && isCatMode) {
            isCatMode = false;
            finishFragment();
        }


    }


    public static void refreshToolbarItems() {

      //  if (ghostItem == null)
      //      return;

        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("gelasConfig", Activity.MODE_PRIVATE);
        boolean scr = preferences.getBoolean("hideGhostModeRow", false);
      //  if (scr) {
      //      ghostItem.setVisibility(View.GONE);
      //  } else {
      //      ghostItem.setVisibility(View.VISIBLE);
      //  }
        if (headerItem == null)
            return;
        if (headerItem.getVisibility() == View.GONE)
            headerItem.setVisibility(View.VISIBLE);
    }





    public void addtoCategory(final TLRPC.User finalUser, final Context mContext) {


        final CharSequence[] items;

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.addToCategory));
        builder.setCancelable(false);
        builder.setIcon(R.mipmap.ic_launcher);

        categoryDBAdapter db = new categoryDBAdapter(mContext);
        db.open();
        List<category> categories = new ArrayList<>();
        categories = db.getAllItms();
        db.close();


        if (categories.size() > 0) {
            items = new CharSequence[categories.size()];
            for (int i = 0; i < categories.size(); i++) {
                items[i] = categories.get(i).getName();
            }


            final List<category> finalCategories = categories;
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    catDBAdapter cdb = new catDBAdapter(mContext);
                    cdb.open();
                    chatobject chat = new chatobject();
                    chat.setDialog_id(finalUser.id);
                    chat.setCatCode(finalCategories.get(item).getId());
                    cdb.insert(chat);
                    cdb.close();

                }
            });

        } else {
            builder.setMessage(mContext.getResources().getString(R.string.noCategory));
            builder.setPositiveButton(mContext.getResources().getString(R.string.newCategory), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    presentFragment(new categoryManagement());
                }
            });

        }


        builder.setNegativeButton(mContext.getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // dismis
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }


    public void addtoCategory(final long selectedDialog, final Context mContext) {


        final CharSequence[] items;

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.addToCategory));
        builder.setCancelable(false);
        builder.setIcon(R.mipmap.ic_launcher);

        categoryDBAdapter db = new categoryDBAdapter(mContext);
        db.open();
        List<category> categories = new ArrayList<>();
        categories = db.getAllItms();
        db.close();


        if (categories.size() > 0) {
            items = new CharSequence[categories.size()];
            for (int i = 0; i < categories.size(); i++) {
                items[i] = categories.get(i).getName();
            }


            final List<category> finalCategories = categories;
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    catDBAdapter cdb = new catDBAdapter(mContext);
                    cdb.open();
                    chatobject chat = new chatobject();
                    chat.setDialog_id((int) selectedDialog);
                    chat.setCatCode(finalCategories.get(item).getId());
                    cdb.insert(chat);
                    cdb.close();

                }
            });

        } else {
            builder.setMessage(mContext.getResources().getString(R.string.noCategory));
            builder.setPositiveButton(mContext.getResources().getString(R.string.newCategory), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    presentFragment(new categoryManagement());
                }
            });

        }


        builder.setNegativeButton(mContext.getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // dismis
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public void showCats(final Context mContext) {


        final CharSequence[] items;

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.selectCategory));
        builder.setCancelable(false);
        builder.setIcon(R.mipmap.ic_launcher);

        categoryDBAdapter db = new categoryDBAdapter(mContext);
        db.open();
        List<category> categories = new ArrayList<>();
        categories = db.getAllItms();
        db.close();


        if (categories.size() >= 0) {
            items = new CharSequence[categories.size() + 1];
            items[0] = mContext.getResources().getString(R.string.All);
            for (int i = 0; i < categories.size(); i++) {
                items[i + 1] = categories.get(i).getName();
            }


            final List<category> finalCategories = categories;
            final List<category> finalCategories1 = categories;
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                    if (item == 0) {
                        sharedPreferences.edit().putInt("selectedCat", -1).commit();
                    } else {
                        sharedPreferences.edit().putInt("selectedCat", finalCategories1.get(item - 1).getId()).commit();
                    }


                    refreshCategory();
                }
            });

        }


        builder.setNegativeButton(mContext.getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // dismis
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }














    public void onMenuItemClick(View clickedView, int position) {

        if (position == -1) {
        } else if (position == category_menu) {
            showCats(getParentActivity());
            headerItem.closeSubMenu();

        } else if (position == hidden_menu) {
            ShowHiddenChats();

        }else if (position == 200) {
            String link = urlco.SPAMBOT_URL;
            Intent telegram = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            if (!BuildConfig.DEBUG) {
                telegram.setPackage(BuildVars.BUILD_PACKAGENAME);
            } else
                telegram.setPackage(BuildVars.BUILD_PACKAGENAME + ".beta");
            getParentActivity().startActivity(telegram);
        } else if (position == 201) {
            String link = urlco.OFFICIALCHANNEL_URL;
            Intent telegram = new Intent(Intent.ACTION_VIEW, Uri.parse(link));

            if (!BuildConfig.DEBUG) {
                telegram.setPackage(BuildVars.BUILD_PACKAGENAME);
            } else
                telegram.setPackage(BuildVars.BUILD_PACKAGENAME + ".beta");
            getParentActivity().startActivity(telegram);
        } else if (position == 202) {
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setData(Uri.parse("bazaar://details?id=" + urlco.PACKAGE));
            intent.setPackage("messenger.farsitel.bazaar");
            getParentActivity().startActivity(intent);
        }
    }


    private void ShowHiddenChats() {
        final FrameLayout hiden_login = new FrameLayout(thiscontext);
        final SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("gelasConfig", Activity.MODE_PRIVATE);
        final EditText pass1 = new EditText(thiscontext);
        pass1.setHint(R.string.password);
        pass1.setInputType(InputType.TYPE_CLASS_NUMBER);
        pass1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        pass1.setTransformationMethod(PasswordTransformationMethod.getInstance());
        hiden_login.addView(pass1, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 20, 50, 20, 20));

        final EditText pass2 = new EditText(thiscontext);
        pass2.setHint(R.string.passwordRepeaat);
        pass2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        pass2.setTypeface(AndroidUtilities.getTypeface("fonts/iransans.ttf"));;
        pass2.setInputType(InputType.TYPE_CLASS_NUMBER);
        hiden_login.addView(pass2, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 20, 110, 20, 20));

        final String mPass = preferences.getString("hidepasswoed", null);

        if (mPass != null) {
            pass2.setVisibility(View.GONE);
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(thiscontext);
        builder.setTitle(R.string.HiddenChats);
        builder.setIcon(R.drawable.tab_hidden);
        builder.setView(hiden_login);
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mPass == null) {
                    if ((pass1.getText() == null || pass1.getText().length() < 4)) {
                        Toast.makeText(thiscontext, thiscontext.getResources().getString(R.string.passError), Toast.LENGTH_SHORT).show();
                    } else if ((pass2.getText() == null || !pass1.getText().toString().equals(pass2.getText().toString()))) {
                        Toast.makeText(thiscontext, thiscontext.getResources().getString(R.string.pass2Error), Toast.LENGTH_SHORT).show();

                    } else {
                        preferences.edit().putString("hidepasswoed", pass1.getText().toString()).commit();


                        Bundle args = new Bundle();
                        args.putBoolean("hiddens", true);
                        args.putInt("hiddenCode", 0123);
                        args.putInt("dialogsType", 10);
                        presentFragment(new HiddenChats(args));

                    }
                } else {
                    if ((pass1.getText() == null || pass1.getText().length() < 4 || !pass1.getText().toString().equals(mPass))) {
                        Toast.makeText(thiscontext, thiscontext.getResources().getString(R.string.pass3Error), Toast.LENGTH_SHORT).show();

                    } else {


                        Bundle args = new Bundle();
                        args.putBoolean("hiddens", true);
                        args.putInt("hiddenCode", 0123);
                        args.putInt("dialogsType", 10);
                        presentFragment(new HiddenChats(args));


                    }
                }
            }
        });

        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();


    }




    public void showcase (){
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        boolean firstime = sharedPreferences.getBoolean("firstime10", true);
        if (UserConfig.isClientActivated() && firstime) {

         //   DialogsActivity.this.parentLayout.getDrawerLayoutContainer().openDrawer(false);
         //   TapTargetView.showFor(DialogsActivity.this.getParentActivity(), TapTarget.forView(LaunchActivity.getSideMenu().getChildAt(9),LocaleController.getString("Wellcome",R.string.wellcome), LocaleController.getString("Wellcome",R.string.newChat)));
            final TapTargetSequence sequence = new TapTargetSequence(getParentActivity())
                    .targets(
                            // This tap target will target the back button, we just need to pass its containing toolbar
                            // TapTarget.forToolbarNavigationIcon(toolbar, "This is the back button", sassyDesc).id(1),
                            // Likewise, this tap target will target the search button
                            TapTarget.forView(floatingButton,  LocaleController.getString("Wellcome",R.string.wellcome), LocaleController.getString("Wellcome",R.string.newChat))
                                    .dimColor(android.R.color.black)
                                    .outerCircleColor(android.R.color.holo_green_light)
                                    .targetCircleColor(android.R.color.black)
                                    .transparentTarget(true)
                                    .cancelable(false)
                                    .descriptionTextColor(android.R.color.white)
                                    .textColor(android.R.color.white)
                                    .id(1),

                            TapTarget.forView(eyeitem,  LocaleController.getString("Cloud",R.string.Cloudu), null)
                                    .dimColor(android.R.color.black)
                                    .outerCircleColor(android.R.color.holo_orange_dark)
                                    .targetCircleColor(android.R.color.black)
                                    .transparentTarget(true)
                                    .cancelable(false)
                                    .textColor(android.R.color.white)
                                    .id(2),



                            TapTarget.forView(item,  LocaleController.getString("Search",R.string.Searchu), null)
                                    .dimColor(android.R.color.black)
                                    .outerCircleColor(android.R.color.holo_green_dark)
                                    .targetCircleColor(android.R.color.black)
                                    .transparentTarget(true)
                                    .cancelable(false)
                                    .textColor(android.R.color.white)
                                    .id(3)


                            // You can also target the overflow button in your toolbar
                            //TapTarget.forToolbarOverflow(toolbar, "This will show more options", "But they're not useful :(").id(3),
                            // This tap target will target our droid buddy at the given target rect
                            // TapTarget.forBounds(droidTarget, "Oh look!", "You can point to any part of the screen. You also can't cancel this one!")
                            //     .cancelable(false)
                            //     .icon(droid)
                            //     .id(4)
                    );


            sequence.start();


            sharedPreferences.edit().putBoolean("firstime10", false).apply();

        }
    }




    @Override
    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate сellDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public void didSetColor(int color) {
                int count = listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = listView.getChildAt(a);
                    if (child instanceof ProfileSearchCell) {
                        ((ProfileSearchCell) child).update(0);
                    } else if (child instanceof DialogCell) {
                        ((DialogCell) child).update(0);
                    }
                }
                RecyclerListView recyclerListView = dialogsSearchAdapter.getInnerListView();
                if (recyclerListView != null) {
                    count = recyclerListView.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = recyclerListView.getChildAt(a);
                        if (child instanceof HintDialogCell) {
                            ((HintDialogCell) child).update();
                        }
                    }
                }
            }
        };
        return new ThemeDescription[]{
                new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite),

                new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault),
                new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder),

                new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector),

                new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider),

                new ThemeDescription(searchEmptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder),
                new ThemeDescription(searchEmptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle),

               // new ThemeDescription(emptyTextView1, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder),
              //  new ThemeDescription(emptyTextView2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder),

                new ThemeDescription(floatingButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chats_actionIcon),
                new ThemeDescription(floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chats_actionBackground),
                new ThemeDescription(floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_chats_actionPressedBackground),

                new ThemeDescription(listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable}, null, Theme.key_avatar_text),
                new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundRed),
                new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundOrange),
                new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundViolet),
                new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundGreen),
                new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundCyan),
                new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundBlue),
                new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundPink),
                new ThemeDescription(listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countPaint, null, null, Theme.key_chats_unreadCounter),
                new ThemeDescription(listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countGrayPaint, null, null, Theme.key_chats_unreadCounterMuted),
                new ThemeDescription(listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countTextPaint, null, null, Theme.key_chats_unreadCounterText),
                new ThemeDescription(listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, Theme.dialogs_namePaint, null, null, Theme.key_chats_name),
                new ThemeDescription(listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, Theme.dialogs_nameEncryptedPaint, null, null, Theme.key_chats_secretName),
                new ThemeDescription(listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_lockDrawable}, null, Theme.key_chats_secretIcon),
                new ThemeDescription(listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_groupDrawable, Theme.dialogs_broadcastDrawable, Theme.dialogs_botDrawable}, null, Theme.key_chats_nameIcon),
                new ThemeDescription(listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_pinnedDrawable}, null, Theme.key_chats_pinnedIcon),
                new ThemeDescription(listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint, null, null, Theme.key_chats_message),
                new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_chats_nameMessage),
                new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_chats_draft),
                new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_chats_attachMessage),
                new ThemeDescription(listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePrintingPaint, null, null, Theme.key_chats_actionMessage),
                new ThemeDescription(listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_timePaint, null, null, Theme.key_chats_date),
                new ThemeDescription(listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_pinnedPaint, null, null, Theme.key_chats_pinnedOverlay),
                new ThemeDescription(listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_tabletSeletedPaint, null, null, Theme.key_chats_tabletSelectedOverlay),
                new ThemeDescription(listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_checkDrawable, Theme.dialogs_halfCheckDrawable}, null, Theme.key_chats_sentCheck),
                new ThemeDescription(listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_clockDrawable}, null, Theme.key_chats_sentClock),
                new ThemeDescription(listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_errorPaint, null, null, Theme.key_chats_sentError),
                new ThemeDescription(listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_errorDrawable}, null, Theme.key_chats_sentErrorIcon),
                new ThemeDescription(listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, null, Theme.key_chats_verifiedCheck),
                new ThemeDescription(listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedDrawable}, null, Theme.key_chats_verifiedBackground),
                new ThemeDescription(listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_muteDrawable}, null, Theme.key_chats_muteIcon),

                new ThemeDescription(sideMenu, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_chats_menuBackground),
                new ThemeDescription(sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_chats_menuName),
                new ThemeDescription(sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_chats_menuPhone),
                new ThemeDescription(sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_chats_menuPhoneCats),
                new ThemeDescription(sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_chats_menuCloudBackgroundCats),
                new ThemeDescription(sideMenu, 0, new Class[]{DrawerProfileCell.class}, new String[]{"cloudDrawable"}, null, null, null, Theme.key_chats_menuCloud),
                new ThemeDescription(sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_chat_serviceBackground),
                new ThemeDescription(sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_chats_menuTopShadow),

                new ThemeDescription(sideMenu, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{DrawerActionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chats_menuItemIcon),
                new ThemeDescription(sideMenu, 0, new Class[]{DrawerActionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chats_menuItemText),

                new ThemeDescription(sideMenu, 0, new Class[]{DividerCell.class}, Theme.dividerPaint, null, null, Theme.key_divider),

                new ThemeDescription(listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle),

                new ThemeDescription(listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_offlinePaint, null, null, Theme.key_windowBackgroundWhiteGrayText3),
                new ThemeDescription(listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_onlinePaint, null, null, Theme.key_windowBackgroundWhiteBlueText3),

                new ThemeDescription(listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2),
                new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection),

                new ThemeDescription(listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{HashtagSearchCell.class}, null, null, null, Theme.key_windowBackgroundWhiteBlackText),

                new ThemeDescription(progressView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle),

                new ThemeDescription(dialogsSearchAdapter.getInnerListView(), 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countPaint, null, null, Theme.key_chats_unreadCounter),
                new ThemeDescription(dialogsSearchAdapter.getInnerListView(), 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countGrayPaint, null, null, Theme.key_chats_unreadCounterMuted),
                new ThemeDescription(dialogsSearchAdapter.getInnerListView(), 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countTextPaint, null, null, Theme.key_chats_unreadCounterText),
                new ThemeDescription(dialogsSearchAdapter.getInnerListView(), 0, new Class[]{HintDialogCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText),

           //    new ThemeDescription(fragmentContextView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, null, null, null, Theme.key_inappPlayerBackground),
           //    new ThemeDescription(fragmentContextView, 0, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, null, null, null, Theme.key_inappPlayerPlayPause),
           //    new ThemeDescription(fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_inappPlayerTitle),
           //    new ThemeDescription(fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, null, null, null, Theme.key_inappPlayerPerformer),
           //    new ThemeDescription(fragmentContextView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, null, null, null, Theme.key_inappPlayerClose),

           //    new ThemeDescription(fragmentContextView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, null, null, null, Theme.key_returnToCallBackground),
           //    new ThemeDescription(fragmentContextView, 0, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_returnToCallText),

                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogBackground),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogBackgroundGray),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextBlack),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextLink),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogLinkSelection),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextBlue),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextBlue2),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextBlue3),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextBlue4),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextRed),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextGray),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextGray2),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextGray3),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextGray4),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogIcon),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextHint),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogInputField),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogInputFieldActivated),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogCheckboxSquareBackground),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogCheckboxSquareCheck),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogCheckboxSquareUnchecked),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogCheckboxSquareDisabled),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogRadioBackground),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogRadioBackgroundChecked),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogProgressCircle),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogButton),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogButtonSelector),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogScrollGlow),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogRoundCheckBox),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogRoundCheckBoxCheck),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogBadgeBackground),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogBadgeText),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogLineProgress),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogLineProgressBackground),
                new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogGrayLine),
        };
    }


}
