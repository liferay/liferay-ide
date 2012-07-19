package com.liferay.ide.velocity.editor.completion;

import com.liferay.ide.velocity.editor.VelocityEditor;
import com.liferay.ide.velocity.vaulttec.ui.editor.text.VelocityTextGuesser;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;


/**
 * This interface defines the contract for extensions that wish to contribute additional code completions for the
 * Velocity editor.
 *
 * @author Peter Friese
 * @since 24.01.2006
 */
public interface ICompletionProvider
{

    /**
     * Compute extra completion proposals.
     *
     * @param editor The underlying Velocity Editor
     * @param file The underlying file.
     * @param doc The document.
     * @param prefix Helps with reading the prefix.
     * @param offset The cursor position.
     * @return A {@link Collection} of {@link org.eclipse.jface.text.contentassist.ICompletionProposal}s.
     * @throws CoreException
     */
    Collection<ICompletionProposal> getExtraProposals(VelocityEditor editor,
        IFile file,
        IDocument doc,
        VelocityTextGuesser prefix,
        int offset) throws CoreException;

    Collection<ICompletionProposal> getVariableProposals( String aPrefix, int anOffset ) throws CoreException;

}
