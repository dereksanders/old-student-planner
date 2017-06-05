package core;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
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
				"-fx-font-size: 11.0pt;" + "-fx-text-fill: #" + colorToHex(appBlue) + ";" + "-fx-font-weight: bold;"
						+ "-fx-background-color: linear-gradient(from 25.0% 25.0% to 100.0% 100.0%, #fff, #ddd);"
						+ "-fx-border-color: #ccc");
	}

	public static void setTitleStyle(Label title) {
		title.setStyle(
				"-fx-font-size: 18.0pt;" + "-fx-text-fill: #" + colorToHex(appBlue) + ";" + "-fx-font-weight: bold;");
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
}