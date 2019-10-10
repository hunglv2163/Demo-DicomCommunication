# Demo-DicomCommunication
1. StoreSCP1
usage: storescp -b [<aet>[@<ip>]:]<port>
Run app in Test class. Threre are 2 parameters are passed to function:
   para[0]: "-b": 
   para[1]: "STORESCP:4444"; specify the port on which the Application Entity shall listening for incoming association requests. If no local IP address of the network interface is specified, connections on any/all local addresses are accepted. If an AE Title is specified, only requests with matching Called AE Title will be accepted. 
  STORESCP: Application Entity Title
  4444: port of store server
  
2. Dcmecho:

