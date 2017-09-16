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
import com.liferay.ide.swtbot.ui.page.ComboBox;
import com.liferay.ide.swtbot.ui.page.Dialog;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Tree;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Vicky Wang
 */
public class NewClassDialog extends Dialog
{

    private Button browseBtn;
    private Text className;
    private Button createBtn;
    private Text javaPackage;
    private Tree paths;
    private ComboBox superClasses;

    public NewClassDialog( SWTWorkbenchBot bot )
    {
        super( bot );

        className = new Text( bot, CLASSNAME );
        javaPackage = new Text( bot, JAVA_PACKAGE );
        createBtn = new Button( bot, CREATE );
        browseBtn = new Button( bot, BROWSE_WITH_DOT );
        paths = new Tree( bot );
        superClasses = new ComboBox( bot, SUPERCLASS );
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

    public Tree getPaths()
    {
        return paths;
    }

    public ComboBox getSuperClass()
    {
        return superClasses;
    }
}
