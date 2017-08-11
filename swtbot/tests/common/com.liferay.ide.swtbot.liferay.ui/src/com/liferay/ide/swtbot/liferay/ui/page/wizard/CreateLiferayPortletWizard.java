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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.swtbot.ui.page.Button;
import com.liferay.ide.swtbot.ui.page.ComboBox;
import com.liferay.ide.swtbot.ui.page.Radio;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;
import com.liferay.ide.swtbot.ui.util.StringPool;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Ashley Yuan
 */
public class CreateLiferayPortletWizard extends Wizard
{

    private Button browsePackageBtn;
    private Button browseSourceBtn;
    private Button browseSuperClassBtn;
    private Text javaPackage;
    private Radio newPortlet;
    private Text portletClass;
    private ComboBox portletPluginProjects;
    private Text sourceFolder;
    private ComboBox superClasses;
    private Radio useDefault;

    public CreateLiferayPortletWizard( SWTBot bot, int validationMsgIndex )
    {
        this( bot, StringPool.BLANK, validationMsgIndex );
    }

    public CreateLiferayPortletWizard( SWTBot bot, String title, int validationMsgIndex )
    {
        super( bot, title, validationMsgIndex );

        portletPluginProjects = new ComboBox( bot, PORTLET_PLUGIN_PROJECT );
        sourceFolder = new Text( bot, SOURCE_FOLDER );
        newPortlet = new Radio( bot, CREATE_NEW_PORTLET );
        useDefault = new Radio( bot, USE_DEFAULT_PORTLET_MVCPORTLET );
        portletClass = new Text( bot, PORTLET_CLASS );
        javaPackage = new Text( bot, JAVA_PACKAGE );
        superClasses = new ComboBox( bot, SUPERCLASS );
        browseSourceBtn = new Button( bot, BROWSE_WITH_DOT );
        browsePackageBtn = new Button( bot, BROWSE_WITH_DOT, 1 );
        browseSuperClassBtn = new Button( bot, BROWSE_WITH_DOT, 2 );
    }

    public void createLiferayPortlet( boolean defaultMvc )
    {
        createLiferayPortlet( StringPool.BLANK, null, defaultMvc, null, null, null );
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
        String projectName, String srcFolder, boolean defaultMvc, String portletClassValue, String javaPackageValue,
        String superClass )
    {
        if( projectName != null && !projectName.equals( StringPool.BLANK ) )
        {
            portletPluginProjects.setSelection( projectName );
        }
        if( srcFolder != null )
        {
            sourceFolder.setText( srcFolder );
        }

        if( defaultMvc )
        {
            useDefault.click();

            assertFalse( newPortlet.isSelected() );
            assertTrue( useDefault.isSelected() );
        }
        else
        {
            newPortlet.click();

            assertTrue( newPortlet.isSelected() );
            assertFalse( useDefault.isSelected() );

            if( portletClass != null )
            {
                portletClass.setText( portletClassValue );
            }
            if( javaPackage != null )
            {
                javaPackage.setText( javaPackageValue );
            }
            if( superClass != null )
            {
                setSuperClassCombobox( superClass );
            }
        }

    }

    public void createLiferayPortlet( String projectName, String portletClass, String javaPackage, String superClass )
    {
        createLiferayPortlet( projectName, null, false, portletClass, javaPackage, superClass );
    }

    public String[] getAvailableSuperClasses()
    {
        return superClasses.items();
    }

    public Button getBrowsePackageBtn()
    {
        return browsePackageBtn;
    }

    public Button getBrowseSourceBtn()
    {
        return browseSourceBtn;
    }

    public Button getBrowseSuperClassBtn()
    {
        return browseSuperClassBtn;
    }

    public Text getJavaPackage()
    {
        return javaPackage;
    }

    public Radio getNewPortlet()
    {
        return newPortlet;
    }

    public Text getPortletClass()
    {
        return portletClass;
    }

    public ComboBox getPortletPluginProjects()
    {
        return portletPluginProjects;
    }

    public Text getSourceFolder()
    {
        return sourceFolder;
    }

    public ComboBox getSuperClasses()
    {
        return superClasses;
    }

    public Radio getUseDefault()
    {
        return useDefault;
    }

    public void setSuperClassCombobox( String superclassName )
    {
        String[] avilableSuperclasses = superClasses.items();

        for( String mySuperclass : avilableSuperclasses )
        {
            if( mySuperclass.equals( superclassName ) )
            {
                this.superClasses.setSelection( superclassName );
                return;
            }
        }

        superClasses.setText( superclassName );
    }

}
