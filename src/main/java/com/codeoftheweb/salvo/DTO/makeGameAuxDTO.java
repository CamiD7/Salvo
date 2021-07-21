package com.codeoftheweb.salvo.DTO;

import com.codeoftheweb.salvo.Classes.Game;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class GameAuxDTO {
    private long id;
    private Date created;
    private String gameState;
    Set<GamePlayerDTO> gamePlayers;

    public GameAuxDTO() {
    }

    public GameAuxDTO(Game game) {
        this.id = game.getId();
        this.created = game.getCreated();
        this.gameState = "PLACESHIPS";
        this.gamePlayers = game.getGamePlayers().stream().map(gp -> new GamePlayerDTO(gp)).collect(Collectors.toSet());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    public Set<GamePlayerDTO> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayerDTO> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }
}
