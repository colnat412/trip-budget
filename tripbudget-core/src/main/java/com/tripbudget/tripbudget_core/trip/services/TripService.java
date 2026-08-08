package com.tripbudget.tripbudget_core.trip.services;

import com.tripbudget.tripbudget_core.trip.dtos.request.CreateTripRequest;
import com.tripbudget.tripbudget_core.trip.dtos.request.InitialMemberRequest;
import com.tripbudget.tripbudget_core.trip.dtos.response.TripResponse;
import com.tripbudget.tripbudget_core.trip.entities.TripEntity;
import com.tripbudget.tripbudget_core.trip.entities.TripMemberEntity;
import com.tripbudget.tripbudget_core.trip.enums.TripMemberRole;
import com.tripbudget.tripbudget_core.trip.repositories.TripMemberRepository;
import com.tripbudget.tripbudget_core.trip.repositories.TripRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service()
@Transactional(readOnly = true)
public class TripService {
    private final TripRepository tripRepository;
    private final TripMemberRepository tripMemberRepository;

    public TripService(TripRepository tripRepository, TripMemberRepository tripMemberRepository) {
        this.tripRepository = tripRepository;
        this.tripMemberRepository = tripMemberRepository;
    }

    @Transactional
    public TripResponse createTrip(Long currentUserId, CreateTripRequest dto){
            // validate dto
            validateCreateRequest(dto);

            TripEntity trip = TripEntity.create(
                    currentUserId,dto.getName(),
                    dto.getDestination(),
                    dto.getDescription(),
                    dto.getStartDate(),
                    dto.getEndDate(),
                    dto.getBaseCurrency()
            );
            TripEntity savedTrip = tripRepository.save(trip);

            TripMemberEntity tripOwnerMember = TripMemberEntity.createOwner(savedTrip, currentUserId);

            // set OWNER
            tripMemberRepository.save(tripOwnerMember);

            // add member if have
            List<InitialMemberRequest> initialMemberRequestList =
                    dto.getInitialMembers() == null
                            ? List.of()
                            : dto.getInitialMembers();

            Set<Long> invitedUsers = new HashSet<>();

            for(InitialMemberRequest item: initialMemberRequestList) {
                if(item.userId().equals(currentUserId)){
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Trip owner must not be added again as a member"
                    );
                }
                if(!invitedUsers.add(item.userId())) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "A user can be invited only once"
                    );
                }
                if (item.role() == TripMemberRole.OWNER) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Inviting member cannot have OWNER role"
                    );
                }
            }

            List<TripMemberEntity> invitations = initialMemberRequestList.stream()
                    .map(item -> TripMemberEntity
                            .invite(savedTrip, item.userId(), item.role())).toList();

            tripMemberRepository.saveAll(invitations);

            return TripResponse.from(savedTrip);
    }

    private void validateCreateRequest(CreateTripRequest request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "End date must be on or after start date"
            );
        }
    }
}
