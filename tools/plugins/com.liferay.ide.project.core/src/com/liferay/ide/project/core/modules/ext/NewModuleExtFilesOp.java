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

package com.liferay.ide.project.core.modules.ext;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Charles Wu
 * @author Seiphon Wang
 */
public interface NewModuleExtFilesOp extends NewModuleExtOp {

	public ElementType TYPE = new ElementType(NewModuleExtFilesOp.class);

	@DelegateImplementation(NewModuleExtFilesOpMethods.class)
	@Override
	public Status execute(ProgressMonitor monitor);

	public Value<String> getModuleExtProjectName();

	public void setModuleExtProjectName(String value);

	@Listeners(ModuleExtProjectNameSelectionChangedListener.class)
	@Service(impl = ModuleExtProjectNamePossibleValuesService.class)
	public ValueProperty PROP_MODULE_EXT_PROJECT_NAME = new ValueProperty(TYPE, "ModuleExtProjectName");

}