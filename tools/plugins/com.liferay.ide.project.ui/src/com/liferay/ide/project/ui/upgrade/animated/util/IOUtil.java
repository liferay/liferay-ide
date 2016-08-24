/*
 * Copyright (c) 2014, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 418454
 */
package com.liferay.ide.project.ui.upgrade.animated.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.IStatus;
import org.w3c.dom.Document;

/**
 * @author Eike Stepper
 */
public final class IOUtil
{
  private static final int MAX_FILE_NAME_LENGTH = 200;

  private static final int DEFAULT_BUFFER_SIZE = 8192;

  private static final ObjectOutputStream DEV_NULL = createDevNull();

  private static final String IMAGE_DATA_PREFIX = "imagedata:";

  private IOUtil()
  {
  }

  private static ObjectOutputStream createDevNull()
  {
    try
    {
      return new ObjectOutputStream(new OutputStream()
      {
        @Override
        public void write(int b) throws IOException
        {
          // Do nothing.
        }
      });
    }
    catch (IOException ex)
    {
      // Can't happen.
      return null;
    }
  }

  public static boolean isValidFolder(File folder)
  {
    try
    {
      return folder.isDirectory() && folder.getAbsoluteFile().equals(folder.getCanonicalFile()) && folder.listFiles() != null;
    }
    catch (IOException ex)
    {
      return false;
    }
  }

  public static File getExistingFolder(File file)
  {
    if (file.isDirectory())
    {
      return file.getAbsoluteFile();
    }

    File parent = file.getParentFile();
    if (parent != null)
    {
      return getExistingFolder(parent);
    }

    return null;
  }

  public static File getCanonicalFile(File file)
  {
    try
    {
      return file.getCanonicalFile();
    }
    catch (IOException ex)
    {
      return file.getAbsoluteFile();
    }
  }

  public static File getFromPath(String command)
  {
    String path = System.getenv().get("PATH");

    StringTokenizer tokenizer = new StringTokenizer(path, File.pathSeparator);
    while (tokenizer.hasMoreTokens())
    {
      String folder = tokenizer.nextToken();
      File file = new File(folder, command);
      if (file.isFile())
      {
        return file;
      }
    }

    return null;
  }

  public static boolean isSerializeable(Object object)
  {
    try
    {
      DEV_NULL.writeObject(object);
      return true;
    }
    catch (Exception ex)
    {
      return false;
    }
  }

