package com.liferay.ide.upgrade.problems.test.apichanges;

import java.io.File;

public class Liferay70VersionsPropertiesAutoCorrectTest extends AutoCorrectLiferayVersionPropertiesTestBase {

	@Override
	public String getImplClassName() {
		return "Liferay70VersionsProperties";
	}

	@Override
	public File getOriginalTestFile() {
		return new File("jsptests/jukebox-portlet/docroot/WEB-INF/liferay-plugin-package.properties");
	}

	@Override
	public String getVersion() {
		return "7.0";
	}

}
