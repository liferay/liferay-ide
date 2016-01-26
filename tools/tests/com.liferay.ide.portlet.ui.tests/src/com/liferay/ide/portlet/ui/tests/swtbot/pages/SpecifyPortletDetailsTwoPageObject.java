
package com.liferay.ide.portlet.ui.tests.swtbot.pages;

import com.liferay.ide.project.ui.tests.swtbot.CreateLiferayPortletWizard;
import com.liferay.ide.project.ui.tests.swtbot.ProjectWizard;
import com.liferay.ide.ui.tests.swtbot.page.CheckBoxPageObject;
import com.liferay.ide.ui.tests.swtbot.page.ComboBoxPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TextPageObject;
import com.liferay.ide.ui.tests.swtbot.page.WizardPageObject;

import org.eclipse.swtbot.swt.finder.SWTBot;

public class SpecifyPortletDetailsTwoPageObject<T extends SWTBot> extends WizardPageObject<T>
    implements CreateLiferayPortletWizard, ProjectWizard
{

    CheckBoxPageObject<SWTBot> addToControlPanel;

    CheckBoxPageObject<SWTBot> allowMultipleInstances;

    CheckBoxPageObject<SWTBot> createEntryClass;

    TextPageObject<SWTBot> css;

    TextPageObject<SWTBot> cssClassWrapper;

    ComboBoxPageObject<SWTBot> displayCategory;

    ComboBoxPageObject<SWTBot> entryCategory;

    TextPageObject<SWTBot> entryClass;

    TextPageObject<SWTBot> entryWeight;

    TextPageObject<SWTBot> icon;

    TextPageObject<SWTBot> javaScript;

    public SpecifyPortletDetailsTwoPageObject( T bot )
    {
        this( bot, TEXT_BLANK, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, INDEX_VALIDATION_PORTLET_MESSAGE3 );
    }

    public SpecifyPortletDetailsTwoPageObject(
        T bot, String title, String cancelButtonText, String finishButtonText, String backButtonText,
        String nextButtonText, int validationMessageIndex )
    {
        super( bot, title, cancelButtonText, finishButtonText, backButtonText, nextButtonText, validationMessageIndex );
        icon = new TextPageObject<SWTBot>( bot, LABEL_ICON );
        allowMultipleInstances = new CheckBoxPageObject<SWTBot>( bot, LABEL_ALLOW_MULTIPLE_INSTANCES );
        css = new TextPageObject<SWTBot>( bot, LABEL_CSS );
        javaScript = new TextPageObject<SWTBot>( bot, LABEL_JAVASCRIPT );
        cssClassWrapper = new TextPageObject<SWTBot>( bot, LABEL_CSS_CLASS_WRAPPER );
        displayCategory = new ComboBoxPageObject<SWTBot>( bot, LABEL_DISPLAY_CATEGORY );
        addToControlPanel = new CheckBoxPageObject<SWTBot>( bot, LABEL_ADD_TO_CONTROL_PANEL );
        entryCategory = new ComboBoxPageObject<SWTBot>( bot, LABEL_ENTRY_CATEGORY );
        entryWeight = new TextPageObject<SWTBot>( bot, LABEL_ENTRY_WEIGHT );
        createEntryClass = new CheckBoxPageObject<SWTBot>( bot, LABEL_CREATE_ENTRY_CLASS );
        entryClass = new TextPageObject<SWTBot>( bot, LABEL_ENTRY_CLASS );

    }

    public CheckBoxPageObject<SWTBot> getAddToControlPanel()
    {
        return addToControlPanel;
    }

    public CheckBoxPageObject<SWTBot> getAllowMultipleInstances()
    {
        return allowMultipleInstances;
    }

    public CheckBoxPageObject<SWTBot> getCreateEntryClass()
    {
        return createEntryClass;
    }

    public TextPageObject<SWTBot> getCss()
    {
        return css;
    }

    public TextPageObject<SWTBot> getCssClassWrapper()
    {
        return cssClassWrapper;
    }

    public ComboBoxPageObject<SWTBot> getDisplayCategory()
    {
        return displayCategory;
    }

    public ComboBoxPageObject<SWTBot> getEntryCategory()
    {
        return entryCategory;
    }

    public TextPageObject<SWTBot> getEntryClass()
    {
        return entryClass;
    }

    public TextPageObject<SWTBot> getEntryWeight()
    {
        return entryWeight;
    }

    public TextPageObject<SWTBot> getIcon()
    {
        return icon;
    }

    public TextPageObject<SWTBot> getJavaScript()
    {
        return javaScript;
    }

}
