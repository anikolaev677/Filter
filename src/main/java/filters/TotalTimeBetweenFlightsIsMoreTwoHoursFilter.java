package filters;

import com.gridnine.testing.flights.Flight;
import com.gridnine.testing.flights.Segment;

import java.time.ZoneOffset;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TotalTimeBetweenFlightsIsMoreTwoHoursFilter implements Filter {
    private final Predicate<Flight> isMoreOneSegment = flight -> flight.getSegments().size() > 1;
    private final Predicate<Flight> isDifferenceOfTwoHours = flight -> {
        List<Segment> segments = flight.getSegments();
        long firstSegmentArrivalTime = segments.get(0).getArrivalDate().toEpochSecond(ZoneOffset.ofHours(0));
        long totalTimeSegmentFlight = 0;
        long lastSegmentDepartureTime = segments.get(segments.size() - 1).getDepartureDate().toEpochSecond(ZoneOffset.ofHours(0));
        long totalDifference;
        long departureTime;
        long arrivalTime;

        for (int i = segments.size() - 2; i > 0; i--) {
            departureTime = segments.get(i).getDepartureDate().toEpochSecond(ZoneOffset.ofHours(0));
            arrivalTime = segments.get(i).getArrivalDate().toEpochSecond(ZoneOffset.ofHours(0));
            totalTimeSegmentFlight += arrivalTime - departureTime;
        }
        totalDifference = lastSegmentDepartureTime - firstSegmentArrivalTime - totalTimeSegmentFlight;

        return totalDifference > 7200;
    };

    @Override
    public Stream<Flight> filtering(Stream<Flight> listToFiltering) {
        return listToFiltering.filter(isMoreOneSegment).filter(isDifferenceOfTwoHours).parallel();
    }
}
