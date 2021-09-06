package com.liferay.ide.upgrade.problems.test.apichanges74;

import java.io.File;

import com.liferay.ide.upgrade.problems.test.apichanges.APITestBase;

public class RemoveXXssProtectionPropertyTest extends APITestBase {

	@Override
	public String getComponentName() {
		return "com.liferay.ide.upgrade.problems.core.internal.liferay74.RemoveXXssProtectionProperty";
	}

	@Override
	public File getTestFile() {
		return new File("projects/copyPortalSettings/portal-ext.properties");
	}

}
