package org.example;
import java.util.*;

public class User {

    public int user_id;
    public List<Integer> cards_id=new ArrayList<Integer>();

    public static List<User> users=new ArrayList<User>();
    public User(int id){
        this.user_id=id;
        users.add(this);

    }

    public void add_card(int card_id){
        this.cards_id.add(card_id);
    }


    @Override
    public String toString(){
        return "User ID: "+this.user_id+" karty: "+cards_id.toString();
    }

    public static User get_User(int user_id){
        return User.users.stream()
                .filter(user->user.user_id==user_id)
                .findFirst()
                .orElse(null);
    }

    public static User get_User_cards(int id){
        for (User user: User.users)
        {
            for (int card_id: user.cards_id)
            {
                if (card_id==id){
                    return user;
                }

            }

        }
        return null;
    }

    public static void main(String[] args) {
        User a=new User(2);
        User b=new User(1);

        System.out.printf(User.get_User(2).toString());
    }
}
