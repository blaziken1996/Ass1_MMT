package client;

import common.DisplayOutput;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by trung on 22/09/2016.
 */
public class ClientSendFile extends Thread {
    private Socket client;
    private File file;
    private DisplayOutput displayOutput;

    public ClientSendFile(String host, int port, File file, DisplayOutput displayOutput) {
        try {
            this.displayOutput = displayOutput;
            client = new Socket(host, port);
            this.file = file;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            OutputStream out = client.getOutputStream();
            FileInputStream in = new FileInputStream(file);
            int count;
            byte[] buffer = new byte[8192];
            while ((count = in.read(buffer)) > 0) out.write(buffer, 0, count);
            out.flush();
            in.close();
            client.close();
            displayOutput.show("Successfully sent file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
