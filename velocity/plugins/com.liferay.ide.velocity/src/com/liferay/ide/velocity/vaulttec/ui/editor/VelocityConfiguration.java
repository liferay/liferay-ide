package com.liferay.ide.velocity.vaulttec.ui.editor;

import com.liferay.ide.velocity.editor.VelocityCompletionProcessor;
import com.liferay.ide.velocity.editor.VelocityEditor;
import com.liferay.ide.velocity.scanner.HTMLScanner;
import com.liferay.ide.velocity.scanner.HTMLScriptScanner;
import com.liferay.ide.velocity.scanner.HTMLTagScanner;
import com.liferay.ide.velocity.scanner.NonRuleBasedDamagerRepairer;
import com.liferay.ide.velocity.scanner.VelocityPartitionScanner;
import com.liferay.ide.velocity.ui.editor.xml.IEditorConfiguration;
import com.liferay.ide.velocity.ui.editor.xml.VelocityAutoIndentStrategy;
import com.liferay.ide.velocity.ui.editor.xml.WholePartitionDamagerRepairer;
import com.liferay.ide.velocity.vaulttec.ui.IColorConstants;
import com.liferay.ide.velocity.vaulttec.ui.VelocityColorProvider;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;


/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 35 $
 * @author <a href="mailto:akmal.sarhan@gmail.com">Akmal Sarhan </a>
 */
public class VelocityConfiguration extends SourceViewerConfiguration implements IEditorConfiguration
{

    public static final String[]       PREFORMATTED_PARTITIONS = { IEditorConfiguration.SCRIPT_PARTITION, IEditorConfiguration.CDATA_PARTITION, IEditorConfiguration.PROC_INST_PARTITION, IEditorConfiguration.MULTI_LINE_COMMENT};
    // public static final String[] ESCAPED_PARTITIONS = { "__script_partition",
    // "__cdata_partition", "__proc_inst_partition", "__comment_partition" };
    public static final String[]       ESCAPED_PARTITIONS      = { IEditorConfiguration.CDATA_PARTITION, IEditorConfiguration.PROC_INST_PARTITION, IEditorConfiguration.MULTI_LINE_COMMENT,IEditorConfiguration.COMMENT_PARTITION}; // look
                                                                                                                                            // at
                                                                                                                                            // the
                                                                                                                                            // commented
                                                                                                                                            // out
                                                                                                                                            // code
                                                                                                                                            // line
                                                                                                                                            // 760
                                                                                                                                            // in
                                                                                                                                            // VelocityCompletionProcessor
    public static final String[]       CDATA_PARTITIONS        = { IEditorConfiguration.CDATA_PARTITION };
    private static Set                 fEMPTY_TAG_SET;
    static
    {
        fEMPTY_TAG_SET = new HashSet();
        for (int i = 0; i < com.liferay.ide.velocity.ui.editor.xml.IHTMLConstants.EMPTY_TAGS.length; i++)
        {
            fEMPTY_TAG_SET.add(com.liferay.ide.velocity.ui.editor.xml.IHTMLConstants.EMPTY_TAGS[i]);
        }
    }
    private VelocityEditor             fEditor;
    private VelocityAutoIndentStrategy fAutoIndentStrategy;
    private HTMLTagScanner             fTagScanner;
    private HTMLScriptScanner          fScriptScanner;
    private HTMLScanner                fScanner;
    VelocityColorProvider              cp                      = null;
    

