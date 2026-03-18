package com.amazingco;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import es.uva.inf.poo.maps.GPSCoordinate;

/**
 * PackageLocker management system for Amazingco. Copyright 2023, Amazingco.
 * All rights reserved. A PackageLocker is characterized by an identifier, a
 * GPS location, an opening time, a capacity and an operative state. Each
 * PackageLocker contains a variable number of lockers; AmazingCo employees
 * place packages into empty lockers.
 *
 * @author ivamoro
 * @author erisoto
 */

public class PackageLocker extends GroupablePoint {

	private Map<Package, Integer> lockers;
	private Boolean eligible;

	/**
	 * Constructor for PackageLocker.
	 * @param identifier   String representing the package locker identifier
	 * @param coordinates  GPSCoordinate representing the locker location
	 * @param openingTime  LocalTime representing the locker opening time
	 * @param capacity     Integer representing the number of lockers
	 */
	public PackageLocker(String identifier, GPSCoordinate coordinates, LocalTime openingTime,
			Integer capacity) {
		super(identifier, coordinates, openingTime, capacity);
		this.lockers = new HashMap<>();

	}

	/**
	 * Adds a package to this package locker with the provided expiry date.
	 *
	* @param pkg      Package to be added to the locker
	* @param deadline LocalDate representing the storage deadline for the
	*                 package
	 * @throws IllegalArgumentException if the package is null, certified,
	 *                                  unpaid, the locker is not operative,
	 *                                  the package was already picked up, the
	 *                                  date is null, there is no space available,
	 *                                  or a package with the same code already
	 *                                  exists in the locker
	 */
	@Override
	public void addPackage(Package pkg, LocalDate deadline) {
		if (pkg == null) {
			throw new IllegalArgumentException("Package cannot be null");
		}
		if (pkg.isCertified() == true) {
			throw new IllegalArgumentException("Cannot add certified package to locker");
		}
		if (pkg.isPaid() == false) {
			throw new IllegalArgumentException("Package must be paid to be stored in locker");
		}
		if (super.isOperative() == false) {
			throw new IllegalArgumentException("Cannot add package: locker not operative");
		}
		if (pkg.isPickedUp()) {
			throw new IllegalArgumentException("Cannot add package that has already been picked up");
		}
		if (deadline == null) {
			throw new IllegalArgumentException("Expiry date cannot be null");
		}
		if (!isSpaceAvailable()) {
			throw new IllegalArgumentException("No space available");
		}
		if (getPackageInLocker(pkg.getPackageCode()) != null) {
			throw new IllegalArgumentException("Cannot have two packages with the same code in locker");
		}

		pkg.setStorageEndDate(deadline);
		lockers.put(pkg, getNextEmptyLockerNumber());
	}

	/**
	 * Removes the package identified by the given code, using the provided
	 * current date to determine overdue or return behavior.
	 *
	* @param packageCode String representing the package code
	* @param currentDate LocalDate representing the current date for removal
	 * @throws IllegalArgumentException if the locker is not operative, the
	 *                                  package code is null, the package is not
	 *                                  present, or the date is null
	 */
	@Override
	public void removePackage(String packageCode, LocalDate currentDate) {
		if (super.isOperative() == false) {
			throw new IllegalArgumentException("Cannot remove package: locker not operative");
		}
		if (packageCode == null) {
			throw new IllegalArgumentException("Package code cannot be null");
		}
		if (getPackageInLocker(packageCode) == null) {
			throw new IllegalArgumentException("Package is not in locker");
		}
		if (currentDate == null) {
			throw new IllegalArgumentException("Date cannot be null");
		}
		if (!getPackageInLocker(packageCode).isOverdue(currentDate)) {
			getPackageInLocker(packageCode).setOverdue(false);
			lockers.remove(getPackageInLocker(packageCode));
		}
		if (getPackageInLocker(packageCode).isOverdue(currentDate)) {
			getPackageInLocker(packageCode).setOverdue(true);
			lockers.remove(getPackageInLocker(packageCode));
		}
	}

	/**
	 * Returns the number of occupied lockers.
	 *
	 * @return Integer representing the number of occupied lockers
	 */
	public Integer getOccupiedLockers() {
		return lockers.size();
	}

	/**
	 * Returns the number of empty lockers.
	 *
	 * @return Integer representing the number of empty lockers
	 */
	public Integer getEmptyLockers() {
		return super.getCapacity() - getOccupiedLockers();
	}

	/**
	* Sets the capacity (number of lockers) for this PackageLocker.
	*
	* @param capacity Integer representing the number of lockers
	* @throws IllegalArgumentException if capacity is null, negative, or less
	*                                  than the current number of occupied
	*                                  lockers
	 */
	public void setCapacity(Integer capacity) {
		if (capacity == null) {
			throw new IllegalArgumentException("Capacity cannot be null");
		}
		if (capacity < 0) {
			throw new IllegalArgumentException("Capacity cannot be negative");
		}
		if (capacity < lockers.size()) {
			throw new IllegalArgumentException("Capacity cannot be less than the number of stored packages");
		}
		this.capacity = capacity;
	}

	/**
	 * Returns true if there is available space, false otherwise.
	 */
	@Override
	public Boolean isSpaceAvailable() {
		if (lockers.size() >= super.getCapacity()) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the package stored in the locker matching the provided code.
	 *
	 * @param packageCode String representing the package code
	 */
	@Override
	public Package getPackageInLocker(String packageCode) {
		for (Package p : lockers.keySet()) {
			if (p.getPackageCode().equals(packageCode)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Returns the locker number to assign for the next empty locker.
	 *
	 * @return Integer representing the next empty locker number
	 */
	private Integer getNextEmptyLockerNumber() {
		return lockers.size() + 1;
	}

	/**
	 * Returns whether this package locker is eligible for a given package.
	 *
	 * @return Boolean indicating eligibility
	 */
	public Boolean isEligible() {
		return eligible;
	}

	/**
	 * Sets the eligibility flag for this locker.
	 *
	 * @param eligible Boolean indicating eligibility
	 * @throws IllegalArgumentException if elegible is null
	 */
	protected void setEligible(Boolean eligible) {
		if (eligible == null) {
			throw new IllegalArgumentException("Eligible flag cannot be null");
		}
		this.eligible = eligible;
	}

	/**
	 * @return always false, since package lockers do not accept certified
	 *         deliveries
	 */
	@Override
	public Boolean isCertified() {
		return false;
	}

	/**
	 * @return always false, since package lockers do not accept cash-on-delivery
	 */
	@Override
	public Boolean isCashOnDelivery() {
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(lockers, eligible);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof PackageLocker)) {
			return false;
		}
		PackageLocker other = (PackageLocker) obj;
		return Objects.equals(lockers, other.lockers) && Objects.equals(eligible, other.eligible);
	}

	@Override
	public String toString() {
		return "PackageLocker [lockers=" + lockers + ", eligible=" + eligible + "]";
	}
}