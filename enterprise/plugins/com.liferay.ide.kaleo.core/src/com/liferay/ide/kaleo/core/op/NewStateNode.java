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

package com.liferay.ide.kaleo.core.op;

import com.liferay.ide.kaleo.core.model.Node;
import com.liferay.ide.kaleo.core.model.State;
import com.liferay.ide.kaleo.core.op.internal.NewNodeNameValidationService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Gregory Amerson
 */
public interface NewStateNode extends State {

	public ElementType TYPE = new ElementType(NewStateNode.class);

	@DefaultValue(text = "New State")
	@Service(impl = NewNodeNameValidationService.class)
	public ValueProperty PROP_NAME = new ValueProperty(TYPE, Node.PROP_NAME);

}