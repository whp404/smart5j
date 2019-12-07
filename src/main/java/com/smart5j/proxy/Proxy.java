package com.smart5j.proxy;

public interface Proxy {

    Object doProxy(ProxyChain proxyChain) throws  Throwable;
}
