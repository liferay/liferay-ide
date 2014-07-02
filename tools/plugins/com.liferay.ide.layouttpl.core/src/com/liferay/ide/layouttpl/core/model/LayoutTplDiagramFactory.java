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
package com.liferay.ide.layouttpl.core.model;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class LayoutTplDiagramFactory implements ILayoutTplDiagramFactory
{

    public static final ILayoutTplDiagramFactory INSTANCE = new LayoutTplDiagramFactory();

    public LayoutTplDiagramElement newLayoutTplDiagram()
    {
        return new LayoutTplDiagramElement();
    }

    public PortletColumnElement newPortletColumn()
    {
        return new PortletColumnElement();
    }

    public PortletColumnElement newPortletColumnFromElement( IDOMElement portletColumnElement )
    {
        return PortletColumnElement.createFromElement( portletColumnElement, this );
    }

    public PortletLayoutElement newPortletLayout()
    {
        return new PortletLayoutElement();
    }

    public PortletLayoutElement newPortletLayoutFromElement( IDOMElement portletLayoutElement )
    {
        return PortletLayoutElement.createFromElement( portletLayoutElement, this );
    }

}
