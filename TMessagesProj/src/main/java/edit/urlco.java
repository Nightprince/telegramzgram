package edit;

import java.io.Serializable;
import java.util.ArrayList;

import org.zgram.messenger.R;
import org.zgram.tgnet.TLRPC;


public class urlco implements Serializable {


    public static int[] voiceColor2 = new int[]{R.drawable.mic, R.drawable.mic1, R.drawable.mic2, R.drawable.mic3, R.drawable.mic4, R.drawable.mic5, R.drawable.mic5, R.drawable.mic2, R.drawable.mic5};
    public static final String API_URL = "";
    public static final String BASE_URL = "";
    public static final String IMAGES = "";
    public static final String COVERS = "";
    public static final String PACKAGE = "org.zgram.messenger";
    public static final String GET_CHANNELS = "";
    public static final String GET_CHANNELSALFA = "";
    public static final String POST_ADMIN = "";
    public static final String POST_CHANNELS = "";
    public static final String POST_CHATROOMS = "";
    public static final String DELETE_A_CHANNEL = "";
    public static final String DELETE_A_CHATROOM = "";

    public static final String GET_THEMES = API_URL + "themes"; // GET

    public static final String THEMES_BASE_URL = BASE_URL + "themes/";

    public static final String OFFICIALCHANNEL_URL = "https://telegram.me/avaalgram";
	 public static final String OFFICIALCHANNEL_ID = "avaalgram";
    public static final String SPAMBOT_URL = "https://telegram.me/SpamBot";
    //public static final String OFFICIAL_ID_URL = "https://mougram.me/a_g_it";
    //public static final String DEVELOPER_ID = "asia-it";

    //public static final String WebSite = "http://tablighit.ir/";

    public static ArrayList<TLRPC.TL_dialog> dialogsCats = new ArrayList();
    public static final int REPORT_BOT_ID = 178220800;
    public static  boolean AnalyticInitialized = false ;


   // public static final String REPORT = "http://app.i-gram.ir/application_v2/v1/reportChannel";



 //  public static void setCats(int code) {
 //      catDBAdapter catDB = new catDBAdapter(ApplicationLoader.getInstance());
 //      catDB.open();
 //      dialogsCats.clear();
 //      for (int i = 0; i < MessagesController.getInstance().dialogs.size(); i++) {
 //          TLRPC.TL_dialog d = (TLRPC.TL_dialog) MessagesController.getInstance().dialogs.get(i);
 //          if (catDB.isCategoried((int) d.id) == code) {
 //              dialogsCats.add(d);
 //          }
 //      }
 //      catDB.close();
 //  }


}


