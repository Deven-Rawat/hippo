<#ftl output_format="HTML">
<#include "../include/imports.ftl">

<@hst.setBundle basename="homepage.website.labels"/>
<@fmt.message key="search-banner.placeholder" var="placeholderText"/>
<@fmt.message key="search-banner.buttonLabel" var="buttonLabel"/>
<@fmt.message key="search-banner.title" var="searchTitle"/>
<@fmt.message key="search-banner.text" var="searchBannerText"/>

<section class="search-banner" aria-label="Search form">
    <div class="grid-wrapper grid-wrapper--collapse">
        <p class="search-banner__headline">${searchBannerText}</p>
        <form role="search" method="get" action="${searchLink}" class="search-banner__form">
            <div>
                <input type="text" name="query" id="query" class="search-banner__input" placeholder="${placeholderText}" value="${query!""}" title="${searchTitle}">
            </div>
            <div>
                <button class="search-banner__button">${buttonLabel}</button>
            </div>
        </form>
    </div>
</section>
