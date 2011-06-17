package com.page5of4.jmeter.sampler;

import java.util.List;

import org.apache.jmeter.samplers.SampleResult;
import org.jolokia.client.request.J4pReadResponse;

public class JolokiaSampleResult extends SampleResult {

   private static final long serialVersionUID = 1L;

   private List<J4pReadResponse> responses;

   public List<J4pReadResponse> getResponses() {
      return responses;
   }

   public void setResponses(List<J4pReadResponse> responses) {
      this.responses = responses;
   }

   public JolokiaSampleResult() {

   }

   public JolokiaSampleResult(List<J4pReadResponse> responses) {
      super();
      this.responses = responses;
   }

}
