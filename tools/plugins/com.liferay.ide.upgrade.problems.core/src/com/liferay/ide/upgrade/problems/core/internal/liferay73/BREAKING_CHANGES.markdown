# What are the Breaking Changes for Liferay 7.3?

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
* Recommendations: For example, recommending using a newly introduced API that
  replaces an old API, in spite of the old API being kept in Liferay Portal for
  backwards compatibility.

*This document has been reviewed through commit `20bb08f47603`.*

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

### Liferay FontAwesome Is No Longer Included by Default
- **Date:** 2019-Aug-21
- **JIRA Ticket:** [LPS-100021](https://issues.liferay.com/browse/LPS-100021)

#### What changed? [](id=what-changed-1)

Liferay FontAwesome, which included icon fonts for Font Awesome,
Glyphicon, and custom Liferay icons, is no longer included by default.

#### Who is affected? [](id=who-is-affected-1)

This affects pages or sites that have a Theme applied that does not include these icon fonts itself. Any content or code on such pages or sites that uses these icon fonts will no longer work.

#### How should I update my code? [](id=how-should-i-update-my-code-1)

Depending on how you're using icon fonts, there's a few approaches you can take.

##### For liferay-ui:icon usage

Replace `<liferay-ui:icon iconCssClass="icon-user">` with
`<liferay-ui:icon icon="user" markupView="lexicon" />`

##### For JS generated icons

Those manually generating FontAwesome icon html can use the
`Liferay.Util.getLexiconIconTpl('user')` API. For example, the previous call
would return the html code for a user svg icon.

##### For direct html within jsps

Developers directly using icons in jsps can either use the `liferay-ui:icon`
tag as explained above or the `clay:icon` one to generate svg-based icons
instead.

##### For non-controlled code

If you don't have access to the content that uses the icon fonts or you don't want to update the code or content, you can include the fonts in your Themes.

During the 7.2 upgrade process, the theme upgrade assistant prompts developers
to keep FontAwesome as part of the Theme. Themes that already include the icon
fonts won't be affected and will continue to work in 7.3.

#### Why was this change made? [](id=why-was-this-change-made-1)

This change was made to save bandwidth and increase performance of your sites
by not serving unnecessary files.

---------------------------------------

### Removed liferay.frontend.ProgressBar
- **Date:** 2019-Aug-28
- **JIRA Ticket:** [LPS-100122](https://issues.liferay.com/browse/LPS-100122)

#### What changed? [](id=what-changed-2)

The legacy metal+soy `liferay.frontend.ProgressBar` component, used as a
temporary bridge for legacy behaviour, was removed.

#### Who is affected? [](id=who-is-affected-2)

This affects any code that relies on `liferay.frontend.ProgressBar`; this is
usually done via `soy` as `{call liferay.frontend.ProgressBar /}`.

#### How should I update my code? [](id=how-should-i-update-my-code-2)

There's no direct replacement for the `liferay.frontend.ProgressBar` component.
If you have a component that relies on it, you can co-locate a copy of the old
implementation and use it locally within your module.

#### Why was this change made? [](id=why-was-this-change-made-2)

The `liferay.frontend.ProgressBar` component was deprecated in 7.2 and is no
longer used.

---------------------------------------

### Removed liferay.frontend.Slider
- **Date:** 2019-Oct-10
- **JIRA Ticket:** [LPS-100124](https://issues.liferay.com/browse/LPS-100124)

#### What changed? [](id=what-changed-3)

The legacy metal+soy `liferay.frontend.Slider` component, used as a
temporary bridge for legacy behaviour, was removed.

#### Who is affected? [](id=who-is-affected-3)

This affects any code that relies on `liferay.frontend.Slider`; this is
usually done via `soy` as `{call liferay.frontend.Slider /}`.

#### How should I update my code? [](id=how-should-i-update-my-code-3)

There's no direct replacement for the `liferay.frontend.Slider` component.
If you have a component that relies on it, you can co-locate a copy of the old
implementation and use it locally within your module.

#### Why was this change made? [](id=why-was-this-change-made-3)

The `liferay.frontend.Slider` component was deprecated in 7.2 and is no
longer used.

---------------------------------------

### Removed com.liferay.asset.taglib.servlet.taglib.soy.AssetTagsSelectorTag [](id=removed-soy-assettagselectortag)
- **Date:** 2019-Oct-15
- **JIRA Ticket:** [LPS-100144](https://issues.liferay.com/browse/LPS-100144)

#### What changed? [](id=what-changed-4)

The Java class
`com.liferay.asset.taglib.servlet.taglib.soy.AssetTagsSelectorTag` was removed.

#### Who is affected? [](id=who-is-affected-4)

This affects any code that directly instantiates or extends this class.

#### How should I update my code? [](id=how-should-i-update-my-code-4)

There's no direct replacement for the removed class. If you have code that
depends on it, you must copy over the old implementation to your own
project and change the dependency to rely on your local version.

#### Why was this change made? [](id=why-was-this-change-made-4)

The `asset:asset-tags-selector` and its components have been migrated to React,
making the old tag and its soy infrastructure unnecessary.

---------------------------------------

### Removed Portal Property user.groups.copy.layouts.to.user.personal.site [](id=removed-portal-property-user-groups-copy-layouts-to-user-personal-site)
- **Date:** 2019-Dec-26
- **JIRA Ticket:** [LPS-106339](https://issues.liferay.com/browse/LPS-106339)

#### What changed? [](id=what-changed-5)

The portal property `user.groups.copy.layouts.to.user.personal.site` and the
behavior associated with it were removed.

#### Who is affected? [](id=who-is-affected-5)

This affects anyone who set the `user.groups.copy.layouts.to.user.personal.site`
property to `true` to copy User Group pages to User Personal Sites.

#### How should I update my code? [](id=how-should-i-update-my-code-5)

There's no direct replacement for this property. If you depend on the behavior,
you can copy the old implementations of
`UserGroupLocalServiceImpl#copyUserGroupLayouts` to your own project.

#### Why was this change made? [](id=why-was-this-change-made-5)

The behavior associated with this property has been deprecated since 6.2.

---------------------------------------

### Removed Support for Auto Deploying EXT Plugins
- **Date:** 2019-Dec-31
- **JIRA Ticket:** [LPS-106008](https://issues.liferay.com/browse/LPS-106008)

#### What changed? [](id=what-changed-6)

The support for deploying EXT plugins using Auto Deployer (via
`liferay-home/deploy folder`) was removed. EXT plugins copied to the deploy
folder are no longer recognized.

#### Who is affected? [](id=who-is-affected-6)

This affects anyone deploying EXT plugins via the Auto Deployer.

#### How should I update my code? [](id=how-should-i-update-my-code-6)

There's no direct replacement for the removed feature. If you have an EXT
plugin, you must deploy it manually or use [`ant direct-deploy`](https://github.com/liferay/liferay-plugins-ee/blob/7.0.x/ext/build-common-ext.xml#L211).

#### Why was this change made? [](id=why-was-this-change-made-6)

This feature has been deprecated since 7.1.

---------------------------------------

### Replaced OSGi configuration Property autoUpgrade
- **Date:** 2020-Jan-03
- **JIRA Ticket:** [LPS-102842](https://issues.liferay.com/browse/LPS-102842)

#### What changed? [](id=what-changed-7)

The OSGi property `autoUpgrade` defined in
`com.liferay.portal.upgrade.internal.configuration.ReleaseManagerConfiguration.config`
was replaced with the portal property `upgrade.database.auto.run`.

Unlike the old property, which only controlled the upgrade processes in
modules, the new one also affects the Core upgrade processes. The default value
is `false`, so upgrade processes won't run on startup or module deployment. You
can execute module upgrade processes anytime via Gogo console.

#### Who is affected? [](id=who-is-affected-7)

This affects development environments where you don't want to run the upgrade
when a new process is deployed. This property can't be set to `true` in
production environments. In these cases, you must use the upgrade tool to execute minor and major schema version changes.

#### How should I update my code? [](id=how-should-i-update-my-code-7)

This change doesn't affect your code.

#### Why was this change made? [](id=why-was-this-change-made-7)

This change was made to unify the auto-upgrade feature between the Core and
modules. The default value has also changed to avoid the execution of new
upgrade processes on startup in production environments.

---------------------------------------

### Removed Cache Bootstrap Feature
- **Date:** 2020-Jan-8
- **JIRA Ticket:** [LPS-96563](https://issues.liferay.com/browse/LPS-96563)

#### What changed? [](id=what-changed-8)

The cache bootstrap feature has been removed. These properties can no longer
be used to enable/configure cache bootstrap:

`ehcache.bootstrap.cache.loader.enabled`,
`ehcache.bootstrap.cache.loader.properties.default`,
`ehcache.bootstrap.cache.loader.properties.${specific.cache.name}`.

#### Who is affected? [](id=who-is-affected-8)

This affects anyone using the properties listed above.

#### How should I update my code? [](id=how-should-i-update-my-code-8)

There's no direct replacement for the removed feature. If you have code that
depends on it, you must implement it yourself.

#### Why was this change made? [](id=why-was-this-change-made-8)

This change was made to avoid security issues.

---------------------------------------

### Removed liferay-frontend:cards-treeview Tag
- **Date:** 2020-Jan-10
- **JIRA Ticket:** [LPS-106899](https://issues.liferay.com/browse/LPS-106899)

#### What changed? [](id=what-changed-9)

The `liferay-frontend:cards-treeview` tag was removed.

#### Who is affected? [](id=who-is-affected-9)

This affects anyone using the tag from a jsp, or some of its components inside
a SOY (Closure Templates) template.

#### How should I update my code? [](id=how-should-i-update-my-code-9)

There's no direct replacement for the removed feature. If you have code that
depends on it, you must implement it yourself.

#### Why was this change made? [](id=why-was-this-change-made-9)

This change was made because the tag was primarily used internally.

---------------------------------------

### Removed liferay-frontend:contextual-sidebar Tag [](id=removed-liferay-frontend-contextual-sidebar-tag)
- **Date:** 2020-Jan-10
- **JIRA Ticket:** [LPS-100146](https://issues.liferay.com/browse/LPS-100146)

#### What changed? [](id=what-changed-10)

The `liferay-frontend:contextual-sidebar` tag was removed.

#### Who is affected? [](id=who-is-affected-10)

This affects anyone using the tag from a jsp or some of its components inside
a SOY (Closure Templates) template.

#### How should I update my code? [](id=how-should-i-update-my-code-10)

There's no direct replacement for the removed feature. If you have code that
depends on it, you must implement it yourself.

#### Why was this change made? [](id=why-was-this-change-made-10)

This change was made because the tag was primarily used internally.

---------------------------------------

### Changed Control Menu and Product Menu Positioning
- **Date:** 2020-Feb-04
- **JIRA Ticket:** [LPS-107487](https://issues.liferay.com/browse/LPS-107487)

#### What changed? [](id=what-changed-11)

The placement and structure of the Control and Product Menus has changed to
address several accessibility concerns and common visual glitches.

These changes have been applied to the Control and Product menus:
- The Product Menu has been moved outside of the Control Menu
- The Control Menu now uses `position:sticky` to control its behaviour
- Styles of the menus inside the Control Menu have been updated to account for the new sticky behaviour

#### Who is affected? [](id=who-is-affected-11)

This could affect developers that have a custom Control Panel Theme with a
customized `portlet.ftl` template or those that have developed a custom menu
that behaves as a sticky bar and is included using the `*ControlMenuEntry` API.

#### How should I update my code? [](id=how-should-i-update-my-code-11)

##### Control Panel Themes

Developers with custom Control Panel themes should move the call (if any) to the
`@liferay.control_menu` macro above the portlet section in their `portlet.ftl`.

**Before:**

```
<section class="portlet" id="portlet_${htmlUtil.escapeAttribute(portletDisplay.getId())}">
	${portletDisplay.writeContent(writer)}
</section>

<#if portletDisplay.isStateMax()>
	<@liferay.control_menu />
</#if>
```

**After:**

```
<#if portletDisplay.isStateMax()>
	<@liferay.control_menu />
</#if>

<section class="portlet" id="portlet_${htmlUtil.escapeAttribute(portletDisplay.getId())}">
	${portletDisplay.writeContent(writer)}
</section>
```

##### Custom Sticky Bars

Developers with custom Sticky Bars included using the `*ControlMenuEntry` API
can use the newly included extension points in the Control Menu to inject their
components.

Move the code that injects the menu to a `DynamicInclude` component and register
it for the proper position:
- Before the Control Menu: Use `com.liferay.product.navigation.taglib#/page.jsp#pre`
- After the Control Menu: Use `com.liferay.product.navigation.taglib#/page.jsp#post`

#### Why was this change made? [](id=why-was-this-change-made-11)

This change was made to improve accessibility and simplify the required logic
for positioning and controlling top-positioned menus. It provides a more correct
and expected markup that avoids common visual glitches.

---------------------------------------

### jQuery Is No Longer Included by Default [](id=jquery-is-no-longer-included-by-default)
- **Date:** 2020-Feb-04
- **JIRA Ticket:** [LPS-95726](https://issues.liferay.com/browse/LPS-95726)

#### What changed? [](id=what-changed-12)

Previously, `jQuery` was being included on every page by default and made
available through the global `window.$` and the scoped `AUI.$` variables. After
this change, `jQuery` is no longer included by default and those variables are
`undefined`.

#### Who is affected? [](id=who-is-affected-12)

This affects any developer who used `AUI.$` or `window.$` in their custom
scripts.

#### How should I update my code? [](id=how-should-i-update-my-code-12)

Use any of the strategies to add third party libraries to provide your own
version of JQuery to use in your code.

Additionally, as a temporary measure, you can bring back the old behaviour by
setting the `Enable jQuery` property in *System Settings* &rarr; *Third Party*
&rarr; *jQuery* to `true`.

#### Why was this change made? [](id=why-was-this-change-made-12)

This change was made to avoid bundling and serving additional library code on
every page that was mostly unused and redundant.

---------------------------------------

### Server-side Parallel Rendering Is No Longer Supported
- **Date:** 2020-Mar-16
- **JIRA Ticket:** [LPS-110359](https://issues.liferay.com/browse/LPS-110359)

#### What changed? [](id=what-changed-13)

Properties with the prefix `layout.parallel.render` were removed, which means
parallel rendering is only supported when AJAX rendering is enabled.

#### Who is affected? [](id=who-is-affected-13)

This affects anyone using the removed properties.

#### How should I update my code? [](id=how-should-i-update-my-code-13)

Remove any properties prefixed with `layout.parallel.render` from your
properties file.

#### Why was this change made? [](id=why-was-this-change-made-13)

This feature has been deprecated.

---------------------------------------

### Simple Editor Is No Longer Bundled by Default
- **Date:** 2020-Mar-27
- **JIRA Ticket:** [LPS-110734](https://issues.liferay.com/browse/LPS-110734)

### What changed? [](id=what-changed-14)

As of 7.3, CKEditor is the default and only supported WYSIWYG editor.

### Who is affected [](id=who-is-affected-14)

This affects anyone who uses the Liferay Frontend Editor Simple Web module.

### How should I update my code? [](id=how-should-i-update-my-code-14)

If you've configured Liferay Portal to use the Simple Editor, you can remove
these configurations. If you still want to use the Simple Editor, you must take
these steps:

- Keep your configurations.
- Open https://repository.liferay.com/nexus/index.html in your browser.
- Search for `com.liferay.frontend.editor.simple.web`.
- Download a .jar file for the `com.liferay.frontend.editor.simple.web` module.
- Deploy the .jar file you downloaded in your liferay-portal installation.

#### Why was this change made? [](id=why-was-this-change-made-14)

This change was made to consolidate all our UX for writing Rich Text Content
around a single Editor to provide a more cohesive and comprehensive experience.

---------------------------------------

### TinyMCE Editor Is No Longer Bundled by Default
- **Date:** 2020-Mar-27
- **JIRA Ticket:** [LPS-110733](https://issues.liferay.com/browse/LPS-110733)

### What changed? [](id=what-changed-15)

As of 7.3, CKEditor is the default and only supported WYSIWYG editor.

### Who is affected [](id=who-is-affected-15)

This affects anyone who uses TinyMCE.

### How should I update my code? [](id=how-should-i-update-my-code-15)

If you've configured Liferay Portal to use the TinyMCE, you can remove
these configurations. If you still want to use TinyMCE, you must take these
steps:

- Keep your configurations.
- Open https://repository.liferay.com/nexus/index.html in your browser.
- Search for `com.liferay.frontend.editor.tinymce.web`.
- Download a .jar file for the `com.liferay.frontend.editor.tinymce.web` module.
- Deploy the .jar file you downloaded in your liferay-portal instalation.

#### Why was this change made? [](id=why-was-this-change-made-15)

This change was made to consolidate all our UX for writing Rich Text Content
around a single Editor to provide a more cohesive and comprehensive experience.

---------------------------------------

### asset.vocabulary.default Now Holds a Language Key
- **Date:** 2020-Apr-28
- **JIRA Ticket:** [LPS-112334](https://issues.liferay.com/browse/LPS-112334)

### What changed? [](id=what-changed-16)

`asset.vocabulary.default` is now a language key and no longer has a fixed value
of `Topic`.

### Who is affected [](id=who-is-affected-16)

This affects anyone who overwrites the property.

### How should I update my code? [](id=how-should-i-update-my-code-16)

There is no need to change the code if the property is not overwritten. If the
property was overwritten and the specified key is not found, the provided text
will be taken as the name of the default vocabulary.

#### Why was this change made? [](id=why-was-this-change-made-16)

The change was made so users don't have to change the name for the default
vocabulary in all languages.

---------------------------------------

### Liferay.Poller Is No Longer Initialized by Default [](id=liferaypoller-is-no-longer-initialized-by-default)
- **Date:** 2020-May-19
- **JIRA Ticket:** [LPS-112942](https://issues.liferay.com/browse/LPS-112942)

#### What changed? [](id=what-changed-17)

The global AUI `Liferay.Poller` utility is now deprecated and is no longer
initialized by default.

#### Who is affected? [](id=who-is-affected-17)

This affects any code that relies on `Liferay.Poller`; this is usually done via
a call to `Liferay.Poller.init()` in a JSP.

#### How should I update my code? [](id=how-should-i-update-my-code-17)

There's no direct replacement for the `Liferay.Poller` utility. If you must
initialize `Liferay.Poller`, update your JSP to use the code below:

```
<%@ page import="com.liferay.petra.encryptor.Encryptor" %>

<%-- For access to `company` and `themeDisplay`. --%>
<liferay-theme:defineObjects>

<aui:script use="liferay-poller">
	<c:if test="<%= themeDisplay.isSignedIn() %>">
		Liferay.Poller.init({
			encryptedUserId:
				'<%= Encryptor.encrypt(company.getKeyObj(), String.valueOf(themeDisplay.getUserId())) %>',
		});
	</c:if>
</aui:script>
```

#### Why was this change made? [](id=why-was-this-change-made-17)

The `Liferay.Poller` component was only used in the Chat application, which is
archived. Skipping initialization by default streamlines page loads for the
common case.

---------------------------------------

### ContentTransformerListener Is Disabled By Default
- **Date:** 2020-May-25
- **JIRA Ticket:** [LPS-114239](https://issues.liferay.com/browse/LPS-114239)

#### What changed? [](id=what-changed-18)

`ContentTransformerListener` is now disabled by default.

#### Who is affected? [](id=who-is-affected-18)

This affects Liferay Portal installations using legacy web content features
provided by the `ContentTransformerListener`, such as embedding web content
inside another web content, a legacy edit in place infrastructure, token
replacements (`@article_group_id@`, `@articleId;elementName@`), etc.

#### How should I update my code? [](id=how-should-i-update-my-code-18)

There's no need to update your code. If you still want to use
`ContentTransformerListener`, you can enable it in System Settings via the
*Enable ContentTransformerListener* property under *Content & Data* &rarr; *Web
Content* &rarr; *Virtual Instance Scope* &rarr; *Web Content*.

#### Why was this change made? [](id=why-was-this-change-made-18)

`ContentTransformerListener` was disabled to improve performance, due to its
expensive string processing on article elements (calling
`HtmlUtil.stripComments` and `HtmlUtil.stripHtml` on article fields).

---------------------------------------

### Liferay.BrowserSelectors.run Is No Longer Called
- **Date:** 2020-May-26
- **JIRA Ticket:** [LPS-112983](https://issues.liferay.com/browse/LPS-112983)

#### What changed? [](id=what-changed-19)

The `Liferay.BrowserSelectors.run()` function is no longer called on pages,
which as a result removes some CSS classes from the opening `<html>` tag. Many
of these are now added to the `<body>` element instead.

#### Who is affected? [](id=who-is-affected-19)

This affects any code that relies on these CSS classes in the `<html>` element:

- `aol`
- `camino`
- `edgeHTML` or `edge`
- `firefox`
- `flock`
- `gecko`
- `icab`
- `ie`, `ie6`, `ie7`, `ie9`, or `ie11`
- `js`
- `konqueror`
- `mac`
- `mozilla`
- `netscape`
- `opera`
- `presto`
- `safari`
- `secure`
- `touch`
- `trident`
- `webkit`
- `win`

#### How should I update my code? [](id=how-should-i-update-my-code-19)

There's no direct replacement for the `Liferay.BrowserSelectors.run()` function,
but you can adapt your CSS and JavaScript to target new classes on the `<body>`
element instead. These classes are added to the `<body>` element to reflect
the browser you're currently using:

- `chrome`
- `edge`
- `firefox`
- `ie`
- `mobile`
- `other`

Alternatively, you can still invoke `Liferay.BrowserSelectors.run()` to apply
the old classes to the `<html>` element with the code below:

```
<aui:script use="liferay-browser-selectors">
	Liferay.BrowserSelectors.run();
</aui:script>
```

#### Why was this change made? [](id=why-was-this-change-made-19)

The classes, some of which referred to outdated browsers, were being added to
the top `<html>` element via legacy JavaScript that depended on Alloy UI. This
change, which removes the outdated browser references, is now done on the server
side, improving page loading times.

---------------------------------------

### Remove Support for Blocking Cache [](id=remove-support-for-blocking-cache)
- **Date:** 2020-Jun-17
- **JIRA Ticket:** [LPS-115687](https://issues.liferay.com/browse/LPS-115687)

#### What changed? [](id=what-changed-20)

Blocking cache support was removed. These properties can no longer be used to
enable blocking cache:

- `ehcache.blocking.cache.allowed`
- `permissions.object.blocking.cache`
- `value.object.entity.blocking.cache`

#### Who is affected? [](id=who-is-affected-20)

This affects anyone using the properties listed above.

#### How should I update my code? [](id=how-should-i-update-my-code-20)

There's no direct replacement for the removed feature. If you have code that
depends on it, you must implement it yourself.

#### Why was this change made? [](id=why-was-this-change-made-20)

This change was made to improve performance because blocking caches should never be enabled.

---------------------------------------

### Remove Support for Setting Cache Properties for Each Entity Model [](id=removed-setting-cache-properties-for-each-entity-model)
- **Date:** 2020-Jun-24
- **JIRA Ticket:** [LPS-116049](https://issues.liferay.com/browse/LPS-116049)

#### What changed? [](id=what-changed-21)

Support was removed for setting these cache properties for an entity:

- `value.object.entity.cache.enabled*`
- `value.object.finder.cache.enabled*`
- `value.object.column.bitmask.enabled*`

For example, these properties are for entity
`com.liferay.portal.kernel.model.User`:

- `value.object.entity.cache.enabled.com.liferay.portal.kernel.model.User`
- `value.object.finder.cache.enabled.com.liferay.portal.kernel.model.User`
- `value.object.column.bitmask.enabled.com.liferay.portal.kernel.model.User`

#### Who is affected? [](id=who-is-affected-21)

This affects anyone using the properties listed above for an entity.

#### How should I update my code? [](id=how-should-i-update-my-code-21)

There's no direct replacement for the removed feature. You must remove
these properties from your entities.

#### Why was this change made? [](id=why-was-this-change-made-21)

This change was made because these properties are not useful for an entity.

---------------------------------------

### Dynamic Data Mapping fields in Elasticsearch have changed to a nested document
- **Date:** 2020-Jul-27
- **JIRA Ticket:** [LPS-103224](https://issues.liferay.com/browse/LPS-103224)

#### What changed? [](id=what-changed-22)

Dynamic Data Mapping fields in Elasticsearch that start with `ddm__keyword__` and `ddm__text__` have been moved to a new nested document `ddmFieldArray`.

The new nested document `ddmFieldArray` has several entries with following fields:
 - `ddmFieldName`: Contains the Dynamic Data Mapping structure field name. This name is generated using `DDMIndexer.encodeName` methods.
 - `ddmFieldValue*`: Contains the indexed data. The name of this field is generated using `DDMIndexer.getValueFieldName` and depends on its data type and language.
 - `ddmValueFieldName`: Contains the index field name where the indexed data is stored.

 This change is not applied if you are using SOLR search engine.

#### Who is affected? [](id=who-is-affected-22)

This affects anyone with custom developments that execute queries in the Elasticsearch index using `ddm__keyword__*` and `ddm__text__*` fields.

#### How should I update my code? [](id=how-should-i-update-my-code-22)

You have to use the new nested document `ddmFieldArray` in your Elasticsearch queries.

There are some examples in Liferay code, for example, you can see the usage of DDM_FIELD_ARRAY constant in [DDMIndexerImpl](https://github.com/liferay/liferay-portal/blob/master/modules/apps/dynamic-data-mapping/dynamic-data-mapping-service/src/main/java/com/liferay/dynamic/data/mapping/internal/util/DDMIndexerImpl.java) and [AssetHelperImpl](https://github.com/liferay/liferay-portal/blob/master/modules/apps/asset/asset-service/src/main/java/com/liferay/asset/internal/util/AssetHelperImpl.java) java classes.

You can also restore the legacy behavior from System Settings and continue using `ddm__keyword__*` and `ddm__text__*` fields.
 - Go to "System Settings" => "Dynamic Data Mapping" => "Dynamic Data Mapping Indexer"
 - Check "Enable Legacy Dynamic Data Mapping Index Fields"
 - It is necessary to execute a full reindex after changing this configuration.

#### Why was this change made? [](id=why-was-this-change-made-22)

This change was made to avoid Elasticsearch "Limit of total fields has been exceeded" error that can be produced if you have a large number of Dynamic Data Mapping structures.

For more information about this error, see [LPS-103224](https://issues.liferay.com/browse/LPS-103224).

---------------------------------------

### Removed liferay-editor-image-uploader Plugin
- **Date:** 2020-Mar-27
- **JIRA Ticket:** [LPS-110734](https://issues.liferay.com/browse/LPS-110734)

### What changed? [](id=what-changed-23)

`liferay-editor-image-uploader` AUI plugin was removed. Its code was merged
into `addimages` CKEditor plugin, used by Alloy Editor and CKEditor.

### Who is affected [](id=who-is-affected-23)

This affects custom solutions that use the plugin directly.

### How should I update my code? [](id=how-should-i-update-my-code-23)

There's no direct replacement for the `liferay-editor-image-uploader` plugin.
If you have a component that relies on it, you can co-locate a copy of the old
implementation and use it locally within your module.

#### Why was this change made? [](id=why-was-this-change-made-23)

This change was made to enable image drag and drop handling in CKEditor, and
have a common solution for both Alloy Editor and CKEditor.

---------------------------------------