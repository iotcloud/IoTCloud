namespace java cgl.iotcloud.thrift

struct Property {
    1:string name
    2:string value
}

struct EndpointRequest {
    1:string name
    2:string type
    3:string path
}

struct EndpointResponse {
    1:string name
    2:string type
    3:string address
    4:list<Property> properties
}

struct NodeId {
    1:string name
    2:string group
}

struct Node {
    1:string name
    2:string group
    3:list<EndpointResponse> consumers
    4:list<EndpointResponse> producers
}

struct Response {
    1:i32 status
    2:string reason
}

service NodeService {
    Response registerNode(1:NodeId nodeId)
    Response unRegisterNode(1:NodeId nodeId)
    EndpointResponse registerProducer(1:NodeId nodeId, 2:EndpointRequest endpoint)
    Response unRegisterProducer(1:NodeId nodeId, 2:EndpointRequest endpoint)
    EndpointResponse registerConsumer(1:NodeId nodeId, 2:EndpointRequest endpoint)
    Response unRegisterConsumer(1:NodeId nodeId, 2:EndpointRequest endpoint)
    list<NodeId> getNodes()
    Node getNode(1:NodeId id)
}




