package com.liferay.ide.velocity.scanner;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 7 $
 * @author <a href="mailto:akmal.sarhan@gmail.com">Akmal Sarhan </a>
 */
public class XMLTagDetector implements IWordDetector
{

    /**
     * DOCUMENT ME!
     * 
     * @param aChar
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean isWordStart(char aChar)
    {
        return (aChar == '<') || (aChar == '>');
    }

    /**
     * DOCUMENT ME!
     * 
     * @param aChar
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean isWordPart(char aChar)
    {
        return Character.isLetterOrDigit(aChar) || (aChar == '-') || (aChar == '<') || (aChar == '_') || (aChar == '>') || (aChar == '/');
    }
}
