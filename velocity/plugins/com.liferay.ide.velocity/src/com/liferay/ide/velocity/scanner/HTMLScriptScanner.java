package com.liferay.ide.velocity.scanner;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

import com.liferay.ide.velocity.editor.EditorsUtil;
import com.liferay.ide.velocity.vaulttec.ui.IColorConstants;
import com.liferay.ide.velocity.vaulttec.ui.VelocityColorProvider;

/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 7 $
 * @author <a href="mailto:akmal.sarhan@gmail.com">Akmal Sarhan </a>
 */
public class HTMLScriptScanner extends RuleBasedScanner
{

    // private static final int START = 0;
    // private static final int ATTRIBUTE = 1;
    // private static final int CONTENT = 2;
    // private static final int ENDTAG = 3;
    // private static final int END = 4;
    private int                 fState;
    private HTMLElementDetector fWordDetector;
    private String              fElement;
    private IToken              fDefaultToken;
    private IToken              fScriptToken;
    private IToken              fElementToken;
    private IToken              fEndElementToken;
    private IToken              fStringToken;

    public HTMLScriptScanner(VelocityColorProvider manager)
    {
        fDefaultToken = manager.getToken(IColorConstants.SCRIPT);
        fScriptToken = manager.getToken(IColorConstants.SCRIPT);
        fElementToken = manager.getToken(IColorConstants.HTML_TAG);
        fEndElementToken = manager.getToken(IColorConstants.HTML_ENDTAG);
        fStringToken = manager.getToken(IColorConstants.HTML_String);
        setDefaultReturnToken(fDefaultToken);
        fWordDetector = new HTMLElementDetector();
        setRules(null);
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public IToken nextToken()
    {
        super.fTokenOffset = super.fOffset;
        super.fColumn = -1;
        if (super.fRules != null)
        {
            for (int i = 0; i < super.fRules.length; i++)
            {
                IToken token = super.fRules[i].evaluate(this);
                if (!token.isUndefined()) { return token; }
            }
        }
        for (int c = EditorsUtil.skipWhitespace(this); c != -1; c = read())
        {
            if (fState == 0)
            {
                if (c == 60)
                {
                    return fDefaultToken;
                } else
                {
                    unread();
                    fElement = EditorsUtil.getWord(fWordDetector, this);
                    fState = 1;
                    return fElementToken;
                }
            }
            if (fState == 1)
            {
                if (c == 62)
                {
                    fState = 2;
                    return fDefaultToken;
                }
                if ((c == 34) || (c == 39))
                {
                    if (super.fOffset > (super.fTokenOffset + 1))
                    {
                        unread();
                        return fDefaultToken;
                    } else
                    {
                        EditorsUtil.skipString(c, this);
                        return fStringToken;
                    }
                }
            } else if (fState == 2)
            {
                if (c == 60)
                {
                    int start = super.fOffset;
                    if (read() == 47)
                    {
                        String name = EditorsUtil.getWord(fWordDetector, this);
                        if (name.equalsIgnoreCase(fElement))
                        {
                            if (start > (super.fTokenOffset + 1))
                            {
                                while (super.fOffset >= start)
                                {
                                    unread();
                                }
                                return fScriptToken;
                            }
                            fState = 3;
                            while (super.fOffset > (start + 1))
                            {
                                unread();
                            }
                            return fDefaultToken;
                        }
                    }
                    while (super.fOffset > start)
                    {
                        unread();
                    }
                }
            } else if (fState == 3)
            {
                EditorsUtil.getWord(fWordDetector, this);
                fState = 4;
                return fEndElementToken;
            }
        }
        unread();
        if (super.fOffset > super.fTokenOffset)
        {
            return fDefaultToken;
        } else
        {
            return Token.EOF;
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param document
     *            DOCUMENT ME!
     * @param offset
     *            DOCUMENT ME!
     * @param length
     *            DOCUMENT ME!
     */
    public void setRange(IDocument document, int offset, int length)
    {
        super.setRange(document, offset, length);
        fState = 0;
    }

    class HTMLElementDetector implements IWordDetector
    {

        HTMLElementDetector()
        {
        }

        public boolean isWordStart(char c)
        {
            return XMLChar.isNameStart(c);
        }

        public boolean isWordPart(char c)
        {
            return XMLChar.isName(c);
        }
    }
}
