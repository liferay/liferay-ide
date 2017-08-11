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

package com.liferay.ide.swtbot.ui.eclipse.page;

import com.liferay.ide.swtbot.ui.page.Dialog;
import com.liferay.ide.swtbot.ui.page.Tree;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Terry Jia
 */
public class PreferencesDialog extends Dialog
{

    private Tree preferencesTypes;

    public PreferencesDialog( SWTBot bot )
    {
        super( bot, "Preferences" );

        preferencesTypes = new Tree( bot );
    }

    public void selectPreferencesType( String categroy, String item )
    {
        preferencesTypes.getTreeItem( categroy ).expand();

        preferencesTypes.getTreeItem( categroy ).getTreeItem( item ).select();
    }

    public ServerRuntimeEnvironmentsPreferencesDialog selectServerRuntimeEnvironmentsPage()
    {
        selectPreferencesType( "Server", "Runtime Environments" );

        return new ServerRuntimeEnvironmentsPreferencesDialog( bot );
    }

}
