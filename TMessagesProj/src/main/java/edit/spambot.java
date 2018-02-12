package edit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import org.zgram.messenger.BuildConfig;
import org.zgram.messenger.R;

import org.zgram.messenger.AndroidUtilities;
import org.zgram.messenger.ApplicationLoader;
import org.zgram.messenger.BuildVars;
import org.zgram.messenger.LocaleController;
import org.zgram.ui.ActionBar.ActionBar;
import org.zgram.ui.ActionBar.BaseFragment;
import org.zgram.ui.ActionBar.Theme;
import org.zgram.ui.Components.LayoutHelper;


public class spambot extends BaseFragment {


    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();


        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    @Override
    public View createView(final Context context) {
        actionBar.setBackgroundColor(Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
        actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR,true);
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("ReportElimination", R.string.ReportElimination));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        fragmentView = new FrameLayout(context);
        fragmentView.setLayoutParams(new FrameLayout.LayoutParams(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        fragmentView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_spambot, null);

        Button btnReport = (Button) fragmentView.findViewById(R.id.btnReport);

         btnReport.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String link = "http://t.me/SpamBot";
        Intent telegram = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        if (!BuildConfig.DEBUG) {
            telegram.setPackage(BuildVars.BUILD_PACKAGENAME);
        } else
            telegram.setPackage(BuildVars.BUILD_PACKAGENAME + ".beta");
        getParentActivity().startActivity(telegram);



    }
});








        return fragmentView;
    }




    @Override
    public void onResume() {
        super.onResume();
        updateTheme();
        if (urlco.AnalyticInitialized)
           ApplicationLoader.getInstance().trackScreenView("ReportElimination");
    }

    private void updateTheme() {
        SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, AndroidUtilities.THEME_PREFS_MODE);
        int def = themePrefs.getInt("themeColor", Theme.getColor(Theme.key_actionBarDefault));
        actionBar.setBackgroundColor(themePrefs.getInt("prefHeaderColor", def));
        actionBar.setTitleColor(themePrefs.getInt("prefHeaderTitleColor", 0xffffffff));

        Drawable back = getParentActivity().getResources().getDrawable(R.drawable.ic_ab_back);
        back.setColorFilter(themePrefs.getInt("prefHeaderIconsColor", 0xffffffff), PorterDuff.Mode.MULTIPLY);
        actionBar.setBackButtonDrawable(back);
    }


}
