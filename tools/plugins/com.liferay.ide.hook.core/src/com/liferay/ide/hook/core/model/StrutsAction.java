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

package com.liferay.ide.hook.core.model;

import com.liferay.ide.hook.core.model.internal.StrutsActionPathPossibleValuesCacheService;
import com.liferay.ide.hook.core.model.internal.StrutsActionPathPossibleValuesService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeConstraintBehavior;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Kamesh Sampath
 */
@Image(path = "images/elcl16/action_url_16x16.png")
public interface StrutsAction extends Element {

	public ElementType TYPE = new ElementType(StrutsAction.class);

	public ReferenceValue<JavaTypeName, JavaType> getStrutsActionImpl();

	public Value<String> getStrutsActionPath();

	public void setStrutsActionImpl(JavaTypeName value);

	public void setStrutsActionImpl(String value);

	public void setStrutsActionPath(String value);

	@JavaTypeConstraint(
		behavior = JavaTypeConstraintBehavior.AT_LEAST_ONE, kind = JavaTypeKind.CLASS,
		type = {"com.liferay.portal.kernel.struts.StrutsAction", "com.liferay.portal.kernel.struts.StrutsPortletAction"}
	)
	@Label(standard = "Struts Action Impl")
	@MustExist
	@Reference(target = JavaType.class)
	@Required
	@Type(base = JavaTypeName.class)
	@XmlBinding(path = "struts-action-impl")
	public ValueProperty PROP_STRUTS_ACTION_IMPL = new ValueProperty(TYPE, "StrutsActionImpl");

	@Label(standard = "Struts Action Path")
	@Required
	@Services(
		{
			@Service(context = Service.Context.METAMODEL, impl = StrutsActionPathPossibleValuesCacheService.class),
			@Service(impl = StrutsActionPathPossibleValuesService.class)
		}
	)
	@XmlBinding(path = "struts-action-path")
	public ValueProperty PROP_STRUTS_ACTION_PATH = new ValueProperty(TYPE, "StrutsActionPath");

}