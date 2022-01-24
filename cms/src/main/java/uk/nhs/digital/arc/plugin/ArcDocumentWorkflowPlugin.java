package uk.nhs.digital.arc.plugin;

import org.apache.wicket.model.StringResourceModel;
import org.hippoecm.addon.workflow.StdWorkflow;
import org.hippoecm.addon.workflow.WorkflowDescriptorModel;
import org.hippoecm.frontend.dialog.IDialogService;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.service.render.RenderPlugin;
import org.hippoecm.frontend.session.UserSession;
import org.hippoecm.repository.api.WorkflowDescriptor;
import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.cms7.services.eventbus.HippoEventBus;
import org.onehippo.repository.documentworkflow.DocumentWorkflow;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.arc.plugin.dialog.JsonReviewDialog;
import uk.nhs.digital.arc.plugin.util.DoctypeDetector;
import uk.nhs.digital.arc.process.ManifestProcessingSummary;
import uk.nhs.digital.arc.process.ManifestProcessor;
import uk.nhs.digital.externalstorage.workflow.AbstractExternalFileTask;

import java.io.IOException;

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
                    //repoWork(getNodePath());
                    publishEvent();
                }

                return null;
            }

            private String getNodePath() throws RepositoryException {
                return getModel().getNode().getPath();
            }

            @Override
            protected IDialogService.Dialog createRequestDialog() {
                ManifestProcessingSummary manifestProcessingSummary = null;
                try {
                    String manifestLocation = DoctypeDetector.getManifestLocationValue(getModel().getNode());

                    manifestProcessingSummary = runProcessorInReviewMode(manifestLocation, getNodePath());
                    previewInError = manifestProcessingSummary.isInError();

                    return new JsonReviewDialog(this, manifestProcessingSummary.getConcatenatedMessages(), manifestProcessingSummary.isInError());
                } catch (RepositoryException e) {
                    e.printStackTrace();
                }

                return null;
            }

            /**
             * Initiate the event that will service this request
             */
            private void publishEvent() {
                final HippoEventBus eventBus = HippoServiceRegistry.getService(HippoEventBus.class);
                final String currentUser = UserSession.get().getJcrSession().getUserID();

                try {
                    if (eventBus != null) {
                        final String manifestLocation = DoctypeDetector.getManifestLocationValue(getModel().getNode());

                        log.debug("Now posting event for ARC create for manifest {}", manifestLocation);
                        eventBus.post(new HippoWorkflowEvent()
                            .className(AbstractExternalFileTask.class.getName())
                            .application("arc")
                            .timestamp(System.currentTimeMillis())
                            .user(currentUser)
                            .set("manifest_file", manifestLocation)
                            .set("node_path", getNodePath())
                            .set("session_user", currentUser)
                            .set("methodName", "arc_create")
                        );

                        log.debug("Event now posted for manifest {}", manifestLocation);
                    }
                } catch (RepositoryException rex) {
                    rex.printStackTrace();
                }
            }

            private ManifestProcessingSummary runProcessorInReviewMode(String manifestPath, String nodePath) {
                ManifestProcessingSummary responseMessages = null;

                try {
                    ManifestProcessor processor = new ManifestProcessor(null, manifestPath, nodePath);
                    responseMessages = processor.readWrapperFromFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return responseMessages;
            }

            //            private void repoWork(String nodePath) {
            //                final RepositoryScheduler scheduler = HippoServiceRegistry.getService(RepositoryScheduler.class);
            //
            //                final Date now = new Date();
            //                final long nowMillis = now.getTime();
            //                final String jobName = "automaticreportcreation_" + nowMillis;
            //
            //                final RepositoryJobInfo myJobInfo = new RepositoryJobInfo(jobName, ArcRepositoryJob.class);
            //
            //                try {
            //                    String manifestLocation = DoctypeDetector.getManifestLocationValue(getModel().getNode());
            //                    myJobInfo.setAttribute("manifest_file", manifestLocation);
            //                    myJobInfo.setAttribute("node_path", nodePath);
            //
            //                    final String triggerName = "arc_ " + nowMillis;
            //
            //                    log.info("jobTrigger {} for job {} now created and scheduled", jobName, triggerName);
            //                    final RepositoryJobTrigger myJobTrigger =
            //                        new RepositoryJobSimpleTrigger("arc_" + nowMillis, now);
            //
            //                    scheduler.scheduleJob(myJobInfo, myJobTrigger);
            //                } catch (RepositoryException e) {
            //                    e.printStackTrace();
            //                }
            //            }
        });
    }
}