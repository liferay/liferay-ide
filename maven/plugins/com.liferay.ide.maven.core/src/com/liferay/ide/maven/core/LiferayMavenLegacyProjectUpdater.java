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

package com.liferay.ide.maven.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.project.core.upgrade.ILiferayLegacyProjectUpdater;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import java.nio.file.Files;

import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatProcessor;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author Andy Wu
 */
@SuppressWarnings("restriction")
public class LiferayMavenLegacyProjectUpdater implements ILiferayLegacyProjectUpdater {

	@Override
	public boolean isNeedUpgrade(IFile pomFile) {
		String tagName = "artifactId";
		String[] values =
			{"liferay-maven-plugin", "portal-service", "util-java", "util-bridges", "util-taglib", "util-slf4j"};

		IDOMModel domModel = null;

		boolean retval = false;

		try {
			IModelManager modelManager = StructuredModelManager.getModelManager();

			domModel = (IDOMModel)modelManager.getModelForRead(pomFile);

			IDOMDocument document = domModel.getDocument();

			NodeList elements = document.getElementsByTagName(tagName);

			if (elements != null) {
				for (int i = 0; i < elements.getLength(); i++) {
					IDOMElement element = (IDOMElement)elements.item(i);

					String textContent = element.getTextContent();

					if (!CoreUtil.empty(textContent)) {
						textContent = textContent.trim();

						for (String str : values) {
							if (textContent.equals(str)) {
								retval = true;
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
		}
		finally {
			if (domModel != null) {
				domModel.releaseFromRead();
			}
		}

		return retval;
	}

	@Override
	public boolean isNeedUpgrade(IProject project) {
		IFile pomFile = project.getFile("pom.xml");

		return isNeedUpgrade(pomFile);
	}

	@Override
	public void upgradePomFile(IProject project, File outputFile) {
		IFile pomFile = project.getFile("pom.xml");
		IFile tempPomFile = project.getFile(".pom-tmp.xml");

		boolean needUpgrade = isNeedUpgrade(pomFile);

		if ((outputFile == null) && !needUpgrade) {
			return;
		}

		IDOMModel domModel = null;

		try {
			if (outputFile != null) {
				pomFile.copy(tempPomFile.getFullPath(), true, null);

				pomFile = tempPomFile;
			}

			IModelManager modelManager = StructuredModelManager.getModelManager();

			domModel = (IDOMModel)modelManager.getModelForRead(pomFile);

			if (needUpgrade) {
				IDOMDocument document = domModel.getDocument();

				domModel.aboutToChangeModel();

				NodeList elements = document.getElementsByTagName("artifactId");

				if (elements != null) {
					for (int i = 0; i < elements.getLength(); i++) {
						IDOMElement element = (IDOMElement)elements.item(i);

						String textContent = element.getTextContent();

						if (!CoreUtil.empty(textContent)) {
							textContent = textContent.trim();

							// remove liferay-maven-plugin

							if (textContent.equals("liferay-maven-plugin")) {
								IDOMElement liferayMavenPluginNode = (IDOMElement)element.getParentNode();

								_removeChildren(liferayMavenPluginNode);

								IDOMElement pluginsNode = (IDOMElement)liferayMavenPluginNode.getParentNode();

								pluginsNode.removeChild(liferayMavenPluginNode);
							}

								// fix dependencies

							else if (textContent.equals("portal-service") || textContent.equals("util-java") ||
									 textContent.equals("util-bridges") || textContent.equals("util-taglib") ||
									 textContent.equals("util-slf4j")) {

								IDOMElement dependencyElement = (IDOMElement)element.getParentNode();

								String[] fixArtifactIdandVersion = _getFixedArtifactIdAndVersion(textContent);

								_removeChildren(element);

								Document ownerDocument = element.getOwnerDocument();

								Text artifactIdTextContent = ownerDocument.createTextNode(fixArtifactIdandVersion[0]);

								element.appendChild(artifactIdTextContent);

								NodeList versionList = dependencyElement.getElementsByTagName("version");

								if ((versionList != null) && (versionList.getLength() == 1)) {
									IDOMElement versionElement = (IDOMElement)versionList.item(0);

									_removeChildren(versionElement);

									Text versionTextContent = ownerDocument.createTextNode(fixArtifactIdandVersion[1]);

									versionElement.appendChild(versionTextContent);
								}
							}
						}
					}
				}

				if (_isProtletProject(project)) {
					_addCssBuilderPlugin(_getPluginsNode(document));
				}

				if (_isServiceBuilderProject(project)) {
					_addServiceBuilderPlugin(_getPluginsNode(document), project.getName());
					_addDependency(
						_getDependenciesNode(document), "biz.aQute.bnd", "biz.aQute.bnd.annotation", "3.2.0",
						"provided");
				}

				if (_isServiceBuildersubProject(project)) {
					_addDependency(
						_getDependenciesNode(document), "biz.aQute.bnd", "biz.aQute.bnd.annotation", "3.2.0",
						"provided");
				}

				if (_isThemeProject(project)) {
					_addProperties(_getPropertiesNode(document));
					_addMavenThemePlugins(_getPluginsNode(document));
				}

				_cleanBuildNode(document);

				domModel.changedModel();
			}

			if (FileUtil.exists(tempPomFile)) {
				tempPomFile.delete(true, null);
			}

			if (outputFile != null) {
				try (OutputStream fos = Files.newOutputStream(outputFile.toPath())) {
					domModel.save(fos);
				}
				catch (Exception e) {
				}
			}
			else {
				domModel.save();
			}
		}
		catch (Exception e) {
			LiferayMavenCore.logError("update pom file error", e);
		}
		finally {
			if (domModel != null) {
				domModel.releaseFromRead();
			}
		}
	}

	private void _addChildNode(Element pluginNode, Document document, String tagName, String value) {
		Element testNode = document.createElement(tagName);

		testNode.appendChild(document.createTextNode(value));

		pluginNode.appendChild(testNode);
	}

	private void _addCssBuilderPlugin(Node pluginsNode) {
		Document document = pluginsNode.getOwnerDocument();

		Element cssBuidlerPlugin = document.createElement("plugin");

		_addChildNode(cssBuidlerPlugin, document, "groupId", "com.liferay");
		_addChildNode(cssBuidlerPlugin, document, "artifactId", "com.liferay.css.builder");
		_addChildNode(cssBuidlerPlugin, document, "version", "1.0.21");

		Element executions = document.createElement("executions");
		Element execution = document.createElement("execution");

		executions.appendChild(execution);

		cssBuidlerPlugin.appendChild(executions);

		_addChildNode(execution, document, "id", "default-build-css");
		_addChildNode(execution, document, "phase", "generate-sources");

		Element goals = document.createElement("goals");

		execution.appendChild(goals);

		_addChildNode(goals, document, "goal", "build-css");

		Element configuration = document.createElement("configuration");

		cssBuidlerPlugin.appendChild(configuration);

		_addChildNode(configuration, document, "docrootDirName", "src/main/webapp");

		pluginsNode.appendChild(cssBuidlerPlugin);

		_formatNode(cssBuidlerPlugin);
	}

	private void _addCssBuilderThemePlugin(Node pluginsNode) {
		Document document = pluginsNode.getOwnerDocument();

		Element cssBuidlerPlugin = document.createElement("plugin");

		_addChildNode(cssBuidlerPlugin, document, "groupId", "com.liferay");
		_addChildNode(cssBuidlerPlugin, document, "artifactId", "com.liferay.css.builder");
		_addChildNode(cssBuidlerPlugin, document, "version", "1.0.23");

		Element executions = document.createElement("executions");
		Element execution = document.createElement("execution");

		executions.appendChild(execution);

		cssBuidlerPlugin.appendChild(executions);

		_addChildNode(execution, document, "id", "default-build-css");
		_addChildNode(execution, document, "phase", "compile");

		Element goals = document.createElement("goals");

		execution.appendChild(goals);

		_addChildNode(goals, document, "goal", "build-css");

		Element configuration = document.createElement("configuration");

		cssBuidlerPlugin.appendChild(configuration);
		_addChildNode(configuration, document, "docrootDirName", "${com.liferay.portal.tools.theme.builder.outputDir}");
		_addChildNode(configuration, document, "outputDirName", "/");
		_addChildNode(configuration, document, "portalCommonPath", "target/deps/com.liferay.frontend.css.common.jar");

		pluginsNode.appendChild(cssBuidlerPlugin);

		_formatNode(cssBuidlerPlugin);
	}

	private void _addDependency(
		Node dependenciesNode, String groupId, String artifactId, String version, String scope) {

		Document document = dependenciesNode.getOwnerDocument();

		NodeList dependencyList = document.getElementsByTagName("dependency");

		boolean exist = false;

		if ((dependencyList != null) && (dependencyList.getLength() > 0)) {
			for (int i = 0; i < dependencyList.getLength(); i++) {
				Element dependency = (Element)dependencyList.item(i);

				NodeList groupIdNodeList = dependency.getElementsByTagName("groupId");

				Node groupIdNode = groupIdNodeList.item(0);

				String tempGroupId = groupIdNode.getTextContent();

				NodeList artifactIdNodeList = dependency.getElementsByTagName("artifactId");

				Node artifactIdNode = artifactIdNodeList.item(0);

				String tempArtifactId = artifactIdNode.getTextContent();

				if (groupId.equals(tempGroupId) && artifactId.equals(tempArtifactId)) {
					exist = true;
				}
			}
		}

		if (!exist) {
			Element dependency = document.createElement("dependency");

			_addChildNode(dependency, document, "groupId", groupId);
			_addChildNode(dependency, document, "artifactId", artifactId);
			_addChildNode(dependency, document, "version", version);
			_addChildNode(dependency, document, "scope", scope);

			dependenciesNode.appendChild(dependency);

			_formatNode(dependenciesNode);
		}
	}

	private void _addMavenDependencyPlugin(Node pluginsNode) {
		Document document = pluginsNode.getOwnerDocument();

		Element mavenDependencyPlguin = document.createElement("plugin");

		_addChildNode(mavenDependencyPlguin, document, "artifactId", "maven-dependency-plugin");

		Element executions = document.createElement("executions");
		Element execution = document.createElement("execution");

		executions.appendChild(execution);

		mavenDependencyPlguin.appendChild(executions);

		_addChildNode(execution, document, "phase", "generate-sources");

		Element goals = document.createElement("goals");

		execution.appendChild(goals);

		_addChildNode(goals, document, "goal", "copy");

		Element configuration = document.createElement("configuration");
		Element artifactItems = document.createElement("artifactItems");

		Element commonArtifactItem = document.createElement("artifactItem");

		_addChildNode(commonArtifactItem, document, "groupId", "com.liferay");
		_addChildNode(commonArtifactItem, document, "artifactId", "com.liferay.frontend.css.common");
		_addChildNode(commonArtifactItem, document, "version", "2.0.1");

		Element styledArtifactItem = document.createElement("artifactItem");

		_addChildNode(styledArtifactItem, document, "groupId", "com.liferay");
		_addChildNode(styledArtifactItem, document, "artifactId", "com.liferay.frontend.theme.styled");
		_addChildNode(styledArtifactItem, document, "version", "2.0.13");

		Element unstyledArtifactItem = document.createElement("artifactItem");

		_addChildNode(unstyledArtifactItem, document, "groupId", "com.liferay");
		_addChildNode(unstyledArtifactItem, document, "artifactId", "com.liferay.frontend.theme.unstyled");
		_addChildNode(unstyledArtifactItem, document, "version", "2.0.13");

		artifactItems.appendChild(commonArtifactItem);
		artifactItems.appendChild(styledArtifactItem);
		artifactItems.appendChild(unstyledArtifactItem);

		configuration.appendChild(artifactItems);

		_addChildNode(configuration, document, "outputDirectory", "${project.build.directory}/deps");
		_addChildNode(configuration, document, "stripVersion", "true");

		mavenDependencyPlguin.appendChild(configuration);

		pluginsNode.appendChild(mavenDependencyPlguin);

		_formatNode(mavenDependencyPlguin);
	}

	private void _addMavenThemePlugins(Node pluginsNode) {
		_addMavenDependencyPlugin(pluginsNode);
		_addMavenWarPlugin(pluginsNode);
		_addCssBuilderThemePlugin(pluginsNode);
		_addThemeBuilderPlugin(pluginsNode);
	}

	private void _addMavenWarPlugin(Node pluginsNode) {
		Document document = pluginsNode.getOwnerDocument();

		Element mavenWarPlguin = document.createElement("plugin");

		_addChildNode(mavenWarPlguin, document, "artifactId", "maven-war-plugin");
		_addChildNode(mavenWarPlguin, document, "version", "3.0.0");

		Element configuration = document.createElement("configuration");

		_addChildNode(configuration, document, "packagingExcludes", "**/*.scss");

		Element webResources = document.createElement("webResources");
		Element resource = document.createElement("resource");

		_addChildNode(resource, document, "directory", "${com.liferay.portal.tools.theme.builder.outputDir}");

		Element excludes = document.createElement("excludes");

		_addChildNode(excludes, document, "exclude", "**/*.scss");

		resource.appendChild(excludes);

		webResources.appendChild(resource);

		configuration.appendChild(webResources);

		mavenWarPlguin.appendChild(configuration);

		pluginsNode.appendChild(mavenWarPlguin);

		_formatNode(mavenWarPlguin);
	}

	private void _addProperties(Node propertiesNode) {
		Document document = propertiesNode.getOwnerDocument();

		_addChildNode(
			(Element)propertiesNode, document, "com.liferay.portal.tools.theme.builder.outputDir",
			"target/build-theme");
		_addChildNode((Element)propertiesNode, document, "project.build.sourceEncoding", "UTF-8");

		_formatNode(propertiesNode);
	}

	private void _addServiceBuilderPlugin(Node pluginsNode, String projectName) {
		Document document = pluginsNode.getOwnerDocument();

		Element serviceBuidlerPlugin = document.createElement("plugin");

		_addChildNode(serviceBuidlerPlugin, document, "groupId", "com.liferay");
		_addChildNode(serviceBuidlerPlugin, document, "artifactId", "com.liferay.portal.tools.service.builder");
		_addChildNode(serviceBuidlerPlugin, document, "version", "1.0.148");

		Element configuration = document.createElement("configuration");

		_addChildNode(configuration, document, "apiDirName", "../" + projectName + "-service/src/main/java");
		_addChildNode(configuration, document, "autoNamespaceTables", "true");
		_addChildNode(configuration, document, "buildNumberIncrement", "true");
		_addChildNode(configuration, document, "hbmFileName", "src/main/resources/META-INF/portlet-hbm.xml");
		_addChildNode(configuration, document, "implDirName", "src/main/java");
		_addChildNode(configuration, document, "inputFileName", "src/main/webapp/WEB-INF/service.xml");
		_addChildNode(
			configuration, document, "modelHintsFileName", "src/main/resources/META-INF/portlet-model-hints.xml");
		_addChildNode(configuration, document, "osgiModule", "false");
		_addChildNode(configuration, document, "pluginName", projectName);
		_addChildNode(configuration, document, "propsUtil", "com.liferay.util.service.ServiceProps");
		_addChildNode(configuration, document, "resourcesDirName", "src/main/resources");
		_addChildNode(configuration, document, "springNamespaces", "beans");
		_addChildNode(configuration, document, "springFileName", "src/main/resources/META-INF/portlet-spring.xml");
		_addChildNode(configuration, document, "sqlDirName", "src/main/webapp/WEB-INF/sql");
		_addChildNode(configuration, document, "sqlFileName", "tables.sql");

		serviceBuidlerPlugin.appendChild(configuration);

		pluginsNode.appendChild(serviceBuidlerPlugin);

		_formatNode(serviceBuidlerPlugin);
	}

	private void _addThemeBuilderPlugin(Node pluginsNode) {
		Document document = pluginsNode.getOwnerDocument();

		Element themeBuidlerPlugin = document.createElement("plugin");

		_addChildNode(themeBuidlerPlugin, document, "groupId", "com.liferay");
		_addChildNode(themeBuidlerPlugin, document, "artifactId", "com.liferay.portal.tools.theme.builder");
		_addChildNode(themeBuidlerPlugin, document, "version", "1.0.1");

		Element executions = document.createElement("executions");
		Element execution = document.createElement("execution");

		executions.appendChild(execution);

		themeBuidlerPlugin.appendChild(executions);

		_addChildNode(execution, document, "phase", "generate-resources");

		Element goals = document.createElement("goals");

		execution.appendChild(goals);

		_addChildNode(goals, document, "goal", "build-theme");

		Element configuration = document.createElement("configuration");

		_addChildNode(configuration, document, "diffsDir", "src/main/webapp/");
		_addChildNode(configuration, document, "outputDir", "${com.liferay.portal.tools.theme.builder.outputDir}");
		_addChildNode(
			configuration, document, "parentDir",
			"${project.build.directory}/deps/com.liferay.frontend.theme.styled.jar");
		_addChildNode(configuration, document, "parentName", "_styled");
		_addChildNode(
			configuration, document, "unstyledDir",
			"${project.build.directory}/deps/com.liferay.frontend.theme.unstyled.jar");

		execution.appendChild(configuration);

		pluginsNode.appendChild(themeBuidlerPlugin);

		_formatNode(themeBuidlerPlugin);
	}

	private void _cleanBuildNode(Document document) {
		NodeList buildList = document.getElementsByTagName("build");

		if ((buildList == null) || (buildList.getLength() == 0)) {
			return;
		}
		else if (buildList.getLength() > 1) {

			// more than one build tag , ignore

		}
		else {
			Element buildNode = (Element)buildList.item(0);

			NodeList pluginsList = buildNode.getElementsByTagName("plugins");

			if ((pluginsList != null) && (pluginsList.getLength() == 1)) {
				Element pluginsNode = (Element)pluginsList.item(0);

				NodeList pluginList = pluginsNode.getElementsByTagName("plugin");

				if ((pluginList == null) || (pluginList.getLength() == 0)) {
					buildNode.removeChild(pluginsNode);
				}
			}

			NodeList buildChildrenList = buildNode.getChildNodes();

			if ((buildChildrenList != null) && (buildChildrenList.getLength() > 0)) {
				boolean deleteBuildNode = true;

				for (int i = 0; i < buildChildrenList.getLength(); i++) {
					Node node = buildChildrenList.item(i);

					if (node.getNodeType() != Node.TEXT_NODE) {
						deleteBuildNode = false;
					}
				}

				if (deleteBuildNode) {
					Node parentNode = buildNode.getParentNode();

					parentNode.removeChild(buildNode);
				}
			}
		}
	}

	private void _formatNode(Node node) {
		_formatProcessor.formatNode(node);
	}

	private Node _getDependenciesNode(IDOMDocument document) {
		NodeList dependenciesList = document.getElementsByTagName("dependencies");

		if ((dependenciesList != null) && (dependenciesList.getLength() == 1)) {
			return dependenciesList.item(0);
		}
		else {
			Element dependenciesNode = document.createElement("dependencies");

			NodeList projectNodeList = document.getElementsByTagName("project");

			Node node = projectNodeList.item(0);

			node.appendChild(dependenciesNode);

			return dependenciesNode;
		}
	}

	private String[] _getFixedArtifactIdAndVersion(String artifactId) {
		if (_dependenciesConvertMap == null) {
			_dependenciesConvertMap = new String[5][];

			_dependenciesConvertMap[0] = new String[] {"portal-service", "com.liferay.portal.kernel", "2.6.0"};
			_dependenciesConvertMap[1] = new String[] {"util-java", "com.liferay.util.java", "2.0.0"};
			_dependenciesConvertMap[2] = new String[] {"util-bridges", "com.liferay.util.bridges", "2.0.0"};
			_dependenciesConvertMap[3] = new String[] {"util-taglib", "com.liferay.util.taglib", "2.0.0"};
			_dependenciesConvertMap[4] = new String[] {"util-slf4j", "com.liferay.util.slf4j", "1.0.0"};
		}

		for (String[] str : _dependenciesConvertMap) {
			if (artifactId.equals(str[0])) {
				String[] result = new String[2];

				result[0] = str[1];
				result[1] = str[2];

				return result;
			}
		}

		return null;
	}

	private Node _getPluginsNode(IDOMDocument document) {
		NodeList buildList = document.getElementsByTagName("build");

		if ((buildList != null) && (buildList.getLength() == 1)) {
			IDOMElement buildNode = (IDOMElement)buildList.item(0);

			NodeList pluginsList = buildNode.getElementsByTagName("plugins");

			if ((pluginsList == null) || (pluginsList.getLength() != 1)) {
				Element pluginsNode = document.createElement("plugins");

				buildNode.appendChild(pluginsNode);

				return pluginsNode;
			}
			else {
				return pluginsList.item(0);
			}
		}
		else {
			Element buildNode = document.createElement("build");
			Element pluginsNode = document.createElement("plugins");

			buildNode.appendChild(pluginsNode);

			NodeList projectNodeList = document.getElementsByTagName("project");

			Node node = projectNodeList.item(0);

			node.appendChild(buildNode);

			return pluginsNode;
		}
	}

	private Node _getPropertiesNode(IDOMDocument document) {
		NodeList nodeList = document.getElementsByTagName("properties");

		if ((nodeList != null) && (nodeList.getLength() > 0)) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(0);

				if (node instanceof Element) {
					Element element = (Element)node;

					Node parentNode = element.getParentNode();

					String nodeName = parentNode.getNodeName();

					if (nodeName.equals("project")) {
						return node;
					}
				}
			}
		}

		Element propertiesListNode = document.createElement("properties");

		NodeList projectNodeList = document.getElementsByTagName("project");

		Node node = projectNodeList.item(0);

		node.appendChild(propertiesListNode);

		return propertiesListNode;
	}

	private boolean _hasDependency(IProject project, String groupId, String artifactId) {
		boolean retval = false;

		File pomFile = FileUtil.getFile(project.getFile("pom.xml"));

		MavenXpp3Reader mavenReader = new MavenXpp3Reader();

		try (FileReader reader = new FileReader(pomFile.getAbsolutePath())) {
			Model model = mavenReader.read(reader);

			List<Dependency> dependencies = model.getDependencies();

			for (Dependency dependency : dependencies) {
				if (groupId.equals(dependency.getGroupId()) && artifactId.equals(dependency.getArtifactId())) {
					retval = true;

					break;
				}
			}
		}
		catch (FileNotFoundException fnfe) {
		}
		catch (IOException ioe) {
		}
		catch (XmlPullParserException xppe) {
		}

		return retval;
	}

	private boolean _isProtletProject(IProject project) {
		IFile portletFile = project.getFile("src/main/webapp/WEB-INF/portlet.xml");
		IFile liferayPortletFile = project.getFile("src/main/webapp/WEB-INF/liferay-portlet.xml");

		if (FileUtil.exists(portletFile) && FileUtil.exists(liferayPortletFile)) {
			return true;
		}

		return false;
	}

	private boolean _isServiceBuilderProject(IProject project) {
		IFile serviceFile = project.getFile("src/main/webapp/WEB-INF/service.xml");

		if (FileUtil.exists(serviceFile)) {
			return true;
		}

		return false;
	}

	private boolean _isServiceBuildersubProject(IProject project) {
		if (StringUtil.endsWith(project.getName(), "-service") &&
			_hasDependency(project, "com.liferay.portal", "portal-service")) {

			return true;
		}

		return false;
	}

	private boolean _isThemeProject(IProject project) {
		IFile lookAndFeelFile = project.getFile("src/main/webapp/WEB-INF/liferay-look-and-feel.xml");

		if (FileUtil.exists(lookAndFeelFile)) {
			return true;
		}

		return false;
	}

	private void _removeChildren(Node node) {
		while (node.hasChildNodes()) {
			node.removeChild(node.getFirstChild());
		}
	}

	private static String[][] _dependenciesConvertMap;

	private IStructuredFormatProcessor _formatProcessor = new FormatProcessorXML();

}