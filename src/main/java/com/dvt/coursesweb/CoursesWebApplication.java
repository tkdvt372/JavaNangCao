package com.dvt.coursesweb;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class CoursesWebApplication {
	@Bean
	public Docket api(){
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.withClassAnnotation(RestController.class)).paths(PathSelectors.any())
				.build().apiInfo(apiInfo()).useDefaultResponseMessages(false);
	}

	@Bean
	public ApiInfo apiInfo(){
		final ApiInfoBuilder builder = new ApiInfoBuilder();
		return builder.build();
	}
	@Bean
	public Cloudinary cloudinary(){
		Cloudinary c = new Cloudinary(ObjectUtils.asMap(
				"cloud_name", "df6xlriko",
				"api_key", "672971318197823",
				"api_secret", "Rq88j3TExUXgfEgQUNomHBGWEpg",
				"secure", true
		));
		return c;
	}

	public static void main(String[] args) {
		SpringApplication.run(CoursesWebApplication.class, args);
	}

}
