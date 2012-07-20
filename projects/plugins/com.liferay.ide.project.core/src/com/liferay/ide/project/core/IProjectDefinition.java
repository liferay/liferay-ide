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
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.project.core;

import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;

/**
 * @author Gregory Amerson
 */
public interface IProjectDefinition
{

    public static final String ID = "com.liferay.ide.project.core.liferayProjects";

    public String getDisplayName();

    public IProjectFacet getFacet();

    public IFacetedProjectTemplate getFacetedProjectTemplate();

    public String getFacetId();

    public int getMenuIndex();

    public String getShortName();

    public void setupNewProject( IDataModel dataModel, IFacetedProjectWorkingCopy facetedProject );

}
