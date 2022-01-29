package uk.nhs.digital.common.components;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.website.beans.General;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GeneralArticleComponent extends DocumentChildComponent {

    private static Logger logger = LoggerFactory.getLogger(GeneralArticleComponent.class);

    @Override
    public void doBeforeRender(final HstRequest hstRequest, final HstResponse hstResponse) {
        super.doBeforeRender(hstRequest, hstResponse);
        final HstRequestContext context = RequestContextProvider.get();
        if (context != null) {
            HttpServletRequest request = context.getServletRequest();
            Object bean = hstRequest.getAttribute(REQUEST_ATTR_DOCUMENT);
            if (bean != null && bean instanceof HippoBean) {
                General generalDocument = (General) bean;
                if (StringUtils.isNotBlank(generalDocument.getEarlyAccessKey()) && !generalDocument.getEarlyAccessKey().equals(request.getParameter("key"))) {
                    logger.debug("Early access key is set and no or wrong key is being used. Redirecting to 404 error code");
                    try {
                        hstResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        hstResponse.sendRedirect(PAGE_NOT_FOUND);
                    } catch (IOException ioe) {
                        logger.error("Error redirecting to PAGE_NOT_FOUND", ioe);
                    }
                }
            }
        }
    }

}
