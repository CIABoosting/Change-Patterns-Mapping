/*
 * HelpIndex.java - Index for help searching feature
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2002 Slava Pestov
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

package org.gjt.sp.jedit.help;

import java.io.*;
import java.net.*;
import java.util.zip.*;
import java.util.*;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.MiscUtilities;
public class HelpIndex
{
	public HelpIndex()
	{
		files = new ArrayList();
		words = new HashMap();
	} 
	public HelpIndex(String fileListPath, String wordIndexPath)
	{
		this();
	} 
	/**
	 * Indexes all HTML and text files in the specified directory.
	 * @param dir The directory
	 */
	public void indexDirectory(String dir) throws Exception
	{
		String[] files = VFSManager.getFileVFS()
			._listDirectory(null,dir,"*.{html,txt}",true,null);

		for(int i = 0; i < files.length; i++)
		{
			indexURL(files[i]);
		}
	} 
	/**
	 * Indexes all HTML and text files in the specified JAR file.
	 * @param jar The JAR file
	 */
	public void indexJAR(ZipFile jar) throws Exception
	{
		
	} 
	/**
	 * Reads the specified HTML file and adds all words defined therein to the
	 * index.
	 * @param url The HTML file's URL
	 */
	public void indexURL(String url) throws Exception
	{
		files.add(url);
		int fileIndex = files.size() - 1;

		InputStream _in;

		if(MiscUtilities.isURL(url))
			_in =  new URL(url).openStream();
		else
			_in = new FileInputStream(url);

		indexStream(_in,fileIndex);
	} 
	public HashMap getWords()
	{
		return words;
	} 
	private ArrayList files;

	private HashMap words;

	/**
	 * Reads the specified HTML file and adds all words defined therein to the
	 * index.
	 * @param _in The input stream
	 * @param fileIndex The file index
	 */
	private void indexStream(InputStream _in, int fileIndex) throws Exception
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(_in));

		StringBuffer word = new StringBuffer();
		boolean insideTag = false;
		boolean insideEntity = false;

		int c;
		while((c = in.read()) != -1)
		{
			char ch = (char)c;
			if(insideTag)
			{
				if(ch == '>')
					insideTag = false;
			}
			else if(insideEntity)
			{
				if(ch == ';')
					insideEntity = false;
			}
			else if(ch == '<')
				insideTag = true;
			else if(ch == '&')
				insideEntity = true;
			else if(!Character.isLetterOrDigit(ch))
			{
				if(word.length() != 0)
				{
					addWord(word.toString(),fileIndex);
					word.setLength(0);
				}
			}
			else
				word.append(ch);
		}

		in.close();
	} 
	private void addWord(String word, int index)
	{
		word = word.toLowerCase();

		Word w = (Word)words.get(word);
		if(w == null)
			words.put(word,new Word(word,index));
		else
			w.addOccurrence(index);
	} 
	public static class Word
	{
		String word;

		int fileCount = 0;
		int[] files;

		Word(String word, int file)
		{
			this.word = word;
			files = new int[5];
			addOccurrence(file);
		}

		void addOccurrence(int file)
		{
			for(int i = 0; i < fileCount; i++)
			{
				if(files[i] == file)
					return;
			}

			if(fileCount >= files.length)
			{
				int[] newFiles = new int[files.length * 2];
				System.arraycopy(files,0,newFiles,0,fileCount);
				files = newFiles;
			}

			files[fileCount++] = file;
		}

		public String toString()
		{
			StringBuffer buf = new StringBuffer();
			for(int i = 0; i < fileCount; i++)
			{
				if(i != 0)
					buf.append(",");
				buf.append(files[i]);
			}
			return buf.toString();
		}
	} 
}
