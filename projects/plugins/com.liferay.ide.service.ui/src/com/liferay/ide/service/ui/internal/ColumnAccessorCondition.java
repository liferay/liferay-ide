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

package com.liferay.ide.service.ui.internal;

import com.liferay.ide.service.core.model.IServiceBuilder;
import com.liferay.ide.service.core.model.IServiceBuilder610;

import org.eclipse.sapphire.ui.SapphireModelCondition;

/**
 * @author Gregory Amerson
 */
public class ColumnAccessorCondition extends SapphireModelCondition
{

	@Override
	protected boolean evaluate( )
	{
		IServiceBuilder serviceBuilderElement = this.getPart().getLocalModelElement().nearest( IServiceBuilder.class );
		// accessor attribute for Column entity is only available in 6.1.0
		return serviceBuilderElement instanceof IServiceBuilder610;
	}

}
