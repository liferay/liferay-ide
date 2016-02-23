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

package com.liferay.ide.portlet.ui.tests.page;

import com.liferay.ide.portlet.ui.tests.LiferayPortletWizard;
import com.liferay.ide.ui.tests.ProjectWizard;
import com.liferay.ide.ui.tests.swtbot.page.ButtonPageObject;
import com.liferay.ide.ui.tests.swtbot.page.CheckBoxPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TablePageObject;
import com.liferay.ide.ui.tests.swtbot.page.WizardPageObject;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Ashley Yuan
 */
public class ModifiersInterfacesMethodStubsPageObject<T extends SWTBot> extends WizardPageObject<T>
    implements LiferayPortletWizard, ProjectWizard
{

    CheckBoxPageObject<SWTBot> publicCheckbox;
    CheckBoxPageObject<SWTBot> abstractCheckbox;
    CheckBoxPageObject<SWTBot> finalCheckbox;

    TablePageObject<SWTBot> interfacesTable;
    ButtonPageObject<SWTBot> addButton;
    ButtonPageObject<SWTBot> removeButton;

    CheckBoxPageObject<SWTBot> createEntryClassCheckbox;
    CheckBoxPageObject<SWTBot> constrcutFromSuperClassCheckbox;
    CheckBoxPageObject<SWTBot> inheritedAbstractMethodsCheckbox;
    CheckBoxPageObject<SWTBot> initCheckbox;
    CheckBoxPageObject<SWTBot> destoryCheckbox;
    CheckBoxPageObject<SWTBot> doViewCheckbox;
    CheckBoxPageObject<SWTBot> doEditCheckbox;
    CheckBoxPageObject<SWTBot> doHelpCheckbox;
    CheckBoxPageObject<SWTBot> doAboutCheckbox;
    CheckBoxPageObject<SWTBot> doConfigCheckbox;
    CheckBoxPageObject<SWTBot> doEditDefaultsCheckbox;
    CheckBoxPageObject<SWTBot> doEditGuestCheckbox;
    CheckBoxPageObject<SWTBot> doPreviewCheckbox;
    CheckBoxPageObject<SWTBot> doPrintCheckbox;
    CheckBoxPageObject<SWTBot> processActionCheckbox;
    CheckBoxPageObject<SWTBot> serveResourceCheckbox;

    public ModifiersInterfacesMethodStubsPageObject( T bot, int validationMessageIndex )
    {
        this( bot, TEXT_BLANK, BUTTON_BACK, BUTTON_NEXT, BUTTON_FINISH, BUTTON_CANCEL, validationMessageIndex );
    }

    public ModifiersInterfacesMethodStubsPageObject(
        T bot, String title, String cancelButtonText, String finishButtonText, String backButtonText,
        String nextButtonText, int validationMessageIndex )
    {
        super( bot, title, BUTTON_BACK, BUTTON_NEXT, BUTTON_FINISH, BUTTON_CANCEL, validationMessageIndex );

        publicCheckbox = new CheckBoxPageObject<SWTBot>( bot, LABEL_PUBLIC );
        abstractCheckbox = new CheckBoxPageObject<SWTBot>( bot, LABEL_ABSTRACT );
        finalCheckbox = new CheckBoxPageObject<SWTBot>( bot, LABEL_FINAL );

        interfacesTable = new TablePageObject<SWTBot>( bot, LABEL_INTERFACES );
        addButton = new ButtonPageObject<SWTBot>( bot, BUTTON_ADD );
        removeButton = new ButtonPageObject<SWTBot>( bot, BUTTON_REMOVE );

        constrcutFromSuperClassCheckbox = new CheckBoxPageObject<SWTBot>( bot, LABEL_CONSTRUCTORS_FROM_SUPERCLASS );
        inheritedAbstractMethodsCheckbox = new CheckBoxPageObject<SWTBot>( bot, LABEL_INHERITED_ABSTRACT_METHODS );
        initCheckbox = new CheckBoxPageObject<SWTBot>( bot, LABEL_INIT );
        destoryCheckbox = new CheckBoxPageObject<SWTBot>( bot, LABEL_DESTROY );
        doViewCheckbox = new CheckBoxPageObject<SWTBot>( bot, LABEL_DOVIEW );
        doEditCheckbox = new CheckBoxPageObject<SWTBot>( bot, LABEL_DOEDIT );
        doHelpCheckbox = new CheckBoxPageObject<SWTBot>( bot, LABEL_DOHELP );
        doAboutCheckbox = new CheckBoxPageObject<SWTBot>( bot, LABEL_DOABOUT );
        doConfigCheckbox = new CheckBoxPageObject<SWTBot>( bot, LAEBL_DOCONFIG );
        doEditDefaultsCheckbox = new CheckBoxPageObject<SWTBot>( bot, LABEL_DOEDITDEFAULTS );
        doEditGuestCheckbox = new CheckBoxPageObject<SWTBot>( bot, LABEL_DOEDITGUEST );
        doPreviewCheckbox = new CheckBoxPageObject<SWTBot>( bot, LABEL_DOPREVIEW );
        doPrintCheckbox = new CheckBoxPageObject<SWTBot>( bot, LABEL_DOPRINT );
        processActionCheckbox = new CheckBoxPageObject<SWTBot>( bot, LABEL_PROCESSACTION );
        serveResourceCheckbox = new CheckBoxPageObject<SWTBot>( bot, LABEL_SERVERESOURCE );
    }

    public void clickButton( String buttonLabel )
    {
        ButtonPageObject<SWTBot> button = new ButtonPageObject<SWTBot>( bot, buttonLabel );
        button.click();
    }

    public void deselectCheckbox( String checkboxLabel )
    {
        CheckBoxPageObject<SWTBot> selectCheckbox = new CheckBoxPageObject<SWTBot>( bot, checkboxLabel );
        selectCheckbox.deselect();
    }

    public boolean isAbstractChecked()
    {
        return abstractCheckbox.isChecked();
    }

    public boolean isAbstractEnabled()
    {
        return abstractCheckbox.isEnabled();
    }

    public boolean isButtonEnabled( String buttonLabel )
    {
        ButtonPageObject<SWTBot> button = new ButtonPageObject<SWTBot>( bot, buttonLabel );

        return button.isEnabled();
    }

    public boolean isConstrcutFromSuperClassChecked()
    {
        return constrcutFromSuperClassCheckbox.isChecked();
    }

    public boolean isConstrcutFromSuperClassEnabled()
    {
        return constrcutFromSuperClassCheckbox.isEnabled();
    }

    public boolean isCreateEntryClassChecked()
    {
        return createEntryClassCheckbox.isChecked();
    }

    public boolean isCreateEntryClassEnabled()
    {
        return createEntryClassCheckbox.isEnabled();
    }

    public boolean isDestoryChecked()
    {
        return destoryCheckbox.isChecked();
    }

    public boolean isDestoryEnabled()
    {
        return destoryCheckbox.isEnabled();
    }

    public boolean isDoAboutChecked()
    {
        return doAboutCheckbox.isChecked();
    }

    public boolean isDoAboutEnabled()
    {
        return doAboutCheckbox.isEnabled();
    }

    public boolean isDoConfigChecked()
    {
        return doConfigCheckbox.isChecked();
    }

    public boolean isDoConfigEnabled()
    {
        return doConfigCheckbox.isEnabled();
    }

    public boolean isDoEditChecked()
    {
        return doEditCheckbox.isChecked();
    }

    public boolean isDoEditDefaultsChecked()
    {
        return doEditDefaultsCheckbox.isChecked();
    }

    public boolean isDoEditDefaultsEnabled()
    {
        return doEditDefaultsCheckbox.isEnabled();
    }

    public boolean isDoEditEnabled()
    {
        return doEditCheckbox.isEnabled();
    }

    public boolean isDoEditGuestChecked()
    {
        return doEditGuestCheckbox.isChecked();
    }

    public boolean isDoEditGuestEnabled()
    {
        return doEditGuestCheckbox.isEnabled();
    }

    public boolean isDoHelpChecked()
    {
        return doHelpCheckbox.isChecked();
    }

    public boolean isDoHelpEnabled()
    {
        return doHelpCheckbox.isEnabled();
    }

    public boolean isDoPreviewChecked()
    {
        return doPreviewCheckbox.isChecked();
    }

    public boolean isDoPreviewEnabled()
    {
        return doPreviewCheckbox.isEnabled();
    }

    public boolean isDoPrintChecked()
    {
        return doPrintCheckbox.isChecked();
    }

    public boolean isDoPrintEnabled()
    {
        return doPrintCheckbox.isEnabled();
    }

    public boolean isDoViewChecked()
    {
        return doViewCheckbox.isChecked();
    }

    public boolean isDoViewEnabled()
    {
        return doViewCheckbox.isEnabled();
    }

    public boolean isFinalChecked()
    {
        return finalCheckbox.isChecked();
    }

    public boolean isFinalEnabled()
    {
        return finalCheckbox.isEnabled();
    }

    public boolean isInheritedAbstractMethodsChecked()
    {
        return inheritedAbstractMethodsCheckbox.isChecked();
    }

    public boolean isInheritedAbstractMethodsEnabled()
    {
        return inheritedAbstractMethodsCheckbox.isEnabled();
    }

    public boolean isInitChecked()
    {
        return initCheckbox.isChecked();
    }

    public boolean isInitEnabled()
    {
        return initCheckbox.isEnabled();
    }

    public boolean isProcessActionChecked()
    {
        return processActionCheckbox.isChecked();
    }

    public boolean isProcessActionEnabled()
    {
        return processActionCheckbox.isEnabled();
    }

    public boolean isPublicChecked()
    {
        return publicCheckbox.isChecked();
    }

    public boolean isPublicEnabled()
    {
        return publicCheckbox.isEnabled();
    }

    public boolean isServeResourceChecked()
    {
        return serveResourceCheckbox.isChecked();
    }

    public boolean isServeResourceEnabled()
    {
        return serveResourceCheckbox.isEnabled();
    }

    public void selectCheckbox( String checkboxLabel )
    {
        CheckBoxPageObject<SWTBot> selectCheckbox = new CheckBoxPageObject<SWTBot>( bot, checkboxLabel );
        selectCheckbox.select();
    }

    public void selectInterface( int interfaceIndex )
    {
        interfacesTable.click( interfaceIndex, 0 );
    }
}
