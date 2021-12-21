package uk.nhs.digital.arc.transformer.abs;

import uk.nhs.digital.arc.json.ArcDoctype;

public abstract class AbstractPageLevelTransformer extends AbstractTransformer {
    protected ArcDoctype doctype;

    public void setDoctype(ArcDoctype doctype) {
        this.doctype = doctype;
    }
}
