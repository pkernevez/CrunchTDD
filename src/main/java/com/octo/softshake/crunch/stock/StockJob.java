package com.octo.softshake.crunch.stock;

import org.apache.crunch.*;
import org.apache.crunch.impl.mr.MRPipeline;
import org.apache.crunch.lib.SecondarySort;
import org.apache.crunch.types.avro.Avros;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class StockJob extends Configured implements Tool {


    public static PCollection<Delta> parseStock(PCollection<String> line) {
        return line.parallelDo(new DoFn<String, Delta>() {
            @Override
            public float scaleFactor() {
                return 1;
            }

            @Override
            public void process(String input, Emitter<Delta> emitter) {
                emitter.emit(SmartUtil.fromStock(input));
            }
        }, Avros.reflects(Delta.class));
    }

    public static PCollection<Delta> parseMouvement(PCollection<String> line) {
        return line.parallelDo(new MapFn<String, Delta>() {
            @Override
            public Delta map(String input) {
                return SmartUtil.fromMouvement(input);
            }
        }, Avros.reflects(Delta.class));
    }

    public static PCollection<String> toStock(PCollection<Delta> line) {
        return line.parallelDo(new MapFn<Delta, String>() {
            @Override
            public String map(Delta input) {
                return SmartUtil.toStock(input);
            }
        }, Avros.strings());
    }

    public static PCollection<Delta> merge(PCollection<Delta> col1, PCollection<Delta> col2) {
        return col1.union(col2);
    }
//
    public static PTable<String, Pair<Long, Delta>> convertTo(final PCollection<Delta> deltas) {
        PTable<String, Delta> deltasByStock =
                deltas.by(new MapFn<Delta, String>() {
            @Override
            public String map(Delta input) {
                return input.getStockId();
            }
        }, Avros.strings());

        PTable<String, Pair<Long, Delta>> result = deltasByStock.mapValues(new MapFn<Delta, Pair<Long, Delta>>() {
            @Override
            public Pair<Long, Delta> map(Delta input) {
                return new Pair<Long, Delta>(- input.getDateAsTime(), input);
            }
        }, Avros.pairs(Avros.longs(), Avros.reflects(Delta.class)));
        return result;
    }


    public int run(Pipeline pipeline, String stockFile, String mvtFile, String stockOutput) {

        PCollection<String> stockLines = pipeline.readTextFile(stockFile);

        PCollection<Delta> stocks = parseStock(stockLines);

        PCollection<String> mouvementLines = pipeline.readTextFile(mvtFile);

        PCollection<Delta> mvts = parseMouvement(mouvementLines);

        PCollection<Delta> all = merge(stocks, mvts);

        PTable<String, Pair<Long, Delta>> data = convertTo(all);

        PCollection<Delta> snaphots = SecondarySort.sortAndApply(data, new StockFiller(), Avros.reflects(Delta.class), 36);

        pipeline.writeTextFile(toStock(snaphots), stockOutput);

        PipelineResult result = pipeline.done();
        return result.succeeded() ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new Configuration(), new StockJob(), args);
    }

    public int run(String[] args) throws Exception {

        if (args.length != 3) {
            System.err.println("Usage: hadoop jar crunch-demo-1.0-SNAPSHOT-job.jar"
                    + " [generic options] stockInput MoveQQQInput output");
            System.err.println();
            GenericOptionsParser.printGenericCommandUsage(System.err);
            return 1;
        }
        // Create an object to coordinate pipeline creation and execution.
        Pipeline pipeline = new MRPipeline(StockJob.class, getConf());

        return run(pipeline, args[0], args[1], args[2]);

    }

}
