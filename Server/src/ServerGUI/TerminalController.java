package ServerGUI;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by Dark Light on 13/10/2016.
 */
public class TerminalController implements Initializable{

    @FXML
    private TextArea txtArea;

    @FXML
    private Label lblIP;

    @FXML
    private Label lblPort;

    private SimpleDateFormat dateFormat;

    public void setInfo (String address, int port) {
        lblIP.setText("Server IP: " + address.substring(1));
        lblPort.setText("Port: " + Integer.toString(port));
    }

    public void consoleLog(String address) {
        txtArea.appendText("[" + dateFormat.format(new Date()) + "] " + address + "\n");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    }
}
