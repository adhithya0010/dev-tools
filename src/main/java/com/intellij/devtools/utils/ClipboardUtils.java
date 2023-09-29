package com.intellij.devtools.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Optional;

public class ClipboardUtils {

  private ClipboardUtils() {}

  public static void copy(String data) {
    StringSelection stringSelection = new StringSelection(data);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection, null);
  }

  public static Optional<String> paste() {
    Optional<String> data = Optional.empty();
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    Transferable contents = clipboard.getContents(ClipboardUtils.class);
    if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
      try {
        String content = (String) contents.getTransferData(DataFlavor.stringFlavor);
        data = Optional.of(content);
      } catch (UnsupportedFlavorException | IOException e) {
        e.printStackTrace();
      }
    }
    return data;
  }
}
