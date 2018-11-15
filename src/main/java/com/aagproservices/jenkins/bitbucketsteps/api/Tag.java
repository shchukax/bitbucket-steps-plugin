package com.aagproservices.jenkins.bitbucketsteps.api;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aleks Gekht
 * @version 0.1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tag implements Serializable {

    private static final long serialVersionUID = 4246867143258684246L;

    @JsonProperty("name")
    private String name;

    @JsonProperty("message")
    private String message;

    @JsonProperty("startPoint")
    private String startPoint;

    @JsonIgnore
    private Map<String, Object> unmappedFields = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public Map<String, Object> getUnmappedFields() {
        return unmappedFields;
    }

    @JsonAnySetter
    public void setUnmappedFields(String name, Object value) {
        this.unmappedFields.put(name, value);
    }

    @Override
    public String toString() {
        return "Tag {" +
            "name='" + name + "'" +
            ", message='" + message + "'" +
            ", startPoint='" + startPoint + "'" +
            ", unmappedFields=" + unmappedFields +
            "}";
    }
}
