package gradesPlot;

import core.Planner;
import core.ProfileController;
import model.Profile;

public class GradesPlotController extends ProfileController {

	public GradesPlot gradesPlot;

	public GradesPlotController(Profile active, Planner p) {
		super(active, p);
	}
}
