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

package com.liferay.ide.project.ui.tests.page;

<<<<<<< HEAD
=======
import com.liferay.ide.project.ui.tests.swtbot.CreateLiferayPortletWizard;
import com.liferay.ide.project.ui.tests.swtbot.ProjectWizard;
import com.liferay.ide.ui.tests.swtbot.page.CheckBoxPageObject;
import com.liferay.ide.ui.tests.swtbot.page.ComboBoxPageObject;
>>>>>>> 901a919... JSF and Vaadin Test
import com.liferay.ide.ui.tests.swtbot.page.RadioPageObject;
import com.liferay.ide.ui.tests.swtbot.page.ComboBoxPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TextPageObject;
import com.liferay.ide.ui.tests.swtbot.page.WizardPageObject;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Ashley Yuan
 * @author Terry Jia
 * @author Li Lu
 */
public class CreateLiferayPortletWizardPageObject<T extends SWTBot> extends WizardPageObject<T>
    implements CreateLiferayPortletWizard, ProjectWizard
{

    RadioPageObject<SWTBot> createNewPortlet;

    TextPageObject<SWTBot> javaPackage;

    TextPageObject<SWTBot> portletClass;

    ComboBoxPageObject<SWTBot> portletPluginProject;

    public CheckBoxPageObject<SWTBot> print;

    TextPageObject<SWTBot> sourceFolder;

    ComboBoxPageObject<SWTBot> superClass;

    RadioPageObject<SWTBot> useDefaultPortlet;

    public CreateLiferayPortletWizardPageObject( T bot )
    {
        this( bot, TEXT_BLANK );
    }

    public CreateLiferayPortletWizardPageObject( T bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_VALIDATION_MESSAGE );
    }

    public CreateLiferayPortletWizardPageObject( T bot, String wizardTitle, int validationMessageIndex )
    {
        super( bot, wizardTitle, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, validationMessageIndex );

        portletPluginProject = new ComboBoxPageObject<SWTBot>( bot, LABEL_PORTLET_PLUGIN_PROJECT );
        sourceFolder = new TextPageObject<SWTBot>( bot, LABEL_SOURCE_FOLDER );
        createNewPortlet = new RadioPageObject<SWTBot>( bot, RADIO_CREATE_NEW_PORTLET );
        useDefaultPortlet = new RadioPageObject<SWTBot>( bot, RADIO_USE_DEFAULT_PORTLET );
        portletClass = new TextPageObject<SWTBot>( bot, LABEL_PORTLET_CLASS );
        javaPackage = new TextPageObject<SWTBot>( bot, LABEL_JAVA_PACKAGE );
        superClass = new ComboBoxPageObject<SWTBot>( bot, LABEL_SUPERCLASS );
    }

    public RadioPageObject<SWTBot> getCreateNewPortlet()
    {
        return createNewPortlet;
    }

    public TextPageObject<SWTBot> getJavaPackage()
    {
        return javaPackage;
    }

    public TextPageObject<SWTBot> getPortletClass()
    {
        return portletClass;
    }

    public ComboBoxPageObject<SWTBot> getPortletPluginProject()
    {
        return portletPluginProject;
    }

    public CheckBoxPageObject<SWTBot> getPrint()
    {
        return print;
    }

    public TextPageObject<SWTBot> getSourceFolder()
    {
        return sourceFolder;
    }

    public ComboBoxPageObject<SWTBot> getSuperClass()
    {
        return superClass;
    }

    public RadioPageObject<SWTBot> getUseDefaultPortlet()
    {
        return useDefaultPortlet;
    }

}
