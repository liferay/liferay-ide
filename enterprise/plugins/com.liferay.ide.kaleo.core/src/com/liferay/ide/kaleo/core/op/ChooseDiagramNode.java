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

import com.liferay.ide.kaleo.core.model.WorkflowNode;
import com.liferay.ide.kaleo.core.model.internal.TransitionPossibleValuesService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Gregory Amerson
 */
@Image(path = "images/arrow_16x16.png")
@Label(standard = "existing node")
public interface ChooseDiagramNode extends WorkflowNode {

	public ElementType TYPE = new ElementType(ChooseDiagramNode.class);

	@Service(impl = TransitionPossibleValuesService.class)
	public ValueProperty PROP_NAME = new ValueProperty(TYPE, WorkflowNode.PROP_NAME);

}