package com.aagproservices.jenkins.bitbucketsteps.service;

import com.aagproservices.jenkins.bitbucketsteps.BitbucketServer;
import com.aagproservices.jenkins.bitbucketsteps.api.Branch;
import com.aagproservices.jenkins.bitbucketsteps.api.FileUpdate;
import com.aagproservices.jenkins.bitbucketsteps.api.PullRequest;
import com.aagproservices.jenkins.bitbucketsteps.api.Tag;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ContentService extends BaseService {

    public ContentService(final BitbucketServer bitbucketSite) {
        super(bitbucketSite);
    }

    public JSONObject createTag(final String project, final String repoSlug, final Tag tag) throws BadRequestException {
        try {
            JSONObject json = new JSONObject()
                    .put("name", tag.getName())
                    .put("message", tag.getMessage())
                    .put("startPoint", tag.getStartPoint());

            RequestBody body = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), json.toString());
            Request request = buildRequest (project, repoSlug,"tags", HttpMethod.POST, body, null);
            return executeRequest(request);
        } catch (JSONException ex) {
            throw new RuntimeException("Error creating tag", ex);
        }
    }

    public JSONObject getTags(final String project, final String repoSlug) throws BadRequestException {
        try {
            Request request = buildRequest (project, repoSlug,"tags", HttpMethod.GET, null, null);
            return executeRequest(request);
        } catch (JSONException ex) {
            throw new RuntimeException("Error creating tag", ex);
        }
    }

    public JSONObject createBranch(final String project, final String repoSlug, final Branch branch) throws BadRequestException {
        try {
            JSONObject json = new JSONObject()
                    .put("name", branch.getName())
                    .put("message", branch.getMessage())
                    .put("startPoint", branch.getStartPoint());

            RequestBody body = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), json.toString());
            Request request = buildRequest (project, repoSlug,"branches", HttpMethod.POST, body, null);
            return executeRequest(request);
        } catch (JSONException ex) {
            throw new RuntimeException("Error creating branch", ex);
        }
    }

    public JSONObject createPullRequest(final String project, final String repoSlug, final PullRequest pullRequest) throws BadRequestException {
        try {
            JSONObject json = new JSONObject()
                    .put("title", pullRequest.getTitle())
                    .put("description", pullRequest.getDescription())
                    .put("state", "OPEN")
                    .put("open", true)
                    .put("closed", false)
                    .put("locked", false)
                    .put("fromRef", new JSONObject()
                            .put("id", pullRequest.getFrom())
                            .put("repository", new JSONObject()
                                    .put("slug", repoSlug)
                                    .put("name", "")
                                    .put("project", new JSONObject()
                                            .put("key", project))))
                    .put("toRef", new JSONObject()
                            .put("id", pullRequest.getTo())
                            .put("repository", new JSONObject()
                                    .put("slug", repoSlug)
                                    .put("name", "")
                                    .put("project", new JSONObject()
                                            .put("key", project))))

                    .put("reviewers", new JSONArray());

            RequestBody body = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), json.toString());
            Request request = buildRequest (project, repoSlug,"pull-requests", HttpMethod.POST, body, null);
            return executeRequest(request);
        } catch (JSONException ex) {
            throw new RuntimeException("Error creating pull request", ex);
        }
    }

    public JSONObject mergePullRequest(final String project, final String repoSlug, final int pullRequestId) throws BadRequestException {
        boolean canMerge;
        int prVersion;

        try {
            Request request = buildRequest(project, repoSlug,"pull-requests/" + pullRequestId, HttpMethod.GET, null, null);
            JSONObject prDetails = executeRequest(request);
            try {
                canMerge = prDetails.getBoolean("canMerge");
            } catch(JSONException ex) {
                canMerge = true;
            }
            prVersion = prDetails.getInt("version");
        } catch(JSONException ex) {
            throw new BadRequestException("Cannot retrieve pull request info for ID " + pullRequestId, ex);
        }

        if(canMerge) {
            try {
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), "{}");
                Map<String, String> params = Collections.singletonMap("version", String.valueOf(prVersion));
                Request request = buildRequest(project, repoSlug,"pull-requests/" + pullRequestId + "/merge", HttpMethod.POST, body, params);
                return executeRequest(request);
            } catch(JSONException ex) {
                throw new BadRequestException("Error merging pull request with ID " + pullRequestId, ex);
            }
        } else {
            throw new BadRequestException("Automated merge not possible for pull request with ID " + pullRequestId);
        }
    }

    public JSONObject updateFile(final String project, final String repoSlug, final FileUpdate fileUpdate, String workspace) {
        if(fileUpdate.getSourceCommitId() == null || fileUpdate.getSourceCommitId().trim().length() == 0) {
            try {
                Map<String, String> params = Stream.of(
                        new AbstractMap.SimpleEntry<>("until", fileUpdate.getBranch()),
                        new AbstractMap.SimpleEntry<>("limit", "0"),
                        new AbstractMap.SimpleEntry<>("start", "0")
                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                Request request = buildRequest(project, repoSlug,"commits", HttpMethod.GET, null, params);
                JSONObject history = executeRequest(request);
                JSONArray commits = history.getJSONArray("values");
                if(commits.length() > 0) {
                    fileUpdate.setSourceCommitId(commits.getJSONObject(0).getString("id"));
                } else {
                    fileUpdate.setSourceCommitId(null);
                }
            } catch(JSONException| ArrayIndexOutOfBoundsException ex) {
                throw new BadRequestException("Error retrieving current commit ID", ex);
            }
        }

        try {
            List<MultipartField> params = new ArrayList<>(Arrays.asList(
                    new MultipartField("message", fileUpdate.getMessage()),
                    new MultipartField("branch", fileUpdate.getBranch())
            ));
            if (fileUpdate.getSourceCommitId() != null) {
                params.add(new MultipartField("sourceCommitId", fileUpdate.getSourceCommitId()));
            }

            File updateFile = new File(workspace, fileUpdate.getFile());
            RequestBody body = buildBodyForFileUpload("content", updateFile.getAbsolutePath(), guessMediaType(updateFile), params);
            Request request = buildRequest(project, repoSlug,"browse/" + fileUpdate.getFile(), HttpMethod.PUT, body, null);
            return executeRequest(request);
        } catch(JSONException ex) {
            throw new BadRequestException("Error committing file", ex);
        }
    }
}
