# Liferay Upgrade Plan

Here are the steps for upgrading Liferay and your custom plugins to @product-ver@. Note, you can upgrade Liferay (steps I and II) and your custom plugins (step III) simultaneously.

| **Important:** Before executing the upgrade plan, read about upgrading a
| cluster or upgrading a sharded environment, if that's your situation.
| **Notice:** <a href="/deployment/deployment/-/knowledge_base/6-2/upgrading-liferay">
| If you're on Liferay Portal 6.0.x, upgrade to Liferay Portal 6.2. If you're on Liferay
| Portal 6.1.x, upgrade to @product@ 7.1.</a>

## Upgrade Steps

<ol id="root">
	<li requirement="required">
		<div class="title">Prepare for Upgrade</div>
		<div class="description">Prepare for Upgrade</div>
	</li>
	<ol>
		<li requirement="recommended">
			<div class="title">Check for Upgrade Planner Updates</div>
			<div class="description">Check for Upgrade Planner Updates</div>
		</li>
		<li requirement="recommended">
			<div class="title">Check Installed JDKs</div>
			<div class="description">Check Installed JDKs</div>
		</li>
	</ol>
	<li requirement="required">
		<div class="title"><a href="/deployment/docs/upgrading-to-product-ver">Upgrade database and configuration</a></div>
		<div class="description">Upgrade your @product@ database and configuration.</a>Note, step <em>D. Prepare a new @product@ production server</em> can be done in parallel with steps <em>A. through C.</em></div>
	</li>
	<ol>
		<li requirement="required">
			<div class="title"><a href="/deployment/docs/planning-for-deprecated-applications">Planning for deprecated applications</a></div>
			<div class="description">Examine the deprecated applications: Remove unwanted applications from production and note ones to modify after upgrading to @product-ver@</div>
		</li>
		<li requirement="required">
			<div class="title"><a href="/deployment/docs/test-upgrading-a-product-backup-copy">Test upgrading a database</a></div>
			<div class="description">Test upgrading a database</div>
		</li>
		<ol>
			<li requirement="required">
				<div class="title">Copy the production database and save the copy logs</div>
				<div class="description">Copy the production database and save the copy logs</div>
			</li>
			<li requirement="required">
				<div class="title">Copy the production installation to a test server and configure it to use the database copy.</div>
				<div class="description">Copy the production installation to a test server and configure it to use the database copy.</div>
			</li>
			<li requirement="required">
				<div class="title">Check the @product@ UI and database copy logs for unused objects (sites, users, pages, etc.)</div>
				<div class="description">Check the @product@ UI and database copy logs for unused objects (sites, users, pages, etc.)</div>
			</li>
			<li requirement="required">
				<div class="title">Use the script console or UI to remove the noted unused objects</div>
				<div class="description">Use the script console or UI to remove the noted unused objects</div>
			</li>
			<li requirement="required">
 				<div class="title">Test @product@ with its pruned database</div>
				<div class="description">Test @product@ with its pruned database</div>
			</li>
			<li requirement="required">
				<div class="title">Install @product-ver@ on a test server and configure it to use the database copy</div>
				<div class="description">Install @product-ver@ on a test server and configure it to use the database copy</div>
			</li>
			<li requirement="required">
 				<div class="title">Upgrade the database to @product-ver@ (see step <em>E. Upgrade the @product@ data</em>); then return here</div>
				<div class="description">Upgrade the database to @product-ver@ (see step <em>E. Upgrade the @product@ data</em>); then return here</div>
			</li>
			<li requirement="required">
				<div class="title">If the upgrade took too long, search the upgrade log to identify more unused objects. Then start back at step <em>1.</em> with a fresh copy of the production database.</div>
				<div class="description">If the upgrade took too long, search the upgrade log to identify more unused objects. Then start back at step <em>1.</em> with a fresh copy of the production database.</div>
			</li>
			<li requirement="required">
 				<div class="title">Test this upgraded portal and resolve any issues</div>
				<div class="description">Test this upgraded portal and resolve any issues</div>
			</li>
			<li requirement="required">
 				<div class="title">Checkpoint: Noted all unused objects and how to remove them</div>
 				<div class="description">Checkpoint: Noted all unused objects and how to remove them</div>
			</li>
		</ol>
		<li requirement="required">
			<div class="title"><a href="/deployment/docs/preparing-to-upgrade-the-product-database">Prepare to upgrade the production database</a></div>
			<div class="description">Prepare to upgrade the production database</div>
		</li>
		<ol>
			<li requirement="required">
				<div class="title">Remove all noted unused objects</div>
				<div class="description">Remove all noted unused objects</div>
			</li>
			<li requirement="required">
				<div class="title">Test @product@ with its pruned database</div>
				<div class="description">Test @product@ with its pruned database</div>
			</li>
			<li requirement="required">
				<div class="title">Upgrade your Marketplace apps</div>
				<div class="description">Upgrade your Marketplace apps</div>
			</li>
			<li requirement="required">
				<div class="title">Publish all staged changes to production</div>
				<div class="description">Publish all staged changes to production</div>
			</li>
			<li requirement="required">
				<div class="title">Synchronize a complete @product@ backup</div>
				<div class="description">Synchronize a complete @product@ backup, including your pruned production database</div>
			</li>
			<li requirement="required">
				<div class="title">Checkpoint: Ready to upgrade the production database</div>
				<div class="description">Checkpoint: Ready to upgrade the production database</div>
			</li>
		</ol>
		<li requirement="required">
			<div class="title"><a href="/deployment/docs/preparing-a-new-product-server-for-data-upgrade">Prepare a new @product@ server for data upgrade</a></div>
			<div class="description">Prepare a new @product@ server for data upgrade</div>
		</li>
		<ol>
			<li requirement="required">
				<div class="title">Install @product-ver@</div>
				<div class="description">Install @product-ver@</div>
			</li>
			<li requirement="required">
				<div class="title">Install the latest fix pack (DXP only)</div>
				<div class="description">Install the latest fix pack (DXP only)</div>
			</li>
			<li requirement="required">
				<div class="title">Migrate your OSGi configurations (@product@ 7.0+)</div>
				<div class="description">Migrate your OSGi configurations (@product@ 7.0+)</div>
			</li>
			<li requirement="required">
				<div class="title">Migrate your portal properties</div>
				<div class="description">Migrate your portal properties</div>
			</li>
			<ol>
				<li requirement="required">
					<div class="title">Update your portal properties</div>
					<div class="description">Update your portal properties</div>
				</li>
				<li requirement="required">
					<div class="title">Convert applicable properties to OSGi configurations</div>
					<div class="description">Convert applicable properties to OSGi configurations</div>
				</li>
			</ol>
			<li requirement="required">
				<div class="title">Configure your Documents and Media file store</div>
				<div class="description">Configure your Documents and Media file store</div>
			</li>
			<li requirement="required">
				<div class="title">Disable indexing</div>
				<div class="description">Disable indexing</div>
			</li>
			<li requirement="required">
				<div class="title">Checkpoint: Prepared the new @product@ production server</div>
				<div class="description">Checkpoint: Prepared the new @product@ production server</div>
			</li>
		</ol>
		<li requirement="required">
			<div class="title"><a href="/deployment/docs/upgrading-the-product-data">Upgrade the @product@ data</a></div>
			<div class="description">Upgrade the @product@ data</div>
		</li>
		<ol>
			<li requirement="required">
				<div class="title"><a href="/deployment/docs/tuning-your-database-for-the-upgrade">Tune your database for the upgrade</a></div>
				<div class="description">Tune your database for the upgrade</div>
			</li>
			<li requirement="required">
				<div class="title"><a href="/deployment/docs/configuring-the-data-upgrade">Configure the data upgrade</a></div>
				<div class="description">Configure the data upgrade</div>
			</li>
			<li requirement="required">
				<div class="title"><a href="/deployment/docs/upgrading-the-core-using-the-upgrade-tool">Upgrade the core</a></div>
				<div class="description">Upgrade the core</div>
			</li>
			<ol>
				<li requirement="required">
					<div class="title">Run the data upgrade tool</div>
					<div class="description">Run the data upgrade tool</div>
				</li>
				<li requirement="required">
					<div class="title">Resolve any core upgrade issues</div>
					<div class="description">Resolve any core upgrade issues</div>
				</li>
				<li requirement="required">
					<div class="title">restore the pruned production database backup</div>
					<div class="description">If the issues were with upgrades to @product@ 7.0 or lower, restore the pruned production database backup</div>
				</li>
				<li requirement="required">
					<div class="title">Upgrade your resolved issues</div>
					<div class="description">Upgrade your resolved issues (step <em>a</em>); continue if there were no issues</div>
				</li>
			</ol>
			<li requirement="required">
				<div class="title"><a href="/deployment/docs/upgrading-modules-using-gogo-shell">Upgrade the @product@ modules</a></div>
				<div class="description">Upgrade the @product@ modules, if you didn't upgrade them automatically with the core</div>
			</li>
			<ol>
				<li requirement="required">
					<div class="title">Upgrade modules that are ready for upgrade</div>
					<div class="description">Upgrade modules that are ready for upgrade</div>
				</li>
				<li requirement="required">
					<div class="title">Verify upgraded modules</div>
					<div class="description">Verify upgraded modules</div>
				</li>
				<li requirement="required">
					<div class="title">Resolve any module upgrade issues</div>
					<div class="description">Resolve any module upgrade issues</div>
				</li>
				<li requirement="required">
					<div class="title">Upgrade your resolved module issues</div>
					<div class="description">Upgrade your resolved module issues (step <em>a</em>); continue if there were no issues</div>
				</li>
			</ol>
			<li requirement="required">
				<div class="title">Checkpoint: Completed upgrading the database</div>
				<div class="description">Checkpoint: Completed upgrading the database</div>
			</li>
		</ol>
		<li requirement="required">
			<div class="title"><a href="/deployment/docs/executing-post-upgrade-tasks">Execute post-upgrade Tasks</a></div>
			<div class="description">Execute post-upgrade Tasks</div>
		</li>
		<ol>
			<li requirement="required">
				<div class="title">Remove the database tunning made for the upgrade process</div>
				<div class="description">Remove the database tunning made for the upgrade process</div>
			</li>
			<li requirement="required">
				<div class="title">Re-enable and re-index the search indexes</div>
				<div class="description">Re-enable and re-index the search indexes</div>
			</li>
			<li requirement="required">
				<div class="title">Update web content permissions (@product@ 7.0 and lower)</div>
				<div class="description">Update web content permissions (@product@ 7.0 and lower)</div>
			</li>
			<li requirement="required">
				<div class="title">Address deprecated apps</div>
				<div class="description">Address deprecated apps</div>
			</li>
			<li requirement="required">
				<div class="title">Checkpoint: Completed post-upgrade tasks</div>
				<div class="description">Checkpoint: Completed post-upgrade tasks</div>
			</li>
		</ol>
	</ol>
	<li requirement="required">
		<div class="title">Upgrade Code</div>
		<div class="description">Upgrade Code</div>
	</li>
	<ol>
		<li requirement="required">
			<div class="title">Setup Liferay Workspace</div>
		</li>
		<ol>
			<li requirement="required" commandId="create_new_liferay_workspace">
				<div class="title"><a href="">Create New Liferay Workspace</a></div>
				<div class="description">If you have an existing 7.x workspace, skip to the next step.</div>
			</li>
			<li requirement="required" commandId="import_existing_projects">
				<div class="title"><a href="">Import Existing Liferay Workspace</a></div>
				<div class="description">If you do not have an existing 7.x workspace, go to the previous step.</div>
			</li>
		</ol>
		<li requirement="required">
			<div class="title">Configure Liferay Workspace Settings</div>
			<div class="description">Configure Liferay Workspace Settings</div>
		</li>
		<ol>
			<li requirement="required" commandId="configure_bundle_url">
				<div class="title">Configure Bundle URL</div>
				<div class="description">Configure Bundle URL</div>
			</li>
			<li requirement="required">
				<div class="title">Configure Target Platform Version</div>
				<div class="description">Configure Target Platform Version</div>
			</li>
		</ol>
		<li requirement="required" commandId="initialize_server_bundle">
			<div class="title">Initialize Server Bundle</div>
			<div class="description">Initialize Server Bundle</div>
		</li>
	</ol>
	<li requirement="required">
		<div class="title">Migrate Plugin SDK Projects</div>
		<div class="description">Migrate Plugin SDK Projects</div>
	</li>
	<ol>
		<li requirement="required">
			<div class="title">Import Existing Plugins SDK Projects</div>
			<div class="description">Import Existing Plugins SDK Projects</div>
		</li>
		<li requirement="required">
			<div class="title">Migrate Existing Plugins to Workspace</div>
			<div class="description">Migrate Existing Plugins to Workspace</div>
		</li>
	</ol>
	<li requirement="required">
		<div class="title">Upgrade Build Dependencies</div>
		<div class="description">Upgrade Build Dependencies</div>
	</li>
	<ol>
		<li requirement="required">
			<div class="title">Update Repository URL</div>
			<div class="description">Update Repository URL</div>
		</li>
		<li requirement="required">
			<div class="title">Update Workspace Plugin Version</div>
			<div class="description">Update Workspace Plugin Version</div>
		</li>
		<li requirement="required">
			<div class="title">Remove Dependency Versions</div>
			<div class="description">Remove Dependency Versions</div>
		</li>
	</ol>
	<li requirement="required">
		<div class="title">Fix Upgrade Problems</div>
		<div class="description">Fix Upgrade Problems</div>
	</li>
	<ol>
		<li requirement="required" commandId="auto_correct_find_upgrade_problems">
			<div class="title">Auto-Correct Upgrade Problems</div>
			<div class="description">Auto-Correct Upgrade Problems</div>
		</li>
		<li requirement="required" commandId="find_upgrade_problems">
			<div class="title">Find Upgrade Problems</div>
			<div class="description">Find Upgrade Problems</div>
		</li>
		<li requirement="required">
			<div class="title">Resolve Upgrade Problems</div>
			<div class="description">Resolve Upgrade Problems</div>
		</li>
		<li requirement="required" commandId="remove_upgrade_problems_markers">
			<div class="title">Remove Problem Markers</div>
			<div class="description">Remove Problem Markers</div>
		</li>
	</ol>
	<li requirement="required">
		<div class="title">Upgrade Liferay Services</div>
		<div class="description">Upgrade Liferay Services</div>
	</li>
	<ol>
		<li commandId="remove_legacy_files" requirement="required">
			<div class="title">Remove Legacy Files</div>
			<div class="description">Remove Legacy Files</div>
		</li>
		<li requirement="required">
			<div class="title">Upgrade to Declarative Services</div>
			<div class="description">Upgrade to Declarative Services</div>
		</li>
		<li commandId="rebuild_services" requirement="required">
			<div class="title">Rebuild Services</div>
			<div class="description">Rebuild Services</div>
		</li>
	</ol>
	<li requirement="required">
		<div class="title">Upgrade Plugins to OSGi Modules</div>
		<div class="description">Upgrade Plugins to OSGi Modules</div>
	</li>
	<ol>
		<li requirement="required">
			<div class="title">Override/extension (module)</div>
			<div class="description">Override/extension (module)</div>
		</li>
		<li requirement="required">
			<div class="title">Liferay core JSP hook (WAR)</div>
			<div class="description">Liferay core JSP hook (WAR)</div>
		</li>
		<li requirement="required">
			<div class="title">Liferay portlet JSP hook (WAR)</div>
			<div class="description">Liferay portlet JSP hook (WAR)</div>
		</li>
		<li requirement="required">
			<div class="title">Service wrapper hook (WAR)</div>
			<div class="description">Service wrapper hook (WAR)</div>
		</li>
		<li requirement="required">
			<div class="title">Language key hook (WAR)</div>
			<div class="description">Language key hook (WAR)</div>
		</li>
		<li requirement="required">
			<div class="title">Model listener hook (WAR)</div>
			<div class="description">Model listener hook (WAR)</div>
		</li>
		<li requirement="required">
			<div class="title">Event actions hook (WAR)</div>
			<div class="description">Event actions hook (WAR)</div>
		</li>
		<li requirement="required">
			<div class="title">Servlet filter hook (WAR)</div>
			<div class="description">Servlet filter hook (WAR)</div>
		</li>
		<li requirement="required">
			<div class="title">Portal properties hook (WAR)</div>
			<div class="description">Portal properties hook (WAR)</div>
		</li>
		<li requirement="required">
			<div class="title">Struts action hook (WAR)</div>
			<div class="description">Struts action hook (WAR)</div>
		</li>
	</ol>
	<li requirement="required">
		<div class="title">Upgrading Themes</div>
		<div class="description">Upgrading Themes</div>
	</li>
	<li requirement="required">
		<div class="title">Layout Templates</div>
		<div class="description">Layout Templates</div>
	</li>
	<li requirement="required">
		<div class="title">Framework/feature upgrades</div>
		<div class="description">Framework/feature upgrades</div>
	</li>
	<ol>
		<li requirement="required">
			<div class="title">Using JNDI data source</div>
			<div class="description">Using JNDI data source</div>
		</li>
		<li requirement="required">
			<div class="title">Service Builder service invocation</div>
			<div class="description">Service Builder service invocation</div>
		</li>
		<li requirement="required">
			<div class="title">Service Builder</div>
			<div class="description">Service Builder</div>
		</li>
		<li requirement="required">
			<div class="title">Velociy templates (deprecated)</div>
			<div class="description">Velociy templates (deprecated)</div>
		</li>
	</ol>
	<li requirement="required">
		<div class="title">Portlets</div>
		<div class="description">Portlets</div>
	</li>
	<ol>
		<li requirement="required">
			<div class="title">JSF Portlet</div>
			<div class="description">JSF Portlet</div>
		</li>
		<li requirement="required">
			<div class="title">Spring Portlet MVC</div>
			<div class="description">Spring Portlet MVC</div>
		</li>
		<li requirement="required">
			<div class="title">Liferay MVC Portlet</div>
			<div class="description">Liferay MVC Portlet</div>
		</li>
		<li requirement="required">
			<div class="title">GenericPortlet</div>
			<div class="description">GenericPortlet</div>
		</li>
		<li requirement="required">
			<div class="title">Servlet-based Portlet</div>
			<div class="description">Servlet-based Portlet</div>
		</li>
		<li requirement="required">
			<div class="title">Struts 1 Portlet</div>
			<div class="description">Struts 1 Portlet</div>
		</li>
	</ol>
	<li requirement="required">
		<div class="title">Web plugins</div>
		<div class="description">Web plugins</div>
	</li>
	<li requirement="required">
		<div class="title">Ext plugins</div>
		<div class="description">Ext plugins</div>
	</li>
</ol>

## Related Topics

[Upgrading to @product-ver@](/deployment/deployment/-/knowledge_base/7-2/upgrading-to-liferay-72)

[Upgrading a Sharded Environment](/deployment/deployment/-/knowledge_base/7-2/upgrading-sharded-environment)

[Updating a Cluster](/deployment/deployment/-/knowledge_base/7-2/updating-a-cluster)

[From Liferay 6 to 7](/develop/tutorials/-/knowledge_base/7-2/from-liferay-6-to-liferay-7)

[From @product@ 7.x to 7.2](/develop/tutorials/-/knowledge_base/7-2/from-liferay-7-1-to-7-2)

[Implementing Application Data Upgrades](/develop/tutorials/-/knowledge_base/7-2/data-upgrades)