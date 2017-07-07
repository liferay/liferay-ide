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
import com.liferay.ide.swtbot.ui.tests.page.CheckBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.DialogPO;

/**
 * @author Li Lu
 */
public class DeleteResourcesDialog extends DialogPO implements UIBase
{

    private CheckBoxPO _deleteFromDiskCheckBox;

    public DeleteResourcesDialog( SWTBot bot )
    {
        super( bot, LABEL_DELETE_RESOURCE, BUTTON_CANCEL, BUTTON_OK );

        _deleteFromDiskCheckBox = new CheckBoxPO( bot, LABEL_DELETE_FROM_DISK );
    }

    public void confirmDeleteFromDisk()
    {
        _deleteFromDiskCheckBox.select();
    }

}
