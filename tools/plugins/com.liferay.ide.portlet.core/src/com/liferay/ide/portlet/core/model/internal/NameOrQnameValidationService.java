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
 * Contributors:
 *               Kamesh Sampath - initial implementation
 *******************************************************************************/

package com.liferay.ide.portlet.core.model.internal;

import com.liferay.ide.portlet.core.model.QName;

import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Kamesh Sampath
 */
public class NameOrQnameValidationService extends ValidationService
{

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.modeling.PropertyValidationService#validate()
     */
    private Listener listener;

    @Override
    public Status compute()
    {
        Element element = context( Element.class );

        final String elementLabel = element.type().getLabel( false, CapitalizationType.FIRST_WORD_ONLY, false );
        QName iqName = null;
        String name = null;
        String nsURI = null;
        String localPart = null;
        if( element instanceof QName )
        {
            iqName = (QName) element;
            nsURI = iqName.getNamespaceURI().text( false );
            localPart = iqName.getLocalPart().text( false );
        }

        if( isEmptyOrNull( name ) && isEmptyOrNull( nsURI ) && isEmptyOrNull( localPart ) )
        {
            return Status.createErrorStatus( Resources.bind( Resources.message, elementLabel ) );
        }
        else if( isEmptyOrNull( name ) && ( isEmptyOrNull( nsURI ) || isEmptyOrNull( localPart ) ) )
        {
            return Status.createErrorStatus( Resources.bind( Resources.invalidQname, elementLabel ) );
        }

        return Status.createOkStatus();
    }

    /**
     * @param text
     * @return
     */

    @Override
    protected void initValidationService()
    {
        this.listener = new FilteredListener<PropertyContentEvent>()
        {
            protected void handleTypedEvent( final PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().getLocalPart().attach( this.listener );
        op().getNamespaceURI().attach( this.listener );
    }

    private boolean isEmptyOrNull( String text )
    {
        if( text == null || text.trim().length() == 0 )
        {
            return true;
        }
        return false;
    }

    private static final class Resources extends NLS
    {
        public static String message;
        public static String invalidQname;

        static
        {
            initializeMessages( NameOrQnameValidationService.class.getName(), Resources.class );
        }
    }

    private QName op()
    {
        return context( QName.class );
    }
}
