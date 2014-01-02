package com.liferay.ide.core.jna;

import com.sun.jna.Library;

public interface CLibrary extends Library
{
    public int chmod( String path, int mode );
}