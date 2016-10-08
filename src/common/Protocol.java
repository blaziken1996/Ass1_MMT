package common;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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

    public static byte[] intToBytes(int x) {
        byte[] bytes = new byte[Integer.BYTES];
        for (int i = Integer.BYTES - 1; i >= 0; i--, x >>= 8) {
            bytes[i] = (byte) (x & 0xFF);
        }
        return bytes;
    }

    public static byte[] inetAddressToBytes(InetSocketAddress address) {
        byte[] result = new byte[8];
        System.arraycopy(address.getAddress().getAddress(), 0, result, 0, 4);
        System.arraycopy(Protocol.intToBytes(address.getPort()), 0, result, 4, 4);
        return result;
    }

    public static InetSocketAddress readInetAddress(InputStream in) throws IOException {
        byte[] buffer = new byte[4];
        in.read(buffer, 0, 4);
        return new InetSocketAddress(InetAddress.getByAddress(buffer), Protocol.readInt(in));
    }
    public static byte[] stringToBytes(String s) throws UnsupportedEncodingException {
        byte[] sbytes = s.getBytes(ENCODE);
        byte[] len = intToBytes(sbytes.length);
        byte[] result = new byte[sbytes.length + len.length];
        System.arraycopy(len, 0, result, 0, len.length);
        System.arraycopy(sbytes, 0, result, len.length, sbytes.length);
        return result;
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
        //for (int i = 0; i < len; i++) bytes[i] = (byte) in.read();
        in.read(bytes, 0, len);
        return new String(bytes, Protocol.ENCODE);
    }
}
