package uk.nhs.digital.arc.transformer.impl.pagelevel;

import org.onehippo.forge.content.pojo.model.ContentNode;
import org.onehippo.forge.content.pojo.model.ContentPropertyType;
import uk.nhs.digital.arc.json.Dataset;
import uk.nhs.digital.arc.json.publicationsystem.PublicationsystemRelatedlink;
import uk.nhs.digital.arc.json.publicationsystem.PublicationsystemResourcelink;
import uk.nhs.digital.arc.transformer.abs.AbstractPageLevelTransformer;

public class DatasetTransformer extends AbstractPageLevelTransformer {

    private Dataset dataset;

    @Override
    public ContentNode process() {
        dataset = (Dataset) doctype;

        ContentNode cn = new ContentNode(dataset.getTitleReq(), doctype.getDoctypeReq().toLowerCase());

        setPubSystemSingleProp(cn,"Title", dataset.getTitleReq());
        setPubSystemSingleProp(cn,"Summary", dataset.getSummaryReq());
        setPubSystemSingleProp(cn,"NominalDate", dataset.getNominalDateReq());

        setPubSystemMultipleProp(cn,"GeographicCoverage", dataset.getGeographicCoverage());
        setPubSystemMultipleProp(cn,"Granularity", dataset.getGranularity());
        cn.setProperty(PUBLICATION_SYSTEM + "CoverageStart", ContentPropertyType.DATE, dataset.getCoverageStart());
        cn.setProperty(PUBLICATION_SYSTEM + "CoverageEnd", ContentPropertyType.DATE, dataset.getCoverageEnd());

        processAttachments(cn);
        processLinks(cn);
        return cn;
    }

    private void processAttachments(ContentNode cn) {
        for (PublicationsystemRelatedlink link: dataset.getFiles()) {
            String nodeTypeName = "Files-v3";
            String displayName = link.getLinkTextReq();
            String resource = link.getLinkUrlReq();

            populateAndCreateExternalAttachmentNode(cn, nodeTypeName, displayName, resource);
        }
    }

    private void processLinks(ContentNode cn) {
        for (PublicationsystemResourcelink link: dataset.getResourceLinks()) {
            ContentNode linkNode = new ContentNode(PUBLICATION_SYSTEM + "ResourceLinks", PUBLICATION_SYSTEM + "relatedlink");
            linkNode.setProperty(PUBLICATION_SYSTEM + "linkText", link.getLinkTextReq());
            linkNode.setProperty(PUBLICATION_SYSTEM + "linkUrl", link.getLinkUrlReq());

            cn.addNode(linkNode);
        }
    }

}