package com.liferay.ide.layouttpl.ui.editor;

import org.eclipse.gef.ui.palette.DefaultPaletteViewerPreferences;
import org.eclipse.jface.preference.IPreferenceStore;


public class LayoutTplPaletteViewerPreferences extends DefaultPaletteViewerPreferences {

	public LayoutTplPaletteViewerPreferences() {
		super();
		setup();
	}

	public LayoutTplPaletteViewerPreferences(IPreferenceStore store) {
		super(store);
		setup();
	}

	protected void setup() {

	}

}
