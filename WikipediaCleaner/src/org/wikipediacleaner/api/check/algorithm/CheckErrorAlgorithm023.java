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

package org.wikipediacleaner.api.check.algorithm;

import java.util.Collection;
import java.util.List;

import org.wikipediacleaner.api.check.CheckErrorResult;
import org.wikipediacleaner.api.data.PageAnalysis;
import org.wikipediacleaner.api.data.PageElementTag;
import org.wikipediacleaner.i18n.GT;


/**
 * Algorithm for analyzing error 23 of check wikipedia project.
 * Error 23: Nowiki not correct end
 */
public class CheckErrorAlgorithm023 extends CheckErrorAlgorithmBase {

  public CheckErrorAlgorithm023() {
    super("Nowiki not correct end");
  }

  /**
   * Analyze a page to check if errors are present.
   * 
   * @param pageAnalysis Page analysis.
   * @param errors Errors found in the page.
   * @return Flag indicating if the error was found.
   */
  public boolean analyze(
      PageAnalysis pageAnalysis,
      Collection<CheckErrorResult> errors) {
    if (pageAnalysis == null) {
      return false;
    }

    // Check every <nowiki> tag
    List<PageElementTag> nowikiTags = pageAnalysis.getTags(PageElementTag.TAG_WIKI_NOWIKI);
    boolean result = false;
    for (PageElementTag nowikiTag : nowikiTags) {
      if ((!nowikiTag.isFullTag()) &&
          (!nowikiTag.isEndTag()) &&
          (nowikiTag.getMatchingTag() == null)) {
        if (errors == null) {
          return true;
        }
        result = true;
        CheckErrorResult errorResult = createCheckErrorResult(
            pageAnalysis.getPage(),
            nowikiTag.getBeginIndex(), nowikiTag.getEndIndex());
        errorResult.addReplacement("", GT._("Delete"));
        errors.add(errorResult);
      }
    }
    return result;
  }
}
