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

import com.liferay.ide.project.core.ValidationPreferences;
import com.liferay.ide.xml.search.ui.util.ValidatorUtil;

import java.util.Objects;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jst.j2ee.internal.common.J2EECommonMessages;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.search.core.util.DOMUtils;
import org.eclipse.wst.xml.search.editor.references.IXMLReference;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Kuo Zhang
 */
@SuppressWarnings("restriction")
public class ServiceBuilderDescriptorValidator extends LiferayBaseValidator {

	@Override
	protected boolean validateSyntax(
		IXMLReference reference, IDOMNode node, IFile file, IValidator validator, IReporter reporter,
		boolean batchMode) {

		int severity = getServerity(ValidationPreferences.ValidationType.SYNTAX_INVALID, file);

		if (severity == ValidationMessage.IGNORE) {
			return true;
		}

		String validationMsg = null;

		if (node.getNodeType() == Node.TEXT_NODE) {
			Node parentNode = node.getParentNode();

			String parentNodeName = parentNode.getNodeName();

			if (parentNodeName.equals("namespace") && !ValidatorUtil.isValidNamespace(DOMUtils.getNodeValue(node))) {
				validationMsg = getMessageText(ValidationPreferences.ValidationType.SYNTAX_INVALID, node);
			}
		}
		else if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
			Attr attrNode = (Attr)node;

			Element element = attrNode.getOwnerElement();

			if (Objects.equals("package-path", node.getNodeName()) &&
				Objects.equals("service-builder", element.getNodeName())) {

				String nodeValue = DOMUtils.getNodeValue(node);

				if (nodeValue != null) {

					// Use standard java conventions to validate the package
					// name

					IStatus javaStatus = JavaConventions.validatePackageName(
						nodeValue, CompilerOptions.VERSION_1_7, CompilerOptions.VERSION_1_7);

					if ((javaStatus.getSeverity() == IStatus.ERROR) || (javaStatus.getSeverity() == IStatus.WARNING)) {
						validationMsg = J2EECommonMessages.ERR_JAVA_PACAKGE_NAME_INVALID + javaStatus.getMessage();
					}
				}
			}
		}

		if (validationMsg != null) {
			String liferayPluginValidationType = getLiferayPluginValidationType(
				ValidationPreferences.ValidationType.SYNTAX_INVALID, file);

			addMessage(
				node, file, validator, reporter, batchMode, validationMsg, severity, liferayPluginValidationType);

			return false;
		}

		return true;
	}

}