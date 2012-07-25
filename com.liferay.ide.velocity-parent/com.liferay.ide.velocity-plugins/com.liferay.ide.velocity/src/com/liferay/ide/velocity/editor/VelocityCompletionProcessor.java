package com.liferay.ide.velocity.editor;

import com.liferay.ide.velocity.editor.completion.ICompletionProvider;
import com.liferay.ide.velocity.editor.completion.ProviderManager;
import com.liferay.ide.velocity.preferences.GeneralPreferencePage;
import com.liferay.ide.velocity.ui.VeloContextType;
import com.liferay.ide.velocity.ui.editor.xml.IEditorConfiguration;
import com.liferay.ide.velocity.ui.editor.xml.IHTMLConstants;
import com.liferay.ide.velocity.vaulttec.ui.VelocityPlugin;
import com.liferay.ide.velocity.vaulttec.ui.VelocityPluginImages;
import com.liferay.ide.velocity.vaulttec.ui.editor.VelocityConfiguration;
import com.liferay.ide.velocity.vaulttec.ui.editor.VelocityEditorEnvironment;
import com.liferay.ide.velocity.vaulttec.ui.editor.text.VelocityTextGuesser;
import com.liferay.ide.velocity.vaulttec.ui.model.Directive;
import com.wutka.dtd.DTDAttribute;
import com.wutka.dtd.DTDChoice;
import com.wutka.dtd.DTDElement;
import com.wutka.dtd.DTDEnumeration;
import com.wutka.dtd.DTDItem;
import com.wutka.dtd.DTDMixed;
import com.wutka.dtd.DTDName;
import com.wutka.dtd.DTDNotationList;
import com.wutka.dtd.DTDSequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.velocity.runtime.directive.Macro;
import org.apache.velocity.runtime.directive.VelocimacroProxy;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;


/**
 * DOCUMENT ME!
 *
 * @version $Revision: 32 $
 * @author <a href="mailto:akmal.sarhan@gmail.com">Akmal Sarhan </a>
 * @author Peter Friese
 */
public class VelocityCompletionProcessor extends TemplateCompletionProcessor implements IContentAssistProcessor, IHTMLConstants
{
    boolean			   upperCaseEnabled;
    private static final String       DEFAULT_IMAGE       = "icons/template.gif"; //$NON-NLS-1$
    private Collection<String>	flatValuesTags;
    private Collection<String>	emptyTagSet;
    private Collection<String>	tagSet;
    private Comparator<Object>	proposalComparator  = new Comparator<Object>() {
							      public int compare(Object aProposal1, Object aProposal2)
							      {
								  String text1 = ((CompletionProposal) aProposal1).getDisplayString();
								  String text2 = ((CompletionProposal) aProposal2).getDisplayString();
								  return text1.compareTo(text2);
							      }

							      public boolean equals(Object aProposal)
							      {
								  return false;
							      }
							  };
    private static final String[]     flatValues	  = new String[] { "compact", "checked", "declare", "readonly", "disabled", "selected", "defer", "ismap", "nohref",
	    "noshade", "nowrap", "multiple", "noresize"  };
    public static final String[]      tags		= new String[] { "AREA", "BASE", "BASEFONT", "BR", "COL", "FRAME", "HR", "IMG", "INPUT", "ISINDEX", "LINK", "META",
	    "PARAM", "A", "ADDRESS", "APPLET", "B", "BIG", "BLOCKQUOTE", "BODY", "CAPTION", "CENTER", "CITE", "CODE", "COLGROUP", "DD", "DFN", "DIR", "DIV", "DL", "DT", "EM",
	    "FONT", "FORM", "FRAMESET", "H1", "H2", "H3", "H4", "H5", "H6", "HEAD", "HTML", "I", "KBD", "LI", "MAP", "MENU", "NOBR", "NOFRAMES", "OBJECT", "OL", "OPTION", "P",
	    "PRE", "SAMP", "SCRIPT", "SELECT", "SMALL", "SPAN", "STRIKE", "S", "STRONG", "STYLE", "SUB", "SUP", "TABLE", "TD", "TEXTAREA", "TH", "TITLE", "TR", "TT", "U", "UL",
	    "VAR"					};
    public static final String[]      jspTags	     = new String[] { "C:FOREACH", "C:IF", "C:CHOOSE", "C:WHEN","C:OUT", "C:OTHERWISE", "C:URL", "C:PARAM", "C:SET", "C:REMOVE",
	    "C:FORTOKENS", "FMT:MESSAGE", "FMT:FORMATDATE", "JSP:PARAM", "JSP:OUTPUT", "JSP:SETPROPERTY", "JSP:GETPROPERTY", "C:IMPORT", "C:REDIRECT", "JSP:DIRECTIVE.INCLUDE",
	    "JSP:INCLUDE", "JSP:USEBEAN", "JSP:ROOT"     };
    private VelocityEditor	    fEditor;
    private boolean		   fCompleteDirectives = true;
    protected int		     fTabWidth	   = 4;
    private boolean		   isJsp	       = false;
    private Map<String, List<String>> jspAttrMap;
    private Map<String, List<String>> jspAttrValuesMap;

