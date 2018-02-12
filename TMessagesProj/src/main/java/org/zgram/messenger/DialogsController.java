package org.zgram.messenger;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import java.util.HashMap;
import org.aspectj.lang.JoinPoint;
import org.zgram.messenger.exoplayer2.text.ttml.TtmlNode;

import edit.acc.PreferenceManager;

public class DialogsController {
    private static DialogsController instance;
    private HashMap<Long, DialogInfo> dialogsDict = new HashMap();
    public String hiddenPasscodeHash;
    public byte[] hiddenPasscodeSalt = new byte[0];
    public int hiddenPasscodeType;
    public boolean hiddenUseFingerprint;
    private PythonSQLiteOpenHelper tDBHelper = PythonSQLiteOpenHelper.getInstance(ApplicationLoader.applicationContext);

    public static class DialogInfo {
        int auto_download;
        int bookmark_mid;
        boolean fav;
        boolean hidden;
        boolean lock;
        boolean private_read;
        boolean private_typing;

        public DialogInfo(boolean fav, boolean hidden, boolean lock, boolean private_read, boolean private_typing, int bookmark_mid, int auto_download) {
            this.fav = fav;
            this.hidden = hidden;
            this.lock = lock;
            this.private_read = private_read;
            this.private_typing = private_typing;
            this.bookmark_mid = bookmark_mid;
            this.auto_download = auto_download;
        }
    }

