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

package com.liferay.ide.swtbot.liferay.ui.page.dialog;

import com.liferay.ide.swtbot.ui.page.Button;
import com.liferay.ide.swtbot.ui.page.Dialog;
import com.liferay.ide.swtbot.ui.page.Text;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Vicky Wang
 */
public class NewImplClassDialog extends Dialog
{

    private Button browseBtn;
    private Text className;
    private Button createBtn;
    private Text javaPackage;

    public NewImplClassDialog( SWTWorkbenchBot bot )
    {
        super( bot );

        javaPackage = new Text( bot, JAVA_PACKAGE );
        createBtn = new Button( bot, CREATE );
        className = new Text( bot, CLASSNAME );
        browseBtn = new Button( bot, BROWSE_WITH_DOT );
    }

    public Button getBrowseBtn()
    {
        return browseBtn;
    }

    public Text getClassName()
    {
        return className;
    }

    public Button getCreateBtn()
    {
        return createBtn;
    }

    public Text getJavaPackage()
    {
        return javaPackage;
    }

}
