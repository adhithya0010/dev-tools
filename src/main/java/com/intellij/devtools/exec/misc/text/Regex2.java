package com.intellij.devtools.exec.misc.text;

import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.devtools.exec.Operation;
import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.exec.OperationGroup;
import com.intellij.devtools.utils.GridConstraintUtils;
import com.intellij.devtools.utils.ProjectUtils;
import com.intellij.icons.AllIcons;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.lang.Language;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actions.IncrementalFindAction;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.SyntaxTraverser;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollBar;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.intellij.util.Alarm;
import com.intellij.util.ObjectUtils;
import com.intellij.util.SmartList;
import com.intellij.util.ui.GridBag;
import com.intellij.util.ui.JBInsets;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import org.intellij.lang.regexp.RegExpBundle;
import org.intellij.lang.regexp.RegExpHighlighter;
import org.intellij.lang.regexp.RegExpLanguage;
import org.intellij.lang.regexp.RegExpMatch;
import org.intellij.lang.regexp.RegExpMatchResult;
import org.intellij.lang.regexp.RegExpMatcherProvider;
import org.intellij.lang.regexp.RegExpModifierProvider;
import org.intellij.lang.regexp.psi.RegExpGroup;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

public class Regex2 extends Operation {

  @Override
  public String getNodeName() {
    return "Regex";
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public OperationCategory getOperationCategory() {
    return OperationCategory.TEXT;
  }

  @Override
  public OperationGroup getOperationGroup() {
    return OperationGroup.MISC;
  }

  private static final Key<List<RegExpMatch>> LATEST_MATCHES = Key.create("REG_EXP_LATEST_MATCHES");
  private static final Key<RegExpMatchResult> RESULT = Key.create("REG_EXP_RESULT");

  public static final class Keys {
    // do not load CheckRegExpForm early, declare this key in separate class
    public static final Key<Boolean> CHECK_REG_EXP_EDITOR = Key.create("CHECK_REG_EXP_EDITOR");
  }

  private static final String LAST_EDITED_REGEXP = "last.edited.regexp";

  private final EditorTextField myRegExp;
  private final EditorTextField mySampleText;

  private final JPanel myRootPanel;
  private final JBLabel myRegExpIcon = new JBLabel();
  private final JBLabel mySampleIcon = new JBLabel();

  private final List<RangeHighlighter> mySampleHighlights = new SmartList<>();
  private RangeHighlighter myRegExpHighlight = null;

  public Regex2() {
    final Project project = ProjectUtils.getProject();
    PsiFileFactory factory = PsiFileFactory.getInstance(ProjectUtils.getProject());
    PsiFile myNewFile =
        factory.createFileFromText("abc", RegExpLanguage.INSTANCE, "abc abc abc", true, false);
    final Document document = PsiDocumentManager.getInstance(project).getDocument(myNewFile);

    final Language language = RegExpLanguage.INSTANCE;
    final LanguageFileType fileType = language.getAssociatedFileType();

    myRegExp =
        new EditorTextField(document, project, fileType, false, false) {
          private final Disposable disposable = Disposer.newDisposable();

          @Override
          protected void onEditorAdded(@NotNull Editor editor) {
            super.onEditorAdded(editor);
            editor
                .getCaretModel()
                .addCaretListener(
                    new CaretListener() {

                      @Override
                      public void caretPositionChanged(@NotNull CaretEvent event) {
                        final int offset = editor.logicalPositionToOffset(event.getNewPosition());
                        highlightRegExpGroup(offset, myNewFile);
                      }
                    },
                    disposable);
          }

          @Override
          public void removeNotify() {
            super.removeNotify();
            removeHighlights(HighlightManager.getInstance(myNewFile.getProject()));
          }

          @Override
          protected @NotNull EditorEx createEditor() {
            final EditorEx editor = super.createEditor();
            editor.putUserData(
                org.intellij.lang.regexp.intention.CheckRegExpForm.Keys.CHECK_REG_EXP_EDITOR,
                Boolean.TRUE);
            editor.putUserData(IncrementalFindAction.SEARCH_DISABLED, Boolean.TRUE);
            editor.setEmbeddedIntoDialogWrapper(true);
            return editor;
          }

          @Override
          public Dimension getPreferredSize() {
            final Dimension size = super.getPreferredSize();
            if (size.height > 250) {
              size.height = 250;
            }
            return size;
          }

          @Override
          protected void updateBorder(@NotNull EditorEx editor) {
            setupBorder(editor);
          }
        };
    myRegExp.addFocusListener(
        new FocusAdapter() {
          @Override
          public void focusGained(FocusEvent e) {
            final Editor editor = myRegExp.getEditor();
            if (editor == null) return;
            final int offset = editor.getCaretModel().getOffset();
            highlightRegExpGroup(offset, myNewFile);
          }
        });
    setupIcon(myRegExp, myRegExpIcon);

    String sampleText =
        PropertiesComponent.getInstance(project)
            .getValue(LAST_EDITED_REGEXP, RegExpBundle.message("checker.sample.text"));
    mySampleText =
        new EditorTextField(sampleText, project, PlainTextFileType.INSTANCE) {
          private final Disposable disposable = Disposer.newDisposable();

          @Override
          protected void onEditorAdded(@NotNull Editor editor) {
            super.onEditorAdded(editor);
            editor
                .getCaretModel()
                .addCaretListener(
                    new CaretListener() {

                      @Override
                      public void caretPositionChanged(@NotNull CaretEvent event) {
                        final int offset = editor.logicalPositionToOffset(event.getNewPosition());
                        highlightSampleGroup(offset, myNewFile);
                      }
                    },
                    disposable);
          }

          @Override
          public void removeNotify() {
            super.removeNotify();
            removeHighlights(HighlightManager.getInstance(myNewFile.getProject()));
          }

          @Override
          protected @NotNull EditorEx createEditor() {
            final EditorEx editor = super.createEditor();
            editor.putUserData(IncrementalFindAction.SEARCH_DISABLED, Boolean.TRUE);
            editor.setEmbeddedIntoDialogWrapper(true);
            return editor;
          }

          @Override
          public Dimension getPreferredSize() {
            final Dimension size = super.getPreferredSize();
            if (size.height > 250) {
              size.height = 250;
            }
            return size;
          }

          @Override
          protected void updateBorder(@NotNull EditorEx editor) {
            setupBorder(editor);
          }
        };
    mySampleText.addFocusListener(
        new FocusAdapter() {
          @Override
          public void focusGained(FocusEvent e) {
            final Editor editor = mySampleText.getEditor();
            if (editor == null) return;
            final int offset = editor.getCaretModel().getOffset();
            highlightSampleGroup(offset, myNewFile);
          }
        });

    setupIcon(mySampleText, mySampleIcon);
    mySampleText.setOneLineMode(false);

    myRootPanel =
        new JPanel(new GridBagLayout()) {
          private final Disposable disposable = Disposer.newDisposable();
          private Alarm updater;

          @Override
          public void addNotify() {
            super.addNotify();
            IdeFocusManager.getGlobalInstance().requestFocus(mySampleText, true);

            registerFocusShortcut(myRegExp, "shift TAB", mySampleText);
            registerFocusShortcut(myRegExp, "TAB", mySampleText);
            registerFocusShortcut(mySampleText, "shift TAB", myRegExp);
            registerFocusShortcut(mySampleText, "TAB", myRegExp);

            updater = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, disposable);
            final DocumentListener documentListener =
                new DocumentListener() {
                  @Override
                  public void documentChanged(@NotNull DocumentEvent e) {
                    update();
                  }
                };
            myRegExp.addDocumentListener(documentListener);
            mySampleText.addDocumentListener(documentListener);

            update();
            mySampleText.selectAll();
          }

          private static void registerFocusShortcut(
              JComponent source, String shortcut, EditorTextField target) {
            final AnAction action =
                new AnAction() {
                  @Override
                  public void actionPerformed(@NotNull AnActionEvent e) {
                    IdeFocusManager.findInstance().requestFocus(target.getFocusTarget(), true);
                  }
                };
            action.registerCustomShortcutSet(CustomShortcutSet.fromString(shortcut), source);
          }

          private void update() {
            // triggers resizing of balloon when necessary
            myRootPanel.revalidate();
            Balloon balloon = JBPopupFactory.getInstance().getParentBalloonFor(myRootPanel);
            if (balloon != null && !balloon.isDisposed()) balloon.revalidate();

            updater.cancelAllRequests();
            if (!updater.isDisposed()) {
              updater.addRequest(
                  () -> {
                    final RegExpMatchResult result =
                        isMatchingText(myNewFile, myRegExp.getText(), mySampleText.getText());
                    myNewFile.putUserData(RESULT, result);
                    if (result != RegExpMatchResult.MATCHES && result != RegExpMatchResult.FOUND) {
                      setMatches(myNewFile, null);
                    }
                    ApplicationManager.getApplication()
                        .invokeLater(
                            () -> reportResult(result, myNewFile),
                            ModalityState.any(),
                            __ -> updater.isDisposed());
                  },
                  0);
            }
          }

          @Override
          public void removeNotify() {
            super.removeNotify();
            PropertiesComponent.getInstance(project)
                .setValue(LAST_EDITED_REGEXP, mySampleText.getText());
          }
        };
    myRootPanel.setBorder(JBUI.Borders.empty(UIUtil.DEFAULT_VGAP, UIUtil.DEFAULT_HGAP));

    myRegExp.setPreferredSize(new Dimension(-1, 100));
    mySampleText.setPreferredSize(new Dimension(-1, 200));

    GridBag gridBag = new GridBag();
    JBInsets insets = JBUI.insets(UIUtil.DEFAULT_VGAP / 2, UIUtil.DEFAULT_HGAP / 2);

    myRootPanel.setLayout(new GridBagLayout());
    gridBag.nextLine();
    myRootPanel.add(
        createLabel("Regular expression", myRegExp), gridBag.next().insets(insets).fillCellNone());
    myRootPanel.add(
        myRegExp, gridBag.next().insets(insets).fillCellHorizontally().coverLine().weightx(1f));
    gridBag.nextLine();
    myRootPanel.add(
        createLabel("Text", mySampleText), gridBag.next().insets(insets).fillCellNone());
    myRootPanel.add(
        mySampleText, gridBag.next().insets(insets).fillCellHorizontally().coverLine().weightx(1f));
    myRootPanel.add(
        new Spacer(), gridBag.nextLine().next().coverLine().fillCell().weightx(1f).weighty(1f));

    setLayout(new GridLayoutManager(1, 1));
    add(myRootPanel, GridConstraintUtils.buildGridConstraint(0, 0, GridConstraints.FILL_BOTH));

    myRegExp.setText("(abc)");
    mySampleText.setText("abc abc abc def");
  }

