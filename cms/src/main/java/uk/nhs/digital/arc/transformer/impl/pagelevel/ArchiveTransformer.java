package uk.nhs.digital.arc.transformer.impl.pagelevel;

import org.onehippo.forge.content.pojo.model.ContentNode;
import uk.nhs.digital.arc.json.Archive;
import uk.nhs.digital.arc.transformer.abs.AbstractPageLevelTransformer;

public class ArchiveTransformer extends AbstractPageLevelTransformer {

    @Override
    public ContentNode process() {
        Archive archive = (Archive)doctype;
        ContentNode cn = new ContentNode(archive.getTitleReq(), doctype.getDoctypeReq().toLowerCase());

        cn.setProperty(PUBLICATIONSYSTEM_TITLE_UC, archive.getTitleReq());
        cn.setProperty(PUBLICATIONSYSTEM_SUMMARY,archive.getSummaryReq());
        cn.setProperty(PUBLICATIONSYSTEM_ADMINISTRATIVESOURCES, archive.getAdministrativeSources());
        setMultipleProp(cn,PUBLICATIONSYSTEM_GEOGRAPHICCOVERAGE, archive.getGeographicCoverage());
        // setPubSystemMultipleProp(cn, "GeographicCoverage", archive.getGeographicCoverage());
        setMultipleProp(cn,PUBLICATIONSYSTEM_INFORMATIONTYPE, archive.getInformationType());
        // setPubSystemMultipleProp(cn, "InformationType", archive.getInformationType());
        setMultipleProp(cn, PUBLICATIONSYSTEM_GRANULARITY, archive.getGranularity());
        // setPubSystemMultipleProp(cn, "Granularity", archive.getGranularity());

        return cn;
    }

}
