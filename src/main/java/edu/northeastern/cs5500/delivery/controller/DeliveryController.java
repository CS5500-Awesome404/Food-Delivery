package edu.northeastern.cs5500.delivery.controller;

import edu.northeastern.cs5500.delivery.model.Delivery;
import edu.northeastern.cs5500.delivery.repository.GenericRepository;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class DeliveryController {
    private final GenericRepository<Delivery> deliveries;

    @Inject
    DeliveryController(GenericRepository<Delivery> deliveryRepository) {
        deliveries = deliveryRepository;

        log.info("DeliveryController > construct");

        if (deliveries.count() > 0) {
            return;
        }

        log.info("DeliveryController > construct > adding default deliveries");

        final Delivery defaultDelivery1 = new Delivery();
        defaultDelivery1.setTitle("Hot dog");

        final Delivery defaultDelivery2 = new Delivery();
        defaultDelivery2.setTitle("A steak");
        defaultDelivery2.setDescription("Not a hot dog");

        try {
            addDelivery(defaultDelivery1);
            addDelivery(defaultDelivery2);
        } catch (Exception e) {
            log.error("DeliveryController > construct > adding default deliveries > failure?");
            e.printStackTrace();
        }
    }

    @Nullable
    public Delivery getDelivery(@Nonnull ObjectId uuid) {
        log.debug("DeliveryController > getDelivery({})", uuid);
        return deliveries.get(uuid);
    }

    @Nonnull
    public Collection<Delivery> getDeliveries() {
        log.debug("DeliveryController > getDeliveries()");
        return deliveries.getAll();
    }

    @Nonnull
    public Delivery addDelivery(@Nonnull Delivery delivery) throws Exception {
        log.debug("DeliveryController > addDelivery(...)");
        if (!delivery.isValid()) {
            // TODO: replace with a real invalid object exception
            // probably not one exception per object type though...
            throw new Exception("InvalidDeliveryException");
        }

        ObjectId id = delivery.getId();

        if (id != null && deliveries.get(id) != null) {
            // TODO: replace with a real duplicate key exception
            throw new Exception("DuplicateKeyException");
        }

        return deliveries.add(delivery);
    }

    public void updateDelivery(@Nonnull Delivery delivery) throws Exception {
        log.debug("DeliveryController > updateDelivery(...)");
        deliveries.update(delivery);
    }

    public void deleteDelivery(@Nonnull ObjectId id) throws Exception {
        log.debug("DeliveryController > deleteDelivery(...)");
        deliveries.delete(id);
    }
}
