package uk.nhs.digital.arc.transformer.impl.website;

import org.onehippo.forge.content.pojo.model.ContentNode;
import uk.nhs.digital.arc.json.PublicationBodyItem;
import uk.nhs.digital.arc.json.website.WebsiteSection;
import uk.nhs.digital.arc.transformer.abs.AbstractSectionTransformer;

import javax.jcr.Session;

public class PublicationWebsiteSectionTransformer extends AbstractSectionTransformer {

    private final WebsiteSection section;
    private String contentNodeName;

    public PublicationWebsiteSectionTransformer(Session session, PublicationBodyItem section) {
        super(session);
        this.section = (WebsiteSection)section;
        this.setContentNodeName("website:sections");
    }

    /**
     * This transformer does its own work but is also extended for body
     * items (notably {@link BodyItemSectionTransformer}), so we need to
     * set the name of the containing node as a property per case
     * @param contentNodeName is the name of that parent node
     */
    protected void setContentNodeName(String contentNodeName) {
        this.contentNodeName = contentNodeName;
    }

    @Override
    public ContentNode process() {
        ContentNode sn = new ContentNode(contentNodeName, "website:section");

        sn.setProperty("website:headinglevel", section.getHeadingLevel());
        sn.setProperty("website:title", section.getTitle());

        ContentNode htmlNode = new ContentNode("website:html", "hippostd:html");
        htmlNode.setProperty("hippostd:content", section.getHtml());
        sn.addNode(htmlNode);

        return sn;
    }
}
