package com.aagproservices.jenkins.bitbucketsteps.step.execution;

import com.aagproservices.jenkins.bitbucketsteps.BitbucketServer;
import com.aagproservices.jenkins.bitbucketsteps.service.ContentService;
import com.aagproservices.jenkins.bitbucketsteps.step.AbstractStepExecution;
import com.aagproservices.jenkins.bitbucketsteps.step.descriptor.CreatePullRequestStep;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.json.JSONObject;

/**
 * @author Aleks Gekht
 * @version 0.1.0
 * Execution implementation of the step "createTag".
 */
public class CreatePullRequestExecution extends AbstractStepExecution<Integer, CreatePullRequestStep> {

    private static final long serialVersionUID = 7220386183041962984L;

    /**
     * Constructor that takes the needed information for the execution of the step.
     *
     * @param createPullRequestStep The step that is going to be executed.
     * @param context        The step context.
     * @param bitbucketSite The configured site of bitbucket.
     */
    public CreatePullRequestExecution(final CreatePullRequestStep createPullRequestStep, final StepContext context, final BitbucketServer bitbucketSite) {
        super(createPullRequestStep, context, bitbucketSite);
    }

    @Override
    public void validate(final CreatePullRequestStep step) {
        super.validate(step);

        if (step.getTitle() == null || step.getTitle().isEmpty()) {
            throw new IllegalStateException("The title of the pull request is null or empty");
        }

        if (step.getFromRef() == null || step.getFromRef().isEmpty()) {
            throw new IllegalStateException("The source ref is null or empty");
        }

        if (step.getToRef() == null || step.getToRef().isEmpty()) {
            throw new IllegalStateException("The destination ref is null or empty");
        }
    }

    @Override
    protected Integer run() throws Exception {
        try {
            JSONObject result = getService(ContentService.class).createPullRequest(getStep().getProject(), getStep().getRepoSlug(), getStep().getPullRequest());
            return result.getInt("id");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
