package graphs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import metrics.Metrics;

public class Graph {

	/**
	 * Generates a bar chart from method data
	 * 
	 * @Param Metrics
	 * @return PieChart
	 */
	public Node generatePieChart(Metrics metrics) {
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
		
		for (String k : metrics.getMethods().keySet()) {
			int val = metrics.getMethods().get(k);
			String data = k + "(): " + val;
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
	public Node generateBarChart(Metrics metrics) {

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String,Number> barChart = new BarChart<String,Number>(xAxis,yAxis);
        
		for (String k : metrics.getFields().keySet()) {
			int val = metrics.getFields().get(k);
			String data = k + ": " + val;
			XYChart.Series<String, Number> series = new XYChart.Series<>(); 
			series.getData().add(new XYChart.Data<>(data, val)); ;
			barChart.getData().add(series);
		}
        
		barChart.setScaleX(1.2);
		barChart.setScaleY(1.2);
		barChart.setLayoutX(700);
		barChart.setLayoutY(60);
		barChart.setTitle("Most Used Fields");
		return barChart;
	}
}