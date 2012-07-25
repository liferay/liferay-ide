package com.liferay.ide.velocity.vaulttec.ui.editor.actions;

import org.eclipse.ui.texteditor.ITextEditorActionConstants;

/**
 * Action IDs for standard actions, for groups in the menu bar, and for actions
 * in context menus of Velocity views.
 */
public interface IVelocityActionConstants extends ITextEditorActionConstants
{

    /**
     * Edit menu: name of standard Code Assist global action (value
     * <code>"ContentAssist"</code>).
     */
    public static final String CONTENT_ASSIST  = "ContentAssist";
    /**
     * Edit menu: name of standard Open Editor global action (value
     * <code>"GotoDefinition"</code>).
     */
    public static final String GOTO_DEFINITION = "GotoDefinition";
    public static final String JTIDY           = "JTidy";
    /**
     * Source menu: name of standard Comment global action (value
     * <code>"Comment"</code>).
     */
    public static final String COMMENT         = "Comment";
    public static final String TOGGLE_COMMENT  = "ToggleComment";
    public static final String FORMAT          = "FormatEditor";
    /**
     * Source menu: name of standard Uncomment global action (value
     * <code>"Uncomment"</code>).
     */
    public static final String UNCOMMENT       = "Uncomment";
}
