package uk.nhs.digital.arc.transformer.abs;

import javax.jcr.Session;

public abstract class AbstractSectionTransformer extends AbstractTransformer {

    public static final String WEBSITE_HEADINGLEVEL = "website:headinglevel";
    public static final String WEBSITE_TITLE = "website:title";
    public static final String WEBSITE_HTML = "website:html";
    public static final String WEBSITE_SECTION = "website:section";

    public static final String WEBSITE_EXPLANATORYLINE = "website:explanatoryLine";
    public static final String WEBSITE_QUALIFYINGINFORMATION = "website:qualifyingInformation";
    public static final String WEBSITE_SECTIONS = "website:sections";
    public static final String WEBSITE_INFOGRAPHIC = "website:infographic";
    public static final String WEBSITE_ICON = "website:icon";
    public static final String WEBSITE_COLOUR = "website:colour";
    public static final String WEBSITE_HEADLINE = "website:headline";

    public AbstractSectionTransformer(Session session) {
        super.setSession(session);
    }
}
