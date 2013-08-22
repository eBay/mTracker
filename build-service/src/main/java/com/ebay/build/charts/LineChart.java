package com.ebay.build.charts;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.TextAnchor;

public class LineChart extends ApplicationFrame {
	private static final long serialVersionUID = 1L;

	public LineChart(final String title, double[] systemReliability, double[] overallRelibity, String[] columnKeys, String imageName) {

		super(title);
		final CategoryDataset dataset = createDataset(systemReliability, overallRelibity, columnKeys);
        createChart(dataset, imageName, overallRelibity);
	}

	

    private CategoryDataset createDataset(double[] systemReliability, double[] overallRelibity, String[] columnKeys) {
        
        // create the data set...
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset(); 
        for(int i = 0, j = 0; i < systemReliability.length && j < columnKeys.length; i++, j++) {
        	dataset.addValue(systemReliability[i], "System Reliability", columnKeys[j]);
        }

        for(int i = 0, j = 0; i < overallRelibity.length && j < columnKeys.length; i++, j++) {
        	dataset.addValue(overallRelibity[i], "Overall Reliability", columnKeys[j]);   
        }
        
        return dataset;
        
    }

	private JFreeChart createChart(final CategoryDataset dataset, String imageName, double[] overallRelibity) {

		// create the chart...
		final JFreeChart chart = ChartFactory.createLineChart("", // chart
																	// title
				"", // x axis label
				"Reliability(%)", // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
				);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		chart.setBackgroundPaint(Color.white);

		// final StandardLegend legend = (StandardLegend) chart.getLegend();
		// legend.setDisplaySeriesShapes(true);

		// get a reference to the plot for further customisation...
		final CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.WHITE);
		// plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
		plot.setDomainGridlinePaint(Color.WHITE);
		plot.setRangeGridlinePaint(Color.GRAY);
		
		CategoryAxis axis = (CategoryAxis)plot.getDomainAxis();
		axis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		
		final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
		renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.INSIDE12, TextAnchor.TOP_LEFT)); 
		renderer.setBaseShapesFilled(true);
		renderer.setBaseShapesVisible(true);
		renderer.setBaseItemLabelsVisible(true);
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());  
		plot.setRenderer(renderer);

		// change the auto tick unit selection to integer units only...
		final NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
		numberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		numberAxis.setAutoTickUnitSelection(false);
		numberAxis.setTickUnit(new NumberTickUnit(1));
		Arrays.sort(overallRelibity);
		numberAxis.setLowerBound(Math.floor(overallRelibity[0]) - 5);
		numberAxis.setUpperBound(100);	
		
		OutputStream os = null;
		try {
			String path = "./images";
			File dirFile = new File(path);
			if(!dirFile.exists()){
				if(!dirFile.mkdir()){
					throw new Exception("Directory does not exist, fail to create !");
				}
			}
			
			File file = new File(path + "/" + imageName);
			if(!file.exists()){
				if(!file.createNewFile()){
					throw new Exception("File does not exist, fail to create !");
				}
			}
			os = new FileOutputStream(file);		
			ChartUtilities.writeChartAsJPEG(os, chart, 600, 400);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (os != null)
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		return chart;
	}

}
