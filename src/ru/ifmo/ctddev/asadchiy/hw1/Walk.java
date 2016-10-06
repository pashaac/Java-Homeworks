package ru.ifmo.ctddev.asadchiy.hw1;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Count MD5 hashes for every file.
 * File list should be in file with path arg[0]
 * The result is written to file arg[1]
 * <p>
 * Created by Pavel Asadchiy
 * on 10.09.16 13:38.
 */
public class Walk {

    private static final Logger log = Logger.getLogger(Walk.class.getName());
    private static final byte[] buffer = new byte[1024];

    private static Optional<List<String>> readAllLines(String path) {
        if (!Files.isReadable(Paths.get(path))) {
            log.log(Level.SEVERE, "can't read from file with path=" + path);
            return Optional.empty();
        }
        try {
            return Optional.of(Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8).stream()
                    .collect(Collectors.toList()));
        } catch (IOException e) {
            log.log(Level.SEVERE, "can't read from file with path=" + path + ", errorMsg=" + e.getMessage());
            return Optional.empty();
        }
    }

    static void writeMd5CheckSums(String path, List<String> hashes) {
        if (!Files.isWritable(Paths.get(path))) {
            log.log(Level.SEVERE, "can't write to file with path=" + path);
            return;
        }
        try {
            Files.write(Paths.get(path), hashes, StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            log.log(Level.SEVERE, "no file with path=" + path);
        } catch (IOException e) {
            log.log(Level.SEVERE, "io message=" + e.getMessage());
        }
    }

    private static String formOutputMessage(final byte[] checksum) {
        String md5 = "";
        for (byte element : checksum) {
            md5 += Integer.toString((element & 0xff) + 0x100, 16).substring(1);
        }
        return md5;
    }

    static String calculateMd5Checksum(final String path) {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(path, "r")) {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            int size;
            while ((size = randomAccessFile.read(buffer)) > 0) {
                md5.update(buffer, 0, size);
            }
            return formOutputMessage(md5.digest()) + " " + path;
        } catch (NoSuchAlgorithmException e) {
            log.log(Level.SEVERE, "can't find md5 algorithm in system, message=" + e.getMessage());
        } catch (FileNotFoundException e) {
            log.log(Level.SEVERE, "no file with path=" + path);
        } catch (IOException e) {
            log.log(Level.SEVERE, "io message=" + e.getMessage());
        }
        return "00000000000000000000000000000000 " + path;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            log.log(Level.SEVERE, "run program with params <input file> <output file>");
            return;
        }
        writeMd5CheckSums(args[1], readAllLines(args[0]).orElseThrow(IllegalStateException::new).stream()
                .map(Walk::calculateMd5Checksum)
                .collect(Collectors.toList()));
    }

}
