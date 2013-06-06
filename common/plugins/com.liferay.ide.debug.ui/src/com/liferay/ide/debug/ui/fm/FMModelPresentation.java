package com.liferay.ide.debug.ui.fm;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.debug.core.fm.FMValue;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;


public class FMModelPresentation extends LabelProvider implements IDebugModelPresentation
{

    public IEditorInput getEditorInput( Object element )
    {
        IEditorInput editorInput = null;

        if( element instanceof IFile )
        {
            editorInput = new FileEditorInput( (IFile) element );
        }
        else if( element instanceof ILineBreakpoint )
        {
            final IMarker marker = ( (ILineBreakpoint) element ).getMarker();

            IResource resource = marker.getResource();

            if( resource instanceof IFile )
            {
                editorInput = new FileEditorInput( (IFile) resource );
            }
        }

        return editorInput;
    }

    public String getEditorId( IEditorInput input, Object element )
    {
        if( element instanceof IFile || element instanceof ILineBreakpoint )
        {
            return "com.liferay.ide.freemarker.editor.FreemarkerEditor";
        }

        return null;
    }

    public void setAttribute( String attribute, Object value )
    {
        System.out.println(attribute + " " + value);
    }

    public Image getImage(Object element)
    {
        return null;
    }

    public String getText(Object element)
    {
        return null;
    }

    public void computeDetail( IValue value, IValueDetailListener listener )
    {
        String detail = StringPool.EMPTY;

        if( value instanceof FMValue )
        {
            FMValue fmValue = (FMValue) value;
            detail = fmValue.getDetailString();
        }

        listener.detailComputed( value, detail );
    }

}
