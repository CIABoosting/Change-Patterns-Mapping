/* 
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * MacOSHandler.java - Various handlers for Mac OS Plugin
 * Copyright (C) 2002 Kris Kopicki
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
 
import com.apple.mrj.*;
import java.io.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;
public class MacOSHandler implements MRJQuitHandler, MRJAboutHandler,
	MRJOpenDocumentHandler, MRJPrefsHandler, Handler
{
	
    private String		lastOpenFile;
	private ExitThread	et = new ExitThread();
	
	private final MRJOSType defaultType = new MRJOSType(jEdit.getProperty("MacOSPlugin.default.type"));
	private final MRJOSType defaultCreator = new MRJOSType(jEdit.getProperty("MacOSPlugin.default.creator"));
	public MacOSHandler()
	{
		if (jEdit.getBooleanProperty("MacOSPlugin.useScreenMenuBar",
			jEdit.getBooleanProperty("MacOSPlugin.default.useScreenMenuBar"))
		)
			System.setProperty("com.apple.macos.useScreenMenuBar","true");
		else
			System.setProperty("com.apple.macos.useScreenMenuBar","false");
		
		if (jEdit.getBooleanProperty("MacOSPlugin.liveResize",
			jEdit.getBooleanProperty("MacOSPlugin.default.liveResize"))
		)
			System.setProperty("com.apple.mrj.application.live-resize","true");
		else
			System.setProperty("com.apple.mrj.application.live-resize","false");
	}
	public void handleQuit()
    {
		if (!et.isAlive())
			et.start();
		else
			Log.log(Log.DEBUG,this,"ExitThread still alive.");
	}
	public void handleAbout()
    {
		new AboutDialog(jEdit.getLastView());
	}
	public void handlePrefs()
	{
		new OptionsDialog(jEdit.getLastView());
	}
	public void handleOpenFile(File file)
    {
		if (jEdit.openFile(jEdit.getLastView(),file.getPath()) != null)
        {
            lastOpenFile = file.getPath();
        } else {
            Log.log(Log.ERROR,this,"Error opening file.");
        }
	}
	public void handleOpenFile(ViewUpdate msg)
    {
		if(msg.getWhat() == ViewUpdate.CREATED)
		{
			if(lastOpenFile != null)
			{
				jEdit.getLastView().setBuffer(jEdit.getBuffer(lastOpenFile));
			}
			((MacOSPlugin)jEdit.getPlugin("MacOSPlugin")).started(true);
		}
	}
	public void handleFileCodes(BufferUpdate msg)
	{
		Buffer buffer = msg.getBuffer();
		
		if (!buffer.isDirty() && msg.getWhat() == BufferUpdate.DIRTY_CHANGED)
		{
			try
			{
				MRJFileUtils.setFileTypeAndCreator( buffer.getFile(),
					(MRJOSType)buffer.getProperty("MacOSPlugin.type"),
					(MRJOSType)buffer.getProperty("MacOSPlugin.creator") );
			}
			catch (Exception e)
			{
				Log.log(Log.ERROR,this,"Error setting type/creator for "+
					buffer.getFile().getPath());
			}
		}
		else if (msg.getWhat() == BufferUpdate.CREATED )
		{
			buffer.setProperty("MacOSPlugin.type",defaultType);
			buffer.setProperty("MacOSPlugin.creator",defaultCreator);
			
			if (jEdit.getBooleanProperty("MacOSPlugin.preserveCodes",
				jEdit.getBooleanProperty("MacOSPlugin.default.preserveCodes")))
			{
				try
				{
					MRJOSType	type	= MRJFileUtils.getFileType(buffer.getFile());
					MRJOSType	creator	= MRJFileUtils.getFileCreator(buffer.getFile());
					
					if (!type.equals(new MRJOSType("")))
						buffer.setProperty("MacOSPlugin.type",type);
					if (!creator.equals(new MRJOSType("")))
						buffer.setProperty("MacOSPlugin.creator",creator);
				}
				catch (Exception e) {} 
			}
			Log.log(Log.DEBUG,this,"Assigned MRJOSTypes " + buffer.getProperty("MacOSPlugin.type")
			+ "/" + buffer.getProperty("MacOSPlugin.creator") + " to " + buffer.getPath());
		}
	}
	class ExitThread extends Thread
	{
		public void run()
		{
			jEdit.exit(jEdit.getLastView(),false);
			et = new ExitThread();
		}
	}
}
