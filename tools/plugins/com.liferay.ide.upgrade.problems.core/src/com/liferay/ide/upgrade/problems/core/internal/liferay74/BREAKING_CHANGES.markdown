# What are the Breaking Changes for Liferay 7.4?

This document presents a chronological list of changes that break existing functionality, APIs, or contracts with third party Liferay developers or users. We try our best to minimize these disruptions, but sometimes they are unavoidable.

Here are some of the types of changes documented in this file:

* Functionality that is removed or replaced
* API incompatibilities: Changes to public Java or JavaScript APIs
* Changes to context variables available to templates
* Changes in CSS classes available to Liferay themes and portlets
* Configuration changes: Changes in configuration files, like `portal.properties`, `system.properties`, etc.
* Execution requirements: Java version, J2EE Version, browser versions, etc.
* Deprecations or end of support: For example, warning that a certain feature or API will be dropped in an upcoming version.

*This document has been reviewed through commit `211d7a331ffc`.*

## Breaking Changes Contribution Guidelines

Each change must have a brief descriptive title and contain the following information:

* **[Title]** Provide a brief descriptive title. Use past tense and follow the capitalization rules from <http://en.wikibooks.org/wiki/Basic_Book_Design/Capitalizing_Words_in_Titles>.
* **Date:** Specify the date you submitted the change. Format the date as *YYYY-MMM-DD* (e.g., 2014-Feb-25).
* **JIRA Ticket:** Reference the related JIRA ticket (e.g., LPS-12345) (Optional).
* **What changed?** Identify the affected component and the type of change that was made.
* **Who is affected?** Are end-users affected? Are developers affected? If the only affected people are those using a certain feature or API, say so.
* **How should I update my code?** Explain any client code changes required.
* **Why was this change made?** Explain the reason for the change. If applicable, justify why the breaking change was made instead of following a deprecation process.

Here is the template to use for each breaking change (note how it ends with a horizontal rule):

```
### Title
- **Date:**
- **JIRA Ticket:**

#### What changed?

#### Who is affected?

#### How should I update my code?

#### Why was this change made?

---------------------------------------

```

The remaining content of this document consists of the breaking changes listed in ascending chronological order.

## Breaking Changes List

