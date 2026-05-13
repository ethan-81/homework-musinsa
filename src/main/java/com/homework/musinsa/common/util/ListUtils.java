package com.homework.musinsa.common.util;

import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@UtilityClass
public class ListUtils {
    public <T> List<List<T>> partitionList(List<T> originalList, int chunkSize) {
        if (originalList == null || originalList.isEmpty()) {
            return Collections.emptyList();
        }

        int size = originalList.size();
        return IntStream.range(0, (size + chunkSize - 1) / chunkSize)
                .mapToObj(i -> originalList.subList(i * chunkSize, Math.min((i + 1) * chunkSize, size)))
                .collect(Collectors.toList());
    }
}
