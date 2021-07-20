package com.codeoftheweb.salvo.Controller;

import com.codeoftheweb.salvo.Classes.*;
import com.codeoftheweb.salvo.DTO.PlayerDTO;
import com.codeoftheweb.salvo.Repository.GamePlayerRepository;
import com.codeoftheweb.salvo.Repository.GameRepository;
import com.codeoftheweb.salvo.Repository.PlayerRepository;
import com.codeoftheweb.salvo.Repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository repo;

    @Autowired
    private GamePlayerRepository gamePlayRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired Salvo salvoRepository;

    @Autowired
    PlayerDTO playerDTO;


    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(@RequestParam String email, @RequestParam String password) {
        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByEmail(email) != null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }
        playerRepository.save(new PlayerDTO(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping("/games")
    public Map<String, Object> getAllGames(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (isGuest(authentication)) {
            dto.put("player", "Guest");

        } else {
            dto.put("player",new PlayerDTO(playerRepository.findByEmail(authentication.getName())));
        }
        dto.put("games", repo.findAll().stream().map(g -> makeGameDTO(g)).collect(Collectors.toList()));
        return dto;
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "not logged in"), HttpStatus.FORBIDDEN);
        }
        PlayerDTO player = playerRepository.findByEmail(authentication.getName());
        if (player == null) {
            return new ResponseEntity<>(makeMap("error", "player is not logged in"), HttpStatus.UNAUTHORIZED);
        }
        Game game = new Game(new Date());
        repo.save(game);
        GamePlayer gamePlayer = new GamePlayer(game, player, new Date());
        gamePlayRepo.save(gamePlayer);

        return new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
    }


    @RequestMapping(path = "/game/{game_id}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable long game_id, Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        Date joinDate = new Date();
        Game game = repo.getById(game_id);
        if (!isGuest(authentication)) {
            if (game != null) {
                if (game.getGamePlayers().size() == 1) {
                    GamePlayer gamePlayer = game.getGamePlayers().stream().findFirst().get();
                    PlayerDTO player = playerRepository.findByEmail(authentication.getName());
                    if (gamePlayer.getPlayer() != player) {
                        GamePlayer gamePlayer1 = new GamePlayer(game, player, new Date());
                        gamePlayRepo.save(gamePlayer1);
                        return new ResponseEntity<>(makeMap("gpid", gamePlayer1.getId()), HttpStatus.CREATED);
                    } else {
                        return new ResponseEntity<>(makeMap("Error", "You cannot rejoin a game"), HttpStatus.FORBIDDEN);
                    }
                } else {
                    return new ResponseEntity<>(makeMap("Error", "the game is full"), HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(makeMap("error", "The game does not exists"), HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(makeMap("Error", "You must login"), HttpStatus.UNAUTHORIZED);
        }

    }


    @RequestMapping("/game_view/{gamePl_id}")
    public ResponseEntity<Map<String, Object>> getGame(@PathVariable Long gamePl_id, Authentication authentication) {
        GamePlayer gp1 = gamePlayRepo.findById(gamePl_id).get();
        Game game = gp1.getGame();
        PlayerDTO player = playerRepository.findByEmail(authentication.getName());
        if (gp1.getPlayer().getId() != player.getId()) {
            return new ResponseEntity<>(makeMap("error", "dont cheat"), HttpStatus.UNAUTHORIZED);
        } else {
            Map<String, Object> m = makeGame1DTO(game);
            m.put("ships", gp1.getShip().stream().map(barco -> makeShipsDTO(barco)).collect(Collectors.toList()));
            m.put("salvoes", gp1.getGame().getGamePlayers().stream().flatMap((salvo) -> salvo.getSalvo().stream().map(this::makeSalvoDTO)));
            m.put("hits", makeHitsDTO());
            return new ResponseEntity<>(m, HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/games/players/{gpid}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> placeShips(@PathVariable long gpid, Authentication authentication, @RequestBody List<Ship> ship) {
        GamePlayer gamePlayer = gamePlayRepo.getById(gpid);
        PlayerDTO player = playerRepository.findByEmail(authentication.getName());
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "you must log in"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getPlayer().getId() != player.getId()){
            return new ResponseEntity<>(makeMap("error", "you dont belong here"), HttpStatus.UNAUTHORIZED);}
        /*if (!gamePlayer.getPlayer().equals(authentication.getName())) {
            return new ResponseEntity<>(makeMap("error", "you must log in"), HttpStatus.UNAUTHORIZED);
        }*/
        if (gamePlayer.getShip().size() != 0) {
            return new ResponseEntity<>(makeMap("error", "You already have ships"), HttpStatus.FORBIDDEN);
        }
        if (ship.size() == 5) {
            ship.forEach(ships -> shipRepository.save(new Ship(ships.getType(), ships.getShipLocations(), gamePlayer)));
            return new ResponseEntity<>(makeMap("OK", "Ships placed"), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(makeMap("error", "You cant place more than 5 ships"), HttpStatus.FORBIDDEN);
    }

    @RequestMapping (value = "/games/players/{gamePlayerId}/salvoes",method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> storeSalvoes(@PathVariable long gamePlayerId,Authentication authentication, @RequestBody List<Salvo> salvo){
        GamePlayer gamePlayer = gamePlayRepo.getById(gamePlayerId);
        PlayerDTO player = playerRepository.findByEmail(authentication.getName());
        if (isGuest(authentication)){
            return new ResponseEntity<>(makeMap("Error","You must log in"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getPlayer().getId() != player.getId()){
            return new ResponseEntity<>(makeMap("Error","You don´t belong here"),HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getSalvo().size()!= 0){
            return new ResponseEntity<>(makeMap("Error","You already fired"), HttpStatus.FORBIDDEN);
        }
        if (salvo.size() > 0 || salvo.size() < 5){
            salvo.forEach(salvoes -> salvoRepository.save(new Salvo(gamePlayer, salvoes.getTurn(), salvoes.getLocation())));
            return new ResponseEntity<>(makeMap("OK", "Ships placed"), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(makeMap("error", "You already submitted a salvo"), HttpStatus.FORBIDDEN);

    }





    private Map<String,Object> makeHitsDTO(){
        Map<String,Object> dto = new LinkedHashMap<String,Object>();
        dto.put("self", new ArrayList<>());
        dto.put("opponent",new ArrayList<>());
        return dto;
    }

    private Map<String, Object> makeGame1DTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreated());
        dto.put("gameState", "PLACESHIPS");
        dto.put("gamePlayers", game.getGamePlayers().stream().map(gp -> makeGamePlayerDTO(gp)).collect(Collectors.toList()));
        return dto;
    }

    private Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreated());
        dto.put("gamePlayers", game.getGamePlayers().stream().map(gp -> makeGamePlayerDTO(gp)).collect(Collectors.toList()));
        dto.put("scores", game.getGamePlayers().stream().map(gp -> makeScoreDTO(gp)).collect(Collectors.toList()));
        return dto;
    }

    private Map<String, Object> makeGamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> gamePlayerdto = new LinkedHashMap<String, Object>();
        gamePlayerdto.put("gpid", gamePlayer.getId());
        gamePlayerdto.put("player", PlayerDTO(gamePlayer.getPlayer()));
        return gamePlayerdto;
    }

    /*private Map<String, Object> makePlayerDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", player.getId());
        dto.put("email", player.getEmail());
        return dto;
    }*/

    private Map<String, Object> makeShipsDTO(Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("type", ship.getType());
        dto.put("locations", ship.getShipLocations());
        return dto;
    }

    private Map<String, Object> makeSalvoDTO(Salvo salvo) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("turn", salvo.getTurn());
        dto.put("player", salvo.getGamePlayer().getPlayer().getId());
        dto.put("locations", salvo.getLocation());
        return dto;
    }

    private Map<String, Object> makeScoreDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        if (gamePlayer.getScore().isPresent()) {
            dto.put("player", gamePlayer.getPlayer().getId());
            dto.put("score", gamePlayer.getScore().get().getScore());
            dto.put("finishDate", gamePlayer.getScore().get().getFinished());
            return dto;
        } else {
            dto.put("score", "el juego no tiene puntaje");
            return dto;
        }

    }


    private boolean isGuest (Authentication authentication){
            return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }


    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}