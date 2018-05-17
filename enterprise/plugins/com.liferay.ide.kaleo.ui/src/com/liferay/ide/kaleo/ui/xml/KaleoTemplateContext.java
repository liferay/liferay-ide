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

package com.liferay.ide.kaleo.ui.xml;

import com.liferay.ide.kaleo.ui.KaleoUI;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.templates.Template;

import org.w3c.dom.Node;

/**
 * @author Gregory Amerson
 */
public enum KaleoTemplateContext {

	DOCUMENT("#document"), UNKNOWN("unknown"), WORKFLOW_DEFINITION("workflow-definition");

	public static KaleoTemplateContext fromId(String contextTypeId) {
		for (KaleoTemplateContext context : values()) {
			if (context.getContextTypeId().equals(contextTypeId)) {
				return context;
			}
		}

		return UNKNOWN;
	}

	public static KaleoTemplateContext fromNodeName(String idSuffix) {
		for (KaleoTemplateContext context : values()) {
			if (context.getNodeName().equals(idSuffix)) {
				return context;
			}
		}

		return UNKNOWN;
	}

	public String getContextTypeId() {
		return _PREFIX + _nodeName;
	}

	/**
	 * Return templates depending on the context type.
	 */
	public Template[] getTemplates(IProject eclipsePrj, Node node, String prefix) {
		Collection<Template> templates = new ArrayList<>();

		try {
			addTemplates(eclipsePrj, templates, node, prefix);
		}
		catch (CoreException ce) {
		}

		return templates.toArray(new Template[templates.size()]);
	}

	protected void addTemplates(IProject eclipsePrj, Collection<Template> templates, Node currentNode, String prefix)
		throws CoreException {
	}

	protected String getNodeName() {
		return _nodeName;
	}

	private KaleoTemplateContext(String nodeName) {
		_nodeName = nodeName;
	}

	private static final String _PREFIX = KaleoUI.PLUGIN_ID + ".templates.contextType.";

	private final String _nodeName;

}