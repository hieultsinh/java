package com.pubnub.api;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.pubnub.api.matchers.JSONAssert.assertJSONArrayHas;
import static com.pubnub.api.matchers.JSONAssert.assertJSONArrayHasNo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class NamespaceTest {
    Pubnub pubnub = new Pubnub("demo", "demo");
    double random;

    @Before
    public void setUp() {
        pubnub.setOrigin("dara24.devbuild");
        pubnub.setCacheBusting(false);

        random = Math.random();
    }

    @Test
    public void testGetAllNamespacesAndRemoveThem() throws InterruptedException {
        final CountDownLatch latch1 = new CountDownLatch(3);
        final CountDownLatch latch2 = new CountDownLatch(1);
        final CountDownLatch latch3 = new CountDownLatch(3);
        final CountDownLatch latch4 = new CountDownLatch(1);

        final TestHelper.SimpleCallback cb1 = new TestHelper.SimpleCallback(latch1);
        final TestHelper.SimpleCallback cb2 = new TestHelper.SimpleCallback(latch2);
        final TestHelper.SimpleCallback cb3 = new TestHelper.SimpleCallback(latch3);
        final TestHelper.SimpleCallback cb4 = new TestHelper.SimpleCallback(latch4);

        String[] groups = new String[]{"jtest1" + random, "jtest2" + random, "jtest3" + random};
        String[] namespaces = new String[]{"jspace1" + random, "jspace2" + random, "jspace13" + random};

        // add
        for (int i = 0; i < groups.length; i++) {
            String group = groups[i];
            String namespace = namespaces[i];

            pubnub.addChannelToGroup(namespace, group, "ch1", cb1);
        }

        latch1.await(10, TimeUnit.SECONDS);

        // get
        pubnub.namespaces(cb2);
        latch2.await(10, TimeUnit.SECONDS);

        JSONArray result = (JSONArray) cb2.getResponse();

        assertFalse("Error is thrown", cb1.responseIsError());
        assertEquals("OK", cb1.getResponse());

        assertJSONArrayHas(namespaces[0], result);
        assertJSONArrayHas(namespaces[1], result);
        assertJSONArrayHas(namespaces[2], result);

        // remove
        pubnub.removeNamespace(namespaces[0], cb3);
        pubnub.removeNamespace(namespaces[1], cb3);
        pubnub.removeNamespace(namespaces[2], cb3);

        latch3.await(10, TimeUnit.SECONDS);

        // get again
        pubnub.namespaces(cb4);
        latch4.await(10, TimeUnit.SECONDS);

        result = (JSONArray) cb4.getResponse();

        assertFalse("Error is thrown", cb3.responseIsError());
        assertEquals("OK", cb3.getResponse());

        assertJSONArrayHasNo(namespaces[0], result);
        assertJSONArrayHasNo(namespaces[1], result);
        assertJSONArrayHasNo(namespaces[2], result);
    }
}
