package com.page5of4.jmeter.sampler.gui;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.apache.jmeter.gui.util.PowerTableModel;

/**
 * @link http://code.google.com/p/jmeter-plugins/
 * @author undera
 */
public class ButtonPanelAddCopyRemove extends JPanel {
   private static final long serialVersionUID = 1L;
   private final JButton addRowButton;
   private final JButton copyRowButton;
   private final JButton deleteRowButton;

   public ButtonPanelAddCopyRemove(JTable grid, PowerTableModel tableModel, Object[] defaultValues) {
      setLayout(new GridLayout(1, 2));

      addRowButton = new JButton("Add Row");
      copyRowButton = new JButton("Copy Row");
      deleteRowButton = new JButton("Delete Row");

      addRowButton.addActionListener(new AddRowAction(this, grid, tableModel, deleteRowButton, defaultValues));
      copyRowButton.addActionListener(new CopyRowAction(this, grid, tableModel, deleteRowButton));
      deleteRowButton.addActionListener(new DeleteRowAction(this, grid, tableModel, deleteRowButton));

      add(addRowButton);
      add(copyRowButton);
      add(deleteRowButton);
   }
}
