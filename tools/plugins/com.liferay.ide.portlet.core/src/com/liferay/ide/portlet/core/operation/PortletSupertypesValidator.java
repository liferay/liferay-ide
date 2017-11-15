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

package com.liferay.ide.portlet.core.operation;

import org.eclipse.jst.j2ee.internal.web.operations.AbstractSupertypesValidator;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class PortletSupertypesValidator extends AbstractSupertypesValidator {

	public static boolean isGenericPortletSuperclass(IDataModel dataModel) {
		return isGenericPortletSuperclass(dataModel, false);
	}

	public static boolean isGenericPortletSuperclass(IDataModel dataModel, boolean checkHierarchy) {
		if (INewPortletClassDataModelProperties.QUALIFIED_GENERIC_PORTLET.equals(getSuperclass(dataModel))) {
			return true;
		}

		if (checkHierarchy &&
			hasSuperclass(
				dataModel, getSuperclass(dataModel), INewPortletClassDataModelProperties.QUALIFIED_GENERIC_PORTLET)) {

			return true;
		}

		return false;
	}

	public static boolean isLiferayPortletSuperclass(IDataModel dataModel) {
		return isLiferayPortletSuperclass(dataModel, false);
	}

	public static boolean isLiferayPortletSuperclass(IDataModel dataModel, boolean checkHierarchy) {
		if (INewPortletClassDataModelProperties.QUALIFIED_LIFERAY_PORTLET.equals(getSuperclass(dataModel))) {
			return true;
		}

		if (checkHierarchy &&
			hasSuperclass(
				dataModel, getSuperclass(dataModel), INewPortletClassDataModelProperties.QUALIFIED_LIFERAY_PORTLET)) {

			return true;
		}

		return false;
	}

	public static boolean isMVCPortletSuperclass(IDataModel dataModel) {
		return isMVCPortletSuperclass(dataModel, false);
	}

	public static boolean isMVCPortletSuperclass(IDataModel dataModel, boolean checkHierarchy) {
		if (INewPortletClassDataModelProperties.QUALIFIED_MVC_PORTLET.equals(getSuperclass(dataModel))) {
			return true;
		}

		if (checkHierarchy &&
			hasSuperclass(
				dataModel, getSuperclass(dataModel), INewPortletClassDataModelProperties.QUALIFIED_MVC_PORTLET)) {

			return true;
		}

		return false;
	}

}