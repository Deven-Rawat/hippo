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

/**
 * This class is used to determine which of the various instances of the {@link uk.nhs.digital.arc.transformer.abs.AbstractTransformer} classes
 * we will use during the process of extracting data from a Json class and adding it to a content node
 *
 * @author Ian Pearce
 */
public class InnerSectionTransformerFactory {

    /**
     * A {@link uk.nhs.digital.arc.json.Publication} contains {@link PublicationBodyItem} instances in an array. Each of those instances will require
     * a {@link uk.nhs.digital.arc.transformer.abs.AbstractSectionTransformer}  class to facilitate that extraction in order to add teh data to a ContentNode
     *
     * @param section is the section that has been found inside the PublicationPage. It is of type {@link PublicationBodyItem}
     * @param session is used as part of the initialisation of the transformer class
     * @return an instance of an {@link AbstractSectionTransformer} which wil extract the values and add to a content node
     */
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

    /**
     * A {@link uk.nhs.digital.arc.json.PublicationPage} has a number of {@link PublicationBodyItem} instances in an array. Each of those instances will require
     * a {@link uk.nhs.digital.arc.transformer.abs.AbstractSectionTransformer}  class to facilitate that extraction in order to add teh data to a ContentNode
     *
     * @param section is the section that has been found inside the PublicationPage. It is of type {@link PublicationBodyItem}
     * @param session is used as part of the initialisation of the transformer class
     * @return an instance of an {@link AbstractSectionTransformer} which wil extract the values and add to a content node
     */
    public static AbstractSectionTransformer getBodyItemTransformerFromPublicationPageSectionType(final PublicationBodyItem section, final Session session) {
        // Note: As more sections are required to be converted (and thus more Transformer classes), then more
        // clauses will be need in the switch statement below
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
