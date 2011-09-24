/*******************************************************************************
 * Copyright (c) 2000-2011 Accenture Services Pvt Ltd., All rights reserved.
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
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNamespaceResolver;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.liferay.ide.eclipse.portlet.core.util.PortletAppModelConstants;
import com.liferay.ide.eclipse.portlet.core.util.PortletModelUtil;
import com.liferay.ide.eclipse.portlet.core.util.PortletUtil;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */

public final class QNameTextNodeValueBinding extends XmlValueBindingImpl {

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
				Element domNode = qNamedElement.getDomNode();
				value = qNamedElement.getText();
				if ( value != null ) {
					String prefix = PortletUtil.stripSuffix( value.trim() );
					Attr attrib = domNode.getAttributeNode( String.format( PortletAppModelConstants.NS_DECL, prefix ) );
					if ( attrib != null ) {
						QName qname = new QName( attrib.getValue(), PortletUtil.stripPrefix( value ) );
						value = qname.toString();
					}

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
			XmlElement qNamedElement = parent.getChildElement( this.params[0], true );
			String qualifiedNodeValue = PortletModelUtil.defineNS( qNamedElement, qName );
			qNamedElement.setText( qualifiedNodeValue );
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
