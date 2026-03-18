package com.amazingco;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Package entity used by Amazingco systems. Holds package code, storage end
 * date, price and flags for exceeded deadline, certified delivery, payment,
 * pickup and return status. For certified deliveries the package contains a
 * list of allowed identifiers (IDs) authorized to collect it. The package
 * code must have ten digits: the first nine are digits and the tenth is the
 * remainder of the sum of the first nine digits modulo 10.
 *
 * <p>Packages that are not paid may only be delivered at points supporting
 * cash-on-delivery. Certified deliveries may only be performed at PostOffice
 * locations. The class provides two constructors: one for certified packages
 * (with authorized IDs) and another for non-certified packages.
 *
 * @author ivamoro
 * @author erisoto
 */

public class Package {
	private String packageCode;
	private LocalDate storageEndDate;
	private Boolean pickedUp;
	private Boolean returned;
	private Boolean overdue;
	private Integer price;
	private Boolean paid;
	private Boolean certified;
	private ArrayList<String> identifications;

	/**
	 * Constructor for non-certified packages.
	 *
	* @param price        Integer representing the package price
	* @param paid         Boolean indicating whether the package has been paid
	* @param packageCode  String representing the package code
	 */
	public Package(Integer price, Boolean paid, String packageCode) {
		setPrice(price);
		setPaid(paid);
		setCertified(false);
		setPackageCode(packageCode);
		this.pickedUp = false;
		this.returned = false;
		this.overdue = false;
	}

	/**
	 * Constructor for certified packages.
	 *
	* @param price           Integer representing the package price
	* @param paid            Boolean indicating whether the package has been paid
	* @param identifications Array of strings of IDs authorized to pick up the
	*                        package at a PostOffice
	* @param packageCode     String representing the package code
	 */
	public Package(Integer price, Boolean paid, ArrayList<String> identifications, String packageCode) {
		setPrice(price);
		setPaid(paid);
		setIdentifications(identifications);
		setCertified(true);
		setPackageCode(packageCode);
	}

	/**
	 * Returns the list of identifiers authorized to pick up this package at a
	 * PostOffice.
	 *
	 * @return Array of strings of authorized identifiers
	 */
	public ArrayList<String> getIdentifications() {
		return identifications;
	}

	/**
	 * Sets the list of authorized identifiers for certified packages.
	 *
	 * @param identifications Array of strings of identifiers
	 * @throws IllegalArgumentException if identifications is null
	 */
	private void setIdentifications(ArrayList<String> identifications) {
		if (identifications == null) {
			throw new IllegalArgumentException("Identifications cannot be null");
		} else {
			this.identifications = identifications;
		}
	}

	/**
	 * Adds an identifier to the list of authorized IDs for this package.
	 *
	 * @param id String identifier to add
	 * @throws IllegalArgumentException if id is null, the package has already
	 *                                  been picked up, or the package is not certified
	 */
	public void addIdentification(String id) {
		if (id == null) {
			throw new IllegalArgumentException("ID cannot be null");
		}
		if (pickedUp == true) {
			throw new IllegalArgumentException("Cannot add ID: package already picked up");
		}
		if (identifications == null) {
			throw new IllegalArgumentException("Package is not certified; cannot add identifications");
		}
		identifications.add(id);

	}

	/**
	 * Returns whether this package is certified.
	 *
	 * @return Boolean indicating certification status
	 */
	public Boolean isCertified() {
		return certified;
	}

	/**
	 * Sets the certified flag for this package.
	 *
	 * @param certificado Boolean indicating certification
	 * @throws IllegalArgumentException if certificado is null
	 */
	private void setCertified(Boolean certified) {
		if (certified == null) {
			throw new IllegalArgumentException("Certified flag cannot be null");
		} else {
			this.certified = certified;
		}
	}

	/**
	 * Returns the package price.
	 *
	 * @return Integer representing the price
	 */
	public Integer getPrice() {
		return price;
	}

	/**
	 * Sets the package price.
	 *
	 * @param precio Integer representing the price
	 * @throws IllegalArgumentException if precio is null or negative
	 */
	private void setPrice(Integer price) {
		if (price == null) {
			throw new IllegalArgumentException("Price cannot be null");
		} else if (price < 0) {
			throw new IllegalArgumentException("Price cannot be negative");
		} else {
			this.price = price;
		}
	}

	/**
	 * Returns whether the package has been paid.
	 *
	 * @return Boolean indicating payment status
	 */
	public Boolean isPaid() {
		return paid;
	}

	/**
	 * Sets the paid flag for this package.
	 *
	 * @param paid Boolean indicating payment status
	 * @throws IllegalArgumentException if paid is null
	 */
	protected void setPaid(Boolean paid) {
		if (paid == null) {
			throw new IllegalArgumentException("Paid flag cannot be null");
		} else {
			this.paid = paid;
		}
	}

	/**
	 * Returns the package code.
	 *
	 * @return String representing the package code
	 */
	public String getPackageCode() {
		return packageCode;
	}

	/**
	 * Sets the package code after validating its format: must be 10 digits and
	 * the last digit must equal the sum of the first nine digits modulo 10.
	 *
	 * @param codigoPaquete String representing the package code
	 * @throws IllegalArgumentException if the code is null, does not have 10
	 *                                  digits, or fails the checksum rule
	 */
	private void setPackageCode(String packageCode) {
		if (packageCode == null) {
			throw new IllegalArgumentException("Package code cannot be null");
		}
		if (!hasValidCodeDigits(packageCode)) {
			throw new IllegalArgumentException("Package code must have 10 digits");
		}
		if ((!isChecksumValid(packageCode))) {
			throw new IllegalArgumentException(
					"Package code does not satisfy checksum (last digit must be sum of first nine mod 10)");
		}
		this.packageCode = packageCode;

	}

