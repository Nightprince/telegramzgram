/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2017.
 */

package org.zgram.ui.ActionBar;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.zgram.messenger.AndroidUtilities;
import org.zgram.messenger.ApplicationLoader;
import org.zgram.messenger.FileLog;
import org.zgram.tgnet.ConnectionsManager;

public class BaseFragment {

    private boolean isFinished = false;
    protected Dialog visibleDialog = null;

    protected View fragmentView;
    protected ActionBarLayout parentLayout;
    protected ActionBar actionBar;
    protected int classGuid = 0;
    protected Bundle arguments;
    protected boolean swipeBackEnabled = true;
    protected boolean hasOwnBackground = false;

    public BaseFragment() {
        classGuid = ConnectionsManager.getInstance().generateClassGuid();
    }

    public BaseFragment(Bundle args) {
        arguments = args;
        classGuid = ConnectionsManager.getInstance().generateClassGuid();
    }

    public ActionBar getActionBar() {
        return actionBar;
    }

    public View getFragmentView() {
        return fragmentView;
    }








    public Dialog showDialog2(Dialog dialog) {

        if (dialog == null || parentLayout == null || parentLayout.animationInProgress || parentLayout.startedTracking || parentLayout.checkTransitionAnimation()) {
            return null;
        }
        try {

            if (visibleDialog != null) {
                visibleDialog.dismiss();
                visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e( e);
        }
        try {
            visibleDialog = dialog;
            visibleDialog.setCanceledOnTouchOutside(true);
            visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    onDialogDismiss(visibleDialog);
                    visibleDialog = null;
                }
            });
            visibleDialog.show();
            //Log.e("BaseFragment","showDialog " + allowInTransition);
            //Always after .show()
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, AndroidUtilities.THEME_PREFS_MODE);
            int color = preferences.getInt("dialogColor", preferences.getInt(("themeColor"), Theme.getColor(Theme.key_actionBarDefault)));
            int id = visibleDialog.getContext().getResources().getIdentifier("android:id/alertTitle", null, null);
            TextView tv = (TextView) visibleDialog.findViewById(id);
            if (tv != null) tv.setTextColor(color);
            id = visibleDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
            View divider = visibleDialog.findViewById(id);
            if (divider != null) divider.setBackgroundColor(color);

            Button btn = (Button) visibleDialog.findViewById(android.R.id.button1);
            if (btn != null) btn.setTextColor(color);
            btn = (Button) visibleDialog.findViewById(android.R.id.button2);
            if (btn != null) btn.setTextColor(color);
            btn = (Button) visibleDialog.findViewById(android.R.id.button3);
            if (btn != null) btn.setTextColor(color);
            int bgColor = preferences.getInt("prefBGColor", 0xffffffff);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            /*visibleDialog.getWindow().setBackgroundDrawable(new ColorDrawable(bgColor));
            int tColor = preferences.getInt("prefTitleColor", 0xff212121);
            tColor = 0xffff0000;
            tv = (TextView) visibleDialog.findViewById(android.R.id.text1);
            if(tv != null)tv.setTextColor(tColor);
            tv = (TextView) visibleDialog.findViewById(android.R.id.text2);
            if(tv != null)tv.setTextColor(tColor);
            id = visibleDialog.getContext().getResources().getIdentifier("android:id/message", null, null);
            tv = (TextView) visibleDialog.findViewById(id);
            if(tv != null)tv.setTextColor(tColor);*/

            //
            return visibleDialog;
        } catch (Exception e) {
            FileLog.e( e);
        }
        return null;
    }






    public View createView(Context context) {
        return null;
    }

    public Bundle getArguments() {
        return arguments;
    }

    protected void clearViews() {
        if (fragmentView != null) {
            ViewGroup parent = (ViewGroup) fragmentView.getParent();
            if (parent != null) {
                try {
                    onRemoveFromParent();
                    parent.removeView(fragmentView);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            fragmentView = null;
        }
        if (actionBar != null) {
            ViewGroup parent = (ViewGroup) actionBar.getParent();
            if (parent != null) {
                try {
                    parent.removeView(actionBar);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            actionBar = null;
        }
        parentLayout = null;
    }

    protected void onRemoveFromParent() {

    }

    protected void setParentLayout(ActionBarLayout layout) {
        if (parentLayout != layout) {
            parentLayout = layout;
            if (fragmentView != null) {
                ViewGroup parent = (ViewGroup) fragmentView.getParent();
                if (parent != null) {
                    try {
                        onRemoveFromParent();
                        parent.removeView(fragmentView);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                if (parentLayout != null && parentLayout.getContext() != fragmentView.getContext()) {
                    fragmentView = null;
                }
            }
            if (actionBar != null) {
                boolean differentParent = parentLayout != null && parentLayout.getContext() != actionBar.getContext();
                if (actionBar.getAddToContainer() || differentParent) {
                    ViewGroup parent = (ViewGroup) actionBar.getParent();
                    if (parent != null) {
                        try {
                            parent.removeView(actionBar);
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                }
                if (differentParent) {
                    actionBar = null;
                }
            }
            if (parentLayout != null && actionBar == null) {
                actionBar = createActionBar(parentLayout.getContext());
                actionBar.parentFragment = this;
            }
        }
    }

    protected ActionBar createActionBar(Context context) {
        ActionBar actionBar = new ActionBar(context);
        actionBar.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
        actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSelector), false);
        actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarActionModeDefaultSelector), true);
        actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultIcon), false);
        actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon), true);
        return actionBar;
    }

    public void finishFragment() {
        finishFragment(true);
    }

    public void finishFragment(boolean animated) {
        if (isFinished || parentLayout == null) {
            return;
        }
        parentLayout.closeLastFragment(animated);
    }

    public void removeSelfFromStack() {
        if (isFinished || parentLayout == null) {
            return;
        }
        parentLayout.removeFragmentFromStack(this);
    }

    public boolean onFragmentCreate() {
        return true;
    }

    public void onFragmentDestroy() {
        ConnectionsManager.getInstance().cancelRequestsForGuid(classGuid);
        isFinished = true;
        if (actionBar != null) {
            actionBar.setEnabled(false);
        }
    }

    public boolean needDelayOpenAnimation() {
        return false;
    }

    public void onResume() {

    }

    public void onPause() {
        if (actionBar != null) {
            actionBar.onPause();
        }
        try {
            if (visibleDialog != null && visibleDialog.isShowing() && dismissDialogOnPause(visibleDialog)) {
                visibleDialog.dismiss();
                visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public BaseFragment getFragmentForAlert(int offset) {
        if (parentLayout == null || parentLayout.fragmentsStack.size() <= 1 + offset) {
            return this;
        }
        return parentLayout.fragmentsStack.get(parentLayout.fragmentsStack.size() - 2 - offset);
    }

    public void onConfigurationChanged(android.content.res.Configuration newConfig) {

    }

    public boolean onBackPressed() {
        return true;
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {

    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {

    }

    public void saveSelfArgs(Bundle args) {

    }

    public void restoreSelfArgs(Bundle args) {

    }

    public boolean presentFragment(BaseFragment fragment) {
        return parentLayout != null && parentLayout.presentFragment(fragment);
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast) {
        return parentLayout != null && parentLayout.presentFragment(fragment, removeLast);
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation) {
        return parentLayout != null && parentLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, true);
    }

    public Activity getParentActivity() {
        if (parentLayout != null) {
            return parentLayout.parentActivity;
        }
        return null;
    }

    public void startActivityForResult(final Intent intent, final int requestCode) {
        if (parentLayout != null) {
            parentLayout.startActivityForResult(intent, requestCode);
        }
    }

    public void dismissCurrentDialig() {
        if (visibleDialog == null) {
            return;
        }
        try {
            visibleDialog.dismiss();
            visibleDialog = null;
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        return true;
    }

    public void onBeginSlide() {
        try {
            if (visibleDialog != null && visibleDialog.isShowing()) {
                visibleDialog.dismiss();
                visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (actionBar != null) {
            actionBar.onPause();
        }
    }

    protected void onTransitionAnimationStart(boolean isOpen, boolean backward) {

    }

    protected void onTransitionAnimationEnd(boolean isOpen, boolean backward) {

    }

    protected void onBecomeFullyVisible() {

    }

    protected AnimatorSet onCustomTransitionAnimation(boolean isOpen, final Runnable callback) {
        return null;
    }

    public void onLowMemory() {

    }

    public Dialog showDialog(Dialog dialog) {
        return showDialog(dialog, false, null);
    }

    public Dialog showDialog(Dialog dialog, Dialog.OnDismissListener onDismissListener) {
        return showDialog(dialog, false, onDismissListener);
    }

    public Dialog showDialog(Dialog dialog, boolean allowInTransition, final Dialog.OnDismissListener onDismissListener) {
        if (dialog == null || parentLayout == null || parentLayout.animationInProgress || parentLayout.startedTracking || !allowInTransition && parentLayout.checkTransitionAnimation()) {
            return null;
        }
        try {
            if (visibleDialog != null) {
                visibleDialog.dismiss();
                visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            visibleDialog = dialog;
            visibleDialog.setCanceledOnTouchOutside(true);
            visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (onDismissListener != null) {
                        onDismissListener.onDismiss(dialog);
                    }
                    onDialogDismiss(visibleDialog);
                    visibleDialog = null;
                }
            });
            visibleDialog.show();
            return visibleDialog;
        } catch (Exception e) {
            FileLog.e(e);
        }
        return null;
    }

    protected void onDialogDismiss(Dialog dialog) {

    }

    public Dialog getVisibleDialog() {
        return visibleDialog;
    }

    public void setVisibleDialog(Dialog dialog) {
        visibleDialog = dialog;
    }

    public boolean extendActionMode(Menu menu) {
        return false;
    }

    public ThemeDescription[] getThemeDescriptions() {
        return null;
    }
}
