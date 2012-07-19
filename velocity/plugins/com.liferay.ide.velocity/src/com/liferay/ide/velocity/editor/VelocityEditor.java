package com.liferay.ide.velocity.editor;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.velocity.runtime.directive.VelocimacroProxy;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.browser.WorkbenchBrowserSupport;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.liferay.ide.velocity.scanner.XMLElementGuesser;
import com.liferay.ide.velocity.ui.editor.outline.VelocityOutlinePage;
import com.liferay.ide.velocity.ui.editor.xml.VelocityAutoIndentStrategy;
import com.liferay.ide.velocity.vaulttec.ui.IPreferencesConstants;
import com.liferay.ide.velocity.vaulttec.ui.VelocityColorProvider;
import com.liferay.ide.velocity.vaulttec.ui.VelocityPlugin;
import com.liferay.ide.velocity.vaulttec.ui.editor.VelocityConfiguration;
import com.liferay.ide.velocity.vaulttec.ui.editor.VelocityDocumentProvider;
import com.liferay.ide.velocity.vaulttec.ui.editor.VelocityEditorEnvironment;
import com.liferay.ide.velocity.vaulttec.ui.editor.VelocityReconcilingStrategy;
import com.liferay.ide.velocity.vaulttec.ui.editor.actions.FormatAction;
import com.liferay.ide.velocity.vaulttec.ui.editor.actions.GotoDefinitionAction;
import com.liferay.ide.velocity.vaulttec.ui.editor.actions.IVelocityActionConstants;
import com.liferay.ide.velocity.vaulttec.ui.editor.actions.IVelocityActionDefinitionIds;
import com.liferay.ide.velocity.vaulttec.ui.editor.actions.JTidyAction;
import com.liferay.ide.velocity.vaulttec.ui.editor.actions.ToggleCommentAction;
import com.liferay.ide.velocity.vaulttec.ui.editor.text.VelocityTextGuesser;
import com.liferay.ide.velocity.vaulttec.ui.model.ITreeNode;
import com.liferay.ide.velocity.vaulttec.ui.model.ModelTools;
import com.wutka.dtd.DTD;
import com.wutka.dtd.DTDElement;
import com.wutka.dtd.DTDParser;


/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 37 $
 * @author <a href="mailto:akmal.sarhan@gmail.com">Akmal Sarhan </a>
 */
