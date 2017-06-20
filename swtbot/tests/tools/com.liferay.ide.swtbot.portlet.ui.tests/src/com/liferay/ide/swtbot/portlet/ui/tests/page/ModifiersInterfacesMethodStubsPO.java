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

package com.liferay.ide.swtbot.portlet.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.portlet.ui.tests.LiferayPortletWizard;
import com.liferay.ide.swtbot.project.ui.tests.ProjectWizard;
import com.liferay.ide.swtbot.ui.tests.page.ButtonPO;
import com.liferay.ide.swtbot.ui.tests.page.CheckBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.TablePO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Ashley Yuan
 */
public class ModifiersInterfacesMethodStubsPO extends WizardPO implements LiferayPortletWizard, ProjectWizard
{

    private CheckBoxPO _publicCheckbox;
    private CheckBoxPO _abstractCheckbox;
    private CheckBoxPO _finalCheckbox;

    private ButtonPO _addButton;
    private ButtonPO _removeButton;

    private TablePO _interfacesTable;

    private CheckBoxPO _createEntryClassCheckbox;
    private CheckBoxPO _constrcutFromSuperClassCheckbox;
    private CheckBoxPO _inheritedAbstractMethodsCheckbox;
    private CheckBoxPO _initCheckbox;
    private CheckBoxPO _destoryCheckbox;
    private CheckBoxPO _doViewCheckbox;
    private CheckBoxPO _doEditCheckbox;
    private CheckBoxPO _doHelpCheckbox;
    private CheckBoxPO _doAboutCheckbox;
    private CheckBoxPO _doConfigCheckbox;
    private CheckBoxPO _doEditDefaultsCheckbox;
    private CheckBoxPO _doEditGuestCheckbox;
    private CheckBoxPO _doPreviewCheckbox;
    private CheckBoxPO _doPrintCheckbox;
    private CheckBoxPO _processActionCheckbox;
    private CheckBoxPO _serveResourceCheckbox;

    public ModifiersInterfacesMethodStubsPO( SWTBot bot, int validationMessageIndex )
    {
        this( bot, TEXT_BLANK, validationMessageIndex );
    }

    public ModifiersInterfacesMethodStubsPO( SWTBot bot, String title, int validationMessageIndex )
    {
        super( bot, title, BUTTON_BACK, BUTTON_NEXT, BUTTON_FINISH, BUTTON_CANCEL, validationMessageIndex );

        _publicCheckbox = new CheckBoxPO( bot, LABEL_PUBLIC );
        _abstractCheckbox = new CheckBoxPO( bot, LABEL_ABSTRACT );
        _finalCheckbox = new CheckBoxPO( bot, LABEL_FINAL );
        _interfacesTable = new TablePO( bot, LABEL_INTERFACES );
        _addButton = new ButtonPO( bot, BUTTON_ADD );
        _removeButton = new ButtonPO( bot, BUTTON_REMOVE );
        _constrcutFromSuperClassCheckbox = new CheckBoxPO( bot, LABEL_CONSTRUCTORS_FROM_SUPERCLASS );
        _inheritedAbstractMethodsCheckbox = new CheckBoxPO( bot, LABEL_INHERITED_ABSTRACT_METHODS );
        _initCheckbox = new CheckBoxPO( bot, LABEL_INIT );
        _destoryCheckbox = new CheckBoxPO( bot, LABEL_DESTROY );
        _doViewCheckbox = new CheckBoxPO( bot, LABEL_DOVIEW );
        _doEditCheckbox = new CheckBoxPO( bot, LABEL_DOEDIT );
        _doHelpCheckbox = new CheckBoxPO( bot, LABEL_DOHELP );
        _doAboutCheckbox = new CheckBoxPO( bot, LABEL_DOABOUT );
        _doConfigCheckbox = new CheckBoxPO( bot, LAEBL_DOCONFIG );
        _doEditDefaultsCheckbox = new CheckBoxPO( bot, LABEL_DOEDITDEFAULTS );
        _doEditGuestCheckbox = new CheckBoxPO( bot, LABEL_DOEDITGUEST );
        _doPreviewCheckbox = new CheckBoxPO( bot, LABEL_DOPREVIEW );
        _doPrintCheckbox = new CheckBoxPO( bot, LABEL_DOPRINT );
        _processActionCheckbox = new CheckBoxPO( bot, LABEL_PROCESSACTION );
        _serveResourceCheckbox = new CheckBoxPO( bot, LABEL_SERVERESOURCE );
    }

    public ButtonPO get_addButton()
    {
        return _addButton;
    }

    public ButtonPO get_removeButton()
    {
        return _removeButton;
    }

    public CheckBoxPO get_publicCheckbox()
    {
        return _publicCheckbox;
    }

    public void set_publicCheckbox( CheckBoxPO _publicCheckbox )
    {
        this._publicCheckbox = _publicCheckbox;
    }

    public CheckBoxPO get_abstractCheckbox()
    {
        return _abstractCheckbox;
    }

    public void set_abstractCheckbox( CheckBoxPO _abstractCheckbox )
    {
        this._abstractCheckbox = _abstractCheckbox;
    }

