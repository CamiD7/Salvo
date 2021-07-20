package com.codeoftheweb.salvo.Classes;
import com.codeoftheweb.salvo.Score;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;


import java.util.Date;
import java.util.Set;

@Entity
public class Game {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private Date created;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }


    public Game() { }

    public Game(Date created) {
        this.created = created;
    }

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    Set<Score> score;

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public void addGame(GamePlayer game) {
        game.setGame(this);
        game.add(game);
    }

    public long getId() {
        return id;
    }



    public Set<Score> getScore() {
        return score;
    }
}
