package com.liferay.ide.project.ui.migration;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

/**
 * @author Lovett li
 */
public class MigratorComparator extends ViewerComparator
{

    private int columnIndex;
    private static final int DESCENDING = 1;
    private int direction = DESCENDING;

    public MigratorComparator()
    {
        this.columnIndex = 0;
        direction = DESCENDING;
    }

    public int getDirection()
    {
        return direction == 1 ? SWT.DOWN : SWT.UP;
    }

    public void setColumn( int column )
    {
        if( column == this.columnIndex )
        {
            direction = 1 - direction;
        }
        else
        {
            this.columnIndex = column;
            direction = DESCENDING;
        }
    }

    @Override
    public int compare( Viewer viewer, Object e1, Object e2 )
    {
        TaskProblem t1 = (TaskProblem) e1;
        TaskProblem t2 = (TaskProblem) e2;
        int flag = 0;
        switch( columnIndex )
        {
        case 0:
            flag = t1.getTitle().compareTo( t2.getTitle() );
            break;
        case 1:
            flag = t1.getSummary().compareTo( t2.getSummary() );
            break;
        case 2:
            if( t1.getLineNumber() == t2.getLineNumber() )
            {
                flag = 0;
            }
            else
            {
                flag = t1.getLineNumber() > t2.getLineNumber() ? 1 : -1;
            }
            break;
        default:
            flag = 0;
        }
        if( direction == DESCENDING )
        {
            flag = -flag;
        }
        return flag;
    }
}
