package com.liferay.ide.velocity.scanner;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import com.liferay.ide.velocity.vaulttec.ui.editor.text.VelocityTextGuesser;

/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 7 $
 * @author <a href="mailto:akmal.sarhan@gmail.com">Akmal Sarhan </a>
 */
public class XMLElementGuesser
{

    private int    tagoffset = -1;
    private String fText;
    private int    fType;

    public XMLElementGuesser(IDocument aDocument, int anOffset, boolean aGuessEnd)
    {
        guessWord(aDocument, anOffset, aGuessEnd);
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public int getType()
    {
        return fType;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public int getTagOffset()
    {
        return tagoffset;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public String getText()
    {
        return fText;
    }

    private static final boolean isWordPart(char aChar)
    {
        return Character.isLetterOrDigit(aChar) || (aChar == '-') || (aChar == '_');
    }

    /**
     * DOCUMENT ME!
     * 
     * @param aDocument
     *            DOCUMENT ME!
     * @param anOffset
     *            DOCUMENT ME!
     * @param aGuessEnd
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private final String guessWord(IDocument aDocument, int anOffset, boolean aGuessEnd)
    {
        try
        {
            // Guess start position
            int start = anOffset;
            while ((start >= 1) && isWordPart(aDocument.getChar(start - 1)))
            {
                start--;
            }
            // Guess end position
            int end = anOffset;
            if (aGuessEnd)
            {
                int len = aDocument.getLength() - 1;
                while ((end < len) && isWordPart(aDocument.getChar(end)))
                {
                    end++;
                }
            }
            fText = aDocument.get(start, (end - start));
            if (start >= 1)
            {
                // Directive or shorthand reference
                char c1 = aDocument.getChar(start - 1);
                char c0 = 0;
                try
                {
                    c0 = aDocument.getChar(start - 2);
                }
                catch (Exception e)
                {
                    // TODO: handle exception
                }
                if (c1 == '<')
                {
                    fType = VelocityTextGuesser.TAG_DIRECTIVE;
                    tagoffset = start - 1;
                } else if ((c1 == '/') && (c0 == '<'))
                {
                    fType = VelocityTextGuesser.TAG_CLOSE;
                    tagoffset = start - 2;
                } else
                {
                    fType = -1;
                }
            }
        }
        catch (BadLocationException e)
        {
        }
        return fText;
    }
}
