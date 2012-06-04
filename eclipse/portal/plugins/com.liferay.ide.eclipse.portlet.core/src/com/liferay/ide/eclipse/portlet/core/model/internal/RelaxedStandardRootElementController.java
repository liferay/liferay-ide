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
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.eclipse.portlet.core.model.internal;

import com.liferay.ide.eclipse.portlet.core.model.IPortletApp;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.sapphire.modeling.xml.StandardRootElementController;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlNamespace;
import org.w3c.dom.Document;


/**
 * @author Gregory Amerson
 */
public class RelaxedStandardRootElementController extends StandardRootElementController
{
    private static Map<String, String> getDefatulSchemas()
    {
        final Map<String, String> map = new HashMap<String, String>(1);
        
        map.put( getDefaultNamespace(), getDefaultNamespace() );
        
        return map;
    }

    private static String getDefaultElementName()
    {
        return IPortletApp.class.getAnnotation( XmlBinding.class ).path();
    }

    private static String getDefaultNamespace()
    {
        return IPortletApp.class.getAnnotation( XmlNamespace.class ).uri();
    }

    public RelaxedStandardRootElementController()
    {
        super( getDefaultNamespace(), "", getDefaultElementName(), getDefatulSchemas() );
    }
    
    @Override
    protected boolean checkRootElement( Document document, RootElementInfo rinfo )
    {
        return true;
    }
    
}
