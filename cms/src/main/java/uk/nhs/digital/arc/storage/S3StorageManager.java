package uk.nhs.digital.arc.storage;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import uk.nhs.digital.arc.util.FilePathUtils;
import uk.nhs.digital.externalstorage.s3.S3ObjectKeyGenerator;
import uk.nhs.digital.externalstorage.s3.S3ObjectMetadata;
import uk.nhs.digital.externalstorage.s3.S3SdkConnector;

import java.util.UUID;

public class S3StorageManager implements ArcStorageManager {

    private AmazonS3 s3;

    public S3ObjectMetadata uploadFile(String docbase, String sourceFilePath) {
        FilePathUtils sourceFilePathUtils = new FilePathUtils(docbase, sourceFilePath);
        String targetFileName = sourceFilePathUtils.getFilename();

        final S3ObjectKeyGenerator s3ObjectKeyGenerator = new S3ObjectKeyGenerator(this::newRandomString);

        AmazonS3 s3 = getAmazonS3Client();
        S3SdkConnector s3Connector = new S3SdkConnector(s3, "nhsd-hippo-euwest-1", s3ObjectKeyGenerator);

        if (sourceFilePathUtils.isS3Bucket()) {
            String sourceBucketName = sourceFilePathUtils.getS3Bucketname();
            String noBucketSourceFilePath = sourceFilePathUtils.getFilePathNoBucket();
            S3ObjectMetadata metaData = s3Connector.copyFileFromOtherBucket(noBucketSourceFilePath, sourceBucketName, targetFileName);

            return metaData;
        }
        return null;
    }

    /**
     * Get an instance of the S3 client, or create if not yet available
     * @return AmazonS3 instance
     */
    public AmazonS3 getAmazonS3Client() {
        if (this.s3 == null) {
            AWSCredentialsProvider provider = new EnvironmentVariableCredentialsProvider();
            AmazonS3ClientBuilder s3Builder = AmazonS3ClientBuilder.standard()
                .withCredentials(provider)
                .withRegion(Regions.fromName("eu-west-1"));

            s3 = s3Builder.build();
        }
        return s3;
    }

    private String newRandomString() {
        return UUID.randomUUID().toString();
    }
}
