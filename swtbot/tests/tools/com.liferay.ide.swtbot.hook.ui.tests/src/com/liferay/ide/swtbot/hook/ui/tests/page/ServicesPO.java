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
import com.liferay.ide.swtbot.ui.tests.page.TablePO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Vicky Wang
 */
public class ServicesPO extends WizardPO implements HookConfigurationWizard, ProjectWizard
{

    private ButtonPO _addButton;
    private DialogPO _addServiceDialog;
    private DialogPO _addServiceWrapperDialog;
    private TablePO _definePortalServicesTable;
    private ButtonPO _editButton;
    private ButtonPO _removeButton;

    public ServicesPO( SWTBot bot, int indexPortalPropertiesValidationMessage )
    {
        this( bot, TEXT_BLANK, indexPortalPropertiesValidationMessage );
    }

    public ServicesPO( SWTBot bot, String title, int indexPortalPropertiesValidationMessage )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, indexPortalPropertiesValidationMessage );

        _addServiceDialog = new DialogPO( bot, WINDOW_ADD_SERVICE, BUTTON_CANCEL, BUTTON_OK );
        _addServiceWrapperDialog = new DialogPO( bot, WINDOW_ADD_SERVICE_WRAPPER, BUTTON_CANCEL, BUTTON_OK );
        _definePortalServicesTable = new TablePO( bot, LABLE_DEFINE_PORTAL_SERVICES );

        _addButton = new ButtonPO( bot, BUTTON_ADD );
        _editButton = new ButtonPO( bot, BUTTON_EDIT );
        _removeButton = new ButtonPO( bot, BUTTON_REMOVE_WITH_DOT );
    }

    public ButtonPO getAddButton()
    {
        return _addButton;
    }

    public DialogPO getAddServiceWrapper()
    {
        return _addServiceWrapperDialog;
    }

    public TablePO getDefinePortalServices()
    {
        return _definePortalServicesTable;
    }

    public ButtonPO getEditButton()
    {
        return _editButton;
    }

    public ButtonPO getRemoveButton()
    {
        return _removeButton;
    }

    public DialogPO getAddService()
    {
        return _addServiceDialog;
    }

}