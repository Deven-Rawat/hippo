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
import uk.nhs.digital.arc.process.ManifestProcessor2;
import uk.nhs.digital.arc.process.MessageBuilder;

import java.io.IOException;
import java.util.Date;

import javax.jcr.RepositoryException;


public class ArcDocumentWorkflowPlugin extends RenderPlugin<WorkflowDescriptor> {

    private static final Logger log = LoggerFactory.getLogger(ArcDocumentWorkflowPlugin.class);
    private boolean previewInError = false;

    public ArcDocumentWorkflowPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);
        log.info("Initialising ARC doc workflow");

        add(new StdWorkflow<DocumentWorkflow>("create_report",
            new StringResourceModel("create_report", this),
            this.getPluginContext(),
            (WorkflowDescriptorModel) getModel()) {

            @Override
            protected void invoke() {
                super.invoke();
                log.info("Invoke now");
            }

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
                    log.info("** Now creating job from menu item");

                    repoWork(getNodePath());
                    log.info("** Completed creating job from menu item");
                }

                return null;
            }

            private String getNodePath() throws RepositoryException {
                return getModel().getNode().getPath();
            }

            @Override
            protected IDialogService.Dialog createRequestDialog() {
                MessageBuilder messageBuilder = runProcessorInReviewMode();
                previewInError = messageBuilder.isInError();

                return new JsonReviewDialog(this, messageBuilder.getConcatenatedMessages(), messageBuilder.isInError());
            }

            private MessageBuilder runProcessorInReviewMode() {
                MessageBuilder response = null;

                try {
                    ManifestProcessor2 proc2 = new ManifestProcessor2(null, "http://localhost/wrapper_localhost.json", getNodePath());
                    response = proc2.readWrapperFromFile();
                } catch (RepositoryException | IOException e) {
                    e.printStackTrace();
                }

                return response;
            }

            private void repoWork(String nodePath) {
                final RepositoryScheduler scheduler = HippoServiceRegistry.getService(RepositoryScheduler.class);

                log.error("**** Creating jobInfo from menu item");

                Date now = new Date();
                String jobName = "automaticreportcreation_" + now.getTime();
                final RepositoryJobInfo myJobInfo = new RepositoryJobInfo(jobName, ArcRepositoryJob.class);

                myJobInfo.setAttribute("manifest_file", "http://localhost/wrapper_localhost.json");
                myJobInfo.setAttribute("node_path", nodePath);

                log.info("**** Creating jobTrigger from menu item");
                final RepositoryJobTrigger myJobTrigger =
                    new RepositoryJobSimpleTrigger("arc_" + now.getTime(), now);

                try {
                    log.info("**** Calling scheduler from menu item");
                    scheduler.scheduleJob(myJobInfo, myJobTrigger);
                    log.info("**** Scheduler now called from menu item");
                } catch (RepositoryException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}