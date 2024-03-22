package sap.escooters.business_logic_layer;

public class Location {
	private final double latitude;
    private final double longitude;
	
	public Location(double lat, double lon) {
		this.latitude = lat;
		this.longitude = lon;
	}
	
	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
}
