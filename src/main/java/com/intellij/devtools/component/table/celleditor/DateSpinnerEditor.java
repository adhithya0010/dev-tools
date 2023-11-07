package com.intellij.devtools.component.table.celleditor;

import com.intellij.devtools.exec.misc.time.TimeFormatter;

import java.awt.Component;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import javax.swing.DefaultCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;

public class DateSpinnerEditor extends DefaultCellEditor {

  private final JSpinner spinner;
  private final TimeFormatter.TimeHolder timeHolder;

  private final Map<Integer, TemporalField> TEMPORAL_FIELD_MAP =
      Map.of(
          Calendar.DAY_OF_MONTH, ChronoField.DAY_OF_MONTH,
          Calendar.MONTH, ChronoField.MONTH_OF_YEAR,
          Calendar.YEAR, ChronoField.YEAR,
          Calendar.HOUR, ChronoField.HOUR_OF_DAY,
          Calendar.MINUTE, ChronoField.MINUTE_OF_HOUR,
          Calendar.SECOND, ChronoField.SECOND_OF_MINUTE,
          Calendar.MILLISECOND, ChronoField.MILLI_OF_SECOND);

  public DateSpinnerEditor(Date date, String dateFormat, TimeFormatter.TimeHolder timeHolder) {
    super(new JTextField());
    this.timeHolder = timeHolder;
    spinner = new JSpinner();
    spinner.setModel(new SpinnerDateModel());
    spinner.setEditor(new JSpinner.DateEditor(spinner, dateFormat));
  }

  public Component getTableCellEditorComponent(
      JTable table, Object value, boolean isSelected, int row, int column) {
    SpinnerDateModel model = (SpinnerDateModel) spinner.getModel();
    spinner.setValue(model.getValue());
    return spinner;
  }

  public Object getCellEditorValue() {
    SpinnerDateModel model = (SpinnerDateModel) spinner.getModel();

    Calendar calendar = Calendar.getInstance();
    calendar.setTime((Date) spinner.getValue());

    timeHolder
        .getZonedDateTime()
        .with(
            TEMPORAL_FIELD_MAP.get(model.getCalendarField()),
            calendar.get(model.getCalendarField()));
    return calendar.get(model.getCalendarField());
  }
}
