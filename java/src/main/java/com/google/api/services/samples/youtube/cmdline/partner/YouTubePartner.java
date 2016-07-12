package com.google.api.services.samples.youtube.cmdline.partner;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;

public class YouTubePartner extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient {
  /**
   * The default encoded root URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_ROOT_URL = "https://www.googleapis.com/";

  /**
   * The default encoded service path of the service. This is determined when the library is
   * generated and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_SERVICE_PATH = "youtube/partner/v1/";

  /**
   * The default encoded base URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   */
  public static final String DEFAULT_BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;


  public YouTubePartner(HttpTransport transport, JsonFactory jsonFactory,
			HttpRequestInitializer httpRequestInitializer) {
	  this(new Builder(transport, jsonFactory, httpRequestInitializer));
  }
  
  /**
   * @param builder builder
   */
  YouTubePartner(Builder builder) {
    super(builder);
  }
  
  /**
   * Builder for {@link YouTube}.
   *
   * <p>
   * Implementation is not thread-safe.
   * </p>
   *
   * @since 1.3.0
   */
  public static final class Builder extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder {

    /**
     * Returns an instance of a new builder.
     *
     * @param transport HTTP transport, which should normally be:
     *        <ul>
     *        <li>Google App Engine:
     *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
     *        <li>Android: {@code newCompatibleTransport} from
     *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
     *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
     *        </li>
     *        </ul>
     * @param jsonFactory JSON factory, which may be:
     *        <ul>
     *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
     *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
     *        <li>Android Honeycomb or higher:
     *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
     *        </ul>
     * @param httpRequestInitializer HTTP request initializer or {@code null} for none
     * @since 1.7
     */
    public Builder(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
        com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      super(
          transport,
          jsonFactory,
          DEFAULT_ROOT_URL,
          DEFAULT_SERVICE_PATH,
          httpRequestInitializer,
          false);
    }

    /** Builds a new instance of {@link YouTube}. */
    @Override
    public YouTubePartner build() {
      return new YouTubePartner(this);
    }

    @Override
    public Builder setRootUrl(String rootUrl) {
      return (Builder) super.setRootUrl(rootUrl);
    }

    @Override
    public Builder setServicePath(String servicePath) {
      return (Builder) super.setServicePath(servicePath);
    }

    @Override
    public Builder setHttpRequestInitializer(com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      return (Builder) super.setHttpRequestInitializer(httpRequestInitializer);
    }

    @Override
    public Builder setApplicationName(String applicationName) {
      return (Builder) super.setApplicationName(applicationName);
    }

    @Override
    public Builder setSuppressPatternChecks(boolean suppressPatternChecks) {
      return (Builder) super.setSuppressPatternChecks(suppressPatternChecks);
    }

    @Override
    public Builder setSuppressRequiredParameterChecks(boolean suppressRequiredParameterChecks) {
      return (Builder) super.setSuppressRequiredParameterChecks(suppressRequiredParameterChecks);
    }

    @Override
    public Builder setSuppressAllChecks(boolean suppressAllChecks) {
      return (Builder) super.setSuppressAllChecks(suppressAllChecks);
    }

    /**
     * Set the {@link YouTubeRequestInitializer}.
     *
     * @since 1.12
     */
    public Builder setYouTubeRequestInitializer(
        YouTubeRequestInitializer youtubeRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(youtubeRequestInitializer);
    }

    @Override
    public Builder setGoogleClientRequestInitializer(
        com.google.api.client.googleapis.services.GoogleClientRequestInitializer googleClientRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
    }
  }  
  
  /**
   * An accessor for creating requests from the LiveCuepoints collection.
   *
   * <p>The typical use is:</p>
   * <pre>
   *   {@code YouTube youtube = new YouTube(...);}
   *   {@code YouTube.LiveCuepoints.Insert request = youtube.liveCuepoints().insert(parameters ...)}
   * </pre>
   *
   * @return the resource collection
   */
  public LiveCuepoints liveCuepoints() {
    return new LiveCuepoints();
  }
  
  /**
   * The "liveCuepoints" collection of methods.
   */
  public class LiveCuepoints {
	  
