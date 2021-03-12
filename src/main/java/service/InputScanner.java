package service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class InputScanner {
    private final Scanner scanner = new Scanner(System.in);
    private final AtomicBoolean isParsedLine = new AtomicBoolean(false);
    private List<Integer> parseLineList;

    public InputScanner() {
        if (isInputLineExist()) {
            String[] splitLine = isInputLineExist() ? scanner.nextLine().split(" ") : new String[0];
            parseLineList = new ArrayList<>(splitLine.length);

            Arrays.stream(splitLine)
                    .forEach(s -> {
                        if (s.matches("([1-9]+[\\s]?[1-9]|([1-9]))+")) {
                            parseLineList.add(Integer.parseInt(s));
                        } else {
                            isParsedLine.set(s.matches("([\\r]?[\\n]?)"));
                        }
                    });
            if (splitLine.length == parseLineList.size()) {
                isParsedLine.set(true);
            }
        }
    }

    private boolean isInputLineExist() {
        return scanner.hasNextLine();
    }

    public boolean isParsedLineSuccess() {
        return isParsedLine.get();
    }


    public List<Integer> getParsedLineList() {
        return parseLineList;
    }
}