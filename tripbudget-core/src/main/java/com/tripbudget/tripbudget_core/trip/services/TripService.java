package com.tripbudget.tripbudget_core.trip.services;

import com.tripbudget.tripbudget_core.common.services.HashidsService;
import com.tripbudget.tripbudget_core.trip.dtos.request.CreateTripRequest;
import com.tripbudget.tripbudget_core.trip.dtos.request.GetAllTripsRequest;
import com.tripbudget.tripbudget_core.trip.dtos.request.InitialMemberRequest;
import com.tripbudget.tripbudget_core.trip.dtos.request.TripFilterRequest;
import com.tripbudget.tripbudget_core.trip.dtos.request.UpdateTripRequest;
import com.tripbudget.tripbudget_core.trip.dtos.response.PageResponse;
import com.tripbudget.tripbudget_core.trip.dtos.response.TripResponse;
import com.tripbudget.tripbudget_core.trip.entities.TripEntity;
import com.tripbudget.tripbudget_core.trip.entities.TripMemberEntity;
import com.tripbudget.tripbudget_core.trip.enums.TripMemberRole;
import com.tripbudget.tripbudget_core.trip.enums.TripMemberStatus;
import com.tripbudget.tripbudget_core.trip.repositories.TripMemberRepository;
import com.tripbudget.tripbudget_core.trip.repositories.TripRepository;
import com.tripbudget.tripbudget_core.trip.specifications.TripSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    private final HashidsService hashidsService;

    public TripService(
            TripRepository tripRepository,
            TripMemberRepository tripMemberRepository,
            HashidsService hashidsService
    ) {
        this.tripRepository = tripRepository;
        this.tripMemberRepository = tripMemberRepository;
        this.hashidsService = hashidsService;
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
                Long memberUserId = hashidsService.decode(item.userId());
                if(memberUserId.equals(currentUserId)){
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Trip owner must not be added again as a member"
                    );
                }
                if(!invitedUsers.add(memberUserId)) {
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
                            .invite(savedTrip, hashidsService.decode(item.userId()), item.role())).toList();

            tripMemberRepository.saveAll(invitations);

            return TripResponse.from(savedTrip, hashidsService);
    }

    public PageResponse<TripResponse> getMyTrips(
            Long currentUserId,
            int page,
            int size,
            TripFilterRequest filter
    ) {
        Specification<TripEntity> spec = TripSpecifications.buildSpecification(currentUserId, filter);
        Sort sort = TripSpecifications.buildSort(
                filter != null ? filter.sortBy() : null,
                filter != null ? filter.sortDirection() : null
        );

        Page<TripEntity> tripPage = tripRepository.findAll(spec, PageRequest.of(page, size, sort));

        Page<TripResponse> responsePage = tripPage.map(t -> TripResponse.from(t, hashidsService));

        return PageResponse.from(responsePage);
    }

    public TripResponse getTripById(Long tripId, Long currentUserId)
    {
        TripEntity trip = this.getActiveMemberTrip(tripId, currentUserId);
        return TripResponse.from(trip, hashidsService);
    }

    @Transactional
    public TripResponse update(Long currentUserId, Long tripId, UpdateTripRequest dto)
    {
        TripEntity trip = this.getActiveMemberTrip(tripId, currentUserId);

        if(dto.getEndDate() != null && dto.getStartDate() != null && dto.getEndDate().isBefore(dto.getStartDate()))
        {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "End date must be on or after start date"
            );
        }

        trip.update(
                dto.getName(),
                dto.getDescription(),
                dto.getDestination(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getBaseCurrency()
        );

        return TripResponse.from(trip, hashidsService);
    }

    @Transactional
    public void deleteTrip(
            Long currentUserId,
            Long tripId
    ) {
        TripEntity trip = getActiveMemberTrip(
                tripId,
                currentUserId
        );
        trip.deleteTrip();
//        tripRepository.delete(trip);
    }

    private TripEntity getActiveMemberTrip(
            Long tripId,
            Long currentUserId
    ) {
        TripEntity trip = tripRepository.findActiveTrip(tripId);

        if(trip == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Trip not found"
            );
        }

        boolean isActiveMember =
                tripMemberRepository.existsByTrip_IdAndUserIdAndStatus(
                        tripId,
                        currentUserId,
                        TripMemberStatus.ACTIVE
                );

        if (!isActiveMember) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not have access to this trip"
            );
        }

        return trip;
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
