package edit.settt;

import org.zgram.messenger.DialogObject;
import org.zgram.messenger.MessagesController;
import org.zgram.tgnet.TLRPC;

import edit.Setting;


/**
 * Created by Pouya on 8/25/2016.
 */
public class TurnQuitToHideController {
    public static void add(Long id){
        String m= Setting.getLastInListsquithide();
        m=m+"-"+String.valueOf(id);
        Setting.setLastInLists(m);
    }
    public static void add(String user){
        String m= Setting.getLastInListsquithide();
        m=m+"-"+String.valueOf(user);
        Setting.setLastInLists(m);
    }
    public static Boolean is(String user){
        try {
            if (Setting.getLastInListsquithide() == null || Setting.getLastInListsquithide().length() < 1)
                return false;
            boolean m = Setting.getLastInListsquithide().toLowerCase().contains(user.toLowerCase());
            return m;
        }catch (Exception e){
            return false;
        }
    }
    public static Boolean is(TLRPC.TL_dialog dialog){

        if(DialogObject.isChannel(dialog)){
            int diid=(int)dialog.id;
            TLRPC.Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-diid));
            if(chat.username!=null&&is(chat.username)){
                return true;
            }
        }
        return false;
    }
}
