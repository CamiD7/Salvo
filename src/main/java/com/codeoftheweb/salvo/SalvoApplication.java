package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}
	@Bean
	public CommandLineRunner initData(PlayerRepository PlayerRepo, GameRepository GameRepo, GamePlayerRepository gameplayRepo, ShipRepository shipRepo, SalvoRepository salvoRepo,ScoreRepository scoreRepo ) {
		return (args) -> {

			Player player1 = new Player("camila.duarte@gmail.com");
			Player player2 = new Player("santiago.bru@gmail.com");
			Player player3 = new Player("juan@gmail.com");

			PlayerRepo.save(player1);
			PlayerRepo.save(player2);
			PlayerRepo.save(player3);


			Game game1 =  new Game();
			//Game game2 =  new Game();
			//Game game3 = new Game() ;

			Date date1 = new Date();
			Date date2 = Date.from(date1.toInstant().plusSeconds(3600));
			Date date3 = Date.from(date1.toInstant().plusSeconds(7200));

			game1.setCreated(date1);
			//game2.setCreated(date2);
			//game3.setCreated(date3);

			GameRepo.save(game1);
			/*GameRepo.save(game2);
			GameRepo.save(game3);*/

			GamePlayer gamePlayer1 = new GamePlayer(game1,player1,date1);
			GamePlayer gamePlayer2 = new GamePlayer(game1,player2,date2);
			GamePlayer gamePlayer3 = new GamePlayer(game1,player3,date3);

			gameplayRepo.save(gamePlayer1);
			gameplayRepo.save(gamePlayer2);
			gameplayRepo.save(gamePlayer3);


			Ship ship1 = new Ship("cargo", List.of("H1","H2","H3","H4","H5"),gamePlayer1);
			Ship ship2 = new Ship("battleship", List.of("A1","A2","A3","A4"),gamePlayer1);
			Ship ship3 = new Ship("submarine", List.of("C1","D1","E1"),gamePlayer2);

			shipRepo.save(ship1);
			shipRepo.save(ship2);
			shipRepo.save(ship3);

			Salvo salvo1= new Salvo(gamePlayer1,1,List.of("B1","H8","E3","F10","J5"));
			Salvo salvo2= new Salvo(gamePlayer2,1,List.of("A6","E3","F1","C4","D4"));
			Salvo salvo3 = new Salvo(gamePlayer1,2,List.of("D3","F6","C8","A1","G5"));
			Salvo salvo4 = new Salvo(gamePlayer2,2,List.of("E4","E3","D1","F5","D6"));
			Salvo salvo5 = new Salvo(gamePlayer1,3,List.of("D3","H6","C6","I7","G10"));
			Salvo salvo6 = new Salvo(gamePlayer2,3,List.of("D10","F6","C6","A1","G5"));
			Salvo salvo7 = new Salvo(gamePlayer1,4,List.of("I3","A6","J6","I5","G7"));
			Salvo salvo8 = new Salvo(gamePlayer2,4,List.of("D3","G9","C6","J10","H9"));

			salvoRepo.save(salvo1);
			salvoRepo.save(salvo2);
			salvoRepo.save(salvo3);
			salvoRepo.save(salvo4);
			salvoRepo.save(salvo5);
			salvoRepo.save(salvo6);
			salvoRepo.save(salvo7);
			salvoRepo.save(salvo8);


			Score score1= new Score(1, new Date(),player1,game1);
			Score score2= new Score(2, new Date(),player2,game1);
			//Score score3= new Score(0.5, new Date(),player1,game1);

			scoreRepo.save(score1);
			scoreRepo.save(score2);



		};
	}}
