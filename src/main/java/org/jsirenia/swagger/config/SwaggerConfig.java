package org.jsirenia.swagger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.google.common.collect.Sets;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration // 必须存在
@EnableSwagger2 // 必须存在
@EnableWebMvc // 必须存在
// @ComponentScan(basePackages = {}) //必须存在 扫描的API Controller package name
// 也可以直接扫描class (basePackageClasses)
public class SwaggerConfig {
	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				//.host("localhost:8080/api")
				.protocols(Sets.newHashSet("https", "http"))
				// .pathMapping("/")
				.select()
				// 只生成被Api这个注解注解过的类接口
				// //.apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
				// 只生成被ApiOperation这个注解注解过的api接口
				//.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
				// 生成所有API接口
				// //.apis(RequestHandlerSelectors.basePackage("com.hw.one.core.controller"))
				.paths(PathSelectors.any())
				.build();

	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("API")
				.description("API在线API文档")
				// .license("稳定版")
				// .termsOfServiceUrl("http://localhost:8080/dist/index.html")
				// .contact(new
				// Contact("ONE基础平台","http://192.168.15.246:8025/#/login","scsoft@163.com"))
				.version("2.0.0").build();
	}
}