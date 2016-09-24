package client;

import common.DisplayOutput;
import common.Protocol;
import common.ReadInput;

import java.io.File;
import java.io.IOException;

import static java.util.Arrays.asList;

/**
 * Created by trung on 22/09/2016.
 */
public class ClientReadUserInput extends Thread {
    private Client client;
    private ReadInput readInput;
    private DisplayOutput displayOutput;

    public ClientReadUserInput(Client client, ReadInput readInput, DisplayOutput displayOutput) {
        this.client = client;
        this.readInput = readInput;
        this.displayOutput = displayOutput;
    }

    @Override
    public void run() {
        boolean isRunning = true;
        while (isRunning) {
            String userinput = readInput.read();
            if (userinput == null || userinput.isEmpty()) continue;
            String[] inputs = Protocol.messageSeparator(userinput);
            if (inputs[0].compareTo(Protocol.SEND_MSG) == 0 || inputs[0].compareTo(Protocol.send_msg) == 0) {
                try {
                    byte[] _1 = inputs[1].getBytes(Protocol.ENCODE);
                    byte[] _2 = inputs[2].getBytes(Protocol.ENCODE);
                    client.write(asList(Protocol.intToBytes(Protocol.SEND_MSG_CODE),
                            Protocol.intToBytes(_1.length), _1, Protocol.intToBytes(_2.length), _2));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (inputs[0].compareTo(Protocol.SEND_FILE) == 0 || inputs[0].compareTo(Protocol.send_file) == 0) {
                File file = new File(inputs[2]);
                if (file.exists()) {
                    client.getReceiverFileMap().put(inputs[1], file);
                    try {
                        byte[] _1 = inputs[1].getBytes(Protocol.ENCODE);
                        byte[] _2 = Protocol.getFileName(inputs[2]).getBytes(Protocol.ENCODE);
                        client.write(asList(Protocol.intToBytes(Protocol.FILE_REQ_CODE),
                                Protocol.intToBytes(_1.length), _1, Protocol.intToBytes(_2.length), _2));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    displayOutput.show("There is no file at " + inputs[2]);
                }
            } else if (inputs[0].compareTo(Protocol.onlinelist) == 0 || inputs[0].compareTo(Protocol.ONLINELIST) == 0) {
                try {
                    client.write(asList(Protocol.intToBytes(Protocol.ONLINE_LIST_CODE)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (inputs[0].compareTo(Protocol.QUIT) == 0 || inputs[0].compareTo(Protocol.quit) == 0) {
                try {
                    client.write(asList(Protocol.intToBytes(Protocol.END_CONNECT_CODE)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isRunning = false;
            } else {
                displayOutput.show("I don't understand your command.");
            }
        }
    }
}
