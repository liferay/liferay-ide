package com.liferay.ide.eclipse.velocity.editor.completion;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;

import com.liferay.ide.eclipse.velocity.editor.VelocityEditor;
import com.liferay.ide.eclipse.velocity.vaulttec.ui.editor.text.VelocityTextGuesser;


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
    Collection getExtraProposals(VelocityEditor editor,
        IFile file,
        IDocument doc,
        VelocityTextGuesser prefix,
        int offset) throws CoreException;

}
