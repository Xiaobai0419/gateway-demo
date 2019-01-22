package com.sunfield.gateway.config;

import com.sunfield.gateway.filter.CustomFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteLocatorConfig {

    //经测试，这里实现的路由-CustomFilter绑定，导致这个路由的所有路径都只走这个过滤器，不会走默认过滤器（配置为ElapsedGatewayFilterFactory），但会走全局过滤器
    @Bean
    public RouteLocator customerRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                //外层：route的参数为Function<PredicateSpec, AsyncBuilder>，内部也是调用其apply方法，但不是返回其值，而是返回RouteLocatorBuilder.Builder
                //需要注意的是uri方法返回AsyncBuilder后，继续的调用中，都是AbstractBuilder的方法
                //而AsyncBuilder是其子类，根据AbstractBuilder类型参数，B为AsyncBuilder类型，也是order,id方法的返回类型
                //也是lambda方法体最终返回类型，与Function<PredicateSpec, AsyncBuilder>的抽象方法apply的返回类型对应
                //route方法体中调用apply的时候传入传出类型也与此对应
                .route(r -> r.path("/test/prefix/**")
                        //filters的参数为Function<GatewayFilterSpec, UriSpec>类型，对应于Function函数式编程接口，这里的lambda表达式是Function的一个实现，内容是apply方法的实现体，f是R apply(T t);唯一抽象方法的传入参数，根据Function定义Function<T, R>，f为GatewayFilterSpec类型
                        //filters方法的内容里调用了Function的apply方法，传入的是一个GatewayFilterSpec实例，并返回，该方法返回类型为Function<T, R>中的R，在这里是UriSpec
                        //所以调用apply时，方法体是下面的lambda中定义的，传入的是GatewayFilterSpec实例给f,经过方法体一系列链式调用，返回的（方法体是一句话时默认省略return）是GatewayFilterSpec实例，它是UriSpec的子类对象
                        //所以lambda本质上就是单抽象方法接口的实现，箭头前面的标识符是Function<T, R>中的T类型，代表抽象方法的传入参数，箭头后面的一句话最后返回值应是Function<T, R>中的R，代表抽象方法返回值
                        //以Function为参数的方法一般是在方法体中调用其apply方法并返回其值，注意返回值类型对应即可
                        .filters(f -> f.stripPrefix(2)//r.path(“/test/prefix/**”)表示自定义了访问前缀，在真正的Gateway进行路由转发的时候，会用过f.stripPrefix(2)把前缀去掉。--经试验，如果不去掉，相当于调用真实服务的地址也带上访问前缀，调用失败（注意：返回码是404 not found，但后台日志有Route matched:xxx,说明成功路由到了匹配的服务，但该服务下没有这个访问路径，而不带配置的访问前缀访问的，后台日志为Failed to handle request，干脆没有路由成功）！！
                                .filter(new CustomFilter())
                                .addResponseHeader("X-Response-test", "test"))
                        .uri("lb://sc-consumer")//基于服务发现的服务名访问，注意协议类型为lb://而不是http://（无法路由成功），如果服务名小写，这里大小写均可访问成功
                        .order(0)
                        .id("test_consumer_service")
                )
                .build();
    }
}
