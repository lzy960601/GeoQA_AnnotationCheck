package UserExp.Servers.micron;

import UserExp.Utils.GlobalStore;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.sql.Connection;

@SpringBootApplication
public class MicRonWebApplication extends SpringBootServletInitializer {

	private static Logger log = Logger.getLogger(MicRonWebApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(MicRonWebApplication.class, args);
		log.info(String.format("Global Store Begin!"));
		GlobalStore.Entry();
		log.info(String.format("Global Store Complete!"));
	}

	@Override // 为了打包springboot项目
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(this.getClass());
	}

	/**
	 * 处理跨域访问
	 * 
	 * @author HuangZX
	 * @date: 2019年3月23日 下午11:32:58
	 */
	// @Configuration
	// public class CorsConfig extends WebMvcConfigurerAdapter {
	// @Override
	// public void addCorsMappings(CorsRegistry registry) {
	// registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST",
	// "OPTIONS", "PUT")
	// .allowedHeaders("Content-Type", "X-Requested-With", "accept", "Origin",
	// "Access-Control-Request-Method",
	// "Access-Control-Request-Headers")
	// .exposedHeaders("Access-Control-Allow-Origin",
	// "Access-Control-Allow-Credentials")
	// .allowCredentials(true).maxAge(3600);
	// }
	// }

}
