package planner;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import core.ProfileController;
import core.Style;
import core.View;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import model.Profile;

/**
 * The Class Planner.
 */
public class Planner extends View implements Observer {

	private volatile static Planner uniqueInstance;
	private static int initialWidth = 940;
	private static int initialHeight = 800;

	public ProfileController pc;
	public Observable observable;
	/* Application Views */
	private ArrayList<View> views;

	/* GUI */
	private Scene scene;
	private HBox options;
	private BorderPane viewPane;

	/* controlPane elements */
	private ChoiceBox<View> chooseView;
	private Button addTerm;
	private Button editTerm;
	private Button addCourse;
	private Button editCourse;

	private BooleanProperty termsExist = new SimpleBooleanProperty(false);
	private BooleanProperty coursesExist = new SimpleBooleanProperty(false);

	/**
	 * Instantiates a new planner.
	 *
	 * @param observable
	 *            the observable
	 */
	private Planner(Observable observable) {
		this.observable = observable;
		observable.addObserver(this);

		this.views = new ArrayList<>();
		this.viewPane = new BorderPane();

		this.options = initOptions();
		this.options.setStyle("-fx-padding: 10; -fx-background-color: #ddd");
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
	public static Planner getInstance(Observable observable) {
		if (uniqueInstance == null) {
			synchronized (Planner.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new Planner(observable);
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
	private BorderPane initLayout() {
		BorderPane bp = new BorderPane();
		chooseView = new ChoiceBox<>(FXCollections.observableArrayList(this.views));
		Style.setChoiceBoxStyle(chooseView);
		chooseView.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldIndex, Number newIndex) {
				if (newIndex.intValue() == -1) {
					newIndex = new Integer(0);
				}
				viewPane.setCenter(views.get(newIndex.intValue()).mainLayout);
			}
		});
		bp.setTop(chooseView);
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
	 * Gets the next color in the sequence of application colors.
	 *
	 * @return the next color
	 */
	public Color getNextColor() {
		for (Color c : Style.selectableColors) {
			if (this.pc.active.currentlySelectedTerm.courseColors.get(Style.colorToHex(c)) == null) {
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
		chooseView.setItems(FXCollections.observableArrayList(this.views));
		if (chooseView.getValue() == null) {
			chooseView.setValue(this.views.get(0));
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
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable observable, Object data) {
		if (observable instanceof Profile) {
			this.termsExist.set(((Profile) observable).terms.size() > 0);
			this.coursesExist.set(((Profile) observable).coursesExist());
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
