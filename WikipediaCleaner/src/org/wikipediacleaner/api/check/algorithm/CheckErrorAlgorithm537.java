/*
 *  WPCleaner: A tool to help on Wikipedia maintenance tasks.
 *  Copyright (C) 2013  Nicolas Vervelle
 *
 *  See README.txt file for licensing information.
 */

package org.wikipediacleaner.api.check.algorithm;

import java.util.Collection;
import java.util.List;

import org.wikipediacleaner.api.check.CheckErrorResult;
import org.wikipediacleaner.api.data.PageAnalysis;
import org.wikipediacleaner.api.data.PageElementTitle;


/**
 * Algorithm for analyzing error 537 of check wikipedia project.
 * Error 537: Unclosed quotes in heading (see [[Special:LintErrors/unclosed-quotes-in-heading]])
 */
public class CheckErrorAlgorithm537 extends CheckErrorAlgorithmBase {

  public CheckErrorAlgorithm537() {
    super("Unclosed quotes in heading");
  }

  /**
   * Analyze a page to check if errors are present.
   * 
   * @param analysis Page analysis.
   * @param errors Errors found in the page.
   * @param onlyAutomatic True if analysis could be restricted to errors automatically fixed.
   * @return Flag indicating if the error was found.
   */
  @Override
  public boolean analyze(
      PageAnalysis analysis,
      Collection<CheckErrorResult> errors, boolean onlyAutomatic) {
    if ((analysis == null) || (analysis.getPage() == null)) {
      return false;
    }

    // Analyze each title
    List<PageElementTitle> titles = analysis.getTitles();
    String contents = analysis.getContents();
    boolean result = false;
    for (PageElementTitle title : titles) {

      // Try to reduce the area
      int beginIndex = title.getBeginIndex() + title.getFirstLevel();
      int endIndex = title.getEndIndex() - title.getSecondLevel();
      while ((beginIndex < endIndex) &&
             (contents.charAt(beginIndex) == ' ')) {
        beginIndex++;
      }
      while ((endIndex > beginIndex) &&
             (contents.charAt(endIndex - 1) == ' ')) {
        endIndex--;
      }

      // Analyze title for quotes
      int countQuotes = 0;
      int firstQuotesIndex = -1;
      int firstQuotesSize = -1;
      int index = beginIndex;
      while (index < endIndex) {
        if (contents.charAt(index) == '\'') {
          int count = 1;
          while ((index + count < endIndex) &&
                 (contents.charAt(index + count) == '\'')) {
            count++;
          }
          if (count > 1) {
            switch (count) {
            case 2:
            case 3:
              countQuotes += 1;
              break;
            case 4:
              countQuotes += 1;
              count = 3;
              break;
            case 5:
              countQuotes += 2;
              break;
            default:
              countQuotes += 2;
              count = 5;
              break;
            }
            if (firstQuotesIndex < 0) {
              firstQuotesIndex = index;
              firstQuotesSize = count;
            }
          }
          index += count;
        } else {
          index++;
        }
      }

      // Report error if needed
      if ((countQuotes % 2) != 0) {
        if (errors == null) {
          return true;
        }
        result = true;

        // Report a single block of quotes
        if (countQuotes == 1) {
          CheckErrorResult errorResult = createCheckErrorResult(
              analysis, firstQuotesIndex, endIndex);
          if (endIndex == firstQuotesIndex + firstQuotesSize) {
            boolean automatic = true;
            if (contents.substring(beginIndex, endIndex).indexOf('"') >= 0) {
              automatic = false;
            }
            if (contents.substring(beginIndex, firstQuotesIndex).indexOf('\'') >= 0) {
              automatic = false;
            }
            errorResult.addReplacement("", automatic);
          } else {
            String quotes = contents.substring(firstQuotesIndex, firstQuotesIndex + firstQuotesSize);
            errorResult.addReplacement(
                contents.substring(firstQuotesIndex, endIndex) + quotes,
                quotes + "..." + quotes,
                false);
            errorResult.addReplacement(contents.substring(
                firstQuotesIndex + firstQuotesSize, endIndex),
                "...",
                false);
          }
          errors.add(errorResult);
        }
        // Report other errors
        CheckErrorResult errorResult = createCheckErrorResult(
            analysis, firstQuotesIndex, endIndex);
        errors.add(errorResult);
      }
    }

    return result;
  }

  /**
   * Automatic fixing of some errors in the page.
   * 
   * @param analysis Page analysis.
   * @return Page contents after fix.
   */
  @Override
  protected String internalAutomaticFix(PageAnalysis analysis) {
    return fixUsingAutomaticReplacement(analysis);
  }
}