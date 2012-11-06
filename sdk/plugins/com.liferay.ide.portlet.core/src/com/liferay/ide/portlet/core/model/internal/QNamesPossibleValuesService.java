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

import com.liferay.ide.portlet.core.model.EventDefinition;
import com.liferay.ide.portlet.core.model.EventDefinitionRef;
import com.liferay.ide.portlet.core.model.PortletApp;
import com.liferay.ide.portlet.core.model.PublicRenderParameter;
import com.liferay.ide.portlet.core.model.SupportedPublicRenderParameter;

import java.util.SortedSet;

import javax.xml.namespace.QName;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.services.PossibleValuesService;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public class QNamesPossibleValuesService extends PossibleValuesService
{

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.modeling.PossibleValuesService#fillPossibleValues(java.util.SortedSet)
     */
    @Override
    protected void fillPossibleValues( SortedSet<String> values )
    {
        IModelElement imodelElement = context( IModelElement.class );
        // values.add( param( "0" ) );
        PortletApp portletApp = context( IModelElement.class ).nearest( PortletApp.class );
        
        if( imodelElement instanceof EventDefinitionRef )
        {
            ModelElementList<EventDefinition> eventDefs = portletApp.getEventDefinitions();
            
            for( EventDefinition eventDefinition : eventDefs )
            {
                if( eventDefinition.getNamespaceURI().getContent() != null &&
                    eventDefinition.getLocalPart().getContent() != null )
                {
                    values.add( getQName(
                        eventDefinition.getNamespaceURI().getContent( false ),
                        eventDefinition.getLocalPart().getContent() ) );
                }
            }
        }
        else if( imodelElement instanceof SupportedPublicRenderParameter )
        {
            ModelElementList<PublicRenderParameter> publicRenderParameters = portletApp.getPublicRenderParameters();
            
            for( PublicRenderParameter publicRenderParam : publicRenderParameters )
            {
                if( publicRenderParam.getNamespaceURI().getContent() != null &&
                    publicRenderParam.getLocalPart().getContent() != null )
                {
                    values.add( getQName(
                        publicRenderParam.getNamespaceURI().getContent( false ),
                        publicRenderParam.getLocalPart().getContent() ) );
                }
            }
        }

    }

    /**
     * @param nsURI
     * @param localPart
     * @return
     */
    private String getQName( String nsURI, String localPart )
    {
        QName qName = null;

        qName = new QName( nsURI, localPart );
        return qName.toString();
    }
}