    public CheckBoxPO get_finalCheckbox()
    {
        return _finalCheckbox;
    }

    public void set_finalCheckbox( CheckBoxPO _finalCheckbox )
    {
        this._finalCheckbox = _finalCheckbox;
    }

    public TablePO get_interfacesTable()
    {
        return _interfacesTable;
    }

    public void set_interfacesTable( TablePO _interfacesTable )
    {
        this._interfacesTable = _interfacesTable;
    }

    public CheckBoxPO get_createEntryClassCheckbox()
    {
        return _createEntryClassCheckbox;
    }

    public void set_createEntryClassCheckbox( CheckBoxPO _createEntryClassCheckbox )
    {
        this._createEntryClassCheckbox = _createEntryClassCheckbox;
    }

    public CheckBoxPO get_constrcutFromSuperClassCheckbox()
    {
        return _constrcutFromSuperClassCheckbox;
    }

    public void set_constrcutFromSuperClassCheckbox( CheckBoxPO _constrcutFromSuperClassCheckbox )
    {
        this._constrcutFromSuperClassCheckbox = _constrcutFromSuperClassCheckbox;
    }

    public CheckBoxPO get_inheritedAbstractMethodsCheckbox()
    {
        return _inheritedAbstractMethodsCheckbox;
    }

    public void set_inheritedAbstractMethodsCheckbox( CheckBoxPO _inheritedAbstractMethodsCheckbox )
    {
        this._inheritedAbstractMethodsCheckbox = _inheritedAbstractMethodsCheckbox;
    }

    public CheckBoxPO get_initCheckbox()
    {
        return _initCheckbox;
    }

    public void set_initCheckbox( CheckBoxPO _initCheckbox )
    {
        this._initCheckbox = _initCheckbox;
    }

    public CheckBoxPO get_destoryCheckbox()
    {
        return _destoryCheckbox;
    }

    public void set_destoryCheckbox( CheckBoxPO _destoryCheckbox )
    {
        this._destoryCheckbox = _destoryCheckbox;
    }

    public CheckBoxPO get_doViewCheckbox()
    {
        return _doViewCheckbox;
    }

    public void set_doViewCheckbox( CheckBoxPO _doViewCheckbox )
    {
        this._doViewCheckbox = _doViewCheckbox;
    }

    public CheckBoxPO get_doEditCheckbox()
    {
        return _doEditCheckbox;
    }

    public void set_doEditCheckbox( CheckBoxPO _doEditCheckbox )
    {
        this._doEditCheckbox = _doEditCheckbox;
    }

    public CheckBoxPO get_doHelpCheckbox()
    {
        return _doHelpCheckbox;
    }

    public void set_doHelpCheckbox( CheckBoxPO _doHelpCheckbox )
    {
        this._doHelpCheckbox = _doHelpCheckbox;
    }

    public CheckBoxPO get_doAboutCheckbox()
    {
        return _doAboutCheckbox;
    }

    public void set_doAboutCheckbox( CheckBoxPO _doAboutCheckbox )
    {
        this._doAboutCheckbox = _doAboutCheckbox;
    }

    public CheckBoxPO get_doConfigCheckbox()
    {
        return _doConfigCheckbox;
    }

    public void set_doConfigCheckbox( CheckBoxPO _doConfigCheckbox )
    {
        this._doConfigCheckbox = _doConfigCheckbox;
    }

    public CheckBoxPO get_doEditDefaultsCheckbox()
    {
        return _doEditDefaultsCheckbox;
    }

    public void set_doEditDefaultsCheckbox( CheckBoxPO _doEditDefaultsCheckbox )
    {
        this._doEditDefaultsCheckbox = _doEditDefaultsCheckbox;
    }

    public CheckBoxPO get_doEditGuestCheckbox()
    {
        return _doEditGuestCheckbox;
    }

    public void set_doEditGuestCheckbox( CheckBoxPO _doEditGuestCheckbox )
    {
        this._doEditGuestCheckbox = _doEditGuestCheckbox;
    }

    public CheckBoxPO get_doPreviewCheckbox()
    {
        return _doPreviewCheckbox;
    }

    public void set_doPreviewCheckbox( CheckBoxPO _doPreviewCheckbox )
    {
        this._doPreviewCheckbox = _doPreviewCheckbox;
    }

    public CheckBoxPO get_doPrintCheckbox()
    {
        return _doPrintCheckbox;
    }

    public void set_doPrintCheckbox( CheckBoxPO _doPrintCheckbox )
    {
        this._doPrintCheckbox = _doPrintCheckbox;
    }

    public CheckBoxPO get_processActionCheckbox()
    {
        return _processActionCheckbox;
    }

    public void set_processActionCheckbox( CheckBoxPO _processActionCheckbox )
    {
        this._processActionCheckbox = _processActionCheckbox;
    }

    public CheckBoxPO get_serveResourceCheckbox()
    {
        return _serveResourceCheckbox;
    }

    public void set_serveResourceCheckbox( CheckBoxPO _serveResourceCheckbox )
    {
        this._serveResourceCheckbox = _serveResourceCheckbox;
    }

    public void selectInterface( int interfaceIndex )
    {
        _interfacesTable.click( interfaceIndex, 0 );
    }

}
