import java.util.*;
import java.io.*;


// we don't actually need an int of cards because we can just use the chart/math below.

//int[] deckOfCards   = new int[52];        // a deck of cards consists of 56 cards.

// card sequence
// 1    2   3   4   5   6   7   8   9   10  11  12  13          // Clubs >= 1 && <= 13 else cardDesignation = num;
// 14   15  16  17  18  19  20  21  22  23  24  25  26          // Diamonds >= 14 && <= 26 if (num >= 14) cardDesignation = num - 13;
// 27   28  29  30  31  32  33  34  35  36  37  38  39          // Hearts >= 27 && <= 39 if (num >= 27) cardDesignation = num - 26;
// 40   41  42  43  44  45  46  47  48  49  50  51  52          // Spades >= 40 && <= 52 (if num >= 40) cardDesignation = num - 39;
// A    -   -   -   -   -   -   -   -   -   J   Q   K           // FACE CARDS (- Numbered Cards)
//cardsInPlayerDeck[0] = 1 = CA = Ace of Clubs

class Game {
    public int CompareCards(int cardOne, int cardTwo, int round) {

        System.out.println("ROUND " + round + ": player 1 plays " + playerDeck[0].GetCardFace(cardOne) + playerDeck[0].GetCardSuit(cardOne) + " ~VS~ " + "player 2\'s " + playerDeck[1].GetCardFace(cardTwo) + playerDeck[1].GetCardSuit(cardTwo) + "!");
    
        while (cardOne > 13) cardOne -= 13;
        while (cardTwo > 13) cardTwo -= 13;
    
        if (cardOne == 1) cardOne += 13;
        if (cardTwo == 1) cardTwo += 13;
    
        if (cardOne > cardTwo) return 1;
        if (cardOne < cardTwo) return -1;
        System.out.println("~~~~~TIE BREAKER~~~~~");
        /*try {
            System.in.read();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        return 0;
    }
    // FiFO
    class ShiftingStack {
        // a game is won when one player holds ALL other players cards, so the stack can never be bigger than this.
        // i believe this method also allows me to check if a player has reached the max size when they win a card, versus having to check if all players are out of cards.
        int maxSize;
        int curSize;
        int[] cardsInPlayerDeck;

        public boolean HasLost() {
            if (curSize > 0) System.out.println(curSize + " @ " + maxSize);
            return curSize == 0;
        }
        public ShiftingStack(int numOfPlayers) {
            maxSize = numOfPlayers * 52;
            cardsInPlayerDeck = new int[maxSize];
            //randomize();  // debugging issues.
            shuffle();
        }
        public void push(int cardVal) {
            if (curSize < maxSize) {
                curSize++;
                for (int index = curSize - 1; index > 0; index--) {
                    cardsInPlayerDeck[index] = cardsInPlayerDeck[index - 1];
                }
                cardsInPlayerDeck[0] = cardVal;
            }
        }
        public int pop() {
            if (curSize < 1) return -1;
            int poppedCard = cardsInPlayerDeck[curSize - 1];
            curSize--;
            return poppedCard;
        }
        public int top(int cardsWon) {
            if (curSize < cardsWon) return -1;
            return cardsInPlayerDeck[curSize - cardsWon];
        }
        public void winRound(int numberOfCards, ShiftingStack loser, int winner) {
            for (int i = numberOfCards; i > 0; i--) {
                System.out.println("player " + (winner + 1) + " wins " + this.GetCardFace(loser.top(1)) + this.GetCardSuit(loser.top(1)));
                this.push(loser.pop());
                this.push(pop());
            }
        }
        public char GetCardSuit(int cardNum) {  // cardNum is the most-recently popped card from the stack (the card just put into play from a players hand)
            if (cardNum >= 40) {
                return 'S';
            }
            if (cardNum >= 27) return 'H';
            if (cardNum >= 14) return 'D';
            if (cardNum >= 1) return 'C';
            return '!';
        }
        // return the card face type; A, J, Q, K or 2-10
        public char GetCardFace(int cardNum) {
            //int temp = cardNum;
            //while (temp > 13) temp -= 13;
        
            cardNum = (cardNum - 1) % 13 + 1;
        
            if (cardNum > 1 && cardNum < 10) return (char)(cardNum + '0');
            if (cardNum == 10) return 'X';
            if (cardNum == 1) return 'A';
            if (cardNum == 11) return 'J';
            if (cardNum == 12) return 'Q';
            if (cardNum == 13) return 'K';
            return '!';
        }

        /*
            This function will randomize the deck of cards in the stack.
        */
        public void randomize() {
            // I could have used the built-in shuffle method, but i decided to write my own for practice.
            // Collections.shuffle();   // ez
            // https://stackoverflow.com/questions/4040001/creating-random-numbers-with-no-duplicates
        
            Random randomCards = new Random();
            curSize = 0;        // effectively wiping out any cards in the deck (such as in the case of a new game.)
            boolean[] assignedCards = new boolean[52];
            while (curSize < maxSize) {
                //int drawRandomCard = randomCards.ints(1, 1, 53).findFirst().getAsInt(); // fF & gAI required since only pulling a single value.
                int drawRandomCard = randomCards.nextInt(52) + 1;
                if (!assignedCards[drawRandomCard - 1]) {
                    cardsInPlayerDeck[curSize] = drawRandomCard;
                    curSize++;
                    assignedCards[drawRandomCard - 1] = true;
                }
            }
        }
        public void shuffle() {
            Random randomCards = new Random();
            curSize = 52;        // effectively wiping out any cards in the deck (such as in the case of a new game.)
            for (int index = 0; index < 52; index++) {
                cardsInPlayerDeck[index] = index + 1;
                if (index > 0) {
                    int offset = randomCards.nextInt(index + 1);
                    int index2 = (index - offset);
                    int swap = cardsInPlayerDeck[index];
                    cardsInPlayerDeck[index] = cardsInPlayerDeck[index2];
                    cardsInPlayerDeck[index2] = swap;
                }
            }
        }
    }

    int numOfPlayers    = 2;                    // not meant to be elaborate; for testing purposes.
    int currentPlayer   = 1;                    // keep track of which players turn it is.

    ShiftingStack[] playerDeck = new ShiftingStack[2];
    public Game() {
       playerDeck[0] = new ShiftingStack(numOfPlayers);
       playerDeck[1] = new ShiftingStack(numOfPlayers);
    }
    public void RunGame() {
        int roundCounter = 1;
        while (!playerDeck[0].HasLost() && !playerDeck[1].HasLost()) {
            int cardsWon = 1;
            int cardCompareResult = CompareCards(playerDeck[0].top(cardsWon), playerDeck[1].top(cardsWon), roundCounter);
            while (cardCompareResult == 0) {    // in the instance of a tie...
                cardsWon++;
                //if (playerDeck[0].top(cardsWon) == -1) return;
                cardCompareResult = CompareCards(playerDeck[0].top(cardsWon), playerDeck[1].top(cardsWon), roundCounter);
            }
            roundCounter++;
            if (cardCompareResult > 0) {
                playerDeck[0].winRound(cardsWon, playerDeck[1], 0);
            }
            else {
                playerDeck[1].winRound(cardsWon, playerDeck[0], 1);
            }
        }
        System.out.println("player " + (playerDeck[0].HasLost() ? 2 : 1) + " has won!");
        //throw {
        //}
    }
    public static void main(String[] argv) {
        Game game = new Game();
        game.RunGame();
    }
}
