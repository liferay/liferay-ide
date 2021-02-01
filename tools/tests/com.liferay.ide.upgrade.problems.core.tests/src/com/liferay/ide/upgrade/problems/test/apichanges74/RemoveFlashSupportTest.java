package com.liferay.ide.upgrade.problems.test.apichanges74;

import java.io.File;

import com.liferay.ide.upgrade.problems.test.apichanges.APITestBase;

public class RemoveFlashSupportTest extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 1;
	}

	@Override
	public String getComponentName() {
		return "com.liferay.ide.upgrade.problems.core.internal.liferay74.RemovePortalFlashSupport";
	}

	@Override
	public File getTestFile() {
		return new File("javatests/ServicePreAction.java");
	}

}
