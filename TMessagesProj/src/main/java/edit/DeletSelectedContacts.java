package edit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.zgram.messenger.AndroidUtilities;
import org.zgram.messenger.ApplicationLoader;
import org.zgram.messenger.ContactsController;
import org.zgram.messenger.FileLog;
import org.zgram.messenger.LocaleController;
import org.zgram.messenger.MessagesController;
import org.zgram.messenger.NotificationCenter;
import org.zgram.messenger.R;
import org.zgram.messenger.Utilities;
import org.zgram.messenger.support.widget.LinearLayoutManager;
import org.zgram.messenger.support.widget.RecyclerView;
import org.zgram.messenger.support.widget.RecyclerView.Adapter;
import org.zgram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.zgram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.zgram.messenger.support.widget.RecyclerView.ViewHolder;
import org.zgram.messenger.volley.DefaultRetryPolicy;
import org.zgram.tgnet.TLRPC.InputUser;
import org.zgram.tgnet.TLRPC.TL_contact;
import org.zgram.tgnet.TLRPC.User;
import org.zgram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.zgram.ui.ActionBar.ActionBarMenu;
import org.zgram.ui.ActionBar.AlertDialog.Builder;
import org.zgram.ui.ActionBar.BaseFragment;
import org.zgram.ui.ActionBar.Theme;
import org.zgram.ui.Adapters.SearchAdapterHelper;
import org.zgram.ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.zgram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.zgram.ui.Cells.GroupCreateSectionCell;
import org.zgram.ui.Cells.GroupCreateUserCell;
import org.zgram.ui.ChatActivity;
import org.zgram.ui.Components.EditTextBoldCursor;
import org.zgram.ui.Components.EmptyTextProgressView;
import org.zgram.ui.Components.GroupCreateDividerItemDecoration;
import org.zgram.ui.Components.GroupCreateSpan;
import org.zgram.ui.Components.LayoutHelper;
import org.zgram.ui.Components.RecyclerListView;
import org.zgram.ui.Components.RecyclerListView.FastScrollAdapter;
import org.zgram.ui.Components.RecyclerListView.Holder;
import org.zgram.ui.Components.RecyclerListView.OnItemClickListener;
import org.zgram.ui.GroupCreateFinalActivity;

public class DeletSelectedContacts extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, OnClickListener {
    private static final int check_all = 2;
    private static final int done_button = 1;
    private GroupCreateAdapter adapter;
    private ArrayList<GroupCreateSpan> allSpans = new ArrayList();
    private int chatId;
    private int chatType = 0;
    ArrayList<User> contacts = new ArrayList();
    private int containerHeight;
    private GroupCreateSpan currentDeletingSpan;
    private AnimatorSet currentDoneButtonAnimation;
    private GroupCreateActivityDelegate delegate;
    private View doneButton;
    private boolean doneButtonVisible;
    private EditTextBoldCursor editText;
    private EmptyTextProgressView emptyView;
    private int fieldY;
    private boolean ignoreScrollEvent;
    private boolean inviteMode = false;
    private boolean isAlwaysShare;
    private boolean isDelete = true;
    private boolean isGroup;
    private boolean isNeverShare;
    private GroupCreateDividerItemDecoration itemDecoration;
    private RecyclerListView listView;
    private int maxCount = MessagesController.getInstance().maxMegagroupCount;
    private ScrollView scrollView;
    private boolean searchWas;
    private boolean searching;
    private HashMap<Integer, GroupCreateSpan> selectedContacts = new HashMap();
    private SpansContainer spansContainer;

    class C07805 implements Callback {
        C07805() {
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public void onDestroyActionMode(ActionMode mode) {
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }
    }

    class C07816 implements OnEditorActionListener {
        C07816() {
        }

        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            return actionId == 6 && DeletSelectedContacts.this.onDonePressed();
        }
    }

