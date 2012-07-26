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
 *******************************************************************************/

package com.liferay.ide.portlet.ui.navigator.actions;

import com.liferay.ide.portlet.ui.navigator.PortletsNode;
import com.liferay.ide.portlet.ui.wizard.NewPortletWizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Kamesh Sampath
 */
public class NewPortletAction extends BaseSelectionListenerAction
{

    private static final String ACTION_MESSAGE = "New Portlet";
    protected Object selectedNode;

    /**
     * @param text
     */
    public NewPortletAction()
    {
        super( ACTION_MESSAGE );
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.ui.actions.BaseSelectionListenerAction#updateSelection(org.eclipse.jface.viewers.IStructuredSelection
     * )
     */
    @Override
    protected boolean updateSelection( IStructuredSelection selection )
    {
        if( selection.size() == 1 )
        {
            this.selectedNode = selection.getFirstElement();
            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run()
    {
        if( isEnabled() )
        {
            if( this.selectedNode instanceof PortletsNode )
            {
                PortletsNode portletsNode = (PortletsNode) this.selectedNode;
                IProject currentProject = portletsNode.getParent().getProject();
                NewPortletWizard newPortletWizard = new NewPortletWizard( currentProject );
                WizardDialog wizardDialog = new WizardDialog( Display.getDefault().getActiveShell(), newPortletWizard );
                wizardDialog.create();
                wizardDialog.open();
            }
        }
    }

}
