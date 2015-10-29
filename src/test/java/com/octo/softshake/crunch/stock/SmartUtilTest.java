package com.octo.softshake.crunch.stock;

import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by pke on 06.10.15.
 */
public class SmartUtilTest {

    @Test
    public void testParseFormat() throws Exception {
        Date asDate = SmartUtil.parse("25/10/2015");
        String asString = SmartUtil.format(asDate);
        assertEquals("25/10/2015", asString);

    }

    @Test
    public void testBidon() throws Exception {
        if (null instanceof Object)
            System.out.print("we");

    }

    @Test
        public void testFromStock() throws Exception {
            Delta mvt = SmartUtil.fromStock("1,1330,1,H15,28/08/2015,23002,CP");

            assertEquals("CP", mvt.marque);
        assertEquals(23002, mvt.article);
        assertEquals(1330, mvt.magasin);
        assertEquals(1, mvt.typeStock);
        assertEquals("28/08/2015", mvt.date);
        assertEquals(1, mvt.delta);

    }

    @Test
    public void testFromMouvement() throws Exception {
        Delta mvt = SmartUtil.fromMouvement("CP,08/01/2015,1304,19797,2,3,H14,CP_19797,08/01/2015_CP_19797");

        assertEquals("CP", mvt.marque);
        assertEquals(19797, mvt.article);
        assertEquals(1304, mvt.magasin);
        assertEquals(2, mvt.typeStock);
        assertEquals("08/01/2015", mvt.date);
        assertEquals(3, mvt.delta);

    }
}