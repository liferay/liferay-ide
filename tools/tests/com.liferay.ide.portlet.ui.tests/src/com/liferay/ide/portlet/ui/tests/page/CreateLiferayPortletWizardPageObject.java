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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.portlet.ui.tests.LiferayPortletWizard;
import com.liferay.ide.ui.tests.ProjectWizard;
import com.liferay.ide.ui.tests.swtbot.page.RadioPageObject;
import com.liferay.ide.ui.tests.swtbot.page.ComboBoxPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TextPageObject;
import com.liferay.ide.ui.tests.swtbot.page.WizardPageObject;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;

/**
 * @author Ashley Yuan
 */
public class CreateLiferayPortletWizardPageObject<T extends SWTBot> extends WizardPageObject<T>
    implements LiferayPortletWizard, ProjectWizard
{

    ComboBoxPageObject<SWTBot> portletPluginProject;
    TextPageObject<SWTBot> sourceFolderText;
    RadioPageObject<SWTBot> createNewPortletRadio;
    RadioPageObject<SWTBot> useDefaultPortletRadio;
    TextPageObject<SWTBot> portletClassText;
    TextPageObject<SWTBot> javaPackageText;
    ComboBoxPageObject<SWTBot> superClassCombobox;

    public CreateLiferayPortletWizardPageObject( T bot )
    {
        this( bot, TEXT_BLANK );
    }

    public CreateLiferayPortletWizardPageObject( T bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public CreateLiferayPortletWizardPageObject( T bot, String title, int validationMessageIndex )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, validationMessageIndex );

        portletPluginProject = new ComboBoxPageObject<SWTBot>( bot, LABEL_PORTLET_PLUGIN_PROJECT );
        sourceFolderText = new TextPageObject<SWTBot>( bot, LABEL_SOURCE_FOLDER );
        createNewPortletRadio = new RadioPageObject<SWTBot>( bot, RADIO_CREATE_NEW_PORTLET );
        useDefaultPortletRadio = new RadioPageObject<SWTBot>( bot, RADIO_USE_DEFAULT_PORTLET );
        portletClassText = new TextPageObject<SWTBot>( bot, LABEL_PORTLET_CLASS );
        javaPackageText = new TextPageObject<SWTBot>( bot, LABEL_JAVA_PACKAGE );
        superClassCombobox = new ComboBoxPageObject<SWTBot>( bot, LABEL_SUPERCLASS );
    }

    public void browse( int index )
    {
        clickClosingButton( browseButton( index ) );
    }

    protected SWTBotButton browseButton( int index )
    {
        return bot.button( BUTTON_BROWSE, index );
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
            portletPluginProject.setSelection( projectName );
        }
        if( srcFolder != null )
        {
            sourceFolderText.setText( srcFolder );
        }

        if( defaultMvc )
        {
            useDefaultPortletRadio.click();

            assertFalse( createNewPortletRadio.isSelected() );
            assertTrue( useDefaultPortletRadio.isSelected() );
        }
        else
        {
            createNewPortletRadio.click();

            assertTrue( createNewPortletRadio.isSelected() );
            assertFalse( useDefaultPortletRadio.isSelected() );
        }

        if( portletClass != null )
        {
            portletClassText.setText( portletClass );
        }
        if( javaPackage != null )
        {
            javaPackageText.setText( javaPackage );
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

    public String[] getAvailableSuperclasses()
    {
        return superClassCombobox.getAvailableComboValues();
    }

    public String getJavaPackageText()
    {
        return javaPackageText.getText();
    }

    public String getPortletClassText()
    {
        return portletClassText.getText();
    }

    public String getPortletPluginProject()
    {
        return portletPluginProject.getText();
    }

    public String getSourceFolderText()
    {
        return sourceFolderText.getText();
    }

    public String getSuperClassCombobox()
    {
        return superClassCombobox.getText();
    }

    public boolean isJavaPackageTextEnabled()
    {
        return javaPackageText.isEnabled();
    }

    public boolean isPortletClassTextEnabled()
    {
        return portletClassText.isEnabled();
    }

    public boolean isRadioSelected( String radioLabel )
    {
        RadioPageObject<SWTBot> radio = new RadioPageObject<SWTBot>( bot, radioLabel );
        return radio.isSelected();
    }

    public boolean isSuperClassComboboxEnabled()
    {
        return superClassCombobox.isEnabled();
    }

    public void setJavaPackageText( String javaPackage )
    {
        this.javaPackageText.setText( javaPackage );
    }

    public void setPortletClassText( String portletClassText )
    {
        this.portletClassText.setText( portletClassText );
    }

    public void setSourceFolderText( String sourceFolder )
    {
        this.sourceFolderText.setText( sourceFolder );
    }

    public void setSuperClassCombobox( String superclassName )
    {
        String[] avilableSuperclasses = getAvailableSuperclasses();

        for( String mySuperclass : avilableSuperclasses )
        {
            if( mySuperclass.equals( superclassName ) )
            {
                this.superClassCombobox.setSelection( superclassName );
                return;
            }
        }
        this.superClassCombobox.setText( superclassName );
    }
}
