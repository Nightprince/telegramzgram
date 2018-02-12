package edit.Helper.Packet;

import org.json.JSONException;
import org.json.JSONObject;
import org.zgram.messenger.ApplicationLoader;
import org.zgram.messenger.UserConfig;
import org.zgram.messenger.volley.AuthFailureError;
import org.zgram.messenger.volley.Request;
import org.zgram.messenger.volley.RequestQueue;
import org.zgram.messenger.volley.Response;
import org.zgram.messenger.volley.VolleyError;
import org.zgram.messenger.volley.toolbox.StringRequest;
import org.zgram.messenger.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import edit.Helper.UrlController;
import edit.Setting;

/**
 * Created by Pouya on 11/16/2016.
 */
public class SendRegidPacket {


    public void Send() {
        RequestQueue queue = Volley.newRequestQueue(ApplicationLoader.applicationContext);


        String url = UrlController.SERVERADD + "setregid.php";

        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.e("errstr", response);

                        try {
                            JSONObject js = new JSONObject(response);
                            if(js.getInt("done")==1){
                                Setting.RegidSended();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }


                        return;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            //Log.e("Response", error.getMessage());
                        } catch (Exception e) {

                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map=new HashMap<>();
                map.put("phone", UserConfig.getCurrentUser().phone);
                map.put("regid", Setting.getRegId());
                return map;
            }
        };
        queue.add(strRequest);
    }



}
