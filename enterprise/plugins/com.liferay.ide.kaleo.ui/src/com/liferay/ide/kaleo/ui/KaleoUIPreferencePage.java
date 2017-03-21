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

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Gregory Amerson
 */
public class KaleoUIPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

    public KaleoUIPreferencePage()
    {
        super( GRID );
        setPreferenceStore( KaleoUI.getDefault().getPreferenceStore() );
    }

    @Override
    protected void createFieldEditors()
    {
        final String[][] labelAndValues = new String[][]
        {
            {
                "Always", MessageDialogWithToggle.ALWAYS
            },
            {
                "Never", MessageDialogWithToggle.NEVER
            },
            {
                "Prompt", MessageDialogWithToggle.PROMPT
            }
        };

        addField
        (
            new RadioGroupFieldEditor
            (
                KaleoUIPreferenceConstants.EDITOR_PERSPECTIVE_SWITCH,
                "Open the Kaleo Designer perspective when opening kaleo workflow files.",
                3,
                labelAndValues,
                getFieldEditorParent(),
                true
            )
        );
    }

    public void init( IWorkbench workbench )
    {
    }

}
