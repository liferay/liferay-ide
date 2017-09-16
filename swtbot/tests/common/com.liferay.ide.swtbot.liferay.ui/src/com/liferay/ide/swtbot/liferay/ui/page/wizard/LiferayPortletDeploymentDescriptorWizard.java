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
import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.ComboBox;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Ashley Yuan
 * @author Li Lu
 */
public class LiferayPortletDeploymentDescriptorWizard extends Wizard
{

    private CheckBox addToControlPanel;
    private CheckBox allowMultipleInstances;
    private Button browseCssBtn;
    private Button browseIconBtn;
    private Button browseJavaScriptBtn;
    private CheckBox createEntryClass;
    private Text css;
    private Text cssClassWrapper;
    private ComboBox displayCategories;
    private ComboBox entryCategories;
    private Text entryClass;
    private Text entryWeight;
    private Text icon;
    private Text javaScript;

    public LiferayPortletDeploymentDescriptorWizard( SWTWorkbenchBot bot )
    {
        super( bot, 6 );

        icon = new Text( bot, ICON );
        allowMultipleInstances = new CheckBox( bot, ALLOW_MULTIPLE_INSTANCES );
        css = new Text( bot, CSS );
        javaScript = new Text( bot, JAVASCRIPT );
        cssClassWrapper = new Text( bot, CSS_CLASS_WRAPPER );
        displayCategories = new ComboBox( bot, DISPLAY_CATEGORY );
        addToControlPanel = new CheckBox( bot, ADD_TO_CONTROL_PANEL );
        entryCategories = new ComboBox( bot, ENTRY_CATEGORY );
        entryWeight = new Text( bot, ENTRY_WEIGHT );
        createEntryClass = new CheckBox( bot, CREATE_ENTRY_CLASS );
        entryClass = new Text( bot, ENTRY_CLASS );
        browseIconBtn = new Button( bot, BROWSE_WITH_DOT, 0 );
        browseCssBtn = new Button( bot, BROWSE_WITH_DOT, 1 );
        browseJavaScriptBtn = new Button( bot, BROWSE_WITH_DOT, 2 );
    }

    public CheckBox getAddToControlPanel()
    {
        return addToControlPanel;
    }

    public CheckBox getAllowMultipleInstances()
    {
        return allowMultipleInstances;
    }

    public Button getBrowseCssBtn()
    {
        return browseCssBtn;
    }

    public Button getBrowseIconBtn()
    {
        return browseIconBtn;
    }

    public Button getBrowseJavaScriptBtn()
    {
        return browseJavaScriptBtn;
    }

    public CheckBox getCreateEntryClass()
    {
        return createEntryClass;
    }

    public Text getCss()
    {
        return css;
    }

    public Text getCssClassWrapper()
    {
        return cssClassWrapper;
    }

    public ComboBox getDisplayCategory()
    {
        return displayCategories;
    }

    public ComboBox getEntryCategory()
    {
        return entryCategories;
    }

    public Text getEntryClass()
    {
        return entryClass;
    }

    public Text getEntryWeight()
    {
        return entryWeight;
    }

    public Text getIcon()
    {
        return icon;
    }

    public Text getJavaScript()
    {
        return javaScript;
    }

    public void setDisplayCategoryCombobox( String displayCategoryCombobox )
    {
        String[] avilableDisplayCategoryValues = displayCategories.items();

        for( String mycategory : avilableDisplayCategoryValues )
        {
            if( mycategory.equals( displayCategoryCombobox ) )
            {
                displayCategories.setSelection( displayCategoryCombobox );

                return;
            }
        }

        displayCategories.setText( displayCategoryCombobox );
    }

    public void setEntryCategoryCombobox( String entryCategoryCombobox )
    {
        String[] avilableEntryCategoryValues = entryCategories.items();

        for( String myEnteryCategory : avilableEntryCategoryValues )
        {
            if( myEnteryCategory.equals( entryCategoryCombobox ) )
            {
                entryCategories.setSelection( entryCategoryCombobox );

                return;
            }
        }

        entryCategories.setText( entryCategoryCombobox );
    }

    public void specifyLiferayDisplay(
        String displayCategoryValue, boolean addToControlPanelValue, String entryCategoryValue, String entryWeightValue,
        boolean createEntryClassValue, String entryClassValue )
    {
        String[] avilableDisplayCategoryValues = displayCategories.items();

        if( displayCategories != null )
        {

            for( String myDisplayCategory : avilableDisplayCategoryValues )
            {

                if( myDisplayCategory.equals( displayCategories ) )
                {
                    displayCategories.setSelection( displayCategoryValue );
                }
            }

            displayCategories.setText( displayCategoryValue );
        }

        if( addToControlPanelValue )
        {
            addToControlPanel.select();

            if( entryCategories != null )
            {
                String[] avilableEntryCategoryValues = displayCategories.items();

                for( String myEntryCategory : avilableEntryCategoryValues )
                {
                    if( myEntryCategory.equals( entryCategories ) )
                    {
                        entryCategories.setSelection( entryCategoryValue );
                    }
                }

                entryCategories.setText( entryCategoryValue );
            }

            if( entryWeight != null )
            {
                entryWeight.setText( entryWeightValue );
            }

            if( createEntryClassValue )
            {
                createEntryClass.select();

                if( entryClass != null )
                {
                    entryClass.setText( entryClassValue );
                }
            }
            else
            {
                createEntryClass.deselect();
            }
        }
        else
        {
            addToControlPanel.deselect();
        }
    }

    public void specifyLiferayPortletInfo(
        String liferayPortletIcon, boolean allowMultipleInstancesVaule, String liferayPortletCss,
        String liferayPortletJavaScript, String cssClassWrapperValue )
    {
        if( liferayPortletIcon != null )
        {
            icon.setText( liferayPortletIcon );
        }

        if( allowMultipleInstancesVaule )
        {
            allowMultipleInstances.select();
        }
        else
        {
            allowMultipleInstances.deselect();
        }

        if( liferayPortletCss != null )
        {
            css.setText( liferayPortletCss );
        }

        if( liferayPortletJavaScript != null )
        {
            javaScript.setText( liferayPortletJavaScript );
        }

        if( cssClassWrapper != null )
        {
            cssClassWrapper.setText( cssClassWrapperValue );
        }
    }

}
