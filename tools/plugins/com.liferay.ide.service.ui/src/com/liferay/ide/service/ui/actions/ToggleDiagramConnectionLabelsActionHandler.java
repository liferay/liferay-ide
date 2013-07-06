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
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.service.ui.actions;

import com.liferay.ide.service.core.model.ServiceBuilder;

import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;

public class ToggleDiagramConnectionLabelsActionHandler extends SapphireActionHandler
{

    protected boolean checked = true;

    @Override
    public boolean isChecked()
    {
        return checked;
    }

    @Override
    protected Object run( SapphireRenderingContext context )
    {
        checked = !checked;

        ServiceBuilder serviceBuilder = (ServiceBuilder) context.getPart().getModelElement();
        serviceBuilder.setShowRelationshipLabels( checked );
        serviceBuilder.refresh();

        return null;
    }

}
