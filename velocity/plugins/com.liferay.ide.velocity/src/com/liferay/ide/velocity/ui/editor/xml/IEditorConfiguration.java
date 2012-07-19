package com.liferay.ide.velocity.ui.editor.xml;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.ISourceViewer;

/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 7 $
 * @author <a href="mailto:akmal.sarhan@gmail.com">Akmal Sarhan </a>
 */
public interface IEditorConfiguration
{

    public static final String DEFAULT_PARTITION   = "__dftl_partition_content_type";
    public static final String JAVADOC_PARTITION   = "__javadoc_partition";
    public static final String COMMENT_PARTITION   = "__comment_partition";
    public static final String COMMENT1_PARTITION  = "__comment1_partition";
    public static final String STRING_PARTITION    = "__string_partition";
    public static final String TAG_PARTITION       = "__tag_partition";
    public static final String SCRIPT_PARTITION    = "__script_partition";
    public static final String PROC_INST_PARTITION = "__proc_inst_partition";
    public static final String CDATA_PARTITION     = "__cdata_partition";
    public final static String SINGLE_LINE_COMMENT = "__singleline_comment";
    public final static String MULTI_LINE_COMMENT  = "__multiline_comment";
    public final static String PROC_PARTITION      = "__proc_inst_partition";
    public final static String TEXT                = "__text";
    public final static String COMMENT             = "comment";
    public final static String DOC_COMMENT         = "__doc_comment";
    public final static String PARSED_STRING       = "__parsed_string";
    public final static String UNPARSED_STRING     = "__unparsed_string";

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public abstract IPreferenceStore getPreferences();

    /**
     * DOCUMENT ME!
     * 
     * @param isourceviewer
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public abstract int getLineWidth(ISourceViewer isourceviewer);

    /**
     * DOCUMENT ME!
     * 
     * @param isourceviewer
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public abstract int getTabWidth(ISourceViewer isourceviewer);

    /**
     * DOCUMENT ME!
     * 
     * @param isourceviewer
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public abstract String getTab(ISourceViewer isourceviewer);
}
