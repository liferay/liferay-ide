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

package com.liferay.ide.layouttpl.ui;

import com.liferay.ide.layouttpl.ui.wizard.LayoutTplTemplateContextTypeIds;

import java.io.IOException;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.osgi.framework.BundleContext;

/**
 * @author Gregory Amerson
 */
public class LayoutTplUI extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "com.liferay.ide.layouttpl.ui";

	public static IStatus createErrorStatus(Exception e) {
		return createErrorStatus(e.getMessage(), e);
	}

	public static IStatus createErrorStatus(String msg, Exception e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
	}

	public static LayoutTplUI getDefault() {
		return _plugin;
	}

	public static void logError(Exception e) {
		logError(e.getMessage(), e);
	}

	public static void logError(String msg, Exception e) {
		ILog log = getDefault().getLog();

		log.log(createErrorStatus(msg, e));
	}

	public LayoutTplUI() {
	}

	public ContextTypeRegistry getTemplateContextRegistry() {
		if (_fContextTypeRegistry == null) {
			ContributionContextTypeRegistry registry = new ContributionContextTypeRegistry();

			registry.addContextType(LayoutTplTemplateContextTypeIds.NEW);

			_fContextTypeRegistry = registry;
		}

		return _fContextTypeRegistry;
	}

	public TemplateStore getTemplateStore() {
		if (_fTemplateStore == null) {
			_fTemplateStore = new ContributionTemplateStore(
				getTemplateContextRegistry(), getPreferenceStore(), "com.liferay.ide.layouttpl.ui.custom_templates");

			try {
				_fTemplateStore.load();
			}
			catch (IOException ioe) {
				logError(ioe);
			}
		}

		return _fTemplateStore;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		_plugin = null;
		super.stop(context);
	}

	private static LayoutTplUI _plugin;

	private ContributionContextTypeRegistry _fContextTypeRegistry;
	private ContributionTemplateStore _fTemplateStore;

}