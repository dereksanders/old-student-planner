package core;

import javafx.scene.layout.BorderPane;

public abstract class View {

	public BorderPane mainLayout;

	/* TODO: Is this necessary when Observer#update exists? */
	public abstract void refresh();
}
