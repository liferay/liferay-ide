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
 *      Kamesh Sampath - initial implementation
 *      Gregory Amerson - initial implementation review and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.portlet.core.model.internal;

import com.liferay.ide.portlet.core.PortletCore;
import com.liferay.ide.portlet.core.model.Portlet;
import com.liferay.ide.portlet.core.model.PortletApp;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.services.PossibleValuesService;

/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 */
public class PortletNamePossibleValueService extends PossibleValuesService
{

    private static Map<IResource, PortletApp> portletModels = new HashMap<IResource, PortletApp>();
    private PortletApp localPortletModel;
    private String[] localPortletNames;


    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.modeling.PossibleValuesService#fillPossibleValues(java.util.SortedSet)
     */
    @Override
    protected void fillPossibleValues( Set<String> values )
    {
        if( this.localPortletModel == null )
        {
            final IResource eresource = context( Element.class ).resource().adapt( IFile.class );
            final IContainer resourceFolder = eresource.getParent();

            try
            {
                final IResource resource = resourceFolder.findMember( "portlet.xml" ); //$NON-NLS-1$

                if( resource != null )
                {
                    final PortletApp cachedPortletModel = portletModels.get( resource );

                    if( cachedPortletModel == null )
                    {
                        File file = resource.getLocation().toFile();
                        XmlResourceStore portletXmlResourceStore = new XmlResourceStore( file );
                        RootXmlResource portletXmlResource = new RootXmlResource( portletXmlResourceStore );
                        this.localPortletModel = PortletApp.TYPE.instantiate( portletXmlResource );
                        portletModels.put( resource, this.localPortletModel );
                    }
                    else
                    {
                        this.localPortletModel = cachedPortletModel;
                    }
                }

            }
            catch( ResourceStoreException e )
            {
                PortletCore.logError( e );
            }
        }

        if( this.localPortletModel != null && this.localPortletNames == null )
        {
            List<String> portletNameList = new LinkedList<String>();
            List<Portlet> portlets = this.localPortletModel.getPortlets();

            for( Portlet iPortlet : portlets )
            {
                portletNameList.add( iPortlet.getPortletName().text() );
            }

            this.localPortletNames = portletNameList.toArray( new String[0] );
        }

        if( this.localPortletNames != null )
        {
            Collections.addAll( values, this.localPortletNames );
        }

    }

}
