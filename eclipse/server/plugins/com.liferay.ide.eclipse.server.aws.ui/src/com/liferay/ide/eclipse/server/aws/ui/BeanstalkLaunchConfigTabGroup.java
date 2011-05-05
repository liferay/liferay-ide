package com.liferay.ide.eclipse.server.aws.ui;

import com.liferay.ide.eclipse.server.tomcat.ui.LiferayTomcatLaunchConfigTabGroup;


public class BeanstalkLaunchConfigTabGroup extends LiferayTomcatLaunchConfigTabGroup {

	public BeanstalkLaunchConfigTabGroup() {
		super();
	}

	@Override
	protected String getServerTypeId() {
		return "com.liferay.ide.eclipse.server.aws";
	}


}
