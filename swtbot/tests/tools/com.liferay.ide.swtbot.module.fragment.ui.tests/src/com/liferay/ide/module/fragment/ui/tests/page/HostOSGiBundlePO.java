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

package com.liferay.ide.swtbot.module.fragment.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;
import com.liferay.ide.swtbot.module.fragment.tests.ModuleFragmentProjectWizard;
import com.liferay.ide.swtbot.project.ui.tests.ProjectWizard;
import com.liferay.ide.swtbot.ui.tests.swtbot.page.DialogPO;
import com.liferay.ide.swtbot.ui.tests.swtbot.page.TextPO;

/**
 * @author Vicky Wang
 */
public class HostOSGiBundlePO extends DialogPO implements ModuleFragmentProjectWizard, ProjectWizard
{

    private TextPO _osgiBundleText;

    public HostOSGiBundlePO( SWTBot bot )
    {
        super( bot, BUTTON_CANCEL, BUTTON_OK );

        _osgiBundleText = new TextPO( bot, LABLE_SELECT_OSGI_BUNDLE );

    }

    public void setOSGiBundle( String text )
    {
        _osgiBundleText.setText( text );
    }

}
