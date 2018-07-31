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

package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.hook.core.model.ServiceWrapper;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeConstraintBehavior;
import org.eclipse.sapphire.java.JavaTypeConstraintService;
import org.eclipse.sapphire.java.JavaTypeConstraintServiceData;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;

/**
 * @author Gregory Amerson
 */
public class ServiceImplJavaTypeConstraintService extends JavaTypeConstraintService {

	@Override
	protected JavaTypeConstraintServiceData compute() {
		return new JavaTypeConstraintServiceData(_kinds, _getServiceTypes(), _behavior);
	}

	@Override
	protected void initJavaTypeConstraintService() {
		super.initJavaTypeConstraintService();

		Property property = context().find(Property.class);

		PropertyDef propertyDef = property.definition();

		JavaTypeConstraint javaTypeConstraintAnnotation = propertyDef.getAnnotation(JavaTypeConstraint.class);

		Set<JavaTypeKind> kind = EnumSet.noneOf(JavaTypeKind.class);

		for (JavaTypeKind k : javaTypeConstraintAnnotation.kind()) {
			kind.add(k);
		}

		_kinds = kind;

		_behavior = javaTypeConstraintAnnotation.behavior();

		_service = context(ServiceWrapper.class);

		Listener listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			public void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		_service.attach(listener, "ServiceType");
	}

	private Set<String> _getServiceTypes() {
		ReferenceValue<JavaTypeName, JavaType> reference = _service.getServiceType();

		JavaTypeName type = reference.content(false);

		Set<String> types = new HashSet<>();

		if (type != null) {
			types.add(type.qualified() + "Wrapper");
		}

		return types;
	}

	private JavaTypeConstraintBehavior _behavior;
	private Set<JavaTypeKind> _kinds;
	private ServiceWrapper _service;

}