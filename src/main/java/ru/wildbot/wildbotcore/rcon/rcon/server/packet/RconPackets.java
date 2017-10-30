/*
 * Copyright 2017 Peter P. (JARvis PROgrammer)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.wildbot.wildbotcore.rcon.rcon.server.packet;

import com.google.common.collect.Sets;
import lombok.NoArgsConstructor;
import lombok.Synchronized;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class RconPackets {
    private final Map<Byte, RconPacketType> types = new HashMap<>();

    public RconPackets(final RconPacketType... packetTypes) {
        for (RconPacketType packetType : packetTypes) register(packetType);
    }

    public RconPackets(final Collection<RconPacketType> packetTypes) {
        for (RconPacketType packetType : packetTypes) register(packetType);
    }

    public static RconPackets ofDefault() {
        return new RconPackets(DEFAULT_PACKETS);
    }

    @Synchronized
    public RconPacketType get(final byte id) {
        return types.get(id);
    }

    @Synchronized public RconPacketType reRegister(final RconPacketType type) {
        return types.put(type.getId(), type);
    }

    @Synchronized public RconPacketType register(final RconPacketType type) {
        return types.putIfAbsent(type.getId(), type);
    }

    private static final Collection<RconPacketType> DEFAULT_PACKETS = Sets.newHashSet(
            new RconPacketType<>((byte) 0, new RconPacketInPing())
    );
}