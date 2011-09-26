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

import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyValidationService;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.Value;

import com.liferay.ide.eclipse.portlet.core.model.IEventDefinition;
import com.liferay.ide.eclipse.portlet.core.model.IPublicRenderParameter;
import com.liferay.ide.eclipse.portlet.core.model.IQName;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public class NameOrQnameValidationService extends ModelPropertyValidationService<Value<String>> {

	String[] params;

	@Override
	public void init( IModelElement element, ModelProperty property, String[] params ) {
		super.init( element, property, params );
		this.params = params;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.ModelPropertyValidationService#validate()
	 */
	@Override
	public Status validate() {
		final String elementLabel =
			element().getModelElementType().getLabel( false, CapitalizationType.FIRST_WORD_ONLY, false );
		IEventDefinition eventDefinition = null;
		IPublicRenderParameter publicRenderParameter = null;
		IQName iqName = null;
		String name = null;
		String nsURI = null;
		String localPart = null;
		if ( element() instanceof IQName ) {
			iqName = (IQName) element();
			nsURI = iqName.getNamespaceURI().getText( false );
			localPart = iqName.getLocalPart().getText( false );
		}

		if ( element() instanceof IEventDefinition ) {
			eventDefinition = (IEventDefinition) element();
			name = eventDefinition.getName().getContent( false );
		}

		if ( element() instanceof IPublicRenderParameter ) {
			publicRenderParameter = (IPublicRenderParameter) element();
			name = publicRenderParameter.getName().getContent( false );
		}

		if ( isEmptyOrNull( name ) && isEmptyOrNull( nsURI ) && isEmptyOrNull( localPart ) ) {
			return Status.createErrorStatus( Resources.bind( Resources.message, elementLabel ) );
		}
		else if ( isEmptyOrNull( name ) && ( isEmptyOrNull( nsURI ) || isEmptyOrNull( localPart ) ) ) {
			return Status.createErrorStatus( Resources.bind( Resources.invalidQname, elementLabel ) );
		}

		return Status.createOkStatus();
	}

	/**
	 * @param text
	 * @return
	 */
	private boolean isEmptyOrNull( String text ) {
		if ( text == null || text.trim().length() == 0 ) {
			return true;
		}
		return false;
	}

	private static final class Resources extends NLS {

		public static String message;
		public static String invalidQname;

		static {
			initializeMessages( NameOrQnameValidationService.class.getName(), Resources.class );
		}
	}

}
