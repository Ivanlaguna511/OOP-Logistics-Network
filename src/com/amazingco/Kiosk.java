package com.amazingco;

import java.time.LocalDate;
import java.time.LocalTime;
import es.uva.inf.poo.maps.GPSCoordinate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Amazingco Kiosk system. Copyright 2023, Amazingco. All rights reserved.
 * A Kiosk requires an identifier, a GPS coordinate, an opening time and a
 * capacity. A kiosk pickup point is initialized with the number of packages it
 * can store; this capacity can be modified. When a cash-on-delivery package is
 * picked up at a kiosk, the package is marked as paid and the collected
 * amounts are accumulated so they can be later transferred to AmazingCo. The
 * Kiosk class provides a method to pay AmazingCo which resets the collected
 * amount to zero.
 *
 * @author ivamoro
 * @author erisoto
 */

public class Kiosk extends GroupablePoint {

	private List<Package> packages;
	private Integer collectedAmount;

	/**
	 * Constructor for Kiosk.
	 * @param identifier   String representing the kiosk identifier
	 * @param coordinates  GPSCoordinate representing the kiosk location
	 * @param openingTime  LocalTime representing the kiosk opening time
	 * @param capacity     Integer representing the kiosk capacity
	 */
	public Kiosk(String identifier, GPSCoordinate coordinates, LocalTime openingTime, Integer capacity) {
		super(identifier, coordinates, openingTime, capacity);
		this.packages = new ArrayList<Package>();
		this.collectedAmount = 0;
	}

	/**
	 * @throws IllegalArgumentException if the capacity is null, less than one, or
	 *                                  less than the current number of stored
	 *                                  packages
	 */
	@Override
	public void setCapacity(Integer capacity) {
		if (capacity == null) {
			throw new IllegalArgumentException("Capacity cannot be null");
		}
		if (capacity < 1) {
			throw new IllegalArgumentException("Capacity must be at least 1");
		}
		if (capacity < packages.size()) {
			throw new IllegalArgumentException("Capacity cannot be less than number of stored packages");
		}
		this.capacity = capacity;
	}

	/**
	 * Adds a package to the kiosk with the provided expiry date.
	 *
	 * @throws IllegalArgumentException if the kiosk is not operative, the
	 *                                  package is null, the package is
	 *                                  certified (kiosk does not accept
	 *                                  certified packages), the package is
	 *                                  already picked up, the expiry date is
	 *                                  null, there is no space available, or a
	 *                                  package with the same code already
	 *                                  exists in the kiosk
	 */
	@Override
	public void addPackage(Package pkg, LocalDate deadline) {
		if (super.isOperative() == false) {
			throw new IllegalArgumentException("Cannot add package: kiosk is not operative");
		}
		if (pkg == null) {
			throw new IllegalArgumentException("Package cannot be null");
		}
		if (pkg.isCertified() == true) {
			throw new IllegalArgumentException("Cannot add certified package to kiosk");
		}
		if (pkg.isPickedUp()) {
			throw new IllegalArgumentException("Cannot add package that has already been picked up");
		}
		if (deadline == null) {
			throw new IllegalArgumentException("Expiry date cannot be null");
		}
		if (!isSpaceAvailable()) {
			throw new IllegalArgumentException("No space available to store the package");
		}
		if (getPackageInLocker(pkg.getPackageCode()) != null) {
			throw new IllegalArgumentException("A package with that code already exists in the kiosk");
		}
		pkg.setStorageEndDate(deadline);
		pkg.setReturned(false);
		packages.add(pkg);

	}

	/**
	 * Removes a package identified by its code, using the provided current
	 * date to determine overdue or return behavior.
	 *
	 * @throws IllegalArgumentException if the kiosk is not operative, the
	 *                                  package code is null, the package is not
	 *                                  present in the kiosk, or the provided
	 *                                  date is null
	 */
	@Override
    public void removePackage(String packageCode, LocalDate currentDate) {
        if (super.isOperative() == false) {
            throw new IllegalArgumentException("Cannot remove package: kiosk is not operative");
        }
        if (packageCode == null) {
            throw new IllegalArgumentException("Package code cannot be null");
        }
        
        Package pkg = getPackageInLocker(packageCode);
        if (pkg == null) {
            throw new IllegalArgumentException("Package is not in kiosk");
        }
        if (currentDate == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        
        // ¡Al guardar la variable "pkg" nos libramos de la doble comprobación y del código muerto!
        if (!pkg.isPaid()) {
            if (pkg.isOverdue(currentDate)) {
                pkg.setReturned(true);
                pkg.setOverdue(true);
                packages.remove(pkg);
            } else {
                addToCollectedAmount(pkg);
                pkg.setPickedUp(true);
                pkg.setOverdue(false);
                packages.remove(pkg);
            }
        }
        
        if (pkg.isPaid()) {
            if (pkg.isOverdue(currentDate)) {
                pkg.setReturned(true);
                pkg.setOverdue(true);
                packages.remove(pkg);
            } else {
                pkg.setOverdue(false);
                pkg.setPickedUp(true);
                packages.remove(pkg);
            }
        }
    }

	/**
	 * Checks whether there is available space in the kiosk.
	 */
	@Override
	public Boolean isSpaceAvailable() {
		return packages.size() < super.getCapacity();
	}

	/**
	 * Returns the package stored in the kiosk matching the provided code.
	 */
	@Override
	public Package getPackageInLocker(String packageCode) {
		for (Package pkg : packages) {
			if (pkg.getPackageCode().equals(packageCode)) {
				return pkg;
			}
		}
		return null;
	}

	/**
	 * Returns the accumulated collected amount for this kiosk.
	 *
	 * @return Integer containing the accumulated collected amount
	 */
	public Integer getCollectedAmount() {
		return collectedAmount;
	}

	/**
	 * Adds the package price to the kiosk's collected amount and marks the
	 * package as paid.
	 *
	 * @param pkg Package whose price will be added to the collected amount
	 */
	private void addToCollectedAmount(Package pkg) {
		collectedAmount = getCollectedAmount() + pkg.getPrice();
		pkg.setPaid(true);
	}

	/**
	 * Performs the payment to AmazingCo by resetting the collected amount to
	 * zero.
	 */
	public void payAmazingCo() {
		collectedAmount = 0;
	}

	/**
	 * @return always false, since a kiosk does not accept certified packages
	 */
	@Override
	public Boolean isCertified() {
		return false;
	}

	@Override
	public Boolean isCashOnDelivery() {
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(packages, collectedAmount);
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
		if (!(obj instanceof Kiosk)) {
			return false;
		}
		Kiosk other = (Kiosk) obj;
		return Objects.equals(packages, other.packages) && Objects.equals(collectedAmount, other.collectedAmount);
	}

	@Override
	public String toString() {
		return "Kiosk [packages=" + packages + ", collectedAmount=" + collectedAmount + "]";
	}
}