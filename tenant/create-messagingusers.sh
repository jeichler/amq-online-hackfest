for i in {1..100} ; do

oc apply -f -  << EOF

apiVersion: user.enmasse.io/v1beta1
kind: MessagingUser
metadata:
  name: team3-space.user$i
spec:
  username: user$i
  authentication:
    type: password
    password: Zm9vYmFyCg== # Base64 encoded "foobar"
  authorization:
    - addresses: ["results_user$i"]
      operations: ["recv"]
    - addresses: ["input_online, input_batch"]
      operations: ["send"]

EOF


done
