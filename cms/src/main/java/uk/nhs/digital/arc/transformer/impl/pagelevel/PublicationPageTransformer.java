package uk.nhs.digital.arc.transformer.impl.pagelevel;

import org.onehippo.forge.content.pojo.model.ContentNode;
import uk.nhs.digital.arc.factory.InnerSectionTransformerFactory;
import uk.nhs.digital.arc.json.PublicationBodyItem;
import uk.nhs.digital.arc.json.PublicationPage;
import uk.nhs.digital.arc.transformer.abs.AbstractPageLevelTransformer;
import uk.nhs.digital.arc.transformer.abs.AbstractSectionTransformer;

public class PublicationPageTransformer extends AbstractPageLevelTransformer {

    private PublicationPage publicationPage;

    @Override
    public ContentNode process() {
        publicationPage = (PublicationPage)doctype;

        ContentNode cn = new ContentNode(publicationPage.getTitleReq(), PUBLICATION_SYSTEM + "publicationPage");

        setPubSystemSingleProp(cn,"Title", publicationPage.getTitleReq());

        processSections(cn);

        return cn;
    }

    /**
     * PublicationPage doc types have multiple sections and each section has its own Processor
     *
     * @param cn is the {@link ContentNode} to which we will attach the new node we create
     */
    private void processSections(ContentNode cn) {
        for (PublicationBodyItem bodyItem : publicationPage.getBody()) {
            AbstractSectionTransformer sectionTransformer =
                InnerSectionTransformerFactory.getBodyItemTransformerFromPublicationPageSectionType(bodyItem, session);
            sectionTransformer.setStorageManager(storageManger);

            //* Create the nodes for this section
            ContentNode sectionNode = sectionTransformer.process();
            cn.addNode(sectionNode);
        }
    }
}
