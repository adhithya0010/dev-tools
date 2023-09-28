package com.intellij.devtools.exec.generator.impl;

import static com.intellij.devtools.MessageKeys.GENERATOR_TIMESTAMP_NAME;

import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.exec.OperationGroup;
import com.intellij.devtools.exec.Parameter;
import com.intellij.devtools.exec.Parameter.Type;
import com.intellij.devtools.exec.ParameterGroup;
import com.intellij.devtools.exec.generator.Generator;
import com.intellij.devtools.locale.MessageBundle;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import javax.swing.Icon;

public class TimeStampGenerator extends Generator {

  private static final String DEFAULT_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ";

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
  protected String generate() {
    Map<String, Object> parameterResult = getParameterResult();
    ZoneId zoneId = ZoneId.of((String) parameterResult.get("timezone"));
    ZoneId defaultZoneId = ZoneId.systemDefault();
    LocalDateTime dateTime = LocalDateTime.of((Integer) parameterResult.get("year"),
        (Integer) parameterResult.get("month"), (Integer) parameterResult.get("day"),
        (Integer) parameterResult.get("hour"), (Integer) parameterResult.get("minute"),
        (Integer) parameterResult.get("second"), (Integer) parameterResult.get("nano"));
    ZonedDateTime zonedDateTime = dateTime.atZone(zoneId);
    DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern(
        (String) parameterResult.get("timeFormat"));
    String formattedTime = zonedDateTime.format(timeFormat);
    String millis = String.valueOf(zonedDateTime.toInstant().toEpochMilli());

    ZonedDateTime localZonedTime = zonedDateTime.withZoneSameInstant(defaultZoneId);
    String localTime = localZonedTime.format(timeFormat);

    Map<String, String> values = new LinkedHashMap<>();
    values.put("Input Timezone", zoneId.getId());
    values.put("Formatted Time", formattedTime);
    values.put("Millis", millis);
    values.put("Local Timezone", defaultZoneId.getId());
    values.put("Local Time", localTime);
    return formatResult(values);
  }

  @Override
  public List<ParameterGroup> getParameterGroups() {
    ZoneId zoneId = ZoneId.systemDefault();

    Parameter timeFormat = Parameter.builder()
        .name("timeFormat")
        .label("Time Format")
        .type(Type.TEXT)
        .defaultValue(DEFAULT_TIME_FORMAT)
        .build();
    Parameter timeZone = Parameter.builder()
        .name("timezone")
        .label("Timezone")
        .type(Type.TEXT)
        .defaultValue(zoneId.toString())
        .build();

    LocalDateTime localDateTime = LocalDateTime.now(zoneId);

    Parameter day = Parameter.builder()
        .name("day")
        .label("Day")
        .type(Type.NUMBER)
        .defaultValue(String.valueOf(localDateTime.getDayOfMonth()))
        .minValue(1)
        .maxValue(31)
        .build();
    Parameter month = Parameter.builder()
        .name("month")
        .label("Month")
        .type(Type.NUMBER)
        .defaultValue(String.valueOf(localDateTime.getMonthValue()))
        .minValue(1)
        .maxValue(12)
        .build();
    Parameter year = Parameter.builder()
        .name("year")
        .label("Year")
        .type(Type.NUMBER)
        .defaultValue(String.valueOf(localDateTime.getYear()))
        .minValue(1970)
        .maxValue(4000)
        .build();
    Parameter hour = Parameter.builder()
        .name("hour")
        .label("Hour")
        .type(Type.NUMBER)
        .defaultValue(String.valueOf(localDateTime.getHour()))
        .minValue(0)
        .maxValue(23)
        .build();
    Parameter minute = Parameter.builder()
        .name("minute")
        .label("Minute")
        .type(Type.NUMBER)
        .defaultValue(String.valueOf(localDateTime.getMinute()))
        .minValue(0)
        .maxValue(59)
        .build();
    Parameter second = Parameter.builder()
        .name("second")
        .label("Second")
        .type(Type.NUMBER)
        .defaultValue(String.valueOf(localDateTime.getSecond()))
        .minValue(0)
        .maxValue(59)
        .build();

    Parameter nano = Parameter.builder()
        .name("nano")
        .label("Nano")
        .type(Type.NUMBER)
        .defaultValue(String.valueOf(localDateTime.get(ChronoField.NANO_OF_SECOND)))
        .minValue(0)
        .maxValue(Long.MAX_VALUE)
        .build();

    List<ParameterGroup> parameterGroups = new ArrayList<>();
    parameterGroups.add(ParameterGroup.builder().parameters(List.of(timeFormat)).build());
    parameterGroups.add(ParameterGroup.builder().parameters(List.of(timeZone)).build());
    parameterGroups.add(ParameterGroup.builder().parameters(List.of(day, month, year)).build());
    parameterGroups.add(ParameterGroup.builder().parameters(List.of(hour, minute, second, nano)).build());
    return parameterGroups;
  }

  private String formatResult(Map<String, String> values) {
    return values.entrySet().stream().reduce(new StringJoiner("\n"), (sj, entry) -> {
      sj.add(String.format("%-20s\t:\t%-2s", entry.getKey(), entry.getValue()));
      return sj;
    }, (sj1, sj2) -> {
      StringJoiner sj = new StringJoiner("\n");
      sj.add(sj1.toString());
      sj.add(sj2.toString());
      return sj;
    }).toString();
  }
}
