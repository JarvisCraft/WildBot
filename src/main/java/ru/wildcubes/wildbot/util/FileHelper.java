package ru.wildcubes.wildbot.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

@UtilityClass
public class FileHelper {
    @NonNull public static List<String> readLines(final JarFile jarFile, final ZipEntry entry) {
        try {
            final BufferedReader inputStream = new BufferedReader(new InputStreamReader(jarFile.getInputStream(entry)));
            return inputStream.lines().collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}
