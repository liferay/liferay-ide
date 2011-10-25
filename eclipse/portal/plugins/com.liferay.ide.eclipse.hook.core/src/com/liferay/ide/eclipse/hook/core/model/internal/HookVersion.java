/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.hook.core.model.internal;

import org.eclipse.sapphire.modeling.annotations.EnumSerialization;
import org.eclipse.sapphire.modeling.annotations.Label;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public enum HookVersion {
	@Label( standard = "5.2.0" )
	@EnumSerialization( primary = "5.2.0" )
	v5_2_0,

	@Label( standard = "6.0.0" )
	@EnumSerialization( primary = "6.0.0" )
	v6_0_0,

	@Label( standard = "6.1.0" )
	@EnumSerialization( primary = "6.1.0" )
	v6_1_0
}
