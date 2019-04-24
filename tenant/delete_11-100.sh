for i in {11..100} 

do

oc delete address team3-space.results-user$i
oc delete MessagingUser team3-space.user$i

done
