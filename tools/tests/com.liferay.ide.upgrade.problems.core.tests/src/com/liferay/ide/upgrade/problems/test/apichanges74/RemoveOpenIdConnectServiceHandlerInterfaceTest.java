package com.liferay.ide.upgrade.problems.test.apichanges74;

import java.io.File;

import com.liferay.ide.upgrade.problems.test.apichanges.APITestBase;

public class RemoveOpenIdConnectServiceHandlerInterfaceTest extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 4;
	}

	@Override
	public String getComponentName() {
		return "com.liferay.ide.upgrade.problems.core.internal.liferay74.RemoveOpenIdConnectServiceHandlerInterface";
	}

	@Override
	public File getTestFile() {
		return new File("javatests/OpenIdConnectFilter.java");
	}

}