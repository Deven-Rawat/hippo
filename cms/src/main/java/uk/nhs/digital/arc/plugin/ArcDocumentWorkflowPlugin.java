package uk.nhs.digital.arc.plugin;

import org.apache.wicket.model.StringResourceModel;
import org.hippoecm.addon.workflow.StdWorkflow;
import org.hippoecm.addon.workflow.WorkflowDescriptorModel;
import org.hippoecm.frontend.dialog.IDialogService;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.service.render.RenderPlugin;
import org.hippoecm.repository.api.WorkflowDescriptor;
import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.repository.documentworkflow.DocumentWorkflow;
import org.onehippo.repository.scheduling.RepositoryJobInfo;
import org.onehippo.repository.scheduling.RepositoryJobSimpleTrigger;
import org.onehippo.repository.scheduling.RepositoryJobTrigger;
import org.onehippo.repository.scheduling.RepositoryScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.arc.job.ArcRepositoryJob;
import uk.nhs.digital.arc.plugin.dialog.JsonReviewDialog;
import uk.nhs.digital.arc.plugin.util.DoctypeDetector;
import uk.nhs.digital.arc.process.ManifestProcessor;
import uk.nhs.digital.arc.process.ProcessingMessageSummary;

import java.io.IOException;
import java.util.Date;

import javax.jcr.RepositoryException;


public class ArcDocumentWorkflowPlugin extends RenderPlugin<WorkflowDescriptor> {

    private static final Logger log = LoggerFactory.getLogger(ArcDocumentWorkflowPlugin.class);
    private boolean previewInError = false;

    public ArcDocumentWorkflowPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);

        add(new StdWorkflow<DocumentWorkflow>("create_report",
            new StringResourceModel("create_report", this),
            this.getPluginContext(),
            (WorkflowDescriptorModel) getModel()) {

            @Override
            public String getSubMenu() {
                //* Check path to figure out if this is a Publication doc type
                try {
                    if (DoctypeDetector.isContentPublication(getModel().getNode())) {
                        return "document";
                    }
                } catch (RepositoryException e) {
                    e.printStackTrace();
                }

                return super.getSubMenu();
            }

            @Override
            protected String execute(DocumentWorkflow workflow) throws Exception {
                if (!previewInError) {
                    repoWork(getNodePath());
                }

                return null;
            }

            private String getNodePath() throws RepositoryException {
                return getModel().getNode().getPath();
            }

            @Override
            protected IDialogService.Dialog createRequestDialog() {
                ProcessingMessageSummary processingMessageSummary = null;
                try {
                    String manifestLocation = DoctypeDetector.getManifestLocationValue(getModel().getNode());

                    processingMessageSummary = runProcessorInReviewMode(manifestLocation, getNodePath());
                    previewInError = processingMessageSummary.isInError();

                    return new JsonReviewDialog(this, processingMessageSummary.getConcatenatedMessages(), processingMessageSummary.isInError());
                } catch (RepositoryException e) {
                    e.printStackTrace();
                }

                return null;
            }

            private ProcessingMessageSummary runProcessorInReviewMode(String manifestPath, String nodePath) {
                ProcessingMessageSummary responseMessages = null;

                try {
                    ManifestProcessor processor = new ManifestProcessor(null, manifestPath, nodePath);
                    responseMessages = processor.readWrapperFromFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return responseMessages;
            }

            private void repoWork(String nodePath) {
                final RepositoryScheduler scheduler = HippoServiceRegistry.getService(RepositoryScheduler.class);

                final Date now = new Date();
                final long nowMillis = now.getTime();
                final String jobName = "automaticreportcreation_" + nowMillis;

                final RepositoryJobInfo myJobInfo = new RepositoryJobInfo(jobName, ArcRepositoryJob.class);

                try {
                    String manifestLocation = DoctypeDetector.getManifestLocationValue(getModel().getNode());
                    myJobInfo.setAttribute("manifest_file", manifestLocation);
                    myJobInfo.setAttribute("node_path", nodePath);

                    final String triggerName = "arc_ " + nowMillis;

                    log.info("jobTrigger {} for job {} now created and scheduled", jobName, triggerName);
                    final RepositoryJobTrigger myJobTrigger =
                        new RepositoryJobSimpleTrigger("arc_" + nowMillis, now);

                    scheduler.scheduleJob(myJobInfo, myJobTrigger);
                } catch (RepositoryException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}