package com.ebay.build.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.ApplicationFrame;

public class BarChart extends ApplicationFrame {

	
	private static final long serialVersionUID = 1L;

	

	public BarChart(String title, String abscissaAxisName, String verticalAxisName, double[][] data, String[] rowKeys, String[] columnKeys, String imageName) {
		//Passing parameters to functions
		
		super(title);
		CategoryDataset dataset = createDataset(data, rowKeys, columnKeys);
		createChart(title, abscissaAxisName, verticalAxisName, dataset, imageName);

	}

	//create a data set
	public CategoryDataset createDataset(double[][] dataArray, String[] rowKeys, String[] columnKeys) {
		CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
				rowKeys, columnKeys, dataArray);
		return dataset;
	}

	
	/**
	 * 
	 * use a data set to create a chart
	 * @param title
	 * @param abscissaAxis
	 * @param verticalAxis
	 * @param dataset
	 * @param imageName
	 * @return JFreeChart
	 */
	public JFreeChart createChart(String title, String abscissaAxis, String verticalAxis, CategoryDataset dataset, String imageName) {
		// Create a JFreeChart
		JFreeChart chart = ChartFactory.createStackedBarChart(title,
				abscissaAxis, verticalAxis, dataset, PlotOrientation.VERTICAL,
				true, true, false);

		StackedBarRenderer stackedRenderer = (StackedBarRenderer) chart
				.getCategoryPlot().getRenderer();
		stackedRenderer.setMaximumBarWidth(.06);
		stackedRenderer.setShadowVisible(false);

		
		// Set title of BarChart
		TextTitle titleInner = new TextTitle(title, new Font("Calibri",
				Font.BOLD, 15));
		chart.setTitle( titleInner);


		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		CategoryItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesPaint(0, new Color(70, 130, 180));
		renderer.setSeriesPaint(1,new Color(205, 38, 38));
		renderer.setSeriesPaint(2, new Color(162, 205, 90));

		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeGridlinePaint(Color.GRAY);
		plot.setRangeGridlineStroke(new BasicStroke());

		CategoryAxis axis = (CategoryAxis)plot.getDomainAxis();
		
		// Set font of abscissa axis
		axis.setLabelFont(new Font("Calibri", Font.BOLD, 15));
		
		if(imageName == "Weekly_Trend.jpeg")
		{
			plot = (CategoryPlot) chart.getPlot();
			axis = (CategoryAxis)plot.getDomainAxis();
			axis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		}
		
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
