package org.mvnsearch.account;

import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;

/**
 * RSocket reactive service proxy
 *
 * @author linux_china
 */
public class RSocketReactiveServiceProxy implements InvocationHandler {
    private RSocketRequester rsocketRequester;
    private String serviceFullName;
    private Duration timeout;

    public RSocketReactiveServiceProxy(RSocketRequester rsocketRequester, Class serviceInterface, Duration timeout) {
        this.rsocketRequester = rsocketRequester;
        this.serviceFullName = serviceInterface.getCanonicalName();
        this.timeout = timeout;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String routeKey = serviceFullName + "." + method.getName();
        Class returnDataType = null;
        Type genericReturnType = method.getGenericReturnType();
        if (genericReturnType != null) {
            ParameterizedType aType = (ParameterizedType) method.getGenericReturnType();
            returnDataType = (Class) aType.getActualTypeArguments()[0];
        }
        if (method.getReturnType().isAssignableFrom(Mono.class)) {
            return rsocketRequester.route(routeKey).data(args[0]).retrieveMono(returnDataType).timeout(timeout);
        } else if (method.getReturnType().isAssignableFrom(Mono.class)) {
            return rsocketRequester.route(routeKey).data(args[0]).retrieveFlux(returnDataType).timeout(timeout);
        } else {
            return rsocketRequester.route(routeKey).data(args[0]).send().timeout(timeout);
        }
    }
}
