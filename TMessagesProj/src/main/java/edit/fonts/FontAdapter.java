package edit.fonts;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.zgram.messenger.R;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.zgram.messenger.ApplicationLoader;
import org.zgram.messenger.LocaleController;
import org.zgram.messenger.Utilities;

public class FontAdapter extends ArrayAdapter<font> {
    Context context;

    public FontAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public FontAdapter(Context context, int resource, List<font> items) {
        super(context, resource, items);
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.font_row, null);
        }
        final font myFont = (font) getItem(position);
        if (myFont != null) {
            TextView name = (TextView) v.findViewById(R.id.font);
            String assetPath = "fonts/" + myFont.getAddress();
            name.setText(myFont.getName());
            name.setTextSize(18.0f);
            name.setTypeface(Typeface.createFromAsset(ApplicationLoader.applicationContext.getAssets(), assetPath));
            name.setOnClickListener(new OnClickListener() {

                class C09411 implements DialogInterface.OnClickListener {
                    C09411() {
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putString("myFont2", myFont.getAddress()).commit();
                        Utilities.restartApp();
                    }
                }

                public void onClick(View v) {
                    Builder builder = new Builder(FontAdapter.this.context);
                    builder.setTitle(FontAdapter.this.context.getResources().getString(R.string.FontChange));
                    builder.setMessage(LocaleController.getString("FontApplied", R.string.FontApplied) + IOUtils.LINE_SEPARATOR_UNIX + LocaleController.getString("ClickOkToRestart", R.string.ClickOkToRestart));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C09411());
                    builder.show();
                }
            });
        }
        return v;
    }
}
