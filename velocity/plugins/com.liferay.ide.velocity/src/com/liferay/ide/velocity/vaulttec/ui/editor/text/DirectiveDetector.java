package com.liferay.ide.velocity.vaulttec.ui.editor.text;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * A Velocity directive aware word detector.
 */
public class DirectiveDetector implements IWordDetector
{

    /**
     * Determines if the specified character is permissible as the first
     * character in a Velocity directive. A character may start a Velocity
     * directive if and only if it is one of the following:
     * <ul>
     * <li>a hash (#)
     * </ul>
     * 
     * @param aChar
     *            the character to be tested.
     * @return true if the character may start a Velocity directive; false
     *         otherwise.
     * @see java.lang.Character#isLetter(char)
     * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart
     */
    public boolean isWordStart(char aChar)
    {
        return aChar == '#';
    }

    /**
     * Determines if the specified character may be part of a Velocity directive
     * as other than the first character. A character may be part of a Velocity
     * directive if and only if it is one of the following:
     * <ul>
     * <li>a letter (a..z, A..Z)
     * <li>a digit (0..9)
     * <li>a hyphen ("-")
     * <li>a connecting punctuation character ("_")
     * </ul>
     * 
     * We add '@' as little kludge for block macros that have the pattern
     * #@<chars>  Technically the can only appear after the '#' character, but
     * this is probably ok.
     * 
     * @param aChar
     *            the character to be tested.
     * @return true if the character may be part of a Velocity directive; false
     *         otherwise.
     * @see java.lang.Character#isLetterOrDigit(char)
     * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart
     */
    public boolean isWordPart(char aChar)
    {
        return Character.isLetterOrDigit(aChar) || (aChar == '@') || (aChar == '-') || (aChar == '_');
    }
}
