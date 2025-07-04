package org.market.bingebuddies.services;

import org.market.bingebuddies.dtos.ScreeningEventDTO;

import java.util.List;
import java.util.Optional;

public interface ScreeningEventService {
    List<ScreeningEventDTO> getUpcomingScreeningEventsForClub(Long clubId);
    ScreeningEventDTO addScreeningEvent(Long clubId, ScreeningEventDTO screeningEventDTO);

}
