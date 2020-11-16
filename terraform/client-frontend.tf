resource "google_cloud_run_service" "client-frontend" {
  name     = "client-frontend"
  location = "us-central1"

  template {
    spec {
      containers {
        image = "gcr.io/${var.project_id}/client-frontend"
        resources {
          limits = {
            memory        = "128Mi"
          }
        }
        env {
          name = "EVENTS_ENDPOINT"
          value = google_cloud_run_service.client-sse-handler.status[0].url
        }
        env {
          name = "ORDERS_ENDPOINT"
          value = google_cloud_run_service.client-orders-api.status[0].url
        }
      }
    }
  }

  traffic {
    percent         = 100
    latest_revision = true
  }

  metadata {
    namespace = var.project_id
  }

  depends_on = [google_project_service.run]
}