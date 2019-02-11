package com.chinatelecom.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SignatureV4Sample {
    /** Put your access key here **/
    private static final String accessKey = "6fc56a43ed2b551b94c6";
    /** Put your secret key here **/
    private static final String secretKey = "8d33af6fdd78361f9ef07d2f54522e6ea374cdc9";
    /** Put your bucket name here **/
    private static final String bucketName = "bgm";
    /** Put your object name here **/
    private static final String objectName = "23432";
    /** Put your object content here **/
    private static final String objectContent = "hello world!";
    /** The name of the region where the bucket is created. (e.g. cn-180622) **/
    private static final String regionName = "js";
    
    /** SHA256 hash of an empty request body **/
    public static final String EMPTY_BODY_SHA256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
    public static final String UNSIGNED_PAYLOAD = "UNSIGNED-PAYLOAD";
    public static final String SCHEME = "AWS4";
    public static final String ALGORITHM = "HMAC-SHA256";
    public static final String TERMINATOR = "aws4_request";
    public static final String SERVICE_NAME = "s3";
    
    private static SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");

    static{
        TimeZone utc = TimeZone.getTimeZone("UTC");
        timeFormatter.setTimeZone(utc);
        dateFormatter.setTimeZone(utc);
    }

    public static void main(String[] args) throws Exception {
        // set payloadHashEnabled is true if you want to compute the entire payload checksum and include it in signature calculation
        boolean payloadHashEnabled = true;
        putObject(bucketName, objectName, regionName, accessKey, secretKey, payloadHashEnabled);
        getObject(bucketName, objectName, regionName, accessKey, secretKey);
        String presignedUrl = getPresignedUrlToObject(bucketName, objectName, regionName, accessKey, secretKey);
        String response = invokeHttpRequest(new URL(presignedUrl), "GET", new HashMap<String, String>(), null);
        System.out.println("Get object by presignedUrl response:" + response);
    }
    
    public static void putObject(String bucketName, String objectName, String regionName, String accessKey, String secretKey, boolean payloadHashEnabled) {
        System.out.println("************************************************");
        System.out.println("*        Executing sample 'PutObject'          *");
        System.out.println("************************************************");
        URL endpointUrl;
        try {
            endpointUrl = new URL("http://oos-" + regionName + ".ctyunapi.cn/" + bucketName + "/" + objectName);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to parse service endpoint: " + e.getMessage());
        }
        String contentHashString = null;
        if (payloadHashEnabled) {
            // signed payload option, precompute hash of the body content
            byte[] contentHash = hash(objectContent);
            contentHashString = toHex(contentHash);
        } else {
            // unsigned payload option, use a constant string UNSIGNED-PAYLOAD
            contentHashString = UNSIGNED_PAYLOAD;
        }
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("x-amz-content-sha256", contentHashString);
        headers.put("content-length", String.valueOf(objectContent.getBytes().length));
        headers.put("x-amz-storage-class", "REDUCED_REDUNDANCY");
        
        String authorization = computeSignature(headers, 
                                                       null, // no query parameters
                                                       contentHashString, 
                                                       accessKey, 
                                                       secretKey,
                                                       endpointUrl,
                                                       "PUT",
                                                       SERVICE_NAME,
                                                       regionName);
                
        // place the computed signature into a formatted 'Authorization' header and call OOS
        headers.put("Authorization", authorization);
        String response = invokeHttpRequest(endpointUrl, "PUT", headers, objectContent);
        System.out.println("Put object response:"+response);
    }
    
    /**
     * Request the content of object from the given
     * bucket in the given region using virtual hosted-style object addressing.
     */
    public static void getObject(String bucketName, String objectName, String regionName, String accessKey, String secretKey) {
        System.out.println("************************************************");
        System.out.println("*        Executing sample 'getObject'          *");
        System.out.println("************************************************");
        // the region-specific endpoint to the target object expressed in path style
        URL endpointUrl;
        try {
            endpointUrl = new URL("http://" + bucketName + ".oos-" + regionName + ".ctyunapi.cn/" + objectName);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to parse service endpoint: " + e.getMessage());
        }
        
        // for a simple GET, we have no body so supply the precomputed 'empty' hash
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("x-amz-content-sha256", EMPTY_BODY_SHA256);

        String authorization = computeSignature(headers, 
                                                       null, // no query parameters
                                                       EMPTY_BODY_SHA256, 
                                                       accessKey, 
                                                       secretKey,
                                                       endpointUrl, 
                                                       "GET", 
                                                       SERVICE_NAME, 
                                                       regionName);
                
        // place the computed signature into a formatted 'Authorization' header and call OOS
        headers.put("Authorization", authorization);
        String response = invokeHttpRequest(endpointUrl, "GET", headers, null);
        System.out.println("Get object response:" + response);
    }
    
    /**
     * Construct a basic presigned url to the object in the
     * given bucket and region using path-style object addressing. The signature
     * V4 authorization data is embedded in the url as query parameters.
     */
    public static String getPresignedUrlToObject(String bucketName, String objectName, String regionName, String accessKey, String secretKey) {
        System.out.println("******************************************************");
        System.out.println("*    Executing sample 'GetPresignedUrlToObject'      *");
        System.out.println("******************************************************");
        URL endpointUrl;
        try {
            endpointUrl = new URL("http://oos-" + regionName + ".ctyunapi.cn/" + bucketName + "/" + objectName);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to parse service endpoint: " + e.getMessage());
        }
        
        // construct the query parameter string to accompany the url
        Map<String, String> queryParams = new HashMap<String, String>();
         
        // for SignatureV4, the max expiry for a presigned url is 7 days, expressed in seconds
        int expiresIn = 7 * 24 * 60 * 60;
        queryParams.put("X-Amz-Expires", "" + expiresIn);
        
        // we have no headers for this sample, but the next method 'computeSignatureForQueryparameters' will add 'host'
        Map<String, String> headers = new HashMap<String, String>();
        
        String authorizationQueryParameters = computeSignatureForQueryparameters(headers, 
                                                       queryParams,
                                                       UNSIGNED_PAYLOAD, // for a presigned url, use a constant string UNSIGNED-PAYLOAD
                                                       accessKey, 
                                                       secretKey,
                                                       endpointUrl, 
                                                       "GET", 
                                                       SERVICE_NAME, 
                                                       regionName);
                
        // build the presigned url to incorporate the authorization elements as query parameters
        String presignedUrl = endpointUrl.toString() + "?" + authorizationQueryParameters;
        System.out.println("Get presignedUrl:"+presignedUrl);
        return presignedUrl;
    }
    
    /**
     * Computes an AWS4 authorization for a request, suitable for embedding in
     * query parameters.
     * 
     * @param headers
     *            The request headers; 'Host' will be added to this set.
     * @param queryParameters
     *            Any query parameters that will be added to the endpoint. The
     *            parameters should be specified in canonical format.
     * @param bodyHash
     *            Precomputed SHA256 hash of the request body content; for a
     *            presigned url, use a constant string UNSIGNED-PAYLOAD.
     * @param accessKey
     *            The user's Access Key.
     * @param secretKey
     *            The user's Secret Key.
     * @param endpointUrl
     *            The request endpoint url.
     * @param httpMethod
     *            The request http method.
     * @param serviceName
     *            The signing name of the service, e.g. 's3'.
     * @param regionName
     *            The system name of the OOS region associated with the
     *            endpoint.
     * @return The computed authorization string for the request. This value
     *         needs to be set in the url as query parameters.
     */
    public static String computeSignatureForQueryparameters(Map<String, String> headers,
                                   Map<String, String> queryParameters,
                                   String bodyHash,
                                   String accessKey,
                                   String secretKey,
                                   URL endpointUrl,
                                   String httpMethod,
                                   String serviceName,
                                   String regionName) {
        // first get the date and time for the subsequent request, and convert
        // to ISO 8601 format
        // for use in signature generation
        Date now = new Date();
        String dateTimeStamp = timeFormatter.format(now);

        // make sure "Host" header is added
        String hostHeader = endpointUrl.getHost();
        int port = endpointUrl.getPort();
        if ( port > -1 ) {
            hostHeader = hostHeader.concat(":" + Integer.toString(port));
        }
        headers.put("Host", hostHeader);
        
        // canonicalized headers need to be expressed in the query
        // parameters processed in the signature
        String canonicalizedHeaderNames = getCanonicalizeHeaderNames(headers);
        String canonicalizedHeaders = getCanonicalizedHeaderString(headers);
        
        // we need scope as part of the query parameters
        String dateStamp = dateFormatter.format(now);
        String scope =  dateStamp + "/" + regionName + "/" + serviceName + "/" + TERMINATOR;
        
        // add the fixed authorization params required by Signature V4
        queryParameters.put("X-Amz-Algorithm", SCHEME + "-" + ALGORITHM);
        queryParameters.put("X-Amz-Credential", accessKey + "/" + scope);
        
        // x-amz-date is now added as a query parameter, but still need to be in ISO8601 basic form
        queryParameters.put("X-Amz-Date", dateTimeStamp);
        
        queryParameters.put("X-Amz-SignedHeaders", canonicalizedHeaderNames);
        
        // build the expanded canonical query parameter string that will go into the
        // signature computation
        String canonicalizedQueryParameters = getCanonicalizedQueryString(queryParameters);
        
        // express all the header and query parameter data as a canonical request string
        String canonicalRequest = getCanonicalRequest(endpointUrl, httpMethod,
                canonicalizedQueryParameters, canonicalizedHeaderNames,
                canonicalizedHeaders, bodyHash);
        System.out.println("--------- Canonical request --------");
        System.out.println(canonicalRequest);
        System.out.println("------------------------------------");
        
        // construct the string to be signed
        String stringToSign = getStringToSign(SCHEME, ALGORITHM, dateTimeStamp, scope, canonicalRequest);
        System.out.println("--------- String to sign -----------");
        System.out.println(stringToSign);
        System.out.println("------------------------------------");
        
        // compute the signing key
        byte[] kSecret = (SCHEME + secretKey).getBytes();
        byte[] kDate = sign(dateStamp, kSecret, "HmacSHA256");
        byte[] kRegion = sign(regionName, kDate, "HmacSHA256");
        byte[] kService = sign(serviceName, kRegion, "HmacSHA256");
        byte[] kSigning = sign(TERMINATOR, kService, "HmacSHA256");
        byte[] signature = sign(stringToSign, kSigning, "HmacSHA256");
        
        // form up the authorization parameters for the caller to place in the query string
        StringBuilder authString = new StringBuilder();
        
        authString.append("X-Amz-Algorithm=" + queryParameters.get("X-Amz-Algorithm"));
        authString.append("&X-Amz-Credential=" + queryParameters.get("X-Amz-Credential"));
        authString.append("&X-Amz-Date=" + queryParameters.get("X-Amz-Date"));
        authString.append("&X-Amz-Expires=" + queryParameters.get("X-Amz-Expires"));
        authString.append("&X-Amz-SignedHeaders=" + queryParameters.get("X-Amz-SignedHeaders"));
        authString.append("&X-Amz-Signature=" + toHex(signature));

        return authString.toString();
    }
    
    /**
     * Hashes the string contents (assumed to be UTF-8) using the SHA-256
     * algorithm.
     */
    public static byte[] hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes("UTF-8"));
            return md.digest();
        } catch (Exception e) {
            throw new RuntimeException("Unable to compute hash while signing request: " + e.getMessage(), e);
        }
    }
    
    /**
     * Converts byte data to a Hex-encoded string.
     *
     * @param data
     *            data to hex encode.
     *
     * @return hex-encoded string.
     */
    public static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(data[i]);
            if (hex.length() == 1) {
                // Append leading zero.
                sb.append("0");
            } else if (hex.length() == 8) {
                // Remove ff prefix from negative numbers.
                hex = hex.substring(6);
            }
            sb.append(hex);
        }
        return sb.toString().toLowerCase(Locale.getDefault());
    }
    
    /**
     * Computes an AWS4 signature for a request, ready for inclusion as an
     * 'Authorization' header.
     * 
     * @param headers
     *            The request headers; 'Host' and 'X-Amz-Date' will be added to
     *            this set.
     * @param queryParameters
     *            Any query parameters that will be added to the endpoint. The
     *            parameters should be specified in canonical format.
     * @param bodyHash
     *            Precomputed SHA256 hash of the request body content; this
     *            value should also be set as the header 'X-Amz-Content-SHA256'
     *            for non-streaming uploads.
     * @param accessKey
     *            The user's access Key.
     * @param secretKey
     *            The user's secret Key.
     * @param endpointUrl
     *            The request endpoint url.
     * @param httpMethod
     *            The request http method.
     * @param serviceName
     *            The signing name of the service, e.g. 's3'.
     * @param regionName
     *            The system name of the OOS region associated with the
     *            endpoint.
     * @return The computed authorization string for the request. This value
     *         needs to be set as the header 'Authorization' on the subsequent
     *         HTTP request.
     */
    public static String computeSignature(Map<String, String> headers,
                                   Map<String, String> queryParameters,
                                   String bodyHash,
                                   String accessKey,
                                   String secretKey,
                                   URL endpointUrl,
                                   String httpMethod,
                                   String serviceName,
                                   String regionName) {
        // first get the date and time for the subsequent request, and convert
        // to ISO 8601 format for use in signature generation
        Date now = new Date();
        String dateTimeStamp = timeFormatter.format(now);

        // update the headers with required 'x-amz-date' and 'host' values
        headers.put("x-amz-date", dateTimeStamp);
        
        String hostHeader = endpointUrl.getHost();
        int port = endpointUrl.getPort();
        if ( port > -1 ) {
            hostHeader = hostHeader.concat(":" + Integer.toString(port));
        }
        headers.put("Host", hostHeader);
        
        // canonicalize the headers; we need the set of header names as well as the
        // names and values to go into the signature process
        String canonicalizedHeaderNames = getCanonicalizeHeaderNames(headers);
        String canonicalizedHeaders = getCanonicalizedHeaderString(headers);
        
        // if any query string parameters have been supplied, canonicalize them
        String canonicalizedQueryParameters = getCanonicalizedQueryString(queryParameters);
        
        // canonicalize the various components of the request
        String canonicalRequest = getCanonicalRequest(endpointUrl, httpMethod,
                canonicalizedQueryParameters, canonicalizedHeaderNames,
                canonicalizedHeaders, bodyHash);
        System.out.println("--------- Canonical request --------");
        System.out.println(canonicalRequest);
        System.out.println("------------------------------------");
        
        // construct the string to be signed
        String dateStamp = dateFormatter.format(now);
        String scope =  dateStamp + "/" + regionName + "/" + serviceName + "/" + TERMINATOR;
        String stringToSign = getStringToSign(SCHEME, ALGORITHM, dateTimeStamp, scope, canonicalRequest);
        System.out.println("--------- String to sign -----------");
        System.out.println(stringToSign);
        System.out.println("------------------------------------");
        
        // compute the signing key
        byte[] kSecret = (SCHEME + secretKey).getBytes();
        byte[] kDate = sign(dateStamp, kSecret, "HmacSHA256");
        byte[] kRegion = sign(regionName, kDate, "HmacSHA256");
        byte[] kService = sign(serviceName, kRegion, "HmacSHA256");
        byte[] kSigning = sign(TERMINATOR, kService, "HmacSHA256");
        byte[] signature = sign(stringToSign, kSigning, "HmacSHA256");
        
        String credentialsAuthorizationHeader = "Credential=" + accessKey + "/" + scope;
        String signedHeadersAuthorizationHeader = "SignedHeaders=" + canonicalizedHeaderNames;
        String signatureAuthorizationHeader = "Signature=" + toHex(signature);

        String authorizationHeader = SCHEME + "-" + ALGORITHM + " "
                + credentialsAuthorizationHeader + ", "
                + signedHeadersAuthorizationHeader + ", "
                + signatureAuthorizationHeader;

        return authorizationHeader;
    }
    
    /**
     * Returns the canonical collection of header names that will be included in
     * the signature. The request headers in the list are the same headers that
     * you included in the CanonicalHeaders string.We suggest you consider
     * including all the header names in in the signing process in order to
     * prevent data tampering.
     */
    public static String getCanonicalizeHeaderNames(Map<String, String> headers) {
        List<String> sortedHeaders = new ArrayList<String>();
        sortedHeaders.addAll(headers.keySet());
        Collections.sort(sortedHeaders, String.CASE_INSENSITIVE_ORDER);

        StringBuilder buffer = new StringBuilder();
        for (String header : sortedHeaders) {
            if (buffer.length() > 0) buffer.append(";");
            buffer.append(header.toLowerCase());
        }

        return buffer.toString();
    }
    
    /**
     * Computes the canonical headers with values for the request.
     */
    public static String getCanonicalizedHeaderString(Map<String, String> headers) {
        if ( headers == null || headers.isEmpty() ) {
            return "";
        }
        
        // step1: sort the headers by case-insensitive order
        List<String> sortedHeaders = new ArrayList<String>();
        sortedHeaders.addAll(headers.keySet());
        Collections.sort(sortedHeaders, String.CASE_INSENSITIVE_ORDER);

        // step2: form the canonical header:value entries in sorted order. 
        // Multiple white spaces in the values should be compressed to a single 
        // space.
        StringBuilder buffer = new StringBuilder();
        for (String key : sortedHeaders) {
            buffer.append(key.toLowerCase().replaceAll("\\s+", " ") + ":" + headers.get(key).replaceAll("\\s+", " "));
            buffer.append("\n");
        }

        return buffer.toString();
    }
    
    /**
     * Examines the specified query string parameters and returns a
     * canonicalized form.
     * <p>
     * The canonicalized query string is formed by first sorting all the query
     * string parameters, then URI encoding both the key and value and then
     * joining them, in order, separating key value pairs with an '&'.
     *
     * @param parameters
     *            The query string parameters to be canonicalized.
     *
     * @return A canonicalized form for the specified query string parameters.
     */
    public static String getCanonicalizedQueryString(Map<String, String> parameters) {
        if ( parameters == null || parameters.isEmpty() ) {
            return "";
        }
        
        SortedMap<String, String> sorted = new TreeMap<String, String>();

        Iterator<Map.Entry<String, String>> pairs = parameters.entrySet().iterator();
        while (pairs.hasNext()) {
            Map.Entry<String, String> pair = pairs.next();
            String key = pair.getKey();
            String value = pair.getValue();
            sorted.put(urlEncode(key, false), urlEncode(value, false));
        }

        StringBuilder builder = new StringBuilder();
        pairs = sorted.entrySet().iterator();
        while (pairs.hasNext()) {
            Map.Entry<String, String> pair = pairs.next();
            builder.append(pair.getKey());
            builder.append("=");
            builder.append(pair.getValue());
            if (pairs.hasNext()) {
                builder.append("&");
            }
        }

        return builder.toString();
    }
    
    /**
     * Returns the canonical request string to go into the signer process; this 
       consists of several canonical sub-parts.
     * @return
     */
    public static String getCanonicalRequest(URL endpoint, 
                                         String httpMethod,
                                         String queryParameters, 
                                         String canonicalizedHeaderNames,
                                         String canonicalizedHeaders, 
                                         String bodyHash) {
        String canonicalRequest =
                        httpMethod + "\n" +
                        getCanonicalizedResourcePath(endpoint) + "\n" +
                        queryParameters + "\n" +
                        canonicalizedHeaders + "\n" +
                        canonicalizedHeaderNames + "\n" +
                        bodyHash;
        return canonicalRequest;
    }
    
    public static String getStringToSign(String scheme, String algorithm, String dateTime, String scope, String canonicalRequest) {
        String stringToSign =
                        scheme + "-" + algorithm + "\n" +
                        dateTime + "\n" +
                        scope + "\n" +
                        toHex(hash(canonicalRequest));
        return stringToSign;
    }
    
    /**
     * Returns the canonicalized resource path for the service endpoint.
     */
    public static String getCanonicalizedResourcePath(URL endpoint) {
        if ( endpoint == null ) {
            return "/";
        }
        String path = endpoint.getPath();
        if ( path == null || path.isEmpty() ) {
            return "/";
        }
        
        String encodedPath = urlEncode(path, true);
        if (encodedPath.startsWith("/")) {
            return encodedPath;
        } else {
            return "/".concat(encodedPath);
        }
    }
    
    public static String urlEncode(String url, boolean keepPathSlash) {
        String encoded;
        try {
            encoded = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding is not supported.", e);
        }
        if ( keepPathSlash ) {
            encoded = encoded.replace("%2F", "/");
        }
        return encoded;
    }
    
    public static byte[] sign(String stringData, byte[] key, String algorithm) {
        try {
            byte[] data = stringData.getBytes("UTF-8");
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key, algorithm));
            return mac.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("Unable to calculate a request signature: " + e.getMessage(), e);
        }
    }
    
    /**
     * Makes a http request to the specified endpoint
     */
    public static String invokeHttpRequest(URL endpointUrl,
                                         String httpMethod,
                                         Map<String, String> headers,
                                         String requestBody) {
        HttpURLConnection connection = createHttpConnection(endpointUrl, httpMethod, headers);
        try {
            if ( requestBody != null ) {
                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());
                wr.write(requestBody.getBytes());
                wr.flush();
                wr.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Request failed. " + e.getMessage(), e);
        }
        return executeHttpRequest(connection);
    }
    
    public static HttpURLConnection createHttpConnection(URL endpointUrl,
            String httpMethod, Map<String, String> headers) {
        try {
            HttpURLConnection connection = (HttpURLConnection) endpointUrl.openConnection();
            connection.setRequestMethod(httpMethod);

            if (headers != null) {
                System.out.println("--------- Request headers ---------");
                for (String headerKey : headers.keySet()) {
                    System.out.println(headerKey + ": " + headers.get(headerKey));
                    connection.setRequestProperty(headerKey, headers.get(headerKey));
                }
                System.out.println("------------------------------------");
            }

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            return connection;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Cannot create connection. " + e.getMessage(), e);
        }
    }
    
    public static String executeHttpRequest(HttpURLConnection connection) {
        try {
            // Get Response
            InputStream is;
            try {
                is = connection.getInputStream();
            } catch (IOException e) {
                is = connection.getErrorStream();
            }
            
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            throw new RuntimeException("Request failed. " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

}
