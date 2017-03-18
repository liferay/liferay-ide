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

package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.project.core.modules.fragment.NewModuleFragmentOp;

import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.services.ServiceContext;

/**
 * @author Simon Jiang
 */

public class FragmentProviderValueServiceCondition extends ProviderValueServerConditon<NewModuleFragmentOp>
{

    @Override
    protected ValueProperty getProperty( final ServiceContext context )
    {
        return NewModuleFragmentOp.PROP_PROJECT_PROVIDER;
    }

}
