package com.ym.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.HttpStack;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 *
 * Created by Jake M on 10.12.13
 */

/**
 * An {@link HttpStack} based on {@link java.net.HttpURLConnection}.
 */
class RedirectHurlStack implements HttpStack {

    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    /**
     * An interface for transforming URLs before use.
     */
    public interface UrlRewriter {

        /**
         * Returns a URL to use instead of the provided one, or null to indicate this URL should not
         * be used at all.
         */
        public String rewriteUrl(String originalUrl);
    }

    private final UrlRewriter mUrlRewriter;
    private final SSLSocketFactory mSslSocketFactory;

    public RedirectHurlStack() {
        this(null);
    }

    /**
     * @param urlRewriter Rewriter to use for request URLs
     */
    public RedirectHurlStack(UrlRewriter urlRewriter) {
        this(urlRewriter, null);
    }

    /**
     * @param urlRewriter      Rewriter to use for request URLs
     * @param sslSocketFactory SSL factory to use for HTTPS connections
     */
    public RedirectHurlStack(UrlRewriter urlRewriter, SSLSocketFactory sslSocketFactory) {
        mUrlRewriter = urlRewriter;
        mSslSocketFactory = sslSocketFactory;
    }

    @Override
    public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders)
            throws IOException, AuthFailureError {
        String url = request.getUrl();
        return performRequest(request, additionalHeaders, url);
    }

    private HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders,
            String url) throws AuthFailureError, IOException {
        HashMap<String, String> map = new HashMap<String, String>();
        map.putAll(request.getHeaders());
        map.putAll(additionalHeaders);
        if (mUrlRewriter != null) {
            String rewritten = mUrlRewriter.rewriteUrl(url);
            if (rewritten == null) {
                throw new IOException("URL blocked by rewriter: " + url);
            }
            url = rewritten;
        }
        URL parsedUrl = new URL(url);
        HttpURLConnection connection = openConnection(parsedUrl, request);
        for (String headerName : map.keySet()) {
            connection.addRequestProperty(headerName, map.get(headerName));
        }
        setConnectionParametersForRequest(connection, request);
        // Initialize HttpResponse with data from the HttpURLConnection.
        ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);
        int responseCode = connection.getResponseCode();
        if (responseCode == -1) {
            // -1 is returned by getResponseCode() if the response code could not be retrieved.
            // Signal to the caller that something was wrong with the connection.
            throw new IOException("Could not retrieve response code from HttpUrlConnection.");
        }

        if (responseCode == HttpStatus.SC_MOVED_PERMANENTLY
                || responseCode == HttpStatus.SC_MOVED_TEMPORARILY) {

            String newUrl = connection.getHeaderField("Location");
            return performRequest(request, additionalHeaders, newUrl);
        }

        StatusLine responseStatus = new BasicStatusLine(protocolVersion,
                connection.getResponseCode(), connection.getResponseMessage());
        BasicHttpResponse response = new BasicHttpResponse(responseStatus);
        response.setEntity(entityFromConnection(connection));
        for (Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
            if (header.getKey() != null) {
                Header h = new BasicHeader(header.getKey(), header.getValue().get(0));
                response.addHeader(h);
            }
        }
        return response;
    }

    /**
     * Initializes an {@link org.apache.http.HttpEntity} from the given {@link java.net.HttpURLConnection}.
     *
     * @return an HttpEntity populated with data from <code>connection</code>.
     */
    private static HttpEntity entityFromConnection(HttpURLConnection connection) {
        BasicHttpEntity entity = new BasicHttpEntity();
        InputStream inputStream;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException ioe) {
            inputStream = connection.getErrorStream();
        }
        entity.setContent(inputStream);
        entity.setContentLength(connection.getContentLength());
        entity.setContentEncoding(connection.getContentEncoding());
        entity.setContentType(connection.getContentType());
        return entity;
    }

    /**
     * Create an {@link java.net.HttpURLConnection} for the specified {@code url}.
     */
    protected HttpURLConnection createConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }

    /**
     * Opens an {@link java.net.HttpURLConnection} with parameters.
     *
     * @return an open connection
     */
    private HttpURLConnection openConnection(URL url, Request<?> request) throws IOException {
        HttpURLConnection connection = createConnection(url);

        int timeoutMs = request.getTimeoutMs();
        connection.setConnectTimeout(timeoutMs);
        connection.setReadTimeout(timeoutMs);
        connection.setUseCaches(false);
        connection.setDoInput(true);
//        connection.setInstanceFollowRedirects(true);

        // use caller-provided custom SslSocketFactory, if any, for HTTPS
        if ("https".equals(url.getProtocol()) && mSslSocketFactory != null) {
            ((HttpsURLConnection) connection).setSSLSocketFactory(mSslSocketFactory);
        }

        return connection;
    }

    @SuppressWarnings("deprecation")
    /* package */ static void setConnectionParametersForRequest(HttpURLConnection connection,
            Request<?> request) throws IOException, AuthFailureError {
        switch (request.getMethod()) {
            case Method.DEPRECATED_GET_OR_POST:
                // This is the deprecated way that needs to be handled for backwards compatibility.
                // If the request's post body is null, then the assumption is that the request is
                // GET.  Otherwise, it is assumed that the request is a POST.
                byte[] postBody = request.getPostBody();
                if (postBody != null) {
                    // Prepare output. There is no need to set Content-Length explicitly,
                    // since this is handled by HttpURLConnection using the size of the prepared
                    // output stream.
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.addRequestProperty(HEADER_CONTENT_TYPE,
                            request.getPostBodyContentType());
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.write(postBody);
                    out.close();
                }
                break;
            case Method.GET:
                // Not necessary to set the request method because connection defaults to GET but
                // being explicit here.
                connection.setRequestMethod("GET");
                break;
            case Method.DELETE:
                connection.setRequestMethod("DELETE");
                break;
            case Method.POST:
                connection.setRequestMethod("POST");
                addBodyIfExists(connection, request);
                break;
            case Method.PUT:
                connection.setRequestMethod("PUT");
                addBodyIfExists(connection, request);
                break;
            default:
                throw new IllegalStateException("Unknown method type.");
        }
    }

    private static void addBodyIfExists(HttpURLConnection connection, Request<?> request)
            throws IOException, AuthFailureError {
        byte[] body = request.getBody();
        if (body != null) {
            connection.setDoOutput(true);
            connection.addRequestProperty(HEADER_CONTENT_TYPE, request.getBodyContentType());
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(body);
            out.close();
        }
    }
}

