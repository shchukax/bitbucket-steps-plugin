package com.aagproservices.jenkins.bitbucketsteps.step.descriptor;

import com.aagproservices.jenkins.bitbucketsteps.api.FileUpdate;
import com.aagproservices.jenkins.bitbucketsteps.step.AbstractStep;
import com.aagproservices.jenkins.bitbucketsteps.step.AbstractStepDescriptor;
import com.aagproservices.jenkins.bitbucketsteps.step.execution.UpdateFileExecution;
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
public class UpdateFileStep extends AbstractStep {

    private static final long serialVersionUID = -72456875661233127L;

    private String file;
    private String message;
    private String branch;
    private String sourceCommitId;

    /**
     * Constructor which takes the necessary information to create a page.
     *
     * @param project
     *        Project or username where the repo is located
     * @param repoSlug
     *        Repository slug
     * @param file
     *        Title of pull request to create
     * @param message
     *        Description of pull request
     * @param branch
     *        Source branch of pull request
     * @param sourceCommitId
     *        Destination branch of pull request
     */
    @DataBoundConstructor
    public UpdateFileStep(final String project, final String repoSlug,
                          final String file, final String message, final String branch, final String sourceCommitId) {
        super(project, repoSlug);
        this.file = file;
        this.message = message;
        this.branch = branch;
        this.sourceCommitId = sourceCommitId;
    }

    @Override
    public StepExecution start(final StepContext context) throws Exception {
        return new UpdateFileExecution(this, context, getSite());
    }

    /**
     * Returns the file of the pull request that will be created
     *
     * @return The file of the tag
     */
    public String getFile() {
        return file;
    }

    /**
     * Returns pull request message
     *
     * @return pull request message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns source ref of the pull request
     *
     * @return Source ref of the pull request
     */
    public String getBranch() {
        return branch;
    }

    /**
     * Returns source ref of the pull request
     *
     * @return Source ref of the pull request
     */
    public String getSourceCommitId() {
        return sourceCommitId;
    }

    public FileUpdate getFileUpdate() {
        FileUpdate fileUpdate = new FileUpdate();
        fileUpdate.setFile(getFile());
        fileUpdate.setMessage(getMessage());
        fileUpdate.setBranch(getBranch());
        fileUpdate.setSourceCommitId(getSourceCommitId());
        return fileUpdate;
    }

    @Extension
    public static class Descriptor extends AbstractStepDescriptor {

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Updates or create file in the specified branch";
        }

        @Override
        public String getFunctionName() {
            return "bitbucketUpdateFile";
        }

    }
}
