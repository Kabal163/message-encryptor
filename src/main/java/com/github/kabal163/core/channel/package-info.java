/**
 * Provides ability to work with requests and responses
 * in multithreading application.
 * The functionality represents a number of request channels
 * with bounded priority and one fixed size response channel.
 * The channels are managed by {@link com.github.kabal163.core.channel.ChannelsHolder}.
 * The {@code ChannelHolder} is the entrypoint of the package.
 *
 * @see com.github.kabal163.core.channel.ChannelsHolder
 */
package com.github.kabal163.core.channel;