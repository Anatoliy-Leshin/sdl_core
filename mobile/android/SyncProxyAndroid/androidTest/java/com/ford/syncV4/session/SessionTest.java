package com.ford.syncV4.session;

import android.test.AndroidTestCase;

import com.ford.syncV4.protocol.enums.ServiceType;
import com.ford.syncV4.service.Service;

/**
 * Created by Andrew Batutin on 1/21/14
 */
public class SessionTest extends AndroidTestCase {

    public static final String APP_ID = "APP_ID";
    public static final String APP_ID_DEFAULT = "";
    public static final byte SESSION_ID = 0x01;

    public void testSessionIsCreated() throws Exception {
        Session session = new Session();
        assertNotNull("syncSession object should be created",session);
    }

    public void testServiceAddedToServiceList() throws Exception {
        Session session = new Session();
        Service service = new Service();
        session.addService(service);
        assertTrue("service should be at list", session.hasService(service));
    }

    public void testServiceRemovedFromServiceList() throws Exception {
        Session session = new Session();
        Service service = new Service();
        session.addService(service);
        boolean res = session.removeService(service);
        assertTrue("element should be removed", res);
        assertFalse("service list should be empty", session.hasService(service));
    }

<<<<<<< HEAD
    public void testInitialSessionCreationCreatesRPCService() throws Exception {
        Session session = Session.createSession(ServiceType.RPC, (byte) 0, false);
        Service service = session.getServiceList().get(0);
        assertEquals("currentSession id should be SESSION_ID", (byte) 0, session.getSessionId());
        assertEquals("should be RPC service", ServiceType.RPC, service.getServiceType());
        assertEquals("service should belong to the currentSession", session, service.getSession());
    }

    public void testRemoveServiceRemovesService() throws Exception {
        Session session = Session.createSession(ServiceType.RPC, (byte) 0, false);
=======
    public void testSessionAddRPCService() throws Exception {
        Session session = SessionTest.getInitializedSession();
        Service service = new Service();
        service.setServiceType(ServiceType.RPC);
        service.setAppId(SessionTest.APP_ID);
        session.addService(service);

        Service rpcService = session.getServiceByAppId(SessionTest.APP_ID, ServiceType.RPC);
        assertNotNull(rpcService);
        assertEquals(SessionTest.APP_ID, rpcService.getAppId());
        assertEquals(ServiceType.RPC, rpcService.getServiceType());
    }

    public void testRemoveServiceRemovesService() throws Exception {
        Session session = SessionTest.getInitializedSession();
>>>>>>> develop
        Service service = new Service();
        service.setAppId(SessionTest.APP_ID);
        service.setServiceType(ServiceType.RPC);
        session.addService(service);
        assertTrue("service should be removed", session.removeService(service));
    }

    public void testStopSessionClearsServiceList() throws Exception {
<<<<<<< HEAD
        Session session = Session.createSession(ServiceType.RPC, (byte) 10, false);
        session.stopSession();
        assertEquals("service list should be 0",  0, session.getServiceList().size());
        assertEquals("session id should be 0",0, session.getSessionId());
=======
        Session session = getInitializedSession();
        session.stopSession(APP_ID);
        assertEquals("service list should be 0", 0, session.getServicesNumber());
        assertEquals("session id should be 0", 0, session.getSessionIdByAppId(APP_ID));
>>>>>>> develop
    }

    public void testEmptyServicesList() {
        Session session = getInitializedSession();
        assertTrue(session.isServicesEmpty());
    }

    public void testEmptyServicesListWithNonEmptyList() {
        Session session = getInitializedSession();
        session.addService(session.createService(SessionTest.APP_ID, ServiceType.Audio_Service));
        assertFalse(session.isServicesEmpty());
    }

    public void testHasServiceByCorrectType() {
        Session session = getInitializedSession();
        Service service = session.createService(SessionTest.APP_ID, ServiceType.Audio_Service);
        service.setAppId(SessionTest.APP_ID);
        session.addService(service);
        assertTrue(session.hasService(APP_ID, ServiceType.Audio_Service));
    }

    public void testHasServiceByIncorrectType() {
        Session session = getInitializedSession();
        session.createService(SessionTest.APP_ID, ServiceType.Audio_Service);
        assertFalse(session.hasService(APP_ID, ServiceType.Mobile_Nav));
    }

    public void testPreventAddServiceWithSameType() {
        Session session = new Session();
        Service service_A = session.createService(SessionTest.APP_ID, ServiceType.Audio_Service);
        Service service_B = session.createService(SessionTest.APP_ID, ServiceType.Audio_Service);
        Service service_C = session.createService(SessionTest.APP_ID, ServiceType.Audio_Service);
        session.addService(service_A);
        session.addService(service_B);
        session.addService(service_C);
        assertEquals(1, session.getServicesNumber());
    }

    public static Session getInitializedSession() {
        Session session = new Session();
        session.addAppId(APP_ID);
        session.updateSessionId(SESSION_ID);
        return session;
    }
}