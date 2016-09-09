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

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.wizard.ModifyModulesWizard;

/**
 * @author Andy Wu
 * @author Simon Jiang
 * @author Joye Luo
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class DeployPage extends Page
{

    public DeployPage( Composite parent, int style, LiferayUpgradeDataModel dataModel )
    {
        super( parent, style, dataModel, DEPLOY_PAGE_ID, true );

        Button deployButton = new Button( this, SWT.PUSH );
        deployButton.setText( "Deploy" );
        deployButton.addSelectionListener( new SelectionAdapter()
        {

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                final String serverName = dataModel.getLiferayServerName().content();

                final IServer server = ServerUtil.getServer( serverName );

                final ModifyModulesWizard wizard = new ModifyModulesWizard( server );

                final WizardDialog dialog = new WizardDialog( UIUtil.getActiveShell(), wizard );

                dialog.open();
            }
        } );
    }

    @Override
    public String getDescriptor()
    {
        return "This step will deploy your projects into the local server.\n" +
            "Note: Please ensure that a local server is started.\n";
    }

    @Override
    public String getPageTitle()
    {
        return "Deploy";
    }

}
