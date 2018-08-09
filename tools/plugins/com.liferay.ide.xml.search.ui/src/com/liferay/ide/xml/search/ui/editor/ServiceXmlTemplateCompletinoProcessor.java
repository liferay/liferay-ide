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

package com.liferay.ide.xml.search.ui.editor;

import com.liferay.ide.xml.search.ui.LiferayXMLSearchUI;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.swt.graphics.Image;

import org.osgi.framework.Bundle;

/**
 * @author Kuo Zhang
 */
public class ServiceXmlTemplateCompletinoProcessor extends TemplateCompletionProcessor {

	@Override
	protected TemplateContextType getContextType(ITextViewer viewer, IRegion region) {
		TemplateContextType type = null;

		ContextTypeRegistry registry = getTemplateContextRegistry();

		if (registry != null) {
			type = registry.getContextType(ServiceXmlContextType.ID_SERVICE_XML_TAG);
		}

		return type;
	}

	@Override
	protected Image getImage(Template template) {
		LiferayXMLSearchUI plugin = LiferayXMLSearchUI.getDefault();

		Bundle bundle = plugin.getBundle();

		URL url = bundle.getEntry("/icons/service_template.gif");

		ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);

		return imageDescriptor.createImage();
	}

	protected ContextTypeRegistry getTemplateContextRegistry() {
		LiferayXMLSearchUI plugin = LiferayXMLSearchUI.getDefault();

		return plugin.getContextTypeRegistry();
	}

	@Override
	protected Template[] getTemplates(String contextTypeId) {
		Template[] templates = null;

		TemplateStore store = getTemplateStore();

		if (store != null) {
			templates = store.getTemplates(contextTypeId);
		}

		return templates;
	}

	protected TemplateStore getTemplateStore() {
		LiferayXMLSearchUI plugin = LiferayXMLSearchUI.getDefault();

		return plugin.getServiceXmlTemplateStore();
	}

}