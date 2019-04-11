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

package com.liferay.ide.portlet.ui.editor.internal;

import com.liferay.ide.core.model.internal.GenericResourceBundlePathService;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.portlet.core.model.Portlet;
import com.liferay.ide.portlet.core.model.PortletInfo;
import com.liferay.ide.portlet.core.model.SupportedLocales;
import com.liferay.ide.portlet.core.model.internal.LocaleBundleValidationService;
import com.liferay.ide.portlet.core.util.PortletUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.DisposeEvent;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.Status.Severity;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 * @author Kuo Zhang
 * @author Simon Jiang
 */
public class CreatePortletResourceBundleActionHandler extends AbstractResourceBundleActionHandler {

	@Override
	public void init(SapphireAction action, ActionHandlerDef def) {
		super.init(action, def);

		Element element = getModelElement();

		listener = new FilteredListener<PropertyEvent>() {

			@Override
			protected void handleTypedEvent(PropertyEvent event) {
				refreshEnablementState();
			}

		};

		element.attach(listener, property().name());
		element.attach(listener, Portlet.PROP_SUPPORTED_LOCALES.name());
		element.attach(
			listener, Portlet.PROP_SUPPORTED_LOCALES.name() + "/" + SupportedLocales.PROP_SUPPORTED_LOCALE.name());

		Listener listen = new Listener() {

			public void handle(Event event) {
				if (event instanceof DisposeEvent) {
					getModelElement().detach(listener, property().name());
					getModelElement().detach(listener, Portlet.PROP_SUPPORTED_LOCALES.name());
					getModelElement().detach(
						listener,
						Portlet.PROP_SUPPORTED_LOCALES.name() + "/" + SupportedLocales.PROP_SUPPORTED_LOCALE.name());
				}
			}

		};

		attach(listen);
	}

	@Override
	protected boolean computeEnablementState() {
		boolean enabled = super.computeEnablementState();

		if (enabled) {
			Portlet portlet = (Portlet)getModelElement();

			Value<Path> resourceBundle = portlet.getResourceBundle();

			if ((resourceBundle != null) && !resourceBundle.empty()) {
				Status status = resourceBundle.validation();

				if (status.severity() == Severity.ERROR) {
					enabled = false;
				}
			}

			if (ListUtil.isNotEmpty(portlet.getSupportedLocales())) {
				for (SupportedLocales sl : portlet.getSupportedLocales()) {

					/**
					 * By now, the error means the locale is not unique or not among possible values
					 * or empty, that makes the button "Create Locale Bundles" disabled. The warning
					 * means "No resource bundle defined", in this case the button should be
					 * enabled.
					 */
					Status status = sl.validation();

					if (status.severity() == Severity.ERROR) {
						enabled = false;

						break;
					}
				}
			}
		}

		return enabled;
	}

	@Override
	protected Object run(Presentation context) {
		SapphirePart part = context.part();

		ITextEditor editor = part.adapt(ITextEditor.class);

		editor.doSave(new NullProgressMonitor());

		List<IFile> missingRBFiles = new ArrayList<>();

		Portlet portlet = (Portlet)getModelElement();

		IProject project = portlet.adapt(IProject.class);

		Value<Path> resourceBundle = portlet.getResourceBundle();

		String text = resourceBundle.text();

		String defaultRBFileName = PortletUtil.convertJavaToIoFileName(
			text, GenericResourceBundlePathService.RB_FILE_EXTENSION);

		int index = text.lastIndexOf(".");

		String packageName = "";

		if (index == -1) {
			index = text.length();
			packageName = "";
		}
		else {
			packageName = text.substring(0, index);
		}

		IFolder rbSourceFolder = getResourceBundleFolderLocation(project, defaultRBFileName);

		IPath entryPath = rbSourceFolder.getLocation();

		PortletInfo portletInfo = portlet.getPortletInfo();

		StringBuilder rbFileBuffer = _buildDefaultRBContent(portletInfo);

		// Create the default Resource Bundle if it does not exist

		if (!getFileFromClasspath(project, defaultRBFileName)) {
			IFile drbFile = wroot.getFileForLocation(entryPath.append(defaultRBFileName));

			missingRBFiles.add(drbFile);
		}

		// Create bundles for each supported locale for which the resource bundle is
		// missing

		List<SupportedLocales> supportedLocales = portlet.getSupportedLocales();

		for (SupportedLocales iSupportedLocale : supportedLocales) {
			if (iSupportedLocale != null) {
				String locale = PortletUtil.localeString(SapphireUtil.getText(iSupportedLocale.getSupportedLocale()));

				String localizedIOFileName = PortletUtil.convertJavaToIoFileName(
					text, GenericResourceBundlePathService.RB_FILE_EXTENSION, locale);

				if (!getFileFromClasspath(project, localizedIOFileName)) {
					IFile rbFile = wroot.getFileForLocation(entryPath.append(localizedIOFileName));

					missingRBFiles.add(rbFile);
				}
			}
		}

		createFiles(context, project, packageName, missingRBFiles, rbFileBuffer);

		setEnabled(false);

		Portlet p = getModelElement().nearest(Portlet.class);

		for (SupportedLocales sl : p.getSupportedLocales()) {
			Value<String> locale = sl.getSupportedLocale();

			LocaleBundleValidationService service = locale.service(LocaleBundleValidationService.class);

			service.forceRefresh();
		}

		return null;
	}

	/**
	 * @param portletInfo
	 * @return
	 */
	private StringBuilder _buildDefaultRBContent(PortletInfo portletInfo) {
		StringBuilder rbFileBuffer = new StringBuilder();

		rbFileBuffer.append("#Portlet Information\n");
		rbFileBuffer.append("javax.portlet.title");
		rbFileBuffer.append("=");
		rbFileBuffer.append(((portletInfo != null) && (portletInfo.getTitle() != null)) ? portletInfo.getTitle() : "");
		rbFileBuffer.append("\n");
		rbFileBuffer.append("javax.portlet.short-title");
		rbFileBuffer.append("=");
		rbFileBuffer.append(
			((portletInfo != null) && (portletInfo.getShortTitle() != null)) ? portletInfo.getShortTitle() : "");
		rbFileBuffer.append("\n");
		rbFileBuffer.append("javax.portlet.keywords");
		rbFileBuffer.append("=");
		rbFileBuffer.append(
			((portletInfo != null) && (portletInfo.getKeywords() != null)) ? portletInfo.getKeywords() : "");
		rbFileBuffer.append("\n");
		rbFileBuffer.append("#Other Properties");
		rbFileBuffer.append("\n");

		return rbFileBuffer;
	}

}