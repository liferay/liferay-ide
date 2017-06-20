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
import com.liferay.ide.swtbot.ui.tests.page.DialogPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;

/**
 * @author Vicky Wang
 */
public class NewImplClassPO extends DialogPO implements HookConfigurationWizard, ProjectWizard
{

    private ButtonPO _createButton;
    private TextPO _javaPackageText;
    private TextPO _classname;
    private ButtonPO _browseButton;

    public NewImplClassPO( SWTBot bot )
    {
        this( bot, TEXT_BLANK );
    }

    public NewImplClassPO( SWTBot bot, String title )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_OK );

        _javaPackageText = new TextPO( bot, LABLE_JAVA_PACKAGE );
        _createButton = new ButtonPO( bot, BUTTON_CREATE );
        _classname = new TextPO( bot, LABLE_CLASS_NAME );
        _browseButton = new ButtonPO( bot, BUTTON_BROWSE_WITH_DOT );
    }

    public ButtonPO getCreateButton()
    {
        return _createButton;
    }

    public void setJavaPackage( String text )
    {
        _javaPackageText.setText( text );
    }

    public TextPO getJavaPackage()
    {
        return _javaPackageText;
    }

    public TextPO getClassname()
    {
        return _classname;
    }

    public void setClassname( String classname )
    {
        this._classname.setText( classname );
    }

    public ButtonPO getBrowseButton()
    {
        return _browseButton;
    }

}
