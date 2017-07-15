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
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

/**
 * @author Ying Xu
 */
public class NewJavaPackageWizard extends Wizard implements UI
{

    private Text _name;
    private Text _sourceFolder;

    public NewJavaPackageWizard( SWTBot bot )
    {
        super( bot, TEXT_BLANK, CANCEL, FINISH, TEXT_BLANK, TEXT_BLANK );

        _sourceFolder = new Text( bot, LABEL_SOURCE_FOLDER );
        _name = new Text( bot, LABEL_NAME );
    }

    public Text getSourceFolderText()
    {
        return _sourceFolder;
    }

    public void setName( String packageName )
    {
        _name.setText( packageName );
    }

}
