# Liferay Code Upgrade Plan

[TOC levels=1-4]

<p>
Here are the steps for upgrading your development environment and custom plugins.
</p>

<ol id="root">
    <li>
        <div class="title">Upgrade Your Development Environment</div>
        <div class="description">Upgrade development environment to the latest version of Liferay Workspace and initialize your server bundle.</div>
    </li>
    <ol>
        <li>
            <div class="title">Set up Liferay Workspace</div>
        </li>
        <ol>
            <li commandId="create_new_liferay_workspace">
                <div class="title"><a href="https://web-community-beta.wedeploy.io/web/guest/docs/7-2/reference/-/knowledge_base/reference/tooling">Create New Liferay Workspace</a></div>
                <div class="description">If you have an existing 7.x workspace, skip to the next step.</div>
            </li>
            <li commandId="import_existing_liferay_workspace">
                <div class="title"><a href="https://web-community-beta.wedeploy.io/web/guest/docs/7-2/reference/-/knowledge_base/reference/blade-cli">Import Existing Liferay Workspace</a></div>
                <div class="description">If you do not have an existing 7.x workspace, go to the previous step.</div>
            </li>
        </ol>
        <li>
            <div class="title">Configure Liferay Workspace Settings</div>
            <div class="description">Configure your Liferay Workspace settings.</div>
        </li>
        <ol>
            <li commandId="configure_bundle_url">
                <div class="title">Configure Bundle URL</div>
                <div class="description">Configure your bundle URL.</div>
            </li>
            <li commandId="configure_target_platform_version">
                <div class="title">Configure Target Platform Version</div>
                <div class="description">Configure your Target Platform version.</div>
            </li>
        </ol>
        <li commandId="initialize_server_bundle">
            <div class="title">Initialize Server Bundle</div>
            <div class="description">Initialize your Liferay server bundle.</div>
        </li>
    </ol>
    <li>
        <div class="title">Migrate Plugin SDK Projects</div>
        <div class="description">Migrate your Plugin SDK projects to your Liferay Workspace.</div>
    </li>
    <ol>
        <li commandId="import_existing_plugins_sdk">
            <div class="title">Import Existing Plugins SDK Projects</div>
            <div class="description">Import your existing Plugins SDK projects.</div>
        </li>
        <li commandId="migrate_existing_plugins_to_workspace">
            <div class="title">Migrate Existing Plugins to Workspace</div>
            <div class="description">Migrate existing plugins to Workspace.</div>
        </li>
    </ol>
    <li>
        <div class="title">Upgrade Build Dependencies</div>
        <div class="description">Upgrade your build dependencies.</div>
    </li>
    <ol>
        <li commandId="update_repository_url">
            <div class="title">Update Repository URL</div>
            <div class="description">Update your repository URL.</div>
        </li>
        <li>
            <div class="title">Update Workspace Plugin Version</div>
            <div class="description">Update your Workspace Plugin version</div>
        </li>
        <li>
            <div class="title">Remove dependency Versions</div>
            <div class="description">Remove dependency versions.</div>
        </li>
    </ol>
    <li>
        <div class="title">Fix Upgrade Problems</div>
        <div class="description">Here you'll fix an assortment of problems found in upgrading your code.</div>
    </li>
    <ol>
        <li commandId="auto_correct_find_upgrade_problems">
            <div class="title">Auto-Correct Upgrade Problems</div>
            <div class="description">Leverage auto-correct to upgrade code.</div>
        </li>
        <li commandId="find_upgrade_problems">
            <div class="title">Find Upgrade Problems</div>
            <div class="description">Find upgrade problems.</div>
        </li>
        <li>
            <div class="title">Resolve Upgrade Problems</div>
            <div class="description">Mark upgrade problems as resolved after addressing them.</div>
        </li>
        <li commandId="remove_upgrade_problems_markers">
            <div class="title">Remove Problem Markers</div>
            <div class="description">Remove problem markers.</div>
        </li>
    </ol>
    <li>
        <div class="title">Upgrade Liferay Service Builder Services</div>
        <div class="description">Upgrade your Liferay Service Builder Services.</div>
    </li>
    <ol>
        <li commandId="remove_legacy_files">
            <div class="title">Remove Legacy Files</div>
            <div class="description">Remove unneeded legacy files.</div>
        </li>
        <li>
            <div class="title">Upgrade to Declarative Services</div>
            <div class="description">Upgrade to OSGi Declarative Services.</div>
        </li>
        <li commandId="rebuild_services">
            <div class="title">Rebuild Services</div>
            <div class="description">Rebuild your services.</div>
        </li>
    </ol>
    <li>
        <div class="title">Upgrade Customization Plugins</div>
        <div class="description">Upgrade your customization plugins, such as wrappers, overrides, and extension hooks.</div>
    </li>
    <ol>
        <li>
            <div class="title">Upgrade Override/Extension Modules</div>
            <div class="description">Upgrade your override and extension modules.</div>
        </li>
        <li>
            <div class="title">Upgrade Liferay Core JSP Hooks</div>
            <div class="description">Upgrade your Liferay core JSP Hook WARs.</div>
        </li>
        <li>
            <div class="title">Upgrade Liferay Portlet JSP Hooks</div>
            <div class="description">Upgrade your Liferay portlet JSP Hook WARs.</div>
        </li>
        <li>
            <div class="title">Upgrade Service Wrapper Hooks</div>
            <div class="description">Upgrade your service wrapper Hook WARs.</div>
        </li>
        <li>
            <div class="title">Upgrade Language Key Hooks</div>
            <div class="description">Upgrade your Language Key Hook WARs.</div>
        </li>
        <li>
            <div class="title">Upgrade Model Listener Hooks</div>
            <div class="description">Upgrade your Model Listener Hook WARs.</div>
        </li>
        <li>
            <div class="title">Upgrade Event Actions Hooks</div>
            <div class="description">Upgrade your Event Actions Hook WARs.</div>
        </li>
        <li>
            <div class="title">Upgrade Servlet Filter Hooks</div>
            <div class="description">Upgrade your Servlet Filter Hook WARs.</div>
        </li>
        <li>
            <div class="title">Upgrade Portal Properties Hooks</div>
            <div class="description">Upgrade your Portal Properties Hook WARs.</div>
        </li>
        <li>
            <div class="title">Upgrade Struts Action Hooks</div>
            <div class="description">Upgrade your Struts Action Hook WARs.</div>
        </li>
    </ol>
    <li>
        <div class="title">Upgrade Themes</div>
        <div class="description">Upgrade your Themes.</div>
    </li>
    <li>
        <div class="title">Upgrade Layout Templates</div>
        <div class="description">Upgrade your Layout Templates.</div>
    </li>
    <li>
        <div class="title">Upgrade Frameworks and Features</div>
        <div class="description">Upgrade your use of frameworks and features.</div>
    </li>
    <ol>
        <li>
            <div class="title">Upgrade JNDI Data Source Usage</div>
            <div class="description">Upgrade JNDI data source usage.</div>
        </li>
        <li>
            <div class="title">Upgrade Service Builder Service Invocation</div>
            <div class="description">Upgrade Service Builder service invocations.</div>
        </li>
        <li>
            <div class="title">Upgrade Service Builder</div>
            <div class="description">Upgrade Service Builder.</div>
        </li>
        <li>
            <div class="title">Upgrade Velociy templates (deprecated)</div>
            <div class="description">Upgrade or migrate off of Velociy templates (deprecated).</div>
        </li>
    </ol>
    <li>
        <div class="title">Upgrade Portlets</div>
        <div class="description">Upgrade your portlets.</div>
    </li>
    <ol>
        <li>
            <div class="title">Upgrade JSF Portlets</div>
            <div class="description">Upgrade your JSF portlets.</div>
        </li>
        <li>
            <div class="title">Upgrade Spring Portlet MVC Portlets</div>
            <div class="description">Upgrade your Spring Portlet MVC portlets.</div>
        </li>
        <li>
            <div class="title">Upgrade Liferay MVC Portlets</div>
            <div class="description">Upgrade your Liferay MVC Portlets.</div>
        </li>
        <li>
            <div class="title">Upgrade GenericPortlets</div>
            <div class="description">GenericPortlets</div>
        </li>
        <li>
            <div class="title">Upgrade Servlet-based Portlets</div>
            <div class="description">Upgrade your servlet-based portlets.</div>
        </li>
        <li>
            <div class="title">Upgrade Struts 1 Portlets</div>
            <div class="description">Upgrade your Struts 1 portlets.</div>
        </li>
    </ol>
    <li>
        <div class="title">Upgrade Web Plugins</div>
        <div class="description">Upgrade your web plugins.</div>
    </li>
    <li>
        <div class="title">Upgrade Ext Plugins</div>
        <div class="description">Upgrade your Ext plugins.</div>
    </li>
</ol>