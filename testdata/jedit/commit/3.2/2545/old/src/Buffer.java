/*
 * Buffer.java - jEdit buffer
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1998, 1999, 2000, 2001, 2002 Slava Pestov
 * Portions copyright (C) 1999, 2000 mike dillon
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

package org.gjt.sp.jedit;

import gnu.regexp.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.Toolkit;
import java.io.File;
import java.util.*;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.buffer.*;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.search.RESearchMatcher;
import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.*;
/**
 * An in-memory copy of an open file.
 *
 * This class is partially thread-safe, however you must pay attention to a few
 * very important issues:
 * <ul>
 * <li>Changes to a buffer can only be made from the AWT thread.
 * <li>When accessing the buffer from another thread, you must
 * grab a read lock if you plan on performing more than one call, to ensure that
 * the buffer contents are not changed by the AWT thread for the duration of the
 * lock.
 * <li>
 *
 * @author Slava Pestov
 * @version $Id$
 */
public class Buffer implements EBComponent
{
	/**
	 * Line separator property.
	 */
	public static final String LINESEP = "lineSeparator";

	/**
	 * Backed up property.
	 * @since jEdit 3.2pre2
	 */
	public static final String BACKED_UP = "Buffer__backedUp";

	/**
	 * Caret info properties.
	 * @since jEdit 3.2pre1
	 */
	public static final String CARET = "Buffer__caret";
	public static final String SELECTION = "Buffer__selection";

	/**
	 * This should be a physical line number, so that the scroll
	 * position is preserved correctly across reloads (which will
	 * affect virtual line numbers, due to fold being reset)
	 */
	public static final String SCROLL_VERT = "Buffer__scrollVert";
	public static final String SCROLL_HORIZ = "Buffer__scrollHoriz";

	/**
	 * Character encoding used when loading and saving.
	 * @since jEdit 3.2pre4
	 */
	public static final String ENCODING = "encoding";

	/**
	 * This property is set to 'true' if the file has a trailing newline.
	 * @since jEdit 4.0pre1
	 */
	public static final String TRAILING_EOL = "trailingEOL";

