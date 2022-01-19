package uk.nhs.digital.arc.storage;

import org.onehippo.cms7.services.HippoServiceRegistry;
import uk.nhs.digital.arc.util.FilePathData;
import uk.nhs.digital.externalstorage.s3.PooledS3Connector;
import uk.nhs.digital.externalstorage.s3.S3ObjectMetadata;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

public class S3StorageManager implements ArcStorageManager {

    private PooledS3Connector s3Connector;

    public S3StorageManager() {
    }

    public S3ObjectMetadata uploadFileToS3(String docbase, String sourceFilePath) {
        FilePathData sourceFilePathData = new FilePathData(docbase, sourceFilePath);
        String targetFileName = sourceFilePathData.getFilename();
        S3ObjectMetadata metaData = null;

        //s3 = getAmazonS3Client();
        //S3SdkConnector s3Connector = new S3SdkConnector(s3, "nhsd-hippo-euwest-1", s3ObjectKeyGenerator);

        if (sourceFilePathData.isS3Protocol()) {
            String sourceBucketName = sourceFilePathData.getS3Bucketname();
            String noBucketSourceFilePath = sourceFilePathData.getFilePathNoBucket();

            if (getS3Connector().doesObjectExist(sourceBucketName, noBucketSourceFilePath)) {
                metaData = getS3Connector().copyFileFromOtherBucket(noBucketSourceFilePath, sourceBucketName, targetFileName);
            }
        }
        return metaData;
    }

    @Override
    public boolean fileExists(String fullyReferencedFile) {
        FilePathData filePathData = new FilePathData(fullyReferencedFile);
        return isFileS3AndExists(filePathData);
    }

    @Override
    public boolean fileExists(FilePathData filePathData) {
        return isFileS3AndExists(filePathData);
    }

    private boolean isFileS3AndExists(FilePathData filePathData) {
        //AmazonS3 s3 = getAmazonS3Client();
        if (filePathData.isS3Protocol()) {
            //return s3.doesObjectExist(filePathData.getS3Bucketname(), filePathData.getFilePathNoBucket());
            return getS3Connector().doesObjectExist(filePathData.getS3Bucketname(), filePathData.getFilePathNoBucket());
        }

        return false;
    }

    @Override
    public ArcFileData getFileMetaData(FilePathData sourceFilePathData) {
        if (sourceFilePathData.isS3Protocol()) {
            //S3Object s3object = getS3Object(sourceFilePathData);
            //S3ObjectInputStream s3ObjectInputStream = s3object.getObjectContent();
            // String contentType = s3object.getObjectMetadata().getContentType();

            return getS3Object(sourceFilePathData); // new ArcFileData(delegateStream, contentType);
        }

        return null;
    }

    @Override
    public InputStream getFileInputStream(FilePathData filePathData) {
        ArcFileData fileMetaData = getFileMetaData(filePathData);
        return fileMetaData.getDelegateStream();
    }

    private ArcFileData getS3Object(FilePathData filePathData) {
        // AmazonS3 s3 = getAmazonS3Client();
        final AtomicReference<ArcFileData> arcFileData = new AtomicReference<>();

        getS3Connector().download(filePathData.getS3Bucketname(), filePathData.getFilePathNoBucket(),
            s3File -> {
                arcFileData.set(new ArcFileData(s3File.getContent(), s3File.getContentType()));
            }
        );

        //S3Object s3object = s3.getObject(new GetObjectRequest(filePathData.getS3Bucketname(),
        //    filePathData.getFilePathNoBucket()));

        return arcFileData.get();
    }

    /**
     * Get an instance of the S3 client, or create if not yet available
     * @return AmazonS3 instance
     */
    //    public AmazonS3 getAmazonS3Client() {
    //        if (this.s3 == null) {
    //            PooledS3Connector connector = HippoServiceRegistry.getService(PooledS3Connector.class);
    //            connector.
    //
    //            AWSCredentialsProvider provider = new EnvironmentVariableCredentialsProvider();
    //            AmazonS3ClientBuilder s3Builder = AmazonS3ClientBuilder.standard()
    //                .withCredentials(provider)
    //                .withRegion(Regions.fromName("eu-west-1"));
    //
    //            s3 = s3Builder.build();
    //        }
    //        return s3;
    //    }

    //    public void setS3(AmazonS3 s3) {
    //        this.s3 = s3;
    //    }

    private PooledS3Connector getS3Connector() {
        if (s3Connector == null) {
            s3Connector = HippoServiceRegistry.getService(PooledS3Connector.class);
        }

        return s3Connector;
    }
}
