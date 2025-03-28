<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "./iconGenerator.ftl">

<#macro navigationBlockSmall item id colourVariant isDarkMolecule isYellowLink hasHeading>
    <#assign hasTitle = item.title?has_content />
    <#assign hasContent = item.content?has_content />
    <#assign hasImage = item.icon?has_content />
    <#assign hasLink = item.external?has_content || item.internal?has_content />
    <#assign hasLabel = item.label?has_content />
    <#assign label = hasLabel?then(item.label, item.title) />

    <article class="nhsd-m-card nhsd-m-card--with-icon ${isDarkMolecule}" id="${id}">
        <#if hasLink>
            <#if item.internal?has_content>
                <a href="<@hst.link hippobean=item.internal/>" class="nhsd-a-box-link ${isYellowLink}" aria-label="${label}">
            <#else>
                <a href="${item.external}" class="nhsd-a-box-link ${isYellowLink}" aria-label="${label}">
            </#if>
        </#if>
        <div class="nhsd-a-box nhsd-a-box--bg-${colourVariant}">
            <div class="nhsd-m-card__content-box">
                <#if hasTitle>
                    <#if hasHeading>
                        ${hasHeading}
                        <h3 class="nhsd-t-heading-s">${item.title}</h3>
                    <#else>
                        <h2 class="nhsd-t-heading-s">${item.title}</h2>
                    </#if>
                </#if>
                <div class="nhsd-a-icon nhsd-a-icon--size-xxl nhsd-m-card__icon" aria-hidden="true">
                    <@hst.link hippobean=item.icon var="image"/>
                    <#assign imgDescription = item.icon.description />

                    <#if hasImage>
                        <svg xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid meet" focusable="false" viewBox="0 0 16 16">
                            <path d="M8,16l-6.9-4V4L8,0l6.9,4v8L8,16z M2,11.5L8,15l6-3.5v-7L8,1L2,4.5V11.5z"/>
                            <#if image?ends_with("svg")>
                                <#assign lightTxt = "FFFFFF" />
                                <#assign darkTxt = "231F20" />
                                <#assign colour = isDarkMolecule?has_content?then(lightTxt, darkTxt)/>

                                <#assign imageUrl = '${image?replace("/binaries", "/svg-magic/binaries")}' />
                                <#assign imageUrl += "?colour=${colour}" />
                                <image x="4" y="4" width="8" height="8" href="${imageUrl}"/>
                            <#else>
                                <image x="4" y="4" width="8" height="8" href="${image}"/>
                            </#if>
                        </svg>
                    <#else>
                        <svg xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid meet" focusable="false" viewBox="0 0 16 16">
                            <path d="M8,16l-6.9-4V4L8,0l6.9,4v8L8,16z M2,11.5L8,15l6-3.5v-7L8,1L2,4.5V11.5z"/>
                            <svg xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid meet" focusable="false" viewBox="0 0 16 16"  width="42%" height="42%" x="29%" y="29%">
                                <path d="M8.5,15L15,8L8.5,1L7,2.5L11.2,7H1v2h10.2L7,13.5L8.5,15z"/>
                            </svg>
                        </svg>
                    </#if>
                </div>

                <#if hasContent>
                    <p class="nhsd-t-body-s">${item.content}</p>
                </#if>
            </div>
            <div class="nhsd-m-card__button-box">
                <@buildInlineSvg "right" "s" "nhsd-a-arrow"/>
            </div>
        </div>
        <#if hasLink>
            </a>
        </#if>
    </article>
</#macro>
