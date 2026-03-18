package com.amazingco;

import java.time.LocalDate;

/**
 * Interface for managing package pickup registrations in the Amazingco system.
 *
 * This registry allows checking if a package pickup has been registered, retrieving
 * details of registered pickups, and registering new certified pickups. It ensures
 * that only valid and authorized pickups are recorded, maintaining the integrity
 * of the pickup records.
 *
 * @author ivamoro
 * @author erisoto
 */
public interface IdentificationRegistry {
	
	/**
	 * Check whether a pickup for the given package code has been registered.
	 *
	 * @param packageCode package identifier
	 * @return {@code true} when a pickup record for {@code packageCode} exists,
	 *         {@code false} otherwise
	 */
	public boolean isPackagePickupRegistered(String packageCode);

	/**
	 * Returns the registered package for the given code.
	 *
	 * @param packageCode package identifier; requires {@code isPackageRegistered(packageCode)}
	 * @return non-null registered {@code Package}
	 * @throws IllegalArgumentException or AssertionError when
	 *                                  {@code !isPackageRegistered(packageCode)}
	 */
	public Package getPackageRegistered(String packageCode);

	/**
	 * Returns the identifier (e.g. ID number) of the person who picked up the package.
	 *
	 * @param packageCode package identifier; requires {@code isPackageRegistered(packageCode)}
	 * @return a valid ID (non-null)
	 * @throws IllegalArgumentException or AssertionError when
	 *                                  {@code !isPackageRegistered(packageCode)}
	 */
	public String getRegisteredIdFor(String packageCode);

	/**
	 * Returns the pickup date for the given package code.
	 *
	 * @param packageCode package identifier; requires {@code isPackageRegistered(packageCode)}
	 * @return non-null pickup {@code LocalDate}
	 * @throws IllegalArgumentException or AssertionError when
	 *                                  {@code !isPackageRegistered(packageCode)}
	 */
	public LocalDate getPickupDateFor(String packageCode);

	/**
	 * Register the certified pickup of a package.
	 *
	* @param p          package to register; requires {@code p != null}, that it is not already registered
	*                   and that {@code p.isCertified()} is true
	* @param id        identifier of the person picking up; must be authorized in the package
	 * @param pickupDate date of pickup; requires {@code pickupDate != null} and that it is not after the
	 *                   allowed pickup expiration
	 * @throws IllegalArgumentException or AssertionError when preconditions are violated
	 */
    public void registerCertifiedPackagePickup(Package p, String id, LocalDate pickupDate);

}