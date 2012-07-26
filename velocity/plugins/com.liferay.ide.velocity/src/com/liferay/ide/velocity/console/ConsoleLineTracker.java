package com.liferay.ide.velocity.console;

import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.console.IPatternMatchListener;

/**
 * We use this class to install our PatternMatcherListener for inserting
 * hyperlinks for files into the console display.
 */

public class ConsoleLineTracker implements IConsoleLineTracker
{

  public void dispose()
  {
  }

  public void init(IConsole console)
  {
    IPatternMatchListener listener = new PatternMatchListener();
    console.addPatternMatchListener(listener);
  }
  
  public void lineAppended(IRegion line)
  {
  }
  
}
