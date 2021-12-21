package uk.nhs.digital.arc.transformer.impl.pagelevel;

import org.onehippo.forge.content.pojo.model.ContentNode;
import uk.nhs.digital.arc.factory.InnerSectionTransformerFactory;
import uk.nhs.digital.arc.json.Publication;
import uk.nhs.digital.arc.json.PublicationBodyItem;
import uk.nhs.digital.arc.json.publicationsystem.PublicationsystemExternalattachment;
import uk.nhs.digital.arc.transformer.abs.AbstractPageLevelTransformer;
import uk.nhs.digital.arc.transformer.abs.AbstractSectionTransformer;

public class PublicationTransformer extends AbstractPageLevelTransformer {

    private Publication publication;

    @Override
    public ContentNode process() {
        publication = (Publication)doctype;

        ContentNode cn = new ContentNode(publication.getTitleReq(), doctype.getDoctypeReq().toLowerCase());

        setPubSystemSingleProp(cn,"Title", publication.getTitleReq());
        setPubSystemSingleProp(cn,"Summary", publication.getSummaryReq());
        setPubSystemSingleProp(cn,"NominalDate", publication.getNominalDateReq());
        setPubSystemSingleProp(cn,"PubliclyAccessible", "false");

        processSections(cn);
        processAttachments(cn);
        return cn;
    }

    private void processSections(ContentNode cn) {
        for (PublicationBodyItem section : publication.getSections()) {
            AbstractSectionTransformer sectionTransformer =
                InnerSectionTransformerFactory.getWebsiteTransformerFromPublicationSectionType(section, session);
            sectionTransformer.setStorageManager(storageManger);

            ContentNode sectionNode = sectionTransformer.process();
            cn.addNode(sectionNode);
        }
    }

    private void processAttachments(ContentNode cn) {
        for (PublicationsystemExternalattachment attachment : publication.getAttachments()) {
            String nodeTypeName = "Attachments-v3";
            String displayName = attachment.getDisplayName();
            String resource = attachment.getResource();

            populateAndCreateExternalAttachmentNode(cn, nodeTypeName, displayName, resource);
        }
    }
}
