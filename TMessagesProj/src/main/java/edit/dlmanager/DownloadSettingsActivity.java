package edit.dlmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.koushikdutta.ion.loader.MediaFile;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.zgram.messenger.R;
import org.zgram.messenger.ApplicationLoader;
import org.zgram.messenger.FileLog;
import org.zgram.messenger.LocaleController;
import org.zgram.messenger.support.widget.LinearLayoutManager;
import org.zgram.messenger.support.widget.RecyclerView;
import org.zgram.messenger.support.widget.RecyclerView.ViewHolder;
import org.zgram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.zgram.ui.ActionBar.BaseFragment;
import org.zgram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.zgram.ui.ActionBar.BottomSheet.Builder;
import org.zgram.ui.ActionBar.Theme;
import org.zgram.ui.Cells.CheckBoxCell;
import org.zgram.ui.Cells.TextCheckCell;
import org.zgram.ui.Cells.TextDetailSettingsCell;
import org.zgram.ui.Cells.TextSettingsCell;
import org.zgram.ui.Components.LayoutHelper;
import org.zgram.ui.Components.RecyclerListView;
import org.zgram.ui.Components.RecyclerListView.Holder;
import org.zgram.ui.Components.RecyclerListView.OnItemClickListener;
import org.zgram.ui.Components.RecyclerListView.SelectionAdapter;

import java.util.Calendar;

import edit.dlmanager.Services.DownloadReceiver;


