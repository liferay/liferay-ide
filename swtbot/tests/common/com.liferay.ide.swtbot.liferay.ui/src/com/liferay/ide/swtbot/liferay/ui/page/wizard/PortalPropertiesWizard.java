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
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Vicky Wang
 */
public class PortalPropertiesWizard extends Wizard
{

    private Dialog addEventActionDialog;
    private Dialog addPropertyOverrideDialog;
    private Button browseBtn;
    private Table defineActionsOnPortalEvents;
    private Button eventAddBtn;
    private Button eventEditBtn;
    private Button eventRemoveBtn;
    private Dialog newLiferayHookConfigurationDialog;
    private Text portalPropertiesFile;
    private Button propertyAddBtn;
    private Button propertyEditBtn;
    private Button propertyRemoveBtn;
    private Table specifyProperties;

    public PortalPropertiesWizard( SWTBot bot )
    {
        super( bot, 1 );

        portalPropertiesFile = new Text( bot, PORTAL_PROPERTIES_FILE );
        defineActionsOnPortalEvents = new Table( bot, DEFINE_ACTIONS_ON_PORTAL_EVENTS );
        specifyProperties = new Table( bot, SPECIFY_PROPERTIES_TO_OVERRIDE );
        addEventActionDialog = new Dialog( bot, ADD_EVENT_ACTION );
        addPropertyOverrideDialog = new Dialog( bot, ADD_PROPERTY_OVERRIDE );
        newLiferayHookConfigurationDialog =
            new Dialog( bot, NEW_LIFERAY_HOOK, BACK_WITH_LEFT_BRACKET, NEXT_WITH_BRACKET );
        browseBtn = new Button( bot, BROWSE_WITH_DOT, 0 );
        eventAddBtn = new Button( bot, ADD_WITH_DOT, 0 );
        eventEditBtn = new Button( bot, EDIT_WITH_DOT, 0 );
        eventRemoveBtn = new Button( bot, REMOVE_WITH_DOT, 0 );
        propertyAddBtn = new Button( bot, ADD_WITH_DOT, 1 );
        propertyEditBtn = new Button( bot, EDIT_WITH_DOT, 1 );
        propertyRemoveBtn = new Button( bot, REMOVE_WITH_DOT, 1 );
    }

    public Dialog getAddEventActionDialog()
    {
        return addEventActionDialog;
    }

    public Dialog getAddPropertyOverrideDialog()
    {
        return addPropertyOverrideDialog;
    }

    public Button getBrowseBtn()
    {
        return browseBtn;
    }

    public Table getDefineActionsOnPortalEvents()
    {
        return defineActionsOnPortalEvents;
    }

    public Button getEventAddBtn()
    {
        return eventAddBtn;
    }

    public Button getEventEditBtn()
    {
        return eventEditBtn;
    }

    public Button getEventRemoveBtn()
    {
        return eventRemoveBtn;
    }

    public Dialog getNewLiferayHookConfigurationDialog()
    {
        return newLiferayHookConfigurationDialog;
    }

    public Text getPortalPropertiesFile()
    {
        return portalPropertiesFile;
    }

    public Button getPropertyAddBtn()
    {
        return propertyAddBtn;
    }

    public Button getPropertyEditBtn()
    {
        return propertyEditBtn;
    }

    public Button getPropertyRemoveBtn()
    {
        return propertyRemoveBtn;
    }

    public Table getSpecifyProperties()
    {
        return specifyProperties;
    }

}