    public VelocityCompletionProcessor(VelocityEditor anEditor, boolean aCompleteDirectives) {
	upperCaseEnabled = VelocityPlugin.getDefault().getPreferenceStore().getBoolean(GeneralPreferencePage.P_CASE);
	fEditor = anEditor;

	isJsp = anEditor.getEditorInput().getName().matches(".*?\\.jsp.*");
	fCompleteDirectives = aCompleteDirectives;
	tagSet = new ArrayList<String>(Arrays.asList(tags));
	if (isJsp) {
	    tagSet.addAll(Arrays.asList(jspTags));
	    jspAttrMap = new HashMap<String, List<String>>();
	    jspAttrMap.put("c:foreach", Arrays.asList(new String[] { "var", "items", "begin", "end", "step", "varStatus" }));
	    jspAttrMap.put("c:if", Arrays.asList(new String[] { "test", "var", "scope" }));
	    jspAttrMap.put("c:choose", Arrays.asList(new String[] { "test" }));
	    jspAttrMap.put("c:when", Arrays.asList(new String[] { "test" }));
	    jspAttrMap.put("c:otherwise", Arrays.asList(new String[] { "test" }));
	    jspAttrMap.put("c:url", Arrays.asList(new String[] { "value", "context", "var", "scope" }));
	    jspAttrMap.put("c:param", Arrays.asList(new String[] { "name", "value" }));
	    jspAttrMap.put("c:set", Arrays.asList(new String[] { "value", "target", "property", "var", "scope" }));
	    jspAttrMap.put("c:remove", Arrays.asList(new String[] { "var", "scope" }));
	    jspAttrMap.put("c:fortokens", Arrays.asList(new String[] { "var", "items", "begin", "end", "step", "varStatus", "delims" }));
	    jspAttrMap.put("fmt:message", Arrays.asList(new String[] { "key", "bundle", "var", "scope" }));
	    jspAttrMap.put("c:import", Arrays.asList(new String[] { "url", "context", "var", "scope", "charEncoding", "varReader" }));
	    jspAttrMap.put("c:redirect", Arrays.asList(new String[] { "url", "context" }));
	    jspAttrMap.put("jsp:directive.include", Arrays.asList(new String[] { "file" }));
	    jspAttrMap.put("jsp:include", Arrays.asList(new String[] { "page", "flush" }));
	    jspAttrMap.put("jsp:usebean", Arrays.asList(new String[] { "beanName", "id", "class", "scope", "type" }));
	    jspAttrMap.put("jsp:root", Arrays.asList(new String[] { "version", "xmlns", "xmlns:jsp", "xmlns:c", "xmlns:fn", "xmlns:fmt" }));
	    jspAttrMap.put("jsp:output", Arrays.asList(new String[] { "omit-xml-declaration", "doctype-root-element", "doctype-public", "doctype-system" }));
	    jspAttrMap.put("jsp:getProperty", Arrays.asList(new String[] { "name", "property" }));
	    jspAttrMap.put("jsp:param", Arrays.asList(new String[] { "name", "value" }));
	    jspAttrMap.put("c:out", Arrays.asList(new String[] {  "value","escapeXml" ,"default"}));
	    jspAttrMap.put("jsp:setproperty", Arrays.asList(new String[] { "param", "name", "value", "property" }));
	    jspAttrMap.put("fmt:formatdate", Arrays.asList(new String[] { "type", "var", "scope", "value", "pattern", "dateStyle", "timeStyle", "timeZone" }));
	    jspAttrValuesMap = new HashMap<String,List<String>>();
	    jspAttrValuesMap.put("type", Arrays.asList(new String[] { "date", "time", "both" }));
	    jspAttrValuesMap.put("test", Arrays.asList(new String[] { "${}" }));
	    jspAttrValuesMap.put("flush", Arrays.asList(new String[] { "true", "false" }));
	    jspAttrValuesMap.put("escapeXml", Arrays.asList(new String[] { "true", "false" }));
	    jspAttrValuesMap.put("required", Arrays.asList(new String[] { "true", "false" }));
	    jspAttrValuesMap.put("omit-xml-declaration", Arrays.asList(new String[] { "true", "yes", "false", "no" }));
	    jspAttrValuesMap.put("dateStyle", Arrays.asList(new String[] { "default", "short", "medium", "long", "full" }));
	    jspAttrValuesMap.put("timeStyle", Arrays.asList(new String[] { "default", "short", "medium", "long", "full" }));
	    jspAttrValuesMap.put("xmlns", Arrays.asList(new String[] { "http://www.w3c.org/1999/xhtml" }));
	    jspAttrValuesMap.put("xmlns:jsp", Arrays.asList(new String[] { "http://java.sun.com/JSP/Page" }));
	    jspAttrValuesMap.put("xmlns:c", Arrays.asList(new String[] { "http://java.sun.com/jsp/jstl/core" }));
	    jspAttrValuesMap.put("xmlns:fn", Arrays.asList(new String[] { "http://java.sun.com/jsp/jstl/functions" }));
	    jspAttrValuesMap.put("xmlns:fmt", Arrays.asList(new String[] { "http://java.sun.com/jsp/jstl/fmt" }));
	    jspAttrValuesMap.put("doctype-public", Arrays.asList(new String[] { "-//W3C//DTD XHTML 1.0 Transitional//EN" }));
	    jspAttrValuesMap.put("doctype-system", Arrays.asList(new String[] { "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" }));
	    jspAttrValuesMap.put("scope", Arrays.asList(new String[] { "page", "request", "session", "application" }));
	}
	emptyTagSet = Arrays.asList(IHTMLConstants.EMPTY_TAGS);
	flatValuesTags = Arrays.asList(flatValues);
    }

