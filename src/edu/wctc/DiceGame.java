package edu.wctc;

import java.util.*;
import java.util.stream.Collectors;
import  java.util.Comparator;

public class DiceGame{
    private final List<Player> players;
    private final List<Die> dice;
    private final int maxRolls;
    private Player currentPlayer;

    public DiceGame(int countPlayers, int countDice, int maxRolls) {
        players = new ArrayList<>();
        dice = new ArrayList<>();
        this.maxRolls = maxRolls;

        for (int i = 0; i < countPlayers; i++) {
            Player player = new Player();
            players.add(player);
        }

        for (int i = 0; i < countDice; i++) {
            Die die = new Die(6);
            dice.add(die);
        }

        if (players.size() < 2) {
            throw new IllegalArgumentException();
        }
    }

    private boolean allDiceHeld() {

        /*Returns true if all dice are held, false otherwise.

                Hint: allMatch*/
        if(dice.stream().allMatch(Die::isBeingHeld) == true){
            return true;
        }
        return false;
    }

    public boolean autoHold(int faceValue) {
        /*If there already is a die with the given face value that is held, just return true.

If there is a die with the given face value that is unheld, hold it and return true.

If there is no die with the given face value, return false.

Hints: filter, findFirst, isPresent*/

        if (dice.stream().filter(die -> die.getFaceValue() == faceValue && die.isBeingHeld() == true).collect(Collectors.toList()).size() > 0) {
            return true;
        }
        else if(dice.stream().filter(die -> die.getFaceValue() == faceValue && die.isBeingHeld() == false).collect(Collectors.toList()).size() > 0){
            for (Die die: dice) {
                if(die.getFaceValue() == faceValue && die.isBeingHeld() == false){
                    die.holdDie();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean currentPlayerCanRoll(){
        /*	Returns true if the current player has any rolls remaining AND if not all dice are held.*/
        if(currentPlayer.getRollsUsed() < maxRolls && allDiceHeld() == false){
            return true;
        }
        else {
            return false;
        }

    }

    public int getCurrentPlayerNumber(){
        /*Returns the player number of the current player.*/
        int playerNumber = currentPlayer.getPlayerNumber();
        return playerNumber;
    }

    public int getCurrentPlayerScore(){
        //Returns the score of the current player
        int playerScore = currentPlayer.getScore();
        return playerScore;
    }

    public String getDiceResults(){
        /*Resets a string composed by concatenating each Die's toString.

        Hints: map, Collectors.joining*/
        String concatenate = dice.stream().map(String::valueOf).collect(Collectors.joining("\n"));
        return concatenate;
    }

    public String getFinalWinner(){
        /*Finds the player with the most wins and returns its toString.

Hints: Collections.max, Comparator.comparingInt*/
        Player winner = new Player();
        for(int i = 0; i < players.size(); i++){
            Player previousPlayer =  players.get(0);

            if(players.get(i).getWins() >= previousPlayer.getWins()){
                winner = players.get(i);
            }
        }

        return winner.toString();
    }

    public String getGameResults(){

        /*Sorts the player list by score, highest to lowest.

        Awards each player that earned the highest score a win and all others a loss.

                Returns a string composed by concatenating each Player's toString.

        Hints: Comparator.comparingInt, reversed

        More hints: forEach

        Final hints: map, Collectors.joining*/
        players.sort(Comparator.comparingInt(Player::getScore));
        Collections.reverse(players);
        List<Player> winners = new ArrayList<>();
        List<Player> losers = new ArrayList<>();
        int previousPlayerScore = 0;
        for(Player player: players){
            if(player.getScore() >= previousPlayerScore){
                winners.add(player);
                previousPlayerScore = player.getScore();
            }
            else{
                losers.add(player);
            }
        }

        for(Player player: winners){
            player.addWin();
        }
        for(Player player : losers){
            player.addLoss();
        }
        return players.stream().map(String::valueOf).collect(Collectors.joining("\n"));
    }

    private boolean isHoldingDie(int faceValue){
        /*Returns true if there is any held die with a matching face value, false otherwise.

        Hints: filter, findFirst, isPresent*/
        if(dice.stream().filter(die -> die.isBeingHeld() == true && die.getFaceValue() == faceValue).collect(Collectors.toList()).size() > 0){
            return true;
        }
        return false;
    }

    public boolean nextPlayer(){
        /*If there are more players in the list after the current player,
        updates currentPlayer to be the next player and returns true. Otherwise, returns false.*/
        for(int i = 0; i < players.size()-1; i++){
            if(players.get(i) == currentPlayer){
                if(i > players.size()){
                    break;
                }
                currentPlayer = players.get(i+1);
                return true;
            }
        }

            return false;

    }

    public void playerHold(char dieNum){
        /*Finds the die with the given die number (NOT the face value) and holds it.

                Hints: filter, findFirst, isPresent*/
        List<Die> holding = new ArrayList<>();
        holding = dice.stream().filter(die -> die.getDieNum() == dieNum).collect(Collectors.toList());
        for (Die die: holding) {
            die.holdDie();
        }
    }

    public void resetDice(){
        /*Resets each die.

                Hint: forEach*/
        for (Die die: dice) {
            die.resetDie();
        }
    }

    public void resetPlayers(){
        /*
        Resets each player.

        Hint: forEach*/
        for (Player player: players) {
            player.resetPlayer();
        }
    }

    public void rollDice(){
        /*Logs the roll for the current player, then rolls each die.

                Hint: forEach*/
        currentPlayer.roll();
        for (Die die: dice) {
            die.rollDie();
        }
    }

    public void scoreCurrentPlayer(){
        /*
        If there is currently a ship (6), captain (5), and crew (4) die held, adds the points for the remaining two dice (the cargo) to the current player's score.

        If there is not a 6, 5, and 4 held, assigns no points.

        Note: The 6, 5, and 4 held as the ship, captain, and crew are not worth points.*/
        int score = 0;
        if(autoHold(6) && autoHold(5) && autoHold(4)){
            for (Die die: dice) {
                score += die.getFaceValue();
            }
            score -= 15;
        }
        else{
            score += 0;
        }
        currentPlayer.setScore(score);
    }

    public void startNewGame(){
        /*
        Assigns the first player in the list as the current player.
        (The list will still be sorted by score from the previous round, so winner will end up going first.)

        Resets all players.*/
        currentPlayer = players.get(0);

        resetPlayers();
    }

}
