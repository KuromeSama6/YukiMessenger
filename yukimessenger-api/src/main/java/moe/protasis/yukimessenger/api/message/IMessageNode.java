package moe.protasis.yukimessenger.api.message;

import moe.protasis.yukicommons.api.json.JsonWrapper;

public interface IMessageNode {
    String GetId();
    void Send(JsonWrapper data);
}
