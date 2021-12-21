package uk.nhs.digital.arc.util;

public class FilePathUtils {

    private static final int S3_PROTO_LENGTH = 5;
    private static final int HTTP_PROTO_LENGTH = 7;
    private static final int HTTPS_PROTO_LENGTH = 8;

    private final String filePath;

    public FilePathUtils(String docbase, String filePath) {
        if (filePath.contains("://")) {
            this.filePath = filePath;
        } else {
            this.filePath = docbase + "/" + filePath;
        }
    }

    public boolean isS3Bucket() {
        return filePath != null && filePath.startsWith("s3://");
    }

    public boolean isHttp() {
        return filePath != null && filePath.startsWith("http://");
    }

    public boolean isHttps() {
        return filePath != null && filePath.startsWith("https://");
    }

    public String getS3Bucketname() {
        if (isS3Bucket()) {
            String workPath = filePath.substring(S3_PROTO_LENGTH);
            return firstElement(workPath);
        }

        return null;
    }

    public String getFilename() {
        String[] elements = filePath.split("/");
        return elements != null && elements.length > 0 ? elements[elements.length - 1] : null;
    }

    private String firstElement(String workPath) {
        String[] elements = workPath.split("/");
        return elements != null && elements.length > 0 ? elements[0] : null;
    }

    public String getFilePathNoBucket() {
        return getFolderOrFilePathNoBucket(true);
    }

    public String getFolderPathNoBucket() {
        return getFolderOrFilePathNoBucket(false);
    }

    private String getFolderOrFilePathNoBucket(boolean wantFilename) {
        int offset = 0;
        String bucketName = "";

        if (isS3Bucket()) {
            bucketName = getS3Bucketname();
            offset = S3_PROTO_LENGTH + 1 + bucketName.length();
        }

        if (isHttp()) {
            offset = HTTP_PROTO_LENGTH;
        }

        if (isHttps()) {
            offset = HTTPS_PROTO_LENGTH;
        }

        String workPath = filePath.substring(offset);

        if (wantFilename) {
            return workPath.substring(0, workPath.length());
        } else {
            String fileName = getFilename();
            return workPath.substring(0, workPath.length() - 1 - fileName.length());
        }
    }
}
