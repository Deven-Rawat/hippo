package uk.nhs.digital.arc.json.website;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.nhs.digital.arc.json.PublicationBodyItem;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebsiteIconlist extends PublicationBodyItem {

    @JsonProperty("title")
    private String title;
    @JsonProperty("headinglevel_REQ")
    private String headinglevelReq;
    @JsonProperty("introduction")
    private String introduction;
    @JsonProperty("iconlistitems")
    private List<WebsiteIconlistitem> iconlistitems = null;

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("headinglevel_REQ")
    public String getHeadinglevelReq() {
        return headinglevelReq;
    }

    @JsonProperty("headinglevel_REQ")
    public void setHeadinglevelReq(String headinglevelReq) {
        this.headinglevelReq = headinglevelReq;
    }

    @JsonProperty("introduction")
    public String getIntroduction() {
        return introduction;
    }

    @JsonProperty("introduction")
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    @JsonProperty("iconlistitems")
    public List<WebsiteIconlistitem> getIconlistitems() {
        return iconlistitems;
    }

    @JsonProperty("iconlistitems")
    public void setIconlistitems(List<WebsiteIconlistitem> iconlistitems) {
        this.iconlistitems = iconlistitems;
    }

}