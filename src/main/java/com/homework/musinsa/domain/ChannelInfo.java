package com.homework.musinsa.domain;

public record ChannelInfo(
        String channelType,
        String channelTransactionId) {

    public ChannelInfo {
        if (channelType == null || channelType.isBlank()) {
            throw new IllegalArgumentException("Channel type must not be null or blank");
        }

        if (channelTransactionId == null || channelTransactionId.isBlank()) {
            throw new IllegalArgumentException("Channel transaction id must not be null or blank");
        }
    }

    public static ChannelInfo of(String channelType, String channelTransactionId) {
        return new ChannelInfo(channelType, channelTransactionId);
    }
}
