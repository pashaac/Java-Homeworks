package ru.ifmo.ctddev.asadchiy.hw1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Recursive walk through directories and calculate MD5 hash for files
 *
 * @see Walk
 * <p>
 * Created by Pavel Asadchiy
 * on 11.09.16 9:47.
 */
public class RecursiveWalk {

    private static final Logger log = Logger.getLogger(RecursiveWalk.class.getName());

    private static Stream<Path> subfiles(final Path directory) {
        try {
            return Files.walk(directory);
        } catch (IOException e) {
            log.log(Level.SEVERE, "can't get files contains in directory=" + directory + ", errorMsg=" + e.getMessage());
            return Stream.empty();
        }
    }

    private static Optional<List<String>> mineFiles(final String path) {
        try {
            return Optional.of(Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8).stream()
                    .map(Paths::get)
                    .flatMap(RecursiveWalk::subfiles)
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .collect(Collectors.toList()));
        } catch (IOException e) {
            log.log(Level.SEVERE, "can't read from file with path=" + path + ", errorMsg=" + e.getMessage());
            return Optional.empty();
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            log.log(Level.SEVERE, "run program with params <input file> <output file>");
            return;
        }
        Walk.writeMd5CheckSums(args[1], mineFiles(args[0]).orElseThrow(IllegalStateException::new).stream()
                .map(Walk::calculateMd5Checksum)
                .collect(Collectors.toList()));
    }
}
