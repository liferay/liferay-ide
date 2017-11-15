/**
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
 */

package com.liferay.ide.portlet.core.model;

import org.eclipse.sapphire.modeling.annotations.EnumSerialization;
import org.eclipse.sapphire.modeling.annotations.Label;

/**
 * @author Kamesh Sampath
 */
@Label(standard = "Transport Gurantee")
public enum TransportGuarantee {

	@Label(standard = "NONE")
	@EnumSerialization(primary = "NONE")
	NONE,

	@Label(standard = "INTEGRAL")
	@EnumSerialization(primary = "INTEGRAL")
	INTEGRAL,

	@Label(standard = "CONFIDENTIAL")
	@EnumSerialization(primary = "CONFIDENTIAL")
	CONFIDENTIAL

}