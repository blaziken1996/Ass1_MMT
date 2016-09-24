package client;

import common.Protocol;
import common.TerminalInput;
import common.TerminalOutput;

import java.io.IOException;
import java.net.Socket;

import static java.util.Arrays.asList;

/**
 * Created by trung on 22/09/2016.
 */
public class ClientRunOnTerminal {
    public static void main(String[] args) {
        try {
            TerminalOutput to = new TerminalOutput();
            if (args.length < 3) {
                to.show("Need 3 arguments: server address, server port and name");
                return;
            }
            TerminalInput ti = new TerminalInput(System.in);
            Client client = new Client(new Socket(args[0], Integer.parseInt(args[1])), args[2]);
            byte[] name = client.getName().getBytes(Protocol.ENCODE);
            client.write(asList(Protocol.intToBytes(name.length), name));
            new ClientReadUserInput(client, ti, to).start();
            new ClientReadSocketInput(client, to, ti).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
