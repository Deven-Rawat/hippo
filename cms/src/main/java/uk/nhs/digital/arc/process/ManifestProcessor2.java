package uk.nhs.digital.arc.process;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.jcr.Session;

public class ManifestProcessor2 {
    private static final Logger log = LoggerFactory.getLogger(ManifestProcessor2.class);
    private final String manifestFile;
    private final String nodePath;
    private final boolean preview;

    private ObjectMapper objectMapper = null;
    private Session session = null;

    private ArcStorageManager storageManager;

    public ManifestProcessor2(Session session, String manifestFile, String nodePath) {
        objectMapper = new ObjectMapper();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.session = session;
        this.manifestFile = manifestFile;
        this.nodePath = nodePath;

        this.preview = session == null;

        if (!preview) {
            this.storageManager = new S3StorageManager();
        }
    }

    public MessageBuilder readWrapperFromFile() throws IOException {
        URL wrapperUrl = new URL(manifestFile);
        InputStream in = wrapperUrl.openStream();
        MessageBuilder messages = new MessageBuilder();

        ManifestWrapper wrapper = objectMapper.readValue(in, ManifestWrapper.class);

        //printObjectAsJsonString(wrapper);
        wrapper.getPages().stream().forEach(p -> {
            try {
                messages.addProcessMessage(loadAndProcessPage(wrapper.getDocumentBase(), p));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return messages;
    }

    private ProcessMessage loadAndProcessPage(String docbase, Page page) throws IOException {
        URL wrapperUrl = new URL(docbase + "/" + page.getPageRef() );
        InputStream in = wrapperUrl.openStream();
        ProcessMessage processMessage = new ProcessMessage();
        processMessage.addMessageLine("Now checking the file '" + page.getPageRef() + "', which is used by the page '" + page.getPageName() + "' ...\n");

        try {
            Class jsonDataClass = JsonClassFactory.getJsonDataClassFromDocumentType(page.getDocumentType());

            ArcDoctype jsonObject = (ArcDoctype) objectMapper.readValue(in, jsonDataClass);

            if (!preview) {
                addPublicationSystemDocument(docbase, page.getDocumentType(), page.getPageName(), jsonObject);
            }
            // printObjectAsJsonString(jsonObject);
            processMessage.addMessageLine("... parsed OK\n");
        } catch (Exception e) {
            processMessage.addErrorMessageLine("\nError encountered during parsing:\n");
            processMessage.addErrorMessageLine(ExceptionUtils.getStackTrace(e));
            e.printStackTrace();
        }

        return processMessage;
    }

    private void addPublicationSystemDocument(String docbase, String documentType, String pageName, ArcDoctype arcDocument) {
        DocumentManager documentManager = new WorkflowDocumentManagerImpl(session);
        WorkflowDocumentVariantImportTask importTask = new WorkflowDocumentVariantImportTask(documentManager);
        log.info("Starting export task");
        importTask.setLogger(log);
        importTask.start();

        AbstractPageLevelTransformer at = ArcTransformerFactory.getArcTransformerFromDocumentType(documentType);
        at.setDocbase(docbase);
        at.setDoctype(arcDocument);
        at.setSession(session);
        at.setStorageManager(this.storageManager);

        ContentNode cn = at.process();

        String locale = null;
        String localizedName = arcDocument.getTitleReq();
        cn.setName(localizedName);

        String[] paths = ContentPathUtils.splitToFolderPathAndName(nodePath);

        importTask.createOrUpdateDocumentFromVariantContentNode(cn, cn.getPrimaryType(), paths[0] + "/" + pageName, locale, localizedName);
    }

//    private String printObjectAsJsonString(Object object) throws JsonProcessingException {
//        final String asString = objectMapper.writeValueAsString(object);
//        System.out.println(asString);
//        return asString;
//    }

//    private void handleUpload(FileUpload upload) throws FileUploadViolationException {
//        final PooledS3Connector s3Connector = HippoServiceRegistry.getService(PooledS3Connector.class);
//
//        String fileName = upload.getClientFileName();
//        String mimeType = upload.getContentType();
//
//        try {
//            final S3ObjectMetadata s3ObjectMetadata = s3Connector.upload(
//                wrapCheckedException(upload::getInputStream),
//                fileName,
//                mimeType
//            );
//
//            JcrNodeModel nodeModel = (JcrNodeModel) this.getDefaultModel();
//            Node node = nodeModel.getNode();
//            try {
//                setResourceProperties(node, s3ObjectMetadata);
//            } catch (Exception ex) {
//                throw new RuntimeException(ex);
//            }
//
//        } catch (Exception ex) {
//            log.error("Cannot upload resource", ex);
//            throw new FileUploadViolationException(ex.getMessage());
//        }
//    }
//
//    private void setResourceProperties(Node node, S3ObjectMetadata metadata) throws RepositoryException {
//        node.setProperty(ExternalStorageConstants.PROPERTY_EXTERNAL_STORAGE_LAST_MODIFIED, Calendar.getInstance());
//        node.setProperty(ExternalStorageConstants.PROPERTY_EXTERNAL_STORAGE_FILE_NAME, metadata.getFileName());
//        node.setProperty(ExternalStorageConstants.PROPERTY_EXTERNAL_STORAGE_SIZE, metadata.getSize());
//        node.setProperty(ExternalStorageConstants.PROPERTY_EXTERNAL_STORAGE_REFERENCE, metadata.getReference());
//        node.setProperty(ExternalStorageConstants.PROPERTY_EXTERNAL_STORAGE_PUBLIC_URL, metadata.getUrl());
//        node.setProperty(ExternalStorageConstants.PROPERTY_EXTERNAL_STORAGE_MIME_TYPE, metadata.getMimeType());
//    }
}
