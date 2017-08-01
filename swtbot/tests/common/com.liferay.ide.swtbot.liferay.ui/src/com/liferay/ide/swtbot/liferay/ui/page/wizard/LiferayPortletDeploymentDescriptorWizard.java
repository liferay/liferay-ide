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

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.liferay.ui.LiferayPortletWizardUI;
import com.liferay.ide.swtbot.liferay.ui.WizardUI;
import com.liferay.ide.swtbot.ui.page.Button;
import com.liferay.ide.swtbot.ui.page.CheckBox;
import com.liferay.ide.swtbot.ui.page.ComboBox;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

/**
 * @author Ashley Yuan
 * @author Li Lu
 */
public class LiferayPortletDeploymentDescriptorWizard extends Wizard implements LiferayPortletWizardUI, WizardUI
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

    public LiferayPortletDeploymentDescriptorWizard( SWTBot bot )
    {
        this( bot, TEXT_BLANK, BACK_WITH_LEFT_BRACKET, NEXT_WITH_BRACKET, FINISH, CANCEL, INDEX_SPECIFY_LIFERAY_PORTLET_DEPLOYMENT_DESCRIPTOR_PAGE );
    }

    public LiferayPortletDeploymentDescriptorWizard( SWTBot bot, int validationMsgIndex )
    {
        this( bot, TEXT_BLANK, BACK_WITH_LEFT_BRACKET, NEXT_WITH_BRACKET, FINISH, CANCEL, validationMsgIndex );
    }

    public LiferayPortletDeploymentDescriptorWizard(
        SWTBot bot, String title, String cancelButtonText, String finishBtnText, String backBtnText,
        String nextButtonText, int validationMsgIndex )
    {
        super( bot, title, BACK_WITH_LEFT_BRACKET, NEXT_WITH_BRACKET, FINISH, CANCEL, validationMsgIndex );

        icon = new Text( bot, LABEL_ICON );
        allowMultipleInstances = new CheckBox( bot, CHECKBOX_ALLOW_MULTIPLE_INSTANCES );
        css = new Text( bot, LABEL_CSS );
        javaScript = new Text( bot, LABEL_JAVASCRIPT );
        cssClassWrapper = new Text( bot, LABEL_CSS_CLASS_WRAPPER );
        displayCategories = new ComboBox( bot, LABEL_DISPLAY_CATEGORY );
        addToControlPanel = new CheckBox( bot, CHECKBOX_ADD_TO_CONTROL_PANEL );
        entryCategories = new ComboBox( bot, LABEL_ENTRY_CATEGORY );
        entryWeight = new Text( bot, LABEL_ENTRY_WEIGHT );
        createEntryClass = new CheckBox( bot, CHECKBOX_CREATE_ENTRY_CLASS );
        entryClass = new Text( bot, LABEL_ENTRY_CLASS );
        browseIconBtn = new Button( bot, BROWSE_WITH_THREE_DOT, 0 );
        browseCssBtn = new Button( bot, BROWSE_WITH_THREE_DOT, 1 );
        browseJavaScriptBtn = new Button( bot, BROWSE_WITH_THREE_DOT, 2 );
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

    public CheckBox getAddToControlPanel()
    {
        return addToControlPanel;
    }

    public void setAddToControlPanel( CheckBox addToControlPanel )
    {
        this.addToControlPanel = addToControlPanel;
    }

    public CheckBox getAllowMultipleInstances()
    {
        return allowMultipleInstances;
    }

    public void setAllowMultipleInstances( CheckBox allowMultipleInstances )
    {
        this.allowMultipleInstances = allowMultipleInstances;
    }

    public Button getBrowseCssBtn()
    {
        return browseCssBtn;
    }

    public void setBrowseCssBtn( Button browseCssBtn )
    {
        this.browseCssBtn = browseCssBtn;
    }

    public Button getBrowseIconBtn()
    {
        return browseIconBtn;
    }

    public void setBrowseIconBtn( Button browseIconBtn )
    {
        this.browseIconBtn = browseIconBtn;
    }

    public Button getBrowseJavaScriptBtn()
    {
        return browseJavaScriptBtn;
    }

    public void setBrowseJavaScriptBtn( Button browseJavaScriptBtn )
    {
        this.browseJavaScriptBtn = browseJavaScriptBtn;
    }

    public CheckBox getCreateEntryClass()
    {
        return createEntryClass;
    }

    public void setCreateEntryClass( CheckBox createEntryClass )
    {
        this.createEntryClass = createEntryClass;
    }

    public Text getCss()
    {
        return css;
    }

    public void setCss( Text css )
    {
        this.css = css;
    }

    public Text getCssClassWrapper()
    {
        return cssClassWrapper;
    }

    public void setCssClassWrapper( Text cssClassWrapper )
    {
        this.cssClassWrapper = cssClassWrapper;
    }

    public ComboBox getDisplayCategory()
    {
        return displayCategories;
    }

    public void setDisplayCategory( ComboBox displayCategory )
    {
        this.displayCategories = displayCategory;
    }

    public ComboBox getEntryCategory()
    {
        return entryCategories;
    }

    public void setEntryCategory( ComboBox entryCategory )
    {
        this.entryCategories = entryCategory;
    }

    public Text getEntryClass()
    {
        return entryClass;
    }

    public void setEntryClass( Text entryClass )
    {
        this.entryClass = entryClass;
    }

    public Text getEntryWeight()
    {
        return entryWeight;
    }

    public void setEntryWeight( Text entryWeight )
    {
        this.entryWeight = entryWeight;
    }

    public Text getIcon()
    {
        return icon;
    }

    public void setIcon( Text icon )
    {
        this.icon = icon;
    }

    public Text getJavaScript()
    {
        return javaScript;
    }

    public void setJavaScript( Text javaScript )
    {
        this.javaScript = javaScript;
    }

}
