package com.liferay.ide.velocity.vaulttec.ui.editor.actions;

import com.liferay.ide.velocity.editor.EditorsUtil;
import com.liferay.ide.velocity.editor.compare.VelocityCompare;
import com.liferay.ide.velocity.editor.compare.VelocityInput;
import com.liferay.ide.velocity.scanner.VelocityPartitionScanner;
import com.liferay.ide.velocity.ui.editor.xml.IEditorConfiguration;
import com.liferay.ide.velocity.ui.editor.xml.VelocityAutoIndentStrategy;
import com.liferay.ide.velocity.vaulttec.ui.VelocityPlugin;
import com.liferay.ide.velocity.vaulttec.ui.editor.VelocityConfiguration;
import com.liferay.ide.velocity.vaulttec.ui.model.Directive;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;


public class Formatter
{

    private Object             _selected = null;

    public static final String LINE_SEP  = System.getProperty("line.separator");

    /**
     * @param document
     */
    public IDocument format(final IDocument document)
    {
        Runnable r = new Runnable() {

            public void run()
            {
                String textBeforeFormatting = document.get();
                IDocument tempDocument = new Document();
                boolean isUppercaseEnabled = VelocityPlugin.getDefault().isUppercaseEnabled();
                Pattern pattern = null;
                Matcher matcher = null;
                String newText = textBeforeFormatting;
                String patternStr = "^[\\s]*";
                String replacementStr = "";
                // put all the tags on newLine but not those which start
                // with ##
                pattern = Pattern.compile("<[/]?(?!textarea)[a-zA-Z]+");
                matcher = pattern.matcher(newText);
                StringBuffer buf = new StringBuffer();
                while ((matcher.find()))
                {
                    // Get the match result
                    String replaceStr = matcher.group();
                    // Insert replacement
                    matcher.appendReplacement(buf, replaceStr);
                }
                matcher.appendTail(buf);
                // Get result
                newText = buf.toString();
                String patternStrClose, replaceStrClose = "";
                patternStrClose = "(</.*?>|<.*?/>)";
                replaceStrClose = "$1\n";
                pattern = Pattern.compile(patternStrClose, Pattern.MULTILINE);
                matcher = pattern.matcher(newText);
                newText = matcher.replaceAll(replaceStrClose);
                patternStrClose = "(<(?!/)[a-zA-Z].*?>)\\s*?(?=<(?!/)[a-zA-Z].*?>)";
                replaceStrClose = "$1\n";
                pattern = Pattern.compile(patternStrClose, Pattern.MULTILINE);
                matcher = pattern.matcher(newText);
                newText = matcher.replaceAll(replaceStrClose);
                pattern = Pattern.compile(patternStr, Pattern.MULTILINE);
                matcher = pattern.matcher(newText);
                newText = matcher.replaceAll(replacementStr);
                IDocumentPartitioner partitioner = new FastPartitioner(new VelocityPartitionScanner(), VelocityPartitionScanner.TYPES);
                partitioner.connect(tempDocument);
                tempDocument.setDocumentPartitioner(partitioner);
                tempDocument.set(newText);
                int numberOfLines = tempDocument.getNumberOfLines();
                if (numberOfLines > 0)
                {
                    for (int i = 0; i < numberOfLines; i++)
                    {
                        try
                        {
                            IRegion lineInformation = tempDocument.getLineInformation(i);
                            int orginalOffset = lineInformation.getOffset();
                            int originalLength = lineInformation.getLength();
                            String oldText = tempDocument.get(orginalOffset, originalLength);
                            String smartIndentOnTabResult = smartIndentOnTab(tempDocument, orginalOffset);
                            tempDocument.replace(orginalOffset, originalLength, smartIndentOnTabResult + oldText);
                        }
                        catch (BadLocationException e)
                        {
                            System.out.println(e);
                        }
                    }
                }
                pattern = Pattern.compile("^[\t]|^[\\s]{4}", Pattern.MULTILINE);
                matcher = pattern.matcher(tempDocument.get());
                newText = matcher.replaceAll(replacementStr);
                VelocityInput left = new VelocityInput("left", textBeforeFormatting);
                VelocityInput right = new VelocityInput("right", newText);
                VelocityCompare velocityCompare = new VelocityCompare(left, right, null);
                if (velocityCompare.openCompareDialog() == 0)
                {
                    document.set(right.getText());
                }
            }
        };
        r.run();
        return document;
    }

