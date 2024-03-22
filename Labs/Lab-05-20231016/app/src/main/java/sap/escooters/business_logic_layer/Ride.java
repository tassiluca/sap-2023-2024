package sap.escooters.business_logic_layer;

import java.util.Date;
import java.util.Optional;

import io.vertx.core.json.JsonObject;

public class Ride {

	private final Date startedDate;
	private Optional<Date> endDate;
	private final User user;
	private final EScooter scooter;
	private boolean ongoing;
	private final String id;
	
	public Ride(String id, User user, EScooter scooter) {
		this.id = id;
		this.startedDate = new Date();
		this.endDate = Optional.empty();
		this.user = user;
		this.scooter = scooter;
		this.ongoing = true;
	}
	
	public String getId() {
		return id;
	}
	
	public void end() {
		this.endDate = Optional.of(new Date());
		this.ongoing = false;
		save();
	}

	public Date getStartedDate() {
		return startedDate;
	}

	public boolean isOngoing() {
		return this.ongoing;
	}
	
	public Optional<Date> getEndDate() {
		return endDate;
	}

	public User getUser() {
		return user;
	}

	public EScooter getEScooter() {
		return scooter;
	}

	public void save() {
		try {
			DomainModelImpl.getDataSourcePort().saveRide(toJson());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public JsonObject toJson() {
		JsonObject rideObj = new JsonObject();
		rideObj.put("id", this.getId());
		rideObj.put("userId", this.getUser().getId());
		rideObj.put("escooterId", this.getEScooter().getId());
		rideObj.put("startDate", this.getStartedDate().toString());
		Optional<Date> endDate = this.getEndDate();
		if (endDate.isPresent()) {
			rideObj.put("endDate", endDate.get().toString());			
		} else {
			rideObj.putNull("location");			
		}
		return rideObj;
	}
}
