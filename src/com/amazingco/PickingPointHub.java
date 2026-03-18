package com.amazingco;

import java.time.LocalTime;
import java.util.Arrays;

import es.uva.inf.poo.maps.GPSCoordinate;

/**
 * PickingPointHub for Amazingco systems.
 *
 * A hub of groupable pickup points (Kiosk or PackageLocker). A hub must be
 * initialized with at least two groupable points. All grouped elements must
 * share the same GPS location and cannot have duplicate identifiers. Groupable
 * elements may be added or removed dynamically as long as the hub maintains
 * the minimum of two elements. A hub is considered to have available space if
 * any of its grouped elements has available space. A hub is eligible for
 * cash-on-delivery if at least one grouped element is a Kiosk.
 * 
 * @author ivamoro
 * 
 * @author erisoto
 *
 */
public class PickingPointHub extends PickingPoint {

	GroupablePoint[] groupables;

	/**
	 * Constructor for PickingPointHub.
	 * @param identifier   String representing the PickingPointHub identifier
	 * @param coordinates  GPSCoordinate representing the hub location
	 * @param openingTime  LocalTime representing the hub opening time
	 * @param groupables   Array of GroupablePoint forming the hub (Kiosks and
	 *                     PackageLockers)
	 */
	public PickingPointHub(String identifier, GPSCoordinate coordinates, LocalTime openingTime,
			GroupablePoint[] groupables) {
		super(identifier, coordinates, openingTime);
		setGroupable(groupables);

	}

	/**
	* Sets the groupable elements for this hub, replacing any existing ones.
	* @param groupables Array of GroupablePoint forming the hub
	* @throws IllegalArgumentException if groupables is null, has fewer than 2
	*                                  elements, contains duplicate identifiers,
	*                                  or elements have differing GPS locations
	 */
	public void setGroupable(GroupablePoint[] groupables) {
		if (groupables == null) {
			throw new IllegalArgumentException("groupables cannot be null");
		}
		if (groupables.length < 2) {
			throw new IllegalArgumentException("groupables must contain at least 2 elements");
		}
		if (!checkSameIdentifier(groupables)) {
			throw new IllegalArgumentException("There are duplicate groupable identifiers");
		}
		if (!checkSameLocation(super.getCoordinates(), groupables)) {
			throw new IllegalArgumentException("All groupables must have the same GPS location");
		}
		this.groupables = new GroupablePoint[groupables.length];
		for (int i = 0; i < groupables.length; i++) {
			this.groupables[i] = groupables[i];
		}

	}

	/**
	* @param coordinates GPSCoordinate representing the location to compare
	* @param groupables  Array of GroupablePoint to check
	* @return true if all elements share the same location, false otherwise
	*/
	private boolean checkSameLocation(GPSCoordinate coordinates, GroupablePoint[] groupables) {
		for (GroupablePoint groupable : groupables) {
			if (groupable == null) {
				return false;
			}
			if (groupable.getCoordinates() == null || !groupable.getCoordinates().equals(coordinates)) {
				return false;
			}
		}
		return true;
	}

