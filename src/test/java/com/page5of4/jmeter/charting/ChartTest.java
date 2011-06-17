package com.page5of4.jmeter.charting;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.stat.StatUtils;
import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.ResultCollectorHelper;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.Visualizer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.MovingAverage;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
import org.junit.Test;

public class ChartTest {

   public static class Entries {
      private Map<RegularTimePeriod, Entry> map = new HashMap<RegularTimePeriod, Entry>();
      private List<Double> values = new ArrayList<Double>();

      public void add(RegularTimePeriod p, Double value) {
         if(!map.containsKey(p)) {
            map.put(p, new Entry());
         }
         map.get(p).add(value);
         values.add(value);
      }

      public double[] getAllAsDoubles() {
         return ArrayUtils.toPrimitive(values.toArray(new Double[values.size()]));
      }

      public Set<Map.Entry<RegularTimePeriod, Entry>> entrySet() {
         return map.entrySet();
      }
   }

   public static class Entry {
      private List<Number> values = new ArrayList<Number>();

      public void add(Number value) {
         values.add(value);
      }

      public List<Number> getValues() {
         return values;
      }

      public TimeSeriesDataItem getDataItem(RegularTimePeriod key) {
         double value = 0.0;
         for(Number n : values) {
            value += n.doubleValue();
         }
         return new TimeSeriesDataItem(key, value / (double)values.size());
      }

      public TimeSeriesDataItem getMaximumDataItem(RegularTimePeriod key) {
         double value = 0.0;
         for(Number n : values) {
            value = Math.max(value, n.doubleValue());
         }
         return new TimeSeriesDataItem(key, value);
      }

      public TimeSeriesDataItem getMinimumDataItem(RegularTimePeriod key) {
         double value = Double.MAX_VALUE;
         for(Number n : values) {
            value = Math.min(value, n.doubleValue());
         }
         return new TimeSeriesDataItem(key, value);
      }
   }

   public static class SimpleVisualizer implements Visualizer {

      private Entries times = new Entries();
      private Entries threads = new Entries();

      public Entries getTimes() {
         return times;
      }

      public Entries getThreads() {
         return threads;
      }

      public TimeSeriesCollection getData() {
         TimeSeriesCollection data = new TimeSeriesCollection();
         Integer averagingPeriod = 30 * 1000;

         {
            TimeSeries minimumSeries = new TimeSeries("Min");
            for(Map.Entry<RegularTimePeriod, Entry> entry : times.entrySet()) {
               minimumSeries.add(entry.getValue().getMaximumDataItem(entry.getKey()));
            }
            data.addSeries(MovingAverage.createMovingAverage(minimumSeries, "", averagingPeriod, 0));
         }
         /*
         {
            TimeSeries threadsSeries = new TimeSeries("Threads");
            for(Map.Entry<RegularTimePeriod, Entry> entry : threads.entrySet()) {
               threadsSeries.add(entry.getValue().getMaximumDataItem(entry.getKey()));
            }
            data.addSeries(threadsSeries);
         }*/
         return data;
      }

      @Override
      public void add(SampleResult sr) {
         HTTPSampleResult hsr = (HTTPSampleResult)sr;
         Millisecond ms = new Millisecond(new Date(hsr.getTimeStamp()));
         times.add(ms, (double)hsr.getTime());
         threads.add(ms, (double)hsr.getAllThreads());
      }

      @Override
      public boolean isStats() {
         return false;
      }
   }

   @Test
   public void when_generating_a_chart() throws Exception {
      JMeterUtils.getProperties("jakarta-jmeter-2.4/bin/jmeter.properties");
      JMeterUtils.setJMeterHome("jakarta-jmeter-2.4");
      {
         SimpleVisualizer v = new SimpleVisualizer();
         ResultCollector rc = new ResultCollector();
         ResultCollectorHelper rch = new ResultCollectorHelper(rc, v);
         XStreamJTLParser p = new XStreamJTLParser(new File("/Users/jlewallen/Dropbox/profile-users-gr.jtl"), rch);
         p.parse();

         System.out.println("Saving...");
         TimeSeriesCollection data = v.getData();
         JFreeChart chart = ChartFactory.createTimeSeriesChart("Response Time", "Time", "Time (ms)", data, true, true, true);
         XYItemRenderer renderer = chart.getXYPlot().getRenderer();
         renderer.setSeriesStroke(0, new BasicStroke(1.25f));
         renderer.setSeriesPaint(0, Color.blue);

         final Marker three = new ValueMarker(3000);
         three.setPaint(Color.red);
         three.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
         three.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
         three.setStroke(new BasicStroke(1.5f));
         chart.getXYPlot().addRangeMarker(three);

         double value = StatUtils.percentile(v.getTimes().getAllAsDoubles(), 95);
         System.out.println(value);
         final Marker percentile = new ValueMarker(value);
         percentile.setPaint(Color.red);
         percentile.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
         percentile.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
         percentile.setStroke(new BasicStroke(1.5f));
         chart.getXYPlot().addRangeMarker(percentile);

         chart.setAntiAlias(true);
         saveChart(chart);
         System.out.println("Done...");
      }

      /*
      {
         SimpleVisualizer v = new SimpleVisualizer();
         ResultCollector rc = new ResultCollector();
         ResultCollectorHelper rch = new ResultCollectorHelper(rc, v);
         XStreamJTLParser p = new XStreamJTLParser(new File("/Users/jlewallen/Dropbox/profile-users-jolokia.jtl"), rch);
         p.parse();
      }
      */

   }

   public void saveChart(JFreeChart chart) {
      String fileName = "chart.png";
      try {
         ChartUtilities.saveChartAsPNG(new File(fileName), chart, 800, 600);
      }
      catch(IOException e) {
         e.printStackTrace();
         System.err.println("Problem occurred creating chart.");
      }
   }
}
