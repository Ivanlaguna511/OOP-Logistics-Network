package com.amazingco;

import java.time.LocalTime;
import java.util.Objects;

import es.uva.inf.poo.maps.GPSCoordinate;

/**
 * Picking point abstraction for Amazingco systems.
 *
 * Represents a pickup location with an identifier, GPS coordinates and an
 * opening time. Subclasses include PackageLocker, Kiosk, PickingPointHub and
 * PostOffice.
 *
 * @author ivamoro
 * @author erisoto
 */
public abstract class PickingPoint {

	private String identifier;
	private GPSCoordinate coordinates;
	private Boolean operative;
	private LocalTime openingTime;

	/**
	 * Constructor for PickingPoint.
	 * @param identifier   String representing the Picking Point identifier
	 * @param coordinates  GPSCoordinate representing the Picking Point location
	 * @param openingTime  LocalTime representing the Picking Point opening time
	 */
	public PickingPoint(String identifier, GPSCoordinate coordinates, LocalTime openingTime) {
		setIdentifier(identifier);
		setCoordinates(coordinates);
		setOpeningTime(openingTime);
		this.operative = true;
	}

	/**
	 * Returns whether there is available space at this picking point.
	 *
	 * @return Boolean indicating availability
	 */
	public abstract Boolean isSpaceAvailable();

	/**
	 * Returns whether this picking point accepts certified deliveries.
	 *
	 * @return Boolean indicating certified-delivery support
	 */
	public abstract Boolean isCertified();

	/**
	 * Returns whether this picking point accepts cash-on-delivery packages.
	 *
	 * @return Boolean indicating cash-on-delivery support
	 */
	public abstract Boolean isCashOnDelivery();

	/**
	 * Sets the identifier for this picking point.
	 *
	 * @param identifier String identifier
	 * @throws IllegalArgumentException if identifier is null
	 */
	private void setIdentifier(String identifier) {
		if (identifier == null) {
			throw new IllegalArgumentException("Identifier cannot be null");
		}
		this.identifier = identifier;
	}

	/**
	 * Returns the identifier of this picking point.
	 *
	 * @return String identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Sets the GPS coordinates for this picking point.
	 *
	 * @param coordinates GPSCoordinate representing location
	 */
	private void setCoordinates(GPSCoordinate coordinates) {
		if (coordinates == null) {
			throw new IllegalArgumentException("Coordinates cannot be null");
		}
		this.coordinates = coordinates;
	}

	/**
	 * Returns the GPS coordinates of this picking point.
	 *
	 * @return GPSCoordinate location
	 */
	public GPSCoordinate getCoordinates() {
		return coordinates;
	}

	/**
	 * Sets the opening time for this picking point.
	 *
	 * @param openingTime LocalTime representing opening time
	 * @throws IllegalArgumentException if openingTime is null
	 */
	private void setOpeningTime(LocalTime openingTime) {
		if (openingTime == null) {
			throw new IllegalArgumentException("Opening time cannot be null");
		}
		this.openingTime = openingTime;

	}

	/**
	 * Returns the opening time of this picking point.
	 *
	 * @return LocalTime opening time
	 */
	public LocalTime getOpeningTime() {
		return openingTime;
	}

	/**
	 * Sets whether this picking point is operative.
	 *
	 * @param operative Boolean indicating operative status
	 * @throws IllegalArgumentException if operative is null
	 */
	public void setOperative(Boolean operative) {
		if (operative == null) {
			throw new IllegalArgumentException("Operative flag cannot be null");
		}
		this.operative = operative;
	}

	/**
	 * Returns whether this picking point is operative.
	 *
	 * @return Boolean indicating operative status
	 */
	public Boolean isOperative() {
		return operative;
	}

	@Override
	public int hashCode() {
		return Objects.hash(coordinates, openingTime, identifier, operative);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof PickingPoint)) {
			return false;
		}
		PickingPoint other = (PickingPoint) obj;
		return Objects.equals(coordinates, other.coordinates) && Objects.equals(openingTime, other.openingTime)
				&& Objects.equals(identifier, other.identifier) && Objects.equals(operative, other.operative);
	}

	@Override
	public String toString() {
		return "PickingPoint [identifier=" + identifier + ", coordinates=" + coordinates + ", operative="
			+ operative + ", openingTime=" + openingTime + "]";
	}
}