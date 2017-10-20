package system.logging;

import java.io.PrintWriter;
import java.io.StringWriter;

public class MessageFormat {

    public static String format(String paramString, Throwable paramThrowable) {
        StringBuffer sbMsg = new StringBuffer(paramString);

        if (paramThrowable != null) {
            sbMsg.append(" <");
            sbMsg.append(paramThrowable.toString());
            sbMsg.append(">");

            StringWriter sw = new StringWriter(1024);
            PrintWriter pw = new PrintWriter(sw);
            paramThrowable.printStackTrace(pw);
            pw.close();
            sbMsg.append(sw.toString());
        }

        return sbMsg.toString();
    }

    public static String format(String paramString, Object... paramVarArgs) {
        String[] strValue = paramString.split("\\{\\}", -1);
        StringBuffer sbMsg = new StringBuffer(paramString.length());

        for (int i = 0, len = strValue.length; i < len; i++) {
            sbMsg.append(strValue[i]);
            if (i < len - 1) {
                if (paramVarArgs.length > i) {
                    sbMsg.append(String.valueOf(paramVarArgs[i]));
                } else {
                    sbMsg.append("{}");
                }
            }
        }

        return sbMsg.toString();
    }

}
