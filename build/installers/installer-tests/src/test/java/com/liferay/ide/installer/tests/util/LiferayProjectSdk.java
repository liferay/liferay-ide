package com.liferay.ide.installer.tests.util;

public class LiferayProjectSdk extends Installer {

	public LiferayProjectSdk(String type) {
		super(type);
	}

	@Override
	public String command() {
		String command = "";

		if (isWindow()) {
			command = InstallerUtil.getProjectSdkFullNameWin();
		}
		else if (isLinux()) {
			command = InstallerUtil.getProjectSdkFullNameLinux();
		}

		return command;
	}

}
