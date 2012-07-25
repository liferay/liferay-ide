package com.liferay.ide.velocity.vaulttec.ui.editor.text;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * A Velocity identifier aware word detector.
 */
public class IdentifierDetector implements IWordDetector
{

    /**
     * Determines if the specified character is permissible as the first
     * character in a Velocity identifier. A character may start a Velocity
     * identifier if and only if it is one of the following:
     * <ul>
     * <li>a letter (a..z, A..Z)
     * </ul>
     * 
     * @param aChar
     *            the character to be tested.
     * @return true if the character may start a Velocity identifier; false
     *         otherwise.
     * @see java.lang.Character#isLetter(char)
     * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart
     */
    public boolean isWordStart(char aChar)
    {
        return Character.isLetter(aChar);
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
     * <li>a punctuation character (".")
     * </ul>
     * 
     * @param aChar
     *            the character to be tested.
     * @return true if the character may be part of a Velocity identifier; false
     *         otherwise.
     * @see java.lang.Character#isLetterOrDigit(char)
     * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart
     */
    public boolean isWordPart(char aChar)
    {
        return (Character.isLetterOrDigit(aChar) || (aChar == '-') || (aChar == '_'));
        // || aChar == '.'
    }
}
