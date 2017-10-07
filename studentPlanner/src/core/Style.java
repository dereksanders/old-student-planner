package core;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class Style {

	/* All styles of border strokes that are used in the application */
	public static BorderStroke fullBorderStroke = new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
			CornerRadii.EMPTY, BorderStroke.THIN);

	public static BorderStroke noRightBorderStroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK,
			Color.BLACK, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID,
			BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noLeftBorderStroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
			BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
			CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noTopLeftBorderStroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK,
			Color.BLACK, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
			BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noTopBorderStroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
			BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
			CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noTopRightBorderStroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK,
			Color.BLACK, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID,
			BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noTopRightLeftBorderStroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK,
			Color.BLACK, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID,
			BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noTopBottomBorderStroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK,
			Color.BLACK, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
			BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noTopBottomLeftBorderStroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK,
			Color.BLACK, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
			BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noBottomBorderStroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK,
			Color.BLACK, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
			BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noBottomLeftBorderStroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK,
			Color.BLACK, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
			BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noRightLeftBorderStroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK,
			Color.BLACK, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID,
			BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static Color appGrey = Color.web("0xdddddd");
	public static Color appBlue = Color.web("0x6680e6");
	public static Color appRed = Color.web("0xff6666");
	public static Color appYellow = Color.web("0xffcc66");
	public static Color appGreen = Color.web("0x669966");
	public static ObservableList<Color> selectableColors = FXCollections.observableArrayList(appBlue, appRed, appYellow,
			appGreen);

	public static void setButtonStyle(Button button) {
		button.setStyle(
				"-fx-background-color: linear-gradient(#26ce26, #1b9b1b); -fx-text-fill: #fff; -fx-background-radius: 0;");
	}

	public static void setTitleStyle(Label title) {
		title.setStyle("-fx-font-size: 15.0pt;" + "-fx-text-fill: #00a300;" + "-fx-font-weight: bold;");
	}

	public static void setSmallTitleStyle(Label title) {
		title.setStyle("-fx-font-size: 13.0pt;" + "-fx-text-fill: #00a300;" + "-fx-font-weight: bold;");
	}

	@SuppressWarnings("rawtypes")
	public static void setChoiceBoxStyle(ChoiceBox box) {
		box.setStyle(
				"-fx-background-color: linear-gradient(#eee, #ddd); -fx-text-fill: #000; -fx-border-width: 1; -fx-border-color: #ccc");
	}

	@SuppressWarnings("rawtypes")
	public static void setComboBoxStyle(ComboBox box) {
		box.setStyle(
				"-fx-background-color: linear-gradient(#eee, #ddd); -fx-text-fill: #000; -fx-border-width: 1; -fx-border-color: #ccc");
	}

	public static void addPadding(Node n) {
		n.setStyle(n.getStyle() + "-fx-padding: 10;");
	}

	public static void addPadding(Node n, int[] vals) {

		if (vals.length < 4) {
			System.out.println("Not enough arguments.");
			return;
		}

		// TOP, RIGHT, BOTTOM, LEFT
		n.setStyle(n.getStyle() + "-fx-padding: " + vals[0] + " " + vals[1] + " " + vals[2] + " " + vals[3] + ";");
	}

	/**
	 * Color to hex.
	 *
	 * @param color
	 *            the color
	 * @return the string
	 */
	public static String colorToHex(Color color) {
		String hex1;
		String hex2;

		hex1 = Integer.toHexString(color.hashCode()).toUpperCase();

		switch (hex1.length()) {
		case 2:
			hex2 = "000000";
			break;
		case 3:
			hex2 = String.format("00000%s", hex1.substring(0, 1));
			break;
		case 4:
			hex2 = String.format("0000%s", hex1.substring(0, 2));
			break;
		case 5:
			hex2 = String.format("000%s", hex1.substring(0, 3));
			break;
		case 6:
			hex2 = String.format("00%s", hex1.substring(0, 4));
			break;
		case 7:
			hex2 = String.format("0%s", hex1.substring(0, 5));
			break;
		default:
			hex2 = hex1.substring(0, 6);
		}
		return hex2;
	}

	public static Color randomColor() {

		int lowerInt = 0;
		int upperInt = 9;

		int lowerAlpha = 0;
		int upperAlpha = 5;

		int hexLength = 6;

		StringBuilder color = new StringBuilder(8);
		color.append("0x");

		for (int i = 0; i < hexLength; i++) {

			if (Math.random() < 0.5) {

				// choose an int
				int randInt = (int) (((upperInt - lowerInt) * Math.random()) + lowerInt);

				color.append(randInt);

			} else {

				// choose a letter - 97 is ASCII value for 'a'
				int randAlpha = ((int) (((upperAlpha - lowerAlpha) * Math.random()) + lowerAlpha)) + 97;

				color.append((char) randAlpha);
			}
		}

		return Color.web(color.toString());
	}
}