  private void highlightSampleGroup(int offset, @NotNull PsiFile myNewFile) {
    final HighlightManager highlightManager = HighlightManager.getInstance(myNewFile.getProject());
    removeHighlights(highlightManager);

    final List<RegExpMatch> matches = getMatches(myNewFile);
    int index = indexOfGroupAtOffset(matches, offset);
    if (index > 0) {
      @Nullable RegExpGroup group =
          SyntaxTraverser.psiTraverser(myNewFile)
              .filter(RegExpGroup.class)
              .filter(RegExpGroup::isCapturing)
              .get(index - 1);
      highlightRegExpGroup(group, highlightManager);
      highlightMatchGroup(highlightManager, matches, index);
    } else {
      highlightMatchGroup(highlightManager, matches, 0);
    }
  }

  private void highlightRegExpGroup(int offset, @NotNull PsiFile myNewFile) {
    final RegExpGroup group = findCapturingGroupAtOffset(myNewFile, offset);
    final HighlightManager highlightManager = HighlightManager.getInstance(myNewFile.getProject());
    removeHighlights(highlightManager);
    if (group != null) {
      final int index =
          SyntaxTraverser.psiTraverser(myNewFile).filter(RegExpGroup.class).indexOf(e -> e == group)
              + 1;
      highlightRegExpGroup(group, highlightManager);
      highlightMatchGroup(highlightManager, getMatches(myNewFile), index);
    } else {
      highlightMatchGroup(highlightManager, getMatches(myNewFile), 0);
    }
  }

