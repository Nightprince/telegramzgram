package edit.acc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;
import java.io.File;
import org.zgram.messenger.ApplicationLoader;
import org.zgram.tgnet.SerializedData;
import org.zgram.tgnet.TLRPC.User;
import org.zgram.ui.LaunchActivity;

public class Turbo {
    public static SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences(PreferenceManager.prefsName, 0);
    public static boolean categorizeProfile = preferences.getBoolean("cp_enable", true);
    public static boolean copySender = preferences.getBoolean("copySender", true);
    public static boolean directBot = preferences.getBoolean("direct_bot", true);
    public static boolean directChannel = preferences.getBoolean("direct_channel", true);
    public static boolean directContact = preferences.getBoolean("direct_contact", false);
    public static boolean directGroup = preferences.getBoolean("direct_group", false);
    public static boolean favoriteMessages = preferences.getBoolean("fm_enable", true);
   // public static boolean ghostMode = preferences.getBoolean("ghost_mode", false);
    public static boolean hideTyping = preferences.getBoolean("hide_typing", false);
    public static boolean loadOldConfig = ApplicationLoader.applicationContext.getSharedPreferences("tellgram", 0).getBoolean("load_old_config", true);
    public static boolean saveInProfileNotQuote = preferences.getBoolean("fm_notquot", false);

    public static void setBooleanValue(String pref, boolean value) {
        preferences.edit().putBoolean(pref, value).commit();
        updatePreferences();
    }

    public static void setIntValue(String pref, int value) {
        preferences.edit().putInt(pref, value).commit();
        updatePreferences();
    }

    public static void setStringValue(String pref, String value) {
        preferences.edit().putString(pref, value).commit();
        updatePreferences();
    }

    public static boolean containValue(String pref) {
        return preferences.contains(pref);
    }

    public static void removeValue(String pref) {
        preferences.edit().remove(pref).commit();
    }

    private static void updatePreferences() {
        saveInProfileNotQuote = preferences.getBoolean("fm_notquot", false);
        categorizeProfile = preferences.getBoolean("cp_enable", true);
        favoriteMessages = preferences.getBoolean("fm_enable", true);
        directContact = preferences.getBoolean("direct_contact", false);
        directGroup = preferences.getBoolean("direct_group", false);
        directChannel = preferences.getBoolean("direct_channel", true);
        directBot = preferences.getBoolean("direct_bot", true);
        copySender = preferences.getBoolean("copySender", true);
    }

    public static String getUserPath(String path) {
        return AccountsController.getInstance().activeAccount.publicFolder ? path : path + "/User" + AccountsController.getInstance().activeAccountId;
    }

    public static User getOldUser() {
        String string = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0).getString("user", null);
        if (string == null) {
            return null;
        }
        byte[] bytes = Base64.decode(string, 0);
        if (bytes == null) {
            return null;
        }
        SerializedData data = new SerializedData(bytes);
        User user = User.TLdeserialize(data, data.readInt32(false), false);
        data.cleanup();
        return user;
    }

    public static void deleteDirectory(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteDirectory(child);
            }
        }
        file.delete();
    }

    public static void resetApp() {
        ((AlarmManager) ApplicationLoader.applicationContext.getSystemService("alarm")).set(1, System.currentTimeMillis() + 1000, PendingIntent.getActivity(ApplicationLoader.applicationContext, 123456, new Intent(ApplicationLoader.applicationContext, LaunchActivity.class), 268435456));
        System.exit(0);
    }
}
