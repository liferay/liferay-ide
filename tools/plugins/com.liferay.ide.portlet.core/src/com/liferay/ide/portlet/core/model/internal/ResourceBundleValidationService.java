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

package com.liferay.ide.portlet.core.model.internal;

import com.liferay.ide.portlet.core.model.Portlet;
import com.liferay.ide.portlet.core.model.ResourceBundle;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 */
public class ResourceBundleValidationService extends ValidationService
{

    private FilteredListener<PropertyContentEvent> listener;

    public Status compute()
    {
        final Element modelElement = context( Element.class );

        if( ! modelElement.disposed() && modelElement instanceof ResourceBundle )
        {
            final String bundle = modelElement.property( context( ValueProperty.class ) ).text( false );
            if ( bundle != null && bundle.indexOf( "/" ) != -1 )
            {
                final String correctBundle = bundle.replace( "/", "." ); 
                return Status.createErrorStatus( Resources.bind(
                    StringEscapeUtils.unescapeJava( Resources.invalidResourceBundleWithSlash ), new Object[] {
                        "'" + bundle + "'", "'" + correctBundle + "'" } ) );
            }
            else if ( bundle != null && ( bundle.startsWith( "." ) || bundle.contains( ".." ) ) ) 
            {
                return Status.createErrorStatus( Resources.bind(
                    StringEscapeUtils.unescapeJava( Resources.invalidResourceBundleFileName ), new Object[] {
                        "'" + bundle + "'" } ) );
            }

        }


        return Status.createOkStatus();
    }

    protected void initValidationService()
    {
        this.listener = new FilteredListener<PropertyContentEvent>()
        {

            protected void handleTypedEvent( final PropertyContentEvent event )
            {
                if( !context( ResourceBundle.class ).disposed() )
                {
                    refresh();
                }
            }
        };

        context( ResourceBundle.class ).nearest( Portlet.class ).getResourceBundle().attach( this.listener );
    }

    private static final class Resources extends NLS
    {
        public static String invalidResourceBundleWithSlash;
        public static String invalidResourceBundleFileName;

        static
        {
            initializeMessages( ResourceBundleValidationService.class.getName(), Resources.class );
        }
    }

}
