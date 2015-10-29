package com.octo.softshake.crunch.stock;

import com.google.common.collect.Lists;
import org.apache.crunch.PCollection;
import org.apache.crunch.PTable;
import org.apache.crunch.Pair;
import org.apache.crunch.impl.mem.MemPipeline;
import org.apache.crunch.types.avro.Avros;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by pke on 04.10.15.
 */
public class StockJobTest {

    private final Delta[] DELTA_STOCKS = new Delta[]{
            new Delta("Migros", 1, 10, 1, "24/10/2015", +1),
            new Delta("Coop", 1, 20, 1, "24/10/2015", +18),
    };

    private final Delta[] DELTA_MOUVEMENTS = new Delta[]{
            new Delta("Migros", 1, 10, 1, "22/10/2015", -1),
            new Delta("Coop", 1, 20, 1, "21/10/2015", -10),
            new Delta("Coop", 1, 20, 1, "20/10/2015", +2),
    };

    private final Delta[] DELTA_UNSORTED = new Delta[]{
            new Delta("Migros", 1, 10, 1, "22/10/2015", -1),
            new Delta("Coop", 1, 20, 1, "24/10/2015", +18),
            new Delta("Migros", 1, 10, 1, "24/10/2015", +1)
    };

    private final Pair<String, Delta>[] DELTA_FILLED = new Pair[]{
            new Pair("Migros,1,10,1", new Delta("Migros", 1, 10, 1, "24/10/2015", 10)),
            new Pair("Migros,1,10,1", new Delta("Migros", 1, 10, 1, "22/10/2015", -1)),
            new Pair("Migros,1,10,1", new Delta("Migros", 1, 10, 1, "22/10/2015", -1)),
    };


    @Test
    public void testParseStock() throws Exception {
        PCollection<String> input = MemPipeline.collectionOf(new String[]{"1,1330,1,H15,28/08/2015,23002,CP", "2,1011,1,H15,28/08/2015,22603,CP"});
        List<Delta> result = Lists.newArrayList(StockJob.parseStock(input).materialize());

        assertEquals(2, result.size());
        assertEquals("CP", result.get(0).marque);
        assertEquals(23002, result.get(0).article);
    }

    @Test
    public void testParseMouvement() throws Exception {
        PCollection<String> input = MemPipeline.collectionOf(new String[]{"CP,08/01/2015,1304,19990,2,2,H14,CP_19990,08/01/2015_CP_19990",
                "CP,08/01/2015,1304,20361,2,2,H14,CP_20361,08/01/2015_CP_20361"});
        List<Delta> result = Lists.newArrayList(StockJob.parseMouvement(input).materialize());

        assertEquals(2, result.size());
        assertEquals("CP", result.get(0).marque);
        assertEquals(19990, result.get(0).article);
    }

    @Test
    public void testToStock() throws Exception {
        String[] data = new String[]{"1,1330,1,,28/08/2015,23002,CP", "2,1011,1,,28/08/2015,22603,CP"};
        PCollection<String> input = MemPipeline.collectionOf(data);
        PCollection<Delta> deltas = StockJob.parseStock(input);

        PCollection<String> stocks = StockJob.toStock(deltas);
        List<String> expected = Lists.newArrayList(stocks.materialize());

        assertEquals(Lists.newArrayList(data),expected);
    }

    @Test
    public void testMerge() throws Exception {
        PCollection<Delta> stocks = MemPipeline.collectionOf(DELTA_STOCKS);
        PCollection<Delta> mvts = MemPipeline.collectionOf(DELTA_MOUVEMENTS);
        List<Delta> result = Lists.newArrayList(StockJob.merge(stocks, mvts).materialize());

        assertEquals(5, result.size());

    }

    @Test
    public void testConvertTo() throws Exception {
        PCollection<Delta> unsorted = MemPipeline.typedCollectionOf(Avros.reflects(Delta.class), DELTA_UNSORTED);

        PTable<String, Pair<Long, Delta>> stringPairPTable = StockJob.convertTo(unsorted);
        List<Pair<String, Pair<Long, Delta>>> list = Lists.newArrayList(stringPairPTable.materialize());
        assertEquals(3, list.size());
        assertEquals("Migros,1,10,1", list.get(0).first());
        assertEquals((Long)(-DELTA_UNSORTED[0].getDateAsTime()), list.get(0).second().first());
        assertEquals(DELTA_UNSORTED[0], list.get(0).second().second());
    }
}