package com.liferay.ide.velocity.ui.editor.xml;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.source.ISourceViewer;

import com.liferay.ide.velocity.editor.EditorsUtil;
import com.liferay.ide.velocity.vaulttec.ui.VelocityPlugin;
import com.liferay.ide.velocity.vaulttec.ui.editor.VelocityConfiguration;
import com.liferay.ide.velocity.vaulttec.ui.model.Directive;


/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 13 $
 * @author <a href="mailto:akmal.sarhan@gmail.com">Akmal Sarhan </a>
 */
public class VelocityAutoIndentStrategy extends DefaultIndentLineAutoEditStrategy
{

    public static final String  NAME = "VelocityAutoIndentStrategy";
    public static Set           fEMPTY_TAG_SET;
    static
    {
        fEMPTY_TAG_SET = new HashSet();
        for (int i = 0; i < IHTMLConstants.EMPTY_TAGS.length; i++)
        {
            fEMPTY_TAG_SET.add(IHTMLConstants.EMPTY_TAGS[i]);
        }
    }
    public IEditorConfiguration fConfig;
    public ISourceViewer        fViewer;
    public int                  fTabWidth;
    public String               fTab;
    public boolean              isUsingTab;

    public VelocityAutoIndentStrategy(IEditorConfiguration cf, ISourceViewer viewer)
    {
        fConfig = cf;
        fViewer = viewer;
        initPreferences();
    }

