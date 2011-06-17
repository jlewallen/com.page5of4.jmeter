package com.page5of4.jmeter.control.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import org.apache.jmeter.control.gui.AbstractControllerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import com.page5of4.jmeter.control.UntilNumberOfThreadsController;

public class UntilNumberOfThreadsControllerGui extends AbstractControllerGui implements ActionListener {

   private static final long serialVersionUID = 1L;

   private static final Logger log = LoggingManager.getLoggerForClass();

   public UntilNumberOfThreadsControllerGui() {
      super();
      initialize();
   }

   @Override
   public String getLabelResource() {
      return this.getClass().getSimpleName();
   }

   @Override
   public String getStaticLabel() {
      return "While Running Controller";
   }

   @Override
   public TestElement createTestElement() {
      UntilNumberOfThreadsController controller = new UntilNumberOfThreadsController();
      modifyTestElement(controller);
      return controller;
   }

   @Override
   public void modifyTestElement(TestElement e) {
      UntilNumberOfThreadsController controller = (UntilNumberOfThreadsController)e;
      configureTestElement(controller);
   }

   private void initialize() {
      setLayout(new BorderLayout(0, 5));
      setBorder(makeBorder());
      add(makeTitlePanel(), BorderLayout.NORTH);

      JPanel mainPanel = new JPanel(new BorderLayout());
      // mainPanel.add(createConditionPanel(), BorderLayout.NORTH);
      add(mainPanel, BorderLayout.CENTER);
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      // TODO Auto-generated method stub
   }

}
