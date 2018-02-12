package edit.Uplist;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;

import org.zgram.messenger.AndroidUtilities;
import org.zgram.messenger.ContactsController;
import org.zgram.messenger.MessagesController;
import org.zgram.messenger.support.widget.LinearLayoutManager;
import org.zgram.messenger.support.widget.RecyclerView;
import org.zgram.tgnet.TLRPC;
import org.zgram.ui.ActionBar.BaseFragment;
import org.zgram.ui.ChatActivity;
import org.zgram.ui.Components.LayoutHelper;
import org.zgram.ui.Components.RecyclerListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edit.CustomHintDialogCell;
import org.zgram.messenger.R;


public class SlidingBar extends FrameLayout {

    public static SlidingDrawer slidingDrawer;
    private ImageView slide_image;
    private RecyclerListView mRecyclerView;
    private lastChatAdapter madapter;


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SlidingBar(final Context context, final BaseFragment fragment) {
        super(context);
        RecyclerListView horizontalListView = new RecyclerListView(context) {
            @Override
            public boolean onInterceptTouchEvent(MotionEvent e) {
                if (getParent() != null && getParent().getParent() != null) {
                    getParent().getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onInterceptTouchEvent(e);
            }
        };
        horizontalListView.setTag(9);
        horizontalListView.setItemAnimator(null);
        horizontalListView.setLayoutAnimation(null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        horizontalListView.setLayoutManager(layoutManager);
        //horizontalListView.setDisallowInterceptTouchEvents(true);
        horizontalListView.setAdapter(new CategoryAdapterRecycler(context));
        horizontalListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                didPressedOnSubDialog((Integer) view.getTag(), fragment);

            }
        });
        horizontalListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {
            @Override
            public boolean onItemClick(View view, int position) {

                return true;
            }
        });

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        slidingDrawer = (SlidingDrawer) inflater.inflate(R.layout.sliding_drawer, this, false);
        slide_image = (ImageView) slidingDrawer.findViewById(R.id.slide_image);




        slidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                slide_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_close_bar));
            }
        });
        slidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                slide_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_open_bar));
            }
        });

        addView(slidingDrawer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            horizontalListView.setRotation(180);
        }

        LinearLayout slideContent = (LinearLayout) slidingDrawer.findViewById(R.id.content);
//
        slideContent.addView(horizontalListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER));


    }

    public SlidingBar(Context context) {
        super(context);
    }


    private class CategoryAdapterRecycler extends RecyclerView.Adapter {

        Context context;
        List<TLRPC.TL_dialog> dialogs = new ArrayList<>();


        public CategoryAdapterRecycler(Context context) {
            this.context = context;
            dialogs = MessagesController.getInstance().dialogs;
            sortDefault((ArrayList<TLRPC.TL_dialog>) dialogs);
        }

        public void setIndex(int value) {
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new CustomHintDialogCell(context);
            view.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(65), AndroidUtilities.dp(80)));
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            CustomHintDialogCell cell = (CustomHintDialogCell) holder.itemView;

            TLRPC.TL_dialog dialog = dialogs.get(position);
            TLRPC.Chat chat = null;
            TLRPC.User user = null;
            int did = 0;
            did = (int) dialog.id;

            if (dialog.id < 0) {
                chat = MessagesController.getInstance().getChat(-(int) dialog.id);
            } else {
                chat = MessagesController.getInstance().getChat((int) dialog.id);
                user = MessagesController.getInstance().getUser((int) dialog.id);
            }
            cell.setTag(did);
            String name = "";

            if (user != null) {
                name = ContactsController.formatName(user.first_name, user.last_name);
            } else if (chat != null) {
                name = chat.title;
            }

            cell.setDialog(did, true, name);
        }

        @Override
        public int getItemCount() {
            return dialogs.size();
        }
    }

    private class Holder extends RecyclerView.ViewHolder {

        public Holder(View itemView) {
            super(itemView);
        }
    }


    public void didPressedOnSubDialog(int did, BaseFragment fragment) {

        Bundle args = new Bundle();
        if (did > 0) {
            args.putInt("user_id", did);
        } else {
            args.putInt("chat_id", -did);
        }
        fragment.presentFragment(new ChatActivity(args));


    }


    private void sortDefault(ArrayList<TLRPC.TL_dialog> dialogs) {
        Collections.sort(dialogs, new Comparator<TLRPC.TL_dialog>() {
            @Override
            public int compare(TLRPC.TL_dialog dialog, TLRPC.TL_dialog dialog2) {
                if (dialog.last_message_date == dialog2.last_message_date) {
                    return 0;
                } else if (dialog.last_message_date < dialog2.last_message_date) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }
}
