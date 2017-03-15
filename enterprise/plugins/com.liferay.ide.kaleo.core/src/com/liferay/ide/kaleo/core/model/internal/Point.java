package com.liferay.ide.kaleo.core.model.internal;

/**
 * @author Gregory Amerson
 */
public class Point
{
    private int x;
    private int y;

    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public Point()
    {
        this(-1,-1);
    }

    public int getX()
    {
        return this.x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return this.y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    @Override
    public boolean equals( Object obj )
    {
        return obj instanceof Point && ((Point)obj).getX() == this.x && ((Point)obj).getY() == this.y;
    }
}
