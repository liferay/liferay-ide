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

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;

import com.liferay.ide.eclipse.portlet.core.util.PortletUtil;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */

public final class ResourceBundleValueBinding

extends XmlValueBindingImpl

{

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

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.ValueBindingImpl#read()
	 */
	@Override
	public String read() {
		String value = null;
		final XmlElement parent = xml( false );
		if ( this.params != null && parent != null ) {
			final XmlElement element = parent.getChildElement( this.params[0], false );

			if ( element != null ) {

				value = element.getText();

				// System.out.println( "Reading VALUE ___________________ " + value );

				if ( value != null ) {
					value = value.trim();
				}
			}
		}
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.ValueBindingImpl#write(java.lang.String)
	 */
	@Override
	public void write( final String value ) {
		String val = value;
		IProject project = element().adapt( IProject.class );

		// System.out.println( "VALUE ___________________ " + val );

		if ( val != null ) {
			val = PortletUtil.convertIOToJavaFileName( project, value.trim() );
		}

		// System.out.println( "TextNodeValueBinding.write() - Parent " + xml( true ).getParent() );
		final XmlElement element = xml( false ).getChildElement( this.params[0], true );
		element.setText( val );
	}

}
