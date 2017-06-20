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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.portlet.ui.tests.LiferayPortletWizard;
import com.liferay.ide.swtbot.project.ui.tests.ProjectWizard;
import com.liferay.ide.swtbot.ui.tests.page.ButtonPO;
import com.liferay.ide.swtbot.ui.tests.page.ComboBoxPO;
import com.liferay.ide.swtbot.ui.tests.page.RadioPO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Ashley Yuan
 */
public class CreateLiferayPortletWizardPO extends WizardPO implements LiferayPortletWizard, ProjectWizard
{

    private ComboBoxPO _portletPluginProjectComboBox;
    private TextPO _sourceFolderText;
    private RadioPO _createNewPortletRadio;
    private RadioPO _useDefaultPortletRadio;
    private TextPO _portletClassText;
    private TextPO _javaPackageText;
    private ComboBoxPO _superClassCombobox;
    private ButtonPO _browseSourceButton;
    private ButtonPO _browsePackageButton;
    private ButtonPO _browseSuperclassButton;


    public CreateLiferayPortletWizardPO( SWTBot bot )
    {
        this( bot, TEXT_BLANK );
    }

    public CreateLiferayPortletWizardPO( SWTBot bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public CreateLiferayPortletWizardPO( SWTBot bot, String title, int validationMessageIndex )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, validationMessageIndex );

        _portletPluginProjectComboBox = new ComboBoxPO( bot, LABEL_PORTLET_PLUGIN_PROJECT );
        _sourceFolderText = new TextPO( bot, LABEL_SOURCE_FOLDER );
        _createNewPortletRadio = new RadioPO( bot, RADIO_CREATE_NEW_PORTLET );
        _useDefaultPortletRadio = new RadioPO( bot, RADIO_USE_DEFAULT_PORTLET );
        _portletClassText = new TextPO( bot, LABEL_PORTLET_CLASS );
        _javaPackageText = new TextPO( bot, LABEL_JAVA_PACKAGE );
        _superClassCombobox = new ComboBoxPO( bot, LABEL_SUPERCLASS );
        _browseSourceButton = new ButtonPO( bot, BUTTON_BROWSE_WITH_DOT );
        _browsePackageButton = new ButtonPO( bot, BUTTON_BROWSE_WITH_DOT, 1 );
        _browseSuperclassButton = new ButtonPO( bot, BUTTON_BROWSE_WITH_DOT, 2 );
    }

    public ButtonPO get_browseSourceButton()
    {
        return _browseSourceButton;
    }

    public ButtonPO get_browsePackageButton()
    {
        return _browsePackageButton;
    }

    public ButtonPO get_browseSuperclassButton()
    {
        return _browseSuperclassButton;
    }

    public void createLiferayPortlet( boolean defaultMvc )
    {
        createLiferayPortlet( TEXT_BLANK, null, defaultMvc, null, null, null );
    }

    public void createLiferayPortlet( String projectName )
    {
        createLiferayPortlet( projectName, null, false, null, null, null );
    }

    public void createLiferayPortlet( String projectName, boolean defaultMvc )
    {
        createLiferayPortlet( projectName, null, defaultMvc, null, null, null );
    }

    public void createLiferayPortlet( String projectName, String srcFolder )
    {
        createLiferayPortlet( projectName, srcFolder, false, null, null, null );
    }

    public void createLiferayPortlet(
        String projectName, String srcFolder, boolean defaultMvc, String portletClass, String javaPackage,
        String superClass )
    {
        if( projectName != null && !projectName.equals( "" ) )
        {
            _portletPluginProjectComboBox.setSelection( projectName );
        }
        if( srcFolder != null )
        {
            _sourceFolderText.setText( srcFolder );
        }

        if( defaultMvc )
        {
            _useDefaultPortletRadio.click();

            assertFalse( _createNewPortletRadio.isSelected() );
            assertTrue( _useDefaultPortletRadio.isSelected() );
        }
        else
        {
            _createNewPortletRadio.click();

            assertTrue( _createNewPortletRadio.isSelected() );
            assertFalse( _useDefaultPortletRadio.isSelected() );
        }

        if( portletClass != null )
        {
            _portletClassText.setText( portletClass );
        }
        if( javaPackage != null )
        {
            _javaPackageText.setText( javaPackage );
        }
        if( superClass != null )
        {
            setSuperClassCombobox( superClass );
        }
    }

    public void createLiferayPortlet( String projectName, String portletClass, String javaPackage, String superClass )
    {
        createLiferayPortlet( projectName, null, false, portletClass, javaPackage, superClass );
    }

    public RadioPO get_createNewPortletRadio()
    {
        return _createNewPortletRadio;
    }

    public RadioPO get_useDefaultPortletRadio()
    {
        return _useDefaultPortletRadio;
    }

    public String[] getAvailableSuperclasses()
    {
        return _superClassCombobox.getAvailableComboValues();
    }

    public String getJavaPackageText()
    {
        return _javaPackageText.getText();
    }

    public String getPortletClassText()
    {
        return _portletClassText.getText();
    }

    public String getPortletPluginProject()
    {
        return _portletPluginProjectComboBox.getText();
    }

    public String getSourceFolderText()
    {
        return _sourceFolderText.getText();
    }

    public String getSuperClassCombobox()
    {
        return _superClassCombobox.getText();
    }

    public boolean isJavaPackageTextEnabled()
    {
        return _javaPackageText.isEnabled();
    }

    public boolean isPortletClassTextEnabled()
    {
        return _portletClassText.isEnabled();
    }

    public boolean isSuperClassComboboxEnabled()
    {
        return _superClassCombobox.isEnabled();
    }

    public void setJavaPackageText( String javaPackage )
    {
        this._javaPackageText.setText( javaPackage );
    }

    public void setPortletClassText( String portletClassText )
    {
        this._portletClassText.setText( portletClassText );
    }

    public void setSourceFolderText( String sourceFolder )
    {
        this._sourceFolderText.setText( sourceFolder );
    }

    public void setSuperClassCombobox( String superclassName )
    {
        String[] avilableSuperclasses = getAvailableSuperclasses();

        for( String mySuperclass : avilableSuperclasses )
        {
            if( mySuperclass.equals( superclassName ) )
            {
                this._superClassCombobox.setSelection( superclassName );
                return;
            }
        }

        _superClassCombobox.setText( superclassName );
    }

}
