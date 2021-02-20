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

package com.liferay.ide.server.ui.editor;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.server.core.portal.PortalServer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IPublishListener;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.util.PublishAdapter;
import org.eclipse.wst.server.ui.editor.ServerEditorSection;
import org.eclipse.wst.server.ui.internal.ContextIds;

/**
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public abstract class AbstractPortalServerEditorSection extends ServerEditorSection {

	public void createSection(Composite parent) {
		if (!needCreate()) {
			return;
		}

		super.createSection(parent);

		FormToolkit toolkit = getFormToolkit(parent.getDisplay());

		section = toolkit.createSection(
			parent,
			ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR |
			Section.DESCRIPTION | ExpandableComposite.FOCUS_TITLE);

		section.setText(getSectionLabel());
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));

		Composite composite = toolkit.createComposite(section);

		GridLayout layout = new GridLayout();

		layout.numColumns = 3;
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 15;

		composite.setLayout(layout);

		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));

		IWorkbench workbench = PlatformUI.getWorkbench();

		IWorkbenchHelpSystem whs = workbench.getHelpSystem();

		whs.setHelp(composite, ContextIds.EDITOR_SERVER);
		whs.setHelp(section, ContextIds.EDITOR_SERVER);

		toolkit.paintBordersFor(composite);

		section.setClient(composite);

		createEditorSection(toolkit, composite);

		Label label = createLabel(toolkit, composite, StringPool.EMPTY);
		GridData data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);

		label.setLayoutData(data);

		setDefault = toolkit.createHyperlink(composite, Msgs.restoreDefaultsLink, SWT.WRAP);

		setDefault.addHyperlinkListener(
			new HyperlinkAdapter() {

				public void linkActivated(HyperlinkEvent e) {
					updating = true;

					setDefault();

					updating = false;

					validate();
				}

			});

		data = new GridData(SWT.FILL, SWT.CENTER, true, false);

		data.horizontalSpan = 3;

		setDefault.setLayoutData(data);

		initialize();
	}

	public void dispose() {
		if (server != null) {
			server.removePropertyChangeListener(listener);

			IServer originalServer = server.getOriginal();

			if (originalServer != null) {
				originalServer.removePublishListener(publishListener);
			}
		}
	}

	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);

		if (!needCreate()) {
			return;
		}

		if (server != null) {
			portalServer = (PortalServer)server.loadAdapter(PortalServer.class, null);

			IRuntime runtime = server.getRuntime();

			portalRuntime = (PortalRuntime)runtime.loadAdapter(PortalRuntime.class, null);

			portalBundle = portalRuntime.getPortalBundle();

			addChangeListeners();
		}
	}

	protected void addChangeListeners() {
		listener = new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent event) {
				if (updating) {
					return;
				}

				updating = true;

				addPropertyListeners(event);

				updating = false;
			}

		};

		server.addPropertyChangeListener(listener);

		publishListener = new PublishAdapter() {

			public void publishFinished(IServer server2, IStatus status) {
				boolean flag = false;

				IModule[] modules = server2.getModules();

				if (status.isOK() && (modules.length == 0)) {
					flag = true;
				}

				if (flag != allowRestrictedEditing) {
					allowRestrictedEditing = flag;
				}
			}

		};

		IServer originalServer = server.getOriginal();

		originalServer.addPublishListener(publishListener);
	}

	protected abstract void addPropertyListeners(PropertyChangeEvent event);

	protected abstract void createEditorSection(FormToolkit toolkit, Composite composite);

	protected Label createLabel(FormToolkit toolkit, Composite parent, String text) {
		Label label = toolkit.createLabel(parent, text);

		FormColors colors = toolkit.getColors();

		label.setForeground(colors.getColor(IFormColors.TITLE));

		return label;
	}

	protected void doValidate() {
	}

	protected abstract String getSectionLabel();

	protected void initialize() {
		if ((portalServer == null) || (portalBundle == null)) {
			return;
		}

		updating = true;

		initProperties();

		updating = false;

		validate();
	}

	protected abstract void initProperties();

	protected abstract boolean needCreate();

	protected abstract void setDefault();

	protected void validate() {
		if (portalServer != null) {
			setErrorMessage(null);
		}

		doValidate();
	}

	protected boolean allowRestrictedEditing;
	protected PropertyChangeListener listener;
	protected PortalBundle portalBundle;
	protected PortalRuntime portalRuntime;
	protected PortalServer portalServer;
	protected IPublishListener publishListener;
	protected Section section;
	protected Hyperlink setDefault;
	protected boolean updating;

	private static class Msgs extends NLS {

		public static String restoreDefaultsLink;

		static {
			initializeMessages(AbstractPortalServerEditorSection.class.getName(), Msgs.class);
		}

	}

}