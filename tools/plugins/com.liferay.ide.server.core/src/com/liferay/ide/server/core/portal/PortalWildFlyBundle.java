/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.server.core.portal;

import com.liferay.ide.server.util.JavaUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.launching.IVMInstall;

import org.osgi.framework.Version;

/**
 * @author Simon Jiang
 */
public class PortalWildFlyBundle extends PortalJBossBundle {

	public PortalWildFlyBundle(IPath path) {
		super(path);
	}

	public PortalWildFlyBundle(Map<String, String> appServerProperties) {
		super(appServerProperties);
	}

	@Override
	public String getDisplayName() {
		return "JBoss WildFly";
	}

	@Override
	public String[] getRuntimeStartVMArgs(IVMInstall vmInstall) {
		List<String> args = new ArrayList<>();

		args.add("-Dorg.jboss.resolver.warning=true");
		args.add("-Djava.net.preferIPv4Stack=true");
		args.add("-Dsun.rmi.dgc.client.gcInterval=3600000");
		args.add("-Dsun.rmi.dgc.server.gcInterval=3600000");
		args.add("-Djboss.modules.system.pkgs=org.jboss.byteman,org.jboss.logmanager");
		args.add("-Djava.awt.headless=true");
		args.add("-Dfile.encoding=UTF8");

		args.add("-server");
		args.add("-Djava.util.logging.manager=org.jboss.logmanager.LogManager");

		Version jdkVersion = Version.parseVersion(JavaUtil.getJDKVersion(vmInstall));
		Version jdk8Version = Version.parseVersion("1.8");

		if (jdkVersion.compareTo(jdk8Version) <= 0) {
			File jbossLogmanagerJarFile = getJbossLib(
				bundlePath, "/modules/system/layers/base/org/jboss/logmanager/main/");

			if (Objects.nonNull(jbossLogmanagerJarFile)) {
				args.add("-Xbootclasspath/p:\"" + jbossLogmanagerJarFile.getAbsolutePath() + "\"");
			}

			File jbosslog4jJarFile = getJbossLib(
				bundlePath, "/modules/system/layers/base/org/jboss/log4j/logmanager/main/");

			if (Objects.nonNull(jbosslog4jJarFile)) {
				args.add("-Xbootclasspath/p:\"" + jbosslog4jJarFile.getAbsolutePath() + "\"");
			}

			File jbossCommonJarFile = getJbossLib(bundlePath, "/modules/system/layers/base/org/wildfly/common/main/");

			if (Objects.nonNull(jbossCommonJarFile)) {
				args.add("-Xbootclasspath/p:\"" + jbossCommonJarFile.getAbsolutePath() + "\"");
			}
		}
		else {
			File jbossCommonJarFile = getJbossLib(bundlePath, "/modules/system/layers/base/org/wildfly/common/main/");

			if (Objects.nonNull(jbossCommonJarFile)) {
				args.add("-Xbootclasspath/a:\"" + jbossCommonJarFile.getAbsolutePath() + "\"");
			}
		}

		args.add("-Dorg.jboss.boot.log.file=\"" + bundlePath.append("/standalone/log/boot.log") + "\"");
		args.add("-Dlogging.configuration=file:\"" + bundlePath + "/standalone/configuration/logging.properties\"");
		args.add("-Djboss.home.dir=\"" + bundlePath + "\"");
		args.add("-Djboss.bind.address.management=localhost");
		args.add("-Duser.timezone=GMT");
		args.add("-Dorg.jboss.logmanager.nocolor=true");

		return args.toArray(new String[0]);
	}

	@Override
	public String[] getRuntimeStopProgArgs() {
		List<String> args = new ArrayList<>();

		args.add("-mp \"" + this.bundlePath.toPortableString() + "/modules\"");
		args.add("org.jboss.as.cli");
		args.add("--controller=localhost");
		args.add("--connect");
		args.add("--command=:shutdown");

		return args.toArray(new String[0]);
	}

	@Override
	public String getType() {
		return "wildfly";
	}

}