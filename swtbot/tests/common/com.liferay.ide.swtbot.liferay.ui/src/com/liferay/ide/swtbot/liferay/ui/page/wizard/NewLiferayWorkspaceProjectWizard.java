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

package com.liferay.ide.swtbot.liferay.ui.page.wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.liferay.ui.ModuleWizardUI;
import com.liferay.ide.swtbot.ui.page.ComboBox;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

/**
 * @author Ying Xu
 * @author Ashley Xu
 */
public class NewLiferayWorkspaceProjectWizard extends Wizard implements ModuleWizardUI
{

    private ComboBox buildTypes;
    private Text workspaceName;

    public NewLiferayWorkspaceProjectWizard( SWTBot bot )
    {
        super( bot );

        workspaceName = new Text( bot, LABEL_WORKSPACE_NAME );
        buildTypes = new ComboBox( bot, LABEL_BUILD_TYPE );
    }

    public ComboBox getBuildTypes()
    {
        return buildTypes;
    }

    public Text getWorkspaceName()
    {
        return workspaceName;
    }

}