    /**
     * DOCUMENT ME!
     */
    public void initPreferences()
    {
        fTabWidth = fConfig.getTabWidth(fViewer);
        fTab = fConfig.getTab(fViewer);
        isUsingTab = "\t".equals(fTab);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param document
     *            DOCUMENT ME!
     * @param cmd
     *            DOCUMENT ME!
     */
    public void customizeDocumentCommand(IDocument document, DocumentCommand cmd)
    {
        if ((cmd.length == 0) && (cmd.text != null) && EditorsUtil.isLineDelimiter(document, cmd.text))
        {
            smartIndentAfterNewLine(document, cmd);
        } else if (">".equals(cmd.text))
        {
            smartIndentOnTagClose(document, cmd);
        } else if ("\t".equals(cmd.text))
        {
            smartIndentOnTab(document, cmd);
        } else if ("".equals(cmd.text) && (cmd.length == 1))
        {
            smartIndentOnBackspace(document, cmd);
        } else if (cmd.text != null)
        {
            String re = "";
            boolean replace = false;
            int s = 0;
            try
            {
                s = cmd.text.charAt(0);
            }
            catch (Exception e)
            {
                return;
            }
            if (VelocityPlugin.getDefault().isAutoCompletionEnabled())
            {
                switch (s)
                {
                    case '\'':
                    case '\"':
                        replace = true;
                        re = cmd.text;
                        break;
                    case '(':
                        replace = true;
                        re = ")";
                        break;
                    case '[':
                        replace = true;
                        re = "]";
                        break;
                    case '{':
                        replace = true;
                        re = "}";
                        break;
                    default:
                        break;
                }
            }
            if (replace)
            {
                try
                {
                    document.replace(cmd.offset, cmd.length, re);
                }
                catch (BadLocationException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void smartIndentAfterNewLine(IDocument doc, DocumentCommand cmd)
    {
        int docLength = doc.getLength();
        if ((cmd.offset == -1) || (docLength == 0)) { return; }
        String f = getVeloIdentifier(doc, cmd.offset - 4, cmd.offset + 1);
        if ((f != null) && f.equals("#end"))
        {
            smartIndentOnVeloTagClose(doc, cmd);
            return;
        }
        try
        {
            int p = (cmd.offset != docLength) ? cmd.offset : (cmd.offset - 1);
            int line = doc.getLineOfOffset(p);
            int start = doc.getLineOffset(line);
            int whiteend = findEndOfWhiteSpace(doc, cmd.offset, start + doc.getLineLength(line));
            int indent;
            if (isInsidePreformattedPartitions(p, doc))
            {
                for (; line >= 0; line--)
                {
                    if (!EditorsUtil.isBlankLine(doc, line))
                    {
                        break;
                    }
                }
                if (line >= 0)
                {
                    indent = getIndentOfLine(doc, line);
                } else
                {
                    indent = 0;
                }
                if (whiteend > 0)
                {
                    cmd.length += (whiteend - cmd.offset);
                }
                cmd.text += getIndentString(indent).toString();
                return;
            }
            if (EditorsUtil.isInsidePartition(p, new String[] {IEditorConfiguration.MULTI_LINE_COMMENT, IEditorConfiguration.COMMENT_PARTITION, IEditorConfiguration.TAG_PARTITION }, doc))
            {
                IDocumentPartitioner partitioner = doc.getDocumentPartitioner();
                int matchoffset = partitioner.getPartition(p).getOffset();
                indent = getIndentOfLine(doc, doc.getLineOfOffset(matchoffset));
                indent = indent(indent, 1);
                indent = unIndentOnTagEnd(indent, cmd.offset, doc);
                if (whiteend > 0)
                {
                    cmd.length += (whiteend - cmd.offset);
                }
                cmd.text += getIndentString(indent).toString();
                return;
            }
            int i = 0;
            if ((i = isEndTag(cmd.offset, doc)) > 0)
            {
                int matchoffset = 0;
                String _f = getVeloIdentifier(doc, i, i + 5);
                if ((_f != null) && _f.equalsIgnoreCase("#end"))
                {
                    matchoffset = findStartVeloBefore(cmd.offset, doc);
                } else
                {
                    matchoffset = findMatchingOpenTagBefore(cmd.offset, doc);
                }
                if (matchoffset < 0) { return; }
                indent = getIndentOfLine(doc, doc.getLineOfOffset(matchoffset));
                if (whiteend > 0)
                {
                    cmd.length += (whiteend - cmd.offset);
                }
                cmd.text += getIndentString(indent).toString();
                return;
            }
            line = getNonBlankLineAbove(line + 1, doc);
            if (line >= 0)
            {
                indent = getIndentOfLine(doc, line);
            } else
            {
                indent = 0;
            }
            start = doc.getLineOffset(line);
            int n = getTagCount(doc, start, cmd.offset, true);
            if (n > 0)
            {
                indent = indent(indent, n);
            } else if (n < 0)
            {
                indent = unIndent(indent, -n);
            }
            if (whiteend > 0)
            {
                cmd.length += (whiteend - cmd.offset);
            }
            cmd.text += getIndentString(indent).toString();
        }
        catch (BadLocationException e)
        {
        }
    }

    public void smartIndentOnTagClose(IDocument doc, DocumentCommand cmd)
    {
        if ((cmd.offset == -1) || (doc.getLength() == 0)) { return; }
        if (isInsidePreformattedPartitions(cmd.offset, doc)) { return; }
        try
        {
            int p = (cmd.offset != doc.getLength()) ? cmd.offset : (cmd.offset - 1);
            int line = doc.getLineOfOffset(p);
            int start = doc.getLineOffset(line);
            int whiteend = findEndOfWhiteSpace(doc, start, cmd.offset);
            int matchoffset;
            if (((cmd.offset - 1) == whiteend) && (doc.getChar(cmd.offset - 1) == '/'))
            {
                matchoffset = findMatchingOpenTagBefore(whiteend, doc);
                if ((matchoffset < 0) || (matchoffset == whiteend))
                {
                    return;
                } else
                {
                    sameIndentAs(matchoffset, line, doc, cmd);
                    return;
                }
            }
            char c;
            if (((cmd.offset - 2) == whiteend) && (cmd.offset > 1) && ((c = doc.getChar(cmd.offset - 1)) == doc.getChar(cmd.offset - 2)) && (c == '-'))
            {
                IDocumentPartitioner partitioner = doc.getDocumentPartitioner();
                matchoffset = partitioner.getPartition(p).getOffset();
                if ((matchoffset < 0) || (matchoffset == whiteend))
                {
                    return;
                } else
                {
                    sameIndentAs(matchoffset, line, doc, cmd);
                    return;
                }
            }
            matchoffset = findOpenBracketBefore(cmd.offset, doc);
            if (matchoffset != whiteend) { return; }
            if (doc.getChar(matchoffset + 1) == '/')
            {
                matchoffset = findMatchingOpenTagBefore(whiteend, doc);
                if (matchoffset < 0)
                {
                    return;
                } else
                {
                    sameIndentAs(matchoffset, line, doc, cmd);
                    return;
                }
            } else
            {
                return;
            }
        }
        catch (BadLocationException e)
        {
        }
    }

    public void smartIndentOnVeloTagClose(IDocument doc, DocumentCommand cmd)
    {
        if ((cmd.offset == -1) || (doc.getLength() == 0)) { return; }
        String f = getVeloIdentifier(doc, cmd.offset - 4, cmd.offset + 1);
        if ((f == null) || !f.equals("#end")) { return; }
        if (isInsidePreformattedPartitions(cmd.offset, doc)) { return; }
        try
        {
            int p = (cmd.offset != doc.getLength()) ? cmd.offset : (cmd.offset - 1);
            int line = doc.getLineOfOffset(p);
            int start = doc.getLineOffset(line);
            int whiteend = findEndOfWhiteSpace(doc, start, cmd.offset);
            int matchoffset;
            if (((cmd.offset - 1) == whiteend) && (doc.getChar(cmd.offset - 1) == '/'))
            {
                matchoffset = findMatchingOpenTagBefore(whiteend, doc);
                if ((matchoffset < 0) || (matchoffset == whiteend))
                {
                    return;
                } else
                {
                    sameIndentAs(matchoffset, line, doc, cmd);
                    return;
                }
            }
            char c;
            if (((cmd.offset - 2) == whiteend) && (cmd.offset > 1) && ((c = doc.getChar(cmd.offset - 1)) == doc.getChar(cmd.offset - 2)) && (c == '-'))
            {
                IDocumentPartitioner partitioner = doc.getDocumentPartitioner();
                matchoffset = partitioner.getPartition(p).getOffset();
                if ((matchoffset < 0) || (matchoffset == whiteend))
                {
                    return;
                } else
                {
                    sameIndentAs(matchoffset, line, doc, cmd);
                    return;
                }
            }
            matchoffset = findOpenVeloBefore(cmd.offset - 1, doc);
            if (matchoffset != whiteend) { return; }
            matchoffset = findStartVeloBefore(whiteend, doc);
            if (matchoffset < 0)
            {
                return;
            } else
            {
                sameIndentAs(matchoffset, line, doc, cmd);
                int indent = getIndentOfLine(doc, doc.getLineOfOffset(matchoffset));
                cmd.text += getIndentString(indent).toString();
                return;
            }
        }
        catch (BadLocationException e)
        {
        }
    }

    /**
     * @param i
     * @param doc
     * @return
     */
    private int findOpenVeloBefore(int end, IDocument document)
    {
        try
        {
            for (end--; end >= 0; end--)
            {
                char c = document.getChar(end);
                if (c == '#') { return end; }
                if ((c == '"') || (c == '\''))
                {
                    end = getStringStart(document, end, c);
                }
            }
        }
        catch (BadLocationException badlocationexception)
        {
        }
        return -1;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param end
     *            DOCUMENT ME!
     * @param document
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public static int findStartVeloBefore(int end, IDocument document)
    {
        try
        {
            int moveNext = 0;
            for (end--; end >= 0; end--)
            {
                char c = document.getChar(end);
                if (c == '#')
                {
                    String id = getVeloIdentifier(document, end, end + 9);
                    if ((moveNext == 0) && (id.equalsIgnoreCase("#if") || id.equalsIgnoreCase("#foreach") || id.equalsIgnoreCase("#macro")))
                    {
                        return end;
                    } else if (id.equalsIgnoreCase("#end"))
                    {
                        ++moveNext;
                    }
                    if ((moveNext > 0) && (id.equalsIgnoreCase("#if") || id.equalsIgnoreCase("#foreach") || id.equalsIgnoreCase("#macro")))
                    {
                        --moveNext;
                    }
                }
                if ((c == '"') || (c == '\''))
                {
                    end = getStringStart(document, end, c);
                }
            }
        }
        catch (BadLocationException badlocationexception)
        {
        }
        return -1;
    }

    public void smartIndentOnTab(IDocument doc, DocumentCommand cmd)
    {
        int originalCmdOffset = cmd.offset;
        if ((originalCmdOffset == -1) || (doc.getLength() == 0)) { return; }
        if (isInsidePreformattedPartitions(originalCmdOffset, doc)) { return; }
        try
        {
            int len = doc.getLength();
            int p = (originalCmdOffset != len) ? originalCmdOffset : (originalCmdOffset - 1);
            int line = doc.getLineOfOffset(p);
            int start = doc.getLineOffset(line);
            int whiteend = findEndOfWhiteSpace(doc, start, start + doc.getLineLength(line));
            String f = getVeloIdentifier(doc, whiteend, whiteend + 5);
            if ((f != null) && f.equals("#end"))
            {
                smartindentonVeloTab(doc, cmd);
                return;
            }
            if (originalCmdOffset > whiteend) { return; }
            int indent;
            int i = 0;
            if ((i = isEndTag(whiteend, doc)) > 0)
            {
                int matchoffset = findMatchingOpenTagBefore(i, doc);
                if (matchoffset < 0) { return; }
                indent = getIndentOfLine(doc, doc.getLineOfOffset(matchoffset));
            } else
            {
                indent = 0;
                line = getNonBlankLineAbove(line, doc);
                // here we have to handle comments
                if (line >= 0)
                {
                    indent = getIndentOfLine(doc, line);
                    indent = unIndentOnTagEnd(indent, whiteend, doc);
                    int n = getTagCount(doc, doc.getLineOffset(line), start, true);
                    if (n > 0)
                    {
                        indent = indent(indent, n);
                    } else if (n < 0)
                    {
                        indent = unIndent(indent, -n);
                    }
                }
            }
            if (indent == indentWidthOf(doc.get(start, whiteend - start), fTabWidth))
            {
                indent = indent(indent, 1);
            }
            cmd.offset = start;
            cmd.length = whiteend - start;
            cmd.text = getIndentString(indent).toString();
        }
        catch (BadLocationException e)
        {
        }
    }

    public void smartIndentOnBackspace(IDocument doc, DocumentCommand cmd)
    {
        if (isUsingTab) { return; }
        if ((cmd.offset == -1) || (doc.getLength() == 0)) { return; }
        try
        {
            int len = doc.getLength();
            int p = (cmd.offset != len) ? cmd.offset : (cmd.offset - 1);
            int line = doc.getLineOfOffset(p);
            int start = doc.getLineOffset(line);
            int whiteend = findEndOfWhiteSpace(doc, start, start + doc.getLineLength(line));
            if ((cmd.offset + 1) != whiteend) { return; }
            int indent = indentWidthOf(doc.get(start, whiteend - start), fTabWidth);
            indent = unIndent(indent, 1);
            cmd.offset = start;
            cmd.length = whiteend - start;
            cmd.text = getIndentString(indent).toString();
        }
        catch (BadLocationException e)
        {
        }
    }

    public boolean isStartOfLine(int offset, int line, IDocument document)
    {
        try
        {
            if (document.getLineOfOffset(offset) != line) { return false; }
            int start = document.getLineOffset(line);
            return findEndOfWhiteSpace(document, start, offset) == offset;
        }
        catch (BadLocationException e)
        {
            return false;
        }
    }

    public void sameIndentAs(int matchoffset, int line, IDocument document, DocumentCommand cmd)
    {
        try
        {
            int indent = getIndentOfLine(document, document.getLineOfOffset(matchoffset));
            StringBuffer buf = getIndentString(indent);
            int start = (line < 0) ? 0 : document.getLineOffset(line);
            int whiteend = findEndOfWhiteSpace(document, start, cmd.offset);
            buf.append(document.get(whiteend, cmd.offset - whiteend));
            buf.append(cmd.text);
            cmd.length += (cmd.offset - start);
            cmd.offset = start;
            cmd.text = buf.toString();
        }
        catch (BadLocationException e)
        {
        }
    }

    public int findOpenBracketBefore(int end, IDocument document)
    {
        try
        {
            for (end--; end >= 0; end--)
            {
                char c = document.getChar(end);
                if (c == '<') { return end; }
                if ((c == '"') || (c == '\''))
                {
                    end = getStringStart(document, end, c);
                }
            }
        }
        catch (BadLocationException badlocationexception)
        {
        }
        return -1;
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
                    line = doc.getLineOfOffset(partitioner.getPartition(start).getOffset()) +1;
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

    public int getIndentOfLine(IDocument d, int line) throws BadLocationException
    {
        if (line >= 0)
        {
            int start = d.getLineOffset(line);
            int end = (start + d.getLineLength(line)) - 1;
            int whiteend = findEndOfWhiteSpace(d, start, end);
            return indentWidthOf(d.get(start, whiteend - start), fTabWidth);
        } else
        {
            return 0;
        }
    }

    public StringBuffer getIndentString(int indent)
    {
        StringBuffer ret = new StringBuffer();
        int n = indent / fTabWidth;
        for (int i = 0; i < n; i++)
        {
            ret.append(fTab);
        }
        n = indent - (n * fTabWidth);
        for (int i = 0; i < n; i++)
        {
            ret.append(' ');
        }
        return ret;
    }

    public int indent(int indent, int n)
    {
        return ((indent / fTabWidth) + n) * fTabWidth;
    }

    public int unIndent(int indent, int n)
    {
        return ((((indent - 1) / fTabWidth) - n) + 1) * fTabWidth;
    }

    public int unIndentOnTagEnd(int indent, int offset, IDocument doc)
    {
        if (isEndTag(offset, doc) > 0)
        {
            return unIndent(indent, 1);
        } else
        {
            return indent;
        }
    }

    public int isEndTag(int offset, IDocument doc)
    {
        int len = doc.getLength();
        if ((offset + 1) >= len) { return 0; }
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
            String f = getVeloIdentifier(doc, i, i + 5);
            if (((c == '<') && (cc == '/')) || ((c == '/') && (cc == '>')) || ((c == '-') && (cc == '-') && (ccc == '>')))
            {
                return i;
            } else if ((f != null) && f.equalsIgnoreCase("#end")) { return i; }
        }
        catch (BadLocationException e)
        {
        }
        return 0;
    }

    public int getStringEnd(IDocument d, int pos, int end, char ch) throws BadLocationException
    {
        while (pos < end)
        {
            char curr = d.getChar(pos);
            pos++;
            if (curr == '\\')
            {
                pos++;
            } else if (curr == ch) { return pos; }
        }
        return end;
    }

    public static int getStringStart(IDocument document, int end, char quote)
    {
        try
        {
            for (end--; end >= 0; end--)
            {
                char c = document.getChar(end);
                if (c == quote)
                {
                    if ((end == 0) || (document.getChar(end - 1) != '\\')) { return end; }
                    end--;
                }
            }
        }
        catch (BadLocationException e)
        {
        }
        return -1;
    }

    public boolean isBlankLine(IDocument d, int line)
    {
        try
        {
            int start = d.getLineOffset(line);
            int len = d.getLineLength(line);
            for (int i = 0; i < len; i++)
            {
                if (!Character.isWhitespace(d.getChar(start + i))) { return false; }
            }
        }
        catch (BadLocationException badlocationexception)
        {
        }
        return true;
    }

    public boolean startsWithDelimiter(IDocument d, int line)
    {
        String[] delimiters = d.getLegalLineDelimiters();
        try
        {
            int start = d.getLineOffset(line);
            int len = d.getLineLength(line);
            for (int i = 0; i < delimiters.length; i++)
            {
                String delimiter = delimiters[i];
                if (len >= delimiter.length())
                {
                    boolean found = true;
                    for (int k = 0; k < delimiter.length(); k++)
                    {
                        if (d.getChar(start + k) == delimiter.charAt(k))
                        {
                            continue;
                        }
                        found = false;
                        break;
                    }
                    if (found) { return true; }
                }
            }
        }
        catch (BadLocationException badlocationexception)
        {
        }
        return false;
    }

    public boolean endsWithDelimiter(IDocument d, String txt)
    {
        String[] delimiters = d.getLegalLineDelimiters();
        for (int i = 0; i < delimiters.length; i++)
        {
            if (txt.endsWith(delimiters[i])) { return true; }
        }
        return false;
    }

    public boolean isInsideEscapedPartitions(int offset, IDocument doc)
    {
        if (EditorsUtil.isInsidePartition(offset, VelocityConfiguration.CDATA_PARTITIONS, true, doc))
        {
            return true;
        } else
        {
            return EditorsUtil.isInsidePartition(offset, VelocityConfiguration.ESCAPED_PARTITIONS, doc);
        }
    }

    public boolean isInsidePreformattedPartitions(int offset, IDocument doc)
    {
        if (EditorsUtil.isInsidePartition(offset, VelocityConfiguration.CDATA_PARTITIONS, true, doc))
        {
            return true;
        } else
        {
            return EditorsUtil.isInsidePartition(offset, VelocityConfiguration.PREFORMATTED_PARTITIONS, doc);
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param end
     *            DOCUMENT ME!
     * @param doc
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public static int findMatchingOpenTagBefore(int end, IDocument doc)
    {
        int level = 1;
        String idorg = getIdentifier(doc, end + 2, doc.getLength());
     
        try
        {
            char prev = '\0';
            for (end--; end >= 0; end--)
            {
                if (EditorsUtil.isInsidePartition(end, VelocityConfiguration.CDATA_PARTITIONS, true, doc))
                {
                    end = doc.getPartition(end).getOffset() - 1;
                } else if (EditorsUtil.isInsidePartition(end, VelocityConfiguration.ESCAPED_PARTITIONS, doc))
                {
                    end = doc.getPartition(end).getOffset()-1;
                }
                char c = doc.getChar(end);
                if(c=='/' && prev=='>') level++;
                if (c == '<')
                {
                    if (prev == '/')
                    {
                        level++;
                    } else if ((prev != '!') && (prev != '?') && (prev != '%') && (prev != '#'))
                    {
                        String id = getIdentifier(doc, end + 1, doc.getLength());
//                        if (!fEMPTY_TAG_SET.contains(id.toUpperCase()) && !id.equals(""))
//                        {
                            level--;
//                        }
                        if ((level == 0) && idorg.equalsIgnoreCase(id))
                        // if (level == 0 )
                        { return end; }
                    }
                } else if ((c == '"') || (c == '\''))
                {
                    end = getStringStart(doc, end, c);
                }
// TODO               if(c=='/' && prev=='>') level++;
                prev = c;
            }
        }
        catch (BadLocationException e)
        {
        }
        return -1;
    }

    public int getTagCount(IDocument document, int start, int end, boolean skip) throws BadLocationException
    {
        int bracketcount = 0;
        while (start < end)
        {
            if (EditorsUtil.isInsidePartition(start, VelocityConfiguration.ESCAPED_PARTITIONS, true, document))
            {
                ITypedRegion partition = document.getPartition(start);
                start = partition.getOffset() + partition.getLength();
            }
            char c = document.getChar(start);
            start++;
            switch (c)
            {
                default:
                    break;
                // case 35: // '#'
                // ////////
                case 35: // '#'
                {
                    if (start >= end)
                    {
                        break;
                    }
                    char c1 = document.getChar(start);
                    // if ((c1 == '!') || (c1 == '?') || (c1 == '%') || (c1 ==
                    // '#'))
                    // {
                    // skip = false;
                    //
                    // break;
                    // }
                    if (c1 != '#')
                    {
                        String id = getIdentifier(document, start, end + 1).toLowerCase();
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
                // ///////
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
                        String id = getIdentifier(document, start, end).toUpperCase();
//                        if (!fEMPTY_TAG_SET.contains(id))
//                        {
                            bracketcount++;
                            skip = false;
//                        }
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

    // public int isEndTag(int offset, IDocument doc) {
    // return this.isEndTag(offset, doc);
    // }
    public static String getVeloIdentifier(IDocument document, int start, int end)
    {
        start = skipWhitespace(document, start, end);
        if (start >= end) { return ""; }
        StringBuffer ret = new StringBuffer();
        try
        {
            char c = document.getChar(start++);
            if (isVeloIdentifierStart(c))
            {
                while (start < end)
                {
                    ret.append(c);
                    c = document.getChar(start++);
                    if (!isIdentifierChar(c))
                    {
                        break;
                    }
                }
            }
        }
        catch (BadLocationException e)
        {
        }
        return ret.toString();
    }

    public static boolean isVeloIdentifierStart(char c)
    {
        return c == '#';
    }

    public static String getIdentifier(IDocument document, int start, int end)
    {
        start = skipWhitespace(document, start, end);
        if (start >= end) { return ""; }
        StringBuffer ret = new StringBuffer();
        try
        {
            char c = document.getChar(start++);
            if (isIdentifierStart(c))
            {
                while (start < end)
                {
                    ret.append(c);
                    c = document.getChar(start++);
                    if (!isIdentifierChar(c))
                    {
                        break;
                    }
                }
            }
        }
        catch (BadLocationException e)
        {
        }
        return ret.toString();
    }

    public static int skipWhitespace(IDocument document, int start, int end)
    {
        try
        {
            for (char c = document.getChar(start); (start < end) && Character.isWhitespace(c); c = document.getChar(++start))
            {
            }
        }
        catch (BadLocationException e)
        {
        }
        return start;
    }

    public static boolean isIdentifierStart(char c)
    {
        return Character.isLetter(c);
    }

    public static boolean isIdentifierChar(char c)
    {
	return Character.isLetterOrDigit(c) || c == ':' || c == '.' ;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param doc
     *            DOCUMENT ME!
     * @param cmd
     *            DOCUMENT ME!
     */
    public void smartindentonVeloTab(IDocument doc, DocumentCommand cmd)
    {
        try
        {
            int len = doc.getLength();
            int p = (cmd.offset != len) ? cmd.offset : (cmd.offset - 1);
            int line = doc.getLineOfOffset(p);
            int start = doc.getLineOffset(line);
            int whiteend = findEndOfWhiteSpace(doc, start, start + doc.getLineLength(line));
            int indent;
            if ((isEndTag(whiteend, doc)) > 0)
            {
                int matchoffset = findStartVeloBefore(start, doc);
                if (matchoffset < 0) { return; }
                indent = getIndentOfLine(doc, doc.getLineOfOffset(matchoffset));
            } else
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
                    } else if (n < 0)
                    {
                        indent = unIndent(indent, -n);
                    }
                }
            }
            if (indent == indentWidthOf(doc.get(start, whiteend - start), fTabWidth))
            {
                indent = indent(indent, 1);
            }
            cmd.offset = start;
            cmd.length = whiteend - start;
            cmd.text = getIndentString(indent).toString();
        }
        catch (BadLocationException e)
        {
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param str
     *            DOCUMENT ME!
     * @param tabwidth
     *            DOCUMENT ME!
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
     *            DOCUMENT ME!
     * @param start
     *            DOCUMENT ME!
     * @param tabwidth
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public static int indentWidthOf(String str, int start, int tabwidth)
    {
        int ret = 0;
        for (int len = str.length(); start < len; start++)
        {
            char c = str.charAt(start);
            if (c == '\t')
            {
                ret = ((ret / tabwidth) + 1) * tabwidth;
                continue;
            }
            if ((c == ' ') || (c == '\240'))
            {
                ret++;
                continue;
            }
            if ((c != '\n') && (c != '\r'))
            {
                break;
            }
            ret = 0;
        }
        return ret;
    }
}
