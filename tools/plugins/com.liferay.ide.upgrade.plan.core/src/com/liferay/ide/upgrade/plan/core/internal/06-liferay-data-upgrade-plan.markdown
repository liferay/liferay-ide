<h1>Liferay Data Upgrade Plan</h1>

<p>
Here are the steps for upgrading your Liferay database and configuration. The steps here outline the process and link to the detailed articles.
</p>

<p>
<strong>Important:</strong> If you're upgrading a <a href="/docs/7-2/deploy/-/knowledge_base/deploy/updating-a-cluster">cluster</a> or <a href="/docs/7-2/deploy/-/knowledge_base/deploy/upgrading-sharded-environment">sharded environment</a>, read their upgrade instructions first.
</p>

<p>
<strong>Important:</strong> If you're on Liferay Portal 6.0.x, <a href="/docs/6-2/deploy/-/knowledge_base/deploy/upgrading-liferay">upgrade to Liferay Portal 6.2</a> before upgrading to 7.2.
</p>

<p>
<strong>Important:</strong> If you're running on Liferay Portal 6.1.x, <a href="/docs/7-1/deploy/-/knowledge_base/deploy/upgrading-to-liferay-71">upgrade to Liferay 7.1</a><a href="/docs/7-1/deploy/-/knowledge_base/deploy/upgrading-to-liferay-71"> before upgrading to 7.2.
</p>

<p>
<strong>Note:</strong> You can <a href="/docs/7-2/deploy/-/knowledge_base/deploy/preparing-a-new-product-server-for-data-upgrade">prepare a new Liferay server for data upgrade</a> in parallel with the steps up to and including the step to <a href="/docs/7-2/deploy/-/knowledge_base/deploy/upgrading-the-product-data">upgrade the Liferay data.</a>
</p>

