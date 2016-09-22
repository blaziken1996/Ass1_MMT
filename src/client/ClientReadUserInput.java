package client;

import common.DisplayOutput;
import common.Protocol;
import common.ReadInput;

import java.io.File;
import java.util.ArrayList;

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
                ArrayList<String> messages = new ArrayList<>(2);
                messages.add(inputs[1]);
                messages.add(inputs[2]);
                client.write(messages, Protocol.SEND_MSG_CODE);
            } else if (inputs[0].compareTo(Protocol.SEND_FILE) == 0 || inputs[0].compareTo(Protocol.send_file) == 0) {
                File file = new File(inputs[2]);
                if (file.exists()) {
                    client.getReceiverFileMap().put(inputs[1], file);
                    ArrayList<String> messages = new ArrayList<>(2);
                    messages.add(inputs[1]);
                    messages.add(Protocol.getFileName(inputs[2]));
                    client.write(messages, Protocol.FILE_REQ_CODE);
                } else {
                    displayOutput.show("There is no file at " + inputs[2]);
                }
            } else if (inputs[0].compareTo(Protocol.onlinelist) == 0 || inputs[0].compareTo(Protocol.ONLINELIST) == 0) {
                client.write(null, Protocol.ONLINE_LIST_CODE);
            } else if (inputs[0].compareTo(Protocol.QUIT) == 0 || inputs[0].compareTo(Protocol.quit) == 0) {
                client.write(null, Protocol.END_CONNECT_CODE);
                isRunning = false;
            } else {
                displayOutput.show("I don't understand your command.");
            }
        }
    }
}
