package com.liferay.ide.upgrade.problems.test.apichanges73;

import java.io.File;

import com.liferay.ide.upgrade.problems.test.apichanges.APITestBase;

public class RemovedLayoutParallelRenderPropertyTest extends APITestBase {
	
	@Override
	public int getExpectedNumber() {
		return 5;
	}

	@Override
	public String getComponentName() {
		return "com.liferay.ide.upgrade.problems.core.internal.liferay73.RemovedLayoutParallelRenderProperty";
	}

	@Override
	public File getTestFile() {
		return new File("projects/copyPortalSettings/portal.properties");
	}

}
