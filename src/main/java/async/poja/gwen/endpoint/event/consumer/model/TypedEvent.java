package async.poja.gwen.endpoint.event.consumer.model;

import async.poja.gwen.PojaGenerated;
import async.poja.gwen.endpoint.event.model.PojaEvent;

@PojaGenerated
public record TypedEvent(String typeName, PojaEvent payload) {}
