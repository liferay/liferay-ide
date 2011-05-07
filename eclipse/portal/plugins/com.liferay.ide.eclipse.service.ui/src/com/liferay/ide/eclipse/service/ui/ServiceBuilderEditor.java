package com.liferay.ide.eclipse.service.ui;


import com.liferay.ide.eclipse.service.core.model.IServiceBuilder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.ui.swt.graphiti.editor.SapphireDiagramEditor;
import org.eclipse.sapphire.ui.swt.graphiti.editor.SapphireDiagramEditorFactory;
import org.eclipse.sapphire.ui.swt.graphiti.editor.SapphireDiagramEditorInput;
import org.eclipse.sapphire.ui.swt.xml.editor.SapphireEditorForXml;
import org.eclipse.ui.PartInitException;


public class ServiceBuilderEditor extends SapphireEditorForXml {

	private SapphireDiagramEditor pageDiagram;

	public ServiceBuilderEditor() {
		super(ServiceUI.PLUGIN_ID);

		setRootModelElementType(IServiceBuilder.TYPE);
		setEditorDefinitionPath(ServiceUI.PLUGIN_ID + "/sdef/ServiceBuilder.sdef/serviceBuilderPage");
	}

	@Override
	protected void createDiagramPages() throws PartInitException {
		IPath path = new Path(ServiceUI.PLUGIN_ID + "/sdef/ServiceBuilder.sdef/diagramPage");
		this.pageDiagram = new SapphireDiagramEditor(this.getModelElement(), path);
		SapphireDiagramEditorInput diagramEditorInput = null;

		try {
			diagramEditorInput =
				SapphireDiagramEditorFactory.createEditorInput(this.getModelElement().adapt(IFile.class));
		}
		catch (Exception e) {
			ServiceUI.logError(e);
		}

        if (diagramEditorInput != null) {
			addPage(0, this.pageDiagram, diagramEditorInput);
			setPageText(0, "Diagram");
			setPageId(this.pages.get(0), "Diagram", this.pageDiagram.getPart());
		}
	}

	@Override
	public void doSave(final IProgressMonitor monitor) {
		super.doSave(monitor);

		this.pageDiagram.doSave(monitor);
	}
}
