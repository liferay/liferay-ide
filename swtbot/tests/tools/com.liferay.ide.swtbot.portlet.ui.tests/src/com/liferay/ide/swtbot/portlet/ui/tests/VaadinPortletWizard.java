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
public interface VaadinPortletWizard extends UIBase
{

    public final int INDEX_VAADIN_VALIDATION_MESSAGE1 = 3;
    public final int INDEX_VAADIN_VALIDATION_MESSAGE2 = 4;
    public final int INDEX_VAADIN_VALIDATION_MESSAGE3 = 6;
    public final String LABEL_APPLICATION_CLASS = "Application class:";
    public final String TEXT_MUST_SPECIFY_VAADIN_PORTLET_CLASS = " Must specify a vaadin portlet class.";

}
