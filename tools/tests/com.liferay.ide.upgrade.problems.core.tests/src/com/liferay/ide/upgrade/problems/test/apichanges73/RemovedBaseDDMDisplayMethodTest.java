package com.liferay.ide.upgrade.problems.test.apichanges73;

import java.io.File;

import com.liferay.ide.upgrade.problems.test.apichanges.APITestBase;

/**
 * @author Ethan Sun
 */
public class RemovedBaseDDMDisplayMethodTest extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 17;
	}
	
	@Override
	public String getComponentName() {
		return "com.liferay.ide.upgrade.problems.core.internal.liferay73.RemovedBaseDDMDisplayMethod";
	}

	@Override
	public File getTestFile() {
		return new File("projects/classnameid-related-test/BaseDDMDisplayMethodTest.java");
	}
	
}
