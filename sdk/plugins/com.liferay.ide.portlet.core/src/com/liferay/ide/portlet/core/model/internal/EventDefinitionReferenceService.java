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
 * Contributors:
 *               Kamesh Sampath - initial implementation
 *******************************************************************************/

package com.liferay.ide.portlet.core.model.internal;

import static org.eclipse.sapphire.modeling.util.MiscUtil.equal;

import com.liferay.ide.portlet.core.model.EventDefinition;
import com.liferay.ide.portlet.core.model.PortletApp;

import javax.xml.namespace.QName;

import org.eclipse.sapphire.services.ReferenceService;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public class EventDefinitionReferenceService extends ReferenceService
{
    private static final String QUERY_BY_NAME = "name";
    private static final String QUERY_BY_QNAME = "qname";

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.modeling.ReferenceService#resolve(java.lang.String)
     */
    @Override
    public Object resolve( String reference )
    {
        final PortletApp config = context( PortletApp.class );

        if( config != null )
        {
            for( EventDefinition eventDefinition : config.getEventDefinitions() )
            {
                if( QUERY_BY_NAME.equals( param( "0" ) ) )
                {
                    if( equal( eventDefinition.getName().getContent(), reference ) )
                    {
                        return eventDefinition;
                    }
                }
                else if( QUERY_BY_QNAME.equals( param( "0" ) ) )
                {
                    if( equal( getQName( eventDefinition ), reference ) )
                    {
                        return eventDefinition;
                    }
                }

            }
        }

        return null;
    }

    /**
     * @param eventDefinition
     * @return
     */
    private String getQName( EventDefinition eventDefinition )
    {
        QName qName = null;
        String nsURI = eventDefinition.getNamespaceURI().getContent();
        String localPart = eventDefinition.getLocalPart().getContent();
        qName = new QName( nsURI, localPart );
        return qName.toString();
    }
}
