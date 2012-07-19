package com.liferay.ide.velocity.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.liferay.ide.velocity.vaulttec.ui.VelocityPlugin;

/**
 * Velocimacro library settings.
 */
public class JtidyPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

    public static final String   JTIDY_INDENT = "indent";
    public static final String[] JTIDY_NUMER  = new String[] { "indent-spaces", "wrap" };
    public static final String[] JTIDY_PREF   = new String[] {
            "output-xml",
            "output-xhtml",
            "clean",
            "logical-emphasis",
            "show-warnings",
            "drop-font-tags",
            "drop-empty-paras",
            "bare",
            "markup",
            "add-xml-decl",
            "add-xml-pi",
            "assume-xml-procins",
            "input-xml",
            "quote-marks",
            "quote-ampersand",
            "uppercase-attributes",
            "fix-backslash",
            "quote-nbsp",
            "uppercase-tags",
            "enclose-block-text",
            "enclose-text",
            "indent-attributes",
            "break-before-br",
            "literal-attributes",
            "tidy-mark",
            "numeric-entities",
            "wrap-attributes",
            "wrap-sections"                  };

    public JtidyPreferencePage()
    {
        super(FieldEditorPreferencePage.GRID);
        setPreferenceStore(VelocityPlugin.getDefault().getPreferenceStore());
        setDescription("Tidy");
        initializeDefaults();
    }

    private void initializeDefaults()
    {
        IPreferenceStore store = getPreferenceStore();
        for (int i = 0; i < 10; i++)
        {
            store.setDefault(JTIDY_PREF[i], true);
        }
        store.setDefault(JTIDY_NUMER[0], 2);
        // store.setDefault(JTIDY_NUMER[1], 2);
        store.setDefault(JTIDY_NUMER[1], 80);
        store.setDefault(JTIDY_INDENT, true);
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common
     * GUI blocks needed to manipulate various types of preferences. Each field
     * editor knows how to save and restore itself.
     */
    public void createFieldEditors()
    {
        addField(new BooleanFieldEditor(JTIDY_INDENT, JTIDY_INDENT, getFieldEditorParent()));
        for (int i = 0; i < JTIDY_NUMER.length; i++)
        {
            addField(new IntegerFieldEditor(JTIDY_NUMER[i], JTIDY_NUMER[i], getFieldEditorParent()));
        }
        for (int i = 0; i < JTIDY_PREF.length; i++)
        {
            addField(new BooleanFieldEditor(JTIDY_PREF[i], JTIDY_PREF[i], getFieldEditorParent()));
        }
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
