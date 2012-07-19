package com.liferay.ide.velocity.vaulttec.ui.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class Main
{

    public static void main(String[] args)
    {
        File a = new File("/tmp/a");
        File b = new File("/tmp/b");
        copyFile(a, b);
        System.err.println("copied");
    }

    public static void copyFile(File source, File dest)
    {
        try
        {
            FileChannel in = new FileInputStream(source).getChannel();
            if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();
            FileChannel out = new FileOutputStream(dest).getChannel();
            in.transferTo(0, in.size(), out);
            in.close();
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}