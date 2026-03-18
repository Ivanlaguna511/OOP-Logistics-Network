package com.amazingco;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.uva.inf.poo.maps.GPSCoordinate;

/**
 * Post office pickup point for Amazingco systems.
 *
 * PostOffice accepts certified deliveries and implements
 * {@link IdentificationRegistry}. It is conceptually unlimited in capacity
 * and always reports available space. It accumulates cash-on-delivery
 * collections similarly to Kiosk and provides a method to reset the collected
 * amount when paid to AmazingCo.
 *
 * @author ivamoro
 * @author erisoto
 */
public class PostOffice extends PickingPoint implements IdentificationRegistry {
	private Integer collectedAmount;
	private List<Package> registeredPackages;
	private List<Package> storedPackages;
	private List<String> registeredIds;

	/**
	 * Constructs a PostOffice with the given identifier, coordinates and opening time.
	 * @param identifier   String representing the PostOffice identifier
	 * @param coordinates  GPSCoordinate representing the PostOffice location
	 * @param openingTime  LocalTime representing the PostOffice opening time
	 */
	public PostOffice(String identifier, GPSCoordinate coordinates, LocalTime openingTime) {
		super(identifier, coordinates, openingTime);
		this.registeredPackages = new ArrayList<Package>();
		this.storedPackages = new ArrayList<Package>();
		this.registeredIds = new ArrayList<String>();
		this.collectedAmount = 0;
	}

