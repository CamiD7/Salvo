package com.codeoftheweb.salvo.Classes;
import com.codeoftheweb.salvo.DTO.PlayerDTO;
import com.codeoftheweb.salvo.Salvo;
import com.codeoftheweb.salvo.Score;
import com.codeoftheweb.salvo.Ship;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @OneToMany(mappedBy="gamePlayers", fetch=FetchType.EAGER)
    private Set<Ship> ship = new HashSet<>();
        private Date date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    Set<Salvo> salvoes = new HashSet<>();


    public GamePlayer() {}


    public GamePlayer(Game game, Player player, Date date){
        this.game = game;
        this.player = player;
        this.date = date;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void add(GamePlayer game) {
    }

    public Player getPlayer() {
        return player;
    }

    public void setGamePlayer(PlayerDTO player) {
    }

    public Set<Ship> getShip() {
        return ship;
    }

    public void setShip(Set<Ship> ship) {
        this.ship = ship;
    }


    public Set<Salvo> getSalvo(){ return salvoes;}

    public void setSalvo(Set<Salvo> ship) {this.salvoes = salvoes;}


    public Optional<Score> getScore(){
        return player.getScore(game);
    }
}
