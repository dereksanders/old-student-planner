package dashboard;

import core.ProfileController;
import javafx.event.ActionEvent;
import model.Profile;

public class DashboardController extends ProfileController {

	public Dashboard dashboard;

	public DashboardController(Profile profile) {

		super(profile);
	}

	public void addPriority(ActionEvent e) {

		this.dashboard.priorities.add(this.dashboard.chooseEvent.getValue());

		// The profile itself has not been modified, so only the dashboard view needs to
		// be refreshed.
		this.dashboard.refresh();
	}
}
