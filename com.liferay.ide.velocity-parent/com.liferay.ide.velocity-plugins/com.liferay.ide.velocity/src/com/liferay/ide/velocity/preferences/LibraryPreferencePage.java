package com.liferay.ide.velocity.preferences;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.liferay.ide.velocity.vaulttec.ui.IPreferencesConstants;
import com.liferay.ide.velocity.vaulttec.ui.VelocityPlugin;

/**
 * Velocimacro library settings.
 */
public class LibraryPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

    private static final String PREFIX = "LibraryPreferences.";

    public LibraryPreferencePage()
    {
        super(FieldEditorPreferencePage.GRID);
        setPreferenceStore(VelocityPlugin.getDefault().getPreferenceStore());
        setDescription(VelocityPlugin.getMessage(PREFIX + "description"));
    }

    /**
     * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
     */
    protected void createFieldEditors()
    {
        DirectoryFieldEditor macroPath = new DirectoryFieldEditor(IPreferencesConstants.LIBRARY_PATH, VelocityPlugin.getMessage(PREFIX + "path"), getFieldEditorParent());
        addField(macroPath);
        LibraryEditor library = new LibraryEditor(IPreferencesConstants.LIBRARY_LIST, VelocityPlugin.getMessage(PREFIX + "libraryList"), macroPath, getFieldEditorParent());
        addField(library);
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench aWorkbench)
    {
    }

    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk()
    {
        boolean value = super.performOk();
        VelocityPlugin.getDefault().savePluginPreferences();
        return value;
    }
}
