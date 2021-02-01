package com.liferay.ide.upgrade.problems.test.apichanges74;

import java.io.File;

import com.liferay.ide.upgrade.problems.test.apichanges.APITestBase;

public class RefacrtorClamdIntegrationAntivirusScannerTest extends APITestBase {

	@Override
	public String getComponentName() {
		return "com.liferay.ide.upgrade.problems.core.internal.liferay74.RefacrtorClamdIntegrationAntivirusScanner";
	}

	@Override
	public File getTestFile() {
		return new File("projects/copyPortalSettings/portal.properties");
	}

}
