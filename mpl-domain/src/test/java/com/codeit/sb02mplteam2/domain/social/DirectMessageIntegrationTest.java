package com.codeit.sb02mplteam2.domain.social;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageCreateRequest;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageWsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class DirectMessageIntegrationTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  private BlockingQueue<DirectMessageWsResponse> blockingQueue;

  @BeforeEach
  void setup() {
    blockingQueue = new LinkedBlockingDeque<>();
  }

  @Test
  void testSendAndReceiveDirectMessage() throws Exception {
    // 1️⃣ 로그인 요청 → JWT 문자열 그대로 받기
    LoginRequest loginRequest = new LoginRequest("abc@naver.com", "12123aa@@");
    ResponseEntity<String> loginResponse =
        restTemplate.postForEntity("/api/auth/login", loginRequest, String.class);

    assertThat(loginResponse.getStatusCode().is2xxSuccessful()).isTrue();
    String jwtToken = Objects.requireNonNull(loginResponse.getBody());

    // 2️⃣ WebSocket STOMP 세션 연결
    List<Transport> transports = Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()));
    SockJsClient sockJsClient = new SockJsClient(transports);

    WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
    stompClient.setMessageConverter(new MappingJackson2MessageConverter());

    StompHeaders connectHeaders = new StompHeaders();
    connectHeaders.add("Authorization", "Bearer " + jwtToken);

    StompSession session = stompClient
        .connectAsync(
            String.format("ws://localhost:%d/ws", port),
            new StompSessionHandlerAdapter() {},
            connectHeaders)
        .get(5, TimeUnit.SECONDS);

    assertThat(session.isConnected()).isTrue();

    // 3️⃣ /user/queue/dm/messages 구독
    session.subscribe("/user/queue/dm/messages", new StompFrameHandler() {
      @Override
      public Type getPayloadType(StompHeaders headers) {
        return DirectMessageWsResponse.class;
      }

      @Override
      public void handleFrame(StompHeaders headers, Object payload) {
        blockingQueue.add((DirectMessageWsResponse) payload);
      }
    });

    // 4️⃣ 메시지 발송
    String testContent = "안녕하세요, 테스트 DM!";
    DirectMessageCreateRequest createRequest = new DirectMessageCreateRequest(1L, 1L, testContent);

    session.send("/app/dm/send", createRequest);

    // 5️⃣ 수신된 메시지 확인 (최대 5초 대기)
    DirectMessageWsResponse received = blockingQueue.poll(5, TimeUnit.SECONDS);
    assertThat(received)
        .withFailMessage("STOMP 메시지를 5초 내에 수신하지 못했습니다")
        .isNotNull();
    assertThat(received.message().content()).isEqualTo(testContent);
    assertThat(received.receiverId()).isNotNull();
  }

  // 🔹 로그인 요청 DTO
  static class LoginRequest {
    private String email;
    private String password;

    public LoginRequest() {}
    public LoginRequest(String email, String password) {
      this.email = email;
      this.password = password;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
  }
}
