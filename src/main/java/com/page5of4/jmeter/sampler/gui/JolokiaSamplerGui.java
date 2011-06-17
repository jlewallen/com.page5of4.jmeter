package com.page5of4.jmeter.sampler.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.jmeter.gui.util.PowerTableModel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.testelement.property.NullProperty;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import com.page5of4.jmeter.sampler.JolokiaQuery;
import com.page5of4.jmeter.sampler.JolokiaSampler;

public class JolokiaSamplerGui extends AbstractSamplerGui implements TableModelListener, CellEditorListener {

   private static final long serialVersionUID = 1L;

   private static final Logger log = LoggingManager.getLoggerForClass();

   private JTextField urlField;
   private PowerTableModel tableModel;
   private JTable grid;

   public JolokiaSamplerGui() {
      initialize();
   }

   @Override
   public String getStaticLabel() {
      return "Jolokia Diagnostic Sampler";
   }

   public String getLabelResource() {
      return this.getClass().getSimpleName();
   }

   private void createTableModel() {
      tableModel = new PowerTableModel(new String[] { "Domain", "Filter", "Query" }, new Class[] { String.class, String.class, String.class });
      tableModel.addTableModelListener(this);
      grid.setModel(tableModel);
   }

   private JPanel createParamsPanel() {
      JPanel panel = new JPanel(new BorderLayout(5, 5));
      panel.setBorder(BorderFactory.createTitledBorder("Queries"));
      panel.setPreferredSize(new Dimension(200, 200));

      JScrollPane scroll = new JScrollPane(createGrid());
      scroll.setPreferredSize(scroll.getMinimumSize());
      panel.add(scroll, BorderLayout.CENTER);
      panel.add(new ButtonPanelAddCopyRemove(grid, tableModel, new String[] { "", "", "" }), BorderLayout.SOUTH);

      return panel;
   }

   private JTable createGrid() {
      grid = new JTable();
      grid.getDefaultEditor(String.class).addCellEditorListener(this);
      createTableModel();
      grid.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      grid.setMinimumSize(new Dimension(200, 100));
      return grid;
   }

   public TestElement createTestElement() {
      JolokiaSampler sampler = new JolokiaSampler();
      modifyTestElement(sampler);
      return sampler;
   }

   public void modifyTestElement(TestElement s) {
      super.configureTestElement(s);
      if(s instanceof JolokiaSampler) {
         JolokiaSampler sampler = (JolokiaSampler)s;
         sampler.setUrl(urlField.getText());
         sampler.setQueries(JMeterGuiUtilities.tableModelRowsToCollectionProperty(tableModel, JolokiaSampler.QUERIES));
      }
   }

   @Override
   public void configure(TestElement sampler) {
      super.configure(sampler);
      urlField.setText(sampler.getPropertyAsString(JolokiaSampler.URL));
      JMeterProperty queryValues = sampler.getProperty(JolokiaSampler.QUERIES);
      if(!(queryValues instanceof NullProperty)) {
         CollectionProperty columns = (CollectionProperty)queryValues;
         tableModel.removeTableModelListener(this);
         JMeterGuiUtilities.collectionPropertyToTableModelRows(columns, tableModel);
         tableModel.addTableModelListener(this);
      }
   }

   @Override
   public void clearGui() {
      super.clearGui();
      log.info("clearGui: " + tableModel);
      urlField.setText("http://127.0.0.1:8080/jolokia/");
      CollectionProperty columns =
            JolokiaQuery.build(JolokiaQuery.build("org.infinispan", "component=Statistics,*"), JolokiaQuery.build("org.infinispan.application", "component=Statistics,*"),
                  JolokiaQuery.build("java.lang", "type=OperatingSystem"));
      JMeterGuiUtilities.collectionPropertyToTableModelRows(columns, tableModel);
   }

   private void initialize() {
      setLayout(new BorderLayout(0, 5));
      setBorder(makeBorder());

      add(makeTitlePanel(), BorderLayout.NORTH);

      JPanel mainPanel = new JPanel(new GridBagLayout());

      GridBagConstraints labelConstraints = new GridBagConstraints();
      labelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;

      GridBagConstraints editConstraints = new GridBagConstraints();
      editConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
      editConstraints.weightx = 1.0;
      editConstraints.fill = GridBagConstraints.HORIZONTAL;

      addToPanel(mainPanel, labelConstraints, 0, 1, new JLabel("URL: ", JLabel.RIGHT));
      addToPanel(mainPanel, editConstraints, 1, 1, urlField = new JTextField());

      JPanel container = new JPanel(new BorderLayout());
      container.add(mainPanel, BorderLayout.NORTH);
      container.add(createParamsPanel(), BorderLayout.CENTER);
      add(container, BorderLayout.CENTER);
   }

   private void addToPanel(JPanel panel, GridBagConstraints constraints, int col, int row, JComponent component) {
      constraints.gridx = col;
      constraints.gridy = row;
      panel.add(component, constraints);
   }

   @Override
   public void editingStopped(ChangeEvent e) {
      // TODO Auto-generated method stub
   }

   @Override
   public void editingCanceled(ChangeEvent e) {
      // TODO Auto-generated method stub
   }

   @Override
   public void tableChanged(TableModelEvent e) {
      // TODO Auto-generated method stub
   }
}
