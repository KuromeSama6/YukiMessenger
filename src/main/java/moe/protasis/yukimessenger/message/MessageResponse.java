package moe.protasis.yukimessenger.message;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import moe.protasis.yukicommons.json.JsonWrapper;

@Getter
@AllArgsConstructor
public class MessageResponse {
    private final boolean processed;
    private final JsonWrapper data;
}
