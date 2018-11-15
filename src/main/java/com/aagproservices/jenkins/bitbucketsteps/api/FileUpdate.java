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
public class FileUpdate implements Serializable {

    private static final long serialVersionUID = 424686714325347246L;

    @JsonProperty("file")
    private String file;

    @JsonProperty("message")
    private String message;

    @JsonProperty("branch")
    private String branch;

    @JsonProperty("sourceCommitId")
    private String sourceCommitId;

    @JsonIgnore
    private Map<String, Object> unmappedFields = new HashMap<>();

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getSourceCommitId() {
        return sourceCommitId;
    }

    public void setSourceCommitId(String sourceCommitId) {
        this.sourceCommitId = sourceCommitId;
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
        return "FileUpdate {" +
            "file='" + file + "'" +
            ", message='" + message + "'" +
            ", branch='" + branch + "'" +
            ", sourceCommitId='" + sourceCommitId + "'" +
            ", unmappedFields=" + unmappedFields +
            "}";
    }
}