public class VelocityEditor extends TextEditor implements IPropertyChangeListener
{

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createAnnotationAccess()
     */
    private static final String         TEMPLATE_PROPOSALS = "template_proposals_action";                        //$NON-NLS-1$
    public static Browser               definitionBrowser;
    private static final String         PREFIX             = "VelocityEditor.";
    private static DTD                  dtd                = null;
    public static final Map<String, String>  map                = new HashMap<String, String>(100);
    static
    {
        map.put("A", getDocLocation() + "wdghtml40/special/a.html");
        map.put("ABBR", getDocLocation() + "wdghtml40/phrase/abbr.html");
        map.put("ACRONYM", getDocLocation() + "wdghtml40/phrase/acronym.html");
        map.put("ADDRESS", getDocLocation() + "wdghtml40/block/address.html");
        map.put("APPLET", getDocLocation() + "wdghtml40/special/applet.html");
        map.put("AREA", getDocLocation() + "wdghtml40/special/area.html");
        map.put("B", getDocLocation() + "wdghtml40/fontstyle/b.html");
        map.put("BASE", getDocLocation() + "wdghtml40/head/base.html");
        map.put("BASEFONT", getDocLocation() + "wdghtml40/special/basefont.html");
        map.put("BDO", getDocLocation() + "wdghtml40/special/bdo.html");
        map.put("BIG", getDocLocation() + "wdghtml40/fontstyle/big.html");
        map.put("BLOCKQUOTE", getDocLocation() + "wdghtml40/block/blockquote.html");
        map.put("BODY", getDocLocation() + "wdghtml40/html/body.html");
        map.put("BR", getDocLocation() + "wdghtml40/special/br.html");
        map.put("BUTTON", getDocLocation() + "wdghtml40/forms/button.html");
        map.put("CAPTION", getDocLocation() + "wdghtml40/tables/caption.html");
        map.put("CENTER", getDocLocation() + "wdghtml40/block/center.html");
        map.put("CITE", getDocLocation() + "wdghtml40/phrase/cite.html");
        map.put("CODE", getDocLocation() + "wdghtml40/phrase/code.html");
        map.put("COL", getDocLocation() + "wdghtml40/tables/col.html");
        map.put("COLGROUP", getDocLocation() + "wdghtml40/tables/colgroup.html");
        map.put("DD", getDocLocation() + "wdghtml40/lists/dd.html");
        map.put("DEL", getDocLocation() + "wdghtml40/phrase/del.html");
        map.put("DFN", getDocLocation() + "wdghtml40/phrase/dfn.html");
        map.put("DIR", getDocLocation() + "wdghtml40/lists/dir.html");
        map.put("DIV", getDocLocation() + "wdghtml40/block/div.html");
        map.put("DL", getDocLocation() + "wdghtml40/lists/dl.html");
        map.put("DT", getDocLocation() + "wdghtml40/lists/dt.html");
        map.put("EM", getDocLocation() + "wdghtml40/phrase/em.html");
        map.put("FIELDSET", getDocLocation() + "wdghtml40/forms/fieldset.html");
        map.put("FONT", getDocLocation() + "wdghtml40/special/font.html");
        map.put("FORM", getDocLocation() + "wdghtml40/forms/form.html");
        map.put("FRAME", getDocLocation() + "wdghtml40/frames/frame.html");
        map.put("FRAMESET", getDocLocation() + "wdghtml40/frames/frameset.html");
        map.put("H1", getDocLocation() + "wdghtml40/block/h1.html");
        map.put("H2", getDocLocation() + "wdghtml40/block/h2.html");
        map.put("H3", getDocLocation() + "wdghtml40/block/h3.html");
        map.put("H4", getDocLocation() + "wdghtml40/block/h4.html");
        map.put("H5", getDocLocation() + "wdghtml40/block/h5.html");
        map.put("H6", getDocLocation() + "wdghtml40/block/h6.html");
        map.put("HEAD", getDocLocation() + "wdghtml40/head/head.html");
        map.put("HR", getDocLocation() + "wdghtml40/block/hr.html");
        map.put("HTML", getDocLocation() + "wdghtml40/html/html.html");
        map.put("I", getDocLocation() + "wdghtml40/fontstyle/i.html");
        map.put("IFRAME", getDocLocation() + "wdghtml40/special/iframe.html");
        map.put("IMG", getDocLocation() + "wdghtml40/special/img.html");
        map.put("INPUT", getDocLocation() + "wdghtml40/forms/input.html");
        map.put("INS", getDocLocation() + "wdghtml40/phrase/ins.html");
        map.put("ISINDEX", getDocLocation() + "wdghtml40/block/isindex.html");
        map.put("KBD", getDocLocation() + "wdghtml40/phrase/kbd.html");
        map.put("LABEL", getDocLocation() + "wdghtml40/forms/label.html");
        map.put("LEGEND", getDocLocation() + "wdghtml40/forms/legend.html");
        map.put("LI", getDocLocation() + "wdghtml40/lists/li.html");
        map.put("LINK", getDocLocation() + "wdghtml40/head/link.html");
        map.put("MAP", getDocLocation() + "wdghtml40/special/map.html");
        map.put("MENU", getDocLocation() + "wdghtml40/lists/menu.html");
        map.put("META", getDocLocation() + "wdghtml40/head/meta.html");
        map.put("NOFRAMES", getDocLocation() + "wdghtml40/frames/noframes.html");
        map.put("NOSCRIPT", getDocLocation() + "wdghtml40/block/noscript.html");
        map.put("OBJECT", getDocLocation() + "wdghtml40/special/object.html");
        map.put("OL", getDocLocation() + "wdghtml40/lists/ol.html");
        map.put("OPTGROUP", getDocLocation() + "wdghtml40/forms/optgroup.html");
        map.put("OPTION", getDocLocation() + "wdghtml40/forms/option.html");
        map.put("P", getDocLocation() + "wdghtml40/block/p.html");
        map.put("PARAM", getDocLocation() + "wdghtml40/special/param.html");
        map.put("PRE", getDocLocation() + "wdghtml40/block/pre.html");
        map.put("Q", getDocLocation() + "wdghtml40/special/q.html");
        map.put("S", getDocLocation() + "wdghtml40/fontstyle/s.html");
        map.put("SAMP", getDocLocation() + "wdghtml40/phrase/samp.html");
        map.put("SCRIPT", getDocLocation() + "wdghtml40/special/script.html");
        map.put("SELECT", getDocLocation() + "wdghtml40/forms/select.html");
        map.put("SMALL", getDocLocation() + "wdghtml40/fontstyle/small.html");
        map.put("SPAN", getDocLocation() + "wdghtml40/special/span.html");
        map.put("STRIKE", getDocLocation() + "wdghtml40/fontstyle/strike.html");
        map.put("STRONG", getDocLocation() + "wdghtml40/phrase/strong.html");
        map.put("STYLE", getDocLocation() + "wdghtml40/head/style.html");
        map.put("SUB", getDocLocation() + "wdghtml40/special/sub.html");
        map.put("SUP", getDocLocation() + "wdghtml40/special/sup.html");
        map.put("TABLE", getDocLocation() + "wdghtml40/tables/table.html");
        map.put("TBODY", getDocLocation() + "wdghtml40/tables/tbody.html");
        map.put("TD", getDocLocation() + "wdghtml40/tables/td.html");
        map.put("TEXTAREA", getDocLocation() + "wdghtml40/forms/textarea.html");
        map.put("TFOOT", getDocLocation() + "wdghtml40/tables/tfoot.html");
        map.put("TH", getDocLocation() + "wdghtml40/tables/th.html");
        map.put("THEAD", getDocLocation() + "wdghtml40/tables/thead.html");
        map.put("TITLE", getDocLocation() + "wdghtml40/head/title.html");
        map.put("TR", getDocLocation() + "wdghtml40/tables/tr.html");
        map.put("TT", getDocLocation() + "wdghtml40/fontstyle/tt.html");
        map.put("U", getDocLocation() + "wdghtml40/fontstyle/u.html");
        map.put("UL", getDocLocation() + "wdghtml40/lists/ul.html");
        map.put("VAR", getDocLocation() + "wdghtml40/phrase/var.html");
        map.put("BACKGROUND", getDocLocation() + "wdghtml40/css.html#background");
        map.put("BACKGROUND-ATTACHMENT", getDocLocation() + "wdghtml40/css.html#background-attachment");
        map.put("BACKGROUND-COLOR", getDocLocation() + "wdghtml40/css.html#background-color");
        map.put("BACKGROUND-IMAGE", getDocLocation() + "wdghtml40/css.html#background-image");
        map.put("BACKGROUND-POSITION", getDocLocation() + "wdghtml40/css.html#background-position");
        map.put("BACKGROUND-REPEAT", getDocLocation() + "wdghtml40/css.html#background-repeat");
        map.put("BORDER", getDocLocation() + "wdghtml40/css.html#border");
        map.put("BORDER-BOTTOM", getDocLocation() + "wdghtml40/css.html#border-bottom");
        map.put("BORDER-BOTTOM-WIDTH", getDocLocation() + "wdghtml40/css.html#border-bottom-width");
        map.put("BORDER-COLOR", getDocLocation() + "wdghtml40/css.html#border-color");
        map.put("BORDER-LEFT", getDocLocation() + "wdghtml40/css.html#border-left");
        map.put("BORDER-LEFT-WIDTH", getDocLocation() + "wdghtml40/css.html#border-left-width");
        map.put("BORDER-RIGHT", getDocLocation() + "wdghtml40/css.html#border-right");
        map.put("BORDER-RIGHT-WIDTH", getDocLocation() + "wdghtml40/css.html#border-right-width");
        map.put("BORDER-STYLE", getDocLocation() + "wdghtml40/css.html#border-style");
        map.put("BORDER-TOP", getDocLocation() + "wdghtml40/css.html#border-top");
        map.put("BORDER-TOP-WIDTH", getDocLocation() + "wdghtml40/css.html#border-top-width");
        map.put("BORDER-WIDTH", getDocLocation() + "wdghtml40/css.html#border-width");
        map.put("BOX", getDocLocation() + "wdghtml40/css.html#box");
        map.put("CLASSIFICATION", getDocLocation() + "wdghtml40/css.html#classification");
        map.put("CLEAR", getDocLocation() + "wdghtml40/css.html#clear");
        map.put("COLOR", getDocLocation() + "wdghtml40/css.html#color");
        map.put("COLOR-BACKGROUND", getDocLocation() + "wdghtml40/css.html#color-background");
        map.put("COLOR-UNITS", getDocLocation() + "wdghtml40/css.html#color-units");
        map.put("DISPLAY", getDocLocation() + "wdghtml40/css.html#display");
        map.put("FLOAT", getDocLocation() + "wdghtml40/css.html#float");
        map.put("FONT", getDocLocation() + "wdghtml40/css.html#font");
        map.put("FONT-FAMILY", getDocLocation() + "wdghtml40/css.html#font-family");
        map.put("FONT-PROPERTIES", getDocLocation() + "wdghtml40/css.html#font-properties");
        map.put("FONT-SIZE", getDocLocation() + "wdghtml40/css.html#font-size");
        map.put("FONT-STYLE", getDocLocation() + "wdghtml40/css.html#font-style");
        map.put("FONT-VARIANT", getDocLocation() + "wdghtml40/css.html#font-variant");
        map.put("FONT-WEIGHT", getDocLocation() + "wdghtml40/css.html#font-weight");
        map.put("HEIGHT", getDocLocation() + "wdghtml40/css.html#height");
        map.put("LENGTH", getDocLocation() + "wdghtml40/css.html#length");
        map.put("LETTER-SPACING", getDocLocation() + "wdghtml40/css.html#letter-spacing");
        map.put("LINE-HEIGHT", getDocLocation() + "wdghtml40/css.html#line-height");
        map.put("LIST-STYLE", getDocLocation() + "wdghtml40/css.html#list-style");
        map.put("LIST-STYLE-IMAGE", getDocLocation() + "wdghtml40/css.html#list-style-image");
        map.put("LIST-STYLE-POSITION", getDocLocation() + "wdghtml40/css.html#list-style-position");
        map.put("LIST-STYLE-TYPE", getDocLocation() + "wdghtml40/css.html#list-style-type");
        map.put("MARGIN", getDocLocation() + "wdghtml40/css.html#margin");
        map.put("MARGIN-BOTTOM", getDocLocation() + "wdghtml40/css.html#margin-bottom");
        map.put("MARGIN-LEFT", getDocLocation() + "wdghtml40/css.html#margin-left");
        map.put("MARGIN-RIGHT", getDocLocation() + "wdghtml40/css.html#margin-right");
        map.put("MARGIN-TOP", getDocLocation() + "wdghtml40/css.html#margin-top");
        map.put("PADDING", getDocLocation() + "wdghtml40/css.html#padding");
        map.put("PADDING-BOTTOM", getDocLocation() + "wdghtml40/css.html#padding-bottom");
        map.put("PADDING-LEFT", getDocLocation() + "wdghtml40/css.html#padding-left");
        map.put("PADDING-RIGHT", getDocLocation() + "wdghtml40/css.html#padding-right");
        map.put("PADDING-TOP", getDocLocation() + "wdghtml40/css.html#padding-top");
        map.put("PERCENTAGE", getDocLocation() + "wdghtml40/css.html#percentage");
        map.put("TEXT", getDocLocation() + "wdghtml40/css.html#text");
        map.put("TEXT-ALIGN", getDocLocation() + "wdghtml40/css.html#text-align");
        map.put("TEXT-DECORATION", getDocLocation() + "wdghtml40/css.html#text-decoration");
        map.put("TEXT-INDENT", getDocLocation() + "wdghtml40/css.html#text-indent");
        map.put("TEXT-TRANSFORM", getDocLocation() + "wdghtml40/css.html#text-transform");
        map.put("UNITS", getDocLocation() + "wdghtml40/css.html#units");
        map.put("URLS", getDocLocation() + "wdghtml40/css.html#urls");
        map.put("VERTICAL-ALIGN", getDocLocation() + "wdghtml40/css.html#vertical-align");
        map.put("WHITE-SPACE", getDocLocation() + "wdghtml40/css.html#white-space");
        map.put("WIDTH", getDocLocation() + "wdghtml40/css.html#width");
        map.put("WORD-SPACING", getDocLocation() + "wdghtml40/css.html#word-spacing");
    }
    private VelocityColorProvider       cp;
    // private AnnotationModel fAnnotationModel;
    private ModelTools                  fModelTools;
    private VelocityReconcilingStrategy fReconcilingStrategy;
    private VelocityOutlinePage         fOutlinePage;
    /**
     * Last cursor position (line) handled in
     * <code>handleCursorPositionChanged()</code>
     */
    private int                         fLastCursorLine;
    VelocityConfiguration               vc                 = null;
    public MouseClickListener           fMouseListener;
    private ProjectionSupport           projectionSupport;
    private Set                         set;
      
