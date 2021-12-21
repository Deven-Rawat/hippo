package uk.nhs.digital.arc.transformer.impl.website;

import org.apache.commons.lang3.StringUtils;
import org.onehippo.forge.content.pojo.model.ContentNode;
import uk.nhs.digital.arc.json.PublicationBodyItem;
import uk.nhs.digital.arc.json.website.WebsiteInfographic;
import uk.nhs.digital.arc.transformer.abs.AbstractSectionTransformer;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class InfographicTransformer extends AbstractSectionTransformer {
    private final WebsiteInfographic section;

    public InfographicTransformer(Session session, PublicationBodyItem section) {
        super(session);
        this.section = (WebsiteInfographic)section;
    }

    @Override
    public ContentNode process() {
        //* Process section
        ContentNode sectionNode = new ContentNode("website:sections", "website:infographic");

        sectionNode.setProperty("website:colour", section.getColourReq());
        sectionNode.setProperty("website:headline", section.getHeadlineReq());
        setSingleNodeLevelProperty(sectionNode, "website:explanatoryLine", "hippostd:html", "hippostd:content", section.getExplanatoryLine());
        setSingleNodeLevelProperty(sectionNode, "website:qualifyingInformation", "hippostd:html", "hippostd:content", section.getQualifyingInformation());

        processIcon(sectionNode);

        return sectionNode;
    }

    private void processIcon(ContentNode currentSectionNode) {
        if (!StringUtils.isEmpty(section.getIcon())) {
            try {
                Node iconNode = session.getNode(section.getIcon());
                ContentNode iconContent = setSingleNodeLevelProperty(currentSectionNode, "website:icon", "hippogallerypicker:imagelink", "hippo:docbase", iconNode.getIdentifier());
                String[] categories = new String[]{"life", "cms"};
                iconContent.setProperty("hippo:values", categories);
                iconContent.setProperty("hippo:facets", new String[]{});
                iconContent.setProperty("hippo:modes", new String[]{"single"});
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
    }
}
