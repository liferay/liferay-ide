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

import com.liferay.ide.swtbot.ui.tests.page.ButtonPO;
import com.liferay.ide.swtbot.ui.tests.page.DialogPO;

/**
 * @author Ying Xu
 */
public class DeleteRuntimeConfirmPO extends DialogPO
{

    private ButtonPO _ok;

    public DeleteRuntimeConfirmPO( SWTBot bot )
    {
        super( bot, BUTTON_CANCEL, BUTTON_OK );
        _ok = new ButtonPO( bot, BUTTON_OK );
    }

    public ButtonPO getOk()
    {
        return _ok;
    }

}
