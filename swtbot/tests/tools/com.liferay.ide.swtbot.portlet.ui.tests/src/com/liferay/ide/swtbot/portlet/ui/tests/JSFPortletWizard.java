/*******************************************************************************
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
 *
 *******************************************************************************/

package com.liferay.ide.swtbot.portlet.ui.tests;

import com.liferay.ide.swtbot.ui.tests.UIBase;

/**
 * @author Li Lu
 */
public interface JSFPortletWizard extends UIBase
{

    final String CREATE_VIRW_FILES = "Create view files";
    final int INDEX__JSF_VALIDATION_MESSAGE1 = 2;

    final int INDEX_JSF_VALIDATION_MESSAGE2 = 4;
    final int INDEX_JSF_VALIDATION_MESSAGE3 = 6;
    final String LABEL_ICE_FACES = "ICEfaces";
    final String LABEL_LIFERAY_FACES_ALLOY = "Liferay Faces Alloy";
    final String LABEL_PRIME_FACES = "PrimeFaces";

    final String LABEL_RICH_FACES = "RichFaces";
    final String LABEL_STANDARD_JSF = "Standard JSF";
    final String LABEL_VIEW_FOLDER = "View folder:";

    final String PORTLET_CLASS_DEFAULT_VALUE = "javax.portlet.faces.GenericFacesPortlet";
    final String TEXT_JSP_FOLDER_CANOT_BE_EMPTY = " JSP folder cannot be empty.";
    final String TEXT_MUST_BE_VALID_PORTLET_CLASS = " JSF portlet class must be a valid portlet class.";

    final String TEXT_MUST_SPECIFY_JSF_PORTLET_CLASS = " Must specify a JSF portlet class.";
    final String TEXT_VIEW_FILE_OEVERWRITTEN = " View file already exists and will be overwritten.";

    final String TEXT_VIEWS_SHOULD_IN_WEB_INF_FOLDER = " The views should be generated in the WEB-INF folder.";

}
