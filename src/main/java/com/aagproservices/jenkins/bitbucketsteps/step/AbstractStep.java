package com.aagproservices.jenkins.bitbucketsteps.step;

import com.aagproservices.jenkins.bitbucketsteps.BitbucketServer;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.workflow.steps.Step;

import java.io.Serializable;

/**
 * @author Aleks Gekht
 * @version 0.1.0
 * Abstract base class for all the available steps within this plugin.
 */
public abstract class AbstractStep extends Step implements Serializable {

    private static final long serialVersionUID = -2394672691414818804L;

    private BitbucketServer site;
    protected String project;
    protected String repoSlug;

    /**
     * Constructor which extracts the information of the configured site (global Jenkins config) from it's descriptor
     * and constructs an instance of {@link BitbucketServer} which can be used by the steps.
     */
    public AbstractStep(String project, String repoSlug) {
        this.project = project;
        this.repoSlug = repoSlug;

        Jenkins jenkins = Jenkins.getInstanceOrNull();
        if (jenkins == null) {
            throw new IllegalStateException("Jenkins instance is null!");
        }
        BitbucketServer.BitbucketServerDescriptor siteDescriptor = (BitbucketServer.BitbucketServerDescriptor)jenkins.getDescriptor(BitbucketServer.class);

        if (siteDescriptor != null) {
            this.site = new BitbucketServer(
                    siteDescriptor.getUrl(),
                    siteDescriptor.getUsername(),
                    siteDescriptor.getPassword(),
                    siteDescriptor.getTimeout(),
                    siteDescriptor.getPoolSize());
        }
    }

    /**
     * Returns the configured {@link BitbucketServer}.
     *
     * @return The configured {@link BitbucketServer}.
     */
    public BitbucketServer getSite() {
        return site;
    }

    public String getProject() {
        return project;
    }

    public String getRepoSlug() {
        return repoSlug;
    }
}
