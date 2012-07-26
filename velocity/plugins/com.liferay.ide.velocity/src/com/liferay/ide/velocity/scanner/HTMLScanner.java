package com.liferay.ide.velocity.scanner;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.WhitespaceRule;

import com.liferay.ide.velocity.vaulttec.ui.editor.text.WhitespaceDetector;

/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 7 $
 * @author <a href="mailto:akmal.sarhan@gmail.com">Akmal Sarhan </a>
 */
public class HTMLScanner extends RuleBasedScanner
{

    public HTMLScanner()
    {
        List rules = new ArrayList();
        rules.add(new WhitespaceRule(new WhitespaceDetector()));
        IRule[] result = new IRule[rules.size()];
        rules.toArray(result);
        setRules(result);
    }
}
