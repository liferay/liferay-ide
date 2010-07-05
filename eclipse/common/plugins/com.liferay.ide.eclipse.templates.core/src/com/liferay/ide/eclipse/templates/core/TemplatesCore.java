package com.liferay.ide.eclipse.templates.core;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class TemplatesCore extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.liferay.ide.eclipse.templates.core";

	// The shared instance
	private static TemplatesCore plugin;
	
	private static Map<String, TemplateModel> templateModels = new HashMap<String, TemplateModel>();

	private static Map<String, TemplateModel> pluginModels = new HashMap<String, TemplateModel>();

	private static IConfigurationElement[] tplDefinitionElements;

	/**
	 * The constructor
	 */
	public TemplatesCore() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static TemplatesCore getDefault() {
		return plugin;
	}


	public static ITemplateOperation getTemplateOperation(String templateId) {
		return getTemplateOperation(templateId, null);
	}

	public static ITemplateOperation getTemplateOperation(String templateId, Object context) {
		// look up the template
		// find the plugin
		// if it doesn't have an engine for that plugin, then create one
		// then load the template with default context

		TemplateModel model = getTemplateModel(templateId);

		return new TemplateOperation(model);
	}

	private static TemplateModel getTemplateModel(String templateId) {
		if (templateId == null) {
			return null;
		}

		TemplateModel model = templateModels.get(templateId);
		if (model == null) {
			IConfigurationElement element = getTplDefinitionElement(templateId);

			String pluginName = element.getContributor().getName();
			TemplateModel pluginModel = pluginModels.get(pluginName);

			if (pluginModel == null) {
				pluginModel = createPluginModel(element, pluginName);
				try {
					initializeModel(pluginModel);
				}
				catch (Exception e) {
					logError(e);
					pluginModel = null;
				}

				if (pluginModel != null) {
					pluginModels.put(pluginName, pluginModel);
				}
			}

			if (pluginModel != null) {
				templateModels.put(templateId, pluginModel);
				model = pluginModel;
			}
		}

		return model;
	}

	private static void initializeModel(TemplateModel templateModel)
		throws Exception {

		VelocityEngine engine = templateModel.getEngine();

		engine.setProperty("resource.loader", "url");
		engine.setProperty("url.resource.loader.class", "org.apache.velocity.runtime.resource.loader.URLResourceLoader");
		URL loaderRoot = Platform.getBundle(templateModel.bundleId).getEntry(templateModel.templateFolder);
		engine.setProperty("url.resource.loader.root", loaderRoot.toURI().toASCIIString());
		engine.setProperty("url.resource.loader.cache", true);
		// properties.put("url.resource.loader.modificationCheckInterval", 10);

		templateModel.getEngine().init();
	}

	private static IConfigurationElement getTplDefinitionElement(String templateId) {
		if (templateId == null) {
			return null;
		}

		IConfigurationElement[] elements = getTplDefinitionElements();

		for (IConfigurationElement element : elements) {
			if ("template".equals(element.getName())) {
				if (templateId.equals(element.getAttribute("id"))) {
					return element;
				}
			}
		}

		return null;
	}

	private static TemplateModel createPluginModel(IConfigurationElement element, String pluginName) {
		TemplateModel templateModel = null;
		try {
			String id = element.getAttribute("id");
			String name = element.getAttribute("name");
			String resource = element.getAttribute("resource");
			String templateFolder = null;

			IConfigurationElement[] items = ((IExtension) element.getParent()).getConfigurationElements();

			for (IConfigurationElement item : items) {
				if ("templatesFolder".equals(item.getName())) {
					templateFolder = item.getAttribute("path");
				}
			}

			VelocityEngine velocityEngine = new VelocityEngine();
			templateModel = new TemplateModel(pluginName, id, name, resource, templateFolder, velocityEngine);
		}
		catch (Exception e) {
			TemplatesCore.logError(e);
		}
		return templateModel;
	}

	public static void logError(Exception e) {
		getDefault().getLog().log(createErrorStatus(e));
	}

	public static IStatus createErrorStatus(Exception e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e);
	}

	private static IConfigurationElement[] getTplDefinitionElements() {
		if (tplDefinitionElements == null) {
			tplDefinitionElements =
				Platform.getExtensionRegistry().getConfigurationElementsFor(PLUGIN_ID + ".templateDefinition");
		}

		return tplDefinitionElements;
	}

}
