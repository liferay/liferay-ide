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

package com.liferay.ide.xml.search.ui.validators;

import com.liferay.ide.project.core.ValidationPreferences.ValidationType;

import org.eclipse.core.resources.IFile;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.search.core.util.DOMUtils;
import org.eclipse.wst.xml.search.editor.references.IXMLReference;

import org.w3c.dom.Node;

/**
 * @author Kuo Zhang
 */
@SuppressWarnings("restriction")
public class LiferayPortletDescriptorValidator extends LiferayBaseValidator {

	public static final String MESSAGE_ENTRY_WEIGHT_SYNTAX_INVALID = Msgs.entryWeightSyntaxInvalid;

	@Override
	protected boolean validateSyntax(
		IXMLReference reference, IDOMNode node, IFile file, IValidator validator, IReporter reporter,
		boolean batchMode) {

		int severity = getServerity(ValidationType.SYNTAX_INVALID, file);

		if (severity == ValidationMessage.IGNORE) {
			return true;
		}

		Node parentNode = node.getParentNode();

		if ("control-panel-entry-weight".equals(parentNode.getNodeName())) {
			String validationMsg = null;

			String nodeValue = DOMUtils.getNodeValue(node);

			if (nodeValue != null) {
				try {
					Double.parseDouble(nodeValue);
				}
				catch (NumberFormatException nfe) {
					validationMsg = NLS.bind(MESSAGE_ENTRY_WEIGHT_SYNTAX_INVALID, nodeValue);
				}
			}

			if (validationMsg != null) {
				String liferayPluginValidationType = getLiferayPluginValidationType(
					ValidationType.SYNTAX_INVALID, file);

				addMessage(
					node, file, validator, reporter, batchMode, validationMsg, severity, liferayPluginValidationType);

				return false;
			}
		}

		return true;
	}

	private static class Msgs extends NLS {

		public static String entryWeightSyntaxInvalid;

		static {
			initializeMessages(LiferayPortletDescriptorValidator.class.getName(), Msgs.class);
		}

	}

}