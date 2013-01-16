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

import com.liferay.ide.portlet.core.model.Portlet;
import com.liferay.ide.portlet.core.model.PortletApp;

import java.io.File;
import java.util.List;
import java.util.SortedSet;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.services.PossibleValuesService;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public class PortletNamePossibleValueService extends PossibleValuesService
{

    private PortletApp portletApp;

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.modeling.ModelPropertyService#init(org.eclipse.sapphire.modeling.IModelElement,
     * org.eclipse.sapphire.modeling.ModelProperty, java.lang.String[])
     */
    @Override
    protected void init()
    {
        super.init();
        IResource eresource = context( IModelElement.class ).resource().adapt( IFile.class );
        IContainer resourceFolder = eresource.getParent();
        try
        {
            IResource resource = resourceFolder.findMember( "portlet.xml" ); //$NON-NLS-1$
            if( resource != null )
            {
                File file = resource.getLocation().toFile();
                XmlResourceStore portletXmlResourceStore = new XmlResourceStore( file );
                RootXmlResource portletXmlResource = new RootXmlResource( portletXmlResourceStore );
                portletApp = PortletApp.TYPE.instantiate( portletXmlResource );
            }

        }
        catch( ResourceStoreException e )
        {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.modeling.PossibleValuesService#fillPossibleValues(java.util.SortedSet)
     */
    @Override
    protected void fillPossibleValues( SortedSet<String> values )
    {
        if( portletApp != null )
        {
            List<Portlet> portlets = portletApp.getPortlets();
            for( Portlet iPortlet : portlets )
            {
                values.add( iPortlet.getPortletName().getText() );
            }
        }

    }

}
