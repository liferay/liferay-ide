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

package com.liferay.ide.portlet.ui.tests.page;

import com.liferay.ide.portlet.ui.tests.LiferayPortletWizard;
import com.liferay.ide.ui.tests.swtbot.page.AbstractSelectionPageObject;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Ashley Yuan
 */
public class CssSelectionPageObject extends AbstractSelectionPageObject<SWTBot> implements LiferayPortletWizard
{

    public CssSelectionPageObject( SWTBot bot, String title, int labelIndex )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_OK, labelIndex );
    }

}
