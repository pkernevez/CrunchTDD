package com.octo.softshake.crunch.stock;

import org.apache.crunch.DoFn;
import org.apache.crunch.Emitter;
import org.apache.crunch.Pair;

import java.util.Date;
import java.util.Iterator;

public class StockFiller extends DoFn<Pair<String, Iterable<Pair<Long, Delta>>>, Delta> {

    private Delta currentStock = null;
    private Delta nextStock = null; // Next in the algo, previous in time

    @Override
    public float scaleFactor() {
        return 10;
    }

    @Override
    public void process(Pair<String, Iterable<Pair<Long, Delta>>> input, Emitter<Delta> emitter) {
        currentStock = null;
        nextStock = null;
        Iterator<Pair<Long, Delta>> deltas = input.second().iterator();
        while (deltas.hasNext()) {
            Delta delta = deltas.next().second();
            int i = 0;
            if (currentStock == null) {
                currentStock = delta.detach();
                nextStock = delta.decreaseDate();
            } else if (currentStock.date.equals(delta.date)) {
                nextStock = nextStock.remove(delta);
            } else if (currentStock.getDateAsTime() < delta.getDateAsTime()) {
                throw new RuntimeException(
                        "Invalid order for date : " + currentStock.date + " should be >= than " + delta.date);
            } else {
                while (currentStock.getDateAsTime() > delta.getDateAsTime()) {
                    emitter.emit(currentStock);
                    currentStock = nextStock;
                    nextStock = nextStock.decreaseDate();
                }
                nextStock = nextStock.remove(delta);
            }
            i++;
        }
        if (currentStock != null)
            emitter.emit(currentStock);
        if (nextStock != null)
            emitter.emit(nextStock);
    }

}
