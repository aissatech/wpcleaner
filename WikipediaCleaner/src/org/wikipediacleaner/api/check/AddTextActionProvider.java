/*
 *  WPCleaner: A tool to help on Wikipedia maintenance tasks.
 *  Copyright (C) 2013  Nicolas Vervelle
 *
 *  See README.txt file for licensing information.
 */

package org.wikipediacleaner.api.check;

import javax.swing.Action;
import javax.swing.JTextPane;
import javax.swing.text.Element;

import org.wikipediacleaner.gui.swing.action.AddTextAction;
import org.wikipediacleaner.utils.StringChecker;
import org.wikipediacleaner.utils.TextProvider;


/**
 * An action provider for replacing text.
 */
public class AddTextActionProvider implements ActionProvider {

  private final String prefix;
  private final String suffix;
  private final TextProvider textProvider;
  private final String question;
  private final String[] possibleValues;
  private final boolean onlyList;
  private final String defaultValue;
  private final StringChecker checker;

  /**
   * @param prefix Prefix.
   * @param suffix Suffix.
   * @param textProvider Optional text provider.
   * @param question Question asked to the user.
   * @param checker String checker to verify the value.
   */
  public AddTextActionProvider(
      String prefix, String suffix,
      TextProvider textProvider, String question,
      StringChecker checker) {
    this(prefix, suffix, textProvider, question, "", checker);
  }

  /**
   * @param prefix Prefix.
   * @param suffix Suffix.
   * @param textProvider Optional text provider.
   * @param question Question asked to the user.
   * @param defaultValue Value used by default.
   * @param checker String checker to verify the value.
   */
  public AddTextActionProvider(
      String prefix, String suffix, TextProvider textProvider,
      String question, String defaultValue,
      StringChecker checker) {
    this(
        prefix, suffix, textProvider, question,
        null, false, defaultValue, checker);
  }

  /**
   * @param prefix Prefix.
   * @param suffix Suffix.
   * @param textProvider Optional text provider.
   * @param question Question asked to the user.
   * @param defaultValue Value used by default.
   * @param checker String checker to verify the value.
   */
  public AddTextActionProvider(
      String prefix, String suffix, TextProvider textProvider,
      String question,
      String[] possibleValues, boolean onlyList, String defaultValue,
      StringChecker checker) {
    this.prefix = prefix;
    this.suffix = suffix;
    this.textProvider = textProvider;
    this.question = question;
    this.possibleValues = possibleValues;
    this.onlyList = onlyList;
    this.defaultValue = defaultValue;
    this.checker = checker;
  }

  /* (non-Javadoc)
   * @see org.wikipediacleaner.api.check.ActionProvider#getAction(javax.swing.text.Element, javax.swing.JTextPane)
   */
  @Override
  public Action getAction(Element element, JTextPane textPane) {
    return new AddTextAction(
        prefix, suffix, textProvider, question,
        possibleValues, onlyList, defaultValue,
        checker,
        element, textPane);
  }

  /**
   * @param text New text.
   * @return True if this action can give this new text.
   */
  @Override
  public boolean isPossibleReplacement(String text) {
    return false;
  }

}
