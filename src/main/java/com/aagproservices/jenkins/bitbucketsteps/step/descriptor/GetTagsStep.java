package com.aagproservices.jenkins.bitbucketsteps.step.descriptor;

import com.aagproservices.jenkins.bitbucketsteps.step.AbstractStep;
import com.aagproservices.jenkins.bitbucketsteps.step.AbstractStepDescriptor;
import com.aagproservices.jenkins.bitbucketsteps.step.execution.GetTagsExecution;
import hudson.Extension;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;

/**
 * @author Aleks Gekht
 * @version 0.1.0
 *          Descriptor and definition of the step "getTags" which allows the user to retrieve existing tags.
 */
public class GetTagsStep extends AbstractStep {

    private static final long serialVersionUID = -7249517566925473127L;

    /**
     * Constructor which takes the necessary information to create a page.
     *
     * @param project
     *        Project or username where the repo is located
     * @param repoSlug
     *        Repository slug
     */
    @DataBoundConstructor
    public GetTagsStep(final String project, final String repoSlug) {
        super(project, repoSlug);
    }

    @Override
    public StepExecution start(final StepContext context) throws Exception {
        return new GetTagsExecution(this, context, getSite());
    }

    @Extension
    public static class Descriptor extends AbstractStepDescriptor {

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Retrieve list of existing tags in the repository";
        }

        @Override
        public String getFunctionName() {
            return "bitbucketGetTags";
        }

    }
}
