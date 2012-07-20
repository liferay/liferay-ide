/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.project.ui.action;

import com.liferay.ide.project.ui.ProjectUIPlugin;
import com.liferay.ide.project.ui.wizard.NewProjectFromSourceWizard;
import com.liferay.ide.ui.action.AbstractNewProjectWizardProjectElement;
import com.liferay.ide.ui.action.NewWizardAction;

/**
 * @author Gregory Amerson
 */
public class ImportLiferayProjectWizardAction extends NewWizardAction
{

    static class ImportLiferayProjectElement extends AbstractNewProjectWizardProjectElement
    {
        protected String getContributorID()
        {
            return ProjectUIPlugin.PLUGIN_ID;
        }

        @Override
        protected String getProjectElementAttribute( String attr )
        {
            if( NewWizardAction.ATT_NAME.equals( attr ) )
            {
                return "";
            }
            else if( NewWizardAction.ATT_ICON.equals( attr ) )
            {
                return "/icons/n16/plugin_new.png";
            }

            return null;
        }

        @Override
        protected Object createNewWizard()
        {
            return new NewProjectFromSourceWizard();
        }

        @Override
        protected String getProjectParameterElementAttribute( String name )
        {
            if( NewWizardAction.TAG_NAME.equals( name ) )
            {
                return NewWizardAction.ATT_MENUINDEX;
            }
            else if( NewWizardAction.TAG_VALUE.equals( name ) )
            {
                return "100";
            }

            return null;
        }

    }

    public ImportLiferayProjectWizardAction()
    {
        super( new ImportLiferayProjectElement() );
        setText( "New Liferay Project from Existing Source" );
    }

}
