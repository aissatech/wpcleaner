/*
 *  WikipediaCleaner: A tool to help on Wikipedia maintenance tasks.
 *  Copyright (C) 2008  Nicolas Vervelle
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.wikipediacleaner.api.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Suggestions for text replacements.
 */
public class Suggestion {

  private final static Log log = LogFactory.getLog(Suggestion.class);

  private final static String TAG_NOWIKI_1 = "<nowiki>";
  private final static String TAG_NOWIKI_2 = "</nowiki>";

  private final Pattern pattern;
  private final Map<String, String> replacements;

  /**
   * Create a Suggestion.
   * 
   * @param patternText Search pattern.
   * @return Suggestion or null if there's a problem.
   */
  public static Suggestion createSuggestion(String patternText) {
    try {
      Pattern pattern = Pattern.compile(patternText);
      return new Suggestion(pattern);
    } catch (PatternSyntaxException e) {
      log.warn("Incorrect pattern syntax for [" + patternText + "]: " + e.getMessage());
    }
    return null;
  }

  /**
   * @param pattern Search pattern.
   */
  private Suggestion(Pattern pattern) {
    this.pattern = pattern;
    this.replacements = new HashMap<String, String>();
  }

  /**
   * Add a possible replacement.
   * 
   * @param replacement Replacement.
   * @param comment Comment.
   */
  public void addReplacement(String replacement, String comment) {
    if (replacement != null) {
      if ((replacement.startsWith(TAG_NOWIKI_1)) &&
          (replacement.endsWith(TAG_NOWIKI_2))) {
        replacement = replacement.substring(
            TAG_NOWIKI_1.length(),
            replacement.length() - TAG_NOWIKI_2.length());
      }
      replacements.put(replacement, comment);
    }
  }

  /**
   * @param text Text to look at.
   * @param index Index in the text to look at.
   * @return A matcher if the pattern matches the text to look at.
   */
  public Matcher lookingAt(String text, int index) {
    Matcher matcher = pattern.matcher(text.substring(index));
    if (matcher.lookingAt()) {
      return matcher;
    }
    return null;
  }

  /**
   * @param initialText Initial text.
   * @return Possible replacements.
   */
  public List<String> getReplacements(String initialText) {
    List<String> list = new ArrayList<String>();
    for (Entry<String, String> replacement : replacements.entrySet()) {
      list.add(pattern.matcher(initialText).replaceFirst(replacement.getKey()));
    }
    return list;
  }
}
