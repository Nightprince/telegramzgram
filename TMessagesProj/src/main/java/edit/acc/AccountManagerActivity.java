package edit.acc;

import android.app.AlarmManager;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import org.zgram.messenger.R;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.zgram.messenger.AndroidUtilities;
import org.zgram.messenger.ApplicationLoader;
import org.zgram.messenger.ContactsController;
import org.zgram.messenger.FileLoader;
import org.zgram.messenger.ImageLoader;
import org.zgram.messenger.LocaleController;
import org.zgram.messenger.MessagesController;
import org.zgram.messenger.MessagesStorage;
import org.zgram.messenger.UserConfig;
import org.zgram.messenger.Utilities;
import org.zgram.messenger.support.widget.LinearLayoutManager;
import org.zgram.messenger.support.widget.RecyclerView.LayoutParams;
import org.zgram.messenger.support.widget.RecyclerView.ViewHolder;
import org.zgram.tgnet.ConnectionsManager;
import org.zgram.tgnet.RequestDelegate;
import org.zgram.tgnet.TLObject;
import org.zgram.tgnet.TLRPC;
import org.zgram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.zgram.ui.ActionBar.BaseFragment;
import org.zgram.ui.Components.LayoutHelper;
import org.zgram.ui.Components.RecyclerListView;
import org.zgram.ui.Components.RecyclerListView.Holder;
import org.zgram.ui.Components.RecyclerListView.OnItemClickListener;
import org.zgram.ui.Components.RecyclerListView.SelectionAdapter;
import org.zgram.ui.LaunchActivity;

public class AccountManagerActivity extends BaseFragment {
    private ArrayList<AccountsController.AppAccount> accounts;
    private AccountsAdapter listAdapter = null;
    private RecyclerListView listView;

    class C37021 extends ActionBarMenuOnItemClick {

        class C37001 implements OnClickListener {
            C37001() {
            }

            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("tellgram", 0);
                int lastAddedAccountId = preferences.getInt("accounts_last_id", -1);
                preferences.edit().putInt("active_account", lastAddedAccountId + 1).commit();
                AccountsController.getInstance().listAppAccounts.put(Integer.valueOf(lastAddedAccountId + 1), new AccountsController.AppAccount(lastAddedAccountId + 1));
                AccountsController.getInstance().saveAppAccounts();
                ApplicationLoader.applicationContext.getSharedPreferences("tellgram", 0).edit().putBoolean("load_old_config", false).commit();
                AccountManagerActivity.this.resetApp();
            }
        }

        class C37012 implements OnClickListener {
            C37012() {
            }

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }

        C37021() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                AccountManagerActivity.this.finishFragment();
            } else if (id == 0) {
                Builder builder = new Builder(AccountManagerActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("UserAdd", R.string.UserAdd));
                builder.setMessage(LocaleController.getString("UserAddAlert", R.string.UserAddAlert));
                builder.setPositiveButton(LocaleController.getString("Add", R.string.Add), new C37001());
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new C37012());
                AccountManagerActivity.this.showDialog(builder.create());
            }
        }
    }

    class C37032 implements Comparator<AccountsController.AppAccount> {
        C37032() {
        }

        public int compare(AccountsController.AppAccount account1, AccountsController.AppAccount account2) {
            if (account1.id > account2.id) {
                return 1;
            }
            if (account1.id < account2.id) {
                return -1;
            }
            return 0;
        }
    }

    class C37063 implements OnItemClickListener {

        class C37052 implements OnClickListener {
            C37052() {
            }

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }

        C37063() {
        }

        public void onItemClick(View view, final int position) {
            if (view.isEnabled() && ((AccountsController.AppAccount) AccountManagerActivity.this.accounts.get(position)).id != AccountsController.getInstance().activeAccountId) {
                Builder builder = new Builder(AccountManagerActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("UserChange", R.string.UserChange));
                builder.setMessage(LocaleController.getString("UserChangeAlert", R.string.UserChangeAlert));
                builder.setPositiveButton(LocaleController.getString("Change", R.string.Change), new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AccountsController.AppAccount account = (AccountsController.AppAccount) AccountManagerActivity.this.accounts.get(position);
                        if (Turbo.getOldUser() == null || account.id != 0) {
                            ApplicationLoader.applicationContext.getSharedPreferences("tellgram", 0).edit().putBoolean("load_old_config", false).commit();
                        } else {
                            ApplicationLoader.applicationContext.getSharedPreferences("tellgram", 0).edit().putBoolean("load_old_config", true).commit();
                        }
                        ApplicationLoader.applicationContext.getSharedPreferences("tellgram", 0).edit().putInt("active_account", account.id).commit();
                        AccountManagerActivity.this.resetApp();
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new C37052());
                AccountManagerActivity.this.showDialog(builder.create());
            }
        }
    }

    class C37074 implements RequestDelegate {
        C37074() {
        }

        public void run(TLObject response, TLRPC.TL_error error) {
            ConnectionsManager.getInstance().cleanup();
        }
    }

    private class AccountsAdapter extends SelectionAdapter {

        class C37121 implements View.OnClickListener {
            C37121() {
            }

            public void onClick(View v) {
                final AccountsController.AppAccount account = ((AccountCell) v.getParent()).getAccount();
                Builder builder = new Builder(AccountManagerActivity.this.getParentActivity());
                ArrayList<CharSequence> items = new ArrayList();
                final ArrayList<Integer> options = new ArrayList();
                if (account.id != AccountsController.getInstance().activeAccountId) {
                    Toast.makeText(AccountManagerActivity.this.getParentActivity(), LocaleController.getString("UserNotActive", R.string.UserNotActive), Toast.LENGTH_LONG).show();
                }
                items.add(LocaleController.getString("AppAccountName", R.string.AppAccountName));
                options.add(Integer.valueOf(0));
                items.add(account.publicFolder ? LocaleController.getString("UserFolderPrivate", R.string.UserFolderPrivate) : LocaleController.getString("UserFolderPublic", R.string.UserFolderPublic));
                options.add(Integer.valueOf(1));
                builder.setItems((CharSequence[]) items.toArray(new CharSequence[items.size()]), new OnClickListener() {

                    class C37092 implements OnClickListener {
                        C37092() {
                        }

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }

                    public void onClick(DialogInterface dialog, int i) {
                        boolean z = true;
                        if (i >= 0 && !options.isEmpty()) {
                            if (((Integer) options.get(i)).intValue() == 0) {
                                LinearLayout layout = new LinearLayout(AccountManagerActivity.this.getParentActivity());
                                layout.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f));
                                final EditText tv = new EditText(AccountManagerActivity.this.getParentActivity());
                                tv.setText(account.name != null ? account.name : "");
                                layout.addView(tv, LayoutHelper.createLinear(-1, -2, 1));
                                Builder builder = new Builder(AccountManagerActivity.this.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppAccountName", R.string.AppAccountName));
                                builder.setView(layout);
                                builder.setPositiveButton(LocaleController.getString("Change", R.string.Change), new OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (tv.getText() != null) {
                                            account.name = tv.getText().toString();
                                            if (account.name.isEmpty()) {
                                                account.name = null;
                                            }
                                            AccountsController.AppAccount globalAccount = (AccountsController.AppAccount) AccountsController.getInstance().listAppAccounts.get(Integer.valueOf(account.id));
                                            if (globalAccount != null) {
                                                globalAccount.name = account.name;
                                                AccountsController.getInstance().saveAppAccounts();
                                            }
                                            AccountManagerActivity.this.listAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new C37092());
                                AccountManagerActivity.this.showDialog(builder.create());
                            } else if (((Integer) options.get(i)).intValue() == 1) {
                                AccountsController.AppAccount appAccount2 = account;
                                if (account.publicFolder) {
                                    z = false;
                                }
                                appAccount2.publicFolder = z;
                                AccountsController.AppAccount globalAccount = (AccountsController.AppAccount) AccountsController.getInstance().listAppAccounts.get(Integer.valueOf(account.id));
                                if (globalAccount != null) {
                                    globalAccount.publicFolder = account.publicFolder;
                                    AccountsController.getInstance().saveAppAccounts();
                                }
                                AccountManagerActivity.this.listAdapter.notifyDataSetChanged();
                                if (account.id == AccountsController.getInstance().activeAccountId) {
                                    final HashMap<Integer, File> paths = ImageLoader.getInstance().createMediaPaths();
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            FileLoader.getInstance().setMediaDirs(paths);
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
                AccountManagerActivity.this.showDialog(builder.create());
            }
        }

        class C37162 implements View.OnClickListener {

            class C37141 implements OnClickListener {
                C37141() {
                }

                public void onClick(DialogInterface dialog, int which) {
                    final ProgressDialog progressDialog = new ProgressDialog(AccountManagerActivity.this.getParentActivity());
                    progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    Utilities.globalQueue.postRunnable(new Runnable() {
                        public void run() {
                            AccountManagerActivity.this.performLogout();
                            progressDialog.dismiss();
                        }
                    });
                }
            }

            class C37152 implements OnClickListener {
                C37152() {
                }

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }

            C37162() {
            }

            public void onClick(View v) {
                AccountsController.AppAccount account = ((AccountCell) v.getParent()).getAccount();
                Builder builder = new Builder(AccountManagerActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("UserRemove", R.string.UserRemove));
                builder.setMessage(LocaleController.formatString("UserRemoveAlert", R.string.UserRemoveAlert, new Object[]{account.number}) + "\n" + LocaleController.getString("UserRestartAlert", R.string.UserRestartAlert));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C37141());
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new C37152());
                AccountManagerActivity.this.showDialog(builder.create());
            }
        }

        private AccountsAdapter() {
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            AccountCell accountCell = new AccountCell(AccountManagerActivity.this.getParentActivity());
            accountCell.setOnSettingsClick(new C37121());
            accountCell.setOnRemoveClick(new C37162());
            accountCell.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(accountCell);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            AccountsController.AppAccount account = (AccountsController.AppAccount) AccountManagerActivity.this.accounts.get(position);
            ((AccountCell) holder.itemView).setAccount(position, account, position != AccountManagerActivity.this.accounts.size() + -1);
            if (AccountsController.getInstance().activeAccountId == account.id) {
                ((AccountCell) holder.itemView).setIsActive(true);
            } else {
                ((AccountCell) holder.itemView).setIsActive(false);
            }
        }

        public int getItemCount() {
            return AccountManagerActivity.this.accounts.size();
        }

        public boolean isEnabled(ViewHolder holder) {
            return true;
        }
    }

    public View createView(Context context) {
        int i = 1;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("AppAccountManage", R.string.AppAccountManage));
        this.actionBar.createMenu().addItem(0, (int) R.drawable.add);
        this.actionBar.setActionBarMenuOnItemClick(new C37021());
        this.accounts = new ArrayList();
        for (Integer intValue : AccountsController.getInstance().listAppAccounts.keySet()) {
            this.accounts.add(AccountsController.getInstance().listAppAccounts.get(Integer.valueOf(intValue.intValue())));
        }
        Collections.sort(this.accounts, new C37032());
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.listAdapter = new AccountsAdapter();
        this.listView = new RecyclerListView(context);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        RecyclerListView recyclerListView = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView.setVerticalScrollbarPosition(i);
        this.listView.setAdapter(this.listAdapter);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setOnItemClickListener(new C37063());
        return this.fragmentView;
    }

    private void resetApp() {
        ((AlarmManager) ApplicationLoader.applicationContext.getSystemService("alarm")).set(1, System.currentTimeMillis() + 1000, PendingIntent.getActivity(ApplicationLoader.applicationContext, 123456, new Intent(ApplicationLoader.applicationContext, LaunchActivity.class), 268435456));
        System.exit(0);
    }

    public void performLogout() {
        AndroidUtilities.getUserPrefs("Notifications", 0).edit().clear().commit();
        ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putLong("lastGifLoadTime", 0).putLong("lastStickersLoadTime", 0).commit();
        ApplicationLoader.applicationContext.getSharedPreferences(PreferenceManager.prefsName, 0).edit().remove("gifhint").commit();
        MessagesController.getInstance().unregistedPush();
        ConnectionsManager.getInstance().sendRequest(new TLRPC.TL_auth_logOut(), new C37074());
        UserConfig.clearConfig();
        AccountsController.deleteAppAccount(AccountsController.getInstance().activeAccountId);
        MessagesStorage.getInstance().cleanup(false);
        MessagesController.getInstance().cleanup();
        ContactsController.getInstance().deleteAllAppAccounts();
        Turbo.resetApp();
    }
}