    public VelocityEditor()
    {
        fModelTools = new ModelTools(this);
        fReconcilingStrategy = new VelocityReconcilingStrategy(this);
        cp = VelocityEditorEnvironment.getColorProvider();
        VelocityPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
    }
    
    private void openBrowser(String url)
    {
      try
      {
        IWebBrowser browser = WorkbenchBrowserSupport.getInstance()
          .createBrowser(0, "MyBrowserID", "MyBrowserName", "MyBrowser Tooltip");
        browser.openURL(new URL(url));     
       }
      catch (Exception e)
      {
        System.out.println(e);
      }      
    }

    private static String getDocLocation()
    {
      
        String loc = "";
        URL url = VelocityPlugin.getDefault().getBundle().getEntry("/");
        try
        {
            loc = FileLocator.resolve(url).toString();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return loc;
    }

    /**
     * Currently we only validate against the xhtml strict dtd, this is used by
     * completion to test what elements are valid in the given context.
     */
    public static DTDElement getHTMLElement(String name)
    {
      
      if (dtd == null)
      {
        String path = VelocityPlugin.getInstallPath() + "xhtml1-strict.dtd";
        URL url = null;
        try
        {
          url = new URL(path);
        } catch (MalformedURLException e2)
        {
          e2.printStackTrace();
          return null;
        }
        try
        {
          DTDParser parser = new DTDParser(url, false);
          dtd = parser.parse(false);
        } 
        catch (IOException e1)
        {
          e1.printStackTrace();
          return null;
        }
      }
      
      return (DTDElement) dtd.elements.get(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.editors.text.TextEditor#initializeEditor()
     */
    protected void initializeEditor()
    {
        super.initializeEditor();
        VelocityEditorEnvironment.connect();
        setDocumentProvider(new VelocityDocumentProvider());
        VelocityConfiguration vc = new VelocityConfiguration(this);
        setSourceViewerConfiguration(vc);
    }

    // /*
    // * (non-Javadoc)
    // *
    // * @see
    // org.eclipse.ui.editors.text.TextEditor#initializeKeyBindingScopes()
    // */
    // protected void initializeKeyBindingScopes()
    // {
    // setKeyBindingScopes(new String[] {
    // "com.googlecode.veloeclipse.vaulttec.ui.velocityEditorScope" });
    // }
    private void enableBrowserLikeLinks()
    {
        if (fMouseListener == null)
        {
            fMouseListener = new MouseClickListener();
            fMouseListener.install();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#createActions()
     */
    protected void createActions()
    {
        super.createActions();
        // Add goto definition action
        IAction action = new GotoDefinitionAction(VelocityPlugin.getDefault().getResourceBundle(), PREFIX + "GotoDefinition.", this);
        action.setActionDefinitionId(IVelocityActionDefinitionIds.GOTO_DEFINITION);
        setAction(IVelocityActionConstants.GOTO_DEFINITION, action);
        action = new TextOperationAction(VelocityPlugin.getDefault().getResourceBundle(), "ContentAssistProposal.", this, ISourceViewer.CONTENTASSIST_PROPOSALS); //$NON-NLS-1$
        action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
        // setAction("ContentAssistProposal", action); //$NON-NLS-1$
        setAction(IVelocityActionConstants.CONTENT_ASSIST, action);
        // jtidy
        action = new JTidyAction(VelocityPlugin.getDefault().getResourceBundle(), PREFIX + "JTidy.", this);
        action.setActionDefinitionId(IVelocityActionDefinitionIds.JTIDY);
        setAction(IVelocityActionConstants.JTIDY, action);
        // TOGGLE_COMMENT
        action = new ToggleCommentAction(VelocityPlugin.getDefault().getResourceBundle(), PREFIX + "ToggleComment.", this);
        action.setActionDefinitionId(IVelocityActionDefinitionIds.TOGGLE_COMMENT);
        setAction(IVelocityActionConstants.TOGGLE_COMMENT, action);
        // FormatEditor
        action = new FormatAction(VelocityPlugin.getDefault().getResourceBundle(), PREFIX + "FormatEditor.", this);
        action.setActionDefinitionId(IVelocityActionDefinitionIds.FORMAT);
        setAction(IVelocityActionConstants.FORMAT, action);
        // template proposal
        action = new TextOperationAction(TemplateMessages.getResourceBundle(), "Editor." + TEMPLATE_PROPOSALS + ".", //$NON-NLS-1$ //$NON-NLS-2$
                this, ISourceViewer.CONTENTASSIST_PROPOSALS);
        action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
        setAction(TEMPLATE_PROPOSALS, action);
        markAsStateDependentAction(TEMPLATE_PROPOSALS, true);
        enableBrowserLikeLinks();
        configureToggleCommentAction();
    }

    /**
     * Get the outline page if requested.
     * 
     * @see org.eclipse.core.runtime.IAdaptable.getAdapter(Class)
     */
    public Object getAdapter(Class aClass)
    {
        // Object adapter;
        // if (aClass.equals(IContentOutlinePage.class))
        // {
        // if ((fOutlinePage == null) || fOutlinePage.isDisposed())
        // {
        // fOutlinePage = new VelocityOutlinePage(this);
        // if (getEditorInput() != null)
        // {
        // fOutlinePage.setInput(getEditorInput());
        // }
        // }
        // adapter = fOutlinePage;
        // } else
        // {
        // adapter = super.getAdapter(aClass);
        // }
        // return adapter;
        // folding
        if (IContentOutlinePage.class.equals(aClass))
        {
            if (fOutlinePage == null || fOutlinePage.isDisposed())
            {
                fOutlinePage = new VelocityOutlinePage(this);
                if (getEditorInput() != null)
                {
                    fOutlinePage.setInput(getEditorInput());
                }
            }
            return fOutlinePage;
        }
        if (projectionSupport != null)
        {
            Object adapter = projectionSupport.getAdapter(getSourceViewer(), aClass);
            if (adapter != null) return adapter;
        }
        return super.getAdapter(aClass);
    }

    /**
     * Disconnect from editor environment and dispose outline page.
     * 
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    public void dispose()
    {
        if ((fOutlinePage != null) && !fOutlinePage.isDisposed())
        {
            fOutlinePage.dispose();
            fOutlinePage = null;
        }
        VelocityEditorEnvironment.disconnect();
        super.dispose();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#editorContextMenuAboutToShow(org.eclipse.jface.action.IMenuManager)
     */
    protected void editorContextMenuAboutToShow(IMenuManager aMenu)
    {
        super.editorContextMenuAboutToShow(aMenu);
        addAction(aMenu, IWorkbenchActionConstants.MB_ADDITIONS, IVelocityActionConstants.GOTO_DEFINITION);
        addAction(aMenu, IWorkbenchActionConstants.MB_ADDITIONS, IVelocityActionConstants.CONTENT_ASSIST);
        addAction(aMenu, IWorkbenchActionConstants.MB_ADDITIONS, IVelocityActionConstants.TOGGLE_COMMENT);
        addAction(aMenu, IWorkbenchActionConstants.MB_ADDITIONS, IVelocityActionConstants.FORMAT);
        addAction(aMenu, IWorkbenchActionConstants.MB_ADDITIONS, IVelocityActionConstants.JTIDY);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#handleCursorPositionChanged()
     */
    protected void handleCursorPositionChanged()
    {
        super.handleCursorPositionChanged();
        int line = getCursorLine();
        if ((line > 0) && (line != fLastCursorLine))
        {
            fLastCursorLine = line;
            if ((fOutlinePage != null) && !fOutlinePage.isDisposed())
            {
                fOutlinePage.selectNode(line, false);
            }
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public IDocument getDocument()
    {
        ISourceViewer viewer = getSourceViewer();
        if (viewer != null) { return viewer.getDocument(); }
        return null;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param anOffset
     *            DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    public int getLine(int anOffset)
    {
        int line;
        try
        {
            line = getDocument().getLineOfOffset(anOffset) + 1;
        }
        catch (BadLocationException e)
        {
            line = -1;
        }
        return line;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public int getCursorLine()
    {
        int line = -1;
        ISourceViewer sourceViewer = getSourceViewer();
        if (sourceViewer != null)
        {
            StyledText styledText = sourceViewer.getTextWidget();
            int caret = widgetOffset2ModelOffset(sourceViewer, styledText.getCaretOffset());
            IDocument document = sourceViewer.getDocument();
            if (document != null)
            {
                try
                {
                    line = document.getLineOfOffset(caret) + 1;
                }
                catch (BadLocationException e)
                {
                    VelocityPlugin.log(e);
                }
            }
        }
        return line;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param aNode
     *            DOCUMENT ME!
     * @param aMoveCursor
     *            DOCUMENT ME!
     */
    public void highlightNode(ITreeNode aNode, boolean aMoveCursor)
    {
        resetHighlightRange();
        IDocument doc = getDocument();
        try
        {
            int offset = doc.getLineOffset(aNode.getStartLine() - 1);
            IRegion endLine = doc.getLineInformation(aNode.getEndLine() - 1);
            int length = (endLine.getOffset() + endLine.getLength()) - offset;
            setHighlightRange(offset, length + 1, aMoveCursor);
        }
        catch (BadLocationException e)
        {
            resetHighlightRange();
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param offset
     *            DOCUMENT ME!
     * @param aMoveCursor
     *            DOCUMENT ME!
     */
    public void highlightOpenTag(int offset, boolean aMoveCursor)
    {
        resetHighlightRange();
        IDocument doc = getDocument();
        int linenr = -1;
        try
        {
            markInNavigationHistory();
            linenr = doc.getLineOfOffset(offset);
            IRegion endLine = doc.getLineInformation(linenr);
            int length = (endLine.getOffset() + endLine.getLength()) - offset;
            setHighlightRange(offset, length + 1, aMoveCursor);
            markInNavigationHistory();
        }
        catch (BadLocationException e)
        {
            resetHighlightRange();
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param aNode
     *            DOCUMENT ME!
     */
    public void revealNode(ITreeNode aNode)
    {
        ISourceViewer viewer = getSourceViewer();
        if (viewer != null)
        {
            IDocument doc = getDocument();
            try
            {
                int offset = doc.getLineOffset(aNode.getStartLine() - 1);
                IRegion endLine = doc.getLineInformation(aNode.getEndLine() - 1);
                int length = (endLine.getOffset() + endLine.getLength()) - offset;
                // Reveal segment's text area in document
                StyledText widget = getSourceViewer().getTextWidget();
                widget.setRedraw(false);
                viewer.revealRange(offset, length);
                widget.setRedraw(true);
            }
            catch (BadLocationException e)
            {
                resetHighlightRange();
            }
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param aLine
     *            DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    public ITreeNode getNodeByLine(int aLine)
    {
        return fModelTools.getNodeByLine(aLine);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param aRegion
     *            DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    public String getDefinitionLine(IRegion aRegion)
    {
        int tagOffset = -1;
        String guessed = null;
        if (aRegion != null)
        {
            XMLElementGuesser xmlguess = new XMLElementGuesser(getDocument(), aRegion.getOffset(), true);
            guessed = xmlguess.getText();
            int xmltype = xmlguess.getType();
            tagOffset = xmlguess.getTagOffset();
            if ((guessed != null) && map.containsKey(guessed.toUpperCase()))
            {
                switch (xmltype)
                {
                    case VelocityTextGuesser.TAG_DIRECTIVE:
                        guessed = "Ctrl+Alt+Mouseclick to show definition of " + guessed;
                        break;
                    case VelocityTextGuesser.TAG_CLOSE:
                        int i = VelocityAutoIndentStrategy.findMatchingOpenTagBefore(tagOffset, getDocument());
                        if (i > -1)
                        {
                            int lnr = -1;
                            try
                            {
                                lnr = getDocument().getLineOfOffset(i);
                            }
                            catch (BadLocationException e)
                            {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            guessed = "Ctrl+Alt+Mouseclick to go to matching tag of " + guessed + ((lnr > -1) ? ("  -> " + (lnr + 1) + "") : "");
                        } else
                        {
                            guessed = "this tag is incorrectly closed";
                        }
                        break;
                    default:
                        guessed = null;
                        break;
                }
            } else
            {
                guessed = null;
                VelocityTextGuesser guess = new VelocityTextGuesser(getDocument(), aRegion.getOffset(), true);
                // Check if guessed text references an externally defined macro
                if (guess.getType() == VelocityTextGuesser.TYPE_DIRECTIVE)
                {
                    VelocimacroProxy vp = VelocityEditorEnvironment.getParser().getLibraryMacro(guess.getText());
                    if (vp != null)
                    {
                        String template = ((IFileEditorInput) getEditorInput()).getFile().getName();
                        if (!vp.getTemplateName().equals(template))
                        {
                            StringBuffer buf = new StringBuffer();
                            buf.append("#macro (");
                            buf.append(vp.getName());
                            buf.append(") - ");
                            buf.append(vp.getTemplateName());
                            return buf.toString();
                        }
                    }
                } 
                else if (guess.getType() == VelocityTextGuesser.TYPE_END)
                {
                    int i = VelocityAutoIndentStrategy.findStartVeloBefore(guess.getTagOffset(), getDocument());
                    int lnr = -1;
                    if (i > -1)
                    {
                        try
                        {
                            lnr = getDocument().getLineOfOffset(i);
                        }
                        catch (BadLocationException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    guessed = "Ctrl+Alt+Mouseclick to go to directive start " + ((lnr > -1) ? ("  -> " + (lnr + 1) + "") : "");
                }
                // Look through model tree for guessed text
                ITreeNode node = fModelTools.getNodeByGuess(guess);
                if (node != null)
                {
                    IDocument doc = getDocument();
                    try
                    {
                        aRegion = doc.getLineInformation(node.getStartLine() - 1);
                        StringBuffer buf = new StringBuffer();
                        buf.append(node.getStartLine());
                        buf.append(": ");
                        buf.append(doc.get(aRegion.getOffset(), aRegion.getLength()).trim());
                        return buf.toString();
                    }
                    catch (BadLocationException e)
                    {
                    }
                }
            }
        }
        return guessed;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param aRegion
     *            DOCUMENT ME!
     */
    public void gotoDefinition(IRegion aRegion)
    {
        int tagOffset = -1;
        if (aRegion != null)
        {
            XMLElementGuesser xmlguess = new XMLElementGuesser(getDocument(), aRegion.getOffset(), true);
            String guessed = xmlguess.getText();
            int xmltype = xmlguess.getType();
            tagOffset = xmlguess.getTagOffset();
            if ((guessed != null) && map.containsKey(guessed.toUpperCase()))
            {
                switch (xmltype)
                {
                    case VelocityTextGuesser.TAG_DIRECTIVE:
                      guessed = (String) map.get(guessed.toUpperCase());
                      openBrowser(guessed);
                      return;
                    case VelocityTextGuesser.TAG_CLOSE:
                        int i = VelocityAutoIndentStrategy.findMatchingOpenTagBefore(tagOffset, getDocument());
                        if (i > -1)
                        {
                            highlightOpenTag(i, true);
                        }
                        break;
                    default:
                      guessed = (String) map.get(guessed.toUpperCase());
                      openBrowser(guessed);
                      return;
                }
            } 
            else
            {
                VelocityTextGuesser guess = new VelocityTextGuesser(getDocument(), aRegion.getOffset(), true);
                // Check if guessed text references an externally defined macro
                if ((guess.getType() == VelocityTextGuesser.TYPE_DIRECTIVE) || (guess.getType() == VelocityTextGuesser.TYPE_VARIABLE))
                {

                   
                    VelocimacroProxy vp = VelocityEditorEnvironment.getParser().getLibraryMacro(guess.getText());
                    if (vp != null)
                    {
                        String template = ((IFileEditorInput) getEditorInput()).getFile().getName();
                        if (!vp.getTemplateName().equals(template)) 
                        {
                         
                          try
                          {
                            // Goto the macro definition
                            IPreferenceStore store = VelocityPlugin.getDefault().getPreferenceStore();
                            String filestr = store.getString(IPreferencesConstants.LIBRARY_PATH) + "/" + vp.getTemplateName();
                            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(filestr)); 
                            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();                          
                            ITextEditor teditor = (ITextEditor)IDE.openEditor(page, file);
                            IDocumentProvider provider = teditor.getDocumentProvider();
                            IDocument document = provider.getDocument(teditor.getEditorInput());
                            int start = document.getLineOffset(vp.getLine()-2); 
                            teditor.selectAndReveal(start + vp.getColumn()-1, 0);                
                          }
                          catch(Exception e)
                          {
                            e.printStackTrace();
                          }
                          
                          return; 
                       }
                    }
                    // Look through model tree for guessed text
                    ITreeNode node = fModelTools.getNodeByGuess(guess);
                    if (node != null)
                    {
                        markInNavigationHistory();
                        highlightNode(node, true);
                        markInNavigationHistory();
                    }
                } else if (guess.getType() == VelocityTextGuesser.TYPE_END)
                {
                    int i = VelocityAutoIndentStrategy.findStartVeloBefore(guess.getTagOffset(), getDocument());
                    if (i > -1)
                    {
                        highlightOpenTag(i, true);
                    }
                }
            }
            getSourceViewer().invalidateTextPresentation();
        }
    }

    /**
     * Returns true if specified line belongs to a <code>#foreach</code>
     * block.
     */
    public boolean isLineWithinLoop(int aLine)
    {
        return fModelTools.isLineWithinLoop(aLine);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param aLine
     *            DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    public List getVariables(int aLine)
    {
        return fModelTools.getVariables(aLine);
    }

    
    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public VelocityReconcilingStrategy getReconcilingStrategy()
    {
        return fReconcilingStrategy;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public Object[] getRootElements()
    {
        return fReconcilingStrategy.getRootElements();
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public ITreeNode getRootNode()
    {
        return fReconcilingStrategy.getRootNode();
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public ITreeNode getLastRootNode()
    {
        return fReconcilingStrategy.getLastRootNode();
    }

    /**
     * DOCUMENT ME!
     */
    public void updateOutlinePage()
    {
        if (fOutlinePage != null)
        {
            fOutlinePage.update();
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param aLine
     *            DOCUMENT ME!
     */
    public void moveCursor(int aLine)
    {
        ISourceViewer sourceViewer = getSourceViewer();
        try
        {
            int offset = getDocument().getLineOffset(aLine - 1);
            sourceViewer.setSelectedRange(offset, 0);
            sourceViewer.revealRange(offset, 0);
        }
        catch (BadLocationException e)
        {
        }
    }
    
    
    /**
     * Determines if the specified character may be part of a Velocity
     * reference. A character may be part of a Velocity directive if and only if
     * it is one of the following:
     * <ul>
     * <li>a letter (a..z, A..Z)
     * <li>a digit (0..9)
     * <li>a hyphen ("-")
     * <li>a connecting punctuation character ("_")
     * </ul>
     * 
     * @param aChar
     *            the character to be tested.
     * @return true if the character may be part of a Velocity reference; false
     *         otherwise.
     * @see java.lang.Character#isLetterOrDigit(char)
     */
    public static boolean isReferencePart(char aChar)
    {
        return Character.isLetterOrDigit(aChar) || (aChar == '-') || (aChar == '_');
    }
        
    /**
     * Displays an error message in editor's status line.
     */
    public void displayErrorMessage(String aMessage)
    {
        IStatusLineManager manager = getStatusLineManager();
        if (manager != null)
        {
            manager.setErrorMessage(aMessage);
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param aMessage
     *            DOCUMENT ME!
     * @param aLine
     *            DOCUMENT ME!
     */
    public void addProblemMarker(String aMessage, int aLine, int severity)
    {
        IFile file = ((IFileEditorInput) getEditorInput()).getFile();
        try
        {
            IMarker marker = file.createMarker(IMarker.PROBLEM);
            marker.setAttribute(IMarker.SEVERITY, severity);
            marker.setAttribute(IMarker.MESSAGE, aMessage);
            marker.setAttribute(IMarker.LINE_NUMBER, aLine);
            Position pos = new Position(getDocument().getLineOffset(aLine - 1));
            getSourceViewer().getAnnotationModel().addAnnotation(new MarkerAnnotation(marker), pos);
        }
        catch (Exception e)
        {
            VelocityPlugin.log(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.editors.text.TextEditor#handlePreferenceStoreChanged(org.eclipse.jface.util.PropertyChangeEvent)
     */
    protected void handlePreferenceStoreChanged(PropertyChangeEvent event)
    {
        cp.handlePreferenceStoreChanged(event);
        super.handlePreferenceStoreChanged(event);
    }

    // /*
    // * @see AbstractTextEditor#doSetInput(IEditorInput)
    // */
    // protected void doSetInput(IEditorInput input) throws CoreException
    // {
    // super.doSetInput(input);
    // // configureToggleCommentAction();
    // }
    private void configureToggleCommentAction()
    {
        IAction action = getAction(IVelocityActionConstants.TOGGLE_COMMENT); //$NON-NLS-1$
        if (action instanceof ToggleCommentAction)
        {
            ISourceViewer sourceViewer = getSourceViewer();
            SourceViewerConfiguration configuration = getSourceViewerConfiguration();
            ((ToggleCommentAction) action).configure(sourceViewer, configuration);
        }
    }

    /**
     * @return
     */
    public VelocityConfiguration getVelocityConfiguration()
    {
        return vc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent event)
    {
        cp.handlePreferenceStoreChanged(event);
        ISourceViewer viewer = getSourceViewer();
        if (viewer != null)
        {
            viewer.invalidateTextPresentation();
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param token
     *            DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    public static int findLocalizedModifier(String token)
    {
        if (token == null) { return 0; }
        if (token.equalsIgnoreCase(Action.findModifierString(SWT.CTRL))) { return SWT.CTRL; }
        if (token.equalsIgnoreCase(Action.findModifierString(SWT.SHIFT))) { return SWT.SHIFT; }
        if (token.equalsIgnoreCase(Action.findModifierString(SWT.ALT))) { return SWT.ALT; }
        if (token.equalsIgnoreCase(Action.findModifierString(SWT.COMMAND))) { return SWT.COMMAND; }
        return 0;
    }

    public class MouseClickListener implements KeyListener, MouseListener, MouseMoveListener, FocusListener, PaintListener, IPropertyChangeListener, IDocumentListener, ITextInputListener
    {

        /** The session is active. */
        public boolean  fActive;
        /** The currently active style range. */
        public IRegion  fActiveRegion;
        /** The currently active style range as position. */
        private Position fRememberedPosition;
        /** The hand cursor. */
        private Cursor   fCursor;
        /** The link color. */
        private Color    fColor;
        /** The key modifier mask. */
        private int      fKeyModifierMask;

        public void deactivate()
        {
            deactivate(false);
        }

        public void deactivate(boolean redrawAll)
        {
            if (!fActive) { return; }
            repairRepresentation(redrawAll);
            fActive = false;
        }

        public void install()
        {
            ISourceViewer sourceViewer = getSourceViewer();
            if (sourceViewer == null) { return; }
            StyledText text = sourceViewer.getTextWidget();
            if ((text == null) || text.isDisposed()) { return; }
            updateColor(sourceViewer);
            sourceViewer.addTextInputListener(this);
            IDocument document = sourceViewer.getDocument();
            if (document != null)
            {
                document.addDocumentListener(this);
            }
            text.addKeyListener(this);
            text.addMouseListener(this);
            text.addMouseMoveListener(this);
            text.addFocusListener(this);
            text.addPaintListener(this);
            updateKeyModifierMask();
            IPreferenceStore preferenceStore = getPreferenceStore();
            preferenceStore.addPropertyChangeListener(this);
        }

        private void updateKeyModifierMask()
        {
            fKeyModifierMask = computeStateMask("Ctrl+Alt");
            if (fKeyModifierMask == -1)
            {
            }
        }

        private int computeStateMask(String modifiers)
        {
            if (modifiers == null) { return -1; }
            if (modifiers.length() == 0) { return SWT.NONE; }
            int stateMask = 0;
            StringTokenizer modifierTokenizer = new StringTokenizer(modifiers, ",;.:+-* "); //$NON-NLS-1$
            while (modifierTokenizer.hasMoreTokens())
            {
                int modifier = findLocalizedModifier(modifierTokenizer.nextToken());
                if ((modifier == 0) || ((stateMask & modifier) == modifier)) { return -1; }
                stateMask = stateMask | modifier;
            }
            return stateMask;
        }

        public void uninstall()
        {
            if (fColor != null)
            {
                fColor.dispose();
                fColor = null;
            }
            if (fCursor != null)
            {
                fCursor.dispose();
                fCursor = null;
            }
            ISourceViewer sourceViewer = getSourceViewer();
            if (sourceViewer == null) { return; }
            sourceViewer.removeTextInputListener(this);
            IDocument document = sourceViewer.getDocument();
            if (document != null)
            {
                document.removeDocumentListener(this);
            }
            IPreferenceStore preferenceStore = getPreferenceStore();
            if (preferenceStore != null)
            {
                preferenceStore.removePropertyChangeListener(this);
            }
            StyledText text = sourceViewer.getTextWidget();
            if ((text == null) || text.isDisposed()) { return; }
            text.removeKeyListener(this);
            text.removeMouseListener(this);
            text.removeMouseMoveListener(this);
            text.removeFocusListener(this);
            text.removePaintListener(this);
        }

        private void updateColor(ISourceViewer viewer)
        {
            if (fColor != null)
            {
                fColor.dispose();
            }
            StyledText text = viewer.getTextWidget();
            if ((text == null) || text.isDisposed()) { return; }
            fColor = new Color(Display.getCurrent(), 0, 0, 255);
        }

        private void repairRepresentation()
        {
            repairRepresentation(false);
        }

        private void repairRepresentation(boolean redrawAll)
        {
            if (fActiveRegion == null) { return; }
            ISourceViewer viewer = getSourceViewer();
            if (viewer != null)
            {
                resetCursor(viewer);
                int offset = fActiveRegion.getOffset();
                int length = fActiveRegion.getLength();
                // remove style
                if (!redrawAll && viewer instanceof ITextViewerExtension2)
                {
                    ((ITextViewerExtension2) viewer).invalidateTextPresentation(offset, length);
                } else
                {
                    viewer.invalidateTextPresentation();
                }
                // remove underline
                if (viewer instanceof ITextViewerExtension5)
                {
                    ITextViewerExtension5 extension = (ITextViewerExtension5) viewer;
                    offset = extension.modelOffset2WidgetOffset(offset);
                } else
                {
                    offset -= viewer.getVisibleRegion().getOffset();
                }
                StyledText text = viewer.getTextWidget();
                try
                {
                    text.redrawRange(offset, length, true);
                }
                catch (IllegalArgumentException x)
                {
                }
            }
            fActiveRegion = null;
        }

        private IRegion selectWord(IDocument document, int anchor)
        {
            try
            {
                int offset = anchor;
                char c;
                while (offset >= 0)
                {
                    c = document.getChar(offset);
                    if (!Character.isJavaIdentifierPart(c)) // || (c == '$'))
                    {
                        break;
                    }
                    --offset;
                }
                int start = offset;
                offset = anchor;
                int length = document.getLength();
                while (offset < length)
                {
                    c = document.getChar(offset);
                    if (!Character.isJavaIdentifierPart(c)) // || (c == '$'))
                    {
                        break;
                    }
                    ++offset;
                }
                int end = offset;
                if (start == end)
                {
                    return new Region(start, 0);
                } else
                {
                    return new Region(start + 1, end - start - 1);
                }
            }
            catch (BadLocationException x)
            {
                return null;
            }
        }

        IRegion getCurrentTextRegion(ISourceViewer viewer)
        {
            int offset = getCurrentTextOffset(viewer);
            if (offset == -1) { return null; }
            try
            {
                return selectWord(viewer.getDocument(), offset);
            }
            catch (Exception e)
            {
                return null;
            }
        }

        private int getCurrentTextOffset(ISourceViewer viewer)
        {
            try
            {
                StyledText text = viewer.getTextWidget();
                if ((text == null) || text.isDisposed()) { return -1; }
                Display display = text.getDisplay();
                Point absolutePosition = display.getCursorLocation();
                Point relativePosition = text.toControl(absolutePosition);
                int widgetOffset = text.getOffsetAtLocation(relativePosition);
                if (viewer instanceof ITextViewerExtension5)
                {
                    ITextViewerExtension5 extension = (ITextViewerExtension5) viewer;
                    return extension.widgetOffset2ModelOffset(widgetOffset);
                } else
                {
                    return widgetOffset + viewer.getVisibleRegion().getOffset();
                }
            }
            catch (IllegalArgumentException e)
            {
                return -1;
            }
        }

        private void highlightRegion(ISourceViewer viewer, IRegion region)
        {
            if (region.equals(fActiveRegion)) { return; }
            repairRepresentation();
            StyledText text = viewer.getTextWidget();
            if ((text == null) || text.isDisposed()) { return; }
            // highlight region
            int offset = 0;
            int length = 0;
            if (viewer instanceof ITextViewerExtension5)
            {
                ITextViewerExtension5 extension = (ITextViewerExtension5) viewer;
                IRegion widgetRange = extension.modelRange2WidgetRange(region);
                if (widgetRange == null) { return; }
                offset = widgetRange.getOffset();
                length = widgetRange.getLength();
            } else
            {
                offset = region.getOffset() - viewer.getVisibleRegion().getOffset();
                length = region.getLength();
            }
            StyleRange oldStyleRange = text.getStyleRangeAtOffset(offset);
            Color foregroundColor = fColor;
            Color backgroundColor = (oldStyleRange == null) ? text.getBackground() : oldStyleRange.background;
            StyleRange styleRange = new StyleRange(offset, length, foregroundColor, backgroundColor);
            text.setStyleRange(styleRange);
            // underline
            text.redrawRange(offset, length, true);
            fActiveRegion = region;
        }

        private void activateCursor(ISourceViewer viewer)
        {
            StyledText text = viewer.getTextWidget();
            if ((text == null) || text.isDisposed()) { return; }
            Display display = text.getDisplay();
            if (fCursor == null)
            {
                fCursor = new Cursor(display, SWT.CURSOR_HAND);
            }
            text.setCursor(fCursor);
        }

        private void resetCursor(ISourceViewer viewer)
        {
            StyledText text = viewer.getTextWidget();
            if ((text != null) && !text.isDisposed())
            {
                text.setCursor(null);
            }
            if (fCursor != null)
            {
                fCursor.dispose();
                fCursor = null;
            }
        }

        /*
         * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
         */
        public void keyPressed(KeyEvent event)
        {
            if (fActive)
            {
                deactivate();
                return;
            }
            if (event.keyCode != fKeyModifierMask)
            {
                deactivate();
                return;
            }
            fActive = true;
            // removed for #25871
            //
            // ISourceViewer viewer= getSourceViewer();
            // if (viewer == null)
            // return;
            //			
            // IRegion region= getCurrentTextRegion(viewer);
            // if (region == null)
            // return;
            //			
            // highlightRegion(viewer, region);
            // activateCursor(viewer);
        }

        /*
         * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
         */
        public void keyReleased(KeyEvent event)
        {
            if (!fActive) { return; }
            deactivate();
        }

        /*
         * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
         */
        public void mouseDoubleClick(MouseEvent e)
        {
        }

        /*
         * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
         */
        public void mouseDown(MouseEvent event)
        {
            if (!fActive) { return; }
            if (event.stateMask != fKeyModifierMask)
            {
                deactivate();
                return;
            }
            if (event.button != 1)
            {
                deactivate();
                return;
            }
        }

        /*
         * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
         */
        public void mouseUp(MouseEvent e)
        {
            if (!fActive) { return; }
            if (e.button != 1)
            {
                deactivate();
                return;
            }
            boolean wasActive = fCursor != null;
            if (wasActive)
            {
                IAction action = getAction("GotoDefinition"); //$NON-NLS-1$
                if (action != null)
                {
                    action.run();
                    // repairRepresentation(true);
                    // getSourceViewer().invalidateTextPresentation();
                }
            }
            deactivate();
        }

        /*
         * @see org.eclipse.swt.events.MouseMoveListener#mouseMove(org.eclipse.swt.events.MouseEvent)
         */
        public void mouseMove(MouseEvent event)
        {
            if (event.widget instanceof Control && !((Control) event.widget).isFocusControl())
            {
                deactivate();
                return;
            }
            if (!fActive)
            {
                if (event.stateMask != fKeyModifierMask) { return; }
                // modifier was already pressed
                fActive = true;
            }
            ISourceViewer viewer = getSourceViewer();
            if (viewer == null)
            {
                deactivate();
                return;
            }
            StyledText text = viewer.getTextWidget();
            if ((text == null) || text.isDisposed())
            {
                deactivate();
                return;
            }
            if (((event.stateMask & SWT.BUTTON1) != 0) && (text.getSelectionCount() != 0))
            {
                deactivate();
                return;
            }
            IRegion region = getCurrentTextRegion(viewer);
            if ((region == null) || (region.getLength() == 0))
            {
                repairRepresentation();
                getSourceViewer().invalidateTextPresentation();
                return;
            }
            highlightRegion(viewer, region);
            activateCursor(viewer);
        }

        /*
         * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
         */
        public void focusGained(FocusEvent e)
        {
          IFile file = ((IFileEditorInput) getEditorInput()).getFile();
          try
          {
            // Automatically update the file in the editor if it has changed in the 
            // filesystem
            file.refreshLocal(IResource.DEPTH_ZERO, null);
          } 
          catch (CoreException e1)
          {
            // Just skip it.
          }
        }

        /*
         * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
         */
        public void focusLost(FocusEvent event)
        {
            deactivate();
        }

        /*
         * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
         */
        public void documentAboutToBeChanged(DocumentEvent event)
        {
            if (fActive && (fActiveRegion != null))
            {
                fRememberedPosition = new Position(fActiveRegion.getOffset(), fActiveRegion.getLength());
                try
                {
                    event.getDocument().addPosition(fRememberedPosition);
                }
                catch (BadLocationException x)
                {
                    fRememberedPosition = null;
                }
            }
        }

        /*
         * @see org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.jface.text.DocumentEvent)
         */
        public void documentChanged(DocumentEvent event)
        {
            if ((fRememberedPosition != null) && !fRememberedPosition.isDeleted())
            {
                event.getDocument().removePosition(fRememberedPosition);
                fActiveRegion = new Region(fRememberedPosition.getOffset(), fRememberedPosition.getLength());
            }
            fRememberedPosition = null;
            ISourceViewer viewer = getSourceViewer();
            if (viewer != null)
            {
                StyledText widget = viewer.getTextWidget();
                if ((widget != null) && !widget.isDisposed())
                {
                    widget.getDisplay().asyncExec(new Runnable() {

                        public void run()
                        {
                            deactivate();
                        }
                    });
                }
            }
        }

        /*
         * @see org.eclipse.jface.text.ITextInputListener#inputDocumentAboutToBeChanged(org.eclipse.jface.text.IDocument,
         *      org.eclipse.jface.text.IDocument)
         */
        public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput)
        {
            if (oldInput == null) { return; }
            deactivate();
            oldInput.removeDocumentListener(this);
        }

        /*
         * @see org.eclipse.jface.text.ITextInputListener#inputDocumentChanged(org.eclipse.jface.text.IDocument,
         *      org.eclipse.jface.text.IDocument)
         */
        public void inputDocumentChanged(IDocument oldInput, IDocument newInput)
        {
            if (newInput == null) { return; }
            newInput.addDocumentListener(this);
        }

        /*
         * @see PaintListener#paintControl(PaintEvent)
         */
        public void paintControl(PaintEvent event)
        {
            if (fActiveRegion == null) { return; }
            ISourceViewer viewer = getSourceViewer();
            if (viewer == null) { return; }
            StyledText text = viewer.getTextWidget();
            if ((text == null) || text.isDisposed()) { return; }
            int offset = 0;
            int length = 0;
            if (viewer instanceof ITextViewerExtension5)
            {
                ITextViewerExtension5 extension = (ITextViewerExtension5) viewer;
                IRegion widgetRange = extension.modelRange2WidgetRange(new Region(offset, length));
                if (widgetRange == null) { return; }
                offset = widgetRange.getOffset();
                length = widgetRange.getLength();
            } else
            {
                IRegion region = viewer.getVisibleRegion();
                if (!includes(region, fActiveRegion)) { return; }
                offset = fActiveRegion.getOffset() - region.getOffset();
                length = fActiveRegion.getLength();
            }
            // support for bidi
            Point minLocation = getMinimumLocation(text, offset, length);
            Point maxLocation = getMaximumLocation(text, offset, length);
            int x1 = minLocation.x;
            int x2 = (minLocation.x + maxLocation.x) - minLocation.x - 1;
            int y = (minLocation.y + text.getLineHeight()) - 1;
            GC gc = event.gc;
            if ((fColor != null) && !fColor.isDisposed())
            {
                gc.setForeground(fColor);
            }
            gc.drawLine(x1, y, x2, y);
        }

        private boolean includes(IRegion region, IRegion position)
        {
            return (position.getOffset() >= region.getOffset()) && ((position.getOffset() + position.getLength()) <= (region.getOffset() + region.getLength()));
        }

        private Point getMinimumLocation(StyledText text, int offset, int length)
        {
            Point minLocation = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
            for (int i = 0; i <= length; i++)
            {
                Point location = text.getLocationAtOffset(offset + i);
                if (location.x < minLocation.x)
                {
                    minLocation.x = location.x;
                }
                if (location.y < minLocation.y)
                {
                    minLocation.y = location.y;
                }
            }
            return minLocation;
        }

        private Point getMaximumLocation(StyledText text, int offset, int length)
        {
            Point maxLocation = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
            for (int i = 0; i <= length; i++)
            {
                Point location = text.getLocationAtOffset(offset + i);
                if (location.x > maxLocation.x)
                {
                    maxLocation.x = location.x;
                }
                if (location.y > maxLocation.y)
                {
                    maxLocation.y = location.y;
                }
            }
            return maxLocation;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
         */
        public void propertyChange(PropertyChangeEvent event)
        {
            // TODO Auto-generated method stub
        }
    }

    // folding
    // private RecipeOccurrencesUpdater fOccurrencesUpdater;
    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl(Composite parent)
    {
        super.createPartControl(parent);
        ProjectionViewer projectionViewer = (ProjectionViewer) getSourceViewer();
        projectionSupport = new ProjectionSupport(projectionViewer, getAnnotationAccess(), getSharedColors());
        projectionSupport.install();
        projectionViewer.doOperation(ProjectionViewer.TOGGLE);
        // fOccurrencesUpdater= new RecipeOccurrencesUpdater(this);
    }

    protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles)
    {
        fAnnotationAccess = createAnnotationAccess();
        fOverviewRuler = createOverviewRuler(getSharedColors());
        ISourceViewer viewer = new ProjectionViewer(parent, ruler, fOverviewRuler, true, styles);
        // ensure decoration support has been created and configured:
        getSourceViewerDecorationSupport(viewer);
        return viewer;
    }

    public void fold(int foldingStart, int foldingEnd)
    {
        Position foldingPosition = new Position(foldingStart, foldingEnd - foldingStart);
        if (getSourceViewer().getAnnotationModel() != null)
        {
            getSourceViewer().getAnnotationModel().addAnnotation(new ProjectionAnnotation(), foldingPosition);
        }
    }

    public static IEditorPart openInEditor(IFile file)
    {
        if (file != null)
        {
            IWorkbenchPage p = VelocityPlugin.getActiveWorkbenchWindow().getActivePage();
            if (p != null)
            {
                IEditorPart editorPart = null;
                try
                {
                    editorPart = IDE.openEditor(p, file, true);
                }
                catch (PartInitException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // initializeHighlightRange(editorPart);
                return editorPart;
            }
        }
        return null;
    }
   
}