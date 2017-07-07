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

package com.liferay.ide.swtbot.ui.tests.eclipse.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.ui.tests.UIBase;
import com.liferay.ide.swtbot.ui.tests.page.DialogPO;
import com.liferay.ide.swtbot.ui.tests.page.TreePO;

/**
 * @author Terry Jia
 */
public class PreferencesDialog extends DialogPO implements UIBase
{

    private TreePO _preferencesTypes;

    public PreferencesDialog( SWTBot bot )
    {
        super( bot, "Preferences", BUTTON_CANCEL, BUTTON_OK );

        _preferencesTypes = new TreePO( bot );
    }

    public void selectPreferencesType( String categroy, String item )
    {
        _preferencesTypes.getTreeItem( categroy ).expand();

        _preferencesTypes.getTreeItem( categroy ).getTreeItem( item ).select();
    }

    public ServerRuntimeEnvironmentsPreferencesPage selectServerRuntimeEnvironmentsPage()
    {
        selectPreferencesType( "Server", "Runtime Environments" );

        return new ServerRuntimeEnvironmentsPreferencesPage( bot );
    }

}
