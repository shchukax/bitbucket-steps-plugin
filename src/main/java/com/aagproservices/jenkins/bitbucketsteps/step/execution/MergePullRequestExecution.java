package com.aagproservices.jenkins.bitbucketsteps.step.execution;

import com.aagproservices.jenkins.bitbucketsteps.BitbucketServer;
import com.aagproservices.jenkins.bitbucketsteps.service.ContentService;
import com.aagproservices.jenkins.bitbucketsteps.step.AbstractStepExecution;
import com.aagproservices.jenkins.bitbucketsteps.step.descriptor.CreatePullRequestStep;
import com.aagproservices.jenkins.bitbucketsteps.step.descriptor.MergePullRequestStep;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.json.JSONObject;

/**
 * @author Aleks Gekht
 * @version 0.1.0
 * Execution implementation of the step "createTag".
 */
public class MergePullRequestExecution extends AbstractStepExecution<JSONObject, MergePullRequestStep> {

    private static final long serialVersionUID = 7223466183041962984L;

    /**
     * Constructor that takes the needed information for the execution of the step.
     *
     * @param mergePullRequestStep The step that is going to be executed.
     * @param context        The step context.
     * @param bitbucketSite The configured site of bitbucket.
     */
    public MergePullRequestExecution(final MergePullRequestStep mergePullRequestStep, final StepContext context, final BitbucketServer bitbucketSite) {
        super(mergePullRequestStep, context, bitbucketSite);
    }

    @Override
    public void validate(final MergePullRequestStep step) {
        super.validate(step);

        if (step.getId() == 0) {
            throw new IllegalStateException("Pull request is 0");
        }
    }

    @Override
    protected JSONObject run() throws Exception {
        try {
            return getService(ContentService.class).mergePullRequest(getStep().getProject(), getStep().getRepoSlug(), getStep().getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