    public int getIndentOfLine(IDocument d, int line) throws BadLocationException
    {
        if (line >= 0)
        {
            int start = d.getLineOffset(line);
            int end = (start + d.getLineLength(line)) - 1;
            int whiteend = findEndOfWhiteSpace(d, start, end);
            return VelocityAutoIndentStrategy.indentWidthOf(d.get(start, whiteend - start), 4);
        }
        else
        {
            return 0;
        }
    }

    protected int findEndOfWhiteSpace(IDocument document, int offset, int end) throws BadLocationException
    {
        while (offset < end)
        {
            char c = document.getChar(offset);
            if (c != ' ' && c != '\t') { return offset; }
            offset++;
        }
        return end;
    }

    public boolean isInsideEscapedPartitions(int offset, IDocument doc)
    {
        if (EditorsUtil.isInsidePartition(offset, VelocityConfiguration.CDATA_PARTITIONS, true, doc))
        {
            return true;
        }
        else
        {
            return EditorsUtil.isInsidePartition(offset, VelocityConfiguration.ESCAPED_PARTITIONS, doc);
        }
    }

    public int getNonBlankLineAbove(int line, IDocument doc)
    {
        IDocumentPartitioner partitioner = doc.getDocumentPartitioner();
        while (--line >= 0)
        {
            if (EditorsUtil.isBlankLine(doc, line))
            {
                continue;
            }
            try
            {
                int start = doc.getLineOffset(line);
                if (EditorsUtil.isInsidePartition(start, new String[] { IEditorConfiguration.CDATA_PARTITION }, true, doc))
                {
                    line = doc.getLineOfOffset(partitioner.getPartition(start).getOffset());
                    continue;
                }
                if (isInsideEscapedPartitions(start, doc))
                {
                    line = doc.getLineOfOffset(partitioner.getPartition(start).getOffset()) + 1;
                    continue;
                }
            }
            catch (BadLocationException e)
            {
            }
            return line;
        }
        return line;
    }

    public int unIndentOnTagEnd(int indent, int offset, IDocument doc)
    {
        if (isEndTag(offset, doc) > 0)
        {
            return unIndent(indent, 1);
        }
        else
        {
            return indent;
        }
    }

    public int isEndTag(int offset, IDocument doc)
    {
        int len = doc.getLength();
        if ((offset + 1) >= len || isInsideEscapedPartitions(offset, doc)) { return 0; }
        try
        {
            int i = offset;
            char c = '\0';
            try
            {
                i = findEndOfWhiteSpace(doc, offset, offset + 20);
                c = doc.getChar(i);
            }
            catch (Exception e)
            {
                i = offset;
            }
            char cc = doc.getChar(i + 1);
            char ccc = '\0';
            if ((offset + 2) < len)
            {
                ccc = doc.getChar(i + 2);
            }
            String f = VelocityAutoIndentStrategy.getVeloIdentifier(doc, i, i + 5);
            if (((c == '<') && (cc == '/')) || ((c == '/') && (cc == '>')) || ((c == '-') && (cc == '-') && (ccc == '>')))
            {
                return i;
            }
            else if ((f != null) && f.equalsIgnoreCase("#end")) { return i; }
        }
        catch (BadLocationException e)
        {
        }
        return 0;
    }

    private int getStringEnd(IDocument d, int pos, int end, char ch) throws BadLocationException
    {
        while (pos < end)
        {
            char curr = d.getChar(pos);
            pos++;
            if (curr == '\\')
            {
                pos++;
            }
            else if (curr == ch) { return pos; }
        }
        return end;
    }

    private String smartindentonVeloTab(IDocument doc, int cmdoffset)
    {
        try
        {
            int len = doc.getLength();
            int p = (cmdoffset != len) ? cmdoffset : (cmdoffset - 1);
            int line = doc.getLineOfOffset(p);
            int start = doc.getLineOffset(line);
            int whiteend = findEndOfWhiteSpace(doc, start, start + doc.getLineLength(line));
            int indent;
            if ((isEndTag(whiteend, doc)) > 0)
            {
                int matchoffset = VelocityAutoIndentStrategy.findStartVeloBefore(start, doc);
                if (matchoffset < 0) { return ""; }
                indent = getIndentOfLine(doc, doc.getLineOfOffset(matchoffset));
            }
            else
            {
                indent = 0;
                line = getNonBlankLineAbove(line, doc);
                if (line >= 0)
                {
                    indent = getIndentOfLine(doc, line);
                    indent = unIndentOnTagEnd(indent, whiteend, doc);
                    int n = getTagCount(doc, doc.getLineOffset(line), start, true);
                    if (n > 0)
                    {
                        indent = indent(indent, n);
                    }
                    else if (n < 0)
                    {
                        indent = unIndent(indent, -n);
                    }
                }
            }
            if (indent == indentWidthOf(doc.get(start, whiteend - start), 4))
            {
                indent = indent(indent, 1);
            }
            return getIndentString(indent).toString();
        }
        catch (BadLocationException e)
        {
        }
        return "";
    }

