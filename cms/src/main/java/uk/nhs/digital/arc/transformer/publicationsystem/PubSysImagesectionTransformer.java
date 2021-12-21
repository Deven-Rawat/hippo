package uk.nhs.digital.arc.transformer.publicationsystem;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.apache.commons.io.IOUtils;
import org.onehippo.forge.content.pojo.model.BinaryValue;
import org.onehippo.forge.content.pojo.model.ContentNode;
import uk.nhs.digital.arc.json.PublicationBodyItem;
import uk.nhs.digital.arc.json.publicationsystem.PublicationsystemImagesection;
import uk.nhs.digital.arc.transformer.abs.AbstractSectionTransformer;
import uk.nhs.digital.arc.util.FilePathUtils;

import java.io.IOException;

import javax.jcr.Session;

public class PubSysImagesectionTransformer extends AbstractSectionTransformer {

    private final PublicationsystemImagesection imageSection;

    public PubSysImagesectionTransformer(Session session, PublicationBodyItem section) {
        super(session);
        imageSection = (PublicationsystemImagesection)section;
    }

    @Override
    public ContentNode process() {
        ContentNode sectionNode = new ContentNode(PUBLICATION_SYSTEM + "bodySections", PUBLICATION_SYSTEM + "imageSection");

        sectionNode.setProperty(PUBLICATION_SYSTEM + "altText", imageSection.getAltTextReq());
        sectionNode.setProperty(PUBLICATION_SYSTEM + "caption", imageSection.getCaption());
        sectionNode.setProperty(PUBLICATION_SYSTEM + "imageSize", imageSection.getImageSizeReq());
        sectionNode.setProperty(PUBLICATION_SYSTEM + "link", imageSection.getLink());
        getImageDataFromS3File(sectionNode);

        return sectionNode;
    }

    private void getImageDataFromS3File(ContentNode sectionNode) {
        FilePathUtils sourceFilePathUtils = new FilePathUtils(docbase, imageSection.getImageReq());

        if (sourceFilePathUtils.isS3Bucket()) {
            S3Object s3object = getS3Object(sourceFilePathUtils);
            S3ObjectInputStream inputStream = s3object.getObjectContent();

            try {
                ContentNode newNode = new ContentNode(PUBLICATION_SYSTEM + "image", PUBLICATION_SYSTEM + "resource");
                this.addFileRelatedProperties(newNode,
                    new BinaryValue(IOUtils.toByteArray(inputStream)),
                    s3object.getObjectMetadata().getContentType(),
                    sourceFilePathUtils.getFilename());

                sectionNode.addNode(newNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