    DialogsController() {
        SQLiteDatabase tDB = this.tDBHelper.getReadableDatabase();
        Cursor c = tDB.query("dialogs", new String[]{"did", "fav", "hidden", JoinPoint.SYNCHRONIZATION_LOCK, "private_read", "private_typing", "bookmark_mid", "auto_download"}, "user = ?", new String[]{"" + UserConfig.getClientUserId()}, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                this.dialogsDict.put(Long.valueOf(c.getLong(c.getColumnIndex("did"))), new DialogInfo(c.getInt(c.getColumnIndex("fav")) == 1, c.getInt(c.getColumnIndex("hidden")) == 1, c.getInt(c.getColumnIndex(JoinPoint.SYNCHRONIZATION_LOCK)) == 1, c.getInt(c.getColumnIndex("private_read")) == 1, c.getInt(c.getColumnIndex("private_typing")) == 1, c.getInt(c.getColumnIndex("bookmark_mid")), c.getInt(c.getColumnIndex("auto_download"))));
            }
            c.close();
        }
        tDB.close();
        loadHiddenConfig();
    }

    public static DialogsController getInstance() {
        if (instance == null) {
            instance = new DialogsController();
        }
        return instance;
    }

    public boolean isDialogFavorite(long did) {
        DialogInfo info = (DialogInfo) this.dialogsDict.get(Long.valueOf(did));
        return info != null && info.fav;
    }

    public boolean isDialogHidden(long did) {
        SharedPreferences tPrefs = ApplicationLoader.applicationContext.getSharedPreferences(PreferenceManager.prefsName, 0);
        DialogInfo info = (DialogInfo) this.dialogsDict.get(Long.valueOf(did));
        if (info == null || !info.hidden || tPrefs.getString("hidden_key", "").length() == 0) {
            return false;
        }
        return true;
    }

    public boolean isDialogLock(long did) {
        DialogInfo info = (DialogInfo) this.dialogsDict.get(Long.valueOf(did));
        return info != null && info.lock;
    }

    public boolean isDialogPrivateRead(long did) {
        return ApplicationLoader.applicationContext.getSharedPreferences(PreferenceManager.prefsName, 0).contains("hide_reading" + did);
    }

    public boolean isDialogPrivateTyping(long did) {
        return ApplicationLoader.applicationContext.getSharedPreferences(PreferenceManager.prefsName, 0).contains("hide_typing" + did);
    }

    public int getBookmarkMessageId(long did) {
        DialogInfo info = (DialogInfo) this.dialogsDict.get(Long.valueOf(did));
        return info != null ? info.bookmark_mid : 0;
    }

    public int getAutoDownloadMask(long did) {
        DialogInfo info = (DialogInfo) this.dialogsDict.get(Long.valueOf(did));
        return info != null ? info.auto_download : 0;
    }

    public void removeDialog(long did) {
        SQLiteDatabase tDB = this.tDBHelper.getWritableDatabase();
        tDB.delete("dialogs", "user = ? AND did = ?", new String[]{"" + UserConfig.getClientUserId(), "" + did});
        tDB.close();
    }

    public void setDialogFavorite(long did, boolean value) {
        DialogInfo info = (DialogInfo) this.dialogsDict.get(Long.valueOf(did));
        if (info != null) {
            info.fav = value;
        }
        setX(did, "fav", value);
    }

    public void setDialogHidden(long did, boolean value) {
        if (ApplicationLoader.applicationContext.getSharedPreferences(PreferenceManager.prefsName, 0).getString("hidden_key", "").length() != 0) {
            DialogInfo info = (DialogInfo) this.dialogsDict.get(Long.valueOf(did));
            if (info != null) {
                info.hidden = value;
            }
            setX(did, "hidden", value);
        }
    }

    public void setDialogPrivateRead(long did, boolean value) {
        DialogInfo info;
        DialogInfo info2 = (DialogInfo) this.dialogsDict.get(Long.valueOf(did));
        if (info2 == null) {
            SQLiteDatabase tDB = this.tDBHelper.getReadableDatabase();
            Cursor c = tDB.query("dialogs", new String[]{"did", "fav", "hidden", JoinPoint.SYNCHRONIZATION_LOCK, "private_read", "private_typing", "bookmark_mid", "auto_download"}, "user = ?", new String[]{"" + UserConfig.getClientUserId()}, null, null, null);
            if (c != null) {
                info = info2;
                while (c.moveToNext()) {
                    info = new DialogInfo(c.getInt(c.getColumnIndex("fav")) == 1, c.getInt(c.getColumnIndex("hidden")) == 1, c.getInt(c.getColumnIndex(JoinPoint.SYNCHRONIZATION_LOCK)) == 1, c.getInt(c.getColumnIndex("private_read")) == 1, c.getInt(c.getColumnIndex("private_typing")) == 1, c.getInt(c.getColumnIndex("bookmark_mid")), c.getInt(c.getColumnIndex("auto_download")));
                    this.dialogsDict.put(Long.valueOf(c.getLong(c.getColumnIndex("did"))), info);
                }
                c.close();
            } else {
                info = info2;
            }
            tDB.close();
        } else {
            info = info2;
        }
        info.private_read = value;
        setX(did, "private_read", value);
    }

    public void setDialogPrivateTyping(long did, boolean value) {
        DialogInfo info;
        DialogInfo info2 = (DialogInfo) this.dialogsDict.get(Long.valueOf(did));
        if (info2 == null) {
            SQLiteDatabase tDB = this.tDBHelper.getReadableDatabase();
            Cursor c = tDB.query("dialogs", new String[]{"did", "fav", "hidden", JoinPoint.SYNCHRONIZATION_LOCK, "private_read", "private_typing", "bookmark_mid", "auto_download"}, "user = ?", new String[]{"" + UserConfig.getClientUserId()}, null, null, null);
            if (c != null) {
                info = info2;
                while (c.moveToNext()) {
                    info = new DialogInfo(c.getInt(c.getColumnIndex("fav")) == 1, c.getInt(c.getColumnIndex("hidden")) == 1, c.getInt(c.getColumnIndex(JoinPoint.SYNCHRONIZATION_LOCK)) == 1, c.getInt(c.getColumnIndex("private_read")) == 1, c.getInt(c.getColumnIndex("private_typing")) == 1, c.getInt(c.getColumnIndex("bookmark_mid")), c.getInt(c.getColumnIndex("auto_download")));
                    this.dialogsDict.put(Long.valueOf(c.getLong(c.getColumnIndex("did"))), info);
                }
                c.close();
            } else {
                info = info2;
            }
            tDB.close();
        } else {
            info = info2;
        }
        info.private_typing = value;
        setX(did, "private_typing", value);
    }

    public void setBookmarkMessageId(long did, int value) {
        DialogInfo info = (DialogInfo) this.dialogsDict.get(Long.valueOf(did));
        if (info == null) {
            info.bookmark_mid = value;
            setX(did, "bookmark_mid", value);
        } else {
            info.bookmark_mid = value;
            setX(did, "bookmark_mid", value);
        }
    }

    public void setAutoDownloadMask(long did, int value) {
        DialogInfo info;
        DialogInfo info2 = (DialogInfo) this.dialogsDict.get(Long.valueOf(did));
        if (info2 == null) {
            SQLiteDatabase tDB = this.tDBHelper.getReadableDatabase();
            Cursor c = tDB.query("dialogs", new String[]{"did", "fav", "hidden", JoinPoint.SYNCHRONIZATION_LOCK, "private_read", "private_typing", "bookmark_mid", "auto_download"}, "user = ?", new String[]{"" + UserConfig.getClientUserId()}, null, null, null);
            if (c != null) {
                info = info2;
                while (c.moveToNext()) {
                    info = new DialogInfo(c.getInt(c.getColumnIndex("fav")) == 1, c.getInt(c.getColumnIndex("hidden")) == 1, c.getInt(c.getColumnIndex(JoinPoint.SYNCHRONIZATION_LOCK)) == 1, c.getInt(c.getColumnIndex("private_read")) == 1, c.getInt(c.getColumnIndex("private_typing")) == 1, c.getInt(c.getColumnIndex("bookmark_mid")), c.getInt(c.getColumnIndex("auto_download")));
                    this.dialogsDict.put(Long.valueOf(c.getLong(c.getColumnIndex("did"))), info);
                }
                c.close();
            } else {
                info = info2;
            }
            tDB.close();
        } else {
            info = info2;
        }
        info.auto_download = value;
        setX(did, "auto_download", value);
    }

    private void setX(long did, String column, boolean value) {
        if (!isInDB(did)) {
            addToDB(did);
        }
        ContentValues values = new ContentValues();
        values.put(column, Boolean.valueOf(value));
        SQLiteDatabase tDB = this.tDBHelper.getWritableDatabase();
        tDB.update("dialogs", values, "user = ? AND did = ?", new String[]{"" + UserConfig.getClientUserId(), "" + did});
        tDB.close();
    }

    private void setX(long did, String column, int value) {
        if (!isInDB(did)) {
            addToDB(did);
        }
        ContentValues values = new ContentValues();
        values.put(column, Integer.valueOf(value));
        SQLiteDatabase tDB = this.tDBHelper.getWritableDatabase();
        tDB.update("dialogs", values, "user = ? AND did = ?", new String[]{"" + UserConfig.getClientUserId(), "" + did});
        tDB.close();
    }

    private void addToDB(long did) {
        ContentValues values = new ContentValues();
        values.put("user", Integer.valueOf(UserConfig.getClientUserId()));
        values.put("did", Long.valueOf(did));
        values.put("fav", Boolean.valueOf(false));
        values.put("hidden", Boolean.valueOf(false));
        values.put(JoinPoint.SYNCHRONIZATION_LOCK, Boolean.valueOf(false));
        values.put("private_read", Boolean.valueOf(false));
        values.put("private_typing", Boolean.valueOf(false));
        values.put("auto_download", Integer.valueOf(0));
        values.put("bookmark_mid", Integer.valueOf(0));
        SQLiteDatabase tDB = this.tDBHelper.getWritableDatabase();
        tDB.insert("dialogs", null, values);
        tDB.close();
    }

    private boolean isInDB(long did) {
        SQLiteDatabase tDB = this.tDBHelper.getReadableDatabase();
        Cursor c = tDB.query("dialogs", new String[]{TtmlNode.ATTR_ID}, "user = ? AND did = ?", new String[]{"" + UserConfig.getClientUserId(), "" + did}, null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                c.close();
                tDB.close();
                return true;
            }
            c.close();
        }
        tDB.close();
        return false;
    }

    public void loadHiddenConfig() {
        SharedPreferences tPrefs = ApplicationLoader.applicationContext.getSharedPreferences(PreferenceManager.prefsName, 0);
        this.hiddenPasscodeHash = tPrefs.getString("hidden_key", "");
        this.hiddenUseFingerprint = tPrefs.getBoolean("hidden_key_use_fingerprint", false);
        this.hiddenPasscodeType = tPrefs.getInt("hidden_key_type", 0);
        String passcodeSaltString = tPrefs.getString("hidden_key_salt", "");
        if (passcodeSaltString.length() > 0) {
            this.hiddenPasscodeSalt = Base64.decode(passcodeSaltString, 0);
        } else {
            this.hiddenPasscodeSalt = new byte[0];
        }
    }

    public void saveHiddenConfig() {
        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences(PreferenceManager.prefsName, 0).edit();
        edit.putString("hidden_key", this.hiddenPasscodeHash);
        edit.putString("hidden_key_salt", this.hiddenPasscodeSalt.length > 0 ? Base64.encodeToString(this.hiddenPasscodeSalt, 0) : "");
        edit.putInt("hidden_key_type", this.hiddenPasscodeType);
        edit.putBoolean("hidden_key_use_fingerprint", this.hiddenUseFingerprint);
        edit.commit();
    }
}
