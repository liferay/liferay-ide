package com.liferay.ide.velocity.preferences;

import java.util.ArrayList;
import java.util.StringTokenizer;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;

/**
 * A field editor to maintain a list of Velocity user directives.
 */
public class DirectiveEditor extends ListEditor
{

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
    public DirectiveEditor(String aName, String aLabelText, Composite aParent)
    {
        init(aName, aLabelText);
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
        DirectiveDialog dialog = new DirectiveDialog(getShell());
        if (dialog.open() == Window.OK) { return dialog.getValue(); }
        return null;
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
