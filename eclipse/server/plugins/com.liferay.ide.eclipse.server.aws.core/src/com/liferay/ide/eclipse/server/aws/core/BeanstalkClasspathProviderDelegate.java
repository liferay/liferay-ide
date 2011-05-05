package com.liferay.ide.eclipse.server.aws.core;

import com.liferay.ide.eclipse.server.aws.core.util.BeanstalkUtil;
import com.liferay.ide.eclipse.server.tomcat.core.LiferayTomcatRuntimeClasspathProvider;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.wst.server.core.IRuntime;


public class BeanstalkClasspathProviderDelegate extends LiferayTomcatRuntimeClasspathProvider {

	public BeanstalkClasspathProviderDelegate() {
		super();
	}

	@Override
	public IClasspathEntry[] resolveClasspathContainer(IProject project, IRuntime runtime) {
		IBeanstalkRuntime beanstalkRuntime = BeanstalkUtil.getBeanstalkRuntime(runtime);

		IPath location = beanstalkRuntime.getRuntimeStubLocation();
		String runtimeId = beanstalkRuntime.getRuntimeStubTypeId();

		return resolveClasspathContainerForPath(location, runtimeId);
	}

}
