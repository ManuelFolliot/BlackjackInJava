
// permettra d'utiliser des éléments graphiques
import java.awt.*;
// permettra d'utiliser des événements
import java.awt.event.*;
// ArrayList permettra de créer des listes dynamiques
import java.util.ArrayList;
// permettra de générer des nombres aléatoires
import java.util.Random;
// permettra de créer une interface graphique avec Swing.
import javax.swing.*;

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

        // Cette méthode nous permet d'obtenir la valeur de la carte tirée
        public int getValue() {
            if ("AJQK".contains(value)) {
                if (value == "A") {
                    // 11 pour les As (on ajustera plus tard pour éventuellement modifier sa valeur
                    // par 1)
                    return 11;
                }
                // 10 pour les figures (Valet, Reine, Roi)
                return 10;
            }
            // sinon, c'est la valeur indiquée par la carte
            return Integer.parseInt(value);
        }

        public boolean isAce() {
            return value == "A";
        }

        public String getImagePath() {
            return "src/PlayingCards/" + toString() + ".png";
        }
    }

    // ArrayList est une classe de la bibliothèque Java importée avec java.util
    // Card indique que cette liste deck recevra des objets Card
    ArrayList<Card> deck;
    Random shuffledDeck = new Random(); // mélange le deck

    // la main du dealer
    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;

    // la main du joueur
    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;

    // la fenêtre où l'on jouera la partie
    int boardWidth = 600;
    int boardHeight = boardWidth;

    // on définit les dimensions des cartes
    int cardWidth = 110;
    int cardHeight = 154;

    // JFrame est issue de la bibliothèque javax.swing et va permettre de créer une
    // interface graphique
    JFrame frame = new JFrame("Black Jack");
    JPanel gamePanel = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            try {
                // on va chercher les images des cartes
                // tout d'abord la carte face cachée du dealer
                Image hiddenCardImg = new ImageIcon(getClass().getResource("src/PlayingCards/Back.png")).getImage();
                if (!stayButton.isEnabled()) {
                    hiddenCardImg = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();
                }
                g.drawImage(hiddenCardImg, 20, 20, cardWidth, cardHeight, this);

                // puis on tire les cartes du dealer
                for (int i = 0; i < dealerHand.size(); i++) {
                    Card card = dealerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, cardWidth + 25 + (cardWidth + 5) * i, 20, cardWidth, cardHeight, null);
                }

                // puis les cartes du joueur
                for (int i = 0; i < playerHand.size(); i++) {
                    Card card = playerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, 20 + (cardWidth + 5) * i, 320, cardWidth, cardHeight, null);
                }

                if (!stayButton.isEnabled()) {
                    dealerSum = reduceDealerAce();
                    playerSum = reducePlayerAce();
                    System.out.println("STAY: ");
                    System.out.println(dealerSum);
                    System.out.println(playerSum);

                    String message = "";
                    if (playerSum > 21) {
                        message = "You lose";
                    } else if (dealerSum > 21) {
                        message = "You win";
                    } else if (playerSum == dealerSum) {
                        message = "Tie";
                    } else if (playerSum > dealerSum) {
                        message = "You win";
                    } else if (playerSum < dealerSum) {
                        message = "You lose";
                    }

                    g.setFont(new Font("Arial", Font.PLAIN, 30));
                    g.setColor(Color.white);
                    g.drawString(message, 220, 250);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };
    JPanel buttonPanel = new JPanel();
    JButton hitButton = new JButton("Hit");
    JButton stayButton = new JButton("Stay");

    Blackjack() {
        startGame();

        // on définit les paramètres du cadre dans lequel on jouera au Blackjack
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53, 101, 77));
        frame.add(gamePanel);

        hitButton.setFocusable(false);
        buttonPanel.add(hitButton);
        stayButton.setFocusable(false);
        buttonPanel.add(stayButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        hitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Card card = deck.remove(deck.size() - 1);
                playerSum += card.getValue();
                playerAceCount += card.isAce() ? 1 : 0;
                playerHand.add(card);
                if (reducePlayerAce() > 21) {
                    hitButton.setEnabled(false);
                }

                // permet de mettre à jour le panel
                gamePanel.repaint();
            }
        });

        stayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hitButton.setEnabled(false);
                stayButton.setEnabled(false);

                while (dealerSum < 17) {
                    Card card = deck.remove(deck.size() - 1);
                    dealerSum += card.getValue();
                    dealerAceCount += card.isAce() ? 1 : 0;
                    dealerHand.add(card);
                }
                gamePanel.repaint();
            }
        });

        gamePanel.repaint();
    }

    public void startGame() {
        buildDeck();
        shuffleDeck();

        // constitution de la main du dealer
        dealerHand = new ArrayList<Card>();
        dealerSum = 0;
        dealerAceCount = 0;

        // on utilise deck.size() -1 pour accéder à la dernière carte du deck avant de
        // la retirer avec remove
        hiddenCard = deck.remove(deck.size() - 1);
        // on récupère la valeur de la propriété value de la carte en question
        dealerSum += hiddenCard.getValue();
        // si c'est un as, on incrémente le nombre d'as
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card = deck.remove(deck.size() - 1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        System.out.println("Dealer hand");
        System.out.println(dealerHand);
        System.out.println(hiddenCard);
        System.out.println(dealerSum);

        // constitution du jeu du joueur
        playerHand = new ArrayList<Card>();
        playerSum = 0;
        playerAceCount = 0;

        for (int i = 0; i < 2; i++) {
            // on ne déclare pas la classe Card car on réutilise une variable déjà créée
            card = deck.remove(deck.size() - 1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }

        System.out.println("Player hand");
        System.out.println(playerHand);
        System.out.println(playerSum);
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

    public void shuffleDeck() {
        for (int i = deck.size() - 1; i > 0; i--) {
            // on génère un nombre aléatoire entre 0 et la taille du deck
            int randomNumber = shuffledDeck.nextInt(deck.size());
            // on obtient la carte actuelle sur laquelle la boucle se trouve
            Card currentCard = deck.get(i);
            // on obtient une carte aléatoire grâce au nombre aléatoire
            Card randomCard = deck.get(randomNumber);

            // on échange les cartes récupérées
            deck.set(i, randomCard);
            deck.set(randomNumber, currentCard);
        }
        System.out.println("Deck shuffled");
        System.out.println(deck);
    }

    public int reducePlayerAce() {
        while (playerSum > 21 && playerAceCount > 0) {
            playerSum -= 10;
            playerAceCount -= 1;
        }
        return playerSum;
    }

    public int reduceDealerAce() {
        while (dealerSum > 21 && dealerAceCount > 0) {
            dealerSum -= 10;
            dealerAceCount -= 1;
        }
        return dealerSum;
    }

    public static void main(String[] args) {
        new Blackjack();
    }
}
