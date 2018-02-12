package edit;

import java.io.IOException;
import java.io.InputStream;
import org.zgram.messenger.ApplicationLoader;

public class JsonFileHelper {
    public static String loadJSONFromAsset(String path) {
        try {
            InputStream is = ApplicationLoader.applicationContext.getAssets().open(path);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            return json;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
