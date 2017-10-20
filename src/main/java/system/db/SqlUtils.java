package system.db;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import system.exception.ApplicationException;

public class SqlUtils {

    private static final Pattern pParam1 = Pattern.compile("(?)");

    private static final Pattern pParam2 = Pattern.compile(":(\\w+)");

    public static String formatSql(String sql, Object... params) {
        int start = 0;
        int end;
        int len = sql.length();
        int index = 0;
        StringBuilder sbSql = new StringBuilder(len);
        Matcher m = pParam1.matcher(sql);

        while (m.find()) {
            if (index < params.length) {
                end = m.start(0);
                sbSql.append(sql, start, end);
                Object value = params[index];
                if (value instanceof String) {
                    sbSql.append("'").append(value).append("'");
                } else if (value instanceof Number) {
                    sbSql.append(value);
                } else {
                    sbSql.append("'").append(value).append("'");
                }
                start = m.end(1);
            } else {
                throw new ApplicationException("パラメータの数が足りないです。");
            }
            index++;
        }
        if (start < len) {
            sbSql.append(sql, start, len);
        }

        return sbSql.toString();
    }

    public static String formatSql(String sql, Map<String, Object> params) {
        int start = 0;
        int end;
        int len = sql.length();
        StringBuilder sbSql = new StringBuilder(len);
        Matcher m = pParam2.matcher(sql);

        while (m.find()) {
            String key = m.group(1);
            if (params.containsKey(key)) {
                end = m.start(0);
                sbSql.append(sql, start, end);
                Object value = params.get(key);
                if (value instanceof String) {
                    sbSql.append("'").append(value).append("'");
                } else if (value instanceof Number) {
                    sbSql.append(value);
                } else {
                    sbSql.append("'").append(value).append("'");
                }
                start = m.end(1);
            } else {
                throw new ApplicationException("paramsに" + key + "のパラメータが存在しません。");
            }

        }
        if (start < len) {
            sbSql.append(sql, start, len);
        }

        return sbSql.toString();
    }

}
