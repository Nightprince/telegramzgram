package org.zgram.messenger;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.google.android.gms.analytics.ecommerce.Promotion;
import java.util.HashMap;
import java.util.Locale;
import net.hockeyapp.android.LoginActivity;
import org.aspectj.lang.JoinPoint;
import org.zgram.SQLite.SQLiteCursor;
import org.zgram.messenger.exoplayer2.text.ttml.TtmlNode;

public class PythonSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String QUERY_CREATE_TABLE_CATEGORIES = String.format("CREATE TABLE IF NOT EXISTS %s (`%s` INTEGER primary key autoincrement, `%s` TEXT, `%s` INTEGER);", new Object[]{"categories", TtmlNode.ATTR_ID, "name", "ordering"});
    private static final String QUERY_CREATE_TABLE_CATEGORY_DIALOGS = String.format("CREATE TABLE IF NOT EXISTS %s (`%s` INTEGER, `%s` INTEGER, `%s` INTEGER, PRIMARY KEY (user, cat_id, did));", new Object[]{"category_dialogs", "user", "cat_id", "did"});
    private static final String QUERY_CREATE_TABLE_CONTACTS = String.format("CREATE TABLE IF NOT EXISTS %s (`%s` INTEGER primary key autoincrement, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER);", new Object[]{"contacts", TtmlNode.ATTR_ID, "user", "cid", "hidden", JoinPoint.SYNCHRONIZATION_LOCK});
    private static final String QUERY_CREATE_TABLE_CONTACT_CHANGES = String.format("CREATE TABLE IF NOT EXISTS %s (`%s` INTEGER primary key autoincrement, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER DEFAULT (strftime('%%s','now') * 1000), `%s` TEXT, `%s` TEXT);", new Object[]{"contact_changes", TtmlNode.ATTR_ID, "user", "uid", LoginActivity.EXTRA_MODE, Promotion.ACTION_VIEW, "date", "old_data", "new_data"});
    private static final String QUERY_CREATE_TABLE_DIALOGS = String.format("CREATE TABLE IF NOT EXISTS %s (`%s` INTEGER primary key autoincrement, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER);", new Object[]{"dialogs", TtmlNode.ATTR_ID, "user", "did", "fav", "hidden", JoinPoint.SYNCHRONIZATION_LOCK, "private_read", "private_typing", "auto_download", "bookmark_mid"});
    private static final String QUERY_CREATE_TABLE_DIALOG_TYPES = String.format("CREATE TABLE IF NOT EXISTS %s (`%s` INTEGER primary key autoincrement, `%s` TEXT, `%s` INTEGER, `%s` INTEGER);", new Object[]{"tabs", TtmlNode.ATTR_ID, "name", "show", "ordering"});
    private static final String QUERY_CREATE_TABLE_DOWNLOAD_MANAGER_QUEUE = String.format("CREATE TABLE IF NOT EXISTS %s (`%s` INTEGER primary key autoincrement, `%s` TEXT, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER);", new Object[]{"download_manager_queue", TtmlNode.ATTR_ID, "name", "ordering", "schedule", "start_time", "stop_time", "start_wifi", "stop_wifi", "start_data", "stop_data", "day_of_week", "simultaneous"});
    private static final String QUERY_CREATE_TABLE_MAIN_MENU = String.format("CREATE TABLE IF NOT EXISTS %s (`%s` INTEGER primary key autoincrement, `%s` TEXT, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` TEXT);", new Object[]{"main_menu", TtmlNode.ATTR_ID, "name", "type", "show", "ordering", "parent", "data"});
    private static final String QUERY_CREATE_TABLE_MULTI_FORWARD_DIALOG_TYPES = String.format("CREATE TABLE IF NOT EXISTS %s (`%s` INTEGER primary key autoincrement, `%s` TEXT, `%s` INTEGER, `%s` INTEGER);", new Object[]{"multi_forward_dialog_types", TtmlNode.ATTR_ID, "name", "show", "ordering"});
    private static final String QUERY_CREATE_TABLE_SPECIAL_CONTACTS = String.format("CREATE TABLE IF NOT EXISTS %s (`%s` INTEGER primary key autoincrement, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER);", new Object[]{"special_contacts", TtmlNode.ATTR_ID, "user", "uid", "online", "offline", "avatar", "name", "username", "phone", "log"});
    private static final String QUERY_CREATE_TABLE_SPECIAL_CONTACTS_STATISTICS = String.format("CREATE TABLE IF NOT EXISTS %s (`%s` INTEGER primary key autoincrement, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER DEFAULT (strftime('%%s','now') * 1000));", new Object[]{"special_contacts_statistics", TtmlNode.ATTR_ID, "user", "type", "date"});
    private static HashMap<Context, PythonSQLiteOpenHelper> mInstances;
    public static DispatchQueue telegraphDatabaseQueue = new DispatchQueue("PythonDatabaseQueue");

    private PythonSQLiteOpenHelper(Context context) {
        super(context, "Python.db", null, 8);
    }

    public static PythonSQLiteOpenHelper getInstance(Context context) {
        if (mInstances == null) {
            mInstances = new HashMap();
        }
        if (mInstances.get(context) == null) {
            mInstances.put(context, new PythonSQLiteOpenHelper(context));
            telegraphDatabaseQueue.setPriority(10);
        }
        return (PythonSQLiteOpenHelper) mInstances.get(context);
    }

    public void onCreate(SQLiteDatabase db) {
        version1(db);
        version2(db);
        version3(db);
        version4(db);
        createMainMenu(db);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            version2(db);
        }
        if (oldVersion < 3) {
            db.execSQL("DROP TABLE IF EXISTS fav_emojies");
            db.execSQL("DROP TABLE IF EXISTS fav_stickers");
            version3(db);
        }
        if (oldVersion < 4) {
            version4(db);
        }
        if (oldVersion < 5) {
            db.execSQL("DROP TABLE IF EXISTS download_manager");
            db.execSQL("DROP TABLE IF EXISTS download_manager_queue");
            db.execSQL("DROP TABLE IF EXISTS dialogs");
            db.execSQL(QUERY_CREATE_TABLE_DOWNLOAD_MANAGER_QUEUE);
            db.execSQL(QUERY_CREATE_TABLE_DIALOGS);
            db.execSQL("INSERT INTO download_manager_queue VALUES (1, '', 0, 0, 120, 480, 0, 0, 0, 0, 127, 1)");
        }
        if (oldVersion < 6) {
            db.execSQL("DROP TABLE IF EXISTS messages");
            db.execSQL("DROP TABLE IF EXISTS main_menu");
            db.execSQL(QUERY_CREATE_TABLE_MAIN_MENU);
            db.execSQL("DROP TABLE IF EXISTS special_contacts");
            db.execSQL(QUERY_CREATE_TABLE_SPECIAL_CONTACTS);
            db.execSQL("DROP TABLE IF EXISTS category_dialogs");
            db.execSQL(QUERY_CREATE_TABLE_CATEGORY_DIALOGS);
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('', 3, 1, 0)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('', 2, 1, 1)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('account_change', 0, 1, 2)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('account_manage', 0, 1, 3)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('', 1, 1, 4)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('download_manager', 0, 1, 5)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('file_manager', 0, 1, 6)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('categories', 0, 1, 7)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('time_line', 0, 1, 8)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('favorite_messages', 0, 1, 9)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('', 1, 1, 10)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('new_group', 0, 1, 11)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('new_secret', 0, 1, 12)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('new_channel', 0, 1, 13)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('', 1, 1, 14)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('contacts', 0, 1, 15)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('contacts_online', 0, 1, 16)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('contacts_changes', 0, 1, 17)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('id_finder', 0, 1, 18)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('contacts_special', 0, 1, 19)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('', 1, 1, 20)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('settings', 0, 1, 21)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('settings_telegraph', 0, 1, 22)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('theme', 0, 1, 23)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('tutorial', 0, 1, 24)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('store_comment', 0, 1, 25)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('invite', 0, 1, 26)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('channel', 0, 1, 27)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('support', 0, 1, 28)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('delete_account', 0, 1, 29)");
        }
        if (oldVersion < 7) {
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('anti_ads', 0, 1, 23)");
        }
        if (oldVersion < 8) {
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('calls', 0, 1, 19)");
            db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('screen_light', 0, 1, 23)");
        }
    }

    private void version1(SQLiteDatabase db) {
        db.execSQL(QUERY_CREATE_TABLE_CONTACT_CHANGES);
    }

    private void version2(SQLiteDatabase db) {
        db.execSQL(QUERY_CREATE_TABLE_DIALOG_TYPES);
        db.execSQL(QUERY_CREATE_TABLE_DOWNLOAD_MANAGER_QUEUE);
        db.execSQL(QUERY_CREATE_TABLE_CATEGORIES);
        db.execSQL(QUERY_CREATE_TABLE_CATEGORY_DIALOGS);
        db.execSQL(QUERY_CREATE_TABLE_DIALOGS);
        db.execSQL(QUERY_CREATE_TABLE_CONTACTS);
        db.execSQL(QUERY_CREATE_TABLE_SPECIAL_CONTACTS);
        db.execSQL(QUERY_CREATE_TABLE_SPECIAL_CONTACTS_STATISTICS);
        db.execSQL(QUERY_CREATE_TABLE_MAIN_MENU);
        db.execSQL("INSERT INTO tabs (name, show, ordering) VALUES ('all', 1, 0)");
        db.execSQL("INSERT INTO tabs (name, show, ordering) VALUES ('unread', 1, 1)");
        db.execSQL("INSERT INTO tabs (name, show, ordering) VALUES ('fav', 1, 2)");
        db.execSQL("INSERT INTO tabs (name, show, ordering) VALUES ('user', 1, 3)");
        db.execSQL("INSERT INTO tabs (name, show, ordering) VALUES ('groups', 0, 4)");
        db.execSQL("INSERT INTO tabs (name, show, ordering) VALUES ('group', 1, 5)");
        db.execSQL("INSERT INTO tabs (name, show, ordering) VALUES ('supergroup', 1, 6)");
        db.execSQL("INSERT INTO tabs (name, show, ordering) VALUES ('channel', 1, 7)");
        db.execSQL("INSERT INTO tabs (name, show, ordering) VALUES ('bot', 1, 8)");
        db.execSQL("INSERT INTO download_manager_queue VALUES (1, '', 0, 0, 120, 480, 0, 0, 0, 0, 127, 1)");
    }

    private void version3(SQLiteDatabase db) {
        db.execSQL("INSERT INTO tabs (name, show, ordering) VALUES ('secret', 0, 3)");
    }

    private void version4(SQLiteDatabase db) {
        db.execSQL(QUERY_CREATE_TABLE_MULTI_FORWARD_DIALOG_TYPES);
        db.execSQL("INSERT INTO multi_forward_dialog_types (name, show, ordering) VALUES ('all', 1, 0)");
        db.execSQL("INSERT INTO multi_forward_dialog_types (name, show, ordering) VALUES ('fav', 1, 1)");
        db.execSQL("INSERT INTO multi_forward_dialog_types (name, show, ordering) VALUES ('user', 1, 2)");
        db.execSQL("INSERT INTO multi_forward_dialog_types (name, show, ordering) VALUES ('groups', 0, 3)");
        db.execSQL("INSERT INTO multi_forward_dialog_types (name, show, ordering) VALUES ('group', 1, 4)");
        db.execSQL("INSERT INTO multi_forward_dialog_types (name, show, ordering) VALUES ('supergroup', 1, 5)");
        db.execSQL("INSERT INTO multi_forward_dialog_types (name, show, ordering) VALUES ('channel', 1, 6)");
        db.execSQL("INSERT INTO multi_forward_dialog_types (name, show, ordering) VALUES ('bot', 1, 7)");
        db.execSQL("INSERT INTO multi_forward_dialog_types (name, show, ordering) VALUES ('contact', 1, 8)");
    }

    public static void createMainMenu(SQLiteDatabase db) {
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('', 3, 1, 0)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('', 2, 1, 1)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('account_change', 0, 1, 2)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('account_manage', 0, 1, 3)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('', 1, 1, 4)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('download_manager', 0, 1, 5)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('file_manager', 0, 1, 6)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('categories', 0, 1, 7)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('time_line', 0, 1, 8)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('favorite_messages', 0, 1, 9)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('', 1, 1, 10)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('new_group', 0, 1, 11)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('new_secret', 0, 1, 12)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('new_channel', 0, 1, 13)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('', 1, 1, 14)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('contacts', 0, 1, 15)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('contacts_online', 0, 1, 16)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('contacts_changes', 0, 1, 17)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('id_finder', 0, 1, 18)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('contacts_special', 0, 1, 19)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('calls', 0, 1, 20)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('', 1, 1, 21)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('settings', 0, 1, 22)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('settings_telegraph', 0, 1, 23)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('theme', 0, 1, 24)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('screen_light', 0, 1, 25)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('anti_ads', 0, 1, 26)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('tutorial', 0, 1, 27)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('store_comment', 0, 1, 28)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('invite', 0, 1, 29)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('channel', 0, 1, 30)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('support', 0, 1, 31)");
        db.execSQL("INSERT INTO main_menu (name, type, show, ordering) VALUES ('delete_account', 0, 1, 32)");
    }

    public static int getTableNextIndex(String tableName) {
        int index = 1;
        SQLiteCursor cursor = null;
        try {
            cursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT seq FROM SQLITE_SEQUENCE WHERE name = %s", new Object[]{tableName}), new Object[0]);
            if (cursor.next()) {
                index = cursor.intValue(0) + 1;
            }
            if (cursor != null) {
                cursor.dispose();
            }
        } catch (Throwable e) {
            FileLog.e(e);
            if (cursor != null) {
                cursor.dispose();
            }
        }
        return index;
    }

    public static int getTableNextIndexNative(SQLiteDatabase tDB, String tableName) {
        int index = 1;
        Cursor c = tDB.query("SQLITE_SEQUENCE", new String[]{"seq"}, "name = ?", new String[]{tableName}, null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                index = c.getInt(0) + 1;
            }
            c.close();
        }
        return index;
    }
}
