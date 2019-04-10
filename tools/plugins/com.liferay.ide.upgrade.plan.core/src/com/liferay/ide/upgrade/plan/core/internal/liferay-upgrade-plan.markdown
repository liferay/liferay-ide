---
header-id: liferay-upgrade-plan
---

# Liferay Upgrade Plan

[TOC levels=1-4]

Here are the steps for upgrading your @product@ database and configuration to @product-ver@. The section
[Upgrading to @product-ver@](/docs/7-2/deploy/-/knowledge_base/deploy/upgrading-to-product-ver)
demonstrate the process. The steps here outline the process and link to the
detailed articles.

| **Important:** If you're upgrading a
| [cluster](/docs/7-2/deploy/-/knowledge_base/deploy/updating-a-cluster)
| or
| [sharded environment](/docs/7-2/deploy/-/knowledge_base/deploy/upgrading-sharded-environment),
| read their upgrade instructions first.

| **Important:** If you're on Liferay Portal 6.0.x,
| [upgrade to Liferay Portal 6.2](/docs/6-2/deploy/-/knowledge_base/deploy/upgrading-liferay)
| before upgrading to @product-ver@.

| **Important:** If you're running on Liferay Portal 6.1.x,
| [upgrade to @product@ 7.1](/docs/7-1/deploy/-/knowledge_base/deploy/upgrading-to-liferay-71)
| before upgrading to @product-ver@.

| **Note:** You can
| [prepare a new @product@ server for data upgrade](/docs/7-2/deploy/-/knowledge_base/deploy/preparing-a-new-product-server-for-data-upgrade)
| in parallel with the steps up to and including the step to
| [upgrade the @product@ data.](/docs/7-2/deploy/-/knowledge_base/deploy/upgrading-the-product-data)

