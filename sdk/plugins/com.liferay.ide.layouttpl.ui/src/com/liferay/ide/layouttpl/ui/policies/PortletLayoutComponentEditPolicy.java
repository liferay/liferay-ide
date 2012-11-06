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
 *******************************************************************************/

package com.liferay.ide.layouttpl.ui.policies;

import com.liferay.ide.layouttpl.ui.cmd.PortletLayoutDeleteCommand;
import com.liferay.ide.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.layouttpl.ui.model.PortletLayout;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

/**
 * @author Greg Amerson
 */
public class PortletLayoutComponentEditPolicy extends ComponentEditPolicy
{

    protected Command createDeleteCommand( GroupRequest deleteRequest )
    {
        Object parent = getHost().getParent().getModel();
        Object child = getHost().getModel();

        if( parent instanceof LayoutTplDiagram && child instanceof PortletLayout )
        {
            return new PortletLayoutDeleteCommand( (LayoutTplDiagram) parent, (PortletLayout) child );
        }

        return super.createDeleteCommand( deleteRequest );
    }

}
