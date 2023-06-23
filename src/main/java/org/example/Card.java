package org.example;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
public class Card {
    public int card_id;
    public double limit;
    public double spended;
    public List<Transakcje> transakcje=new ArrayList<>();
    public static List<Card> karty=new ArrayList<>();

    public Card(int id, double lim){
        this.card_id=id;
        this.limit=lim;
        this.spended=0;
        karty.add(this);

    }

    public void add_transakcje(Transakcje t){
        this.transakcje.add(t);
    }

    public boolean AddSpended(double liczba){
        if(this.spended+liczba<=this.limit) {
            this.spended += liczba;
            return true;
        }
        return false;
    }
    public static Card get_Card(int card_id){
        return Card.karty.stream()
                .filter(karta->karta.card_id==card_id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString(){
        return "ID: "+this.card_id+ " limit: "+this.limit+" Spended: "+this.spended;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            double min=0;
            double max=90;

            Random random=new Random();
            double wynik= random.nextDouble() *(max-min)+min;

            BigDecimal big=new BigDecimal(wynik);
            big=big.setScale(5, RoundingMode.HALF_UP);
            double lim= big.doubleValue();

            int min1=0;
            int max1=1000;
            Random random1=new Random() ;
            int id= random1.nextInt(max1)+min1;

            Card c=new Card(id,lim);
        }
        System.out.println(Card.karty.toString());
    }

}
