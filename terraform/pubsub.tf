resource "google_pubsub_topic" "pizza-store" {
  name = "pizza-store"
}

resource "google_pubsub_subscription" "pizza-store-push-sub" {
  name  = "pizza-store-push-sub"
  topic = google_pubsub_topic.pizza-store.name

  ack_deadline_seconds = 20

  retry_policy {
    minimum_backoff = "10s"
  }

  push_config {
    push_endpoint = "${google_cloud_run_service.orders-api.status[0].url}/orders"
  }

  filter = "attributes.eventId = \"PIZZA_ORDER_REQUEST\" OR attributes.eventId = \"PIZZA_PREPARED_REQUEST\" OR attributes.eventId = \"PIZZA_BAKED_REQUEST\" OR attributes.eventId = \"PIZZA_LEFT_STORE_REQUEST\" OR attributes.eventId = \"PIZZA_DELIVERED_REQUEST\""
}