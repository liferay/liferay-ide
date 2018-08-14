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

package com.liferay.ide.portlet.core.model;

import com.liferay.ide.portlet.core.model.internal.NameOrQnameValidationService;
import com.liferay.ide.portlet.core.model.internal.QNameLocalPartValueBinding;
import com.liferay.ide.portlet.core.model.internal.QNamespaceValueBinding;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Kamesh Sampath
 */
public interface QName extends Element {

	public ElementType TYPE = new ElementType(QName.class);

	public Value<String> getLocalPart();

	public Value<String> getNamespaceURI();

	public void setLocalPart(String value);

	public void setNamespaceURI(String value);

	@CustomXmlValueBinding(impl = QNameLocalPartValueBinding.class, params = {"qname", "localpart"})
	@Label(standard = "Local Part")
	@Service(impl = NameOrQnameValidationService.class)
	@XmlBinding(path = "qname")
	public ValueProperty PROP_LOCAL_PART = new ValueProperty(TYPE, "LocalPart");

	@CustomXmlValueBinding(impl = QNamespaceValueBinding.class, params = "qname")
	@Label(standard = "Namespace URI")
	@Service(impl = NameOrQnameValidationService.class)
	@XmlBinding(path = "qname")
	public ValueProperty PROP_NAMESPACE_URI = new ValueProperty(TYPE, "NamespaceURI");

}