    /**
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer,
     *      int)
     */
    public ICompletionProposal[] computeCompletionProposals(ITextViewer aViewer, int anOffset)
    {
	ICompletionProposal[] proposals = null;
	IDocument doc = aViewer.getDocument();
	VelocityTextGuesser prefix = new VelocityTextGuesser(doc, anOffset, false);
	if (prefix.getType() == VelocityTextGuesser.TYPE_DIRECTIVE) {
	    if (fCompleteDirectives) {
		proposals = getDirectiveProposals(prefix.getText(), anOffset - prefix.getText().length());
	    }
	}
	else if (prefix.getType() == VelocityTextGuesser.TYPE_VARIABLE) {
	    proposals = getVariableProposals(prefix.getText(), anOffset - prefix.getText().length());
	}
	else if (prefix.getType() == VelocityTextGuesser.TAG_DIRECTIVE) {
	    proposals = getTAGProposals(doc, prefix.getText(), anOffset - prefix.getText().length());
	}
	else if (prefix.getType() == VelocityTextGuesser.TAG_CLOSE) {
	    proposals = getTAGCloseProposals(doc, prefix.getText(), anOffset - prefix.getText().length());
	}
	else if (prefix.getType() == VelocityTextGuesser.TYPE_APOSTROPHE) {
	    proposals = getAttributeValuesProposals(doc, prefix.getText(), anOffset - prefix.getText().length());
	}
	else if (EditorsUtil.isInsidePartition(anOffset, new String[] { IEditorConfiguration.TAG_PARTITION, IEditorConfiguration.SCRIPT_PARTITION }, true, doc)) {
	    proposals = getAttributesProposals(doc, prefix.getText(), anOffset - prefix.getText().length());
	}
	// else
	// {
	// proposals = getWordsProposals(doc, prefix.getText(), anOffset -
	// prefix.getText().length());
	// }
	if (proposals == null) {
	    proposals = new ICompletionProposal[0];
	    proposals = super.computeCompletionProposals(aViewer, anOffset);
	}
	Collection<?> extraProposals = getExtraProposals(doc, prefix, anOffset - prefix.getText().length());
	List defaultProposals = new ArrayList(Arrays.asList(proposals));
	defaultProposals.addAll(extraProposals);
	ICompletionProposal[] result = (ICompletionProposal[]) defaultProposals.toArray(new ICompletionProposal[defaultProposals.size()]);
	return result;
    }

    /**
     * Gives extenders the possibility to add their own completion proposals.
     *
     * @param doc
     * @param prefix
     * @param i
     * @return
     */
    private Collection getExtraProposals(IDocument doc, VelocityTextGuesser prefix, int offset)
    {
	Collection result = new ArrayList();
	IFile file = (IFile) fEditor.getEditorInput().getAdapter(IFile.class);
	ICompletionProvider[] completionProviders = ProviderManager.getInstance().getProviders();
	for (int i = 0; i < completionProviders.length; i++) {
	    ICompletionProvider provider = completionProviders[i];
	    Collection extraProposals;
	    try {
		extraProposals = provider.getExtraProposals(fEditor, file, doc, prefix, offset);
		result.addAll(extraProposals);
	    }
	    catch (CoreException e) {
		e.printStackTrace();
	    }
	}
	return result;
    }

