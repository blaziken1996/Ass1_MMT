package common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by trung on 22/09/2016.
 */
public class TerminalInput implements ReadInput {
    private BufferedReader br;

    public TerminalInput(InputStream in) {
        br = new BufferedReader(new InputStreamReader(in));
    }

    @Override
    synchronized public String read() {
        try {
            return br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}