
// ArrayList permettra de créer des listes dynamiques
import java.util.ArrayList;

public class Blackjack {
    private class Card {
        String value;
        String suit;

        Card(String value, String suit) {
            this.value = value;
            this.suit = suit;
        }

        // Override permet de remplacer la méthode toString de la superclasse Object
        // quand on appelera toString avec println(deck), ça sera notre méthode utilisée
        @Override
        public String toString() {
            return value + "-" + suit;
        }
    }

    // ArrayList est une classe de la bibliothèque Java importée avec java.util
    // Card indique que cette liste deck recevra des objets Card
    ArrayList<Card> deck;

    Blackjack() {
        startGame();
    }

    public void startGame() {
        buildDeck();
    }

    public void buildDeck() {
        // on initialise deck pour pouvoir l'utiliser.
        deck = new ArrayList<Card>();
        String[] values = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" };
        String[] suits = { "C", "D", "H", "S" };

        for (String suit : suits) {
            for (String value : values) {
                Card card = new Card(value, suit);
                deck.add(card);
            }
        }
        System.out.println("Deck built");
        System.out.println(deck);
    }

    public static void main(String[] args) {
        new Blackjack();
    }
}
