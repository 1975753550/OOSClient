package com.chinatelecom.util;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.ssl.TrustStrategy;

public class AnyTrustStrategy implements TrustStrategy{

    @Override
    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        return true;
    }
}
