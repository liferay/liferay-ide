package com.liferay.ide.velocity.vaulttec.ui.editor.actions;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

import com.liferay.ide.velocity.vaulttec.ui.IPreferencesConstants;
import com.liferay.ide.velocity.vaulttec.ui.VelocityPlugin;
import com.liferay.ide.velocity.vaulttec.ui.VelocityPluginImages;

/**
 * A toolbar action which toggles the presentation model of the connected text
 * editor. The editor shows either the highlight range only or always the whole
 * document.
 * <p>
 * Adopted from <code>org.eclipse.jdt.internal.ui.javaeditor.*</code>
 */
public class TogglePresentationAction extends TextEditorAction
{

    /**
     * Constructs and updates the action.
     */
    public TogglePresentationAction()
    {
        super(VelocityPlugin.getDefault().getResourceBundle(), "VelocityEditor.TogglePresentation.", null);
        VelocityPluginImages.setToolImageDescriptors(this, "segment_edit.gif");
        setToolTipText(VelocityPlugin.getMessage("VelocityEditor.TogglePresentation.tooltip"));
        update();
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run()
    {
        ITextEditor editor = getTextEditor();
        if (editor != null)
        {
            IRegion remembered = editor.getHighlightRange();
            editor.resetHighlightRange();
            boolean showAll = !editor.showsHighlightRangeOnly();
            setChecked(showAll);
            editor.showHighlightRangeOnly(showAll);
            if (remembered != null)
            {
                editor.setHighlightRange(remembered.getOffset(), remembered.getLength(), true);
            }
            IPreferenceStore store = VelocityPlugin.getDefault().getPreferenceStore();
            store.setValue(IPreferencesConstants.EDITOR_SHOW_SEGMENTS, showAll);
        }
    }

    /**
     * @see org.eclipse.ui.texteditor.IUpdate#update()
     */
    public void update()
    {
        ITextEditor editor = getTextEditor();
        boolean checked = ((editor != null) && editor.showsHighlightRangeOnly());
        setChecked(checked);
        setEnabled(editor != null);
    }

    /**
     * @see org.eclipse.ui.texteditor.TextEditorAction#setEditor(org.eclipse.ui.texteditor.ITextEditor)
     */
    public void setEditor(ITextEditor anEditor)
    {
        super.setEditor(anEditor);
        if (anEditor != null)
        {
            IPreferenceStore store = VelocityPlugin.getDefault().getPreferenceStore();
            boolean showSegments = store.getBoolean(IPreferencesConstants.EDITOR_SHOW_SEGMENTS);
            if (anEditor.showsHighlightRangeOnly() != showSegments)
            {
                IRegion remembered = anEditor.getHighlightRange();
                anEditor.resetHighlightRange();
                anEditor.showHighlightRangeOnly(showSegments);
                if (remembered != null)
                {
                    anEditor.setHighlightRange(remembered.getOffset(), remembered.getLength(), true);
                }
            }
            update();
        }
    }
}