public class DownloadSettingsActivity extends BaseFragment implements  TimePickerDialog.OnTimeSetListener {
    private int activeDaysRow;
    boolean[] days = new boolean[]{true, true, true, true, true, true, true};
    private int disableWifiRow;
    private int enableDMRow;
    private int enableWifiRow;
    private int endTimeRow;
    private int justTodayRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int rowCount = 0;
    private int startTimeRow;

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

    }

    class C34121 extends ActionBarMenuOnItemClick {
        C34121() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                finishFragment();
            }
        }
    }

    class C34172 implements OnItemClickListener {
        C34172() {
        }

        public void onItemClick(View view, final int position) {
            final SharedPreferences preferences;
            Editor editor;
            if (position == enableDMRow) {
                preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                boolean dr = preferences.getBoolean("download_receiver", false);
                if (dr) {
                    new DownloadReceiver().cancelAlarm(ApplicationLoader.applicationContext);
                }
                editor = preferences.edit();
                editor.putBoolean("download_receiver", !dr);
                editor.commit();
                if (view instanceof TextCheckCell) {
                    boolean z;
                    TextCheckCell textCheckCell = (TextCheckCell) view;
                    if (dr) {
                        z = false;
                    } else {
                        z = true;
                    }
                    textCheckCell.setChecked(z);
                }
            } else if (position == justTodayRow) {
                preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                boolean jt = preferences.getBoolean("download_just_today", true);
                editor = preferences.edit();
                editor.putBoolean("download_just_today", !jt);
                editor.commit();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(!jt);
                }
            } else if (position == activeDaysRow) {
                if (getParentActivity() != null) {
                    final boolean[] maskValues = new boolean[7];
                    Builder builder = new Builder(getParentActivity());
                    builder.setApplyTopPadding(false);
                    builder.setApplyBottomPadding(false);
                    LinearLayout linearLayout = new LinearLayout(getParentActivity());
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                    for (int a = 0; a < 7; a++) {
                        String name = null;
                        if (a == 0) {
                            name = LocaleController.getString("Saturday", R.string.Saturday);
                            maskValues[a] = preferences.getBoolean("dm_saturday", true);
                        } else if (a == 1) {
                            name = LocaleController.getString("Sunday", R.string.Sunday);
                            maskValues[a] = preferences.getBoolean("dm_sunday", true);
                        } else if (a == 2) {
                            name = LocaleController.getString("Monday", R.string.Monday);
                            maskValues[a] = preferences.getBoolean("dm_monday", true);
                        } else if (a == 3) {
                            name = LocaleController.getString("Tuesday", R.string.Tuesday);
                            maskValues[a] = preferences.getBoolean("dm_tuesday", true);
                        } else if (a == 4) {
                            name = LocaleController.getString("Wednesday", R.string.Wednesday);
                            maskValues[a] = preferences.getBoolean("dm_wednesday", true);
                        } else if (a == 5) {
                            name = LocaleController.getString("Thursday", R.string.Thursday);
                            maskValues[a] = preferences.getBoolean("dm_thursday", true);
                        } else if (a == 6) {
                            name = LocaleController.getString("Friday", R.string.Friday);
                            maskValues[a] = preferences.getBoolean("dm_friday", true);
                        }
                        CheckBoxCell checkBoxCell = new CheckBoxCell(getParentActivity(), true);
                        checkBoxCell.setTag(Integer.valueOf(a));
                        checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        checkBoxCell.setTextColor(0xff000000);
                        linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48));
                        checkBoxCell.setText(name, "", maskValues[a], true);

                        checkBoxCell.setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                CheckBoxCell cell = (CheckBoxCell) v;
                                int num = ((Integer) cell.getTag()).intValue();
                                maskValues[num] = !maskValues[num];
                                cell.setChecked(maskValues[num], true);
                            }
                        });
                    }
                    BottomSheetCell cell = new BottomSheetCell(getParentActivity(), 1);
                    cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    cell.setTextAndIcon(LocaleController.getString("Save", R.string.Save).toUpperCase(), 0);
                    cell.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                    final int i = position;
                    cell.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            try {
                                if (visibleDialog != null) {
                                    visibleDialog.dismiss();
                                }
                            } catch (Exception e) {
                                FileLog.e("tmessages", e);
                            }
                            for (int a = 0; a < 7; a++) {
                                Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                                if (a == 0) {
                                    editor.putBoolean("dm_saturday", maskValues[a]).commit();
                                } else if (a == 1) {
                                    editor.putBoolean("dm_sunday", maskValues[a]).commit();
                                } else if (a == 2) {
                                    editor.putBoolean("dm_monday", maskValues[a]).commit();
                                } else if (a == 3) {
                                    editor.putBoolean("dm_tuesday", maskValues[a]).commit();
                                } else if (a == 4) {
                                    editor.putBoolean("dm_wednesday", maskValues[a]).commit();
                                } else if (a == 5) {
                                    editor.putBoolean("dm_thursday", maskValues[a]).commit();
                                } else if (a == 6) {
                                    editor.putBoolean("dm_friday", maskValues[a]).commit();
                                }
                            }
                            if (listAdapter != null) {
                                listAdapter.notifyItemChanged(i);
                            }
                        }
                    });
                    linearLayout.addView(cell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48));
                    builder.setCustomView(linearLayout);
                    showDialog2(builder.create());
                }
            } else if (position == startTimeRow) {
                preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                        editor.putInt("download_shour", hourOfDay).commit();
                        editor.putInt("download_sminute", minute).commit();
                        saveReminder();
                        if (listAdapter != null) {
                            listAdapter.notifyItemChanged(position);
                        }
                    }


                }, preferences.getInt("download_shour", 12), preferences.getInt("download_sminute",  10), false).show(getParentActivity().getFragmentManager(), "Timepickerdialog");
            } else if (position == endTimeRow) {
                preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                        Editor editor = preferences.edit();
                        editor.putInt("download_ehour", hourOfDay).commit();
                        editor.putInt("download_eminute", minute).commit();
                        saveReminder();
                        if (listAdapter != null) {
                            listAdapter.notifyItemChanged(position);
                        }
                    }

                }, preferences.getInt("download_ehour",  8), preferences.getInt("download_eminute",  10), false).show(getParentActivity().getFragmentManager(), "Timepickerdialog_end");
            } else if (position == enableWifiRow) {
                preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                boolean ew = preferences.getBoolean("download_ewifi", false);
                editor = preferences.edit();
                editor.putBoolean("download_ewifi", !ew);
                editor.commit();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(!ew);
                }
            } else if (position == disableWifiRow) {
                preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                boolean dw = preferences.getBoolean("download_dwifi", false);
                editor = preferences.edit();
                editor.putBoolean("download_dwifi", !dw);
                editor.commit();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(!dw);
                }
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            int position = holder.getAdapterPosition();
            boolean justToday = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("download_just_today", true);
            if (position == enableDMRow || position == startTimeRow || ((position == activeDaysRow && !justToday) || position == endTimeRow || position == enableWifiRow || position == disableWifiRow || position == justTodayRow)) {
                return true;
            }
            return false;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new TextCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new TextDetailSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    view = new TextSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            switch (holder.getItemViewType()) {
                case 0:
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    if (position == enableDMRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("DownloaderEnableScheduler", R.string.DownloaderEnableScheduler), preferences.getBoolean("download_receiver", false), true);
                        return;
                    } else if (position == enableWifiRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("DownloaderEnableWifi", R.string.DownloaderEnableWifi), preferences.getBoolean("download_ewifi", false), true);
                        return;
                    } else if (position == disableWifiRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("DownloaderDisableWifi", R.string.DownloaderDisableWifi), preferences.getBoolean("download_dwifi", false), true);
                        return;
                    } else if (position == justTodayRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("DownloaderJustToday", R.string.DownloaderJustToday), preferences.getBoolean("download_just_today", true), false);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextDetailSettingsCell detailSettingsCell = (TextDetailSettingsCell) holder.itemView;
                    if (position == activeDaysRow) {
                        String text = "";
                        for (int a = 0; a < 7; a++) {
                            if (a == 0) {
                                if (preferences.getBoolean("dm_saturday", true)) {
                                    text = text + LocaleController.getString("Saturday", R.string.Saturday) + ", ";
                                }
                            } else if (a == 1) {
                                if (preferences.getBoolean("dm_sunday", true)) {
                                    text = text + LocaleController.getString("Sunday", R.string.Sunday) + ", ";
                                }
                            } else if (a == 2) {
                                if (preferences.getBoolean("dm_monday", true)) {
                                    text = text + LocaleController.getString("Monday", R.string.Monday) + ", ";
                                }
                            } else if (a == 3) {
                                if (preferences.getBoolean("dm_tuesday", true)) {
                                    text = text + LocaleController.getString("Tuesday", R.string.Tuesday) + ", ";
                                }
                            } else if (a == 4) {
                                if (preferences.getBoolean("dm_wednesday", true)) {
                                    text = text + LocaleController.getString("Wednesday", R.string.Wednesday) + ", ";
                                }
                            } else if (a == 5) {
                                if (preferences.getBoolean("dm_thursday", true)) {
                                    text = text + LocaleController.getString("Thursday", R.string.Thursday) + ", ";
                                }
                            } else if (a == 6 && preferences.getBoolean("dm_friday", true)) {
                                text = text + LocaleController.getString("Friday", R.string.Friday) + ", ";
                            }
                        }
                        StringBuilder textSB = new StringBuilder(text);
                        if (textSB.length() != 0) {
                            textSB.setCharAt(textSB.length() - 2, ' ');
                        }
                        detailSettingsCell.setTextAndValue(LocaleController.getString("DownloaderDays", R.string.DownloaderDays), String.valueOf(textSB), true);
                        detailSettingsCell.setMultilineDetail(false);
                        return;
                    }
                    return;
                case 2:
                    TextSettingsCell settingsCell = (TextSettingsCell) holder.itemView;
                    int hour;
                    int minut;
                    String time;
                    if (position == startTimeRow) {
                        hour = preferences.getInt("download_shour", 12);
                        minut = preferences.getInt("download_sminute", 10);
                        if (minut < 10) {
                            time = String.format("%d", new Object[]{Integer.valueOf(hour)}) +  String.format(":%d", new Object[]{0}) + String.format("%d", new Object[]{minut});
                        } else {
                            time = String.format("%d", new Object[]{Integer.valueOf(hour)}) + ":" + String.format("%d", new Object[]{minut});
                        }
                        settingsCell.setTextAndValue(LocaleController.getString("DownloaderStartTime", R.string.DownloaderStartTime), time, true);
                        return;
                    } else if (position == endTimeRow) {
                        hour = preferences.getInt("download_ehour",8);
                        minut = preferences.getInt("download_eminute", 10);
                        if ( minut < 10) {
                            time = String.format("%d", new Object[]{Integer.valueOf(hour)}) + String.format(":%d", new Object[]{0}) + String.format("%d", new Object[]{minut});
                        } else {
                            time = String.format("%d", new Object[]{Integer.valueOf(hour)}) + ":" + String.format("%d", new Object[]{minut});
                        }
                        settingsCell.setTextAndValue(LocaleController.getString("DownloaderEndTime", R.string.DownloaderEndTime), time, true);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemCount() {
            return rowCount;
        }

        public int getItemViewType(int position) {
            if (position == enableDMRow || position == enableWifiRow || position == disableWifiRow || position == justTodayRow) {
                return 0;
            }
            if (position == activeDaysRow) {
                return 1;
            }
            if (position == startTimeRow || position == endTimeRow) {
                return 2;
            }
            return 0;
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        int i = rowCount;
        rowCount = i + 1;
        enableDMRow = i;
        i = rowCount;
        rowCount = i + 1;
        justTodayRow = i;
        i = rowCount;
        rowCount = i + 1;
        activeDaysRow = i;
        i = rowCount;
        rowCount = i + 1;
        startTimeRow = i;
        i = rowCount;
        rowCount = i + 1;
        endTimeRow = i;
        i = rowCount;
        rowCount = i + 1;
        enableWifiRow = i;
        i = rowCount;
        rowCount = i + 1;
        disableWifiRow = i;
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("TabsSettings", R.string.TabsSettings));
        actionBar.setActionBarMenuOnItemClick(new C34121());
        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        listAdapter = new ListAdapter(context);
        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, -1.0f));
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new C34172());
        return fragmentView;
    }

    public void onResume() {
        super.onResume();
    }

  
    public void saveReminder() {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        int sHour = preferences.getInt("download_shour", 12);
        int sMinute = preferences.getInt("download_sminute", 10);
        int eHour = preferences.getInt("download_ehour", 8);
        int eMinute = preferences.getInt("download_eminute", 10);
        new DownloadReceiver().cancelAlarm(ApplicationLoader.applicationContext);
        if (preferences.getBoolean("download_just_today", true)) {
            Calendar mCalendar = Calendar.getInstance();
            Calendar mCalendarEnd = Calendar.getInstance();
            mCalendar.set(Calendar.HOUR_OF_DAY, sHour);
            mCalendar.set(Calendar.MINUTE, sMinute);
            mCalendar.set(Calendar.SECOND, 0);
            mCalendarEnd.set(Calendar.HOUR_OF_DAY, eHour);
            mCalendarEnd.set(Calendar.MINUTE, eMinute);
            mCalendarEnd.set(Calendar.SECOND, 0);
            new DownloadReceiver().setAlarm(ApplicationLoader.applicationContext, mCalendar, mCalendarEnd, 100);
            return;
        }
        if (preferences.getBoolean("dm_saturday", true)) {
            setRepeatAlarm(1, sHour, sMinute, eHour, eMinute);
        }
        if (preferences.getBoolean("dm_sunday", true)) {
            setRepeatAlarm(2, sHour, sMinute, eHour, eMinute);
        }
        if (preferences.getBoolean("dm_monday", true)) {
            setRepeatAlarm(3, sHour, sMinute, eHour, eMinute);
        }
        if (preferences.getBoolean("dm_tuesday", true)) {
            setRepeatAlarm(4, sHour, sMinute, eHour, eMinute);
        }
        if (preferences.getBoolean("dm_wednesday", true)) {
            setRepeatAlarm(5, sHour, sMinute, eHour, eMinute);
        }
        if (preferences.getBoolean("dm_thursday", true)) {
            setRepeatAlarm(6, sHour, sMinute, eHour, eMinute);
        }
        if (preferences.getBoolean("dm_friday", true)) {
            setRepeatAlarm(7, sHour, sMinute, eHour, eMinute);
        }
    }

    private void setRepeatAlarm(int day, int sHour, int sMinute, int eHour, int eMinute) {
        Calendar mCalendar_r = Calendar.getInstance();
        Calendar mCalendarEnd_r = Calendar.getInstance();
        mCalendar_r.set(Calendar.DAY_OF_WEEK, day);
        mCalendar_r.set(Calendar.HOUR_OF_DAY, sHour);
        mCalendar_r.set(Calendar.MINUTE, sMinute);
        mCalendar_r.set(Calendar.SECOND, 0);
        mCalendar_r.set(Calendar.MILLISECOND, 0);
        mCalendarEnd_r.set(Calendar.DAY_OF_WEEK, day);
        mCalendarEnd_r.set(Calendar.HOUR_OF_DAY, eHour);
        mCalendarEnd_r.set(Calendar.MINUTE, eMinute);
        mCalendarEnd_r.set(Calendar.SECOND, 0);
        mCalendarEnd_r.set(Calendar.MILLISECOND, 0);
        new DownloadReceiver().setRepeatAlarm(ApplicationLoader.applicationContext, mCalendar_r, mCalendarEnd_r, day + MediaFile.FILE_TYPE_DTS);
    }
}
