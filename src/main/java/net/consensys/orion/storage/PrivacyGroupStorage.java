/*
 * Copyright 2019 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.consensys.orion.storage;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.tuweni.io.Base64.encodeBytes;

import net.consensys.orion.enclave.Enclave;
import net.consensys.orion.enclave.PrivacyGroupPayload;
import net.consensys.orion.exception.OrionErrorCode;
import net.consensys.orion.exception.OrionException;
import net.consensys.orion.http.server.HttpContentType;
import net.consensys.orion.utils.Serializer;

import java.util.Arrays;
import java.util.Optional;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.concurrent.AsyncResult;
import org.apache.tuweni.crypto.sodium.Box;
import org.apache.tuweni.kv.KeyValueStore;


public class PrivacyGroupStorage implements Storage<PrivacyGroupPayload> {

  private final KeyValueStore<Bytes, Bytes> store;
  private final Enclave enclave;

  public PrivacyGroupStorage(final KeyValueStore<Bytes, Bytes> store, final Enclave enclave) {
    this.store = store;
    this.enclave = enclave;
  }

  @Override
  public AsyncResult<String> put(final PrivacyGroupPayload data) {
    final String key = generateDigest(data);
    final Bytes keyBytes = Bytes.wrap(key.getBytes(UTF_8));
    final Bytes dataBytes = Bytes.wrap(Serializer.serialize(HttpContentType.CBOR, data));
    return store.putAsync(keyBytes, dataBytes).thenSupply(() -> key);
  }

  @Override
  public String generateDigest(final PrivacyGroupPayload data) {
    final Box.PublicKey[] addresses =
        Arrays.stream(data.addresses()).map(enclave::readKey).toArray(Box.PublicKey[]::new);
    return encodeBytes(enclave.generatePrivacyGroupId(addresses, data.randomSeed(), data.type()));
  }


  @Override
  public AsyncResult<Optional<PrivacyGroupPayload>> get(final String key) {
    final Bytes keyBytes = Bytes.wrap(key.getBytes(UTF_8));
    return store.getAsync(keyBytes).thenApply(
        maybeBytes -> Optional.ofNullable(maybeBytes).map(
            bytes -> Serializer.deserialize(HttpContentType.CBOR, PrivacyGroupPayload.class, bytes.toArrayUnsafe())));
  }

  @Override
  public AsyncResult<Optional<PrivacyGroupPayload>> update(String key, PrivacyGroupPayload data) {
    throw new OrionException(OrionErrorCode.METHOD_UNIMPLEMENTED);
  }
}
