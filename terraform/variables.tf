variable "project_id" {
    type  = string
    default = "techday-pizza-store-demo"
}

variable "service_account" {
  type    = string
  default = "ewogICJ0eXBlIjogInNlcnZpY2VfYWNjb3VudCIsCiAgInByb2plY3RfaWQiOiAidGVjaGRheS1waXp6YS1zdG9yZS1kZW1vIiwKICAicHJpdmF0ZV9rZXlfaWQiOiAiZjE0MGJmNzgzNjdmNDVjNDVmNmY4NzczMWUzMjIzYmJhY2JhOTVkNSIsCiAgInByaXZhdGVfa2V5IjogIi0tLS0tQkVHSU4gUFJJVkFURSBLRVktLS0tLVxuTUlJRXZRSUJBREFOQmdrcWhraUc5dzBCQVFFRkFBU0NCS2N3Z2dTakFnRUFBb0lCQVFDNVRnK0poaldtN0ZueFxuREUwd3ZxZDdFS0NhNk80a2xvNzM2SDdEMlA5dFgrcjZ4aGk1dU43NkJtakhGVWdxM2lzcW9NdFFMVGIxTU1wc1xuMnEwMlFSR3VreGtKVU92WC83L2tUK1pXTExheHpKcVROelRFYWcxWjJPUW83WEs3eW4zdjVRZG9ubXpyRHNiNFxuMmUzSTNSMGROaHJ1dWFXTk5rMjFtbk5lSi9QR0VFS2ppS1dKRDRhYXV2N3hsdi9MV1dTWExpM21KYVhqejBoMVxuNmJxUWVkRU5VbWxvNWJVRC9yaWVuRWZ2RTZSazZYdEw1eStVWUFMWG9oanBQYi9sYURKVEk2NEVxQnJzem1yaVxubHVhdHB4bS83dzE2b3pxdnRkOVVidDUwbmpmanZwZmFFd2RUYXB2dUZXUGFsbytBOHp2YndkNjg5RkxKa1ROTlxuOHhuV211aEhBZ01CQUFFQ2dnRUFFemtBVHAxZ0NuRmhRU3FzQlQ3NTQ3L05VVlNSZzdVa25wMzlMSGJoeEdoYlxuV1dsY1hrUEp5YmtKZklGK0s0aEJoUm9LRFVhcmVWTDI1TVRvakFUQy8wK2VITVBLdTJ6NTdJYkhWN0Fxd1pYK1xuZ1FBTWxzV0RuZ3JzWER5Smx1YUNHdlV3eUE4TE1IVW5lZUF0bFJVdTlIMU0zTy9nTng1bmdFdkY1UUlTL1R3N1xuMzF5YzlpRlBMYVo2RnhOK0FjMTlTdnhCMDk5S0RrcWdPdm1hMTZXSGRzZHZOT041VWRGb3pLTzlvOE95dmg1c1xubjZ3SUtyU2cwVWFqSGVUNEpMbFI1RTBHeVFtMy9BSWJLSDAvajNyb3ZBdkI5TUdpaldyK0UxLzZIeGNkNjZjR1xuOUZrdlN0RmFjT3pEVlN1YkEwUjlIbEdLYTN0c3ZLZ3hDTjlJeVMrd1JRS0JnUUQ5am1SSEN1MDFUL1ZUeThtRVxuU05EdmR1TWNWaTV6bldzckRLeFB5cHJoQjRGcGdWNmJRSWtGbjJDNkxHVEtQU21VNzdvMnd1MlZIQ05CM3BJTFxuM0tTcC83czBVSXRuajljcXVTOFMxTVhKSmdUS1RXUDJFNnNKR1l4Vkw0WmYyY0ZZdWp3ZlpEdFUzVkdYRm95b1xudkcrbVovZ3lCckVXVncxV045WisrZjRrZXdLQmdRQzdGMFVubnFPbThOT1N1ZVhxNjhlb2tIZDRxR3NhTVBvbVxuNEt1TURRRzFTWjVrTC9OM0JsMWVrbU5teEtXdXpWQmYxU2dRWWFmcXp1MW1jM0dMQ2dIL21Ebnk3cjNLR1RPYlxuSFZ3QWVxRkJSdlhxcXRNSTdIM0hzelhzbFZpNVE3SzBLWFNHcTZBZStJdDFMcEZxakNBQ0s2djVjK2FpSWU5d1xuRWxwZll0K2ZwUUtCZ0Z5eTZrWnU0RHBWZVVPaGhaTXZhL09nYTNlNHFsTHFnbnZDcmx5ZG5mSVhaR0RHUTJoUFxuTjhrdWM1YWRGRnc3OEMvYlZTRWdNdXdJMVhzSGZDMktCVW1CalZlYSt0eHA0aWhrZTZsTzBPQ3hYWjc0bkVzaVxuOWF6bUFrQkNsOFlEUG9USjBhRklhOGlqQVdsVGZTbFRleDVEZEtJVmdEakNxc1dTdElESHRQcHhBb0dCQUxGVlxuV2pnMmx2bjZsU0FqVW5tOHFNL3V0ejlIcTlUMGpYZGlSMWdGVUJRc3hwUkNHeXc3ZlM3UjVLUVpTczRJVCtHMlxuSWpaOHlmTzMxYS9oUnNNNlpqQ0ZjcEFBbVNaQmd2ZkkrVlorNWZBQXVjR0h6MlpLK3VIWnpRZ0ZlMGFIazVCUFxuSnVNck5DZUhBSEMvQmhpTzN4d1BsdjlvU3poc29BNkRadU80elN2OUFvR0FCUDBGeElxSTNEcnRrTGVieC9kaFxuN05zRWdFRHNFZjZ1NzRFR08yQ2hzWU5YTm9oN0FJQmpNUEcrLzFmaE1yZUZPd1UrQ0JJNFk5Y2FHQkhCc3ZCWlxueldscGxJbnUxZGpHTUdKTzZkRVd5ZzNSa2dEYXhVenNyZTZlRFduVzY1ZXNJdEViRmJDRWhYMlBlUUxDbm5kNFxuSis5OEVacjlvbUtFTGU5WDI0WXBNRlE9XG4tLS0tLUVORCBQUklWQVRFIEtFWS0tLS0tXG4iLAogICJjbGllbnRfZW1haWwiOiAicHVic3ViQHRlY2hkYXktcGl6emEtc3RvcmUtZGVtby5pYW0uZ3NlcnZpY2VhY2NvdW50LmNvbSIsCiAgImNsaWVudF9pZCI6ICIxMDgyMDYxNDQyOTc4NzY2MjU4MjYiLAogICJhdXRoX3VyaSI6ICJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20vby9vYXV0aDIvYXV0aCIsCiAgInRva2VuX3VyaSI6ICJodHRwczovL29hdXRoMi5nb29nbGVhcGlzLmNvbS90b2tlbiIsCiAgImF1dGhfcHJvdmlkZXJfeDUwOV9jZXJ0X3VybCI6ICJodHRwczovL3d3dy5nb29nbGVhcGlzLmNvbS9vYXV0aDIvdjEvY2VydHMiLAogICJjbGllbnRfeDUwOV9jZXJ0X3VybCI6ICJodHRwczovL3d3dy5nb29nbGVhcGlzLmNvbS9yb2JvdC92MS9tZXRhZGF0YS94NTA5L3B1YnN1YiU0MHRlY2hkYXktcGl6emEtc3RvcmUtZGVtby5pYW0uZ3NlcnZpY2VhY2NvdW50LmNvbSIKfQo="
}

variable "mongo_connexion_string" {
    type  = string
    default = "mongodb://googleCloundRunUser:25yjZFC0MkgF2iXv@article-shard-00-00.smmzv.mongodb.net:27017,article-shard-00-01.smmzv.mongodb.net:27017,article-shard-00-02.smmzv.mongodb.net:27017/pizzaOrder?ssl=true&replicaSet=atlas-pf7b4z-shard-0&authSource=admin&retryWrites=true&w=majority"
}

variable "pubsub_topic" {
    type  = string
    default = "/v1/projects/techday-pizza-store-demo/topics/pizza-store:publish"
}
variable "pubsub_manager_topic" {
    type  = string
    default = "/v1/projects/techday-pizza-store-demo/topics/pizza-store-manager:publish"
}