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

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.ImportLiferayModuleProjectOp;
import com.liferay.ide.project.core.modules.ImportLiferayModuleProjectOpMethods;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sapphire.modeling.Status.Severity;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.wst.server.core.IRuntime;

import org.osgi.framework.Bundle;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Andy Wu
 */
@SuppressWarnings("serial")
public class CustomJspConverter {

	public static String resultPrefix = "result";
	public static String sourcePrefix = "source";

	public static void clearConvertResults() {
		IPath location = ProjectUI.getPluginStateLocation();

		IPath path = location.append(_resultFileName);

		File resultFile = path.toFile();

		if (resultFile.exists()) {
			resultFile.delete();
		}
	}

	public static String[] getConvertResult(String filter) {
		List<String> results = new ArrayList<>();

		IPath location = ProjectUI.getPluginStateLocation();

		IPath path = location.append(_resultFileName);

		File resultFile = path.toFile();

		if (!resultFile.exists()) {
			return new String[0];
		}

		Properties resultProp = PropertiesUtil.loadProperties(resultFile);

		if ((resultProp == null) || ListUtil.isEmpty(resultProp.keySet())) {
			return null;
		}

		Enumeration<?> keys = resultProp.propertyNames();

		while (keys.hasMoreElements()) {
			String key = (String)keys.nextElement();

			if (key.startsWith(filter)) {
				results.add(resultProp.getProperty(key));
			}
		}

		return results.toArray(new String[0]);
	}

