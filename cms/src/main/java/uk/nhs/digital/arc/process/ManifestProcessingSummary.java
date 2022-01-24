package uk.nhs.digital.arc.process;

import java.util.ArrayList;
import java.util.List;

public class ManifestProcessingSummary {
    private List<ProcessOutcome> outcomes = new ArrayList<ProcessOutcome>();

    private boolean inError = false;

    public void addIndividualProcessOutcome(ProcessOutcome pm) {
        outcomes.add(pm);
        if (pm.isInError()) {
            inError = true;
        }
    }

    public boolean isInError() {
        return inError;
    }

    public String getConcatenatedMessages() {
        StringBuilder sb = new StringBuilder();
        for (ProcessOutcome pm : outcomes) {
            sb.append(pm.getMessageLine());
        }

        return sb.toString();
    }
}
