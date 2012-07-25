package com.liferay.ide.velocity.vaulttec.ui.editor.text;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextViewer;

/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 7 $
 * @author <a href="mailto:akmal.sarhan@gmail.com">Akmal Sarhan </a>
 */
public class VelocityDoubleClickStrategy implements ITextDoubleClickStrategy
{

    protected ITextViewer fText;

    /**
     * @see org.eclipse.jface.text.ITextDoubleClickStrategy#doubleClicked(org.eclipse.jface.text.ITextViewer)
     */
    public void doubleClicked(ITextViewer part)
    {
        int pos = part.getSelectedRange().x;
        if (pos < 0) { return; }
        fText = part;
        if (!selectComment(pos))
        {
            selectWord(pos);
        }
    }

    protected boolean selectComment(int caretPos)
    {
        IDocument doc = fText.getDocument();
        int startPos;
        int endPos;
        try
        {
            int pos = caretPos;
            char c = ' ';
            while (pos >= 0)
            {
                c = doc.getChar(pos);
                if (c == '\\')
                {
                    pos -= 2;
                    continue;
                }
                if ((c == Character.LINE_SEPARATOR) || (c == '\"'))
                {
                    break;
                }
                --pos;
            }
            if (c != '\"') { return false; }
            startPos = pos;
            pos = caretPos;
            int length = doc.getLength();
            c = ' ';
            while (pos < length)
            {
                c = doc.getChar(pos);
                if ((c == Character.LINE_SEPARATOR) || (c == '\"'))
                {
                    break;
                }
                ++pos;
            }
            if (c != '\"') { return false; }
            endPos = pos;
            int offset = startPos + 1;
            int len = endPos - offset;
            fText.setSelectedRange(offset, len);
            return true;
        }
        catch (BadLocationException x)
        {
        }
        return false;
    }

    protected boolean selectWord(int caretPos)
    {
        IDocument doc = fText.getDocument();
        int startPos;
        int endPos;
        try
        {
            int pos = caretPos;
            char c;
            while (pos >= 0)
            {
                c = doc.getChar(pos);
                if (!isIdentifierPart(c))
                {
                    break;
                }
                --pos;
            }
            startPos = pos;
            pos = caretPos;
            int length = doc.getLength();
            while (pos < length)
            {
                c = doc.getChar(pos);
                if (!isIdentifierPart(c))
                {
                    break;
                }
                ++pos;
            }
            endPos = pos;
            selectRange(startPos, endPos);
            return true;
        }
        catch (BadLocationException x)
        {
        }
        return false;
    }

    private void selectRange(int startPos, int stopPos)
    {
        int offset = startPos + 1;
        int length = stopPos - offset;
        fText.setSelectedRange(offset, length);
    }

    /**
     * Determines if the specified character may be part of a Velocity
     * identifier as other than the first character. A character may be part of
     * a Velocity identifier if and only if it is one of the following:
     * <ul>
     * <li>a letter (a..z, A..Z)
     * <li>a digit (0..9)
     * <li>a hyphen ("-")
     * <li>a underscore("_")
     * </ul>
     * 
     * @param aChar
     *            the character to be tested.
     * @return true if the character may be part of a Velocity identifier; false
     *         otherwise.
     * @see java.lang.Character#isLetterOrDigit(char)
     */
    private boolean isIdentifierPart(char aChar)
    {
        return (Character.isLetterOrDigit(aChar) || (aChar == '-') || (aChar == '_'));
    }
}
