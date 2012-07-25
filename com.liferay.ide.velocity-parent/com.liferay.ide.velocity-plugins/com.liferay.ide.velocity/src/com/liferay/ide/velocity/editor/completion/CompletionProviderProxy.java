package com.liferay.ide.velocity.editor.completion;

import com.liferay.ide.velocity.editor.VelocityEditor;
import com.liferay.ide.velocity.vaulttec.ui.editor.text.VelocityTextGuesser;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;


/**
 * A proxy class which acts as a stand-in for a {@link ICompletionProvider}. The main purpose of this proxy is to defer
 * creation of the real {@link ICompletionProvider} until we really need it. All calls to this proxy will then be
 * delegated to the lazily created {@link ICompletionProvider}.
 *
 * @author Peter Friese
 * @see ICompletionProvider
 */
public class CompletionProviderProxy
        implements ICompletionProvider
{

    /** Name of the attribute that contains the class name for the completion provider. */
    private static final String ID_EXTENSION_PROVIDER_CLASS = "class";

    /** The real completion provider. */
    private ICompletionProvider completionProvider;

    /** The config element describing the extension. */
    private IConfigurationElement configElement;

    /**
     * Creates a new {@link CompletionProviderProxy}.
     *
     * @param configElement The extension element describing this longrunner provider in the plugin.xml file.
     */
    public CompletionProviderProxy(IConfigurationElement configElement)
    {
        this.configElement = configElement;
    }

    /**
     * Lazily initializes the real {@link ILongrunnerProvider}.
     *
     * @return An instantiated {@link ILongrunnerProvider}
     * @throws CoreException If the provider could not be instantiated.
     */
    private ICompletionProvider getCompletionProvider() throws CoreException
    {
        if (completionProvider == null)
        {
            completionProvider = (ICompletionProvider)configElement
                    .createExecutableExtension(ID_EXTENSION_PROVIDER_CLASS);
        }
        return completionProvider;
    }

    /**
     * {@inheritDoc}
     *
     * @throws CoreException
     */
    public Collection getExtraProposals(VelocityEditor editor, IFile file,
        IDocument doc,
        VelocityTextGuesser prefix,
        int offset) throws CoreException
    {
        return getCompletionProvider().getExtraProposals(editor, file, doc, prefix, offset);
    }

    public Collection<ICompletionProposal> getVariableProposals( String aPrefix, int anOffset ) throws CoreException
    {
        return getCompletionProvider().getVariableProposals( aPrefix, anOffset );
    }

}
