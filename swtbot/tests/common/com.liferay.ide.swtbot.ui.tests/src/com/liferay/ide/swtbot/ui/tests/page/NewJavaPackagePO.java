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

package com.liferay.ide.swtbot.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.ui.tests.UIBase;

/**
 * @author Ying Xu
 */
public class NewJavaPackagePO extends WizardPO implements UIBase
{

    private TextPO _name;
    private TextPO _sourceFolder;

    public NewJavaPackagePO( SWTBot bot )
    {
        this( bot, TEXT_BLANK, BUTTON_CANCEL, BUTTON_FINISH );
    }

    public NewJavaPackagePO( SWTBot bot, String title, String cancelButtonText, String finishButtonText )
    {
        super( bot, title, cancelButtonText, finishButtonText, TEXT_BLANK, TEXT_BLANK );
        _sourceFolder = new TextPO( bot, LABEL_SOURCE_FOLDER );
        _name = new TextPO( bot, LABEL_NAME );

    }

    public TextPO getSourceFolderText()
    {
        return _sourceFolder;
    }

    public void setName( String packageName )
    {
        _name.setText( packageName );
    }

}
