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

package com.liferay.ide.swtbot.project.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.project.ui.tests.NewLiferayModuleProjectWizard;
import com.liferay.ide.swtbot.ui.tests.page.ComboBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Ying Xu
 * @author Ashley Xu
 */
public class NewLiferayWorkspaceProjectWizardPO extends WizardPO implements NewLiferayModuleProjectWizard
{

    private TextPO _workspaceNameText;
    private ComboBoxPO _buildType;

    public NewLiferayWorkspaceProjectWizardPO( SWTBot bot )
    {
        super( bot, TEXT_BLANK, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT );
        _workspaceNameText = new TextPO( bot, LABEL_WORKSPACE_NAME );
        _buildType = new ComboBoxPO( bot, LABEL_BUILD_TYPE );
    }

    public void setWorkspaceNameText( String workspaceNameText )
    {
        _workspaceNameText.setText( workspaceNameText );
    }

    public ComboBoxPO get_buildType()
    {
        return _buildType;
    }

}
