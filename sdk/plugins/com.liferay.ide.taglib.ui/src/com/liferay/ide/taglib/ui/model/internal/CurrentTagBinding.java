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
 *******************************************************************************/

package com.liferay.ide.taglib.ui.model.internal;

import com.liferay.ide.taglib.ui.model.Tag;

import org.eclipse.sapphire.modeling.ElementBindingImpl;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Resource;

/**
 * @author Gregory Amerson
 */
public class CurrentTagBinding extends ElementBindingImpl
{

    @Override
    public Resource read()
    {
        return null;
    }

    @Override
    public ModelElementType type( Resource resource )
    {
        return Tag.TYPE;
    }

}
