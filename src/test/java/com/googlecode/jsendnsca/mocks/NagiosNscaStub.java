/* * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */package com.googlecode.jsendnsca.mocks;import com.googlecode.jsendnsca.MessagePayload;import com.googlecode.jsendnsca.builders.MessagePayloadBuilder;import com.googlecode.jsendnsca.utils.IOUtils;import java.io.*;import java.net.ServerSocket;import java.net.Socket;import java.util.Date;import java.util.LinkedList;import java.util.List;@SuppressWarnings({"ALL"})public class NagiosNscaStub implements Runnable {    private final List<MessagePayload> messagePayloads = new LinkedList<MessagePayload>();    private ServerSocket serverSocket;    private final int port;    private boolean listening;    private final String password;    private boolean sendInitialisationVector = true;    private int simulateTimeoutInMs;    private int maxMessageSizeInChars = 512;    public NagiosNscaStub(int port, String password) {        this.port = port;        this.password = password;    }    public void setSendInitialisationVector(boolean sendInitialisationVector) {        this.sendInitialisationVector = sendInitialisationVector;    }    public void setSimulateTimeoutInMs(int simulateTimeoutInMs) {        this.simulateTimeoutInMs = simulateTimeoutInMs;    }    public void start() throws Exception {        serverSocket = new ServerSocket(port);        Thread listeningThread = new Thread(this);        listening = true;        listeningThread.start();        Thread.sleep(100);    }    public void run() {        while (listening) {            try {                new MultiServerThread(serverSocket.accept()).start();            } catch (IOException ignore) {            }        }    }    public List<MessagePayload> getMessagePayloadList() {        return messagePayloads;    }    public void turnOnLargeMessageSupportAsInNsca291() {        maxMessageSizeInChars = 4096;    }    public void stop() throws IOException {        listening = false;        serverSocket.close();    }    @SuppressWarnings({"ClassExplicitlyExtendsThread"})    private class MultiServerThread extends Thread {        private static final int INITIALISATION_VECTOR_SIZE = 128;        private Socket socket;        private MultiServerThread(Socket socket) {            super("MultiServerThread");            this.socket = socket;        }        @Override        public void run() {            DataOutputStream outputStream = null;            InputStream inputStream = null;            try {                outputStream = new DataOutputStream(socket.getOutputStream());                sleep();                byte[] initVector = new byte[INITIALISATION_VECTOR_SIZE];                if (sendInitialisationVector) {                    outputStream.write(initVector);                    outputStream.writeInt((int) new Date().getTime());                    outputStream.flush();                    inputStream = socket.getInputStream();                    messagePayloads.add(parsePayload(inputStream, initVector));                }            } catch (IOException e) {                e.printStackTrace();            } finally {                IOUtils.closeQuietly(inputStream);                IOUtils.closeQuietly(outputStream);                if (socket != null) {                    try {                        socket.close();                    } catch (IOException ignore) {                    }                }            }        }        private void sleep() {            try {                Thread.sleep(simulateTimeoutInMs);            } catch (InterruptedException ignored) {            }        }        private MessagePayload parsePayload(InputStream inputStream, byte[] initVector) throws IOException {            DataInputStream stream = new DataInputStream(inputStream);            byte[] bytes = new byte[16 + 64 + 128 + maxMessageSizeInChars];            stream.readFully(bytes);            decrypt(bytes, initVector);            stream = new DataInputStream(new ByteArrayInputStream(bytes));            int amountToSkip = 12;            skipBytes(stream, amountToSkip);            short level = stream.readShort();            byte[] hostNameBytes = new byte[64];            stream.readFully(hostNameBytes);            String hostName = new String(hostNameBytes).trim();            byte[] serviceNameBytes = new byte[128];            stream.readFully(serviceNameBytes);            String serviceName = new String(serviceNameBytes).trim();            byte[] messageBytes = new byte[maxMessageSizeInChars];            stream.readFully(messageBytes);            String message = new String(messageBytes).trim();            return new MessagePayloadBuilder().withHostname(hostName).withLevel(level).withServiceName(serviceName).withMessage(message).create();        }        private void skipBytes(DataInputStream stream, int number) throws IOException {            long count = stream.skip(number);            if (count < number) {                throw new RuntimeException("Wanted to skip [" + number + "] bytes but only skipped [" + count + "]");            }        }        private void decrypt(byte[] sendBuffer, byte[] initVector) {            if (password != null) {                byte[] myPasswordBytes = password.getBytes();                for (int y = 0, x = 0; y < sendBuffer.length; y++, x++) {                    if (x >= myPasswordBytes.length) {                        x = 0;                    }                    sendBuffer[y] ^= myPasswordBytes[x];                }            }            for (int y = 0, x = 0; y < sendBuffer.length; y++, x++) {                if (x >= INITIALISATION_VECTOR_SIZE) {                    x = 0;                }                sendBuffer[y] ^= initVector[x];            }        }    }}