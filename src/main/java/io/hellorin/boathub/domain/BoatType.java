package io.hellorin.boathub.domain;

/**
 * Enum representing different types of boats.
 * Provides type safety and predefined options for boat classification.
 */
public enum BoatType {
    SAILBOAT("Sailboat"),
    MOTORBOAT("Motorboat"),
    YACHT("Yacht"),
    SPEEDBOAT("Speedboat"),
    FISHING_BOAT("Fishing Boat"),
    OTHER("Other");

    private final String displayName;

    /**
     * Constructor for BoatType enum.
     * @param displayName The human-readable display name for the boat type
     */
    BoatType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name of the boat type.
     * @return The human-readable display name
     */
    public String getDisplayName() {
        return displayName;
    }
}
