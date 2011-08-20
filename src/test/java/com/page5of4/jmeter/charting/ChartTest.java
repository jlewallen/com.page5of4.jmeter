package com.page5of4.jmeter.charting;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.ResultCollectorHelper;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.Visualizer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.MovingAverage;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
import org.junit.Test;

import com.page5of4.jmeter.sampler.JolokiaSampleResult;

public class ChartTest {

   public static interface SampleFilter {
      public boolean include(HTTPSampleResult hsr);

   }

   public static class SampleFilters {
      public static List<HTTPSampleResult> filter(List<HTTPSampleResult> samples, SampleFilter filter) {
         List<HTTPSampleResult> filtered = new ArrayList<HTTPSampleResult>();
         for(HTTPSampleResult hsr : samples) {
            if(filter.include(hsr)) {
               filtered.add(hsr);
            }
         }
         return filtered;
      }

      public static List<HTTPSampleResult> filter(List<HTTPSampleResult> samples, String label) {
         return filter(samples, new LabelSampleFilter(label));
      }
   }

   public static class LabelSampleFilter implements SampleFilter {
      private String label;

      public LabelSampleFilter(String label) {
         super();
         this.label = label;
      }

      @Override
      public boolean include(HTTPSampleResult hsr) {
         return hsr.getSampleLabel().equals(this.label);
      }
   }

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

      public double getAverage() {
         double value = 0.0;
         for(Number n : values) {
            value += n.doubleValue();
         }
         return value / (double)values.size();
      }

