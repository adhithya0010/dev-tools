package com.intellij.devtools.component.table.celleditor;

import com.intellij.devtools.exec.misc.time.TimeFormatter;
import com.intellij.openapi.ui.ComboBox;
import java.awt.Component;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

public class TimezoneComboBoxEditor extends DefaultCellEditor {

  private final JComboBox<Object> comboBox;
  private final TimeFormatter.TimeHolder timeHolder;

  public TimezoneComboBoxEditor(TimeFormatter.TimeHolder timeHolder) {
    super(new JTextField());
    this.timeHolder = timeHolder;
    comboBox = new ComboBox<>(ZoneId.getAvailableZoneIds().stream().sorted().toArray());
    AutoCompleteDecorator.decorate(comboBox);
  }

  @Override
  public Component getTableCellEditorComponent(
      JTable table, Object value, boolean isSelected, int row, int column) {
    comboBox.setSelectedItem(value);
    return comboBox;
  }

  @Override
  public Object getCellEditorValue() {
    if (Objects.nonNull(comboBox.getSelectedItem())) {
      ZonedDateTime zonedDateTime = timeHolder.getZonedDateTime();
      ZoneId zoneId = ZoneId.of((String) comboBox.getSelectedItem());
      timeHolder.setZonedDateTime(ZonedDateTime.of(zonedDateTime.toLocalDateTime(), zoneId));
    }
    return comboBox.getSelectedItem();
  }
}
