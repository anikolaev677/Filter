package service;

import com.gridnine.testing.flights.Flight;
import filters.Filter;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FilterChain {
    private final List<Flight> flights;
    private boolean isUnknownFilterNumber = false;

    public FilterChain(List<Flight> flights) {
        this.flights = flights;
    }

    private List<Filter> createFiltersImpl() {
        List<Filter> filterImplInstances = new ArrayList<>();
        String packageName = Filter.class.getPackage().getName();
        URL resource = Thread.currentThread()
                .getContextClassLoader()
                .getResource(packageName.replace('.', '/'));
        assert resource != null;
        File directory = new File(resource.getFile());
        if (!directory.exists()) {
            return filterImplInstances;
        }
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            return filterImplInstances;
        }
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith("Filter.class") && !file.getName().startsWith("Filter")) {
                try {
                    Class<?> aClass = Class.forName(String.format("%s.%s",
                            packageName,
                            file.getName().substring(0, file.getName().indexOf("."))));
                    Filter newInstance = (Filter) aClass.newInstance();
                    filterImplInstances.add(newInstance);
                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return filterImplInstances;
    }

    public Stream<Flight> flightsFiltering() {
        InputScanner scanner = new InputScanner();
        Stream<Flight> flightStream = flights.parallelStream();
        List<Integer> parsedLineList = scanner.getParsedLineList();
        List<Filter> filters = getFilters();

        if (scanner.isParsedLineSuccess()) {
            for (int i = 0; i < filters.size(); i++) {
                for (int indexParsedLine : parsedLineList) {
                    if (filters.indexOf(filters.get(i)) == indexParsedLine - 1) {
                        flightStream = filters.get(i).filtering(flightStream);
                    }
                    if (filters.size() < indexParsedLine) {
                        isUnknownFilterNumber = true;
                        break;
                    }
                }
            }
        } else {
            isUnknownFilterNumber = true;
        }
        return flightStream;
    }

    public boolean isUnknownFilterNumber() {
        return isUnknownFilterNumber;
    }

    public List<Filter> getFilters() {
        return createFiltersImpl();
    }

    public boolean isFlightsListEmpty() {
        return flights.isEmpty();
    }
}
