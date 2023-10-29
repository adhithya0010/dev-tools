package com.intellij.devtools.exec.generator.impl;

import static com.intellij.devtools.MessageKeys.GENERATOR_TIMESTAMP_NAME;

import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.exec.OperationGroup;
import com.intellij.devtools.exec.generator.Generator;
import com.intellij.devtools.locale.MessageBundle;
import com.intellij.devtools.utils.ComponentUtils;
import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import com.intellij.uiDesigner.core.Spacer;
import com.intellij.util.ui.GridBag;
import com.intellij.util.ui.JBInsets;
import com.intellij.util.ui.JBUI;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class TimeStampGenerator extends Generator {

  private static final String DEFAULT_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

  private TimeHolder timeHolder;
  private JBTextField timeFormatTextField;
  private JBLabel formatCorrectnessLabel;
  private JButton infoButton;

  @Override
  public String getNodeName() {
    return MessageBundle.get(GENERATOR_TIMESTAMP_NAME);
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public OperationCategory getOperationCategory() {
    return null;
  }

  @Override
  public OperationGroup getOperationGroup() {
    return OperationGroup.GENERATOR;
  }

  @Override
  protected void configureParameters(JPanel parametersPanel) {
    super.configureParameters(parametersPanel);
    GridBag gridBag = new GridBag();
    JBInsets insets = JBInsets.create(3, 3);
    timeFormatTextField = new JBTextField(DEFAULT_TIME_FORMAT, 25);
    infoButton = new RoundedButton(AllIcons.General.BalloonInformation);
    formatCorrectnessLabel = new JBLabel();
    validatePattern();
    // to remote the spacing between the image and button's borders
    infoButton.setMargin(JBUI.emptyInsets());
    infoButton.setSize(new Dimension(24, 24));
    infoButton.setPreferredSize(new Dimension(24, 24));
    infoButton.setMaximumSize(new Dimension(24, 24));
    infoButton.setMinimumSize(new Dimension(24, 24));

    parametersPanel.setLayout(new GridBagLayout());
    gridBag.nextLine();
    JScrollPane dateInputTable = getDateInputTable();
    dateInputTable.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    dateInputTable.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    parametersPanel.add(
        dateInputTable,
        gridBag.next().insets(insets).fillCellHorizontally().weightx(1f).coverLine());

    gridBag.nextLine();
    parametersPanel.add(new JBLabel("Time pattern"), gridBag.next().insets(insets).fillCellNone());
    parametersPanel.add(infoButton, gridBag.next().insets(insets).fillCellNone());
    parametersPanel.add(timeFormatTextField, gridBag.next().insets(insets).fillCellNone());
    parametersPanel.add(formatCorrectnessLabel, gridBag.next().insets(insets).fillCellNone());
    parametersPanel.add(new Spacer(), gridBag.next().fillCellHorizontally().coverLine());
  }

  @Override
  protected void configureListeners() {
    super.configureListeners();
    infoButton.addActionListener(
        evt -> {
          try {
            Desktop.getDesktop()
                .browse(
                    new URL(
                            "https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns")
                        .toURI());
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
    timeFormatTextField
        .getDocument()
        .addDocumentListener(
            new DocumentListener() {
              @Override
              public void insertUpdate(DocumentEvent evt) {
                validatePattern();
              }

              @Override
              public void removeUpdate(DocumentEvent e) {
                validatePattern();
              }

              @Override
              public void changedUpdate(DocumentEvent e) {
                validatePattern();
              }
            });
  }

  private void validatePattern() {
    String pattern = timeFormatTextField.getText();
    try {
      DateTimeFormatter.ofPattern(pattern);
      formatCorrectnessLabel.setIcon(AllIcons.General.InspectionsOK);
      formatCorrectnessLabel.setToolTipText("Pattern is valid");
    } catch (IllegalArgumentException e) {
      formatCorrectnessLabel.setIcon(AllIcons.General.Error);
      formatCorrectnessLabel.setToolTipText("Pattern is invalid");
    }
  }

  private JScrollPane getDateInputTable() {
    ZoneId zoneId = ZoneId.systemDefault();
    timeHolder = new TimeHolder(ZonedDateTime.now(zoneId));

    Object[] columns = {"Day", "Month", "Year", "Hour", "Minute", "Second", "Nano", "Timezone"};
    Object[][] row = {
      {
        timeHolder.getZonedDateTime().getDayOfMonth(),
        timeHolder.getZonedDateTime().getMonthValue(),
        timeHolder.getZonedDateTime().getYear(),
        timeHolder.getZonedDateTime().getHour(),
        timeHolder.getZonedDateTime().getMinute(),
        timeHolder.getZonedDateTime().getSecond(),
        timeHolder.getZonedDateTime().getNano(),
        zoneId.getId()
      }
    };
    JTable table = new JBTable(new DefaultTableModel(row, columns));
    table.setRowSelectionAllowed(false);
    TableColumnModel tcm = table.getColumnModel();
    Iterator<TableColumn> iterator = tcm.getColumns().asIterator();
    Map<Object, TableCellEditor> cellEditorMap = getCellEditorMap();
    while (iterator.hasNext()) {
      TableColumn tableColumn = iterator.next();
      tableColumn.setCellEditor(cellEditorMap.get(tableColumn.getIdentifier()));
    }
    return ComponentUtils.attachScroll(table);
  }

  private Map<Object, TableCellEditor> getCellEditorMap() {
    ZoneId zoneId = ZoneId.systemDefault();
    Date date = new Date();
    return Map.of(
        "Day", new DateSpinnerEditor(date, "d", timeHolder),
        "Month", new DateSpinnerEditor(date, "M", timeHolder),
        "Year", new DateSpinnerEditor(date, "yyyy", timeHolder),
        "Hour", new DateSpinnerEditor(date, "H", timeHolder),
        "Minute", new DateSpinnerEditor(date, "m", timeHolder),
        "Second", new DateSpinnerEditor(date, "s", timeHolder),
        "Nano", new DateSpinnerEditor(date, "SSSSSSSSS", timeHolder),
        "Timezone", new TimezoneComboBoxEditor(zoneId, timeHolder));
  }

  @Override
  protected String generate() {
    try {
      DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern(timeFormatTextField.getText());

      Map<String, String> values = new LinkedHashMap<>();
      values.put("Formatted Time", timeHolder.getZonedDateTime().format(timeFormat));
      values.put("Millis", timeHolder.getZonedDateTime().toInstant().toEpochMilli() + "");
      System.out.println(formatResult(values));
      return formatResult(values);
    } catch (Exception e) {
      e.printStackTrace();
      return "ERROR";
    }
  }

  private String formatResult(Map<String, String> values) {
    return values.entrySet().stream()
        .reduce(
            new StringJoiner("\n"),
            (sj, entry) -> {
              sj.add(String.format("%-14s\t:\t%-2s", entry.getKey(), entry.getValue()));
              return sj;
            },
            (sj1, sj2) -> {
              StringJoiner sj = new StringJoiner("\n");
              sj.add(sj1.toString());
              sj.add(sj2.toString());
              return sj;
            })
        .toString();
  }

  @Setter
  @Getter
  @AllArgsConstructor
  public static class TimeHolder {
    private ZonedDateTime zonedDateTime;
  }
}
