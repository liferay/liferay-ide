/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
 *               Kamesh Sampath - initial implementation
 *******************************************************************************/

package com.liferay.ide.portlet.ui.editor.internal;

import com.liferay.ide.portlet.ui.editor.PortletXmlEditor;
import com.liferay.ide.portlet.ui.wizard.NewPortletWizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;

/**
 * @author Kamesh Sampath
 */
public class CreateLiferayPortletActionHandler extends SapphireActionHandler
{

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.ui.SapphireActionHandler#run(org.eclipse.sapphire.ui.SapphireRenderingContext)
     */
    @Override
    protected Object run( SapphireRenderingContext context )
    {
        IProject currentProject = null;

        if( context.getPart().getParentPart() instanceof PortletXmlEditor )
        {
            PortletXmlEditor portletXmlEditor = (PortletXmlEditor) context.getPart().getParentPart();
            currentProject = portletXmlEditor.getProject();
        }

        NewPortletWizard newPortletWizard = new NewPortletWizard( currentProject );
        WizardDialog wizardDialog = new WizardDialog( context.getShell(), newPortletWizard );
        wizardDialog.create();
        wizardDialog.open();
        
        return null;
    }

}
