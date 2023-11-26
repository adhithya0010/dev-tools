package com.intellij.devtools.exec.misc.text;

import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.devtools.component.editortextfield.customization.IconEditorCustomization;
import com.intellij.devtools.component.editortextfield.customization.RegexEditorCustomization;
import com.intellij.devtools.component.editortextfield.customization.WrapTextCustomization;
import com.intellij.devtools.exec.Operation;
import com.intellij.devtools.utils.ComponentUtils;
import com.intellij.devtools.utils.ProjectUtils;
import com.intellij.icons.AllIcons;
import com.intellij.lang.Language;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.SyntaxTraverser;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBLabel;
import com.intellij.uiDesigner.core.Spacer;
import com.intellij.util.ObjectUtils;
import com.intellij.util.SmartList;
import com.intellij.util.ui.GridBag;
import com.intellij.util.ui.JBInsets;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.intellij.lang.regexp.RegExpBundle;
import org.intellij.lang.regexp.RegExpHighlighter;
import org.intellij.lang.regexp.RegExpLanguage;
import org.intellij.lang.regexp.RegExpMatch;
import org.intellij.lang.regexp.RegExpMatchResult;
import org.intellij.lang.regexp.RegExpMatcherProvider;
import org.intellij.lang.regexp.RegExpModifierProvider;
import org.intellij.lang.regexp.psi.RegExpGroup;
import org.jetbrains.annotations.Nullable;

public class Regex extends Operation {

  private static final Key<List<RegExpMatch>> LATEST_MATCHES = Key.create("REG_EXP_LATEST_MATCHES");
  private static final Key<RegExpMatchResult> RESULT = Key.create("REG_EXP_RESULT");

  private PsiFile tempFile;
  private EditorTextField regularExpressionTextField;
  private EditorTextField inputTextField;

  private final JBLabel myRegExpIcon = new JBLabel();
  private final JBLabel mySampleIcon = new JBLabel();

  private final List<RangeHighlighter> sampleHighlights = new SmartList<>();
  private RangeHighlighter regularExpressionHighlighter = null;

  @Override
  public String getNodeName() {
    return "Regex";
  }

  public Regex() {
    configureComponents();
    configureParameters(parametersPanel);
    configureLayouts();
    configureListeners();
  }

  @Override
  protected void configureComponents() {
    Project project = ProjectUtils.getProject();
    PsiFileFactory factory = PsiFileFactory.getInstance(ProjectUtils.getProject());

    tempFile =
        factory.createFileFromText("abc", RegExpLanguage.INSTANCE, "abc abc abc", true, false);
    Document document = PsiDocumentManager.getInstance(project).getDocument(tempFile);

    Language language = RegExpLanguage.INSTANCE;

    Disposable regexpDisposable = Disposer.newDisposable();
    Disposable mySampleTextDisposable = Disposer.newDisposable();

    regularExpressionTextField =
        ComponentUtils.createEditorTextField(
            language,
            WrapTextCustomization.ENABLED,
            new IconEditorCustomization(myRegExpIcon),
            new RegexEditorCustomization(this::highlightRegExpGroup, tempFile, regexpDisposable));
    inputTextField =
        ComponentUtils.createEditorTextField(
            PlainTextLanguage.INSTANCE,
            WrapTextCustomization.ENABLED,
            new IconEditorCustomization(mySampleIcon),
            new RegexEditorCustomization(
                this::highlightSampleGroup, tempFile, mySampleTextDisposable));

    regularExpressionTextField.setDocument(document);
    regularExpressionTextField.setDisposedWith(regexpDisposable);
    inputTextField.setDisposedWith(mySampleTextDisposable);

    regularExpressionTextField.setPreferredSize(new Dimension(-1, 100));
    inputTextField.setPreferredSize(new Dimension(-1, 200));
  }

  @Override
  protected void configureListeners() {
    regularExpressionTextField.addFocusListener(
        new FocusAdapter() {
          @Override
          public void focusGained(FocusEvent e) {
            final Editor editor = regularExpressionTextField.getEditor();
            if (editor == null) return;
            final int offset = editor.getCaretModel().getOffset();
            highlightRegExpGroup(offset, tempFile);
          }
        });

    inputTextField.addFocusListener(
        new FocusAdapter() {
          @Override
          public void focusGained(FocusEvent e) {
            final Editor editor = inputTextField.getEditor();
            if (editor == null) return;
            final int offset = editor.getCaretModel().getOffset();
            highlightSampleGroup(offset, tempFile);
          }
        });

    final DocumentListener documentListener =
        new DocumentListener() {
          @Override
          public void documentChanged(DocumentEvent e) {
            update(tempFile);
          }
        };
    regularExpressionTextField.addDocumentListener(documentListener);
    inputTextField.addDocumentListener(documentListener);

    regularExpressionTextField.setText("([a-z])\\w+");
    inputTextField.setText(readSampleText());
  }