	/**
	 * Returns the storage end date for this package.
	 *
	 * @return LocalDate representing the storage end date
	 */
	public LocalDate getStorageEndDate() {
		return storageEndDate;
	}

	/**
	 * Sets the storage end date for this package.
	 *
	 * @param storageEndDate LocalDate representing the storage end date
	 * @throws IllegalArgumentException if storageEndDate is null
	 */
	protected void setStorageEndDate(LocalDate storageEndDate) {
		if (storageEndDate == null) {
			throw new IllegalArgumentException("Storage end date cannot be null");
		}
		this.storageEndDate = storageEndDate;
	}

	/**
	 * Returns whether the package has been picked up.
	 *
	 * @return Boolean indicating pickup status
	 */
	public Boolean isPickedUp() {
		return pickedUp;
	}

	/**
	 * Sets the picked-up flag for this package.
	 *
	 * @param pickedUp Boolean indicating pickup status
	 * @throws IllegalArgumentException if pickedUp is null or the package has
	 *                                  already been returned
	 */
	protected void setPickedUp(Boolean pickedUp) {
		if (pickedUp == null) {
			throw new IllegalArgumentException("Picked-up flag cannot be null");
		}
		if (returned == true) {
			throw new IllegalArgumentException("Package was returned and cannot be picked up");
		}
		this.pickedUp = pickedUp;
	}

	/**
	 * Returns whether the package has been returned.
	 *
	 * @return Boolean indicating return status
	 */
	public Boolean isReturned() {
		return returned;
	}

	/**
	 * Sets the returned flag for this package.
	 *
	 * @param returned Boolean indicating return status
	 * @throws IllegalArgumentException if returned is null or the package has
	 *                                  already been picked up
	 */
	protected void setReturned(Boolean returned) {
		if (returned == null) {
			throw new IllegalArgumentException("Returned flag cannot be null");
		}
		if (pickedUp == true) {
			throw new IllegalArgumentException("Package was picked up and cannot be returned");
		}
		this.returned = returned;
	}

	/**
	 * Returns whether the package has exceeded its storage deadline compared to
	 * the provided date.
	 *
	 * @param date LocalDate to compare against
	 * @throws IllegalArgumentException if date is null
	 * @return Boolean indicating whether the package is overdue
	 */
	public Boolean isOverdue(LocalDate date) {
		if (date == null) {
			throw new IllegalArgumentException("Provided date cannot be null");
		}
		return storageEndDate.isBefore(date);
	}

	/**
	 * Sets the overdue flag for this package.
	 *
	 * @param overdue Boolean indicating overdue status
	 */
	protected void setOverdue(Boolean overdue) {
		if (overdue == null) {
			throw new IllegalArgumentException("Overdue flag cannot be null");
		}
		this.overdue = overdue;
	}

	/**
	 * Validates whether the package code contains the required digits.
	 *
	 * @param packageCode String representing the package code
	 * @return Boolean indicating whether the code contains valid digits
	 */
	private Boolean hasValidCodeDigits(String packageCode) {
		if (packageCode.length() != 10) {
			throw new IllegalArgumentException("Package code must have 10 digits");
		}
		int digits = 0;
		for (int i = 0; i < packageCode.length(); i++) {
			if (Character.isDigit(packageCode.charAt(i))) {
				digits++;
			} else {
				throw new IllegalArgumentException("Package code must contain only digits");
			}
		}
		return digits == 10;
	}

	/**
	 * Checks the checksum rule: last digit equals sum of first nine digits
	 * modulo 10.
	 *
	 * @param codigoPaquete String representing the package code
	 * @return Boolean indicating whether the checksum is valid
	 */
	private Boolean isChecksumValid(String packageCode) {
		int sum = 0;

		for (int i = 0; i < packageCode.length() - 1; i++) {
			char ch = packageCode.charAt(i);
			int digit = Character.getNumericValue(ch);
			sum = sum + digit;
		}
		int lastDigit = Character.getNumericValue(packageCode.charAt(packageCode.length() - 1));
		return (sum % 10) == lastDigit;
	}

	@Override
	public int hashCode() {
		return Objects.hash(certified, packageCode, returned, overdue, storageEndDate, identifications,
			paid, price, pickedUp);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Package)) {
			return false;
		}
		Package other = (Package) obj;
		return Objects.equals(certified, other.certified) && Objects.equals(packageCode, other.packageCode)
			&& Objects.equals(returned, other.returned) && Objects.equals(overdue, other.overdue)
			&& Objects.equals(storageEndDate, other.storageEndDate)
			&& Objects.equals(identifications, other.identifications) && Objects.equals(paid, other.paid)
			&& Objects.equals(price, other.price) && Objects.equals(pickedUp, other.pickedUp);
	}

	@Override
	public String toString() {
		return "Package [packageCode=" + packageCode + ", storageEndDate=" + storageEndDate + ", pickedUp="
			+ pickedUp + ", returned=" + returned + ", overdue=" + overdue + ", price=" + price + ", paid="
			+ paid + ", certified=" + certified + ", identifications=" + identifications + "]";
	}
}