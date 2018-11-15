package com.aagproservices.jenkins.bitbucketsteps.step.descriptor;

import com.aagproservices.jenkins.bitbucketsteps.api.PullRequest;
import com.aagproservices.jenkins.bitbucketsteps.step.AbstractStep;
import com.aagproservices.jenkins.bitbucketsteps.step.AbstractStepDescriptor;
import com.aagproservices.jenkins.bitbucketsteps.step.execution.CreatePullRequestExecution;
import com.aagproservices.jenkins.bitbucketsteps.step.execution.MergePullRequestExecution;
import hudson.Extension;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;

/**
 * @author Aleks Gekht
 * @version 0.1.0
 *          Descriptor and definition of the step "createTag" which allows the user tpo create a page.
 */
public class MergePullRequestStep extends AbstractStep {

    private static final long serialVersionUID = -72234175661233127L;

    private int id;

    /**
     * Constructor which takes the necessary information to create a page.
     *
     * @param project
     *        Project or username where the repo is located
     * @param repoSlug
     *        Repository slug
     * @param id
     *        ID of the pull request to merge
     */
    @DataBoundConstructor
    public MergePullRequestStep(final String project, final String repoSlug, final int id) {
        super(project, repoSlug);
        this.id = id;
    }

    @Override
    public StepExecution start(final StepContext context) throws Exception {
        return new MergePullRequestExecution(this, context, getSite());
    }

    /**
     * Returns the ID of the pull request to merge
     *
     * @return Pull request ID
     */
    public int getId() {
        return id;
    }

    @Extension
    public static class Descriptor extends AbstractStepDescriptor {

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Merge pull request";
        }

        @Override
        public String getFunctionName() {
            return "bitbucketMergePullRequest";
        }

    }
}
