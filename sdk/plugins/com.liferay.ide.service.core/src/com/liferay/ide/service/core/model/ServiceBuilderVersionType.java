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
 *    Kamesh Sampath - initial implementation
 *    Gregory Amerson - IDE-355
 *******************************************************************************/

package com.liferay.ide.service.core.model;

import org.eclipse.sapphire.modeling.annotations.EnumSerialization;
import org.eclipse.sapphire.modeling.annotations.Label;

/**
 * @author Gregory Amerson
 */
public enum ServiceBuilderVersionType
{

	@Label( standard = "6.0.0" )
	@EnumSerialization( primary = "6.0.0" )
	v6_0_0,

	@Label( standard = "6.1.0" )
	@EnumSerialization( primary = "6.1.0" )
	v6_1_0

}
