package uk.nhs.digital.arc.process;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.onehippo.forge.content.exim.core.DocumentManager;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentManagerImpl;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentVariantImportTask;
import org.onehippo.forge.content.exim.core.util.ContentPathUtils;
import org.onehippo.forge.content.pojo.model.ContentNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.arc.factory.ArcTransformerFactory;
import uk.nhs.digital.arc.factory.JsonClassFactory;
import uk.nhs.digital.arc.json.ArcDoctype;
import uk.nhs.digital.arc.json.ManifestWrapper;
import uk.nhs.digital.arc.json.Page;
import uk.nhs.digital.arc.storage.ArcStorageManager;
import uk.nhs.digital.arc.storage.S3StorageManager;
import uk.nhs.digital.arc.transformer.abs.AbstractPageLevelTransformer;
import uk.nhs.digital.arc.util.FilePathData;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class ManifestProcessor {
    private static final Logger log = LoggerFactory.getLogger(ManifestProcessor.class);
    private final String manifestFile;
    private final String nodePath;
    private final boolean preview;

    private Session session = null;

    private ArcStorageManager storageManager;

    public ManifestProcessor(Session session, String manifestFile, String nodePath) {
        this.session = session;
        this.manifestFile = manifestFile;
        this.nodePath = nodePath;

        this.preview = session == null;

        this.storageManager = new S3StorageManager(); //new S3ObjectKeyGenerator(this::newRandomString));
    }

    public ProcessingMessageSummary readWrapperFromFile() throws IOException {
        FilePathData manifestFileData = new FilePathData(manifestFile);
        ProcessingMessageSummary messageSummary = new ProcessingMessageSummary();

        if (manifestFileData.isS3Protocol() && storageManager.fileExists(manifestFile)) {
            InputStream in = storageManager.getFileInputStream(manifestFileData);
            List<String> createdPages = new ArrayList<>();
            List<String> existingPages = new ArrayList<>();
            String reportRootFolder = getReportRootFolder();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            ManifestWrapper wrapper = objectMapper.readValue(in, ManifestWrapper.class);

            if (!preview) {
                existingPages = getExistingPages();
            }

            wrapper.getPages().stream().forEach(page -> {
                try {
                    messageSummary.addProcessMessage(loadAndProcessPage(wrapper.getDocumentBase(), page, objectMapper));
                    createdPages.add(reportRootFolder + "/" + page.getPageName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            if (!preview) {
                removeOrphanedPages(existingPages, createdPages);
            }
        } else {
            messageSummary.addProcessMessage(new ProcessMessage("Could not locate manifest file in the location: " + manifestFile,
                ProcessMessage.ERROR));
        }
        return messageSummary;
    }

    private void removeOrphanedPages(List<String> existingPages, List<String> createdPages) {
        Set<String> removed = Sets.difference(new HashSet<>(existingPages), new HashSet<>(createdPages));
        removed.stream().forEach(pageName -> {
            log.debug("*** Following page will be removed from the folder: {}", pageName);
            //try {
            new WorkflowDocumentManagerImpl(session).deleteDocument(pageName);
            //Node pageNode = session.getNode(pageName);
            //NodeIterator nodeIt = pageNode.getNodes();
            //
            //while (nodeIt.hasNext()) {
            //    nodeIt.nextNode().remove();
            //}
            //pageNode.remove();
            //log.debug("*** Now removed");
            //} catch (RepositoryException e) {
            //    e.printStackTrace();
            //}
        });
    }

    /**
     * Find all pages currently attributed to this report
     *
     * @return the {@link List} of content node paths
     */
    private List<String> getExistingPages() {
        List<String> createdPages = new ArrayList<>();

        Node folderNode = null;
        try {
            folderNode = session.getNode(getReportRootFolder());
            NodeIterator pages = folderNode.getNodes();

            while (pages.hasNext()) {
                Node pageNode = pages.nextNode();
                createdPages.add(pageNode.getPath());
            }
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        return createdPages;
    }

    private String getReportRootFolder() {
        String[] paths = ContentPathUtils.splitToFolderPathAndName(nodePath);
        return paths[0];
    }

    private ProcessMessage loadAndProcessPage(String docbase, Page page, ObjectMapper objectMapper) throws IOException {
        FilePathData filePathData = new FilePathData(docbase, page.getPageRef());

        ProcessMessage processMessage = new ProcessMessage("Now checking the manifest segment '"
            + page.getPageRef()
            + "', which is used by the page '"
            + page.getPageName() + "' ...\n");

        if (filePathData.isS3Protocol() && storageManager.fileExists(filePathData)) {
            InputStream in = storageManager.getFileInputStream(filePathData);

            try {
                Class jsonDataClass = JsonClassFactory.getJsonDataClassFromDocumentType(page.getDocumentType());

                ArcDoctype jsonObject = (ArcDoctype) objectMapper.readValue(in, jsonDataClass);

                if (!preview) {
                    addPublicationSystemDocument(docbase, page.getDocumentType(), page.getPageName(), jsonObject);
                } else {
                    checkValidityOfUrls(docbase, jsonObject.getAllReferencedExternalUrls(), processMessage);
                }

                if (!processMessage.isInError()) {
                    processMessage.addMessageLine("... parsed OK\n\n");
                } else {
                    processMessage.addErrorMessageLine("... errors above will stop processing from continuing. Please adjust file locations where necessary\n\n");
                }
            } catch (Exception e) {
                processMessage.addErrorMessageLine("\nError encountered during parsing:\n");
                processMessage.addErrorMessageLine(ExceptionUtils.getStackTrace(e) + "\n");
                e.printStackTrace();
            }
        } else {
            processMessage.addErrorMessageLine("** Could not find the segment: " + filePathData.getFilePath() + " - check docbase and filename form a valid path\n");
        }
        return processMessage;
    }

    private void checkValidityOfUrls(String docbase, List<String> allReferencedExternalUrls, ProcessMessage processMessage) {
        for (String referencedFile : allReferencedExternalUrls) {
            if (!storageManager.fileExists(new FilePathData(docbase, referencedFile))) {
                processMessage.addIndentedErrorMessageLine("** Unable to find file '" + referencedFile + "' in the location you specified\n");
            } else {
                processMessage.addIndentedMessageLine("File '" + referencedFile + "' found OK \n");
            }
        }
    }

    private void addPublicationSystemDocument(String docbase, String documentType, String pageName, ArcDoctype arcDocument) {
        DocumentManager documentManager = new WorkflowDocumentManagerImpl(session);
        WorkflowDocumentVariantImportTask importTask = new WorkflowDocumentVariantImportTask(documentManager);
        importTask.setContentNodeBinder(new ArcJcrContentNodeBinder());

        log.info("Starting export task");
        importTask.setLogger(log);
        importTask.start();

        AbstractPageLevelTransformer at = ArcTransformerFactory.getArcTransformerFromDocumentType(documentType);
        at.setDocbase(docbase);
        at.setDoctype(arcDocument);
        at.setSession(session);
        at.setStorageManager(storageManager);

        ContentNode cn = at.process();

        String localizedName = arcDocument.getTitleReq();
        cn.setName(localizedName);

        String[] paths = ContentPathUtils.splitToFolderPathAndName(nodePath);

        importTask.createOrUpdateDocumentFromVariantContentNode(cn, cn.getPrimaryType(), paths[0] + "/" + pageName, null, localizedName);

        ArcJcrContentNodeBinder binder = (ArcJcrContentNodeBinder)importTask.getContentNodeBinder();
        for (ExternalStorageReference reference: binder.getExternalStorageReferences()) {
            System.out.println("Found: " + reference);
        }
    }
}
