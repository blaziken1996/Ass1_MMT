package common;

/**
 * Created by trung on 22/09/2016.
 */
public class TerminalOutput implements DisplayOutput {

    @Override
    public void show(String output) {
        System.out.println(output);
    }
}
