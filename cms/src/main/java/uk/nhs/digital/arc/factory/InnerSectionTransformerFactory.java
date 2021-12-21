package uk.nhs.digital.arc.factory;

import uk.nhs.digital.arc.json.PublicationBodyItem;
import uk.nhs.digital.arc.transformer.abs.AbstractSectionTransformer;
import uk.nhs.digital.arc.transformer.impl.website.BodyItemSectionTransformer;
import uk.nhs.digital.arc.transformer.impl.website.EmphasisTransformer;
import uk.nhs.digital.arc.transformer.impl.website.InfographicTransformer;
import uk.nhs.digital.arc.transformer.impl.website.PublicationWebsiteSectionTransformer;
import uk.nhs.digital.arc.transformer.publicationsystem.PubSysChartsectionTransformer;
import uk.nhs.digital.arc.transformer.publicationsystem.PubSysImagesectionTransformer;
import uk.nhs.digital.arc.transformer.publicationsystem.PubSysTextsectionTransformer;

import javax.jcr.Session;

public class InnerSectionTransformerFactory {
    public static AbstractSectionTransformer getWebsiteTransformerFromPublicationSectionType(final PublicationBodyItem section, final Session session) {
        switch (section.getClass().getSimpleName()) {
            case "WebsiteSection":
                return new PublicationWebsiteSectionTransformer(session, section);

            case "WebsiteInfographic":
                return new InfographicTransformer(session, section);

            case "WebsiteEmphasis":
                return new EmphasisTransformer(session, section);

            default:
                return null;
        }
    }

    public static AbstractSectionTransformer getBodyItemTransformerFromPublicationPageSectionType(final PublicationBodyItem section, final Session session) {
        switch (section.getClass().getSimpleName()) {
            case "PublicationsystemChartsection":
                return new PubSysChartsectionTransformer(session, section);

            case "WebsiteSection":
                return new BodyItemSectionTransformer(session, section);

            case "PublicationsystemImagesection":
                return new PubSysImagesectionTransformer(session, section);

            case "PublicationsystemTextsection":
                return new PubSysTextsectionTransformer(session, section);

            default:
                return null;
        }
    }
}