    public VelocityConfiguration(VelocityEditor anEditor)
    {
        fEditor = anEditor;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getConfiguredContentTypes(org.eclipse.jface.text.source.ISourceViewer)
     */
    public String[] getConfiguredContentTypes(ISourceViewer aSourceViewer)
    {
        return VelocityPartitionScanner.TYPES;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public IDocumentPartitioner createDocumentPartitioner()
    {
        return new FastPartitioner(new VelocityPartitionScanner(), VelocityPartitionScanner.TYPES);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param sourceViewer
     *            DOCUMENT ME!
     * @param contentType
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType)
    {
        if (fAutoIndentStrategy == null)
        {
            fAutoIndentStrategy = new VelocityAutoIndentStrategy(this, sourceViewer);
        }
         return new IAutoEditStrategy[] {fAutoIndentStrategy};
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getTextHover(org.eclipse.jface.text.source.ISourceViewer,
     *      java.lang.String)
     */
    public ITextHover getTextHover(ISourceViewer aSourceViewer, String aContentType)
    {
        ITextHover hover;
        if (aContentType.equals(IDocument.DEFAULT_CONTENT_TYPE) || aContentType.equals(IEditorConfiguration.TAG_PARTITION) || aContentType.equals(IEditorConfiguration.PARSED_STRING))
        {
            hover = new VelocityTextHover(fEditor);
        } else
        {
            hover = null;
        }
        return hover;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getAnnotationHover(org.eclipse.jface.text.source.ISourceViewer)
     */
    public IAnnotationHover getAnnotationHover(ISourceViewer aSourceViewer)
    {
        return new VelocityAnnotationHover();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getContentAssistant(org.eclipse.jface.text.source.ISourceViewer)
     */
    public IContentAssistant getContentAssistant(ISourceViewer aSourceViewer)
    {
        ContentAssistant assistant = new ContentAssistant();
        assistant.setContentAssistProcessor(new VelocityCompletionProcessor(fEditor, true), IDocument.DEFAULT_CONTENT_TYPE);
        assistant.setContentAssistProcessor(new VelocityCompletionProcessor(fEditor, false), IEditorConfiguration.PARSED_STRING);
        assistant.setContentAssistProcessor(new VelocityCompletionProcessor(fEditor, false), IEditorConfiguration.CDATA_PARTITION);
        assistant.setContentAssistProcessor(new VelocityCompletionProcessor(fEditor, false), IEditorConfiguration.DOC_COMMENT);
        assistant.setContentAssistProcessor(new VelocityCompletionProcessor(fEditor, false), IEditorConfiguration.MULTI_LINE_COMMENT);
        assistant.setContentAssistProcessor(new VelocityCompletionProcessor(fEditor, false), IEditorConfiguration.PROC_PARTITION);
        assistant.setContentAssistProcessor(new VelocityCompletionProcessor(fEditor, true), IEditorConfiguration.SCRIPT_PARTITION);
        assistant.setContentAssistProcessor(new VelocityCompletionProcessor(fEditor, false), IEditorConfiguration.SINGLE_LINE_COMMENT);
        assistant.setContentAssistProcessor(new VelocityCompletionProcessor(fEditor, true), IEditorConfiguration.TAG_PARTITION);
        assistant.enableAutoInsert(true);
        assistant.enableAutoActivation(true);
        return assistant;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getDoubleClickStrategy(org.eclipse.jface.text.source.ISourceViewer,
     *      java.lang.String)
     */
    public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer aSourceViewer, String aContentType)
    {
        return VelocityEditorEnvironment.getDoubleClickStrategy();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getDefaultPrefixes(org.eclipse.jface.text.source.ISourceViewer,
     *      java.lang.String)
     */
    public String[] getDefaultPrefixes(ISourceViewer aSourceViewer, String aContentType)
    {
        return new String[] { "##", "" };
    }

    protected HTMLScanner getHTMLScanner()
    {
        if (fScanner == null)
        {
            cp = VelocityEditorEnvironment.getColorProvider();
            fScanner = new HTMLScanner();
            fScanner.setDefaultReturnToken(cp.getToken(IColorConstants.HTML_String));
        }
        return fScanner;
    }
   
    protected HTMLTagScanner getHTMLTagScanner()
    {
        if (fTagScanner == null)
        {
            cp = VelocityEditorEnvironment.getColorProvider();
            fTagScanner = new HTMLTagScanner(cp);
        }
        return fTagScanner;
    }

    protected HTMLScriptScanner getScriptScanner()
    {
        if (fScriptScanner == null)
        {
            cp = VelocityEditorEnvironment.getColorProvider();
            fScriptScanner = new HTMLScriptScanner(cp);
        }
        return fScriptScanner;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getPresentationReconciler(org.eclipse.jface.text.source.ISourceViewer)
     */
    public IPresentationReconciler getPresentationReconciler(ISourceViewer aSourceViewer)
    {
        cp = VelocityEditorEnvironment.getColorProvider();
        PresentationReconciler rec = new PresentationReconciler();
        NonRuleBasedDamagerRepairer ndr = null;
        DefaultDamagerRepairer dr = null;
        dr = new DefaultDamagerRepairer(VelocityEditorEnvironment.getCodeScanner());
        rec.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        rec.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
      
        dr = new DefaultDamagerRepairer(getHTMLTagScanner());
        rec.setDamager(dr, IEditorConfiguration.TAG_PARTITION);
        rec.setRepairer(dr, IEditorConfiguration.TAG_PARTITION);
        
//        dr = new DefaultDamagerRepairer(getXMLTagScanner());
//	rec.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
//	rec.setRepairer(dr,IDocument.DEFAULT_CONTENT_TYPE);
//	
//        dr = new DefaultDamagerRepairer(getXMLScanner());
//	rec.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
//	rec.setRepairer(dr,IDocument.DEFAULT_CONTENT_TYPE);
//	
        ndr = new NonRuleBasedDamagerRepairer(cp.getTextAttribute(IColorConstants.COMMENT));
        rec.setDamager(ndr, IEditorConfiguration.SINGLE_LINE_COMMENT);
        rec.setRepairer(ndr, IEditorConfiguration.SINGLE_LINE_COMMENT);
        ndr = new NonRuleBasedDamagerRepairer(cp.getTextAttribute(IColorConstants.DOC_COMMENT));
        rec.setDamager(ndr, IEditorConfiguration.DOC_COMMENT);
        rec.setRepairer(ndr, IEditorConfiguration.DOC_COMMENT);
        ndr = new NonRuleBasedDamagerRepairer(cp.getTextAttribute(IColorConstants.STRING));
        rec.setDamager(ndr, IEditorConfiguration.UNPARSED_STRING);
        rec.setRepairer(ndr, IEditorConfiguration.UNPARSED_STRING);
        dr = new DefaultDamagerRepairer(VelocityEditorEnvironment.getStringScanner());
        rec.setDamager(dr, IEditorConfiguration.PARSED_STRING);
        rec.setRepairer(dr, IEditorConfiguration.PARSED_STRING);
        WholePartitionDamagerRepairer wdr = new WholePartitionDamagerRepairer(getScriptScanner());
        rec.setDamager(wdr, IEditorConfiguration.SCRIPT_PARTITION);
        rec.setRepairer(wdr, IEditorConfiguration.SCRIPT_PARTITION);
        ndr = new NonRuleBasedDamagerRepairer(cp.getTextAttribute(IColorConstants.COMMENT));
        rec.setDamager(ndr, IEditorConfiguration.MULTI_LINE_COMMENT);
        rec.setRepairer(ndr, IEditorConfiguration.MULTI_LINE_COMMENT);
        return rec;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getReconciler(org.eclipse.jface.text.source.ISourceViewer)
     */
    public IReconciler getReconciler(ISourceViewer aSourceViewer)
    {
        return new MonoReconciler(fEditor.getReconcilingStrategy(), false);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getIndentPrefixes(org.eclipse.jface.text.source.ISourceViewer,
     *      java.lang.String)
     */
    public String[] getIndentPrefixes(ISourceViewer aSourceViewer, String aContentType)
    {
        Vector prefixes = new Vector();
        // Create prefixes from JDT preferences settings
        int tabWidth = getTabWidth(aSourceViewer);
        // IPreferenceStore prefs =
        // JavaPlugin.getDefault().getPreferenceStore();
        boolean useSpaces = true; // prefs.getBoolean(PreferenceConstants.EDITOR_SPACES_FOR_TABS);
        // prefix[0] is either '\t' or ' ' x tabWidth, depending on useSpaces
        for (int i = 0; i <= tabWidth; i++)
        {
            StringBuffer prefix = new StringBuffer();
            if (useSpaces)
            {
                for (int j = 0; (j + i) < tabWidth; j++)
                {
                    prefix.append(' ');
                }
                if (i != 0)
                {
                    prefix.append('\t');
                }
            } else
            {
                for (int j = 0; j < i; j++)
                {
                    prefix.append(' ');
                }
                if (i != tabWidth)
                {
                    prefix.append('\t');
                }
            }
            prefixes.add(prefix.toString());
        }
        prefixes.add("");
        return (String[]) prefixes.toArray(new String[prefixes.size()]);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getTabWidth(org.eclipse.jface.text.source.ISourceViewer)
     */
    public int getTabWidth(ISourceViewer aSourceViewer)
    {
        // Get tab width from JDT preferences settings
        IPreferenceStore prefs =
        EditorsPlugin.getDefault().getPreferenceStore();
        int width =  prefs.getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
        return width;
    }

    /*
     * (non-Javadoc)
     * @see com.googlecode.veloeclipse.vaulttec.ui.editor.xml.IEditorConfiguration#getPreferences()
     */
    public IPreferenceStore getPreferences()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.googlecode.veloeclipse.vaulttec.ui.editor.xml.IEditorConfiguration#getLineWidth(org.eclipse.jface.text.source.ISourceViewer)
     */
    public int getLineWidth(ISourceViewer isourceviewer)
    {
        IPreferenceStore prefs =
        EditorsPlugin.getDefault().getPreferenceStore();
        int width =  prefs.getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN);
        return width;
    }

    /*
     * (non-Javadoc)
     * @see com.googlecode.veloeclipse.vaulttec.ui.editor.xml.IEditorConfiguration#getTab(org.eclipse.jface.text.source.ISourceViewer)
     */
    public String getTab(ISourceViewer isourceviewer)
    {
        IPreferenceStore prefs =
        EditorsPlugin.getDefault().getPreferenceStore();
        if (prefs.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS))
        {
          int tabWidth = getTabWidth(isourceviewer);
          StringBuffer sb = new StringBuffer(tabWidth);
          for (int i=0; i<tabWidth; i++)
          {
            sb.append(' ');
          }
          return sb.toString();
        }
        return "\t";
    }
    // public IPresentationReconciler getPresentationReconciler(ISourceViewer
    // sourceViewer)
    // {
    // PresentationReconciler reconciler = new PresentationReconciler();
    // DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getHTMLScanner());
    // reconciler.setDamager(dr, "__dftl_partition_content_type");
    // reconciler.setRepairer(dr, "__dftl_partition_content_type");
    // dr = new DefaultDamagerRepairer(getHTMLTagScanner());
    // reconciler.setDamager(dr, "__tag_partition");
    // reconciler.setRepairer(dr, "__tag_partition");
    // WholePartitionDamagerRepairer wdr = new
    // WholePartitionDamagerRepairer(getScriptScanner());
    // reconciler.setDamager(wdr, "__script_partition");
    // reconciler.setRepairer(wdr, "__script_partition");
    // wdr = new WholePartitionDamagerRepairer(getScriptScanner());
    // reconciler.setDamager(wdr, "__cdata_partition");
    // reconciler.setRepairer(wdr, "__cdata_partition");
    // NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(new
    // TextAttribute(EditorsPlugin.getColor("HTMLProcessInstruction")));
    // reconciler.setDamager(ndr, "__proc_inst_partition");
    // reconciler.setRepairer(ndr, "__proc_inst_partition");
    // ndr = new NonRuleBasedDamagerRepairer(new
    // TextAttribute(EditorsPlugin.getColor("HTMLComment")));
    // reconciler.setDamager(ndr, "__comment_partition");
    // reconciler.setRepairer(ndr, "__comment_partition");
    // return reconciler;
    // }
}
