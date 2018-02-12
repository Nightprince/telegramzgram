package edit.settt;

import android.content.Context;
import android.util.Log;

import org.zgram.SQLite.SQLiteCursor;
import org.zgram.SQLite.SQLiteDatabase;
import org.zgram.SQLite.SQLiteException;
import org.zgram.messenger.ApplicationLoader;
import org.zgram.messenger.MessageObject;
import org.zgram.messenger.MessagesStorage;
import org.zgram.tgnet.NativeByteBuffer;
import org.zgram.tgnet.TLRPC;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by Pouya on 8/9/2016.
 */
public class SavedMessage {

    public SavedMessage() {

    }

    public static ArrayList<MessageObject> getAllStoredMessage(){
        //Loadall();
        ArrayList<MessageObject> mlist=new ArrayList<>();
         SQLiteDatabase database= MessagesStorage.getInstance().getDatabase();
        TLRPC.Message message=null;
        SQLiteCursor cursor = null;
        try {
            cursor = database.queryFinalized("SELECT read_state, data, send_state, mid, date, uid FROM storemessages");
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        try {
            while (cursor.next()) {
               // Log.e("row","loaded record " + cursor.intValue(3) );
                NativeByteBuffer data = cursor.byteBufferValue(1);
                if (data != null) {
                     message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                    data.reuse();
                    MessageObject.setUnreadFlags(message, cursor.intValue(0));
                    message.id = cursor.intValue(3);
                    message.date = cursor.intValue(4);
                    message.dialog_id = cursor.longValue(5);
//                    messages.add(message);

                    int lower_id = (int) message.dialog_id;
                    //addUsersAndChatsFromMessage(message, usersToLoad, chatsToLoad);
                    message.send_state = cursor.intValue(2);
                    if (message.to_id.channel_id == 0 && !MessageObject.isUnread(message) && lower_id != 0 || message.id > 0) { //TODO check
                        message.send_state = 0;
                    }
                    if (lower_id == 0 && !cursor.isNull(5)) {
                        message.random_id = cursor.longValue(5);
                    }
                }
                if(message!=null){
                   MessageObject ss= new MessageObject(message,null,true);
                    mlist.add(ss);
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        cursor.dispose();
        return mlist;
    }
    public static ArrayList<MessageObject> getAll(Context context){
        ArrayList<MessageObject> list=new ArrayList<>();
        list=getAllStoredMessage();
        return list;
    }

    public static void Save(MessageObject msg, Context context) {

       ;; Log.e("trytosave","mid:"+msg.messageOwner.id);
        DBMsg msgs = new DBMsg(msg);
        SaveToDatabase(msgs);
    }
    public static boolean SaveToDatabase(DBMsg msg){
    ;;    Log.e("savingtodb","mid:"+msg.mid);
        try {
            SQLiteDatabase db= MessagesStorage.getInstance().getDatabase();
           // db.executeInt("insert into storemessages(mid,uid,read_state,send_state,date,data,out,ttl,media,imp) values(?,?,?,?,?,?,?,?,?,?)",(double)msg.mid,msg.uid,msg.read_state,msg.send_state,msg.date,msg.data,msg.out,msg.ttl,msg.media,msg.imp);
            db.executeFast("INSERT INTO storemessages SELECT * FROM messages WHERE mid =" + String.valueOf(msg.mid) + "").stepThis().dispose();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

return true;

    }


    public static void Delete(MessageObject selectedObject) {
        SQLiteDatabase database=null;
        File cacheFile = new File(ApplicationLoader.getFilesDirFixed(), "cache4.db");
        try {
            database = new SQLiteDatabase(cacheFile.getPath());
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        try {
            database.executeFast("delete from storemessages  WHERE mid =" + String.valueOf(selectedObject.messageOwner.id) + "").stepThis().dispose();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }
}
