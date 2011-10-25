/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package com.liferay.ide.eclipse.hook.core.model.internal;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNamespaceResolver;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class InvertingBooleanXmlValueBinding extends XmlValueBindingImpl

{

	private XmlPath path;

	@Override
	public void init( final IModelElement element, final ModelProperty property, final String[] params ) {
		super.init( element, property, params );

		final XmlNamespaceResolver xmlNamespaceResolver = resource().getXmlNamespaceResolver();
		this.path = new XmlPath( params[0], xmlNamespaceResolver );
	}

	@Override
	public String read() {
		String value = null;

		final XmlElement element = xml( false );

		if ( element != null ) {
			value = element.getChildNodeText( this.path );

			if ( value != null ) {
				if ( value.equalsIgnoreCase( "true" ) ) {
					value = "false";
				}
				else if ( value.equalsIgnoreCase( "false" ) ) {
					value = "true";
				}
			}
		}

		return value;
	}

	@Override
	public void write( final String value ) {
		String val = value;

		if ( val != null ) {
			if ( val.equalsIgnoreCase( "true" ) ) {
				val = "false";
			}
			else if ( val.equalsIgnoreCase( "false" ) ) {
				val = "true";
			}
		}

		xml( true ).setChildNodeText( this.path, val, true );
	}

	@Override
	public XmlNode getXmlNode() {
		final XmlElement element = xml( false );

		if ( element != null ) {
			return element.getChildNode( this.path, false );
		}

		return null;
	}

}
