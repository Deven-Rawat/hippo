package uk.nhs.digital.arc.daemon;

import org.onehippo.cms7.services.eventbus.HippoEventListenerRegistry;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.onehippo.repository.modules.DaemonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.arc.process.ManifestProcessor;

import java.io.IOException;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

public class ArcListener implements DaemonModule {
    private static Logger LOGGER = LoggerFactory.getLogger(ArcListener.class);
    private Session session;

    @Subscribe
    public void handleEvent(HippoWorkflowEvent event) {
        if (event.success() && "arc".equals(event.get("application"))
            && event.get("methodName").equals("arc_create")) {
            processManifest(event);
        }
    }

    /**
     * Do the manifest processing work here.
     *
     * We take the event, pull out the parameters from the event posting and then call the processing class
     * @param event is the contextual event information, the most important of which are the manifest_file and node_path
     */
    private void processManifest(HippoWorkflowEvent event) {
        final String manifestFile = (String)event.get("manifest_file");
        final String nodePath = (String)event.get("node_path");
        final String requestingUser = (String)event.get("user");

        LOGGER.info("Manifest processing executing now with manifest_file value of '{}' using node path {} for user {}", manifestFile,
            nodePath,
            requestingUser);

        Session userSession = null;

        try {
            // Create content as the user that requested it (By default, we would be a system user)
            userSession = session.impersonate(new SimpleCredentials(requestingUser, "".toCharArray()));
            ManifestProcessor manifestProcessor = new ManifestProcessor(userSession, manifestFile, nodePath);
            manifestProcessor.readWrapperFromFile();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

        if (userSession != null) {
            userSession.logout();
        }
    }

    @Override
    public void initialize(Session session) {
        this.session = session;
        HippoEventListenerRegistry.get().register(this);
    }

    @Override
    public void shutdown() {
        HippoEventListenerRegistry.get().unregister(this);
    }
}