  private static int indexOfGroupAtOffset(List<RegExpMatch> matches, int offset) {
    int index = -1;
    for (RegExpMatch match : matches) {
      final int count = match.count();
      for (int i = 0; i < count; i++) {
        final int start = match.start(i);
        if (start <= offset && match.end(i) >= offset) {
          index = i;
          // don't break here, because there may be a better matching group inside the current group
        } else if (start > offset) {
          break;
        }
      }
    }
    return index;
  }

  private static RegExpGroup findCapturingGroupAtOffset(@NotNull PsiFile myNewFile, int offset) {
    PsiElement element = myNewFile.findElementAt(offset);
    RegExpGroup group = null;
    while (element != null) {
      if (element instanceof RegExpGroup g) {
        if (g.isCapturing()) {
          group = g;
          break;
        }
      }
      element = element.getParent();
    }
    return group;
  }

  private void highlightMatchGroup(
      HighlightManager highlightManager, List<RegExpMatch> matches, int group) {
    final Editor editor = mySampleText.getEditor();
    if (editor == null) {
      return;
    }
    for (RegExpMatch match : matches) {
      final int start = match.start(group);
      final int end = match.end(group);
      if (start < 0 || end < 0) continue;
      if (group != 0 || start != 0 || end != mySampleText.getText().length()) {
        highlightManager.addRangeHighlight(
            editor, start, end, RegExpHighlighter.MATCHED_GROUPS, true, mySampleHighlights);
      }
    }
  }

