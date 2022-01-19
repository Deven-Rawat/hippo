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
        return this.process(WEBSITE_SECTIONS);
    }

    public ContentNode process(String nodeName) {
        //* Process section
        ContentNode sectionNode = new ContentNode(nodeName, WEBSITE_INFOGRAPHIC);

        sectionNode.setProperty(WEBSITE_COLOUR, section.getColourReq());
        sectionNode.setProperty(WEBSITE_HEADLINE, section.getHeadlineReq());
        setSingleNodeLevelProperty(sectionNode, WEBSITE_EXPLANATORYLINE, HIPPOSTD_HTML, HIPPOSTD_CONTENT, section.getExplanatoryLine());
        setSingleNodeLevelProperty(sectionNode, WEBSITE_QUALIFYINGINFORMATION, HIPPOSTD_HTML, HIPPOSTD_CONTENT, section.getQualifyingInformation());

        processIcon(sectionNode);

        return sectionNode;
    }

    private void processIcon(ContentNode currentSectionNode) {
        if (!StringUtils.isEmpty(section.getIcon())) {
            try {
                Node iconNode = session.getNode(section.getIcon());
                ContentNode iconContent = setSingleNodeLevelProperty(currentSectionNode, WEBSITE_ICON, "hippogallerypicker:imagelink", "hippo:docbase", iconNode.getIdentifier());
                String[] categories = new String[]{"life", "cms"};
                iconContent.setProperty(HIPPO_VALUES, categories);
                iconContent.setProperty(HIPPO_FACETS, new String[]{});
                iconContent.setProperty(HIPPO_MODES, new String[]{"single"});
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
    }
}
