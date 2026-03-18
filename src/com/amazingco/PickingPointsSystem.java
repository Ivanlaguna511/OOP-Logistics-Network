package com.amazingco;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import es.uva.inf.poo.maps.GPSCoordinate;

/**
 * Management system for Amazingco picking points.
 *
 * Holds the collection of available picking points and provides queries to
 * find nearby points, points with free space, eligible points for a given
 * package, and lists of operative/out-of-service points.
 */
public class PickingPointsSystem {
    private List<PickingPoint> pickingPoints;

    /**
     * Constructor for PickingPointsSystem.
     */
    public PickingPointsSystem() {
        pickingPoints = new ArrayList<>();
    }

    /**
     * Adds a picking point to the system.
     *
     * @param pickingPoint PickingPoint to add
     * @throws IllegalArgumentException if pickingPoint is null or has a duplicate identifier
     */
    public void addPickingPoint(PickingPoint pickingPoint) {
        if (pickingPoint == null) {
            throw new IllegalArgumentException("Cannot add null picking point");
        }
        if (hasDuplicateIdentifier(pickingPoint)) {
            throw new IllegalArgumentException("Cannot add picking point with duplicate identifier");
        }
        this.pickingPoints.add(pickingPoint);
    }

    /**
     * Removes a picking point identified by the given identifier.
     *
     * @param identifier String identifier of the picking point to remove
     * @throws IllegalArgumentException if identifier is null or no picking point with that identifier exists
     */
    public void removePickingPoint(String identifier) {
        if (identifier == null) {
            throw new IllegalArgumentException("Identifier cannot be null");
        }
        PickingPoint pointToRemove = findPickingPointByIdentifier(identifier);
        if (pointToRemove == null) {
            throw new IllegalArgumentException("No picking point with that identifier");
        }
        pickingPoints.remove(pointToRemove);
    }

    /**
     * Returns a list of all operative picking points.
     *
     * @return List of operative PickingPoint instances
     */
    public List<PickingPoint> getOperativePickingPoints() {
        List<PickingPoint> operativePoints = new ArrayList<>();
        for (PickingPoint pickingpoint : pickingPoints) {
            if (pickingpoint.isOperative()) {
                operativePoints.add(pickingpoint);
            }
        }
        return operativePoints;
    }

    /**
     * Returns a list of all out-of-service picking points.
     *
     * @return List of non-operative PickingPoint instances
     */
    public List<PickingPoint> getOutOfServicePickingPoints() {
        List<PickingPoint> outOfService = new ArrayList<>();
        for (PickingPoint pickingpoint : this.pickingPoints) {
            if (!pickingpoint.isOperative()) {
                outOfService.add(pickingpoint);
            }
        }
        return outOfService;
    }

    /**
     * Returns picking points within the given radius of the provided GPS coordinate.
     *
     * @param gps   GPSCoordinate center point
     * @param radius Double radius
     * @return List of nearby PickingPoint instances
     * @throws IllegalArgumentException if gps or radius are null
     */
    public List<PickingPoint> getNearbyPickingPoints(GPSCoordinate gps, Double radius) {
        if (gps == null) {
            throw new IllegalArgumentException("GPS cannot be null");
        }
        if (radius == null) {
            throw new IllegalArgumentException("Radius cannot be null");
        }
        List<PickingPoint> nearby = new ArrayList<>();
        for (PickingPoint pickingpoint : this.pickingPoints) {
            if (pickingpoint.getCoordinates().getDistanceTo(gps) <= radius) {
                nearby.add(pickingpoint);
            }
        }
        return nearby;
    }

    /**
     * Returns picking points that currently have free space.
     *
     * @return List of PickingPoint instances with available space
     */
    public List<PickingPoint> getPickingPointsWithAvailableSpace() {
        List<PickingPoint> available = new ArrayList<>();
        for (PickingPoint pickingpoint : this.pickingPoints) {
            if (pickingpoint.isSpaceAvailable()) {
                available.add(pickingpoint);
            }
        }
        return available;
    }

    /**
     * Returns picking points within the radius that are eligible for the given
     * package according to its type and payment status.
     *
     * @param gps     GPSCoordinate center point
     * @param radius  Double radius
     * @param pkg     Package for which to find eligible points
     * @return List of eligible PickingPoint instances
     */
    public List<PickingPoint> getEligiblePickingPoints(GPSCoordinate gps, Double radius, Package pkg) {
        if (gps == null) {
            throw new IllegalArgumentException("GPS cannot be null");
        }
        if (radius == null) {
            throw new IllegalArgumentException("Radius cannot be null");
        }
        if (pkg == null) {
            throw new IllegalArgumentException("Package cannot be null");
        }
        
        List<PickingPoint> eligible = new ArrayList<>();
        
        if (pkg.isCertified()) {
            for (PickingPoint p : pickingPoints) {
                if (p.getCoordinates().getDistanceTo(gps) <= radius && p.isCertified()) {
                    eligible.add(p);
                }
            }
        } else if (pkg.isPaid()) {
            for (PickingPoint p : pickingPoints) {
                if (p.getCoordinates().getDistanceTo(gps) <= radius) {
                    eligible.add(p);
                }
            }
        } else { // Implicitly !pkg.isPaid()
            for (PickingPoint p : pickingPoints) {
                if (p.getCoordinates().getDistanceTo(gps) <= radius && p.isCashOnDelivery()) {
                    eligible.add(p);
                }
            }
        }
        return eligible;
    }

    /**
     * Checks whether any existing picking point has the same identifier.
     */
    private Boolean hasDuplicateIdentifier(PickingPoint pickingPoint) {
        for (PickingPoint p : pickingPoints) {
            if (p.getIdentifier().equals(pickingPoint.getIdentifier())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds a picking point by its identifier.
     */
    private PickingPoint findPickingPointByIdentifier(String identifier) {
        for (PickingPoint p : pickingPoints) {
            if (p.getIdentifier().equals(identifier)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pickingPoints);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PickingPointsSystem)) {
            return false;
        }
        PickingPointsSystem other = (PickingPointsSystem) obj;
        return Objects.equals(pickingPoints, other.pickingPoints);
    }

    @Override
    public String toString() {
        return "PickingPointsSystem [pickingPoints=" + pickingPoints + "]";
    }
}