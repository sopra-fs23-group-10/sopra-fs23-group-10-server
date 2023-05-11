package ch.uzh.ifi.hase.soprafs23.rest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResultTupleDTO {

    private long gameId;
    private long invitingPlayerId;
    private long invitingPlayerResult;
    private long invitedPlayerId;
    private long invitedPlayerResult;
}
