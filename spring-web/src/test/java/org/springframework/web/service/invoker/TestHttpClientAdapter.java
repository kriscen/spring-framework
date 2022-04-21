/*
 * Copyright 2002-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.service.invoker;

import java.time.Duration;
import java.util.Arrays;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * {@link HttpClientAdapter} with stubbed responses.
 *
 * @author Rossen Stoyanchev
 * @author Olga Maciaszek-Sharma
 */
@SuppressWarnings("unchecked")
class TestHttpClientAdapter implements HttpClientAdapter {

	@Nullable
	private String invokedMethodName;

	@Nullable
	private HttpRequestDefinition requestDefinition;

	@Nullable
	private ParameterizedTypeReference<?> bodyType;


	/**
	 * Create the proxy for the give service type.
	 */
	public <S> S createService(Class<S> serviceType, HttpServiceMethodArgumentResolver... resolvers) {

		HttpServiceProxyFactory factory = new HttpServiceProxyFactory(
				Arrays.asList(resolvers), this, ReactiveAdapterRegistry.getSharedInstance(), Duration.ofSeconds(5));

		return factory.createService(serviceType);
	}


	public String getInvokedMethodName() {
		assertThat(this.invokedMethodName).isNotNull();
		return this.invokedMethodName;
	}

	public HttpRequestDefinition getRequestDefinition() {
		assertThat(this.requestDefinition).isNotNull();
		return this.requestDefinition;
	}

	@Nullable
	public ParameterizedTypeReference<?> getBodyType() {
		return this.bodyType;
	}


	// HttpClientAdapter implementation

	@Override
	public Mono<Void> requestToVoid(HttpRequestDefinition definition) {
		saveInput("requestToVoid", definition, null);
		return Mono.empty();
	}

	@Override
	public Mono<HttpHeaders> requestToHeaders(HttpRequestDefinition definition) {
		saveInput("requestToHeaders", definition, null);
		return Mono.just(new HttpHeaders());
	}

	@Override
	public <T> Mono<T> requestToBody(HttpRequestDefinition definition, ParameterizedTypeReference<T> bodyType) {
		saveInput("requestToBody", definition, bodyType);
		return (Mono<T>) Mono.just(getInvokedMethodName());
	}

	@Override
	public <T> Flux<T> requestToBodyFlux(HttpRequestDefinition definition, ParameterizedTypeReference<T> bodyType) {
		saveInput("requestToBodyFlux", definition, bodyType);
		return (Flux<T>) Flux.just("request", "To", "Body", "Flux");
	}

	@Override
	public Mono<ResponseEntity<Void>> requestToBodilessEntity(HttpRequestDefinition definition) {
		saveInput("requestToBodilessEntity", definition, null);
		return Mono.just(ResponseEntity.ok().build());
	}

	@Override
	public <T> Mono<ResponseEntity<T>> requestToEntity(
			HttpRequestDefinition definition, ParameterizedTypeReference<T> type) {

		saveInput("requestToEntity", definition, type);
		return Mono.just((ResponseEntity<T>) ResponseEntity.ok("requestToEntity"));
	}

	@Override
	public <T> Mono<ResponseEntity<Flux<T>>> requestToEntityFlux(
			HttpRequestDefinition definition, ParameterizedTypeReference<T> bodyType) {

		saveInput("requestToEntityFlux", definition, bodyType);
		return Mono.just(ResponseEntity.ok((Flux<T>) Flux.just("request", "To", "Entity", "Flux")));
	}

	private <T> void saveInput(
			String methodName, HttpRequestDefinition definition, @Nullable ParameterizedTypeReference<T> bodyType) {

		this.invokedMethodName = methodName;
		this.requestDefinition = definition;
		this.bodyType = bodyType;
	}

}
