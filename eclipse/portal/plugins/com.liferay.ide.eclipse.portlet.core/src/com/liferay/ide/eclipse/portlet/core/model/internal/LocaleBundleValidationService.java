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

import java.util.List;
import java.util.Locale;

import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.modeling.ModelPropertyValidationService;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.Value;

import com.liferay.ide.eclipse.portlet.core.model.IPortlet;
import com.liferay.ide.eclipse.portlet.core.model.IResourceBundle;
import com.liferay.ide.eclipse.portlet.core.util.PortletUtil;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public class LocaleBundleValidationService extends ModelPropertyValidationService<Value<String>> {

	final Locale[] AVAILABLE_LOCALES = Locale.getAvailableLocales();
	final Locale DEFAULT_LOCALE = Locale.getDefault();

	/**
	 * 
	 */
	@Override
	public Status validate() {
		final String locale = target().getText();
		final String localeDisplayString = PortletUtil.getLocaleDisplayString( locale );
		final String localeString = PortletUtil.getLocaleString( locale );
		boolean isDefaultLocale = ( locale == null || DEFAULT_LOCALE.toString().equals( locale ) );
		IPortlet portlet = nearest( IPortlet.class );
		List<IResourceBundle> resourceBundles = portlet.getResourceBundles();

		if ( locale != null ) {

			// Check for other locales
			for ( IResourceBundle iResourceBundle : resourceBundles ) {
				String bundleName = iResourceBundle.getResourceBundle().getText();
				if ( bundleName != null && bundleName.lastIndexOf( '.' ) != -1 ) {

					bundleName = bundleName.substring( bundleName.lastIndexOf( '.' ), bundleName.length() );

					if ( !isDefaultLocale ) {
						if ( bundleName.indexOf( "_" ) == -1 ) {
							return Status.createWarningStatus( Resources.bind( Resources.message, new Object[] {
								localeDisplayString, localeString } ) );
						}
						else {
							String rbLocale = bundleName.substring( bundleName.indexOf( "_" ) + 1, bundleName.length() );
							if ( !locale.equals( rbLocale ) ) {
								return Status.createWarningStatus( Resources.bind( Resources.message, new Object[] {
									localeDisplayString, localeString } ) );
							}
						}
					}

				}

			}

		}

		return Status.createOkStatus();
	}

	private static final class Resources extends NLS {

		public static String message;
		static {
			initializeMessages( LocaleBundleValidationService.class.getName(), Resources.class );
		}
	}
}
