package com.liferay.ide.velocity.console;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.liferay.ide.velocity.vaulttec.ui.IPreferencesConstants;
import com.liferay.ide.velocity.vaulttec.ui.VelocityPlugin;


/**
 * We use this to create hyperlinks in the debug console so that the 
 * user may click on a hyperlink in the debug output and be taken directly
 * to that file's editor at the given line number.  This class scans
 * the console debug output and creates hyperlinks whenever it detects
 * a file pattern.
 */

public class PatternMatchListener implements IPatternMatchListener
{
  TextConsole console = null;

  public void connect(TextConsole console)
  {
    this.console = console;
  }

  public void disconnect()
  {
  }

  
  Pattern filere = Pattern.compile("(\\S+\\.(vm|vsl|vtl|html|xml))\\[.*?(\\d+),.*?(\\d+)]");

  public void matchFound(PatternMatchEvent event)
  {
    Preferences prefs = VelocityPlugin.getDefault().getPluginPreferences();
    String base = prefs.getString(IPreferencesConstants.VELOCITY_RESOURCE_DIR);
    
    try
    {
      String str = console.getDocument().get(event.getOffset(), event.getLength());
      Matcher matcher = filere.matcher(str);
      if (matcher.matches())
      {     
        String filename = matcher.group(1);
        String filestr = null;
        if (!filename.startsWith("/") && !filename.startsWith("\\"))
        {          
          // Add a path seperator if the filename does not already have one at the beginning
          filestr = base + "/" + matcher.group(1);
        }
        else
        {
          filestr = base + matcher.group(1);          
        }
        
        int linenum = Integer.parseInt(matcher.group(3));
        int colnum = Integer. parseInt(matcher.group(4));
        
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(filestr)); 
        MyHyperLink link = new MyHyperLink();
        link.file = file;
        link.linenum = linenum;
        link.colnum = colnum;
        console.addHyperlink(link, event.getOffset(), event.getLength());
      }
    } 
    catch (BadLocationException e)
    {
    }    
  }

  public int getCompilerFlags()
  {
    return 0;
  }

  public String getLineQualifier()
  {
    return null;
  }

  public String getPattern()
  {
    return "\\S+\\.(vm|vsl|vtl|html|xml)\\[.+,.+\\]";
  }
  
  /**
   * The Hyperlink class contains the logic for finding the editor the hyperlink 
   * refers to, and opening the editor for the given file.
   */
  public static class MyHyperLink implements IHyperlink
  {
    IFile file = null;
    int linenum = 1;
    int colnum = 1;
    
    
    public void linkActivated()
    {     
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      try
      {
        ITextEditor teditor = null;
        
        IEditorPart part = IDE.openEditor(page, file);

        if (part instanceof MultiPageEditorPart)
        {
          MultiPageEditorPart editor = (MultiPageEditorPart)part;
          IEditorPart[] editors = editor.findEditors(part.getEditorInput());
          teditor = (ITextEditor)editors[0];
        }
        else if (!(part instanceof ITextEditor))
        {
          // Not sure what to do with this
          return;
        }
        else
        {
          teditor = (ITextEditor)part;
        }
        
        IDocumentProvider provider = teditor.getDocumentProvider();
        IDocument document = provider.getDocument(teditor.getEditorInput());
        int start = document.getLineOffset(linenum-1); 
        teditor.selectAndReveal(start + colnum -1, 0);                
      } 
      catch (PartInitException e)
      {
      }
      catch (BadLocationException e)
      {        
      }
    }

    public void linkEntered()
    {
    }

    public void linkExited()
    {
    }
    
  }

}
