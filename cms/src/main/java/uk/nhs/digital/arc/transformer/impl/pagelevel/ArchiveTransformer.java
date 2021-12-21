package uk.nhs.digital.arc.transformer.impl.pagelevel;

import org.onehippo.forge.content.pojo.model.ContentNode;
import uk.nhs.digital.arc.json.Archive;
import uk.nhs.digital.arc.transformer.abs.AbstractPageLevelTransformer;

public class ArchiveTransformer extends AbstractPageLevelTransformer {

    @Override
    public ContentNode process() {
        Archive archive = (Archive)doctype;
        ContentNode cn = new ContentNode(archive.getTitleReq(), doctype.getDoctypeReq().toLowerCase());

        setPubSystemSingleProp(cn, "Title", archive.getTitleReq());
        setPubSystemSingleProp(cn, "Summary", archive.getSummaryReq());
        setPubSystemSinglePropOptional(cn, "AdministrativeSources", archive.getAdministrativeSources());

        setPubSystemMultipleProp(cn, "GeographicCoverage", archive.getGeographicCoverage());

        setPubSystemMultipleProp(cn, "InformationType", archive.getInformationType());

        setPubSystemMultipleProp(cn, "Granularity", archive.getGranularity());

        return cn;
    }

}
