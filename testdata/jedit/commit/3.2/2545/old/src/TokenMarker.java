/*
 * TokenMarker.java - Tokenizes lines of text
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1998, 2002 Slava Pestov
 * Copyright (C) 1999, 2000 mike dillon
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

package org.gjt.sp.jedit.syntax;

import gnu.regexp.*;
import javax.swing.text.Segment;
import java.util.*;
import org.gjt.sp.jedit.search.CharIndexedSegment;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
/**
 * A token marker splits lines of text into tokens. Each token carries
 * a length field and an identification tag that can be mapped to a color
 * or font style for painting that token.
 *
 * @author Slava Pestov, mike dillon
 * @version $Id$
 *
 * @see org.gjt.sp.jedit.syntax.Token
 * @see org.gjt.sp.jedit.syntax.TokenHandler
 */
public class TokenMarker
{
	public TokenMarker()
	{
		ruleSets = new Hashtable(64);
	} 
	public String getName()
	{
		return name;
	} 
	public void setName(String name)
	{
		if (name == null)
			throw new NullPointerException();

		this.name = name;
		rulePfx = name.concat("::");
	} 
	public void addRuleSet(String setName, ParserRuleSet rules)
	{
		if (rules == null)
			return;

		if (setName == null)
			setName = "MAIN";

		ruleSets.put(rulePfx.concat(setName), rules);

		if (setName.equals("MAIN"))
			mainRuleSet = rules;
	} 
	public ParserRuleSet getMainRuleSet()
	{
		return mainRuleSet;
	} 
	public ParserRuleSet getRuleSet(String setName)
	{
		ParserRuleSet rules;

		rules = (ParserRuleSet) ruleSets.get(setName);

		if (rules == null && !setName.startsWith(rulePfx))
		{
			int delim = setName.indexOf("::");
			if(delim == -1)
			{
				byte id = Token.stringToToken(setName);
				rules = ParserRuleSet.getStandardRuleSet(id);
			}
			else
			{
				String modeName = setName.substring(0, delim);

				Mode mode = jEdit.getMode(modeName);
				if(mode == null)
				{
					Log.log(Log.ERROR,TokenMarker.class,
						"Unknown edit mode: " + modeName);
					rules = null;
				}
				else
				{
					TokenMarker marker = mode.getTokenMarker();
					rules = marker.getRuleSet(setName);
				}
			}

			ruleSets.put(setName, rules);
		}

		if (rules == null)
		{
			Log.log(Log.ERROR,this,"Unresolved delegate target: " + setName);
			return ParserRuleSet.getStandardRuleSet(Token.INVALID);
		}
		else
			return rules;
	} 
	/**
	 * Do not call this method directly; call Buffer.markTokens() instead.
	 */
	public LineContext markTokens(LineContext prevContext,
		TokenHandler tokenHandler, Segment line)
	{
		this.tokenHandler = tokenHandler;
		this.line = line;

		lastOffset = line.offset;
		lineLength = line.count + line.offset;

		context = new LineContext();

		if(prevContext == null)
			context.rules = getMainRuleSet();
		else
		{
			context.parent = prevContext.parent;
			context.inRule = prevContext.inRule;
			context.rules = prevContext.rules;
		}

		keywords = context.rules.getKeywords();
		escaped = false;

		seenWhitespaceEnd = false;
		whitespaceEnd = line.offset;
		ParserRule rule;
		int terminateChar = context.rules.getTerminateChar();
		boolean terminated = false;

main_loop:	for(pos = line.offset; pos < lineLength; pos++)
		{
			if(terminateChar >= 0 && pos >= terminateChar
				&& !terminated)
			{
				terminated = true;
				context = new LineContext(ParserRuleSet
					.getStandardRuleSet(context.rules
					.getDefault()),context);
				keywords = context.rules.getKeywords();
			} 
			if(context.parent != null)
			{
				rule = context.parent.inRule;
				if(rule != null)
				{
					if(checkDelegateEnd(rule))
					{
						seenWhitespaceEnd = true;
						continue main_loop;
					}
				}
			} 
			char ch = line.array[pos];

			rule = context.rules.getRules(ch);
			while(rule != null)
			{
				if (handleRule(rule,false))
				{
					seenWhitespaceEnd = true;
					continue main_loop;
				}
	
				rule = rule.next;
			} 
			if(Character.isWhitespace(ch))
			{
				if(!seenWhitespaceEnd)
					whitespaceEnd = pos + 1;

				if(context.inRule != null)
					handleRule(context.inRule,true);

				handleNoWordBreak();

				if(keywords != null)
					markKeyword(false);

				if(lastOffset != pos)
				{
					tokenHandler.handleToken(
						context.rules.getDefault(),
						lastOffset - line.offset,
						pos - lastOffset,
						context);
				}

				tokenHandler.handleToken(
					(ch == '\t' ? Token.TAB
					: Token.WHITESPACE),pos - line.offset,1,
					context);
				lastOffset = pos + 1;

				escaped = false;
			}
			else
			{
				if(keywords != null || context.rules.getRuleCount() != 0)
				{
					String noWordSep = context.rules.getNoWordSep();

					if(!Character.isLetterOrDigit(ch)
						&& noWordSep.indexOf(ch) == -1)
					{
						if(context.inRule != null)
							handleRule(context.inRule,true);

						handleNoWordBreak();

						markKeyword(true);

						tokenHandler.handleToken(
							context.rules.getDefault(),
							lastOffset - line.offset,1,
							context);
						lastOffset = pos + 1;
					}
				}

				seenWhitespaceEnd = true;
				escaped = false;
			} 
		} 
		pos = lineLength;

		if(context.inRule != null)
			handleRule(context.inRule,true);

		handleNoWordBreak();
		markKeyword(true);

		if(context.parent != null)
		{
			rule = context.parent.inRule;
			if((rule != null && (context.parent.inRule.action
				& ParserRule.NO_LINE_BREAK) == ParserRule.NO_LINE_BREAK)
				|| terminated)
			{
				context = context.parent;
				keywords = context.rules.getKeywords();
				context.inRule = null;
			}
		} 
		tokenHandler.handleToken(Token.END,pos - line.offset,0,context);

		return context.intern();
	} 
	private Hashtable ruleSets;
	private String name;
	private String rulePfx;
	private ParserRuleSet mainRuleSet;

