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

package com.liferay.ide.swtbot.liferay.ui.action;

import com.liferay.ide.swtbot.liferay.ui.UIAction;
import com.liferay.ide.swtbot.ui.eclipse.page.TextDialog;
import com.liferay.ide.swtbot.ui.eclipse.page.TreeDialog;
import com.liferay.ide.swtbot.ui.page.Dialog;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public class DialogAction extends UIAction
{

    Dialog dialog = new Dialog( bot );
    TextDialog textDialog = new TextDialog( bot );
    TreeDialog treeDialog = new TreeDialog( bot );

    public DialogAction( SWTWorkbenchBot bot )
    {
        super( bot );
    }

    public void confirm()
    {
        dialog.confirm();
    }

    public void prepareText( String text )
    {
        textDialog.getText().setText( text );
    }

    public void selectItem( String item )
    {
        treeDialog.getItems().select( item );
    }

    public void selectItems( String... items )
    {
        treeDialog.getItems().select( items );
    }

}
