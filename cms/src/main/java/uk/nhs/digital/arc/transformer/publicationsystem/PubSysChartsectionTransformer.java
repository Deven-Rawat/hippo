package uk.nhs.digital.arc.transformer.publicationsystem;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.value.BinaryImpl;
import org.onehippo.forge.content.pojo.model.BinaryValue;
import org.onehippo.forge.content.pojo.model.ContentNode;
import uk.nhs.digital.arc.json.PublicationBodyItem;
import uk.nhs.digital.arc.json.publicationsystem.PublicationsystemChartsection;
import uk.nhs.digital.arc.transformer.abs.AbstractSectionTransformer;
import uk.nhs.digital.arc.util.FilePathUtils;
import uk.nhs.digital.arc.util.HighchartsInputConversionForArc;

import java.io.IOException;

import javax.jcr.Binary;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class PubSysChartsectionTransformer extends AbstractSectionTransformer {

    private final PublicationsystemChartsection chartSection;

    public PubSysChartsectionTransformer(Session session, PublicationBodyItem section) {
        super(session);
        chartSection = (PublicationsystemChartsection)section;
    }

    @Override
    public ContentNode process() {
        ContentNode sectionNode = new ContentNode(PUBLICATION_SYSTEM + "bodySections", PUBLICATION_SYSTEM + "chartSection");

        sectionNode.setProperty(PUBLICATION_SYSTEM + "title", chartSection.getTitleReq());
        sectionNode.setProperty(PUBLICATION_SYSTEM + "type", chartSection.getTypeReq());
        sectionNode.setProperty(PUBLICATION_SYSTEM + "yTitle", chartSection.getyTitleReq());
        getAndChartDataInJsonFromS3File(sectionNode);

        return sectionNode;
    }

    private void getAndChartDataInJsonFromS3File(ContentNode sectionNode) {
        FilePathUtils sourceFilePathUtils = new FilePathUtils(docbase, chartSection.getDataFileReq());

        if (sourceFilePathUtils.isS3Bucket()) {
            S3Object s3object = getS3Object(sourceFilePathUtils);
            S3ObjectInputStream inputStream = s3object.getObjectContent();

            try {
                Binary binaryData = getJsonDataFromS3File(sectionNode, inputStream);

                ContentNode newNode = new ContentNode(PUBLICATION_SYSTEM + "dataFile", PUBLICATION_SYSTEM + "resource");
                //                newNode.setProperty(HIPPO_FILENAME, sourceFilePathUtils.getFilename());
                //                newNode.setProperty(HIPPO_TEXT, new BinaryValue(new byte[0], mimeType, StandardCharsets.UTF_8.displayName()));
                //                newNode.setProperty(JCR_DATA, new BinaryValue(IOUtils.toByteArray(b.getStream()),
                //                    s3object.getObjectMetadata().getContentType(),
                //                    StandardCharsets.UTF_8.displayName()));
                //                newNode.setProperty(JCR_ENCODING, StandardCharsets.UTF_8.displayName());
                //                newNode.setProperty(JCR_LAST_MODIFIED, ContentPropertyType.DATE, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date()));
                //                newNode.setProperty(JCR_MIME_TYPE, mimeType);

                this.addFileRelatedProperties(newNode,
                    new BinaryValue(IOUtils.toByteArray(binaryData.getStream())),
                    s3object.getObjectMetadata().getContentType(),
                    sourceFilePathUtils.getFilename());

                //                newNode.setProperty(HIPPO_FILENAME, sourceFilePathUtils.getFilename());
                //                newNode.setProperty(HIPPO_TEXT, new BinaryValue(new byte[0], mimeType, StandardCharsets.UTF_8.displayName()));
                //                newNode.setProperty(JCR_DATA, new BinaryValue(IOUtils.toByteArray(b.getStream()),
                //                    mimeType,
                //                    StandardCharsets.UTF_8.displayName()));
                //                newNode.setProperty(JCR_ENCODING, StandardCharsets.UTF_8.displayName());
                //                newNode.setProperty(JCR_LAST_MODIFIED, ContentPropertyType.DATE, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date()));
                //                newNode.setProperty(JCR_MIME_TYPE, mimeType);

                sectionNode.addNode(newNode);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
    }

    private Binary getJsonDataFromS3File(ContentNode sectionNode, S3ObjectInputStream inputStream) throws IOException, RepositoryException {
        Binary b = new BinaryImpl(inputStream);
        String json = HighchartsInputConversionForArc.process(chartSection.getTypeReq(),
            chartSection.getTitleReq(),
            chartSection.getyTitleReq(),
            b);

        // String mimeType = s3object.getObjectMetadata().getContentType();
        sectionNode.setProperty(PUBLICATION_SYSTEM + "chartConfig", json);
        return b;
    }
}
