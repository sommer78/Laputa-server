package com.laputa.utils;

import com.laputa.core.http.MediaType;
import com.laputa.core.http.UriTemplate;
import com.laputa.core.http.annotation.Consumes;
import com.laputa.core.http.annotation.Context;
import com.laputa.core.http.annotation.Path;
import com.laputa.core.http.rest.Handler;
import com.laputa.core.http.rest.params.*;
import io.netty.channel.ChannelHandlerContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 09.12.15.
 */
public class AnnotationsUtil {

    public static Handler[] register(String rootPath, Object o) {
        return registerHandler(rootPath, o);
    }

    public static Handler[] register(Object o) {
        return registerHandler("", o);
    }

    private static Handler[] registerHandler(String rootPath, Object handler) {
        Class<?> handlerClass = handler.getClass();
        Annotation pathAnnotation = handlerClass.getAnnotation(Path.class);
        String handlerMainPath = ((Path) pathAnnotation).value();

        List<Handler> processors = new ArrayList<>();

        for (Method method : handlerClass.getMethods()) {
            Annotation consumes = method.getAnnotation(Consumes.class);
            String contentType = MediaType.APPLICATION_JSON;
            if (consumes != null) {
                contentType = ((Consumes) consumes).value()[0];
            }

            Annotation path = method.getAnnotation(Path.class);
            if (path != null) {
                String fullPath = rootPath + handlerMainPath + ((Path) path).value();
                UriTemplate uriTemplate = new UriTemplate(fullPath);

                Handler handlerHolder = new Handler(uriTemplate, method, handler);

                for (int i = 0; i < method.getParameterCount(); i++) {
                    Parameter parameter = method.getParameters()[i];

                    Annotation queryParamAnnotation = parameter.getAnnotation(com.laputa.core.http.annotation.QueryParam.class);
                    if (queryParamAnnotation != null) {
                        handlerHolder.params[i] = new QueryParam(((com.laputa.core.http.annotation.QueryParam) queryParamAnnotation).value(), parameter.getType());
                    }

                    Annotation pathParamAnnotation = parameter.getAnnotation(com.laputa.core.http.annotation.PathParam.class);
                    if (pathParamAnnotation != null) {
                        handlerHolder.params[i] = new PathParam(((com.laputa.core.http.annotation.PathParam) pathParamAnnotation).value(), parameter.getType());
                    }

                    Annotation formParamAnnotation = parameter.getAnnotation(com.laputa.core.http.annotation.FormParam.class);
                    if (formParamAnnotation != null) {
                        handlerHolder.params[i] = new FormParam(((com.laputa.core.http.annotation.FormParam) formParamAnnotation).value(), parameter.getType());
                    }

                    Annotation contextAnnotation = parameter.getAnnotation(Context.class);
                    if (contextAnnotation != null) {
                        handlerHolder.params[i] = new ContextParam(ChannelHandlerContext.class);
                    }

                    if (pathParamAnnotation == null && queryParamAnnotation == null && formParamAnnotation == null &&
                            contextAnnotation == null) {
                        handlerHolder.params[i] = new BodyParam(parameter.getName(), parameter.getType(), contentType);
                    }
                }

                processors.add(handlerHolder);
            }
        }

        return processors.toArray(new Handler[processors.size()]);
    }

}
