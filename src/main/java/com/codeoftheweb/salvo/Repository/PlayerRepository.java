package com.codeoftheweb.salvo.Repository;


import java.util.List;

import com.codeoftheweb.salvo.Classes.Player;
import com.codeoftheweb.salvo.DTO.PlayerDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PlayerRepository extends JpaRepository<PlayerDTO, Long> {
    PlayerDTO findByEmail(@Param("email") String email);

}


