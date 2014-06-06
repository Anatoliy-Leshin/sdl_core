package com.ford.syncV4.protocol;

import android.os.Environment;

<<<<<<< HEAD
=======
import com.ford.syncV4.protocol.WiProProtocol.MessageFrameAssembler;
>>>>>>> develop
import com.ford.syncV4.protocol.enums.FrameType;
import com.ford.syncV4.protocol.enums.ServiceType;
import com.ford.syncV4.protocol.secure.secureproxy.ProtocolSecureManager;
import com.ford.syncV4.proxy.constants.ProtocolConstants;
import com.ford.syncV4.streaming.AbstractPacketizer;
import com.ford.syncV4.util.DebugTool;
import com.ford.syncV4.util.logger.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public abstract class AbstractProtocol {

    protected static final String CLASS_NAME = AbstractProtocol.class.getSimpleName();
    public static final int SSL_OVERHEAD = 64;
    public static final int MTU_SIZE = 1500;
    public static int PROTOCOL_FRAME_HEADER_SIZE = ProtocolConstants.PROTOCOL_FRAME_HEADER_SIZE_DEFAULT;
    public static int MAX_DATA_SIZE = MTU_SIZE - PROTOCOL_FRAME_HEADER_SIZE - SSL_OVERHEAD;

    protected IProtocolListener _protocolListener = null;
    protected byte[] mHeaderBuf = new byte[PROTOCOL_FRAME_HEADER_SIZE];
    protected int mHeaderBufWritePos = 0;

    private static File audioFile;
    private static File videoFile;
    private static FileOutputStream audioOutputFileStream;
    private static FileOutputStream videoOutputFileStream;

    protected boolean hasRPCStarted;

    public synchronized ProtocolSecureManager getProtocolSecureManager() {
        return protocolSecureManager;
    }

    public synchronized void setProtocolSecureManager(ProtocolSecureManager protocolSecureManager) {
        this.protocolSecureManager = protocolSecureManager;
    }

    private ProtocolSecureManager protocolSecureManager;

    protected ProtocolVersion mProtocolVersion = new ProtocolVersion();


    // Caller must provide a non-null IProtocolListener interface reference.
    public AbstractProtocol(IProtocolListener protocolListener) {
        if (protocolListener == null) {
            throw new IllegalArgumentException("Provided protocol listener interface reference is null");
        } // end-if
        _protocolListener = protocolListener;
        setProtocolVersion(ProtocolConstants.PROTOCOL_VERSION_MIN);
    }// end-ctor

    // This method receives raw bytes as they arrive from transport.  Those bytes
    // are then collected by the protocol and assembled into complete messages and
    // handled internally by the protocol or propagated to the protocol listener.

    public abstract void HandleReceivedBytes(byte[] receivedBytes, int length);

    // This method receives a protocol message (e.g. RPC, BULK, etc.) and processes
    // it for transmission over the transport.  The results of this processing will
    // be sent to the onProtocolMessageBytesToSend() method on protocol listener
    // interface.  Note that the ProtocolMessage itself contains information
    // about the type of message (e.g. RPC, BULK, etc.) and the protocol syncSession
    // over which to send the message, etc.
    public abstract void SendMessage(ProtocolMessage protocolMessage, ServiceType serviceTypeToBeSecured);

    public abstract void SendMessage(ProtocolMessage protocolMessage);

    public byte getProtocolVersion() {
        return mProtocolVersion.getCurrentVersion();
    }

    /**
     * <b>This method is for the Test Cases only</b>
     *
     * @param version test protocol version
     */
    public void set_TEST_ProtocolMinVersion(byte version) {
        ProtocolConstants.PROTOCOL_VERSION_MIN = version;
        setProtocolVersion(version);
    }

    /**
     * <b>This method is for the Test Cases only</b>
     *
     * @param version test protocol version
     */
    public void set_TEST_ProtocolMaxVersion(byte version) {
        ProtocolConstants.PROTOCOL_VERSION_MAX = version;
    }

    public void setProtocolVersion(byte version) {
        mProtocolVersion.setCurrentVersion(version);

        if (mProtocolVersion.getCurrentVersion() >= ProtocolConstants.PROTOCOL_VERSION_TWO) {
            updateDataStructureToProtocolVersion(version);
        } else {
            Logger.d(CLASS_NAME + " Protocol version:" + mProtocolVersion.getCurrentVersion());
        }
    }

    /**
     * Send heart beat header
     * @param sessionId id of the current session
     */
    public abstract void SendHeartBeatMessage(byte sessionId);

    /**
     * Send heart beat ack header
     * @param sessionId id of the current session
     */
    public abstract void SendHeartBeatAckMessage(byte sessionId);

    /**
     * This method starts a protocol syncSession. A corresponding call to the protocol
     * listener onProtocolSessionStarted() method will be made when the protocol
     * syncSession has been established.
     *
     * @param sessionId ID of the current active session
     */
    public abstract void StartProtocolSession(byte sessionId);

<<<<<<< HEAD
    public abstract void StartProtocolService(ServiceType serviceType, Session session, boolean isCyphered);
=======
    public abstract void StartProtocolService(ServiceType serviceType, byte sessionId);
>>>>>>> develop

    // This method ends a protocol syncSession.  A corresponding call to the protocol
    // listener onProtocolServiceEnded() method will be made when the protocol
    // syncSession has ended.
    public abstract void EndProtocolService(ServiceType serviceType, byte sessionID);


    // TODO REMOVE
    // This method sets the interval at which heartbeat protocol messages will be
    // sent to SYNC.
    public abstract void SetHeartbeatSendInterval(int heartbeatSendInterval_ms);

    // This method sets the interval at which heartbeat protocol messages are
    // expected to be received from SYNC.
    public abstract void SetHeartbeatReceiveInterval(int heartbeatReceiveInterval_ms);

    // This method is called whenever the protocol receives a complete frame
    protected void handleProtocolFrameReceived(ProtocolFrameHeader header, byte[] data,
                                               WiProProtocol.MessageFrameAssembler assembler) {
        if (data != null) {
            Logger.d(CLASS_NAME + " receive " + data.length + " bytes");
        } else {
            Logger.w(CLASS_NAME + " receive null bytes");
        }
        resetHeartbeat(header.getSessionID());
        assembler.handleFrame(header, data);
    }

    // This method is called whenever a protocol has an entire frame to send
    protected void handleProtocolFrameToSend(ProtocolFrameHeader header, byte[] data, int offset,
                                             int length) {
        if (data != null) {
            Logger.d(CLASS_NAME + " transmit " + data.length + " bytes");
        } else {
            Logger.w(CLASS_NAME + " transmit null bytes");
        }

        Logger.d(CLASS_NAME + " transmit ProtocolFrameHeader:" + header.toString());

        resetHeartbeatAck(header.getSessionID());
        composeMessage(header, data, offset, length);
    }

    private void composeMessage(ProtocolFrameHeader header, byte[] data, int offset, int length) {
            Logger.d("SyncProxyTester", "Frame encrypted " + header.isEncrypted());
            if (data != null) {
                if (offset >= data.length) {
                    throw new IllegalArgumentException("offset should not be more then length");
                }
                byte[] dataChunkNotCyphered = null;
                if (offset + length >= data.length) {
                    dataChunkNotCyphered = Arrays.copyOfRange(data, offset, data.length);
                } else {
                    dataChunkNotCyphered = Arrays.copyOfRange(data, offset, offset + length);
                }

                if (getProtocolSecureManager() != null) {
                    try {
                        byte[] dataChunk = getProtocolSecureManager().sendDataTOSSLClient(header.isEncrypted(), dataChunkNotCyphered);
                        header.setDataSize(dataChunk.length);
                        sendMessage(header, dataChunk);
                    } catch (IOException e) {
                        getProtocolSecureManager().reportAnError(e);
                        DebugTool.logError("Error data coding", e);
                    } catch (InterruptedException e) {
                        getProtocolSecureManager().reportAnError(e);
                        DebugTool.logError("Error data coding", e);
                    }
                } else {
                    sendMessage(header, dataChunkNotCyphered);
                }
        } else {
            byte[] frameHeader = header.assembleHeaderBytes();
            handleProtocolMessageBytesToSend(frameHeader, 0, frameHeader.length);
        }
    }

<<<<<<< HEAD
    private void sendMessage(ProtocolFrameHeader header, byte[] dataChunk) {
        byte[] frameHeader = header.assembleHeaderBytes();
        byte[] commonArray = new byte[frameHeader.length + dataChunk.length];
        System.arraycopy(frameHeader, 0, commonArray, 0, frameHeader.length);
        System.arraycopy(dataChunk, 0, commonArray, frameHeader.length, dataChunk.length);
        handleProtocolMessageBytesToSend(commonArray, 0, commonArray.length);
    }

    private synchronized void resetHeartbeatAck() {
=======
    private synchronized void resetHeartbeatAck(byte sessionId) {
>>>>>>> develop
        if (_protocolListener != null) {
            _protocolListener.onResetHeartbeatAck(sessionId);
        }
    }

    private synchronized void resetHeartbeat(byte sessionId) {
        if (_protocolListener != null) {
            _protocolListener.onResetHeartbeat(sessionId);
        }
    }

    private void logMobileNaviMessages(ProtocolFrameHeader header, byte[] data) {
        if (header.getServiceType().equals(ServiceType.Audio_Service)) {
            Logger.d("AUDIO SERVICE - ProtocolFrameHeader: " + header.toString());
            if (data != null && data.length > 0) {
                Logger.d("AUDIO SERVICE - Hex Data frame: " + AbstractPacketizer.printBuffer(data, 0, data.length));
            }
        }
    }

    private void initVideoDumpStream() {
        String filename = "ford_video.txt";
        if (videoFile == null) {
            videoFile = new File(Environment.getExternalStorageDirectory(), filename);
        }
        if (videoOutputFileStream == null) {
            try {
                videoOutputFileStream = new FileOutputStream(videoFile);
            } catch (FileNotFoundException e) {
                // handle exception
            }
        }
    }

    private void initAudioDumpStream() {
        String filename = "ford_audio.txt";
        if (audioFile == null) {
            audioFile = new File(Environment.getExternalStorageDirectory(), filename);
        }
        if (audioOutputFileStream == null) {
            try {
                audioOutputFileStream = new FileOutputStream(audioFile);
            } catch (FileNotFoundException e) {
                // handle exception
            }
        }
    }

    private void writeToSdCardAudioFile(ProtocolFrameHeader header, byte[] data) {
        if (header.getServiceType().equals(ServiceType.Audio_Service)) {
            if (header.getFrameType().equals(FrameType.Single)) {
               /* try {
                    if (audioOutputFileStream != null) {
                        audioOutputFileStream.write(data);
                    }
                } catch (IOException e) {
                    Log.w("SyncProxyTester", e.toString());
                }*/
                // Log.d("ford_audio.txt","audio: " + new String(data));
            } else {
                Logger.w(CLASS_NAME + " wrong frame type for video streaming");
            }
        }
    }

    private void writeToSdCardVideoFile(ProtocolFrameHeader header, byte[] data) {
        if (header.getServiceType().equals(ServiceType.Mobile_Nav)) {
            if (header.getFrameType().equals(FrameType.Single)) {
               /* try {
                    if (videoOutputFileStream != null) {
                        videoOutputFileStream.write(data);
                    }
                } catch (IOException e) {
                    Log.w("SyncProxyTester", e.toString());
                }*/
                // Log.d("ford_video.txt","video: " + new String(data));
            } else {
                Logger.w(CLASS_NAME + " wrong frame type for video streaming");
            }
        }
    }

    // This method handles protocol message bytes that are ready to send.
    // A callback is sent to the protocol listener.
    protected void handleProtocolMessageBytesToSend(byte[] bytesToSend, int offset, int length) {
        _protocolListener.onProtocolMessageBytesToSend(bytesToSend, offset, length);
    }

    // This method handles received protocol messages.
    protected void handleProtocolMessageReceived(ProtocolMessage message) {
        _protocolListener.onProtocolMessageReceived(message);
    }

    /**
     * This method handles response of the EndService coming from SDL.
     * A callback is sent to the protocol listener.
     */
    protected void handleProtocolServiceEnded(ServiceType serviceType, byte sessionId) {
        _protocolListener.onProtocolServiceEnded(serviceType, sessionId);
    }

    /**
     * This method handles response of the EndServiceAck coming from SDL.
     * A callback is sent to the protocol listener.
     */
    protected void handleProtocolServiceEndedAck(ServiceType serviceType, byte sessionId) {
        _protocolListener.onProtocolServiceEndedAck(serviceType, sessionId);
    }

    /**
     * This method handles the startup of a protocol syncSession. A callback is sent to the
     * protocol listener.
     *
     * @param serviceType
     * @param sessionId
     * @param version
     */
    protected void handleProtocolSessionStarted(ServiceType serviceType,
<<<<<<< HEAD
                                                byte sessionID, boolean encrypted, byte version, String correlationID) {
        Session session = Session.createSession(serviceType, sessionID, encrypted);
        _protocolListener.onProtocolSessionStarted(session, version, correlationID);
    }

    protected void handleProtocolServiceStarted(ServiceType serviceType,
                                                byte sessionID, boolean encrypted, byte version, String correlationID) {
        if (sessionID == 0) {
            throw new IllegalArgumentException("Can't create service with id 0. serviceType" + serviceType + ";sessionID " + sessionID);
        }
        _protocolListener.onProtocolServiceStarted(serviceType, sessionID, encrypted, version, correlationID);
=======
                                                byte sessionId, byte version) {
        _protocolListener.onProtocolSessionStarted(sessionId, version);
    }

    protected void handleProtocolServiceStarted(ServiceType serviceType,
                                                byte sessionID, byte version) {
        if (serviceType.equals(ServiceType.RPC)) {
            throw new IllegalArgumentException("Can't create RPC service without creating syncSession. serviceType" + serviceType + ";sessionID " + sessionID);
        }
        if (sessionID == 0) {
            throw new IllegalArgumentException("Can't create service with id 0. serviceType" + serviceType + ";sessionID " + sessionID);
        }
        _protocolListener.onProtocolServiceStarted(serviceType, sessionID, version);
>>>>>>> develop
    }

    // This method handles protocol errors. A callback is sent to the protocol
    // listener.
    protected void handleProtocolError(String string, Exception ex) {
        _protocolListener.onProtocolError(string, ex);
    }

    protected void handleAppUnregistered() {
        _protocolListener.onProtocolAppUnregistered();
    }

    protected void handleProtocolHeartbeatACK(byte sessionId) {
        _protocolListener.onProtocolHeartbeatACK(sessionId);
    }

    protected void handleProtocolHeartbeat(byte sessionId) {
        _protocolListener.onProtocolHeartbeat(sessionId);
    }

    protected void updateDataStructureToProtocolVersion(byte version) {
        Logger.d(CLASS_NAME + " Data structure updated to v:" + version);
        // TODO : Incorporate SSL overhead const
        // Implement here

        switch (version) {
            case ProtocolConstants.PROTOCOL_VERSION_ONE:
                PROTOCOL_FRAME_HEADER_SIZE = ProtocolConstants.PROTOCOL_FRAME_HEADER_SIZE_V_1;
                break;
            default:
                PROTOCOL_FRAME_HEADER_SIZE = ProtocolConstants.PROTOCOL_FRAME_HEADER_SIZE_V_2;
                break;
        }

        MAX_DATA_SIZE = MTU_SIZE - PROTOCOL_FRAME_HEADER_SIZE - SSL_OVERHEAD;
        mHeaderBuf = new byte[AbstractProtocol.PROTOCOL_FRAME_HEADER_SIZE];
    }

    protected void resetDataStructureToProtocolVersion() {
        mHeaderBuf = new byte[PROTOCOL_FRAME_HEADER_SIZE];
        mHeaderBufWritePos = 0;
    }
}