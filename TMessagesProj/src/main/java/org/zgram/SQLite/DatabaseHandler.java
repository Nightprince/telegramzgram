package org.zgram.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.zgram.messenger.FileLog;
import org.zgram.messenger.exoplayer2.text.ttml.TtmlNode;

import edit.Favourite;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "favourites";
    private static final String TABLE_FAVS = "tbl_favs";

    private static final String KEY_ID = "id";
    private static final String KEY_CHAT_ID = "chat_id";



    private static final String COL_CHANGE_TYPE = "change_type";
    private static final String COL_CHAT_ID = "chat_id";
    private static final String COL_DATE = "date";
    private static final String COL_ID = "id";
    private static final String COL_PREVIOUS_NAME = "previous_name";
    public static final String DIALOG_STATUS_COL_HAS_HOTGRAM = "has_hotgram";
    public static final String DIALOG_STATUS_COL_ID = "id";
    public static final String DIALOG_STATUS_COL_INVITE_SENT = "invite_sent";
    private static final String KEY_CLOUD_ID = "cloud_id";
    private static final String KEY_MSG_ID = "msg_id";
    private static final String TABLE_CONTACT_CHANGE_LOG = "TABLE_CONTACT_CHANGE_LOG";
    private static final String TABLE_DIALOG_STATUS = "TABLE_DIALOG_STATUS";
    private static final String TABLE_DOWNLOAD_QUEUE = "TABLE_DOWNLOAD_QUEUE";
    private static final String TABLE_FAVS_MESSAGES = "tbl_favs_messages";




    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FAVS_TABLE = "CREATE TABLE " + TABLE_FAVS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_CHAT_ID + " INTEGER" + ")";
        db.execSQL(CREATE_FAVS_TABLE);


        String QUERY_CREATE_TABLE_SPECIAL_CONTACTS = String.format("CREATE TABLE IF NOT EXISTS %s (`%s` INTEGER primary key autoincrement, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER);", new Object[]{"special_contacts", TtmlNode.ATTR_ID, "user", "uid", "online", "offline", "avatar", "name", "username", "phone", "log"});
        String QUERY_CREATE_TABLE_SPECIAL_CONTACTS_STATISTICS = String.format("CREATE TABLE IF NOT EXISTS %s (`%s` INTEGER primary key autoincrement, `%s` INTEGER, `%s` INTEGER, `%s` INTEGER DEFAULT (strftime('%%s','now') * 1000));", new Object[]{"special_contacts_statistics", TtmlNode.ATTR_ID, "user", "type", "date"});


        db.execSQL(QUERY_CREATE_TABLE_SPECIAL_CONTACTS);
        db.execSQL(QUERY_CREATE_TABLE_SPECIAL_CONTACTS_STATISTICS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVS);
        onCreate(db);
    }

    public void addFavourite(Favourite favourite) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CHAT_ID, favourite.getChatID());
        db.insert(TABLE_FAVS, null, values);
        db.close();
    }







    public Favourite getFavouriteByChatId(long chat_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {

            String [] projection = {
                    KEY_ID,
                    KEY_CHAT_ID
            };

            String whereClause = KEY_CHAT_ID+"=?";
            String [] whereArgs = {String.valueOf(chat_id)};

            cursor = db.query(
                    TABLE_FAVS,
                    projection,
                    whereClause,
                    whereArgs,
                    null,
                    null,
                    null
            );

            if( cursor != null && cursor.moveToFirst() ){
                return new Favourite(cursor.getLong(1));
            }
        } catch (Exception e) {
            if(cursor != null)
                cursor.close();
            FileLog.e( e);
            return null;
        } finally {
            if(cursor != null)
                cursor.close();
        }
        return null;
    }

    public void deleteFavourite(Long chat_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVS, KEY_CHAT_ID + " = ?", new String[] { String.valueOf(chat_id) });
        db.close();
    }

    /*public List<Favourite> getAllFavourites() {
        List<Favourite> favsList = new ArrayList<Favourite>();

        String selectQuery = "SELECT  * FROM " + TABLE_FAVS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Favourite favourite = new Favourite();
                favourite.setID(Integer.parseInt(cursor.getString(0)));
                favourite.setChatID(cursor.getLong(1));

                favsList.add(favourite);
            } while (cursor.moveToNext());
        }

        return favsList;
    }

    public int getFavouritesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_FAVS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }*/

}
