namespace java cgl.iotcloud.thrift

struct TProperty {
    1:string name
    2:string value
}

struct TEndpointRequest {
    1:string name
    2:string type
    3:string path
}

struct TEndpointResponse {
    1:string name
    2:string type
    3:string address
    4:list<TProperty> properties
}

struct TNodeId {
    1:string name
    2:string group
}

struct TNode {
    1:string name
    2:string group
    3:list<TEndpointResponse> consumers
    4:list<TEndpointResponse> producers
}

struct TResponse {
    1:i32 status
    2:string reason
}

service TNodeService {
    TResponse registerNode(1:TNodeId nodeId)
    TResponse unRegisterNode(1:TNodeId nodeId)
    TEndpointResponse registerProducer(1:TNodeId nodeId, 2:TEndpointRequest endpoint)
    TResponse unRegisterProducer(1:TNodeId nodeId, 2:TEndpointRequest endpoint)
    TEndpointResponse registerConsumer(1:TNodeId nodeId, 2:TEndpointRequest endpoint)
    TResponse unRegisterConsumer(1:TNodeId nodeId, 2:TEndpointRequest endpoint)
    list<TNodeId> getNodes()
    TNode getNode(1:TNodeId id)
}




