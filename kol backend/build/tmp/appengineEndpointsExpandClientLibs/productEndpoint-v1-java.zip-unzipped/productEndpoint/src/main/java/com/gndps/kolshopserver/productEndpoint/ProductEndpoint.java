/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2015-08-03 17:34:38 UTC)
 * on 2015-09-30 at 19:16:29 UTC 
 * Modify at your own risk.
 */

package com.gndps.kolshopserver.productEndpoint;

/**
 * Service definition for ProductEndpoint (v1).
 *
 * <p>
 * This is an API
 * </p>
 *
 * <p>
 * For more information about this service, see the
 * <a href="" target="_blank">API Documentation</a>
 * </p>
 *
 * <p>
 * This service uses {@link ProductEndpointRequestInitializer} to initialize global parameters via its
 * {@link Builder}.
 * </p>
 *
 * @since 1.3
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public class ProductEndpoint extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient {

  // Note: Leave this static initializer at the top of the file.
  static {
    com.google.api.client.util.Preconditions.checkState(
        com.google.api.client.googleapis.GoogleUtils.MAJOR_VERSION == 1 &&
        com.google.api.client.googleapis.GoogleUtils.MINOR_VERSION >= 15,
        "You are currently running with version %s of google-api-client. " +
        "You need at least version 1.15 of google-api-client to run version " +
        "1.20.0 of the productEndpoint library.", com.google.api.client.googleapis.GoogleUtils.VERSION);
  }

  /**
   * The default encoded root URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_ROOT_URL = "https://kol-server.appspot.com/_ah/api/";

  /**
   * The default encoded service path of the service. This is determined when the library is
   * generated and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_SERVICE_PATH = "productEndpoint/v1/";

  /**
   * The default encoded base URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   */
  public static final String DEFAULT_BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

  /**
   * Constructor.
   *
   * <p>
   * Use {@link Builder} if you need to specify any of the optional parameters.
   * </p>
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
  public ProductEndpoint(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
      com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
    this(new Builder(transport, jsonFactory, httpRequestInitializer));
  }

  /**
   * @param builder builder
   */
  ProductEndpoint(Builder builder) {
    super(builder);
  }

  @Override
  protected void initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest<?> httpClientRequest) throws java.io.IOException {
    super.initialize(httpClientRequest);
  }

  /**
   * Create a request for the method "getProductsList".
   *
   * This request holds the parameters needed by the productEndpoint server.  After setting any
   * optional parameters, call the {@link GetProductsList#execute()} method to invoke the remote
   * operation.
   *
   * @param shopId
   * @param startIndex
   * @param count
   * @return the request
   */
  public GetProductsList getProductsList(java.lang.Integer shopId, java.lang.Integer startIndex, java.lang.Integer count) throws java.io.IOException {
    GetProductsList result = new GetProductsList(shopId, startIndex, count);
    initialize(result);
    return result;
  }

  public class GetProductsList extends ProductEndpointRequest<com.gndps.kolshopserver.productEndpoint.model.ProductCollection> {

    private static final String REST_PATH = "productcollection/{shopId}/{startIndex}/{count}";

    /**
     * Create a request for the method "getProductsList".
     *
     * This request holds the parameters needed by the the productEndpoint server.  After setting any
     * optional parameters, call the {@link GetProductsList#execute()} method to invoke the remote
     * operation. <p> {@link GetProductsList#initialize(com.google.api.client.googleapis.services.Abst
     * ractGoogleClientRequest)} must be called to initialize this instance immediately after invoking
     * the constructor. </p>
     *
     * @param shopId
     * @param startIndex
     * @param count
     * @since 1.13
     */
    protected GetProductsList(java.lang.Integer shopId, java.lang.Integer startIndex, java.lang.Integer count) {
      super(ProductEndpoint.this, "GET", REST_PATH, null, com.gndps.kolshopserver.productEndpoint.model.ProductCollection.class);
      this.shopId = com.google.api.client.util.Preconditions.checkNotNull(shopId, "Required parameter shopId must be specified.");
      this.startIndex = com.google.api.client.util.Preconditions.checkNotNull(startIndex, "Required parameter startIndex must be specified.");
      this.count = com.google.api.client.util.Preconditions.checkNotNull(count, "Required parameter count must be specified.");
    }

    @Override
    public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
      return super.executeUsingHead();
    }

    @Override
    public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
      return super.buildHttpRequestUsingHead();
    }

    @Override
    public GetProductsList setAlt(java.lang.String alt) {
      return (GetProductsList) super.setAlt(alt);
    }

    @Override
    public GetProductsList setFields(java.lang.String fields) {
      return (GetProductsList) super.setFields(fields);
    }

    @Override
    public GetProductsList setKey(java.lang.String key) {
      return (GetProductsList) super.setKey(key);
    }

    @Override
    public GetProductsList setOauthToken(java.lang.String oauthToken) {
      return (GetProductsList) super.setOauthToken(oauthToken);
    }

    @Override
    public GetProductsList setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (GetProductsList) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public GetProductsList setQuotaUser(java.lang.String quotaUser) {
      return (GetProductsList) super.setQuotaUser(quotaUser);
    }

    @Override
    public GetProductsList setUserIp(java.lang.String userIp) {
      return (GetProductsList) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Integer shopId;

    /**

     */
    public java.lang.Integer getShopId() {
      return shopId;
    }

    public GetProductsList setShopId(java.lang.Integer shopId) {
      this.shopId = shopId;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.Integer startIndex;

    /**

     */
    public java.lang.Integer getStartIndex() {
      return startIndex;
    }

    public GetProductsList setStartIndex(java.lang.Integer startIndex) {
      this.startIndex = startIndex;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.Integer count;

    /**

     */
    public java.lang.Integer getCount() {
      return count;
    }

    public GetProductsList setCount(java.lang.Integer count) {
      this.count = count;
      return this;
    }

    @Override
    public GetProductsList set(String parameterName, Object value) {
      return (GetProductsList) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "saveProduct".
   *
   * This request holds the parameters needed by the productEndpoint server.  After setting any
   * optional parameters, call the {@link SaveProduct#execute()} method to invoke the remote
   * operation.
   *
   * @param content the {@link com.gndps.kolshopserver.productEndpoint.model.Product}
   * @return the request
   */
  public SaveProduct saveProduct(com.gndps.kolshopserver.productEndpoint.model.Product content) throws java.io.IOException {
    SaveProduct result = new SaveProduct(content);
    initialize(result);
    return result;
  }

  public class SaveProduct extends ProductEndpointRequest<com.gndps.kolshopserver.productEndpoint.model.RestCallResponse> {

    private static final String REST_PATH = "saveProduct";

    /**
     * Create a request for the method "saveProduct".
     *
     * This request holds the parameters needed by the the productEndpoint server.  After setting any
     * optional parameters, call the {@link SaveProduct#execute()} method to invoke the remote
     * operation. <p> {@link
     * SaveProduct#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param content the {@link com.gndps.kolshopserver.productEndpoint.model.Product}
     * @since 1.13
     */
    protected SaveProduct(com.gndps.kolshopserver.productEndpoint.model.Product content) {
      super(ProductEndpoint.this, "POST", REST_PATH, content, com.gndps.kolshopserver.productEndpoint.model.RestCallResponse.class);
    }

    @Override
    public SaveProduct setAlt(java.lang.String alt) {
      return (SaveProduct) super.setAlt(alt);
    }

    @Override
    public SaveProduct setFields(java.lang.String fields) {
      return (SaveProduct) super.setFields(fields);
    }

    @Override
    public SaveProduct setKey(java.lang.String key) {
      return (SaveProduct) super.setKey(key);
    }

    @Override
    public SaveProduct setOauthToken(java.lang.String oauthToken) {
      return (SaveProduct) super.setOauthToken(oauthToken);
    }

    @Override
    public SaveProduct setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (SaveProduct) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public SaveProduct setQuotaUser(java.lang.String quotaUser) {
      return (SaveProduct) super.setQuotaUser(quotaUser);
    }

    @Override
    public SaveProduct setUserIp(java.lang.String userIp) {
      return (SaveProduct) super.setUserIp(userIp);
    }

    @Override
    public SaveProduct set(String parameterName, Object value) {
      return (SaveProduct) super.set(parameterName, value);
    }
  }

  /**
   * Builder for {@link ProductEndpoint}.
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

    /** Builds a new instance of {@link ProductEndpoint}. */
    @Override
    public ProductEndpoint build() {
      return new ProductEndpoint(this);
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
     * Set the {@link ProductEndpointRequestInitializer}.
     *
     * @since 1.12
     */
    public Builder setProductEndpointRequestInitializer(
        ProductEndpointRequestInitializer productendpointRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(productendpointRequestInitializer);
    }

    @Override
    public Builder setGoogleClientRequestInitializer(
        com.google.api.client.googleapis.services.GoogleClientRequestInitializer googleClientRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
    }
  }
}