Here are the upgrade steps:

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
		<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/01-upgrading-to-liferay-7-2-intro.markdown#upgrading-to-product-ver">Upgrade the database and configuration</a></div>
		<div class="description">Upgrade your @product@ database and configuration.</a></div>
	</li>
	<ol>
		<li icon="" requirement="required">
			<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/02-planning-for-deprecated-apps.markdown">Plan for deprecated applications</a></div>
			<div class="description"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/02-planning-for-deprecated-apps.markdown">Examine the deprecated applications: Remove unwanted applications from production and note ones to modify after upgrading to @product-ver@.</a></div>
		</li>
		<li icon="" requirement="required">
			<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/03-test-upgrading-a-liferay-backup-copy.markdown#test-upgrading-a-product-backup-copy">Test upgrading a database</a></div>
			<div class="description"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/03-test-upgrading-a-liferay-backup-copy.markdown#test-upgrading-a-product-backup-copy">Test upgrading a @product@ backup copy.</a></div>
		</li>
		<ol>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/03-test-upgrading-a-liferay-backup-copy.markdown#copy-the-production-installation-to-a-test-server">Copy the Production Installation</a></div>
				<div class="description">Copy the production installation to a test server.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/03-test-upgrading-a-liferay-backup-copy.markdown#copy-the-production-backup-to-the-test-database">Copy the Production Database Backup</a></div>
				<div class="description">Copy the production backup to the test database and save the copy logs.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/03-test-upgrading-a-liferay-backup-copy.markdown#remove-duplicate-web-content-structure-field-names-">Remove Duplicate Web Content Structure Field Names</a></div>
				<div class="description">Remove duplicate web content and structure field names.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/03-test-upgrading-a-liferay-backup-copy.markdown#find-and-remove-unused-objects">Find and Remove Unused Objects</a></div>
				<div class="description">Find and remove unused objects.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/03-test-upgrading-a-liferay-backup-copy.markdown#test-product-with-its-pruned-database-copy">Test @product@ with its Pruned Database Copy</a></div>
				<div class="description">Test @product@ with its pruned database copy.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/03-test-upgrading-a-liferay-backup-copy.markdown#install-product-ver-on-a-test-server-and-configure-it-to-use-the-pruned-database">Install @product-ver@ on a Test Server</a></div>
				<div class="description">Install @product-ver@ on a test server configured to use the pruned database.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/03-test-upgrading-a-liferay-backup-copy.markdown#upgrade-the-database">Upgrade the database to @product-ver@</a></a></div>
				<div class="description">Upgrade the database to @product-ver@</a>; then return here.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title">Did the upgrade took too long?</div>
				<div class="description">If the upgrade took too long <a href="upgrading-the-product-data">search the upgrade log for more unused objects</a>. Then
				<a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/03-test-upgrading-a-liferay-backup-copy.markdown#test-upgrading-a-product-backup-copy">restart testing upgrades on with a fresh copy of the @product@ database.</a></div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/03-test-upgrading-a-liferay-backup-copy.markdown#test-the-upgraded-portal-and-resolve-any-issues">Test the Upgraded Portal</a></div>
				<div class="description">Test the upgraded portal and resolve any issues.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/03-test-upgrading-a-liferay-backup-copy.markdown#checkpoint-youve-pruned-and-upgraded-a-production-database-copy">Checkpoint: You've pruned and upgraded a production database copy</a></div>
				<div class="description">Checkpoint: You've pruned and upgraded a production database copy.</div>
			</li>
		</ol>
		<li icon="" requirement="required">
			<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/04-preparing-to-upgrade-the-liferay-database.markdown#preparing-to-upgrade-the-product-database">Prepare to upgrade the @product@ database</a></div>
			<div class="description">Prepare to upgrade the @product@ database.</div>
		</li>
		<ol>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/04-preparing-to-upgrade-the-liferay-database.markdown#remove-all-unused-objects-you-identified-earlier">Remove all noted unused objects</a></div>
				<div class="description">Remove all noted unused objects.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/04-preparing-to-upgrade-the-liferay-database.markdown#test-product-with-its-pruned-database">Test @product@</a></div>
				<div class="description">Test @product@ with its pruned database.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/04-preparing-to-upgrade-the-liferay-database.markdown#upgrade-your-marketplace-apps">Upgrade your Marketplace apps</a></div>
				<div class="description">Upgrade your Marketplace apps.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/04-preparing-to-upgrade-the-liferay-database.markdown#publish-all-staged-changes-to-production">Publish all staged changes</a></div>
				<div class="description">Publish all staged changes to production.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/04-preparing-to-upgrade-the-liferay-database.markdown#synchronize-a-complete-product-backup">Synchronize a complete @product@ backup</a></div>
				<div class="description">Synchronize a complete @product@ backup, including your pruned production database.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title">Checkpoint: Ready to upgrade the production database</div>
				<div class="description">Checkpoint: Ready to upgrade the production database.</div>
			</li>
		</ol>
		<li icon="" requirement="required">
			<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/04-preparing-to-upgrade-the-liferay-database.markdown#synchronize-a-complete-product-backup">Prepare a new @product@ server</a></div>
			<div class="description">Prepare a new @product@ server for data upgrade.</div>
		</li>
		<ol>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/05-preparing-a-new-liferay-server.markdown#request-an-upgrade-patch-from-liferay-support-liferay-dxp-only">Request an upgrade patch</a></div>
				<div class="description">Request an upgrade patch (DXP only).</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/05-preparing-a-new-liferay-server.markdown#install-product-ver">Install @product-ver@</a></div>
				<div class="description">Install @product-ver@.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/05-preparing-a-new-liferay-server.markdown#install-product-ver">Install the latest upgrade patch of fix pack</a></div>
				<div class="description">Install the latest upgrade patch of fix pack. (DXP only)</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/05-preparing-a-new-liferay-server.markdown#migrate-your-osgi-configurations-product-70">Migrate your OSGi configurations</a></div>
				<div class="description">Migrate your OSGi configurations. (@product@ 7.0+)</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/05-preparing-a-new-liferay-server.markdown#migrate-your-portal-properties">Migrate your portal properties</a></div>
				<div class="description">Migrate your portal properties.</div>
			</li>
			<ol>
				<li icon="" requirement="required">
					<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/05-preparing-a-new-liferay-server.markdown#update-your-portal-properties">Update your portal properties</a></div>
					<div class="description">Update your portal properties.</div>
				</li>
				<li icon="" requirement="required">
					<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/05-preparing-a-new-liferay-server.markdown#convert-applicable-properties-to-osgi-configurations">Convert applicable properties to OSGi configurations</a></div>
					<div class="description">Convert applicable properties to OSGi configurations.</div>
				</li>
			</ol>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/05-preparing-a-new-liferay-server.markdown#configure-your-documents-and-media-file-store">Configure your Documents and Media file store</a></div>
				<div class="description">Configure your Documents and Media file store.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/05-preparing-a-new-liferay-server.markdown#configure-your-documents-and-media-file-store">Disable indexing</a></div>
				<div class="description">>Disable indexing.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/05-preparing-a-new-liferay-server.markdown#configure-your-documents-and-media-file-store">Checkpoint: Prepared the new @product@ production server</a></div>
				<div class="description">Checkpoint: Prepared the new @product@ production server.</div>
			</li>
		</ol>
		<li icon="" requirement="required">
			<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/06-upgrading-the-liferay-database/01-upgrading-the-liferay-database-intro.markdown#upgrading-the-product-data">Upgrade the @product@ data</a></div>
			<div class="description">Upgrade the @product@ data.</div>
		</li>
		<ol>
			<li icon="" requirement="required">
				<div class="title"><a href="/docs/7-2/deploy/-/knowledge_base/deploy/tuning-your-database-for-the-upgrade">Tune your database for the upgrade</a></div>
				<div class="description">Tune your database for the upgrade.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/06-upgrading-the-liferay-database/03-configuring-the-data-upgrade.markdown#configuring-the-data-upgrade">Configure the data upgrade</a></div>
				<div class="description">Configure the data upgrade.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/06-upgrading-the-liferay-database/04-upgrading-the-core-using-the-upgrade-tool.markdown#upgrading-the-core-using-the-upgrade-tool">Upgrade the core</a></div>
				<div class="description">Upgrade the core.</div>
			</li>
			<ol>
				<li icon="" requirement="required">
					<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/06-upgrading-the-liferay-database/04-upgrading-the-core-using-the-upgrade-tool.markdown#upgrade-tool-usage">Run the data upgrade tool</a></div>
					<div class="description">Run the data upgrade tool.</div>
				</li>
				<li icon="" requirement="required">
					<div class="title">Resolve any core upgrade issues</div>
					<div class="description">Resolve any core upgrade issues.</div>
				</li>
				<li icon="" requirement="required">
					<div class="title">Restore the pruned production database backup.</div>
					<div class="description">If the issues were with upgrades to @product@ 7.0 or lower,
					<a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/04-preparing-to-upgrade-the-liferay-database.markdown#synchronize-a-complete-product-backup">restore the pruned production database backup.</a></div>
				</li>
				<li icon="" requirement="required">
					<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/06-upgrading-the-liferay-database/04-upgrading-the-core-using-the-upgrade-tool.markdown#upgrading-the-core-using-the-upgrade-tool">Upgrade your resolved issues</a></div>
					<div class="description">If there were issues, resolve them and <a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/06-upgrading-the-liferay-database/04-upgrading-the-core-using-the-upgrade-tool.markdown#upgrading-the-core-using-the-upgrade-tool">restart the data upgrade tool;</a> continue if there were no issues.</div>
				</li>
			</ol>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/06-upgrading-the-liferay-database/05-upgrading-modules-using-gogo-shell.markdown#upgrading-modules-using-gogo-shell">Upgrade the @product@ modules</a></div>
				<div class="description">Upgrade the @product@ modules, if you didn't upgrade them automatically with the core.</div>
			</li>
			<ol>
				<li icon="" requirement="required">
					<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/06-upgrading-the-liferay-database/05-upgrading-modules-using-gogo-shell.markdown#executing-module-upgrades">Upgrade modules that are ready for upgrade</a></div>
					<div class="description">Upgrade modules that are ready for upgrade.</div>
				</li>
				<li icon="" requirement="required">
					<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/06-upgrading-the-liferay-database/05-upgrading-modules-using-gogo-shell.markdown#executing-verify-processes">Verify upgraded modules</a></div>
					<div class="description">Verify upgraded modules.</div>
				</li>
				<li icon="" requirement="required">
					<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/06-upgrading-the-liferay-database/05-upgrading-modules-using-gogo-shell.markdown#checking-upgrade-status">Resolve any module upgrade issues</a></div>
					<div class="description">Resolve any module upgrade issues.</div>
				</li>
				<li icon="" requirement="required">
					<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/06-upgrading-the-liferay-database/05-upgrading-modules-using-gogo-shell.markdown#executing-module-upgrades">Upgrade your resolved module issues</div>
					<div class="description">Upgrade your resolved module issues; continue if there were no issues.</div>
				</li>
			</ol>
			<li icon="" requirement="required">
				<div class="title">Checkpoint: Completed upgrading the @product@ data</div>
				<div class="description">Checkpoint: Completed upgrading the @product@ data.</div>
			</li>
		</ol>
		<li icon="" requirement="required">
			<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/07-executing-post-upgrade-tasks.markdown#executing-post-upgrade-tasks">Execute post-upgrade Tasks</a></div>
			<div class="description">Execute post-upgrade Tasks.</div>
		</li>
		<ol>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/07-executing-post-upgrade-tasks.markdown#tuning-your-database-for-production">Remove the database tuning</a></div>
				<div class="description">Remove the database tuning made for the upgrade process.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/07-executing-post-upgrade-tasks.markdown#re-enabling-search-indexing-and-reindexing-search-indexes">Re-enable and re-index the search indexes</a></div>
				<div class="description">Re-enable and re-index the search indexes.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/07-executing-post-upgrade-tasks.markdown#enabling-web-content-view-permissions">Update web content permissions</a></div>
				<div class="description">Update web content permissions. (@product@ 7.0 and lower)</div>
			</li>
			<li icon="" requirement="required">
				<div class="title"><a href="https://github.com/jhinkey/liferay-docs/blob/72-upgrading-liferay/deployment/articles/05-upgrading-to-liferay-7-2/02-planning-for-deprecated-apps.markdown">Address deprecated apps</a></div>
				<div class="description">Address deprecated apps.</div>
			</li>
			<li icon="" requirement="required">
				<div class="title">Checkpoint: Completed the post-upgrade tasks</div>
				<div class="description">Checkpoint: Completed the post-upgrade tasks.</div>
			</li>
		</ol>
	</ol>
</ol>