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

package com.liferay.ide.swtbot.hook.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.hook.ui.tests.HookConfigurationWizard;
import com.liferay.ide.swtbot.project.ui.tests.ProjectWizard;
import com.liferay.ide.swtbot.ui.tests.page.ButtonPO;
import com.liferay.ide.swtbot.ui.tests.page.ComboBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.DialogPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.TreePO;

/**
 * @author Vicky Wang
 */
public class NewClassPO extends DialogPO implements HookConfigurationWizard, ProjectWizard
{

    private TextPO _classNameText;
    private TextPO _javaPackageText;
    private ButtonPO _createButton;
    private ButtonPO _browseButton;
    private TreePO _pathTree;
    private ComboBoxPO _superClass;

    public void select( String... items )
    {
        _pathTree.selectTreeItem( items );
    }

    public NewClassPO( SWTBot bot )
    {
        this( bot, TEXT_BLANK );
    }

    public NewClassPO( SWTBot bot, String title )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_OK );

        _classNameText = new TextPO( bot, LABLE_CLASS_NAME );
        _javaPackageText = new TextPO( bot, LABLE_JAVA_PACKAGE );
        _createButton = new ButtonPO( bot, BUTTON_CREATE );
        _browseButton = new ButtonPO( bot, BUTTON_BROWSE_WITH_DOT );
        _pathTree = new TreePO( bot );
        _superClass = new ComboBoxPO( bot, COMBOBOX_SUPERCLASS );
    }

    public ComboBoxPO getSuperClass()
    {
        return _superClass;
    }

    public void setSuperClass( String superClass )
    {
        this._superClass.setText( superClass );;
    }

    public ButtonPO getCreateButton()
    {
        return _createButton;
    }

    public void setCreateButton( ButtonPO createButton )
    {
        _createButton = createButton;
    }

    public void setJavaPackage( String text )
    {
        _javaPackageText.setText( text );
    }

    public void setClassName( String text )
    {
        _classNameText.setText( text );
    }

    public ButtonPO getBrowseButton()
    {
        return _browseButton;
    }

}
