package GUI;

/**
 * Created by Dark Light on 11/10/2016.
 */
class ChatMessage {
    private String text;
    private boolean self = false;
    public ChatMessage(String text, boolean self) {
        this.text = text;
        this.self = self;
    }
    public ChatMessage(String text) {
        this(text, false);
    }

    public String getText() {
        return text;
    }

    public boolean isSelf() {
        return self;
    }
}