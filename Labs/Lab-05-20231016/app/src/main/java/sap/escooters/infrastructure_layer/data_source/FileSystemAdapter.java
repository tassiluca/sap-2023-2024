package sap.escooters.infrastructure_layer.data_source;

import io.vertx.core.json.JsonObject;
import sap.escooters.business_logic_layer.DataSourceException;
import sap.escooters.business_logic_layer.DataSourcePort;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class FileSystemAdapter implements DataSourcePort {

	private final String USERS_PATH = "users";
	private final String ESCOOTERS_PATH = "escooters";
	private final String RIDES_PATH = "rides";
	
	private final String dbaseFolder;
	
	public FileSystemAdapter(String dbaseFolder) {
		this.dbaseFolder =  dbaseFolder;
	}

	public void init() {
		makeDir(dbaseFolder);
		makeDir(dbaseFolder + File.separator + USERS_PATH);
		makeDir(dbaseFolder + File.separator + ESCOOTERS_PATH);
		makeDir(dbaseFolder + File.separator + RIDES_PATH);
	}	

	@Override
	public void saveUser(JsonObject user) throws DataSourceException {
		this.saveObj(USERS_PATH, user.getString("id"), user);
	}

	@Override
	public void saveEScooter(JsonObject escooter) throws DataSourceException {
		this.saveObj(ESCOOTERS_PATH, escooter.getString("id"), escooter);
	}

	@Override
	public void saveRide(JsonObject ride) throws DataSourceException {
		this.saveObj(RIDES_PATH, ride.getString("id"), ride);
	}

	private void saveObj(String db, String id, JsonObject obj) throws DataSourceException {
		try {									
			FileWriter fw = new FileWriter(dbaseFolder + File.separator + db + File.separator + id + ".json");
			java.io.BufferedWriter wr = new BufferedWriter(fw);				
			wr.write(obj.encodePrettily());
			wr.flush();
			fw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new DataSourceException(ex.getMessage());
		}
	}
	
	private void makeDir(String name) {
		try {
			File dir = new File(name);
			if (!dir.exists()) {
				dir.mkdir();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
