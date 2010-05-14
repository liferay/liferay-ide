/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.portlet.ui.editor;

import com.liferay.ide.eclipse.core.model.IModelChangedEvent;
import com.liferay.ide.eclipse.core.model.IModelChangedListener;
import com.liferay.ide.eclipse.portlet.core.IServiceBuilderModel;
import com.liferay.ide.eclipse.portlet.core.ServiceBuilderModel;
import com.liferay.ide.eclipse.ui.form.FormEntry;
import com.liferay.ide.eclipse.ui.form.FormEntryAdapter;
import com.liferay.ide.eclipse.ui.form.FormLayoutFactory;
import com.liferay.ide.eclipse.ui.form.IContextPart;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

/**
 * @author Greg Amerson
 */
public class ServiceBuilderGeneralSection extends SectionPart implements IContextPart, IModelChangedListener {

	protected FormEntry authorEntry;

	protected FormEntry namespaceEntry;

	protected FormEntry packagePathEntry;

	protected ServiceBuilderFormPage page;

	public ServiceBuilderGeneralSection(ServiceBuilderFormPage page, Composite parent) {
		super(parent, page.getManagedForm().getToolkit(), ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);

		this.page = page;

		initialize(page.getManagedForm());

		getSection().clientVerticalSpacing = FormLayoutFactory.SECTION_HEADER_VERTICAL_SPACING;

		getSection().setData("part", this);

		createSection();
	}

	public void cancelEdit() {
	}

	@Override
	public void dispose() {
		super.dispose();

		getModel().dispose();
	}

	public void fireSaveNeeded() {
	}

	public String getContextId() {
		return null;
	}

	public FormPage getPage() {
		return page;
	}

	public boolean isEditable() {
		return true;
	}

	public void modelChanged(IModelChangedEvent event) {
		if (event != null) {
			if (event.getChangeType() == IModelChangedEvent.WORLD_CHANGED) {
				this.refresh();

				return;
			}

			if (event.getChangedProperty().equals(IServiceBuilderModel.PROPERTY_NAMESPACE)) {
				namespaceEntry.setValue(event.getNewValue().toString());
			}

			if (event.getChangedProperty().equals(IServiceBuilderModel.PROPERTY_AUTHOR)) {
				authorEntry.setValue(event.getNewValue().toString());
			}

			if (event.getChangedProperty().equals(IServiceBuilderModel.PROPERTY_PACKAGE_PATH)) {
				packagePathEntry.setValue(event.getNewValue().toString());
			}
		}
	}

	public void refresh() {
		if (getModel().getPackagePath() != null) {
			packagePathEntry.setValue(getModel().getPackagePath());
		}

		if (getModel().getNamespace() != null) {
			namespaceEntry.setValue(getModel().getNamespace());
		}

		if (getModel().getAuthor() != null) {
			authorEntry.setValue(getModel().getAuthor());
		}

		super.refresh();
	}

	protected void createAuthorEntry(Composite client, FormToolkit toolkit, IActionBars actionBars) {
		authorEntry = new FormEntry(client, toolkit, "Author:", null, false);
		authorEntry.setFormEntryListener(new FormEntryAdapter(this, actionBars) {

			public void textValueChanged(FormEntry entry) {
				getModel().setAuthor(entry.getValue().trim());
			}
		});

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 2;

		authorEntry.getText().setLayoutData(gd);
		authorEntry.setEditable(isEditable());
	}

	protected void createNamespaceEntry(Composite client, FormToolkit toolkit, IActionBars actionBars) {
		namespaceEntry = new FormEntry(client, toolkit, "Namespace:", null, false);
		namespaceEntry.setFormEntryListener(new FormEntryAdapter(this, actionBars) {

			public void textValueChanged(FormEntry entry) {
				getModel().setNamespace(entry.getValue().trim());
			}
		});

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 2;

		namespaceEntry.getText().setLayoutData(gd);
		namespaceEntry.setEditable(isEditable());
	}

	protected void createPackagePathEntry(Composite client, FormToolkit toolkit, IActionBars actionBars) {
		packagePathEntry = new FormEntry(client, toolkit, "Package path:", null, false);
		packagePathEntry.setFormEntryListener(new FormEntryAdapter(this, actionBars) {

			public void textValueChanged(FormEntry entry) {
				getModel().setPackagePath(entry.getValue().trim());
			}
		});

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 2;

		packagePathEntry.getText().setLayoutData(gd);
		packagePathEntry.setEditable(isEditable());
	}

	protected void createSection() {
		FormToolkit toolkit = page.getEditor().getToolkit();

		TableWrapData data = new TableWrapData(TableWrapData.FILL_GRAB);
		data.colspan = 2;

		Section section = getSection();
		section.setText("General");
		section.setDescription("Specify package, namespace, and author settings for generated services.");
		section.setLayout(FormLayoutFactory.createClearTableWrapLayout(false, 1));
		section.setLayoutData(data);

		Composite client = toolkit.createComposite(section);
		client.setLayout(FormLayoutFactory.createSectionClientGridLayout(false, 3));

		IActionBars actionBars = page.getEditor().getEditorSite().getActionBars();

		createPackagePathEntry(client, toolkit, actionBars);

		createNamespaceEntry(client, toolkit, actionBars);

		createAuthorEntry(client, toolkit, actionBars);

		toolkit.paintBordersFor(client);

		section.setClient(client);

		getModel().addModelChangedListener(this);
	}

	protected ServiceBuilderModel getModel() {
		return ((ServiceBuilderEditor) page.getEditor()).getServiceModel();
	}

}
