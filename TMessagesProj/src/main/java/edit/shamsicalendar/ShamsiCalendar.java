package edit.shamsicalendar;

import android.annotation.SuppressLint;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@SuppressLint({"UseValueOf"})
public class ShamsiCalendar {
    public static final int CURRENT_CENTURY = 13;
    private static final String[] shamsiMonths = new String[]{"فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور", "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"};
    private static final String[] shamsiMonthsEn = new String[]{"Farvardin", "Ordibehesht", "Khordad", "Tir", "Mordad", "Shahrivar", "Mehr", "Aban", "Azar", "Dey", "Bahman", "Esfand"};
    private static final String[] shamsiWeekDays = new String[]{"شنبه", "یکشنبه", "دوشنبه", "سه شنبه", "چهارشنبه", "پنج شنبه", "جمعه"};
    private static final Map shamsiWeekDaysMap = new HashMap();

    static {
        shamsiWeekDaysMap.put(new Integer(7), shamsiWeekDays[0]);
        shamsiWeekDaysMap.put(new Integer(1), shamsiWeekDays[1]);
        shamsiWeekDaysMap.put(new Integer(2), shamsiWeekDays[2]);
        shamsiWeekDaysMap.put(new Integer(3), shamsiWeekDays[3]);
        shamsiWeekDaysMap.put(new Integer(4), shamsiWeekDays[4]);
        shamsiWeekDaysMap.put(new Integer(5), shamsiWeekDays[5]);
        shamsiWeekDaysMap.put(new Integer(6), shamsiWeekDays[6]);
    }

    public static ShamsiDate dateToShamsi(Date date) {
        int i2;
        int i3;
        Roozh roozh = new Roozh();
        roozh.GregorianToPersian(date.getYear(), date.getMonth(), date.getDay());
        int l2 = roozh.getMonth();
        long dSTSavings = (((long) (TimeZone.getDefault().inDaylightTime(date) ? TimeZone.getDefault().getDSTSavings() : 0)) + date.getTime()) - new Date("01/01/1900").getTime();
        long j = dSTSavings / 1000;
        int i4 = (int) (j % 60);
        j /= 60;
        int i5 = (int) (j % 60);
        int i6 = (int) ((j / 60) % 24);
        long j2 = dSTSavings / 86400000;
        if (j2 <= 78) {
            int i = (short) ((int) (((10 + j2) / 30) + 10));
            i2 = (short) ((int) (((10 + j2) % 30) + 1));
            i3 = 1278;
        } else {
            j = j2 - 78;
            int i7 = 1279;
            while (true) {
                j2 = ((long) (i7 + 11)) % 33;
                i3 = (j2 == 32 || j2 % 4 != 0) ? 0 : 1;
                if (j <= ((long) (i3 + 365))) {
                    break;
                }
                j -= (long) (i3 + 365);
                i7 = (short) (i7 + 1);
            }
            short s;
            if (j <= 186) {
                i2 = (short) ((int) (((j - 1) % 31) + 1));
                s = (short) ((int) (((j - 1) / 31) + 1));
                i3 = i7;
            } else {
                i2 = (short) ((int) ((((j - 1) - 186) % 30) + 1));
                s = (short) ((int) ((((j - 1) - 186) / 30) + 7));
                i3 = i7;
            }
        }
        return new ShamsiDate(i3, l2, i2, i6, i5, i4);
    }

    public static int getDaysInMonth(ShamsiDate shamsiDate) {
        int i = 29;
        if (shamsiDate.getMonth() < 7) {
            return 31;
        }
        if (shamsiDate.getMonth() < 12) {
            return 30;
        }
        Date shamsiToDate = shamsiToDate(new ShamsiDate(shamsiDate.getYear(), shamsiDate.getMonth(), 29));
        Calendar instance = Calendar.getInstance();
        instance.setTime(shamsiToDate);
        instance.add(5, 1);
        if (dateToShamsi(instance.getTime()).getMonth() == 12) {
            i = 30;
        }
        return i;
    }

    public static String getShamsiMonth(int i) {
        return (i >= 1 || i <= 12) ? shamsiMonths[i - 1] : null;
    }

    public static String getShamsiMonthEn(int i) {
        return (i >= 1 || i <= 12) ? shamsiMonthsEn[i - 1] : null;
    }

    public static String[] getShamsiMonths() {
        return shamsiMonths;
    }

    public static String getShamsiWeekDay(int i) {
        return (i < 0 || i > 6) ? null : shamsiWeekDays[i];
    }

    public static String[] getShamsiWeekDays() {
        return shamsiWeekDays;
    }

    public static Map getShamsiWeekDaysMap() {
        return shamsiWeekDaysMap;
    }

    public static Date shamsiToDate(ShamsiDate shamsiDate) {
        int month = shamsiDate.getMonth() >= 7 ? (((shamsiDate.getMonth() - 7) * 30) + 186) + shamsiDate.getDay() : ((shamsiDate.getMonth() - 1) * 31) + shamsiDate.getDay();
        long j = shamsiDate.getYear() == 1278 ? (long) (month - 287) : 79;
        for (int i = 1279; i < shamsiDate.getYear(); i++) {
            long j2 = ((long) (i + 11)) % 33;
            Integer valueOf = (j2 == 32 || j2 % 4 != 0) ? null : Integer.valueOf(1);
            j = valueOf.intValue() == 1 ? j + 366 : j + 365;
        }
        Date date = new Date(new Date("01/01/1900").getTime() + ((((((long) month) + j) - 1) * 86400000) + ((long) ((((shamsiDate.getHour() * 3600) * 1000) + ((shamsiDate.getMinute() * 60) * 1000)) + (shamsiDate.getSecond() * 1000)))));
        return TimeZone.getDefault().inDaylightTime(date) ? new Date(date.getTime() - ((long) TimeZone.getDefault().getDSTSavings())) : date;
    }
}
