/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.adt.ui.wizard;

import com.liferay.ide.adt.core.model.MobileSDKLibrariesOp;
import com.liferay.ide.adt.core.model.ServerInstance;

import java.util.List;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.ui.ListSelectionService;
import org.eclipse.sapphire.ui.ListSelectionService.ListSelectionChangedEvent;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.SapphirePart.PartInitializationEvent;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class ServerInstancesListener extends Listener
{
    private MobileSDKLibrariesOp mobileSDKConfigOp;
    private ListSelectionService selectionService;

    public ServerInstancesListener()
    {
        super();
    }

    @Override
    public void handle( Event event )
    {
        if( event instanceof PartInitializationEvent )
        {
            final SapphirePart part = ( (PartInitializationEvent) event ).part();
            this.selectionService = part.service( ListSelectionService.class );
            this.selectionService.attach( this );
            this.mobileSDKConfigOp = part.getLocalModelElement().nearest( MobileSDKLibrariesOp.class );
        }
        else if( event instanceof ListSelectionChangedEvent )
        {
            final List<Element> previousSelection = ( (ListSelectionChangedEvent) event ).before();
            final List<Element> selection = ( (ListSelectionChangedEvent) event ).after();

            if( ! previousSelection.isEmpty() )
            {
                previousSelection.get( 0 ).nearest( ServerInstance.class ).detach( this, "Url" );
            }

            if( ! selection.isEmpty() )
            {
                final ServerInstance selectedServer = selection.get( 0 ).nearest( ServerInstance.class );

                selectedServer.attach( this, "Url" );

                updateSelectedServerInstance( selectedServer );
            }
        }
        else if( event instanceof PropertyContentEvent )
        {
            updateSelectedServerInstance( this.selectionService.selection().get( 0 ).nearest( ServerInstance.class ) );
        }
    }

    private void updateSelectedServerInstance( final ServerInstance selectedServer )
    {
        this.mobileSDKConfigOp.setOmniUsername( selectedServer.getOmniUsername().content() );
        this.mobileSDKConfigOp.setOmniPassword( selectedServer.getOmniPassword().content() );
        this.mobileSDKConfigOp.setUrl( selectedServer.getUrl().content() );
    }
}