    /**
     * @param string
     * @param i
     * @return
     */
    private ICompletionProposal[] getAttributesProposals(IDocument doc, String aPrefix, int anOffset)
    {
	String string = null;
	try {
	    ITypedRegion partition = doc.getPartition(anOffset);
	    int i = partition.getOffset();
	    int b = partition.getLength();
	    string = doc.get(i, b);
	}
	catch (BadLocationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	String s = findMatchingTagIdentifierBefore(anOffset, doc);
	List<String> variables = null;
	if (s != null) {
	    DTDElement element = VelocityEditor.getHTMLElement(s.toLowerCase());
	    variables = new ArrayList<String>();
	    if (element != null) {
		Hashtable table = element.attributes;
		Collection collection = table.values();
		if (collection.size() > 0) {
		    for (Iterator iter = collection.iterator(); iter.hasNext();) {
			DTDAttribute attrib = (DTDAttribute) iter.next();
			if (flatValuesTags.contains(attrib.getName())) {
			    if ((string != null) && (string.indexOf(attrib.getName() + " ") == -1)) {
				variables.add(attrib.getName());
			    }
			}
			else {
			    if ((string != null) && (string.indexOf(attrib.getName() + "=") == -1)) {
				variables.add(attrib.getName());
			    }
			}
		    }
		}
	    }
	    if (element == null && isJsp) {
		for (String attrib : jspAttrMap.get(s)) {
		    if ((string != null) && (string.indexOf(attrib + "=") == -1)) {
			variables.add(attrib);
		    }
		}
		// variables=(List) jspAttrMap.get(s);
	    }
	}
	ICompletionProposal[] result = null;
	CompletionProposal proposal = null;
	if (variables != null) {
	    List proposals = new ArrayList();
	    Iterator iter = variables.iterator();
	    while (iter.hasNext()) {
		String variable = (String) iter.next();
		String append = "=\"\"";
		int cur = 2;
		if (variable.startsWith(aPrefix.toLowerCase())) {
		    if (flatValuesTags.contains(variable.toLowerCase())) {
			append = " ";
			cur = 1;
		    }
		    proposal = new CompletionProposal(variable + append, anOffset, aPrefix.length(), variable.length() + cur, null, variable, null, null);
		    proposals.add(proposal);
		}
	    }
	    Collections.sort(proposals, proposalComparator);
	    result = (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);
	}
	return result;
    }

    private ICompletionProposal[] getAttributeValuesProposals(IDocument doc, String aPrefix, int anOffset)
    {
	String s = findMatchingTagIdentifierBefore(anOffset - 2, doc);
	String attribute = getAttribute(doc, anOffset - 3);
	DTDElement element = VelocityEditor.getHTMLElement(s.toLowerCase());
	Collection variables = new ArrayList();
	if (element != null) {
	    Hashtable table = element.attributes;
	    Collection collection = table.values();
	    if (collection.size() > 0) {
		for (Iterator iter = collection.iterator(); iter.hasNext();) {
		    DTDAttribute attrib = (DTDAttribute) iter.next();
		    if (attrib.getName().equalsIgnoreCase(attribute)) {
			dumpAttribute(attrib, variables);
		    }
		}
	    }
	}
	if (element == null && isJsp && jspAttrValuesMap.get(attribute) != null) {
	    variables = jspAttrValuesMap.get(attribute);
	}
	ICompletionProposal[] result = null;
	CompletionProposal proposal = null;
	if (!variables.isEmpty()) {
	    List proposals = new ArrayList();
	    Iterator iter = variables.iterator();
	    while (iter.hasNext()) {
		String variable = (String) iter.next();
		if (variable.startsWith(aPrefix.toLowerCase())) {
		    proposal = new CompletionProposal(variable + "\" ", anOffset, aPrefix.length() + 1, variable.length() + 2, null, variable, null, null);
		    proposals.add(proposal);
		}
	    }
	    Collections.sort(proposals, proposalComparator);
	    result = (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);
	}
	return result;
    }

    private Set extractWords(String input)
    {
	StringTokenizer stringTokenizer = new StringTokenizer(input, "#=:?\t <>,;()$.'}{][&%!*-/\\\n\"\'");
	Set set = new HashSet();
	while (stringTokenizer.hasMoreTokens()) {
	    set.add(stringTokenizer.nextToken());
	}
	return set;
    }

    private ICompletionProposal[] getWordsProposals(IDocument doc, String aPrefix, int anOffset)
    {
	if (aPrefix == null || aPrefix.length() == 0)
	    return null;
	ICompletionProposal[] result = null;
	CompletionProposal proposal = null;
	Set variables = extractWords(doc.get());
	if (!variables.isEmpty()) {
	    List proposals = new ArrayList();
	    Iterator iter = variables.iterator();
	    while (iter.hasNext()) {
		String variable = (String) iter.next();
		if (variable.startsWith(aPrefix) && !variable.equals(aPrefix)) {
		    proposal = new CompletionProposal(variable, anOffset, aPrefix.length(), variable.length(), null, variable, null, null);
		    proposals.add(proposal);
		}
	    }
	    if (proposals.isEmpty())
		return null;
	    Collections.sort(proposals, proposalComparator);
	    result = (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);
	}
	return result;
    }

    protected int findMatchingOpenTagBefore(int end, IDocument doc)
    {
	int level = 1;
	try {
	    char prev = '\0';
	    for (end--; end >= 0; end--) {
		if (EditorsUtil.isInsidePartition(end, VelocityConfiguration.CDATA_PARTITIONS, true, doc)) {
		    end = doc.getPartition(end).getOffset() - 1;
		}
		else if (EditorsUtil.isInsidePartition(end, VelocityConfiguration.ESCAPED_PARTITIONS, doc)) {
		    end = doc.getPartition(end).getOffset() -1;
		}
		char c = doc.getChar(end);
		if (c == '/' && prev == '>')
		    level++;
		if (c == '<') {
		    if (prev == '/') {
			level++;
		    }
		    else if ((prev != '!') && (prev != '?') && (prev != '%') && (prev != '#')) {
			String id = getIdentifier(doc, end + 1, doc.getLength());
			// if (!fEMPTY_TAG_SET.contains(id.toUpperCase())) {
			level--;
			// }
			if (level == 0) {
			    return end;
			}
		    }
		}
		else if ((c == '"') || (c == '\'')) {
		    end = getStringStart(doc, end, c);
		}
		// TODO if(c=='/' && prev=='>') level++;
		prev = c;
	    }
	}
	catch (BadLocationException e) {
	}
	return -1;
    }

    private ICompletionProposal[] getTAGCloseProposals(IDocument doc, String aPrefix, int anOffset)
    {
	String closetag = ">";
	String s = findMatchingOpenTagIdentifierBefore(anOffset - 2, doc);
	int lasttagOffset = findMatchingOpenTagBefore(anOffset - 2, doc);

	int start = 0;
	try {
	    start = doc.getLineOffset(doc.getLineOfOffset(anOffset));
	}
	catch (BadLocationException e2) {
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	}
	String ddd = null;
	int dlength = 0;
	try {
	    ddd = sameIndentAs(lasttagOffset, doc.getLineOfOffset(anOffset), doc, anOffset, s + closetag);
	    dlength = ddd.length();
	}
	catch (BadLocationException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
	// int curPo = start + dlength;
	ICompletionProposal[] result = null;
	String[] tags = new String[] { "A", "ADDRESS", "APPLET", "B", "BIG", "BLOCKQUOTE", "BODY", "CAPTION", "CENTER", "CITE", "CODE", "COL", "COLGROUP", "DD", "DFN", "DIR",
		"DIV", "DL", "DT", "EM", "FONT", "FORM", "FRAMESET", "H1", "H2", "H3", "H4", "H5", "H6", "HEAD", "HTML", "I", "KBD", "LI", "MAP", "MENU", "NOBR", "NOFRAMES",
		"OBJECT", "OL", "OPTION", "P", "PRE", "SAMP", "SCRIPT", "SELECT", "SMALL", "SPAN", "STRIKE", "S", "STRONG", "STYLE", "SUB", "SUP", "TABLE", "TD", "TEXTAREA", "TH",
		"TITLE", "TR", "TT", "U", "UL", "VAR", "C:FOREACH", "C:IF", "C:CHOOSE", "C:WHEN", "C:OTHERWISE", "C:URL", "C:PARAM", "C:SET", "C:REMOVE", "C:FORTOKENS",
		"FMT:MESSAGE" };
	List variables = Arrays.asList(tags);
	List proposals = new ArrayList();
	CompletionProposal proposal = null;
	if ((s != null) && (s.length() > 0)) {
	    proposal = new CompletionProposal(ddd, start, aPrefix.length() + (anOffset - start), dlength, null, s, null, "test3");
	    proposals.add(proposal);
	    result = (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);
	}
	else if (!variables.isEmpty()) {
	    Iterator iter = variables.iterator();
	    String variable = null;
	    while (iter.hasNext()) {
		variable = (String) iter.next();
		if (variable.startsWith(aPrefix.toUpperCase())) {
		    String opentag = variable;
		    proposal = new CompletionProposal(opentag + closetag, anOffset, aPrefix.length(), variable.length() + 1, null, variable, null, "test3");
		    proposals.add(proposal);
		}
	    }
	    Collections.sort(proposals, proposalComparator);
	    result = (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);
	}
	return result;
    }

    /**
     * @param string
     * @param i
     * @return
     */
    private ICompletionProposal[] getTAGProposals(IDocument doc, String aPrefix, int anOffset)
    {
	Set list = new HashSet();
	if (anOffset > 1) {
	    String s = findMatchingOpenTagIdentifierBefore(anOffset - 1, doc);
	    if (s != null) {

		DTDElement element = VelocityEditor.getHTMLElement(s.toLowerCase());
		if (element != null) {
		    DTDItem item = element.getContent();
		    list = new HashSet();
		    dumpDTDItem(item, list);
		}

		if (isJsp)
//
		    list.addAll(Arrays.asList(jspTags));
	    }
	}
	if (list.isEmpty()) {
	    list = null;
	}
	ICompletionProposal[] result = null;
	Collection variables = null;
	if (list != null) {
	    variables = list;
	}
	else {
	    variables = tagSet;
	}
	CompletionProposal proposal = null;
	if (!variables.isEmpty()) {
	    List proposals = new ArrayList();
	    Iterator iter = variables.iterator();
	    while (iter.hasNext()) {
		String variable = (String) iter.next();
		if (variable.startsWith(aPrefix.toUpperCase())) {
		    String closetag = " />";
		    if (!emptyTagSet.contains(variable)) {
			int lineOfOffset = 0;
			try {
			    lineOfOffset = doc.getLineOfOffset(anOffset);
			}
			catch (BadLocationException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
			closetag = " >\n" + getIndentString(getIndentOfLine(doc, lineOfOffset)) + "</" + variable + ">";
		    }
		    int compromize = 1;
		    String opentag = variable + closetag;
		    if (!VelocityPlugin.getDefault().isUppercaseEnabled())
			opentag = opentag.toLowerCase();
		    proposal = new CompletionProposal(opentag, anOffset, aPrefix.length(), variable.length() + compromize, null, variable, null, null);
		    proposals.add(proposal);
		}
	    }
	    Collections.sort(proposals, proposalComparator);
	    result = (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);
	}
	return result;
    }

    /**
     * Returns proposals from all directives with given prefix.
     */
    private ICompletionProposal[] getDirectiveProposals(String aPrefix, int anOffset)
    {
	List proposals = new ArrayList();
	// Add system directives
	String[] directives = Directive.DIRECTIVES;
	for (int i = directives.length - 1; i >= 0; i--) {
	    String directive = directives[i];
	    if (directive.substring(1).startsWith(aPrefix)) {
		int cursorPos;
		if ((i == Directive.TYPE_ELSE) || (i == Directive.TYPE_END) || (i == Directive.TYPE_STOP)) {
		    cursorPos = directive.length() - 1;
		}
		else {
		    directive += "()";
		    cursorPos = directive.length() - 2;
		}
		proposals.add(new CompletionProposal(directive.substring(1), anOffset, aPrefix.length(), cursorPos, VelocityPluginImages
			.get(VelocityPluginImages.IMG_OBJ_SYSTEM_DIRECTIVE), directive, null, null));
	    }
	}
	// Add Velocity library macros
	for (VelocimacroProxy vp : VelocityEditorEnvironment.getParser().getLibraryMacros())
	{
	  String name = vp.getName();
	    if (name.startsWith(aPrefix))
	    {
		String insert = name + "()";
		int cursorPos;
		StringBuffer buffer = new StringBuffer();
		buffer.append('#');
		buffer.append(name);
		buffer.append('(');
		if (vp.getNumArgs() == 0) {
		    cursorPos = insert.length();
		    buffer.append(')');
		}
		else {
		    cursorPos = insert.length() - 1;
		    for (Macro.MacroArg arg : vp.getMacroArgs())
		    {
		      buffer.append('$');
		      buffer.append(arg.name);
		    }
		    buffer.append(')');
		}
		buffer.append(" - ");
		buffer.append(vp.getTemplateName());
		proposals.add(new CompletionProposal(insert, anOffset, aPrefix.length(), cursorPos, VelocityPluginImages.get(VelocityPluginImages.IMG_OBJ_MACRO),
			buffer.toString(), null, null));
	    }
	}
	// Add user directives
	Iterator userDirectives = VelocityEditorEnvironment.getParser().getUserDirectives().iterator();
	while (userDirectives.hasNext()) {
	    String directive = ((String) userDirectives.next());
	    if (directive.substring(1).startsWith(aPrefix)) {
		directive += "()";
		int cursorPos = directive.length() - 1;
		proposals.add(new CompletionProposal(directive.substring(1), anOffset, aPrefix.length(), cursorPos, VelocityPluginImages
			.get(VelocityPluginImages.IMG_OBJ_USER_DIRECTIVE), directive, null, null));
	    }
	}
	Collections.sort(proposals, proposalComparator);
	return (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);
    }

    /**
     * Returns proposals from all variables with given prefix.
     */
    private ICompletionProposal[] getVariableProposals(String aPrefix, int anOffset)
    {
	ICompletionProposal[] result = null;
	List variables = fEditor.getVariables(fEditor.getLine(anOffset));
//	if (!variables.isEmpty()) {
	    List proposals = new ArrayList();
	    Iterator iter = variables.iterator();
	    while (iter.hasNext()) {
		String variable = (String) iter.next();
		if (variable.substring(1).startsWith(aPrefix)) {
		    proposals.add(new CompletionProposal(variable.substring(1), anOffset, aPrefix.length(), variable.length() - 1, null, variable, null, null));
		}
	    }

	    ICompletionProvider[] completionProviders = ProviderManager.getInstance().getProviders();

	    for(ICompletionProvider cp : completionProviders)
	    {
            try
            {
    	        proposals.addAll( cp.getVariableProposals(aPrefix, anOffset));
            }
            catch (Exception e)
            {
                //noerror
            }
	    }

	    Collections.sort(proposals, proposalComparator);
	    result = (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);
//	}
	return result;
    }

    /**
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer,
     *      int)
     */
    public IContextInformation[] computeContextInformation(ITextViewer viewer, int documentOffset)
    {
	return null;
    }

    /**
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
     */
    public char[] getCompletionProposalAutoActivationCharacters()
    {
	return new char[] { '#', '$', '<', '/', '.' };
    }

    /**
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationAutoActivationCharacters()
     */
    public char[] getContextInformationAutoActivationCharacters()
    {
	return (new char[] { '>' });
    }

    /**
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
     */
    public IContextInformationValidator getContextInformationValidator()
    {
	return null;
    }

    /**
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
     */
    public String getErrorMessage()
    {
	return null;
    }

    protected String findMatchingOpenTagIdentifierBefore(int end, IDocument doc)
    {
	int level = 1;
	String identifier = null;
	try {
	    char prev = '\0';
	    for (end--; end >= 0; end--) {
		if (EditorsUtil.isInsidePartition(end, VelocityConfiguration.CDATA_PARTITIONS, true, doc)) {
		    end = doc.getPartition(end).getOffset() - 1;
		}
		 else if (EditorsUtil.isInsidePartition(end,
		 VelocityConfiguration.ESCAPED_PARTITIONS, doc))
		 {
		 end = doc.getPartition(end).getOffset()-1;

		 }
		char c = doc.getChar(end);
		if (c == '/' && prev == '>')
		    level++;
		if (c == '<') {
		    if (prev == '/') {
			level++;
		    }
		    else if ((prev != '!') && (prev != '?') && (prev != '%') && (prev != '#')) {
			String id = getIdentifier(doc, end + 1, doc.getLength());
			// if (!fEMPTY_TAG_SET.contains(id.toUpperCase())) {
			level--;
			// }
			if (level == 0) {
			    identifier = id;
			    return identifier;
			}
		    }
		}
		else if ((c == '"') || (c == '\'')) {
		    end = getStringStart(doc, end, c);
		}
		// if(c=='/' && prev=='>') level++;
		prev = c;
	    }
	}
	catch (BadLocationException e) {
	}
	return identifier;
    }

    protected String findMatchingTagIdentifierBefore(int end, IDocument doc)
    {
	int level = 1;
	String identifier = null;
	try {
	    char prev = '\0';
	    for (end--; end >= 0; end--) {
		if (EditorsUtil.isInsidePartition(end, VelocityConfiguration.CDATA_PARTITIONS, true, doc)) {
		    end = doc.getPartition(end).getOffset() - 1;
		}
		else if (EditorsUtil.isInsidePartition(end, VelocityConfiguration.ESCAPED_PARTITIONS, doc)) {
		    end = doc.getPartition(end).getOffset();
		}
		char c = doc.getChar(end);
		if (c == '<') {
		    if (prev == '/') {
			level++;
		    }
		    else if ((prev != '!') && (prev != '?') && (prev != '%') && (prev != '#')) {
			String id = getIdentifier(doc, end + 1, doc.getLength());
			identifier = id;
			return identifier;
		    }
		}
		else if ((c == '"') || (c == '\'')) {
		    end = getStringStart(doc, end, c);
		}
		prev = c;
	    }
	}
	catch (BadLocationException e) {
	}
	return identifier;
    }

    protected int getStringStart(IDocument document, int end, char quote)
    {
	try {
	    for (end--; end >= 0; end--) {
		char c = document.getChar(end);
		if (c == quote) {
		    if ((end == 0) || (document.getChar(end - 1) != '\\')) {
			return end;
		    }
		    end--;
		}
	    }
	}
	catch (BadLocationException e) {
	}
	return -1;
    }

    private String getIdentifier(IDocument document, int start, int end)
    {
	start = skipWhitespace(document, start, end);
	if (start >= end) {
	    return "";
	}
	StringBuffer ret = new StringBuffer();
	try {
	    char c = document.getChar(start++);
	    if (isIdentifierStart(c)) {
		while (start < end) {
		    ret.append(c);
		    c = document.getChar(start++);
		    if (!isIdentifierChar(c)) {
			break;
		    }
		}
	    }
	}
	catch (BadLocationException e) {
	}
	return ret.toString();
    }

    private String getAttribute(IDocument document, int offset)
    {
	StringBuffer ret = new StringBuffer();
	try {
	    char c = document.getChar(offset--);
	    if (Character.isLetter(c)) {
		while ((c != ' ') && isIdentifierChar(c)) {
		    ret.append(c);
		    c = document.getChar(offset--);
		}
	    }
	}
	catch (BadLocationException e) {
	}
	return ret.reverse().toString();
    }

    private int skipWhitespace(IDocument document, int start, int end)
    {
	try {
	    for (char c = document.getChar(start); (start < end) && Character.isWhitespace(c); c = document.getChar(++start)) {
	    }
	}
	catch (BadLocationException e) {
	}
	return start;
    }

    private boolean isIdentifierStart(char c)
    {
	return Character.isLetter(c)|| c == '!';
    }

    private boolean isIdentifierChar(char c)
    {
	return Character.isLetterOrDigit(c) || c == ':' || c == '.' || c == '-';
    }

    protected int getIndentOfLine(IDocument d, int line)
    {
	try {
	    if (line >= 0) {
		int start = d.getLineOffset(line);
		int end = (start + d.getLineLength(line)) - 1;
		int whiteend = findEndOfWhiteSpace(d, start, end);
		return indentWidthOf(d.get(start, whiteend - start), fTabWidth);
	    }
	    else {
		return 0;
	    }
	}
	catch (Exception e) {
	    return 0;
	}
    }

    /**
     * DOCUMENT ME!
     *
     * @param str
     *                DOCUMENT ME!
     * @param tabwidth
     *                DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static int indentWidthOf(String str, int tabwidth)
    {
	return indentWidthOf(str, 0, tabwidth);
    }

    /**
     * DOCUMENT ME!
     *
     * @param str
     *                DOCUMENT ME!
     * @param start
     *                DOCUMENT ME!
     * @param tabwidth
     *                DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static int indentWidthOf(String str, int start, int tabwidth)
    {
	int ret = 0;
	for (int len = str.length(); start < len; start++) {
	    char c = str.charAt(start);
	    if (c == '\t') {
		ret = ((ret / tabwidth) + 1) * tabwidth;
		continue;
	    }
	    if ((c == ' ') || (c == '\240')) {
		ret++;
		continue;
	    }
	    if ((c != '\n') && (c != '\r')) {
		break;
	    }
	    ret = 0;
	}
	return ret;
    }

    protected int findEndOfWhiteSpace(IDocument document, int offset, int end) throws BadLocationException
    {
	while (offset < end) {
	    char c = document.getChar(offset);
	    if ((c != ' ') && (c != '\t')) {
		return offset;
	    }
	    offset++;
	}
	return end;
    }

    protected StringBuffer getIndentString(int indent)
    {
	StringBuffer ret = new StringBuffer();
	int n = indent / fTabWidth;
	for (int i = 0; i < n; i++) {
	    ret.append("    ");
	}
	n = indent - (n * fTabWidth);
	for (int i = 0; i < n; i++) {
	    ret.append(' ');
	}
	return ret;
    }

    protected String sameIndentAs(int matchoffset, int line, IDocument document, int actualOffset, String variable)
    {
	try {
	    int indent = getIndentOfLine(document, document.getLineOfOffset(matchoffset));
	    StringBuffer buf = getIndentString(indent);
	    int start = (line < 0) ? 0 : document.getLineOffset(line);
	    int whiteend = findEndOfWhiteSpace(document, start, actualOffset);
	    buf.append(document.get(whiteend, actualOffset - whiteend));
	    buf.append(variable);
	    return buf.toString();
	}
	catch (BadLocationException e) {
	}
	return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param item
     *                DOCUMENT ME!
     * @param list
     *                DOCUMENT ME!
     */
    public static void dumpDTDItem(DTDItem item, Set list)
    {
	if (item == null) {
	    return;
	}
	else if (item instanceof DTDName) {
	    list.add(((DTDName) item).value.toUpperCase());
	}
	else if (item instanceof DTDChoice) {
	    DTDItem[] items = ((DTDChoice) item).getItems();
	    for (int i = 0; i < items.length; i++) {
		dumpDTDItem(items[i], list);
	    }
	}
	else if (item instanceof DTDSequence) {
	    DTDItem[] items = ((DTDSequence) item).getItems();
	    for (int i = 0; i < items.length; i++) {
		dumpDTDItem(items[i], list);
	    }
	}
	else if (item instanceof DTDMixed) {
	    DTDItem[] items = ((DTDMixed) item).getItems();
	    for (int i = 0; i < items.length; i++) {
		dumpDTDItem(items[i], list);
	    }
	}
    }

    /**
     * DOCUMENT ME!
     *
     * @param attr
     *                DOCUMENT ME!
     * @param list
     *                DOCUMENT ME!
     */
    public static void dumpAttribute(DTDAttribute attr, Collection list)
    {
	if (attr.type instanceof DTDEnumeration) {
	    String[] items = ((DTDEnumeration) attr.type).getItems();
	    for (int i = 0; i < items.length; i++) {
		list.add(items[i]);
	    }
	}
	else if (attr.type instanceof DTDNotationList) {
	    String[] items = ((DTDNotationList) attr.type).getItems();
	    for (int i = 0; i < items.length; i++) {
		list.add(items[i]);
	    }
	}
    }

    protected Template[] getTemplates(String contextTypeId)
    {
	return TemplateEditorUI.getDefault().getTemplateStore().getTemplates(contextTypeId);
    }

    /**
     * Return the XML context type that is supported by this plugin.
     */
    // protected ContextType getContextType(ITextViewer viewer, IRegion region)
    // {
    // return
    // TemplateEditorUI.getDefault().getContextTypeRegistry().getContextType(VeloContextType.XML_CONTEXT_TYPE);
    // }
    /**
     * Always return the default image.
     */
    protected Image getImage(Template template)
    {
	ImageRegistry registry = TemplateEditorUI.getDefault().getImageRegistry();
	Image image = registry.get(DEFAULT_IMAGE);
	if (image == null) {
			ImageDescriptor desc =
				TemplateEditorUI.imageDescriptorFromPlugin( "com.liferay.ide..velocity", DEFAULT_IMAGE ); //$NON-NLS-1$
	    registry.put(DEFAULT_IMAGE, desc);
	    image = registry.get(DEFAULT_IMAGE);
	}
	return image;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getContextType(org.eclipse.jface.text.ITextViewer,
     *      org.eclipse.jface.text.IRegion)
     */
    protected TemplateContextType getContextType(ITextViewer viewer, IRegion region)
    {
	return TemplateEditorUI.getDefault().getContextTypeRegistry().getContextType(VeloContextType.XML_CONTEXT_TYPE);
	// return null;
    }
}