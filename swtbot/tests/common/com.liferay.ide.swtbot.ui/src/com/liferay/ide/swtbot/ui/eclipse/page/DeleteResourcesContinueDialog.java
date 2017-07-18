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

/**
 * @author Ying Xu
 */
public class DeleteResourcesContinueDialog extends Dialog implements UI
{

    private Button continueBtn;

    public DeleteResourcesContinueDialog( SWTBot bot, String title )
    {
        super( bot, title );

        continueBtn = new Button( bot, CONTINUE );
    }

    public Button getContinueBtn()
    {
        return continueBtn;
    }

}