    private String smartIndentOnTab(IDocument doc, int originalCmdOffset)
    {
        if ((originalCmdOffset == -1) || (doc.getLength() == 0)) { return ""; }
        try
        {
            int len = doc.getLength();
            int p = (originalCmdOffset != len) ? originalCmdOffset : (originalCmdOffset - 1);
            int line = doc.getLineOfOffset(p);
            int start = doc.getLineOffset(line);
            int whiteend = findEndOfWhiteSpace(doc, start, start + doc.getLineLength(line));
            String f = VelocityAutoIndentStrategy.getVeloIdentifier(doc, whiteend, whiteend + 5);
            if ((f != null) && f.equals("#end")) { return smartindentonVeloTab(doc, originalCmdOffset); }
            if (originalCmdOffset > whiteend) { return ""; }
            int indent;
            int i = 0;
            if ((i = isEndTag(whiteend, doc)) > 0)
            {
                int matchoffset = VelocityAutoIndentStrategy.findMatchingOpenTagBefore(i, doc);
                if (matchoffset < 0) { return ""; }
                indent = getIndentOfLine(doc, doc.getLineOfOffset(matchoffset));
            }
            else
            {
                indent = 0;
                line = getNonBlankLineAbove(line, doc);
                if (line >= 0)
                {
                    indent = getIndentOfLine(doc, line);
                    indent = unIndentOnTagEnd(indent, whiteend, doc);
                    int n = getTagCount(doc, doc.getLineOffset(line), start, true);
                    if (n > 0)
                    {
                        indent = indent(indent, n);
                    }
                    else if (n < 0)
                    {
                        indent = unIndent(indent, -n);
                    }
                }
            }
            if (indent == indentWidthOf(doc.get(start, whiteend - start), 4))
            {
                indent = indent(indent, 1);
            }
            return getIndentString(indent).toString();
        }
        catch (BadLocationException e)
        {
        }
        return "";
    }

    public static int indentWidthOf(String str, int tabwidth)
    {
        return VelocityAutoIndentStrategy.indentWidthOf(str, 0, tabwidth);
    }

    public StringBuffer getIndentString(int indent)
    {
        StringBuffer ret = new StringBuffer();
        int n = indent / 4;
        for (int i = 0; i < n; i++)
        {
            ret.append("    ");
        }
        n = indent - (n * 4);
        for (int i = 0; i < n; i++)
        {
            ret.append(' ');
        }
        return ret;
    }

    public int indent(int indent, int n)
    {
        return ((indent / 4) + n) * 4;
    }

    public int unIndent(int indent, int n)
    {
        return ((((indent - 1) / 4) - n) + 1) * 4;
    }

