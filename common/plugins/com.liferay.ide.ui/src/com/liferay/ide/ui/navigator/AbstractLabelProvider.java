/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.ui.navigator;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;


/**
 * @author Gregory Amerson
 */
public abstract class AbstractLabelProvider extends LabelProvider
{

    private final ImageRegistry imageRegistry;
    
    public AbstractLabelProvider()
    {
        super();

        this.imageRegistry = new ImageRegistry();
        
        initalizeImageRegistry( this.imageRegistry );
    }

    @Override
    public void dispose()
    {
        this.imageRegistry.dispose();
    }

    protected ImageRegistry getImageRegistry()
    {
        return this.imageRegistry;
    }

    protected abstract void initalizeImageRegistry(ImageRegistry registry);
}
