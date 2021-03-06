package org.mvnsearch.rsocket.requester;

import org.mvnsearch.account.AccountService;
import org.mvnsearch.account.UserService;
import org.mvnsearch.spring.boot.rsocket.RSocketRemoteServiceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.util.MimeTypeUtils;

import java.net.URI;

/**
 * rsocket configuration
 *
 * @author linux_china
 */
@Configuration
public class RSocketConfiguration {

    @Bean
    public RSocketRequester rsocketRequester(RSocketStrategies strategies, RSocketMessageHandler rsocketMessageHandler) {
        return RSocketRequester.builder()
                .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
                .rsocketStrategies(strategies)
                //.rsocketFactory(rsocketFactory -> rsocketFactory.acceptor(rsocketMessageHandler.responder()))
                .connectWebSocket(URI.create("ws://127.0.0.1:8088/rsocket")).block();
    }

    @Bean
    public AccountService accountService(RSocketRequester rsocketRequester) {
        return RSocketRemoteServiceBuilder.client(rsocketRequester, AccountService.class).build();
    }

    @Bean
    public UserService userService(RSocketRequester rsocketRequester) {
        return RSocketRemoteServiceBuilder.client(rsocketRequester, UserService.class).build();
    }
}
