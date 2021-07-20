package com.codeoftheweb.salvo.DTO;

import com.codeoftheweb.salvo.Classes.GamePlayer;
import com.codeoftheweb.salvo.Classes.Player;
import com.codeoftheweb.salvo.Classes.Score;

import java.util.HashSet;
import java.util.Set;

public class PlayerDTO {
    private long id;
    private String email;
    private String password;
    Set<GamePlayer> gamePlayer;
    Set<Score> scores = new HashSet<>();

    public PlayerDTO() {
    }

    public PlayerDTO(PlayerDTO player ) {
        this.id = player.getId();
        this.email = player.getEmail();
        this.password = player.getPassword();
        this.gamePlayer = player.getGamePlayer();
        this.scores = player.getScores();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setGamePlayer(this);
        gamePlayer.add(gamePlayer);
    }
}
