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
import com.liferay.ide.swtbot.ui.tests.page.ButtonPO;
import com.liferay.ide.swtbot.ui.tests.page.DialogPO;
import com.liferay.ide.swtbot.ui.tests.page.TablePO;

/**
 * @author Terry Jia
 */
public class ServerRuntimeEnvironmentsPreferencesPage extends DialogPO implements UIBase
{

    private TablePO _runtimes;

    private ButtonPO _add;
    private ButtonPO _edit;
    private ButtonPO _remove;

    public ServerRuntimeEnvironmentsPreferencesPage( SWTBot bot )
    {
        super( bot, "Preferences", BUTTON_CANCEL, BUTTON_OK );

        _runtimes = new TablePO( bot, "Server runtime environments:" );

        _add = new ButtonPO( bot, "Add..." );
        _edit = new ButtonPO( bot, "Edit..." );
        _remove = new ButtonPO( bot, "Remove" );
    }

    public void selectRuntime( String runtimeName )
    {
        _runtimes.click( runtimeName );
    }

    public ButtonPO getAddButton()
    {
        return _add;
    }

    public ButtonPO getEditButton()
    {
        return _edit;
    }

    public ButtonPO getRemoveButton()
    {
        return _remove;
    }

}
