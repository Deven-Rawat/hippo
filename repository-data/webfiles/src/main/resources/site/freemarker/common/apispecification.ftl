<#ftl output_format="HTML">

<#include "../include/imports.ftl">
<#include "../nhsd-common/macro/heroes/hero.ftl">
<#include "../nhsd-common/macro/heroes/hero-options.ftl">
<#-- @ftlvariable name="document" type="uk.nhs.digital.website.beans.ApiSpecification" -->
<#-- @ftlvariable name="hstRequestContext" type="org.hippoecm.hst.core.request.HstRequestContext" -->

<#if document?? >

    <#assign isTryItNow = hstRequestContext.servletRequest.parameterMap?keys?seq_contains("tryitnow")>
    <#if isTryItNow >

        <#include "api-try-it-now.ftl">

    <#else>
        <#assign renderHtml = "uk.nhs.digital.common.components.apispecification.ApiSpecificationRendererDirective"?new() />

        <#-- Add meta tags -->
        <#include "../common/macro/metaTags.ftl">
        <@metaTags></@metaTags>

        <article itemscope>
            <@hero getHeroOptions(document) />

            <div class="nhsd-t-grid nhsd-!t-margin-top-6">
                <div class="nhsd-t-row"><@renderHtml specificationJson=document.json path=path documentHandleUuid=document.getCanonicalHandleUUID()/></div>
            </div>
        </article>

        <script>
            // used in function tryEndpointNow from apispecification.js
            <@hst.renderURL var="tryItNowUrl"/>
            const tryEndpointNowBaseUrl = '${tryItNowUrl}';
        </script>
        <script src="<@hst.webfile path="/apispecification/apispecification.js"/>"> </script>
    </#if>

</#if>
