package common;

import java.io.*;
import java.net.Socket;

/**
 * Created by trung on 15/09/2016.
 */
public class SendFile {

    public static void send(File file, Socket socket) throws IOException {
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            OutputStream outputStream = socket.getOutputStream();
            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            int count;
            byte[] buffer = new byte[8192];
            while ((count = bufferedInputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, count);
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) fileInputStream.close();
            if (bufferedInputStream != null) bufferedInputStream.close();
        }
    }
}
