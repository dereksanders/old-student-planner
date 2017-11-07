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

	public static Color borderColor = Color.web("0xccc");

	/* All styles of border strokes that are used in the application */
	public static BorderStroke fullBorderStroke = new BorderStroke(borderColor, BorderStrokeStyle.SOLID,
			CornerRadii.EMPTY, BorderStroke.THIN);

	public static BorderStroke noRightBorderStroke = new BorderStroke(borderColor, borderColor, borderColor,
			borderColor, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID,
			BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noLeftBorderStroke = new BorderStroke(borderColor, borderColor, borderColor, borderColor,
			BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
			CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noTopLeftBorderStroke = new BorderStroke(borderColor, borderColor, borderColor,
			borderColor, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
			BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noTopBorderStroke = new BorderStroke(borderColor, borderColor, borderColor, borderColor,
			BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
			CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noTopRightBorderStroke = new BorderStroke(borderColor, borderColor, borderColor,
			borderColor, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID,
			BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noTopRightLeftBorderStroke = new BorderStroke(borderColor, borderColor, borderColor,
			borderColor, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID,
			BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noTopBottomBorderStroke = new BorderStroke(borderColor, borderColor, borderColor,
			borderColor, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
			BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noTopBottomLeftBorderStroke = new BorderStroke(borderColor, borderColor, borderColor,
			borderColor, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
			BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noBottomBorderStroke = new BorderStroke(borderColor, borderColor, borderColor,
			borderColor, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
			BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noBottomLeftBorderStroke = new BorderStroke(borderColor, borderColor, borderColor,
			borderColor, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
			BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static BorderStroke noRightLeftBorderStroke = new BorderStroke(borderColor, borderColor, borderColor,
			borderColor, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID,
			BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderStroke.THIN, Insets.EMPTY);

	public static Color appGrey = Color.web("0xeee");
	public static Color appBlue = Color.web("0x3f4be0");
	public static Color appRed = Color.web("0xff6666");
	public static Color appYellow = Color.web("0xffff6e");
	public static Color appGreen = Color.web("0x0dbe0d");
	public static Color appOrange = Color.web("0xffb466");
	public static Color appPurple = Color.web("0x996699");
	public static Color appTeal = Color.web("0x669999");
	public static Color appPink = Color.web("0xffccff");
	public static Color appBrown = Color.web("0xb34d1a");
	public static Color appDarkGrey = Color.web("0x202225");
	public static Color appWhite = Color.web("0xfff");
	public static Color appBlack = Color.web("0x000");

	public static ObservableList<Color> selectableColors = FXCollections.observableArrayList(appBlue, appRed, appYellow,
			appGreen, appOrange, appPurple, appTeal, appPink, appBrown, appDarkGrey);

	public static void setButtonStyle(Button button) {
		button.setStyle("-fx-background-color: #" + Style.colorToHex(Style.appGreen) + "; -fx-text-fill: #"
				+ Style.colorToHex(Style.appWhite) + "; -fx-background-radius: 0;");
	}

	public static void setTitleStyle(Label title) {
		title.setStyle("-fx-font-size: 15.0pt;" + "-fx-text-fill: #" + Style.colorToHex(Style.appBlack) + ";");
	}

	public static void setSmallTitleStyle(Label title) {
		title.setStyle("-fx-font-size: 13.0pt;" + "-fx-text-fill: #" + Style.colorToHex(Style.appBlack) + ";");
	}

	@SuppressWarnings("rawtypes")
	public static void setChoiceBoxStyle(ChoiceBox box) {
		// box.setStyle("-fx-background-color: #" + Style.colorToHex(Style.appWhite) +
		// "; -fx-text-fill: #000;"
		// + "-fx-background-radius: 0");
		box.setStyle(
				"-fx-background-color: linear-gradient(#eee, #ddd); -fx-text-fill: #000; -fx-border-width: 1; -fx-border-color: #ccc");
	}

	@SuppressWarnings("rawtypes")
	public static void setComboBoxStyle(ComboBox box) {
		// box.setStyle("-fx-background-color: #" + Style.colorToHex(Style.appWhite) +
		// "; -fx-text-fill: #000;"
		// + "-fx-background-radius: 0");
		box.setStyle(
				"-fx-background-color: linear-gradient(#eee, #ddd); -fx-text-fill: #000; -fx-border-width: 1; -fx-border-color: #ccc");
	}

	public static void addPadding(Node n) {
		n.setStyle(n.getStyle() + "-fx-padding: 10;");
	}

	public static void addPadding(Node n, int[] vals) {

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