	private TokenHandler tokenHandler;
	private Segment line;
	private LineContext context;
	private KeywordMap keywords;
	private Segment pattern = new Segment();
	private int lastOffset;
	private int lineLength;
	private int pos;
	private boolean escaped;

	private int whitespaceEnd;
	private boolean seenWhitespaceEnd;
	private boolean checkDelegateEnd(ParserRule rule)
	{
		if(rule.end == null)
			return false;

		LineContext tempContext = context;
		context = context.parent;
		keywords = context.rules.getKeywords();
		boolean tempEscaped = escaped;
		boolean b = handleRule(rule,true);
		context = tempContext;
		keywords = context.rules.getKeywords();

		if(b && !tempEscaped)
		{
			if(context.inRule != null)
				handleRule(context.inRule,true);

			markKeyword(true);

			tokenHandler.handleToken(
				(context.parent.inRule.action & ParserRule.EXCLUDE_MATCH)
				== ParserRule.EXCLUDE_MATCH
				? context.parent.rules.getDefault()
				: context.parent.inRule.token,
				pos - line.offset,pattern.count,context);

			context = (LineContext)context.parent.clone();
			keywords = context.rules.getKeywords();
			context.inRule = null;
			lastOffset = pos + pattern.count;

			pos += (pattern.count - 1);

			return true;
		}

		rule = context.parent.rules.getEscapeRule();
		if(rule != null && handleRule(rule,false))
			return true;

		return false;
	} 
	/**
	 * Checks if the rule matches the line at the current position
	 * and handles the rule if it does match
	 */
	private boolean handleRule(ParserRule checkRule, boolean end)
	{
		if(!end)
		{
			if(Character.toUpperCase(checkRule.hashChar)
				!= Character.toUpperCase(line.array[pos]))
			{
				return false;
			}

			if((checkRule.action & ParserRule.AT_LINE_START)
				== ParserRule.AT_LINE_START)
			{
				if((((checkRule.action & ParserRule.MARK_PREVIOUS) != 0) ?
					lastOffset : pos) != line.offset)
					return false;
			}
			else if((checkRule.action & ParserRule.AT_WHITESPACE_END)
				== ParserRule.AT_WHITESPACE_END)
			{
				if((((checkRule.action & ParserRule.MARK_PREVIOUS) != 0) ?
					lastOffset : pos) != whitespaceEnd)
					return false;
			}
			else if((checkRule.action & ParserRule.AT_WORD_START)
				== ParserRule.AT_WORD_START)
			{
				if((((checkRule.action & ParserRule.MARK_PREVIOUS) != 0) ?
					lastOffset : pos) != lastOffset)
					return false;
			}
		} 
		int matchedChars = 1;

		if(!end || (checkRule.action & ParserRule.MARK_FOLLOWING) == 0)
		{
			if((checkRule.action & ParserRule.REGEXP) == 0 || end)
			{
				pattern.array = (end ? checkRule.end : checkRule.start);
				pattern.offset = 0;
				pattern.count = pattern.array.length;
				matchedChars = pattern.count;

				if(!TextUtilities.regionMatches(context.rules
					.getIgnoreCase(),line,pos,pattern.array))
				{
					return false;
				}
			}
			else
			{
				int matchStart = pos - line.offset;
				REMatch match = checkRule.startRegexp.getMatch(
					new CharIndexedSegment(line,matchStart),
					0,RE.REG_ANCHORINDEX);
				if(match == null)
					return false;
				else if(match.getStartIndex() != 0)
					throw new InternalError("Can't happen");
				else
					matchedChars = match.getEndIndex();
			}
		} 
		if((checkRule.action & ParserRule.IS_ESCAPE) == ParserRule.IS_ESCAPE)
		{
			if(context.inRule != null)
				handleRule(context.inRule,true);

			escaped = !escaped;
			pos += pattern.count - 1;
		}
		else if(escaped)
		{
			escaped = false;
			pos += pattern.count - 1;
		} 
		else if(!end)
		{
			if(context.inRule != null)
				handleRule(context.inRule,true);

			markKeyword((checkRule.action & ParserRule.MARK_PREVIOUS)
				!= ParserRule.MARK_PREVIOUS);

			switch(checkRule.action & ParserRule.MAJOR_ACTIONS)
			{
			case ParserRule.SEQ:
				tokenHandler.handleToken(checkRule.token,
					pos - line.offset,matchedChars,context);

				ParserRuleSet delegateSet = checkRule.getDelegateRuleSet(this);
				if(delegateSet != null)
				{
					context = new LineContext(delegateSet,
						context.parent);
					keywords = context.rules.getKeywords();
				}
				break;
			case ParserRule.SPAN:
			case ParserRule.EOL_SPAN:
				context.inRule = checkRule;

				delegateSet = checkRule.getDelegateRuleSet(this);

				tokenHandler.handleToken(
					((checkRule.action & ParserRule.EXCLUDE_MATCH)
					== ParserRule.EXCLUDE_MATCH
					? context.rules.getDefault() : checkRule.token),
					pos - line.offset,matchedChars,context);

				String spanEndSubst = null;
				/* if((checkRule.action & ParserRule.REGEXP) == ParserRule.REGEXP)
					spanEndSubst = checkRule.startRegexp... */
				context = new LineContext(delegateSet, context);
				keywords = context.rules.getKeywords();

				break;
			case ParserRule.MARK_FOLLOWING:
				tokenHandler.handleToken((checkRule.action
					& ParserRule.EXCLUDE_MATCH)
					== ParserRule.EXCLUDE_MATCH ?
					context.rules.getDefault()
					: checkRule.token,pos - line.offset,
					pattern.count,context);

				context.inRule = checkRule;
				break;
			case ParserRule.MARK_PREVIOUS:
				if ((checkRule.action & ParserRule.EXCLUDE_MATCH)
					== ParserRule.EXCLUDE_MATCH)
				{
					if(pos != lastOffset)
					{
						tokenHandler.handleToken(
							checkRule.token,
							lastOffset - line.offset,
							pos - lastOffset,
							context);
					}

					tokenHandler.handleToken(
						context.rules.getDefault(),
						pos - line.offset,pattern.count,
						context);
				}
				else
				{
					tokenHandler.handleToken(checkRule.token,
						lastOffset - line.offset,
						pos - lastOffset + pattern.count,
						context);
				}

				break;
			default:
				throw new InternalError("Unhandled major action");
			}

			pos += (matchedChars - 1);
			lastOffset = pos + 1;

		} 
		else if((context.inRule.action & ParserRule.MARK_FOLLOWING) != 0)
		{
			if(pos != lastOffset)
			{
				tokenHandler.handleToken(
					context.inRule.token,
					lastOffset - line.offset,
					pos - lastOffset,context);
			}

			lastOffset = pos;
			context.inRule = null;
		} 
		return true;
	} 
	private void handleNoWordBreak()
	{
		if(context.parent != null)
		{
			ParserRule rule = context.parent.inRule;
			if(rule != null && (context.parent.inRule.action
				& ParserRule.NO_WORD_BREAK) != 0)
			{
				if(pos != lastOffset)
				{
					tokenHandler.handleToken(rule.token,
						lastOffset - line.offset,
						pos - lastOffset,context);
				}

				lastOffset = pos;
				context = context.parent;
				keywords = context.rules.getKeywords();
				context.inRule = null;
			}
		}
	} 
	private void markKeyword(boolean addRemaining)
	{
		int len = pos - lastOffset;
		if(len == 0)
			return;

		if(context.rules.getHighlightDigits())
		{
			boolean digit = false;
			boolean mixed = false;

			for(int i = lastOffset; i < pos; i++)
			{
				char ch = line.array[i];
				if(Character.isDigit(ch))
					digit = true;
				else
					mixed = true;
			}

			if(mixed)
			{
				RE digitRE = context.rules.getDigitRegexp();

				if(digit)
				{ 
					if(digitRE == null)
					{
						digit = false;
					}
					else
					{
						CharIndexedSegment seg = new CharIndexedSegment(
							line,false);
						int oldCount = line.count;
						int oldOffset = line.offset;
						line.offset = lastOffset;
						line.count = len;
						if(!digitRE.isMatch(seg))
							digit = false;
						line.offset = oldOffset;
						line.count = oldCount;
					}
				}
			}

			if(digit)
			{
				tokenHandler.handleToken(Token.DIGIT,
					lastOffset - line.offset,
					len,context);
				lastOffset = pos;

				return;
			}
		} 
		if(keywords != null)
		{
			byte id = keywords.lookup(line, lastOffset, len);

			if(id != Token.NULL)
			{
				tokenHandler.handleToken(id,
					lastOffset - line.offset,
					len,context);
				lastOffset = pos;
				return;
			}
		} 
		if(addRemaining)
		{
			tokenHandler.handleToken(context.rules.getDefault(),
				lastOffset - line.offset,len,context);
			lastOffset = pos;
		} 
	} 
	public static class LineContext
	{
		private static Hashtable intern = new Hashtable();

