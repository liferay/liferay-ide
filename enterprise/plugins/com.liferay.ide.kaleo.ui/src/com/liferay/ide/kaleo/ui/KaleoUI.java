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

package com.liferay.ide.kaleo.ui;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.kaleo.core.model.ScriptLanguageType;
import com.liferay.ide.kaleo.ui.editor.WorkflowTextEditor;
import com.liferay.ide.kaleo.ui.helpers.TextEditorHelper;
import com.liferay.ide.kaleo.ui.xml.KaleoTemplateContext;

import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Gregory Amerson
 */
public class KaleoUI extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "com.liferay.ide.kaleo.ui";

	public static ImageDescriptor createDescriptor(String pluginId, String image) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, "icons/" + image);
	}

	public static IStatus createErrorStatus(Exception e) {
		return createErrorStatus(e.getMessage(), e);
	}

	public static IStatus createErrorStatus(String string) {
		return new Status(IStatus.ERROR, PLUGIN_ID, string);
	}

	public static IStatus createErrorStatus(String msg, Exception e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
	}

	public static IStatus createInfoStatus(String msg) {
		return new Status(IStatus.INFO, PLUGIN_ID, msg, null);
	}

	public static KaleoUI getDefault() {
		return _plugin;
	}

	public static String getDefaultScriptForType(ScriptLanguageType scriptType, String nodeType) {
		Bundle bundle = getDefault().getBundle();

		URL scriptFileUrl = bundle.getEntry(
			"/scripts/default." + nodeType.toLowerCase() + "." + scriptType.toString().toLowerCase());

		try {
			return CoreUtil.readStreamToString(scriptFileUrl.openStream());
		}
		catch (Exception e) {
		}

		return null;
	}

	public static IKaleoEditorHelper getKaleoEditorHelper(String languageType) {
		IKaleoEditorHelper retval = null;

		for (IKaleoEditorHelper editoHelper : getDefault().getKaleoEditorHelpers()) {
			if (editoHelper.getLanguageType().equals(languageType)) {
				retval = editoHelper;

				break;
			}
		}

		if (retval == null) {
			retval = getDefault()._getDefaultTextEditorHelper();
		}

		return retval;
	}

	public static IKaleoEditorHelper getKaleoEditorHelperByEditorId(String editorId) {
		IKaleoEditorHelper retval = null;

		for (IKaleoEditorHelper editoHelper : getDefault().getKaleoEditorHelpers()) {
			if (editoHelper.getEditorId().equals(editorId)) {
				retval = editoHelper;

				break;
			}
		}

		return retval;
	}

	public static IPreferenceStore getPrefStore() {
		return getDefault().getPreferenceStore();
	}

	public static void logError(Exception e) {
		ILog log = getDefault().getLog();

		log.log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
	}

	public static void logError(String msg, Exception e) {
		ILog log = getDefault().getLog();

		log.log(new Status(IStatus.ERROR, PLUGIN_ID, msg, e));
	}

	public static IStatus logInfo(String msg, IStatus status) {
		return new Status(IStatus.INFO, PLUGIN_ID, msg, status.getException());
	}

	public KaleoUI() {
	}

	public ContextTypeRegistry getContextTypeRegistry() {
		if (_contextTypeRegistry == null) {
			_contextTypeRegistry = new ContributionContextTypeRegistry();
			KaleoTemplateContextType contextType = new KaleoTemplateContextType();

			_contextTypeRegistry.addContextType(contextType);
		}

		return _contextTypeRegistry;
	}

	public IKaleoEditorHelper[] getKaleoEditorHelpers() {
		if (_kaleoEditorHelpers == null) {
			IExtensionRegistry registry = Platform.getExtensionRegistry();

			IConfigurationElement[] elements = registry.getConfigurationElementsFor(IKaleoEditorHelper.EXTENSION_ID);

			try {
				List<IKaleoEditorHelper> helpers = new ArrayList<>();

				for (IConfigurationElement element : elements) {
					Object o = element.createExecutableExtension("class");

					if (o instanceof AbstractKaleoEditorHelper) {
						AbstractKaleoEditorHelper kaleoEditorHelper = (AbstractKaleoEditorHelper)o;

						kaleoEditorHelper.setLanguageType(element.getAttribute("languageType"));
						kaleoEditorHelper.setEditorId(element.getAttribute("editorId"));
						kaleoEditorHelper.setFileExtension(element.getAttribute("fileExtension"));
						kaleoEditorHelper.setContributorName(element.getContributor().getName());

						helpers.add(kaleoEditorHelper);
					}
				}

				_kaleoEditorHelpers = helpers.toArray(new IKaleoEditorHelper[0]);
			}
			catch (Exception e) {
				logError(e);
			}
		}

		return _kaleoEditorHelpers;
	}

	public ContextTypeRegistry getTemplateContextRegistry() {
		if (_contextTypeRegistry == null) {
			ContributionContextTypeRegistry registry = new ContributionContextTypeRegistry();

			for (KaleoTemplateContext contextType : KaleoTemplateContext.values()) {
				registry.addContextType(contextType.getContextTypeId());
			}

			_contextTypeRegistry = registry;
		}

		return _contextTypeRegistry;
	}

	public TemplateStore getTemplateStore() {
		if (_templateStore == null) {
			_templateStore = new ContributionTemplateStore(
				getTemplateContextRegistry(), getPreferenceStore(), _TEMPLATES_KEY);

			try {
				_templateStore.load();
			}
			catch (IOException ioe) {
				logError("Unable to load pom templates", ioe);
			}
		}

		return _templateStore;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		_plugin = null;
		super.stop(context);
	}

	private IKaleoEditorHelper _getDefaultTextEditorHelper() {
		if (_defaultEditorHelper == null) {
			_defaultEditorHelper = new TextEditorHelper();

			_defaultEditorHelper.setLanguageType("text");

			_defaultEditorHelper.setEditorId(WorkflowTextEditor.ID);

			_defaultEditorHelper.setFileExtension("txt");

			_defaultEditorHelper.setContributorName(getBundle().getSymbolicName());
		}

		return _defaultEditorHelper;
	}

	private static final String _TEMPLATES_KEY = PLUGIN_ID + ".templates";

	private static KaleoUI _plugin;

	private ContextTypeRegistry _contextTypeRegistry;
	private TextEditorHelper _defaultEditorHelper;
	private IKaleoEditorHelper[] _kaleoEditorHelpers;
	private TemplateStore _templateStore;

}