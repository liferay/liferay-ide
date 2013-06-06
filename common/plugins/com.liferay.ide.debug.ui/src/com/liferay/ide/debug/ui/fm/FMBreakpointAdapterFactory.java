package com.liferay.ide.debug.ui.fm;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.ui.texteditor.ITextEditor;


public class FMBreakpointAdapterFactory implements IAdapterFactory
{

    public Object getAdapter( Object adaptableObject, Class adapterType )
    {
        if( adaptableObject instanceof ITextEditor )
        {
            ITextEditor editorPart = (ITextEditor) adaptableObject;
            IResource resource = (IResource) editorPart.getEditorInput().getAdapter( IResource.class );

            if( resource != null )
            {
                String extension = resource.getFileExtension();

                if( extension != null && extension.equals( "ftl" ) )
                {
                    return new FMLineBreakpointAdapter();
                }
            }
        }

        return null;
    }

    public Class[] getAdapterList()
    {
        return new Class[] { IToggleBreakpointsTarget.class };
    }

}