  private void highlightRegExpGroup(RegExpGroup group, HighlightManager highlightManager) {
    Editor editor = myRegExp.getEditor();
    if (editor == null) {
      return;
    }
    final PsiElement[] array = {group};
    List<RangeHighlighter> highlighter = new SmartList<>();
    highlightManager.addOccurrenceHighlights(
        editor, array, RegExpHighlighter.MATCHED_GROUPS, true, highlighter);
    myRegExpHighlight = highlighter.get(0);
  }

  private void removeHighlights(HighlightManager highlightManager) {
    final Editor sampleEditor = mySampleText.getEditor();
    if (sampleEditor != null) {
      for (RangeHighlighter highlighter : mySampleHighlights) {
        highlightManager.removeSegmentHighlighter(sampleEditor, highlighter);
      }
      mySampleHighlights.clear();
    }
    final Editor regExpEditor = myRegExp.getEditor();
    if (myRegExpHighlight != null && regExpEditor != null) {
      highlightManager.removeSegmentHighlighter(regExpEditor, myRegExpHighlight);
      myRegExpHighlight = null;
    }
  }

  private static JLabel createLabel(
      @NotNull @NlsContexts.Label String labelText, @NotNull JComponent component) {
    final JLabel label = new JLabel(UIUtil.removeMnemonic(labelText));
    final int index = UIUtil.getDisplayMnemonicIndex(labelText);
    if (index != -1) {
      label.setDisplayedMnemonic(labelText.charAt(index + 1));
      label.setDisplayedMnemonicIndex(index);
    }
    label.setLabelFor(component);
    return label;
  }

  private static void setupIcon(@NotNull EditorTextField field, @NotNull JComponent icon) {
    field.addSettingsProvider(
        editor -> {
          icon.setBorder(JBUI.Borders.emptyLeft(2));
          final JScrollPane scrollPane = editor.getScrollPane();
          scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
          scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
          final JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
          verticalScrollBar.setBackground(editor.getBackgroundColor());
          verticalScrollBar.add(JBScrollBar.LEADING, icon);
          verticalScrollBar.setOpaque(true);
        });
  }

  private void reportResult(RegExpMatchResult result, @NotNull PsiFile myNewFile) {
    switch (result) {
      case NO_MATCH -> {
        setIconAndTooltip(
            mySampleIcon, AllIcons.General.BalloonError, RegExpBundle.message("tooltip.no.match"));
        setIconAndTooltip(myRegExpIcon, null, null);
      }
      case MATCHES -> {
        setIconAndTooltip(
            mySampleIcon, AllIcons.General.InspectionsOK, RegExpBundle.message("tooltip.matches"));
        setIconAndTooltip(myRegExpIcon, null, null);
        final Editor editor = mySampleText.getEditor();
        if (editor != null) {
          final HighlightManager highlightManager =
              HighlightManager.getInstance(myNewFile.getProject());
          removeHighlights(highlightManager);
        }
      }
      case FOUND -> {
        final List<RegExpMatch> matches = getMatches(myNewFile);
        final Editor editor = mySampleText.getEditor();
        if (editor != null) {
          final HighlightManager highlightManager =
              HighlightManager.getInstance(myNewFile.getProject());
          removeHighlights(highlightManager);
          highlightMatchGroup(highlightManager, matches, 0);
        }
        if (matches.size() > 1) {
          setIconAndTooltip(
              mySampleIcon,
              AllIcons.General.InspectionsOK,
              RegExpBundle.message("tooltip.found.multiple", matches.size()));
        } else {
          setIconAndTooltip(
              mySampleIcon, AllIcons.General.InspectionsOK, RegExpBundle.message("tooltip.found"));
        }
        setIconAndTooltip(myRegExpIcon, null, null);
      }
      case INCOMPLETE -> {
        setIconAndTooltip(
            mySampleIcon,
            AllIcons.General.BalloonWarning,
            RegExpBundle.message("tooltip.more.input.expected"));
        setIconAndTooltip(myRegExpIcon, null, null);
      }
      case BAD_REGEXP -> {
        setIconAndTooltip(mySampleIcon, null, null);
        setIconAndTooltip(
            myRegExpIcon,
            AllIcons.General.BalloonError,
            RegExpBundle.message("tooltip.bad.pattern"));
      }
      case TIMEOUT -> {
        setIconAndTooltip(mySampleIcon, null, null);
        setIconAndTooltip(
            myRegExpIcon,
            AllIcons.General.BalloonWarning,
            RegExpBundle.message("tooltip.pattern.is.too.complex"));
      }
    }
  }

