package uk.nhs.digital.arc.job;

import org.onehippo.repository.scheduling.RepositoryJob;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.arc.process.ManifestProcessor2;

import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class ArcRepositoryJob implements RepositoryJob {
    private static final Logger log = LoggerFactory.getLogger(ArcRepositoryJob.class);

    public ArcRepositoryJob() {
        log.error("TestRepositoryJob ctor called()");
    }

    @Override
    public void execute(RepositoryJobExecutionContext repositoryJobExecutionContext) throws RepositoryException {
        Session session = null;

        try {
            session = repositoryJobExecutionContext.createSystemSession();
            String manifestFile = repositoryJobExecutionContext.getAttribute("manifest_file");
            String nodePath = repositoryJobExecutionContext.getAttribute("node_path");

            log.info("TestRepositoryJob executing repo job now with manifest_file value of '" + manifestFile + "'");

            ManifestProcessor2 manifestProcessor = new ManifestProcessor2(session, manifestFile, nodePath);
            try {
                manifestProcessor.readWrapperFromFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }
}
