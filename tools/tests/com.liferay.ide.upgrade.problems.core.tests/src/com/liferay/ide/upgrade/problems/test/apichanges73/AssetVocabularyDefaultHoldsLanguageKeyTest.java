package com.liferay.ide.upgrade.problems.test.apichanges73;

import com.liferay.ide.upgrade.problems.test.apichanges.APITestBase;

import java.io.File;

public class AssetVocabularyDefaultHoldsLanguageKeyTest extends APITestBase {

	@Override
	public String getComponentName() {
		return "com.liferay.ide.upgrade.problems.core.internal.liferay73.AssetVocabularyDefaultHoldsLanguageKey";
	}

	@Override
	public File getTestFile() {
		return new File("projects/copyPortalSettings/portal.properties");
	}

} 
