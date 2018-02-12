package edit.acc;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.mp4parser.iso14496.part15.SyncSampleEntry;
import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.zgram.messenger.AndroidUtilities;
import org.zgram.messenger.ApplicationLoader;
import org.zgram.messenger.FileLog;
import org.zgram.messenger.exoplayer2.text.ttml.TtmlNode;

public class AccountsController {
    private static AccountsController instance;
    public AppAccount activeAccount;
    public int activeAccountId = -1;
    public Hashtable<Integer, AppAccount> listAppAccounts = new Hashtable();

    public static AccountsController getInstance() {
        if (instance == null) {
            instance = new AccountsController();
            instance.reloadAppAccounts();
        }
        return instance;
    }


    public static class AppAccount {
        public boolean autoSync;
        public int id;
        public String name;
        public String number;
        public boolean publicFolder;
        public String robotName;

        public AppAccount(int id) {
            this(id, "", null, null, true, true);
        }

        public AppAccount(int id, String number, String name, String robotName, boolean autoSync, boolean publicFolder) {
            this.id = id;
            this.number = number;
            this.name = name;
            this.robotName = robotName;
            this.autoSync = autoSync;
            this.publicFolder = publicFolder;
        }
    }







    public static void deleteAppAccount(int id) {
        final int finalId = id;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                AppAccount account = (AppAccount) AccountsController.getInstance().listAppAccounts.get(Integer.valueOf(finalId));
                if (!(account == null || Turbo.getOldUser() == null || account.id != 0)) {
                    ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0).edit().putString("user", null);
                    ApplicationLoader.applicationContext.getSharedPreferences("tellgram", 0).edit().putBoolean("load_old_config", false).commit();
                }
                AccountsController.getInstance().listAppAccounts.remove(Integer.valueOf(finalId));
                AccountsController.getInstance().saveAppAccounts();
            }
        });
        deleteUserPrefs(id);
        Turbo.deleteDirectory(new File(ApplicationLoader.getFilesDirFixed().getParentFile(), "user" + id));
        Turbo.deleteDirectory(new File(AndroidUtilities.getCacheDir().getParentFile(), "user" + id));
    }

    public static void deleteUserPrefs(int id) {
        File[] array = new File(ApplicationLoader.getFilesDirFixed().getParentFile().getParentFile(), "shared_prefs").listFiles();
        if (array != null && array.length > 0) {
            for (int b = 0; b < array.length; b++) {
                String name = array[b].getName().toLowerCase();
                if (name.endsWith("-" + id + ".xml") || name.endsWith("-" + id + ".bak")) {
                    array[b].delete();
                }
            }
        }
    }

    private void upgradePrefsName() {
        File prefsFolder = new File(ApplicationLoader.getFilesDirFixed().getParentFile().getParentFile(), "shared_prefs");
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("tellgram", 0);
        if (!preferences.getBoolean("upgrade_prefs_name", false)) {
            for (Integer intValue : this.listAppAccounts.keySet()) {
                int id = intValue.intValue();
                new File(prefsFolder, "userconfing" + id + ".xml").renameTo(new File(prefsFolder, "userconfing-" + id + ".xml"));
                new File(prefsFolder, PreferenceManager.prefsName + id + ".xml").renameTo(new File(prefsFolder, "mainconfig-" + id + ".xml"));
                new File(prefsFolder, "langconfig" + id + ".xml").renameTo(new File(prefsFolder, "langconfig-" + id + ".xml"));
                new File(prefsFolder, "dataconfig" + id + ".xml").renameTo(new File(prefsFolder, "dataconfig-" + id + ".xml"));
                new File(prefsFolder, "uploadinfo" + id + ".xml").renameTo(new File(prefsFolder, "uploadinfo-" + id + ".xml"));
                new File(prefsFolder, "logininfo2" + id + ".xml").renameTo(new File(prefsFolder, "logininfo2-" + id + ".xml"));
                new File(prefsFolder, "videoconvert" + id + ".xml").renameTo(new File(prefsFolder, "videoconvert-" + id + ".xml"));
                new File(prefsFolder, "Notifications" + id + ".xml").renameTo(new File(prefsFolder, "Notifications-" + id + ".xml"));
                new File(prefsFolder, "emoji" + id + ".xml").renameTo(new File(prefsFolder, "emoji-" + id + ".xml"));
                new File(prefsFolder, "stats" + id + ".xml").renameTo(new File(prefsFolder, "stats-" + id + ".xml"));
            }
            preferences.edit().putBoolean("upgrade_prefs_name", true).commit();
        }
    }

    public void saveLogin(String number, boolean autoSync) {
        ApplicationLoader.applicationContext.getSharedPreferences("tellgram", 0).edit().putInt("accounts_last_id", this.activeAccountId).commit();
        AppAccount account = (AppAccount) this.listAppAccounts.get(Integer.valueOf(this.activeAccountId));
        if (account != null) {
            account.autoSync = autoSync;
            account.number = number;
            saveAppAccounts();
        }
    }

    public void saveAppAccounts() {
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("tellgram", 0).edit();
        try {
            JSONArray array = new JSONArray();
            for (Integer intValue : this.listAppAccounts.keySet()) {
                AppAccount user = (AppAccount) this.listAppAccounts.get(Integer.valueOf(intValue.intValue()));
                JSONObject object = new JSONObject();
                object.put(TtmlNode.ATTR_ID, user.id);
                object.put("number", user.number);
                object.put("name", user.name);
                object.put("robotName", user.robotName);
                object.put(SyncSampleEntry.TYPE, user.autoSync);
                object.put("publicFolder", user.publicFolder);
                array.put(object);
            }
            editor.putString("accounts", array.toString());
        } catch (Throwable e) {
            FileLog.e(e);
        }
        editor.commit();
    }

    public void reloadAppAccounts() {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("tellgram", 0);
        try {
            JSONArray jsonArray = new JSONArray(preferences.getString("accounts", "[]"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = jsonObject.optInt(TtmlNode.ATTR_ID, -1);
                String phone = jsonObject.optString("number", "");
                String name = jsonObject.optString("name", null);
                String robotName = jsonObject.optString("robotName", null);
                boolean sync = jsonObject.optBoolean(SyncSampleEntry.TYPE, true);
                boolean publicFolder = jsonObject.optBoolean("publicFolder", true);
                if (id > -1) {
                    this.listAppAccounts.put(Integer.valueOf(id), new AppAccount(id, phone, name, robotName, sync, publicFolder));
                }
            }
            if (this.listAppAccounts.size() == 0) {
                int lastAddedAccountId = preferences.getInt("accounts_last_id", -1);
                this.listAppAccounts.put(Integer.valueOf(lastAddedAccountId + 1), new AppAccount(lastAddedAccountId + 1));
            }
            this.activeAccountId = preferences.getInt("active_account", 0);
            if (!this.listAppAccounts.containsKey(Integer.valueOf(this.activeAccountId))) {
                this.activeAccountId = getFirstAccountIdInTable();
                preferences.edit().putInt("active_account", this.activeAccountId).commit();
            }
            this.activeAccount = (AppAccount) this.listAppAccounts.get(Integer.valueOf(this.activeAccountId));
        } catch (Exception e) {
        }
    }

    public int getFirstAccountIdInTable() {
        Iterator iterator = this.listAppAccounts.keySet().iterator();
        if (iterator.hasNext()) {
            return ((Integer) iterator.next()).intValue();
        }
        return ApplicationLoader.applicationContext.getSharedPreferences("tellgram", 0).getInt("accounts_last_id", -1) + 1;
    }

    public boolean isNewActiveAccount() {
        if (ApplicationLoader.applicationContext.getSharedPreferences("tellgram", 0).getInt("accounts_last_id", -1) < this.activeAccountId) {
            return true;
        }
        return false;
    }

    public static void deleteDirectory(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteDirectory(child);
            }
        }
        file.delete();
    }
}
