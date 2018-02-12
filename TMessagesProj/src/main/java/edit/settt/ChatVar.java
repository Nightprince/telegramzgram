package edit.settt;

import edit.Setting;

/**
 * Created by Pouya on 8/25/2016.
 */
public class ChatVar {
    private static String channaltojoin;

    public static String getChannaltojoin() {
        return Setting.getCurrentJoiningChannel();
    }

    public static void setChannaltojoin(String channaltojoin) {
        Setting.setCurrentJoiningChannel(channaltojoin);
    }
}
