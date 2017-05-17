package grades;

import core.Planner;
import core.Term;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;

public class GradesPlot {

	public static BorderPane init() {

		BorderPane gpbp = new BorderPane();

		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel("Term");

		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Grade %");

		LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);
		lineChart.setTitle("Grade Monitoring");

		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName("My grades");
		lineChart.setLegendVisible(false);

		for (Term t : Planner.terms) {
			series.getData().add(new XYChart.Data<>(t.name + " (" + t.end.getYear() + ")", t.grade));
		}

		lineChart.getData().add(series);
		gpbp.setCenter(lineChart);
		return gpbp;
	}
}
