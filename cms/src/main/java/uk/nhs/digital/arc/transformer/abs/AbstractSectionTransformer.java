package uk.nhs.digital.arc.transformer.abs;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import uk.nhs.digital.arc.storage.S3StorageManager;
import uk.nhs.digital.arc.util.FilePathUtils;

import javax.jcr.Session;

public abstract class AbstractSectionTransformer extends AbstractTransformer {
    public AbstractSectionTransformer(Session session) {
        super.setSession(session);
    }

    public S3Object getS3Object(FilePathUtils sourceFilePathUtils) {
        S3StorageManager stm = (S3StorageManager)storageManger;
        AmazonS3 s3 = stm.getAmazonS3Client();

        S3Object s3object = s3.getObject(new GetObjectRequest(sourceFilePathUtils.getS3Bucketname(),
            sourceFilePathUtils.getFilePathNoBucket()));

        return s3object;
    }
}
