package com.liferay.ide.eclipse.layouttpl.ui.editor;

import com.liferay.ide.eclipse.layouttpl.ui.gef.GraphicalEditorWithFlyoutPalette;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.palette.PaletteRoot;

public class LayoutTemplateEditor extends GraphicalEditorWithFlyoutPalette {

	@Override
	protected PaletteRoot getPaletteRoot() {
		// TODO Implement getPaletteRoot method on class
		// GraphicalEditorWithFlyoutPalette
		System.out.println("GraphicalEditorWithFlyoutPalette.getPaletteRoot");
		return null;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Implement doSave method on class EditorPart
		System.out.println("EditorPart.doSave");

	}

}
