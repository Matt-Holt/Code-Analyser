package graphs;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import metrics.FieldMetrics;
import metrics.MethodMetrics;
import metrics.Metrics;

public class Graph {
	
	/**
	 * trims string if it exceeds certain amount of characters
	 * 
	 * @param name
	 * @return string
	 */
	private String trimName(String name) {
		int maxLength = 15;
		String trim = name;
		
		if (name.length() >= maxLength) {
			trim = name.substring(0, maxLength - 3);
			trim = trim + "...";
		}
		
		return trim;
	}

	/**
	 * Generates a bar chart from method data
	 * 
	 * @Param Metrics
	 * @return PieChart
	 */
	public Node generateMethodsChart(Metrics metrics) {
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
		
		for (MethodMetrics m : metrics.getMethods()) {
			String name = trimName(m.getMethodName());
			int val = m.getNumOfLines();
			String data = name + ": " + val;
			pieChartData.add(new PieChart.Data(data, val));
		}
		
		PieChart pieChart = new PieChart(pieChartData);
		pieChart.setScaleX(1.2);
		pieChart.setScaleY(1.2);
		pieChart.setLayoutX(50);
		pieChart.setLayoutY(60);
		pieChart.setTitle("Largest Methods (Lines Written)");
		return pieChart;
	}
	
	/**
	 *Generates a bar chart from field data
	 * 
	 * @Param Metrics
	 * @return PieChart
	 */
	public Node generateFieldsChart(Metrics metrics) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String,Number> barChart = new BarChart<String,Number>(xAxis,yAxis);
        
        for (FieldMetrics f : metrics.getFields()) {
        	String name = trimName(f.getFieldName());
			int val = f.getUseCount();
			String data = name + ": " + val;
			XYChart.Series<String, Number> series = new XYChart.Series<>(); 
			series.getData().add(new XYChart.Data<>(data, val));
			barChart.getData().add(series);	
        }
        
		barChart.setScaleX(1.2);
		barChart.setScaleY(1.2);
		barChart.setLayoutX(700);
		barChart.setLayoutY(60);
		barChart.setTitle("Most Used Fields");
		return barChart;
	}
	
	/**
	 *Generates a pie chart from all files
	 * 
	 * @Param array list of Metrics
	 * @return PieChart
	 */
	public Node generateOverviewChart(ArrayList<Metrics> metrics) {
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
		
		for (Metrics m : metrics) {
			String name = trimName(m.getFileName());
			int val = m.getTotalLines();
			String data = name + ": " + val;
			pieChartData.add(new PieChart.Data(data, val));
		}
		
		PieChart pieChart = new PieChart(pieChartData);
		pieChart.setScaleX(1.2);
		pieChart.setScaleY(1.2);
		pieChart.setLayoutX(700);
		pieChart.setLayoutY(120);
		pieChart.setTitle("Current Project");
		return pieChart;
	}
}
