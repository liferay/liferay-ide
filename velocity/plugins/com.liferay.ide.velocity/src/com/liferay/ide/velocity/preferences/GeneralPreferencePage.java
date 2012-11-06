package com.liferay.ide.velocity.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.liferay.ide.velocity.vaulttec.ui.VelocityPlugin;

/**
 * Velocimacro library settings.
 */
public class GeneralPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

    public static final String P_CASE       = "USE_UPPER_CASE";
    public static final String P_Completion = "AUTO_COMPLETION";
    //public static final String P_PREVIEW = "NO_SHOW_PREVIEW_TAB";

    public GeneralPreferencePage()
    {
        super(FieldEditorPreferencePage.GRID);
        setPreferenceStore(VelocityPlugin.getDefault().getPreferenceStore());
        setDescription("General settings");
        initializeDefaults();
    }

    private void initializeDefaults()
    {
        IPreferenceStore store = getPreferenceStore();
        store.setDefault(P_CASE, true);
        store.setDefault(P_Completion, true);
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common
     * GUI blocks needed to manipulate various types of preferences. Each field
     * editor knows how to save and restore itself.
     */
    public void createFieldEditors()
    {
      addField(new BooleanFieldEditor(P_CASE, "use upper case", getFieldEditorParent()));
      addField(new BooleanFieldEditor(P_Completion, "auto closing of braces,brackets etc.", getFieldEditorParent()));
      //addField(new BooleanFieldEditor(P_PREVIEW, "Do not show editor preview tab", getFieldEditorParent()));
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
