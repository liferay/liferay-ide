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
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Vicky Wang
 */
public class PortalPropertiesPO extends WizardPO implements HookConfigurationWizard, ProjectWizard
{

    private ButtonPO _eventAddButton;
    private ButtonPO _eventEditButton;
    private ButtonPO _eventRemoveButton;
    private ButtonPO _browseButton;
    private ButtonPO _propertyAddButton;
    private ButtonPO _propertyEditButton;
    private ButtonPO _propertyRemoveButton;
    private DialogPO _addEventActionDialog;
    private DialogPO _addPropertyOverrideDialog;
    private DialogPO _newLiferayHookConfigurationDialog;
    private TablePO _defineActionsOnPortalEventsTable;
    private TablePO _specifyPropertiesToOverrideTable;
    private TextPO _portalPropertiesFileText;

    public PortalPropertiesPO( SWTBot bot, int indexPortalPropertiesValidationMessage )
    {
        this( bot, TEXT_BLANK, indexPortalPropertiesValidationMessage );
    }

    public PortalPropertiesPO( SWTBot bot, String title, int indexPortalPropertiesValidationMessage )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, indexPortalPropertiesValidationMessage );

        _portalPropertiesFileText = new TextPO( bot, LABLE_PORTAL_PROPERTIES_FILE );
        _defineActionsOnPortalEventsTable = new TablePO( bot, LABLE_DEFINE_ACTIONS );
        _specifyPropertiesToOverrideTable = new TablePO( bot, LABLE_SPECIFY_PROPERTIES );
        _addEventActionDialog = new DialogPO( bot, WINDOW_ADD_EVENT_ACTION, BUTTON_CANCEL, BUTTON_OK );
        _addPropertyOverrideDialog = new DialogPO( bot, WINDOW_ADD_PROPERTY_OVERRIDE, BUTTON_CANCEL, BUTTON_OK );
        _newLiferayHookConfigurationDialog = new DialogPO( bot, TITLE_NEW_LIFERAY_HOOK, BUTTON_BACK, BUTTON_NEXT );
        _browseButton = new ButtonPO( bot, BUTTON_BROWSE_WITH_DOT, 0 );
        _eventAddButton = new ButtonPO( bot, BUTTON_ADD, 0 );
        _eventEditButton = new ButtonPO( bot, BUTTON_EDIT, 0 );
        _eventRemoveButton = new ButtonPO( bot, BUTTON_REMOVE_WITH_DOT, 0 );
        _propertyAddButton = new ButtonPO( bot, BUTTON_ADD, 1 );
        _propertyEditButton = new ButtonPO( bot, BUTTON_EDIT, 1 );
        _propertyRemoveButton = new ButtonPO( bot, BUTTON_REMOVE_WITH_DOT, 1 );
    }

    public ButtonPO getBrowseButton()
    {
        return _browseButton;
    }

    public ButtonPO getEventAddButton()
    {
        return _eventAddButton;
    }

    public ButtonPO getEventEditButton()
    {
        return _eventEditButton;
    }

    public ButtonPO getEventRemoveButton()
    {
        return _eventRemoveButton;
    }

    public ButtonPO getPropertyAddButton()
    {
        return _propertyAddButton;
    }

    public ButtonPO getPropertyEditButton()
    {
        return _propertyEditButton;
    }

    public ButtonPO getPropertyRemoveButton()
    {
        return _propertyRemoveButton;
    }

    public void setPortalPropertiesFile( String text )
    {
        _portalPropertiesFileText.setText( text );
    }

    public TextPO getPortalPropertiesFile()
    {
        return _portalPropertiesFileText;
    }

    public DialogPO getAddEventAction()
    {
        return _addEventActionDialog;
    }

    public TablePO getDefineActionsOnPortalEvents()
    {
        return _defineActionsOnPortalEventsTable;
    }

    public DialogPO getAddPropertyOverride()
    {
        return _addPropertyOverrideDialog;
    }

    public DialogPO getNewLiferayHookConfiguration()
    {
        return _newLiferayHookConfigurationDialog;
    }

    public TablePO getSpecifyPropertiesToOverride()
    {
        return _specifyPropertiesToOverrideTable;
    }

}
