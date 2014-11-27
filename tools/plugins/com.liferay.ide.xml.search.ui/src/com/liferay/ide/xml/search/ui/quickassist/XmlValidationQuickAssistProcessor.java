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
 *******************************************************************************/

package com.liferay.ide.xml.search.ui.quickassist;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.wst.sse.ui.internal.StructuredMarkerAnnotation;


/**
 * @author Kuo Zhang
 */
@SuppressWarnings( "restriction" )
public class XmlValidationQuickAssistProcessor extends AbstractQuickAssistProcessorFromMarkerResolution
{

    public XmlValidationQuickAssistProcessor()
    {
        super();
    }

    @Override
    public String getErrorMessage()
    {
        return null;
    }

    @Override
    public boolean canFix( Annotation annotation )
    {
        return true;
    }

    @Override
    public boolean canAssist( IQuickAssistInvocationContext invocationContext )
    {
        return true;
    }


    @Override
    protected IMarker getMarkerFromAnnotation( Annotation annotation )
    {
        if( annotation instanceof StructuredMarkerAnnotation )
        {
            return ( (StructuredMarkerAnnotation) annotation ).getMarker();
        }

        return null;
    }

}
