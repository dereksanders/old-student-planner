package gradesPlot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.PriorityQueue;
import java.util.Queue;

import core.Listing;
import core.Style;
import core.View;
import javafx.collections.FXCollections;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.Course;
import model.Profile;
import model.Term;
import utility.GenericHashTable;
import utility.GenericLinkedHashTable;

public class GradesPlot extends View implements Observer {

	private Observable observable;
	private GradesPlotController controller;

	private static BorderPane termGradesPane;
	private static LineChart<String, Number> termGrades;

	private static BorderPane deptCoursesPane;
	private static PieChart deptCourses;

	private static BorderPane deptGradesPane;
	private static BarChart<String, Number> deptGrades;

	private static ArrayList<Course> allCoursesTaken;
	private static ArrayList<String> allDepartments;

	public GradesPlot(GradesPlotController controller) {

		this.controller = controller;
		this.controller.gradesPlot = this;

		this.observable = controller.profile;
		this.observable.addObserver(this);

		this.mainLayout = initLayout();
	}

	public BorderPane initLayout() {

		BorderPane gpbp = new BorderPane();

		termGradesPane = new BorderPane();
		Style.addPadding(termGradesPane);

		deptCoursesPane = new BorderPane();
		Style.addPadding(deptCoursesPane);

		deptGradesPane = new BorderPane();
		Style.addPadding(deptGradesPane);

		TabPane selectGraph = new TabPane();

		// Set up termGrades line chart.
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

		Tab termGradesTab = new Tab();
		termGradesTab.setText("Term Averages");
		termGradesTab.setClosable(false);
		termGradesTab.setContent(termGradesPane);

		// Set up deptCourses pie chart.
		deptCourses = new PieChart();
		deptCourses.setTitle("Courses Taken by Department");

		Tab deptCoursesTab = new Tab();
		deptCoursesTab.setText("Courses Taken by Dept.");
		deptCoursesTab.setClosable(false);
		deptCoursesTab.setContent(deptCoursesPane);

		// Set up deptGrades bar chart.
		CategoryAxis deptGradesXAxis = new CategoryAxis();
		deptGradesXAxis.setLabel("Department");

		NumberAxis deptGradesYAxis = new NumberAxis();
		deptGradesYAxis.setLabel("Grade %");

		deptGrades = new BarChart<String, Number>(deptGradesXAxis, deptGradesYAxis);
		deptGrades.setTitle("Department Averages");

		Tab deptGradesTab = new Tab();
		deptGradesTab.setText("Department Averages");
		deptGradesTab.setClosable(false);
		deptGradesTab.setContent(deptGradesPane);

		selectGraph.getTabs().add(termGradesTab);
		selectGraph.getTabs().add(deptGradesTab);
		selectGraph.getTabs().add(deptCoursesTab);

		gpbp.setCenter(selectGraph);
		return gpbp;
	}

	private void updateTermGrades() {

		termGradesPane.getChildren().clear();

		VBox legend = new VBox(10);
		legend.setBorder(new Border(Style.fullBorderStroke));
		Style.addPadding(legend);
		Label legendTitle = new Label("Legend");
		Style.setTitleStyle(legendTitle);
		legend.getChildren().add(legendTitle);

		termGrades.getData().clear();

		XYChart.Series<String, Number> grades = new XYChart.Series<>();

		for (Term t : controller.profile.terms) {
			grades.getData().add(new XYChart.Data<>(t.name + " (" + t.end.getYear() + ")", t.avg));
			legend.getChildren().add(new Label(t.name + " (" + t.end.getYear() + ")" + ": " + Math.round(t.avg) + "%"));
		}

		termGrades.getData().add(grades);

		termGradesPane.setCenter(termGrades);
		termGradesPane.setRight(legend);
	}

