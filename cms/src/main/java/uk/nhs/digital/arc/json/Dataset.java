package uk.nhs.digital.arc.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import uk.nhs.digital.arc.json.publicationsystem.PublicationsystemResourceOrExternalLink;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dataset extends ArcDoctype {

    @JsonProperty("summary_REQ")
    private String summaryReq;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
    @JsonProperty("related_links")
    private List<PublicationsystemResourceOrExternalLink> files = null;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
    @JsonProperty("resource_links")
    private List<PublicationsystemResourceOrExternalLink> resourceLinks = null;

    @JsonProperty("nominal_date_REQ")
    private String nominalDateReq;
    @JsonProperty("next_publicaton_date")
    private String nextPublicatonDate;
    @JsonProperty("coverage_start")
    private String coverageStart;
    @JsonProperty("coverage_end")
    private String coverageEnd;
    @JsonProperty("geographic_coverage")
    private List<String> geographicCoverage = null;
    @JsonProperty("granularity")
    private List<String> granularity;

    public Dataset(@JsonProperty(value = "doctype_REQ", required = true)String doctypeReq,
                   @JsonProperty(value = "title_REQ", required = true)String titleReq,
                   @JsonProperty(value = "summary_REQ", required = true)String summaryReq) {
        this.doctypeReq = doctypeReq;
        this.titleReq = titleReq;
        this.summaryReq = summaryReq;
    }

    @JsonProperty("summary_REQ")
    public String getSummaryReq() {
        return summaryReq;
    }

    @JsonProperty("summary_REQ")
    public void setSummaryReq(String summaryReq) {
        this.summaryReq = summaryReq;
    }

    @JsonProperty("related_links")
    public List<PublicationsystemResourceOrExternalLink> getFiles() {
        return files;
    }

    @JsonProperty("related_links")
    public void setFiles(List<PublicationsystemResourceOrExternalLink> files) {
        this.files = files;
    }

    @JsonProperty("resource_links")
    public List<PublicationsystemResourceOrExternalLink> getResourceLinks() {
        return resourceLinks;
    }

    @JsonProperty("resource_links")
    public void setResourceLinks(List<PublicationsystemResourceOrExternalLink> resourceLinks) {
        this.resourceLinks = resourceLinks;
    }

    @JsonProperty("nominal_date_REQ")
    public String getNominalDateReq() {
        return nominalDateReq;
    }

    @JsonProperty("nominal_date_REQ")
    public void setNominalDateReq(String nominalDateReq) {
        this.nominalDateReq = nominalDateReq;
    }

    @JsonProperty("next_publicaton_date")
    public String getNextPublicatonDate() {
        return nextPublicatonDate;
    }

    @JsonProperty("next_publicaton_date")
    public void setNextPublicatonDate(String nextPublicatonDate) {
        this.nextPublicatonDate = nextPublicatonDate;
    }

    @JsonProperty("coverage_start")
    public String getCoverageStart() {
        return coverageStart;
    }

    @JsonProperty("coverage_start")
    public void setCoverageStart(String coverageStart) {
        this.coverageStart = coverageStart;
    }

    @JsonProperty("coverage_end")
    public String getCoverageEnd() {
        return coverageEnd;
    }

    @JsonProperty("coverage_end")
    public void setCoverageEnd(String coverageEnd) {
        this.coverageEnd = coverageEnd;
    }

    @JsonProperty("geographic_coverage")
    public List<String> getGeographicCoverage() {
        return geographicCoverage;
    }

    @JsonProperty("geographic_coverage")
    public void setGeographicCoverage(List<String> geographicCoverage) {
        this.geographicCoverage = geographicCoverage;
    }

    @JsonProperty("granularity")
    public List<String> getGranularity() {
        return granularity;
    }

    @JsonProperty("granularity")
    public void setGranularity(List<String> granularity) {
        this.granularity = granularity;
    }

    @Override
    public List<String> getAllReferencedExternalUrls() {
        ArrayList<String> referencedExternalUrls = new ArrayList<>();
        getFiles().stream().forEach(f -> referencedExternalUrls.add(f.getLinkUrlReq()));
        return new ArrayList<>(referencedExternalUrls);
    }
}