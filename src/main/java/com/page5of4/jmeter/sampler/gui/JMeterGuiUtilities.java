package com.page5of4.jmeter.sampler.gui;

import java.util.ArrayList;
import java.util.List;

import org.apache.jmeter.gui.util.PowerTableModel;
import org.apache.jmeter.testelement.property.CollectionProperty;

public abstract class JMeterGuiUtilities {

   public static CollectionProperty tableModelRowsToCollectionProperty(PowerTableModel model, String propname) {
      CollectionProperty rows = new CollectionProperty(propname, new ArrayList<Object>());
      for(int row = 0; row < model.getRowCount(); row++) {
         List<Object> item = getArrayListForArray(model.getRowData(row));
         rows.addItem(item);
      }
      return rows;
   }

   private static List<Object> getArrayListForArray(Object[] rowData) {
      ArrayList<Object> res = new ArrayList<Object>();
      for(int n = 0; n < rowData.length; n++) {
         res.add(rowData[n]);
      }
      return res;
   }

   public static void collectionPropertyToTableModelRows(CollectionProperty prop, PowerTableModel model) {
      model.clearData();
      for(int rowN = 0; rowN < prop.size(); rowN++) {
         @SuppressWarnings("unchecked")
         ArrayList<String> rowObject = (ArrayList<String>)prop.get(rowN).getObjectValue();
         model.addRow(rowObject.toArray());
      }
      model.fireTableDataChanged();
   }
}
