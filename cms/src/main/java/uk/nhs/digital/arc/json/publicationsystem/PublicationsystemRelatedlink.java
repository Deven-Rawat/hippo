package uk.nhs.digital.arc.json.publicationsystem;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonTypeName("related_link")
public class PublicationsystemRelatedlink extends PublicationsystemAbstractlink {

    public PublicationsystemRelatedlink(@JsonProperty(value = "link_text_REQ", required = true) String linkTextReq,
                                         @JsonProperty(value = "link_url_REQ", required = true) String linkUrlReq) {
        super(linkTextReq, linkUrlReq);
    }
}
