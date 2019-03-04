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

package com.liferay.ide.portlet.vaadin.core.operation;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.portlet.core.operation.NewPortletClassDataModelProvider;
import com.liferay.ide.portlet.vaadin.core.VaadinCore;
import com.liferay.ide.sdk.core.ISDKConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.j2ee.common.CommonFactory;
import org.eclipse.jst.j2ee.common.ParamValue;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;

/**
 * @author Henri Sara
 * @author Cindy Li
 */
@SuppressWarnings({"restriction", "rawtypes", "unchecked"})
public class NewVaadinPortletClassDataModelProvider
	extends NewPortletClassDataModelProvider implements INewVaadinPortletClassDataModelProperties {

	public NewVaadinPortletClassDataModelProvider(boolean fragment) {
		super(fragment);
	}

	@Override
	public Object getDefaultProperty(String propertyName) {
		if (CLASS_NAME.equals(propertyName)) {
			return "NewVaadinPortletApplication";
		}
		else if (PORTLET_NAME.equals(propertyName) || LIFERAY_PORTLET_NAME.equals(propertyName)) {
			return _getPortletName().toLowerCase();
		}
		else if (DISPLAY_NAME.equals(propertyName) || TITLE.equals(propertyName) || SHORT_TITLE.equals(propertyName)) {
			return _getPortletName();
		}
		else if (CSS_CLASS_WRAPPER.equals(propertyName)) {
			return _getPortletName().toLowerCase() + ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX;
		}
		else if (SUPERCLASS.equals(propertyName)) {
			return QUALIFIED_VAADIN_APPLICATION;
		}
		else if (VAADIN_PORTLET_CLASS.equals(propertyName)) {
			return QUALIFIED_VAADIN_PORTLET;
		}
		else if (CREATE_JSPS.equals(propertyName)) {
			return false;
		}
		else if (CONSTRUCTOR.equals(propertyName)) {
			return false;
		}
		else if (SHOW_NEW_CLASS_OPTION.equals(propertyName)) {
			return false;
		}

		return super.getDefaultProperty(propertyName);
	}

	@Override
	public Set getPropertyNames() {
		Set propertyNames = super.getPropertyNames();

		propertyNames.add(VAADIN_PORTLET_CLASS);

		return propertyNames;
	}

	@Override
	public DataModelPropertyDescriptor[] getValidPropertyDescriptors(String propertyName) {
		if (SUPERCLASS.equals(propertyName)) {
			String[] vals = {QUALIFIED_VAADIN_APPLICATION};

			return DataModelPropertyDescriptor.createDescriptors(vals, vals);
		}
		else if (VAADIN_PORTLET_CLASS.equals(propertyName)) {
			String[] vals = {QUALIFIED_VAADIN_PORTLET};

			return DataModelPropertyDescriptor.createDescriptors(vals, vals);
		}

		return super.getValidPropertyDescriptors(propertyName);
	}

	@Override
	public boolean isPropertyEnabled(String propertyName) {
		if (CREATE_JSPS.equals(propertyName) || CREATE_JSPS_FOLDER.equals(propertyName)) {
			return false;
		}
		else if (EDIT_MODE.equals(propertyName) || HELP_MODE.equals(propertyName)) {
			return false;
		}

		return super.isPropertyEnabled(propertyName);
	}

	@Override
	public IStatus validate(String propertyName) {

		// also accept the case where the superclass/portlet class does not exist (yet),
		// perform basic java validation

		if (SUPERCLASS.equals(propertyName)) {
			String superclass = getStringProperty(propertyName);

			if (CoreUtil.isNullOrEmpty(superclass)) {
				return VaadinCore.createErrorStatus(Msgs.specifyPortletSuperclass);
			}

			return JavaConventions.validateJavaTypeName(superclass, JavaCore.VERSION_1_5, JavaCore.VERSION_1_5);
		}

		if (VAADIN_PORTLET_CLASS.equals(propertyName)) {
			String vaadinPortletClass = getStringProperty(propertyName);

			if (CoreUtil.isNullOrEmpty(vaadinPortletClass)) {
				return VaadinCore.createErrorStatus(Msgs.specifyVaadinPortletClass);
			}

			return JavaConventions.validateJavaTypeName(vaadinPortletClass, JavaCore.VERSION_1_5, JavaCore.VERSION_1_5);
		}

		return super.validate(propertyName);
	}

	@Override
	protected Object getInitParams() {
		List<ParamValue> initParams = new ArrayList<>();

		if (getStringProperty(VAADIN_PORTLET_CLASS).equals(QUALIFIED_VAADIN_PORTLET)) {
			ParamValue paramValue = CommonFactory.eINSTANCE.createParamValue();

			paramValue.setName("application");

			String pkg = getDataModel().getStringProperty(JAVA_PACKAGE);
			String cls = getDataModel().getStringProperty(CLASS_NAME);

			String qualifiedApplicationClass = ((pkg == null) || StringPool.EMPTY.equals(pkg)) ? cls : pkg + "." + cls;

			paramValue.setValue(qualifiedApplicationClass);

			initParams.add(paramValue);
		}

		return initParams;
	}

	private String _getPortletName() {
		String property = getProperty(CLASS_NAME).toString();

		return property.replaceAll("Application", StringPool.EMPTY);
	}

	private static class Msgs extends NLS {

		public static String specifyPortletSuperclass;
		public static String specifyVaadinPortletClass;

		static {
			initializeMessages(NewVaadinPortletClassDataModelProvider.class.getName(), Msgs.class);
		}

	}

}