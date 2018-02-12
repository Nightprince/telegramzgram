package edit.settt;

import android.content.Context;

import java.util.ArrayList;

import edit.settt.SavedMessage;
import edit.Setting;

/**
 * Created by Pouya on 8/9/2016.
 */

/**
 * Created by Pouya on 6/13/2016.
 */
public class StoreMessagesController {
    private static StoreMessagesController fav;
    private static Context thiscontext;

    public static void addToFavors(Long id){
        String m= Setting.getMessageList();
        m=m+"-"+String.valueOf(id);
        Setting.setMessageList(m);
    }
    public static ArrayList<SavedMessage> get(){
        ArrayList<SavedMessage> list=new ArrayList<>();
        String m=Setting.getMessageList();
        String[] t = m.split("-");
        for(String tt:t){

        }
        return list;
    }
}
