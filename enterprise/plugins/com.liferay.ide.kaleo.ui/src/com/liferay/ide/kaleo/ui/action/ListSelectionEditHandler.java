/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay Developer Studio ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.ui.action;

import java.util.List;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.ui.ListSelectionService;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.ui.forms.PropertyEditorActionHandler;

/**
 * @author Gregory Amerson
 */
public abstract class ListSelectionEditHandler extends PropertyEditorActionHandler
{

    @Override
    protected boolean computeEnablementState()
    {
        if( super.computeEnablementState() == true )
        {
            ListSelectionService selectionService = getSelectionService();

            if( selectionService != null )
            {
                List<Element> selection = selectionService.selection();

                return selection != null && selection.size() == 1;
            }
        }

        return false;
    }

    public abstract Object edit( Element element, final Presentation context );

    protected ListSelectionService getSelectionService()
    {
        return getPart().service( ListSelectionService.class );
    }

    @Override
    public void init( final SapphireAction action, final ActionHandlerDef def )
    {
        super.init( action, def );

        final ImageData typeImage = typeImage();

        if( typeImage != null )
        {
            addImage( typeImage );
        }

        ListSelectionService selectionService = action.getPart().service( ListSelectionService.class );

        Listener selectionListener = new Listener()
        {
            @Override
            public void handle( Event event )
            {
                refreshEnablementState();
            }
        };

        if( selectionService != null )
        {
            selectionService.attach( selectionListener );
        }
    }

    @Override
    protected Object run( final Presentation context )
    {
        Object retval = null;

        ListSelectionService selectionService = getSelectionService();

        if( selectionService != null )
        {
            List<Element> selection = selectionService.selection();

            retval = edit( selection.get( 0 ), context );
        }

        return retval;
    }

    protected abstract ImageData typeImage();
}
