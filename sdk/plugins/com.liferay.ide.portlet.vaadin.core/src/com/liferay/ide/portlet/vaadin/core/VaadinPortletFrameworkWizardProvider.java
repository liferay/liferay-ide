/*******************************************************************************
 * Copyright (c) 2010-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.portlet.vaadin.core;

import com.liferay.ide.project.core.AbstractPortletFrameworkWizardProvider;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;

/**
 * @author Greg Amerson
 */
public class VaadinPortletFrameworkWizardProvider extends AbstractPortletFrameworkWizardProvider
{

    public VaadinPortletFrameworkWizardProvider()
    {
        super();
    }

    public IStatus configureNewProject( IDataModel dataModel, IFacetedProjectWorkingCopy facetedProject )
    {
        // nothing todo for vaadin projects

        return Status.OK_STATUS;
    }

    public void reinitialize()
    {
        // do nothing
    }

}
