package common;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by trung on 16/09/2016.
 */
public class
Protocol {
    public final static int SEND_MSG_CODE = 1;
    public final static int ACCEPT_FILE = 3;
    public final static int DENY_FILE = 4;
    public final static int FILE_REQ_CODE = 2;
    public final static int END_CONNECT_CODE = 6;
    public final static int ONLINE_LIST_CODE = 5;
    public final static int BUFFER_SIZE = 64 * 1024 * 8;
    public final static String ENCODE = "UTF-8";
    public final static String SEND_FILE = "SENDFILE";
    public final static String send_file = SEND_FILE.toLowerCase();
    public final static String SEND_MSG = "SENDMSG";
    public final static String send_msg = SEND_MSG.toLowerCase();
    public final static String ONLINELIST = "WHOISONLINE";
    public final static String onlinelist = ONLINELIST.toLowerCase();
    public final static String QUIT = "QUIT";
    public final static String quit = QUIT.toLowerCase();
    private final static String REGEX = "([^#\\\\]|\\\\[#\\\\])+";
    private final static String FilePathRegex = "([^\\\\/]+)";
    private final static Pattern p = Pattern.compile(REGEX);
    private final static Pattern path = Pattern.compile(FilePathRegex);

    public static byte[] intToBytes(int x) {
        byte[] bytes = new byte[Integer.BYTES];
        for (int i = Integer.BYTES - 1; i >= 0; i--, x >>= 8) {
            bytes[i] = (byte) (x & 0xFF);
        }
        return bytes;
    }

    public static int readInt(InputStream in) throws IOException {
        byte[] bytes = new byte[Integer.BYTES];
        if (in.read(bytes, 0, bytes.length) == -1) throw new IOException();
        int i = 0;
        for (byte b : bytes) i = (i << 8) | (b & 0xFF);
        return i;
        //return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }

    public static String readString(InputStream in) throws IOException {
        int len = readInt(in);
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) bytes[i] = (byte) in.read();
        return new String(bytes, Protocol.ENCODE);
    }

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
