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

package com.liferay.ide.core.templates;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;

import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;

import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import org.osgi.framework.Bundle;

/**
 * @author Gregory Amerson
 */
public class TemplatesCore {

	public static TemplatesCore getDefault() {
		return _plugin;
	}

	/**
	 * look up the template find the plugin if it doesn't have an engine for that
	 * plugin, then create one then load the template with default context
	 */
	public static ITemplateOperation getTemplateOperation(String templateId) {
		TemplateModel model = _getTemplateModel(templateId);

		return new TemplateOperation(model);
	}

	private static TemplateModel _createTemplateModel(IConfigurationElement element, String pluginName) {
		TemplateModel templateModel = null;

		try {
			String id = element.getAttribute("id");

			String resource = element.getAttribute("resource");

			String templateFolder = null;

			List<TemplateVariable> paramList = new ArrayList<>();

			IConfigurationElement[] items = ((IExtension)element.getParent()).getConfigurationElements();

			for (IConfigurationElement item : items) {
				if ("templatesFolder".equals(item.getName())) {
					templateFolder = item.getAttribute("path");
				}

				if ("templateVariable".equals(item.getName())) {
					String paramName = item.getAttribute("name");

					String reqVal = item.getAttribute("required");

					paramList.add(new TemplateVariable(paramName, reqVal));
				}
			}

			Configuration config = new Configuration();

			TemplateVariable[] vars = paramList.toArray(new TemplateVariable[0]);

			templateModel = new TemplateModel(pluginName, config, id, resource, templateFolder, vars);
		}
		catch (Exception e) {
			LiferayCore.logError(e);
		}

		return templateModel;
	}

	private static TemplateModel _getTemplateModel(String templateId) {
		if (templateId == null) {
			return null;
		}

		TemplateModel model = _templateModels.get(templateId);

		if (model != null) {
			return model;
		}

		IConfigurationElement element = _getTplDefinitionElement(templateId);

		IContributor contributor = element.getContributor();

		String pluginName = contributor.getName();

		model = _createTemplateModel(element, pluginName);

		try {
			_initializeModel(model);
		}
		catch (Exception e) {
			LiferayCore.logError(e);

			model = null;
		}

		if (model != null) {
			_templateModels.put(templateId, model);
		}

		return model;
	}

	private static IConfigurationElement _getTplDefinitionElement(String templateId) {
		if (templateId == null) {
			return null;
		}

		IConfigurationElement[] elements = _getTplDefinitionElements();

		for (IConfigurationElement element : elements) {
			if ("template".equals(element.getName()) && templateId.equals(element.getAttribute("id"))) {
				return element;
			}
		}

		return null;
	}

	private static IConfigurationElement[] _getTplDefinitionElements() {
		if (_tplDefinitionElements != null) {
			return _tplDefinitionElements;
		}

		IExtensionRegistry registry = Platform.getExtensionRegistry();

		_tplDefinitionElements = registry.getConfigurationElementsFor(LiferayCore.PLUGIN_ID + ".templateDefinitions");

		return _tplDefinitionElements;
	}

	private static void _initializeModel(TemplateModel templateModel) throws Exception {
		Configuration config = templateModel.getConfig();

		String bundleId = templateModel.getBundleId();

		Bundle bundle = Platform.getBundle(bundleId);

		if (bundle == null) {
			LiferayCore.logError("Could not initialize template model: could not find bundle " + bundleId);
		}

		URL loaderRoot = bundle.getEntry(templateModel.getTemplateFolder());

		URL fileUrl = FileLocator.toFileURL(loaderRoot);

		config.setDirectoryForTemplateLoading(FileUtil.getFile(fileUrl));

		config.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);

		templateModel.setConfig(config);
	}

	private TemplatesCore() {
	}

	private static TemplatesCore _plugin;
	private static Map<String, TemplateModel> _templateModels = new HashMap<>();
	private static IConfigurationElement[] _tplDefinitionElements;

}