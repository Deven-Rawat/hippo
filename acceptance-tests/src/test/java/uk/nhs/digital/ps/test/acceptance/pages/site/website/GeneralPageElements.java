package uk.nhs.digital.ps.test.acceptance.pages.site.website;

import static uk.nhs.digital.ps.test.acceptance.pages.site.website.GeneralPageElements.FieldKeys.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import uk.nhs.digital.ps.test.acceptance.pages.PageHelper;
import uk.nhs.digital.ps.test.acceptance.pages.site.PageElements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralPageElements implements PageElements {

    private static final Map<String, By> pageElements = new HashMap<String, By>() {
        {
            put(GENERAL_TITLE,
                By.xpath("//*[" + getDataUiPathXpath("title") + "]"));
            put(SUMMARY,
                By.xpath("//*[" + getDataUiPathXpath("summary") + "]"));

        }
    };

    public static By getFieldSelector(final String fieldSelectorKey) {
        return pageElements.get(fieldSelectorKey);
    }

    private static String getDataUiPathXpath(String fieldName) {
        return "@data-uipath='document." + fieldName + "'";
    }

    @Override
    public boolean contains(String elementName) {
        return pageElements.containsKey(elementName);
    }

    @Override
    public WebElement getElementByName(String elementName, PageHelper helper) {
        return getElementByName(elementName, 0, helper);
    }

    public WebElement getElementByName(String elementName, int nth, PageHelper helper) {
        List<WebElement> elements = helper.findOptionalElements(pageElements.get(elementName));

        if (elements.size() == 0) {
            return null;
        }

        return elements.get(nth);
    }

    interface FieldKeys {

        String GENERAL_TITLE = "General Title";
        String SUMMARY = "General Summary";
    }
}
