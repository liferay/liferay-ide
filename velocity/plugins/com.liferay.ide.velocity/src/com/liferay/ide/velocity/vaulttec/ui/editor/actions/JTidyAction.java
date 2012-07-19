package com.liferay.ide.velocity.vaulttec.ui.editor.actions;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.w3c.tidy.Tidy;

import com.liferay.ide.velocity.editor.compare.VelocityCompare;
import com.liferay.ide.velocity.editor.compare.VelocityInput;
import com.liferay.ide.velocity.preferences.JtidyPreferencePage;
import com.liferay.ide.velocity.vaulttec.ui.VelocityPlugin;


/**
 * DOCUMENT ME!
 * 
 * @author <a href="mailto:akmal.sarhan@gmail.com">Akmal Sarhan </a>
 * @version $Revision: 14 $
 */
public class JTidyAction extends TextEditorAction implements IObjectActionDelegate
{

    public JTidyAction(ResourceBundle aBundle, String aPrefix, ITextEditor anEditor)
    {
        super(aBundle, aPrefix, anEditor);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run()
    {
        final IDocument document = getTextEditor().getDocumentProvider().getDocument(getTextEditor().getEditorInput());
        tidy(document);
    }

    private void tidy(IDocument fDocument)
    {
        Tidy tidy = new Tidy();
        IPreferenceStore preferenceStore = VelocityPlugin.getDefault().getPreferenceStore();
        Properties properties = new Properties();
        for (int i = 0; i < JtidyPreferencePage.JTIDY_PREF.length; i++)
        {
            properties.setProperty(JtidyPreferencePage.JTIDY_PREF[i], String.valueOf(preferenceStore.getBoolean(JtidyPreferencePage.JTIDY_PREF[i])));
        }
        for (int i = 0; i < JtidyPreferencePage.JTIDY_NUMER.length; i++)
        {
            properties.setProperty(JtidyPreferencePage.JTIDY_NUMER[i], String.valueOf(preferenceStore.getInt(JtidyPreferencePage.JTIDY_NUMER[i])));
        }
        properties.setProperty(JtidyPreferencePage.JTIDY_INDENT, preferenceStore.getBoolean(JtidyPreferencePage.JTIDY_INDENT) ? "auto" : "no");
        tidy.setConfigurationFromProps(properties);
        BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(fDocument.get().getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream errOut = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(errOut, true);
        tidy.setErrout(printWriter);
        tidy.parse(in, out);
        VelocityInput left = new VelocityInput("left", fDocument.get());
        VelocityInput right = new VelocityInput("right", new String(out.toByteArray()));
        BufferedReader rd = new BufferedReader(new StringReader(new String(errOut.toByteArray())));
        String patternStr = "^line";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher("");
        StringBuffer buffer = new StringBuffer();
        // Retrieve all lines that match pattern
        String line = null;
        try
        {
            while ((line = rd.readLine()) != null)
            {
                matcher.reset(line);
                if (matcher.find())
                {
                    buffer.append(line);
                    buffer.append(Formatter.LINE_SEP);
                }
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String errText = buffer.toString();
        VelocityCompare velocityCompare=new VelocityCompare(left, right, errText);
	if (velocityCompare.openCompareDialog() == 0) {
        fDocument.set(right.getText());
	}
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
     *      org.eclipse.ui.IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart)
    {
        // TODO Auto-generated method stub
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action)
    {
        // TODO Auto-generated method stub
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection)
    {
        // TODO Auto-generated method stub
    }
}
