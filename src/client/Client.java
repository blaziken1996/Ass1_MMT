package client;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by trung on 16/09/2016.
 */
public class Client {
    Socket client;

    public Client(Socket client) {
        this.client = client;
    }

    public Client(String host, int port) {
        try {
            this.client = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
