package com.page5of4.jmeter.sampler;

import java.util.Map;

import javax.management.ObjectName;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.jolokia.client.request.J4pReadResponse;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Add to saveservice.properties: _com.page5of4.jmeter.sampler.JolokiaSampleResultConverter=collection
 * jolokiaSample=com.page5of4.jmeter.sampler.JolokiaSampleResult
 * 
 * @author jlewallen
 */
public class JolokiaSampleResultConverter extends AbstractCollectionConverter {

   private static final Logger log = LoggingManager.getLoggerForClass();

   private static final String RESPONSE_NODE_NAME = "response";

   public JolokiaSampleResultConverter(Mapper mapper) {
      super(mapper);
   }

   @Override
   public boolean canConvert(@SuppressWarnings("rawtypes") Class klass) {
      return JolokiaSampleResult.class.equals(klass);
   }

   @Override
   public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context) {
      String[] exclusions = new String[] { "Dynamic MBean Description" };
      JolokiaSampleResult sample = (JolokiaSampleResult)obj;
      setAttributes(writer, context, sample);
      for(J4pReadResponse response : sample.getResponses()) {
         ObjectName objectName = response.getRequest().getObjectName();
         writer.startNode(RESPONSE_NODE_NAME);
         for(Map.Entry<String, String> entry : objectName.getKeyPropertyList().entrySet()) {
            setAttribute(writer, entry.getKey(), entry.getValue());
         }
         for(String attributeName : response.getAttributes()) {
            if(!ArrayUtils.contains(exclusions, attributeName)) {
               writer.addAttribute(attributeName, response.getValue(attributeName).toString());
            }
         }
         writer.endNode();
      }
   }

   private void setAttribute(HierarchicalStreamWriter writer, String name, String value) {
      writer.addAttribute(name, value.replaceAll("\"", ""));
   }

   private void setAttributes(HierarchicalStreamWriter writer, MarshallingContext context, JolokiaSampleResult sample) {
      writer.addAttribute("ts", Long.toString(sample.getTimeStamp()));
   }

   @Override
   public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
      throw new NotImplementedException();
   }

}
