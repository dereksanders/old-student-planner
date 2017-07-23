package core;

import javafx.scene.layout.BorderPane;

/**
 * The Class View.
 */
public abstract class View {

	public BorderPane mainLayout;

	/**
	 * Refreshes the view.
	 */
	public abstract void refresh();
}
