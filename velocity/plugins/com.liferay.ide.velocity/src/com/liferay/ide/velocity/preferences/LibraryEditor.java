package com.liferay.ide.velocity.preferences;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

/**
 * A field editor to maintain a list of Velocimacro files.
 */
public class LibraryEditor extends ListEditor
{

    private DirectoryFieldEditor fDirectory;

    /**
     * Creates a new field editor.
     * 
     * @param aName
     *            the name of the preference this field editor works on
     * @param aLabelText
     *            the label text of the field editor
     * @param aParent
     *            the parent of the field editor's control
     */
    public LibraryEditor(String aName, String aLabelText, DirectoryFieldEditor aDirectory, Composite aParent)
    {
        init(aName, aLabelText);
        fDirectory = aDirectory;
        createControl(aParent);
    }

    protected String createList(String[] aDirectives)
    {
        StringBuffer directives = new StringBuffer();
        for (int i = 0; i < aDirectives.length; i++)
        {
            directives.append(aDirectives[i]);
            directives.append(',');
        }
        return directives.toString();
    }

    protected String getNewInputObject()
    {
        FileDialog dialog = new FileDialog(getShell());
        dialog.setFilterPath(fDirectory.getStringValue());
        String library = dialog.open();
        if (library != null)
        {
            library = new File(library).getName();
        }
        return library;
    }

    protected String[] parseString(String aDirectivesList)
    {
        StringTokenizer st = new StringTokenizer(aDirectivesList, ",\n\r");
        ArrayList v = new ArrayList();
        while (st.hasMoreElements())
        {
            v.add(st.nextElement());
        }
        return (String[]) v.toArray(new String[v.size()]);
    }
}