<p>
Here are the data upgrade steps:
</p>

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
		<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/01-upgrading-to-liferay-7-2-intro.markdown#upgrading-to-product-ver">Upgrade the Database and Configuration</a></div>
		<div class="description">Here you'll upgrade your Liferay database and configuration. Start with understanding the general steps and identifying your upgrade path.</div>
	</li>
	<ol>
		<li icon="" requirement="required">
			<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/02-planning-for-deprecated-apps.markdown">Plan for Deprecated Applications</a></div>
			<div class="description">Examine the deprecated applications: Remove unwanted applications from production and note ones to modify after upgrading.</div>
		</li>
		<li icon="" requirement="required">
			<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/03-test-upgrading-a-liferay-backup-copy.markdown#test-upgrading-a-product-backup-copy">Test Upgrading a Database</a></div>
			<div class="description">Before upgrading your production instance, follow these steps to prune a copy of your database and make sure that you can upgrade it correctly and efficiently.</div>
		</li>
		<ol>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/03-test-upgrading-a-liferay-backup-copy.markdown#copy-the-production-installation-to-a-test-server">Copy the Production Installation</a></div>
				<div class="description">Copy the production installation to a test server.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/03-test-upgrading-a-liferay-backup-copy.markdown#copy-the-production-backup-to-the-test-database">Copy the Production Database Backup</a></div>
				<div class="description">Copy the production backup to the test database and save the copy logs for analysis.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/03-test-upgrading-a-liferay-backup-copy.markdown#remove-duplicate-web-content-structure-field-names-">Remove Duplicate Web Content Structure Field Names</a></div>
				<div class="description">Remove duplicate web content and structure field names.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/03-test-upgrading-a-liferay-backup-copy.markdown#find-and-remove-unused-objects">Find and Remove Unused Objects</a></div>
				<div class="description">The less objects your instance has the faster its data upgrade will be. Follow these steps to find and remove orphaned and unused objects.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/03-test-upgrading-a-liferay-backup-copy.markdown#test-product-with-its-pruned-database-copy">Test Liferay with its Pruned Database Copy</a></div>
				<div class="description">Test Liferay with its pruned database copy.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/03-test-upgrading-a-liferay-backup-copy.markdown#install-product-ver-on-a-test-server-and-configure-it-to-use-the-pruned-database">Install the New Liferay Version on a Test Server</a></div>
				<div class="description">Install the new Liferay version on a test server configured to use the pruned database.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/03-test-upgrading-a-liferay-backup-copy.markdown#upgrade-the-database">Upgrade the Liferay Database</a></a></div>
				<div class="description">Upgrade the Liferay database</a>; then return here.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/03-test-upgrading-a-liferay-backup-copy.markdown#test-upgrading-a-product-backup-copy">If the Upgrade Took Too Long, Prune the Database More and Restart the Upgrade</a></div>
				<div class="description">If the upgrade took too long search the upgrade log for more unused objects. Then restart testing upgrades on a fresh copy of the Liferay database.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/03-test-upgrading-a-liferay-backup-copy.markdown#test-the-upgraded-portal-and-resolve-any-issues">Test the Upgraded Instance</a></div>
				<div class="description">Test the upgraded instance and resolve any issues.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/03-test-upgrading-a-liferay-backup-copy.markdown#checkpoint-youve-pruned-and-upgraded-a-production-database-copy">Checkpoint: You've pruned and upgraded a production database copy</a></div>
				<div class="description">Congratulations! You've pruned and upgraded your production database copy. You're ready to prepare upgrading the production database.</div>
			</li>
		</ol>
		<li icon="" requirement="required">
			<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/04-preparing-to-upgrade-the-liferay-database.markdown#preparing-to-upgrade-the-product-database">Prepare to Upgrade the Liferay Database</a></div>
			<div class="description">Preparing for the production database upgrade involves pruning and testing it, upgrading your Marketplace apps, publishing staged changes, and synchronizing a complete data and configuration backup.</div>
		</li>
		<ol>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/04-preparing-to-upgrade-the-liferay-database.markdown#remove-all-unused-objects-you-identified-earlier">Remove All Noted Unused Objects</a></div>
				<div class="description">Remove all unused objects you noted from pruning your test database.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/04-preparing-to-upgrade-the-liferay-database.markdown#test-product-with-its-pruned-database">Test Liferay</a></div>
				<div class="description">Test Liferay with its pruned database to make sure it looks and works as you expect.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/04-preparing-to-upgrade-the-liferay-database.markdown#upgrade-your-marketplace-apps">Upgrade your Marketplace Apps</a></div>
				<div class="description">Upgrade all of the apps you use from the Marketplace.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/04-preparing-to-upgrade-the-liferay-database.markdown#publish-all-staged-changes-to-production">Publish all Staged Changes</a></div>
				<div class="description">Publish all staged changes to production.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/04-preparing-to-upgrade-the-liferay-database.markdown#synchronize-a-complete-product-backup">Synchronize a Complete Liferay Backup</a></div>
				<div class="description">Synchronize a complete Liferay backup of your configuration and pruned production database.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title">Checkpoint: Ready to upgrade the production database</div>
				<div class="description">Congratulations! You've prepared your production Liferay database for the upgrade.</div>
			</li>
		</ol>
		<li icon="" requirement="required">
			<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/04-preparing-to-upgrade-the-liferay-database.markdown#synchronize-a-complete-product-backup">Prepare a New Liferay Server</a></div>
			<div class="description">Prepare a new Liferay server for executing the data upgrade.</div>
		</li>
		<ol>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/05-preparing-a-new-liferay-server.markdown#request-an-upgrade-patch-from-liferay-support-liferay-dxp-only">Request an Upgrade Patch</a></div>
				<div class="description">Request an upgrade patch from Liferay Support (DXP only).</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/05-preparing-a-new-liferay-server.markdown#install-product-ver">Install the New Liferay Version</a></div>
				<div class="description">Install the new Liferay version on your new server.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/05-preparing-a-new-liferay-server.markdown#install-product-ver">Install the Latest Upgrade Patch of Fix Pack</a></div>
				<div class="description">Install the latest upgrade patch of fix pack. (DXP only)</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/05-preparing-a-new-liferay-server.markdown#migrate-your-osgi-configurations-product-70">Migrate Your OSGi Configurations</a></div>
				<div class="description">Migrate your OSGi configurations to your new server. (Liferay 7.0+)</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/05-preparing-a-new-liferay-server.markdown#migrate-your-portal-properties">Migrate Your Portal Properties</a></div>
				<div class="description">Migrate your portal properties to your new server.</div>
			</li>
			<ol>
				<li icon="" requirement="required">
					<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/05-preparing-a-new-liferay-server.markdown#update-your-portal-properties">Update Your Portal Properties</a></div>
					<div class="description">Update your portal properties.</div>
				</li>
				<li icon="" requirement="required">
					<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/05-preparing-a-new-liferay-server.markdown#convert-applicable-properties-to-osgi-configurations">Convert Applicable Properties to OSGi Configurations</a></div>
					<div class="description">Convert applicable properties to OSGi configurations (OSGi Config Admin).</div>
				</li>
			</ol>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/05-preparing-a-new-liferay-server.markdown#configure-your-documents-and-media-file-store">Configure Your Documents and Media File Store</a></div>
				<div class="description">Configure your Documents and Media file store on the new server.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/05-preparing-a-new-liferay-server.markdown#configure-your-documents-and-media-file-store">Disable Indexing</a></div>
				<div class="description">Improve the data upgrade performance by disabling indexing.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/05-preparing-a-new-liferay-server.markdown#configure-your-documents-and-media-file-store">Checkpoint: Prepared the new Liferay production server</a></div>
				<div class="description">Congratulations! You've prepared the new Liferay production server for executing the Liferay data upgrade.</div>
			</li>
		</ol>
		<li icon="" requirement="required">
			<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/06-upgrading-the-liferay-database/01-upgrading-the-liferay-database-intro.markdown#upgrading-the-product-data">Upgrade the Liferay data</a></div>
			<div class="description">This section explains the data upgrade options, upgrade configuration, and the upgrade process.</div>
		</li>
		<ol>
			<li icon="" requirement="required">
				<div class="title"><a href="/docs/7-2/deploy/-/knowledge_base/deploy/tuning-your-database-for-the-upgrade">Tune Your Database for the Upgrade</a></div>
				<div class="description">Tune your database for the upgrade.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/06-upgrading-the-liferay-database/03-configuring-the-data-upgrade.markdown#configuring-the-data-upgrade">Configure the Data Upgrade</a></div>
				<div class="description">Configure the data upgrade, including the data store and whether to automatically upgrade the modules.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/06-upgrading-the-liferay-database/04-upgrading-the-core-using-the-upgrade-tool.markdown#upgrading-the-core-using-the-upgrade-tool">Upgrade the Core</a></div>
				<div class="description">Upgrade the Liferay core.</div>
			</li>
			<ol>
				<li icon="" requirement="required">
					<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/06-upgrading-the-liferay-database/04-upgrading-the-core-using-the-upgrade-tool.markdown#upgrade-tool-usage">Run the Data Upgrade Tool</a></div>
					<div class="description">Run the data upgrade tool. Resolve any core upgrade issues.</div>
				</li>
				<li icon="" requirement="required">
					<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/04-preparing-to-upgrade-the-liferay-database.markdown#synchronize-a-complete-product-backup">Issues Upgrading to 7.0 or Lower? Restore the Database Backup</a></div>
					<div class="description">If the issues were with upgrades to Liferay 7.0 or lower, get a clean start by restoring the pruned production database backup.</div>
				</li>
				<li icon="" requirement="required">
					<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/06-upgrading-the-liferay-database/04-upgrading-the-core-using-the-upgrade-tool.markdown#upgrading-the-core-using-the-upgrade-tool">Upgrade Your Resolved Issues</a></div>
					<div class="description">If there were issues upgrading to 7.2, resolve them and restart the data upgrade tool; continue if there were no issues.</div>
				</li>
			</ol>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/06-upgrading-the-liferay-database/05-upgrading-modules-using-gogo-shell.markdown#upgrading-modules-using-gogo-shell">Upgrade the Liferay Modules</a></div>
				<div class="description">Learn how to use Gogo Shell to upgrade the Liferay modules, if you didn't upgrade them automatically with the core.</div>
			</li>
			<ol>
				<li icon="" requirement="required">
					<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/06-upgrading-the-liferay-database/05-upgrading-modules-using-gogo-shell.markdown#executing-module-upgrades">Upgrade Modules that are Ready for Upgrade</a></div>
					<div class="description">Discover which modules are ready for upgrade and upgrade them.</div>
				</li>
				<li icon="" requirement="required">
					<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/06-upgrading-the-liferay-database/05-upgrading-modules-using-gogo-shell.markdown#executing-verify-processes">Verify Upgraded Modules</a></div>
					<div class="description">Verify the upgraded modules.</div>
				</li>
				<li icon="" requirement="required">
					<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/06-upgrading-the-liferay-database/05-upgrading-modules-using-gogo-shell.markdown#checking-upgrade-status">Resolve any Module Upgrade Issues</a></div>
					<div class="description">Resolve any module upgrade issues.</div>
				</li>
				<li icon="" requirement="required">
					<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/06-upgrading-the-liferay-database/05-upgrading-modules-using-gogo-shell.markdown#executing-module-upgrades">Upgrade your resolved module issues</div>
					<div class="description">Upgrade your resolved module issues; continue if there were no issues.</div>
				</li>
			</ol>
			<li icon="" requirement="required">
				<div class="title">Checkpoint: Completed upgrading the Liferay data</div>
				<div class="description">Congratulation! You've completed upgrading the Liferay data. It's time to get your production server ready.</div>
			</li>
		</ol>
		<li icon="" requirement="required">
			<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/07-executing-post-upgrade-tasks.markdown#executing-post-upgrade-tasks">Execute post-upgrade Tasks</a></div>
			<div class="description">Before you go live with your upgraded Liferay data, restore your database settings, re-enable search indexes, and make sure you've handled your deprecated apps.</div>
		</li>
		<ol>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/07-executing-post-upgrade-tasks.markdown#tuning-your-database-for-production">Remove the Database Tuning</a></div>
				<div class="description">Remove the database tuning you set for the upgrade process.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/07-executing-post-upgrade-tasks.markdown#re-enabling-search-indexing-and-reindexing-search-indexes">Re-enable and Re-Index the Search Indexes</a></div>
				<div class="description">Re-enable the search indexes and re-index Liferay.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/07-executing-post-upgrade-tasks.markdown#enabling-web-content-view-permissions">Update Web Content Permissions</a></div>
	            <div class="description">Update web content permissions. (7.0 and lower)</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/02-planning-for-deprecated-apps.markdown">Address Deprecated Apps</a></div>
				<div class="description">Address any deprecated apps that still need handling.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title">Checkpoint: Completed the post-upgrade tasks</div>
				<div class="description">Congratulations! You've completed the post-upgrade tasks and your Liferay data upgrade.</div>
			</li>
		</ol>
	</ol>
</ol>