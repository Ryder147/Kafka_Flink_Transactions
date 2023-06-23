package org.example;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.*;

public class Transakcje {
    public int card_id;
    public int user_id;
    public double[] cords;
    public double value;
    public double limit;




    public Transakcje(){
        this.card_id=randId();
        this.value=randvalue();
        this.cords=randcords();
        this.setUser_id();
        this.randlimit();
        this.add_transakcje();
        //this.addspended();


    }
    public void add_transakcje(){
        Card c1=Card.get_Card(this.card_id);
        c1.add_transakcje(this);
    }

    public void setUser_id(){

        int id=this.card_id;
        if(User.get_User_cards(id)==null){
            int min=0;
            int max=200;
            Random random=new Random() ;
            int userid= random.nextInt(max)+min;
            if(User.get_User(userid)==null){
            User user=new User(userid);
            this.user_id=userid;
            user.add_card(id);
            }else{
                User u1=User.get_User(userid);
                u1.add_card(id);
            }
        }else{
            User u1=User.get_User_cards(id);
            this.user_id=u1.user_id;

        }
    }
    public void addspended(){
        int id=this.card_id;
        Card c1=Card.get_Card(id);
        c1.AddSpended(this.value);
    }

    public static int randId(){
        int min=0;
        int max=1000;
        Random random=new Random() ;
        int id= random.nextInt(max)+min;

        return id;


    }



    public double[] randcords(){
        double min=-90;
        double max=90;

        Random random=new Random();
        double wynik= random.nextDouble() *(max-min)+min;

        BigDecimal big=new BigDecimal(wynik);
        big=big.setScale(5,RoundingMode.HALF_UP);
        double szerokosc= big.doubleValue();

        double min1=-180;
        double max1=180;

        double wynik1= random.nextDouble() *(max1-min1)+min1;

        BigDecimal big1=new BigDecimal(wynik1);
        big1=big1.setScale(5,RoundingMode.HALF_UP);
        double wysokosc= big1.doubleValue();

        double[] tab={wysokosc,szerokosc};
        return tab;

    }
    public static double randvalue() {
        double min = 0.01;
        double max = 800;
        Random random=new Random();
        double wynik= random.nextDouble() *(max-min)+min;
        BigDecimal big=new BigDecimal(wynik);
        big=big.setScale(2, RoundingMode.HALF_UP);
        return big.doubleValue();
    }

    public void randlimit() {

        int id =this.card_id;
        if(Card.get_Card(id)==null) {
            double min = 500;
            double max = 1000;
            Random random = new Random();
            double wynik = random.nextDouble() * (max - min) + min;
            BigDecimal big = new BigDecimal(wynik);
            big = big.setScale(2, RoundingMode.HALF_UP);
            this.limit= big.doubleValue();
            Card card=new Card(id,big.doubleValue());

        }else{
            Card c1=Card.get_Card(id);
            this.limit=c1.limit;
        }
    }


    @Override
    public String toString() {
        Jsonb json = JsonbBuilder.create();
        String s = json.toJson(this);
        return s;
    }

    public static void main(String[] args) {
        List<String> t=new ArrayList<String>();

        for (int i = 0; i < 1000; i++) {
            Transakcje t1=new Transakcje();

            t.add(t1.toString());
        }




        System.out.println(t);
        System.out.println(t.size());

        System.out.println(User.users);
        System.out.println(User.users.size());
    }

}