	/**
	* @param groupables Array of GroupablePoint to check
	* @return true if there are no duplicate identifiers among the elements
	*/
	private boolean checkSameIdentifier(GroupablePoint[] groupables) {
		for (int i = 0; i < groupables.length; i++) {
			if (groupables[i] == null) continue;
			for (int j = i + 1; j < groupables.length; j++) {
				if (groupables[j] == null) continue;
				if (groupables[i].getIdentifier().equals(groupables[j].getIdentifier())) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	* Removes a groupable element from this hub.
	*
	* @param groupable GroupablePoint to remove
	* @throws IllegalArgumentException if groupable is null, if hub size would
	*                                  drop below minimum, or if the element is
	*                                  not part of the hub
	*/
	public void removeGroupable(GroupablePoint groupable) {
		if (groupable == null) {
			throw new IllegalArgumentException("groupable cannot be null");
		}
		if (getGroupables().length <= 2) {
			throw new IllegalArgumentException("Cannot remove groupable: minimum hub size reached");
		}
		int idx = indexOfGroupable(groupable);
		if (idx == -1) {
			throw new IllegalArgumentException("Groupable is not part of this hub");
		}
		groupables[idx] = null;

	}

	/**
	* Returns the index of the groupable in the hub array, or -1 if not present.
	*
	* @param groupable GroupablePoint to locate
	* @return index position or -1 if not found
	 */
	private int indexOfGroupable(GroupablePoint groupable) {
		for (int i = 0; i < groupables.length; i++) {
			if (groupables[i] == groupable) {
				return i;
			}
		}
		return -1;
	}

	/**
	* Adds a groupable element to the hub.
	*
	* @param groupable GroupablePoint to add
	* @throws IllegalArgumentException if groupable is null, has a different
	*                                  location, or duplicates an identifier
	 */
	public void addGroupable(GroupablePoint groupable) {
		if (groupable == null) {
			throw new IllegalArgumentException("groupable cannot be null");
		}
		if (!groupable.getCoordinates().equals(super.getCoordinates())) {
			throw new IllegalArgumentException("Groupable must have the same location as the hub");
		}
		int nullPos = getNullPosition();
		if (nullPos == -1) {
			GroupablePoint[] copia = new GroupablePoint[getGroupables().length + 1];
			for (int i = 0; i < groupables.length; i++) {
				copia[i] = groupables[i];
			}
			copia[copia.length - 1] = groupable;
			this.groupables = copia;
			if (!checkSameIdentifier(this.groupables)) {
				throw new IllegalArgumentException("Added groupable had a duplicate identifier");
			}
		} else {
			groupables[nullPos] = groupable;
			if (!checkSameIdentifier(this.groupables)) {
				throw new IllegalArgumentException("Added groupable had a duplicate identifier");
			}
		}

	}

	/**
	* Returns the index of the first empty slot in the hub, or -1 if none.
	 */
	private int getNullPosition() {
		if (groupables == null) return -1;
		for (int i = 0; i < getGroupables().length; i++) {
			if (groupables[i] == null) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the elements belonging to this hub.
	 * @return Array of GroupablePoint elements in the hub
	 */
	public GroupablePoint[] getGroupables() {
		return groupables;
	}

	/**
	* Returns whether any grouped element has available space.
	* @return Boolean indicating if space is available in any groupable
	 */
	@Override
	public Boolean isSpaceAvailable() {
		if (groupables == null) return false;
		for (int i = 0; i < getGroupables().length; i++) {
			if (groupables[i] != null && groupables[i].isSpaceAvailable()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the number of elements in the hub.
	 * @return Integer count of non-null groupable elements
	 */
	public Integer getGroupableCount() {
		if (groupables == null) return 0;
		int count = 0;
		for (GroupablePoint g : groupables) if (g != null) count++;
		return count;
	}

	/**
	* Returns whether any grouped element is eligible for cash-on-delivery for
	* a given package.
	* @return Boolean indicating if any groupable is a Kiosk (eligible for COD)
	*/
	public Boolean isEligibleForCashOnDelivery() {
		if (groupables == null) return false;
		for (int i = 0; i < groupables.length; i++) {
			if (groupables[i].isCashOnDelivery()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets the operative flag for the hub and propagates to grouped elements.
	 *
	 * @param operative Boolean indicating operative status
	 * @throws IllegalArgumentException if operative is null
	 */
	public void setOperative(Boolean operative) {
		if (operative == null) {
			throw new IllegalArgumentException("Operative flag cannot be null");

		}
		if (operative == false) {
			if (groupables != null) {
				for (int i = 0; i < groupables.length; i++) {
					if (groupables[i] != null) groupables[i].setOperative(false);
				}
			}
			super.setOperative(false);
		}
		if (operative == true) {
			if (hasOperativeGroupable()) {
				super.setOperative(true);
			} else {
				super.setOperative(false);
			}

		}
	}

	/**
	 * Returns whether there is any operative groupable element
	 * @return Boolean indicating presence of an operative groupable
	 */
	private Boolean hasOperativeGroupable() {
		for (int i = 0; i < groupables.length; i++) {
			if (groupables[i] != null && Boolean.TRUE.equals(groupables[i].isOperative()))
				return true;
		}
		return false;
	}

	@Override
	public Boolean isCertified() {
		return false;
	}

	@Override
	public Boolean isCashOnDelivery() {
		return isEligibleForCashOnDelivery();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(groupables);
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
		if (!(obj instanceof PickingPointHub)) {
			return false;
		}
		PickingPointHub other = (PickingPointHub) obj;
		return Arrays.equals(groupables, other.groupables);
	}

	@Override
	public String toString() {
		return "PickingPointHub [groupables=" + Arrays.toString(groupables) + "]";
	}
}