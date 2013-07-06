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

package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.hook.core.model.ServiceWrapper;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeConstraintBehavior;
import org.eclipse.sapphire.java.JavaTypeConstraintService;
import org.eclipse.sapphire.java.JavaTypeConstraintServiceData;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;

/**
 * @author Gregory Amerson
 */
public class ServiceImplJavaTypeConstraintService extends JavaTypeConstraintService
{

    private Set<JavaTypeKind> kinds;

    private JavaTypeConstraintBehavior behavior;

    private ServiceWrapper service;

    @Override
    protected void initJavaTypeConstraintService()
    {
        super.initJavaTypeConstraintService();

        final Property property = context().find( Property.class );
        final JavaTypeConstraint javaTypeConstraintAnnotation = property.definition().getAnnotation( JavaTypeConstraint.class );

        final Set<JavaTypeKind> kind = EnumSet.noneOf( JavaTypeKind.class );

        for( JavaTypeKind k : javaTypeConstraintAnnotation.kind() )
        {
            kind.add( k );
        }

        this.kinds = kind;

        this.behavior = javaTypeConstraintAnnotation.behavior();

        this.service = context( ServiceWrapper.class );

        Listener listener = new FilteredListener<PropertyContentEvent>()
        {

            @Override
            public void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        this.service.attach( listener, "ServiceType" ); //$NON-NLS-1$
    }

    private Set<String> getServiceTypes()
    {
        JavaTypeName type = this.service.getServiceType().content( false );

        Set<String> types = new HashSet<String>();

        if( type != null )
        {
            types.add( type.qualified() + "Wrapper" ); //$NON-NLS-1$
        }

        return types;
    }

    @Override
    protected JavaTypeConstraintServiceData compute()
    {
        return new JavaTypeConstraintServiceData( this.kinds, getServiceTypes(), this.behavior );
    }

}
