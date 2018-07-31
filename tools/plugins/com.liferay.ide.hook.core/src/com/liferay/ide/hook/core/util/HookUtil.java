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

package com.liferay.ide.hook.core.util;

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.hook.core.HookCore;
import com.liferay.ide.hook.core.dd.HookDescriptorHelper;
import com.liferay.ide.hook.core.model.CustomJspDir;
import com.liferay.ide.hook.core.model.Hook;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.Value;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.ValManager;
import org.eclipse.wst.validation.internal.ValPrefManagerProject;
import org.eclipse.wst.validation.internal.ValidatorMutable;
import org.eclipse.wst.validation.internal.model.FilterGroup;
import org.eclipse.wst.validation.internal.model.FilterRule;
import org.eclipse.wst.validation.internal.model.ProjectPreferences;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class HookUtil {

	public static boolean configureJSPSyntaxValidationExclude(
		IProject project, IFolder customFolder, boolean configureRule) {

		boolean retval = false;

		try {
			ValManager valManager = ValManager.getDefault();

			Validator validator = valManager.getValidator(HookCore.VALIDATOR_ID, project);

			ValidatorMutable validatorTable = new ValidatorMutable(validator);

			// check for exclude group

			FilterGroup excludeGroup = null;

			for (FilterGroup group : validatorTable.getGroups()) {
				if (group.isExclude()) {
					excludeGroup = group;

					break;
				}
			}

			IPath customFolderPath = customFolder.getFullPath();

			IProject p = customFolder.getProject();

			IPath projectPath = p.getFullPath();

			IPath path = customFolderPath.makeRelativeTo(projectPath);

			String customJSPFolderPattern = path.toPortableString();

			FilterRule folderRule = FilterRule.createFile(customJSPFolderPattern, true, FilterRule.File.FileTypeFolder);

			if (excludeGroup == null) {
				if (configureRule) {
					excludeGroup = FilterGroup.create(true, new FilterRule[] {folderRule});

					validatorTable.add(excludeGroup);

					retval = true;
				}
			}
			else {
				boolean hasCustomJSPFolderRule = false;

				for (FilterRule rule : excludeGroup.getRules()) {
					if (customJSPFolderPattern.equals(rule.getPattern())) {
						if (configureRule) {
							FilterGroup newExcludeGroup = FilterGroup.removeRule(excludeGroup, rule);

							validatorTable.replaceFilterGroup(
								excludeGroup, FilterGroup.addRule(newExcludeGroup, folderRule));
						}

						hasCustomJSPFolderRule = true;

						break;
					}
				}

				if (!hasCustomJSPFolderRule && configureRule) {
					validatorTable.replaceFilterGroup(excludeGroup, FilterGroup.addRule(excludeGroup, folderRule));

					hasCustomJSPFolderRule = true;
				}

				retval = hasCustomJSPFolderRule;
			}

			if (configureRule) {
				ConfigurationManager configurationManager = ConfigurationManager.getManager();

				ProjectConfiguration pc = configurationManager.getProjectConfiguration(project);

				pc.setDoesProjectOverride(true);

				ProjectPreferences pp = new ProjectPreferences(project, true, false, null);

				ValPrefManagerProject vpm = new ValPrefManagerProject(project);

				ValidatorMutable[] validatorTables = {validatorTable};

				vpm.savePreferences(pp, validatorTables);
			}
		}
		catch (Exception e) {
			HookCore.logError("Unable to configure jsp syntax validation folder exclude rule.", e);
		}

		return retval;
	}

	public static IFolder getCustomJspFolder(Hook hook, IProject project) {
		ElementHandle<CustomJspDir> customJspDirHandle = hook.getCustomJspDir();

		CustomJspDir element = customJspDirHandle.content();

		if (element != null) {
			Value<org.eclipse.sapphire.modeling.Path> pathValue = element.getValue();

			if (!pathValue.empty()) {

				// IDE-110 IDE-648

				IWebProject webproject = LiferayCore.create(IWebProject.class, project);

				if ((webproject != null) && (webproject.getDefaultDocrootFolder() != null)) {
					IFolder defaultDocroot = webproject.getDefaultDocrootFolder();

					if (defaultDocroot != null) {
						org.eclipse.sapphire.modeling.Path customJspDir = pathValue.content();

						return defaultDocroot.getFolder(customJspDir.toPortableString());
					}
				}
			}
		}

		return null;
	}

	public static IPath getCustomJspPath(IProject project) {
		HookDescriptorHelper hookDescriptor = new HookDescriptorHelper(project);

		String customJSPFolder = hookDescriptor.getCustomJSPFolder(null);

		if (customJSPFolder != null) {
			IFolder docFolder = CoreUtil.getDefaultDocrootFolder(project);

			if (docFolder != null) {
				IPath newPath = Path.fromOSString(customJSPFolder);

				IPath pathValue = docFolder.getFullPath();

				pathValue = pathValue.append(newPath);

				return pathValue;
			}
		}

		return null;
	}

	/**
	 * A small utility method used to compute the DTD version
	 *
	 * @param document
	 *            - the document that is loaded by the editor
	 */
	public static String getDTDVersion(Document document) {
		String dtdVersion = null;
		DocumentType docType = document.getDoctype();

		if (docType != null) {
			String publicId = docType.getPublicId();
			String systemId = docType.getSystemId();

			if ((publicId != null) && (systemId != null)) {
				if (publicId.contains("6.0.0") || systemId.contains("6.0.0")) {
					dtdVersion = "6.0.0";
				}
				else if (publicId.contains("6.1.0") || systemId.contains("6.1.0")) {
					dtdVersion = "6.1.0";
				}
			}
		}

		return dtdVersion;
	}

}