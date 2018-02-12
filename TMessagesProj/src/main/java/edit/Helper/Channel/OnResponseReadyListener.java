package edit.Helper.Channel;

import org.json.JSONObject;

/**
 * Created by Pouya on 12/6/2016.
 */
public interface OnResponseReadyListener {

    void OnResponseReady(boolean error, JSONObject data, String message);
}
