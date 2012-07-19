package com.liferay.ide.velocity.editor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IWordDetector;

/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 8 $
 * @author <a href="mailto:akmal.sarhan@gmail.com">Akmal Sarhan </a>
 */
public class EditorsUtil
{

    private static final char[]     CR                = { '\r' };
    private static final char[]     LF                = { '\n' };
    private static final char[]     CRLF              = { '\r', '\n' };
    private static final char[]     EMPTY             = {};
    /**
     * Characters used for escape operations
     */
    private static final String[][] HTML_ESCAPE_CHARS = { { "&lt;", "<" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&gt;", ">" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&amp;", "&" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&quot;", "\"" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&agrave;", "\u00e0" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&Agrave;", "\u00c0" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&acirc;", "\u00e2" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&auml;", "\u00e4" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&Auml;", "\u00c4" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&Acirc;", "\u00c2" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&aring;", "\u00e5" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&Aring;", "\u00c5" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&aelig;", "\u00e6" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&AElig;", "\u00c6" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&ccedil;", "\u00e7" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&Ccedil;", "\u00c7" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&eacute;", "\u00e9" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&Eacute;", "\u00c9" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&aacute;", "\u00e1" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&Aacute;", "\u00c1" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&egrave;", "\u00e8" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&Egrave;", "\u00c8" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&ecirc;", "\u00ea" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&Ecirc;", "\u00ca" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&euml;", "\u00eb" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&Euml;", "\u00cb" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&iuml;", "\u00ef" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&Iuml;", "\u00cf" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&iacute;", "\u00ed" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&Iacute;", "\u00cd" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&atilde;", "\u00e3" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&Atilde;", "\u00c3" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&otilde;", "\u00f5" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&Otilde;", "\u00d5" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&oacute;", "\u00f3" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&Oacute;", "\u00d3" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&ocirc;", "\u00f4" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&Ocirc;", "\u00d4" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&ouml;", "\u00f6" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&Ouml;", "\u00d6" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&oslash;", "\u00f8" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&Oslash;", "\u00d8" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&szlig;", "\u00df" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&ugrave;", "\u00f9" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&Ugrave;", "\u00d9" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&uacute;", "\u00fa" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&Uacute;", "\u00da" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&ucirc;", "\u00fb" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&Ucirc;", "\u00db" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&uuml;", "\u00fc" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&Uuml;", "\u00dc" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&nbsp;", " " }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&reg;", "\u00AE" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&copy;", "\u00A9" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&euro;", "\u20A0" }, { //$NON-NLS-1$ //$NON-NLS-2$
                                                      "&#8364;", "\u20AC" } //$NON-NLS-1$ //$NON-NLS-2$
                                                      };

    /**
     * Get html entity for escape character
     * @return null, if no entity found for given character
     */
    public static final String getEntityForChar(char ch)
    {
        switch (ch)
        {
            case '<':
                return "&lt;"; //$NON-NLS-1$
            case '>':
                return "&gt;"; //$NON-NLS-1$
            case '&':
                return "&amp;"; //$NON-NLS-1$
            case '"':
                return "&quot;"; //$NON-NLS-1$
            case '\u00e0':
                return "&agrave;"; //$NON-NLS-1$
            case '\u00e1':
                return "&aacute;"; //$NON-NLS-1$
            case '\u00c0':
                return "&Agrave;"; //$NON-NLS-1$
            case '\u00c1':
                return "&Aacute;"; //$NON-NLS-1$
            case '\u00e2':
                return "&acirc;"; //$NON-NLS-1$
            case '\u00c2':
                return "&Acirc;"; //$NON-NLS-1$
            case '\u00e4':
                return "&auml;"; //$NON-NLS-1$
            case '\u00c4':
                return "&Auml;"; //$NON-NLS-1$
            case '\u00e5':
                return "&aring;"; //$NON-NLS-1$
            case '\u00c5':
                return "&Aring;"; //$NON-NLS-1$
            case '\u00e3':
                return "&atilde;"; //$NON-NLS-1$
            case '\u00c3':
                return "&Atilde;"; //$NON-NLS-1$
            case '\u00e6':
                return "&aelig;"; //$NON-NLS-1$
            case '\u00c6':
                return "&AElig;"; //$NON-NLS-1$
            case '\u00e7':
                return "&ccedil;"; //$NON-NLS-1$
            case '\u00c7':
                return "&Ccedil;"; //$NON-NLS-1$
            case '\u00e9':
                return "&eacute;"; //$NON-NLS-1$
            case '\u00c9':
                return "&Eacute;"; //$NON-NLS-1$
            case '\u00e8':
                return "&egrave;"; //$NON-NLS-1$
            case '\u00c8':
                return "&Egrave;"; //$NON-NLS-1$
            case '\u00ea':
                return "&ecirc;"; //$NON-NLS-1$
            case '\u00ca':
                return "&Ecirc;"; //$NON-NLS-1$
            case '\u00eb':
                return "&euml;"; //$NON-NLS-1$
            case '\u00cb':
                return "&Euml;"; //$NON-NLS-1$
            case '\u00ed':
                return "&iacute;"; //$NON-NLS-1$
            case '\u00cd':
                return "&Iacute;"; //$NON-NLS-1$
            case '\u00ef':
                return "&iuml;"; //$NON-NLS-1$
            case '\u00cf':
                return "&Iuml;"; //$NON-NLS-1$
            case '\u00f5':
                return "&otilde;"; //$NON-NLS-1$
            case '\u00d5':
                return "&Otilde;"; //$NON-NLS-1$
            case '\u00f3':
                return "&oacute;"; //$NON-NLS-1$
            case '\u00f4':
                return "&ocirc;"; //$NON-NLS-1$
            case '\u00d3':
                return "&Oacute;"; //$NON-NLS-1$
            case '\u00d4':
                return "&Ocirc;"; //$NON-NLS-1$
            case '\u00f6':
                return "&ouml;"; //$NON-NLS-1$
            case '\u00d6':
                return "&Ouml;"; //$NON-NLS-1$
            case '\u00f8':
                return "&oslash;"; //$NON-NLS-1$
            case '\u00d8':
                return "&Oslash;"; //$NON-NLS-1$
            case '\u00df':
                return "&szlig;"; //$NON-NLS-1$
            case '\u00f9':
                return "&ugrave;"; //$NON-NLS-1$
            case '\u00d9':
                return "&Ugrave;"; //$NON-NLS-1$
            case '\u00fa':
                return "&uacute;"; //$NON-NLS-1$
            case '\u00da':
                return "&Uacute;"; //$NON-NLS-1$
            case '\u00fb':
                return "&ucirc;"; //$NON-NLS-1$
            case '\u00db':
                return "&Ucirc;"; //$NON-NLS-1$
            case '\u00fc':
                return "&uuml;"; //$NON-NLS-1$
            case '\u00dc':
                return "&Uuml;"; //$NON-NLS-1$
            case '\u00AE':
                return "&reg;"; //$NON-NLS-1$
            case '\u00A9':
                return "&copy;"; //$NON-NLS-1$
            case '\u20A0':
                return "&euro;"; //$NON-NLS-1$
            case '\u20AC':
                return "&#8364;"; //$NON-NLS-1$
            // case '' : return "&euro;"; //$NON-NLS-1$
            // case '\u20AC': return "&#x20AC;"; // euro
            // be carefull with this one (non-breaking white space)
            // case ' ' : return "&nbsp;"; //$NON-NLS-1$
            default:
            {
                int ci = 0xffff & ch;
                if (ci < 160)
                {
                    // nothing special only 7 Bit
                    return null;
                }
                // Not 7 Bit use the unicode system
                return "&#" + ci + ";"; //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
    }

    /**
     * 
     * @param s
     *            string to be modified
     * @return string with escape characters, changed to html entities
     */
    public static final String escapeText(String s)
    {
        if (s == null) { return null; }
        StringBuffer sb = new StringBuffer();
        int n = s.length();
        char c;
        String entity;
        for (int i = 0; i < n; i++)
        {
            c = s.charAt(i);
            entity = getEntityForChar(c);
            if (entity != null)
            {
                sb.append(entity);
            } else
            {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 
     * @param s
     *            string to unescape
     * @return new string with html entities changed to escape characters
     */
    public static final String unescapeText(String s)
    {
        int i, j, k;
        int arraySize = HTML_ESCAPE_CHARS.length;
        if (s != null && (i = s.indexOf("&")) > -1) { //$NON-NLS-1$
            j = s.indexOf(";", i); //$NON-NLS-1$
            if (j > i)
            {
                String temp = s.substring(i, j + 1);
                // search in escape[][] if temp is there
                k = 0;
                while (k < arraySize)
                {
                    if (HTML_ESCAPE_CHARS[k][0].equals(temp))
                    {
                        break;
                    }
                    k++;
                }
                // now we found html escape character
                if (k < arraySize)
                {
                    // replace it to ASCII
                    s = new StringBuffer(s.substring(0, i)).append(HTML_ESCAPE_CHARS[k][1]).append(s.substring(j + 1)).toString();
                    return unescapeText(s); // recursive call
                } else if (k == arraySize)
                {
                    s = new StringBuffer(s.substring(0, i)).append("&") //$NON-NLS-1$
                            .append(unescapeText(s.substring(i + 1))).toString();
                    return s;
                }
            }
        }
        return s;
    }

    public EditorsUtil()
    {
    }

    /**
     * DOCUMENT ME!
     * 
     * @param d
     *            DOCUMENT ME!
     * @param txt
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public static boolean isLineDelimiter(IDocument d, String txt)
    {
        String[] delimiters = d.getLegalLineDelimiters();
        for (int i = 0; i < delimiters.length; i++)
        {
            if (txt.equals(delimiters[i])) { return true; }
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param d
     *            DOCUMENT ME!
     * @param txt
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public static boolean endsWithDelimiter(IDocument d, String txt)
    {
        String[] delimiters = d.getLegalLineDelimiters();
        for (int i = 0; i < delimiters.length; i++)
        {
            if (txt.equals(delimiters[i])) { return true; }
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param document
     *            DOCUMENT ME!
     * @param offset
     *            DOCUMENT ME!
     * @param tabwidth
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public static int getIndentLength(IDocument document, int offset, int tabwidth)
    {
        int ret = 0;
        try
        {
            int lineNr = document.getLineOfOffset(offset);
            int start = document.getLineOffset(lineNr);
            int len = document.getLineLength(lineNr);
            for (int i = start; i < (start + len);)
            {
                char c = document.getChar(start);
                if (c == '\t')
                {
                    ret += (tabwidth - (ret % tabwidth));
                } else if (c == ' ')
                {
                    ret++;
                } else
                {
                    return ret;
                }
            }
            return ret;
        }
        catch (BadLocationException badlocationexception)
        {
            return ret;
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param scanner
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public static int skipWhitespace(ICharacterScanner scanner)
    {
        for (int c = scanner.read(); c != -1; c = scanner.read())
        {
            if (!Character.isWhitespace((char) c)) { return c; }
        }
        return -1;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param delim
     *            DOCUMENT ME!
     * @param scanner
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public static boolean skipString(int delim, ICharacterScanner scanner)
    {
        for (int c = scanner.read(); c != -1; c = scanner.read())
        {
            if (c == delim) { return true; }
            if ((c == 92) && (scanner.read() == -1))
            {
                scanner.unread();
            }
        }
        scanner.unread();
        return false;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param detector
     *            DOCUMENT ME!
     * @param scanner
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public static String getWord(IWordDetector detector, ICharacterScanner scanner)
    {
        StringBuffer ret = new StringBuffer();
        int c = scanner.read();
        if (detector.isWordStart((char) c))
        {
            ret.append((char) c);
            for (c = scanner.read(); c != -1; c = scanner.read())
            {
                if (!detector.isWordPart((char) c))
                {
                    break;
                }
                ret.append((char) c);
            }
        }
        scanner.unread();
        return ret.toString();
    }

    /**
     * DOCUMENT ME!
     * 
     * @param d
     *            DOCUMENT ME!
     * @param line
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public static boolean isBlankLine(IDocument d, int line)
    {
        try
        {
            int start = d.getLineOffset(line);
            int end;
            for (end = start + d.getLineLength(line); start < end; start++)
            {
                if (!Character.isWhitespace(d.getChar(start))) { return false; }
            }
            return start >= end;
        }
        catch (BadLocationException badlocationexception)
        {
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param offset
     *            DOCUMENT ME!
     * @param doc
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public static boolean isEscapedPartition(int offset, IDocument doc)
    {
        try
        {
            ITypedRegion partition = doc.getPartition(offset);
            String type = partition.getType();
            if ((offset != partition.getOffset())
                    && (type.equals("__javadoc_partition") || type.equals("__comment_partition") || type.equals("__comment1_partition") || type.equals("__string_partition"))) { return true; }
        }
        catch (BadLocationException e)
        {
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param offset
     *            DOCUMENT ME!
     * @param partitions
     *            DOCUMENT ME!
     * @param doc
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public static boolean isInsidePartition(int offset, String[] partitions, IDocument doc)
    {
        return isInsidePartition(offset, partitions, false, doc);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param offset
     *            DOCUMENT ME!
     * @param partitions
     *            DOCUMENT ME!
     * @param include_start
     *            DOCUMENT ME!
     * @param doc
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public static boolean isInsidePartition(int offset, String[] partitions, boolean include_start, IDocument doc)
    {
        try
        {
            ITypedRegion partition = doc.getPartition(offset);
            if (include_start || (offset != partition.getOffset()))
            {
                String type = partition.getType();
                
                for (int i = 0; i < partitions.length; i++)
                {
                    if (type.equals(partitions[i])) { return true; }
                }
            }
        }
        catch (BadLocationException e)
        {
        }
        return false;
    }

    private static char[] getLineEnd(StringBuffer line)
    {
        if (line == null) { return EMPTY; }
        int lastIdx = line.length() - 1;
        if (lastIdx < 0) { return EMPTY; }
        char last = line.charAt(lastIdx);
        if (last == '\n')
        {
            if (lastIdx > 0)
            {
                if (line.charAt(lastIdx - 1) == '\r') { return CRLF; // windows
                }
            }
            return LF; // unix
        } else if (last == '\r')
        {
            return CR; // mac
        } else
        {
            return EMPTY;
        }
    }

    private static boolean removeTrailingSpace(StringBuffer line)
    {
        boolean changed = false;
        char lastChar;
        int lineLength = line.length();
        int lastCharsLength = getLineEnd(line).length;
        int lastIdx = lineLength - lastCharsLength - 1;
        while (lastIdx >= 0)
        {
            lastChar = line.charAt(lastIdx);
            if (lastChar != ' ' && lastChar != '\t')
            {
                break;
            }
            lastIdx--;
        }
        if (lastIdx != lineLength - lastCharsLength - 1)
        {
            line.delete(lastIdx + 1, lineLength - lastCharsLength);
            changed = true;
        }
        return changed;
    }

    public static boolean convertSpacesToTabs(StringBuffer line, int tabWidth, boolean removeTrailing)
    {
        char lastChar;
        boolean changed = false;
        if (removeTrailing)
        {
            changed = removeTrailingSpace(line);
        }
        int lineLength = line.length();
        int spacesCount = 0;
        int tabsCount = 0;
        int i = 0;
        for (; i < lineLength; i++)
        {
            lastChar = line.charAt(i);
            if (lastChar == ' ')
            {
                changed = true;
                spacesCount++;
            } else if (lastChar == '\t')
            {
                tabsCount++;
            } else
            {
                break;
            }
        }
        if (spacesCount > 0)
        {
            tabsCount += spacesCount / tabWidth;
            // modulo rest
            int extraSpaces = spacesCount % tabWidth;
            if (i - extraSpaces <= 0 || spacesCount - extraSpaces <= 0) { return false; }
            // delete whitespace to 'i' index, replace with tabs
            line.delete(0, i);
            line.insert(0, fillWith(tabsCount, '\t'));
            // if some last spaces exists, add them back
            if (extraSpaces > 0)
            {
                line.insert(tabsCount, fillWith(extraSpaces, ' '));
            }
        }
        return changed;
    }

    private static char[] fillWith(int length, char c)
    {
        char[] chars = new char[length];
        for (int i = 0; i < length; i++)
        {
            chars[i] = c;
        }
        return chars;
    }
}
