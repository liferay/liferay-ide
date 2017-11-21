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

import com.liferay.ide.kaleo.core.model.internal.ExecutionTypePossibleValuesService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Gregory Amerson
 */
public interface Executable extends Element {

	public String DEFAULT_EXECUTION_TYPE = "onEntry";

	public ElementType TYPE = new ElementType(Executable.class);

	public Value<ExecutionType> getExecutionType();

	public void setExecutionType(ExecutionType executionType);

	public void setExecutionType(String executionType);

	@Label(standard = "execution type")
	@Required
	@Service(impl = ExecutionTypePossibleValuesService.class)
	@Type(base = ExecutionType.class)
	@XmlBinding(path = "execution-type")
	public ValueProperty PROP_EXECUTION_TYPE = new ValueProperty(TYPE, "ExecutionType");

}