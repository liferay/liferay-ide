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

import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.services.ServiceCondition;
import org.eclipse.sapphire.services.ServiceContext;
import org.eclipse.sapphire.services.ValueLabelService;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.project.core.jsf.NewLiferayJSFModuleProjectOp;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;


/**
 * @author Gregory Amerson
 */
public class ProjectProviderValueLabelService extends ValueLabelService
{

    @Override
    public String provide( String value )
    {
        ILiferayProjectProvider provider = LiferayCore.getProvider( value );

        return provider != null ? provider.getDisplayName() : value;
    }

    public static class Condition extends ServiceCondition
    {

        @Override
        public boolean applicable( final ServiceContext context )
        {
            boolean retval = false;

            final ValueProperty prop = context.find( ValueProperty.class );

            if( prop != null &&  (
                            prop.equals( NewLiferayPluginProjectOp.PROP_PROJECT_PROVIDER ) ||
                            prop.equals( NewLiferayJSFModuleProjectOp.PROP_JSF_PROJECT_PROVIDER ) ||
                            prop.equals( NewLiferayModuleProjectOp.PROP_PROJECT_PROVIDER )
                            ) )
            {
                retval = true;
            }

            return retval;
        }
    }

}
