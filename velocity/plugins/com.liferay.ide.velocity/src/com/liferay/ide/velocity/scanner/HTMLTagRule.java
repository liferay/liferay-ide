package com.liferay.ide.velocity.scanner;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * A modification of the PatternRule that skip over quoted strings.
 */
public class HTMLTagRule implements IPredicateRule
{

    protected static final int UNDEFINED = -1;
    /** The token to be returned on success */
    protected IToken           fToken;
    /** The pattern's start sequence */
    protected char[]           fStartSequence;
    /** The pattern's end sequence */
    protected char[]           fEndSequence;
    /** The pattern's column constrain */
    protected int              fColumn   = UNDEFINED;
    /** The pattern's escape character */
    protected char             fEscapeCharacter;
    /** Indicates whether end of line termines the pattern */
    protected boolean          fBreaksOnEOL;

    /**
     * Creates a rule for the given starting and ending sequence. When these
     * sequences are detected the rule will return the specified token.
     * Alternatively, the sequence can also be ended by the end of the line. Any
     * character which follows the given escapeCharacter will be ignored.
     * 
     * @param startSequence
     *            the pattern's start sequence
     * @param endSequence
     *            the pattern's end sequence, <code>null</code> is a legal
     *            value
     * @param token
     *            the token which will be returned on success
     * @param escapeCharacter
     *            any character following this one will be ignored
     * @param indicates
     *            whether the end of the line also termines the pattern
     */
    public HTMLTagRule(String startSequence, String endSequence, IToken token, char escapeCharacter, boolean breaksOnEOL)
    {
       
        //
        fStartSequence = startSequence.toCharArray();
        fEndSequence = ((endSequence == null) ? new char[0] : endSequence.toCharArray());
        fToken = token;
        fEscapeCharacter = escapeCharacter;
        fBreaksOnEOL = breaksOnEOL;
    }

    /**
     * Sets a column constraint for this rule. If set, the rule's token will
     * only be returned if the pattern is detected starting at the specified
     * column. If the column is smaller then 0, the column constraint is
     * considered removed.
     * 
     * @param column
     *            the column in which the pattern starts
     */
    public void setColumnConstraint(int column)
    {
        if (column < 0)
        {
            column = UNDEFINED;
        }
        fColumn = column;
    }

    /*
     * @see IRule#evaluate
     */
    public IToken evaluate(ICharacterScanner scanner)
    {
        return evaluate(scanner, false);
    }

    /*
     * @see IPredicateRule#evaluate(ICharacterScanner, boolean)
     * @since 2.0
     */
    public IToken evaluate(ICharacterScanner scanner, boolean resume)
    {
        if (fColumn == UNDEFINED) { return doEvaluate(scanner, resume); }
        int c = scanner.read();
        scanner.unread();
        if (c == fStartSequence[0])
        {
            return ((fColumn == scanner.getColumn()) ? doEvaluate(scanner, resume) : Token.UNDEFINED);
        } else
        {
            return Token.UNDEFINED;
        }
    }

    /*
     * @see IPredicateRule#getSuccessToken()
     * @since 2.0
     */
    public IToken getSuccessToken()
    {
        return fToken;
    }

    /**
     * Evaluates this rules without considering any column constraints.
     * 
     * @param scanner
     *            the character scanner to be used
     * @return the token resulting from this evaluation
     */
    protected IToken doEvaluate(ICharacterScanner scanner)
    {
        return doEvaluate(scanner, false);
    }

    /**
     * Evaluates this rules without considering any column constraints. Resumes
     * detection, i.e. look sonly for the end sequence required by this rule if
     * the <code>resume</code> flag is set.
     * 
     * @param scanner
     *            the character scanner to be used
     * @param resume
     *            <code>true</code> if detection should be resumed,
     *            <code>false</code> otherwise
     * @return the token resulting from this evaluation
     * @since 2.0
     */
    protected IToken doEvaluate(ICharacterScanner scanner, boolean resume)
    {
        if (resume)
        {
            if (endSequenceDetected(scanner)) { return fToken; }
        } else
        {
            int c = scanner.read();
            if (c == fStartSequence[0])
            {
                if (sequenceDetected(scanner, fStartSequence, false))
                {
                    if (endSequenceDetected(scanner)) { return fToken; }
                }
            }
        }
        scanner.unread();
        return Token.UNDEFINED;
    }

    /**
     * Returns whether the next characters to be read by the character scanner
     * are an exact match with the given sequence. No escape characters are
     * allowed within the sequence. If specified the sequence is considered to
     * be found when reading the EOF character.
     * 
     * @param scanner
     *            the character scanner to be used
     * @param sequence
     *            the sequence to be detected
     * @param eofAllowed
     *            indicated whether EOF terminates the pattern
     * @return <code>true</code> if the given sequence has been detected
     */
    protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed)
    {
        for (int i = 1; i < sequence.length; i++)
        {
            int c = scanner.read();
            if ((c == ICharacterScanner.EOF) && eofAllowed)
            {
                return true;
            } else if (c != sequence[i])
            {
                // Non-matching character detected, rewind the scanner back to
                // the start.
                // Do not unread the first character.
                scanner.unread();
                for (int j = i - 1; j > 0; j--)
                {
                    scanner.unread();
                }
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether the end sequence was detected. As the pattern can be
     * considered ended by a line delimiter, the result of this method is
     * <code>true</code> if the rule breaks on the end of the line, or if the
     * EOF character is read.
     * 
     * @param scanner
     *            the character scanner to be used
     * @return <code>true</code> if the end sequence has been detected
     */
    protected boolean endSequenceDetected(ICharacterScanner scanner)
    {
        int c;
        char[][] delimiters = scanner.getLegalLineDelimiters();
        while ((c = scanner.read()) != ICharacterScanner.EOF)
        {
            if (c == fEscapeCharacter)
            {
                // Skip the escaped character.
                scanner.read();
            } else if ((c == '\"') || (c == '\''))
            {
                if (skipQuoted(scanner, c)) { return true; }
            } else if ((fEndSequence.length > 0) && (c == fEndSequence[0]))
            {
                // Check if the specified end sequence has been found.
                if (sequenceDetected(scanner, fEndSequence, true)) { return true; }
            } else if (fBreaksOnEOL)
            {
                // Check for end of line since it can be used to terminate the
                // pattern.
                for (int i = 0; i < delimiters.length; i++)
                {
                    if ((c == delimiters[i][0]) && sequenceDetected(scanner, delimiters[i], false)) { return true; }
                }
            }
        }
        scanner.unread();
        return true;
    }

    private boolean skipQuoted(ICharacterScanner scanner, int delim)
    {
        int c;
        char[][] delimiters = scanner.getLegalLineDelimiters();
        while ((c = scanner.read()) != ICharacterScanner.EOF)
        {
            if (c == fEscapeCharacter)
            {
                scanner.read();
            } else if (c == delim)
            {
                return false;
            } else if (c == '<')
            {
                scanner.unread();
                return false;
            } else if (fBreaksOnEOL)
            {
                // Check for end of line since it can be used to terminate the
                // pattern.
                for (int i = 0; i < delimiters.length; i++)
                {
                    if ((c == delimiters[i][0]) && sequenceDetected(scanner, delimiters[i], false)) { return true; }
                }
            }
        }
        return false;
    }
}
