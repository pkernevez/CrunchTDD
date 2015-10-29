package com.octo.softshake.crunch.example;

import org.apache.crunch.DoFn;
import org.apache.crunch.Emitter;
import org.apache.crunch.Pair;

/**
 * Created by pke on 02.10.15.
 */
public class Swap extends DoFn<Pair<String, Long>, Pair<Long, String>> {

    @Override
    public void process(Pair<String, Long> input, Emitter<Pair<Long, String>> emitter) {
        emitter.emit(new Pair<Long, String>(input.second(), input.first()));
    }

}
