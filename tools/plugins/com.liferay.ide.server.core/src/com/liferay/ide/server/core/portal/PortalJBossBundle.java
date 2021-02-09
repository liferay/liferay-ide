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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileListing;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.JavaUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.launching.IVMInstall;

import org.osgi.framework.Version;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Simon Jiang
 */
public class PortalJBossBundle extends AbstractPortalBundle {

	public static final int DEFAULT_JMX_PORT = 2099;

	public PortalJBossBundle(IPath path) {
		super(path);
	}

	public PortalJBossBundle(Map<String, String> appServerProperties) {
		super(appServerProperties);
	}

	@Override
	public IPath getAppServerDeployDir() {
		return bundlePath.append("/standalone/deployments/");
	}

	@Override
	public IPath getAppServerLibGlobalDir() {
		return getAppServerDir().append("/modules/com/liferay/portal/main");
	}

	@Override
	public IPath getAppServerPortalDir() {
		IPath retval = null;

		if (bundlePath != null) {
			retval = bundlePath.append("/standalone/deployments/ROOT.war");
		}

		return retval;
	}

	@Override
	public String getDisplayName() {
		return "JBoss AS";
	}

	@Override
	public String getHttpPort() {
		String retVal = "8080";

		File standaloneXmlFile = new File(
			getAppServerDir().toPortableString(), "standalone/configuration/standalone.xml");

		String portValue = getHttpPortValue(standaloneXmlFile, "socket-binding", "name", "http", "port");

		if (!CoreUtil.empty(portValue)) {
			if (portValue.lastIndexOf(":") == -1) {
				retVal = portValue;
			}
			else {
				retVal = portValue.substring(portValue.lastIndexOf(":") + 1, portValue.length() - 1);
			}
		}

		return retVal;
	}

	@Override
	public String getMainClass() {
		return "org.jboss.modules.Main";
	}

	@Override
	public IPath[] getRuntimeClasspath() {
		List<IPath> paths = new ArrayList<>();

		if (FileUtil.exists(bundlePath)) {
			paths.add(bundlePath.append("jboss-modules.jar"));

			IPath loggManagerPath = bundlePath.append("modules/system/layers/base/org/jboss/logmanager/main");

			File loggManagerFile = loggManagerPath.toFile();

			if (FileUtil.exists(loggManagerFile)) {
				File[] libFiles = loggManagerFile.listFiles(
					new FileFilter() {

						@Override
						public boolean accept(File libFile) {
							String libFileName = libFile.getName();

							if (libFile.isFile() && libFileName.endsWith(".jar")) {
								return true;
							}

							return false;
						}

					});

				if (libFiles != null) {
					for (File libFile : libFiles) {
						paths.add(loggManagerPath.append(libFile.getName()));
					}
				}
			}
		}

		return paths.toArray(new IPath[0]);
	}

	@Override
	public String[] getRuntimeStartProgArgs() {
		List<String> args = new ArrayList<>();

		args.add("-mp \"" + bundlePath.toPortableString() + "/modules\"");
		args.add("-jaxpmodule");
		args.add("javax.xml.jaxp-provider");
		args.add("org.jboss.as.standalone");
		args.add("-b");
		args.add("localhost");
		args.add("--server-config=standalone.xml");
		args.add("-Djboss.server.base.dir=\"" + bundlePath.toPortableString() + "/standalone/\"");

		return args.toArray(new String[0]);
	}

	@Override
	public String[] getRuntimeStartVMArgs(IVMInstall vmInstall) {
		List<String> args = new ArrayList<>();

		args.add("-Dcom.sun.management.jmxremote");
		args.add("-Dcom.sun.management.jmxremote.authenticate=false");
		args.add("-Dcom.sun.management.jmxremote.port=" + getJmxRemotePort());
		args.add("-Dcom.sun.management.jmxremote.ssl=false");
		args.add("-Dorg.jboss.resolver.warning=true");
		args.add("-Djava.net.preferIPv4Stack=true");
		args.add("-Dsun.rmi.dgc.client.gcInterval=3600000");
		args.add("-Dsun.rmi.dgc.server.gcInterval=3600000");
		args.add("-Djboss.modules.system.pkgs=org.jboss.byteman");
		args.add("-Djava.awt.headless=true");
		args.add("-Dfile.encoding=UTF8");
		args.add("-server");
		args.add("-Djava.util.logging.manager=org.jboss.logmanager.LogManager");

		Version jdkVersion = Version.parseVersion(JavaUtil.getJDKVersion(vmInstall));
		Version jdk8Version = Version.parseVersion("1.8");

		if (jdkVersion.compareTo(jdk8Version) <= 0) {
			File jbossLogmanagerJarFile = getJbossLib(bundlePath, "/modules/org/jboss/logmanager/main/");

			if (Objects.nonNull(jbossLogmanagerJarFile)) {
				args.add("-Xbootclasspath/p:\"" + jbossLogmanagerJarFile.getAbsolutePath() + "\"");
			}

			File jbossLogmanagerLog4jJarFile = getJbossLib(bundlePath, "/modules/org/jboss/logmanager/log4j/main/");

			if (Objects.nonNull(jbossLogmanagerLog4jJarFile)) {
				args.add("-Xbootclasspath/p:\"" + jbossLogmanagerLog4jJarFile.getAbsolutePath() + "\"");
			}

			File jbosslog4jJarFile = getJbossLib(bundlePath, "/modules/org/apache/log4j/main/");

			if (Objects.nonNull(jbosslog4jJarFile)) {
				args.add("-Xbootclasspath/p:\"" + jbosslog4jJarFile.getAbsolutePath() + "\"");
			}
		}

		args.add("-Djboss.modules.system.pkgs=org.jboss.logmanager");
		args.add("-Dorg.jboss.boot.log.file=\"" + bundlePath.append("/standalone/log/boot.log") + "\"");
		args.add("-Dlogging.configuration=file:\"" + bundlePath + "/standalone/configuration/logging.properties\"");
		args.add("-Djboss.home.dir=\"" + bundlePath + "\"");
		args.add("-Djboss.bind.address.management=localhost");
		args.add("-Duser.timezone=GMT");

		return args.toArray(new String[0]);
	}

