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

import com.liferay.ide.swtbot.ui.page.Button;
import com.liferay.ide.swtbot.ui.page.Dialog;
import com.liferay.ide.swtbot.ui.page.Table;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Terry Jia
 */
public class ServerRuntimeEnvironmentsPreferencesDialog extends Dialog
{

    private Button addBtn;
    private Button editBtn;
    private Button removeBtn;
    private Table runtimes;

    public ServerRuntimeEnvironmentsPreferencesDialog( SWTBot bot )
    {
        super( bot, "Preferences" );

        runtimes = new Table( bot, "Server runtime environments:" );

        addBtn = new Button( bot, "Add..." );
        editBtn = new Button( bot, "Edit..." );
        removeBtn = new Button( bot, "Remove" );
    }

    public Button getAddBtn()
    {
        return addBtn;
    }

    public Button getEditBtn()
    {
        return editBtn;
    }

    public Button getRemoveBtn()
    {
        return removeBtn;
    }

    public Table getRuntimes()
    {
        return runtimes;
    }

}
