package edit.acc;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.util.Set;

public class PreferenceManager {
    public static final String ACCEPT_FLIRT_MSG = "ACCEPT_FLIRT_MSG";
    public static final String ADBLOCKER_BLOCK_LINKS = "ADBLOCKER_BLOCK_LINKS";
    public static final String APP_FIRST_OPEN_TIME = "APP_FIRST_OPEN_TIME";
    public static final String APP_OPENED_COUNT = "APP_OPENED_COUNT";
    public static final String APP_UPDATE_LINK = "APP_UPDATE_LINK";
    public static final String APP_VERSION_IS_NOT_SUPPORTED = "APP_VERSION_IS_NOT_SUPPORTED";
    public static final String BLOCK_RADAR_REQUESTS = "BLOCK_RADAR_REQUESTS";
    public static final String CONFIRM_STICKER = "CONFIRM_STICKER";
    public static final String CONFIRM_VOICE = "CONFIRM_VOICE";
    public static final String DO_NOT_ASK_ME_FOR_RATING = "DO_NOT_ASK_ME_FOR_5STAR";
    public static final String FIRST_TIME_ASKING_FOR_LOCATION_PERMISSION = "FIRST_TIME_ASKING_FOR_LOCATION_PERMISSION";
    public static final String FONT = "TeleTalkFont";
    public static final String FORWARDED_FROM_HOTS_HEADER = "FORWARDED_FROM_HOTS_HEADER";
    public static final String HIDE_PHONE_NUMBER = "Hide_Phone_Number";
    public static final String HIDE_TABS_ON_SCROLL = "HIDE_TABS_ON_SCROLL";
    public static final String INVITED_GROUPS = "INVITED_GROUPS";
    public static final String INVITED_USERS = "INVITED_USERS";
    public static final String INVITE_RIBBON_TEXT = "INVITE_RIBBON_TEXT";
    public static final String INVITE_USER_TEXT = "INVITE_USER_TEXT";
    public static final String JOIN_TELETALK_CHANNEL_DIALOG_SHOWN = "JOIN_TT_CH_DIALOG_SHOWN";
    public static final String LAST_CHECK_FOR_UPDATE_TIME = "LAST_CHECK_FOR_UPDATE_TIME";
    public static final String LAST_PHONE_NUMBER = "LAST_PHONE_NUMBER";
    public static final String NEARBY_OPENED_COUNT = "NEARBY_ACTIVITY_OPENED";
    public static final String NEARBY_TERMS_ACCEPTED = "NEARBY_TERMS_ACCEPTED";
    public static final String NEED_CHANGE_USER_NUMBER = "NEED_CHANGE_USER_NUMBER";
    public static final String RATING_POSTPONE_TIME = "GIVING_5STAR_POSTPONE_TIME";
    public static final String SHOW_AD_BLOCKED_TOAST = "SHOW_AD_BLOCKED_TOAST";
    public static final String SHOW_BOOKMARK_GUIDE = "SHOW_BOOKMARK_GUIDE";
    public static final String SHOW_BOTTOM_NAV_BAR = "SHOW_BOTTOM_NAV_BAR";
    public static final String SHOW_FIRST_LAUNCH_GUIDE = "SHOW_FIRST_LAUNCH_GUIDE";
    public static final String SHOW_GHOST_GUIDE = "SHOW_GHOST_GUIDE";
    public static final String SHOW_INVITE_RIBBON = "SHOW_INVITE_RIBBON";
    public static final String START_FLIRT_MSG = "START_FLIRT_MSG";
    public static final String SURVEY_DIALOG_SHOWN = "SURVEY_DIALOG_SHOWN";
    public static final String TO_BE_LOGGED_INVITES = "TO_BE_LOGGED_INVITES";
    public static final String TRENDS_OPENED_COUNT = "NEARBY_ACTIVITY_OPENED";
    public static final String UPDATE_DIALOG_SHOWN_FOR_VERSION = "UPDATE_DIALOG_SHOWN_FOR_VERSION";
    public static final String USER_DATA_FIRSTNAME = "USER_DATA_FIRSTNAME";
    public static final String USER_DATA_LASTNAME = "USER_DATA_LASTNAME";
    public static final String USER_DATA_PHONE = "USER_DATA_PHONE";
    public static final String USER_DATA_PROFILE_PHOTO_ID = "USER_DATA_PROFILE_PHOTO_ID";
    public static final String USER_DATA_PROFILE_TELETALK_ID = "USER_DATA_PROFILE_TELETALK_ID";
    public static final String USER_DATA_TELEGRAM_ID = "USER_DATA_TELEGRAM_ID";
    public static final String USER_DATA_UPLOADED_AVATAR_TYPE = "USER_DATA_UPLOADED_AVATAR_TYPE";
    public static final String USER_DATA_USERNAME = "USER_DATA_USERNAME";
    public static final String USER_DATA_USER_ID = "USER_DATA_USER_ID";
    public static final String USER_RATED_FOR_APP = "USER_RATED_FOR_APP";
    private static volatile PreferenceManager instance = null;
    public static final String prefsName = "mainconfig";
    private SharedPreferences prefs;

    public static PreferenceManager getInstance(Context context) {
        PreferenceManager localInstance2;
        PreferenceManager localInstance = instance;
        if (localInstance == null) {
            synchronized (PreferenceManager.class) {
                try {
                    localInstance = instance;
                    if (localInstance == null) {
                        localInstance2 = new PreferenceManager(context);
                        instance = localInstance2;
                        localInstance = localInstance2;
                    }
                } catch (Throwable th3) {
                    th3.printStackTrace();
                }
            }
        }
        return localInstance;
    }

    public SharedPreferences getPrefs() {
        return this.prefs;
    }

    public PreferenceManager(Context context) {
        this.prefs = context.getSharedPreferences(prefsName, 0);
    }

    public void saveBoolean(String key, boolean value) {
        this.prefs.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultVal) {
        return this.prefs.getBoolean(key, defaultVal);
    }

    public long getLong(String key, long defValue) {
        return this.prefs.getLong(key, defValue);
    }

    public String getString(String key, String defaultVal) {
        return this.prefs.getString(key, defaultVal);
    }

    public Set<String> getStringSet(String key, Set<String> defValue) {
        return this.prefs.getStringSet(key, defValue);
    }

    public Editor getPrefsEditor() {
        return this.prefs.edit();
    }

    public int getInt(String key, int defValue) {
        return this.prefs.getInt(key, defValue);
    }

    public void finish() {
        instance = null;
    }
}
