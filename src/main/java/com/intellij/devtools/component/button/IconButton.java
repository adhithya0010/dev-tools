package com.intellij.devtools.component.button;

import com.intellij.util.ui.JBUI;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import javax.swing.Icon;
import javax.swing.JButton;

public class IconButton extends JButton {

  // Hit detection.
  private transient Shape shape;

  public IconButton(Icon icon) {
    super(icon);

    // This call causes the JButton not to paint
    // the background.
    // This allows us to paint a round background.
    setContentAreaFilled(false);

    setMargin(JBUI.emptyInsets());
    setSize(new Dimension(24, 24));
    setPreferredSize(new Dimension(24, 24));
    setMaximumSize(new Dimension(24, 24));
    setMinimumSize(new Dimension(24, 24));

    // These statements enlarge the button so that it
    // becomes a circle rather than an oval.
    Dimension size = getPreferredSize();
    size.width = size.height = Math.max(size.width, size.height);
    setPreferredSize(size);
  }

  // Paint the round background and label.
  @Override
  protected void paintComponent(Graphics g) {
    if (getModel().isArmed()) {
      // You might want to make the highlight color
      // a property of the RoundButton class.
      g.setColor(JBUI.CurrentTheme.ActionButton.hoverBorder());
    } else {
      g.setColor(getBackground());
    }
    g.fillRect(0, 0, getSize().width - 1, getSize().height - 1);

    //// This call will paint the label and the
    //        // focus rectangle.
    super.paintComponent(g);
  }

  // Paint the border of the button using a simple stroke.
  @Override
  protected void paintBorder(Graphics g) {
    g.setColor(getForeground());
  }

  @Override
  public boolean contains(int x, int y) {
    // If the button has changed size,
    // make a new shape object.
    if (shape == null || !shape.getBounds().equals(getBounds())) {
      shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
    }
    return shape.contains(x, y);
  }
}
