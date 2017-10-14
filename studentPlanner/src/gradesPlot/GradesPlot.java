package gradesPlot;

import java.util.Observable;
import java.util.Observer;

import core.View;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import model.Profile;
import model.Term;

public class GradesPlot extends View implements Observer {

	private Observable observable;
	private GradesPlotController controller;

	private static LineChart<String, Number> termGrades;

	public GradesPlot(GradesPlotController controller) {

		this.controller = controller;
		this.controller.gradesPlot = this;

		this.observable = controller.profile;
		this.observable.addObserver(this);

		this.mainLayout = initLayout();
	}

	private BorderPane initLayout() {

		BorderPane gpbp = new BorderPane();

		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel("Term");

		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Grade %");

		termGrades = new LineChart<String, Number>(xAxis, yAxis);
		termGrades.setTitle("Term Averages");

		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName("My grades");
		termGrades.setLegendVisible(false);

		for (Term t : controller.profile.terms) {
			series.getData().add(new XYChart.Data<>(t.name + " (" + t.end.getYear() + ")", t.avg));
		}

		termGrades.getData().add(series);
		gpbp.setCenter(termGrades);
		return gpbp;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof Profile) {
			XYChart.Series<String, Number> grades = new XYChart.Series<>();

			for (Term t : controller.profile.terms) {
				grades.getData().add(new XYChart.Data<>(t.name + " (" + t.end.getYear() + ")", t.avg));
			}

			termGrades.getData().clear();
			termGrades.getData().add(grades);
		}
	}

	@Override
	public void refresh() {
		update(this.observable, null);
	}

	@Override
	public String toString() {
		return "Grades Plot";
	}
}
