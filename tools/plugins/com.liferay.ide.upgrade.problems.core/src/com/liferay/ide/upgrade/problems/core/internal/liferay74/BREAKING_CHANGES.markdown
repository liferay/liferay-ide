# What are the Breaking Changes for Liferay 7.4?

This document presents a chronological list of changes that break existing
functionality, APIs, or contracts with third party Liferay developers or users.
We try our best to minimize these disruptions, but sometimes they are
unavoidable.

Here are some of the types of changes documented in this file:

* Functionality that is removed or replaced
* API incompatibilities: Changes to public Java or JavaScript APIs
* Changes to context variables available to templates
* Changes in CSS classes available to Liferay themes and portlets
* Configuration changes: Changes in configuration files, like
  `portal.properties`, `system.properties`, etc.
* Execution requirements: Java version, J2EE Version, browser versions, etc.
* Deprecations or end of support: For example, warning that a certain
  feature or API will be dropped in an upcoming version.

*This document has been reviewed through commit `4334fc6cc349`.*

## Breaking Changes Contribution Guidelines

Each change must have a brief descriptive title and contain the following
information:

* **[Title]** Provide a brief descriptive title. Use past tense and follow
  the capitalization rules from
  <http://en.wikibooks.org/wiki/Basic_Book_Design/Capitalizing_Words_in_Titles>.
* **Date:** Specify the date you submitted the change. Format the date as
  *YYYY-MMM-DD* (e.g., 2014-Feb-25).
* **JIRA Ticket:** Reference the related JIRA ticket (e.g., LPS-12345)
  (Optional).
* **What changed?** Identify the affected component and the type of change that
  was made.
* **Who is affected?** Are end-users affected? Are developers affected? If the
  only affected people are those using a certain feature or API, say so.
* **How should I update my code?** Explain any client code changes required.
* **Why was this change made?** Explain the reason for the change. If
  applicable, justify why the breaking change was made instead of following a
  deprecation process.