	/**
	 * This property is set to 'true' if the file should be GZipped.
	 * @since jEdit 4.0pre4
	 */
	public static final String GZIPPED = "gzipped";
	/**
	 * Displays the 'insert file' dialog box and inserts the selected file
	 * into the buffer.
	 * @param view The view
	 * @since jEdit 2.7pre2
	 */
	public void showInsertFileDialog(View view)
	{
		String[] files = GUIUtilities.showVFSFileDialog(view,null,
			VFSBrowser.OPEN_DIALOG,false);

		if(files != null)
			insertFile(view,files[0]);
	} 
	/**
	 * Reloads the buffer from disk, asking for confirmation if the buffer
	 * is dirty.
	 * @param view The view
	 * @since jEdit 2.7pre2
	 */
	public void reload(View view)
	{
		if(getFlag(DIRTY))
		{
			String[] args = { name };
			int result = GUIUtilities.confirm(view,"changedreload",
				args,JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);
			if(result != JOptionPane.YES_OPTION)
				return;
		}

		view.getEditPane().saveCaretInfo();
		load(view,true);
	} 
	/**
	 * Loads the buffer from disk, even if it is loaded already.
	 * @param view The view
	 * @param reload If true, user will not be asked to recover autosave
	 * file, if any
	 *
	 * @since 2.5pre1
	 */
	public boolean load(final View view, final boolean reload)
	{
		if(isPerformingIO())
		{
			GUIUtilities.error(view,"buffer-multiple-io",null);
			return false;
		}

		setBooleanProperty(BufferIORequest.ERROR_OCCURRED,false);

		setFlag(LOADING,true);

		if(!getFlag(TEMPORARY))
			EditBus.send(new BufferUpdate(this,view,BufferUpdate.LOAD_STARTED));

		final boolean loadAutosave;

		if(reload || !getFlag(NEW_FILE))
		{
			if(file != null)
				modTime = file.lastModified();

			if(!reload && autosaveFile != null && autosaveFile.exists())
				loadAutosave = recoverAutosave(view);
			else
			{
				if(autosaveFile != null)
					autosaveFile.delete();
				loadAutosave = false;
			}

			if(!loadAutosave)
			{
				if(!vfs.load(view,this,path))
				{
					setFlag(LOADING,false);
					return false;
				}
			}
		}
		else
			loadAutosave = false;

		Runnable runnable = new Runnable()
		{
			public void run()
			{
				String newPath = getStringProperty(
					BufferIORequest.NEW_PATH);
				Segment seg = (Segment)getProperty(
					BufferIORequest.LOAD_DATA);
				IntegerArray endOffsets = (IntegerArray)
					getProperty(BufferIORequest.END_OFFSETS);

				boolean readOnly = isReadOnly();
				setFlag(READ_ONLY,false);

				remove(0,getLength());

				if(seg != null && endOffsets != null)
				{
					try
					{
						writeLock();

						contentMgr._setContent(seg.array,seg.count);

						contentInserted(0,seg.count,endOffsets);
					}
					catch(OutOfMemoryError oom)
					{
						Log.log(Log.ERROR,this,oom);
						VFSManager.error(view,path,"out-of-memory-error",null);
					}
					finally
					{
						writeUnlock();
					}
				}

				setFlag(READ_ONLY,readOnly);

				unsetProperty(BufferIORequest.LOAD_DATA);
				unsetProperty(BufferIORequest.END_OFFSETS);
				unsetProperty(BufferIORequest.NEW_PATH);

				undoMgr.clear();
				undoMgr.setLimit(jEdit.getIntegerProperty(
					"buffer.undoCount",100));

				if(!getFlag(TEMPORARY))
					finishLoading();

				/* Ultra-obscure: we have to fire this event
				 * after the buffer might have been collapsed
				 * by finishLoading(), since finishLoading(),
				 * unlike "official" fold APIs, does not notify
				 * the text area to invalidate its cached
				 * virtual to physical information. Note that
				 * the text area's contentInserted() handler
				 * updates 'lastPhysLine' even if the LOADING
				 * flag is set. */
				fireContentInserted(0,0,getLineCount(),getLength() - 1);

				setFlag(LOADING,false);

				if(reload)
					setDirty(false);

				if(!loadAutosave && newPath != null && !path.equals(newPath))
					setPath(newPath);

				if(loadAutosave)
					setFlag(DIRTY,true);

				if(!getFlag(TEMPORARY))
				{
					EditBus.send(new BufferUpdate(Buffer.this,
						view,BufferUpdate.LOADED));
				}
			}
		}; 
		if(getFlag(TEMPORARY))
			runnable.run();
		else
			VFSManager.runInAWTThread(runnable);

		return true;
	} 
	/**
	 * Loads a file from disk, and inserts it into this buffer.
	 * @param view The view
	 *
	 * @since 4.0pre1
	 */
	public boolean insertFile(final View view, String path)
	{
		if(isPerformingIO())
		{
			GUIUtilities.error(view,"buffer-multiple-io",null);
			return false;
		}

		setBooleanProperty(BufferIORequest.ERROR_OCCURRED,false);

		path = MiscUtilities.constructPath(this.path,path);

		Buffer buffer = jEdit.getBuffer(path);
		if(buffer != null)
		{
			view.getTextArea().setSelectedText(
				buffer.getText(0,buffer.getLength()));
			return true;
		}

		VFS vfs = VFSManager.getVFSForPath(path);

		setFlag(IO,true);

		if(!vfs.insert(view,this,path))
		{
			setFlag(IO,false);
			return false;
		}

		VFSManager.runInAWTThread(new Runnable()
		{
			public void run()
			{
				setFlag(IO,false);

				SegmentBuffer sbuf = (SegmentBuffer)getProperty(
					BufferIORequest.LOAD_DATA);
				if(sbuf != null)
				{
					unsetProperty(BufferIORequest.LOAD_DATA);

					view.getTextArea().setSelectedText(sbuf.toString());
				}
			}
		});

		return true;
	} 
	/**
	 * Autosaves this buffer.
	 */
	public void autosave()
	{
		if(autosaveFile == null || !getFlag(AUTOSAVE_DIRTY)
			|| !getFlag(DIRTY)
			|| getFlag(LOADING)
			|| getFlag(IO))
			return;

		setFlag(AUTOSAVE_DIRTY,false);

		VFSManager.runInWorkThread(new BufferIORequest(
			BufferIORequest.AUTOSAVE,null,this,null,
			VFSManager.getFileVFS(),autosaveFile.getPath()));
	} 
	/**
	 * Prompts the user for a file to save this buffer to.
	 * @param view The view
	 * @param rename True if the buffer's path should be changed, false
	 * if only a copy should be saved to the specified filename
	 * @since jEdit 2.6pre5
	 */
	public boolean saveAs(View view, boolean rename)
	{
		String[] files = GUIUtilities.showVFSFileDialog(view,path,
			VFSBrowser.SAVE_DIALOG,false);

		if(files == null)
			return false;

		return save(view,files[0],rename);
	} 
	/**
	 * Saves this buffer to the specified path name, or the current path
	 * name if it's null.
	 * @param view The view
	 * @param path The path name to save the buffer to, or null to use
	 * the existing path
	 */
	public boolean save(View view, String path)
	{
		return save(view,path,true);
	} 
	/**
	 * Saves this buffer to the specified path name, or the current path
	 * name if it's null.
	 * @param view The view
	 * @param path The path name to save the buffer to, or null to use
	 * the existing path
	 * @param rename True if the buffer's path should be changed, false
	 * if only a copy should be saved to the specified filename
	 * @since jEdit 2.6pre5
	 */
	public boolean save(final View view, String path, final boolean rename)
	{
		if(isPerformingIO())
		{
			GUIUtilities.error(view,"buffer-multiple-io",null);
			return false;
		}

		setBooleanProperty(BufferIORequest.ERROR_OCCURRED,false);

		if(path == null && getFlag(NEW_FILE))
			return saveAs(view,rename);

		if(path == null && file != null)
		{
			long newModTime = file.lastModified();

			if(newModTime != modTime)
			{
				Object[] args = { this.path };
				int result = GUIUtilities.confirm(view,
					"filechanged-save",args,
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
				if(result != JOptionPane.YES_OPTION)
					return false;
			}
		}

		setFlag(IO,true);
		EditBus.send(new BufferUpdate(this,view,BufferUpdate.SAVING));

		final String oldPath = this.path;
		final String newPath = (path == null ? this.path : path);

		VFS vfs = VFSManager.getVFSForPath(newPath);

		if(!vfs.save(view,this,newPath))
		{
			setFlag(IO,false);
			return false;
		}

		VFSManager.runInAWTThread(new Runnable()
		{
			public void run()
			{
				setFlag(IO,false);
				finishSaving(view,oldPath,newPath,rename,
					getBooleanProperty(BufferIORequest
					.ERROR_OCCURRED));
			}
		});

		return true;
	} 
	public static final int FILE_NOT_CHANGED = 0;
	public static final int FILE_CHANGED = 1;
	public static final int FILE_DELETED = 2;
	/**
	 * Check if the buffer has changed on disk.
	 * @return One of <code>NOT_CHANGED</code>, <code>CHANGED</code>, or
	 * <code>DELETED</code>.
	 *
	 * @since jEdit 4.1pre3
	 */
	public int checkFileStatus()
	{
		if(!getFlag(IO) && !getFlag(LOADING) && file != null)
		{
			boolean newReadOnly = (file.exists() && !file.canWrite());
			if(newReadOnly != getFlag(READ_ONLY))
			{
				setFlag(READ_ONLY,newReadOnly);
				EditBus.send(new BufferUpdate(this,null,
					BufferUpdate.DIRTY_CHANGED));
			}

			long oldModTime = modTime;
			long newModTime = file.lastModified();

			if(newModTime != oldModTime)
			{
				modTime = newModTime;

				if(!file.exists())
				{
					setFlag(NEW_FILE,true);
					EditBus.send(new BufferUpdate(this,null,
						BufferUpdate.DIRTY_CHANGED));
					return FILE_DELETED;
				}
				else
					return FILE_CHANGED;
			}
		}

		return FILE_NOT_CHANGED;
	} 
	/**
	 * Check if the buffer has changed on disk.
	 */
	public void checkModTime(View view)
	{
		if(file == null || !jEdit.getBooleanProperty("view.checkModStatus"))
			return;

		int status = checkFileStatus();

		if(status == FILE_DELETED)
		{
			Object[] args = { path };
			GUIUtilities.message(view,"filedeleted",args);
		}
		else if(status == FILE_CHANGED)
		{
			String prop = (isDirty() ? "filechanged-dirty"
				: "filechanged-focus");

			Object[] args = { path };
			int result = GUIUtilities.confirm(view,
				prop,args,JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);
			if(result == JOptionPane.YES_OPTION)
			{
				view.getEditPane().saveCaretInfo();
				load(view,true);
			}
		}
	} 
	/**
	 * Returns the last time jEdit modified the file on disk.
	 */
	public long getLastModified()
	{
		return modTime;
	} 
	/**
	 * Sets the last time jEdit modified the file on disk.
	 * @param modTime The new modification time
	 */
	public void setLastModified(long modTime)
	{
		this.modTime = modTime;
	} 
	/**
	 * Returns the virtual filesystem responsible for loading and
	 * saving this buffer.
	 */
	public VFS getVFS()
	{
		return vfs;
	} 
	/**
	 * Returns the autosave file for this buffer. This may be null if
	 * the file is non-local.
	 */
	public final File getAutosaveFile()
	{
		return autosaveFile;
	} 
	/**
	 * Returns the name of this buffer.
	 */
	public final String getName()
	{
		return name;
	} 
	/**
	 * Returns the path name of this buffer.
	 */
	public final String getPath()
	{
		return path;
	} 
	/**
	 * Returns true if this buffer has been closed with
	 * <code>jEdit.closeBuffer()</code>.
	 */
	public final boolean isClosed()
	{
		return getFlag(CLOSED);
	} 
	/**
	 * Returns true if the buffer is loaded.
	 */
	public final boolean isLoaded()
	{
		return !getFlag(LOADING);
	} 
	/**
	 * Returns true if the buffer is currently performing I/O.
	 * @since jEdit 2.7pre1
	 */
	public final boolean isPerformingIO()
	{
		return getFlag(LOADING) || getFlag(IO);
	} 
	/**
	 * Returns true if this file doesn't exist on disk.
	 */
	public final boolean isNewFile()
	{
		return getFlag(NEW_FILE);
	} 
	/**
	 * Sets the new file flag.
	 * @param newFile The new file flag
	 */
	public final void setNewFile(boolean newFile)
	{
		setFlag(NEW_FILE,newFile);
		if(!newFile)
			setFlag(UNTITLED,false);
	} 
	/**
	 * Returns true if this file is 'untitled'.
	 */
	public final boolean isUntitled()
	{
		return getFlag(UNTITLED);
	} 
	/**
	 * Returns true if this file has changed since last save, false
	 * otherwise.
	 */
	public final boolean isDirty()
	{
		return getFlag(DIRTY);
	} 
	/**
	 * Returns true if this file is read only, false otherwise.
	 */
	public final boolean isReadOnly()
	{
		return getFlag(READ_ONLY);
	} 
	/**
	 * Returns true if this file is editable, false otherwise.
	 * @since jEdit 2.7pre1
	 */
	public final boolean isEditable()
	{
		return !(getFlag(READ_ONLY) || getFlag(IO) || getFlag(LOADING));
	} 
	/**
	 * Sets the read only flag.
	 * @param readOnly The read only flag
	 */
	public final void setReadOnly(boolean readOnly)
	{
		setFlag(READ_ONLY,readOnly);
	} 
	/**
	 * Sets the `dirty' (changed since last save) flag of this buffer.
	 */
	public void setDirty(boolean d)
	{
		boolean old_d = getFlag(DIRTY);

		if(d)
		{
			if(getFlag(LOADING) || getFlag(READ_ONLY))
				return;
			if(getFlag(DIRTY) && getFlag(AUTOSAVE_DIRTY))
				return;
			setFlag(DIRTY,true);
			setFlag(AUTOSAVE_DIRTY,true);
		}
		else
		{
			setFlag(DIRTY,false);
			setFlag(AUTOSAVE_DIRTY,false);

			undoMgr.bufferSaved();
		}

		if(d != old_d)
		{
			EditBus.send(new BufferUpdate(this,null,
				BufferUpdate.DIRTY_CHANGED));
		}
	} 
	/**
	 * Returns if this is a temporary buffer.
	 * @see jEdit#openTemporary(View,String,String,boolean)
	 * @see jEdit#commitTemporary(Buffer)
	 * @since jEdit 2.2pre7
	 */
	public boolean isTemporary()
	{
		return getFlag(TEMPORARY);
	} 
	/**
	 * Returns this buffer's icon.
	 * @since jEdit 2.6pre6
	 */
	public Icon getIcon()
	{
		if(getFlag(DIRTY))
			return GUIUtilities.DIRTY_BUFFER_ICON;
		else if(getFlag(READ_ONLY))
			return GUIUtilities.READ_ONLY_BUFFER_ICON;
		else if(getFlag(NEW_FILE))
			return GUIUtilities.NEW_BUFFER_ICON;
		else
			return GUIUtilities.NORMAL_BUFFER_ICON;
	} 
	/**
	 * The buffer is guaranteed not to change between calls to
	 * <code>readLock()</code> and <code>readUnlock()</code>.
	 */
	public final void readLock()
	{
		lock.readLock();
	} 
	/**
	 * The buffer is guaranteed not to change between calls to
	 * <code>readLock()</code> and <code>readUnlock()</code>.
	 */
	public final void readUnlock()
	{
		lock.readUnlock();
	} 
	/**
	 * The buffer cintents are guaranteed not to be read or written
	 * by other threads between calls to <code>writeLock()</code>
	 * and <code>writeUnlock()</code>.
	 */
	public final void writeLock()
	{
		lock.writeLock();
	} 
	/**
	 * The buffer cintents are guaranteed not to be read or written
	 * by other threads between calls to <code>writeLock()</code>
	 * and <code>writeUnlock()</code>.
	 */
	public final void writeUnlock()
	{
		lock.writeUnlock();
	} 
	/**
	 * Returns the number of characters in the buffer.
	 */
	public int getLength()
	{
		return contentMgr.getLength();
	} 
	/**
	 * Returns the number of physical lines in the buffer.
	 * This method is thread-safe.
	 * @since jEdit 3.1pre1
	 */
	public int getLineCount()
	{
		return offsetMgr.getLineCount();
	} 
	/**
	 * Returns the line containing the specified offset.
	 * This method is thread-safe.
	 * @param offset The offset
	 * @since jEdit 4.0pre1
	 */
	public final int getLineOfOffset(int offset)
	{
		try
		{
			readLock();

			if(offset < 0 || offset > getLength())
				throw new ArrayIndexOutOfBoundsException(offset);

			return offsetMgr.getLineOfOffset(offset);
		}
		finally
		{
			readUnlock();
		}
	} 
	/**
	 * Returns the start offset of the specified line.
	 * This method is thread-safe.
	 * @param line The line
	 * @return The start offset of the specified line
	 * @since jEdit 4.0pre1
	 */
	public int getLineStartOffset(int line)
	{
		try
		{
			readLock();

			if(line < 0 || line >= offsetMgr.getLineCount())
				throw new ArrayIndexOutOfBoundsException(line);
			else if(line == 0)
				return 0;

			return offsetMgr.getLineEndOffset(line - 1);
		}
		finally
		{
			readUnlock();
		}
	} 
	/**
	 * Returns the end offset of the specified line.
	 * This method is thread-safe.
	 * @param line The line
	 * @return The end offset of the specified line
	 * invalid.
	 * @since jEdit 4.0pre1
	 */
	public int getLineEndOffset(int line)
	{
		try
		{
			readLock();

			if(line < 0 || line >= offsetMgr.getLineCount())
				throw new ArrayIndexOutOfBoundsException(line);

			return offsetMgr.getLineEndOffset(line);
		}
		finally
		{
			readUnlock();
		}
	} 
	/**
	 * Returns the length of the specified line.
	 * This method is thread-safe.
	 * @param line The line
	 * @since jEdit 4.0pre1
	 */
	public int getLineLength(int line)
	{
		try
		{
			readLock();

			return getLineEndOffset(line)
				- getLineStartOffset(line) - 1;
		}
		finally
		{
			readUnlock();
		}
	} 
	/**
	 * Returns the text on the specified line.
	 * This method is thread-safe.
	 * @param lineIndex The line
	 * @return The text, or null if the line is invalid
	 * @since jEdit 4.0pre1
	 */
	public String getLineText(int lineIndex)
	{
		try
		{
			readLock();

			return getText(getLineStartOffset(lineIndex),
				getLineLength(lineIndex));
		}
		finally
		{
			readUnlock();
		}
	} 
	/**
	 * Copies the text on the specified line into a segment.
	 * This method is thread-safe.
	 * @param lineIndex The line
	 * @since jEdit 4.0pre1
	 */
	public void getLineText(int lineIndex, Segment segment)
	{
		try
		{
			readLock();

			getText(getLineStartOffset(lineIndex),
				getLineLength(lineIndex),segment);
		}
		finally
		{
			readUnlock();
		}
	} 
	/**
	 * Returns the specified text range.
	 * @param start The start offset
	 * @param length The number of characters to get
	 */
	public String getText(int start, int length)
	{
		try
		{
			readLock();

			if(start < 0 || length < 0
				|| start + length > contentMgr.getLength())
				throw new ArrayIndexOutOfBoundsException(start + ":" + length);

			return contentMgr.getText(start,length);
		}
		finally
		{
			readUnlock();
		}
	} 
	/**
	 * Returns the specified text range.
	 * @param start The start offset
	 * @param length The number of characters to get
	 * @param seg The segment to copy the text to
	 */
	public void getText(int start, int length, Segment seg)
	{
		try
		{
			readLock();

			if(start < 0 || length < 0
				|| start + length > contentMgr.getLength())
				throw new ArrayIndexOutOfBoundsException(start + ":" + length);

			contentMgr.getText(start,length,seg);
		}
		finally
		{
			readUnlock();
		}
	} 
	/**
	 * Inserts a string into the buffer.
	 * @param offset The offset
	 * @param str The string
	 * @since jEdit 4.0pre1
	 */
	public void insert(int offset, String str)
	{
		if(str == null || str.length() == 0)
			return;

		if(isReadOnly())
			throw new RuntimeException("buffer read-only");

		try
		{
			writeLock();

			if(offset < 0 || offset > contentMgr.getLength())
				throw new ArrayIndexOutOfBoundsException(offset);

			contentMgr.insert(offset,str);

			integerArray.clear();

			for(int i = 0; i < str.length(); i++)
			{
				if(str.charAt(i) == '\n')
					integerArray.add(i);
			}

			if(!getFlag(UNDO_IN_PROGRESS))
			{
				undoMgr.contentInserted(offset,str.length(),str,
					!getFlag(DIRTY));
			}

			contentInserted(offset,str.length(),integerArray);
		}
		finally
		{
			writeUnlock();
		}
	} 
	/**
	 * Inserts a string into the buffer.
	 * @param offset The offset
	 * @param seg The segment
	 * @since jEdit 4.0pre1
	 */
	public void insert(int offset, Segment seg)
	{
		if(seg.count == 0)
			return;

		if(isReadOnly())
			throw new RuntimeException("buffer read-only");

		try
		{
			writeLock();

			if(offset < 0 || offset > contentMgr.getLength())
				throw new ArrayIndexOutOfBoundsException(offset);

			contentMgr.insert(offset,seg);

			integerArray.clear();

			for(int i = 0; i < seg.count; i++)
			{
				if(seg.array[seg.offset + i] == '\n')
					integerArray.add(i);
			}

			if(!getFlag(UNDO_IN_PROGRESS))
			{
				undoMgr.contentInserted(offset,seg.count,
					seg.toString(),!getFlag(DIRTY));
			}

			contentInserted(offset,seg.count,integerArray);
		}
		finally
		{
			writeUnlock();
		}
	} 
	/**
	 * Removes the specified rang efrom the buffer.
	 * @param offset The start offset
	 * @param length The number of characters to remove
	 */
	public void remove(int offset, int length)
	{
		if(length == 0)
			return;

		if(isReadOnly())
			throw new RuntimeException("buffer read-only");

		try
		{
			writeLock();

			if(offset < 0 || length < 0
				|| offset + length > contentMgr.getLength())
				throw new ArrayIndexOutOfBoundsException(offset + ":" + length);

			int startLine = offsetMgr.getLineOfOffset(offset);

			contentMgr.getText(offset,length,seg);
			int numLines = 0;
			for(int i = 0; i < seg.count; i++)
			{
				if(seg.array[seg.offset + i] == '\n')
					numLines++;
			}

			if(!getFlag(UNDO_IN_PROGRESS))
			{
				undoMgr.contentRemoved(offset,length,
					seg.toString(),!getFlag(DIRTY));
			}

			contentMgr.remove(offset,length);

			offsetMgr.contentRemoved(startLine,offset,numLines,length);

			if(numLines > 0)
			{
				for(int i = 0; i < inUseFVMs.length; i++)
				{
					if(inUseFVMs[i] != null)
						inUseFVMs[i]._invalidate(startLine);
				}
			}

			fireContentRemoved(startLine,offset,numLines,length);

			setDirty(true);
		}
		finally
		{
			writeUnlock();
		}
	} 
	/**
	 * Undoes the most recent edit.
	 *
	 * @since jEdit 4.0pre1
	 */
	public void undo(JEditTextArea textArea)
	{
		if(undoMgr == null)
			return;

		if(!isEditable())
		{
			textArea.getToolkit().beep();
			return;
		}

		try
		{
			writeLock();

			setFlag(UNDO_IN_PROGRESS,true);
			if(!undoMgr.undo(textArea))
				textArea.getToolkit().beep();

			fireTransactionComplete();
		}
		finally
		{
			setFlag(UNDO_IN_PROGRESS,false);

			writeUnlock();
		}
	} 
	/**
	 * Redoes the most recently undone edit. Returns true if the redo was
	 * successful.
	 *
	 * @since jEdit 2.7pre2
	 */
	public void redo(JEditTextArea textArea)
	{
		if(undoMgr == null)
			return;

		if(!isEditable())
		{
			Toolkit.getDefaultToolkit().beep();
			return;
		}

		try
		{
			writeLock();

			setFlag(UNDO_IN_PROGRESS,true);
			if(!undoMgr.redo(textArea))
				textArea.getToolkit().beep();

			fireTransactionComplete();
		}
		finally
		{
			setFlag(UNDO_IN_PROGRESS,false);

			writeUnlock();
		}
	} 
	/**
	 * Returns if an undo or compound edit is currently in progress. If this
	 * method returns true, then eventually a
	 * <code>transactionComplete()</code> buffer event will get fired.
	 * @since jEdit 4.0pre6
	 */
	public boolean isTransactionInProgress()
	{
		return getFlag(UNDO_IN_PROGRESS) || insideCompoundEdit();
	} 
	/**
	 * Starts a compound edit. All edits from now on until
	 * <code>endCompoundEdit()</code> are called will be merged
	 * into one. This can be used to make a complex operation
	 * undoable in one step. Nested calls to
	 * <code>beginCompoundEdit()</code> behave as expected,
	 * requiring the same number of <code>endCompoundEdit()</code>
	 * calls to end the edit.
	 * @see #endCompoundEdit()
	 */
	public void beginCompoundEdit()
	{
		if(getFlag(TEMPORARY))
			return;

		try
		{
			writeLock();

			undoMgr.beginCompoundEdit();
		}
		finally
		{
			writeUnlock();
		}
	} 
	/**
	 * Ends a compound edit. All edits performed since
	 * <code>beginCompoundEdit()</code> was called can now
	 * be undone in one step by calling <code>undo()</code>.
	 * @see #beginCompoundEdit()
	 */
	public void endCompoundEdit()
	{
		if(getFlag(TEMPORARY))
			return;

		try
		{
			writeLock();

			undoMgr.endCompoundEdit();

			fireTransactionComplete();
		}
		finally
		{
			writeUnlock();
		}
	}
	/**
	 * Returns if a compound edit is currently active.
	 * @since jEdit 3.1pre1
	 */
	public boolean insideCompoundEdit()
	{
		return undoMgr.insideCompoundEdit();
	} 
	/**
	 * Adds a buffer change listener.
	 * @param listener The listener
	 * @since jEdit 4.0pre1
	 */
	public void addBufferChangeListener(BufferChangeListener listener)
	{
		bufferListeners.addElement(listener);
	} 
	/**
	 * Removes a buffer change listener.
	 * @param listener The listener
	 * @since jEdit 4.0pre1
	 */
	public void removeBufferChangeListener(BufferChangeListener listener)
	{
		bufferListeners.removeElement(listener);
	} 
	/**
	 * Returns an array of registered buffer change listeners.
	 * @param listener The listener
	 * @since jEdit 4.1pre3
	 */
	public BufferChangeListener[] getBufferChangeListeners()
	{
		return (BufferChangeListener[])bufferListeners
			.toArray(new BufferChangeListener[
			bufferListeners.size()]);
	} 
	/**
	 * Reloads settings from the properties. This should be called
	 * after the <code>syntax</code> or <code>folding</code>
	 * buffer-local properties are changed.
	 */
	public void propertiesChanged()
	{
		Iterator iter = properties.values().iterator();
		while(iter.hasNext())
		{
			PropValue value = (PropValue)iter.next();
			if(value.defaultValue)
				iter.remove();
		}

		parseFully = (!"text".equals(mode.getName())
			&& jEdit.getBooleanProperty("parseFully"));

		setTokenMarker(mode.getTokenMarker());

		String folding = getStringProperty("folding");
		FoldHandler handler = FoldHandler.getFoldHandler(folding);

		if(handler != null)
		{
			setFoldHandler(handler);
		}
		else
		{
			if (folding != null)
				Log.log(Log.WARNING, this, path + ": invalid 'folding' property: " + folding); 
			setFoldHandler(new DummyFoldHandler());
		}

		if(!isTemporary() && firstTimeDone)
			EditBus.send(new BufferUpdate(this,null,BufferUpdate.PROPERTIES_CHANGED));

		firstTimeDone = true;
	} 
	/**
	 * Returns the tab size used in this buffer. This is equivalent
	 * to calling getProperty("tabSize").
	 */
	public int getTabSize()
	{
		return getIntegerProperty("tabSize",8);
	} 
	/**
	 * Returns the indent size used in this buffer. This is equivalent
	 * to calling getProperty("indentSize").
	 * @since jEdit 2.7pre1
	 */
	public final int getIndentSize()
	{
		return getIntegerProperty("indentSize",8);
	} 
	/**
	 * Returns the value of a buffer-local property.
	 * @param name The property name. For backwards compatibility, this
	 * is an <code>Object</code>, not a <code>String</code>.
	 */
	public Object getProperty(Object name)
	{
		PropValue o = (PropValue)properties.get(name);
		if(o != null)
			return o.value;

		if(!(name instanceof String))
			return null;

		if(mode != null)
		{
			Object retVal = mode.getProperty((String)name);
			if(retVal == null)
				return null;

			properties.put(name,new PropValue(retVal,true));
			return retVal;
		}
		else
		{
			String value = jEdit.getProperty("buffer." + name);
			if(value == null)
				return null;

			Object retVal;
			try
			{
				retVal = new Integer(value);
			}
			catch(NumberFormatException nf)
			{
				retVal = value;
			}
			properties.put(name,new PropValue(retVal,true));
			return retVal;
		}
	} 
	/**
	 * Sets the value of a buffer-local property.
	 * @param name The property name
	 * @param value The property value
	 * @since jEdit 4.0pre1
	 */
	public void setProperty(String name, Object value)
	{
		if(value == null)
			properties.remove(name);
		else
		{
			PropValue test = (PropValue)properties.get(name);
			if(test == null)
				properties.put(name,new PropValue(value,false));
			else if(test.value.equals(value))
			{
			}
			else
			{
				test.value = value;
				test.defaultValue = false;
			}
		}
	} 
	/**
	 * Clears the value of a buffer-local property.
	 * @param name The property name
	 * @since jEdit 4.0pre1
	 */
	public void unsetProperty(String name)
	{
		properties.remove(name);
	} 
	/**
	 * Returns the value of a string property.
	 * @param name The property name
	 * @since jEdit 4.0pre1
	 */
	public String getStringProperty(String name)
	{
		Object obj = getProperty(name);
		if(obj != null)
			return obj.toString();
		else
			return null;
	} 
	/**
	 * Sets a string property.
	 * @param name The property name
	 * @param value The value
	 * @since jEdit 4.0pre1
	 */
	public void setStringProperty(String name, String value)
	{
		setProperty(name,value);
	} 
	/**
	 * Returns the value of a boolean property.
	 * @param name The property name
	 * @since jEdit 4.0pre1
	 */
	public boolean getBooleanProperty(String name)
	{
		Object obj = getProperty(name);
		if(obj instanceof Boolean)
			return ((Boolean)obj).booleanValue();
		else if("true".equals(obj) || "on".equals(obj) || "yes".equals(obj))
			return true;
		else
			return false;
	} 
	/**
	 * Sets a boolean property.
	 * @param name The property name
	 * @param value The value
	 * @since jEdit 4.0pre1
	 */
	public void setBooleanProperty(String name, boolean value)
	{
		setProperty(name,value ? Boolean.TRUE : Boolean.FALSE);
	} 
	/**
	 * Returns the value of an integer property.
	 * @param name The property name
	 * @since jEdit 4.0pre1
	 */
	public int getIntegerProperty(String name, int defaultValue)
	{
		boolean defaultValueFlag;
		Object obj;
		PropValue value = (PropValue)properties.get(name);
		if(value != null)
		{
			obj = value.value;
			defaultValueFlag = value.defaultValue;
		}
		else
		{
			obj = getProperty(name);
			defaultValueFlag = true;
		}

		if(obj == null)
			return defaultValue;
		else if(obj instanceof Number)
			return ((Number)obj).intValue();
		else
		{
			try
			{
				int returnValue = Integer.parseInt(
					obj.toString().trim());
				properties.put(name,new PropValue(
					new Integer(returnValue),
					defaultValueFlag));
				return returnValue;
			}
			catch(Exception e)
			{
				return defaultValue;
			}
		}
	} 
	/**
	 * Sets an integer property.
	 * @param name The property name
	 * @param value The value
	 * @since jEdit 4.0pre1
	 */
	public void setIntegerProperty(String name, int value)
	{
		setProperty(name,new Integer(value));
	} 
	/**
	 * Returns the syntax highlighting ruleset at the specified offset.
	 * @since jEdit 4.1pre1
	 */
	public ParserRuleSet getRuleSetAtOffset(int offset)
	{
		int line = getLineOfOffset(offset);
		offset -= getLineStartOffset(line);
		if(offset != 0)
			offset--;

		DefaultTokenHandler tokens = new DefaultTokenHandler();
		markTokens(line,tokens);
		Token token = TextUtilities.getTokenAtOffset(tokens.getTokens(),offset);
		return token.rules;
	} 
	/**
	 * Returns the syntax highlighting keyword map in effect at the
	 * specified offset. Used by the <b>Complete Word</b> command to
	 * complete keywords.
	 * @param offset The offset
	 * @since jEdit 4.0pre3
	 */
	public KeywordMap getKeywordMapAtOffset(int offset)
	{
		return getRuleSetAtOffset(offset).getKeywords();
	} 
	/**
	 * Some settings, like comment start and end strings, can
	 * vary between different parts of a buffer (HTML text and inline
	 * JavaScript, for example).
	 * @param offset The offset
	 * @param name The property name
	 * @since jEdit 4.0pre3
	 */
	public String getContextSensitiveProperty(int offset, String name)
	{
		ParserRuleSet rules = getRuleSetAtOffset(offset);

		Object value = null;

		Hashtable rulesetProps = rules.getProperties();
		if(rulesetProps != null)
			value = rulesetProps.get(name);

		if(value == null)
		{
			value = rules.getMode().getProperty(name);

			if(value == null)
				value = mode.getProperty(name);
		}

		if(value == null)
			return null;
		else
			return String.valueOf(value);
	} 
	static class PropValue
	{
		PropValue(Object value, boolean defaultValue)
		{
			if(value == null)
				throw new NullPointerException();
			this.value = value;
			this.defaultValue = defaultValue;
		}

		Object value;

		/**
		 * If this is true, then this value is cached from the mode
		 * or global defaults, so when the defaults change this property
		 * value must be reset.
		 */
		boolean defaultValue;

		/**
		 * For debugging purposes.
		 */
		public String toString()
		{
			return value.toString();
		}
	} 
	/**
	 * Toggles word wrap between the three available modes. This is used
	 * by the status bar.
	 * @param view We show a message in the view's status bar
	 * @since jEdit 4.1pre3
	 */
	public void toggleWordWrap(View view)
	{
		String wrap = getStringProperty("wrap");
		if(wrap.equals("none"))
			wrap = "soft";
		else if(wrap.equals("soft"))
			wrap = "hard";
		else if(wrap.equals("hard"))
			wrap = "none";
		view.getStatus().setMessageAndClear(jEdit.getProperty(
			"view.status.wrap-changed",new String[] {
			wrap }));
		setProperty("wrap",wrap);
		propertiesChanged();
	} 
	/**
	 * Toggles the line separator between the three available settings.
	 * This is used by the status bar.
	 * @param view We show a message in the view's status bar
	 * @since jEdit 4.1pre3
	 */
	public void toggleLineSeparator(View view)
	{
		String status = null;
		String lineSep = getStringProperty("lineSeparator");
		if("\n".equals(lineSep))
		{
			status = "windows";
			lineSep = "\r\n";
		}
		else if("\r\n".equals(lineSep))
		{
			status = "mac";
			lineSep = "\r";
		}
		else if("\r".equals(lineSep))
		{
			status = "unix";
			lineSep = "\n";
		}
		view.getStatus().setMessageAndClear(jEdit.getProperty(
			"view.status.linesep-changed",new String[] {
			jEdit.getProperty("lineSep." + status) }));
		setProperty("lineSeparator",lineSep);
		propertiesChanged();
	} 
	/**
	 * Returns this buffer's edit mode.
	 */
	public final Mode getMode()
	{
		return mode;
	} 
	/**
	 * Sets this buffer's edit mode. Note that calling this before a buffer
	 * is loaded will have no effect; in that case, set the "mode" property
	 * to the name of the mode. A bit inelegant, I know...
	 * @param mode The mode
	 */
	public void setMode(Mode mode)
	{
		/* This protects against stupid people (like me)
		 * doing stuff like buffer.setMode(jEdit.getMode(...)); */
		if(mode == null)
			throw new NullPointerException("Mode must be non-null");

		this.mode = mode;

		propertiesChanged(); 
	} 
	/**
	 * Sets this buffer's edit mode by calling the accept() method
	 * of each registered edit mode.
	 */
	public void setMode()
	{
		String userMode = getStringProperty("mode");
		if(userMode != null)
		{
			Mode m = jEdit.getMode(userMode);
			if(m != null)
			{
				setMode(m);
				return;
			}
		}

		String nogzName = name.substring(0,name.length() -
			(name.endsWith(".gz") ? 3 : 0));
		Mode[] modes = jEdit.getModes();

		String firstLine = getLineText(0);

		for(int i = 0; i < modes.length; i++)
		{
			if(modes[i].accept(nogzName,firstLine))
			{
				setMode(modes[i]);
				return;
			}
		}

		Mode defaultMode = jEdit.getMode(jEdit.getProperty("buffer.defaultMode"));
		if(defaultMode == null)
			defaultMode = jEdit.getMode("text");
		setMode(defaultMode);
	} 
	/**
	 * Returns the syntax tokens for the specified line.
	 * @param lineIndex The line number
	 * @param tokenHandler The token handler that will receive the syntax
	 * tokens
	 * @since jEdit 4.1pre1
	 */
	public void markTokens(int lineIndex, TokenHandler tokenHandler)
	{
		Segment seg;
		if(SwingUtilities.isEventDispatchThread())
			seg = this.seg;
		else
			seg = new Segment();

		try
		{
			writeLock();

			if(lineIndex < 0 || lineIndex >= offsetMgr.getLineCount())
				throw new ArrayIndexOutOfBoundsException(lineIndex);

			/*
			 * Scan backwards, looking for a line with
			 * a valid line context.
			 */
			int start, end;
			if(parseFully)
			{
				start = -1;
				end = 0;
			}
			else
			{
				start = Math.max(0,lineIndex - 100) - 1;
				end = Math.max(0,lineIndex - 100);
			}

			for(int i = lineIndex - 1; i > end; i--)
			{
				if(offsetMgr.isLineContextValid(i))
				{
					start = i;
					break;
				}
			}

			for(int i = start + 1; i <= lineIndex; i++)
			{
				getLineText(i,seg);

				TokenMarker.LineContext prevContext = (i == 0 ? null
					: offsetMgr.getLineContext(i - 1));

				TokenMarker.LineContext context = offsetMgr.getLineContext(i);
				ParserRule oldRule;
				ParserRuleSet oldRules;
				if(context == null)
				{
					oldRule = null;
					oldRules = null;
				}
				else
				{
					oldRule = context.inRule;
					oldRules = context.rules;
				}

				context = tokenMarker.markTokens(prevContext,
					(i == lineIndex ? tokenHandler
					: DummyTokenHandler.INSTANCE),seg);
				offsetMgr.setLineContext(i,context);

				if(oldRule != context.inRule)
					nextLineRequested = true;
				else if(oldRules != context.rules)
					nextLineRequested = true;
			}

			int lineCount = offsetMgr.getLineCount();
			if(nextLineRequested && lineCount - lineIndex > 1)
			{
				offsetMgr.lineInfoChangedFrom(lineIndex + 1);
			}
		}
		finally
		{
			writeUnlock();
		}
	} 
	/**
	 * Returns true if the next line should be repainted. This
	 * will return true after a line has been tokenized that starts
	 * a multiline token that continues onto the next line.
	 */
	public boolean isNextLineRequested()
	{
		boolean retVal = nextLineRequested;
		nextLineRequested = false;
		return retVal;
	} 
	/**
	 * This method is only public so that the <code>OffsetManager</code>
	 * class can use it.
	 * @since jEdit 4.0pre1
	 */
	public TokenMarker getTokenMarker()
	{
		return tokenMarker;
	} 
	/**
	 * Removes trailing whitespace from all lines in the specified list.
	 * @param lines The line numbers
	 * @since jEdit 3.2pre1
	 */
	public void removeTrailingWhiteSpace(int[] lines)
	{
		try
		{
			beginCompoundEdit();

			for(int i = 0; i < lines.length; i++)
			{
				int pos, lineStart, lineEnd, tail;

				getLineText(lines[i],seg);

				if (seg.count == 0) continue;

				lineStart = seg.offset;
				lineEnd = seg.offset + seg.count - 1;

				for (pos = lineEnd; pos >= lineStart; pos--)
				{
					if (!Character.isWhitespace(seg.array[pos]))
						break;
				}

				tail = lineEnd - pos;

				if (tail == 0) continue;

				remove(getLineEndOffset(lines[i]) - 1 - tail,tail);
			}
		}
		finally
		{
			endCompoundEdit();
		}
	} 
	/**
	 * Shifts the indent of each line in the specified list to the left.
	 * @param lines The line numbers
	 * @since jEdit 3.2pre1
	 */
	public void shiftIndentLeft(int[] lines)
	{
		int tabSize = getTabSize();
		int indentSize = getIndentSize();
		boolean noTabs = getBooleanProperty("noTabs");

		try
		{
			beginCompoundEdit();

			for(int i = 0; i < lines.length; i++)
			{
				int lineStart = getLineStartOffset(lines[i]);
				String line = getLineText(lines[i]);
				int whiteSpace = MiscUtilities
					.getLeadingWhiteSpace(line);
				if(whiteSpace == 0)
					continue;
				int whiteSpaceWidth = Math.max(0,MiscUtilities
					.getLeadingWhiteSpaceWidth(line,tabSize)
					- indentSize);
	
				insert(lineStart + whiteSpace,MiscUtilities
					.createWhiteSpace(whiteSpaceWidth,
					(noTabs ? 0 : tabSize)));
				remove(lineStart,whiteSpace);
			}

		}
		finally
		{
			endCompoundEdit();
		}
	} 
	/**
	 * Shifts the indent of each line in the specified list to the right.
	 * @param lines The line numbers
	 * @since jEdit 3.2pre1
	 */
	public void shiftIndentRight(int[] lines)
	{
		try
		{
			beginCompoundEdit();

			int tabSize = getTabSize();
			int indentSize = getIndentSize();
			boolean noTabs = getBooleanProperty("noTabs");
			for(int i = 0; i < lines.length; i++)
			{
				int lineStart = getLineStartOffset(lines[i]);
				String line = getLineText(lines[i]);
				int whiteSpace = MiscUtilities
					.getLeadingWhiteSpace(line);

				int whiteSpaceWidth = MiscUtilities
					.getLeadingWhiteSpaceWidth(
					line,tabSize) + indentSize;
				insert(lineStart + whiteSpace,MiscUtilities
					.createWhiteSpace(whiteSpaceWidth,
					(noTabs ? 0 : tabSize)));
				remove(lineStart,whiteSpace);
			}
		}
		finally
		{
			endCompoundEdit();
		}
	} 
	/**
	 * If auto indent is enabled, this method is called when the `Tab'
	 * or `Enter' key is pressed to perform mode-specific indentation
	 * and return true, or return false if a normal tab is to be inserted.
	 * @param line The line number to indent
	 * @param canIncreaseIndent If false, nothing will be done if the
	 * calculated indent is greater than the current
	 * @param canDecreaseIndent If false, nothing will be done if the
	 * calculated indent is less than the current
	 * @return true if the tab key event should be swallowed (ignored)
	 * false if a real tab should be inserted
	 */
	public boolean indentLine(int lineIndex, boolean canIncreaseIndent,
		boolean canDecreaseIndent)
	{
		getLineText(lineIndex,seg);

		int tabSize = getTabSize();

		int whitespaceChars = 0;
		int currentIndent = 0;
loop:		for(int i = 0; i < seg.count; i++)
		{
			char c = seg.array[seg.offset + i];
			switch(c)
			{
			case ' ':
				currentIndent++;
				whitespaceChars++;
				break;
			case '\t':
				currentIndent += (tabSize - (currentIndent
					% tabSize));
				whitespaceChars++;
				break;
			default:
				break loop;
			}
		}

		int idealIndent = getIndentForLine(lineIndex);
		if(idealIndent == -1)
			return false;

		if(!canDecreaseIndent && idealIndent <= currentIndent)
			return false;

		if(!canIncreaseIndent && idealIndent >= currentIndent)
			return false;

		try
		{
			beginCompoundEdit();

			int start = getLineStartOffset(lineIndex);

			remove(start,whitespaceChars);
			insert(start,MiscUtilities.createWhiteSpace(
				idealIndent,(getBooleanProperty("noTabs")
				? 0 : tabSize)));
		}
		finally
		{
			endCompoundEdit();
		}

		return true;
	} 
	/**
	 * Indents all specified lines.
	 * @param start The first line to indent
	 * @param end The last line to indent
	 * @since jEdit 3.1pre3
	 */
	public void indentLines(int start, int end)
	{
		try
		{
			beginCompoundEdit();
			for(int i = start; i <= end; i++)
				indentLine(i,true,true);
		}
		finally
		{
			endCompoundEdit();
		}
	} 
	/**
	 * Indents all specified lines.
	 * @param lines The line numbers
	 * @since jEdit 3.2pre1
	 */
	public void indentLines(int[] lines)
	{
		try
		{
			beginCompoundEdit();
			for(int i = 0; i < lines.length; i++)
				indentLine(lines[i],true,true);
		}
		finally
		{
			endCompoundEdit();
		}
	} 
	/**
	 * Returns the ideal leading indent for the specified line.
	 * This will apply the various auto-indent rules.
	 * @param lineIndex The line number
	 */
	public int getIndentForLine(int lineIndex)
	{
		if(lineIndex == 0)
			return -1;

		String openBrackets = (String)getProperty("indentOpenBrackets");
		String closeBrackets = (String)getProperty("indentCloseBrackets");
		String _indentPrevLine = (String)getProperty("indentPrevLine");
		boolean doubleBracketIndent = getBooleanProperty("doubleBracketIndent");
		RE indentPrevLineRE = null;
		if(openBrackets == null)
			openBrackets = "";
		if(closeBrackets == null)
			closeBrackets = "";
		if(_indentPrevLine != null)
		{
			try
			{
				indentPrevLineRE = new RE(_indentPrevLine,
					RE.REG_ICASE,RESearchMatcher.RE_SYNTAX_JEDIT);
			}
			catch(REException re)
			{
				Log.log(Log.ERROR,this,"Invalid 'indentPrevLine'"
					+ " regexp: " + _indentPrevLine);
				Log.log(Log.ERROR,this,re);
			}
		}

		int tabSize = getTabSize();
		int indentSize = getIndentSize();

		String prevLine = null;
		String line = null;

		int start = getLineStartOffset(lineIndex);

		line = getLineText(lineIndex);

		int prevLineIndex = -1;
		for(int i = lineIndex - 1; i >= 0; i--)
		{
			if(getLineLength(i) != 0)
			{
				prevLine = getLineText(i);
				prevLineIndex = i;
				break;
			}
		}

		if(prevLine == null)
			return -1;

		/*
		 * If 'prevLineIndent' matches a line --> +1
		 */
		boolean prevLineMatches = (indentPrevLineRE == null ? false
			: indentPrevLineRE.isMatch(prevLine));

		/*
		 * On the previous line,
		 * if(bob) { --> +1
		 * if(bob) { } --> 0
		 * } else if(bob) { --> +1
		 */
		boolean prevLineStart = true; 
		int prevLineIndent = 0; 
		int prevLineBrackets = 0; 
		int prevLineCloseBracketIndex = -1; 
		for(int i = 0; i < prevLine.length(); i++)
		{
			char c = prevLine.charAt(i);
			switch(c)
			{
			case ' ':
				if(prevLineStart)
					prevLineIndent++;
				break;
			case '\t':
				if(prevLineStart)
				{
					prevLineIndent += (tabSize
						- (prevLineIndent
						% tabSize));
				}
				break;
			default:
				prevLineStart = false;

				if(closeBrackets.indexOf(c) != -1)
				{
					prevLineBrackets = Math.max(
						prevLineBrackets-1,0);
					prevLineCloseBracketIndex = i;
				}
				else if(openBrackets.indexOf(c) != -1)
				{
					/*
					 * If supressBracketAfterIndent is true
					 * and we have something that looks like:
					 * if(bob)
					 * {
					 * then the 'if' will not shift the indent,
					 * because of the {.
					 *
					 * If supressBracketAfterIndent is false,
					 * the above would be indented like:
					 * if(bob)
					 *         {
					 */
					if(!doubleBracketIndent)
						prevLineMatches = false;
					prevLineBrackets++;
				}
				break;
			}
		}

		if(prevLineBrackets == 3)
			prevLineBrackets = 0;

		/*
		 * On the current line,
		 * } --> -1
		 * } else if(bob) { --> -1
		 * if(bob) { } --> 0
		 */
		boolean lineStart = true; 
		int lineIndent = 0; 
		int lineWidth = 0; 
		int lineBrackets = 0; 
		int closeBracketIndex = -1; 
		for(int i = 0; i < line.length(); i++)
		{
			char c = line.charAt(i);
			switch(c)
			{
			case ' ':
				if(lineStart)
				{
					lineIndent++;
					lineWidth++;
				}
				break;
			case '\t':
				if(lineStart)
				{
					lineIndent += (tabSize
						- (lineIndent
						% tabSize));
					lineWidth++;
				}
				break;
			default:
				if(closeBrackets.indexOf(c) != -1)
				{
					if(lineBrackets == 0)
						closeBracketIndex = i;
					else
						lineBrackets--;
				}
				else if(openBrackets.indexOf(c) != -1)
				{
					if((!doubleBracketIndent)&&lineStart)
						prevLineMatches = false;
					lineBrackets++;
				}
				lineStart = false;
				break;
			}
		}

		if(lineBrackets == 3)
		{
			closeBracketIndex = -1;
			lineBrackets = 0;
		}

		if((indentPrevLineRE != null) && (!prevLineMatches) && (prevLineBrackets == 0))
		{
			int indentLineIndex;
			if(prevLineCloseBracketIndex != -1)
			{
				int offset = TextUtilities.findMatchingBracket(
					this,prevLineIndex,prevLineCloseBracketIndex);
				if(offset == -1)
					return -1;
				indentLineIndex = getLineOfOffset(offset);
			}
			else
				indentLineIndex = prevLineIndex;

			if(indentLineIndex >= 0)
			{
				String closeLine = getLineText(indentLineIndex);

				while(--indentLineIndex > 0)
				{
					String tempLine = getLineText(indentLineIndex);

					if(tempLine.trim().length() == 0)
						continue;

					if(indentPrevLineRE.isMatch(tempLine))
						closeLine = tempLine;
					else
						break;
				}

				prevLineIndent = MiscUtilities
					.getLeadingWhiteSpaceWidth(
					closeLine,tabSize);
			}
			else
				return -1;
		}

		if(closeBracketIndex != -1)
		{
			int offset = TextUtilities.findMatchingBracket(
				this,lineIndex,closeBracketIndex);
			if(offset != -1)
			{
				String closeLine = getLineText(getLineOfOffset(offset));
				prevLineIndent = MiscUtilities.getLeadingWhiteSpaceWidth(
					closeLine,tabSize);
			}
			else
				return -1;
		}
		else
		{
			prevLineIndent += (prevLineBrackets * indentSize);
		}

		if (prevLineMatches)
			prevLineIndent += indentSize;

		return prevLineIndent;
	} 
	/**
	 * Returns the virtual column number (taking tabs into account) of the
	 * specified position.
	 *
	 * @param line The line number
	 * @param column The column number
	 * @since jEdit 4.1pre1
	 */
	public int getVirtualWidth(int line, int column)
	{
		try
		{
			readLock();

			int start = getLineStartOffset(line);
			getText(start,column,seg);

			return MiscUtilities.getVirtualWidth(seg,getTabSize());
		}
		finally
		{
			readUnlock();
		}
	} 
	/**
	 * Returns the array offset of a virtual column number (taking tabs
	 * into account) in the segment.
	 *
	 * @param line The line number
	 * @param column The virtual column number
	 * @param totalVirtualWidth If this array is non-null, the total
	 * virtual width will be stored in its first location if this method
	 * returns -1.
	 *
	 * @return -1 if the column is out of bounds
	 *
	 * @since jEdit 4.1pre1
	 */
	public int getOffsetOfVirtualColumn(int line, int column,
		int[] totalVirtualWidth)
	{
		try
		{
			readLock();

			getLineText(line,seg);

			return MiscUtilities.getOffsetOfVirtualColumn(seg,
				getTabSize(),column,totalVirtualWidth);
		}
		finally
		{
			readUnlock();
		}
	} 
	/**
	 * Like the <code>insert()</code> method, but inserts the string at
	 * the specified virtual column. Inserts spaces as appropriate if
	 * the line is shorter than the column.
	 * @param line The line number
	 * @param col The virtual column number
	 * @param str The string
	 */
	public void insertAtColumn(int line, int col, String str)
	{
		try
		{
			writeLock();

			int[] total = new int[1];
			int offset = getOffsetOfVirtualColumn(line,col,total);
			if(offset == -1)
			{
				offset = getLineEndOffset(line) - 1;
				str = MiscUtilities.createWhiteSpace(col - total[0],0) + str;
			}
			else
				offset += getLineStartOffset(line);

			insert(offset,str);
		}
		finally
		{
			writeUnlock();
		}
	} 
	/**
	 * @deprecated Call <code>setProperty()</code> instead.
	 */
	public void putProperty(Object name, Object value)
	{
		if(!(name instanceof String))
			return;

		setProperty((String)name,value);
	} 
	/**
	 * @deprecated Call <code>setBooleanProperty()</code> instead
	 */
	public void putBooleanProperty(String name, boolean value)
	{
		setBooleanProperty(name,value);
	} 
	/**
	 * @deprecated Use org.gjt.sp.jedit.syntax.DefaultTokenHandler instead
	 */
	public static class TokenList extends DefaultTokenHandler
	{
		public Token getFirstToken()
		{
			return getTokens();
		}
	}

	/**
	 * @deprecated Use the other form of <code>markTokens()</code> instead
	 */
	public TokenList markTokens(int lineIndex)
	{
		TokenList list = new TokenList();
		markTokens(lineIndex,list);
		return list;
	} 
	/**
	 * @deprecated
	 */
	public Element[] getRootElements()
	{
		return new Element[] { getDefaultRootElement() };
	} 
	/**
	 * @deprecated
	 */
	public Element getParagraphElement(int offset)
	{
		return new LineElement(this,getLineOfOffset(offset));
	} 
	/**
	 * @deprecated Use <code>getLineOfOffset()</code>,
	 * <code>getLineStartOffset()</code>, and
	 * <code>getLineEndOffset()</code> instead.
	 */
	public Element getDefaultRootElement()
	{
		return new RootElement(this);
	} 
	/**
	 * @deprecated Call <code>insert()</code> instead.
	 */
	public void insertString(int offset, String str, AttributeSet attr)
	{
		insert(offset,str);
	} 
	/**
	 * @deprecated Do not call this method, use <code>getPath()</code>
	 * instead.
	 */
	public final File getFile()
	{
		return file;
	} 
	/**
	 * Returns if the specified line begins a fold.
	 * @since jEdit 3.1pre1
	 */
	public boolean isFoldStart(int line)
	{
		return (line != getLineCount() - 1
			&& getFoldLevel(line) < getFoldLevel(line + 1));
	} 
	/**
	 * Returns the fold level of the specified line.
	 * @param line A physical line index
	 * @since jEdit 3.1pre1
	 */
	public int getFoldLevel(int line)
	{
		try
		{
			writeLock();

			if(line < 0 || line >= offsetMgr.getLineCount())
				throw new ArrayIndexOutOfBoundsException(line);

			if(offsetMgr.isFoldLevelValid(line))
			{
				return offsetMgr.getFoldLevel(line);
			}
			else
			{
				int start = 0;
				for(int i = line - 1; i >= 0; i--)
				{
					if(offsetMgr.isFoldLevelValid(i))
					{
						start = i + 1;
						break;
					}
				}

				int newFoldLevel = 0;
				boolean changed = false;

				for(int i = start; i <= line; i++)
				{
					newFoldLevel = foldHandler.getFoldLevel(this,i,seg);
					if(newFoldLevel != offsetMgr.getFoldLevel(i))
						changed = true;
					offsetMgr.setFoldLevel(i,newFoldLevel);
				}

				if(changed && !getFlag(INSIDE_INSERT))
				{
					fireFoldLevelChanged(start,line);
				}

				return newFoldLevel;
			}
		}
		finally
		{
			writeUnlock();
		}
	} 
	/**
	 * Returns an array. The first element is the start line, the
	 * second element is the end line, of the fold containing the
	 * specified line number.
	 * @param line The line number
	 * @since jEdit 4.0pre3
	 */
	public int[] getFoldAtLine(int line)
	{
		int start, end;

		if(isFoldStart(line))
		{
			start = line;
			int foldLevel = getFoldLevel(line);

			line++;

			while(getFoldLevel(line) > foldLevel)
			{
				line++;

				if(line == getLineCount())
					break;
			}

			end = line - 1;
		}
		else
		{
			start = line;
			int foldLevel = getFoldLevel(line);
			while(getFoldLevel(start) >= foldLevel)
			{
				if(start == 0)
					break;
				else
					start--;
			}

			end = line;
			while(getFoldLevel(end) >= foldLevel)
			{
				end++;

				if(end == getLineCount())
					break;
			}

			end--;
		}

		while(getLineLength(end) == 0 && end > start)
			end--;

		return new int[] { start, end };
	} 
	/**
	 * Creates a floating position.
	 * @param offset The offset
	 */
	public Position createPosition(int offset)
	{
		try
		{
			readLock();

			if(offset < 0 || offset > contentMgr.getLength())
				throw new ArrayIndexOutOfBoundsException(offset);

			return offsetMgr.createPosition(offset);
		}
		finally
		{
			readUnlock();
		}
	} 
	/**
	 * Returns a vector of markers.
	 * @since jEdit 3.2pre1
	 */
	public final Vector getMarkers()
	{
		return markers;
	} 
	/**
	 * If a marker is set on the line of the position, it is removed. Otherwise
	 * a new marker with the specified shortcut is added.
	 * @param pos The position of the marker
	 * @param shortcut The shortcut ('\0' if none)
	 * @since jEdit 3.2pre5
	 */
	public void addOrRemoveMarker(char shortcut, int pos)
	{
		int line = getLineOfOffset(pos);
		if(getMarkerAtLine(line) != null)
			removeMarker(line);
		else
			addMarker(shortcut,pos);
	} 
	/**
	 * Adds a marker to this buffer.
	 * @param pos The position of the marker
	 * @param shortcut The shortcut ('\0' if none)
	 * @since jEdit 3.2pre1
	 */
	public void addMarker(char shortcut, int pos)
	{
		if(!getFlag(READ_ONLY) && jEdit.getBooleanProperty("persistentMarkers"))
			setDirty(true);

		Marker markerN = new Marker(this,shortcut,pos);
		boolean added = false;

		if(!getFlag(LOADING))
		{
			markerN.createPosition();

			for(int i = 0; i < markers.size(); i++)
			{
				Marker marker = (Marker)markers.elementAt(i);
				if(shortcut != '\0' && marker.getShortcut() == shortcut)
					marker.setShortcut('\0');

				if(marker.getPosition() == pos)
				{
					markers.removeElementAt(i);
					i--;
				}
			}

			for(int i = 0; i < markers.size(); i++)
			{
				Marker marker = (Marker)markers.elementAt(i);
				if(marker.getPosition() > pos)
				{
					markers.insertElementAt(markerN,i);
					added = true;
					break;
				}
			}
		}

		if(!added)
			markers.addElement(markerN);

		if(!getFlag(LOADING) && !getFlag(TEMPORARY))
		{
			EditBus.send(new BufferUpdate(this,null,
				BufferUpdate.MARKERS_CHANGED));
		}
	} 
	/**
	 * Returns the first marker within the specified range.
	 * @param start The start offset
	 * @param end The end offset
	 * @since jEdit 4.0pre4
	 */
	public Marker getMarkerInRange(int start, int end)
	{
		for(int i = 0; i < markers.size(); i++)
		{
			Marker marker = (Marker)markers.elementAt(i);
			int pos = marker.getPosition();
			if(pos >= start && pos < end)
				return marker;
		}

		return null;
	} 
	/**
	 * Returns the first marker at the specified line.
	 * @param line The line number
	 * @since jEdit 3.2pre2
	 */
	public Marker getMarkerAtLine(int line)
	{
		for(int i = 0; i < markers.size(); i++)
		{
			Marker marker = (Marker)markers.elementAt(i);
			if(getLineOfOffset(marker.getPosition()) == line)
				return marker;
		}

		return null;
	} 
	/**
	 * Removes all markers at the specified line.
	 * @param line The line number
	 * @since jEdit 3.2pre2
	 */
	public void removeMarker(int line)
	{
		for(int i = 0; i < markers.size(); i++)
		{
			Marker marker = (Marker)markers.elementAt(i);
			if(getLineOfOffset(marker.getPosition()) == line)
			{
				if(!getFlag(READ_ONLY) && jEdit.getBooleanProperty("persistentMarkers"))
					setDirty(true);

				marker.removePosition();
				markers.removeElementAt(i);
				i--;
			}
		}

		EditBus.send(new BufferUpdate(this,null,
			BufferUpdate.MARKERS_CHANGED));
	} 
	/**
	 * Removes all defined markers.
	 * @since jEdit 2.6pre1
	 */
	public void removeAllMarkers()
	{
		if(!getFlag(READ_ONLY) && jEdit.getBooleanProperty("persistentMarkers"))
			setDirty(true);

		for(int i = 0; i < markers.size(); i++)
			((Marker)markers.elementAt(i)).removePosition();

		markers.removeAllElements();

		EditBus.send(new BufferUpdate(this,null,
			BufferUpdate.MARKERS_CHANGED));
	} 
	/**
	 * Returns the marker with the specified shortcut.
	 * @param shortcut The shortcut
	 * @since jEdit 3.2pre2
	 */
	public Marker getMarker(char shortcut)
	{
		Enumeration enum = markers.elements();
		while(enum.hasMoreElements())
		{
			Marker marker = (Marker)enum.nextElement();
			if(marker.getShortcut() == shortcut)
				return marker;
		}
		return null;
	} 
	/**
	 * Returns the next buffer in the list.
	 */
	public final Buffer getNext()
	{
		return next;
	} 
	/**
	 * Returns the previous buffer in the list.
	 */
	public final Buffer getPrev()
	{
		return prev;
	} 
	/**
	 * Returns the position of this buffer in the buffer list.
	 */
	public final int getIndex()
	{
		int count = 0;
		Buffer buffer = prev;
		for(;;)
		{
			if(buffer == null)
				break;
			count++;
			buffer = buffer.prev;
		}
		return count;
	} 
	/**
	 * Returns a string representation of this buffer.
	 * This simply returns the path name.
	 */
	public String toString()
	{
		return name + " (" + MiscUtilities.getParentOfPath(path) + ")";
	} 
	public void handleMessage(EBMessage msg)
	{
		if(msg instanceof PropertiesChanged)
			propertiesChanged();
		else if(msg instanceof EditPaneUpdate)
		{
			EditPaneUpdate emsg = (EditPaneUpdate)msg;
			if(emsg.getWhat() == EditPaneUpdate.CREATED)
			{
			}
			else if(emsg.getWhat() == EditPaneUpdate.DESTROYED)
			{
				JEditTextArea textArea = emsg.getEditPane()
					.getTextArea();
				FoldVisibilityManager mgr = textArea
					.getFoldVisibilityManager();

				for(int i = 0; i < inUseFVMs.length; i++)
				{
					if(mgr == inUseFVMs[i])
					{
						mgr._release();
						inUseFVMs[i] = null;
						break;
					}
				}
			}
		}
	} 
	/**
	 * Plugins and macros should call
	 * <code>textArea.getFoldVisibilityManager()</code>
	 * instead of this method.
	 * @param textArea The text area
	 * @since jEdit 4.0pre1
	 */
	public FoldVisibilityManager _getFoldVisibilityManager(
		JEditTextArea textArea)
	{
		FoldVisibilityManager mgr = new FoldVisibilityManager(this,
			offsetMgr,textArea);

		for(int i = 0; i < inUseFVMs.length; i++)
		{
			if(inUseFVMs[i] == null)
			{
				inUseFVMs[i] = mgr;
				mgr._grab(i);
				return mgr;
			}
		}

		throw new InternalError("Too many text areas editing this buffer");
	} 
	/**
	 * Plugins and macros should not call this method.
	 * @param mgr The fold visibility manager
	 * @since jEdit 4.0pre1
	 */
	public void _releaseFoldVisibilityManager(FoldVisibilityManager mgr)
	{
		inUseFVMs[mgr._getIndex()] = null;
		mgr._release();
	} 
	Buffer prev;
	Buffer next;

	Buffer(String path, boolean newFile, boolean temp, Hashtable props)
	{
		properties = new Hashtable();

		Enumeration enum = props.keys();
		while(enum.hasMoreElements())
		{
			Object key = enum.nextElement();
			Object value = props.get(key);

			properties.put(key,new PropValue(value,false));
		} 
		if(getProperty(ENCODING) == null)
			properties.put(ENCODING,new PropValue(System.getProperty("file.encoding"),false));
		if(getProperty(LINESEP) == null)
			properties.put(LINESEP,new PropValue(System.getProperty("line.separator"),false));

		lock = new ReadWriteLock();
		contentMgr = new ContentManager();
		offsetMgr = new OffsetManager(this);
		integerArray = new IntegerArray();
		undoMgr = new UndoManager(this);
		bufferListeners = new Vector();

		seg = new Segment();

		inUseFVMs = new FoldVisibilityManager[8];

		setFlag(TEMPORARY,temp);

		markers = new Vector();

		setPath(path);

		Mode defaultMode = jEdit.getMode(jEdit.getProperty("buffer.defaultMode"));
		if(defaultMode == null)
			defaultMode = jEdit.getMode("text");
		setMode(defaultMode);

		/* Magic: UNTITLED is only set if newFile param to
		 * constructor is set, NEW_FILE is also set if file
		 * doesn't exist on disk.
		 *
		 * This is so that we can tell apart files created
		 * with jEdit.newFile(), and those that just don't
		 * exist on disk.
		 *
		 * Why do we need to tell the difference between the
		 * two? jEdit.addBufferToList() checks if the only
		 * opened buffer is an untitled buffer, and if so,
		 * replaces it with the buffer to add. We don't want
		 * this behavior to occur with files that don't
		 * exist on disk; only untitled ones.
		 */
		setFlag(UNTITLED,newFile);
		setFlag(NEW_FILE,newFile);

		if(!temp)
			EditBus.addToBus(Buffer.this);
	} 
	void commitTemporary()
	{
		setFlag(TEMPORARY,false);
		EditBus.addToBus(this);

		finishLoading();
	} 
	void close()
	{
		setFlag(CLOSED,true);

		if(autosaveFile != null)
			autosaveFile.delete();

		EditBus.removeFromBus(this);
	} 
	private void setFlag(int flag, boolean value)
	{
		if(value)
			flags |= (1 << flag);
		else
			flags &= ~(1 << flag);
	} 
	private boolean getFlag(int flag)
	{
		int mask = (1 << flag);
		return (flags & mask) == mask;
	} 
	private static final int CLOSED = 0;
	private static final int LOADING = 1;
	private static final int IO = 2;
	private static final int NEW_FILE = 3;
	private static final int UNTITLED = 4;
	private static final int AUTOSAVE_DIRTY = 5;
	private static final int DIRTY = 6;
	private static final int READ_ONLY = 7;
	private static final int UNDO_IN_PROGRESS = 8;
	private static final int TEMPORARY = 9;
	private static final int INSIDE_INSERT = 10;
	private int flags;

	private VFS vfs;
	private String path;
	private String name;
	private File file;
	private File autosaveFile;
	private long modTime;
	private Mode mode;
	private Hashtable properties;

	private ReadWriteLock lock;
	private ContentManager contentMgr;
	private OffsetManager offsetMgr;
	private IntegerArray integerArray;
	private UndoManager undoMgr;
	private Vector bufferListeners;

	private Vector markers;

	private boolean parseFully;
	private TokenMarker tokenMarker;
	private Segment seg;
	private boolean nextLineRequested;

	private FoldHandler foldHandler;
	private FoldVisibilityManager[] inUseFVMs;

	private boolean firstTimeDone;
	private void setPath(String path)
	{
		this.path = path;
		name = MiscUtilities.getFileName(path);

		vfs = VFSManager.getVFSForPath(path);
		if((vfs.getCapabilities() & VFS.WRITE_CAP) == 0)
			setReadOnly(true);

		if(vfs instanceof FileVFS)
		{
			file = new File(path);

			if(autosaveFile != null)
				autosaveFile.delete();
			autosaveFile = new File(file.getParent(),'#' + name + '#');
		}
	} 
	private boolean recoverAutosave(final View view)
	{
		if(!autosaveFile.canRead())
			return false;

		GUIUtilities.hideSplashScreen();

		final Object[] args = { autosaveFile.getPath() };
		int result = GUIUtilities.confirm(view,"autosave-found",args,
			JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);

		if(result == JOptionPane.YES_OPTION)
		{
			vfs.load(view,this,autosaveFile.getPath());

			VFSManager.runInAWTThread(new Runnable()
			{
				public void run()
				{
					GUIUtilities.message(view,"autosave-loaded",args);
				}
			});

			return true;
		}
		else
			return false;
	} 
	private void finishLoading()
	{
		parseBufferLocalProperties();
		FoldHandler oldFoldHandler = foldHandler;
		setMode();

		if(foldHandler == oldFoldHandler)
		{
			offsetMgr.lineInfoChangedFrom(0);

			int collapseFolds = getIntegerProperty("collapseFolds",0);
			offsetMgr.expandFolds(collapseFolds);
		}

		if(parseFully)
		{
			for(int i = 0; i < offsetMgr.getLineCount(); i++)
				markTokens(i,DummyTokenHandler.INSTANCE);
		}
	} 
	private void finishSaving(View view, String oldPath, String path,
		boolean rename, boolean error)
	{
		if(!error && !path.equals(oldPath))
		{
			Buffer buffer = jEdit.getBuffer(path);

			if(rename)
			{
				/* if we save a file with the same name as one
				 * that's already open, we presume that we can
				 * close the existing file, since the user
				 * would have confirmed the overwrite in the
				 * 'save as' dialog box anyway */
				if(buffer != null && /* can't happen? */
					!buffer.getPath().equals(oldPath))
				{
					buffer.setDirty(false);
					jEdit.closeBuffer(view,buffer);
				}

				setPath(path);
			}
			else
			{
				/* if we saved over an already open file using
				 * 'save a copy as', then reload the existing
				 * buffer */
				if(buffer != null && /* can't happen? */
					!buffer.getPath().equals(oldPath))
				{
					buffer.load(view,true);
				}
			}
		} 
		if(rename)
		{
			if(file != null)
				modTime = file.lastModified();

			if(!error)
			{
				try
				{
					writeLock();

					if(autosaveFile != null)
						autosaveFile.delete();

					setFlag(AUTOSAVE_DIRTY,false);
					setFlag(READ_ONLY,false);
					setFlag(NEW_FILE,false);
					setFlag(UNTITLED,false);
					setFlag(DIRTY,false);

					undoMgr.bufferSaved();
				}
				finally
				{
					writeUnlock();
				}

				parseBufferLocalProperties();

				if(!getPath().equals(oldPath))
				{
					jEdit.updatePosition(Buffer.this);
					setMode();
				}
				else
					propertiesChanged();

				EditBus.send(new BufferUpdate(Buffer.this,
					view,BufferUpdate.DIRTY_CHANGED));

				EditBus.send(new BufferUpdate(Buffer.this,
					view,BufferUpdate.SAVED));
			}
		} 
	} 
	private void parseBufferLocalProperties()
	{
		if(getProperty("tabSize")
			.equals(mode.getProperty("tabSize")))
			unsetProperty("tabSize");

		if(getProperty("indentSize")
			.equals(mode.getProperty("indentSize")))
			unsetProperty("indentSize");

		if(getProperty("maxLineLen")
			.equals(mode.getProperty("maxLineLen")))
			unsetProperty("maxLineLen");
		int lastLine = Math.min(9,getLineCount() - 1);
		parseBufferLocalProperties(getText(0,getLineEndOffset(lastLine) - 1));

		int firstLine = Math.max(lastLine + 1, getLineCount() - 10);
		if(firstLine < getLineCount())
		{
			int length = getLineEndOffset(getLineCount() - 1) 
				- (getLineStartOffset(firstLine) + 1);
			parseBufferLocalProperties(getText(getLineStartOffset(firstLine),length));
		}

		for(int i = 0; i < markers.size(); i++)
		{
			Marker marker = (Marker)markers.elementAt(i);
			int pos = marker.getPosition();
			if(pos > getLength())
				marker.setPosition(getLength());
			marker.removePosition();
			marker.createPosition();
		}
	} 
	private void parseBufferLocalProperties(String prop)
	{
		StringBuffer buf = new StringBuffer();
		String name = null;
		boolean escape = false;
		for(int i = 0; i < prop.length(); i++)
		{
			char c = prop.charAt(i);
			switch(c)
			{
			case ':':
				if(escape)
				{
					escape = false;
					buf.append(':');
					break;
				}
				if(name != null)
				{
					String value = buf.toString();
					try
					{
						setProperty(name,new Integer(value));
					}
					catch(NumberFormatException nf)
					{
						setProperty(name,value);
					}
					name = null;
				}
				buf.setLength(0);
				break;
			case '=':
				if(escape)
				{
					escape = false;
					buf.append('=');
					break;
				}
				name = buf.toString();
				buf.setLength(0);
				break;
			case '\\':
				if(escape)
					buf.append('\\');
				escape = !escape;
				break;
			case 'n':
				if(escape)
				{	buf.append('\n');
					escape = false;
					break;
				}
			case 'r':
				if(escape)
				{	buf.append('\r');
					escape = false;
					break;
				}
			case 't':
				if(escape)
				{
					buf.append('\t');
					escape = false;
					break;
				}
			default:
				buf.append(c);
				break;
			}
		}
	} 
	private void setTokenMarker(TokenMarker tokenMarker)
	{
		TokenMarker oldTokenMarker = this.tokenMarker;

		this.tokenMarker = tokenMarker;

		if(oldTokenMarker != null && tokenMarker != oldTokenMarker)
		{
			offsetMgr.lineInfoChangedFrom(0);
		}
	} 
	private void setFoldHandler(FoldHandler foldHandler)
	{
		FoldHandler oldFoldHandler = this.foldHandler;

		if(foldHandler.equals(oldFoldHandler))
			return;

		this.foldHandler = foldHandler;

		if(oldFoldHandler != null)
		{
			offsetMgr.lineInfoChangedFrom(0);

			int collapseFolds = getIntegerProperty("collapseFolds",0);
			offsetMgr.expandFolds(collapseFolds);
		}
	} 
	private void contentInserted(int offset, int length,
		IntegerArray endOffsets)
	{
		try
		{
			setFlag(INSIDE_INSERT,true);

			int startLine = offsetMgr.getLineOfOffset(offset);
			int numLines = endOffsets.getSize();

			offsetMgr.contentInserted(startLine,offset,numLines,length,
				endOffsets);

			if(numLines > 0)
			{
				for(int i = 0; i < inUseFVMs.length; i++)
				{
					if(inUseFVMs[i] != null)
						inUseFVMs[i]._invalidate(startLine);
				}
			}

			setDirty(true);

			if(!getFlag(LOADING))
				fireContentInserted(startLine,offset,numLines,length);
		}
		finally
		{
			setFlag(INSIDE_INSERT,false);
		}
	} 
	private void fireFoldLevelChanged(int start, int end)
	{
		for(int i = 0; i < bufferListeners.size(); i++)
		{
			try
			{
				((BufferChangeListener)bufferListeners.elementAt(i))
					.foldLevelChanged(this,start,end);
			}
			catch(Throwable t)
			{
				Log.log(Log.ERROR,this,"Exception while sending buffer event:");
				Log.log(Log.ERROR,this,t);
			}
		}
	} 
	private void fireContentInserted(int startLine, int offset,
		int numLines, int length)
	{
		for(int i = 0; i < bufferListeners.size(); i++)
		{
			try
			{
				((BufferChangeListener)bufferListeners.elementAt(i))
					.contentInserted(this,startLine,offset,
					numLines,length);
			}
			catch(Throwable t)
			{
				Log.log(Log.ERROR,this,"Exception while sending buffer event:");
				Log.log(Log.ERROR,this,t);
			}
		}
	} 
	private void fireContentRemoved(int startLine, int offset,
		int numLines, int length)
	{
		for(int i = 0; i < bufferListeners.size(); i++)
		{
			try
			{
				((BufferChangeListener)bufferListeners.elementAt(i))
					.contentRemoved(this,startLine,offset,
					numLines,length);
			}
			catch(Throwable t)
			{
				Log.log(Log.ERROR,this,"Exception while sending buffer event:");
				Log.log(Log.ERROR,this,t);
			}
		}
	} 
	private void fireTransactionComplete()
	{
		for(int i = 0; i < bufferListeners.size(); i++)
		{
			try
			{
				((BufferChangeListener)bufferListeners.elementAt(i))
					.transactionComplete(this);
			}
			catch(Throwable t)
			{
				Log.log(Log.ERROR,this,"Exception while sending buffer event:");
				Log.log(Log.ERROR,this,t);
			}
		}
	} 
}
