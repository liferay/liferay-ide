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

package com.liferay.ide.portlet.core.model.internal;

import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

/**
 * @author kamesh.sampath
 */
public class ChoiceValueBinding extends XmlValueBindingImpl {

	@Override
	public XmlNode getXmlNode() {
		XmlElement parent = xml();

		XmlElement element = parent.getChildElement(params[1], false);

		if (element != null) {
			return element;
		}

		element = parent.getChildElement(params[1], false);

		if (element != null) {
			return element;
		}

		return null;
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.sapphire.modeling.BindingImpl#init(org.eclipse.sapphire.modeling.
	 *      IModelElement, org.eclipse.sapphire.modeling.ModelProperty,
	 *      java.lang.String[])
	 */
	@Override
	public void init(Property property) {
		super.init(property);

		this.params = property.definition().getAnnotation(CustomXmlValueBinding.class).params();
		this.path = new XmlPath(params[0], resource().getXmlNamespaceResolver());
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.sapphire.modeling.ValueBindingImpl#read()
	 */
	@Override
	public String read() {
		XmlElement parent = xml(false);

		// System.out.println( "ChoiceValueBinding.read() - \n" + parent );

		String value = null;

		if (parent != null) {

			// System.out.println( "ChoiceValueBinding.read()" + params[0] );

			XmlElement param1Element = parent.getChildElement(params[1], false);
			XmlElement param2Element = parent.getChildElement(params[2], false);

			if ((param1Element != null) && params[0].equals(params[1])) {

				// System.out.println( "ChoiceValueBinding.read() - \n" + eventNameElement );

				value = param1Element.getText();
			}
			else if ((param2Element != null) && params[0].equals(params[2])) {

				// System.out.println( "ChoiceValueBinding.read() - \n" + eventQNameElement );

				value = param2Element.getText();
			}
		}

		return value;
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.sapphire.modeling.ValueBindingImpl#write(java.lang.String)
	 */
	@Override
	public void write(String value) {
		XmlElement parent = xml(true);

		// System.out.println( "EventDefinitionValueBinding.write()" + parent );

		XmlElement param1Element = parent.getChildElement(params[1], false);
		XmlElement param2Element = parent.getChildElement(params[2], false);

		if ((param1Element != null) && params[0].equals(params[1])) {
			parent.removeChildNode(params[2]);
		}
		else if ((param2Element != null) && params[0].equals(params[2])) {
			parent.removeChildNode(params[1]);
		}

		parent.setChildNodeText(this.path, value, true);
	}

	private String[] params;
	private XmlPath path;

}