package edit.acc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.zgram.messenger.R;
import org.zgram.messenger.AndroidUtilities;
import org.zgram.messenger.LocaleController;
import org.zgram.ui.ActionBar.Theme;
import org.zgram.ui.Components.LayoutHelper;

public class AccountCell extends FrameLayout {
    private AccountsController.AppAccount account;
    private ImageView imageView;
    private boolean needDivider;
    private Rect rect = new Rect();
    private ImageView removeButton;
    private ImageView settingsButton;
    private TextView textView;
    private TextView valueTextView;

    public AccountCell(Context context) {
        super(context);
        int i;
        int i2;
        float f;
        int i3 = 3;
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        View view = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i, LocaleController.isRTL ? 85.0f : 71.0f, 10.0f, LocaleController.isRTL ? 71.0f : 85.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(2);
        this.valueTextView.setMaxLines(2);
        TextView textView = this.valueTextView;
        if (LocaleController.isRTL) {
            i2 = 5;
        } else {
            i2 = 3;
        }
        textView.setGravity(i2);
        view = this.valueTextView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i, LocaleController.isRTL ? 85.0f : 71.0f, 35.0f, LocaleController.isRTL ? 71.0f : 85.0f, 0.0f));
        this.imageView = new ImageView(context);
        this.imageView.setScaleType(ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), Mode.MULTIPLY));
        View view2 = this.imageView;
        if (LocaleController.isRTL) {
            i2 = 5;
        } else {
            i2 = 3;
        }
        int i4 = i2 | 16;
        float f2 = LocaleController.isRTL ? 0.0f : 16.0f;
        if (LocaleController.isRTL) {
            f = 16.0f;
        } else {
            f = 0.0f;
        }
        addView(view2, LayoutHelper.createFrame(-2, -2.0f, i4, f2, 0.0f, f, 0.0f));
        this.settingsButton = new ImageView(context);
        this.settingsButton.setFocusable(false);
        this.settingsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector)));
        this.settingsButton.setImageResource(R.drawable.ic_settings);
        this.settingsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), Mode.MULTIPLY));
        this.settingsButton.setScaleType(ScaleType.CENTER);
        View view3 = this.settingsButton;
        if (!LocaleController.isRTL) {
            i3 = 5;
        }
        addView(view3, LayoutHelper.createFrame(40, 40, i3 | 16));
        this.removeButton = new ImageView(context);
        this.removeButton.setFocusable(false);
        this.removeButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector)));
        this.removeButton.setImageResource(R.drawable.ic_ab_delete);
        this.removeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), Mode.MULTIPLY));
        this.removeButton.setScaleType(ScaleType.CENTER);
        if (LocaleController.isRTL) {
            addView(this.removeButton, LayoutHelper.createFrame(40, 40.0f, 19, 45.0f, 0.0f, 10.0f, 0.0f));
        } else {
            addView(this.removeButton, LayoutHelper.createFrame(40, 40.0f, 21, 10.0f, 0.0f, 45.0f, 0.0f));
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), 1073741824), MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.dp(90.0f), 1073741824));
    }

    public void setIsActive(boolean value) {
        if (value) {
            Drawable drawable;
            Drawable drawable2 = getContext().getResources().getDrawable(R.drawable.verified_area);
            drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), Mode.MULTIPLY));
            this.textView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
            if (LocaleController.isRTL) {
                drawable = drawable2;
            } else {
                drawable = null;
            }
            if (LocaleController.isRTL) {
                drawable2 = null;
            }
            this.textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, drawable2, null);
            this.removeButton.setVisibility(0);
            return;
        }
        this.textView.setCompoundDrawables(null, null, null, null);
        this.removeButton.setVisibility(8);
    }

    public void setAccount(int position, AccountsController.AppAccount account, boolean divider) {
        CharSequence charSequence;
        String string;
        this.needDivider = divider;
        this.account = account;
        if (account.name != null) {
            charSequence = (position + 1) + " - " + account.name;
        } else {
            charSequence = (position + 1) + " - " + (account.robotName == null ? account.number : account.robotName);
        }
        this.textView.setText(charSequence);
        StringBuilder stringBuilder = new StringBuilder();
        if (account.publicFolder) {
            string = LocaleController.getString("AppAccountFolderPublicRow", R.string.AppAccountFolderPublicRow);
        } else {
            string = LocaleController.formatString("AppAccountFolderRow", R.string.AppAccountFolderRow, new Object[]{"User" + account.id});
        }
        stringBuilder = stringBuilder.append(string).append("\n");
        if (account.autoSync) {
            string = LocaleController.getString("AppAccountAutoSyncEnable", R.string.AppAccountAutoSyncEnable);
        } else {
            string = LocaleController.getString("AppAccountAutoSyncDisable", R.string.AppAccountAutoSyncDisable);
        }
        this.valueTextView.setText(stringBuilder.append(string).toString());
        this.imageView.setImageResource(account.autoSync ? R.drawable.ic_phonelink_setup_black_24dp : R.drawable.menu_calls);
    }

    public void setOnSettingsClick(OnClickListener listener) {
        this.settingsButton.setOnClickListener(listener);
    }

    public void setOnRemoveClick(OnClickListener listener) {
        this.removeButton.setOnClickListener(listener);
    }

    public AccountsController.AppAccount getAccount() {
        return this.account;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (VERSION.SDK_INT >= 21 && getBackground() != null) {
            this.settingsButton.getHitRect(this.rect);
            if (this.settingsButton.getVisibility() == 0 && this.rect.contains((int) event.getX(), (int) event.getY())) {
                return true;
            }
            this.removeButton.getHitRect(this.rect);
            if (this.removeButton.getVisibility() == 0 && this.rect.contains((int) event.getX(), (int) event.getY())) {
                return true;
            }
            if (event.getAction() == 0 || event.getAction() == 2) {
                getBackground().setHotspot(event.getX(), event.getY());
            }
        }
        return super.onTouchEvent(event);
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
