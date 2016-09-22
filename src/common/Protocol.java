package common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by trung on 16/09/2016.
 */
public class Protocol {
    public final static int SEND_MSG_CODE = 1;
    public final static int RECEIVE_MSG_CODE = 100;
    public final static int ACCEPT_FILE = 200;
    public final static int DENY_FILE = 300;
    public final static int FILE_REQ_CODE = 2;
    public final static int INIT_CHAT_CODE = 0;
    public final static int END_CONNECT_CODE = 3;
    public final static int FILE_RECEIVED_CODE = 4;
    public final static int ONLINE_LIST_CODE = 5;
    public final static int CHAT_REQ_ACCEPT_CODE = 7;
    public final static int CHAT_REQ_DENY_CODE = 8;
    public final static int MSG_AVAIL_CODE = 9;
    public final static String SEND_FILE = "SENDFILE";
    public final static String send_file = SEND_FILE.toLowerCase();
    public final static String SEND_MSG = "SENDMSG";
    public final static String send_msg = SEND_MSG.toLowerCase();
    public final static String ONLINELIST = "whoisonline";
    public final static String onlinelist = ONLINELIST.toLowerCase();
    public final static String QUIT = "QUIT";
    public final static String quit = QUIT.toLowerCase();
    private final static String REGEX = "([^#\\\\]|\\\\[#\\\\])+";
    private final static String FilePathRegex = "([^\\\\/]+)";
    private final static Pattern p = Pattern.compile(REGEX);
    private final static Pattern path = Pattern.compile(FilePathRegex);

    public static String[] messageSeparator(String message) {
        Matcher m = p.matcher(message);
        String[] list = new String[3];
        int count = 0;
        while (m.find() && count < 3) {
            list[count++] = m.group(0);
        }
        return list;
    }

    public static String getFileName(String filepath) {
        Matcher m = path.matcher(filepath);
        String result = null;
        while (m.find()) result = m.group(0);
        return result;
    }
}
