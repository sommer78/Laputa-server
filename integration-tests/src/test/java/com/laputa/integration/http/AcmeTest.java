package com.laputa.integration.http;

import com.laputa.integration.BaseTest;
import com.laputa.server.Holder;
import com.laputa.server.SslContextHolder;
import com.laputa.server.acme.AcmeClient;
import com.laputa.server.acme.ContentHolder;
import com.laputa.server.api.http.HttpAPIServer;
import com.laputa.server.core.BaseServer;
import com.laputa.server.workers.CertificateRenewalWorker;
import com.laputa.utils.ServerProperties;
import org.apache.commons.lang.SystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 07.01.16.
 */
@RunWith(MockitoJUnitRunner.class)
public class AcmeTest extends BaseTest {

    private BaseServer httpServer;
    private Holder holder2;

    @After
    public void shutdown() throws Exception {
        httpServer.close();
    }

    @Before
    public void init() throws Exception {
        ServerProperties properties2 = new ServerProperties("no_certs.properties");
        //disable native linux epoll transport for non linux envs.
        if (!SystemUtils.IS_OS_LINUX) {
            System.out.println("WARNING : DISABLING NATIVE EPOLL TRANSPORT. SYSTEM : " + SystemUtils.OS_NAME);
            properties2.put("enable.native.epoll.transport", false);
        }
        this.holder2 = new Holder(properties2, twitterWrapper, mailWrapper, gcmWrapper, smsWrapper, "no-db.properties");
        httpServer = new HttpAPIServer(holder2).start();
    }

    @Override
    public String getDataFolder() {
        return getRelativeDataFolder("/profiles");
    }

    @Test
    public void testCorrectContext() {
        SslContextHolder sslContextHolder = holder2.sslContextHolder;
        assertNotNull(sslContextHolder);
        assertTrue(sslContextHolder.isAutoGenerationEnabled);
        assertTrue(sslContextHolder.isNeedInitializeOnStart);
        assertNotNull(sslContextHolder.acmeClient);
    }

    @Test
    @Ignore
    public void testCreateCertificates() throws Exception {
        final String STAGING = "acme://letsencrypt.org/staging";
        ContentHolder contentHolder = holder2.sslContextHolder.contentHolder;
        AcmeClient acmeClient = new AcmeClient(STAGING, "test@laputa.cc", "test.laputa.cc", contentHolder);
        assertTrue(acmeClient.requestCertificate());
    }

    @Test
    @Ignore
    public void testWorker() throws Exception {
        AcmeClient acmeClient = Mockito.mock(AcmeClient.class);
        CertificateRenewalWorker certificateRenewalWorker = new CertificateRenewalWorker(acmeClient, 7);
        certificateRenewalWorker.run();
        verify(acmeClient, times(0)).requestCertificate();

        certificateRenewalWorker = new CertificateRenewalWorker(acmeClient, 100);
        certificateRenewalWorker.run();
        verify(acmeClient, times(1)).requestCertificate();
    }
}