      public TimeSeriesDataItem getDataItem(RegularTimePeriod key) {
         return new TimeSeriesDataItem(key, getAverage());
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

   public static class DataFactory {
      public static Entries createTimes(List<HTTPSampleResult> samples) {
         Entries times = new Entries();
         for(HTTPSampleResult hsr : samples) {
            Millisecond ms = new Millisecond(new Date(hsr.getTimeStamp()));
            times.add(ms, (double)hsr.getTime());
         }
         return times;
      }

      public static TimeSeriesCollection createThreadsData(List<HTTPSampleResult> samples) {
         Entries threads = new Entries();
         for(HTTPSampleResult hsr : samples) {
            Millisecond ms = new Millisecond(new Date(hsr.getTimeStamp()));
            threads.add(ms, (double)hsr.getAllThreads());
         }
         TimeSeries threadsSeries = new TimeSeries("Threads");
         for(Map.Entry<RegularTimePeriod, Entry> entry : threads.entrySet()) {
            threadsSeries.add(entry.getValue().getMaximumDataItem(entry.getKey()));
         }
         TimeSeriesCollection data = new TimeSeriesCollection();
         data.addSeries(threadsSeries);
         return data;

      }

      public static TimeSeriesCollection createResponseTimeData(List<HTTPSampleResult> samples) {
         Entries times = new Entries();
         for(HTTPSampleResult hsr : samples) {
            Millisecond ms = new Millisecond(new Date(hsr.getTimeStamp()));
            times.add(ms, (double)hsr.getTime());
         }

         TimeSeries minimumSeries = new TimeSeries("Maximum");
         for(Map.Entry<RegularTimePeriod, Entry> entry : times.entrySet()) {
            minimumSeries.add(entry.getValue().getMaximumDataItem(entry.getKey()));
         }
         TimeSeriesCollection data = new TimeSeriesCollection();
         Integer averagingPeriod = 30 * 1000;
         data.addSeries(MovingAverage.createMovingAverage(minimumSeries, "Response Time", averagingPeriod, 0));
         return data;
      }
   }

   public static class SimpleHTTPCollector implements Visualizer {
      private List<HTTPSampleResult> samples = new ArrayList<HTTPSampleResult>();

      public List<HTTPSampleResult> getSamples() {
         return samples;
      }

      @Override
      public void add(SampleResult sr) {
         samples.add((HTTPSampleResult)sr);
      }

      @Override
      public boolean isStats() {
         return false;
      }
   }

   public static class JolokiaResultCollector implements Visualizer {
      private List<JolokiaSampleResult> samples = new ArrayList<JolokiaSampleResult>();

      public List<JolokiaSampleResult> getSamples() {
         return samples;
      }

      @Override
      public void add(SampleResult sr) {
         samples.add((JolokiaSampleResult)sr);
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
         SimpleHTTPCollector v = new SimpleHTTPCollector();
         ResultCollector rc = new ResultCollector();
         ResultCollectorHelper rch = new ResultCollectorHelper(rc, v);
         XStreamJTLParser p = new XStreamJTLParser(new File("/Users/jlewallen/Dropbox/profile-users-gr.jtl"), rch);
         p.parse();

         Map<String, List<HTTPSampleResult>> labels = new HashMap<String, List<HTTPSampleResult>>();
         for(HTTPSampleResult hsr : v.getSamples()) {
            if(!labels.containsKey(hsr.getSampleLabel())) {
               labels.put(hsr.getSampleLabel(), new ArrayList<HTTPSampleResult>());
            }
            labels.get(hsr.getSampleLabel()).add(hsr);
         }

         short index = 0;
         for(Map.Entry<String, List<HTTPSampleResult>> entry : labels.entrySet()) {
            String title = entry.getKey();
            {
               System.out.println("Saving...");
               TimeSeriesCollection data = DataFactory.createResponseTimeData(entry.getValue());
               JFreeChart chart = ChartFactory.createTimeSeriesChart(title, "Time", "Time (ms)", data, true, true, true);

               final NumberAxis axis2 = new NumberAxis("Secondary");
               XYPlot plot = chart.getXYPlot();
               axis2.setAutoRangeIncludesZero(false);
               plot.setRangeAxis(1, axis2);
               plot.setDataset(1, DataFactory.createThreadsData(entry.getValue()));
               plot.mapDatasetToRangeAxis(1, 1);

               XYItemRenderer renderer = plot.getRenderer();
               renderer.setSeriesStroke(0, new BasicStroke(1.25f));
               renderer.setSeriesPaint(0, Color.blue);

               final StandardXYItemRenderer renderer2 = new StandardXYItemRenderer();
               renderer2.setSeriesPaint(0, Color.yellow);
               plot.setRenderer(1, renderer2);

               renderer.setSeriesStroke(1, new BasicStroke(1.25f));
               renderer.setSeriesPaint(1, Color.yellow);

               final Marker three = new ValueMarker(3000);
               three.setPaint(Color.red);
               three.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
               three.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
               three.setStroke(new BasicStroke(1.5f));
               plot.addRangeMarker(three);

               /*
               double value = StatUtils.percentile(DataFactory.createTimes(entry.getValue()).getAllAsDoubles(), 95);
               System.out.println(value);
               final Marker percentile = new ValueMarker(value);
               percentile.setPaint(Color.red);
               percentile.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
               percentile.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
               percentile.setStroke(new BasicStroke(1.5f));
               plot.addRangeMarker(percentile);
               */

               chart.setAntiAlias(true);
               saveChart(chart, "chart" + index + "-1.png");
               System.out.println("Done...");
            }

            {
               XYSeriesCollection data = new XYSeriesCollection();
               XYSeries series = new XYSeries("Response Time");
               Map<Integer, Entry> map = new HashMap<Integer, Entry>();
               for(int i = 0; i <= 101; ++i) {
                  map.put(i, new Entry());
               }
               for(HTTPSampleResult hsr : entry.getValue()) {
                  map.get(hsr.getAllThreads()).add(hsr.getTime());
               }
               for(int i = 0; i < 100; ++i) {
                  series.add(i, map.get(i).getAverage());
               }
               data.addSeries(series);

               JFreeChart chart = ChartFactory.createXYLineChart(title, "Threads", "Response Time", data, PlotOrientation.VERTICAL, true, true, true);
               XYItemRenderer renderer = chart.getXYPlot().getRenderer();
               renderer.setSeriesStroke(0, new BasicStroke(1.25f));
               renderer.setSeriesPaint(0, Color.blue);
               chart.setAntiAlias(true);
               saveChart(chart, "chart" + index + "-2.png");
            }
            index++;
         }

         writeIndex();
      }

      {
         JolokiaResultCollector v = new JolokiaResultCollector();
         ResultCollector rc = new ResultCollector();
         ResultCollectorHelper rch = new ResultCollectorHelper(rc, v);
         XStreamJTLParser p = new XStreamJTLParser(new File("/Users/jlewallen/Dropbox/profile-users-jolokia.jtl"), rch);
         p.parse();
      }

   }

   public void writeIndex() throws IOException {
      FileWriter writer = new FileWriter("index.html", false);
      File[] files = new File(".").listFiles(new FilenameFilter() {
         @Override
         public boolean accept(File dir, String name) {
            return name.endsWith(".png");
         }
      });
      for(File file : files) {
         writer.append("<div>");
         writer.append("<img src='");
         writer.append(file.getName());
         writer.append("'>");
         writer.append("</div>");
      }
      writer.close();

   }

   public void saveChart(JFreeChart chart, String fileName) {
      try {
         ChartUtilities.saveChartAsPNG(new File(fileName), chart, 800, 600);
      }
      catch(IOException e) {
         e.printStackTrace();
         System.err.println("Problem occurred creating chart.");
      }
   }
}
