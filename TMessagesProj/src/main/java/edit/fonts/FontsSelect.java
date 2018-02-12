package edit.fonts;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import org.zgram.messenger.R;
import edit.urlco;
import java.util.ArrayList;
import java.util.List;
import org.zgram.messenger.AndroidUtilities;
import org.zgram.messenger.ApplicationLoader;
import org.zgram.messenger.LocaleController;
import org.zgram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.zgram.ui.ActionBar.BaseFragment;
import org.zgram.ui.ActionBar.Theme;
import org.zgram.ui.ActionBar.ThemeDescription;
import org.zgram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.zgram.ui.Cells.LoadingCell;
import org.zgram.ui.Cells.ProfileSearchCell;
import org.zgram.ui.Cells.TextInfoPrivacyCell;

public class FontsSelect extends BaseFragment {
    private List<font> Fonts = new ArrayList();
    private ListView listView;

    class C09431 extends ActionBarMenuOnItemClick {
        C09431() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                FontsSelect.this.finishFragment();
            }
        }
    }

    class C09442 implements ThemeDescriptionDelegate {
        C09442() {
        }

        public void didSetColor(int color) {
            int count = FontsSelect.this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = FontsSelect.this.listView.getChildAt(a);
                if (child instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) child).update(0);
                }
            }
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
        this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR, false);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("FontChange", R.string.FontChange));
        this.actionBar.setActionBarMenuOnItemClick(new C09431());
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setLayoutParams(new LayoutParams(-1, -1));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.listView = new ListView(context);
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0);
        this.listView.setBackgroundColor(preferences.getInt("prefBGColor", -1));
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setVerticalScrollBarEnabled(false);
        int hColor = preferences.getInt("prefHeaderColor", preferences.getInt("themeColor", AndroidUtilities.defColor));
        frameLayout.addView(this.listView);
        LayoutParams layoutParams = (LayoutParams) this.listView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 48;
        this.listView.setLayoutParams(layoutParams);
        initFonts(context);
        return this.fragmentView;
    }

    private void initFonts(Context context) {
        this.Fonts.add(new font(context.getResources().getString(R.string.DefaultFont), "rmedium.ttf"));
        this.Fonts.add(new font(context.getResources().getString(R.string.IranSansLight), "iransans_light.ttf"));
        this.Fonts.add(new font(context.getResources().getString(R.string.IranSans), "iransans.ttf"));
        this.Fonts.add(new font(context.getResources().getString(R.string.IranSansMedium), "iransans_medium.ttf"));
        this.Fonts.add(new font(context.getResources().getString(R.string.IranSansBold), "iransans_bold.ttf"));
        this.Fonts.add(new font(context.getResources().getString(R.string.Yekan), "byekan.ttf"));
        this.Fonts.add(new font(context.getResources().getString(R.string.Homa), "hama.ttf"));
        this.Fonts.add(new font(context.getResources().getString(R.string.Handwrite), "dastnevis.ttf"));
        this.Fonts.add(new font(context.getResources().getString(R.string.Morvarid), "morvarid.ttf"));
        this.Fonts.add(new font(context.getResources().getString(R.string.Afsaneh), "afsaneh.ttf"));
        refreshDisplay(context);
    }

    private void refreshDisplay(Context context) {
        this.listView.setAdapter(new FontAdapter(context, R.layout.theme_row, this.Fonts));
    }

    public void onResume() {
        super.onResume();
        if (urlco.AnalyticInitialized) {
            ApplicationLoader.getInstance().trackScreenView("Font Select Activity");
        }
    }

    private void updateTheme() {
        SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, 0);
        this.actionBar.setBackgroundColor(themePrefs.getInt("prefHeaderColor", themePrefs.getInt("themeColor", AndroidUtilities.defColor)));
        this.actionBar.setTitleColor(themePrefs.getInt("prefHeaderTitleColor", -1));
        Drawable back = getParentActivity().getResources().getDrawable(R.drawable.ic_ab_back);
        back.setColorFilter(themePrefs.getInt("prefHeaderIconsColor", -1), Mode.MULTIPLY);
        this.actionBar.setBackButtonDrawable(back);
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate сellDelegate = new C09442();
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[23];
        themeDescriptionArr[0] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[1] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[5] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[6] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[7] = new ThemeDescription(this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[9] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, null, Theme.key_chats_verifiedCheck);
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedDrawable}, null, Theme.key_chats_verifiedBackground);
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_offlinePaint, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_onlinePaint, null, null, Theme.key_windowBackgroundWhiteBlueText3);
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_namePaint, null, null, Theme.key_chats_name);
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable}, null, Theme.key_avatar_text);
        themeDescriptionArr[16] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundRed);
        themeDescriptionArr[17] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundOrange);
        themeDescriptionArr[18] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundViolet);
        themeDescriptionArr[19] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundGreen);
        themeDescriptionArr[20] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundCyan);
        themeDescriptionArr[21] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[22] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundPink);
        return themeDescriptionArr;
    }
}
