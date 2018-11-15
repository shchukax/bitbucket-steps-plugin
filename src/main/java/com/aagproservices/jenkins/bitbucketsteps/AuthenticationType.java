package com.aagproservices.jenkins.bitbucketsteps;

/**
 * @author Aleks Gekht
 * @version 0.1.0
 * Enum to declare the type of authentication.
 */
public enum AuthenticationType {
    BASIC("basic"),
    OAUTH("oauth");

    private String type;

    /**
     * Constructor which takes a {@link String} representation of the enum constant.
     *
     * @param type The {@link String} representation of the enum.
     */
    AuthenticationType(final String type) {
        this.type = type;
    }

    /**
     * Retuns an enum constant from a given {@link String}.
     *
     * @param value The {@link String} representation of the desired enum constant.
     * @return The desired enum constant.
     */
    public static AuthenticationType fromString(final String value) {
        switch (value.toLowerCase()) {
            case "basic":
                return BASIC;
            case "oauth":
                return OAUTH;
            default:
                throw new IllegalArgumentException("AuthenticationType " + value + " is not supported");
        }
    }

    /**
     * Returns a {@link String} representation of the enum constant.
     *
     * @return The {@link String} representation of the enum constant.
     */
    public String getType() {
        return type;
    }
}
