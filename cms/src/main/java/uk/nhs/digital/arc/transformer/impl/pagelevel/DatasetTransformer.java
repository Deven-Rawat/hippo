package uk.nhs.digital.arc.transformer.impl.pagelevel;

import org.onehippo.forge.content.pojo.model.ContentNode;
import org.onehippo.forge.content.pojo.model.ContentPropertyType;
import uk.nhs.digital.arc.json.Dataset;
import uk.nhs.digital.arc.json.publicationsystem.PublicationsystemResourceOrExternalLink;
import uk.nhs.digital.arc.transformer.abs.AbstractPageLevelTransformer;

public class DatasetTransformer extends AbstractPageLevelTransformer {

    private Dataset dataset;

    @Override
    public ContentNode process() {
        dataset = (Dataset) doctype;

        ContentNode cn = new ContentNode(dataset.getTitleReq(), doctype.getDoctypeReq().toLowerCase());

        cn.setProperty(PUBLICATIONSYSTEM_TITLE_UC, dataset.getTitleReq());
        cn.setProperty(PUBLICATIONSYSTEM_SUMMARY, dataset.getSummaryReq());
        cn.setProperty(PUBLICATIONSYSTEM_NOMINALDATE, dataset.getNominalDateReq());

        setMultipleProp(cn, PUBLICATIONSYSTEM_GEOGRAPHICCOVERAGE, dataset.getGeographicCoverage());
        setMultipleProp(cn, PUBLICATIONSYSTEM_GRANULARITY, dataset.getGranularity());
        cn.setProperty(PUBLICATIONSYSTEM_COVERAGESTART, ContentPropertyType.DATE, dataset.getCoverageStart());
        cn.setProperty(PUBLICATIONSYSTEM_COVERAGEEND, ContentPropertyType.DATE, dataset.getCoverageEnd());

        processAttachments(cn);
        processLinks(cn);
        return cn;
    }

    private void processAttachments(ContentNode cn) {
        for (PublicationsystemResourceOrExternalLink link: dataset.getFiles()) {
            String nodeTypeName = "Files-v3";
            String displayName = link.getLinkTextReq();
            String resource = link.getLinkUrlReq();

            populateAndCreateExternalAttachmentNode(cn, nodeTypeName, displayName, resource, PUBLICATIONSYSTEM_RESOURCENODE);
        }
    }

    private void processLinks(ContentNode cn) {
        for (PublicationsystemResourceOrExternalLink link: dataset.getResourceLinks()) {
            //ContentNode linkNode = new ContentNode(PUBLICATION_SYSTEM + "ResourceLinks", PUBLICATION_SYSTEM + "relatedlink");
            ContentNode linkNode = new ContentNode(PUBLICATIONSYSTEM_RESOURCELINKS, PUBLICATIONSYSTEM_RELATEDLINK);
            //linkNode.setProperty(PUBLICATION_SYSTEM + "linkText", link.getLinkTextReq());
            linkNode.setProperty(PUBLICATIONSYSTEM_LINKTEXT, link.getLinkTextReq());
            //linkNode.setProperty(PUBLICATION_SYSTEM + "linkUrl", link.getLinkUrlReq());
            linkNode.setProperty(PUBLICATIONSYSTEM_LINKURL, link.getLinkUrlReq());

            cn.addNode(linkNode);
        }
    }

}