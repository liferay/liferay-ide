package com.liferay.ide.velocity.vaulttec.ui.editor.actions;

import java.util.ResourceBundle;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

import com.liferay.ide.velocity.editor.VelocityEditor;

/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 14 $
 * @author <a href="mailto:akmal.sarhan@gmail.com">Akmal Sarhan </a>
 */
public class GotoDefinitionAction extends TextEditorAction
{

    public GotoDefinitionAction(ResourceBundle aBundle, String aPrefix, ITextEditor anEditor)
    {
        super(aBundle, aPrefix, anEditor);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run()
    {
        VelocityEditor editor = (VelocityEditor) getTextEditor();
        if (editor.fMouseListener.fActiveRegion != null)
        {
            // If the user is using the ctrl-alt mouse click feature, then
            // goto the definition under the mouse, and not under the current cursor location
            editor.gotoDefinition(editor.fMouseListener.fActiveRegion);
            return;
        }
        
        ITextSelection selection = (ITextSelection) editor.getSelectionProvider().getSelection();
        if (!selection.isEmpty())
        {
            
            editor.gotoDefinition(new Region(selection.getOffset(), selection.getLength()));
        }
    }
}
