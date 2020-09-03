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

package com.liferay.ide.project.core.modules;

import aQute.bnd.version.Version;
import aQute.bnd.version.VersionRange;

import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.nio.charset.StandardCharsets;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class ProjectTemplateNameValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	public void dispose() {
		NewLiferayModuleProjectOp op = context(NewLiferayModuleProjectOp.class);

		if (op != null) {
			SapphireUtil.detachListener(op.property(NewLiferayModuleProjectOp.PROP_LIFERAY_VERSION), _listener);
			SapphireUtil.detachListener(op.property(NewLiferayModuleProjectOp.PROP_PROJECT_PROVIDER), _listener);
		}

		super.dispose();
	}

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		IWorkspaceProject liferayWorkspaceProject = LiferayWorkspaceUtil.getLiferayWorkspaceProject();

		if (Objects.isNull(liferayWorkspaceProject)) {
			return retval;
		}

		NewLiferayModuleProjectOp op = context(NewLiferayModuleProjectOp.class);

		String targetPlatformVersionString = liferayWorkspaceProject.getTargetPlatformVersion();

		String liferayVersion = get(op.getLiferayVersion());

		if (CoreUtil.isNotNullOrEmpty(targetPlatformVersionString)) {
			Version tagetPlatformVerstion = Version.parseVersion(targetPlatformVersionString);

			liferayVersion = new String(tagetPlatformVerstion.getMajor() + "." + tagetPlatformVerstion.getMinor());
		}

		String projectTemplateName = get(op.getProjectTemplateName());

		if (projectTemplateName.startsWith("js")) {
			return Status.createErrorStatus(
				"This wizard does not support creating this type of module. Create it using the CLI first and then " +
					"import here.");
		}

		if (projectTemplateName.startsWith("form-field")) {
			NewLiferayProjectProvider<BaseModuleOp> newLiferayProjectProvider = get(op.getProjectProvider());

			if (StringUtil.equalsIgnoreCase(newLiferayProjectProvider.getDisplayName(), "maven")) {
				VersionRange requiredVersionRange = new VersionRange(
					true, new Version("7.0"), new Version("7.2"), false);

				if (!requiredVersionRange.includes(new Version(liferayVersion))) {
					return Status.createErrorStatus("Form Field project is only supported 7.0 and 7.1 for Maven");
				}
			}
		}

		boolean warCoreExt = projectTemplateName.equals("war-core-ext");

		boolean npm = projectTemplateName.startsWith("npm");

		projectTemplateName = projectTemplateName.replace("-", ".");

		VersionRange versionRange = _projectTemplateVersionRangeMap.get(projectTemplateName);

		if (versionRange != null) {
			boolean include = versionRange.includes(new Version(liferayVersion));

			if (!include) {
				if (npm) {
					retval = Status.createErrorStatus(
						"NPM portlet project templates generated from this tool are not supported for specified " +
							"Liferay version. See LPS-97950 for full details.");
				}
				else {
					retval = Status.createErrorStatus(
						"Specified Liferay version is invaild. Must be in range " + versionRange);
				}
			}
		}
		else {
			retval = Status.createWarningStatus("Unable to get supported Liferay version.");
		}

		if (warCoreExt && retval.ok()) {
			NewLiferayProjectProvider<BaseModuleOp> newLiferayProjectProvider = get(op.getProjectProvider());

			if (StringUtil.equalsIgnoreCase(newLiferayProjectProvider.getDisplayName(), "maven")) {
				retval = Status.createErrorStatus("Not support to create maven war-core-ext project.");
			}
		}

		return retval;
	}

	@Override
	protected void initValidationService() {
		super.initValidationService();

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		NewLiferayModuleProjectOp op = context(NewLiferayModuleProjectOp.class);

		SapphireUtil.attachListener(op.property(NewLiferayModuleProjectOp.PROP_LIFERAY_VERSION), _listener);
		SapphireUtil.attachListener(op.property(NewLiferayModuleProjectOp.PROP_PROJECT_PROVIDER), _listener);

		_loadSupportedVersionRanges();
	}

	private void _loadSupportedVersionRanges() {
		File bladeJar = null;

		try {
			bladeJar = FileUtil.getFile(BladeCLI.getBladeCLIPath());
		}
		catch (BladeCLIException bclie) {
		}

		if (bladeJar != null) {
			try (ZipFile zipFile = new ZipFile(bladeJar)) {
				Enumeration<? extends ZipEntry> entries = zipFile.entries();

				while (entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();

					String entryName = entry.getName();

					if (entryName.endsWith(".jar") && entryName.startsWith("com.liferay.project.templates.")) {
						try (InputStream in = zipFile.getInputStream(entry)) {
							ProjectCore projectCore = ProjectCore.getDefault();

							File stateFile = FileUtil.getFile(projectCore.getStateLocation());

							File tempFile = new File(stateFile, entryName);

							FileUtil.writeFileFromStream(tempFile, in);

							try (ZipFile tempZipFile = new ZipFile(tempFile)) {
								Enumeration<? extends ZipEntry> tempEntries = tempZipFile.entries();

								while (tempEntries.hasMoreElements()) {
									ZipEntry tempEntry = tempEntries.nextElement();

									String tempEntryName = tempEntry.getName();

									if (tempEntryName.equals("META-INF/MANIFEST.MF")) {
										try (InputStream manifestInput = tempZipFile.getInputStream(tempEntry);
											InputStreamReader inputReader = new InputStreamReader(
												manifestInput, StandardCharsets.UTF_8);
											BufferedReader bufferReader = new BufferedReader(inputReader)) {

											while (bufferReader.ready()) {
												String line = bufferReader.readLine();

												String liferayVersionString = "Liferay-Versions:";

												if (line.startsWith(liferayVersionString)) {
													String versionRangeValue = line.substring(
														liferayVersionString.length());

													String projectTemplateName = entryName.substring(
														"com.liferay.project.templates.".length(),
														entryName.indexOf('-'));

													_projectTemplateVersionRangeMap.put(
														projectTemplateName, new VersionRange(versionRangeValue));

													break;
												}
											}
										}

										break;
									}
								}
							}

							tempFile.delete();
						}
					}
				}
			}
			catch (IOException ioe) {
			}
		}
	}

	private static Map<String, VersionRange> _projectTemplateVersionRangeMap = new HashMap<>();

	private FilteredListener<PropertyContentEvent> _listener;

}