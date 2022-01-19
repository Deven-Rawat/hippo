package uk.nhs.digital.arc.transformer.publicationsystem;

import org.onehippo.forge.content.pojo.model.ContentNode;
import org.onehippo.forge.content.pojo.model.ContentPropertyType;
import uk.nhs.digital.arc.json.BasicBodyItem;
import uk.nhs.digital.arc.json.publicationsystem.PublicationsystemChangenotice;
import uk.nhs.digital.arc.transformer.abs.AbstractSectionTransformer;
import uk.nhs.digital.arc.util.DateHelper;

import javax.jcr.Session;

public class PubSysChangenoticeTransformer extends AbstractSectionTransformer {

    private final PublicationsystemChangenotice changeNotice;

    public PubSysChangenoticeTransformer(Session session, BasicBodyItem section) {
        super(session);
        changeNotice = (PublicationsystemChangenotice)section;
    }

    @Override
    public ContentNode process() {
        ContentNode sectionNode = new ContentNode(PUBLICATIONSYSTEM_CHANGENOTICE, PUBLICATIONSYSTEM_CHANGENOTICE);
        sectionNode.setProperty(PUBLICATIONSYSTEM_DATE, ContentPropertyType.DATE, DateHelper.massageDate(changeNotice.getDateReq()));
        sectionNode.setProperty(PUBLICATIONSYSTEM_TITLE, changeNotice.getTitle());

        ContentNode cmAtt = new ContentNode(PUBLICATIONSYSTEM_CONTENT, HIPPOSTD_HTML);
        cmAtt.setProperty(HIPPOSTD_CONTENT, changeNotice.getContentReq());
        sectionNode.addNode(cmAtt);

        return sectionNode;
    }
}
