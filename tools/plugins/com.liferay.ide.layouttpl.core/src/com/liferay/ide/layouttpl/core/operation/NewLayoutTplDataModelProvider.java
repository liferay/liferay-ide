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

package com.liferay.ide.layouttpl.core.operation;

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.layouttpl.core.LayoutTplCore;

import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.internal.operation.ArtifactEditOperationDataModelProvider;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings({"restriction", "unchecked", "rawtypes"})
public class NewLayoutTplDataModelProvider
	extends ArtifactEditOperationDataModelProvider implements INewLayoutTplDataModelProperties {

	public NewLayoutTplDataModelProvider() {
	}

	@Override
	public Object getDefaultProperty(String propertyName) {
		if (LAYOUT_TEMPLATE_NAME.equals(propertyName)) {
			return "New Template";
		}
		else if (LAYOUT_TEMPLATE_ID.equals(propertyName)) {
			String name = getStringProperty(LAYOUT_TEMPLATE_NAME);

			if (!CoreUtil.isNullOrEmpty(name)) {
				return name.replaceAll("[^a-zA-Z0-9]+", StringPool.EMPTY).toLowerCase();
			}
		}
		else if (LAYOUT_TEMPLATE_FILE.equals(propertyName)) {
			return "/" + getStringProperty(LAYOUT_TEMPLATE_ID) + ".tpl";
		}
		else if (LAYOUT_WAP_TEMPLATE_FILE.equals(propertyName)) {
			return "/" + getStringProperty(LAYOUT_TEMPLATE_ID) + ".wap.tpl";
		}
		else if (LAYOUT_THUMBNAIL_FILE.equals(propertyName)) {
			return "/" + getStringProperty(LAYOUT_TEMPLATE_ID) + ".png";
		}
		else if (LAYOUT_IMAGE_1_COLUMN.equals(propertyName)) {
			return true;
		}
		else if (LAYOUT_IMAGE_1_2_1_COLUMN.equals(propertyName) || LAYOUT_IMAGE_1_2_I_COLUMN.equals(propertyName) ||
				 LAYOUT_IMAGE_1_2_II_COLUMN.equals(propertyName) ||
				 LAYOUT_IMAGE_2_2_COLUMN.equals(
					 propertyName
						 ) ||
				 LAYOUT_IMAGE_2_I_COLUMN.equals(propertyName) || LAYOUT_IMAGE_2_II_COLUMN.equals(propertyName) ||
				 LAYOUT_IMAGE_2_III_COLUMN.equals(propertyName) || LAYOUT_IMAGE_3_COLUMN.equals(propertyName)) {

			return false;
		}

		return super.getDefaultProperty(propertyName);
	}

	@Override
	public Set getPropertyNames() {
		Set propertyNames = super.getPropertyNames();

		propertyNames.add(LAYOUT_TEMPLATE_NAME);
		propertyNames.add(LAYOUT_TEMPLATE_ID);
		propertyNames.add(LAYOUT_TEMPLATE_FILE);
		propertyNames.add(LAYOUT_WAP_TEMPLATE_FILE);
		propertyNames.add(LAYOUT_THUMBNAIL_FILE);

		propertyNames.add(LAYOUT_IMAGE_1_2_1_COLUMN);
		propertyNames.add(LAYOUT_IMAGE_1_2_I_COLUMN);
		propertyNames.add(LAYOUT_IMAGE_1_2_II_COLUMN);
		propertyNames.add(LAYOUT_IMAGE_1_COLUMN);
		propertyNames.add(LAYOUT_IMAGE_2_2_COLUMN);
		propertyNames.add(LAYOUT_IMAGE_2_I_COLUMN);
		propertyNames.add(LAYOUT_IMAGE_2_II_COLUMN);
		propertyNames.add(LAYOUT_IMAGE_2_III_COLUMN);
		propertyNames.add(LAYOUT_IMAGE_3_COLUMN);

		propertyNames.add(LAYOUT_TPL_FILE_CREATED);

		return propertyNames;
	}

	@Override
	public boolean propertySet(String propertyName, Object propertyValue) {
		boolean layoutOption = false;

		for (int i = 0; i < LAYOUT_PROPERTIES.length; i++) {
			if (LAYOUT_PROPERTIES[i].equals(propertyName)) {
				layoutOption = true;
				break;
			}
		}

		if (layoutOption && !ignoreLayoutOptionPropertySet) {
			ignoreLayoutOptionPropertySet = true;

			for (int i = 0; i < LAYOUT_PROPERTIES.length; i++) {
				setBooleanProperty(LAYOUT_PROPERTIES[i], false);
			}

			setProperty(propertyName, propertyValue);

			ignoreLayoutOptionPropertySet = false;
		}

		return super.propertySet(propertyName, propertyValue);
	}

	@Override
	public IStatus validate(String propertyName) {
		if (LAYOUT_TEMPLATE_ID.equals(propertyName)) {

			// first check to see if an existing property exists.

			LayoutTplDescriptorHelper helper = new LayoutTplDescriptorHelper(getTargetProject());

			if (helper.hasTemplateId(getStringProperty(propertyName))) {
				return LayoutTplCore.createErrorStatus(Msgs.templateIdExists);
			}

			// to avoid marking text like "this" as bad add a z to the end of the string

			String idValue = getStringProperty(propertyName) + "z";

			if (CoreUtil.isNullOrEmpty(idValue)) {
				return super.validate(propertyName);
			}

			IStatus status = JavaConventions.validateFieldName(
				idValue, CompilerOptions.VERSION_1_7, CompilerOptions.VERSION_1_7);

			if (!status.isOK()) {
				return LayoutTplCore.createErrorStatus(Msgs.templateIdInvalid);
			}

			String idText = getStringProperty(propertyName);

			if (CoreUtil.isNullOrEmpty(idText)) {
				return LayoutTplCore.createErrorStatus("Id can't be empty.");
			}
		}
		else if (LAYOUT_TEMPLATE_FILE.equals(propertyName)) {
			String filename = getStringProperty(propertyName);

			if (!_checkoutDocrootFileNameCorrect(filename, "tpl")) {
				return LayoutTplCore.createErrorStatus("Template file name is invalid.");
			}

			IPath filePath = new Path(getStringProperty(LAYOUT_TEMPLATE_FILE));

			if (_checkDocrootFileExists(filePath)) {
				return LayoutTplCore.createWarningStatus(Msgs.templateFileExists);
			}
		}
		else if (LAYOUT_WAP_TEMPLATE_FILE.equals(propertyName)) {
			String filename = getStringProperty(propertyName);

			if (!_checkoutDocrootFileNameCorrect(filename, "tpl")) {
				return LayoutTplCore.createErrorStatus("WAP template file name is invalid.");
			}

			IPath filePath = new Path(getStringProperty(LAYOUT_WAP_TEMPLATE_FILE));

			if (_checkDocrootFileExists(filePath)) {
				return LayoutTplCore.createWarningStatus(Msgs.wapTemplateFileExists);
			}
		}
		else if (LAYOUT_THUMBNAIL_FILE.equals(propertyName)) {
			String filename = getStringProperty(propertyName);

			if (!_checkoutDocrootFileNameCorrect(filename, "")) {
				return LayoutTplCore.createErrorStatus("Thumbnail file name is invalid.");
			}

			IPath filePath = new Path(getStringProperty(LAYOUT_THUMBNAIL_FILE));

			if (_checkDocrootFileExists(filePath)) {
				return LayoutTplCore.createWarningStatus(Msgs.thumbnailFileExists);
			}
		}

		return super.validate(propertyName);
	}

	protected boolean ignoreLayoutOptionPropertySet = false;

	private boolean _checkDocrootFileExists(IPath path) {
		IWebProject webproject = LiferayCore.create(IWebProject.class, getTargetProject());

		if (webproject != null) {
			IFolder defaultDocroot = webproject.getDefaultDocrootFolder();

			if ((defaultDocroot != null) && defaultDocroot.exists(path)) {
				return true;
			}

			return false;
		}

		return false;
	}

	private boolean _checkoutDocrootFileNameCorrect(String filename, String type) {
		int firstindex = filename.indexOf("/");
		int lastindex = filename.lastIndexOf("." + type);

		String filetype = filename.substring(lastindex + 1);

		if (lastindex != -1) {
			if (firstindex != -1) {
				filename = filename.substring(firstindex + 1, lastindex);
			}
			else {
				filename = filename.substring(0, lastindex);
			}

			if (!CoreUtil.isNullOrEmpty(filename) && !filename.startsWith("/") && !filename.startsWith(".") &&
				!CoreUtil.isNullOrEmpty(filetype)) {

				return true;
			}
		}

		return false;
	}

	private static class Msgs extends NLS {

		public static String templateFileExists;
		public static String templateIdExists;
		public static String templateIdInvalid;
		public static String thumbnailFileExists;
		public static String wapTemplateFileExists;

		static {
			initializeMessages(NewLayoutTplDataModelProvider.class.getName(), Msgs.class);
		}

	}

}