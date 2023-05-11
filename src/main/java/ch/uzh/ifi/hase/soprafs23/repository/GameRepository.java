package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("gameRepository")
public interface GameRepository extends JpaRepository<Game, Long> {

    Game findGameByGameId(long gameId);
    Game findGameByInvitingUserId(long invitingUserId);
    Game findGameByInvitedUserId(long invitedUserId);
}
