package system.excel;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.ss.usermodel.Workbook;

import system.exception.ApplicationException;

public class WorkbookUtils {

    protected static final String DATE_YYYYMD = "1";

    protected static final String NUMERIC = "2";

    protected static final String DATE_YYYYMMDD = "3";

    private static DateFormat dfDay = new SimpleDateFormat("yyyy/M/d");

    private static DateFormat dfDay2 = new SimpleDateFormat("yyyy/MM/dd");

    private static DateFormat dfSec = new SimpleDateFormat("hh:mm:ss");

    private static Calendar calDay = Calendar.getInstance();

    private static Calendar calSec = Calendar.getInstance();

    private static Date dtInitDay;

    private static Date dtInitSec;

    protected Workbook workbook;

    protected File file;

    protected String fileName;

    static {
        try {
            dtInitDay = dfDay.parse("1899/12/30");
            dtInitSec = dfSec.parse("00:00:00");
        } catch (ParseException e) {
            throw new ApplicationException(e);
        }
    }

    public static int getColumnIndex(String str) {
        int col = 0;
        for (int i = 0, len = str.length(); i < len; i++) {
            char c = str.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                col = col * 26 + c - 64;
            } else {
                break;
            }
        }

        return col - 1;
    }

    public static String getDay(int days, String type) {
        calDay.setTime(dtInitDay);
        calDay.add(Calendar.DATE, days);
        Date date = calDay.getTime();

        String day = "";

        switch (type) {
        case DATE_YYYYMMDD:
            day = dfDay2.format(date);
            break;
        case DATE_YYYYMD:
        case "8":
            day = dfDay.format(date);
            break;
        }

        return day;
    }

    public static String getSec(int sec) {
        calSec.setTime(dtInitSec);
        calSec.add(Calendar.SECOND, sec);

        return dfSec.format(calSec.getTime());
    }

}
