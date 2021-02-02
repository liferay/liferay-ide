package com.liferay.ide.upgrade.problems.test.apichanges74;

import java.io.File;

import com.liferay.ide.upgrade.problems.test.apichanges.APITestBase;

public class RemovedSoyPortletTest extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 26;
	}

	@Override
	public String getComponentName() {
		return "com.liferay.ide.upgrade.problems.core.internal.liferay74.RemovedSoyPortlet";
	}

	@Override
	public File getTestFile() {
		return new File("javatests/RemovedSoyPortletTest.java");
	}

}
