package core;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Listing {

	private static int defaultWidth = 20;
	private static int defaultHeight = 20;

	private Color color;
	private String text;
	private Rectangle icon;
	private int spacing;
	private int width;
	private int height;

	public Listing(Color color, String text) {

		this.color = color;
		this.text = text;

		this.width = defaultWidth;
		this.height = defaultHeight;
		this.icon = new Rectangle(this.width, this.height);
		this.icon.setFill(this.color);
		this.spacing = 5;
	}

	public HBox show() {

		HBox listing = new HBox(this.spacing);
		listing.getChildren().addAll(this.icon, new Label(this.text));
		return listing;
	}
}
