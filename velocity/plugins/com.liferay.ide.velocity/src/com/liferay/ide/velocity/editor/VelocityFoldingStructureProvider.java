/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Common Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ******************************************************************************/
package com.liferay.ide.velocity.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;

import com.liferay.ide.velocity.vaulttec.ui.model.ITreeNode;

public class VelocityFoldingStructureProvider
{

    private VelocityEditor   fEditor;
    private IDocument        fDocument;
    private IProgressMonitor fProgressMonitor;

    public VelocityFoldingStructureProvider(VelocityEditor editor)
    {
        fEditor = editor;
    }

    public void setProgressMonitor(IProgressMonitor progressMonitor)
    {
        fProgressMonitor = progressMonitor;
    }

    public void setDocument(IDocument document)
    {
        fDocument = document;
    }

    public void updateFoldingRegions(ITreeNode recipe)
    {
        try
        {
            ProjectionAnnotationModel model = (ProjectionAnnotationModel) fEditor.getAdapter(ProjectionAnnotationModel.class);
            if (model == null || recipe == null) return;
            Set currentRegions = new HashSet();
            Object[] children = recipe.getChildren();
            if (children != null)
            {
                addFoldingRegions(currentRegions, children);
            }
            updateFoldingRegions(model, currentRegions);
        }
        catch (ClassCastException e)
        {
            e.printStackTrace();
        }
        catch (BadLocationException e)
        {
            e.printStackTrace();
        }
    }

    public void updateFoldingRegions(Object[] children)
    {
        try
        {
            ProjectionAnnotationModel model = (ProjectionAnnotationModel) fEditor.getAdapter(ProjectionAnnotationModel.class);
            if (model == null || children == null) return;
            Set currentRegions = new HashSet();
            if (children != null)
            {
                addFoldingRegions(currentRegions, children);
            }
            updateFoldingRegions(model, currentRegions);
        }
        catch (ClassCastException e)
        {
            e.printStackTrace();
        }
        catch (BadLocationException e)
        {
            e.printStackTrace();
        }
    }

    private void updateFoldingRegions(ProjectionAnnotationModel model, Set currentRegions)
    {
        Annotation[] deletions = computeDifferences(model, currentRegions);
        Map additionsMap = new HashMap();
        for (Iterator iter = currentRegions.iterator(); iter.hasNext();)
            additionsMap.put(new ProjectionAnnotation(), iter.next());
        if ((deletions.length != 0 || additionsMap.size() != 0) && (fProgressMonitor == null || !fProgressMonitor.isCanceled())) model.modifyAnnotations(deletions, additionsMap, new Annotation[] {});
    }

    private Annotation[] computeDifferences(ProjectionAnnotationModel model, Set current)
    {
        List deletions = new ArrayList();
        for (Iterator iter = model.getAnnotationIterator(); iter.hasNext();)
        {
            Object annotation = iter.next();
            if (annotation instanceof ProjectionAnnotation)
            {
                Position position = model.getPosition((Annotation) annotation);
                if (current.contains(position))
                    current.remove(position);
                else
                    deletions.add(annotation);
            }
        }
        return (Annotation[]) deletions.toArray(new Annotation[deletions.size()]);
    }

    private void addFoldingRegions(Set regions, Object[] elements) throws BadLocationException
    {
        for (int i = 0; i < elements.length; i++)
        {
            ITreeNode element = (ITreeNode) elements[i];
            int startLine = element.getStartLine() - 1;
            int endLine = element.getEndLine() - 1;
            if (startLine < endLine)
            {
                int start = fDocument.getLineOffset(startLine);
                int end = fDocument.getLineOffset(endLine) + fDocument.getLineLength(endLine);
                Position position = new Position(start, end - start);
                regions.add(position);
            }
            Object[] children = (Object[]) element.getChildren();
            if (children != null) addFoldingRegions(regions, children);
        }
    }
}
