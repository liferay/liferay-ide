/*******************************************************************************
 * Copyright (c) 2000-2011 Accenture Services Pvt. Ltd., All rights reserved.
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

import java.util.Locale;

import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public class LocaleTextNodeValueBinding extends XmlValueBindingImpl {

	final Locale[] AVAILABLE_LOCALES = Locale.getAvailableLocales();

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.ValueBindingImpl#read()
	 */
	@Override
	public String read() {
		String value = null;

		final XmlElement element = xml( false );

		if ( element != null ) {

			value = xml( true ).getText();

			// System.out.println( "Reading VALUE ___________________ " + value );

			if ( value != null ) {
				value = value.trim();
				for ( int i = 0; i < AVAILABLE_LOCALES.length; i++ ) {
					Locale locale = AVAILABLE_LOCALES[i];
					if ( value.equals( locale.toString() ) ) {
						value = locale.getDisplayName();
						break;
					}
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
	public void write( String value ) {
		String val = value;

		// System.out.println( "VALUE ___________________ " + val );

		if ( val != null ) {
			val = value.trim();
			for ( int i = 0; i < AVAILABLE_LOCALES.length; i++ ) {
				Locale locale = AVAILABLE_LOCALES[i];
				if ( value.equals( locale.toString() ) ) {
					value = locale.toString();
					break;
				}
			}
		}

		// System.out.println( "TextNodeValueBinding.write() - Parent " + xml( true ).getParent() );

		xml( true ).setText( val );

	}

}
