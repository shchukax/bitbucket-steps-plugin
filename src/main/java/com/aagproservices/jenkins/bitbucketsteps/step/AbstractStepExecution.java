package com.aagproservices.jenkins.bitbucketsteps.step;

import com.aagproservices.jenkins.bitbucketsteps.BitbucketServer;
import com.aagproservices.jenkins.bitbucketsteps.service.BaseService;
import com.aagproservices.jenkins.bitbucketsteps.service.ContentService;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

/**
 * @param <R> The return type of the execution.
 * @param <T> The type of the step which gets executed.
 * @author Aleks Gekht
 * @version 0.1.0
 * Abstract base class for all classes that are meant to execute steps.
 */
public abstract class AbstractStepExecution<R, T extends AbstractStep> extends SynchronousNonBlockingStepExecution<R> {

    private static final long serialVersionUID = 7535652081766832564L;

    private final transient T step;

    private final transient BitbucketServer bitbucketSite;

    /**
     * Constructor which takes the information to initialize the execution of the step.
     *
     * @param step           The step which gets executed.
     * @param context        The context of the step.
     * @param bitbucketSite The configured site of bitbucket (global Jenkins config).
     */
    public AbstractStepExecution(final T step, final StepContext context,
                                 final BitbucketServer bitbucketSite) {
        super(context);
        this.bitbucketSite = bitbucketSite;
        this.step = step;
        validate(step);
    }

    /**
     * Checks if the given step is null.
     *
     * @param step The step which gets checked.
     * @return True if it is null, false if it is not.
     */
    protected boolean isNull(final AbstractStep step) {
        return step == null;
    }

    /**
     * Returns the desired instance of a service. E.g. an instance of
     * {@link ContentService}
     * to execute it's methods.
     *
     * @param clazz The class of the desired service.
     * @param <S>   The type of the desired service.
     * @return An instance of the desired service.
     */
    protected <S extends BaseService> S getService(final Class<S> clazz) {
        switch (clazz.getSimpleName()) {
            case "ContentService":
                return clazz.cast(new ContentService(bitbucketSite));
            default:
                throw new IllegalArgumentException(String.format("\"%s\" is not a valid service", clazz.getSimpleName()));
        }
    }

    /**
     * Validates the step. Must be implemented by each step.
     *
     * @param step The step which gets validated.
     */
    protected void validate(T step) {
        if (isNull(step)) {
            throw new IllegalStateException("Given step of type CreatePageStep is null");
        }

        if (step.getSite() == null) {
            throw new IllegalStateException("Given site is null");
        }

        String project = step.getProject();
        String repoSlug = step.getRepoSlug();

        if (project == null || project.isEmpty()) {
            throw new IllegalArgumentException("Bitbucket project is null or empty!");
        }

        if (repoSlug == null || repoSlug.isEmpty()) {
            throw new IllegalArgumentException("Repository slug is null or empty!");
        }
    }

    /**
     * Returns the step which gets executed.
     *
     * @return The step.
     */
    public T getStep() {
        return step;
    }
}