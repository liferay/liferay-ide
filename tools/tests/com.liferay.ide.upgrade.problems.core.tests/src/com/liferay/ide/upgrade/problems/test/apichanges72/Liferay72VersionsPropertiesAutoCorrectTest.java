package com.liferay.ide.upgrade.problems.test.apichanges72;

import java.io.File;

import com.liferay.ide.upgrade.problems.test.apichanges.AutoCorrectLiferayVersionPropertiesTestBase;

public class Liferay72VersionsPropertiesAutoCorrectTest extends AutoCorrectLiferayVersionPropertiesTestBase {

	@Override
	public String getImplClassName() {
		return "Liferay72VersionsProperties";
	}

	@Override
	public File getOriginalTestFile() {
		return new File("jsptests/jukebox-portlet/docroot/WEB-INF/liferay-plugin-package.properties");
	}

	@Override
	public String getVersion() {
		return "7.2";
	}

}
