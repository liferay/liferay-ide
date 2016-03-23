/*******************************************************************************
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
 *
 *******************************************************************************/

package com.liferay.ide.project.core.upgrade;

import com.liferay.ide.project.core.model.HasLiferayRuntime;
import com.liferay.ide.project.core.model.NamedItem;
import com.liferay.ide.project.core.model.internal.LeastVersionRuntimeValidationService;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.Length;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Simon Jiang
 */
public interface UpgradeLiferayProjectsOp extends ExecutableElement, HasLiferayRuntime
{

    ElementType TYPE = new ElementType( UpgradeLiferayProjectsOp.class );


    // *** Project Name ***

    @Type( base = NamedItem.class )
    ListProperty PROP_SELECTED_PROJECTS = new ListProperty( TYPE, "SelectedProjects" );

    ElementList<NamedItem> getSelectedProjects();

    // *** Target Runtime Name ***

    @Service( impl = LeastVersionRuntimeValidationService.class )
    ValueProperty PROP_RUNTIME_NAME = new ValueProperty( TYPE, HasLiferayRuntime.PROP_RUNTIME_NAME ); 

    // *** Selected Upgrade Action Name ***

    @Type( base = NamedItem.class )
    @Length( min = 1 )
    ListProperty PROP_SELECTED_ACTIONS = new ListProperty( TYPE, "SelectedActions" );

    ElementList<NamedItem> getSelectedActions();


    @DelegateImplementation( UpgradeLiferayProjectsOpMethods.class )
    Status execute( ProgressMonitor monitor );
}
