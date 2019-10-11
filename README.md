# Demo-DicomCommunication
1. StoreSCP1: This app will create a SCP server

   usage: storescp -b [<aet>[@<ip>]:]<port>
   
   Run app in Test class. Threre are 2 parameters are passed to function:
   
      - para[0]: "-b": 
      
      - para[1]: "STORESCP:4444"; specify the port on which the Application Entity shall listening for incoming association requests. If         no local IP address of the network interface is specified, connections on any/all local addresses are accepted. If an AE Title 	         is specified, only requests with matching Called AE Title will be accepted. 
   
        STORESCP: Application Entity Title
  
        4444: port of store server
  
2. Dcmecho: This app will make a echo request to SCP server

   usage: dcmecho STORESCP@localhost:4444
 
   Run app in Test class. There is one parameters is passed to function:
   
      - para[0]: STORESCP@localhost:4444 ; 
   
        STORESCP: Application Entity Title wil match with Application Entity Title of SCP server
   
        localhost: name or IP address of SCP server
   
        4444: port of SCP server
   
3. Dcmsnd: This app will make a send request to SCP server(one or many dicom files )

   usage: dcmesnd STORESCP@localhost:4444 <path of file>
   
   Run app in Test class. There are two parameters are passed to function:
   
      - para[0]: STORESCP@localhost:4444;
   
      - para[1]: "5a7301a0e4b0b4037cf4bfec.dcm"
   
        STORESCP: Application Entity Title wil match with Application Entity Title of SCP server
   
        localhost: name or IP address of SCP server
   
        4444: port of SCP server
   
        <path of file>: the path of file or path of directory contains files
   
   After sending file to SCP server, file will be stored in directory contains StoreSCP app.
   
 4. Dcmhpqr: This app will make a query request to SCP server
   
    usage dcmqr STORESCP@localhost:4444 -qStudyDate=20060204
     
    Queries studies from Feburary 4th 2006 on entity STORESCP at localhost server listening on port 4444.
      
     Run app in Test class. There are two parameters are passed to function:
     
      - para[0]: STORESCP@localhost:4444;
      
      - para[1]: qStudyDate=20060204

        STORESCP: Application Entity Title wil match with Application Entity Title of SCP server
   
        localhost: name or IP address of SCP server
      
        4444: port of SCP server
      
        qStudyDate=20060204: Tag name and value to query.
      
