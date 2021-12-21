package uk.nhs.digital.arc.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FilePathUtilsTest {
    @Test
    public void weCanFindBucketNameInS3Url() {
        FilePathUtils fpu = new FilePathUtils("s3://bucketname", "folder/filename.dat");
        assertTrue("Should be recognised as a S3 bucket", fpu.isS3Bucket());
        assertEquals("bucketname", fpu.getS3Bucketname());
    }

    @Test
    public void weCanFindBucketNameInBucketNameOnlyS3Url() {
        FilePathUtils fpu = new FilePathUtils("s3://", "bucketname");
        assertEquals("", fpu.getS3Bucketname());
    }

    @Test
    public void testWeCanIsolateFileNameForS3() {
        FilePathUtils fpu = new FilePathUtils("s3://bucketname/folder", "filename.dat");
        assertEquals("Should find filename.dat as filename", "filename.dat", fpu.getFilename());
    }

    @Test
    public void testWeCanIsolateFileNameForS3WhenOnlyFileName() {
        FilePathUtils fpu = new FilePathUtils("s3://filename.dat", "");
        assertEquals("Should find filename.dat as filename", "filename.dat", fpu.getFilename());
    }

    @Test
    public void testWeCanIsolateFileNameForHttps() {
        FilePathUtils fpu = new FilePathUtils("https://bucketname", "folder/filename.dat");
        assertEquals("Should find filename.dat as filename", "filename.dat", fpu.getFilename());
    }

    @Test
    public void testWeCanIsolateFileNameForHttpsWhenOnlyFilename() {
        FilePathUtils fpu = new FilePathUtils("https://filename.dat", "");
        assertEquals("Should find filename.dat as filename", "filename.dat", fpu.getFilename());
    }

    @Test
    public void testWeCanIsolateFileNameForHttp() {
        FilePathUtils fpu = new FilePathUtils("http://bucketname/folder", "filename.dat");
        assertEquals("Should find filename.dat as filename", "filename.dat", fpu.getFilename());
    }

    @Test
    public void testWeCanIsolateFileNameForHttpWhenOnlyFilename() {
        FilePathUtils fpu = new FilePathUtils("http://filename.dat", "");
        assertEquals("Should find filename.dat as filename", "filename.dat", fpu.getFilename());
    }

    @Test
    public void testWeCanIsolateFilePathForS3() {
        FilePathUtils fpu = new FilePathUtils("s3://bucketname/folder", "filename.dat");
        assertEquals("Should find filename.dat as filename", "folder/filename.dat", fpu.getFilePathNoBucket());
    }

    @Test
    public void testWeCanIsolateFilePathForHttp() {
        FilePathUtils fpu = new FilePathUtils("http://folderone/foldertwo", "filename.dat");
        assertEquals("Should find filename.dat as filename", "folderone/foldertwo/filename.dat", fpu.getFilePathNoBucket());
    }

    @Test
    public void testWeCanIsolateFilePathForHttps() {
        FilePathUtils fpu = new FilePathUtils("https://folderone/foldertwo", "filename.dat");
        assertEquals("Should find filename.dat as filename", "folderone/foldertwo/filename.dat", fpu.getFilePathNoBucket());
    }

    @Test
    public void testWeCanIsolateFolderPathForS3() {
        FilePathUtils fpu = new FilePathUtils("s3://bucketname/folder", "filename.dat");
        assertEquals("Should find filename.dat as filename", "folder", fpu.getFolderPathNoBucket());
    }

    @Test
    public void testWeCanIsolateFolderPathForHttp() {
        FilePathUtils fpu = new FilePathUtils("http://folderone/foldertwo", "filename.dat");
        assertEquals("Should find filename.dat as filename", "folderone/foldertwo", fpu.getFolderPathNoBucket());
    }

    @Test
    public void testWeCanIsolateFolderPathForHttps() {
        FilePathUtils fpu = new FilePathUtils("https://folderone/foldertwo", "filename.dat");
        assertEquals("Should find filename.dat as filename", "folderone/foldertwo", fpu.getFolderPathNoBucket());
    }
}
