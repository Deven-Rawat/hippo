package uk.nhs.digital.arc.transformer.publicationsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.openMocks;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.onehippo.forge.content.pojo.model.ContentNode;
import uk.nhs.digital.arc.json.publicationsystem.PublicationsystemChartsection;
import uk.nhs.digital.arc.storage.S3StorageManager;

import java.io.InputStream;

import javax.jcr.Session;

public class PubSysChartsectionTransformerTest {

    @Mock
    AmazonS3 mockS3;

    @Mock
    S3Object mockS3Object;

    @Mock
    Session session;

    @Mock
    S3StorageManager mockStorageManager;

    @Mock
    ObjectMetadata mockS3Metadata;

    @Mock
    S3ObjectInputStream mockS3ObjectInputStream;

    @Before
    public void before() {
        openMocks(this);
    }

    @Test
    public void processNodeWithNoS3DataFileProvided() {
        PublicationsystemChartsection section = getPublicationSystemChartSection("http://filelocation");

        PubSysChartsectionTransformer transformer = new PubSysChartsectionTransformer(session, section);
        ContentNode response = transformer.process();

        assertNull(response.getProperty(PubSysChartsectionTransformer.PUBLICATIONSYSTEM_DATAFILE));
    }

    @Test
    public void processNodeWithAS3DataFileProvided() {
        given(mockS3.getObject(any(GetObjectRequest.class))).willReturn(mockS3Object);
        given(mockS3Object.getObjectContent()).willReturn(mockS3ObjectInputStream);
        given(mockS3ObjectInputStream.getDelegateStream()).willReturn(getInputStreamFromDataFile("test-data/arc-data/quit_date_by_gender.xlsx"));
        given(mockS3Object.getObjectMetadata()).willReturn(mockS3Metadata);
        given(mockS3Metadata.getContentType()).willReturn("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        PublicationsystemChartsection section = getPublicationSystemChartSection("s3://file/location");

        PubSysChartsectionTransformer transformer = new PubSysChartsectionTransformer(session, section);
        transformer.setStorageManager(mockStorageManager);
        ContentNode response = transformer.process();

        //assertNotNull(response.getProperty(PubSysChartsectionTransformer.PUBLICATIONSYSTEM_DATAFILE));
        assertEquals(getChartConfigResponseJson(), response.getProperty(PubSysChartsectionTransformer.PUBLICATIONSYSTEM_CHARTCONFIG).getValue());
    }

    private String getChartConfigResponseJson() {
        return "{\"title\":{\"text\":\"Self-reported and CO validated quitters time series\"},"
                + "\"series\":[{\"name\":\"Male\",\"data\":[{\"name\":\"Number of quit attempts\",\"y\":75.788}]},"
                + "{\"name\":\"Female\",\"data\":[{\"name\":\"Number of quit attempts\",\"y\":103.027}]}],"
                + "\"chart\":{\"type\":\"bar\"},\"xAxis\":{\"title\":{},\"categories\":[\"Number of quit attempts\"]},"
                + "\"yAxis\":{\"title\":{\"text\":\"y Title\"}}}";
    }

    private InputStream getInputStreamFromDataFile(String stream) {
        return this.getClass().getClassLoader().getResourceAsStream(stream);
    }

    @NotNull
    private PublicationsystemChartsection getPublicationSystemChartSection(String location) {
        PublicationsystemChartsection section = new PublicationsystemChartsection();
        section.setTitleReq("Self-reported and CO validated quitters time series");
        section.setTypeReq("Bar");
        section.setyTitleReq("y Title");
        section.setDataFileReq(location);
        return section;
    }
}