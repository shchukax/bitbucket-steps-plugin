package com.aagproservices.jenkins.bitbucketsteps;

import com.aagproservices.jenkins.bitbucketsteps.util.HttpUtil;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Representation of a configured site for confluence.
 *
 * @author Aleks Gekht
 * @version 0.1.0
 */
public class BitbucketServer extends AbstractDescribableImpl<BitbucketServer> implements Serializable {

    private static final long serialVersionUID = -1895419369131803022L;

    private URL url;
    private String username;
    private String password;
    private Integer timeout;
    private Integer poolSize;

    //Will be implemented soon.....
    private boolean trustAllCertificates = false;

    private BitbucketServerDescriptor bitbucketServerDescriptor = new BitbucketServerDescriptor();

    /**
     * Constructor that takes the values of this instance.
     *
     * @param username
     *        The username of the confluence user.
     * @param password
     *        The password of the confluence user.
     * @param url
     *        The base URL of Confluence.
     * @param timeout
     *        The desired timeout for the connection.
     * @param poolSize
     *        The max connection pool size.
     */
    @DataBoundConstructor
    public BitbucketServer(final URL url, final String username, final String password,
                           final Integer timeout, final Integer poolSize) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.timeout = timeout;
        this.poolSize = poolSize;
    }

    @Override
    public Descriptor<BitbucketServer> getDescriptor() {
        return bitbucketServerDescriptor;
    }

    /**
     * Returns the username of the Confluence user.
     *
     * @return The username.
     */
    public String getUserName() {
        return username;
    }

    /**
     * Sets the username of the Confluence user.
     *
     * @param userName
     *        The value for username.
     */
    @DataBoundSetter
    public void setUserName(final String userName) {
        this.username = userName;
    }

    /**
     * Return the base URL of Confluence.
     *
     * @return The base URL of Confluence.
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Sets the base URL of Confluence.
     *
     * @param url
     *        The base URL of Confluence.
     */
    @DataBoundSetter
    public void setUrl(final URL url) {
        this.url = url;
    }

    /**
     * Returns the password of the Confluence user.
     *
     * @return The password of the Confluence user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the Confluence user.
     *
     * @param password
     *        The password of the Confluence user.
     */
    @DataBoundSetter
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Returns the timeout for the connections.
     *
     * @return The timeout for the connections.
     */
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * Sets the timeout for the connections.
     *
     * @param timeout
     *        The timeout for the connections.
     */
    @DataBoundSetter
    public void setTimeout(final Integer timeout) {
        this.timeout = timeout;
    }

    /**
     * Returns the max pool size for the connection pool.
     *
     * @return The max pool size.
     */
    public Integer getPoolSize() {
        return poolSize;
    }

    /**
     * Sets the max pool size for the connection pool.
     *
     * @param poolSize
     *        The max pool size for the connection pool.
     */
    @DataBoundSetter
    public void setPoolSize(final Integer poolSize) {
        this.poolSize = poolSize;
    }

    public boolean getTrustAllCertificates() {
        return trustAllCertificates;
    }

    @DataBoundSetter
    public void setTrustAllCertificates(final boolean trustAllCertificates) {
        this.trustAllCertificates = trustAllCertificates;
    }

    /**
     * Descriptor for {@link BitbucketServer}.
     */
    @Extension
    public static final class BitbucketServerDescriptor extends Descriptor<BitbucketServer> implements Serializable {

        private static final long serialVersionUID = 7773097811656159514L;

        private String username;
        private String password;
        private String url;
        private Integer timeout;
        private Integer poolSize;
        private boolean trustAllCertificates;

        /**
         * Constructor that initializes the view.
         */
        public BitbucketServerDescriptor() {
            super(BitbucketServer.class);
            load();
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Bitbucket Server";
        }

        @Override
        public boolean configure(final StaplerRequest req, final JSONObject json) throws FormException {
            username = json.getString("username");
            password = json.getString("password");
            url = json.getString("url");
            timeout = json.getInt("timeout");
            poolSize = json.getInt("poolSize");
//            trustAllCertificates = json.getBoolean("trustAllCertificates");
            validate(url, username, password, timeout);
            save();
            return super.configure(req, json);
        }

        /**
         * Tests the connection with the data from the view.
         *
         * @param username
         *        The username of the Confluence user.
         * @param password
         *        The password of the Confluence user.
         * @param url
         *        The base URL of Confluence.l
         * @param timeout
         *        The timeout for the connections.
         * @return FormValidation to show a success or an error on the view.
         */
        public FormValidation doTestConnection(@QueryParameter("username") final String username,
            @QueryParameter("password") final String password,
            @QueryParameter("url") final String url,
            @QueryParameter("timeout") final Integer timeout) {
            try {
                validate(url, username, password, timeout);
                URL confluenceUrl = new URL(url);
                if (!HttpUtil.isReachable(confluenceUrl, timeout)) {
                    throw new IllegalStateException("Address " + confluenceUrl.toURI().toString() + " is not reachable");
                }
                return FormValidation.okWithMarkup("Success");
            } catch (MalformedURLException e) {
                return FormValidation.errorWithMarkup("The URL " + url + " is malformed");
            } catch (IllegalArgumentException e) {
                return FormValidation.warningWithMarkup(e.getMessage());
            } catch (URISyntaxException e) {
                return FormValidation.errorWithMarkup("URI build from URL " + url + " is malformed");
            } catch (IllegalStateException e) {
                return FormValidation.errorWithMarkup(e.getMessage());
            }
        }

        private void validate(final String url, final String username, final String password, final Integer timeout) {
            validateCredentials(username, password);
            HttpUtil.validateUrl(url);
        }

        private void validateCredentials(final String username, final String password) {
            if (username == null || username.isEmpty()) {
                throw new IllegalArgumentException("Please enter the username of the bitbucket user!");
            }

            if (password == null || password.isEmpty()) {
                throw new IllegalArgumentException("Please enter the passoword of the bitbucket user!");
            }
        }

        /**
         * Returns the configured username of the Confluence user.
         *
         * @return The configured username of the Confluence user.
         */
        public String getUsername() {
            return username;
        }

        /**
         * Returns the configured password of the Confluence user.
         *
         * @return The configured password of the Confluence user.
         */
        public String getPassword() {
            return password;
        }

        public boolean getTrustAllCertificates() {
            return trustAllCertificates;
        }

        /**
         * Returns the configured URL of Confluence.
         *
         * @return The configured URL of Confluence.
         */
        public URL getUrl() {
            try {
                return new URL(url);
            } catch (MalformedURLException e) {
                throw new RuntimeException("I am already validated", e);
            }
        }

        /**
         * Returns the configured timeout for connections.
         *
         * @return The configured timeout.
         */
        public Integer getTimeout() {
            return timeout;
        }

        /**
         * Returns the configured max size of the connection pool.
         *
         * @return The configured max size of the connection pool.
         */
        public Integer getPoolSize() {
            return poolSize;
        }
    }
}
