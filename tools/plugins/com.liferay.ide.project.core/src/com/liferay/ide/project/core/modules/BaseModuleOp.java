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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.project.core.model.ProjectName;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.annotations.AbsolutePath;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;

/**
 * @author Terry Jia
 */
public interface BaseModuleOp extends ExecutableElement
{

    ElementType TYPE = new ElementType( BaseModuleOp.class );

    // *** InitialSelectionPath ***

    @Type( base = Path.class )
    @AbsolutePath
    @ValidFileSystemResourceType( FileSystemResourceType.FOLDER )
    ValueProperty PROP_INITIAL_SELECTION_PATH = new ValueProperty( TYPE, "InitialSelectionPath" );

    Value<Path> getInitialSelectionPath();
    void setInitialSelectionPath( String value );
    void setInitialSelectionPath( Path value );

    // *** FinalProjectName ***

    @DefaultValue( text = "${ProjectName}" )
    ValueProperty PROP_FINAL_PROJECT_NAME = new ValueProperty( TYPE, "FinalProjectName" );

    Value<String> getFinalProjectName();
    void setFinalProjectName( String value );

    // *** ProjectNames ***

    @Type( base = ProjectName.class )
    ListProperty PROP_PROJECT_NAMES = new ListProperty( TYPE, "ProjectNames" );

    ElementList<ProjectName> getProjectNames();

}