	private void updateDeptCourses() {

		deptCoursesPane.getChildren().clear();

		VBox legend = new VBox(10);
		legend.setBorder(new Border(Style.fullBorderStroke));
		Style.addPadding(legend);
		Label legendTitle = new Label("Legend");
		Style.setTitleStyle(legendTitle);
		legend.getChildren().add(legendTitle);

		deptCourses.getData().clear();

		GenericHashTable<String, Integer> deptFrequencies = new GenericHashTable<>(16);

		allCoursesTaken = new ArrayList<>();
		allDepartments = new ArrayList<>();

		for (Term t : this.controller.profile.terms) {

			for (Course c : t.courses) {

				allCoursesTaken.add(c);

				if (!allDepartments.contains(c.departmentID)) {

					allDepartments.add(c.departmentID);
					deptFrequencies.put(c.departmentID, 1);

				} else {

					deptFrequencies.update(c.departmentID, deptFrequencies.get(c.departmentID) + 1);
				}
			}
		}

		ArrayList<PieChart.Data> pieData = new ArrayList<>();

		for (String dept : allDepartments) {

			if (dept != null) {
				pieData.add(new PieChart.Data(dept.toString(), deptFrequencies.get(dept.toString())));
			}
		}

		deptCourses.setData(FXCollections.observableArrayList(pieData));
		deptCourses.setLegendVisible(false);

		Queue<Color> colors = new LinkedList<>(Style.selectableColors);

		for (PieChart.Data data : pieData) {

			Color current = colors.poll();

			if (current != null) {

				data.getNode().setStyle("-fx-pie-color: #" + Style.colorToHex(current) + ";");
				legend.getChildren().add(new Listing(current, data.getName() + ": " + (int) data.getPieValue()).show());
			}
		}

		deptCoursesPane.setCenter(deptCourses);
		deptCoursesPane.setRight(legend);
	}

	private void updateDeptGrades() {

		deptGradesPane.getChildren().clear();

		VBox legend = new VBox(10);
		legend.setBorder(new Border(Style.fullBorderStroke));
		Style.addPadding(legend);
		Label legendTitle = new Label("Legend");
		Style.setTitleStyle(legendTitle);
		legend.getChildren().add(legendTitle);

		deptGrades.getData().clear();

		GenericLinkedHashTable<String, Course> deptCourses = new GenericLinkedHashTable<>(16, false);

		for (Course c : allCoursesTaken) {

			deptCourses.put(c.departmentID, c);
		}

		GenericHashTable<String, Integer> deptAvgs = new GenericHashTable<>(16);

		for (Object dept : deptCourses.keys) {

			if (dept != null) {

				PriorityQueue<Course> courses = deptCourses.get(dept.toString());

				int numCourses = courses.size();
				int sumGrades = 0;

				for (Course c : courses) {

					sumGrades += (int) c.gradeSoFar;
				}

				deptAvgs.put(dept.toString(), sumGrades / numCourses);
			}
		}

		XYChart.Series<String, Number> deptGradesData = new XYChart.Series<>();

		for (String dept : allDepartments) {

			if (dept != null) {

				XYChart.Data<String, Number> deptData = new XYChart.Data<>(dept.toString(),
						deptAvgs.get(dept.toString()));
				deptGradesData.getData().add(deptData);
				deptGradesData.setName(dept.toString());
			}
		}

		deptGrades.getData().add(deptGradesData);

		Queue<Color> colors = new LinkedList<>(Style.selectableColors);

		for (XYChart.Data<String, Number> data : deptGradesData.getData()) {

			Color current = colors.poll();

			if (current != null) {

				data.getNode().setStyle("-fx-bar-fill: #" + Style.colorToHex(current) + ";");
				legend.getChildren().add(new Listing(current, data.getXValue() + ": " + data.getYValue() + "%").show());
			}
		}

		deptGrades.setLegendVisible(false);

		deptGradesPane.setCenter(deptGrades);
		deptGradesPane.setRight(legend);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof Profile) {

			updateTermGrades();
			updateDeptCourses();
			updateDeptGrades();
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
