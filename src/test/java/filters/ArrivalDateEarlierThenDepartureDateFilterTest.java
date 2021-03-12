package filters;

import com.gridnine.testing.flights.Flight;
import com.gridnine.testing.flights.Segment;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.function.Predicate;
import java.util.stream.Stream;

class ArrivalDateEarlierThenDepartureDateFilterTest implements Filter {
    final Predicate<Segment> segmentPredicate = segment -> segment.getDepartureDate().isAfter(segment.getArrivalDate());
    final Predicate<Flight> flightPredicate = flight -> flight.getSegments().parallelStream().allMatch(segmentPredicate);
    LocalDateTime now = LocalDateTime.now();

    /* FlightBuilder.createFlights()
     Segment segment = new Segment(now, now.minusDays(2));
     Flight flight = new Flight(segment);*/
    @Test
    void testFiltering() {

    }

    @Override
    public Stream<Flight> filtering(Stream<Flight> listToFiltering) {
        return null;
    }
}