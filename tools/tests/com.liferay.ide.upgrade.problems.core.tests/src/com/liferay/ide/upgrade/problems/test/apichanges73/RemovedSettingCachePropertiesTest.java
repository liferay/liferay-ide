package com.liferay.ide.upgrade.problems.test.apichanges73;

import java.io.File;

import com.liferay.ide.upgrade.problems.test.apichanges.APITestBase;

public class RemovedSettingCachePropertiesTest extends APITestBase {
	
	@Override
	public int getExpectedNumber() {
		return 6;
	}

	@Override
	public String getComponentName() {
		return "com.liferay.ide.upgrade.problems.core.internal.liferay73.RemovedSettingCacheProperties";
	}

	@Override
	public File getTestFile() {
		return new File("projects/copyPortalSettings/portal.properties");
	}

}
