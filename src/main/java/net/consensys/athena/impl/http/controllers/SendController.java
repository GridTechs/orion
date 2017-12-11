package net.consensys.athena.impl.http.controllers;

import static net.consensys.athena.impl.http.server.Result.notImplemented;

import net.consensys.athena.api.enclave.Enclave;
import net.consensys.athena.api.storage.Storage;
import net.consensys.athena.impl.http.server.ContentType;
import net.consensys.athena.impl.http.server.Controller;
import net.consensys.athena.impl.http.server.Result;

import java.security.PublicKey;

import io.netty.handler.codec.http.FullHttpRequest;

/** Send a base64 encoded payload to encrypt. */
public class SendController implements Controller {
  private final Enclave enclave;
  private final Storage storage;
  private ContentType contentType;

  public SendController(Enclave enclave, Storage storage, ContentType contentType) {
    this.enclave = enclave;
    this.storage = storage;
    this.contentType = contentType;
  }

  @Override
  public Result handle(FullHttpRequest request) {
    // read request
    // if request.from == null, use default node public key as "from"
    // to = to + [nodeAlwaysSendTo] --> default pub key to always send to
    // if to == null, set to to self public key
    // TODO : that's a port from original Haskell code --> shouldn't we just validate the send request ? if to is set, we sent to "to", if not, we  return error ?
    // convert payload from b64 to bytes
    // encryptedPayload = enclave.encrypt(sendRequest.payload, from, to);
    // toReturn = storage.store(encryptedPayload);
    // if [to] is not only self, propagate payload to receipients
    // for each t in [to], find the matching IP from public key, and call the /push API with the encryptedPayload
    return notImplemented(contentType);
  }

  private static class SendRequest {
    String payload; // b64 encoded
    PublicKey from;
    PublicKey[] to;
  }

  private static class SendResponse {
    String key; // b64 digest key result from encrypted payload storage operation
  }
}
