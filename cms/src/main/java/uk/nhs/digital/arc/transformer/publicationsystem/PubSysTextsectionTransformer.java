package uk.nhs.digital.arc.transformer.publicationsystem;

import org.onehippo.forge.content.pojo.model.ContentNode;
import uk.nhs.digital.arc.json.PublicationBodyItem;
import uk.nhs.digital.arc.json.publicationsystem.PublicationsystemTextsection;
import uk.nhs.digital.arc.transformer.abs.AbstractSectionTransformer;

import javax.jcr.Session;

public class PubSysTextsectionTransformer extends AbstractSectionTransformer {

    private final PublicationsystemTextsection textSection;

    public PubSysTextsectionTransformer(Session session, PublicationBodyItem section) {
        super(session);
        textSection = (PublicationsystemTextsection)section;
    }

    @Override
    public ContentNode process() {
        //ContentNode sectionNode = new ContentNode(PUBLICATION_SYSTEM + "bodySections", PUBLICATION_SYSTEM + "textSection");
        ContentNode sectionNode = new ContentNode(PUBLICATIONSYSTEM_BODYSECTIONS, PUBLICATIONSYSTEM_TEXTSECTION);
        //sectionNode.setProperty(PUBLICATIONSYSTEM + "heading", textSection.getHeading());
        sectionNode.setProperty(PUBLICATIONSYSTEM_HEADING, textSection.getHeading());

        //ContentNode cmAtt = new ContentNode(PUBLICATION_SYSTEM + "text", HIPPOSTD_HTML);
        ContentNode cmAtt = new ContentNode(PUBLICATIONSYSTEM_TEXT, HIPPOSTD_HTML);
        cmAtt.setProperty(HIPPOSTD_CONTENT, textSection.getText());
        sectionNode.addNode(cmAtt);

        return sectionNode;
    }
}
