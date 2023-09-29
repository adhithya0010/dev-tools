package com.intellij.devtools.exec;

import static com.intellij.devtools.utils.GridConstraintUtils.buildGridConstraint;
import static com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL;
import static com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK;

import com.intellij.devtools.utils.ComponentUtils;
import com.intellij.uiDesigner.core.GridLayoutManager;
import java.awt.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.commons.collections4.CollectionUtils;

public interface Parameterized {

  default List<ParameterGroup> getParameterGroups() {
    return List.of();
  }

  default boolean configureParameters(JPanel parametersPanel) {
    AtomicBoolean isParametersAdded = new AtomicBoolean(false);
    List<ParameterGroup> parameterGroups = getParameterGroups();
    if (CollectionUtils.isEmpty(parameterGroups)) {
      parametersPanel.setVisible(false);
      return isParametersAdded.get();
    }
    parametersPanel.setLayout(new GridLayoutManager(parameterGroups.size(), 1));
    AtomicInteger i = new AtomicInteger(0);
    parameterGroups.forEach(
        parameterGroup -> {
          JPanel row =
              new JPanel(new GridLayoutManager(1, 2 * parameterGroup.getParameters().size()));
          AtomicInteger j = new AtomicInteger(0);
          parameterGroup
              .getParameters()
              .forEach(
                  parameter -> {
                    Optional<JComponent> componentHolder = ComponentUtils.toComponent(parameter);
                    if (componentHolder.isPresent()) {
                      JLabel label = new JLabel(parameter.getLabel());
                      JComponent component = componentHolder.get();
                      component.setName(parameter.getName());
                      ComponentUtils.setValue(component, parameter.getDefaultValue());
                      row.add(
                          label,
                          buildGridConstraint(
                              0, j.get(), 1, 1, FILL_HORIZONTAL, SIZEPOLICY_CAN_SHRINK));
                      row.add(
                          component,
                          buildGridConstraint(
                              0, j.get() + 1, 1, 1, FILL_HORIZONTAL, SIZEPOLICY_CAN_SHRINK));
                      j.getAndAdd(2);
                      isParametersAdded.set(true);
                    }
                  });
          parametersPanel.add(row, buildGridConstraint(i.getAndIncrement(), 0, FILL_HORIZONTAL));
        });
    parametersPanel.setVisible(true);
    return isParametersAdded.get();
  }

  default Map<String, Object> getParameterResult(JPanel parametersPanel) {
    Map<String, Object> result = new HashMap<>();
    for (int i = 0; i < parametersPanel.getComponentCount(); i++) {
      JPanel row = (JPanel) parametersPanel.getComponent(i);
      for (int j = 1; j < row.getComponentCount(); j += 2) {
        Component component = row.getComponent(j);
        result.put(component.getName(), ComponentUtils.getValue(component));
      }
    }
    return result;
  }
}
