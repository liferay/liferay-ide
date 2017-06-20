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

package com.liferay.ide.swtbot.server.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.server.ui.tests.ServerRuntimeWizard;
import com.liferay.ide.swtbot.ui.tests.page.ButtonPO;
import com.liferay.ide.swtbot.ui.tests.page.DialogPO;
import com.liferay.ide.swtbot.ui.tests.page.TablePO;

/**
 * @author Ying Xu
 */
public class DeleteRuntimePO extends DialogPO implements ServerRuntimeWizard
{

    private TablePO _serverRuntimeEnvironments;
    private ButtonPO _remove;

    public DeleteRuntimePO( SWTBot bot )
    {
        super( bot, BUTTON_CANCEL, BUTTON_OK );
        _serverRuntimeEnvironments = new TablePO( bot, LABEL_RUNTIEME_ENVIRONMENTS );
        _remove = new ButtonPO( bot, BUTTON_REMOVE );
    }

    public TablePO getServerRuntimeEnvironments()
    {
        return _serverRuntimeEnvironments;
    }

    public ButtonPO getRemove()
    {
        return _remove;
    }

}
