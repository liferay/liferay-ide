/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Common Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ******************************************************************************/
package com.liferay.ide.velocity.preferences;

import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;

import com.liferay.ide.velocity.editor.TemplateEditorUI;

/**
 * @see org.eclipse.jface.preference.PreferencePage
 */
public class TemplatesPreferencePage extends TemplatePreferencePage implements IWorkbenchPreferencePage
{

    public TemplatesPreferencePage()
    {
        setPreferenceStore(TemplateEditorUI.getDefault().getPreferenceStore());
        setTemplateStore(TemplateEditorUI.getDefault().getTemplateStore());
        setContextTypeRegistry(TemplateEditorUI.getDefault().getContextTypeRegistry());
    }

    protected boolean isShowFormatterSetting()
    {
        return false;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean performOk()
    {
        boolean ok = super.performOk();
        TemplateEditorUI.getDefault().savePluginPreferences();
        return ok;
    }
}
