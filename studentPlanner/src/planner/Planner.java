package planner;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import core.Main;
import core.ProfileController;
import core.Style;
import core.View;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Profile;

/**
 * The Class Planner.
 */
public class Planner extends View implements Observer {

	public enum VIEW_INDEX {
		DASHBOARD(0), COURSE_SCHEDULE(1), TERM_CALENDAR(2), GRADES(3), GRADES_PLOT(4);

		public int val;

		private VIEW_INDEX(int val) {
			this.val = val;
		}
	};

	public static String menuColor = Style.colorToHex(Style.appGrey);
	public static String hoverColor = Style.colorToHex(Style.appGreen);

	private volatile static Planner uniqueInstance;
	private static int initialWidth = 1280;
	private static int initialHeight = 960;

	public ProfileController pc;
	public Observable observable;
	/* Application Views */
	public ArrayList<View> views;
	public VBox activeViewBox;

	/* GUI */
	private Scene scene;
	private HBox options;
	public BorderPane viewPane;

	/* controlPane elements */
	private Button addTerm;
	private Button editTerm;
	private Button addCourse;
	private Button editCourse;

	private BooleanProperty termsExist = new SimpleBooleanProperty(false);
	private BooleanProperty coursesExist = new SimpleBooleanProperty(false);

	/**
	 * Instantiates a new planner.
	 *
	 * @param profile
	 *            the observable
	 */
	private Planner(Observable profile, ProfileController pc) {

		this.observable = profile;
		profile.addObserver(this);

		this.pc = pc;

		this.views = new ArrayList<>();
		this.viewPane = new BorderPane();

		this.options = initOptions();
		this.options.setStyle("-fx-padding: 10; -fx-background-color: #" + Style.colorToHex(Style.appWhite) + ";");
		this.mainLayout = initLayout();
		this.mainLayout.setStyle("-fx-background-color: #fff;");
		this.scene = new Scene(this.mainLayout, initialWidth, initialHeight);
	}