		public LineContext parent;
		public ParserRule inRule;
		public ParserRuleSet rules;
		public String spanEndSubst;

		public LineContext(ParserRule r, ParserRuleSet rs,
			String spanEndSubst)
		{
			inRule = r;
			rules = rs;
			this.spanEndSubst = spanEndSubst;
		} 
		public LineContext(ParserRuleSet rs, LineContext lc)
		{
			rules = rs;
			parent = (lc == null ? null : (LineContext)lc.clone());
		} 
		public LineContext()
		{
		} 
		public LineContext intern()
		{
			Object obj = intern.get(this);
			if(obj == null)
			{
				intern.put(this,this);
				return this;
			}
			else
				return (LineContext)obj;
		} 
		public int hashCode()
		{
			if(inRule != null)
				return inRule.hashCode();
			else if(rules != null)
				return rules.hashCode();
			else
				return 0;
		} 
		public boolean equals(Object obj)
		{
			if(obj instanceof LineContext)
			{
				LineContext lc = (LineContext)obj;
				if(lc.parent == null)
				{
					if(parent != null)
						return false;
				}
				else 
				{
					if(parent == null)
						return false;
					else if(!lc.parent.equals(parent))
						return false;
				}

				if(lc.spanEndSubst == null)
				{
					if(spanEndSubst != null)
						return false;
				}
				else
				{
					if(spanEndSubst == null)
						return false;
					else if(!lc.spanEndSubst.equals(spanEndSubst))
						return false;
				}

				return lc.inRule == inRule && lc.rules == rules;
			}
			else
				return false;
		} 
		public Object clone()
		{
			LineContext lc = new LineContext();
			lc.inRule = inRule;
			lc.rules = rules;
			lc.parent = (parent == null) ? null : (LineContext) parent.clone();
			lc.spanEndSubst = spanEndSubst;

			return lc;
		} 
	} 
}
