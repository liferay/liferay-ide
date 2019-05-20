package com.liferay.ide.upgrade.problems.test.apichanges72;

import java.io.File;

import com.liferay.ide.upgrade.problems.test.apichanges.APITestBase;

public class RemovedSupportForJSPTemplatesTest extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 3;
	}

	@Override
	public String getComponentName() {
		return "com.liferay.ide.upgrade.problems.core.internal.liferay72.RemovedSupportForJSPTemplates";
	}

	@Override
	public File getTestFile() {
		return new File("projects/test-ext/docroot/WEB-INF/lib/liferay-look-and-feel.xml");
	}

}
