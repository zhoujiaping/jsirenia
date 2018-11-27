package org.jsirenia.util.spi;

import java.util.ServiceLoader;

import org.jsirenia.util.spi.demo.DogService;

public class Spi {
	public static void main(String[] args) {
		ServiceLoader<DogService> loaders = ServiceLoader.load(DogService.class);
		loaders.forEach(service->{
			service.sleep();
		});
	}
}
