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
import com.liferay.ide.ui.tests.swtbot.page.CheckBoxPageObject;
import com.liferay.ide.ui.tests.swtbot.page.ComboBoxPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TextPageObject;
import com.liferay.ide.ui.tests.swtbot.page.WizardPageObject;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;

/**
 * @author Ashley Yuan
 */

public class LiferayPortletDeploymentDescriptorPageObject<T extends SWTBot> extends WizardPageObject<T>
    implements LiferayPortletWizard, ProjectWizard
{

    TextPageObject<SWTBot> iconText;
    CheckBoxPageObject<SWTBot> allowMultipleInstancesCheckbox;
    TextPageObject<SWTBot> cssText;
    TextPageObject<SWTBot> javaScriptText;
    TextPageObject<SWTBot> cssClassWrapperText;

    ComboBoxPageObject<SWTBot> displayCategoryCombobox;
    CheckBoxPageObject<SWTBot> addToControlPanelCheckbox;
    ComboBoxPageObject<SWTBot> entryCategoryCombobox;
    TextPageObject<SWTBot> entryWeightText;
    CheckBoxPageObject<SWTBot> createEntryClassCheckbox;
    TextPageObject<SWTBot> entryClassText;

    public LiferayPortletDeploymentDescriptorPageObject( T bot, int validationMessageIndex )
    {
        this( bot, TEXT_BLANK, BUTTON_BACK, BUTTON_NEXT, BUTTON_FINISH, BUTTON_CANCEL, validationMessageIndex );
    }

    public LiferayPortletDeploymentDescriptorPageObject(
        T bot, String title, String cancelButtonText, String finishButtonText, String backButtonText,
        String nextButtonText, int validationMessageIndex )
    {
        super( bot, title, BUTTON_BACK, BUTTON_NEXT, BUTTON_FINISH, BUTTON_CANCEL, validationMessageIndex );

        iconText = new TextPageObject<SWTBot>( bot, LABEL_ICON );
        allowMultipleInstancesCheckbox = new CheckBoxPageObject<SWTBot>( bot, CHECKBOX_ALLOW_MULTIPLE_INSTANCES );
        cssText = new TextPageObject<SWTBot>( bot, LABEL_CSS );
        javaScriptText = new TextPageObject<SWTBot>( bot, LABEL_JAVASCRIPT );
        cssClassWrapperText = new TextPageObject<SWTBot>( bot, LABEL_CSS_CLASS_WRAPPER );

        displayCategoryCombobox = new ComboBoxPageObject<SWTBot>( bot, LABEL_DISPLAY_CATEGORY );
        addToControlPanelCheckbox = new CheckBoxPageObject<SWTBot>( bot, CHECKBOX_ADD_TO_CONTROL_PANEL );
        entryCategoryCombobox = new ComboBoxPageObject<SWTBot>( bot, LABEL_ENTRY_CATEGORY );
        entryWeightText = new TextPageObject<SWTBot>( bot, LABEL_ENTRY_WEIGHT );
        createEntryClassCheckbox = new CheckBoxPageObject<SWTBot>( bot, CHECKBOX_CREATE_ENTRY_CLASS );
        entryClassText = new TextPageObject<SWTBot>( bot, LABEL_ENTRY_CLASS );
    }

    public void browse( int index )
    {
        clickClosingButton( browseButton( index ) );
    }

    protected SWTBotButton browseButton( int index )
    {
        return bot.button( BUTTON_BROWSE, index );
    }

    public String getCssClassWrapperText()
    {
        return cssClassWrapperText.getText();
    }

    public String getCssText()
    {
        return cssText.getText();
    }

    public String[] getDisplayCategoryAvailableComboValues()
    {
        return displayCategoryCombobox.getAvailableComboValues();
    }

    public String getDisplayCategoryCombobox()
    {
        return displayCategoryCombobox.getText();
    }

    public String[] getEntryCategoryAvailableComboValues()
    {
        return entryCategoryCombobox.getAvailableComboValues();
    }

    public String getEntryCategoryCombobox()
    {
        return entryCategoryCombobox.getText();
    }

    public String getEntryClassText()
    {
        return entryClassText.getText();
    }

    public String getEntryWeightText()
    {
        return entryWeightText.getText();
    }

    public String getIconText()
    {
        return iconText.getText();
    }

    public String getJavaScriptText()
    {
        return javaScriptText.getText();
    }

    public boolean isAddToControlPanelChecked()
    {
        return addToControlPanelCheckbox.isChecked();
    }

    public boolean isAllowMultipleInstancesChecked()
    {
        return allowMultipleInstancesCheckbox.isChecked();
    }

    public boolean isCreateEntryClassChecked()
    {
        return createEntryClassCheckbox.isChecked();
    }

    public boolean isCreateEntryClassEnabled()
    {
        return createEntryClassCheckbox.isEnabled();
    }

    public boolean isEntryCategoryEnabled()
    {
        return entryCategoryCombobox.isEnabled();
    }

    public boolean isEntryClassEnabled()
    {
        return entryClassText.isEnabled();
    }

    public boolean isEntryWeightEnabled()
    {
        return entryWeightText.isEnabled();
    }

    public void setCssClassWrapperText( String cssClassWrapper )
    {
        this.cssClassWrapperText.setText( cssClassWrapper );;
    }

    public void setCssText( String css )
    {
        this.cssText.setText( css );
    }

    public void setDisplayCategoryCombobox( String displayCategory )
    {
        String[] avilableDisplayCategoryValues = getDisplayCategoryAvailableComboValues();

        for( String mycategory : avilableDisplayCategoryValues )
        {
            if( mycategory.equals( displayCategory ) )
            {
                this.displayCategoryCombobox.setSelection( displayCategory );
                return;
            }
        }
        this.displayCategoryCombobox.setText( displayCategory );
    }

    public void setEntryCategoryCombobox( String entryCategory )
    {
        String[] avilableEntryCategoryValues = getEntryCategoryAvailableComboValues();

        for( String myEnteryCategory : avilableEntryCategoryValues )
        {
            if( myEnteryCategory.equals( entryCategory ) )
            {
                this.entryCategoryCombobox.setSelection( entryCategory );
                return;
            }
        }
        this.entryCategoryCombobox.setText( entryCategory );

    }

    public void setEntryClassText( String entryClassText )
    {
        this.entryClassText.setText( entryClassText );
    }

    public void setEntryWeightText( String entryWeightText )
    {
        this.entryWeightText.setText( entryWeightText );
    }

    public void setIconText( String icon )
    {
        this.iconText.setText( icon );;
    }

    public void setJavaScriptText( String javaScript )
    {
        this.javaScriptText.setText( javaScript );
    }

    public void specifyLiferayDisplay(
        String displaycategory, boolean addToControlPanel, String entryCategory, String entryWeight,
        boolean createEntryClass, String entryClass )
    {
        String[] avilableDisplayCategoryValues = getDisplayCategoryAvailableComboValues();

        if( displaycategory != null )
        {

            for( String myDisplayCategory : avilableDisplayCategoryValues )
            {

                if( myDisplayCategory.equals( displaycategory ) )
                {
                    displayCategoryCombobox.setSelection( displaycategory );
                }
            }

            displayCategoryCombobox.setText( displaycategory );

        }

        if( addToControlPanel )
        {
            addToControlPanelCheckbox.select();

            if( createEntryClass )
            {
                createEntryClassCheckbox.select();
            }
            else
            {
                createEntryClassCheckbox.deselect();
            }

        }
        else
        {
            addToControlPanelCheckbox.deselect();
        }

        if( entryCategory != null )
        {
            String[] avilableEntryCategoryValues = getDisplayCategoryAvailableComboValues();

            for( String myEntryCategory : avilableEntryCategoryValues )
            {
                if( myEntryCategory.equals( entryCategory ) )
                {
                    entryCategoryCombobox.setSelection( entryCategory );
                }
            }
            entryCategoryCombobox.setText( entryCategory );

        }

        if( entryWeight != null )
        {
            entryWeightText.setText( entryWeight );
        }

        if( !addToControlPanel )
        {

            addToControlPanelCheckbox.select();
            if( createEntryClass )
            {
                createEntryClassCheckbox.select();
            }
            else
            {
                createEntryClassCheckbox.deselect();
            }
            addToControlPanelCheckbox.deselect();
        }

        if( entryClass != null )
        {
            entryClassText.setText( entryClass );
        }
    }

    public void specifyLiferayPortletInfo(
        String liferayPortletIcon, boolean allowMultipleInstances, String liferayPortletCss,
        String liferayPortletJavaScript, String cssClassWrapper )
    {
        if( liferayPortletIcon != null )
        {
            iconText.setText( liferayPortletIcon );
        }

        if( allowMultipleInstances )
        {
            allowMultipleInstancesCheckbox.select();
        }
        else
        {
            allowMultipleInstancesCheckbox.deselect();
        }

        if( liferayPortletCss != null )
        {
            cssText.setText( liferayPortletCss );
        }

        if( liferayPortletJavaScript != null )
        {
            javaScriptText.setText( liferayPortletJavaScript );
        }

        if( cssClassWrapper != null )
        {
            cssClassWrapperText.setText( cssClassWrapper );
        }
    }
}
