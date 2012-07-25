package com.liferay.ide.velocity.scanner;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.WordPatternRule;
import org.eclipse.jface.text.source.ISourceViewer;

import com.liferay.ide.velocity.ui.editor.xml.IEditorConfiguration;
import com.liferay.ide.velocity.ui.editor.xml.IgnoreCasePatternRule;
import com.liferay.ide.velocity.vaulttec.ui.VelocityColorProvider;
import com.liferay.ide.velocity.vaulttec.ui.editor.VelocityEditorEnvironment;
import com.liferay.ide.velocity.vaulttec.ui.editor.text.EmptyCommentDetector;

/**
 * This scanner recognizes the Velocity strings and comments.
 */
public class VelocityPartitionScanner extends RuleBasedPartitionScanner implements IEditorConfiguration
{

    public final static String[] TYPES = new String[] {
            IDocument.DEFAULT_CONTENT_TYPE,
            SINGLE_LINE_COMMENT,
            MULTI_LINE_COMMENT,
            DOC_COMMENT,
            PARSED_STRING,
            UNPARSED_STRING,
            TAG_PARTITION,
            CDATA_PARTITION,
            SCRIPT_PARTITION,
            PROC_PARTITION,
            TEXT                      };
    VelocityColorProvider        cp    = null;

    /**
     * Creates the partitioner and sets up the appropriate rules.
     */
    public VelocityPartitionScanner()
    {
        cp = VelocityEditorEnvironment.getColorProvider();
        List rules = new ArrayList();
        org.eclipse.jface.text.rules.IToken comment = cp.getToken(MULTI_LINE_COMMENT, true);
        org.eclipse.jface.text.rules.IToken proc_inst = cp.getToken(PROC_PARTITION, true);
        org.eclipse.jface.text.rules.IToken script = cp.getToken(SCRIPT_PARTITION, true);
        org.eclipse.jface.text.rules.IToken cdata = cp.getToken(CDATA_PARTITION, true);
        org.eclipse.jface.text.rules.IToken tag = cp.getToken(TAG_PARTITION, true);
        // org.eclipse.jface.text.rules.IToken text = cp.getToken(TEXT, true);
        rules.add(new MultiLineRule("<!--", "-->", comment));
//        rules.add(new MultiLineRule("<?", "?>", proc_inst));
//        rules.add(new MultiLineRule("<%", "%>", proc_inst));
//        rules.add(new MultiLineRule("<#", "#>", proc_inst));
        rules.add(new MultiLineRule("<![CDATA[", "]]>", proc_inst));
        rules.add(new MultiLineRule("<![", "]>", proc_inst));
        rules.add(new IgnoreCasePatternRule("<script", "</script>", script, '\0', false, false));
        rules.add(new IgnoreCasePatternRule("<pre>", "</pre>", cdata, '\0', false, false));
        rules.add(new HTMLTagRule("<", ">", tag, '\0', true));
        // Add rule for single line comments
        rules.add(new EndOfLineRule("##", cp.getToken(SINGLE_LINE_COMMENT, true)));
        // Add rule for strings
        rules.add(new SingleLineRule("\"", "\"", cp.getToken(PARSED_STRING, true), '\\'));
        // Add rule for character constants.
        rules.add(new SingleLineRule("'", "'", cp.getToken(UNPARSED_STRING, true), '\\'));
        // Add rules for multi-line comments and doc comments
        rules.add(new MultiLineRule("#**", "*#", cp.getToken(DOC_COMMENT, true)));
        rules.add(new MultiLineRule("#*", "*#", cp.getToken(MULTI_LINE_COMMENT, true)));
        // Add special empty comment word rules
        rules.add(new WordPatternRule(new EmptyCommentDetector(), "#***#", null, cp.getToken(DOC_COMMENT, true)));
        rules.add(new WordPatternRule(new EmptyCommentDetector(), "#**#", null, cp.getToken(MULTI_LINE_COMMENT, true)));
        IPredicateRule[] result = new IPredicateRule[rules.size()];
        rules.toArray(result);
        setPredicateRules(result);
    }

    /*
     * (non-Javadoc)
     * @see com.googlecode.veloeclipse.vaulttec.ui.editor.xml.IEditorConfiguration#getPreferences()
     */
    public IPreferenceStore getPreferences()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.googlecode.veloeclipse.vaulttec.ui.editor.xml.IEditorConfiguration#getLineWidth(org.eclipse.jface.text.source.ISourceViewer)
     */
    public int getLineWidth(ISourceViewer isourceviewer)
    {
        return 0;
    }

    /*
     * (non-Javadoc)
     * @see com.googlecode.veloeclipse.vaulttec.ui.editor.xml.IEditorConfiguration#getTabWidth(org.eclipse.jface.text.source.ISourceViewer)
     */
    public int getTabWidth(ISourceViewer isourceviewer)
    {
        return 0;
    }

    /*
     * (non-Javadoc)
     * @see com.googlecode.veloeclipse.vaulttec.ui.editor.xml.IEditorConfiguration#getTab(org.eclipse.jface.text.source.ISourceViewer)
     */
    public String getTab(ISourceViewer isourceviewer)
    {
        return null;
    }
}
