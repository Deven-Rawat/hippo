package uk.nhs.digital.arc.storage;

import uk.nhs.digital.externalstorage.s3.S3ObjectMetadata;

public interface ArcStorageManager {
    S3ObjectMetadata uploadFile(String docbase, String sourceFilePath);
}
