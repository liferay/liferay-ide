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

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;

import org.eclipse.sapphire.DisposeEvent;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.osgi.framework.Version;


/**
 * @author Gregory Amerson
 */
public class UseSdkLocationAction extends SapphireActionHandler
{

    public UseSdkLocationAction()
    {
        super();
    }

    @Override
    public void init( final SapphireAction action, ActionHandlerDef def )
    {
        super.init( action, def );

        final NewLiferayPluginProjectOp op = op( action );

        final FilteredListener<PropertyContentEvent> listener = new FilteredListener<PropertyContentEvent>()
        {
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                final boolean ant = op.getProjectProvider().content( true ).getShortName().equals( "ant" ); //$NON-NLS-1$

                if( ant )
                {
                    final SDK sdk = SDKManager.getInstance().getSDK( op.getPluginsSDKName().content( true ) );

                    if( sdk != null )
                    {
                        Version version = new Version( sdk.getVersion() );

                        if( ( CoreUtil.compareVersions( version, ILiferayConstants.V611 ) > 0 && CoreUtil.compareVersions(
                            version, ILiferayConstants.V6110 ) < 0 ) ||
                            ( CoreUtil.compareVersions( version, ILiferayConstants.V6120 ) >= 0 ) )
                        {
                            action.setEnabled( true );
                        }
                        else
                        {
                            action.setEnabled( false );
                        }
                    }
                }
                else
                {
                    action.setEnabled( false );
                }
            }
        };

        op.property( NewLiferayPluginProjectOp.PROP_PROJECT_PROVIDER ).attach( listener );
        op.property( NewLiferayPluginProjectOp.PROP_PLUGINS_SDK_NAME ).attach( listener );

        final Listener disposeListener = new FilteredListener<DisposeEvent>()
        {
            @Override
            protected void handleTypedEvent( final DisposeEvent event )
            {
                op.property( NewLiferayPluginProjectOp.PROP_PROJECT_PROVIDER ).detach( listener );
                op.property( NewLiferayPluginProjectOp.PROP_PLUGINS_SDK_NAME ).detach( listener );
            }
        };

        action.attach( disposeListener );
    }

    private NewLiferayPluginProjectOp op( final SapphireAction action )
    {
        return action.getPart().getModelElement().nearest( NewLiferayPluginProjectOp.class );
    }

    @Override
    protected Object run( Presentation context )
    {
        NewLiferayPluginProjectOp op = context.part().getModelElement().nearest( NewLiferayPluginProjectOp.class );
        op.setUseSdkLocation( ! getAction().isChecked() );

        return Status.createOkStatus();
    }
}
