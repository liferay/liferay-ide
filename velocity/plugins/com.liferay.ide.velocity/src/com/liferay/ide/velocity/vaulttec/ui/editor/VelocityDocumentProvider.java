package com.liferay.ide.velocity.vaulttec.ui.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

import com.liferay.ide.velocity.scanner.VelocityPartitionScanner;


/**
 * This class provides the IDocuments used by Velocity editors. These IDocuments
 * have an Velocity-aware partition scanner (multi-line comments) attached.
 */
public class VelocityDocumentProvider extends FileDocumentProvider
{

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#createDocument(Object)
     */
    protected IDocument createDocument(Object anElement) throws CoreException
    {
        IDocument document = super.createDocument(anElement);
        if (document != null)
        {
            IDocumentPartitioner partitioner = new FastPartitioner(new VelocityPartitionScanner(), VelocityPartitionScanner.TYPES);
            partitioner.connect(document);
            document.setDocumentPartitioner(partitioner);
        }
        return document;
    }
}
