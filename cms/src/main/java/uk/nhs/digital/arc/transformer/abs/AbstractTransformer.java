package uk.nhs.digital.arc.transformer.abs;

import org.apache.jackrabbit.JcrConstants;
import org.onehippo.forge.content.pojo.model.BinaryValue;
import org.onehippo.forge.content.pojo.model.ContentNode;
import org.onehippo.forge.content.pojo.model.ContentPropertyType;
import uk.nhs.digital.arc.storage.ArcStorageManager;
import uk.nhs.digital.externalstorage.s3.S3ObjectMetadata;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.jcr.Session;

public abstract class AbstractTransformer {

    public static final String EXTERNAL_STORAGE = "externalstorage:";
    public static final String PUBLICATION_SYSTEM = "publicationsystem:";

    public static final String HIPPO_TEXT = "hippo:text";
    public static final String HIPPO_FILENAME = "hippo:filename";

    public static final String HIPPOSTD_CONTENT = "hippostd:content";
    public static final String HIPPOSTD_HTML = "hippostd:html";


    protected ArcStorageManager storageManger;
    protected String docbase;
    protected Session session;

    public AbstractTransformer() {
    }

    public void setStorageManager(ArcStorageManager storageManger) {
        this.storageManger = storageManger;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public abstract ContentNode process();

    protected void setSingleProp(ContentNode cn, String property, String value) {
        cn.setProperty(property, value);
    }

    protected void setSinglePropOptional(ContentNode cn, String property, String value) {
        if (property != null) {
            setSingleProp(cn, property, value);
        }
    }

    protected void setMultipleProp(ContentNode cn, String property, List<String> values) {
        if (values != null) {
            cn.setProperty(property, values.toArray(new String[0]));
        }
    }

    protected void setPubSystemSingleProp(ContentNode cn, String property, String value) {
        setSingleProp(cn, PUBLICATION_SYSTEM + property, value);
    }

    protected void setPubSystemSinglePropOptional(ContentNode cn, String property, String value) {
        setSinglePropOptional(cn, PUBLICATION_SYSTEM + property, value);
    }

    protected void setPubSystemMultipleProp(ContentNode cn, String property, List<String> values) {
        setMultipleProp(cn, PUBLICATION_SYSTEM + property, values);
    }


    protected ContentNode setSingleNodeLevelProperty(ContentNode contentNode, String nodeName, String primaryType, String propertyName, String propertyValue) {
        ContentNode newNode = new ContentNode(nodeName, primaryType);
        newNode.setProperty(propertyName, propertyValue);
        contentNode.addNode(newNode);
        return newNode;
    }

    public void setDocbase(String docbase) {
        this.docbase = docbase;
    }

    protected void populateAndCreateExternalAttachmentNode(ContentNode cn, String nodeTypeName, String displayName, String resource) {
        ContentNode attachmentNode = new ContentNode(PUBLICATION_SYSTEM + nodeTypeName, PUBLICATION_SYSTEM + "extattachment");
        attachmentNode.setProperty(EXTERNAL_STORAGE + "displayName", displayName);
        cn.addNode(attachmentNode);

        S3ObjectMetadata s3meta = storageManger.uploadFile(docbase, resource);

        ContentNode cmAtt = new ContentNode(PUBLICATION_SYSTEM + "attachmentResource", EXTERNAL_STORAGE + "resource");
        cmAtt.setProperty(EXTERNAL_STORAGE + "size", ContentPropertyType.LONG, "10000");
        cmAtt.setProperty(EXTERNAL_STORAGE + "url", s3meta.getUrl());
        cmAtt.setProperty(EXTERNAL_STORAGE + "reference", s3meta.getReference());

        addFileRelatedProperties(cmAtt, new BinaryValue(new byte[0]), s3meta.getMimeType(), s3meta.getFileName());
        attachmentNode.addNode(cmAtt);
    }

    protected void addFileRelatedProperties(ContentNode cmAtt, BinaryValue data, String mimeType, String fileName) {
        cmAtt.setProperty(JcrConstants.JCR_DATA, data);
        cmAtt.setProperty(HIPPO_TEXT, new BinaryValue(new byte[0], mimeType, StandardCharsets.UTF_8.displayName()));

        cmAtt.setProperty(HIPPO_FILENAME, fileName);

        cmAtt.setProperty(JcrConstants.JCR_ENCODING, StandardCharsets.UTF_8.displayName());
        cmAtt.setProperty(JcrConstants.JCR_MIMETYPE, mimeType);
        cmAtt.setProperty(JcrConstants.JCR_LASTMODIFIED, ContentPropertyType.DATE, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date()));
    }
}
