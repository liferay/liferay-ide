/*
 * Created on 24.09.2003
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.liferay.ide.velocity.vaulttec.ui.editor.text;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * @author Administrator
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class OperandDetector implements IWordDetector
{

    /**
     * Detector for empty Velocity comments.
     */
    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
     */
    public boolean isWordStart(char aChar)
    {
        return Character.isDigit(aChar) || (aChar == '|') || (aChar == '%') || (aChar == '~') || (aChar == '<') || (aChar == '&') || (aChar == '=') || (aChar == '-') || (aChar == '+');
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
     */
    public boolean isWordPart(char aChar)
    {
        return Character.isDigit(aChar) || (aChar == '|') || (aChar == '%') || (aChar == '~') || (aChar == '<') || (aChar == '&') || (aChar == '=') || (aChar == '-') || (aChar == '+');
    }
}
