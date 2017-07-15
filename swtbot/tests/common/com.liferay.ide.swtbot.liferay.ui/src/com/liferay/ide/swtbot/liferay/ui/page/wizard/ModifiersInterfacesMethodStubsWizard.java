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
import com.liferay.ide.swtbot.ui.page.Table;
import com.liferay.ide.swtbot.ui.page.Wizard;

/**
 * @author Ashley Yuan
 */
public class ModifiersInterfacesMethodStubsWizard extends Wizard implements LiferayPortletWizardUI, WizardUI
{

    private Button addBtn;
    private CheckBox constrcutFromSuperClass;
    private CheckBox createEntryClass;

    private CheckBox destory;
    private CheckBox doAbout;

    private CheckBox doConfig;

    private CheckBox doEdit;
    private CheckBox doEditDefaults;
    private CheckBox doEditGuest;
    private CheckBox doHelp;
    private CheckBox doPreview;
    private CheckBox doPrint;
    private CheckBox doView;
    private CheckBox inheritedAbstractMethods;
    private CheckBox init;
    private Table interfaces;
    private CheckBox isAbstract;
    private CheckBox isFinal;
    private CheckBox isPublic;
    private CheckBox processAction;
    private Button removeBtn;

    private CheckBox serveResource;

    public ModifiersInterfacesMethodStubsWizard( SWTBot bot, int validationMsgIndex )
    {
        this( bot, TEXT_BLANK, validationMsgIndex );
    }

    public ModifiersInterfacesMethodStubsWizard( SWTBot bot, String title, int validationMsgIndex )
    {
        super( bot, title, BACK_WITH_LEFT_BRACKET, NEXT_WITH_BRACKET, FINISH, CANCEL, validationMsgIndex );

        isPublic = new CheckBox( bot, LABEL_PUBLIC );
        isAbstract = new CheckBox( bot, LABEL_ABSTRACT );
        isFinal = new CheckBox( bot, LABEL_FINAL );
        interfaces = new Table( bot, LABEL_INTERFACES );
        addBtn = new Button( bot, ADD_WITH_THREE_DOT );
        removeBtn = new Button( bot, REMOVE );
        constrcutFromSuperClass = new CheckBox( bot, LABEL_CONSTRUCTORS_FROM_SUPERCLASS );
        inheritedAbstractMethods = new CheckBox( bot, LABEL_INHERITED_ABSTRACT_METHODS );
        init = new CheckBox( bot, LABEL_INIT );
        destory = new CheckBox( bot, LABEL_DESTROY );
        doView = new CheckBox( bot, LABEL_DOVIEW );
        doEdit = new CheckBox( bot, LABEL_DOEDIT );
        doHelp = new CheckBox( bot, LABEL_DOHELP );
        doAbout = new CheckBox( bot, LABEL_DOABOUT );
        doConfig = new CheckBox( bot, LAEBL_DOCONFIG );
        doEditDefaults = new CheckBox( bot, LABEL_DOEDITDEFAULTS );
        doEditGuest = new CheckBox( bot, LABEL_DOEDITGUEST );
        doPreview = new CheckBox( bot, LABEL_DOPREVIEW );
        doPrint = new CheckBox( bot, LABEL_DOPRINT );
        processAction = new CheckBox( bot, LABEL_PROCESSACTION );
        serveResource = new CheckBox( bot, LABEL_SERVERESOURCE );
    }

    public Button getAddBtn()
    {
        return addBtn;
    }

    public CheckBox getConstrcutFromSuperClass()
    {
        return constrcutFromSuperClass;
    }

    public CheckBox getCreateEntryClass()
    {
        return createEntryClass;
    }

    public CheckBox getDestory()
    {
        return destory;
    }

    public CheckBox getDoAbout()
    {
        return doAbout;
    }

    public CheckBox getDoConfig()
    {
        return doConfig;
    }

    public CheckBox getDoEdit()
    {
        return doEdit;
    }

    public CheckBox getDoEditDefaults()
    {
        return doEditDefaults;
    }

    public CheckBox getDoEditGuest()
    {
        return doEditGuest;
    }

    public CheckBox getDoHelp()
    {
        return doHelp;
    }

    public CheckBox getDoPreview()
    {
        return doPreview;
    }

    public CheckBox getDoPrint()
    {
        return doPrint;
    }

    public CheckBox getDoView()
    {
        return doView;
    }

    public CheckBox getInheritedAbstractMethods()
    {
        return inheritedAbstractMethods;
    }

    public CheckBox getInit()
    {
        return init;
    }

    public Table getInterfaces()
    {
        return interfaces;
    }

    public CheckBox getIsAbstract()
    {
        return isAbstract;
    }

    public CheckBox getIsFinal()
    {
        return isFinal;
    }

    public CheckBox getIsPublic()
    {
        return isPublic;
    }

    public CheckBox getProcessAction()
    {
        return processAction;
    }

    public Button getRemoveBtn()
    {
        return removeBtn;
    }

    public CheckBox getServeResource()
    {
        return serveResource;
    }

}
