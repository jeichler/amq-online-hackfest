for i in {1..10} ; do

oc apply -f -  << EOF

apiVersion: user.enmasse.io/v1beta1
kind: MessagingUser
metadata:
  name: team3-space.user$i
spec:
  username: user$i
  authentication:
    type: password
    password: Zm9vYmFy # echo -n "foobar" | base64
  authorization:
    - addresses: ["results-user$i"]
      operations: ["recv"]
    - addresses: ["input-online, input-batch"]
      operations: ["send", "recv"]

EOF

oc apply -f -  << EOF

apiVersion: enmasse.io/v1beta1
kind: Address
metadata:
  labels:
    addressType: queue
  name: team3-space.results-user$i
  namespace: tenant
spec:
  address: results-user$i
  plan: results-queue
  type: queue

EOF


done