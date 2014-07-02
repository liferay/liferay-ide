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
 * Contributors:
 *      Kamesh Sampath - initial implementation
 *      Gregory Amerson - initial implementation review and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.portlet.ui.editor.internal;

import com.liferay.ide.portlet.core.model.EventDefinition;
import com.liferay.ide.portlet.core.model.PortletApp;

import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.forms.MasterDetailsContentNodePart;
import org.eclipse.sapphire.ui.forms.MasterDetailsEditorPagePart;

/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 */
public class DefinePortletEventHandler extends SapphireActionHandler
{

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.ui.SapphireActionHandler#run(org.eclipse.sapphire.ui.SapphireRenderingContext)
     */
    @Override
    protected Object run( Presentation context )
    {
        PortletApp rootModel = (PortletApp) context.part().getModelElement();
        EventDefinition eventDefintion = rootModel.getEventDefinitions().insert();

        // Select the node

        final MasterDetailsEditorPagePart page = getPart().nearest( MasterDetailsEditorPagePart.class );
        final MasterDetailsContentNodePart root = page.outline().getRoot();
        final MasterDetailsContentNodePart node = root.findNode( eventDefintion );

        if( node != null )
        {
            node.select();
        }

        return eventDefintion;
    }
}
