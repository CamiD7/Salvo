package com.codeoftheweb.salvo.Classes;

//import com.codeoftheweb.salvo.Score;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Entity

public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String email;
    private String password;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayer;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<Score> scores = new HashSet<>();

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Player() { }

    public Player( String email, String password) {
        this.email = email;
        this.password = password;
    }




    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setGamePlayer(this);
        gamePlayer.add(gamePlayer);
    }

    public long getId() {
        return id;
    }

    public Optional<Score> getScore(Game game){
        return scores.stream().filter(score -> score.getGameId().equals(game.getId())).findFirst();
    }

    public Set<GamePlayer> getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(Set<GamePlayer> gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }
}

