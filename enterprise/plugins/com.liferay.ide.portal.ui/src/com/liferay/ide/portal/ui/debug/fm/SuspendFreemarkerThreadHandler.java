/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/
package com.liferay.ide.portal.ui.debug.fm;

import com.liferay.ide.portal.core.debug.fm.FMStackFrame;
import com.liferay.ide.portal.core.debug.fm.FMThread;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.debug.core.DebugException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;
import org.eclipse.ui.internal.AbstractEnabledHandler;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class SuspendFreemarkerThreadHandler extends AbstractEnabledHandler
{

    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        FMStackFrame stackFrame = getSelectedFMStackFrame( event.getApplicationContext() );

        if( stackFrame != null )
        {
            try
            {
                stackFrame.suspendJavaThread();
            }
            catch( DebugException e )
            {
                throw new ExecutionException( "Could not suspend related freemaker thread", e );
            }
        }

        return null;
    }

    private Object getSelectedElement( Object context )
    {
        if( context instanceof IEvaluationContext )
        {
            IEvaluationContext evaluationContext = (IEvaluationContext) context;

            Object currentSelectionVar = evaluationContext.getVariable( ISources.ACTIVE_CURRENT_SELECTION_NAME );

            if( currentSelectionVar instanceof IStructuredSelection )
            {
                IStructuredSelection selection = (IStructuredSelection) currentSelectionVar;

                if( selection.size() == 1 )
                {
                    return selection.getFirstElement();
                }
            }
        }

        return null;
    }

    private FMStackFrame getSelectedFMStackFrame( Object context )
    {
        Object selectedElement = getSelectedElement( context );

        FMThread fmThread = null;
        FMStackFrame fmStackFrame = null;

        if( selectedElement instanceof FMThread )
        {
             fmThread = (FMThread) selectedElement;

            if( fmThread.isSuspended() )
            {
                try
                {
                    fmStackFrame = (FMStackFrame) fmThread.getTopStackFrame();
                }
                catch( DebugException e )
                {
                }
            }
        }
        else if( selectedElement instanceof FMStackFrame )
        {
            fmStackFrame = (FMStackFrame) selectedElement;
        }

        return fmStackFrame;
    }

    @Override
    public void setEnabled( Object evaluationContext )
    {
        super.setEnabled( getSelectedFMStackFrame( evaluationContext ) != null );
    }

}