	public static String getCustomJspPath(String sourcePath) {
		String hookFilePath = "/docroot/WEB-INF/liferay-hook.xml";

		File hookFile = new File(sourcePath + hookFilePath);

		if (!hookFile.exists()) {
			hookFile = new File(sourcePath + "/src/main/webapp/WEB-INF/liferay-hook.xml");

			if (!hookFile.exists()) {
				return null;
			}
		}

		String customJspPath = null;

		try (InputStream input = Files.newInputStream(hookFile.toPath())) {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder domBuilder = documentBuilderFactory.newDocumentBuilder();

			domBuilder.setEntityResolver(
				new EntityResolver() {

					public InputSource resolveEntity(String publicId, String systemId)
						throws IOException, SAXException {

						// don't connect internet to fetch dtd for validation

						try (InputStream inputStream = new ByteArrayInputStream(new String("").getBytes())) {
							return new InputSource(inputStream);
						}
					}

				});

			Document doc = domBuilder.parse(input);

			Element root = doc.getDocumentElement();

			NodeList nodeList = root.getChildNodes();

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);

				if ((node.getNodeType() == Node.ELEMENT_NODE) && "custom-jsp-dir".equals(node.getNodeName())) {
					Node child = node.getFirstChild();

					customJspPath = child.getNodeValue();
				}
			}
		}
		catch (Exception e) {
		}

		if (CoreUtil.isNullOrEmpty(customJspPath)) {
			return null;
		}

		File customJspDir = new File(sourcePath + "/docroot/" + customJspPath);

		if (!customJspDir.exists() || !customJspDir.isDirectory()) {
			customJspDir = new File(sourcePath + "/src/main/webapp" + customJspPath);

			if (!customJspDir.exists() || !customJspDir.isDirectory()) {
				return null;
			}
		}

		return customJspPath;
	}

	public void convertJspHookProject(
			String[] sourcePaths, String[] projectNames, String targetPath, IProgressMonitor monitor,
			boolean liferayWorkspace)
		throws Exception {

		_resultProp = new Properties();

		int size = sourcePaths.length;

		for (int i = 0; i < size; i++) {
			String sourcePath = sourcePaths[i];

			String customJspPath = getCustomJspPath(sourcePath);

			if (CoreUtil.isNullOrEmpty(customJspPath)) {
				throw new Exception("convert failed, can not find custom jsp folder");
			}

			_convertToCoreJspHook(sourcePath, customJspPath, targetPath, liferayWorkspace);

			_convertToFragment(sourcePath, customJspPath, targetPath);

			File sourceFile = new File(sourcePath);

			_resultProp.setProperty(sourcePrefix + "." + sourceFile.getName(), projectNames[i] + ":" + customJspPath);
		}

		_saveResultProperties();
	}

	public void doExecute(String[] projectPaths, String[] projectNames, String targetPath, boolean liferayWorkspace) {
		Job job = new WorkspaceJob("Converting Jsp hook to fragments...") {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				IStatus retval = Status.OK_STATUS;

				try {
					convertJspHookProject(projectPaths, projectNames, targetPath, monitor, liferayWorkspace);

					String[] projectPaths = getConvertResult(resultPrefix);

					if (ListUtil.isNotEmpty(projectPaths)) {
						for (String path : projectPaths) {
							ImportLiferayModuleProjectOp importOp = ImportLiferayModuleProjectOp.TYPE.instantiate();

							importOp.setLocation(path);

							org.eclipse.sapphire.modeling.Status status = importOp.validation();

							if (status.severity() != Severity.ERROR) {
								ImportLiferayModuleProjectOpMethods.execute(
									importOp, ProgressMonitorBridge.create(monitor));
							}
						}

						refreshUI();
					}
				}
				catch (Exception e) {
					retval = ProjectUI.createErrorStatus("Error in convert jsp", e);
				}

				return retval;
			}

		};

		try {
			IProgressService progressService = UIUtil.getProgressService();

			progressService.showInDialog(UIUtil.getActiveShell(), job);

			job.schedule();
		}
		catch (Exception e) {
		}
	}

	public IRuntime getLiferay70Runtime() {
		return _liferay70Runtime;
	}

	public void refreshUI() {
		UIUtil.async(
			new Runnable() {

				@Override
				public void run() {
					_ui.refreshTreeViews();
				}

			});
	}

	public void setLiferay62ServerLocation(String liferay62ServerLocation) {
		_liferay62ServerLocation = liferay62ServerLocation;
	}

	public void setLiferay70Runtime(IRuntime liferay70Runtime) {
		_liferay70Runtime = liferay70Runtime;
	}

	public void setUi(CustomJspPage ui) {
		_ui = ui;
	}

	private void _convertToCoreJspHook(
			String sourcePath, String customJspPath, String targetPath, boolean liferayWorkspace)
		throws Exception {

		File commonDir = _getMovedDir(sourcePath, customJspPath, "common");
		File portalDir = _getMovedDir(sourcePath, customJspPath, "portal");
		File taglibDir = _getMovedDir(sourcePath, customJspPath, "taglib");

		// at least one folder exist

		if ((commonDir != null) || (portalDir != null) || (taglibDir != null)) {
			File[] dirs = new File[3];

			dirs[0] = commonDir;
			dirs[1] = portalDir;
			dirs[2] = taglibDir;

			File location = new File(targetPath);

			ProjectUI projectUI = ProjectUI.getDefault();

			Bundle bundle = projectUI.getBundle();

			URL projectZipUrl = bundle.getEntry("resources/codeupgrade.corejsphook.zip");

			projectZipUrl = FileLocator.toFileURL(projectZipUrl);

			File projectZipFile = FileUtil.getFile(projectZipUrl);

			ZipUtil.unzip(projectZipFile, location);

			// File jspDir = new File( location, coreJspHookResourcesPath + "html" );

			File ignoreFolder = new File(location, _coreJspHookResourcesPath + ".ignore/");
			File destFolder = new File(location, _coreJspHookResourcesPath);

			ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime(getLiferay70Runtime());

			for (File dir : dirs) {
				if (dir != null) {

					// copy 70 original jsp file to converted project ignore folder

					List<String> fileRelativizePaths = _getAllRelativizeFilePaths(dir);

					for (String fileRelativizePath : fileRelativizePaths) {
						File original62File = new File(_get62HtmlDir() + dir.getName() + "/" + fileRelativizePath);

						IPath path = liferayRuntime.getAppServerPortalDir();

						IPath appServerPortalDir = path.removeLastSegments(1);

						File original70File = FileUtil.getFile(
							appServerPortalDir.append("ROOT/html/" + dir.getName() + "/" + fileRelativizePath));

						if (original62File.exists() && original70File.exists()) {
							File target62File = new File(
								ignoreFolder, "html/" + dir.getName() + "/" + fileRelativizePath + ".62");
							File target70File = new File(
								destFolder, "html/" + dir.getName() + "/" + fileRelativizePath);

							_makeParentDir(target62File);
							_makeParentDir(target70File);

							FileUtil.copyFile(original62File, target62File);
							FileUtil.copyFile(original70File, target70File);
						}
					}
				}
			}

			String sourceProjectName = (new File(sourcePath)).getName();

			File projectFolder = new File(location, "codeupgrade.corejsphook");

			File gradleWsFile = new File(projectFolder, "build-ws.gradle");
			File gradleFile = new File(projectFolder, "build.gradle");

			if (liferayWorkspace) {
				gradleFile.delete();

				gradleWsFile.renameTo(gradleFile);
			}
			else {
				gradleWsFile.delete();
			}

			File newFolder = new File(location, sourceProjectName + ".corejsphook");

			projectFolder.renameTo(newFolder);

			String s = newFolder.getAbsolutePath();

			_resultProp.setProperty(resultPrefix + "." + sourceProjectName + "/portalCore", s.replace("\\\\", "/"));
		}
	}

	private void _convertToFragment(String sourcePath, String customJspPath, String targetPah) throws Exception {
		File[] portlets = _getPortletDirs(sourcePath, customJspPath);

		if (portlets == null) {
			return;
		}

		for (File portlet : portlets) {
			String fragmentPath = _createFragment(portlet.getName(), sourcePath, targetPah);

			File sourceFile = new File(sourcePath);

			if (CoreUtil.isNotNullOrEmpty(fragmentPath)) {
				_resultProp.setProperty(
					resultPrefix + "." + sourceFile.getName() + "/portlet/" + portlet.getName(), fragmentPath);
			}
		}
	}

	private void _copy62JspFile(String portlet, String jsp, File targetJspDir, String mappedJsp) throws Exception {
		String htmlDir = _get62HtmlDir();

		if (htmlDir == null) {
			return;
		}

		File jsp62 = new File(htmlDir + "portlet/" + portlet + "/" + jsp);

		File targetFile = new File(targetJspDir + "/.ignore/", mappedJsp + ".62");

		_makeParentDir(targetFile);

		FileUtil.copyFile(jsp62, targetFile);
	}

	/**
	 *  the main method of converting jsp hook project
	 */
	private void _copy70JspFile(String portlet, File targetJspDir, String mappedJsp) throws Exception {
		File module = _getModuleFile(portlet);

		try (JarFile jarFile = new JarFile(module)) {
			JarEntry entry = (JarEntry)jarFile.getEntry("META-INF/resources/" + mappedJsp);

			try (InputStream ins = jarFile.getInputStream(entry)) {
				File targetFile = new File(targetJspDir, mappedJsp);

				_makeParentDir(targetFile);

				FileUtil.writeFile(targetFile, ins);
			}
		}
	}

	// convert common, portal, taglib folders to 7.x CustomJspBag

	private String _createEmptyJspHookProject(String portlet, String originProjectName, String targetPath)
		throws Exception {

		String projectName = originProjectName + "-" + portlet + "-fragment";

		if (FileUtil.exists(ProjectUtil.getProject(projectName))) {
			UIUtil.sync(
				new Runnable() {

					@Override
					public void run() {
						MessageDialog.openInformation(
							UIUtil.getActiveShell(), "Convert Custom JSP",
							projectName + " has already been converted.");
					}

				});

			return projectName;
		}

		String module = _portlet2ModuleMap.get(portlet);

		if (module == null) {
			return null;
		}

		StringBuilder strBuilder = new StringBuilder("create ");

		strBuilder.append("-d \"");
		strBuilder.append(targetPath);
		strBuilder.append("\" ");
		strBuilder.append("-t fragment ");
		strBuilder.append("-h ");
		strBuilder.append(module);
		strBuilder.append(" ");
		strBuilder.append("-H ");
		strBuilder.append(_getModuleVersion(portlet));
		strBuilder.append(" ");
		strBuilder.append("\"");
		strBuilder.append(projectName);
		strBuilder.append("\"");

		BladeCLI.execute(strBuilder.toString());

		return projectName;
	}

	// convert portlets under portlet dir into fragment

	private String _createFragment(String portlet, String sourcePath, String targetPath) throws Exception {
		String result = null;

		File src = new File(sourcePath);

		String originProjectName = src.getName();

		String projectName = _createEmptyJspHookProject(portlet, originProjectName, targetPath);

		if (projectName == null) {
			return null;
		}

		File targetJspDir = new File(targetPath + "/" + projectName + "/src/main/resources/META-INF/resources/");

		List<String> jspList = _getAllFilesFromSourcePortletDir(portlet);

		List<String> moduleJsps = _getAllFilesFromModuleJar(portlet);

		for (String jsp : jspList) {
			String mappedJsp = _jspPathConvert(portlet, jsp);

			if ((moduleJsps != null) && moduleJsps.contains(mappedJsp)) {
				_copy62JspFile(portlet, jsp, targetJspDir, mappedJsp);

				_copy70JspFile(portlet, targetJspDir, mappedJsp);
			}
		}

		result = targetPath + "/" + projectName;

		return result;
	}

	private String _get62HtmlDir() {
		if (CoreUtil.empty(_liferay62ServerLocation)) {
			return null;
		}

		File bundleDir = new File(_liferay62ServerLocation);

		String[] names = bundleDir.list(
			new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					if (name.startsWith("tomcat-")) {
						return true;
					}

					return false;
				}

			});

		if ((names != null) && (names.length == 1)) {
			return _liferay62ServerLocation + "/" + names[0] + "/webapps/ROOT/html/";
		}
		else {
			return null;
		}
	}

	private List<String> _getAllFilesFromModuleJar(String portlet) throws Exception {
		List<String> result = new ArrayList<>();

		File moduleFile = _getModuleFile(portlet);

		if (moduleFile == null) {
			return null;
		}

		JarFile jarFile = new JarFile(moduleFile);

		Enumeration<JarEntry> jarEntrys = jarFile.entries();

		while (jarEntrys.hasMoreElements()) {
			JarEntry entry = jarEntrys.nextElement();

			String entryName = entry.getName();

			if (entryName.startsWith("META-INF/resources/") && !entry.isDirectory()) {
				result.add(entryName.substring(19));
			}
		}

		jarFile.close();

		return result;
	}

	private List<String> _getAllFilesFromSourcePortletDir(String portlet) {
		File portletDir = new File(_sourcePortletDir + "/" + portlet);

		return _getAllRelativizeFilePaths(portletDir);
	}

	private List<String> _getAllRelativizeFilePaths(File file) {
		List<String> retVal = new ArrayList<>();

		Path source = file.toPath();

		FileVisitorImpl visitor = new FileVisitorImpl(source);

		try {
			Files.walkFileTree(source, visitor);

			retVal = visitor.getResults();
		}
		catch (IOException ioe) {
		}

		return retVal;
	}

	private File _getModuleFile(String portlet) {
		String moduleName = _portlet2ModuleMap.get(portlet);

		if (moduleName == null) {
			return null;
		}

		String moduleFileName = null;

		for (String name : ServerUtil.getModuleFileListFrom70Server(getLiferay70Runtime())) {
			if (name.contains(moduleName)) {
				moduleFileName = name;
			}
		}

		if (moduleFileName == null) {
			return null;
		}

		ProjectCore projectCore = ProjectCore.getDefault();

		IPath stateLocation = projectCore.getStateLocation();

		final IPath temp = stateLocation.append("moduleCache");

		File tempFile = temp.toFile();

		if (!tempFile.exists()) {
			tempFile.mkdirs();
		}

		return ServerUtil.getModuleFileFrom70Server(getLiferay70Runtime(), moduleFileName, temp);
	}

	private String _getModuleVersion(String portlet) throws Exception {
		File moduleFile = _getModuleFile(portlet);

		JarFile jarFile = new JarFile(moduleFile);

		Manifest manifest = jarFile.getManifest();

		Attributes mainAttributes = manifest.getMainAttributes();

		String version = mainAttributes.getValue("Bundle-Version");

		jarFile.close();

		return version;
	}

	private File _getMovedDir(String sourcePath, String customJspPath, String dirName) {
		String path = "/html/" + dirName;

		File dir = new File(sourcePath + "/docroot/" + customJspPath + path);

		if (dir.exists()) {
			return dir;
		}
		else {
			dir = new File(sourcePath + "/src/main/webapp/" + customJspPath + path);

			if (dir.exists()) {
				return dir;
			}

			return null;
		}
	}

	private File[] _getPortletDirs(String sourcePath, String customJspPath) {
		_sourcePortletDir = sourcePath + "/docroot/" + customJspPath + "/html/portlet/";

		File portletDir = new File(_sourcePortletDir);

		if (!portletDir.exists() || !portletDir.isDirectory()) {
			portletDir = new File(sourcePath + "/src/main/webapp/" + customJspPath + "/html/portlet/");

			if (!portletDir.exists() || !portletDir.isDirectory()) {
				return null;
			}

			_sourcePortletDir = portletDir.getAbsolutePath();
		}

		File[] portlets = portletDir.listFiles(
			new FileFilter() {

				@Override
				public boolean accept(File file) {
					if (file.isDirectory()) {
						return true;
					}
					else {
						return false;
					}
				}

			});

		return portlets;
	}

	private String _jspPathConvert(String portletName, String jspPath) {
		String result = jspPath;

		if (_jspPathMap.contains(portletName)) {
			result = portletName + "/" + jspPath;
		}

		return result;
	}

	private void _makeParentDir(File target) throws Exception {
		File parent = target.getParentFile();

		if (!parent.exists() && !parent.mkdirs()) {
			throw new Exception("can not create dir " + parent);
		}
	}

	private void _saveResultProperties() {
		ProjectUI projectUI = ProjectUI.getDefault();

		IPath projectUITempLocation = projectUI.getStateLocation();

		IPath path = projectUITempLocation.append(_resultFileName);

		File resultFile = path.toFile();

		if (resultFile.exists()) {
			resultFile.delete();
		}

		PropertiesUtil.saveProperties(_resultProp, resultFile);
	}

	private static List<String> _jspPathMap = new ArrayList<String>() {
		{
			add("bookmarks");
			add("blogs");
			add("blogs_admin");
			add("blogs_aggregator");
			add("document_library");
			add("image_gallery_display");
			add("message_boards");
			add("message_boards_admin");
			add("wiki");
			add("wiki_display");
		}
	};
	private static Map<String, String> _portlet2ModuleMap = new HashMap<String, String>() {
		{
			put("activities", "com.liferay.social.activities.web");
			put("announcements", "com.liferay.announcements.web");
			put("asset_browser", "com.liferay.asset.browser.web");
			put("asset_categories_navigation", "com.liferay.asset.categories.navigation.web");
			put("asset_category_admin", "com.liferay.asset.categories.admin.web");
			put("asset_publisher", "com.liferay.asset.publisher.web");
			put("asset_tag_admin", "com.liferay.asset.tags.admin.web");
			put("asset_tags_navigation", "com.liferay.asset.tags.navigation.web");
			put("blogs", "com.liferay.blogs.web");
			put("blogs_admin", "com.liferay.blogs.web");
			put("blogs_aggregator", "com.liferay.blogs.web");
			put("bookmarks", "com.liferay.bookmarks.web");
			put("breadcrumb", "com.liferay.site.navigation.breadcrumb.web");
			put("document_library", "com.liferay.document.library.web");
			put("document_library_display", "com.liferay.document.library.web");
			put("dynamic_data_list_display", "com.liferay.dynamic.data.lists.web");
			put("dynamic_data_lists", "com.liferay.dynamic.data.lists.web");
			put("dynamic_data_mapping", "com.liferay.dynamic.data.mapping.web");
			put("expando", "com.liferay.expando.web");
			put("group_statistics", "com.liferay.social.group.statistics.web");
			put("hello_velocity", "com.liferay.hello.velocity.web");
			put("iframe", "com.liferay.iframe.web");
			put("image_gallery_display", "com.liferay.document.library.web");
			put("journal", "com.liferay.journal.web");
			put("journal_content", "com.liferay.journal.content.web");
			put("journal_content_search", "com.liferay.journal.content.search.web");
			put("language", "com.liferay.site.navigation.language.web");
			put("layout_prototypes", "com.liferay.layout.prototype.web");
			put("layout_set_prototypes", "com.liferay.layout.set.prototype.web");
			put("layouts_admin", "com.liferay.layout.admin.web");
			put("login", "com.liferay.login.web");
			put("message_boards", "com.liferay.message.boards.web");
			put("message_boards_admin", "com.liferay.message.boards.web");
			put("mobile_device_rules", "com.liferay.mobile.device.rules.web");
			put("monitoring", "com.liferay.monitoring.web");
			put("my_account", "com.liferay.my.account.web");
			put("my_sites", "com.liferay.site.my.sites.web");
			put("navigation", "com.liferay.site.navigation.menu.web");
			put("nested_portlets", "com.liferay.nested.portlets.web");
			put("page_comments", "com.liferay.comment.page.comments.web");
			put("page_flags", "com.liferay.flags.web");
			put("page_ratings", "com.liferay.ratings.page.ratings.web");
			put("password_policies_admin", "com.liferay.password.policies.admin.web");
			put("plugins_admin", "com.liferay.plugins.admin.web");
			put("polls", "com.liferay.polls.web");
			put("polls_display", "com.liferay.polls.web");
			put("portal_settings", "com.liferay.portal.settings.web");
			put("portlet_configuration", "com.liferay.portlet.configuration.web");
			put("portlet_css", "com.liferay.portlet.configuration.css.web");
			put("quick_note", "com.liferay.quick.note.web");
			put("recent_bloggers", "com.liferay.blogs.recent.bloggers.web");
			put("requests", "com.liferay.social.requests.web");
			put("roles_admin", "com.liferay.roles.admin.web");
			put("rss", "com.liferay.rss.web");
			put("search", "com.liferay.portal.search.web");
			put("site_browser", "com.liferay.site.browser.web");
			put("site_map", "com.liferay.site.navigation.site.map.web");
			put("sites_admin", "com.liferay.site.admin.web");
			put("sites_directory", "com.liferay.site.navigation.directory.web");
			put("social_activity", "com.liferay.social.activity.web");
			put("staging_bar", "com.liferay.staging.bar.web");
			put("trash", "com.liferay.trash.web");
			put("user_groups_admin", "com.liferay.user.groups.admin.web");
			put("user_statistics", "com.liferay.social.user.statistics.web");
			put("users_admin", "com.liferay.users.admin.web");
			put("web_proxy", "com.liferay.web.proxy.web");
			put("wiki", "com.liferay.wiki.web");
			put("wiki_display", "com.liferay.wiki.web");
			put("workflow_definition_links", "com.liferay.portal.workflow.definition.link.web");
			put("workflow_definitions", "com.liferay.portal.workflow.definition.web");
			put("workflow_instances", "com.liferay.portal.workflow.instance.web");
			put("workflow_tasks", "com.liferay.portal.workflow.task.web");
			put("xsl_content", "com.liferay.xsl.content.web");
		}
	};
	private static String _resultFileName = "convertJspHookResult.properties";

	// some jsps in some portlets were moved to subfolder in corresponding jar so
	// the path need to be converted

	private String _coreJspHookResourcesPath = "codeupgrade.corejsphook/src/main/resources/META-INF/resources/";
	private String _liferay62ServerLocation = null;
	private IRuntime _liferay70Runtime;
	private Properties _resultProp;
	private String _sourcePortletDir = null;
	private CustomJspPage _ui;

	private class FileVisitorImpl implements FileVisitor<Path> {

		public FileVisitorImpl(Path source) {
			_filePaths = new ArrayList<>();

			_source = source;
		}

		public List<String> getResults() {
			return _filePaths;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			Path relativize = _source.relativize(file);

			String s = relativize.toString();

			String relativizePath = s.replaceAll("\\\\", "/");

			_filePaths.add(relativizePath);

			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
			return FileVisitResult.CONTINUE;
		}

		private List<String> _filePaths;
		private Path _source;

	}

}