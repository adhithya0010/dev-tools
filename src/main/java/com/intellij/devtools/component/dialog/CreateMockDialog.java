package com.intellij.devtools.component.dialog;

import com.intellij.devtools.component.editortextfield.customization.WrapTextCustomization;
import com.intellij.devtools.exec.HttpMethod;
import com.intellij.devtools.exec.HttpRequestConfig;
import com.intellij.devtools.utils.ComponentUtils;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.EditorTextField;
import com.intellij.util.ui.GridBag;
import com.intellij.util.ui.JBUI;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public class CreateMockDialog extends DialogWrapper {

  private JPanel dialogPanel;

  private JLabel pathLabel;
  private JLabel portLabel;
  private JLabel responseCodeLabel;
  private JLabel methodLabel;
  private JLabel responseHeadersLabel;
  private JLabel responseBodyLabel;

  private EditorTextField pathTextField;
  private EditorTextField portTextField;
  private EditorTextField responseCodeTextField;
  private JComboBox<HttpMethod> methodComboBox;
  private EditorTextField responseHeadersTextField;
  private EditorTextField responseBodyTextField;

  public CreateMockDialog(JComponent parent) {
    super(parent, false);
    setTitle("Create Mock");
    init();
  }

  @Override
  protected @Nullable JComponent createCenterPanel() {
    configureComponents();
    configureLayouts();
    configureListeners();
    return dialogPanel;
  }

  @Override
  protected boolean postponeValidation() {
    return super.postponeValidation();
  }

  private void configureComponents() {
    dialogPanel = new JPanel();
    setSize(500, 600);
    this.pathLabel = new JLabel("Path");
    this.portLabel = new JLabel("Port");
    this.responseCodeLabel = new JLabel("Response code");
    this.methodLabel = new JLabel("Method");
    this.responseHeadersLabel = new JLabel("Response Headers");
    this.responseBodyLabel = new JLabel("Response Body");
    this.pathTextField = ComponentUtils.createEditorTextField(PlainTextLanguage.INSTANCE);
    this.portTextField = ComponentUtils.createEditorTextField(PlainTextLanguage.INSTANCE);
    this.responseCodeTextField = ComponentUtils.createEditorTextField(PlainTextLanguage.INSTANCE);
    this.methodComboBox = new ComboBox<>(HttpMethod.values());
    this.methodComboBox.setSelectedItem(HttpMethod.GET);
    this.methodComboBox.setRenderer(
        new DefaultListCellRenderer() {
          @Override
          public Component getListCellRendererComponent(
              JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            this.setText(((HttpMethod) value).getValue());
            return this;
          }
        });
    this.responseHeadersTextField =
        ComponentUtils.createEditorTextField(
            PlainTextLanguage.INSTANCE, WrapTextCustomization.ENABLED);
    this.responseBodyTextField =
        ComponentUtils.createEditorTextField(
            PlainTextLanguage.INSTANCE, WrapTextCustomization.ENABLED);
    this.pathTextField.setPlaceholder("Path to mock /test/abc");
    this.portTextField.setPlaceholder("Port for the mock server");
    this.responseCodeTextField.setPlaceholder("Response status code value");
    this.responseHeadersTextField.setPlaceholder(
        "Rows are separated by lines\nKeys and values are separated by :\neg:\nContent-Type:application/json");
    this.responseBodyTextField.setPlaceholder("Response body data");

    this.pathTextField.setOneLineMode(true);
    this.portTextField.setOneLineMode(true);
    this.responseCodeTextField.setOneLineMode(true);
  }

  private void configureLayouts() {
    dialogPanel.setLayout(new GridBagLayout());

    Insets insets = JBUI.insets(5);
    GridBag gridBag = new GridBag();

    gridBag.nextLine();
    dialogPanel.add(
        this.pathLabel,
        gridBag.next().insets(insets).fillCellNone().anchor(GridBagConstraints.NORTHWEST));
    dialogPanel.add(
        this.pathTextField, gridBag.next().insets(insets).weightx(1f).coverLine().fillCell());

    gridBag.nextLine();
    dialogPanel.add(
        this.portLabel,
        gridBag.next().insets(insets).fillCellNone().anchor(GridBagConstraints.NORTHWEST));
    dialogPanel.add(
        this.portTextField, gridBag.next().insets(insets).weightx(1f).coverLine().fillCell());

    gridBag.nextLine();
    dialogPanel.add(
        this.responseCodeLabel,
        gridBag.next().insets(insets).fillCellNone().anchor(GridBagConstraints.NORTHWEST));
    dialogPanel.add(
        this.responseCodeTextField,
        gridBag.next().insets(insets).weightx(1f).coverLine().fillCell());

    gridBag.nextLine();
    dialogPanel.add(
        this.methodLabel,
        gridBag.next().insets(insets).fillCellNone().anchor(GridBagConstraints.NORTHWEST));
    dialogPanel.add(
        this.methodComboBox, gridBag.next().insets(insets).weightx(1f).coverLine().fillCell());

    gridBag.nextLine();
    dialogPanel.add(
        this.responseHeadersLabel,
        gridBag.next().insets(insets).fillCellNone().anchor(GridBagConstraints.NORTHWEST));
    dialogPanel.add(
        this.responseHeadersTextField,
        gridBag
            .next()
            .insets(insets)
            .weightx(1f)
            .weighty(1f)
            .coverLine(3)
            .coverColumn(3)
            .fillCell());

    gridBag.nextLine();
    gridBag.nextLine();
    gridBag.nextLine();
    dialogPanel.add(
        this.responseBodyLabel,
        gridBag.next().insets(insets).fillCellNone().anchor(GridBagConstraints.NORTHWEST));
    dialogPanel.add(
        this.responseBodyTextField,
        gridBag
            .next()
            .insets(insets)
            .weightx(1f)
            .weighty(1f)
            .coverLine(3)
            .coverColumn(3)
            .fillCell());
  }

  private void configureListeners() {
    initValidation();
  }

  @Override
  protected ValidationInfo doValidate() {
    Optional<ValidationInfo> validationInfo;
    if ((validationInfo = validateStringField(pathTextField)).isPresent()) {
      return validationInfo.get();
    }
    if ((validationInfo = validateNumberFields(portTextField)).isPresent()) {
      return validationInfo.get();
    }
    if ((validationInfo = validateNumberFields(responseCodeTextField)).isPresent()) {
      return validationInfo.get();
    }
    return super.doValidate();
  }

  public Optional<HttpRequestConfig> showAndGetData() {
    if (showAndGet()) {
      return Optional.ofNullable(getRequestConfig());
    }
    return Optional.empty();
  }

  private HttpRequestConfig getRequestConfig() {
    String path = this.pathTextField.getText();
    String port = this.portTextField.getText();
    String responseCode = this.responseCodeTextField.getText();
    HttpMethod httpMethod = (HttpMethod) this.methodComboBox.getSelectedItem();
    String responseHeaders = this.responseHeadersTextField.getText();
    String responseBody = this.responseBodyTextField.getText();
    return HttpRequestConfig.builder()
        .path(path)
        .port(Integer.parseInt(port))
        .responseCode(Integer.parseInt(responseCode))
        .method(httpMethod)
        .headers(toMap(responseHeaders))
        .responseBody(responseBody)
        .build();
  }

  private Map<String, List<String>> toMap(String requestHeaders) {
    if (StringUtils.isEmpty(requestHeaders)) {
      return Map.of();
    }
    String[] headerValues = requestHeaders.split("\n");
    Map<String, List<String>> headersMap = new HashMap<>();
    for (String header : headerValues) {
      String[] parts = header.split(":");
      headersMap.put(parts[0], Collections.singletonList(parts[1]));
    }
    return headersMap;
  }

  private Optional<ValidationInfo> validateStringField(EditorTextField textField) {
    Optional<ValidationInfo> validationInfo = Optional.empty();
    if (StringUtils.isEmpty(textField.getText())) {
      validationInfo = Optional.of(new ValidationInfo("Field should not be empty", textField));
    }
    return validationInfo;
  }

  private Optional<ValidationInfo> validateNumberFields(EditorTextField textField) {
    Optional<ValidationInfo> validationInfo = Optional.empty();
    if (StringUtils.isEmpty(textField.getText())) {
      validationInfo = Optional.of(new ValidationInfo("Field should not be empty", textField));
    }
    if (!StringUtils.isNumeric(textField.getText())) {
      validationInfo =
          Optional.of(new ValidationInfo("Only numeric values are allowed", textField));
    }
    return validationInfo;
  }
}
