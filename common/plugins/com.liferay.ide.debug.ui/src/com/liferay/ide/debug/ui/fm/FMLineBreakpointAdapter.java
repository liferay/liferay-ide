package com.liferay.ide.debug.ui.fm;

import com.liferay.ide.debug.core.ILRDebugConstants;
import com.liferay.ide.debug.core.fm.FMLineBreakpoint;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;


public class FMLineBreakpointAdapter implements IToggleBreakpointsTarget
{

    public void toggleLineBreakpoints( IWorkbenchPart part, ISelection selection ) throws CoreException
    {
        ITextEditor textEditor = getEditor( part );
        
        if( textEditor != null )
        {
            IResource resource = (IResource) textEditor.getEditorInput().getAdapter( IResource.class );
            ITextSelection textSelection = (ITextSelection) selection;
            int lineNumber = textSelection.getStartLine();
            IBreakpoint[] breakpoints =
                DebugPlugin.getDefault().getBreakpointManager().getBreakpoints( ILRDebugConstants.ID_FM_DEBUG_MODEL );
            
            for( int i = 0; i < breakpoints.length; i++ )
            {
                IBreakpoint breakpoint = breakpoints[i];
                
                if( resource.equals( breakpoint.getMarker().getResource() ) )
                {
                    if( ( (ILineBreakpoint) breakpoint ).getLineNumber() == ( lineNumber + 1 ) )
                    {
                        // remove
                        breakpoint.delete();
                        return;
                    }
                }
            }
            
            // create line breakpoint (doc line numbers start at 0)
            FMLineBreakpoint lineBreakpoint = new FMLineBreakpoint( resource, lineNumber + 1 );
            DebugPlugin.getDefault().getBreakpointManager().addBreakpoint( lineBreakpoint );
        }
    }

    private ITextEditor getEditor( IWorkbenchPart part )
    {
        if( part instanceof ITextEditor )
        {
            ITextEditor editorPart = (ITextEditor) part;
            IResource resource = (IResource) editorPart.getEditorInput().getAdapter( IResource.class );

            if( resource != null )
            {
                String extension = resource.getFileExtension();

                if( extension != null && extension.equals( "ftl" ) )
                {
                    return editorPart;
                }
            }
        }

        return null;
    }

    public boolean canToggleLineBreakpoints( IWorkbenchPart part, ISelection selection )
    {
        return getEditor( part ) != null;
    }

    public void toggleMethodBreakpoints( IWorkbenchPart part, ISelection selection ) throws CoreException
    {
    }

    public boolean canToggleMethodBreakpoints( IWorkbenchPart part, ISelection selection )
    {
        return false;
    }

    public void toggleWatchpoints( IWorkbenchPart part, ISelection selection ) throws CoreException
    {
    }

    public boolean canToggleWatchpoints( IWorkbenchPart part, ISelection selection )
    {
        return false;
    }

}
