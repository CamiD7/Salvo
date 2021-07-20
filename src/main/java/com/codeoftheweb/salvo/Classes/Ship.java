package com.codeoftheweb.salvo.Classes;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;


import java.util.*;
import java.util.stream.Collectors;

@Entity

public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String type;
    @ElementCollection
    @Column(name = "Location")
    private List<String> shipLocations;


    public Ship(){}


    public Ship(String type, List<String> shipLocations, GamePlayer gamePlayer){
        this.type = type;
        this.shipLocations= shipLocations;
        this.gamePlayers = gamePlayer;

    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer")
    private GamePlayer gamePlayers;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getShipLocations() {
        return shipLocations;
    }

    public void setShipLocations(List<String> shipLocations) {
        this.shipLocations = shipLocations;
    }


    private void add(GamePlayer gamePlayer) {
    }

    private void setGamePlayer(Ship ship) {
    }


}
