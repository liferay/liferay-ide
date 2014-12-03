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

package com.liferay.ide.server.ui.portal;

import com.liferay.ide.server.core.portal.PortalRuntime;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

/**
 * @author Gregory Amerson
 */
public class PortalRuntimeWizard extends WizardFragment
{

    private PortalRuntimeComposite composite;

    public PortalRuntimeWizard()
    {
        super();
    }

    @Override
    public Composite createComposite( Composite parent, IWizardHandle handle )
    {
        composite = new PortalRuntimeComposite( parent, handle );
        return composite;
    }

    @Override
    public boolean hasComposite()
    {
        return true;
    }

    @Override
    public void enter()
    {
        super.enter();

        final IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel().getObject( TaskModel.TASK_RUNTIME );
        runtime.setLocation( new Path( "/lrdev/repos/liferay/bundles/" ) );
        ( ( PortalRuntime) runtime.loadAdapter( PortalRuntime.class, null ) ).setPortalBundleType( "tomcat" );
    }

    @Override
    public boolean isComplete()
    {
        boolean retval = false;

        final IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel().getObject( TaskModel.TASK_RUNTIME );

        if( runtime != null )
        {
            final IStatus status = runtime.validate( null );

            retval = status == null || status.getSeverity() != IStatus.ERROR;
        }

        return retval;
    }

}
