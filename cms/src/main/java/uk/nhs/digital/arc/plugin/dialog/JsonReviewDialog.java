package uk.nhs.digital.arc.plugin.dialog;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.value.IValueMap;
import org.hippoecm.addon.workflow.AbstractWorkflowDialog;
import org.hippoecm.addon.workflow.StdWorkflow;
import org.hippoecm.frontend.dialog.DialogConstants;

public class JsonReviewDialog extends AbstractWorkflowDialog {

    private static final String PARSEERRORS_MESSAGE = "Errors found during parsing the manifest. Please take a look at the output below";
    private static final String ALLCLEAR_MESSAGE = "No errors found during parsing. Output below shows the manifest details that were parsed";

    public JsonReviewDialog(StdWorkflow action, String messages, boolean isInError) {
        super(null, action);

        TextArea<String> commentArea = new TextArea<>(
            "output_area",Model.of(messages));
        commentArea.setOutputMarkupId(true);
        add(commentArea);

        Label msg = new Label("results_label", Model.of(isInError ? PARSEERRORS_MESSAGE : ALLCLEAR_MESSAGE));
        msg.setOutputMarkupId(true);
        add(msg);

        this.setCancelEnabled(false);
        this.setCancelVisible(false);
    }

    @Override
    public IModel getTitle() {
        return new StringResourceModel("json-dialog-title", this);
    }

    @Override
    public IValueMap getProperties() {
        return DialogConstants.LARGE_RELATIVE;
    }
}