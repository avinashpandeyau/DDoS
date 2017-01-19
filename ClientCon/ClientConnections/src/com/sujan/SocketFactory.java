package com.sujan;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

/**
 * Created by sujan on 4/01/17.
 */
public class SocketFactory
{
    public static SSLSocket getSslSocket(ConnectionParameter parameter) throws Exception {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Certificate certificate;

        InputStream is = new BufferedInputStream(new FileInputStream("C:\\Users\\Avinash\\Desktop\\ca.crt"));
        certificate = certificateFactory.generateCertificate(is);

        KeyStore caKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        caKeyStore.load(null, null);
        caKeyStore.setCertificateEntry("ca-certificate", certificate);

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(caKeyStore);

        SSLContext sc = SSLContext.getInstance("TLSv1.2");
        sc.init(null,trustManagerFactory.getTrustManagers(),null);
        return (SSLSocket)sc.getSocketFactory().createSocket(parameter.getHostname(), parameter.getPort());
    }

    public static Socket getSocket(ConnectionParameter parameter) throws Exception {
        return new Socket(parameter.getHostname(), parameter.getPort());
    }
}
