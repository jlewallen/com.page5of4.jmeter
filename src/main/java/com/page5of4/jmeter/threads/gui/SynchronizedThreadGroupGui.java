package com.page5of4.jmeter.threads.gui;

import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.threads.gui.AbstractThreadGroupGui;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import com.page5of4.jmeter.control.UntilNumberOfThreadsController;
import com.page5of4.jmeter.threads.SynchronizedThreadGroup;

public class SynchronizedThreadGroupGui extends AbstractThreadGroupGui {

   private static final Logger log = LoggingManager.getLoggerForClass();

   private static final long serialVersionUID = 1L;

   public SynchronizedThreadGroupGui() {
      super();
   }

   @Override
   public String getLabelResource() {
      return this.getClass().getSimpleName();
   }

   @Override
   public String getStaticLabel() {
      return "Synchronized Thread Group";
   }

   @Override
   public TestElement createTestElement() {
      SynchronizedThreadGroup group = new SynchronizedThreadGroup();
      modifyTestElement(group);
      return group;
   }

   @Override
   public void modifyTestElement(TestElement e) {
      SynchronizedThreadGroup group = (SynchronizedThreadGroup)e;
      group.setNumThreads(1);
      group.setName("Synchronized Thread Group");
      UntilNumberOfThreadsController controller = new UntilNumberOfThreadsController();
      controller.setContinueForever(true);
      controller.setLoops(-1);
      group.setSamplerController(controller);
      super.configureTestElement(e);
   }

   @Override
   public void configure(TestElement e) {
      super.configure(e);
   }

}
