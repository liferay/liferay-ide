package com.liferay.ide.velocity.vaulttec.ui.editor.text;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import com.liferay.ide.velocity.ui.editor.xml.VelocityAutoIndentStrategy;

/**
 * Guesses the start/end and the type of Velocity content (directive or
 * identifier) from a given offset.
 */
public class VelocityTextGuesser
{

    public static final int TYPE_END        = 6;
    public static final int TYPE_APOSTROPHE = 5;
    public static final int TYPE_INVALID    = 0;
    public static final int TYPE_DIRECTIVE  = 1;
    public static final int TAG_DIRECTIVE   = 3;
    public static final int TAG_CLOSE       = 4;
    public static final int TYPE_VARIABLE   = 2;
    public static final int TYPE_MEMBER_QUALIFIER  = 7;
    
    private int             fType;
    private String          fText;
    private int             fLine;
    private int             tagoffset       = -1;
    private String          fVariable;

    /**
     * Create an invalid text guesser.
     */
    public VelocityTextGuesser()
    {
        fType = TYPE_INVALID;
        fText = "";
        fVariable = "";
        fLine = -1;
    }

    public VelocityTextGuesser(IDocument aDocument, int anOffset, boolean aGuessEnd)
    {
        String f = "";
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
            fLine = aDocument.getLineOfOffset(start) + 1;
            // Now guess fType of completion
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
                try
                {
                    f = VelocityAutoIndentStrategy.getVeloIdentifier(aDocument, start - 1, start + 4);
                }
                catch (Exception e)
                {
                    // TODO: handle exception
                }
                if (f.equals("#end"))
                {
                    fType = TYPE_END;
                    tagoffset = start - 1;
                } else if (c1 == '#' || c1 == '@')
                {
                    fType = TYPE_DIRECTIVE;
                } else if (c1 == '\"')
                {
                    fType = TYPE_APOSTROPHE;
                } else if (c1 == '$')
                {
                    fType = TYPE_VARIABLE;
                } else if (c1 == '.') {
                    fType = TYPE_MEMBER_QUALIFIER;
                    // try to find the variable name:
                    int scan = end = start - 1;
                    int line = aDocument.getLineOfOffset(scan);
                    while ((scan >= 1) && (aDocument.getChar(scan - 1) != '$'))
                    {
                        scan--;
                    }
                    
                    // if we moved to a different line, it cannot be a variable:
                    if (aDocument.getLineOfOffset(scan) == line) {
                        fVariable = aDocument.get(scan, (end - scan));
                    }
                    else {
                        fVariable = "";
                    }
                    
                } else if (c1 == '<')
                {
                    fType = TAG_DIRECTIVE;
                } else if ((c1 == '/') && (c0 == '<'))
                {
                    fType = TAG_CLOSE;
                } else
                {
                    if (start >= 2)
                    {
                        // Formal or quiet reference
                        char c2 = aDocument.getChar(start - 2);
                        if ((c2 == '$') && ((c1 == '{') || (c1 == '!')))
                        {
                            fType = TYPE_VARIABLE;
                        } else
                        {
                            if (start >= 3)
                            {
                                // Formal quiet reference
                                char c3 = aDocument.getChar(start - 3);
                                if (((c3 == '$') && (c2 == '!')) || (c1 == '{'))
                                {
                                    fType = TYPE_VARIABLE;
                                }
                            }
                        }
                        // if(fType ==0)
                        // fType = TAG_DIRECTIVE;
                    }
                }
            }
        }
        catch (BadLocationException e)
        {
            fType = TYPE_INVALID;
            fText = "";
            fLine = -1;
        }
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
    public int getType()
    {
        return fType;
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

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public int getLine()
    {
        return fLine;
    }
    
    /**
     * @return The variable, if the current position relates to a member qualifier (the dot). 
     */
    public String getVariable()
    {
        return fVariable;
    }

    /**
     * Determines if the specified character may be part of a Velocity
     * identifier as other than the first character. A character may be part of
     * a Velocity identifier if and only if it is one of the following:
     * <ul>
     * <li>a letter (a..z, A..Z)
     * <li>a digit (0..9)
     * <li>a hyphen ("-")
     * <li>a connecting punctuation character ("_")
     * </ul>
     * 
     * @param aChar
     *            the character to be tested.
     * @return true if the character may be part of a Velocity identifier; false
     *         otherwise.
     * @see java.lang.Character#isLetterOrDigit(char)
     */
    private static final boolean isWordPart(char aChar)
    {
        return Character.isLetterOrDigit(aChar) || (aChar == '-') || (aChar == '_') || (aChar == ':');
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public String toString()
    {
        return "type=" + fType + ", text=" + fText + ", line=" + fLine;
    }
}
