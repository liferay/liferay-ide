package com.liferay.ide.ui.workspace.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;
import com.liferay.ide.ui.liferay.base.TimestampSupport;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;


public class NewLiferayWorkspaceWithInvalidBundleUrl extends SwtbotBase
{
	
    @Test
    public void createLiferayWorkspaceWithDownloadBundleInvalidBundleUrl() {
        String bundleUrl =
            "https://issues.liferay.com/browse/IDE-3605";
        String serverName = "Liferay 7-invalid-bundle-url";
        String workspaceName = timestamp.getName("test-liferay-workspace-gradle");

        wizardAction.openNewLiferayWorkspaceWizard();

        wizardAction.newLiferayWorkspace.prepareGradle(workspaceName);

        wizardAction.newLiferayWorkspace.selectDownloadLiferayBundle();

        wizardAction.newLiferayWorkspace.setServerName(serverName);

        wizardAction.newLiferayWorkspace.setBundleUrl(bundleUrl);

        wizardAction.finish();

        viewAction.project.closeAndDelete(workspaceName);
        
        Assert.assertFalse(viewAction.servers.visibleModuleTry(serverName,workspaceName));
        
        dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm("Liferay 7-invalid-bundle-url");

        dialogAction.preferences.confirm();
    }

    @Rule
    public TimestampSupport timestamp = new TimestampSupport(bot);

}
