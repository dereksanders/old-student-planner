package core;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import courseSchedule.AddCourse;
import courseSchedule.AddTerm;
import courseSchedule.EditCourse;
import courseSchedule.EditTerm;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import model.Profile;

/**
 * The Class Planner.
 */
public class Planner extends View implements Observer {

	private volatile static Planner uniqueInstance;
	private static int initialWidth = 800;
	private static int initialHeight = 800;

	public ProfileController pc;
	public Observable o;
	/* Application Views */
	private ArrayList<View> views;

	/* GUI */
	private Scene scene;
	private HBox options;
	private BorderPane viewPane;

	/* controlPane elements */
	public Button addTerm;
	public Button editTerm;
	public Button addCourse;
	public Button editCourse;

	public BooleanProperty termsExist = new SimpleBooleanProperty(false);
	public BooleanProperty coursesExist = new SimpleBooleanProperty(false);

	/**
	 * Instantiates a new planner.
	 *
	 * @param o
	 *            the o
	 */
	private Planner(Observable o) {
		this.o = o;
		o.addObserver(this);

		this.views = new ArrayList<>();
		this.viewPane = new BorderPane();

		this.options = initOptions();
		this.mainLayout = new BorderPane();
		this.mainLayout.setCenter(viewPane);
		this.mainLayout.setBottom(options);
		this.scene = new Scene(this.mainLayout, initialWidth, initialHeight);
	}

	/**
	 * Gets the single instance of Planner.
	 *
	 * @param o
	 *            the o
	 * @return single instance of Planner
	 */
	public static Planner getInstance(Observable o) {
		if (uniqueInstance == null) {
			synchronized (Planner.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new Planner(o);
				}
			}
		}
		return uniqueInstance;
	}

	/**
	 * Inits the control pane.
	 * 
	 * @return
	 *
	 * @return the border pane
	 */
	private HBox initOptions() {

		HBox options = new HBox(20);

		/* Add Term */
		this.addTerm = new Button("Add Term");
		Style.setButtonStyle(addTerm);
		addTerm.setOnAction(e -> {
			this.pc.addTerm(AddTerm.display());
		});

		/* Edit Term */
		this.editTerm = new Button("Edit Term");
		editTerm.visibleProperty().bind(termsExist);
		Style.setButtonStyle(editTerm);
		editTerm.setOnAction(e -> {
			EditTerm.display(this.pc);
		});

		/* Add Course */
		this.addCourse = new Button("Add Course");
		addCourse.visibleProperty().bind(termsExist);
		Style.setButtonStyle(addCourse);
		addCourse.setOnAction(e -> {
			this.pc.addCourse(AddCourse.display(this.pc));
		});

		/* Edit Course */
		this.editCourse = new Button("Edit Course");
		editCourse.visibleProperty().bind(coursesExist);
		Style.setButtonStyle(editCourse);
		editCourse.setOnAction(e -> {
			this.pc.editCourse(EditCourse.display(this.pc));
		});

		options.getChildren().addAll(addTerm, editTerm, addCourse, editCourse);

		return options;
	}

	/**
	 * Gets the next color in the sequence of application colors.
	 *
	 * @return the next color
	 */
	public Color getNextColor() {

		for (Color c : Style.selectableColors) {
			if (this.pc.active.courseColors.get(c) == null) {
				return c;
			}
		}
		return Color.WHITE;
	}

	/**
	 * Adds the view.
	 *
	 * @param view
	 *            the view
	 */
	public void addView(View view) {
		this.views.add(view);

		/* TODO: Remove this & add view selection */
		if (this.viewPane.getLeft() == null) {
			this.viewPane.setLeft(view.mainLayout);
		} else if (this.viewPane.getRight() == null) {
			this.viewPane.setRight(view.mainLayout);
		} else if (this.viewPane.getBottom() == null) {
			this.viewPane.setBottom(view.mainLayout);
		} else if (this.viewPane.getTop() == null) {
			this.viewPane.setTop(view.mainLayout);
		}
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
	 * @see core.View#refresh()
	 */
	@Override
	public void refresh() {
		update(this.o, null);
		for (View v : this.views) {
			v.refresh();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable arg0, Object arg1) {

		if (arg0 instanceof Profile) {
			this.termsExist.set(((Profile) arg0).terms.size() > 0);
			this.coursesExist.set(((Profile) arg0).coursesExist());
		}
	}
}
