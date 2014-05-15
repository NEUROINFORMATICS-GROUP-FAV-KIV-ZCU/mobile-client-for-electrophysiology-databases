package cz.zcu.kiv.eeg.mobile.base2.data;

import java.util.Calendar;

/**
 * Gathered values used in application. Values' names are descriptive enough.
 */
public class Values {
	
	// activity results flags
	public static final int ADD_RESERVATION_FLAG = 1;
	public static final int SELECT_FILE_FLAG = 2;

	public static final String FILE_PATH = "filePath";

	// shared preferences keys
	public static final String URL_DEFAULT = "https://uu404p22-kiv.fav.zcu.cz:8443"; //
	public static final String PREFS_CREDENTIALS = "AccountCredentials";
	public static final String PREFS_VARIOUS = "VariousValues";

	// REST service suffixes
	public static final String ENDPOINT = "/rest";
	public static final String SERVICE_DATAFILE = "/datafile/";
	public static final String SERVICE_EXPERIMENTS = "/experiments/";
	public static final String SERVICE_SCENARIOS = "/scenarios/";
	public static final String SERVICE_RESEARCH_GROUPS = "/groups/";

	public static final String SERVICE_USER_LOGIN = "/rest/user/";
	public static final String SERVICE_USER = "/user/";
	public static final String SERVICE_GET_LAYOUT = "/rest/form-layouts/?form=%s&layout=%s";
	public static final String SERVICE_GET_COUNT = "/rest/form-layouts/form/count";
	public static final String SERVICE_GET_AVAILABLE_LAYOUTS = "/rest/form-layouts/available";
	public static final String SERVICE_GET_DATA = "/rest/form-layouts/data?entity=";

	public static final String SERVICE_RESERVATION = "/reservation/";
	public static final String SERVICE_ARTIFACTS = "/experiments/artifacts";
	public static final String SERVICE_DIGITIZATIONS = "/experiments/digitizations";
	public static final String SERVICE_DISEASES = "/experiments/diseases";
	public static final String SERVICE_HARDWARE = "/experiments/hardwareList";
	public static final String SERVICE_SOFTWARE = "/experiments/softwareList";
	public static final String SERVICE_PHARMACEUTICAL = "/experiments/pharmaceuticals";
	public static final String SERVICE_ELECTRODE_LOCATIONS = "/experiments/electrodeLocations";
	public static final String SERVICE_ELECTRODE_FIXLIST = "/experiments/electrodeFixList";
	public static final String SERVICE_ELECTRODE_TYPES = "/experiments/electrodeTypes";
	public static final String SERVICE_ELECTRODE_SYSTEMS = "/experiments/electrodeSystems";
	public static final String SERVICE_WEATHER = "/experiments/weatherList";

	// REST service qualifiers
	public static final String SERVICE_QUALIFIER_MINE = "mine";
	public static final String SERVICE_QUALIFIER_ALL = "all";
	
	//INTENT
	public static final int PICK_FIELD_ID_REQUEST = 1;
	public static final String USED_FIELD = "usedFields";
	
	// ViewBuilder
	public static final int NODE_ID = 1;

	// ODML
	public static final String ODML_STRING_TYPE = "string";
	public static final String ODML_INT_TYPE = "int";
	public static final String ODML_BOOLEAN_TYPE = "boolean";
	public static final String ODML_LAYOUT_NAME = "layoutName";
	public static final String ODML_FORM = "form";
	public static final String ODML_LABEL = "label";
	public static final String ODML_REQUIRED = "required";
	public static final String ODML_ID_NODE = "id";
	public static final String ODML_ID_RIGHT = "idRight";
	public static final String ODML_ID_BOTTOM = "idBottom";

	public static final int ADD_SCENARIO_FLAG = 20;
	public static final int ADD_PERSON_FLAG = 21;
	public static final int ADD_ELECTRODE_LOCATION_FLAG = 22;
	public static final int ADD_DIGITIZATION_FLAG = 23;
	public static final int ADD_ARTIFACT_FLAG = 24;
	public static final int ADD_DISEASE_FLAG = 25;
	public static final int ADD_ELECTRODE_FIX_FLAG = 26;
	public static final int ADD_EXPERIMENT_FLAG = 27;
	public static final int ADD_WEATHER_FLAG = 28;

	public static final String ADD_RESERVATION_KEY = "addReservation";
	public static final String ADD_PERSON_KEY = "addPerson";
	public static final String ADD_SCENARIO_KEY = "addScenario";
	public static final String ADD_ELECTRODE_LOCATION_KEY = "addElectrodeLocation";
	public static final String ADD_ARTIFACT_KEY = "addArtifact";
	public static final String ADD_DIGITIZATION_KEY = "addDigitization";
	public static final String ADD_DISEASE_KEY = "addDisease";
	public static final String ADD_ELECTRODE_FIX_KEY = "addElectrodeFix";
	public static final String ADD_EXPERIMENT_KEY = "addExperiment";
	public static final String ADD_WEATHER_KEY = "addWeather";
}