	/**
	 * Gets the single instance of Planner.
	 *
	 * @param observable
	 *            the observable
	 * @return single instance of Planner
	 */
	public static Planner getInstance(Observable observable, ProfileController pc) {
		if (uniqueInstance == null) {
			synchronized (Planner.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new Planner(observable, pc);
				}
			}
		}
		return uniqueInstance;
	}

	/**
	 * Inits the layout.
	 *
	 * @return the border pane
	 */
	public BorderPane initLayout() {
		BorderPane bp = new BorderPane();

		VBox menu = new VBox(0);
		menu.setStyle(menu.getStyle() + "-fx-background-color: #" + menuColor + ";");

		VBox db = new VBox(0);
		db.setPadding(new Insets(10, 20, 10, 20));

		ImageView dbIcon = new ImageView();
		dbIcon.setImage(new Image(Main.class.getResourceAsStream("home.png")));
		dbIcon.setFitWidth(70);
		dbIcon.setPreserveRatio(true);
		dbIcon.setSmooth(true);
		dbIcon.setCache(true);

		Label dashboardTitle = new Label("Home");
		Style.setSmallTitleStyle(dashboardTitle);
		dashboardTitle.setStyle(dashboardTitle.getStyle());

		db.setOnMouseClicked(e -> {
			refresh();
			viewPane.setCenter(views.get(VIEW_INDEX.DASHBOARD.val).mainLayout);
			this.activeViewBox = db;
		});

		db.getChildren().addAll(dbIcon, dashboardTitle);
		db.setAlignment(Pos.CENTER);

		db.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				db.setStyle("-fx-background-color: #" + hoverColor + ";");
				db.getChildren().get(0).setStyle(db.getChildren().get(0).getStyle() + "-fx-text-fill: #fff;");
			}
		});

		db.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				db.setStyle("-fx-background-color: #" + menuColor + ";");
				db.getChildren().get(0).setStyle(db.getChildren().get(0).getStyle() + "-fx-text-fill: #000;");
			}
		});

		VBox cs = new VBox(0);
		cs.setPadding(new Insets(10, 20, 10, 20));

		ImageView csIcon = new ImageView();
		csIcon.setImage(new Image(Main.class.getResourceAsStream("clock.png")));
		csIcon.setFitWidth(70);
		csIcon.setPreserveRatio(true);
		csIcon.setSmooth(true);
		csIcon.setCache(true);

		Label csTitle = new Label("Course Schedule");
		Style.setSmallTitleStyle(csTitle);

		cs.setOnMouseClicked(e -> {
			refresh();
			viewPane.setCenter(views.get(VIEW_INDEX.COURSE_SCHEDULE.val).mainLayout);
			this.activeViewBox = cs;
		});

		cs.getChildren().addAll(csIcon, csTitle);
		cs.setAlignment(Pos.CENTER);

		cs.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				cs.setStyle("-fx-background-color: #" + hoverColor + ";");
				cs.getChildren().get(0).setStyle(cs.getChildren().get(0).getStyle() + "-fx-text-fill: #fff;");
			}
		});

		cs.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				cs.setStyle("-fx-background-color: #" + menuColor + ";");
				cs.getChildren().get(0).setStyle(cs.getChildren().get(0).getStyle() + "-fx-text-fill: #000;");
			}
		});

		VBox tc = new VBox(0);
		tc.setPadding(new Insets(10, 20, 10, 20));

		ImageView tcIcon = new ImageView();
		tcIcon.setImage(new Image(Main.class.getResourceAsStream("calendar.png")));
		tcIcon.setFitWidth(70);
		tcIcon.setPreserveRatio(true);
		tcIcon.setSmooth(true);
		tcIcon.setCache(true);

		Label tcTitle = new Label("Term Calendar");
		Style.setSmallTitleStyle(tcTitle);

		tc.setOnMouseClicked(e -> {
			refresh();
			viewPane.setCenter(views.get(VIEW_INDEX.TERM_CALENDAR.val).mainLayout);
			this.activeViewBox = tc;
		});

		tc.getChildren().addAll(tcIcon, tcTitle);
		tc.setAlignment(Pos.CENTER);

		tc.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				tc.setStyle("-fx-background-color: #" + hoverColor + ";");
				tc.getChildren().get(0).setStyle(tc.getChildren().get(0).getStyle() + "-fx-text-fill: #fff;");
			}
		});

		tc.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				tc.setStyle("-fx-background-color: #" + menuColor + ";");
				tc.getChildren().get(0).setStyle(tc.getChildren().get(0).getStyle() + "-fx-text-fill: #000;");
			}
		});

		VBox g = new VBox(0);
		g.setPadding(new Insets(10, 20, 10, 20));

		ImageView gIcon = new ImageView();
		gIcon.setImage(new Image(Main.class.getResourceAsStream("calculator.png")));
		gIcon.setFitWidth(70);
		gIcon.setPreserveRatio(true);
		gIcon.setSmooth(true);
		gIcon.setCache(true);

		Label gTitle = new Label("Grades");
		Style.setSmallTitleStyle(gTitle);

		g.setOnMouseClicked(e -> {
			refresh();
			viewPane.setCenter(views.get(VIEW_INDEX.GRADES.val).mainLayout);
			this.activeViewBox = g;
		});

		g.getChildren().addAll(gIcon, gTitle);
		g.setAlignment(Pos.CENTER);

		g.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				g.setStyle("-fx-background-color: #" + hoverColor + ";");
				g.getChildren().get(0).setStyle(g.getChildren().get(0).getStyle() + "-fx-text-fill: #fff;");
			}
		});

		g.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				g.setStyle("-fx-background-color: #" + menuColor + ";");
				g.getChildren().get(0).setStyle(g.getChildren().get(0).getStyle() + "-fx-text-fill: #000;");
			}
		});

		VBox gp = new VBox(0);
		gp.setPadding(new Insets(10, 20, 10, 20));

		ImageView gpIcon = new ImageView();
		gpIcon.setImage(new Image(Main.class.getResourceAsStream("bar.png")));
		gpIcon.setFitWidth(70);
		gpIcon.setPreserveRatio(true);
		gpIcon.setSmooth(true);
		gpIcon.setCache(true);

		Label gpTitle = new Label("Grades Plot");
		Style.setSmallTitleStyle(gpTitle);

		gp.setOnMouseClicked(e -> {
			refresh();
			viewPane.setCenter(views.get(VIEW_INDEX.GRADES_PLOT.val).mainLayout);
			this.activeViewBox = gp;
		});

		gp.getChildren().addAll(gpIcon, gpTitle);
		gp.setAlignment(Pos.CENTER);

		gp.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				gp.setStyle("-fx-background-color: #" + hoverColor + ";");
				gp.getChildren().get(0).setStyle(gp.getChildren().get(0).getStyle() + "-fx-text-fill: #fff;");
			}
		});

		gp.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				gp.setStyle("-fx-background-color: #" + menuColor + ";");
				gp.getChildren().get(0).setStyle(gp.getChildren().get(0).getStyle() + "-fx-text-fill: #000;");
			}
		});

		menu.getChildren().addAll(db, cs, tc, g, gp);

		final Menu file = new Menu("File");
		final Menu edit = new Menu("Edit");
		final Menu view = new Menu("View");
		final Menu help = new Menu("Help");

		MenuBar controls = new MenuBar();
		controls.getMenus().addAll(file, edit, view, help);
		controls.setStyle("-fx-background-color: #" + Style.colorToHex(Style.appWhite) + ";");

		bp.setTop(controls);
		bp.setLeft(menu);
		bp.setCenter(viewPane);
		bp.setBottom(this.options);
		return bp;
	}

	/**
	 * Inits the control pane.
	 *
	 * @return the border pane
	 */
	private HBox initOptions() {

		HBox options = new HBox(20);

		/* Add Term */
		this.addTerm = new Button("Add Term");
		Style.setButtonStyle(addTerm);
		addTerm.setOnAction(e -> {
			new AddTerm(this.pc);
		});

		/* Edit Term */
		this.editTerm = new Button("Edit Term");
		editTerm.visibleProperty().bind(termsExist);
		Style.setButtonStyle(editTerm);
		editTerm.setOnAction(e -> {
			new EditTerm(this.pc);
		});

		/* Add Course */
		this.addCourse = new Button("Add Course");
		addCourse.visibleProperty().bind(termsExist);
		Style.setButtonStyle(addCourse);
		addCourse.setOnAction(e -> {
			new AddCourse(this.pc);
		});

		/* Edit Course */
		this.editCourse = new Button("Edit Course");
		editCourse.visibleProperty().bind(coursesExist);
		Style.setButtonStyle(editCourse);
		editCourse.setOnAction(e -> {
			new EditCourse(this.pc);
		});

		options.getChildren().addAll(addTerm, editTerm, addCourse, editCourse);

		return options;
	}

	/**
	 * Adds the view.
	 *
	 * @param view
	 *            the view
	 */
	public void addView(View view) {
		this.views.add(view);
	}

	/**
	 * Gets the scene.
	 *
	 * @return the scene
	 */
	public Scene getScene() {
		return this.scene;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable observable, Object data) {
		if (observable instanceof Profile) {
			this.termsExist.set(((Profile) observable).terms.size() > 0);
			this.coursesExist.set(((Profile) observable).coursesExist());

			if (this.activeViewBox != null) {

				// this.activeViewBox.setBackground(new Background(new BackgroundImage(
				// new Image(Main.class.getResourceAsStream("active2.png")),
				// BackgroundRepeat.NO_REPEAT,
				// BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
				// BackgroundSize.DEFAULT)));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.View#refresh()
	 */
	@Override
	public void refresh() {
		update(this.observable, null);
		for (View v : this.views) {
			v.refresh();
		}
	}
}
