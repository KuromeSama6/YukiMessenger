package moe.protasis.yukimessenger.api.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import moe.protasis.yukicommons.api.json.JsonWrapper;

@Getter
@AllArgsConstructor
public class MessageResponse {
    private final boolean processed;
    private final JsonWrapper data;
}
