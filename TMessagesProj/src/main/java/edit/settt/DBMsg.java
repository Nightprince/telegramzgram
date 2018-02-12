package edit.settt;


import org.zgram.SQLite.SQLiteCursor;
import org.zgram.SQLite.SQLiteDatabase;
import org.zgram.SQLite.SQLiteException;
import org.zgram.messenger.MessageObject;
import org.zgram.messenger.MessagesStorage;

/**
 * Created by Pouya on 9/3/2016.
 */
public class DBMsg {
    public long mid;
    public int uid;
    public int read_state;
    public int send_state;
    public int date;
    public byte[] data;
    public int out;
    public int ttl;
    public int media;
    public byte[] replaydata;
    public int imp;
    public DBMsg(MessageObject msg){
        SQLiteDatabase d= MessagesStorage.getInstance().getDatabase();
        SQLiteCursor cursor=null;
        long curmid=msg.getId();
        try {
        if(msg.getDialogId()<0) {

            cursor = d.queryFinalized("SELECT last_mid,inbox_max FROM dialogs where did="+String.valueOf( msg.getDialogId()));
            cursor.next();
            int inboxmax=cursor.intValue(1);
            long lastmid=cursor.longValue(0);
            int diff=inboxmax-msg.getId();
             curmid=lastmid-diff;

        }
            cursor = d.queryFinalized("SELECT mid,uid," +
                    " read_state, send_state, date," +
                    " data, out, ttl, media," +
                    " replydata , imp  FROM messages where mid="+String.valueOf( curmid));
        } catch (SQLiteException e) {
            e.printStackTrace();

        }
        try {
            while (cursor.next()) {
                this.mid = curmid;
                this.uid = cursor.intValue(1);
                this.read_state = cursor.intValue(2);
                this.send_state = cursor.intValue(3);
                this.date = cursor.intValue(4);
                this.data = cursor.byteArrayValue(5);
                this.out = cursor.intValue(6);
                this.ttl = cursor.intValue(7);
                this.media = cursor.intValue(8);
                this.replaydata = cursor.byteArrayValue(9);
                this.imp = cursor.intValue(10);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

}
