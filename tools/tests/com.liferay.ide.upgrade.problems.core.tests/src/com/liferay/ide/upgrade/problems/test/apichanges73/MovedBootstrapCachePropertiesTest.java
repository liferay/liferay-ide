package com.liferay.ide.upgrade.problems.test.apichanges73;

import java.io.File;

import com.liferay.ide.upgrade.problems.test.apichanges.APITestBase;

/**
 * @author Ethan Sun
 */
public class MovedBootstrapCachePropertiesTest extends APITestBase {
	
	@Override
	public int getExpectedNumber() {
		return 3;
	}

	@Override
	public String getComponentName() {
		return "com.liferay.ide.upgrade.problems.core.internal.liferay73.MovedBootstrapCacheProperties";
	}

	@Override
	public File getTestFile() {
		return new File("projects/copyPortalSettings/portal.properties");
	}

}
