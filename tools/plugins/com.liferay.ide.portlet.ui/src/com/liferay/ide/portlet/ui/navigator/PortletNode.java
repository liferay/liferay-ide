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
 * 		Kamesh Sampath - initial implementation
 * 		Gregory Amerson - initial implementation review and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.portlet.ui.navigator;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.portlet.core.model.Portlet;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.CapitalizationType;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 * @author Gregory Amerson
 */
public class PortletNode
{

    final private PortletsNode parent;
    final private Portlet portlet;

    public PortletNode( PortletsNode portletsNode, Portlet portlet )
    {
        this.parent = portletsNode;
        this.portlet = portlet;
    }

    public PortletsNode getParent()
    {
        return this.parent;
    }

    public String getName()
    {
        String retval = StringPool.EMPTY;

        if( this.portlet != null )
        {
            final Value<String> label = this.portlet.getPortletName();

            retval = label.localized( CapitalizationType.TITLE_STYLE, false );
        }

        return retval;
    }

    public Element getModel()
    {
        return this.portlet;
    }

}
