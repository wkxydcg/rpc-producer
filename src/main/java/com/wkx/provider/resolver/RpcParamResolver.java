package com.wkx.provider.resolver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wkx.provider.annotation.RpcProducer;
import com.wkx.provider.util.ClassUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.util.Set;

public class RpcParamResolver implements HandlerMethodArgumentResolver {

    private static final String JSON_KEY_PRE="rpc_json_request_key";

    private static final String JSON_READ_FLAG="json_read_flag";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Annotation rpcService[]=parameter.getMethod().getDeclaringClass().getAnnotationsByType(RpcProducer.class);
        return rpcService.length > 0;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        String key=JSON_KEY_PRE+httpServletRequest.getRequestedSessionId();
        if(httpServletRequest.getAttribute(JSON_READ_FLAG)!=null){
            JSONArray requestJson=(JSONArray) httpServletRequest.getAttribute(key);
            Object returnObject=convertJsonToObject(requestJson,parameter.getParameterType());
            httpServletRequest.setAttribute(key,requestJson);
            return returnObject;
        }else{
            httpServletRequest.setAttribute(JSON_READ_FLAG,true);
            int contentLength=httpServletRequest.getContentLength();
            byte buffer[] = new byte[contentLength];
            int i=0;
            while (i<contentLength){
                int readLen=httpServletRequest.getInputStream().read(buffer,i,contentLength-i);
                if(readLen==-1){
                    break;
                }
                i=i+readLen;
            }
            String requestStr=new String(buffer,"UTF-8");
            JSONArray requestJson= JSONObject.parseArray(requestStr);
            Object returnObject=convertJsonToObject(requestJson,parameter.getParameterType());
            httpServletRequest.setAttribute(key,requestJson);
            return returnObject;
        }
    }

    private Object convertJsonToObject(JSONArray array,Class cs){
        if(array.size()<=0) return null;
        JSONObject json=(JSONObject) array.get(0);
        if(ClassUtils.checkIsBaseClass(cs)){
            Set<String> keySet=json.keySet();
            if(keySet.isEmpty()) return null;
            Object value;
            value = keySet.stream().findFirst().map(json::get).orElse(null);
            return ClassUtils.castBaseType(value,cs);
        }else{
            Object returnObject=JSONObject.parseObject(array.get(0).toString(),cs);
            array.remove(0);
            return returnObject;
        }
    }
}
