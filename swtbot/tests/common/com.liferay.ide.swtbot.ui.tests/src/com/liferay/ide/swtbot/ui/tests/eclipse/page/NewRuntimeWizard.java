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
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.TreePO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Terry Jia
 */
public class NewRuntimeWizard extends WizardPO implements UIBase
{

    private TextPO _search;
    private TreePO _serverTypes;

    public NewRuntimeWizard( SWTBot bot )
    {
        this( bot, TITLE_NEW_SERVER_RUNTIME_ENVIRONMENT, SPECIFY_PORTAL_BUNDLE_LOCATION_INDEX );
    }

    public NewRuntimeWizard( SWTBot bot, String title, int validationMessageIndex )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, validationMessageIndex );

        _search = new TextPO( bot );
        _serverTypes = new TreePO( bot );
    }

    public void search( String key )
    {
        _search.setText( key );
    }

    public void selectServerType( String categgory, String serverType )
    {
        _serverTypes.getTreeItem( categgory ).getTreeItem( serverType ).select();
    }

}
