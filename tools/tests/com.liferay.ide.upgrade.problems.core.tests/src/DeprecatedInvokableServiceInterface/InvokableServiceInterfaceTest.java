package DeprecatedInvokableServiceInterface;

import java.io.File;

import com.liferay.ide.upgrade.problems.test.apichanges.APITestBase;

public class InvokableServiceInterfaceTest extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 3;
	}

	@Override
	public String getComponentName() {
		return "com.liferay.ide.upgrade.problems.core.internal.liferay72.DeprecatedInvokableServiceInterface";
	}

	@Override
	public File getTestFile() {
		return new File("projects/filetests/InvokableServiceInterfaceImplementeTest.java");
	}

}