Here's the template to use for each breaking change (note how it ends with a
horizontal rule):

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
**80 Columns Rule:** Text should not exceed 80 columns. Keeping text within 80
columns makes it easier to see the changes made between different versions of
the document. Titles, links, and tables are exempt from this rule. Code samples
must follow the column rules specified in Liferay's
[Development Style](http://www.liferay.com/community/wiki/-/wiki/Main/Liferay+development+style).

The remaining content of this document consists of the breaking changes listed
in ascending chronological order.

## Breaking Changes List

### The tag liferay-ui:flash is no longer available [](id=#)
- **Date:** 2020-Oct-13
- **JIRA Ticket:** [LPS-121732](https://issues.liferay.com/browse/LPS-121732)

#### What changed? [](id=what-changed-1)

The tag `liferay-ui:flash` has been deleted and is no longer available.

#### Who is affected? [](id=who-is-affected-1)

This affects any development that uses the `liferay-ui:flash` tag to embed
Adobe Flash movies in a page.

#### How should I update my code? [](id=how-should-i-update-my-code-1)

If you still need to embed Adobe Flash content in a page, you would need to
write your own code using one of the standard mechanisms such as `SWFObject`.

#### Why was this change made? [](id=why-was-this-change-made-1)

This change was made to align with [Adobe dropping support for Flash](https://www.adobe.com/products/flashplayer/end-of-life.html)
in December 31, 2020 and browsers removing Flash support in upcoming versions.

---------------------------------------

### The /portal/flash path is no longer available [](id=#)
- **Date:** 2020-Oct-13
- **JIRA Ticket:** [LPS-121733](https://issues.liferay.com/browse/LPS-121733)

#### What changed? [](id=what-changed-2)

The public path `/portal/flash` that could be used to play an Adobe Flash movie
passing the movie URL as a parameter has been removed.

Additionally, the property and accessors have been removed from `ThemeDisplay`
and are no longer accesible.

#### Who is affected? [](id=who-is-affected-2)

This affects people that were using the path `/c/portal/flash` directly to show
pages with Adobe Flash content.

#### How should I update my code? [](id=how-should-i-update-my-code-2)

A direct code update is not possible. One possible solution would be to create
a custom page simulating to simulate the old behaviour and read the different
movie parameters from the URL and then instantiate it using the common means
for Adobe Flash reproduction.

#### Why was this change made? [](id=why-was-this-change-made-2)

This change was made to align with [Adobe dropping support for Flash](https://www.adobe.com/products/flashplayer/end-of-life.html)
in December 31, 2020 and browsers removing Flash support in upcoming versions.

---------------------------------------

### The AUI module `swfobject` is no longer available [](id=#)
- **Date:** 2020-Oct-13
- **JIRA Ticket:** [LPS-121736](https://issues.liferay.com/browse/LPS-121736)

#### What changed? [](id=what-changed-3)

The AUI module `swfobject` that provided a way to load the library SWFObject
commonly used to embed Adobe Flash content has been removed.

#### Who is affected? [](id=who-is-affected-3)

This affects people that were requiring the AUI `swfobject` module as a way to
make the library available globally.

#### How should I update my code? [](id=how-should-i-update-my-code-3)

If you still need to embed Adobe Flash content, you can inject the SWFObject
library directly in your application using any of the available mechanisms.

#### Why was this change made? [](id=why-was-this-change-made-3)

This change was made to align with [Adobe dropping support for Flash](https://www.adobe.com/products/flashplayer/end-of-life.html)
in December 31, 2020 and browsers removing Flash support in upcoming versions.

---------------------------------------

### Refactor Clamd integration to use Clamd remote service and remove portal [](id=#)
properties configuration for AntivirusScanner selection and hook support for
AntivirusScanner registration in favor of AntivirusScanner OSGi integration.

- **Date:** 2020-Oct-21
- **JIRA Ticket:** [LPS-122280](https://issues.liferay.com/browse/LPS-122280)

#### What changed? [](id=what-changed-4)

The portal impl version of Clamd integration has been pulled out as an OSGi
service to use Clamd remote service.
The portal properties configuration for AntivirusScanner implementation
selection and hook support for AntivirusScanner implementation registration has
been removed in favor of the AntivirusScanner OSGi integration.

#### Who is affected? [](id=who-is-affected-4)

This affects people that were using the portal impl version of Clamd integration
and people that were providing their own AntivirusScanner implementation by hook.

#### How should I update my code? [](id=how-should-i-update-my-code-4)

If you were using the portal impl version of Clamd integration, you need to go
to Control Panel -> System Settings -> Security -> category.antivirus to
configure the new Clamd remote service.

If you were providing your own AntivirusScanner implementation by hook, you need
to update your implementation as an OSGi service with a service ranking higher
than Clamd remote service AntivirusScanner implementation which is default to 0.

#### Why was this change made? [](id=why-was-this-change-made-4)

This change was made to better support container environment and unify the api
to do OSGi integration.

---------------------------------------

### The AssetEntries_AssetCategories table and its corresponding code have been removed from the portal [](id=#)
- **Date:** 2020-Oct-16
- **JIRA Ticket:** [LPS-89065](https://issues.liferay.com/browse/LPS-89065)

#### What changed? [](id=what-changed-5)

AssetEntries_AssetCategories and its corresponding code have been removed from
the portal. In 7.2, this mapping table and the corresponding interface were
replaced by the table AssetEntryAssetCategoryRel and the service
AssetEntryAssetCategoryRelLocalService.

#### Who is affected? [](id=who-is-affected-5)

This affects any content or code that relies on calling the old interfaces for
the AssetEntries_AssetCategories relationship, through the
AssetEntryLocalService and AssetCategoryLocalService.

#### How should I update my code? [](id=how-should-i-update-my-code-5)

Use the new methods in AssetEntryAssetCategoryRelLocalService to retrieve the
same data as before. The method signatures haven't changed; they have just been
relocated to a different service.

#### Why was this change made? [](id=why-was-this-change-made-5)

This change was made due to changes resulting from [LPS-76488](https://issues.liferay.com/browse/LPS-76488),
which let developers control the order of a list of assets for a given category.
The breaking changes regarding the service replacement were notified on
2019-Sep-11, this would be the final step to removing the table.

---------------------------------------

### The way we register display pages for entities has changed [](id=#)
- **Date:** 2020-Oct-27
- **JIRA Ticket:** [LPS-122275](https://issues.liferay.com/browse/LPS-122275)

#### What changed? [](id=what-changed-6)

The way default display pages are handled has changed. From Liferay Portal 7.1
through Liferay Portal 7.3 the entities that had a default display page were
persisted in the database while those that don't have display pages associated
to them were ommited. This behaviour has been switched, so that the default
display pages are not persisted and those entities that don't have a display
page associated to them are tracked.

#### Who is affected? [](id=who-is-affected-6)

Everyone with custom entities for which display pages can be created

#### How should I update my code? [](id=how-should-i-update-my-code-6)

If you have custom entities with display pages, we have created a base upgrade
process (`BaseUpgradeAssetDisplayPageEntries`) that receives a table, primary
key column name and a className, that will handle the swap logic.

#### Why was this change made? [](id=why-was-this-change-made-6)

This change was made to make the logic for display pages more consistent with
the overall concept of display pages.

---------------------------------------

### Previously unused and deprecated JSP tags are no longer available [](id=remove-deprecated-jsp-tags)
- **Date:** 2020-Nov-24
- **JIRA Ticket:** [LPS-112476](https://issues.liferay.com/browse/LPS-112476)

#### What changed? [](id=what-changed-7)

A series of deprecated and unused JSP tags have been removed and are no longer
available. This list includes:

- clay:table
- liferay-ui:alert
- liferay-ui:input-scheduler
- liferay-ui:organization-search-container-results
- liferay-ui:organization-search-form
- liferay-ui:ratings
- liferay-ui:search-speed
- liferay-ui:table-iterator
- liferay-ui:toggle-area
- liferay-ui:toggle
- liferay-ui:user-search-container-results
- liferay-ui:user-search-

#### Who is affected? [](id=who-is-affected-7)

Everyone still using one of the removed tags

#### How should I update my code? [](id=how-should-i-update-my-code-7)

Use the new tags for those where replacements were previously avaialable. In
many cases, there's no direct replacement for these tags, so if you still need
to use them, you could make a copy of the old implementation and serve it
directly from your project.

#### Why was this change made? [](id=why-was-this-change-made-7)

This change was made to remove legacy code that was previously signaled for
removal in an attempt to clarify the default JSP component offering and focus
on providing a smaller but higher quality set of compoentns.

---------------------------------------
### The CSS class .container-fluid-1280 has been replaced with .container-fluid.container-fluid-max-xl [](id=#)
- **Date:** 2020-Nov-24
- **JIRA Ticket:** [LPS-123894](https://issues.liferay.com/browse/LPS-123894)

#### What changed? [](id=what-changed-8)

The CSS class `.container-fluid-1280` has been replaced with `.container-fluid.container-fluid-max-xl` and the compatibility layer that had its style has been removed from Portal.

#### Who is affected? [](id=who-is-affected-8)

All the container elements that had the CSS class `.container-fluid-1280`

#### How should I update my code? [](id=how-should-i-update-my-code-8)

The first recommendation is to use the updated CSS classes from Clay `.container-fluid.container-fluid-max-xl` instead of `.container-fluid-1280`. The second one is to use ClayLayout [Components](https://clayui.com/docs/components/layout.html) & [Taglibs](https://clayui.com/docs/get-started/using-clay-in-jsps.html#clay-sidebar)

#### Why was this change made? [](id=why-was-this-change-made-8)

This change was made to remove deprecated legacy code from Portal and improve the code consistency and performance

---------------------------------------

### Runtime minification of CSS and JS resources is now disabled by default [](id=#)
- **Date:** 2020-Nov-27
- **JIRA Ticket:** [LPS-123550](https://issues.liferay.com/browse/LPS-123550)

#### What changed? [](id=what-changed-9)

The `minifier.enable` setting in `portal.properties` now defaults to
`false`. Instead of performing run-time minification of CSS and JS
resources, we prepare pre-minified resources at build-time. There should
be no user-visible changes in page styles or logic.

#### Who is affected? [](id=who-is-affected-9)

Anybody who relies on specific implementation details of the run-time minifier
(usually the Google Closure Compiler).

#### How should I update my code? [](id=how-should-i-update-my-code-9)

If you wish to maintain the run-time minification behavior, you can set
`minifier.enable` back to `true` in `portal.properties`.

#### Why was this change made? [](id=why-was-this-change-made-9)

By moving minification of frontend resources from run-time to build-time
we reduce server load and gain access to the latest minification
technologies available within the frontend ecosystem.

---------------------------------------

### SoyPortlet is no longer available [](id=#removed-soy-portlet)
- **Date:** 2020-Dec-9
- **JIRA Ticket:** [LPS-122955](https://issues.liferay.com/browse/LPS-122955)

#### What changed? [](id=what-changed-10)

The class `SoyPortlet` used to implement Portlet whose views are backed by
Closure Templates (Soy) has been removed and is no longer available.

#### Who is affected? [](id=who-is-affected-10)

Anyone using `SoyPortlet` as a base for their portlet developments.

#### How should I update my code? [](id=how-should-i-update-my-code-10)

We heavily recommend re-writing your Soy portlets using either a well
established architecture such as `MVCPortlet` using JSPs or a particular frontend
framework of your choice.

As a temporary measure, you could alternatively copy all the necessary removed
classes into you own. However, support for Soy templates is likely to be removed
in this version as well so doing this might require a lot of work.

#### Why was this change made? [](id=why-was-this-change-made-10)

This is done as a way to simplify our frontend technical offering and better
focus on proven technologies with high demand in the market.

A further exploration and analysis of the different frontend options available
can be found in [The State of Frontend Infrastructure](https://liferay.dev/blogs/-/blogs/the-state-of-frontend-infrastructure) including a rationale on why we're moving
away from Soy:

> Liferay has invested several years into Soy believing it was the holy grail.
> We believed the ability to compile Closure templates would provide us the
> performance of JSP with the reusable components of other JavaScript
> frameworks. While it came close to achieving some of those goals, we never
> hit the performance we wanted and more importantly, it always felt like we
> were the only people using this technology.

---------------------------------------

### Server-side Closure Templates (Soy) Support has been removed [](id=server-side-closure-templates-support-has-been-removed)
- **Date:** 2020-Dec-14
- **JIRA Ticket:** [LPS-122956](https://issues.liferay.com/browse/LPS-122956)

#### What changed? [](id=what-changed-11)

The following modules and the classes they exported to allow Soy rendering
server-side have been removed:
- `portal-template-soy-api`
- `portal-template-soy-impl`
- `portal-template-soy-context-contributor`

To simplify the migration, the following modules remain available in a deprecated
deprecated fashion providing only client-side initialization of previous Soy
components:
- `portal-template-soy-renderer-api`
- `portal-template-soy-renderer-impl`

#### Who is affected? [](id=who-is-affected-11)

Anyone directly using removed classes like `SoyContext`, `SoyHTMLData`... or
declaring `TemplateContextContributor` using `LANG_TYPE_SOY` as the value for
the `lang.type` attribute.

Developers using our Soy `ComponentRenderer` to initialize Soy components.

#### How should I update my code? [](id=how-should-i-update-my-code-11)

There is no replacement for the removed Soy support. If you fall under the first
scenario, we recommend switching to a different supported template language and
rewrite your templates and components.

If you're using `ComponentRenderer`, the only difference should be that your
components no longer produce markup server-side. If this is important to you, a
temporary workaround has been added. You can manually generate a version of the
markup you want to render server-side and pass it as a `__placeholder__` property
in your `context` parameter. Keep in mind that `ComponentRenderer` is deprecated
and will go away in the future, so we kindly recommend that you rewrite your
component using a different technology.

#### Why was this change made? [](id=why-was-this-change-made-11)

This is done as a way to simplify our frontend technical offering and better
focus on proven technologies with high demand in the market.

A further exploration and analysis of the different frontend options available
can be found in [The State of Frontend Infrastructure](https://liferay.dev/blogs/-/blogs/the-state-of-frontend-infrastructure) including a rationale on why we're moving
away from Soy:

> Liferay has invested several years into Soy believing it was the holy grail.
> We believed the ability to compile Closure templates would provide us the
> performance of JSP with the reusable components of other JavaScript
> frameworks. While it came close to achieving some of those goals, we never
> hit the performance we wanted and more importantly, it always felt like we
> were the only people using this technology.

---------------------------------------