	@Override
	public String[] getRuntimeStopProgArgs() {
		List<String> args = new ArrayList<>();

		args.add("-mp \"" + bundlePath.toPortableString() + "/modules\"");
		args.add("org.jboss.as.cli");
		args.add("--controller=localhost");
		args.add("--connect");
		args.add("--command=:shutdown");

		return args.toArray(new String[0]);
	}

	@Override
	public String[] getRuntimeStopVMArgs(IVMInstall vmInstall) {
		List<String> args = new ArrayList<>();

		args.add("-Djboss.home.dir=\"" + bundlePath + "\"");

		return args.toArray(new String[0]);
	}

	@Override
	public String getType() {
		return "jboss";
	}

	@Override
	public IPath[] getUserLibs() {
		List<IPath> libs = new ArrayList<>();

		try {
			IPath serverPortalDir = getAppServerPortalDir();

			IPath portalLibPath = serverPortalDir.append("WEB-INF/lib");

			List<File> portallibFiles = FileListing.getFileListing(new File(portalLibPath.toPortableString()));

			for (File lib : portallibFiles) {
				if (FileUtil.exists(lib)) {
					String libName = lib.getName();

					if (libName.endsWith(".jar")) {
						libs.add(new Path(lib.getPath()));
					}
				}
			}

			IPath appServerLibDir = getAppServerLibDir();

			List<File> libFiles = FileListing.getFileListing(new File(appServerLibDir.toPortableString()));

			for (File lib : libFiles) {
				if (FileUtil.exists(lib)) {
					String libName = lib.getName();

					if (libName.endsWith(".jar")) {
						libs.add(new Path(lib.getPath()));
					}
				}
			}
		}
		catch (FileNotFoundException fnfe) {
		}

		return libs.toArray(new IPath[0]);
	}

	@Override
	public void setHttpPort(String port) {
		File standaloneXmlFile = new File(
			getAppServerDir().toPortableString(), "standalone/configuration/standalone.xml");

		_setHttpPortValue(standaloneXmlFile, "socket-binding", "name", "http", "port", port);
	}

	@Override
	protected IPath getAppServerLibDir() {
		return getAppServerDir().append("modules");
	}

	@Override
	protected int getDefaultJMXRemotePort() {
		return DEFAULT_JMX_PORT;
	}

	protected File getJbossLib(IPath bundlePath, String libPathValue) {
		IPath libIPath = bundlePath.append(libPathValue);

		Collection<File> libJars = FileUtils.listFiles(libIPath.toFile(), new String[] {"jar"}, true);

		File[] jarArray = libJars.toArray(new File[0]);

		if (ListUtil.isNotEmpty(jarArray)) {
			return jarArray[0];
		}

		return null;
	}

	private void _setHttpPortValue(
		File xmlFile, String tagName, String attriName, String attriValue, String targetName, String value) {

		DocumentBuilder db = null;

		DocumentBuilderFactory dbf = null;

		try {
			dbf = DocumentBuilderFactory.newInstance();

			db = dbf.newDocumentBuilder();

			Document document = db.parse(xmlFile);

			NodeList connectorNodes = document.getElementsByTagName(tagName);

			for (int i = 0; i < connectorNodes.getLength(); i++) {
				Node node = connectorNodes.item(i);

				NamedNodeMap attributes = node.getAttributes();

				Node protocolNode = attributes.getNamedItem(attriName);

				if (protocolNode != null) {
					String nodeValue = protocolNode.getNodeValue();

					if (nodeValue.equals(attriValue)) {
						Node portNode = attributes.getNamedItem(targetName);

						portNode.setNodeValue(value);

						break;
					}
				}
			}

			TransformerFactory factory = TransformerFactory.newInstance();

			Transformer transformer = factory.newTransformer();

			DOMSource domSource = new DOMSource(document);

			StreamResult result = new StreamResult(xmlFile);

			transformer.transform(domSource, result);
		}
		catch (Exception e) {
			LiferayServerCore.logError(e);
		}
	}

}