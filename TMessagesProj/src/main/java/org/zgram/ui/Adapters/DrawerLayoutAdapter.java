/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2017.
 */

package org.zgram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.zgram.messenger.AndroidUtilities;
import org.zgram.messenger.LocaleController;
import org.zgram.messenger.MessagesController;
import org.zgram.messenger.R;
import org.zgram.messenger.UserConfig;
import org.zgram.messenger.support.widget.RecyclerView;
import org.zgram.ui.ActionBar.Theme;
import org.zgram.ui.Cells.DrawerActionCell;
import org.zgram.ui.Cells.DividerCell;
import org.zgram.ui.Cells.EmptyCell;
import org.zgram.ui.Cells.DrawerProfileCell;
import org.zgram.ui.Components.RecyclerListView;

import java.util.ArrayList;

public class DrawerLayoutAdapter extends RecyclerListView.SelectionAdapter {

    private Context mContext;

    private ArrayList<Item> items = new ArrayList<>(15);

    public DrawerLayoutAdapter(Context context) {
        mContext = context;
        Theme.createDialogsResources(context);
        resetItems();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void notifyDataSetChanged() {
        resetItems();
        super.notifyDataSetChanged();
    }

    @Override
    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return holder.getItemViewType() == 3;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = new DrawerProfileCell(mContext);
                break;
            case 1:
            default:
                view = new EmptyCell(mContext, AndroidUtilities.dp(8));
                break;
            case 2:
                view = new DividerCell(mContext);
                break;
            case 3:
                view = new DrawerActionCell(mContext);
                break;
        }
        view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new RecyclerListView.Holder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                ((DrawerProfileCell) holder.itemView).setUser(MessagesController.getInstance().getUser(UserConfig.getClientUserId()));
                holder.itemView.setBackgroundColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
                break;
            case 3:
                items.get(position).bind((DrawerActionCell) holder.itemView);
                break;
        }
    }

    @Override
    public int getItemViewType(int i) {
        if (i == 0) {
            return 0;
        } else if (i == 1) {
            return 1;
        } else if (i == 5) {
            return 2;
        }
        return 3;
    }

    private void resetItems() {
        items.clear();
        if (!UserConfig.isClientActivated()) {
            return;
        }
        items.add(null); // profile
        items.add(null); // padding
        items.add(new Item(28, LocaleController.getString("Change", R.string.Change), R.drawable.ic_phone_android_white_24dp));
        items.add(new Item(3, LocaleController.getString("NewSecretChat", R.string.NewSecretChat), R.drawable.menu_secret));
        items.add(new Item(4, LocaleController.getString("NewChannel", R.string.NewChannel), R.drawable.menu_broadcast));
        items.add(null); // divider
        //items.add(new Item(16, LocaleController.getString("Member", R.string.buymember), R.drawable.group_admin));
        items.add(new Item(2, LocaleController.getString("NewGroup", R.string.NewGroup), R.drawable.menu_newgroup));
        items.add(new Item(6, LocaleController.getString("Contacts", R.string.Contacts), R.drawable.menu_contacts));
        if (MessagesController.getInstance().callsEnabled) {
            items.add(new Item(10, LocaleController.getString("Calls", R.string.Calls), R.drawable.menu_calls));
        }
        items.add(new Item(19, LocaleController.getString("OnlineContact", R.string.OnlineContacts), R.drawable.ic_mood_white_24dp));
        items.add(new Item(20, LocaleController.getString("SpecificContacts", R.string.SpecificContacts), R.drawable.menu_contacts));
        items.add(new Item(14, LocaleController.getString("contactChanges", R.string.contactChanges), R.drawable.menu_contacts));
        items.add(new Item(26, LocaleController.getString("DeleteContact", R.string.DeleteContact), R.drawable.menu_contacts));
        items.add(new Item(12, LocaleController.getString("UserFinder", R.string.UsernameFinder), R.drawable.menu_contacts));
        items.add(new Item(22, LocaleController.getString("FileManager", R.string.FileManager), R.drawable.ic_ab_attach));
        items.add(new Item(21, LocaleController.getString("DownloadManager", R.string.DownloadManager), R.drawable.ic_cloud_download_white_24dp));
        items.add(new Item(25, LocaleController.getString("ZibaNvis", R.string.SelectFont), R.drawable.ic_font_download_white_24dp));
        items.add(new Item(23, LocaleController.getString("Category", R.string.category), R.drawable.ic_widgets_white_24dp));
        items.add(new Item(17, LocaleController.getString("Monshi", R.string.Secretary), R.drawable.ic_mood_white_24dp));
        items.add(new Item(15, LocaleController.getString("Theme", R.string.Theme), R.drawable.ic_color_lens_white_24dp));
        items.add(new Item(13, LocaleController.getString("Cleaner", R.string.Clearcache), R.drawable.chats_clear));
        items.add(new Item(18, LocaleController.getString("UnReport", R.string.ReportElimination), R.drawable.ic_block_white_24dp));

        items.add(new Item(7, LocaleController.getString("InviteFriends", R.string.InviteFriends), R.drawable.menu_invite));
        items.add(new Item(24, LocaleController.getString("Settings", R.string.PlusSettings), R.drawable.ic_settings_applications_white_24dp));
        items.add(new Item(8, LocaleController.getString("Settings", R.string.Settings), R.drawable.menu_settings));
        items.add(new Item(9, LocaleController.getString("TelegramFaq", R.string.TelegramFaq), R.drawable.menu_help));
    }

    public int getId(int position) {
        if (position < 0 || position >= items.size()) {
            return -1;
        }
        Item item = items.get(position);
        return item != null ? item.id : -1;
    }

    private class Item {
        public int icon;
        public String text;
        public int id;

        public Item(int id, String text, int icon) {
            this.icon = icon;
            this.id = id;
            this.text = text;
        }

        public void bind(DrawerActionCell actionCell) {
            actionCell.setTextAndIcon(text, icon);
        }
    }
}
