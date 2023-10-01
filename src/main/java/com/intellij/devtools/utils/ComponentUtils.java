package com.intellij.devtools.utils;

import com.intellij.devtools.exec.Parameter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBScrollPane;
import java.awt.Component;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.JTextComponent;
import org.jetbrains.annotations.NotNull;

public class ComponentUtils {

  private ComponentUtils() {}

  public static void refreshComponent(JComponent jComponent) {
    jComponent.revalidate();
    jComponent.repaint();
  }

  public static void removeAllChildren(JComponent jComponent) {
    jComponent.removeAll();
  }

  public static void resetTextField(JTextComponent jTextComponent) {
    jTextComponent.setText(null);
  }

  public static void resetTextField(EditorTextField jTextComponent) {
    jTextComponent.setText(null);
  }

  public static JScrollPane attachScroll(JComponent jComponent) {
    return new JBScrollPane(
        jComponent,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
  }

  public static DocumentListener getDocumentChangeListener(
      Function<DocumentEvent, Void> handleChangeFunction) {
    return new DocumentListener() {
      @Override
      public void documentChanged(@NotNull DocumentEvent event) {
        handleChangeFunction.apply(event);
      }
    };
  }

  public static Object getValue(Component component) {
    if (component instanceof JTextField) {
      return ((JTextField) component).getText();
    }
    if (component instanceof JComboBox) {
      return ((JComboBox<?>) component).getSelectedItem();
    }
    if (component instanceof JSpinner) {
      return ((Double) (((JSpinner) component).getValue())).intValue();
    }
    return null;
  }

  public static void setValue(Component component, Object value) {
    if (Objects.isNull(component) || Objects.isNull(value)) {
      return;
    }
    if (component instanceof JTextField) {
      ((JTextField) component).setText((String) value);
    }
    if (component instanceof JComboBox) {
      ((JComboBox<?>) component).setSelectedItem(value);
    }
  }

  public static Optional<JComponent> toComponent(Parameter parameter) {
    JComponent component;
    switch (parameter.getType()) {
      case TEXT:
        component = new JTextField();
        break;
      case NUMBER:
        SpinnerModel spinnerNumberModel =
            new SpinnerNumberModel(
                Integer.parseInt(parameter.getDefaultValue()),
                parameter.getMinValue(),
                parameter.getMaxValue(),
                1);
        component = new JSpinner(spinnerNumberModel);
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor((JSpinner) component, "#");
        ((JSpinner) component).setEditor(editor);
        break;
      case SELECT:
        JComboBox<String> comboBox = new ComboBox<>(parameter.getValues().toArray(new String[0]));
        comboBox.setSelectedItem(parameter.getDefaultValue());
        component = comboBox;
        break;
      default:
        component = null;
    }
    return Optional.ofNullable(component);
  }
}
