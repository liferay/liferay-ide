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
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.BladeCLIException;
import com.liferay.ide.project.core.modules.ImportLiferayModuleProjectOpMethods;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.ValManager;
import org.eclipse.wst.validation.internal.ValPrefManagerProject;
import org.eclipse.wst.validation.internal.ValidatorMutable;
import org.eclipse.wst.validation.internal.model.ProjectPreferences;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class UpgradeUtil {

	public static void clearExistingProjects(IPath location, IProgressMonitor monitor) throws CoreException {
		IProject sdkProject = SDKUtil.getWorkspaceSDKProject();

		if ((sdkProject != null) && location.equals(sdkProject.getLocation())) {
			IProject[] projects = ProjectUtil.getAllPluginsSDKProjects();

			for (IProject project : projects) {
				project.delete(false, true, monitor);
			}

			sdkProject.delete(false, true, monitor);
		}

		IProject[] projects = CoreUtil.getAllProjects();

		for (IProject project : projects) {
			String projectPortableLocation = FileUtil.getLocationPortableString(project);

			if (projectPortableLocation.startsWith(location.toPortableString())) {
				project.delete(false, true, monitor);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static boolean configureProjectValidationExclude(IProject project, boolean disableValidation) {
		boolean retval = false;

		if (project == null) {
			return retval;
		}

		try {
			ValManager valManager = ValManager.getDefault();

			Validator[] vals = valManager.getValidators(project, true);

			ValidatorMutable[] validators = new ValidatorMutable[vals.length];

			for (int i = 0; i < vals.length; i++) {
				validators[i] = new ValidatorMutable(vals[i]);
			}

			ProjectPreferences pp = new ProjectPreferences(project, true, disableValidation, null);

			ValPrefManagerProject vpm = new ValPrefManagerProject(project);

			vpm.savePreferences(pp, validators);
		}
		catch (Exception e) {
		}

		return retval;
	}

	public static void createLiferayWorkspace(IPath targetSDKLocation, String version, IProgressMonitor groupMonitor)
		throws InterruptedException {

		Job job = new Job("Initializing Liferay Workspace...") {

			@Override
			protected IStatus run(IProgressMonitor progress) {
				try {
					StringBuilder sb = new StringBuilder();

					sb.append("--base ");

					File targetSdkFile = targetSDKLocation.toFile();

					sb.append("\"" + targetSdkFile.getAbsolutePath() + "\" ");

					sb.append("init -u");
					sb.append(" -v ");
					sb.append(version);

					progress.worked(30);
					BladeCLI.execute(sb.toString());
					progress.worked(100);
				}
				catch (BladeCLIException bclie) {
					ProjectUI.logError(bclie);

					return ProjectUI.createErrorStatus("Faild execute Liferay Workspace Init Command...", bclie);
				}

				return org.eclipse.core.runtime.Status.OK_STATUS;
			}

		};

		job.setProgressGroup(groupMonitor, IProgressMonitor.UNKNOWN);

		job.schedule();

		job.join();
	}

	public static void deleteEclipseConfigFiles(File project) {
		for (File file : project.listFiles()) {
			if (".classpath".contentEquals(file.getName()) || ".settings".contentEquals(file.getName()) ||
				".project".contentEquals(file.getName())) {

				if (file.isDirectory()) {
					FileUtil.deleteDir(file, true);
				}

				file.delete();
			}
		}
	}

	public static void deleteSDKLegacyProjects(IPath sdkLocation) {
		String[] needDeletedPaths = {"shared/portal-http-service", "webs/resources-importer-web"};

		for (String path : needDeletedPaths) {
			IPath sdkPath = sdkLocation.append(path);

			File file = sdkPath.toFile();

			if (file.exists()) {
				FileUtil.deleteDir(file, true);
			}
		}
	}

	public static void deleteServiceBuilderJarFile(IProject project, IProgressMonitor monitor) {
		try {
			IFolder docrootFolder = CoreUtil.getDefaultDocrootFolder(project);

			if (docrootFolder != null) {
				IFile serviceJarFile = docrootFolder.getFile("WEB-INF/lib/" + project.getName() + "-service.jar");

				if (serviceJarFile.exists()) {
					serviceJarFile.delete(true, monitor);
				}
			}
		}
		catch (CoreException ce) {
			ProjectUI.logError(ce);
		}
	}

	public static List<IProject> getAvailableProject(IProject[] projects) {
		List<IProject> projectList = new ArrayList<>();

		for (IProject project : projects) {
			IPath location = project.getLocation();

			location = location.removeLastSegments(1);

			String parent = location.lastSegment();

			if (StringUtil.equals(parent, "hooks") || StringUtil.equals(parent, "layouttpl") ||
				StringUtil.equals(parent, "webs") || StringUtil.equals(parent, "portlets")) {

				projectList.add(project);
			}
		}

		return projectList;
	}

	public static boolean isAlreadyImported(IPath path) {
		IWorkspaceRoot workspaceRoot = CoreUtil.getWorkspaceRoot();

		IContainer[] containers = workspaceRoot.findContainersForLocationURI(FileUtil.toURI(path));

		long projectCount = Stream.of(
			containers
		).filter(
			container -> container instanceof IProject
		).count();

		if (projectCount > 0) {
			return true;
		}

		return false;
	}

	public static boolean isMavenProject(IPath path) {
		if (FileUtil.notExists(path)) {
			return false;
		}

		IStatus buildType = ImportLiferayModuleProjectOpMethods.getBuildType(path.toOSString());

		return "maven".equals(buildType.getMessage());
	}

	public static boolean isMavenProject(Path path) {
		if (FileUtil.notExists(path)) {
			return false;
		}

		IStatus buildType = ImportLiferayModuleProjectOpMethods.getBuildType(path.toOSString());

		return "maven".equals(buildType.getMessage());
	}

	@SuppressWarnings("unchecked")
	public static void removeIvyPrivateSetting(IPath sdkLocation) throws CoreException {
		IPath ivySettingPath = sdkLocation.append("ivy-settings.xml");

		File ivySettingFile = ivySettingPath.toFile();

		SAXBuilder builder = new SAXBuilder(false);

		builder.setValidation(false);
		builder.setFeature("http://xml.org/sax/features/validation", false);
		builder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

		if (FileUtil.notExists(ivySettingFile)) {
			return;
		}

		try (InputStream ivyInput = Files.newInputStream(ivySettingFile.toPath())) {
			Document doc = builder.build(ivyInput);

			Element itemRem = null;

			Element elementRoot = doc.getRootElement();

			List<Element> resolversElements = elementRoot.getChildren("resolvers");

			for (Iterator<Element> resolversIterator = resolversElements.iterator(); resolversIterator.hasNext();) {
				Element resolversElement = resolversIterator.next();

				List<Element> chainElements = resolversElement.getChildren("chain");

				for (Iterator<Element> chainIterator = chainElements.iterator(); chainIterator.hasNext();) {
					Element chainElement = chainIterator.next();

					List<Element> resolverElements = chainElement.getChildren("resolver");

					Iterator<Element> resolverIterator = resolverElements.iterator();

					while (resolverIterator.hasNext()) {
						Element resolverItem = resolverIterator.next();

						String resolverRefItem = resolverItem.getAttributeValue("ref");

						if (resolverRefItem.equals("liferay-private")) {
							resolverIterator.remove();

							itemRem = resolverItem;
						}
					}
				}

				elementRoot.removeContent(itemRem);

				List<Element> ibiblioElements = resolversElement.getChildren("ibiblio");

				for (Iterator<Element> ibiblioIterator = ibiblioElements.iterator(); ibiblioIterator.hasNext();) {
					Element ibiblioElement = ibiblioIterator.next();

					String liferayPrivateName = ibiblioElement.getAttributeValue("name");

					if (liferayPrivateName.equals("liferay-private")) {
						ibiblioIterator.remove();
						itemRem = ibiblioElement;
					}
				}

				elementRoot.removeContent(itemRem);
			}

			XMLOutputter out = new XMLOutputter();

			try (OutputStream fos = Files.newOutputStream(ivySettingFile.toPath())) {
				out.output(doc, fos);
			}
			catch (Exception e) {
				ProjectUI.logError(e);

				throw new CoreException(
					StatusBridge.create(Status.createErrorStatus("Failed to save change for ivy-settings.xml.", e)));
			}
		}
		catch (CoreException | IOException | JDOMException e) {
			ProjectUI.logError(e);

			throw new CoreException(
				StatusBridge.create(
					Status.createErrorStatus(
						"Failed to remove Liferay private url configuration of ivy-settings.xml.", e)));
		}
	}

}