    class C07827 implements OnKeyListener {
        C07827() {
        }

        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode != 67 || event.getAction() != 1 || DeletSelectedContacts.this.editText.length() != 0 || DeletSelectedContacts.this.allSpans.isEmpty()) {
                return false;
            }
            DeletSelectedContacts.this.spansContainer.removeSpan((GroupCreateSpan) DeletSelectedContacts.this.allSpans.get(DeletSelectedContacts.this.allSpans.size() - 1));
            DeletSelectedContacts.this.updateHint();
            DeletSelectedContacts.this.checkVisibleRows();
            return true;
        }
    }

    class C07838 implements TextWatcher {
        C07838() {
        }

        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void afterTextChanged(Editable editable) {
            if (DeletSelectedContacts.this.editText.length() != 0) {
                DeletSelectedContacts.this.searching = true;
                DeletSelectedContacts.this.searchWas = true;
                DeletSelectedContacts.this.adapter.setSearching(true);
                DeletSelectedContacts.this.itemDecoration.setSearching(true);
                DeletSelectedContacts.this.adapter.searchDialogs(DeletSelectedContacts.this.editText.getText().toString());
                DeletSelectedContacts.this.listView.setFastScrollVisible(false);
                DeletSelectedContacts.this.listView.setVerticalScrollBarEnabled(true);
                DeletSelectedContacts.this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                return;
            }
            DeletSelectedContacts.this.closeSearch();
        }
    }

    class C07849 implements OnItemClickListener {
        C07849() {
        }

        public void onItemClick(View view, int position) {
            boolean z = false;
            if (view instanceof GroupCreateUserCell) {
                GroupCreateUserCell cell = (GroupCreateUserCell) view;
                User user = cell.getUser();
                if (user != null) {
                    boolean exists = DeletSelectedContacts.this.selectedContacts.containsKey(Integer.valueOf(user.id));
                    if (exists) {
                        DeletSelectedContacts.this.spansContainer.removeSpan((GroupCreateSpan) DeletSelectedContacts.this.selectedContacts.get(Integer.valueOf(user.id)));
                    } else if (DeletSelectedContacts.this.maxCount != 0 && DeletSelectedContacts.this.selectedContacts.size() == DeletSelectedContacts.this.maxCount) {
                        return;
                    } else {
                        if (DeletSelectedContacts.this.chatType == 0 && DeletSelectedContacts.this.selectedContacts.size() == MessagesController.getInstance().maxGroupCount) {
                            Builder builder = new Builder(DeletSelectedContacts.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            builder.setMessage(LocaleController.getString("SoftUserLimitAlert", R.string.SoftUserLimitAlert));
                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                            DeletSelectedContacts.this.showDialog(builder.create());
                            return;
                        }
                        boolean z2;
                        MessagesController instance = MessagesController.getInstance();
                        if (DeletSelectedContacts.this.searching) {
                            z2 = false;
                        } else {
                            z2 = true;
                        }
                        instance.putUser(user, z2);
                        GroupCreateSpan span = new GroupCreateSpan(DeletSelectedContacts.this.editText.getContext(), user);
                        DeletSelectedContacts.this.spansContainer.addSpan(span);
                        span.setOnClickListener(DeletSelectedContacts.this);
                    }
                    DeletSelectedContacts.this.updateHint();
                    if (DeletSelectedContacts.this.searching || DeletSelectedContacts.this.searchWas) {
                        AndroidUtilities.showKeyboard(DeletSelectedContacts.this.editText);
                    } else {
                        if (!exists) {
                            z = true;
                        }
                        cell.setChecked(z, true);
                    }
                    if (DeletSelectedContacts.this.editText.length() > 0) {
                        DeletSelectedContacts.this.editText.setText(null);
                    }
                }
            }
        }
    }

    public interface GroupCreateActivityDelegate {
        void didSelectUsers(ArrayList<Integer> arrayList);
    }

    public class GroupCreateAdapter extends FastScrollAdapter {
        private Context context;
        private SearchAdapterHelper searchAdapterHelper;
        private ArrayList<User> searchResult = new ArrayList();
        private ArrayList<CharSequence> searchResultNames = new ArrayList();
        private Timer searchTimer;
        private boolean searching;

        public GroupCreateAdapter(Context ctx) {
            this.context = ctx;
            DeletSelectedContacts.this.contacts.clear();
            ArrayList<TL_contact> arrayList = ContactsController.getInstance().contacts;
            for (int a = 0; a < arrayList.size(); a++) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(((TL_contact) arrayList.get(a)).user_id));
                if (!(user == null || user.self || user.deleted)) {
                    DeletSelectedContacts.this.contacts.add(user);
                }
            }
            this.searchAdapterHelper = new SearchAdapterHelper();
            this.searchAdapterHelper.setDelegate(new SearchAdapterHelperDelegate() {
                public void onDataSetChanged() {
                    GroupCreateAdapter.this.notifyDataSetChanged();
                }

                public void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
                }
            });
        }

        public void setSearching(boolean value) {
            if (this.searching != value) {
                this.searching = value;
                notifyDataSetChanged();
            }
        }

        public String getLetter(int position) {
            if (position < 0 || position >= DeletSelectedContacts.this.contacts.size()) {
                return null;
            }
            User user = (User) DeletSelectedContacts.this.contacts.get(position);
            if (user == null) {
                return null;
            }
            if (LocaleController.nameDisplayOrder == 1) {
                if (!TextUtils.isEmpty(user.first_name)) {
                    return user.first_name.substring(0, 1).toUpperCase();
                }
                if (!TextUtils.isEmpty(user.last_name)) {
                    return user.last_name.substring(0, 1).toUpperCase();
                }
            } else if (!TextUtils.isEmpty(user.last_name)) {
                return user.last_name.substring(0, 1).toUpperCase();
            } else {
                if (!TextUtils.isEmpty(user.first_name)) {
                    return user.first_name.substring(0, 1).toUpperCase();
                }
            }
            return "";
        }

        public int getItemCount() {
            if (!this.searching) {
                return DeletSelectedContacts.this.contacts.size();
            }
            int count = this.searchResult.size();
            int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
            if (globalCount != 0) {
                return count + (globalCount + 1);
            }
            return count;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new GroupCreateSectionCell(this.context);
                    break;
                default:
                    view = new GroupCreateUserCell(this.context, true);
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            User user;
            switch (holder.getItemViewType()) {
                case 0:
                    GroupCreateSectionCell cell = (GroupCreateSectionCell) holder.itemView;
                    if (this.searching) {
                        cell.setText(LocaleController.getString("GlobalSearch", R.string.GlobalSearch));
                        return;
                    }
                    return;
                default:
                    GroupCreateUserCell cell2 = (GroupCreateUserCell) holder.itemView;
                    CharSequence username = null;
                    CharSequence name = null;
                    if (this.searching) {
                        int localCount = this.searchResult.size();
                        int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
                        if (position >= 0 && position < localCount) {
                            user = (User) this.searchResult.get(position);
                        } else if (position <= localCount || position > globalCount + localCount) {
                            user = null;
                        } else {
                            user = (User) this.searchAdapterHelper.getGlobalSearch().get((position - localCount) - 1);
                        }
                        if (user != null) {
                            if (position < localCount) {
                                name = (CharSequence) this.searchResultNames.get(position);
                                if (!(name == null || TextUtils.isEmpty(user.username) || !name.toString().startsWith("@" + user.username))) {
                                    username = name;
                                    name = null;
                                }
                            } else if (position > localCount && !TextUtils.isEmpty(user.username)) {
                                String foundUserName = this.searchAdapterHelper.getLastFoundUsername();
                                if (foundUserName.startsWith("@")) {
                                    foundUserName = foundUserName.substring(1);
                                }
                                try {
                                    CharSequence username2 = new SpannableStringBuilder(null);
                                    try {
                                        ((SpannableStringBuilder) username2).setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), 0, foundUserName.length(), 33);
                                        username = username2;
                                    } catch (Exception e) {
                                        username = username2;
                                        username = user.username;
                                        cell2.setUser(user, name, username);
                                        cell2.setChecked(DeletSelectedContacts.this.selectedContacts.containsKey(Integer.valueOf(user.id)), false);
                                        return;
                                    }
                                } catch (Exception e2) {
                                    username = user.username;
                                    cell2.setUser(user, name, username);
                                    cell2.setChecked(DeletSelectedContacts.this.selectedContacts.containsKey(Integer.valueOf(user.id)), false);
                                    return;
                                }
                            }
                        }
                    }
                    user = (User) DeletSelectedContacts.this.contacts.get(position);
                    cell2.setUser(user, name, username);
                    cell2.setChecked(DeletSelectedContacts.this.selectedContacts.containsKey(Integer.valueOf(user.id)), false);
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (this.searching && position == this.searchResult.size()) {
                return 0;
            }
            return 1;
        }

        public int getPositionForScrollProgress(float progress) {
            return (int) (((float) getItemCount()) * progress);
        }

        public void onViewRecycled(ViewHolder holder) {
            if (holder.itemView instanceof GroupCreateUserCell) {
                ((GroupCreateUserCell) holder.itemView).recycle();
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            return true;
        }

        public void searchDialogs(final String query) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
            if (query == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchAdapterHelper.queryServerSearch(null, true, false, false, false, 0, false);
                notifyDataSetChanged();
                return;
            }
            this.searchTimer = new Timer();
            this.searchTimer.schedule(new TimerTask() {

                class C07871 implements Runnable {

                    class C07861 implements Runnable {
                        C07861() {
                        }

                        public void run() {
                            String search1 = query.trim().toLowerCase();
                            if (search1.length() == 0) {
                                GroupCreateAdapter.this.updateSearchResults(new ArrayList(), new ArrayList());
                                return;
                            }
                            String search2 = LocaleController.getInstance().getTranslitString(search1);
                            if (search1.equals(search2) || search2.length() == 0) {
                                search2 = null;
                            }
                            String[] search = new String[((search2 != null ? 1 : 0) + 1)];
                            search[0] = search1;
                            if (search2 != null) {
                                search[1] = search2;
                            }
                            ArrayList<User> resultArray = new ArrayList();
                            ArrayList<CharSequence> resultArrayNames = new ArrayList();
                            for (int a = 0; a < DeletSelectedContacts.this.contacts.size(); a++) {
                                User user = (User) DeletSelectedContacts.this.contacts.get(a);
                                String name = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                                String tName = LocaleController.getInstance().getTranslitString(name);
                                if (name.equals(tName)) {
                                    tName = null;
                                }
                                int found = 0;
                                int length = search.length;
                                int i = 0;
                                while (i < length) {
                                    String q = search[i];
                                    if (name.startsWith(q) || name.contains(" " + q) || (tName != null && (tName.startsWith(q) || tName.contains(" " + q)))) {
                                        found = 1;
                                    } else if (user.username != null && user.username.startsWith(q)) {
                                        found = 2;
                                    }
                                    if (found != 0) {
                                        if (found == 1) {
                                            resultArrayNames.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, q));
                                        } else {
                                            resultArrayNames.add(AndroidUtilities.generateSearchName("@" + user.username, null, "@" + q));
                                        }
                                        resultArray.add(user);
                                    } else {
                                        i++;
                                    }
                                }
                            }
                            GroupCreateAdapter.this.updateSearchResults(resultArray, resultArrayNames);
                        }
                    }

                    C07871() {
                    }

                    public void run() {
                        GroupCreateAdapter.this.searchAdapterHelper.queryServerSearch(query, true, false, false, false, 0, false);
                        Utilities.searchQueue.postRunnable(new C07861());
                    }
                }

                public void run() {
                    try {
                        GroupCreateAdapter.this.searchTimer.cancel();
                        GroupCreateAdapter.this.searchTimer = null;
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                    AndroidUtilities.runOnUIThread(new C07871());
                }
            }, 200, 300);
        }

        private void updateSearchResults(final ArrayList<User> users, final ArrayList<CharSequence> names) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    GroupCreateAdapter.this.searchResult = users;
                    GroupCreateAdapter.this.searchResultNames = names;
                    GroupCreateAdapter.this.notifyDataSetChanged();
                }
            });
        }
    }

    private class SpansContainer extends ViewGroup {
        private View addingSpan;
        private boolean animationStarted;
        private ArrayList<Animator> animators = new ArrayList();
        private AnimatorSet currentAnimation;
        private View removingSpan;

        class C07901 extends AnimatorListenerAdapter {
            C07901() {
            }

            public void onAnimationEnd(Animator animator) {
                SpansContainer.this.addingSpan = null;
                SpansContainer.this.currentAnimation = null;
                SpansContainer.this.animationStarted = false;
                DeletSelectedContacts.this.editText.setAllowDrawCursor(true);
            }
        }

        public SpansContainer(Context context) {
            super(context);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int minWidth;
            int count = getChildCount();
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int maxWidth = width - AndroidUtilities.dp(32.0f);
            int currentLineWidth = 0;
            int y = AndroidUtilities.dp(12.0f);
            int allCurrentLineWidth = 0;
            int allY = AndroidUtilities.dp(12.0f);
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                if (child instanceof GroupCreateSpan) {
                    child.measure(MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), 1073741824));
                    if (child != this.removingSpan && child.getMeasuredWidth() + currentLineWidth > maxWidth) {
                        y += child.getMeasuredHeight() + AndroidUtilities.dp(12.0f);
                        currentLineWidth = 0;
                    }
                    if (child.getMeasuredWidth() + allCurrentLineWidth > maxWidth) {
                        allY += child.getMeasuredHeight() + AndroidUtilities.dp(12.0f);
                        allCurrentLineWidth = 0;
                    }
                    int x = AndroidUtilities.dp(16.0f) + currentLineWidth;
                    if (!this.animationStarted) {
                        if (child == this.removingSpan) {
                            child.setTranslationX((float) (AndroidUtilities.dp(16.0f) + allCurrentLineWidth));
                            child.setTranslationY((float) allY);
                        } else if (this.removingSpan != null) {
                            if (child.getTranslationX() != ((float) x)) {
                                this.animators.add(ObjectAnimator.ofFloat(child, "translationX", new float[]{(float) x}));
                            }
                            if (child.getTranslationY() != ((float) y)) {
                                this.animators.add(ObjectAnimator.ofFloat(child, "translationY", new float[]{(float) y}));
                            }
                        } else {
                            child.setTranslationX((float) x);
                            child.setTranslationY((float) y);
                        }
                    }
                    if (child != this.removingSpan) {
                        currentLineWidth += child.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                    }
                    allCurrentLineWidth += child.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                }
            }
            if (AndroidUtilities.isTablet()) {
                minWidth = AndroidUtilities.dp(366.0f) / 3;
            } else {
                minWidth = (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(164.0f)) / 3;
            }
            if (maxWidth - currentLineWidth < minWidth) {
                currentLineWidth = 0;
                y += AndroidUtilities.dp(44.0f);
            }
            if (maxWidth - allCurrentLineWidth < minWidth) {
                allY += AndroidUtilities.dp(44.0f);
            }
            DeletSelectedContacts.this.editText.measure(MeasureSpec.makeMeasureSpec(maxWidth - currentLineWidth, 1073741824), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), 1073741824));
            if (!this.animationStarted) {
                int currentHeight = allY + AndroidUtilities.dp(44.0f);
                int fieldX = currentLineWidth + AndroidUtilities.dp(16.0f);
                DeletSelectedContacts.this.fieldY = y;
                if (this.currentAnimation != null) {
                    if (DeletSelectedContacts.this.containerHeight != y + AndroidUtilities.dp(44.0f)) {
                        this.animators.add(ObjectAnimator.ofInt(DeletSelectedContacts.this, "containerHeight", new int[]{0}));
                    }
                    if (DeletSelectedContacts.this.editText.getTranslationX() != ((float) fieldX)) {
                        this.animators.add(ObjectAnimator.ofFloat(DeletSelectedContacts.this.editText, "translationX", new float[]{(float) fieldX}));
                    }
                    if (DeletSelectedContacts.this.editText.getTranslationY() != ((float) DeletSelectedContacts.this.fieldY)) {
                        this.animators.add(ObjectAnimator.ofFloat(DeletSelectedContacts.this.editText, "translationY", new float[]{(float) DeletSelectedContacts.this.fieldY}));
                    }
                    DeletSelectedContacts.this.editText.setAllowDrawCursor(false);
                    this.currentAnimation.playTogether(this.animators);
                    this.currentAnimation.start();
                    this.animationStarted = true;
                } else {
                    DeletSelectedContacts.this.containerHeight = currentHeight;
                    DeletSelectedContacts.this.editText.setTranslationX((float) fieldX);
                    DeletSelectedContacts.this.editText.setTranslationY((float) DeletSelectedContacts.this.fieldY);
                }
            } else if (!(this.currentAnimation == null || DeletSelectedContacts.this.ignoreScrollEvent || this.removingSpan != null)) {
                DeletSelectedContacts.this.editText.bringPointIntoView(DeletSelectedContacts.this.editText.getSelectionStart());
            }
            setMeasuredDimension(width, DeletSelectedContacts.this.containerHeight);
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int count = getChildCount();
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
            }
        }

        public void addSpan(GroupCreateSpan span) {
            DeletSelectedContacts.this.allSpans.add(span);
            DeletSelectedContacts.this.selectedContacts.put(Integer.valueOf(span.getUid()), span);
            DeletSelectedContacts.this.editText.setHintVisible(false);
            if (this.currentAnimation != null) {
                this.currentAnimation.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            this.currentAnimation = new AnimatorSet();
            this.currentAnimation.addListener(new C07901());
            this.currentAnimation.setDuration(150);
            this.addingSpan = span;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleX", new float[]{0.01f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleY", new float[]{0.01f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
            addView(span);
        }

        public void removeSpan(final GroupCreateSpan span) {
            DeletSelectedContacts.this.ignoreScrollEvent = true;
            if (span != null) {
                DeletSelectedContacts.this.selectedContacts.remove(Integer.valueOf(span.getUid()));
                DeletSelectedContacts.this.allSpans.remove(span);
                span.setOnClickListener(null);
                if (this.currentAnimation != null) {
                    this.currentAnimation.setupEndValues();
                    this.currentAnimation.cancel();
                }
                this.animationStarted = false;
                this.currentAnimation = new AnimatorSet();
                this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        SpansContainer.this.removeView(span);
                        SpansContainer.this.removingSpan = null;
                        SpansContainer.this.currentAnimation = null;
                        SpansContainer.this.animationStarted = false;
                        DeletSelectedContacts.this.editText.setAllowDrawCursor(true);
                        if (DeletSelectedContacts.this.allSpans.isEmpty()) {
                            DeletSelectedContacts.this.editText.setHintVisible(true);
                        }
                    }
                });
                this.currentAnimation.setDuration(150);
                this.removingSpan = span;
                this.animators.clear();
                this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleX", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.01f}));
                this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleY", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.01f}));
                this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f}));
                requestLayout();
            }
        }
    }

    public DeletSelectedContacts(Bundle args) {
        super(args);
        this.chatType = args.getInt("chatType", 0);
        this.isAlwaysShare = args.getBoolean("isAlwaysShare", false);
        this.isNeverShare = args.getBoolean("isNeverShare", false);
        this.isGroup = args.getBoolean("isGroup", false);
        this.inviteMode = args.getBoolean("InviteMode", false);
        this.chatId = args.getInt("chatId");
        this.maxCount = this.chatType == 0 ? MessagesController.getInstance().maxMegagroupCount : MessagesController.getInstance().maxBroadcastCount;
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatDidCreated);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatDidCreated);
    }

    public void onClick(View v) {
        GroupCreateSpan span = (GroupCreateSpan) v;
        if (span.isDeleting()) {
            this.currentDeletingSpan = null;
            this.spansContainer.removeSpan(span);
            updateHint();
            checkVisibleRows();
            return;
        }
        if (this.currentDeletingSpan != null) {
            this.currentDeletingSpan.cancelDeleteAnimation();
        }
        this.currentDeletingSpan = span;
        span.startDeleteAnimation();
    }

    public View createView(final Context context) {
        boolean z;
        int i = 1;
        this.searching = false;
        this.searchWas = false;
        this.allSpans.clear();
        this.selectedContacts.clear();
        this.currentDeletingSpan = null;
        if (this.chatType == 2) {
            z = true;
        } else {
            z = false;
        }
        this.doneButtonVisible = z;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.inviteMode) {
            this.actionBar.setTitle(LocaleController.getString("InviteFriends", R.string.InviteFriends));
        } else {
            this.actionBar.setTitle(LocaleController.getString("DeleteContacts", R.string.DeleteContacts));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {

            class C07741 implements DialogInterface.OnClickListener {
                C07741() {
                }

                public void onClick(DialogInterface dialog, int which) {
                    DeletSelectedContacts.this.selectAll();
                }
            }

            class C07752 implements DialogInterface.OnClickListener {
                C07752() {
                }

                public void onClick(DialogInterface dialog, int which) {
                }
            }

            public void onItemClick(int id) {
                if (id == -1) {
                    DeletSelectedContacts.this.finishFragment();
                } else if (id == 1) {
                    if (!DeletSelectedContacts.this.selectedContacts.isEmpty()) {
                        ArrayList arrayList = new ArrayList();
                        arrayList.addAll(DeletSelectedContacts.this.selectedContacts.keySet());
                        int i;
                        User user;
                        if (DeletSelectedContacts.this.inviteMode) {
                            if (arrayList.size() < 5) {
                                Toast.makeText(context, context.getResources().getString(R.string.minis5), 1).show();
                                return;
                            }
                            for (i = 0; i < arrayList.size(); i++) {
                                user = MessagesController.getInstance().getUser((Integer) arrayList.get(i));
                                if (user != null || DeletSelectedContacts.this.getParentActivity() != null) {
                                    //PmHelper.send((long) user.id);
                            Toast.makeText(getParentActivity(),"ok shod",Toast.LENGTH_SHORT).show();

                                }
                            }
                            ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putBoolean("havePowerAccess", true).commit();



                            if (DeletSelectedContacts.this.delegate != null) {
                                DeletSelectedContacts.this.delegate.didSelectUsers(arrayList);
                            }
                            DeletSelectedContacts.this.finishFragment();
                        } else if (DeletSelectedContacts.this.isDelete) {
                            for (i = 0; i < arrayList.size(); i++) {
                                user = MessagesController.getInstance().getUser((Integer) arrayList.get(i));
                                if (user != null || DeletSelectedContacts.this.getParentActivity() != null) {
                                    ArrayList<User> arrayList2 = new ArrayList();
                                    arrayList2.add(user);
                                    ContactsController.getInstance().deleteContact(arrayList2);
                                }
                            }
                            if (DeletSelectedContacts.this.delegate != null) {
                                DeletSelectedContacts.this.delegate.didSelectUsers(arrayList);
                            }
                            DeletSelectedContacts.this.finishFragment();
                        }
                    }
                } else if (id == 2) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.selectAll);
                    builder.setMessage(R.string.selectAllContactsMessage);
                    builder.setPositiveButton(R.string.OK, new C07741());
                    builder.setNegativeButton(R.string.Cancel, new C07752());
                    builder.show();
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        if (!this.inviteMode) {
            menu.addItemWithWidth(2, R.drawable.ic_check_all, AndroidUtilities.dp(56.0f));
        }
        this.doneButton = menu.addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        if (this.chatType != 2) {
            this.doneButton.setScaleX(0.0f);
            this.doneButton.setScaleY(0.0f);
            this.doneButton.setAlpha(0.0f);
        }
        this.fragmentView = new ViewGroup(context) {
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int maxSize;
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(width, height);
                if (AndroidUtilities.isTablet() || height > width) {
                    maxSize = AndroidUtilities.dp(144.0f);
                } else {
                    maxSize = AndroidUtilities.dp(56.0f);
                }
                DeletSelectedContacts.this.scrollView.measure(MeasureSpec.makeMeasureSpec(width, 1073741824), MeasureSpec.makeMeasureSpec(maxSize, Integer.MIN_VALUE));
                DeletSelectedContacts.this.listView.measure(MeasureSpec.makeMeasureSpec(width, 1073741824), MeasureSpec.makeMeasureSpec(height - DeletSelectedContacts.this.scrollView.getMeasuredHeight(), 1073741824));
                DeletSelectedContacts.this.emptyView.measure(MeasureSpec.makeMeasureSpec(width, 1073741824), MeasureSpec.makeMeasureSpec(height - DeletSelectedContacts.this.scrollView.getMeasuredHeight(), 1073741824));
            }

            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                DeletSelectedContacts.this.scrollView.layout(0, 0, DeletSelectedContacts.this.scrollView.getMeasuredWidth(), DeletSelectedContacts.this.scrollView.getMeasuredHeight());
                DeletSelectedContacts.this.listView.layout(0, DeletSelectedContacts.this.scrollView.getMeasuredHeight(), DeletSelectedContacts.this.listView.getMeasuredWidth(), DeletSelectedContacts.this.scrollView.getMeasuredHeight() + DeletSelectedContacts.this.listView.getMeasuredHeight());
                DeletSelectedContacts.this.emptyView.layout(0, DeletSelectedContacts.this.scrollView.getMeasuredHeight(), DeletSelectedContacts.this.emptyView.getMeasuredWidth(), DeletSelectedContacts.this.scrollView.getMeasuredHeight() + DeletSelectedContacts.this.emptyView.getMeasuredHeight());
            }

            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (child == DeletSelectedContacts.this.listView || child == DeletSelectedContacts.this.emptyView) {
                    DeletSelectedContacts.this.parentLayout.drawHeaderShadow(canvas, DeletSelectedContacts.this.scrollView.getMeasuredHeight());
                }
                return result;
            }
        };
        ViewGroup frameLayout = (ViewGroup) this.fragmentView;
        this.scrollView = new ScrollView(context) {
            public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                if (DeletSelectedContacts.this.ignoreScrollEvent) {
                    DeletSelectedContacts.this.ignoreScrollEvent = false;
                    return false;
                }
                rectangle.offset(child.getLeft() - child.getScrollX(), child.getTop() - child.getScrollY());
                rectangle.top += DeletSelectedContacts.this.fieldY + AndroidUtilities.dp(20.0f);
                rectangle.bottom += DeletSelectedContacts.this.fieldY + AndroidUtilities.dp(50.0f);
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }
        };
        this.scrollView.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_windowBackgroundWhite));
        frameLayout.addView(this.scrollView);
        this.spansContainer = new SpansContainer(context);
        this.scrollView.addView(this.spansContainer, LayoutHelper.createFrame(-1, -2.0f));
        this.editText = new EditTextBoldCursor(context) {
            public boolean onTouchEvent(MotionEvent event) {
                if (DeletSelectedContacts.this.currentDeletingSpan != null) {
                    DeletSelectedContacts.this.currentDeletingSpan.cancelDeleteAnimation();
                    DeletSelectedContacts.this.currentDeletingSpan = null;
                }
                return super.onTouchEvent(event);
            }
        };
        this.editText.setTextSize(1, 18.0f);
        this.editText.setHintColor(Theme.getColor(Theme.key_groupcreate_hintText));
        this.editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.editText.setCursorColor(Theme.getColor(Theme.key_groupcreate_cursor));
        this.editText.setInputType(655536);
        this.editText.setSingleLine(true);
        this.editText.setBackgroundDrawable(null);
        this.editText.setVerticalScrollBarEnabled(false);
        this.editText.setHorizontalScrollBarEnabled(false);
        this.editText.setTextIsSelectable(false);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setImeOptions(268435462);
        this.editText.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.spansContainer.addView(this.editText);
        if (!this.inviteMode) {
            this.editText.setHint(LocaleController.getString("DeleteContactPlaceholder", R.string.DeleteContactPlaceholder));
        }
        this.editText.setCustomSelectionActionModeCallback(new C07805());
        this.editText.setOnEditorActionListener(new C07816());
        this.editText.setOnKeyListener(new C07827());
        this.editText.addTextChangedListener(new C07838());
        this.emptyView = new EmptyTextProgressView(context);
        if (ContactsController.getInstance().isLoadingContacts()) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        this.emptyView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
        frameLayout.addView(this.emptyView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.listView = new RecyclerListView(context);
        this.listView.setFastScrollEnabled();
        this.listView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView = this.listView;
        Adapter groupCreateAdapter = new GroupCreateAdapter(context);
        this.adapter = (GroupCreateAdapter) groupCreateAdapter;
        recyclerListView.setAdapter(groupCreateAdapter);
        this.listView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        recyclerListView = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView.setVerticalScrollbarPosition(i);
        recyclerListView = this.listView;
        ItemDecoration groupCreateDividerItemDecoration = new GroupCreateDividerItemDecoration();
        this.itemDecoration = (GroupCreateDividerItemDecoration) groupCreateDividerItemDecoration;
        recyclerListView.addItemDecoration(groupCreateDividerItemDecoration);
        frameLayout.addView(this.listView);
        this.listView.setOnItemClickListener(new C07849());
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(DeletSelectedContacts.this.editText);
                }
            }
        });
        updateHint();
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.editText != null) {
            this.editText.requestFocus();
        }
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.contactsDidLoaded) {
            if (this.emptyView != null) {
                this.emptyView.showTextView();
            }
            if (this.adapter != null) {
                this.adapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.updateInterfaces) {
            if (this.listView != null) {
                int mask = ((Integer) args[0]).intValue();
                int count = this.listView.getChildCount();
                if ((mask & 2) != 0 || (mask & 1) != 0 || (mask & 4) != 0) {
                    for (int a = 0; a < count; a++) {
                        View child = this.listView.getChildAt(a);
                        if (child instanceof GroupCreateUserCell) {
                            ((GroupCreateUserCell) child).update(mask);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.chatDidCreated) {
            removeSelfFromStack();
        }
    }

    public void setContainerHeight(int value) {
        this.containerHeight = value;
        if (this.spansContainer != null) {
            this.spansContainer.requestLayout();
        }
    }

    public int getContainerHeight() {
        return this.containerHeight;
    }

    private void checkVisibleRows() {
        int count = this.listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = this.listView.getChildAt(a);
            if (child instanceof GroupCreateUserCell) {
                GroupCreateUserCell cell = (GroupCreateUserCell) child;
                User user = cell.getUser();
                if (user != null) {
                    cell.setChecked(this.selectedContacts.containsKey(Integer.valueOf(user.id)), true);
                }
            }
        }
    }

    private boolean onDonePressed() {
        if (this.chatType == 2) {
            ArrayList<InputUser> result = new ArrayList();
            for (Integer uid : this.selectedContacts.keySet()) {
                InputUser user = MessagesController.getInputUser(MessagesController.getInstance().getUser(uid));
                if (user != null) {
                    result.add(user);
                }
            }
            MessagesController.getInstance().addUsersToChannel(this.chatId, result, null);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
            Bundle args2 = new Bundle();
            args2.putInt("chat_id", this.chatId);
            presentFragment(new ChatActivity(args2), true);
        } else if (!this.doneButtonVisible || this.selectedContacts.isEmpty()) {
            return false;
        } else {
            ArrayList<Integer> result2 = new ArrayList();
            result2.addAll(this.selectedContacts.keySet());
            if (this.isAlwaysShare || this.isNeverShare) {
                if (this.delegate != null) {
                    this.delegate.didSelectUsers(result2);
                }
                finishFragment();
            } else {
                Bundle args = new Bundle();
                args.putIntegerArrayList("result", result2);
                args.putInt("chatType", this.chatType);
                presentFragment(new GroupCreateFinalActivity(args));
            }
        }
        return true;
    }

    private void closeSearch() {
        this.searching = false;
        this.searchWas = false;
        this.itemDecoration.setSearching(false);
        this.adapter.setSearching(false);
        this.adapter.searchDialogs(null);
        this.listView.setFastScrollVisible(true);
        this.listView.setVerticalScrollBarEnabled(false);
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
    }

    private void updateHint() {
        if (this.chatType == 2) {
            return;
        }
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (this.doneButtonVisible && this.allSpans.isEmpty()) {
            if (this.currentDoneButtonAnimation != null) {
                this.currentDoneButtonAnimation.cancel();
            }
            this.currentDoneButtonAnimation = new AnimatorSet();
            animatorSet = this.currentDoneButtonAnimation;
            animatorArr = new Animator[3];
            animatorArr[0] = ObjectAnimator.ofFloat(this.doneButton, "scaleX", new float[]{0.0f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.doneButton, "scaleY", new float[]{0.0f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.doneButton, "alpha", new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            this.currentDoneButtonAnimation.setDuration(180);
            this.currentDoneButtonAnimation.start();
            this.doneButtonVisible = false;
        } else if (!this.doneButtonVisible && !this.allSpans.isEmpty()) {
            if (this.currentDoneButtonAnimation != null) {
                this.currentDoneButtonAnimation.cancel();
            }
            this.currentDoneButtonAnimation = new AnimatorSet();
            animatorSet = this.currentDoneButtonAnimation;
            animatorArr = new Animator[3];
            animatorArr[0] = ObjectAnimator.ofFloat(this.doneButton, "scaleX", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
            animatorArr[1] = ObjectAnimator.ofFloat(this.doneButton, "scaleY", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
            animatorArr[2] = ObjectAnimator.ofFloat(this.doneButton, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
            animatorSet.playTogether(animatorArr);
            this.currentDoneButtonAnimation.setDuration(180);
            this.currentDoneButtonAnimation.start();
            this.doneButtonVisible = true;
        }
    }

    public void setDelegate(GroupCreateActivityDelegate groupCreateActivityDelegate) {
        this.delegate = groupCreateActivityDelegate;
    }



    private void selectAll() {
        final ProgressDialog progressDialog = new ProgressDialog(getParentActivity());
        progressDialog.setMessage(LocaleController.getString("PleaseWait", R.string.PleaseWait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                DeletSelectedContacts.this.selectedContacts.clear();
                for (int i = 0; i < DeletSelectedContacts.this.contacts.size(); i++) {
                    User user = (User) DeletSelectedContacts.this.contacts.get(i);
                    if (user != null) {
                        DeletSelectedContacts.this.selectedContacts.put(Integer.valueOf(user.id), null);
                    }
                }
                if (DeletSelectedContacts.this.adapter != null) {
                    DeletSelectedContacts.this.adapter.notifyDataSetChanged();
                }
                progressDialog.dismiss();
            }
        }, 500);
    }
}
