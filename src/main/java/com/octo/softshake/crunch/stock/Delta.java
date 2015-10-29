package com.octo.softshake.crunch.stock;

import java.util.Calendar;

/**
 * Created by pke on 04.10.15.
 */
public class Delta implements Comparable, Cloneable {
    public String marque;
    public int article;
    public int magasin;
    public int typeStock;
    public String date;
    public int delta;

    public Delta() {
    }

    public Delta(String pMarque, int pMagasin, int pTypeStock, int pArticle, String pDate, int pDelta) {
        marque = pMarque;
        article = pArticle;
        magasin = pMagasin;
        typeStock = pTypeStock;
        date = pDate;
        delta = pDelta;
    }

    public String getStockId() {
        return marque + "," + magasin + "," + typeStock + "," + article;

    }

    public boolean isSameStock(Object other) {
        if (other == null || !(other instanceof Delta)) {
            return false;
        } else {
            Delta otherMvt = (Delta) other;
            return getStockId().equals(((Delta) other).getStockId());
        }
    }

    @Override
    public String toString() {
        return "Delta{" +
                "marque='" + marque + '\'' +
                ", article=" + article +
                ", =" + magasin +
                ", =" + magasin +
                ", typeStock=" + typeStock +
                ", date=" + date +
                ", delta=" + delta +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Delta delta1 = (Delta) o;

        if (article != delta1.article) return false;
        if (magasin != delta1.magasin) return false;
        if (typeStock != delta1.typeStock) return false;
        if (delta != delta1.delta) return false;
        if (marque != null ? !marque.equals(delta1.marque) : delta1.marque != null) return false;
        return !(date != null ? !date.equals(delta1.date) : delta1.date != null);

    }

    @Override
    public int hashCode() {
        int result = marque != null ? marque.hashCode() : 0;
        result = 31 * result + article;
        result = 31 * result + magasin;
        result = 31 * result + typeStock;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + delta;
        return result;
    }

    @Override
    public int compareTo(Object o) {
        return getStockId().compareTo(((Delta) o).getStockId());
    }

    public Delta decreaseDate() {
        try {
            Delta newMvt = (Delta) this.clone();
            Calendar cal = Calendar.getInstance();
            cal.setTime(SmartUtil.parse(date));
            cal.add(Calendar.DAY_OF_MONTH, -1);
            newMvt.date = SmartUtil.format(cal.getTime());
            return newMvt;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public Delta remove(Delta mvt) {
        if (!isSameStock(mvt)) throw new RuntimeException("Invalide Stock for operation");
        try {
            Delta newobj = (Delta) this.clone();
            newobj.delta = delta - mvt.delta;
            return newobj;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public Long getDateAsTime(){
        return SmartUtil.parse(date).getTime();
    }

    public Delta detach(){
        try {
            return (Delta) this.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
