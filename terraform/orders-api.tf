resource "google_cloud_run_service" "orders-api" {
  name     = "orders-api"
  location = "us-central1"

  template {
    spec {
      containers {
        image = "gcr.io/${var.project_id}/orders-api"
        env {
          name = "QUARKUS_HTTP_PORT"
          value = 8080
        }
        env {
          name = "QUARKUS_TOKEN_MACHINE_SERVICE_ACCOUNT"
          value = var.service_account
        }
        resources {
          limits = {
            memory        = "128Mi"
          }
        }
      }
    }
  }

  traffic {
    percent         = 100
    latest_revision = true
  }

  depends_on = [google_project_service.run]
}