  @Override
  protected void configureLayouts() {
    GridBag gridBag = new GridBag();
    JBInsets insets = JBUI.insets(UIUtil.DEFAULT_VGAP / 2, UIUtil.DEFAULT_HGAP / 2);

    setLayout(new GridBagLayout());
    gridBag.nextLine();
    add(
        createLabel("Regular expression", regularExpressionTextField),
        gridBag.next().insets(insets).anchor(GridBagConstraints.FIRST_LINE_START).fillCellNone());
    add(
        regularExpressionTextField,
        gridBag.next().insets(insets).fillCellHorizontally().coverLine().weightx(1f));
    gridBag.nextLine();
    add(
        createLabel("Text", inputTextField),
        gridBag.next().anchor(GridBagConstraints.FIRST_LINE_START).insets(insets).fillCellNone());
    add(
        inputTextField,
        gridBag.next().insets(insets).fillCellHorizontally().coverLine().weightx(1f));
    add(new Spacer(), gridBag.nextLine().next().coverLine().fillCell().weightx(1f).weighty(1f));
  }

  private void update(PsiFile myNewFile) {
    final RegExpMatchResult result =
        isMatchingText(myNewFile, regularExpressionTextField.getText(), inputTextField.getText());
    myNewFile.putUserData(RESULT, result);
    if (result != RegExpMatchResult.MATCHES && result != RegExpMatchResult.FOUND) {
      setMatches(myNewFile, null);
    }
    ApplicationManager.getApplication()
        .invokeLater(() -> reportResult(result, myNewFile), ModalityState.any());
  }

  private void highlightSampleGroup(int offset, PsiFile myNewFile) {
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

  private void highlightRegExpGroup(int offset, PsiFile myNewFile) {
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

  private static RegExpGroup findCapturingGroupAtOffset(PsiFile myNewFile, int offset) {
    PsiElement element = myNewFile.findElementAt(offset);
    RegExpGroup group = null;
    while (element != null) {
      if (element instanceof RegExpGroup g && g.isCapturing()) {
        group = g;
        break;
      }
      element = element.getParent();
    }
    return group;
  }

  private void highlightMatchGroup(
      HighlightManager highlightManager, List<RegExpMatch> matches, int group) {
    final Editor editor = inputTextField.getEditor();
    if (editor == null) {
      return;
    }
    for (RegExpMatch match : matches) {
      final int start = match.start(group);
      final int end = match.end(group);
      if (start < 0 || end < 0) continue;
      if (group != 0 || start != 0 || end != inputTextField.getText().length()) {
        highlightManager.addRangeHighlight(
            editor, start, end, RegExpHighlighter.MATCHED_GROUPS, true, sampleHighlights);
      }
    }
  }

  private void highlightRegExpGroup(RegExpGroup group, HighlightManager highlightManager) {
    Editor editor = regularExpressionTextField.getEditor();
    if (editor == null) {
      return;
    }
    final PsiElement[] array = {group};
    List<RangeHighlighter> highlighter = new SmartList<>();
    highlightManager.addOccurrenceHighlights(
        editor, array, RegExpHighlighter.MATCHED_GROUPS, true, highlighter);
    regularExpressionHighlighter = highlighter.get(0);
  }

  private void removeHighlights(HighlightManager highlightManager) {
    final Editor sampleEditor = inputTextField.getEditor();
    if (sampleEditor != null) {
      for (RangeHighlighter highlighter : sampleHighlights) {
        highlightManager.removeSegmentHighlighter(sampleEditor, highlighter);
      }
      sampleHighlights.clear();
    }
    final Editor regExpEditor = regularExpressionTextField.getEditor();
    if (regularExpressionHighlighter != null && regExpEditor != null) {
      highlightManager.removeSegmentHighlighter(regExpEditor, regularExpressionHighlighter);
      regularExpressionHighlighter = null;
    }
  }

  private static JLabel createLabel(@NlsContexts.Label String labelText, JComponent component) {
    final JLabel label = new JLabel(UIUtil.removeMnemonic(labelText));
    final int index = UIUtil.getDisplayMnemonicIndex(labelText);
    if (index != -1) {
      label.setDisplayedMnemonic(labelText.charAt(index + 1));
      label.setDisplayedMnemonicIndex(index);
    }
    label.setLabelFor(component);
    return label;
  }

  private void reportResult(RegExpMatchResult result, PsiFile myNewFile) {
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
        final Editor editor = inputTextField.getEditor();
        if (editor != null) {
          final HighlightManager highlightManager =
              HighlightManager.getInstance(myNewFile.getProject());
          removeHighlights(highlightManager);
        }
      }
      case FOUND -> {
        final List<RegExpMatch> matches = getMatches(myNewFile);
        final Editor editor = inputTextField.getEditor();
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

  public static List<RegExpMatch> getMatches(PsiFile myNewFile) {
    return ObjectUtils.notNull(myNewFile.getUserData(LATEST_MATCHES), Collections.emptyList());
  }

  public static void setMatches(PsiFile myNewFile, @Nullable List<RegExpMatch> matches) {
    myNewFile.putUserData(LATEST_MATCHES, matches);
  }

  private static RegExpMatchResult isMatchingText(
      final PsiFile myNewFile, String regExpText, String sampleText) {
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

  private String readSampleText() {
    try {
      return FileUtil.loadTextAndClose(
          this.getClass().getClassLoader().getResourceAsStream("devtools/lorum-ipsum.txt"));
    } catch (IOException e) {
      e.printStackTrace();
      return "";
    }
  }
}
