package uk.nhs.digital.arc.process;

public class ProcessMessage {
    private StringBuilder message = new StringBuilder();
    private boolean inError = false;

    public void addMessageLine(String messageString) {
        message.append(messageString);
    }

    public void addErrorMessageLine(String messageString) {
        message.append(messageString);
        inError = true;
    }

    public String getMessageLine() {
        return message.toString();
    }

    public boolean isInError() {
        return inError;
    }
}
