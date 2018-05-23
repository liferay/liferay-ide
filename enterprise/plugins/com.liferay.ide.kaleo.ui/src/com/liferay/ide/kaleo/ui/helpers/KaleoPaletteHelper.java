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

package com.liferay.ide.kaleo.ui.helpers;

import com.liferay.ide.kaleo.ui.IKaleoEditorHelper;
import com.liferay.ide.kaleo.ui.KaleoUI;
import com.liferay.ide.kaleo.ui.editor.KaleoEditorPaletteFactory;
import com.liferay.ide.kaleo.ui.editor.KaleoPaletteViewerPage;
import com.liferay.ide.kaleo.ui.editor.ScriptCreationFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Gregory Amerson
 */
public class KaleoPaletteHelper {

	public KaleoPaletteHelper(
		IEditorPart editorPart, AbstractUIPlugin uiBundle, String folderName, ImageDescriptor entryImage) {

		_editorPart = editorPart;

		_paletteRoot = KaleoEditorPaletteFactory.createPalette(uiBundle, folderName, entryImage);

		_editDomain = new DefaultEditDomain(_editorPart);

		_editDomain.setPaletteRoot(_paletteRoot);
	}

	public KaleoPaletteViewerPage createPalettePage() {
		_palettePage = new KaleoPaletteViewerPage(getPaletteViewerProvider());

		return _palettePage;
	}

	public DefaultEditDomain getEditDomain() {
		return _editDomain;
	}

	public CombinedTemplateCreationEntry getSelectedEntry() {
		return _selectedEntry;
	}

	public List<TransferDragSourceListener> getTransferDragSourceListeners() {
		if (_transferDragSourceListeners == null) {
			_transferDragSourceListeners = new ArrayList<>();
		}

		return _transferDragSourceListeners;
	}

	public void setSelectedEntry(CombinedTemplateCreationEntry entry) {
		if (_selectedEntry == entry) {
			return;
		}

		_selectedEntry = entry;
		updateDragSource();
	}

	protected void addTextTransferListener() {
		TransferDragSourceListener listener = new TransferDragSourceListenerImpl(TextTransfer.getInstance());

		_getPaletteViewer().addDragSourceListener(listener);
		getTransferDragSourceListeners().add(listener);
	}

	protected PaletteViewerProvider createPaletteViewerProvider() {
		return new PaletteViewerProvider(getEditDomain()) {

			@Override
			protected void configurePaletteViewer(PaletteViewer viewer) {
				super.configurePaletteViewer(viewer);

				viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
				viewer.addSelectionChangedListener(_getViewerSelectionChangedListener());
			}

		};
	}

	protected CombinedTemplateCreationEntry getEntryFromSelection(ISelection selection) {
		if (!selection.isEmpty()) {
			if (selection instanceof IStructuredSelection) {
				Object obj = ((IStructuredSelection)selection).getFirstElement();

				if (obj instanceof EditPart) {
					if (((EditPart)obj).getModel() instanceof CombinedTemplateCreationEntry) {
						Object model = ((EditPart)obj).getModel();

						return (CombinedTemplateCreationEntry)model;
					}
				}
				else if (obj instanceof CombinedTemplateCreationEntry) {
					return (CombinedTemplateCreationEntry)obj;
				}
			}
		}

		return null;
	}

	protected PaletteViewerProvider getPaletteViewerProvider() {
		if (_provider == null) {
			_provider = createPaletteViewerProvider();
		}

		return _provider;
	}

	protected void updateDragSource() {
		Transfer[] supportedTypes = {TextTransfer.getInstance()};

		/*
		 * TRH suggested use of the event's doit field by the fListeners, but
		 * there's no other way to guarantee that TextTransfer is considered
		 * last
		 */
		Iterator<TransferDragSourceListener> iterator = getTransferDragSourceListeners().iterator();

		ArrayList<TransferDragSourceListener> oldListeners = new ArrayList<>();

		while (iterator.hasNext()) {
			TransferDragSourceListener listener = iterator.next();

			oldListeners.add(listener);

			iterator.remove();
		}

		boolean addTextTransfer = false;

		for (int i = 0; i < supportedTypes.length; i++) {
			if (TextTransfer.class.equals(supportedTypes[i].getClass())) {
				addTextTransfer = true;
			}
			else {
				TransferDragSourceListener listener = new TransferDragSourceListenerImpl(supportedTypes[i]);

				_getPaletteViewer().addDragSourceListener(listener);

				getTransferDragSourceListeners().add(listener);
			}
		}

		iterator = oldListeners.iterator();

		while (iterator.hasNext()) {
			TransferDragSourceListener listener = iterator.next();

			_getPaletteViewer().removeDragSourceListener(listener);

			iterator.remove();
		}

		if (addTextTransfer) {
			addTextTransferListener();
		}
	}

	protected class TransferDragSourceListenerImpl implements TransferDragSourceListener {

		public TransferDragSourceListenerImpl(Transfer xfer) {
			_fTransfer = xfer;
		}

		public void dragFinished(DragSourceEvent event) {
			try {
				IWorkbench workBench = PlatformUI.getWorkbench();

				IWorkbenchWindow workBenchWindow = workBench.getActiveWorkbenchWindow();

				IWorkbenchPage workBenchPage = workBenchWindow.getActivePage();

				IEditorPart activeEditor = workBenchPage.getActiveEditor();

				String editorId = activeEditor.getSite().getId();

				IKaleoEditorHelper helper = KaleoUI.getKaleoEditorHelperByEditorId(editorId);

				helper.handleDropFromPalette(activeEditor);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void dragSetData(DragSourceEvent event) {
			if (getSelectedEntry() == null) {
				return;
			}

			if (getSelectedEntry() instanceof CombinedTemplateCreationEntry) {
				CombinedTemplateCreationEntry tool = getSelectedEntry();

				Object tempalte = tool.getTemplate();

				ScriptCreationFactory scriptFactory = (ScriptCreationFactory)tempalte;

				event.data = scriptFactory.getNewObject().toString();
			}
			else {
				event.data = "";
			}
		}

		public void dragStart(DragSourceEvent event) {
		}

		public Transfer getTransfer() {
			return _fTransfer;
		}

		private Transfer _fTransfer;

	}

	private PaletteViewer _getPaletteViewer() {
		if (_palettePage != null) {
			return _palettePage.getViewer();
		}

		return null;
	}

	private ISelectionChangedListener _getViewerSelectionChangedListener() {
		if (_selectionChangedListener == null) {
			_selectionChangedListener = new ISelectionChangedListener() {

				public void selectionChanged(SelectionChangedEvent event) {
					setSelectedEntry(getEntryFromSelection(event.getSelection()));
				}

			};
		}

		return _selectionChangedListener;
	}

	private DefaultEditDomain _editDomain;
	private IEditorPart _editorPart;
	private KaleoPaletteViewerPage _palettePage;
	private PaletteRoot _paletteRoot;
	private PaletteViewerProvider _provider;
	private CombinedTemplateCreationEntry _selectedEntry;
	private ISelectionChangedListener _selectionChangedListener;
	private List<TransferDragSourceListener> _transferDragSourceListeners;

}