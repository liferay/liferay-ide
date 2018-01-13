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

package com.liferay.ide.kaleo.core.model;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Length;
import org.eclipse.sapphire.ListProperty;

/**
 * @author Gregory Amerson
 */
public interface MustTransition extends CanTransition {

	public ElementType TYPE = new ElementType(MustTransition.class);

	@Length(min = 1)
	public ListProperty PROP_TRANSITIONS = new ListProperty(TYPE, CanTransition.PROP_TRANSITIONS);

}