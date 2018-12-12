package com.aagproservices.jenkins.bitbucketsteps.step.execution;

import com.aagproservices.jenkins.bitbucketsteps.BitbucketServer;
import com.aagproservices.jenkins.bitbucketsteps.service.ContentService;
import com.aagproservices.jenkins.bitbucketsteps.step.AbstractStepExecution;
import com.aagproservices.jenkins.bitbucketsteps.step.descriptor.CreateTagStep;
import com.aagproservices.jenkins.bitbucketsteps.step.descriptor.GetTagsStep;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.json.JSONObject;

/**
 * @author Aleks Gekht
 * @version 0.1.0
 * Execution implementation of the step "getTags".
 */
public class GetTagsExecution extends AbstractStepExecution<JSONObject, GetTagsStep> {

    private static final long serialVersionUID = 7220386183068962984L;

    /**
     * Constructor that takes the needed information for the execution of the step.
     *
     * @param getTagsStep   The step that is going to be executed.
     * @param context       The step context.
     * @param bitbucketSite The configured site of bitbucket.
     */
    public GetTagsExecution(final GetTagsStep getTagsStep, final StepContext context, final BitbucketServer bitbucketSite) {
        super(getTagsStep, context, bitbucketSite);
    }

    @Override
    public void validate(final GetTagsStep step) {
        super.validate(step);
    }

    @Override
    protected JSONObject run() throws Exception {
        try {
            return getService(ContentService.class).getTags(getStep().getProject(), getStep().getRepoSlug());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
