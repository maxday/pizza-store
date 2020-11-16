
resource "google_cloud_run_service_iam_binding" "auth-orders-api" {
  location = google_cloud_run_service.orders-api.location
  project = google_cloud_run_service.orders-api.project
  service = google_cloud_run_service.orders-api.name
  role = "roles/run.invoker"
  members = [
    "allUsers",
  ]
}

resource "google_cloud_run_service_iam_binding" "auth-client-frontend" {
  location = google_cloud_run_service.client-frontend.location
  project = google_cloud_run_service.client-frontend.project
  service = google_cloud_run_service.client-frontend.name
  role = "roles/run.invoker"
  members = [
    "allUsers",
  ]
}

resource "google_cloud_run_service_iam_binding" "auth-manager-frontend" {
  location = google_cloud_run_service.manager-frontend.location
  project = google_cloud_run_service.manager-frontend.project
  service = google_cloud_run_service.manager-frontend.name
  role = "roles/run.invoker"
  members = [
    "allUsers",
  ]
}

resource "google_cloud_run_service_iam_binding" "auth-client-orders-api" {
  location    = google_cloud_run_service.client-orders-api.location
  project     = google_cloud_run_service.client-orders-api.project
  service     = google_cloud_run_service.client-orders-api.name
  role = "roles/run.invoker"
  members = [
    "allUsers",
  ]
}

resource "google_cloud_run_service_iam_binding" "auth-client-sse-handler" {
  location    = google_cloud_run_service.client-sse-handler.location
  project     = google_cloud_run_service.client-sse-handler.project
  service     = google_cloud_run_service.client-sse-handler.name
  role = "roles/run.invoker"
  members = [
    "allUsers",
  ]
}

resource "google_cloud_run_service_iam_binding" "auth-manager-sse-handler" {
  location    = google_cloud_run_service.manager-sse-handler.location
  project     = google_cloud_run_service.manager-sse-handler.project
  service     = google_cloud_run_service.manager-sse-handler.name
  role = "roles/run.invoker"
  members = [
    "allUsers",
  ]
}

resource "google_cloud_run_service_iam_binding" "auth-manager-orders-api" {
  location    = google_cloud_run_service.manager-orders-api.location
  project     = google_cloud_run_service.manager-orders-api.project
  service     = google_cloud_run_service.manager-orders-api.name
  role = "roles/run.invoker"
  members = [
    "allUsers",
  ]
}