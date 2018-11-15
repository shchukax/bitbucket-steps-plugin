package com.aagproservices.jenkins.bitbucketsteps.step.descriptor;

import com.aagproservices.jenkins.bitbucketsteps.api.PullRequest;
import com.aagproservices.jenkins.bitbucketsteps.step.AbstractStep;
import com.aagproservices.jenkins.bitbucketsteps.step.AbstractStepDescriptor;
import com.aagproservices.jenkins.bitbucketsteps.step.execution.CreatePullRequestExecution;
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
public class CreatePullRequestStep extends AbstractStep {

    private static final long serialVersionUID = -72495175661233127L;

    private String title;
    private String description;
    private String fromRef;
    private String toRef;

    /**
     * Constructor which takes the necessary information to create a page.
     *
     * @param project
     *        Project or username where the repo is located
     * @param repoSlug
     *        Repository slug
     * @param title
     *        Title of pull request to create
     * @param description
     *        Description of pull request
     * @param fromRef
     *        Source branch of pull request
     * @param toRef
     *        Destination branch of pull request
     */
    @DataBoundConstructor
    public CreatePullRequestStep(final String project, final String repoSlug,
                                 final String title, final String description, final String fromRef, final String toRef) {
        super(project, repoSlug);
        this.title = title;
        this.description = description;
        this.fromRef = fromRef;
        this.toRef = toRef;
    }

    @Override
    public StepExecution start(final StepContext context) throws Exception {
        return new CreatePullRequestExecution(this, context, getSite());
    }

    /**
     * Returns the title of the pull request that will be created
     *
     * @return The title of the tag
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns pull request description
     *
     * @return pull request description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns source ref of the pull request
     *
     * @return Source ref of the pull request
     */
    public String getFromRef() {
        return fromRef;
    }

    /**
     * Returns source ref of the pull request
     *
     * @return Source ref of the pull request
     */
    public String getToRef() {
        return toRef;
    }

    public PullRequest getPullRequest() {
        PullRequest pr = new PullRequest();
        pr.setTitle(getTitle());
        pr.setDescription(getDescription());
        pr.setFrom(getFromRef());
        pr.setTo(getToRef());
        return pr;
    }

    @Extension
    public static class Descriptor extends AbstractStepDescriptor {

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Creates a new pull requested with specified title and description from/to specified ref points";
        }

        @Override
        public String getFunctionName() {
            return "bitbucketCreatePullRequest";
        }

    }
}
