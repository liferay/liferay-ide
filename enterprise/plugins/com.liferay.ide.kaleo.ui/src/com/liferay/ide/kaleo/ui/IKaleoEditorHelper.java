/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay Developer Studio ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.ui;

import com.liferay.ide.kaleo.ui.editor.ScriptPropertyEditorInput;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;

/**
 * @author Gregory Amerson
 */
public interface IKaleoEditorHelper
{

    String EXTENSION_ID = "com.liferay.ide.kaleo.ui.editorHelpers";

    String KALEO_TEMP_PREFIX = "._KALEO_TEMP_SCRIPT_";

    IEditorPart createEditorPart( ScriptPropertyEditorInput editorInput, IEditorSite editorSite );

    String getContributorName();

    String getEditorId();

    String getFileExtension();

    String getLanguageType();

    void handleDropFromPalette( IEditorPart activeEditor );

    void openEditor( ISapphirePart part, Element modelElement, ValueProperty valueProperty );

}
