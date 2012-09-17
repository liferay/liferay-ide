package com.liferay.ide.portal.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class PortalCoreTests
{
    @Test
    public void simpleTest() {
        Object model = new String("xx");
        assertNotNull(model);
        assertEquals("xx", model.toString());
    }

}