  private static void setIconAndTooltip(
      JBLabel label, Icon icon, @NlsContexts.Tooltip String tooltip) {
    label.setIcon(icon);
    label.setToolTipText(tooltip);
  }

  @NotNull public JComponent getPreferredFocusedComponent() {
    return mySampleText;
  }

  @NotNull public JPanel getRootPanel() {
    return myRootPanel;
  }

  @ApiStatus.Internal
  public static @NotNull List<RegExpMatch> getMatches(@NotNull PsiFile myNewFile) {
    return ObjectUtils.notNull(myNewFile.getUserData(LATEST_MATCHES), Collections.emptyList());
  }

  public static void setMatches(@NotNull PsiFile myNewFile, @Nullable List<RegExpMatch> matches) {
    myNewFile.putUserData(LATEST_MATCHES, matches);
  }

  @TestOnly
  public static boolean isMatchingTextTest(@NotNull PsiFile myNewFile, @NotNull String sampleText) {
    return getMatchResult(myNewFile, sampleText) == RegExpMatchResult.MATCHES;
  }

  @TestOnly
  public static RegExpMatchResult getMatchResult(
      @NotNull PsiFile myNewFile, @NotNull String sampleText) {
    return isMatchingText(myNewFile, myNewFile.getText(), sampleText);
  }

  private static RegExpMatchResult isMatchingText(
      @NotNull final PsiFile myNewFile, String regExpText, @NotNull String sampleText) {
    final Language myNewFileLanguage = myNewFile.getLanguage();
    final RegExpMatcherProvider matcherProvider =
        RegExpMatcherProvider.EP.forLanguage(myNewFileLanguage);
    if (matcherProvider != null) {
      final RegExpMatchResult result =
          ReadAction.compute(
              () -> {
                final PsiLanguageInjectionHost host =
                    InjectedLanguageManager.getInstance(myNewFile.getProject())
                        .getInjectionHost(myNewFile);
                return host != null
                    ? matcherProvider.matches(regExpText, myNewFile, host, sampleText, 1000L)
                    : null;
              });
      if (result != null) {
        return result;
      }
    }

    final Integer patternFlags =
        ReadAction.compute(
            () -> {
              final PsiLanguageInjectionHost host =
                  InjectedLanguageManager.getInstance(myNewFile.getProject())
                      .getInjectionHost(myNewFile);
              int flags = 0;
              if (host != null) {
                for (RegExpModifierProvider provider :
                    RegExpModifierProvider.EP.allForLanguage(host.getLanguage())) {
                  flags = provider.getFlags(host, myNewFile);
                  if (flags > 0) break;
                }
              }
              return flags;
            });

    try {
      //noinspection MagicConstant
      final Matcher matcher =
          Pattern.compile(regExpText, patternFlags)
              .matcher(StringUtil.newBombedCharSequence(sampleText, 1000));
      if (matcher.matches()) {
        setMatches(myNewFile, collectMatches(matcher));
        return RegExpMatchResult.MATCHES;
      }
      final boolean hitEnd = matcher.hitEnd();
      if (matcher.find()) {
        setMatches(myNewFile, collectMatches(matcher));
        return RegExpMatchResult.FOUND;
      } else if (hitEnd) {
        return RegExpMatchResult.INCOMPLETE;
      } else {
        return RegExpMatchResult.NO_MATCH;
      }
    } catch (ProcessCanceledException ignore) {
      return RegExpMatchResult.TIMEOUT;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return RegExpMatchResult.BAD_REGEXP;
  }

  private static List<RegExpMatch> collectMatches(Matcher matcher) {
    List<RegExpMatch> matches = new SmartList<>();
    do {
      final RegExpMatch match = new RegExpMatch();
      final int count = matcher.groupCount();
      for (int i = 0; i <= count; i++) {
        match.add(matcher.start(i), matcher.end(i));
      }
      matches.add(match);
    } while (matcher.find());
    return matches;
  }
}
