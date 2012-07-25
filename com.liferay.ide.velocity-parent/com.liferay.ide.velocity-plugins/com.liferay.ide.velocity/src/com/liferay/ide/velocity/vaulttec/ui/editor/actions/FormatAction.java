package com.liferay.ide.velocity.vaulttec.ui.editor.actions;

import java.util.ResourceBundle;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

/**
 * DOCUMENT ME!
 * 
 * @author <a href="mailto:akmal.sarhan@gmail.com">Akmal Sarhan </a>
 * @version $Revision: 14 $
 */
public class FormatAction extends TextEditorAction implements IObjectActionDelegate
{
 private   Formatter formatter;

    public FormatAction(ResourceBundle aBundle, String aPrefix, ITextEditor anEditor) {
	super(aBundle, aPrefix, anEditor);
	 formatter=new Formatter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run()
    {
	final IDocument document = getTextEditor().getDocumentProvider().getDocument(getTextEditor().getEditorInput());
	
	formatter.format(document);
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart)
    {
	// TODO Auto-generated method stub
	
    }

    public void run(IAction action)
    {
	formatter.run(action);
	
    }

    public void selectionChanged(IAction action, ISelection selection)
    {
	formatter.selectionChanged(action, selection);
	
    }

}
