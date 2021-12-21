package uk.nhs.digital.arc.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PublicationPage extends ArcDoctype {

    @JsonProperty("body")
    private List<PublicationBodyItem> body;

    public PublicationPage(@JsonProperty(value = "doctype_REQ", required = true)String doctypeReq,
                           @JsonProperty(value = "title_REQ", required = true)String titleReq) {
        this.doctypeReq = doctypeReq;
        this.titleReq = titleReq;
    }

    @JsonProperty("body")
    public List<PublicationBodyItem> getBody() {
        return body;
    }

    @JsonProperty("body")
    public void setBody(List<PublicationBodyItem> body) {
        this.body = body;
    }

}