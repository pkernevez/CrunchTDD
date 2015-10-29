package com.octo.softshake.crunch.stock;

import junit.framework.TestCase;
import org.apache.crunch.Pair;
import org.apache.crunch.impl.mem.emit.InMemoryEmitter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.octo.softshake.crunch.stock.SmartUtil.parse;
import static com.octo.softshake.crunch.stock.SmartUtil.toStock;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by pke on 28.09.15.
 */
public class StockFillerTest extends TestCase {

    private ByteArrayOutputStream out;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        out = new ByteArrayOutputStream();
    }


    // Permet de savoir si 2 Deltas sont rataché au même stock
    public void test_same_stock() {
        Delta mvt = new Delta("CP", 20572, 1, 1001, "10/03/2015", 1);
        assertTrue(mvt.isSameStock(new Delta("CP", 20572, 1, 1001, "10/04/2015", 1)));
        assertTrue(mvt.isSameStock(new Delta("CP", 20572, 1, 1001, "10/03/2015", 2)));
    }

    public void test_different_stock() {
        Delta mvt = new Delta("CP", 20572, 1, 1001, "10/03/2015", 1);
        assertFalse(mvt.isSameStock(new Delta("XX", 20572, 1, 1001, "10/03/2015", 1)));
        assertFalse(mvt.isSameStock(new Delta("CP", 20571, 1, 1001, "10/03/2015", 1)));
        assertFalse(mvt.isSameStock(new Delta("CP", 20572, 2, 1001, "10/03/2015", 1)));
        assertFalse(mvt.isSameStock(new Delta("CP", 20572, 1, 1002, "10/03/2015", 1)));
        assertFalse(mvt.isSameStock(null));
    }

    public void test_decreaseDate() {
        Delta mvt = new Delta("CP", 20572, 1, 17, "10/11/2015", 1);
        Delta newMvt = mvt.decreaseDate();
        assertEquals("09/11/2015", newMvt.date);
        newMvt.date = "30/03/2015";
        newMvt = newMvt.decreaseDate();
        assertEquals("29/03/2015", newMvt.date);
    }

    public void test_remove_ok() {
        Delta mvt1 = new Delta("CP", 20572, 1, 1001, "10/03/2015", 1);
        Delta mvt2 = new Delta("CP", 20572, 1, 1001, "10/03/2015", 2);
        assertEquals(-1, mvt1.remove(mvt2).delta);
    }

    public void test_remove_failed_if_not_same_stock() {
        Delta mvt1 = new Delta("CP", 1001, 1, 20572, "10/03/2015", 1);
        Delta mvt2 = new Delta("CP", 1001, 1, 20571, "10/03/2015", 1);
        try {
            Delta mvt = mvt1.remove(mvt2);
            fail("Should not pass !");
        } catch (RuntimeException e) {
            assertEquals("Invalide Stock for operation", e.getMessage());
        }
    }

    //    enseigne, codemagasin, codeinternearticle, stockdate, (qte + total_delta) as qte, codecptstockmag, CurrentTime() as date_maj;
    public void test_to_str() {
        Delta mvt = new Delta("CP", 1001, 1, 20572, "10/03/2015", 3);
        assertEquals("3,1001,1,,10/03/2015,20572,CP", toStock(mvt));
    }

    private Pair<String, Iterable<Pair<Long, Delta>>> getSecondarySortDataFromSort(List<Delta> input) {
        List<Pair<Long, Delta>> listOfDelta = new ArrayList();
        for (Delta delta : input) {
            listOfDelta.add(new Pair<Long, Delta>(delta.getDateAsTime(), delta));
        }
        return new Pair<String, Iterable<Pair<Long, Delta>>>(input.get(0).getStockId(), listOfDelta);
    }

    private void checkProcess(Delta[] input, Delta[] expected) {
        StockFiller filler = new StockFiller();
        InMemoryEmitter<Delta> emitter = InMemoryEmitter.create();
        List<Delta> inputAsList = Arrays.asList(input);

        filler.process(getSecondarySortDataFromSort(inputAsList), emitter);
        List<Delta> out = emitter.getOutput();

        assertThat(out).isEqualTo(Arrays.asList(expected));
    }


    public void test_output_for_one_stock_and_1_Deltas() {
        Delta[] input = new Delta[]{new Delta("CP", 1001, 10, 20572, "11/01/2015", 3)};
        Delta[] expected = new Delta[]{
                new Delta("CP", 1001, 10, 20572, "11/01/2015", 3),
                new Delta("CP", 1001, 10, 20572, "10/01/2015", 3)
        };
        checkProcess(input, expected);
    }


    public void test_output_for_one_stock_and_2_Deltas_in_bad_order() {
        StockFiller filler = new StockFiller();
        InMemoryEmitter<Delta> emitter = InMemoryEmitter.create();
        List<Delta> input = Arrays.asList(
                new Delta("CP", 1001, 10, 20572, "10/01/2015", 3),
                new Delta("CP", 1001, 10, 20572, "11/01/2015", 3)
        );
        try {
            filler.process(getSecondarySortDataFromSort(input), emitter);
            fail("Should not pass !");
        } catch (RuntimeException e) {
            assertEquals("Invalid order for date : 10/01/2015 should be >= than 11/01/2015", e.getMessage());
        }
    }

    public void test_output_for_one_stock_and_2_Deltas_on_continuous_day() {
        Delta[] input = new Delta[]{
                new Delta("CP", 1001, 10, 20572, "11/01/2015", 3),
                new Delta("CP", 1001, 10, 20572, "10/01/2015", 1)
        };
        Delta[] expected = new Delta[]{
                new Delta("CP", 1001, 10, 20572, "11/01/2015", 3),
                new Delta("CP", 1001, 10, 20572, "10/01/2015", 3),
                new Delta("CP", 1001, 10, 20572, "09/01/2015", 2)
        };
        checkProcess(input, expected);
    }


    public void test_output_for_one_stock_and_2_Deltas_on_same_day() {
        Delta[] input = new Delta[]{
                new Delta("CP", 1001, 10, 20572, "11/01/2015", 5),
                new Delta("CP", 1001, 10, 20572, "10/01/2015", 1),
                new Delta("CP", 1001, 10, 20572, "10/01/2015", 1)
        };
        Delta[] expected = new Delta[]{
                new Delta("CP", 1001, 10, 20572, "11/01/2015", 5),
                new Delta("CP", 1001, 10, 20572, "10/01/2015", 5),
                new Delta("CP", 1001, 10, 20572, "09/01/2015", 3)
        };
        checkProcess(input, expected);
    }

    public void test_output_for_one_stock_and_2_Deltas_on_separate_day() {
        Delta[] input = new Delta[]{
                new Delta("CP", 1001, 10, 20572, "11/01/2015", 3),
                new Delta("CP", 1001, 10, 20572, "09/01/2015", 1)
        };
        Delta[] expected = new Delta[]{
                new Delta("CP", 1001, 10, 20572, "11/01/2015", 3),
                new Delta("CP", 1001, 10, 20572, "10/01/2015", 3),
                new Delta("CP", 1001, 10, 20572, "09/01/2015", 3),
                new Delta("CP", 1001, 10, 20572, "08/01/2015", 2)
        };
        checkProcess(input, expected);
    }

    public void test_output_for_one_stock_and_2_Deltas_on_continuous_day_and_2_months() {
        Delta[] input = new Delta[]{
                new Delta("CP", 1001, 10, 20572, "01/02/2015", 7),
                new Delta("CP", 1001, 10, 20572, "31/01/2015", 3)
        };
        Delta[] expected = new Delta[]{
                new Delta("CP", 1001, 10, 20572, "01/02/2015", 7),
                new Delta("CP", 1001, 10, 20572, "31/01/2015", 7),
                new Delta("CP", 1001, 10, 20572, "30/01/2015", 4)
        };
        checkProcess(input, expected);
    }


}

