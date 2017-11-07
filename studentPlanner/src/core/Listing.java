package core;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Listing {

	private static int defaultDiameter = 10;

	private Color color;
	private String text;
	private Circle icon;
	private int spacing;

	public Listing(Color color, String text) {

		this.color = color;
		this.text = text;

		this.icon = new Circle(Listing.defaultDiameter);
		this.icon.setFill(this.color);
		this.spacing = 5;
	}

	public HBox show() {

		HBox listing = new HBox(this.spacing);
		listing.getChildren().addAll(this.icon, new Label(this.text));
		return listing;
	}
}
