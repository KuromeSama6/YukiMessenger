package moe.protasis.yukimessenger.rpc;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.stream.Collectors;

@UtilityClass
public class RPCUtil {
    public static String GetMethodParameterSignature(Parameter[] parameters) {
        return Arrays.stream(parameters)
            .map(c -> c.getParameterizedType().getTypeName())
            .collect(Collectors.joining(","));
    }
}
