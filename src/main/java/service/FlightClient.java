package service;

import com.gridnine.testing.flights.Flight;
import filters.Filter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public final class FlightClient {
    private final FilterChain filterChain;

    public FlightClient(FilterChain filterChain) {
        this.filterChain = filterChain;
    }

    public void showFlightList() {
        System.out.println("Select the necessary filters in the list of flights and enter their numbers separated by a space:");
        System.out.println(parseFilterList().iterator().next());
        System.out.println("Enter numbers, separated by a space, to filter or press ENTER: ");
        AtomicInteger i = new AtomicInteger();
        StringBuffer outputErrors = new StringBuffer();
        try {
            Stream.of(filterChain.flightsFiltering().sequential())
                    .peek(flightStream -> Stream.of(flightStream)
                            .filter(flightStream1 -> filterChain.isUnknownFilterNumber())
                            .forEach(flightStream1 -> outputErrors
                                    .append("Incorrect filter input!\nTry again...")))
                    .peek(flightStream -> Stream.of(flightStream)
                            .filter(flightStream2 -> filterChain.isFlightsListEmpty())
                            .forEach(flightStream2 -> outputErrors.append("Flight list is empty...")))
                    .peek(flightStream -> System.out.println(outputErrors.toString()))
                    .filter(flightStream -> outputErrors.length() == 0)
                    .forEach(flightStream -> flightStream
                            .peek(flight -> System.out.println("----------------------------------------------------------------"))
                            .peek(flight -> System.out.println("Flight № " + flight.getIndex() /*i.incrementAndGet()*/
                                    + ":        |    Departure date    |    Arrival date    |"))
                            .map(Flight::getSegments)
                            .forEach(s -> s
                                    .stream()
                                    .peek(segment -> System.out.print(
                                            "-  Segment № " + (s.indexOf(segment) + 1)
                                                    + "     |   "
                                                    + segment.getDepartureDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
                                                    + "   |   "))
                                    .forEach(segment
                                            -> System.out.println(segment.getArrivalDate()
                                            .format(DateTimeFormatter
                                                    .ofPattern("yyyy-MM-dd'T'HH:mm")) + " |"))
                            ));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred while filtering! ");
        }
    }

    public ArrayList<String> parseFilterList() {
        List<Filter> filtersList = filterChain.getFilters();
        ArrayList<String> nameFilters = new ArrayList<>();
        StringBuffer filtersBuffer = new StringBuffer();

        Stream.of(filtersList)
                .forEach(filters -> filters
                        .stream()
                        .peek(filter -> filtersBuffer.append("   ").append(filters.indexOf(filter) + 1).append(" - "))
                        .forEach(filter -> Stream.of(Arrays.asList(filter.getClass().getSimpleName().split("(?=\\p{Lu}.)")))
                                .peek(strings -> strings
                                        .stream()
                                        .filter(s -> strings.indexOf(s) == 0)
                                        .forEach(s -> filtersBuffer.append(s).append(" ")))
                                .peek(strings -> strings
                                        .stream()
                                        .filter(s -> strings.indexOf(s) != 0 && strings.indexOf(s) != strings.size() - 1)
                                        .map(String::toLowerCase)
                                        .forEach(s -> filtersBuffer.append(s).append(" ")))
                                .forEach(strings -> strings
                                        .stream()
                                        .filter(s -> strings.indexOf(s) == strings.size() - 1)
                                        .map(String::toLowerCase)
                                        .forEach(s -> filtersBuffer.append(s).append(";\n")))));
        nameFilters.add(filtersBuffer.toString());
        return nameFilters;
    }
}