    public int getTagCount(IDocument document, int start, int end, boolean skip) throws BadLocationException
    {
        int bracketcount = 0;
        while (start < end)
        {
            if (EditorsUtil.isInsidePartition(start, VelocityConfiguration.ESCAPED_PARTITIONS, true, document))
            {
                org.eclipse.jface.text.ITypedRegion partition = document.getPartition(start);
                start = partition.getOffset() + partition.getLength();
            }
            char c = document.getChar(start);
            start++;
            switch (c)
            {
                default:
                    break;
                case 35: // '#'
                {
                    if (start >= end)
                    {
                        break;
                    }
                    char c1 = document.getChar(start);
                    if (c1 != '#')
                    {
                        String id = VelocityAutoIndentStrategy.getIdentifier(document, start, end + 1).toLowerCase();
                        if (Arrays.asList(Directive.INDENT_DIRECTIVES).contains(id))
                        {
                            bracketcount++;
                            skip = false;
                        }
                        break;
                    }
                    if (!skip)
                    {
                        bracketcount--;
                        start++;
                    }
                    break;
                }
                case 37: // '%'
                case 47: // '/'
                case 63: // '?'
                {
                    if (start >= end)
                    {
                        break;
                    }
                    char c1 = document.getChar(start);
                    if ((c1 == '>') && !skip)
                    {
                        bracketcount--;
                        start++;
                    }
                    break;
                }
                case 60: // '<'
                {
                    if (start >= end)
                    {
                        break;
                    }
                    char c1 = document.getChar(start);
                    if ((c1 == '!') || (c1 == '?') || (c1 == '%') || (c1 == '#'))
                    {
                        skip = false;
                        break;
                    }
                    if (c1 != '/')
                    {
                        String id = VelocityAutoIndentStrategy.getIdentifier(document, start, end).toUpperCase();
                        // if
                        // (!VelocityAutoIndentStrategy.fEMPTY_TAG_SET.contains(id))
                        // {
                        bracketcount++;
                        skip = false;
                        // }
                        break;
                    }
                    if (!skip)
                    {
                        bracketcount--;
                        start++;
                    }
                    break;
                }
                case 34: // '"'
                case 39: // '\''
                {
                    start = getStringEnd(document, start, end, c);
                    break;
                }
            }
        }
        return bracketcount;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action)
    {
        if (_selected == null)
        {
            MessageDialog.openInformation(new Shell(), "VelocityPlugin", "Unable to open file");
            // VelocityPlugin.log("Unable to open file");
            return;
        }
        if (_selected instanceof IStructuredSelection)
        {
            //
            try
            {
                Object[] items = ((IStructuredSelection) _selected).toArray();
                Set files = new HashSet(items.length, 1.0F);
                try
                {
                    for (int i = 0; i < items.length; i++)
                    {
                        if (items[i] instanceof IResource)
                        {
                            IResource resource = (IResource) items[i];
                            switch (resource.getType())
                            {
                                case IResource.FOLDER:
                                case IResource.PROJECT:
                                    IContainer folder = (IContainer) items[i];
                                    getChildren(folder, files);
                                    break;
                                case IResource.FILE:
                                    files.add((IFile) items[i]);
                                    // ((IFile) items[i]).getProject()
                                    break;
                                default:
                                    /**
                                     * @todo use logger to print warning about
                                     *       invalid type
                                     */
                                    break;
                            }
                        }
                    }
                }
                catch (CoreException ex)
                {
                    ex.printStackTrace();
                }
                for (Iterator iter = files.iterator(); iter.hasNext();)
                {
                    IFile directory = (IFile) iter.next();
                    // sortFile(directory);
                    // run();
                }
            }
            catch (Exception e)
            {
                VelocityPlugin.log(e);
            }
        }
        else
        {
            MessageDialog.openInformation(new Shell(), "VelocityPlugin", "Unable to open file");
            // VelocityPlugin.log("Unable to open shell");
            return;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection)
    {
        _selected = null;
        if (selection instanceof IStructuredSelection)
        {
            _selected = (IStructuredSelection) selection;
        }
    }

    private void getChildren(IContainer resource, Set files) throws CoreException
    {
        IResource[] children = resource.members();
        for (int i = 0; i < children.length; i++)
        {
            IResource child = children[i];
            switch (child.getType())
            {
                case IResource.FILE:
                    if (child.getName().endsWith(".vm") || child.getName().endsWith(".html"))
                    {
                        files.add((IFile) child);
                    }
                    break;
                case IResource.FOLDER:
                    getChildren((IFolder) child, files);
                    break;
                case IResource.PROJECT:
                    getChildren((IProject) child, files);
                    break;
                case IResource.ROOT:
                    getChildren((IWorkspaceRoot) child, files);
                    break;
            }
        }
    }

    private void formatFile(IFile file)
    {
        if (file.getFileExtension().equalsIgnoreCase("vm") || file.getFileExtension().equalsIgnoreCase("html"))
        {
            ISelection s = new StructuredSelection(file);
            // provider.setSelection(s);
            String msg = "Are you sure you want to format \'" + file.getName() + "\'?"; //$NON-NLS-1$
            if (!MessageDialog.openQuestion(new Shell(), "Confirm formatting", msg)) { return; }
            // Properties p = new Properties();
            IDocument document = new Document();
            BufferedWriter awriter = null;
            String line = null;
            StringBuffer b = new StringBuffer();
            try
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(new BufferedInputStream(Files.newInputStream(file.getLocation().toFile().toPath()))));
                while ((line = in.readLine()) != null)
                {
                    b.append(line);
                    b.append(LINE_SEP);
                }
                document.set(b.toString());
                document = format(document);
                awriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(file.getLocation().toFile().toPath()), "8859_1"));
                awriter.write(document.get());
                awriter.flush();
                awriter.close();
                if (file instanceof IResource)
                {
                    ((IResource) file).refreshLocal(IResource.DEPTH_ZERO, null);
                }
            }
            catch (Exception e)
            {
                VelocityPlugin.log(e);
            }
            finally
            {
                if (awriter != null)
                {
                    try
                    {
                        awriter.close();
                    }
                    catch (IOException e)
                    {
                        VelocityPlugin.log(e);
                    }
                }
            }
        }
    }
}
