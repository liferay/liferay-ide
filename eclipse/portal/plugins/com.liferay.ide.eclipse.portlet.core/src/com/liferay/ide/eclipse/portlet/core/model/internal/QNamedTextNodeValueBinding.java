/*******************************************************************************
 * Copyright (c) 2000-2011 Accenture Services Services Pvt Ltd., All rights reserved.
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
 *    Kamesh Sampath - initial implementation
 ******************************************************************************/

package com.liferay.ide.eclipse.portlet.core.model.internal;

import javax.xml.namespace.QName;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.xml.XmlAttribute;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNamespaceResolver;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;
import org.w3c.dom.Element;

import com.liferay.ide.eclipse.portlet.core.util.PortletAppModelConstants;
import com.liferay.ide.eclipse.portlet.core.util.PortletUtil;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */

public final class QNamedTextNodeValueBinding

extends XmlValueBindingImpl

{

	private XmlPath path;
	private String[] params;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.BindingImpl#init(org.eclipse.sapphire.modeling.IModelElement,
	 * org.eclipse.sapphire.modeling.ModelProperty, java.lang.String[])
	 */
	@Override
	public void init( final IModelElement element, final ModelProperty property, final String[] params ) {
		super.init( element, property, params );
		this.params = params;
		final XmlNamespaceResolver xmlNamespaceResolver = resource().getXmlNamespaceResolver();
		this.path = new XmlPath( params[0], xmlNamespaceResolver );

		// System.out.println( "TextNodeValueBinding.init()" + this.path );
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.ValueBindingImpl#read()
	 */
	@Override
	public String read() {
		String value = null;

		final XmlElement parent = xml( false );

		if ( parent != null ) {
			XmlElement qNamedElement = parent.getChildElement( this.params[0], false );
			if ( qNamedElement != null ) {
				XmlAttribute qnsAttribute = qNamedElement.getAttribute( "qns", false );
				value = qNamedElement.getText();

				if ( value != null ) {
					value = PortletUtil.stripPrefix( value.trim() );
					QName qname = new QName( qnsAttribute.getText(), value );
					value = qname.toString();

				}
			}
		}
		// System.out.println( "QNamedTextNodeValueBinding.read()" + value );
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.ValueBindingImpl#write(java.lang.String)
	 */
	@Override
	public void write( final String value ) {
		String qNameAsString = value;
		XmlElement parent = xml( true );
		// System.out.println( "VALUE ___________________ " + qNameAsString );

		if ( qNameAsString != null && !"Q_NAME".equals( qNameAsString ) ) {
			qNameAsString = value.trim();
			QName qName = QName.valueOf( qNameAsString );
			String namespaceURI = PortletAppModelConstants.XMLNS_NS_URI;
			String qualifiedName = PortletAppModelConstants.DEFAULT_QNAME_NS_DECL;
			String localPart =
				PortletAppModelConstants.DEFAULT_QNAME_PREFIX + PortletAppModelConstants.COLON + qName.getLocalPart();
			XmlElement qNamedElement = parent.getChildElement( this.params[0], true );
			Element domNode = qNamedElement.getDomNode();
			domNode.setAttributeNS( namespaceURI, qualifiedName, qName.getNamespaceURI() );
			qNamedElement.setText( localPart );
		}
		else {
			// System.out.println( "Remove:" + params[0] + " from " + parent );
			parent.remove();
		}

		// System.out.println( "TextNodeValueBinding.write() - Parent " + xml( true ).getParent() );

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl#getXmlNode()
	 */
	@Override
	public XmlNode getXmlNode() {
		final XmlElement element = xml( false );

		if ( element != null ) {
			return element.getChildNode( this.path, false );
		}

		return null;
	}

}
