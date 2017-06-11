package gradesPlot;

import core.ProfileController;
import model.Profile;
import planner.Planner;

public class GradesPlotController extends ProfileController {

	public GradesPlot gradesPlot;

	public GradesPlotController(Profile active, Planner p) {
		super(active, p);
	}
}
