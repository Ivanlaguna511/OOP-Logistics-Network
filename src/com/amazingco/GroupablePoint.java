package com.amazingco;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import es.uva.inf.poo.maps.GPSCoordinate;

/**
 * Amazingco GroupablePoints system. Copyright 2023, Amazingco.
 * All rights reserved. A GroupablePoint requires an identifier, a GPS
 * coordinate, an opening time and a capacity. GroupablePoint is an abstract
 * class and a generalization of PackageLocker and Kiosk.
 *
 * @author ivamoro
 * @author erisoto
 */

public abstract class GroupablePoint extends PickingPoint {
	Integer capacity;

	/**
	 * Constructor for GroupablePoint.
	* @param identifier   String representing the identifier of the groupable
	*                     point
	* @param coordinates  GPSCoordinate representing the location of the
	*                     groupable point
	* @param openingTime  LocalTime representing the opening time of the
	*                     groupable point
	* @param capacity     Integer representing the capacity of the
	*                     groupable point
	 */
	public GroupablePoint(String identifier, GPSCoordinate coordinates, LocalTime openingTime,
			Integer capacity) {
		super(identifier, coordinates, openingTime);
		if (capacity == null) {
			throw new IllegalArgumentException("capacity cannot be null");
		}
		if (capacity < 1) {
			throw new IllegalArgumentException("capacity must be at least 1");
		}
		this.capacity = capacity;
	}

	/**
	 * Sets the capacity of this groupable point.
	 *
	 * @param capacity Integer representing the container capacity
	 */
	public abstract void setCapacity(Integer capacity);

	/**
	 * Removes a package from this groupable point according to the provided
	 * package code and deadline date when it should be returned to the central
	 * office.
	 *
	 * @param packageCode String representing the package code
	 * @param deadline    LocalDate representing the deadline for return to the
	 *                    central office
	 */
	public abstract void removePackage(String packageCode, LocalDate deadline);

	/**
	 * Returns the package stored in the container linked to the provided
	 * package code.
	 *
	 * @param packageCode String representing the package code
	 * @return the Package stored in the locker matching the given code, or
	 *         null if not found
	 */
	public abstract Package getPackageInLocker(String packageCode);

	/**
	 * Adds the given package to the container with an associated deadline
	 * date.
	 *
	 * @param pkg      Package to be added to the container
	 * @param deadline LocalDate representing the deadline
	 */
	public abstract void addPackage(Package pkg, LocalDate deadline);

	/**
	 * Returns the capacity of this container.
	 *
	 * @return Integer representing the container capacity
	 */
	public Integer getCapacity() {
		return capacity;
	}

	@Override
	public int hashCode() {
		return Objects.hash(capacity);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof GroupablePoint)) {
			return false;
		}
		GroupablePoint other = (GroupablePoint) obj;
		return Objects.equals(capacity, other.capacity);
	}

	@Override
	public String toString() {
		return "GroupablePoint [capacity=" + capacity + "]";
	}
}