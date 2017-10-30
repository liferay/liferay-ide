package com.liferay.ide.velocity.vaulttec.ui.editor.actions;

import com.liferay.ide.velocity.editor.VelocityEditor;
import com.liferay.ide.velocity.vaulttec.ui.VelocityPlugin;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;


/**
 * DOCUMENT ME!
 * 
 * @author <a href="mailto:akmal.sarhan@gmail.com">Akmal Sarhan </a>
 * @version $Revision: 14 $
 */
public class FormatResourceAction implements IObjectActionDelegate
{

    private Object _selected = null;

    /**
     * 
     */
    public FormatResourceAction()
    {
        // TODO Auto-generated constructor stub
    }

    private String lineSep = System.getProperty("line.separator");

    /**
     * @param document
     */
    private IDocument format(final IDocument document)
    {
	Formatter formatter=new Formatter();
	return formatter.format(document);

    }

  
   
  

   
    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
     *      org.eclipse.ui.IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart)
    {
        _part = targetPart;
    }

    private IWorkbenchPart _part;

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action)
    {
        IWorkbenchPartSite site = _part.getSite();
        provider = site.getSelectionProvider();
        if (_selected == null)
        {
            MessageDialog.openInformation(new Shell(), "VelocityPlugin", "Unable to open file");
            // VelocityPlugin.log("Unable to open file");
            return;
        }
        if (_selected instanceof IStructuredSelection)
        {
            //
            try
            {
                Object[] items = ((IStructuredSelection) _selected).toArray();
                Set files = new HashSet(items.length, 1.0F);
                try
                {
                    for (int i = 0; i < items.length; i++)
                    {
                        if (items[i] instanceof IResource)
                        {
                            IResource resource = (IResource) items[i];
                            switch (resource.getType())
                            {
                                case IResource.FOLDER:
                                case IResource.PROJECT:
                                    IContainer folder = (IContainer) items[i];
                                    getChildren(folder, files);
                                    break;
                                case IResource.FILE:
                                    files.add((IFile) items[i]);
                                    // ((IFile) items[i]).getProject()
                                    break;
                                default:
                                    /**
                                     * @todo use logger to print warning about
                                     *       invalid type
                                     */
                                    break;
                            }
                        }
                    }
                }
                catch (CoreException ex)
                {
                    ex.printStackTrace();
                }
                for (Iterator iter = files.iterator(); iter.hasNext();)
                {
                    IFile directory = (IFile) iter.next();
                    formatFile(directory);
                   
                }
            }
            catch (Exception e)
            {
                VelocityPlugin.log(e);
            }
        } else
        {
            MessageDialog.openInformation(new Shell(), "VelocityPlugin", "Unable to open file");
            // VelocityPlugin.log("Unable to open shell");
            return;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection)
    {
        _selected = null;
        if (selection instanceof IStructuredSelection)
        {
            _selected = (IStructuredSelection) selection;
        }
    }

    private void getChildren(IContainer resource, Set files) throws CoreException
    {
        IResource[] children = resource.members();
        for (int i = 0; i < children.length; i++)
        {
            IResource child = children[i];
            switch (child.getType())
            {
                case IResource.FILE:
                    if (child.getName().matches(".*?(html|vm|jspx|jspf)"))
                    {
                        files.add((IFile) child);
                    }
                    break;
                case IResource.FOLDER:
                    getChildren((IFolder) child, files);
                    break;
                case IResource.PROJECT:
                    getChildren((IProject) child, files);
                    break;
                case IResource.ROOT:
                    getChildren((IWorkspaceRoot) child, files);
                    break;
            }
        }
    }

    ISelectionProvider provider = null;

    private void formatFile(IFile file)
    {
        if (file.getFileExtension().matches(".*?(html|vm|jspx|jspf)"))
        {
            ISelection s = new StructuredSelection(file);
            provider.setSelection(s);
           
            IDocument document = new Document();
            BufferedWriter awriter = null;
            String line = null;
            StringBuffer b = new StringBuffer();
            try
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(new BufferedInputStream(Files.newInputStream(file.getLocation().toFile().toPath()))));
                while ((line = in.readLine()) != null)
                {
                    b.append(line);
                    b.append(lineSep);
                }
                document.set(b.toString());
                document = format(document);
                if (file instanceof IResource)
                {
                    file.setContents(new BufferedInputStream(new ByteArrayInputStream(document.get().getBytes())), IFile.FORCE | IFile.KEEP_HISTORY, null);
                    ((IResource) file).refreshLocal(IResource.DEPTH_ZERO, null);
                }
            }
            catch (Exception e)
            {
                VelocityPlugin.log(e);
            }
            finally
            {
                if (awriter != null)
                {
                    try
                    {
                        awriter.close();
                    }
                    catch (IOException e)
                    {
                        VelocityPlugin.log(e);
                    }
                }
            }
            VelocityEditor.openInEditor(file);
        }
    }
}
