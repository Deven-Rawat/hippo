package uk.nhs.digital.arc.transformer.publicationsystem;

import org.apache.commons.io.IOUtils;
import org.onehippo.forge.content.pojo.model.BinaryValue;
import org.onehippo.forge.content.pojo.model.ContentNode;
import uk.nhs.digital.arc.json.PublicationBodyItem;
import uk.nhs.digital.arc.json.publicationsystem.PublicationsystemImagesection;
import uk.nhs.digital.arc.storage.ArcFileData;
import uk.nhs.digital.arc.transformer.abs.AbstractSectionTransformer;
import uk.nhs.digital.arc.util.FilePathData;

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
        ContentNode sectionNode = new ContentNode(PUBLICATIONSYSTEM_BODYSECTIONS, PUBLICATIONSYSTEM_IMAGESECTION);

        sectionNode.setProperty(PUBLICATIONSYSTEM_ALTTEXT, imageSection.getAltTextReq());
        sectionNode.setProperty(PUBLICATIONSYSTEM_CAPTION, imageSection.getCaption());
        sectionNode.setProperty(PUBLICATIONSYSTEM_IMAGESIZE, imageSection.getImageSizeReq());
        sectionNode.setProperty(PUBLICATIONSYSTEM_LINK, imageSection.getLink());
        getImageDataFromS3File(sectionNode);

        return sectionNode;
    }

    private void getImageDataFromS3File(ContentNode sectionNode) {
        FilePathData sourceFilePathUtils = new FilePathData(docbase, imageSection.getImageReq());
        ArcFileData metadata = storageManger.getFileMetaData(sourceFilePathUtils);

        try {
            ContentNode newNode = new ContentNode(PUBLICATIONSYSTEM_IMAGE, PUBLICATIONSYSTEM_RESOURCE);
            this.addFileRelatedProperties(newNode,
                new BinaryValue(IOUtils.toByteArray(metadata.getDelegateStream())),
                metadata.getContentType(),
                sourceFilePathUtils.getFilename());

            sectionNode.addNode(newNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