### Removed the liferay-ui:flash Tag [](id=removed-liferay-ui-flash-tag)
- **Date:** 2020-Oct-13
- **JIRA Ticket:** [LPS-121732](https://issues.liferay.com/browse/LPS-121732)

#### What changed? [](id=what-changed-1)

The tag `liferay-ui:flash` has been deleted and is no longer available.

#### Who is affected? [](id=who-is-affected-1)

This affects any development that uses the `liferay-ui:flash` tag.

#### How should I update my code? [](id=how-should-i-update-my-code-1)

If you still need to embed Adobe Flash content in a page, write your own code using a standard mechanism such as `SWFObject`.

#### Why was this change made? [](id=why-was-this-change-made-1)

This change aligns with [Adobe dropping support for Flash](https://www.adobe.com/products/flashplayer/end-of-life.html) in December 31, 2020 and with upcoming versions browsers removing Flash support.

---------------------------------------

### Removed the /portal/flash Path [](id=removed-portal-flash-support)
- **Date:** 2020-Oct-13
- **JIRA Ticket:** [LPS-121733](https://issues.liferay.com/browse/LPS-121733)

#### What changed? [](id=what-changed-2)

Previously you could play an Adobe Flash movie by passing a movie URL as a parameter to the `/portal/flash` public path. The `/portal/flash path` path has been removed.

Additionally, the property and accessors have been removed from `ThemeDisplay` and are no longer available.

#### Who is affected? [](id=who-is-affected-2)

This affects you if you are using the `/c/portal/flash` path directly to show pages with Adobe Flash content.

#### How should I update my code? [](id=how-should-i-update-my-code-2)

A direct code update is impossible. However, you can create a custom page that simulates the old behavior. The page could parse movie parameters from the URL and instantiate the movie using the common means for Adobe Flash reproduction.

#### Why was this change made? [](id=why-was-this-change-made-2)

This change aligns with [Adobe dropping support for Flash](https://www.adobe.com/products/flashplayer/end-of-life.html) in December 31, 2020 and with upcoming versions browsers removing Flash support.

---------------------------------------

### Removed the swfobject AUI Module [](id=#)
- **Date:** 2020-Oct-13
- **JIRA Ticket:** [LPS-121736](https://issues.liferay.com/browse/LPS-121736)

#### What changed? [](id=what-changed-3)

The AUI module `swfobject` has been removed. It provided a way to load the SWFObject library, commonly used to embed Adobe Flash content.

#### Who is affected? [](id=who-is-affected-3)

This affects you if you are making the SWFObject library available globally via the AUI `swfobject` module.

#### How should I update my code? [](id=how-should-i-update-my-code-3)

If you still need to embed Adobe Flash content, you can inject the SWFObject library directly in your application using other available mechanisms.

#### Why was this change made? [](id=why-was-this-change-made-3)

This change aligns with [Adobe dropping support for Flash](https://www.adobe.com/products/flashplayer/end-of-life.html) in December 31, 2020 and with upcoming versions browsers removing Flash support.

---------------------------------------

### Refactored AntivirusScanner Support and Clamd Integration [](id=refactor-clamd-integration-antivirusscanner-properties)
- **Date:** 2020-Oct-21
- **JIRA Ticket:** [LPS-122280](https://issues.liferay.com/browse/LPS-122280)

#### What changed? [](id=what-changed-4)

The portal's Clamd integration implementation has been replaced by an OSGi service that uses a Clamd remote service. The AntivirusScanner OSGi integration has replaced the AntivirusScanner implementation selection portal properties and AntivirusScanner implementation hook registration portal properties.

#### Who is affected? [](id=who-is-affected-4)

This affects you if you are using the portal's Clamd integration implementation or if you are providing your own AntivirusScanner implementation via a hook.

#### How should I update my code? [](id=how-should-i-update-my-code-4)

Enable the new Clamd integration and AntivirusScanner support. See [Enabling Antivirus Scanning for Uploaded Files](https://learn.liferay.com/dxp/latest/en/system-administration/file-storage/enabling-antivirus-scanning-for-uploaded-files.html).

If you are providing your own AntivirusScanner implementation via a hook, convert it to an OSGi service that has a service ranking higher greater than zero. The Clamd remote service AntivirusScanner implementation service ranking is zero.

#### Why was this change made? [](id=why-was-this-change-made-4)

This change supports container environments better and unifies the API to do OSGi integration.

---------------------------------------

### Removed the AssetEntries_AssetCategories Table and Corresponding Code [](id=removed-assetentries-assetcatagories-table)
- **Date:** 2020-Oct-16
- **JIRA Ticket:** [LPS-89065](https://issues.liferay.com/browse/LPS-89065)

#### What changed? [](id=what-changed-5)

The `AssetEntries_AssetCategories` mapping table and its corresponding code have been removed.

#### Who is affected? [](id=who-is-affected-5)

This affects you if you use `AssetEntryLocalService` and `AssetCategoryLocalService` to operate on relationships between asset entries and asset categories.

#### How should I update my code? [](id=how-should-i-update-my-code-5)

Use the new methods in `AssetEntryAssetCategoryRelLocalService`. Note, that the method signatures are the same as they were in `AssetEntryAssetCategoryRelLocalService`.

#### Why was this change made? [](id=why-was-this-change-made-5)

This change removes an unnecessary table and corresponding code.

It is a followup step to the [Liferay AssetEntries_AssetCategories Is No Longer Used](https://learn.liferay.com/dxp/latest/en/liferay-internals/reference/7-2-breaking-changes.html#liferay-assetentries-assetcategories-is-no-longer-used) 7.2 breaking change where the table was replaced by the `AssetEntryAssetCategoryRel` table and the corresponding interfaces in `AssetEntryLocalService` and `AssetCategoryLocalService` were moved into `AssetEntryAssetCategoryRelLocalService`.

---------------------------------------

### Changed Entity Display Page Registration Tracking Logic [](id=#)
- **Date:** 2020-Oct-27
- **JIRA Ticket:** [LPS-122275](https://issues.liferay.com/browse/LPS-122275)

#### What changed? [](id=what-changed-6)

The persisting (tracking) of entity associations with display pages has changed. In Liferay DXP/Portal versions 7.1 through 7.3, only entity associations with default display pages were persisted; entities with no display page associations and entity associations with specific, non-default display pages were not persisted. This change switches the behavior.

Here is the new behavior:

- Entities linked to a default display page are not persisted.
- Entities that do not have a display page are persisted.
- Entities that have a specific, non-default display page are persisted.

#### Who is affected? [](id=who-is-affected-6)

This affects you if you have custom entities for which display pages can be created.

#### How should I update my code? [](id=how-should-i-update-my-code-6)

If you have custom entities with display pages, swap the display page logic by adding the `BaseUpgradeAssetDisplayPageEntries` upgrade process to your application. The process receives a table, primary key column name, and a class name.

#### Why was this change made? [](id=why-was-this-change-made-6)

This change makes the display page logic more consistent with the overall display page concept.

---------------------------------------

### Removed Deprecated and Unused JSP Tags [](id=remove-deprecated-jsp-tags)
- **Date:** 2020-Nov-24
- **JIRA Ticket:** [LPS-112476](https://issues.liferay.com/browse/LPS-112476)

#### What changed? [](id=what-changed-7)

A series of deprecated and unused JSP tags have been removed and are no longer available. These tags are included:

- `clay:table`
- `liferay-ui:alert`
- `liferay-ui:input-scheduler`
- `liferay-ui:organization-search-container-results`
- `liferay-ui:organization-search-form`
- `liferay-ui:ratings`
- `liferay-ui:search-speed`
- `liferay-ui:table-iterator`
- `liferay-ui:toggle-area`
- `liferay-ui:toggle`
- `liferay-ui:user-search-container-results`
- `liferay-ui:user-search-form`

#### Who is affected? [](id=who-is-affected-7)

This affects you if you are using any of the removed tags.

#### How should I update my code? [](id=how-should-i-update-my-code-7)

See the 7.3 [`liferay-ui.tld`](https://github.com/liferay/liferay-portal/blob/7.3.x/util-taglib/src/META-INF/liferay-ui.tld) for removed tags that have replacements. However, many of the tags have no direct replacement. If you still need to use a tag that does not have a direct replacement, you can copy the old implementation and serve it directly from your project.

#### Why was this change made? [](id=why-was-this-change-made-7)

These tags were removed in an attempt to clarify the default JSP component offering and to focus on providing a smaller but higher quality component set.

---------------------------------------

### Replaced the .container-fluid-1280 CSS Class (id=#)
- **Date:** 2020-Nov-24
- **JIRA Ticket:** [LPS-123894](https://issues.liferay.com/browse/LPS-123894)

#### What changed? [](id=what-changed-8)

The `.container-fluid-1280` CSS class has been replaced with `.container-fluid.container-fluid-max-xl`. The compatibility layer that had `.container-fluid-1280`'s style has been removed too.

#### Who is affected? [](id=who-is-affected-8)

This affects you if your container elements have the `.container-fluid-1280` CSS class.

#### How should I update my code? [](id=how-should-i-update-my-code-8)

Use the updated CSS classes from Clay `.container-fluid.container-fluid-max-xl` instead of `.container-fluid-1280`. Alternatively, use ClayLayout [Components](https://clayui.com/docs/components/layout.html) and [TagLibs](https://clayui.com/docs/get-started/using-clay-in-jsps.html#clay-sidebar).

#### Why was this change made? [](id=why-was-this-change-made-8)

The change removes deprecated legacy code and improves code consistency and performance.

---------------------------------------

### Disabled Runtime Minification of CSS and JavaScript Resources by Default [](id=#)
- **Date:** 2020-Nov-27
- **JIRA Ticket:** [LPS-123550](https://issues.liferay.com/browse/LPS-123550)

#### What changed? [](id=what-changed-9)

The `minifier.enable` portal property now defaults to `false`. Instead of performing minification of CSS and JS resources at run time, we prepare pre-minified resources at build time. There should be no user-visible changes in page styles or logic.

#### Who is affected? [](id=who-is-affected-9)

This affects you if your implementations depend on the runtime minifier (usually the Google Closure Compiler).

#### How should I update my code? [](id=how-should-i-update-my-code-9)

If you want to maintain the former runtime minification behavior, set the `minifier.enable` portal property to `true`.

#### Why was this change made? [](id=why-was-this-change-made-9)

Moving frontend resource minification from run time to build time reduces server load and facilitates using the latest minification technologies available within the frontend ecosystem.

---------------------------------------

### Removed the SoyPortlet Class [](id=removed-soy-portlet)
- **Date:** 2020-Dec-9
- **JIRA Ticket:** [LPS-122955](https://issues.liferay.com/browse/LPS-122955)

#### What changed? [](id=what-changed-10)

The `SoyPortlet` class has been removed. It used to implement a portlet whose views were backed by Closure Templates (Soy).

#### Who is affected? [](id=who-is-affected-10)

This affects you if you are using `SoyPortlet` as a base for your portlet developments.

#### How should I update my code? [](id=how-should-i-update-my-code-10)

We heavily recommend re-writing your Soy portlets using either a well established architecture such as `MVCPortlet` using JSPs or a particular frontend framework of your choice.

#### Why was this change made? [](id=why-was-this-change-made-10)

This was done as a way to simplify our frontend technical offering and better focus on proven technologies with high demand in the market.

A further exploration and analysis of the different front-end options available can be found in [The State of Frontend Infrastructure](https://liferay.dev/blogs/-/blogs/the-state-of-frontend-infrastructure) including a rationale on why we are moving away from Soy:

> Liferay invested several years into Soy believing it was "the Holy Grail". We believed the ability to compile Closure Templates would provide us with performance comparable to JSP and accommodate reusable components from other JavaScript frameworks. While Soy came close to achieving some of our goals, we never hit the performance we wanted and more importantly, we felt like we were the only people using this technology.

---------------------------------------

### Removed Server-side Closure Templates (Soy) Support [](id=removed-server-side-closure-templates-support)
- **Date:** 2020-Dec-14
- **JIRA Ticket:** [LPS-122956](https://issues.liferay.com/browse/LPS-122956)

#### What changed? [](id=what-changed-11)

The following modules and the classes they exported to allow Soy rendering server-side have been removed:
- `portal-template-soy-api`
- `portal-template-soy-impl`
- `portal-template-soy-context-contributor`

To simplify the migration, the following modules are deprected but available to provide only client-side initialization of previous Soy components:
- `portal-template-soy-renderer-api`
- `portal-template-soy-renderer-impl`

#### Who is affected? [](id=who-is-affected-11)

This affects you if you are using removed classes like `SoyContext`, `SoyHTMLData`, etc. and declaring `TemplateContextContributor` using `LANG_TYPE_SOY` as the value for the `lang.type` attribute.

This affects you if you are initializing Soy components using our Soy `ComponentRenderer`.

#### How should I update my code? [](id=how-should-i-update-my-code-11)

There is no replacement for the removed Soy support. If the first scenario describes you, switch to a different supported template language and rewrite your templates and components.

If you are using `ComponentRenderer`, the only difference should be that your components no longer produce markup server-side. If this is important to you, a temporary workaround has been added. You can manually generate a version of the markup you want to render server-side and pass the markup as a `__placeholder__` property in your `context` parameter. Remember, `ComponentRenderer` is deprecated and will eventually be removed; so we kindly recommend rewriting your component using a different technology.

#### Why was this change made? [](id=why-was-this-change-made-11)

This is done as a way to simplify our frontend technical offering and better focus on proven technologies with high demand in the market.

A further exploration and analysis of the different front-end options available can be found in [The State of Frontend Infrastructure](https://liferay.dev/blogs/-/blogs/the-state-of-frontend-infrastructure) including a rationale on why we are moving away from Soy:

> Liferay invested several years into Soy believing it was "the Holy Grail". We believed the ability to compile Closure Templates would provide us with performance comparable to JSP and accommodate reusable components from other JavaScript frameworks. While Soy came close to achieving some of our goals, we never hit the performance we wanted and more importantly, we felt like we were the only people using this technology.

---------------------------------------

### Removed the spi.id Property From the Log4j XML Definition File [](id=#)
- **Date:** 2021-Jan-19
- **JIRA Ticket:** [LPS-125998](https://issues.liferay.com/browse/LPS-125998)

#### What changed? [](id=what-changed-12)

The `spi.id` property in the Log4j XML definition file has been removed.

#### Who is affected? [](id=who-is-affected-12)

This affects you if you are using `@spi.id@` in its custom Log4j XML definition file.

#### How should I update my code? [](id=how-should-i-update-my-code-12)

Remove `@spi.id@` from your Log4j XML definition file.

#### Why was this change made? [](id=why-was-this-change-made-12)

SPI was removed by [LPS-110758](https://issues.liferay.com/browse/LPS-110758).

---------------------------------------

### Removed Deprecated Attributes From the frontend-taglib-clay Tags [](id=#)
- **Date:** 2021-Jan-26
- **JIRA Ticket:** [LPS-125256](https://issues.liferay.com/browse/LPS-125256)

#### What changed? [](id=what-changed-13)

The deprecated attributes have been removed from the `frontend-taglib-clay` TagLib.

#### Who is affected? [](id=who-is-affected-13)

This affects you if you use deprecated attributes in `<clay:*>` tags.

#### Why was this change made? [](id=why-was-this-change-made-13)

The `frontend-taglib-clay` module is now using components from [`Clay v3`](https://github.com/liferay/clay), which does not support the removed attributes.

---------------------------------------

### Changed Handling of HTML Tag Boolean Attributes [](id=#)
- **Date:** 2021-Feb-18
- **JIRA Ticket:** [LPS-127832](https://issues.liferay.com/browse/LPS-127832)

#### What changed? [](id=what-changed-14)

Boolean HTML attributes will only be rendered if passed a value of `true`. The value for such attributes will be their canonical name.

Previously, a value such as `false` for a `disabled` attribute would be rendered into the DOM as `disabled="false"`; now, it the attribute is omitted. Likewise, a `true` value for a `disabled` attribute was formerly rendered into the DOM as `disabled="true"`; now it is rendered as `disabled="disabled"`.

#### Who is affected? [](id=who-is-affected-14)

This affects you if you are passing the following boolean attributes to tag libraries:

- `"allowfullscreen"`
- `"allowpaymentrequest"`
- `"async"`
- `"autofocus"`
- `"autoplay"`
- `"checked"`
- `"controls"`
- `"default"`
- `"disabled"`
- `"formnovalidate"`
- `"hidden"`
- `"ismap"`
- `"itemscope"`
- `"loop"`
- `"multiple"`
- `"muted"`
- `"nomodule"`
- `"novalidate"`
- `"open"`
- `"playsinline"`
- `"readonly"`
- `"required"`
- `"reversed"`
- `"selected"`
- `"truespeed"`

#### How should I update my code? [](id=how-should-i-update-my-code-14)

Make sure to pass a `true` value to boolean attributes you want present in the DOM. Update CSS selectors that target a `true` value (e.g., `[disabled="true"]`) to target presence of the attribute (e.g., `[disabled]`) or its canonical name (e.g., `[disabled="disabled"]`).

#### Why was this change made? [](id=why-was-this-change-made-14)

This change was made for better compliance with [the HTML Standard](https://html.spec.whatwg.org/#boolean-attribute), which says that "The presence of a boolean attribute on an element represents the true value, and the absence of the attribute represents the false value. If the attribute is present, its value must either be the empty string or a value that is an ASCII case-insensitive match for the attribute's canonical name."

---------------------------------------

### Removed CSS Compatibility Layer [](id=#)
- **Date:** 2021-Jan-2
- **JIRA Ticket:** [LPS-123359](https://issues.liferay.com/browse/LPS-123359)

#### What changed? [](id=what-changed-15)

The support for Boostrap 3 markup has been deleted and is no longer available.

#### Who is affected? [](id=who-is-affected-15)

This affects you if you are using Boostrap 3 markup or if you have not correctly migrated to Boostrap 4 markup.

#### How should I update my code? [](id=how-should-i-update-my-code-15)

If you are using Clay markup you can update it by following the last [Clay components](https://clayui.com/docs/components/index.html) version. If your markup is based on Boostrap 3, you can update the markup with Boostrap 4 markup following the [Bootstrap migration guidelines](https://getbootstrap.com/docs/4.4/migration/).

#### Why was this change made? [](id=why-was-this-change-made-15)

The configurable CSS compatibility layer simplified migrating from Liferay 7.0 to 7.1 but removing the layer resolves conflicts with new styles and improves general CSS weight.

---------------------------------------

### item-selector-taglib No Longer fires coverImage-related Events [](id=#)
- **Date:** 2021-Apr-15
- **JIRA Ticket:** [LPS-130359](https://issues.liferay.com/browse/LPS-130359)

#### What changed? [](id=what-changed-16)

The `ImageSelector` JavaScript module no longer fires the `coverImageDeleted`, `coverImageSelected`, and `coverImageUploaded` events using the `Liferay.fire()` API. These events facilitated communication between the `item-selector-taglib` module and `blogs-web` module. Now `Liferay.State` synchronizes the communication using `imageSelectorCoverImageAtom`.

#### Who is affected? [](id=who-is-affected-16)

This affects you if you are listening for the removed events with `Liferay.on()` or similar functions.

#### How should I update my code? [](id=how-should-i-update-my-code-16)

In practice, you should not observe interaction between these two modules, but if you must, you could subscribe to `imageSelectorCoverImageAtom` using the `Liferay.State.subscribe()` API.

#### Why was this change made? [](id=why-was-this-change-made-16)

`Liferay.fire()` and `Liferay.on()` publish globally visible events on a shared channel. The `Liferay.State` API is a better fit for modules that wish to coordinate at a distance in this way, and it does so in a type-safe manner.

---------------------------------------

### Changed the OAuth 2.0 Token Instrospection Feature Identifier [](id=#)
- **Date:** 2021-May-04
- **JIRA Ticket:** [LPS-131573](https://issues.liferay.com/browse/LPS-131573)

#### What changed? [](id=what-changed-17)

The OAuth 2.0 Token Instrospection Feature Identifier was changed from `token_introspection` to `token.introspection`.

#### Who is affected? [](id=who-is-affected-17)

This affects you if you are using the Token Introspection feature identifier. Here are a couple use cases:

- Adding an OAuth 2.0 application programatically with Token Introspection feature identifier enabled.
- Checking if Token Introspection feature identifier is enabled for an OAuth 2.0 application.

#### How should I update my code? [](id=how-should-i-update-my-code-17)

Change the token from `token_introspection` to `token.introspection`.

#### Why was this change made? [](id=why-was-this-change-made-17)

This change was made to align and standarize all OAuth 2.0 constants in our code. We recommend using a dot to separate words in feature identifiers.

---------------------------------------

### Removed JournalArticle Content Field [](id=#)
- **Date:** 2021-May-21
- **JIRA Ticket:** [LPS-129058](https://issues.liferay.com/browse/LPS-129058)

#### What changed? [](id=what-changed-18)

JournalArticle content is now stored by DDM Field services.

#### Who is affected? [](id=who-is-affected-18)

Anyone directly setting the JournalArticle content field.

#### How should I update my code? [](id=how-should-i-update-my-code-18)

Use the new update methods in `JournalArticleLocalService` instead of setting the content field.

#### Why was this change made? [](id=why-was-this-change-made-18)

To make file, page and web content DDM fields easy to reference in the database without fetching and parsing the content.

---------------------------------------

### Class `com.liferay.portal.kernel.util.StringBundler` has been deprecated [](id=removed-string-bundler-class)
- **Date:** 2021-Jun-25
- **JIRA Ticket:** [LPS-133200](https://issues.liferay.com/browse/LPS-133200)

#### What changed? [](id=what-changed-19)

A number of methods that return type
`com.liferay.portal.kernel.util.StringBundler` have been changed to return type
`com.liferay.petra.string.StringBundler`. This list includes:

- com.liferay.frontend.taglib.dynamic.section.BaseJSPDynamicSection.java#modify
- com.liferay.frontend.taglib.dynamic.section.DynamicSection#modify
- com.liferay.portal.kernel.io.unsync.UnsyncStringWriter#getStringBundler
- com.liferay.portal.kernel.layoutconfiguration.util.RuntimePage#getProcessedTemplate
- com.liferay.portal.kernel.layoutconfiguration.util.RuntimePageUtil#getProcessedTemplate
- com.liferay.portal.kernel.servlet.BufferCacheServletResponse#getStringBundler
- com.liferay.portal.kernel.servlet.taglib.BodyContentWrapper.java#getStringBundler
- com.liferay.portal.kernel.theme.PortletDisplay#getContent
- com.liferay.portal.kernel.util.StringUtil#replaceToStringBundler
- com.liferay.portal.kernel.util.StringUtil#replaceWithStringBundler
- com.liferay.portal.layoutconfiguration.util.PortletRenderer#render
- com.liferay.portal.layoutconfiguration.util.PortletRenderer#renderAjax
- com.liferay.portal.layoutconfiguration.util.RuntimePageImpl#getProcessedTemplate
- com.liferay.taglib.BaseBodyTagSupport#getBodyContentAsStringBundler
- com.liferay.taglib.BodyContentWrapper#getStringBundler
- com.liferay.taglib.aui.NavBarTag#getResponsiveButtonsSB

#### Who is affected? [](id=who-is-affected-19)

Everyone calling one of these methods

#### How should I update my code? [](id=how-should-i-update-my-code-19)

Import `com.liferay.petra.string.StringBundler` instead of
`com.liferay.portal.kernel.util.StringBundler`

#### Why was this change made? [](id=why-was-this-change-made-19)

This change was made in order to deprecate class
`com.liferay.portal.kernel.util.StringBundler`

---------------------------------------

### UserLocalService related classes have modified public API [](id=#)
- **Date:** 2021-Jul-7
- **JIRA Ticket:** [LPS-134096](https://issues.liferay.com/browse/LPS-134096)

#### What changed? [](id=what-changed-20)

A number of methods which normally return `void` now return a `boolean` value instead. This list includes:

- com.liferay.portal.kernel.service.UserLocalService#addDefaultGroups
- com.liferay.portal.kernel.service.UserLocalService#addDefaultRoles
- com.liferay.portal.kernel.service.UserLocalService#addDefaultUserGroups
- com.liferay.portal.kernel.service.UserLocalServiceUtil#addDefaultGroups
- com.liferay.portal.kernel.service.UserLocalServiceUtil#addDefaultRoles
- com.liferay.portal.kernel.service.UserLocalServiceUtil#addDefaultUserGroups
- com.liferay.portal.kernel.service.UserLocalServiceWrapper#addDefaultGroups
- com.liferay.portal.kernel.service.UserLocalServiceWrapper#addDefaultRoles
- com.liferay.portal.kernel.service.UserLocalServiceWrapper#addDefaultUserGroups
- com.liferay.portal.service.impl.UserLocalServiceImpl#addDefaultGroups
- com.liferay.portal.service.impl.UserLocalServiceImpl#addDefaultRoles
- com.liferay.portal.service.impl.UserLocalServiceImpl#addDefaultUserGroups

#### Who is affected? [](id=who-is-affected-20)

Everyone calling one of these methods

#### How should I update my code? [](id=how-should-i-update-my-code-20)

No immediate action is needed, but it's important to note the return type has changed.

#### Why was this change made? [](id=why-was-this-change-made-20)

This change was made in order to check if default groups, roles, and/or user groups were added to the given user, or if the user already had these associations.

---------------------------------------

### frontend-css-web CSS module has been removed [](id=#)
- **Date:** 2021-Aug-02
- **JIRA Ticket:** [LPS-127085](https://issues.liferay.com/browse/LPS-127085)

#### What changed? [](id=what-changed-21)

The frontend-css-web module has been removed and all its CSS files have been upgraded, detailed list here: https://docs.google.com/spreadsheets/d/1gWdQvLJe8KApcY70lxQb5fuvg6XKdwkfMEBLVPkigfU

#### Who is affected? [](id=who-is-affected-21)

This change affects the following modules:

- modules/apps/asset/asset-taglib/
- modules/apps/asset/asset-tags-navigation-web/
- modules/apps/captcha/captcha-taglib/
- modules/apps/comment/comment-web/
- modules/apps/commerce/commerce-product-content-web/
- modules/apps/document-library/document-library-web/
- modules/apps/dynamic-data-lists/dynamic-data-lists-web/
- modules/apps/dynamic-data-mapping/dynamic-data-mapping-form-web/
- modules/apps/dynamic-data-mapping/dynamic-data-mapping-web/
- modules/apps/flags/flags-taglib/
- modules/apps/frontend-css/frontend-css-web/
- modules/apps/frontend-editor/frontend-editor-ckeditor-web/
- modules/apps/frontend-js/frontend-js-aui-web/
- modules/apps/frontend-js/frontend-js-components-web/
- modules/apps/frontend-taglib/frontend-taglib/
- modules/apps/frontend-theme/frontend-theme-styled/
- modules/apps/item-selector/item-selector-taglib/
- modules/apps/knowledge-base/knowledge-base-web/
- modules/apps/mobile-device-rules/mobile-device-rules-web/
- modules/apps/polls/polls-web/
- modules/apps/portal-settings/portal-settings-authentication-cas-web/
- modules/apps/product-navigation/product-navigation-control-menu-web/
- modules/apps/site-navigation/site-navigation-directory-web/
- modules/apps/social/social-bookmarks-taglib/
- modules/apps/staging/staging-taglib/
- modules/apps/wiki/wiki-web/
- modules/dxp/apps/portal-search-tuning/portal-search-tuning-rankings-web/
- portal-kernel/
- portal-web/

#### How should I update my code? [](id=how-should-i-update-my-code-21)

No manual updates are required.

#### Why was this change made? [](id=why-was-this-change-made-21)

This change was made to remove deprecated legacy code from Portal and improve the code consistency and performance

---------------------------------------

### OpenIdConnectServiceHandler interface removed [](id=remove-openid-connect-service-handler)
- **Date:** 2021-Aug-09
- **JIRA Ticket:** [LPS-124898](https://issues.liferay.com/browse/LPS-124898)

#### What changed? [](id=what-changed-22)

In order to deliver improvements to OIDC refresh token handling, the authentication process has been improved to handle post-authentication processing.

The following interface has been removed:

- portal.security.sso.openid.connect.OpenIdConnectServiceHandler

And replaced by:

- portal.security.sso.openid.connect.OpenIdConnectAuthenticationHandler

#### Who is affected? [](id=who-is-affected-22)

Everyone implementing or using this interface directly.

#### How should I update my code? [](id=how-should-i-update-my-code-22)

If the code invokes the old interface, change this to invoke the new interface. This means providing an `UnsafeConsumer` which is responsible for signing in the portal user. If on the other hand you have provided a custom implementation of the interface, then you will need to instead implement the new interface and provide a means of refreshing the user's OIDC access tokens using the provided refresh tokens. Otherwise portal sessions will invalidate upon the expiry of the initial access token.

#### Why was this change made? [](id=why-was-this-change-made-22)

For two reasons:

- To detach the access token refresh process from HTTP request handling. Because this can cause problems maintaining OIDC sessions with providers that only allow refresh tokens to be used once. Resulting in premature portal session invalidation.

- To avoid premature portal session invalidation for OIDC providers that provide refresh tokens that expiry at the same time as their corresponding access tokens.

---------------------------------------

### Some static methods in `com.liferay.portal.kernel.servlet.SanitizedServletResponse` have been removed because these relate to the X-Xss-Protection header which is not supported by modern browsers [](id=removed-sanitized-servlet-response-some-static-method)
- **Date:** 2021-Aug-05
- **JIRA Ticket:** [LPS-134188](https://issues.liferay.com/browse/LPS-134188)

#### What changed? [](id=what-changed-23)

The following methods have been removed:

- com.liferay.portal.kernel.servlet.SanitizedServletResponse#disableXSSAuditor
- com.liferay.portal.kernel.servlet.SanitizedServletResponse#disableXSSAuditor
- com.liferay.portal.kernel.servlet.SanitizedServletResponse#disableXSSAuditorOnNextRequest
- com.liferay.portal.kernel.servlet.SanitizedServletResponse#disableXSSAuditorOnNextRequest

A related constant has been removed also:

- com.liferay.portal.kernel.servlet.HttpHeaders#X_XSS_PROTECTION

Finally, the `http.header.secure.x.xss.protection` portal property has been removed.

#### Who is affected? [](id=who-is-affected-23)

Everyone calling one of these methods or referencing the constant. However, such calling code should be removed anyway, because it will have no effect on modern browsers and might give a false sense of security.

#### How should I update my code? [](id=how-should-i-update-my-code-23)

Remove the calling code. Remove `http.header.secure.x.xss.protection` from `portal-ext.properties` if it exists there.  

#### Why was this change made? [](id=why-was-this-change-made-23)

The X-Xss-Protection header is no longer supported by modern browsers.

---------------------------------------