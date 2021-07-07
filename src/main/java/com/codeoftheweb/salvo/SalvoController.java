package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository repo;

    @Autowired
    private GamePlayerRepository gamePlayRepo;


    //@Autowired
    //@Qualifier("/login{mail}")
    //PlayerService playerService;

    @RequestMapping("/games")
    public Map<String, Object> getGame() {
        Map<String, Object> gm = new LinkedHashMap<String, Object>();
        gm.put("games", repo.findAll().stream().map(g->makeGameDTO(g)).collect(Collectors.toList()));
        return gm;
    }



    /*@RequestMapping("/games")
    public List<Object> getAllGames() {
        return repo.findAll().stream().map(g -> makeGameDTO(g)).collect(Collectors.toList());
    }*/

    @RequestMapping("/game_view/{gamePl_id}")
    public Map<String, Object> getGame(@PathVariable Long gamePl_id) {
        GamePlayer gp1 = gamePlayRepo.findById(gamePl_id).get();
        Game game = gp1.getGame();
        Map<String, Object> m = makeGameDTO(game);
        m.put("ships", gp1.getShip().stream().map(barco -> makeShipsDTO(barco)).collect(Collectors.toList()));
        m.put("salvoes", gp1.getGame().getGamePlayers().stream().flatMap((salvo) -> salvo.getSalvo().stream().map(this::makeSalvoDTO)));
        return m;
    }

    //@PostMapping("/players")
    //public void addPlayer(@RequestBody Player player) {playerService.savePlayer(player);}

    //@RequestMapping("/players")
    //public List<Player> getPlayers(){
    //  return playerService.getPlayers();

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
        gamePlayerdto.put("id", gamePlayer.getId());
        gamePlayerdto.put("player", makePlayerDTO(gamePlayer.getPlayer()));
        return gamePlayerdto;
    }

    private Map<String, Object> makePlayerDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", player.getId());
        dto.put("email", player.getUserName());
        return dto;
    }

    private Map<String, Object> makeShipsDTO(Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("type", ship.getType());
        dto.put("locations", ship.getLocation());
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
}