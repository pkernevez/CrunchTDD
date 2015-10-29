package com.octo.softshake.crunch.stock;

import org.apache.commons.io.FileUtils;
import org.apache.crunch.impl.mem.OctoMemPipeline;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.CrunchAssertions.assertThat;
import static org.junit.Assert.*;

public class StockJobIntTest {

    String in_stock = "src/test/resources/stock.txt";
    String in_mvt = "src/test/resources/mvt.txt";
    String out = "target/test/result";

    @Before
    public void setUp() throws Exception {
        FileUtils.deleteDirectory(new File(out));
    }

    @Test
    public void run_nominalCase() throws IOException {
        // Given
        StockJob job = new StockJob();

        // When
        int returnCode = job.run(OctoMemPipeline.getInstance(), in_stock, in_mvt, out);

        // Then
        assertEquals(0, returnCode);
        assertThat(out + "/out.txt").isEqualTo("src/test/resources/expected.txt");
    }
}
