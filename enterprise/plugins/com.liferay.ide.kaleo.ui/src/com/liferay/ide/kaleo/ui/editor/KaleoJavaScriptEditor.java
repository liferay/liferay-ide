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

package com.liferay.ide.kaleo.ui.editor;

import com.liferay.ide.kaleo.ui.KaleoUI;
import com.liferay.ide.kaleo.ui.helpers.KaleoPaletteHelper;

import org.eclipse.gef.ui.views.palette.PalettePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.jsdt.internal.ui.javaeditor.CompilationUnitEditor;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class KaleoJavaScriptEditor extends CompilationUnitEditor
{
    public static final String EDITOR_ID = "com.liferay.ide.kaleo.ui.editor.jsdt";
    private KaleoPaletteHelper paletteHelper;

    public KaleoJavaScriptEditor()
    {
        super();
        final ImageDescriptor entryImage =
            KaleoUI.imageDescriptorFromPlugin( KaleoUI.PLUGIN_ID, "icons/e16/js_obj.gif" );
        this.paletteHelper = new KaleoPaletteHelper( this, KaleoUI.getDefault(), "palette/jsdt", entryImage );
    }

    @Override
    @SuppressWarnings( "rawtypes" )
    public Object getAdapter( Class required )
    {
        if( required == PalettePage.class )
        {
            return this.paletteHelper.createPalettePage();
        }

        return super.getAdapter( required );
    }

    @Override
    public boolean isDirty()
    {
        return false;
    }
}