  /**
   * Creates a broadcast.
   *
   * Create a request for the method "liveBroadcasts.insert".
   *
   * This request holds the parameters needed by the youtube server.  After setting any optional
   * parameters, call the {@link Insert#execute()} method to invoke the remote operation.
   *
   * @param content the {@link com.google.api.services.youtube.model.LiveBroadcast}
   * @return the request
   */
  public Insert insert(com.google.api.services.samples.youtube.cmdline.partner.model.LiveCuepoints content) throws java.io.IOException {
    Insert result = new Insert(content);
    initialize(result);
    return result;
  }

  public class Insert extends YouTubePartnerRequest<com.google.api.services.samples.youtube.cmdline.partner.model.LiveCuepoints> {

    private static final String REST_PATH = "liveCuepoints";

    /**
     * Creates a live cuepoint.
     *
     * Create a request for the method "liveCuepoints.insert".
     *
     * This request holds the parameters needed by the the youtube server.  After setting any optional
     * parameters, call the {@link Insert#execute()} method to invoke the remote operation. <p> {@link
     * Insert#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)} must
     * be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param content the {@link com.google.api.services.samples.youtube.cmdline.partner.model.LiveCuepoints}
     * @since 1.13
     */
    protected Insert(com.google.api.services.samples.youtube.cmdline.partner.model.LiveCuepoints content) {
      super(YouTubePartner.this, "POST", REST_PATH, content, com.google.api.services.samples.youtube.cmdline.partner.model.LiveCuepoints.class);
    }

    @Override
    public Insert setAlt(java.lang.String alt) {
      return (Insert) super.setAlt(alt);
    }

    @Override
    public Insert setFields(java.lang.String fields) {
      return (Insert) super.setFields(fields);
    }

    @Override
    public Insert setKey(java.lang.String key) {
      return (Insert) super.setKey(key);
    }

    @Override
    public Insert setOauthToken(java.lang.String oauthToken) {
      return (Insert) super.setOauthToken(oauthToken);
    }

    @Override
    public Insert setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (Insert) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public Insert setQuotaUser(java.lang.String quotaUser) {
      return (Insert) super.setQuotaUser(quotaUser);
    }

    @Override
    public Insert setUserIp(java.lang.String userIp) {
      return (Insert) super.setUserIp(userIp);
    }
    
    @com.google.api.client.util.Key
    private java.lang.String channelId;
    
    public java.lang.String getChannelId() {
      return channelId;
    }
    
    public Insert setChannelId(String channelId) {
	  this.channelId = channelId;
	  return this;
	}
    
    /**
     * Note: This parameter is intended exclusively for YouTube content partners.
     *
     * The onBehalfOfContentOwner parameter indicates that the request's authorization credentials
     * identify a YouTube CMS user who is acting on behalf of the content owner specified in the
     * parameter value. This parameter is intended for YouTube content partners that own and
     * manage many different YouTube channels. It allows content owners to authenticate once and
     * get access to all their video and channel data, without having to provide authentication
     * credentials for each individual channel. The CMS account that the user authenticates with
     * must be linked to the specified YouTube content owner.
     */
    @com.google.api.client.util.Key
    private java.lang.String onBehalfOfContentOwner;

    /** Note: This parameter is intended exclusively for YouTube content partners.

   The onBehalfOfContentOwner parameter indicates that the request's authorization credentials
   identify a YouTube CMS user who is acting on behalf of the content owner specified in the parameter
   value. This parameter is intended for YouTube content partners that own and manage many different
   YouTube channels. It allows content owners to authenticate once and get access to all their video
   and channel data, without having to provide authentication credentials for each individual channel.
   The CMS account that the user authenticates with must be linked to the specified YouTube content
   owner.
     */
    public java.lang.String getOnBehalfOfContentOwner() {
      return onBehalfOfContentOwner;
    }

    /**
     * Note: This parameter is intended exclusively for YouTube content partners.
     *
     * The onBehalfOfContentOwner parameter indicates that the request's authorization credentials
     * identify a YouTube CMS user who is acting on behalf of the content owner specified in the
     * parameter value. This parameter is intended for YouTube content partners that own and
     * manage many different YouTube channels. It allows content owners to authenticate once and
     * get access to all their video and channel data, without having to provide authentication
     * credentials for each individual channel. The CMS account that the user authenticates with
     * must be linked to the specified YouTube content owner.
     */
    public Insert setOnBehalfOfContentOwner(java.lang.String onBehalfOfContentOwner) {
      this.onBehalfOfContentOwner = onBehalfOfContentOwner;
      return this;
    }

    @Override
    public Insert set(String parameterName, Object value) {
      return (Insert) super.set(parameterName, value);
    }
  }
  }
}
