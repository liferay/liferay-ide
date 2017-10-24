package com.liferay.ide.velocity.vaulttec.ui.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;

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
    	try {
    		if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();
			Files.copy(source.toPath(), dest.toPath());
		} catch (IOException e1) {

			e1.printStackTrace();
		}
    }
}