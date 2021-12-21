package uk.nhs.digital.arc.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.nhs.digital.common.util.json.JacksonJsonSerialiser;
import uk.nhs.digital.common.util.json.JsonSerialiser;
import uk.nhs.digital.ps.chart.AbstractHighchartsParameters;
import uk.nhs.digital.ps.chart.HighchartsParameters;
import uk.nhs.digital.ps.chart.input.DelegatingHighchartsInputParser;
import uk.nhs.digital.ps.chart.input.HighmapsXlsxInputParser;
import uk.nhs.digital.ps.chart.input.ScatterHighchartsXlsxInputParser;
import uk.nhs.digital.ps.chart.input.SeriesHighchartsXlsxInputParser;
import uk.nhs.digital.ps.chart.model.AbstractHighchartsModel;

import javax.jcr.Binary;
import javax.jcr.RepositoryException;

public class HighchartsInputConversionForArc {

    public static String process(String type, String title, String yTitle, Binary fileData) throws RepositoryException {
        DelegatingHighchartsInputParser parser = new DelegatingHighchartsInputParser(new HighmapsXlsxInputParser(),
            new SeriesHighchartsXlsxInputParser(),
            new ScatterHighchartsXlsxInputParser());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonSerialiser jsonSerialiser = new JacksonJsonSerialiser(objectMapper);

        final AbstractHighchartsParameters chartConfig = new HighchartsParameters(type, title, yTitle, fileData);

        final AbstractHighchartsModel chart = parser.parse(chartConfig);

        final String chartConfigJson = jsonSerialiser.toJson(chart);

        return chartConfigJson;
    }
}