	/**
	 * Returns whether a pickup has been registered for the given package code.
	 *
	 * @param packageCode String package code
	 * @throws IllegalArgumentException if packageCode is null
	 */
	public boolean isPackagePickupRegistered(String packageCode) {
		if (packageCode == null) {
			throw new IllegalArgumentException("Package code cannot be null");
		}
		for (Package p : registeredPackages) {
			if (p.getPackageCode().equals(packageCode)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the registered package matching the provided package code.
	 *
	 * @param packageCode String package code
	 * @throws IllegalArgumentException if packageCode is null or no matching
	 *                                  registered package exists
	 */
	public Package getPackageRegistered(String packageCode) {
		if (packageCode == null) {
			throw new IllegalArgumentException("Package code cannot be null");
		}
		if (isPackagePickupRegistered(packageCode)) {
			for (Package p : registeredPackages) {
				if (p.getPackageCode().equals(packageCode)) {
					return p;
				}
			}
		}
		throw new IllegalArgumentException("Provided package code does not match any registered package at PostOffice");
	}

	/**
	 * Returns the registered identifier (first authorized ID) for the given
	 * package code.
	 *
	 * @param packageCode String package code
	 * @throws IllegalArgumentException if packageCode is null or no matching
	 *                                  registered package exists
	 */
	public String getRegisteredIdFor(String packageCode) {
		if (packageCode == null) {
			throw new IllegalArgumentException("Package code cannot be null");
		}
		if (isPackagePickupRegistered(packageCode)) {
			for (Package p : registeredPackages) {
				if (p.getPackageCode().equals(packageCode)) {
					return p.getIdentifications().get(0);
				}
			}
		}
		throw new IllegalArgumentException("Provided package code does not match any registered package at PostOffice");
	}

	/**
	 * Returns the stored pickup date for the registered package matching the
	 * provided package code.
	 *
	 * @param packageCode String package code
	 * @throws IllegalArgumentException if no registered package matches the code
	 */
	public LocalDate getPickupDateFor(String packageCode) {
		if (isPackagePickupRegistered(packageCode)) {
			for (Package p : registeredPackages) {
				if (p.getPackageCode().equals(packageCode)) {
					return p.getStorageEndDate(); // pickup date stored in the package
				}
			}
		}
		throw new IllegalArgumentException("Provided package code does not match any registered package at PostOffice");
	}

	/**
	 * Registers the pickup of a certified package at this PostOffice.
	 *
	 * @param p          Package to register
	 * @param dni        String identifier provided for pickup
	 * @param pickupDate LocalDate of pickup
	 * @throws IllegalArgumentException if the post office is not operative,
	 *                                  the package is null, dni is null, pickup
	 *                                  date is null, or the pickup date is
	 *                                  invalid
	 */
	public void registerCertifiedPackagePickup(Package p, String dni, LocalDate pickupDate) {
		if (super.isOperative() == false) {
			throw new IllegalArgumentException("Cannot register pickup: PostOffice is not operative");
		}
		if (p == null) {
			throw new IllegalArgumentException("Package cannot be null");
		}
		if (dni == null) {
			throw new IllegalArgumentException("ID cannot be null");
		}
		if (pickupDate == null) {
			throw new IllegalArgumentException("Pickup date cannot be null");
		}
		if (!p.getStorageEndDate().isAfter(pickupDate)) {
			throw new IllegalArgumentException("Pickup date is not valid (must be before storage end date)");
		}

		for (String id : p.getIdentifications()) {
			if (dni.equals(id) && !isPackagePickupRegistered(p.getPackageCode()) && p.isCertified()) {
				p.setPickedUp(true);
				p.setPaid(true);
				p.setStorageEndDate(pickupDate);
				this.setCollectedAmount(collectedAmount + p.getPrice());
				registeredPackages.add(p);
				registeredIds.add(dni);
				storedPackages.remove(p);
			}
		}
	}

	/**
	 * Adds a certified package to this PostOffice with the given storage
	 * deadline.
	 *
	* @param pkg      Package to add
	* @param deadline LocalDate representing storage deadline
	 */
	public void addPackage(Package pkg, LocalDate deadline) {
		if (super.isOperative() == false) {
			throw new IllegalArgumentException("Cannot add package: post office is not operative");
		}
		if (pkg == null) {
			throw new IllegalArgumentException("Package cannot be null");
		}
		if (pkg.isCertified() == false) {
			throw new IllegalArgumentException("Only certified packages can be added to PostOffice");
		}
		if (pkg.isPickedUp()) {
			throw new IllegalArgumentException("Cannot add package that has already been picked up");
		}
		if (deadline == null) {
			throw new IllegalArgumentException("Expiry date cannot be null");
		}
		if (isPackageInPostOffice(pkg.getPackageCode())) {
			throw new IllegalArgumentException("Duplicate package codes are not allowed");
		}
		pkg.setStorageEndDate(deadline);
		pkg.setReturned(false);
		storedPackages.add(pkg);

	}

	/**
	 * Returns whether a package with the given code is present in this PostOffice.
	 *
	* @param packageCode String package code
	 * @return Boolean indicating presence
	 */
	private Boolean isPackageInPostOffice(String packageCode) {
		for (Package p : storedPackages) {
			if (p.getPackageCode().equals(packageCode)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the amount collected by this PostOffice.
	 *
	 * @return Integer representing collected amount
	 */
	public Integer getCollectedAmount() {
		return collectedAmount;
	}

	/**
	* Sets the collected amount for this PostOffice.
	*
	* @param collectedAmount Integer collected amount
	* @throws IllegalArgumentException if collectedAmount is null or negative
	 */
	private void setCollectedAmount(Integer collectedAmount) {
		if (collectedAmount == null) {
			throw new IllegalArgumentException("Collected amount cannot be null");
		} else if (collectedAmount < 0) {
			throw new IllegalArgumentException("Collected amount cannot be negative");
		} else {
			this.collectedAmount = collectedAmount;
		}
	}

	/**
	 * Processes a cash-on-delivery pickup: marks the package as paid and picked
	 * up, updates collected amount and removes it from storage.
	 *
	 * @param p Package being picked up
	 */
	public void collectPackageAsCashOnDelivery(Package p) {
		p.setPaid(true);
		p.setPickedUp(true);
		setCollectedAmount(this.collectedAmount + p.getPrice());
		storedPackages.remove(p);
	}

	/**
	 * Performs the payment to AmazingCo by resetting the collected amount to
	 * zero.
	 */
	public void payAmazingCo() {
		collectedAmount = 0;
	}

	@Override
	public Boolean isSpaceAvailable() {
		return true;
	}

	@Override
	public Boolean isCertified() {
		return true;
	}

	@Override
	public Boolean isCashOnDelivery() {
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(registeredPackages, collectedAmount, registeredIds, storedPackages);
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
		if (!(obj instanceof PostOffice)) {
			return false;
		}
		PostOffice other = (PostOffice) obj;
		return Objects.equals(registeredPackages, other.registeredPackages)
			&& Objects.equals(collectedAmount, other.collectedAmount) && Objects.equals(registeredIds, other.registeredIds)
			&& Objects.equals(storedPackages, other.storedPackages);
	}

	@Override
	public String toString() {
		return "PostOffice [collectedAmount=" + collectedAmount + ", registeredPackages=" + registeredPackages
			+ ", storedPackages=" + storedPackages + ", registeredIds=" + registeredIds + "]";
	}
}