  public static byte[] serialize(Serializable object)
  {
    try
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      ObjectOutputStream stream = new ObjectOutputStream(baos);
      stream.writeObject(object);
      stream.flush();

      return baos.toByteArray();
    }
    catch (Exception ex)
    {
      return null;
    }
  }

  public static Serializable deserialize(byte[] bytes)
  {
    try
    {
      ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(bytes));
      return (Serializable)stream.readObject();
    }
    catch (Exception ex)
    {
      return null;
    }
  }

  public static Serializable deserialize(byte[] bytes, final ClassLoader classLoader)
  {
    try
    {
      ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(bytes))
      {
        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException
        {
          if (classLoader != null)
          {
            String className = desc.getName();

            try
            {
              Class<?> c = classLoader.loadClass(className);
              if (c != null)
              {
                return c;
              }
            }
            catch (ClassNotFoundException ex)
            {
              if (!StackTraceElement[].class.getName().equals(className))
              {
              }
            }
          }

          return super.resolveClass(desc);
        }
      };

      return (Serializable)stream.readObject();
    }
    catch (Exception ex)
    {
      return null;
    }
  }

  public static byte[] getSHA1(String contents) throws NoSuchAlgorithmException, IOException
  {
    return getSHA1(new ByteArrayInputStream(contents.getBytes()));
  }

  public static byte[] getSHA1(InputStream contents) throws NoSuchAlgorithmException, IOException
  {
    InputStream stream = null;

    try
    {
      final MessageDigest digest = MessageDigest.getInstance("SHA-1");
      stream = new FilterInputStream(contents)
      {
        @Override
        public int read() throws IOException
        {
          for (;;)
          {
            int ch = super.read();
            switch (ch)
            {
              case -1:
                return -1;

              case 10:
              case 13:
                continue;
            }

            digest.update((byte)ch);
            return ch;
          }
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException
        {
          int read = super.read(b, off, len);
          if (read == -1)
          {
            return -1;
          }

          for (int i = off; i < off + read; i++)
          {
            byte c = b[i];
            if (c == 10 || c == 13)
            {
              if (i + 1 < off + read)
              {
                System.arraycopy(b, i + 1, b, i, read - i - 1);
                --i;
              }

              --read;
            }
          }

          digest.update(b, off, read);
          return read;
        }
      };

      byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
      while (stream.read(bytes) != -1)
      {
        // Do nothing
      }

      return digest.digest();
    }
    finally
    {
      close(stream);
    }
  }

  public static String encodeFileName(String path)
  {
    String result = path.replace(':', '_').replace('/', '_').replace('\\', '_').replace('?', '_');

    int length = result.length();
    if (length > MAX_FILE_NAME_LENGTH)
    {
      String digest;

      try
      {
        byte[] bytes = getSHA1(result);
        digest = "-" + HexUtil.bytesToHex(bytes) + "-";
      }
      catch (Exception ex)
      {
        digest = "---" + result.hashCode() + "---";
      }

      int half = (MAX_FILE_NAME_LENGTH - digest.length() >> 1) - 1;
      result = result.substring(0, half) + digest + result.substring(result.length() - half);
    }

    return result;
  }

  public static String encodeImageData(String imageURI)
  {
    if (!imageURI.startsWith(IMAGE_DATA_PREFIX))
    {
      int lastDot = imageURI.lastIndexOf('.');
      if (lastDot != -1)
      {
        String extension = imageURI.substring(lastDot);
        InputStream in = null;

        try
        {
          in = new URL(imageURI).openStream();
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          copy(in, baos);

          String data = HexUtil.bytesToHex(baos.toByteArray());
          imageURI = IMAGE_DATA_PREFIX + data + extension;
        }
        catch (Exception ex)
        {
          //$FALL-THROUGH$
        }
        finally
        {
          closeSilent(in);
        }
      }
    }

    return imageURI;
  }

  public static String decodeImageData(String imageURI)
  {
    if (imageURI.startsWith(IMAGE_DATA_PREFIX))
    {
      String data = imageURI.substring(IMAGE_DATA_PREFIX.length());
      OutputStream out = null;

      try
      {
        byte[] digest = getSHA1(data);

        int lastDot = data.lastIndexOf('.');
        if (lastDot != -1)
        {
          String extension = data.substring(lastDot);
          data = data.substring(0, lastDot);

          File iconFile = new File(PropertiesUtil.getProperty("java.io.tmpdir"), "icon-" + HexUtil.bytesToHex(digest) + extension);
          if (!iconFile.exists())
          {
            ByteArrayInputStream bais = new ByteArrayInputStream(HexUtil.hexToBytes(data));
            out = new FileOutputStream(iconFile);
            copy(bais, out);
          }

          imageURI = iconFile.toURI().toString();
        }
      }
      catch (Exception ex)
      {
        //$FALL-THROUGH$
      }
      finally
      {
        closeSilent(out);
      }
    }

    return imageURI;
  }

  public static URI newURI(String uri) throws IORuntimeException
  {
    if (StringUtil.isEmpty(uri))
    {
      return null;
    }

    try
    {
      return new URI(uri);
    }
    catch (URISyntaxException ex)
    {
      // Should not happen.
      throw new IORuntimeException(ex);
    }
  }

  public static FileInputStream openInputStream(File file) throws IORuntimeException
  {
    try
    {
      return new FileInputStream(file);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static FileOutputStream openOutputStream(File file) throws IORuntimeException
  {
    try
    {
      mkdirs(file.getParentFile());
      return new FileOutputStream(file);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static Exception closeSilent(Closeable closeable)
  {
    try
    {
      if (closeable != null)
      {
        closeable.close();
      }

      return null;
    }
    catch (Exception ex)
    {
      return ex;
    }
  }

  public static Exception closeSilent(Socket socket)
  {
    try
    {
      if (socket != null)
      {
        socket.close();
      }

      return null;
    }
    catch (Exception ex)
    {
      return ex;
    }
  }

  public static Exception closeSilent(ServerSocket socket)
  {
    try
    {
      if (socket != null)
      {
        socket.close();
      }

      return null;
    }
    catch (Exception ex)
    {
      return ex;
    }
  }

  public static void close(Closeable closeable) throws IORuntimeException
  {
    try
    {
      if (closeable != null)
      {
        closeable.close();
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static void mkdirs(File folder) throws IORuntimeException
  {
    if (folder != null && !folder.exists())
    {
      if (!folder.mkdirs())
      {
        throw new IORuntimeException("Unable to create directory " + folder.getAbsolutePath()); //$NON-NLS-1$
      }
    }
  }

  public static File createTempFolder(String prefix, boolean deleteOnExit) throws IORuntimeException
  {
    try
    {
      File folder = File.createTempFile(prefix, "");
      deleteBestEffort(folder, deleteOnExit);
      mkdirs(folder);
      return folder;
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static boolean deleteBestEffort(File file)
  {
    return deleteBestEffort(file, true);
  }

  public static boolean deleteBestEffort(File file, boolean deleteOnExit)
  {
    boolean deleted = true;
    if (file != null)
    {
      if (file.isDirectory())
      {
        File[] children = file.listFiles();
        if (children != null)
        {
          for (File child : children)
          {
            deleted &= deleteBestEffort(child, deleteOnExit);
          }
        }
      }

      if (!file.delete())
      {
        deleted = false;

        if (deleteOnExit)
        {
          file.deleteOnExit();
        }
      }
    }

    return deleted;
  }

  public static long copy(InputStream input, OutputStream output, byte buffer[]) throws IORuntimeException
  {
    try
    {
      long length = 0;
      int n;

      while ((n = input.read(buffer)) != -1)
      {
        output.write(buffer, 0, n);
        length += n;
      }

      return length;
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static long copy(InputStream input, OutputStream output, int bufferSize) throws IORuntimeException
  {
    return copy(input, output, new byte[bufferSize]);
  }

  public static long copy(InputStream input, OutputStream output) throws IORuntimeException
  {
    return copy(input, output, DEFAULT_BUFFER_SIZE);
  }

  public static void copyTree(File source, File target, boolean bestEffort) throws IORuntimeException
  {
    if (source.isDirectory())
    {
      mkdirs(target);
      File[] files = source.listFiles();
      for (File file : files)
      {
        String name = file.getName();
        copyTree(new File(source, name), new File(target, name), bestEffort);
      }
    }
    else
    {
      try
      {
        copyFile(source, target);
      }
      catch (RuntimeException ex)
      {
        if (!bestEffort)
        {
          throw ex;
        }
      }
    }
  }

  public static void copyTree(File source, File target) throws IORuntimeException
  {
    copyTree(source, target, false);
  }

  public static void copyFile(File source, File target) throws IORuntimeException
  {
    mkdirs(target.getParentFile());

    FileInputStream input = null;
    FileOutputStream output = null;

    try
    {
      input = openInputStream(source);
      output = openOutputStream(target);
      copy(input, output);
    }
    finally
    {
      closeSilent(input);
      closeSilent(output);
    }
  }

  public static byte[] readFile(File file) throws IORuntimeException
  {
    if (file.length() > Integer.MAX_VALUE)
    {
      throw new IllegalArgumentException("File too long: " + file.length()); //$NON-NLS-1$
    }

    int size = (int)file.length();
    FileInputStream input = openInputStream(file);

    try
    {
      ByteArrayOutputStream output = new ByteArrayOutputStream(size);
      copy(input, output);
      return output.toByteArray();
    }
    finally
    {
      closeSilent(input);
    }
  }

  public static void writeFile(File file, byte[] bytes) throws IORuntimeException
  {
    FileOutputStream output = openOutputStream(file);

    try
    {
      ByteArrayInputStream input = new ByteArrayInputStream(bytes);
      copy(input, output);
    }
    finally
    {
      closeSilent(output);
    }
  }

  public static List<String> readLines(File file, String charsetName)
  {
    List<String> lines = new ArrayList<String>();

    if (file.exists())
    {
      InputStream in = null;

      try
      {
        in = new FileInputStream(file);
        return readLines(in, charsetName);
      }
      catch (IOException ex)
      {
        throw new IORuntimeException(ex);
      }
      finally
      {
        closeSilent(in);
      }
    }

    return lines;
  }

  public static List<String> readLines(InputStream in, String charsetName)
  {
    List<String> lines = new ArrayList<String>();

    Reader reader = null;
    BufferedReader bufferedReader = null;

    try
    {
      reader = charsetName == null ? new InputStreamReader(in) : new InputStreamReader(in, charsetName);
      bufferedReader = new BufferedReader(reader);

      String line;
      while ((line = bufferedReader.readLine()) != null)
      {
        lines.add(line);
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      closeSilent(bufferedReader);
      closeSilent(reader);
    }

    return lines;
  }

  public static void writeLines(File file, String charsetName, List<String> lines)
  {
    mkdirs(file.getParentFile());
    OutputStream out = null;

    try
    {
      out = new FileOutputStream(file);
      writeLines(out, charsetName, lines);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      closeSilent(out);
    }
  }

  public static void writeLines(OutputStream out, String charsetName, List<String> lines)
  {
    Writer writer = null;
    BufferedWriter bufferedWriter = null;

    try
    {
      writer = charsetName == null ? new OutputStreamWriter(out) : new OutputStreamWriter(out, charsetName);
      bufferedWriter = new BufferedWriter(writer);

      for (String line : lines)
      {
        bufferedWriter.write(line);
        bufferedWriter.write(StringUtil.NL);
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      closeSilent(bufferedWriter);
      closeSilent(writer);
    }
  }

  public static String readUTF8(File file) throws Exception
  {
    InputStream inputStream = new FileInputStream(file);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    try
    {
      copy(inputStream, outputStream);
    }
    finally
    {
      close(inputStream);
    }

    return new String(outputStream.toByteArray(), "UTF-8");
  }

  public static void writeUTF8(File file, String contents) throws Exception
  {
    mkdirs(file.getParentFile());

    InputStream inputStream = new ByteArrayInputStream(contents.getBytes("UTF-8"));
    OutputStream outputStream = new FileOutputStream(file);

    try
    {
      copy(inputStream, outputStream);
    }
    finally
    {
      close(outputStream);
    }
  }

  public static Object readObject(File file) throws Exception
  {
    return readObject(file, (ClassResolver)null);
  }

  public static Object readObject(File file, ClassLoader classLoader) throws Exception
  {
    return readObject(file, new ClassLoaderClassResolver(classLoader));
  }

  public static Object readObject(File file, final ClassResolver classResolver) throws Exception
  {
    InputStream inputStream = new FileInputStream(file);

    try
    {
      ObjectInputStream ois = new ObjectInputStream(inputStream)
      {
        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException
        {
          if (classResolver != null)
          {
            Class<?> c = classResolver.resolveClass(desc);
            if (c != null)
            {
              return c;
            }
          }

          return super.resolveClass(desc);
        }
      };

      return ois.readObject();
    }
    finally
    {
      close(inputStream);
    }
  }

  public static void writeObject(File file, Object object) throws Exception
  {
    mkdirs(file.getParentFile());

    OutputStream outputStream = new FileOutputStream(file);

    try
    {
      @SuppressWarnings("resource")
      ObjectOutputStream oos = new ObjectOutputStream(outputStream);
      oos.writeObject(object);
      oos.flush();
    }
    finally
    {
      close(outputStream);
    }
  }

  public static String readXML(InputStream inputStream) throws Exception
  {
    try
    {
      DocumentBuilder documentBuilder = XMLUtil.createDocumentBuilder();
      Document document = documentBuilder.parse(inputStream);

      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();

      StringWriter out = new StringWriter();
      transformer.transform(new DOMSource(document), new StreamResult(out));
      out.close();

      return out.toString();
    }
    finally
    {
      IOUtil.close(inputStream);
    }
  }

  public static List<File> listDepthFirst(File file)
  {
    FileCollector collector = new FileCollector();
    visitDepthFirst(file, collector);
    return collector.getFiles();
  }

  public static List<File> listBreadthFirst(File file)
  {
    FileCollector collector = new FileCollector();
    visitBreadthFirst(file, collector);
    return collector.getFiles();
  }

  private static void visitDepthFirst(File file, IOVisitor visitor) throws IORuntimeException
  {
    try
    {
      boolean recurse = visitor.visit(file);
      if (recurse && file.isDirectory())
      {
        visitDepthFirst(file.listFiles(), visitor);
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  private static void visitDepthFirst(File[] files, IOVisitor visitor)
  {
    for (File file : files)
    {
      visitDepthFirst(file, visitor);
    }
  }

  private static void visitBreadthFirst(File file, IOVisitor visitor) throws IORuntimeException
  {
    File[] files = { file };
    visitBreadthFirst(files, visitor);
  }

  private static void visitBreadthFirst(File[] files, IOVisitor visitor) throws IORuntimeException
  {
    try
    {
      boolean[] recurse = new boolean[files.length];
      for (int i = 0; i < files.length; i++)
      {
        File file = files[i];
        recurse[i] = visitor.visit(file);
      }

      for (int i = 0; i < files.length; i++)
      {
        File file = files[i];
        if (file.isDirectory() && recurse[i])
        {
          File[] children = file.listFiles();
          for (File child : children)
          {
            visitBreadthFirst(child, visitor);
          }
        }
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  /**
   * @author Eike Stepper
   */
  private interface IOVisitor
  {
    public boolean visit(File file) throws IOException;
  }

  /**
   * @author Eike Stepper
   */
  private static class FileCollector implements IOVisitor
  {
    private List<File> files = new ArrayList<File>();

    public FileCollector()
    {
    }

    public List<File> getFiles()
    {
      return files;
    }

    public boolean visit(File file) throws IOException
    {
      files.add(file);
      return true;
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface ClassResolver
  {
    public Class<?> resolveClass(ObjectStreamClass v) throws ClassNotFoundException;
  }

  /**
   * @author Eike Stepper
   */
  public static class ClassLoaderClassResolver implements ClassResolver
  {
    private static final String STACK_TRACE_ELEMENT = StackTraceElement[].class.getName();

    private final ClassLoader classLoader;

    public ClassLoaderClassResolver(ClassLoader classLoader)
    {
      this.classLoader = classLoader;
    }

    public Class<?> resolveClass(ObjectStreamClass v) throws ClassNotFoundException
    {
      String className = v.getName();

      try
      {
        return classLoader.loadClass(className);
      }
      catch (ClassNotFoundException ex)
      {
        if (!STACK_TRACE_ELEMENT.equals(className))
        {
        }
      }

      return null;
    }
  }
}
