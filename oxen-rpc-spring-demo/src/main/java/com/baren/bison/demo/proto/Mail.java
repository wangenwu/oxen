/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.baren.bison.demo.proto;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public interface Mail {
  public static final org.apache.avro.Protocol PROTOCOL = org.apache.avro.Protocol.parse("{\"protocol\":\"Mail\",\"namespace\":\"com.baren.bison.demo.proto\",\"types\":[{\"type\":\"record\",\"name\":\"Message\",\"fields\":[{\"name\":\"to\",\"type\":\"string\"},{\"name\":\"from\",\"type\":\"string\"},{\"name\":\"body\",\"type\":\"string\"}]}],\"messages\":{\"send\":{\"request\":[{\"name\":\"message\",\"type\":\"Message\"}],\"response\":\"string\"}}}");
  /**
   */
  CharSequence send(com.baren.bison.demo.proto.Message message) throws org.apache.avro.AvroRemoteException;

  @SuppressWarnings("all")
  public interface Callback extends Mail {
    public static final org.apache.avro.Protocol PROTOCOL = com.baren.bison.demo.proto.Mail.PROTOCOL;
    /**
     * @throws java.io.IOException The async call could not be completed.
     */
    void send(com.baren.bison.demo.proto.Message message, org.apache.avro.ipc.Callback<CharSequence> callback) throws java.io.IOException;
  }
}