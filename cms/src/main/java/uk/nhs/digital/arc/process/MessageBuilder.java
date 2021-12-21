package uk.nhs.digital.arc.process;

import java.util.ArrayList;
import java.util.List;

public class MessageBuilder {
    private List<ProcessMessage> messages = new ArrayList<ProcessMessage>();
    private boolean inError = false;

    public void addProcessMessage(ProcessMessage pm) {
        messages.add(pm);
        if (pm.isInError()) {
            inError = true;
        }
    }

    public boolean isInError() {
        return inError;
    }

    public String getConcatenatedMessages() {
        StringBuilder sb = new StringBuilder();
        for (ProcessMessage pm : messages) {
            sb.append(pm.getMessageLine());
        }

        return sb.toString();
    }
}
