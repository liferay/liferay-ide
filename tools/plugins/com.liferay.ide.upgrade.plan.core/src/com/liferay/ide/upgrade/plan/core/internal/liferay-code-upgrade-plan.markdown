# Liferay Code Upgrade Plan

Here are the steps for upgrading your custom plugins to @product-ver@.

## Upgrade Steps

<ol id="root">
	<li icon="" requirement="required">
		<div class="title">Prepare for Upgrade</div>
		<div class="description">Prepare for Upgrade</div>
	</li>
	<ol>
		<li icon="" requirement="recommended">
			<div class="title">Check for Upgrade Planner Updates</div>
			<div class="description">Check for Upgrade Planner Updates</div>
		</li>
		<li icon="" requirement="recommended">
			<div class="title">Check Installed JDKs</div>
			<div class="description">Check Installed JDKs</div>
		</li>
	</ol>
    <li icon="" requirement="required">
        <div class="title">Upgrade Code</div>
        <div class="description">Upgrade Code</div>
    </li>
    <ol>
        <li icon="" requirement="required">
            <div class="title">Setup Liferay Workspace</div>
        </li>
        <ol>
            <li icon="" requirement="required" commandId="create_new_liferay_workspace">
                <div class="title"><a href="">Create New Liferay Workspace</a></div>
                <div class="description">If you have an existing 7.x workspace, skip to the next step.</div>
            </li>
            <li icon="" requirement="required" commandId="import_existing_projects">
                <div class="title"><a href="">Import Existing Liferay Workspace</a></div>
                <div class="description">If you do not have an existing 7.x workspace, go to the previous step.</div>
            </li>
        </ol>
        <li icon="" requirement="required">
            <div class="title">Configure Liferay Workspace Settings</div>
            <div class="description">Configure Liferay Workspace Settings</div>
        </li>
        <ol>
            <li icon="" requirement="required" commandId="configure_bundle_url">
                <div class="title">Configure Bundle URL</div>
                <div class="description">Configure Bundle URL</div>
            </li>
            <li icon="" requirement="required" commandId="configure_target_platform_version">
                <div class="title">Configure Target Platform Version</div>
                <div class="description">Configure Target Platform Version</div>
            </li>
        </ol>
        <li icon="" requirement="required" commandId="initialize_server_bundle">
            <div class="title">Initialize Server Bundle</div>
            <div class="description">Initialize Server Bundle</div>
        </li>
    </ol>
    <li icon="" requirement="required">
        <div class="title">Migrate Plugin SDK Projects</div>
        <div class="description">Migrate Plugin SDK Projects</div>
    </li>
    <ol>
        <li icon="" requirement="required" commandId="import_existing_plugins_sdk">
            <div class="title">Import Existing Plugins SDK Projects</div>
            <div class="description">Import Existing Plugins SDK Projects</div>
        </li>
        <li icon="" requirement="required" commandId="migrate_existing_plugins_to_workspace">
            <div class="title">Migrate Existing Plugins to Workspace</div>
            <div class="description">Migrate Existing Plugins to Workspace</div>
        </li>
    </ol>
    <li icon="" requirement="required">
        <div class="title">Upgrade Build Dependencies</div>
        <div class="description">Upgrade Build Dependencies</div>
    </li>
    <ol>
        <li icon="" requirement="required">
            <div class="title">Update Repository URL</div>
            <div class="description">Update Repository URL</div>
        </li>
        <li icon="" requirement="required">
            <div class="title">Update Workspace Plugin Version</div>
            <div class="description">Update Workspace Plugin Version</div>
        </li>
        <li icon="" requirement="required">
            <div class="title">Remove Dependency Versions</div>
            <div class="description">Remove Dependency Versions</div>
        </li>
    </ol>
    <li icon="" requirement="required">
        <div class="title">Fix Upgrade Problems</div>
        <div class="description">Fix Upgrade Problems</div>
    </li>
    <ol>
        <li icon="" requirement="required" commandId="auto_correct_find_upgrade_problems">
            <div class="title">Auto-Correct Upgrade Problems</div>
            <div class="description">Auto-Correct Upgrade Problems</div>
        </li>
        <li icon="" requirement="required" commandId="find_upgrade_problems">
            <div class="title">Find Upgrade Problems</div>
            <div class="description">Find Upgrade Problems</div>
        </li>
        <li icon="" requirement="required">
            <div class="title">Resolve Upgrade Problems</div>
            <div class="description">Resolve Upgrade Problems</div>
        </li>
        <li icon="" requirement="required" commandId="remove_upgrade_problems_markers">
            <div class="title">Remove Problem Markers</div>
            <div class="description">Remove Problem Markers</div>
        </li>
    </ol>
    <li icon="" requirement="required">
        <div class="title">Upgrade Liferay Services</div>
        <div class="description">Upgrade Liferay Services</div>
    </li>
    <ol>
        <li icon="" commandId="remove_legacy_files" requirement="required">
            <div class="title">Remove Legacy Files</div>
            <div class="description">Remove Legacy Files</div>
        </li>
        <li icon="" requirement="required">
            <div class="title">Upgrade to Declarative Services</div>
            <div class="description">Upgrade to Declarative Services</div>
        </li>
        <li icon="" commandId="rebuild_services" requirement="required">
            <div class="title">Rebuild Services</div>
            <div class="description">Rebuild Services</div>
        </li>
    </ol>
    <li icon="" requirement="required">
        <div class="title">Upgrade Plugins to OSGi Modules</div>
        <div class="description">Upgrade Plugins to OSGi Modules</div>
    </li>
    <ol>
        <li icon="" requirement="required">
            <div class="title">Override/extension (module)</div>
            <div class="description">Override/extension (module)</div>
        </li>
        <li icon="" requirement="required">
            <div class="title">Liferay core JSP hook (WAR)</div>
            <div class="description">Liferay core JSP hook (WAR)</div>
        </li>
        <li icon="" requirement="required">
            <div class="title">Liferay portlet JSP hook (WAR)</div>
            <div class="description">Liferay portlet JSP hook (WAR)</div>
        </li>
        <li icon="" requirement="required">
            <div class="title">Service wrapper hook (WAR)</div>
            <div class="description">Service wrapper hook (WAR)</div>
        </li>
        <li icon="" requirement="required">
            <div class="title">Language key hook (WAR)</div>
            <div class="description">Language key hook (WAR)</div>
        </li>
        <li icon="" requirement="required">
            <div class="title">Model listener hook (WAR)</div>
            <div class="description">Model listener hook (WAR)</div>
        </li>
        <li icon="" requirement="required">
            <div class="title">Event actions hook (WAR)</div>
            <div class="description">Event actions hook (WAR)</div>
        </li>
        <li icon="" requirement="required">
            <div class="title">Servlet filter hook (WAR)</div>
            <div class="description">Servlet filter hook (WAR)</div>
        </li>
        <li icon="" requirement="required">
            <div class="title">Portal properties hook (WAR)</div>
            <div class="description">Portal properties hook (WAR)</div>
        </li>
        <li icon="" requirement="required">
            <div class="title">Struts action hook (WAR)</div>
            <div class="description">Struts action hook (WAR)</div>
        </li>
    </ol>
    <li icon="" requirement="required">
        <div class="title">Upgrading Themes</div>
        <div class="description">Upgrading Themes</div>
    </li>
    <li icon="" requirement="required">
        <div class="title">Layout Templates</div>
        <div class="description">Layout Templates</div>
    </li>
    <li icon="" requirement="required">
        <div class="title">Framework/feature upgrades</div>
        <div class="description">Framework/feature upgrades</div>
    </li>
    <ol>
        <li icon="" requirement="required">
            <div class="title">Using JNDI data source</div>
            <div class="description">Using JNDI data source</div>
        </li>
        <li icon="" requirement="required">
            <div class="title">Service Builder service invocation</div>
            <div class="description">Service Builder service invocation</div>
        </li>
        <li icon="" requirement="required">
            <div class="title">Service Builder</div>
            <div class="description">Service Builder</div>
        </li>
        <li icon="" requirement="required">
            <div class="title">Velociy templates (deprecated)</div>
            <div class="description">Velociy templates (deprecated)</div>
        </li>
    </ol>
    <li icon="" requirement="required">
        <div class="title">Portlets</div>
        <div class="description">Portlets</div>
    </li>
    <ol>
        <li icon="" requirement="required">
            <div class="title">JSF Portlet</div>
            <div class="description">JSF Portlet</div>
        </li>
        <li icon="" requirement="required">
            <div class="title">Spring Portlet MVC</div>
            <div class="description">Spring Portlet MVC</div>
        </li>
        <li icon="" requirement="required">
            <div class="title">Liferay MVC Portlet</div>
            <div class="description">Liferay MVC Portlet</div>
        </li>
        <li icon="" requirement="required">
            <div class="title">GenericPortlet</div>
            <div class="description">GenericPortlet</div>
        </li>
        <li icon="" requirement="required">
            <div class="title">Servlet-based Portlet</div>
            <div class="description">Servlet-based Portlet</div>
        </li>
        <li icon="" requirement="required">
            <div class="title">Struts 1 Portlet</div>
            <div class="description">Struts 1 Portlet</div>
        </li>
    </ol>
    <li icon="" requirement="required">
        <div class="title">Web plugins</div>
        <div class="description">Web plugins</div>
    </li>
    <li icon="" requirement="required">
        <div class="title">Ext plugins</div>
        <div class="description">Ext plugins</div>
    </li>
</ol>