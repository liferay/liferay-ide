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
 *******************************************************************************/
package com.liferay.ide.alloy.core.webresources;

import org.eclipse.wst.html.webresources.core.IWebResourcesFinderTypeProvider;
import org.eclipse.wst.html.webresources.core.WebResourcesFinderType;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class AlloyFinderTypeProvider implements IWebResourcesFinderTypeProvider
{

    @Override
    public WebResourcesFinderType getWebResourcesFinderType(
        String elementName, String attrName, IStructuredDocumentRegion region, int position )
    {
        if( isCssClassName( elementName, attrName, region, position ) )
        {
            // find CSS class name.
            return WebResourcesFinderType.CSS_CLASS_NAME;
        }

        return null;
    }

    private boolean isCssClassName( String elementName, String attrName, IStructuredDocumentRegion region, int position )
    {
        // <aui:button cssClass="btn-primary" ... />
        // we want to match all aui:* tags that have cssClass
        return elementName != null && isCssElement( elementName ) && attrName != null && isCssAttribute( attrName );
    }

    private boolean isCssElement( String elementName )
    {
        return elementName.startsWith( "ace:" ) ||
               elementName.startsWith( "aui:" ) ||
               elementName.startsWith( "h:" ) ||
               elementName.startsWith( "p:" ) ||
               elementName.startsWith( "rich:" );
    }

    private boolean isCssAttribute( String attrName )
    {
        return attrName.equals( "cssClass" ) ||
               attrName.equals( "bodyClass" ) ||
               attrName.equals( "headherClass" ) ||
               attrName.equals( "styleClass" );
    }

}
