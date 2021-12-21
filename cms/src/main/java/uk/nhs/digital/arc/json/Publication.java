package uk.nhs.digital.arc.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import uk.nhs.digital.arc.json.publicationsystem.*;
import uk.nhs.digital.arc.json.website.WebsiteInfographic;
import uk.nhs.digital.arc.json.website.WebsiteSurvey;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Publication extends ArcDoctype {

    @JsonProperty("nominal_date_REQ")
    private String nominalDateReq;
    @JsonProperty("publically_accessible_REQ")
    private String publicallyAccessibleReq;
    @JsonProperty("summary_REQ")
    private String summaryReq;
    @JsonProperty("seo_summary")
    private String seoSummary;
    @JsonProperty("sections")
    private List<PublicationBodyItem> sections = null;
    @JsonProperty("coverage_start")
    private String coverageStart;
    @JsonProperty("coverage_end")
    private String coverageEnd;
    @JsonProperty("key_facts_head")
    private String keyFactsHead;
    @JsonProperty("key_facts_infographics")
    private List<WebsiteInfographic> keyFactsInfographics = null;
    @JsonProperty("key_facts_tail")
    private String keyFactsTail;
    @JsonProperty("website_survey")
    private WebsiteSurvey websiteSurvey;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
    @JsonProperty("interactive_tools")
    private List<PublicationsystemInteractivetool> interactiveTools = null;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
    @JsonProperty("change_notices")
    private List<PublicationsystemChangenotice> changeNotices = null;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
    @JsonProperty("external_attachments")
    private List<PublicationsystemExternalattachment> attachments = null;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
    @JsonProperty("resource_links")
    private List<PublicationsystemResourcelink> resourceLinks = null;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
    @JsonProperty("related_links")
    private List<PublicationsystemRelatedlink> relatedLinks = null;

    public Publication(@JsonProperty(value = "doctype_REQ", required = true)String doctypeReq,
                       @JsonProperty(value = "nominal_date_REQ", required = true)String nominalDateReq,
                       @JsonProperty(value = "title_REQ", required = true)String titleReq,
                       @JsonProperty(value = "publically_accessible_REQ", required = true)String publicallyAccessibleReq,
                       @JsonProperty(value = "summary_REQ", required = true)String summaryReq) {
        this.doctypeReq = doctypeReq;
        this.nominalDateReq = nominalDateReq;
        this.titleReq = titleReq;
        this.publicallyAccessibleReq = publicallyAccessibleReq;
        this.summaryReq = summaryReq;
    }

    @JsonProperty("nominal_date_REQ")
    public String getNominalDateReq() {
        return nominalDateReq;
    }

    @JsonProperty("nominal_date_REQ")
    public void setNominalDateReq(String nominalDateReq) {
        this.nominalDateReq = nominalDateReq;
    }

    @JsonProperty("publically_accessible_REQ")
    public String getPublicallyAccessibleReq() {
        return publicallyAccessibleReq;
    }

    @JsonProperty("publically_accessible_REQ")
    public void setPublicallyAccessibleReq(String publicallyAccessibleReq) {
        this.publicallyAccessibleReq = publicallyAccessibleReq;
    }

    @JsonProperty("summary_REQ")
    public String getSummaryReq() {
        return summaryReq;
    }

    @JsonProperty("summary_REQ")
    public void setSummaryReq(String summaryReq) {
        this.summaryReq = summaryReq;
    }

    @JsonProperty("seo_summary")
    public String getSeoSummary() {
        return seoSummary;
    }

    @JsonProperty("seo_summary")
    public void setSeoSummary(String seoSummary) {
        this.seoSummary = seoSummary;
    }

    @JsonProperty("sections")
    public List<PublicationBodyItem> getSections() {
        return sections;
    }

    @JsonProperty("sections")
    public void setSections(List<PublicationBodyItem> sections) {
        this.sections = sections;
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

    @JsonProperty("key_facts_head")
    public String getKeyFactsHead() {
        return keyFactsHead;
    }

    @JsonProperty("key_facts_head")
    public void setKeyFactsHead(String keyFactsHead) {
        this.keyFactsHead = keyFactsHead;
    }

    @JsonProperty("key_facts_infographics")
    public List<WebsiteInfographic> getKeyFactsInfographics() {
        return keyFactsInfographics;
    }

    @JsonProperty("key_facts_infographics")
    public void setKeyFactsInfographics(List<WebsiteInfographic> keyFactsInfographics) {
        this.keyFactsInfographics = keyFactsInfographics;
    }

    @JsonProperty("key_facts_tail")
    public String getKeyFactsTail() {
        return keyFactsTail;
    }

    @JsonProperty("key_facts_tail")
    public void setKeyFactsTail(String keyFactsTail) {
        this.keyFactsTail = keyFactsTail;
    }

    @JsonProperty("website_survey")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
    public WebsiteSurvey getWebsiteSurvey() {
        return websiteSurvey;
    }

    @JsonProperty("website_survey")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
    public void setWebsiteSurvey(WebsiteSurvey websiteSurvey) {
        this.websiteSurvey = websiteSurvey;
    }

    @JsonProperty("interactive_tools")
    public List<PublicationsystemInteractivetool> getInteractiveTools() {
        return interactiveTools;
    }

    @JsonProperty("interactive_tools")
    public void setInteractiveTools(List<PublicationsystemInteractivetool> interactiveTools) {
        this.interactiveTools = interactiveTools;
    }

    @JsonProperty("change_notices")
    public List<PublicationsystemChangenotice> getChangeNotices() {
        return changeNotices;
    }

    @JsonProperty("change_notices")
    public void setChangeNotices(List<PublicationsystemChangenotice> changeNotices) {
        this.changeNotices = changeNotices;
    }

    @JsonProperty("external_attachments")
    public List<PublicationsystemExternalattachment> getAttachments() {
        return attachments;
    }

    @JsonProperty("external_attachments")
    public void setAttachments(List<PublicationsystemExternalattachment> attachments) {
        this.attachments = attachments;
    }

    @JsonProperty("resource_links")
    public List<PublicationsystemResourcelink> getResourceLinks() {
        return resourceLinks;
    }

    @JsonProperty("resource_links")
    public void setResourceLinks(List<PublicationsystemResourcelink> resourceLinks) {
        this.resourceLinks = resourceLinks;
    }

    @JsonProperty("related_links")
    public List<PublicationsystemRelatedlink> getRelatedLinks() {
        return relatedLinks;
    }

    @JsonProperty("related_links")
    public void setRelatedLinks(List<PublicationsystemRelatedlink> relatedLinks) {
        this.relatedLinks = relatedLinks;
    }
}