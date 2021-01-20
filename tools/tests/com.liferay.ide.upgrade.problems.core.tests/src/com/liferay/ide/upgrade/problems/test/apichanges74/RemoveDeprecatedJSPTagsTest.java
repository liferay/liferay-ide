package com.liferay.ide.upgrade.problems.test.apichanges74;

import java.io.File;

import com.liferay.ide.upgrade.problems.test.apichanges.APITestBase;

public class RemoveDeprecatedJSPTagsTest extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 12;
	}

	@Override
	public String getComponentName() {
		return "com.liferay.ide.upgrade.problems.core.internal.liferay74.RemoveDeprecatedJSPTags";
	}

	@Override
	public File getTestFile() {
		return new File("jsptests/deprecatedTags/deprecatedTags.jsp");
	}

}
