# pizza-store  :pizza:

Demo application of my "asynchronous notifications" talk 
(slides : https://docs.google.com/presentation/d/15h9lR_wBk8c1mRjn-7rC79rpkB_BsMfY9PJ00kMv3s0/edit?usp=sharing)

This is a full example of how to write a server-sent-events powered realtime event-driven serverless web application using Quarkus, Google Pub/Sub, NodeJS and MongDB.

This app showcases :

 - GitHub Actions automated CI
 - Terraform automated CD on Google Cloud Run
 - Google Pub/Sub integration (with both push and pull usage)
 - Quarkus + intergration testing with TestContainers
 - Server-Sent-Events with NodeJS
