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

package com.liferay.ide.layouttpl.ui.editor;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.layouttpl.core.model.LayoutTplElement;
import com.liferay.ide.layouttpl.core.model.LayoutTplElementsFactory;
import com.liferay.ide.layouttpl.core.util.LayoutTemplateAccessor;
import com.liferay.ide.project.core.descriptor.LiferayDescriptorHelper;

import java.lang.reflect.Method;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.def.EditorPageDef;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

import org.osgi.framework.Version;

/**
 * @author Kuo Zhang
 * @author Joye Luo
 */
@SuppressWarnings("restriction")
public class LayoutTplEditor extends SapphireEditor implements IExecutableExtension, LayoutTemplateAccessor {

	@Override
	public void dispose() {
		super.dispose();

		_definition = null;
		_sourcePage = null;
		_previewPage = null;

		if (_sourceModel != null) {
			_sourceModel.releaseFromEdit();
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		int activePage = getActivePage();

		if (activePage == _PREVIEW_PAGE_INDEX) {
			if (_sourcePage.isDirty()) {
				_sourcePage.doSave(monitor);
			}
		}
		else if (activePage == _SOURCE_PAGE_INDEX) {
			if (_sourcePage.isDirty()) {
				_sourcePage.doSave(monitor);
				refreshDiagramModel();
			}
		}
		else if (activePage == _DESIGN_PAGE_INDEX) {
			if (_designPageChanged) {
				refreshSourceModel();
				_sourcePage.doSave(monitor);
			}
		}

		setSourceModelChanged(false);
		setDesignPageChanged(false);

		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public IContentOutlinePage getContentOutline(Object page) {
		if (page == _sourcePage) {
			return (IContentOutlinePage)this._sourcePage.getAdapter(IContentOutlinePage.class);
		}

		return super.getContentOutline(page);
	}

	@Override
	public boolean isDirty() {
		if (_designPageChanged || _sourcePage.isDirty()) {
			return true;
		}

		return false;
	}

	@Override
	protected void createEditorPages() throws PartInitException {
		_sourcePage = new StructuredTextEditor();

		_sourcePage.setEditorPart(this);

		addPage(_SOURCE_PAGE_INDEX, _sourcePage, getEditorInput());

		setPageText(_SOURCE_PAGE_INDEX, _SOURCE_PAGE_TITLE);

		initSourceModel();

		addDeferredPage(1, _PREVIEW_PAGE_TITLE, "preview");
		addDeferredPage(2, _DESIGN_PAGE_TITLE, "designPage");
	}

	protected LayoutTplElement createEmptyDiagramModel() {
		LayoutTplElement layoutTpl = LayoutTplElement.TYPE.instantiate();

		layoutTpl.setBootstrapStyle(_isBootstrapStyle());

		String editorInputName = getEditorInput().getName();

		layoutTpl.setClassName(editorInputName.replaceAll("\\..*$", ""));

		layoutTpl.setIs62(_is62());

		return layoutTpl;
	}

	@Override
	protected Element createModel() {
		IFile file = getFile();

		_bootstrapStyle = _isBootstrapStyle();
		_is62 = _is62();

		LayoutTplElement layoutTpl = LayoutTplElementsFactory.INSTANCE.newLayoutTplFromFile(
			file, _bootstrapStyle, _is62);

		if (layoutTpl == null) {
			layoutTpl = createEmptyDiagramModel();
		}

		Listener listener = new Listener() {

			@Override
			public void handle(Event event) {
				setDesignPageChanged(true);
				firePropertyChange(PROP_DIRTY);
			}

		};

		layoutTpl.attach(listener, "*");

		return layoutTpl;
	}

	@Override
	protected IEditorPart createPage(String pageDefinitionId) {
		if (pageDefinitionId.equals("preview")) {
			if (_previewPage == null) {
				Element element = getModelElement();

				if (element instanceof LayoutTplElement) {
					_previewPage = new LayoutTplPreviewEditor((LayoutTplElement)element) {

						@Override
						public String getTitle() {
							return "Preview";
						}

					};
				}
			}

			return _previewPage;
		}

		return super.createPage(pageDefinitionId);
	}

	@Override
	protected DefinitionLoader.Reference<EditorPageDef> getDefinition(String pageDefinitionId) {
		if (pageDefinitionId.equals("preview")) {
			if (_definition == null) {
				DefinitionLoader definitionLoader = DefinitionLoader.sdef(LayoutTplEditor.class);

				_definition = definitionLoader.page("preview");
			}

			return _definition;
		}

		return super.getDefinition(pageDefinitionId);
	}

	protected void initSourceModel() {
		if ((_sourceModel == null) && (_sourcePage != null) && (_sourcePage.getDocumentProvider() != null)) {
			IDocumentProvider documentProvider = _sourcePage.getDocumentProvider();

			IDocument doc = documentProvider.getDocument(getEditorInput());

			IModelManager modelManager = StructuredModelManager.getModelManager();

			_sourceModel = (IDOMModel)modelManager.getExistingModelForEdit(doc);

			_sourceModel.addModelStateListener(
				new IModelStateListener() {

					public void modelAboutToBeChanged(IStructuredModel model) {
					}

					public void modelAboutToBeReinitialized(IStructuredModel structuredModel) {
					}

					public void modelChanged(IStructuredModel model) {
						setSourceModelChanged(true);
					}

					public void modelDirtyStateChanged(IStructuredModel model, boolean dirty) {
					}

					public void modelReinitialized(IStructuredModel structuredModel) {
					}

					public void modelResourceDeleted(IStructuredModel model) {
					}

					public void modelResourceMoved(IStructuredModel oldModel, IStructuredModel newModel) {
					}

				});
		}
	}

	@Override
	protected void pageChange(int pageIndex) {
		int[] lastActivePage = new int[1];

		try {
			Method getLastActivePage = SapphireEditor.class.getDeclaredMethod("getLastActivePage");

			getLastActivePage.setAccessible(true);

			lastActivePage[0] = (Integer)getLastActivePage.invoke(this);
		}
		catch (Exception e) {
		}

		if ((lastActivePage[0] == _SOURCE_PAGE_INDEX) && (pageIndex == _PREVIEW_PAGE_INDEX)) {

			/**
			 * if the source page is dirty, but the model didn't get changed,
			 * then don't refresh the model element
			 */
			if (this._sourcePage.isDirty() && _sourceModelChanged) {
				refreshDiagramModel();
			}

			refreshPreviewPage();
		}

		if ((lastActivePage[0] == _SOURCE_PAGE_INDEX) && (pageIndex == _DESIGN_PAGE_INDEX) &&
			this._sourcePage.isDirty() && _sourceModelChanged) {

			refreshDiagramModel();
		}

		if ((lastActivePage[0] == _DESIGN_PAGE_INDEX) && (pageIndex == _SOURCE_PAGE_INDEX) && _designPageChanged) {
			refreshSourceModel();
		}

		if ((lastActivePage[0] == _DESIGN_PAGE_INDEX) && (pageIndex == _PREVIEW_PAGE_INDEX) && _designPageChanged) {
			refreshSourceModel();

			refreshPreviewPage();
		}

		try {
			super.pageChange(pageIndex);
		}
		catch (Exception e) {
		}
	}

	protected void refreshDiagramModel() {
		LayoutTplElement newElement = LayoutTplElementsFactory.INSTANCE.newLayoutTplFromFile(
			getFile(), _bootstrapStyle, _is62);

		if (newElement == null) {

			/**
			 * create an empty model for diagram in memory, but not write to
			 * source
			 */
			newElement = createEmptyDiagramModel();
		}

		Element model = getModelElement();

		model.clear();
		model.copy(newElement);
	}

	protected void refreshPreviewPage() {
		if (_previewPage != null) {
			_previewPage.refreshVisualModel((LayoutTplElement)getModelElement());
		}
	}

	protected void refreshSourceModel() {
		refreshSourceModel((LayoutTplElement)getModelElement());
	}

	protected void refreshSourceModel(LayoutTplElement modelElement) {
		if (_sourceModel != null) {
			String templateSource = getTemplateSource(modelElement);

			_sourceModel.aboutToChangeModel();

			IStructuredDocument structuredDocument = _sourceModel.getStructuredDocument();

			structuredDocument.setText(this, templateSource);

			_sourceModel.changedModel();
		}

		setSourceModelChanged(false);
	}

	protected void setDesignPageChanged(boolean changed) {
		_designPageChanged = changed;
	}

	protected void setSourceModelChanged(boolean changed) {
		_sourceModelChanged = changed;
	}

	private boolean _is62() {
		IProject project = getFile().getProject();

		Version version = Version.parseVersion(LiferayDescriptorHelper.getDescriptorVersion(project));

		if (CoreUtil.compareVersions(version, ILiferayConstants.V620) == 0) {
			return true;
		}

		return false;
	}

	private boolean _isBootstrapStyle() {
		boolean retval = true;

		try {
			IFile file = getFile();

			ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, file.getProject());

			ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

			if (portal == null) {
				return false;
			}

			Version version = Version.parseVersion(portal.getVersion());

			if (CoreUtil.compareVersions(version, ILiferayConstants.V620) < 0) {
				retval = false;
			}
		}
		catch (Exception e) {
		}

		return retval;
	}

	private static final int _DESIGN_PAGE_INDEX = 2;

	private static final String _DESIGN_PAGE_TITLE = "Design";

	private static final int _PREVIEW_PAGE_INDEX = 1;

	private static final String _PREVIEW_PAGE_TITLE = "Preview";

	private static final int _SOURCE_PAGE_INDEX = 0;

	private static final String _SOURCE_PAGE_TITLE = "Source";

	private boolean _bootstrapStyle;
	private DefinitionLoader.Reference<EditorPageDef> _definition;
	private boolean _designPageChanged;
	private boolean _is62;
	private LayoutTplPreviewEditor _previewPage;
	private IDOMModel _sourceModel;
	private boolean _sourceModelChanged;
	private StructuredTextEditor _sourcePage;

}