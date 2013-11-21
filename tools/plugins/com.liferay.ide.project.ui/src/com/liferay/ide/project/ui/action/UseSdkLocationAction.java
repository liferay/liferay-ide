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
 *******************************************************************************/
package com.liferay.ide.project.ui.action;

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;

import org.eclipse.sapphire.DisposeEvent;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.ui.forms.PropertyEditorActionHandler;


/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class UseSdkLocationAction extends PropertyEditorActionHandler
{

    public UseSdkLocationAction()
    {
        super();
    }

    @Override
    public void init( final SapphireAction action, ActionHandlerDef def )
    {
        super.init( action, def );

        final NewLiferayPluginProjectOp op = op();

        final FilteredListener<PropertyContentEvent> listener = new FilteredListener<PropertyContentEvent>()
        {
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refreshEnablementState();
            }
        };

        op.property( NewLiferayPluginProjectOp.PROP_PROJECT_PROVIDER ).attach( listener );
        op.property( NewLiferayPluginProjectOp.PROP_USE_DEFAULT_LOCATION).attach( listener );

        final Listener disposeListener = new FilteredListener<DisposeEvent>()
        {
            @Override
            protected void handleTypedEvent( final DisposeEvent event )
            {
                op.property( NewLiferayPluginProjectOp.PROP_PROJECT_PROVIDER ).detach( listener );
                op.property( NewLiferayPluginProjectOp.PROP_USE_DEFAULT_LOCATION).detach( listener );
            }
        };

        action.attach( disposeListener );
    }

    @Override
    protected boolean computeEnablementState()
    {
        final NewLiferayPluginProjectOp op = op();

        if( op.getProjectProvider().content( true ).getShortName().equals( "ant" ) &&
            op.getUseDefaultLocation().content( true ) )
        {
            return true;
        }

        return false;
    }

    private NewLiferayPluginProjectOp op()
    {
        return this.getPart().getModelElement().nearest( NewLiferayPluginProjectOp.class );
    }

    @Override
    protected Object run( Presentation context )
    {
        NewLiferayPluginProjectOp op = context.part().getModelElement().nearest( NewLiferayPluginProjectOp.class );
        op.setUseSdkLocation( ! getAction().isChecked() );

        return Status.createOkStatus();
    }
}
