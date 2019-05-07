# What are the Breaking Changes for Liferay 7.2?

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

*This document has been reviewed through commit `85a738099b8f`.*

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

### Removed Support for JSP Templates in Themes [](id=removed-support-for-jsp-templates-in-themes)
- **Date:** 2018-Nov-14
- **JIRA Ticket:** [LPS-87064](https://issues.liferay.com/browse/LPS-87064)

#### What changed? [](id=what-changed-1)

Themes can no longer leverage JSP templates. Also, related logic has been
removed from the public APIs `com.liferay.portal.kernel.util.ThemeHelper` and
`com.liferay.taglib.util.ThemeUtil`.

#### Who is affected? [](id=who-is-affected-1)

This affects anyone who has themes using JSP templates or is using the removed
methods.

#### How should I update my code? [](id=how-should-i-update-my-code-1)

If you have a theme using JSP templates, consider migrating it to FreeMarker.

#### Why was this change made? [](id=why-was-this-change-made-1)

JSP is not a real template engine and is rarely used. FreeMarker is the
recommended template engine moving forward.

The removal of JSP templates allows for an increased focus on existing and new
template engines.

---------------------------------------

### Lodash Is No Longer Included by Default
- **Date:** 2018-Nov-27
- **JIRA Ticket:** [LPS-87677](https://issues.liferay.com/browse/LPS-87677)

#### What changed?

Previously, Lodash was included in every page by default and made available
through the global `window._` and scoped `AUI._` variables. Lodash is no longer
included by default and those variables are now undefined.

#### Who is affected?

This affects any developer who used the `AUI._` or `window._` variables in their
custom scripts.

#### How should I update my code?

You should provide your own Lodash version for your custom developments to use
following any of the possible strategies to add third party libraries.

As a temporary measure, you can bring back the old behavior by setting the
*Enable Lodash* property in Liferay Portal's *Control Panel* &rarr;
*Configuration* &rarr; *System Settings* &rarr; *Third Party* &rarr; *Lodash* to
`true`.

#### Why was this change made?

This change was made to avoid bundling and serving additional library code on
every page that was mostly unused and redundant.

---------------------------------------

### Moved Two Staging Properties to OSGi Configuration [](id=moved-staging-properties)
- **Date:** 2018-Dec-12
- **JIRA Ticket:** [LPS-88018](https://issues.liferay.com/browse/LPS-88018)

#### What changed? [](id=what-changed-3)

Two Staging properties have been moved from `portal.properties` to an
OSGi configuration named `ExportImportServiceConfiguration.java` in the
`export-import-service` module.

#### Who is affected? [](id=who-is-affected-3)

This affects anyone using the following portal properties:

- `staging.delete.temp.lar.on.failure`
- `staging.delete.temp.lar.on.success`

#### How should I update my code? [](id=how-should-i-update-my-code-3)

Instead of overriding the `portal.properties` file, you can manage the
properties from Portal's configuration administrator. This can be accessed by
navigating to Liferay Portal's *Control Panel* &rarr; *Configuration* &rarr;
*System Settings* &rarr; *Infrastructure* &rarr; *Export/Import* and editing
the settings there.

If you would like to include the new configuration in your application, follow
the instructions for
[making applications configurable](https://dev.liferay.com/develop/tutorials/-/knowledge_base/7-1/making-applications-configurable).

#### Why was this change made? [](id=why-was-this-change-made-3)

This change was made as part of the modularization efforts to ease portal
configuration changes.

---------------------------------------

### Remove Link Application URLs to Page Functionality [] (id=remove-link-application-urls-to-functionality)
- **Date:** 2018-Dec-14
- **JIRA Ticket:** [LPS-85948](https://issues.liferay.com/browse/LPS-85948)

#### What changed? [](id=what-changed-4)

The *Link Portlet URLs to Page* option in the Look and Feel portlet was marked
as deprecated in Liferay Portal 7.1, allowing the user to show and hide the
option through a configuration property. In Liferay Portal 7.2, this has been
removed and can no longer be configured.

#### Who is affected? [](id=who-is-affected-4)

This affects administrators who used the option in the UI and developers who
leveraged the option in the portlet.

#### How should I update my code? [](id=how-should-i-update-my-code-4)

You should update any portlets leveraging this feature, since any preconfigured
reference to the property is ignored in the portal.

#### Why was this change made? [](id=why-was-this-change-made-4)

A limited number of portlets use this property; there are better ways to achieve
the same results.

---------------------------------------

### Moved TermsOfUseContentProvider out of kernel.util [](id=moved-termsofusecontentprovider-out-of-kernel-uitl)
- **Date:** 2019-Jan-07
- **JIRA Ticket:** [LPS-88869](https://issues.liferay.com/browse/LPS-88869)

#### What changed? [](id=what-changed-5)

The `TermsOfUseContentProvider` interface's package changed:

`com.liferay.portal.kernel.util` &rarr; `com.liferay.portal.kernel.term.of.use`

The `TermsOfUseContentProviderRegistryUtil` class' name and package changed:

`TermsOfUseContentProviderRegistryUtil` &rarr; `TermsOfUseContentProviderUtil`

and

`com.liferay.portal.kernel.util` &rarr; `com.liferay.portal.internal.terms.of.use`

The logic of getting `TermsOfUseContentProvider` was also changed. Instead of
always returning the first service registered, which is random and depends on
the order of registered services, the `TermsOfUseContentProvider` service is
tracked and updated with `com.liferay.portal.kernel.util.ServiceProxyFactory`.
As a result, the `TermsOfUseContentProvider` now respects service ranking.

#### Who is affected? [](id=who-is-affected-5)

This affects anyone who used
`com.liferay.portal.kernel.util.TermsOfUseContentProviderRegistryUtil` to lookup
the `com.liferay.portal.kernel.util.TermsOfUseContentProvider` service.

#### How should I update my code? [](id=how-should-i-update-my-code-5)

If `com.liferay.portal.kernel.util.TermsOfUseContentProvider` is used, update
the import package name. If there is any usage in `portal-web`, update
`com.liferay.portal.kernel.util.TermsOfUseContentProviderRegistryUtil` to
`com.liferay.portal.kernel.term.of.use.TermsOfUseContentProviderUtil`. Remove
usages of `com.liferay.portal.kernel.util.TermsOfUseContentProviderRegistryUtil`
in modules and use the `@Reference` annotation to fetch the
`com.liferay.portal.kernel.term.of.use.TermsOfUseContentProvider` service
instead.

#### Why was this change made? [](id=why-was-this-change-made-5)

This is one of several steps to clean up kernel provider interfaces to reduce
the chance of package version lock down.

---------------------------------------

### Removed HibernateConfigurationConverter and Converter [](id=removed-hibernateconfigurationconverter-and-converter)
- **Date:** 2019-Jan-07
- **JIRA Ticket:** [LPS-88870](https://issues.liferay.com/browse/LPS-88870)

#### What changed? [](id=what-changed-6)

The interface `com.liferay.portal.kernel.util.Converter` and its implementation
`com.liferay.portal.spring.hibernate.HibernateConfigurationConverter` were
removed.

#### Who is affected? [](id=who-is-affected-6)

This removes the support of generating customized `portlet-hbm.xml` files
implemented by `HibernateConfigurationConverter`. Refer to
[LPS-5363](https://issues.liferay.com/browse/LPS-5363) for more information.

#### How should I update my code? [](id=how-should-i-update-my-code-6)

You should remove usages of `HibernateConfigurationConverter`. Make sure the
generated `portlet-hbm.xml` is accurate.

#### Why was this change made? [](id=why-was-this-change-made-6)

This is one of several steps to clean up kernel provider interfaces to reduce
the chance of package version lock down.

---------------------------------------

### Switched to Use JDK Function and Supplier [](id=switched-to-use-jdk-function-and-supplier)
- **Date:** 2019-Jan-08
- **JIRA Ticket:** [LPS-88911](https://issues.liferay.com/browse/LPS-88911)

#### What changed? [](id=what-changed-7)

The `Function` and `Supplier` interfaces in package
`com.liferay.portal.kernel.util` were removed. Their usages were replaced with
`java.util.function.Function` and `java.util.function.Supplier`.

#### Who is affected? [](id=who-is-affected-7)

This affects anyone who implemented the `Function` and `Supplier` interfaces in
package `com.liferay.portal.kernel.util`.

#### How should I update my code?  [](id=how-should-i-update-my-code-7)

You should replace usages of `com.liferay.portal.kernel.util.Function` and
`com.liferay.portal.kernel.util.Supplier` with `java.util.function.Function` and
`java.util.function.Supplier`, respectively.

#### Why was this change made? [](id=why-was-this-change-made-7)

This is one of several steps to clean up kernel provider interfaces to reduce
the chance of package version lock down.

---------------------------------------

### Deprecated com.liferay.portal.service.InvokableService Interface
- **Date:** 2019-Jan-08
- **JIRA Ticket:** [LPS-88912](https://issues.liferay.com/browse/LPS-88912)

#### What changed?

The `InvokableService` and `InvokableLocalService` interfaces in package
`com.liferay.portal.kernel.service` were removed.

#### Who is affected?

This affects anyone who used `InvokableService` and `InvokableLocalService` in
package `com.liferay.portal.kernel.service`.

#### How should I update my code?

You should remove usages of `InvokableService` and `InvokableLocalService`. Make
sure to use the latest version of Service Builder to generate implementations
for services in case there is any compile errors after removal.

#### Why was this change made?

This is one of several steps to clean up kernel provider interfaces to reduce
the chance of package version lock down.

---------------------------------------

### Dropped Support of ServiceLoaderCondition [](id=dropped-support-of-serviceloadercondition)
- **Date:** 2019-Jan-08
- **JIRA Ticket:** [LPS-88913](https://issues.liferay.com/browse/LPS-88913)

#### What changed? [](id=what-changed-9)

The interface `ServiceLoaderCondition` and its implementation
`DefaultServiceLoaderCondition` in package `com.liferay.portal.kernel.util` were
removed.

#### Who is affected? [](id=who-is-affected-9)

This affects anyone using `ServiceLoaderCondition` and
`DefaultServiceLoaderCondition`.

#### How should I update my code? [](id=how-should-i-update-my-code-9)

You should remove usages of `ServiceLoaderCondition`. Update usages of `load`
methods in `com.liferay.portal.kernel.util.ServiceLoader` according to the
updated method signatures.

#### Why was this change made? [](id=why-was-this-change-made-9)

This is one of several steps to clean up kernel provider interfaces to reduce
the chance of package version lock down.

---------------------------------------

### Switched to Use JDK Predicate [](id=switched-to-use-jdk-predicate)
- **Date:** 2019-Jan-14
- **JIRA Ticket:** [LPS-89139](https://issues.liferay.com/browse/LPS-89139)

#### What changed? [](id=what-changed-10)

The interface `com.liferay.portal.kernel.util.PredicateFilter` was removed and
replaced with `java.util.function.Predicate`. As a result, the following
implementations were removed:

- `com.liferay.portal.kernel.util.AggregatePredicateFilter`
- `com.liferay.portal.kernel.util.PrefixPredicateFilter`
- `com.liferay.portal.kernel.portlet.JavaScriptPortletResourcePredicateFilter`
- `com.liferay.dynamic.data.mapping.form.values.query.internal.model.DDMFormFieldValuePredicateFilter`

The `com.liferay.portal.kernel.util.ArrayUtil_IW` class was regenerated.

#### Who is affected? [](id=who-is-affected-10)

This affects anyone who used `PredicateFilter`, `AggregatePredicateFilter`,
`PrefixPredicateFilter`, `JavaScriptPortletResourcePredicateFilter`, and
`DDMFormFieldValuePredicateFilter`.

#### How should I update my code? [](id=how-should-i-update-my-code-10)

You should replace usages of `com.liferay.portal.kernel.util.PredicateFilter`
with `java.util.function.Predicate`. Additionally, remove usages of
`AggregatePredicateFilter`, `PrefixPredicateFilter`,
`JavaScriptPortletResourcePredicateFilter`, and
`DDMFormFieldValuePredicateFilter`.

#### Why was this change made? [](id=why-was-this-change-made-10)

This is one of several steps to clean up kernel provider interfaces to reduce
the chance of package version lock down.

---------------------------------------

### Removed Unsafe Functional Interfaces in Package com.liferay.portal.kernel.util
- **Date:** 2019-Jan-15
- **JIRA Ticket:** [LPS-89223](https://issues.liferay.com/browse/LPS-89223)

#### What changed?

The `com.liferay.portal.osgi.util.test.OSGiServiceUtil` class was removed. Also,
the following interfaces were removed from the `com.liferay.portal.kernel.util`
package:

- `UnsafeConsumer`
- `UnsafeFunction`
- `UnsafeRunnable`

#### Who is affected?

This affects anyone using the class/interfaces mentioned above.

#### How should I update my code?

The `com.liferay.portal.osgi.util.test.OSGiServiceUtil` class has been
deprecated since Liferay Portal 7.1. If usages for this class still exist,
replace it with its direct replacement:
`com.liferay.osgi.util.service.OSGiServiceUtil`. Replace usages of
`UnsafeConsumer`, `UnsafeFunction` and `UnsafeRunnable` with their corresponding
interfaces in package `com.liferay.petra.function`.

#### Why was this change made?

This is one of several steps to clean up kernel provider interfaces to reduce
the chance of package version lock down.

---------------------------------------

### Deprecated NTLM in Portal Distribution
- **Date:** 2019-Jan-21
- **JIRA Ticket:** [LPS-88300](https://issues.liferay.com/browse/LPS-88300)

#### What changed?

NTLM modules have been moved from the `portal-security-sso` project to a
new project named `portal-security-sso-ntlm`.
This new project is deprecated and available to download from Liferay Marketplace.

#### Who is affected?

This affects anyone using NTLM as an authentication system.

#### How should I update my code?

If you want to continue using NTLM as an authentication system, you must
download the corresponding modules from Liferay Marketplace. Alternatively, you
can migrate to Kerberos (recommended), which requires no changes and is
compatible with Liferay Portal 7.0+.

#### Why was this change made?

This change was made to avoid using an old proprietary solution (NTLM). Kerberos
is now recommended, which is a standard protocol and a more secure method of
authentication compared to NTLM.

---------------------------------------

### Deprecated OpenID in Portal Distribution
- **Date:** 2019-Jan-21
- **JIRA Ticket:** [LPS-88906](https://issues.liferay.com/browse/LPS-88906)

#### What changed?

OpenID modules have been moved to a new project named `portal-security-sso-openid`.
This new project is deprecated and available to download from Liferay Marketplace.

#### Who is affected?

This affects anyone using OpenID as an authentication system.

#### How should I update my code?

If you want to continue using OpenID as an authentication system, you must
download the corresponding module from Liferay Marketplace. Alternatively, you
should migrate to OpenID Connect, available on Liferay Portal Distribution.

#### Why was this change made?

This change was made to avoid using a deprecated solution (OpenID). OpenID
Connect is now recommended, which is a more secure method of authentication
since it runs on top of OAuth.

---------------------------------------

### Deprecated Google SSO in Portal Distribution
- **Date:** 2019-Jan-21
- **JIRA Ticket:** [LPS-88905](https://issues.liferay.com/browse/LPS-88905)

#### What changed?

Google SSO modules have been moved from the `portal-security-sso` project
to a new project named `portal-security-sso-google`.
This new projects is deprecated and available to download from Liferay Marketplace.

#### Who is affected?

This affects anyone using Google SSO as an authentication system.

#### How should I update my code?

If you want to continue using Google SSO as an authentication system, you must
download the corresponding module from Liferay Marketplace. Alternatively, you
can use OpenID Connect.

#### Why was this change made?

This change was made to avoid using an old solution for authentication (Google
SSO). OpenID Connect is the recommended specification to use Google
implementation for authentication.

---------------------------------------

### Deprecated dl.tabs.visible property
- **Date:** 2019-Apr-10
- **JIRA Ticket:** [LPS-93948](https://issues.liferay.com/browse/LPS-93948)

#### What changed?

The `dl.tabs.visible` property let users toggle the visibility of the navigation
tabs in Documents & Media when put on a widget page.  This configuration option
has been removed, so that the navigation tab will never be shown in widget pages.

#### Who is affected?

This affects anyone with a value for `dl.tabs.visible` different than `false`.

#### How should I update my code?

No code changes are necessary.

#### Why was this change made?

Documents & Media has been reviewed from an UX perspective, and removing the
navigation tabs in widget pages was part of a UI clean up process.