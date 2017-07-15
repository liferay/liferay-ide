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

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.ui.UI;
import com.liferay.ide.swtbot.ui.page.Button;
import com.liferay.ide.swtbot.ui.page.Dialog;
import com.liferay.ide.swtbot.ui.page.Table;

/**
 * @author Terry Jia
 */
public class ServerRuntimeEnvironmentsPreferencesDialog extends Dialog implements UI
{

    private Table _runtimes;

    private Button _add;
    private Button _edit;
    private Button _remove;

    public ServerRuntimeEnvironmentsPreferencesDialog( SWTBot bot )
    {
        super( bot, "Preferences" );

        _runtimes = new Table( bot, "Server runtime environments:" );

        _add = new Button( bot, "Add..." );
        _edit = new Button( bot, "Edit..." );
        _remove = new Button( bot, "Remove" );
    }

    public void selectRuntime( String runtimeName )
    {
        _runtimes.click( runtimeName );
    }

    public Button getAddButton()
    {
        return _add;
    }

    public Button getEditButton()
    {
        return _edit;
    }

    public Button getRemoveButton()
    {
        return _remove;
    }

}
