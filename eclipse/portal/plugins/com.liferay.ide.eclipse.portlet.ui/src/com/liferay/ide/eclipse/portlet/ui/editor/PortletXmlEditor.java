/**
 * 
 */

package com.liferay.ide.eclipse.portlet.ui.editor;

import org.eclipse.sapphire.ui.swt.xml.editor.SapphireEditorForXml;

import com.liferay.ide.eclipse.portlet.core.model.IPortletApp;

/**
 * @author kamesh
 */
public class PortletXmlEditor extends SapphireEditorForXml {

	public static final String ID = "com.liferay.ide.eclipse.portlet.ui.editor.PortletXmlEditor";

	private static final String EDITOR_DEFINITION_PATH =
		"com.liferay.ide.eclipse.portlet.ui/com/liferay/ide/eclipse/portlet/ui/editor/portlet-app.sdef/portlet-app.editor";

	/**
	 * 
	 */
	public PortletXmlEditor() {
		super( ID );
		initEditorSettings();
	}

	/**
	 * this method will initialize the editor with its settings like model element, UI definition etc.,
	 */
	private void initEditorSettings() {

		setEditorDefinitionPath( EDITOR_DEFINITION_PATH );
		setRootModelElementType( IPortletApp.TYPE );

	}
}
