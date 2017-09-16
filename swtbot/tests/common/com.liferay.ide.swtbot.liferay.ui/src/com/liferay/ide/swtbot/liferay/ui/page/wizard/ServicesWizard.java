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

package com.liferay.ide.swtbot.liferay.ui.page.wizard;

import com.liferay.ide.swtbot.ui.page.Button;
import com.liferay.ide.swtbot.ui.page.Dialog;
import com.liferay.ide.swtbot.ui.page.Table;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Vicky Wang
 */
public class ServicesWizard extends Wizard
{

    private Button addBtn;
    private Dialog addServiceDialog;
    private Dialog addServiceWrapperDialog;
    private Table definePortalServices;
    private Button editBtn;
    private Button removeBtn;

    public ServicesWizard( SWTWorkbenchBot bot )
    {
        super( bot, 0 );

        addServiceDialog = new Dialog( bot, ADD_SERVICE );
        addServiceWrapperDialog = new Dialog( bot, ADD_SERVICE_WRAPPER );
        definePortalServices = new Table( bot, DEFINE_PORTAL_SERVICES_TO_EXTEND );
        addBtn = new Button( bot, ADD_WITH_DOT );
        editBtn = new Button( bot, EDIT_WITH_DOT );
        removeBtn = new Button( bot, REMOVE_WITH_DOT );
    }

    public Button getAddBtn()
    {
        return addBtn;
    }

    public Dialog getAddServiceDialog()
    {
        return addServiceDialog;
    }

    public Dialog getAddServiceWrapperDialog()
    {
        return addServiceWrapperDialog;
    }

    public Table getDefinePortalServices()
    {
        return definePortalServices;
    }

    public Button getEditBtn()
    {
        return editBtn;
    }

    public Button getRemoveBtn()
    {
        return removeBtn;
    }

}
